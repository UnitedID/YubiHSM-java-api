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

/** <code>RandomReseedCmd</code> implements the random reseed command for the YubiHSM. */
public class RandomReseedCmd {

    /** Private constructor */
    private RandomReseedCmd() {}

    /**
     *  Provide YubiHSM DRBG_CTR with a new seed.
     *  The seed is a string of a length 32.
     *
     * @param device the YubiHSM device
     * @param seed the seed string with a length of 32
     * @return return true on success, otherwise a YubiHSMCommandFailedException is thrown
     * @throws YubiHSMInputException argument exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMCommandFailedException command failed exception
     */
    public static boolean execute(DeviceHandler device, String seed) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        seed = validateString("seed", seed, 0, YSM_CTR_DRBG_SEED_SIZE);
        byte[] result = CommandHandler.execute(device, YSM_RANDOM_RESEED, seed.getBytes(), true);

        return parseResult(result);
    }

    /**
     * Parse the result from the YubiHSM
     *
     * @param data the result from the YubiHSM
     * @return return true on success, otherwise a YubiHSMCommandFailedException is thrown
     * @throws YubiHSMCommandFailedException command failed exception
     */
    private static boolean parseResult(byte[] data) throws YubiHSMCommandFailedException {
        if (data[0] == YSM_STATUS_OK) {
            return true;
        } else {
            throw new YubiHSMCommandFailedException("Command " + getCommandString(YSM_RANDOM_RESEED) + " failed: " + getCommandStatus(data[0]));
        }
    }
}
