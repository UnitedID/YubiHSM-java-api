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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitedid.yhsm.SetupCommon;

public class KeyStorageUnlockTest extends SetupCommon {

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
    public void failedUnlockHsm() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        assertFalse(hsm.unlock("1111"));
    }

    @Test
    public void unlockHsm() throws Exception {
        assertTrue(hsm.unlock("2f6af1e667456bb94528e7987344515b"));
    }

    @Test
    public void otpUnlockHsm() throws Exception {
        /* order is crucial here, that's why these are not made into separate tests */

        if (new Integer(hsm.info().get("major")) > 0) {
            /* Incorrect public id */
            try {
                hsm.unlockOtp("010000000000", "ffaaffaaffaaffaaffaaffaaffaaffaa");
                fail("unlockOtp not expected to work");
            } catch (YubiHSMCommandFailedException e) {
                assertEquals("Command YSM_HSM_UNLOCK failed: YSM_INVALID_PARAMETER", e.getMessage());
            }

            /* Right public id, wrong OTP */
            assertFalse(hsm.unlockOtp("4d4d4d000001", "ffaaffaaffaaffaaffaaffaaffaaffaa"));

            /* Right public id, right OTP (for counter values 00002/001) */
            assertTrue(hsm.unlockOtp("4d4d4d000001", "5f8e12aa57abb1677b88606eb2af63b9"));

            /* Replay, will lock the HSM again */
            try {
                hsm.unlockOtp("4d4d4d000001", "5f8e12aa57abb1677b88606eb2af63b9");
                fail("unlockOtp with same OTP not expected to work");
            } catch (YubiHSMCommandFailedException e) {
                assertEquals("Command YSM_HSM_UNLOCK failed: YSM_OTP_REPLAY", e.getMessage());
            }

            /* Right public id, new OTP (counter values 00002/002) */
            assertTrue(hsm.unlockOtp("4d4d4d000001", "083aa5cd1a278ed2e9eb46971b479725"));
        }
    }
}
