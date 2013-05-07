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
import static org.unitedid.yhsm.internal.Defines.YSM_HMAC_SHA1_FINAL;
import static org.unitedid.yhsm.internal.Defines.YSM_HMAC_SHA1_RESET;
import static org.unitedid.yhsm.internal.Defines.YSM_SHA1_HASH_SIZE;

public class HMACCmdTest extends SetupCommon {

    private String data = "Sample #2";
    private String expectedHash = "0922d3405faa3d194f82a45830737d5cc6c75d24";
    private String expectedNextHash = "0000000000000000000000000000000000000000";
    private int keyHandle = 12337;

    @BeforeTest
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testHMACSHA1WithNumericFlags() throws Exception {
        byte flags = YSM_HMAC_SHA1_RESET | YSM_HMAC_SHA1_FINAL;
        Map<String, String> result = hsm.generateHMACSHA1(data, keyHandle, flags, true, false);

        assertEquals(result.get("hash"), expectedHash);
    }

    @Test
    public void testHMACSHA1Next() throws Exception {
        Map<String, String> result = hsm.generateHMACSHA1(data.substring(0, 3), keyHandle, false, false);
        assertEquals(result.get("hash"), expectedNextHash);
        result = hsm.generateHMACSHA1Next(data.substring(3), keyHandle, true, false);
        assertEquals(result.get("hash"), expectedHash);
    }

    @Test
    public void testHMACSHA1NextNext() throws Exception {
        Map<String, String> result = hsm.generateHMACSHA1("", keyHandle, false, false);
        assertEquals(result.get("hash"), expectedNextHash);
        result = hsm.generateHMACSHA1Next(data.substring(0, 3), keyHandle, false, false);
        assertEquals(result.get("hash"), expectedNextHash);
        result = hsm.generateHMACSHA1Next(data.substring(3), keyHandle, true, false);
        assertEquals(result.get("hash"), expectedHash);
    }

    @Test
    public void testHMACSHA1Interupted() throws Exception {
        Map<String, String> result = hsm.generateHMACSHA1(data.substring(0, 3), keyHandle, false, false);
        assertEquals(result.get("hash"), expectedNextHash);
        assertEquals(hsm.echo("123qwe"), "123qwe");
        result = hsm.generateHMACSHA1Next(data.substring(3), keyHandle, true, false);
        assertEquals(result.get("hash"), expectedHash);
    }

    @Test(expectedExceptions = YubiHSMCommandFailedException.class,
          expectedExceptionsMessageRegExp = "Command YSM_HMAC_SHA1_GENERATE failed: YSM_FUNCTION_DISABLED")
    public void testHMACSHA1InvalidKeyHandle() throws Exception {
        hsm.generateHMACSHA1(data, 1, true, false);
    }

    @Test
    public void testHMACSHA1ToBuffer() throws Exception {
        assertEquals(hsm.loadRandomBufferData(0, 0), 0);
        hsm.generateHMACSHA1(data, keyHandle, true, true);
        assertEquals(hsm.loadRandomBufferData(0, 1), YSM_SHA1_HASH_SIZE);
    }

    @Test
    public void testHMACSHA1NextWithBuffer() throws Exception {
        assertEquals(hsm.loadRandomBufferData(0, 0), 0);
        assertEquals(hsm.loadRandomBufferData(0, 1), 0);

        Map<String, String> result = hsm.generateHMACSHA1("", keyHandle, false, false);
        assertEquals(result.get("hash"), expectedNextHash);
        hsm.generateHMACSHA1Next(data.substring(0, 3), keyHandle, false, false);
        hsm.generateHMACSHA1Next(data.substring(3), keyHandle, false, false);
        hsm.generateHMACSHA1Next("", keyHandle, true, true);
        assertEquals(hsm.loadRandomBufferData(0, 1), YSM_SHA1_HASH_SIZE);
    }
}
