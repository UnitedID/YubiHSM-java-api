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

/** <code>MonitorExitCmd</code> is used to send magics to the HSM in debug mode and get it to exit to configuration mode. */
public class MonitorExitCmd {

    /**
     * Send the magics to the HSM to get it to exit to configuration mode.
     *
     * @param deviceHandler the device handler
     * @throws YubiHSMErrorException if the YubiHSM command failed to execute
     */
    public static void execute(DeviceHandler deviceHandler) throws YubiHSMErrorException {
        byte[] data = concatAllArrays(leIntToBA(0xbaadbeef), leIntToBA(0xffffffff - 0xbaadbeef));
        CommandHandler.execute(deviceHandler, YSM_MONITOR_EXIT, data, false);
    }
}
