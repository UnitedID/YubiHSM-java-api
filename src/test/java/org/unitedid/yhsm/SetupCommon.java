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

package org.unitedid.yhsm;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.unitedid.yhsm.internal.DeviceHandler;

public class SetupCommon {
    public YubiHSM hsm;
    public DeviceHandler deviceHandler;
    public int keyHandle = 8192;
    public final String configPassPhrase = "";
    public final String hsmPassPhrase = "badabada";
    public final String adminYubikey = "ftftftfteeee";


    @BeforeTest
    public void setUp() throws Exception {
        String deviceName = "/dev/ttyACM0";
        if (System.getProperty("hsm.test.deviceName") != null) {
            deviceName = System.getProperty("hsm.test.deviceName");
        }
        hsm = new YubiHSM(deviceName);
        deviceHandler = hsm.getRawDevice();
    }

    @AfterTest
    public void tearDown() throws Exception {
        hsm = null;
        deviceHandler = null;
    }

}
