/*
 * Copyright (c) 2011 Yubico AB. All rights reserved.
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
 * @author Fredrik Thulin <fredrik@yubico.com>
 */

package org.unitedid.yhsm.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitedid.yhsm.SetupCommon;
import org.unitedid.yhsm.utility.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class YubikeyOtpDecodeCmdTest extends SetupCommon {

    private final int keyHandle = 0x2000;
    private final String publicId = "4d4d4d000001"; /* ftftftcccccb in modhex */
    private final String privateId = "534543524554";
    private final String key = "fcacd309a20ce1809c2db257f0e8d6ea";


    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testYubikeyDecode1() throws Exception {
        byte[] secretBA = Utils.hexToByteArray(new String(key + privateId));
        String aead = AEADCmd.generateAEAD(deviceHandler, publicId, keyHandle, secretBA).get("aead");
        Map<String, Integer> result;
        result = hsm.decodeYubikeyOtp(publicId, keyHandle, aead, "828e71152b15a4823bb34b6e6a5d4353");
        Map<String, Integer> expected = new HashMap<String, Integer>() {{
            put("useCtr", 1);
            put("sessionCtr", 0);
            put("tsHigh", 39);
            put("tsLow", 24133);
        }};
        assertEquals(expected, result);
    }

    @Test
    public void testYubikeyDecode2() throws Exception {
        byte[] secretBA = Utils.hexToByteArray(new String(key + privateId));
        String aead = AEADCmd.generateAEAD(deviceHandler, publicId, keyHandle, secretBA).get("aead");
        Map<String, Integer> result;
        result = hsm.decodeYubikeyOtp(publicId, keyHandle, aead, "c91e8472c2a76459a2a8b81c32d44955");
        Map<String, Integer> expected = new HashMap<String, Integer>() {{
            put("useCtr", 2);
            put("sessionCtr", 4);
            put("tsHigh", 204);
            put("tsLow", 28386);
        }};
        assertEquals(expected, result);
    }

    @Test
    public void testYubikeyDecodeInvalid() throws Exception {
        thrown.expect(YubiHSMCommandFailedException.class);
        thrown.expectMessage("Command YSM_AEAD_YUBIKEY_OTP_DECODE failed: YSM_OTP_INVALID");

        byte[] secretBA = Utils.hexToByteArray(new String(key + privateId));
        String aead = AEADCmd.generateAEAD(deviceHandler, publicId, keyHandle, secretBA).get("aead");
        hsm.decodeYubikeyOtp(publicId, keyHandle, aead, "000102030405060708090a0b0c0d0e0f");
    }
}
