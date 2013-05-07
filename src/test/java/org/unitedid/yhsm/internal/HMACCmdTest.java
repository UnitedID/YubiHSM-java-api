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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitedid.yhsm.SetupCommon;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HMACCmdTest extends SetupCommon {

    private String data = "Sample #2";
    private String expectedHash = "0922d3405faa3d194f82a45830737d5cc6c75d24";
    private String expectedNextHash = "0000000000000000000000000000000000000000";
    private int keyHandle = 12337;


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
    public void testHMACSHA1WithNumericFlags() throws Exception {
        byte flags = Defines.YSM_HMAC_SHA1_RESET | Defines.YSM_HMAC_SHA1_FINAL;
        Map<String, String> result = hsm.generateHMACSHA1(data, keyHandle, flags, true, false);

        assertEquals(expectedHash, result.get("hash"));
    }

    @Test
    public void testHMACSHA1Next() throws Exception {
        Map<String, String> result = hsm.generateHMACSHA1(data.substring(0, 3), keyHandle, false, false);
        assertEquals(expectedNextHash, result.get("hash"));
        result = hsm.generateHMACSHA1Next(data.substring(3), keyHandle, true, false);
        assertEquals(expectedHash, result.get("hash"));
    }

    @Test
    public void testHMACSHA1NextNext() throws Exception {
        Map<String, String> result = hsm.generateHMACSHA1("", keyHandle, false, false);
        assertEquals(expectedNextHash, result.get("hash"));
        result = hsm.generateHMACSHA1Next(data.substring(0, 3), keyHandle, false, false);
        assertEquals(expectedNextHash, result.get("hash"));
        result = hsm.generateHMACSHA1Next(data.substring(3), keyHandle, true, false);
        assertEquals(expectedHash, result.get("hash"));
    }

    @Test
    public void testHMACSHA1Interupted() throws Exception {
        Map<String, String> result = hsm.generateHMACSHA1(data.substring(0, 3), keyHandle, false, false);
        assertEquals(expectedNextHash, result.get("hash"));
        assertEquals("123qwe", hsm.echo("123qwe"));
        result = hsm.generateHMACSHA1Next(data.substring(3), keyHandle, true, false);
        assertEquals(expectedHash, result.get("hash"));
    }

    @Test
    public void testHMACSHA1InvalidKeyHandle() throws Exception {
        thrown.expect(YubiHSMCommandFailedException.class);
        thrown.expectMessage("Command YSM_HMAC_SHA1_GENERATE failed: YSM_FUNCTION_DISABLED");
        hsm.generateHMACSHA1(data, 1, true, false);
    }

    @Test
    public void testHMACSHA1ToBuffer() throws Exception {
        assertEquals(0, hsm.loadRandomBufferData(0, 0));
        hsm.generateHMACSHA1(data, keyHandle, true, true);
        assertEquals(Defines.YSM_SHA1_HASH_SIZE, hsm.loadRandomBufferData(0, 1));
    }

    @Test
    public void testHMACSHA1NextWithBuffer() throws Exception {
        assertEquals(0, hsm.loadRandomBufferData(0, 0));
        assertEquals(0, hsm.loadRandomBufferData(0, 1));

        Map<String, String> result = hsm.generateHMACSHA1("", keyHandle, false, false);
        assertEquals(expectedNextHash, result.get("hash"));
        hsm.generateHMACSHA1Next(data.substring(0, 3), keyHandle, false, false);
        hsm.generateHMACSHA1Next(data.substring(3), keyHandle, false, false);
        hsm.generateHMACSHA1Next("", keyHandle, true, true);
        assertEquals(Defines.YSM_SHA1_HASH_SIZE, hsm.loadRandomBufferData(0, 1));
    }
}
