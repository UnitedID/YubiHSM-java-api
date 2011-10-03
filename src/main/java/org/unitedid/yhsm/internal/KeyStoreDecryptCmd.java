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

public class KeyStoreDecryptCmd {

    /** Constructur */
    private KeyStoreDecryptCmd() {}

    public static boolean execute(DeviceHandler device, String key) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        byte[] keyBA = hexToByteArray(key);
        keyBA = validateByteArray("key", keyBA, YSM_MAX_KEY_SIZE, 0, YSM_MAX_KEY_SIZE);
        return parseResult(CommandHandler.execute(device, YSM_KEY_STORE_DECRYPT, keyBA, true));
    }

    private static boolean parseResult(byte[] result) throws YubiHSMCommandFailedException {
        if (result[0] == YSM_STATUS_OK) {
            return true;
        } else if (result[0] == YSM_MISMATCH) {
            return false;
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(YSM_KEY_STORE_DECRYPT) + " failed: " + getCommandStatus(result[0]));
        }
    }
}
