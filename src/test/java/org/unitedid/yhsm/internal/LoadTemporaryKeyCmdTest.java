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

package org.unitedid.yhsm.internal;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.unitedid.yhsm.SetupCommon;
import org.unitedid.yhsm.utility.Utils;

import static org.testng.Assert.*;
import static org.unitedid.yhsm.internal.Defines.YSM_TEMP_KEY_HANDLE;

public class LoadTemporaryKeyCmdTest extends SetupCommon {

    private int keyHandle = 8192;
    private String nonce = "f1f2f3f4f5f6";

    @BeforeTest
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testLoadTemporaryKey() throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        hsm.loadBufferData(Utils.concatAllArrays(Utils.hexToByteArray("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"), Utils.leIntToBA(0xffffffff)), 0);
        String aead = hsm.generateBufferAEAD(nonce, keyHandle).get("aead");

        assertTrue(hsm.loadTemporaryKey(nonce, keyHandle, aead));

        String plainText = Utils.validateString("plaintext", "Testing", 0, 0);
        String cipherText = hsm.encryptAES_ECB(plainText, YSM_TEMP_KEY_HANDLE);
        assertNotSame(plainText, cipherText);

        String decryptedText = hsm.decryptAES_ECB(cipherText, YSM_TEMP_KEY_HANDLE);
        assertEquals(plainText, decryptedText);
        String hash = hsm.generateHMACSHA1(Utils.longToByteArray(1), YSM_TEMP_KEY_HANDLE, true, false).get("hash");
        assertEquals(hash, "eab6c0a2b434b6d0daf06500456545faa58935f9");
    }

    @Test(expectedExceptions = YubiHSMCommandFailedException.class)
    public void testLoadTemporaryKeyException() throws Exception {
        hsm.loadTemporaryKey(nonce, keyHandle, "");
        hsm.generateHMACSHA1(Utils.longToByteArray(1), YSM_TEMP_KEY_HANDLE, true, false);
    }
}
