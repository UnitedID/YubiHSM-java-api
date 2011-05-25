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

import java.util.HashMap;
import java.util.Map;

public class SystemInfoCmd {

    private SystemInfoCmd() {}

    public static Map<String, String> execute(DeviceHandler device) {
        byte[] empty = new byte[0];
        byte[] result = CommandHandler.execute(device, Defines.YSM_SYSTEM_INFO_QUERY, empty, true);

        return parseResult(result);
    }

    private static Map<String, String> parseResult(byte[] data) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("major", String.valueOf(data[0]));
        result.put("minor", String.valueOf(data[1]));
        result.put("build", String.valueOf(data[2]));
        result.put("protocol", String.valueOf(data[3]));
        result.put("sysid", "0x" + Utils.byteArrayToHex(new String(data, 4, 12).getBytes()));

        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
