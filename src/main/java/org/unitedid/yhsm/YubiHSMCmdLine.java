/*
 * Copyright (c) 2011 United ID. All rights reserved.
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
 *
 * @author Stefan Wold <stefan.wold@unitedid.org>
 */

package org.unitedid.yhsm;

import org.apache.commons.cli.*;
import org.unitedid.yhsm.internal.YubiHSMCommandFailedException;
import org.unitedid.yhsm.internal.YubiHSMErrorException;
import org.unitedid.yhsm.internal.YubiHSMInputException;

import java.util.Scanner;

public class YubiHSMCmdLine {

    /** Debug output */
    private static boolean debug = false;

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
     *
     * @param deviceName the YubiHSM device name (ex /dev/ttyACM0)
     */
    public static void runUnlock(String deviceName) {
        try {
            YubiHSM hsm = new YubiHSM(deviceName, 1);

            String password;
            Scanner in = new Scanner(System.in);
            System.out.print("Please enter HSM password: ");
            password = in.nextLine();
            in.close();

            if (hsm.keyStorageUnlock(password)) {
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

        if (args.length < 1) {
            printUsage(options);
            System.exit(0);
        }

        /* Defaults */
        String deviceName = "/dev/ttyACM0";

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

            if (cmdLine.hasOption("u")) {
                runUnlock(deviceName);
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
}
