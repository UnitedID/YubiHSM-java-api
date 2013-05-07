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

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.unitedid.yhsm.internal.Defines.YSM_DATA_BUF_SIZE;

public class BufferCmdTest extends SetupCommon {

    @BeforeTest
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testLoadData() throws YubiHSMErrorException {
        assertEquals(BufferCmd.loadData(deviceHandler, "12345", 0), 5);
        assertEquals(BufferCmd.loadData(deviceHandler, "12", 5), 7);
        assertEquals(BufferCmd.loadData(deviceHandler, "12", 0), 2);
    }

    @Test
    public void testRandom() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        String nonce = "112233445566";
        hsm.loadRandomBufferData(20, 0);
        Map aead1 = hsm.generateBufferAEAD(nonce, keyHandle);
        hsm.loadRandomBufferData(20, 0);
        Map aead2 = hsm.generateBufferAEAD(nonce, keyHandle);

        assertNotSame(aead1, aead2);
    }

    @Test
    public void testWouldOverflowBuffer() throws YubiHSMErrorException {
        assertEquals(hsm.loadRandomBufferData(16, YSM_DATA_BUF_SIZE - 8), 64);
        assertEquals(hsm.loadRandomBufferData(16, 0), 16);
        assertEquals(hsm.loadRandomBufferData(1, 16), 17);
        assertEquals(hsm.loadRandomBufferData(7, 10), 17);
        assertEquals(hsm.loadRandomBufferData(1, 62), 63);
        assertEquals(hsm.loadRandomBufferData(63, 62), 64);
    }
}
