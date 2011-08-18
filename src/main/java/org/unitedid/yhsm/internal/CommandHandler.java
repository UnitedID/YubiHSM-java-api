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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.unitedid.yhsm.internal.Defines.*;
import static org.unitedid.yhsm.utility.Utils.*;

public class CommandHandler {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    private CommandHandler() {}

    protected static synchronized byte[] execute(DeviceHandler device, byte command, byte[] data, boolean readResponse) throws YubiHSMErrorException {
        byte[] cmdBuffer;

        if (command != YSM_NULL) {
            cmdBuffer = new byte[]{(byte) (((data.length + 1) << 24) >> 24), command};
        } else {
            cmdBuffer = new byte[]{command};
        }

        log.debug("CMD BUFFER: {}", byteArrayToHex(concatAllArrays(cmdBuffer, data)));
        device.write(concatAllArrays(cmdBuffer, data));

        try {
            if (!readResponse) {
                Thread.sleep(10); // We just sleep for safety since we cant check if we got any output
                return null;
            }

            int sleptTime = 0;
            int timeout = 100; // We enforce a default just in case (100 ms)
            if (device.getTimeout() > 0) {
                timeout = (int) (device.getTimeout() * 1000); // Need milliseconds
            }

            log.debug("CommandHandler ({}) timeout set to: {} ms ", getCommandString(command), timeout);

            while (sleptTime <= timeout) {
                Thread.sleep(1);
                sleptTime += 1;
                if (device.available() > 0) {
                    break;
                }
            }
            log.debug("CommandHandler slept for: {} ms", sleptTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return readDevice(device, command);
    }

    private static byte[] readDevice(DeviceHandler device, byte command) throws YubiHSMErrorException {
        byte[] result = new byte[0];

        if (device.available() > 0) {
            result = device.read(2);
        }
        if (result.length == 0) {
            reset(device);
            throw new YubiHSMErrorException("No data received from the YubiHSM!");
        }

        if ((result[1] & YSM_RESPONSE) != 0) {
            log.debug("Got response from ({}) {}", result[1], getCommandString((byte) (result[1] - YSM_RESPONSE)));
        }

        if (result[1] == (command | YSM_RESPONSE)) {
            int len = (int)result[0] - 1;
            return device.read(len);
        } else {
            reset(device);
            throw new YubiHSMErrorException("YubiHSM responded to the wrong command. Expected " + getCommandString(command) + " but got " + getCommandString((byte) (result[1] - YSM_RESPONSE)));
        }
    }

    public static void reset(DeviceHandler device) throws YubiHSMErrorException {
        byte[] reset = new byte[YSM_MAX_PKT_SIZE - 1];
        for (int i=0; i < YSM_MAX_PKT_SIZE - 1; i++) {
            reset[i] = 0x00;
        }
        execute(device, YSM_NULL, reset, false);
    }
}
