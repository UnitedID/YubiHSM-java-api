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

import static org.unitedid.yhsm.internal.Defines.*;
import static org.unitedid.yhsm.utility.Utils.*;

/** <code>NonceGetCmd</code> implements get nonce from the YubiHSM */
public class NonceGetCmd {

    /** Private constructor */
    private NonceGetCmd() {}

    /**
     * Get a nonce from the YubiHSM. Increment nonce by supplied number.
     * To get the current nonce send 0 as increment.
     *
     * @param device the YubiHSM device
     * @param increment the increment (short)
     * @return returns a Nonce class
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMCommandFailedException command failed exception
     */
    public static Nonce execute(DeviceHandler device, short increment) throws YubiHSMErrorException, YubiHSMCommandFailedException {
        byte[] result = CommandHandler.execute(device, YSM_NONCE_GET, leShortToByteArray(increment), true);

        return parseResult(result);
    }

    /**
     * Parse the response from the YubiHSM
     *
     * @param data the result from the YubiHSM
     * @return returns a Nonce class
     * @throws YubiHSMCommandFailedException command failed exception
     */
    private static Nonce parseResult(byte[] data) throws YubiHSMCommandFailedException {
        Nonce result = null;

        if (data[0] == YSM_STATUS_OK) {
            int vtile = leBAToBeInt(rangeOfByteArray(data, 1, 4));
            int powerUpCount = leBAToBeShort(rangeOfByteArray(data, 5, 2));
            int nonceInt = powerUpCount + vtile;
            String nonce =  byteArrayToHex(rangeOfByteArray(data, 1, YSM_AEAD_NONCE_SIZE));
            result = new Nonce(vtile, powerUpCount, nonceInt, nonce);
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(YSM_NONCE_GET) + " failed: " + getCommandStatus(data[0]));
        }

        return result;
    }
}
