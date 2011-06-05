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

package org.unitedid.yhsm.internal;

import org.unitedid.yhsm.utility.Utils;

import java.util.HashMap;
import java.util.Map;

/** <code>HMACCmd</code> implements the HMAC SHA1 commands for the YubiHSM. */
 public class HMACCmd {

    /** Private constructor */
    private HMACCmd() {}

    /**
     * Generate HMAC SHA1 using a key handle in the YubiHSM.
     *
     * @param deviceHandler the device handler
     * @param data the data used to generate the SHA1
     * @param keyHandle the key handle to use in the YubiHSM
     * @param flags the commands flags, send (byte) 0 to use defaults
     * @param last set to false to not get a hash generated for the initial request
     * @param toBuffer set to true to get the SHA1 stored into the internal buffer, for use in some other cryptographic operations.
     * @return a map containing status and SHA1 hash
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public static Map<String, String> generateHMACSHA1(DeviceHandler deviceHandler, String data, int keyHandle, byte flags, boolean last, boolean toBuffer) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        byte [] dataBA = Utils.validateByteArray("data", data.getBytes(), Defines.YSM_MAX_PKT_SIZE - 6, 0, 0);

        if (flags == 0) {
            flags = Defines.YSM_HMAC_SHA1_RESET;
            if (last) {
                flags |= Defines.YSM_HMAC_SHA1_FINAL;
            }
            if (toBuffer) {
                flags |= Defines.YSM_HMAC_SHA1_TO_BUFFER;
            }
        }

        byte[] flagsBA = { flags };
        byte[] cmdBuffer = Utils.concatAllArrays(Utils.leIntToBA(keyHandle), flagsBA, Utils.addLengthToData(dataBA));
        byte[] result = CommandHandler.execute(deviceHandler, Defines.YSM_HMAC_SHA1_GENERATE, cmdBuffer, true);

        return parseResult(result, keyHandle, last);
    }

    /**
     * Add more input to the HMAC SHA1.
     *
     * @param deviceHandler the device handler
     * @param data the data to add before generating SHA1
     * @param keyHandle the key handle to use in the YubiHSM
     * @param last set to false to not get a hash generated after this call
     * @param toBuffer set to true to get the SHA1 stored into the internal buffer, for use in some other cryptographic operations.
     * @return a map containing status and SHA1 hash
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public static Map<String, String> next(DeviceHandler deviceHandler, String data, int keyHandle, boolean last, boolean toBuffer) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        byte flags;
        if (last) {
            flags = Defines.YSM_HMAC_SHA1_FINAL;
        } else {
            flags = 0x0;
        }
        if (toBuffer) {
            flags |= Defines.YSM_HMAC_SHA1_TO_BUFFER;
        }

        byte[] flagsBA = { flags };
        byte[] cmdBuffer = Utils.concatAllArrays(Utils.leIntToBA(keyHandle), flagsBA, Utils.addLengthToData(data.getBytes()));
        byte[] result = CommandHandler.execute(deviceHandler, Defines.YSM_HMAC_SHA1_GENERATE, cmdBuffer, true);

        return parseResult(result, keyHandle, last);
    }


    /**
     * Parse the response from the YubiHSM for a previous command.
     *
     * @param data the data from the YubiHSM
     * @param keyHandle the key handle used for the command
     * @param last the boolean if this was the final request
     * @return a map containing status and SHA1 hash
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    private static Map<String, String> parseResult(byte[] data, int keyHandle, boolean last) throws YubiHSMErrorException, YubiHSMCommandFailedException, YubiHSMInputException {
        Map<String, String> result = new HashMap<String, String>();
        if (data[4] == Defines.YSM_STATUS_OK) {
            Utils.validateCmdResponseBA("keyHandle", Utils.rangeOfByteArray(data, 0, 4), Utils.leIntToBA(keyHandle));
            if (last) {
                result.put("status", "OK");
                result.put("hash", Utils.byteArrayToHex(Utils.rangeOfByteArray(data, 6, data[5])));
            } else {
                byte[] zeroHash = { 0x00 };
                zeroHash = Utils.validateByteArray("zeroHash", zeroHash, 0, 0, 20);
                result.put("status", "Expect more data");
                result.put("hash", Utils.byteArrayToHex(zeroHash));
            }
        } else {
            throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(Defines.YSM_HMAC_SHA1_GENERATE) + " failed: " + Defines.getCommandStatus(data[4]));
        }

        return result;
    }
}
