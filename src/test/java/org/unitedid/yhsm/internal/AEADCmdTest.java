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

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.unitedid.yhsm.SetupCommon;
import org.unitedid.yhsm.utility.Utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.unitedid.yhsm.internal.Defines.*;


public class AEADCmdTest extends SetupCommon {

    private String nonce = "4d4d4d4d4d4d";

    @BeforeTest
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGenerateAEADAndValidation() throws Exception {
        String aead = hsm.generateAEAD(nonce, 8192, "123qwe").get("aead");
        assertTrue(hsm.validateAEAD(nonce, 8192, aead, "123qwe"));
    }

    @Test
    public void testGenerateAEADAndValidationBA() throws Exception {
        /* Test using 8-bit data that can't be converted from hex-string to byte
         * array and then back into a String.
         */
        byte[] secretBA = Utils.hexToByteArray(new String("ec1c263a5d9bd270db0b19b18ca5396b"));
        String aead = hsm.generateAEAD(nonce, 0x00002000, secretBA).get("aead");
        assertTrue(hsm.validateAEAD(nonce, 0x00002000, aead, secretBA));
    }

    @Test
    public void testGenerateAEADBlocked() throws Exception {
        DefaultArtifactVersion minVersion = new DefaultArtifactVersion("1.0.4");
        DefaultArtifactVersion curVersion = new DefaultArtifactVersion(hsm.getHsmVersion());

        if (curVersion.compareTo(minVersion) == -1) {
            throw new SkipException("This test requires firmware 1.0.4 or later");
        }

        try {
            byte[] secretBA = Utils.hexToByteArray(new String("ec1c263a5d9bd270db0b19b18ca5396b"));
            hsm.generateAEAD(nonce, 0x00000002, secretBA);
        } catch (YubiHSMCommandFailedException e) {
            assertEquals("Command YSM_AEAD_GENERATE failed: YSM_FUNCTION_DISABLED", e.getMessage());
        }
    }

    @Test
    public void testGenerateRandomAEAD() throws Exception {
        int[] bytes = {1, KEY_SIZE + UID_SIZE, YSM_AEAD_MAX_SIZE - YSM_AEAD_MAC_SIZE};
        for(int num : bytes) {
            String aead = hsm.generateRandomAEAD(nonce, 0x20000008, num).get("aead");
            assertEquals(Utils.hexToByteArray(aead).length, num + YSM_AEAD_MAC_SIZE);
        }
    }

    @Test(expectedExceptions = YubiHSMCommandFailedException.class)
    public void testGenerateRandomAEADException() throws Exception {
        hsm.generateRandomAEAD(nonce, 4, 255).get("aead");
    }

    @Test
    public void testGenerateOathHotpAEAD() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        String nonce = "f1f2f3f4f5f6";
        String seed = "3132333435363738393031323334353637383930";
        String expected = "ab9ee1ea245fd11bdfe3fc8a5255de4e8d90b3f6f1f7c97692e0979599de95c5";
        String result = hsm.generateOathHotpAEAD(nonce, 8192, seed);

        assertEquals(result, expected);
    }
}
