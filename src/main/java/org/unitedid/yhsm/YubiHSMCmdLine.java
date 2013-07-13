/*
 * Copyright (c) 2011 - 2013 United ID.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitedid.yhsm;

import org.apache.commons.cli.*;
import org.unitedid.yhsm.internal.YubiHSMCommandFailedException;
import org.unitedid.yhsm.internal.YubiHSMErrorException;
import org.unitedid.yhsm.internal.YubiHSMInputException;
import org.unitedid.yhsm.utility.ModHex;
import org.unitedid.yhsm.utility.Utils;

import java.io.Console;

public class YubiHSMCmdLine {

    /** Debug output */
    private static boolean debug = false;

    /** HSM default device name */
    private static String deviceName = "/dev/ttyACM0";

    /** HSM device */
    private static YubiHSM hsm;

    /** Unlock using OTP, if HSM supports it */
    private static boolean useOtp = true;

    /** Private constructor */
    private YubiHSMCmdLine() {}

    /**
     * Print usage information when running from command line.
     *
     * @param options the option definitions
     */
    public static void printUsage(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.setWidth(80);
        help.printHelp("[-D <device>] [OPTION]...", "", options, "");
    }

    /**
     * Prompts for HSM key storage password on the command line.
     */
    public static void runUnlock() {
        try {
            hsm = new YubiHSM(deviceName, 1);

            Console in = System.console();
            if (in == null) {
                System.out.println("Failed to open system console.");
                System.exit(1);
            }

            String otp = null;
            String password = new String(in.readPassword("Please enter HSM password: "));
            if (hsm.getInfo().getMajorVersion() > 0 && useOtp) {
                otp = ModHex.decode(new String(in.readPassword("Please enter admin YubiKey OTP: ")));
            }

            if (unlock(password, otp)) {
                System.out.println("YubiHSM " + deviceName + " was successfully unlocked.");
            } else {
                System.out.println("Unlock failed, bad password.");
            }

        } catch (YubiHSMCommandFailedException e) {
            System.out.println("Unlock command failed with the reason: " + e.getMessage());
        } catch (YubiHSMInputException e) {
            System.out.println("Invalid input. Password has to be in hex format.");
            if (debug) {
                System.out.println(e);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Password has to be in hex format.");
        } catch (YubiHSMErrorException e) {
            System.out.println("An error has occurred: " + e.getMessage());
        }
    }

    /**
     * Main method.
     *
     * @param args the command arguments
     */
    public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("h", "help", false, "This usage information");
        options.addOption("d", "debug", false, "Debug output");
        options.addOption("D", "device", true, "YubiHSM device name, default is /dev/ttyACM0");
        options.addOption("u", "unlock-hsm", false, "Unlock YubiHSM key storage");
        options.addOption("n", "no-otp", false, "Don't ask for OTP");

        if (args.length < 1) {
            printUsage(options);
            System.exit(0);
        }

        try {
            CommandLine cmdLine = parser.parse(options, args);

            if (cmdLine.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }

            if (cmdLine.hasOption("d")) {
                debug = true;
            }

            if (cmdLine.hasOption("D")) {
                deviceName = cmdLine.getOptionValue("D");
            }

            if (cmdLine.hasOption("u") || cmdLine.hasOption("n")) {
                if (cmdLine.hasOption("n")) {
                    useOtp = false;
                }
                runUnlock();
            } else {
                printUsage(options);
            }
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println("Bad command arguments.");
            printUsage(options);
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * Private function that handles the YubiHSM unlock/storage decrypt logic
     *
     * @param password the password in hex format
     * @param otp the YubiKey OTP string in hex format
     * @return true if YubiHSM was successfully unlocked, false if unlock failed
     * @throws YubiHSMInputException
     * @throws YubiHSMCommandFailedException
     * @throws YubiHSMErrorException
     */
    private static Boolean unlock(String password, String otp) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        if (hsm.getInfo().getMajorVersion() == 0) {
            if (hsm.keyStorageUnlock(password)) {
                return true;
            }
        } else {
            if (useOtp) {
                if (otp == null || otp.isEmpty()) {
                    throw new IllegalArgumentException("Invalid OTP");
                }
                String publicId = Utils.getYubiKeyPublicId(otp);
                String key = Utils.getYubiKeyOtp(otp);
                if (hsm.keyStoreDecrypt(password) && hsm.unlockOtp(publicId, key)) {
                    return true;
                }
            } else if (hsm.keyStoreDecrypt(password)) {
                return true;
            }
        }
        return false;
    }
}
