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

/** <code>EchoCmd</code> implements the YubiHSM echo command. */
public class EchoCmd {

    /** Private constructor */
    private EchoCmd() {}

    /**
     * Test the YubiHSM by sending a string that the YubiHSM will echo back.
     *
     * @param device the device handler
     * @param data the string that the YubiHSM should return
     * @return the the same string sent to the YubiHSM
     * @throws YubiHSMErrorException if the YubiHSM command failed to execute
     */
    public static String execute(DeviceHandler device, String data) throws YubiHSMErrorException {
        byte[] buffer = CommandHandler.execute(device, Defines.YSM_ECHO, Utils.addLengthToData(data.getBytes()), true);
        return parseResult(buffer);
    }

    /**
     * Parse the echo response from the YubiHSM.
     *
     * @param data the result from the YubiHSM
     * @return the string echoed
     */
    private static String parseResult(byte[] data) {
        return new String(data, 1, data[0]);
    }
}
