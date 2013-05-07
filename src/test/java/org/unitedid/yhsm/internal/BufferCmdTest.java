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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitedid.yhsm.SetupCommon;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class BufferCmdTest extends SetupCommon {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testLoadData() throws YubiHSMErrorException {
        assertEquals(5, BufferCmd.loadData(deviceHandler, "12345", 0));
        assertEquals(7, BufferCmd.loadData(deviceHandler, "12", 5));
        assertEquals(2, BufferCmd.loadData(deviceHandler, "12", 0));
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
        assertEquals(64, hsm.loadRandomBufferData(16, Defines.YSM_DATA_BUF_SIZE - 8));
        assertEquals(16, hsm.loadRandomBufferData(16, 0));
        assertEquals(17, hsm.loadRandomBufferData(1, 16));
        assertEquals(17, hsm.loadRandomBufferData(7, 10));
        assertEquals(63, hsm.loadRandomBufferData(1, 62));
        assertEquals(64, hsm.loadRandomBufferData(63, 62));
    }
}
