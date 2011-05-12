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

public class BufferCmd {

    private BufferCmd() {}

    public static int loadData(DeviceHandler device, String data, int offset) {
        int dataLength = data.getBytes().length;
        byte[] off = {(byte) ((offset << 24) >> 24)};

        byte[] cmdBuffer = Utils.concatAllArrays(off, Utils.addLengthToData(data.getBytes()));
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_LOAD, cmdBuffer, true);

        return parseResult(offset, dataLength, result);
    }

    public static int loadRandomData(DeviceHandler device, int size, int offset) {
        byte[] cmdBuffer = {(byte) ((offset << 24) >> 24), (byte) ((size << 24) >> 24)};
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_RANDOM_LOAD, cmdBuffer, true);

        return parseResult(offset, size, result);
    }

    private static int parseResult(int offset, int dataLength, byte[] data) {
        int count = data[0];
        if (offset == 0) {
            if (count != dataLength) {
                System.out.println("Incorrect number of bytes in buffer, got " + count + ", expected " + dataLength);
            }
        }

        return count;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
