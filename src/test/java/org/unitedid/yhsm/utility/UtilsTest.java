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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitedid.yhsm.internal.Defines;
import org.unitedid.yhsm.internal.YubiHSMInputException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddLengthToData() throws Exception {
        byte[] data = "ekoeko".getBytes();
        byte[] expected = {0x06,0x65,0x6b,0x6f,0x65,0x6b,0x6f};

        assertArrayEquals(expected, Utils.addLengthToData(data));
    }

    @Test
    public void testConcatAllArrays() throws Exception {
        byte[] array1 = {0x00, 0x01};
        byte[] array2 = {0x02, 0x03};
        byte[] array3 = {0x01, 0x01};
        byte[] expected = {0x00, 0x01, 0x02, 0x03, 0x01, 0x01};

        assertArrayEquals(expected, Utils.concatAllArrays(array1, array2, array3));
    }

    @Test
    public void testLeIntToBA() throws Exception {
        byte[] expected = {0x00,0x20,0x00,0x00};
        assertArrayEquals(expected, Utils.leIntToBA(8192));
    }

    @Test
    public void testRangeOfByteArray() throws Exception {
        byte[] data = "ekoeko".getBytes();
        byte[] expected = {0x6b, 0x6f};

        assertArrayEquals(expected, Utils.rangeOfByteArray(data, 1, 2));
    }

    @Test
    public void testByteArrayToHexString() throws Exception {
        byte[] data = "ekoeko".getBytes();
        String expected = "656b6f656b6f";

        assertEquals(expected, Utils.byteArrayToHex(data));
    }

    @Test
    public void testHexToByteArray() throws Exception {
        String data = "656b6f656b6f";
        byte[] expected = {0x65,0x6b,0x6f,0x65,0x6b,0x6f};

        assertArrayEquals(expected, Utils.hexToByteArray(data));
    }

    @Test
    public void testValidateNonce() throws Exception {
        String data = "12";
        assertEquals(Defines.YSM_AEAD_NONCE_SIZE, Utils.validateNonce(data.getBytes(), true).length);
        assertEquals(2, Utils.validateNonce(data.getBytes(), false).length);

    }

    @Test
    public void testNonceInputException() throws YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Nonce too long, expected 6 bytes but got 7 bytes.");
        String data = "1234567";
        Utils.validateNonce(data.getBytes(), false);
    }

    @Test
    public void testLeShortToByteArray() {
        byte[] expected = {0x00, 0x20};
        assertArrayEquals(expected, Utils.leShortToByteArray((short) 8192));
    }

    @Test
    public void testLeBAToBeShort() {
        byte[] data = {0x00, 0x20};
        assertEquals(8192, Utils.leBAToBeShort(data));
    }

    @Test
    public void testHexToByteArrayIncompleteHex() throws YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Invalid hex string (aac)!");
        Utils.hexToByteArray("aac");
    }

    @Test
    public void testHexToByteArrayInvalidHexValue() throws YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Invalid hex string (aaxx)!");
        Utils.hexToByteArray("aaxx");
    }

    @Test
    public void testValidateByteArrayMaxLength() throws YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Argument 'test' is too long, expected max 10 but got 12");
        String data = "Test";
        Utils.validateByteArray("test", data.getBytes(), 10, 0, 12);
    }

    @Test
    public void testValidateByteArrayExactLength() throws YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Wrong size of argument 'test', expected 10 but got 12");
        String data = "Test";
        Utils.validateByteArray("test", data.getBytes(), 0, 10, 12);
    }

    @Test
    public void testLongToByteArray() {
        byte[] expected = {0x00,0x00,0x00,0x00,0x00,0x00,0x20,0x00};
        assertArrayEquals(expected, Utils.longToByteArray((long) 8192));
    }
}
