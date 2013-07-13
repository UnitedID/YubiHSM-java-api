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

package org.unitedid.yhsm.utility;

import org.unitedid.yhsm.internal.Defines;
import org.unitedid.yhsm.internal.YubiHSMErrorException;
import org.unitedid.yhsm.internal.YubiHSMInputException;

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

    public static byte[] leShortToByteArray(short value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);

        return buffer.array();
    }

    public static int leBAToBeInt(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        return buffer.getInt();
    }

    public static int leBAToBeShort(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        return buffer.getShort();
    }


    public static byte[] rangeOfByteArray(byte[] data, int offset, int length) {
        byte[] buffer = new byte[length];
        for (int a = 0; a < length; a++, offset++) {
            buffer[a] = data[offset];
        }

        return buffer;
    }

    public static String byteArrayToHex(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] hexToByteArray(String hex) throws YubiHSMInputException {
        if (hex.length() % 2 != 0) {
            throw new YubiHSMInputException("Invalid hex string '" + hex + "'");
        }
        for (int i = 0; i < hex.length(); i++) {
            if (Character.digit(hex.charAt(i), 16) < 0) {
                throw new YubiHSMInputException("Invalid hex string '" + hex + "'");
            }
        }
        byte data[] = new byte[hex.length()/2];
        for(int i=0; i < hex.length(); i+=2) {
            data[i/2] = (Integer.decode("0x" + hex.charAt(i) + hex.charAt(i+1))).byteValue();
        }
        return data;
    }

    public static byte[] validateNonce(byte[] nonce, boolean padding) throws YubiHSMInputException {
        if (nonce.length > Defines.YSM_AEAD_NONCE_SIZE) {
            throw new YubiHSMInputException("Nonce too long, expected " + Defines.YSM_AEAD_NONCE_SIZE + " bytes but got " + nonce.length + " bytes.");
        }
        if (padding && nonce.length <= Defines.YSM_AEAD_NONCE_SIZE) {
            return Arrays.copyOf(nonce, Defines.YSM_AEAD_NONCE_SIZE);
        }

        return nonce;
    }

    public static String validateString(String name, String data, int maxLength, int exactLength) throws YubiHSMInputException {
        if (maxLength > 0 && data.length() > maxLength) {
            throw new YubiHSMInputException("Argument '" + name + "' is too long, expected max " + maxLength + " but got " + data.length());
        }
        if (exactLength > 0 && data.length() != exactLength) {
            throw new YubiHSMInputException("Wrong size of argument '" + name + "', expected " + exactLength + " but got " + data.length());
        }

        return data;
    }

    public static byte[] validateByteArray(String name, byte[] data, int maxLength, int exactLength, int paddingLength) throws YubiHSMInputException {
        if (paddingLength > 0 && data.length < paddingLength) {
            data = Arrays.copyOf(data, paddingLength);
        }
        if (maxLength > 0 && data.length > maxLength) {
            throw new YubiHSMInputException("Argument '" + name + "' is too long, expected max " + maxLength + " but got " + data.length);
        }
        if (exactLength > 0 && data.length != exactLength) {
            throw new YubiHSMInputException("Wrong size of argument '" + name + "', expected " + exactLength + " but got " + data.length);
        }
        return data;
    }

    public static String validateCmdResponseString(String name, String got, String expected) throws YubiHSMErrorException {
        if (!got.equals(expected)) {
            throw new YubiHSMErrorException("Bad " + name + " in response (got " + got + ", expected " + expected + ")");
        }
        return got;
    }

    public static byte[] validateCmdResponseBA(String name, byte[] got, byte[] expected) throws YubiHSMErrorException {
        if (!Arrays.equals(got, expected)) {
            throw new YubiHSMErrorException("Bad " + name + " in response (Got 0x" + byteArrayToHex(got) + ", expected 0x" + byteArrayToHex(expected));
        }
        return got;
    }

    public static byte[] longToByteArray(long value) {
        return new byte[] {
                (byte)((value >> 56) & 0xff),
                (byte)((value >> 48) & 0xff),
                (byte)((value >> 40) & 0xff),
                (byte)((value >> 32) & 0xff),
                (byte)((value >> 24) & 0xff),
                (byte)((value >> 16) & 0xff),
                (byte)((value >> 8 ) & 0xff),
                (byte)(value & 0xff),
        };
    }

    public static String getYubiKeyPublicId(String otp) {
        if ((otp == null) || (otp.length() < 32)) {
            throw new IllegalArgumentException("OTP is too short");
        }
        return otp.substring(0, otp.length() - 32);
    }

    public static String getYubiKeyOtp(String otp) {
        if ((otp == null) || (otp.length() < 32)) {
            throw new IllegalArgumentException("OTP is too short");
        }
        return otp.substring(otp.length() - 32);
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
