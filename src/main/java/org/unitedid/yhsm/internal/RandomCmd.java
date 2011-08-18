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

/** <code>RandomCmd</code> implements YubiHSM random generator. */
public class RandomCmd {

    /** Private constructor */
    private RandomCmd() {}

    /**
     * Tell the YubiHSM to generate a number of random bytes.
     *
     * @param device the YubiHSM device
     * @param bytes the number of bytes to generate
     * @return returns a byte array of random bytes
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException invalid argument exception
     */
    public static byte[] execute(DeviceHandler device, int bytes) throws YubiHSMErrorException, YubiHSMInputException {
        if (bytes > 0 && bytes > YSM_MAX_PKT_SIZE - 1) {
            throw new YubiHSMInputException("'Bytes' int too large, expected less than or equal to " + (YSM_MAX_PKT_SIZE -1) + " but got " + bytes);
        }
        byte[] cmdBuffer = new byte[]{(byte) bytes};
        byte[] result = CommandHandler.execute(device, YSM_RANDOM_GENERATE, cmdBuffer, true);
        return parseResult(result);
    }

    /**
     * Parse the response from the YubiHSM
     *
     * @param data the result from the YubiHSM
     * @return returns a byte array of random bytes
     */
    private static byte[] parseResult(byte[] data) {
        int bytes = data[0];
        return rangeOfByteArray(data, 1, bytes);
    }
}
