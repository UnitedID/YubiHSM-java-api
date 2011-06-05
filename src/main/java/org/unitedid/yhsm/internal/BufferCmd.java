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

/** <code>BufferCmd</code> implements the internal buffer functions of the YubiHSM. */
public class BufferCmd {

    /** Private constructor */
    private BufferCmd() {}

    /**
     * Load data into the YubiHSMs internal buffer.
     *
     * @param device the device handler
     * @param data the data to load into the internal buffer
     * @param offset the offset where to load the data, if set to 0 the buffer will reset before loading the data
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     */
    public static int loadData(DeviceHandler device, String data, int offset) throws YubiHSMErrorException {
        int dataLength = data.getBytes().length;
        byte[] off = {(byte) ((offset << 24) >> 24)};

        byte[] cmdBuffer = Utils.concatAllArrays(off, Utils.addLengthToData(data.getBytes()));
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_LOAD, cmdBuffer, true);

        return parseResult(offset, dataLength, result);
    }

    /**
     * Load data into the YubiHSMs internal buffer.
     *
     * @param device the device handler
     * @param data the data to load into the internal buffer
     * @param offset the offset where to load the data, if set to 0 the buffer will reset before loading the data
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     */
    public static int loadData(DeviceHandler device, byte[] data, int offset) throws YubiHSMErrorException {
        int dataLength = data.length;
        byte[] off = {(byte) ((offset << 24) >> 24)};
        byte[] cmdBuffer = Utils.concatAllArrays(off, Utils.addLengthToData(data));
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_LOAD, cmdBuffer, true);

        return parseResult(offset, dataLength, result);
    }

    /**
     * Load random data into the YubiHSMs internal buffer.
     *
     * @param device the device handler
     * @param length the length of the generated data
     * @param offset the offset where to load the data, if set to 0 the buffer will reset before loading the data
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     */
    public static int loadRandomData(DeviceHandler device, int length, int offset) throws YubiHSMErrorException {
        byte[] cmdBuffer = {(byte) ((offset << 24) >> 24), (byte) ((length << 24) >> 24)};
        byte[] result = CommandHandler.execute(device, Defines.YSM_BUFFER_RANDOM_LOAD, cmdBuffer, true);

        return parseResult(offset, length, result);
    }

    /**
     * Parse the response from the YubiHSM.
     *
     * @param offset the offset, if 0 it's used to do extra validation
     * @param dataLength the length, used for validation of the response if offset was set to
     * @param data the response from the YubiHSM
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if buffer length is not of expected length (can only be thrown if offset is set to 0)
     */
    private static int parseResult(int offset, int dataLength, byte[] data) throws YubiHSMErrorException {
        int count = data[0];
        if (offset == 0) {
            if (count != dataLength) {
                throw new YubiHSMErrorException("Incorrect number of bytes in buffer, got " + count + ", expected " + dataLength);
            }
        }

        return count;
    }
}
