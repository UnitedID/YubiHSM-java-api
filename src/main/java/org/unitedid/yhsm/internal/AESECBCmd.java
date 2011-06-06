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

/** <code>AESECBCmd</code> implements AES ECB block cipher commands for the YubiHSM. */
public class AESECBCmd {

    /** Private constructor */
    private AESECBCmd() {}

    /**
     * AES ECB encrypt a plaintext string using a specific key handle.
     *
     * @param deviceHandler the device handler
     * @param keyHandle the key handle to use when encrypting AES ECB
     * @param plaintext the plaintext string
     * @return a hash string in hex format
     * @throws YubiHSMInputException if an argument does not validate
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    public static String encrypt(DeviceHandler deviceHandler, int keyHandle, String plaintext) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        byte[] cmdBuffer = Utils.concatAllArrays(Utils.leIntToBA(keyHandle), Utils.validateByteArray("plaintext", plaintext.getBytes(), Defines.YSM_BLOCK_SIZE, 0, Defines.YSM_BLOCK_SIZE));
        byte[] result = CommandHandler.execute(deviceHandler, Defines.YSM_AES_ECB_BLOCK_ENCRYPT, cmdBuffer, true);

        return parseResult(result, keyHandle, Defines.YSM_AES_ECB_BLOCK_ENCRYPT, false);
    }

    /**
     * AES ECB decrypt a cipher text using a specific key handle.
     *
     * @param deviceHandler the device handler
     * @param keyHandle the key handle to use when decrypting AES ECB
     * @param cipherText the cipher string
     * @return a plaintext string
     * @throws YubiHSMInputException if an argument does not validate
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    public static String decrypt(DeviceHandler deviceHandler, int keyHandle, String cipherText) throws YubiHSMErrorException, YubiHSMInputException, YubiHSMCommandFailedException {
        byte[] cmdBuffer = Utils.concatAllArrays(Utils.leIntToBA(keyHandle), Utils.validateByteArray("cipherText", Utils.hexToByteArray(cipherText), 0, Defines.YSM_BLOCK_SIZE, 0));
        byte[] result = CommandHandler.execute(deviceHandler, Defines.YSM_AES_ECB_BLOCK_DECRYPT, cmdBuffer, true);

        return parseResult(result, keyHandle, Defines.YSM_AES_ECB_BLOCK_DECRYPT, true);
    }

    /**
     * AES ECB decrypt a cipher text using a specific key handle, and then compare it with the supplied plaintext.
     *
     * @param deviceHandler the device handler
     * @param keyHandle the key handle to use when comparing AES ECB cipher with plaintext
     * @param cipherText the cipher string
     * @param plaintext the plaintext string
     * @return true if successful, false if not successful
     * @throws YubiHSMInputException if an argument does not validate
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    public static boolean compare(DeviceHandler deviceHandler, int keyHandle, String cipherText, String plaintext) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        byte[] cipherTextBA = Utils.validateByteArray("cipherText", Utils.hexToByteArray(cipherText), 0, Defines.YSM_BLOCK_SIZE, 0);
        byte[] plaintextBA = Utils.validateByteArray("plaintext", plaintext.getBytes(), Defines.YSM_BLOCK_SIZE, 0, Defines.YSM_BLOCK_SIZE);
        byte[] keyHandleBA = Utils.leIntToBA(keyHandle);
        byte[] cmdBuffer = Utils.concatAllArrays(keyHandleBA, cipherTextBA, plaintextBA);
        byte[] result = CommandHandler.execute(deviceHandler, Defines.YSM_AES_ECB_BLOCK_DECRYPT_CMP, cmdBuffer, true);

        Utils.validateCmdResponseBA("keyHandle", Utils.rangeOfByteArray(result, 0, 4), keyHandleBA);

        if (result[4] == Defines.YSM_STATUS_OK) {
            return true;
        } else if (result[4] == Defines.YSM_MISMATCH) {
            return false;
        } else {
            throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(Defines.YSM_AES_ECB_BLOCK_DECRYPT_CMP) + " failed: " + Defines.getCommandStatus(result[4]));
        }
    }

    /**
     * Parse the response from the YubiHSM for a previous command.
     *
     * @param data the YubiHSM response data
     * @param keyHandle the key handle used for the command
     * @param command the YubiHSM command executed
     * @param decrypt a boolean used to toggle if we decrypt (if true plaintext is returned, otherwise a hex string)
     * @return a plaintext string or hex string
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    private static String parseResult(byte[] data, int keyHandle, byte command, boolean decrypt) throws YubiHSMErrorException, YubiHSMCommandFailedException {
        Utils.validateCmdResponseBA("keyHandle", Utils.rangeOfByteArray(data, 0, 4), Utils.leIntToBA(keyHandle));
        byte[] result = Utils.rangeOfByteArray(data, 4, Defines.YSM_BLOCK_SIZE);

        if (data[20] == Defines.YSM_STATUS_OK) {
            return decrypt ? new String(result).trim() : Utils.byteArrayToHex(result);
        } else {
            throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(command) + " failed: " + Defines.getCommandStatus(result[4]));
        }
    }
}
