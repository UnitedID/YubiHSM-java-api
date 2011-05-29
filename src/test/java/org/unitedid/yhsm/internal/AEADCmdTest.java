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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AEADCmdTest extends SetupCommon {

    private String nonce = "4d4d4d4d4d4d";


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
    public void testGenerateAEADAndValidation() throws Exception {
        String aead = AEADCmd.generateAEAD(deviceHandler, nonce, 8192, "123qwe").get("aead");
        assertTrue(AEADCmd.validateAEAD(deviceHandler, nonce, 8192, aead, "123qwe"));
    }

    @Test
    public void testGenerateRandomAEAD() throws Exception {
        int[] bytes = {1, Defines.KEY_SIZE + Defines.UID_SIZE, Defines.YSM_AEAD_MAX_SIZE - Defines.YSM_AEAD_MAC_SIZE};
        for(int num : bytes) {
            String aead = AEADCmd.generateRandomAEAD(deviceHandler, nonce, 4, num).get("aead");
            assertEquals(num + Defines.YSM_AEAD_MAC_SIZE, Utils.hexToByteArray(aead).length);
        }
    }

    @Test
    public void testGenerateRandomAEADException() throws Exception {
        thrown.expect(YubiHSMCommandFailedException.class);
        AEADCmd.generateRandomAEAD(deviceHandler, nonce, 4, 255).get("aead");
    }
}
