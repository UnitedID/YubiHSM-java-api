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

import org.testng.annotations.Test;
import org.unitedid.yhsm.internal.YubiHSMInputException;

import static org.testng.Assert.assertEquals;
import static org.unitedid.yhsm.internal.Defines.YSM_AEAD_NONCE_SIZE;

public class UtilsTest {

    @Test
    public void testAddLengthToData() throws Exception {
        byte[] data = "ekoeko".getBytes();
        byte[] expected = {0x06,0x65,0x6b,0x6f,0x65,0x6b,0x6f};

        assertEquals(Utils.addLengthToData(data), expected);
    }

    @Test
    public void testConcatAllArrays() throws Exception {
        byte[] array1 = {0x00, 0x01};
        byte[] array2 = {0x02, 0x03};
        byte[] array3 = {0x01, 0x01};
        byte[] expected = {0x00, 0x01, 0x02, 0x03, 0x01, 0x01};

        assertEquals(Utils.concatAllArrays(array1, array2, array3), expected);
    }

    @Test
    public void testLeIntToBA() throws Exception {
        byte[] expected = {0x00,0x20,0x00,0x00};
        assertEquals(Utils.leIntToBA(8192), expected);
    }

    @Test
    public void testRangeOfByteArray() throws Exception {
        byte[] data = "ekoeko".getBytes();
        byte[] expected = {0x6b, 0x6f};

        assertEquals(Utils.rangeOfByteArray(data, 1, 2), expected);
    }

    @Test
    public void testByteArrayToHexString() throws Exception {
        byte[] data = "ekoeko".getBytes();
        String expected = "656b6f656b6f";

        assertEquals(Utils.byteArrayToHex(data), expected);
    }

    @Test
    public void testHexToByteArray() throws Exception {
        String data = "656b6f656b6f";
        byte[] expected = {0x65,0x6b,0x6f,0x65,0x6b,0x6f};

        assertEquals(Utils.hexToByteArray(data), expected);
    }

    @Test
    public void testValidateNonce() throws Exception {
        String data = "12";
        assertEquals(Utils.validateNonce(data.getBytes(), true).length, YSM_AEAD_NONCE_SIZE);
        assertEquals(Utils.validateNonce(data.getBytes(), false).length, 2);

    }

    @Test(expectedExceptions = YubiHSMInputException.class,
          expectedExceptionsMessageRegExp = "Nonce too long, expected 6 bytes but got 7 bytes.")
    public void testNonceInputException() throws YubiHSMInputException {
        String data = "1234567";
        Utils.validateNonce(data.getBytes(), false);
    }

    @Test
    public void testLeShortToByteArray() {
        byte[] expected = {0x00, 0x20};
        assertEquals(Utils.leShortToByteArray((short) 8192), expected);
    }

    @Test
    public void testLeBAToBeShort() {
        byte[] data = {0x00, 0x20};
        assertEquals(8192, Utils.leBAToBeShort(data), 8192);
    }

    @Test(expectedExceptions = YubiHSMInputException.class,
          expectedExceptionsMessageRegExp = "Invalid hex string 'aac'")
    public void testHexToByteArrayIncompleteHex() throws YubiHSMInputException {
        Utils.hexToByteArray("aac");
    }

    @Test(expectedExceptions = YubiHSMInputException.class,
    expectedExceptionsMessageRegExp = "Invalid hex string 'aaxx'")
    public void testHexToByteArrayInvalidHexValue() throws YubiHSMInputException {
        Utils.hexToByteArray("aaxx");
    }

    @Test(expectedExceptions = YubiHSMInputException.class,
          expectedExceptionsMessageRegExp = "Argument 'test' is too long, expected max 10 but got 12")
    public void testValidateByteArrayMaxLength() throws YubiHSMInputException {
        String data = "Test";
        Utils.validateByteArray("test", data.getBytes(), 10, 0, 12);
    }

    @Test(expectedExceptions = YubiHSMInputException.class,
          expectedExceptionsMessageRegExp = "Wrong size of argument 'test', expected 10 but got 12")
    public void testValidateByteArrayExactLength() throws YubiHSMInputException {
        String data = "Test";
        Utils.validateByteArray("test", data.getBytes(), 0, 10, 12);
    }

    @Test
    public void testLongToByteArray() {
        byte[] expected = {0x00,0x00,0x00,0x00,0x00,0x00,0x20,0x00};
        assertEquals(Utils.longToByteArray((long) 8192), expected);
    }
}
