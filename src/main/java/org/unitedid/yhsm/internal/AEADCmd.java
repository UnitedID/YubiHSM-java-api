/*
 * Copyright (c) 2011 United ID.
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

import java.util.HashMap;
import java.util.Map;

import static org.unitedid.yhsm.internal.Defines.*;
import static org.unitedid.yhsm.utility.Utils.*;


/**
 * <code>AEADCmd</code> implements AEAD commands for the YubiHSM
 */
public class AEADCmd {

    /** Constructor */
    private AEADCmd() {}

    /**
     * Generate AEADCmd block from data for a specific key and nonce.
     *
     * @param device the YubiHSM device handler
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @param data is the byte array to turn into an AEAD
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static Map<String, String> generateAEAD(DeviceHandler device, String nonce, int keyHandle, byte[] data) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = validateNonce(hexToByteArray(nonce), true);
        byte[] newdata = validateByteArray("data", data, 0, 0, YubiHSM.minHashLength);
        byte[] cmdBuffer = concatAllArrays(nonceBA, leIntToBA(keyHandle), addLengthToData(newdata));
        byte[] result = CommandHandler.execute(device, YSM_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle, YSM_AEAD_GENERATE);
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
        byte[] nonceBA = validateNonce(hexToByteArray(nonce), true);
        byte[] len = {(byte) ((size << 24) >> 24)};
        byte[] cmdBuffer = concatAllArrays(nonceBA, leIntToBA(keyHandle), len);
        byte[] result = CommandHandler.execute(device, YSM_RANDOM_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle, YSM_RANDOM_AEAD_GENERATE);
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
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static Map<String, String> generateBufferAEAD(DeviceHandler device, String nonce, int keyHandle) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] nonceBA = validateNonce(hexToByteArray(nonce), true);
        byte[] cmdBuffer = concatAllArrays(nonceBA, leIntToBA(keyHandle));
        byte[] result = CommandHandler.execute(device, YSM_BUFFER_AEAD_GENERATE, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle, YSM_BUFFER_AEAD_GENERATE);
    }

    /**
     *  Validate an AEAD using the YubiHSM, matching it against some known plain text.
     *  Matching is done inside the YubiHSM so the decrypted AEAD is never exposed.
     *
     * @param device the YubiHSM device
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param aead the AEAD (hex string)
     * @param plaintext the plain text data
     * @return returns true if validation was a success, false if the validation failed
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public static boolean validateAEAD(DeviceHandler device, String nonce, int keyHandle, String aead, byte[] plaintext) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        byte[] aeadBA = hexToByteArray(aead);
        byte[] plainBA = validateByteArray("plaintext", plaintext, 0, aeadBA.length - YSM_AEAD_MAC_SIZE, YubiHSM.minHashLength);
        byte[] plainAndAead = concatAllArrays(plainBA, aeadBA);
        if (plainAndAead.length > (YSM_MAX_PKT_SIZE - 0x10))
            throw new YubiHSMInputException("Plaintext+aead too long");
        byte[] nonceBA = validateNonce(hexToByteArray(nonce), true);
        byte[] cmdBuffer = concatAllArrays(nonceBA, leIntToBA(keyHandle), addLengthToData(plainAndAead));
        byte[] result = CommandHandler.execute(device, YSM_AEAD_DECRYPT_CMP, cmdBuffer, true);

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

        if (data[10] == YSM_STATUS_OK) {
            byte[] aead = rangeOfByteArray(data, YSM_AEAD_NONCE_SIZE + 6, data[11]);
            validateCmdResponseBA("keyHandle", rangeOfByteArray(data, 6, 4), leIntToBA(keyHandle));
            result.put("nonce", validateCmdResponseString("nonce",
                                byteArrayToHex(rangeOfByteArray(data, 0, YSM_AEAD_NONCE_SIZE)), nonce));
            result.put("aead", byteArrayToHex(aead));
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(command) + " failed: " + getCommandStatus(data[10]));
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
        validateCmdResponseBA("keyHandle", rangeOfByteArray(data, YSM_AEAD_NONCE_SIZE, 4), leIntToBA(keyHandle));
        validateCmdResponseString("nonce", byteArrayToHex(new String(data, 0, YSM_AEAD_NONCE_SIZE).getBytes()), nonce);
        if (data[10] == YSM_STATUS_OK) {
            return true;
        } else if (data[10] == YSM_MISMATCH) {
            return false;
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(YSM_AEAD_DECRYPT_CMP) + " failed: " + getCommandStatus(data[10]));
        }
    }
}
