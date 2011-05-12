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

public class EchoCmd {

    private EchoCmd() {}

    public static String execute(DeviceHandler device, String data) {
        byte[] buffer = CommandHandler.execute(device, Defines.YSM_ECHO, Utils.addLengthToData(data.getBytes()), true);
        return parseResult(buffer);
    }

    private static String parseResult(byte[] data) {
        return new String(data, 1, data[0]);
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
