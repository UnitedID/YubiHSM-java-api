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

import static org.unitedid.yhsm.internal.Defines.*;
import static org.unitedid.yhsm.utility.Utils.*;

/** <code>LoadTemporaryKeyCmd</code> implements loading AEAD into the phantom key handle */
public class LoadTemporaryKeyCmd {

    /** Private constructor */
    private LoadTemporaryKeyCmd() {}

    /**
     * Load the content of an AEAD into the phantom key handle 0xffffffff.
     *
     * @param device the device handler
     * @param nonce the nonce
     * @param keyHandle the key handle with permission to use YSM_TEMP_KEY_LOAD
     * @param aead the AEAD to load into the phantom key handle
     * @return returns true if the AEAD was successfully loaded
     * @throws YubiHSMCommandFailedException command fail exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException argument exceptions
     */
    public static boolean execute(DeviceHandler device, String nonce, int keyHandle, String aead) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        byte[] nonceBA = validateNonce(hexToByteArray(nonce), true);
        int maxAeadLength = YSM_MAX_KEY_SIZE + 4 + YSM_AEAD_MAC_SIZE;
        byte[] aeadBA = validateByteArray("aead", hexToByteArray(aead), maxAeadLength, 0, 0);
        byte[] cmdBuffer = concatAllArrays(nonceBA, leIntToBA(keyHandle), addLengthToData(aeadBA));
        byte[] result = CommandHandler.execute(device, YSM_TEMP_KEY_LOAD, cmdBuffer, true);

        return parseResult(result, nonce, keyHandle);
    }

    /**
     * Parse the response from the YubiHSM
     *
     * @param result the data from the YubiHSM
     * @param nonce the original nonce
     * @param keyHandle the original key handle
     * @return return true if the command completed successfully
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMCommandFailedException command failed exception
     */
    private static boolean parseResult(byte[] result, String nonce, int keyHandle) throws YubiHSMErrorException, YubiHSMCommandFailedException {
        if (result[10] == YSM_STATUS_OK) {
            validateCmdResponseString("nonce", byteArrayToHex(rangeOfByteArray(result, 0, YSM_AEAD_NONCE_SIZE)), nonce);
            validateCmdResponseBA("keyHandle", rangeOfByteArray(result, 6, 4), leIntToBA(keyHandle));
            return true;
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(YSM_TEMP_KEY_LOAD) + " failed: " + getCommandStatus(result[10]));
        }
    }
}
