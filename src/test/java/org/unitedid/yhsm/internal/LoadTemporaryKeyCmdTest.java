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
import org.unitedid.yhsm.utility.Utils;

import static org.junit.Assert.*;

public class LoadTemporaryKeyCmdTest extends SetupCommon {

    private int keyHandle = 8192;
    private String key = "AAAAAAAAAAAAAAAA";
    private String nonce = "f1f2f3f4f5f6";

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
    public void testLoadTemporaryKey() throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        hsm.loadBufferData(Utils.concatAllArrays(Utils.hexToByteArray("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"), Utils.leIntToBA(0xffffffff)), 0);
        String aead = hsm.generateBufferAEAD(nonce, keyHandle).get("aead");

        assertTrue(hsm.loadTemporaryKey(nonce, keyHandle, aead));

        String plainText = Utils.validateString("plaintext", "Testing", 0, 0);
        String cipherText = hsm.encryptAES_ECB(plainText, Defines.YSM_TEMP_KEY_HANDLE);
        assertNotSame(plainText, cipherText);

        String decryptedText = hsm.decryptAES_ECB(cipherText, Defines.YSM_TEMP_KEY_HANDLE);
        assertEquals(plainText, decryptedText);
        String hash = hsm.generateHMACSHA1(Utils.longToByteArray(1), Defines.YSM_TEMP_KEY_HANDLE, true, false).get("hash");
        assertEquals("eab6c0a2b434b6d0daf06500456545faa58935f9", hash);
    }

    @Test
    public void testLoadTemporaryKeyException() throws Exception {
        thrown.expect(YubiHSMCommandFailedException.class);
        hsm.loadTemporaryKey(nonce, keyHandle, "");
        hsm.generateHMACSHA1(Utils.longToByteArray(1), Defines.YSM_TEMP_KEY_HANDLE, true, false);
    }
}
