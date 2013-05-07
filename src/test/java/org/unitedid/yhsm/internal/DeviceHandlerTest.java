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
import org.unitedid.yhsm.utility.Utils;

import static org.testng.Assert.assertEquals;
import static org.unitedid.yhsm.internal.Defines.YSM_ECHO;

public class DeviceHandlerTest extends SetupCommon {

    @BeforeTest
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterTest
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testDrainClearInput() throws YubiHSMErrorException {
        CommandHandler.execute(deviceHandler, YSM_ECHO, Utils.addLengthToData("ekoeko".getBytes()), false);
        assertEquals(deviceHandler.available(), 9);
        deviceHandler.drain();
        assertEquals(deviceHandler.available(), 0);
    }
}
