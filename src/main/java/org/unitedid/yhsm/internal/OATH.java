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

package org.unitedid.yhsm.internal;

import org.unitedid.yhsm.YubiHSM;
import org.unitedid.yhsm.utility.IntRange;

import java.text.DecimalFormat;
import java.util.Arrays;

import static org.unitedid.yhsm.internal.Defines.YSM_TEMP_KEY_HANDLE;
import static org.unitedid.yhsm.utility.Utils.*;

/** <code>OATH</code> implements OATH HOTP/TOTP validation*/
public class OATH {

    /** Private constructor */
    private OATH() {}

    /**
     * Validate OTP by a token whose seed is available to the YubiHSM through an AEAD.
     *
     * @param hsm the current hsm object
     * @param keyHandle a keyHandle with the permission YSM_TEMP_KEY_LOAD enabled
     * @param nonce the nonce used to generate the AEAD
     * @param aead the AEAD based on the token seed
     * @param counter the current OTP counter
     * @param otp the token OTP

     * @return return next counter value on success, 0 if the OTP couldn't be validated
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static synchronized String HOTP(YubiHSM hsm, int keyHandle, String nonce, String aead,
                                               int counter, String otp)
            throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        int otpLength = otp.trim().length();
        if (otpLength < 6 || otpLength > 8) {
            throw new YubiHSMInputException(
                    "OTP not of required length, should be between 6-8 digits long but was " + otpLength);
        }

        hsm.loadTemporaryKey(nonce, keyHandle, aead);

        String hmac = hsm.generateHMACSHA1(longToByteArray(counter), YSM_TEMP_KEY_HANDLE, true, false).get("hash");
        String code = truncate(hmac, otpLength);

        return code;
    }

    /**
     * Validate OATH-HOTP OTP by a token whose seed is available to the YubiHSM through an AEAD.
     *
     * @param hsm the current hsm object
     * @param keyHandle a keyHandle with the permission YSM_TEMP_KEY_LOAD enabled
     * @param nonce the nonce used to generate the AEAD
     * @param aead the AEAD based on the token seed
     * @param counter the current OTP counter
     * @param otp the token OTP
     * @param lookAhead the number of iterations to run to find the current users OTP
     * @return return next counter value on success, 0 if the OTP couldn't be validated
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static int validateHOTP(YubiHSM hsm, int keyHandle, String nonce, String aead, int counter,
                                   String otp, int lookAhead)
            throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {

        lookAhead += counter;
        for (; counter < lookAhead; counter++) {
            if (HOTP(hsm, keyHandle, nonce, aead, counter, otp).equals(otp)) {
                return counter + 1;
            }
        }

        return 0;
    }

    /**
     * Validate OATH-TOTP OTP by a token whose seed is available to the YubiHSM through an AEAD.
     *
     * @param hsm the current hsm object
     * @param keyHandle a keyHandle with the permission YSM_TEMP_KEY_LOAD enabled
     * @param nonce the nonce used to generate the AEAD
     * @param aead the AEAD based on the token seed
     * @param otp the token OTP
     * @param period an integer giving the period between changes of the OTP value in seconds
     * @param drift drift of the local clock to the client clock, can be used to adjust the time skew without
     *              changing the size of @backwardDrift and @forwardDrift
     * @param backwardDrift the number of @period's we allow to backstep
     * @param forwardDrift the number of @period's we allow to look ahead
     * @return return boolean, true if the OTP validated, false if the OTP validation failed
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static boolean validateTOTP(YubiHSM hsm, int keyHandle, String nonce, String aead, String otp,
                                   int period, int drift, int backwardDrift, int forwardDrift)
            throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        long time = System.currentTimeMillis() / 1000;
        int window = -(int) (time / period);
        for (int i : new IntRange(Math.max(window, -backwardDrift), forwardDrift + 1)) {
            int timeDrift = (drift + i) * period;
            int t = (int) ((time + timeDrift) / period);
            if (HOTP(hsm, keyHandle, nonce, aead, t, otp).equals(otp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Truncate HMAC to an OTP code
     *
     * @param hmac the hmac
     * @param otpLength the length of the OTP (6-8 digits)
     * @return the OTP code
     * @throws YubiHSMInputException argument exceptions
     */
    public static String truncate(String hmac, int otpLength) throws YubiHSMInputException {
        byte[] hmacBA = hexToByteArray(hmac);
        validateByteArray("hmacBA", hmacBA, 0, 20, 0);

        int offset = hmacBA[19] & 0xf;
        int binCode = (hmacBA[offset] & 0x7f) << 24 |
                (hmacBA[offset+1] & 0xff) << 16 |
                (hmacBA[offset+2] & 0xff) << 8 |
                (hmacBA[offset+3] & 0xff);

        // Mmm leading zeros is a pain
        char[] zeros = new char[otpLength];
        Arrays.fill(zeros, '0');
        DecimalFormat decimalFormat = new DecimalFormat(String.valueOf(zeros));

        return decimalFormat.format(binCode % (Math.pow(10, otpLength)));
    }
}
