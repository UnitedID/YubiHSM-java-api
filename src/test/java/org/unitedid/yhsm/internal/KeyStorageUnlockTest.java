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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertFalse(hsm.keyStorageUnlock("1111"));
    }

    @Test
    public void unlockHsm() throws Exception {
        assertTrue(hsm.keyStorageUnlock("2f6af1e667456bb94528e7987344515b"));
    }
}
