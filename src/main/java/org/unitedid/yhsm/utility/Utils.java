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

package org.unitedid.yhsm.utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Utils {

    private Utils() {}

    public static byte[] addLengthToData(byte[] data) {
        byte[] len = {(byte) ((data.length << 24) >> 24)};
        byte[] buffer = Arrays.copyOf(len, len.length + data.length);
        System.arraycopy(data, 0, buffer, len.length, data.length);

        return buffer;
    }


    public static byte[] concatAllArrays(byte[] data1, byte[]... remaining) {
        int bufLength = data1.length;

        for (byte[] arr : remaining) {
            if (arr.length == 0) {
                return data1;
            }

            bufLength += arr.length;
        }

        int offset = data1.length;
        byte[] buffer = Arrays.copyOf(data1, bufLength);

        for (byte[] arr : remaining) {
            System.arraycopy(arr, 0, buffer, offset, arr.length);
            offset += arr.length;
        }

        return buffer;
    }

    public static byte[] leIntToBA(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);

        return buffer.array();
    }

    public static byte[] intToByteArray(int value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(out);
        try {
            dout.writeInt(value);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public static byte[] rangeOfByteArray(byte[] data, int offset, int length) {
        byte[] buffer = new byte[length];
        for (int a = 0; a < length; a++, offset++) {
            buffer[a] = data[offset];
        }

        return buffer;
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] hexToByteArray(String hex) {
        byte data[] = new byte[hex.length()/2];
        for(int i=0;i < hex.length();i+=2) {
            data[i/2] = (Integer.decode("0x"+hex.charAt(i)+hex.charAt(i+1))).byteValue();
        }
        return data;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
