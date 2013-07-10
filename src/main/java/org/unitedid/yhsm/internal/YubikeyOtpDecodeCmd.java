/*
 * Copyright (c) 2011 Yubico AB.
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

import static org.unitedid.yhsm.internal.Defines.*;
import static org.unitedid.yhsm.utility.Utils.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fredrik Thulin <fredrik@yubico.com>
 */
public class YubikeyOtpDecodeCmd {

    /** Constructor */
    private YubikeyOtpDecodeCmd() {}

    /**
     * Load the content of an AEAD into the phantom key handle 0xffffffff.
     *
     * @param device the device handler
     * @param publicId the public id
     * @param keyHandle the key handle with permission to use YSM_TEMP_KEY_LOAD
     * @param aead the AEAD to load into the phantom key handle
     * @return returns true if the AEAD was successfully loaded
     * @throws YubiHSMCommandFailedException command fail exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException argument exceptions
     */
    public static Map<String, Integer> execute(DeviceHandler device, String publicId, int keyHandle, String aead, String otp) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        byte[] publicIdBA = validateNonce(hexToByteArray(publicId), true);
        byte[] otpBA = validateByteArray("otp", hexToByteArray(otp), 0, YSM_OTP_SIZE, 0);
        byte[] aeadBA = validateByteArray("aead", hexToByteArray(aead), 0, YSM_YUBIKEY_AEAD_SIZE, 0);
        byte[] cmdBuffer = concatAllArrays(publicIdBA, leIntToBA(keyHandle), otpBA, aeadBA);
        byte[] result = CommandHandler.execute(device, YSM_AEAD_YUBIKEY_OTP_DECODE, cmdBuffer, true);

        return parseResult(result, publicIdBA, keyHandle);
    }
    /**
     *  Parse the response from the YubiHSM for a previous command.
     *
     * @param data the result from the last command
     * @param publicIdBA the public id in byte array format
     * @param keyHandle the key handle with permission to use YSM_TEMP_KEY_LOAD
     * @return boolean indicating success
     * @throws YubiHSMCommandFailedException command failed exception
     */
    private static Map<String, Integer> parseResult(byte[] data, byte[] publicIdBA, int keyHandle) throws YubiHSMCommandFailedException, YubiHSMErrorException {
        Map<String, Integer> result = new HashMap<String, Integer>();

        byte status = data[YSM_PUBLIC_ID_SIZE + 4 + 2 + 1 + 1 + 2];
        if (status == YSM_STATUS_OK) {
            validateCmdResponseBA("publicId", rangeOfByteArray(data, 0, YSM_PUBLIC_ID_SIZE), publicIdBA);
            validateCmdResponseBA("keyHandle", rangeOfByteArray(data, YSM_PUBLIC_ID_SIZE, 4), leIntToBA(keyHandle));
            int useCtr = leBAToBeShort(rangeOfByteArray(data, YSM_PUBLIC_ID_SIZE + 4, 2));
            int sessionCtr = data[YSM_PUBLIC_ID_SIZE + 4 + 2] & 0xff;
            int timestampHigh = data[YSM_PUBLIC_ID_SIZE + 4 + 2 + 1] & 0xff;
            int timestampLow = leBAToBeShort(rangeOfByteArray(data, YSM_PUBLIC_ID_SIZE + 4 + 2 + 1 + 1, 2));
            result.put("useCtr", useCtr);
            result.put("sessionCtr", sessionCtr);
            result.put("tsHigh", timestampHigh);
            result.put("tsLow", timestampLow);
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(YSM_AEAD_YUBIKEY_OTP_DECODE) + " failed: " + getCommandStatus(status));
        }

        return result;
    }
}

