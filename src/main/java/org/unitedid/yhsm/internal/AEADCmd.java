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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AEADCmd {

    private AEADCmd() {

    }

    /**
     * Generate AEADCmd block from data for a specific key and nonce.
     * If the nonce is all zero the YubiHSM will generate a unique nonce
     * which is returned in the response.
     *
     * @param device
     * @param nonce
     * @param keyHandle
     * @param data
     * @return
     * @throws Exception
     */
    public static Map<String, String> generateAEAD(DeviceHandler device, String nonce, int keyHandle, String data) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);

        //TODO: Fix this properly. Padding data like this for now.
        byte[] newdata = Arrays.copyOf(data.getBytes(), 20);
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle), Utils.addLengthToData(newdata));
        byte[] result = CommandHandler.execute(device, Defines.YSM_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce);
    }

    public static Map<String, String> generateRandomAEAD(DeviceHandler device, String nonce, int keyHandle, int size) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] len = {(byte) ((size << 24) >> 24)};
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle), len);
        byte[] result = CommandHandler.execute(device, Defines.YSM_RANDOM_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce);
    }

    public static Map<String, String> generateBufferAEAD(DeviceHandler device, String nonce, int keyHandle) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle));
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce);
    }

    public static boolean validateAEAD(DeviceHandler device, String nonce, int keyHandle, String aead, String plaintext) throws YubiHSMInputException {
        byte[] newpt = Arrays.copyOf(plaintext.getBytes(), 20);
        byte[] aeadnew = Utils.hexToByteArray(aead);
        byte[] plainAndAead = Utils.concatAllArrays(newpt, aeadnew);
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle), Utils.addLengthToData(plainAndAead));
        byte[] result = CommandHandler.execute(device, Defines.YSM_AEAD_DECRYPT_CMP, cmdBuffer, true);

        return parseValidationResult(result);
    }

    private static Map<String, String> parseResult(byte[] data, String nonce) throws YubiHSMCommandFailedException, YubiHSMErrorException {
        Map<String, String> result = new HashMap<String, String>();

        if (data[10] == Defines.YSM_STATUS_OK) {
            byte[] aead = Utils.rangeOfByteArray(data, Defines.YSM_AEAD_NONCE_SIZE + 6, data[11]);
            result.put("nonce", Utils.validateCmdResponseString("nonce",
                                Utils.byteArrayToHex(new String(data, 0, Defines.YSM_AEAD_NONCE_SIZE).getBytes()), nonce));

            result.put("aead", Utils.byteArrayToHex(aead));
        } else {
            throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(Defines.YSM_AEAD_GENERATE) + " failed: " + Defines.getCommandStatus(data[10]));
        }

        return result;
    }

    private static boolean parseValidationResult(byte[] data) {
        try {
            if (data[10] == Defines.YSM_STATUS_OK) {
                return true;
            } else if (data[10] == Defines.YSM_MISMATCH) {
                return false;
            } else {
                throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(Defines.YSM_AEAD_DECRYPT_CMP) + " failed: " + Defines.getCommandStatus(data[10]));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
