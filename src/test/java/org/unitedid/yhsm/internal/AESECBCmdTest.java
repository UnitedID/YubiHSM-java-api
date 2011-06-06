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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitedid.yhsm.SetupCommon;

import static org.junit.Assert.*;

public class AESECBCmdTest extends SetupCommon {
    private int khEncrypt = 4097; // 0x1001
    private int khDecrypt = 4097; // 0x1001
    private int khCompare = 4097; // 0x1001

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testEncryptAndDecrypt() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        String plaintext = "World domination";
        String cipherText = hsm.encryptAES_ECB(plaintext, khEncrypt);
        assertNotSame(plaintext, cipherText);

        String decrypted = hsm.decryptAES_ECB(cipherText, khDecrypt);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testEncryptInputException() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Argument 'plaintext' is too long, expected max 16 but got 26");
        String aTooLongString = "abcdefghijklmonpqrstuvwxyz";
        hsm.encryptAES_ECB(aTooLongString, khEncrypt);
    }

    @Test
    public void testDecryptInputException() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        thrown.expectMessage("Wrong size of argument 'cipherText', expected 16 but got 19");
        String aTooLongCipher = "112233445566778899aaccddeeff1122334455";
        hsm.decryptAES_ECB(aTooLongCipher, khDecrypt);
    }

    @Test
    public void testCompare() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        String plaintext = "Good deal";
        String cipherText = hsm.encryptAES_ECB(plaintext, khEncrypt);
        assertTrue(hsm.compareAES_ECB(khCompare, cipherText, plaintext));
    }

    @Test
    public void testCompareNotOk() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        String plaintext = "Good deal";
        String cipherText = hsm.encryptAES_ECB(plaintext, khEncrypt);
        assertFalse(hsm.compareAES_ECB(khCompare, cipherText, plaintext.substring(0,5)));
    }
}
