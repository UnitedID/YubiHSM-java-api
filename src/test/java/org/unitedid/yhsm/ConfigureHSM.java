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

package org.unitedid.yhsm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitedid.yhsm.internal.CommandHandler;
import org.unitedid.yhsm.utility.Utils;

public class ConfigureHSM extends SetupCommon {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testConfigureHSM() throws Exception {
        hsm.exitMonitorDebugMode();
        System.out.println(runCommand("hsm ffffffff\r\r2f6af1e667456bb94528e7987344515b\ryes", true));
        System.out.println(runCommand("sysinfo", true));
        hsm.drainData();
        addKeys();
        System.out.println(runCommand("keylist", true));
        deviceHandler.write("exit\r".getBytes());
        Thread.sleep(50);
        hsm.drainData();
        CommandHandler.reset(deviceHandler);
    }

    private void addKeys() throws Exception {

        for (int i = 0; i <= 30; i++ ) {
            int flags = 1 << i;
            String key = new String();
            key = duplicateStr(String.format("%02x", i + 1), 32);
            addKey(flags, i + 1, key);
        }

        addKey(0x200, 0x1000, duplicateStr("06", 32));
        addKey(0xe000, 0x1001, duplicateStr("1001", 16));
        addKey(0xffffffff, 0x2000, duplicateStr("2000", 16));
        addKey(0x10000, 0x3031, "303132333435363738393a3b3c3d3e3f40414243000000000000000000000000");

    }

    private void addKey(int flags, int num, String key) throws Exception {
        String keyLine = String.format("%08x,%s\r", num, key);
        char esc = 0x1b;
        System.out.println(runCommand(String.format("flags %04x", flags), true));
        System.out.println(runCommand("keyload\r" + keyLine + esc, false));
    }

    private String duplicateStr(String data, int times) {
        String buffer = new String();
        for (int i = 0; i < times; i++) {
            buffer += data;
        }
        return buffer;
    }

    private String runCommand(String command, boolean carrierReturn) throws Exception {
        if (carrierReturn) {
            deviceHandler.write((command + '\r').getBytes());
        } else {
            deviceHandler.write(command.getBytes());
        }

        Thread.sleep(20);

        byte[] data = new byte[0];
        int failCount = 0;

        while(true) {
            byte[] b = deviceHandler.read(1);
            if (b == null || b.length == 0) {
                failCount++;
                if (failCount == 5) {
                    throw new Exception("Did not get the next prompt: " + b);
                }
            }
            data = Utils.concatAllArrays(data, b);
            if ((new String(data, 0, data.length)).endsWith("NO_CFG> ") ||
                (new String(data, 0, data.length)).endsWith("HSM> ")) {
                break;
            }
        }

        return new String(data, 0, data.length);
    }


}
