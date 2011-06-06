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

import org.unitedid.yhsm.YubiHSM;
import org.unitedid.yhsm.utility.Utils;

import java.util.HashMap;
import java.util.Map;

/** <code>AEADCmd</code> implements AEAD commands for the YubiHSM */
public class AEADCmd {

    /** Constructor */
    private AEADCmd() {}

    /**
     * Generate AEADCmd block from data for a specific key and nonce.
     *
     * @param device the YubiHSM device handler
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @param data is either a string or a YubiHSM YubiKey secret
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static Map<String, String> generateAEAD(DeviceHandler device, String nonce, int keyHandle, String data) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] newdata = Utils.validateByteArray("data", data.getBytes(), 0, 0, YubiHSM.minHashLength);
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle), Utils.addLengthToData(newdata));
        byte[] result = CommandHandler.execute(device, Defines.YSM_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle, Defines.YSM_AEAD_GENERATE);
    }

    /**
     * Generate a random AEAD block using the YubiHSM internal TRNG.
     * To generate a secret for a YubiKey use public_id as nonce.
     *
     * @param device the YubiHSM device handler
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param size the resulting byte length of the AEAD
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static Map<String, String> generateRandomAEAD(DeviceHandler device, String nonce, int keyHandle, int size) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] len = {(byte) ((size << 24) >> 24)};
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle), len);
        byte[] result = CommandHandler.execute(device, Defines.YSM_RANDOM_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle, Defines.YSM_RANDOM_AEAD_GENERATE);
    }

    /**
     * Generate AEAD block of data buffer for a specific key.
     * After a key has been loaded into the internal data buffer, this command can be
     * used a number of times to get AEADs of the data buffer for different key handles.
     * For example, to encrypt a YubiKey secrets to one or more Yubico KSM's that
     * all have a YubiHSM attached to them.
     *
     * @param device the YubiHSM device handler
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMInputException argmuent exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static Map<String, String> generateBufferAEAD(DeviceHandler device, String nonce, int keyHandle) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle));
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle, Defines.YSM_BUFFER_AEAD_GENERATE);
    }

    /**
     *  Validate an AEAD using the YubiHSM, matching it against some known plain text.
     *  Matching is done inside the YubiHSM so the decrypted AEAD is never exposed.
     *
     * @param device the YubiHSM device
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param aead the AEAD
     * @param plaintext the plain text string
     * @return returns true if validation was a success, false if the validation failed
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static boolean validateAEAD(DeviceHandler device, String nonce, int keyHandle, String aead, String plaintext) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] aeadBA = Utils.validateByteArray("aead", Utils.hexToByteArray(aead), Defines.YSM_MAX_KEY_SIZE + Defines.YSM_AEAD_MAC_SIZE, 0, 0);
        byte[] plainBA = Utils.validateByteArray("plaintext", plaintext.getBytes(), 0, aeadBA.length - Defines.YSM_AEAD_MAC_SIZE, YubiHSM.minHashLength);
        byte[] plainAndAead = Utils.concatAllArrays(plainBA, aeadBA);
        byte[] nonceBA = Utils.validateNonce(Utils.hexToByteArray(nonce), true);
        byte[] cmdBuffer = Utils.concatAllArrays(nonceBA, Utils.leIntToBA(keyHandle), Utils.addLengthToData(plainAndAead));
        byte[] result = CommandHandler.execute(device, Defines.YSM_AEAD_DECRYPT_CMP, cmdBuffer, true);

        return parseValidationResult(result, nonce, keyHandle);
    }

    /**
     * Parse the response from the YubiHSM for a previous command.
     *
     * @param data the data from the YubiHSM
     * @param nonce the original nonce
     * @param keyHandle the key used to generate AEAD
     * @param command the YubiHSM command executed
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    private static Map<String, String> parseResult(byte[] data, String nonce, int keyHandle, byte command) throws YubiHSMCommandFailedException, YubiHSMErrorException {
        Map<String, String> result = new HashMap<String, String>();

        if (data[10] == Defines.YSM_STATUS_OK) {
            byte[] aead = Utils.rangeOfByteArray(data, Defines.YSM_AEAD_NONCE_SIZE + 6, data[11]);
            Utils.validateCmdResponseBA("keyHandle", Utils.rangeOfByteArray(data, 6, 4), Utils.leIntToBA(keyHandle));
            result.put("nonce", Utils.validateCmdResponseString("nonce",
                                Utils.byteArrayToHex(Utils.rangeOfByteArray(data, 0, Defines.YSM_AEAD_NONCE_SIZE)), nonce));
            result.put("aead", Utils.byteArrayToHex(aead));
        } else {
            throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(command) + " failed: " + Defines.getCommandStatus(data[10]));
        }

        return result;
    }

    /**
     * Parse the AEAD validation response from the YubiHSM.
     *
     * @param data the data from the YubiHSM
     * @param nonce the original nonce
     * @param keyHandle the key used to validate the AEAD
     * @return returns true if validation was a success, false if the validation failed
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    private static boolean parseValidationResult(byte[] data, String nonce, int keyHandle) throws YubiHSMCommandFailedException, YubiHSMErrorException {
        Utils.validateCmdResponseBA("keyHandle", Utils.rangeOfByteArray(data, Defines.YSM_AEAD_NONCE_SIZE, 4), Utils.leIntToBA(keyHandle));
        Utils.validateCmdResponseString("nonce", Utils.byteArrayToHex(new String(data, 0, Defines.YSM_AEAD_NONCE_SIZE).getBytes()), nonce);
        if (data[10] == Defines.YSM_STATUS_OK) {
            return true;
        } else if (data[10] == Defines.YSM_MISMATCH) {
            return false;
        } else {
            throw new YubiHSMCommandFailedException("Command " + Defines.getCommandString(Defines.YSM_AEAD_DECRYPT_CMP) + " failed: " + Defines.getCommandStatus(data[10]));
        }
    }
}
