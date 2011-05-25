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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitedid.yhsm.internal.*;

import java.util.Map;

public class YubiHSM  {
    /** Logger */
    private final Logger log = LoggerFactory.getLogger(YubiHSM.class);

    private DeviceHandler deviceHandler;

    public YubiHSM(String device, int timeout) {
        deviceHandler = DeviceHandlerFactory.get(device, timeout);
        CommandHandler.reset(deviceHandler);
    }

    public String echo(String str) {
        return EchoCmd.execute(deviceHandler, str);
    }

    public Map<String, String> info() {
        return SystemInfoCmd.execute(deviceHandler);
    }

    public String infoToString() {
        Map<String, String> info = SystemInfoCmd.execute(deviceHandler);

        return String.format("Version %s.%s.%s  Protocol=%s  SysId: %s", info.get("major"), info.get("minor"),
                                                                         info.get("build"), info.get("protocol"),
                                                                         info.get("sysid"));
    }

    public Map<String, String> generateAEAD(String nonce, int keyHandle, String data) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateAEAD(deviceHandler, nonce, keyHandle, data);
    }

    public Map<String, String> generateRandomAEAD(String nonce, int keyHandle, int length) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateRandomAEAD(deviceHandler, nonce, keyHandle, length);
    }

    public Map<String, String> generateBufferAEAD(String nonce, int keyHandle) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateBufferAEAD(deviceHandler, nonce, keyHandle);
    }

    public boolean validateAEAD(String nonce, int keyHandle, String aead, String plaintext) throws YubiHSMInputException {
        return AEADCmd.validateAEAD(deviceHandler, nonce, keyHandle, aead, plaintext);
    }

    public int loadBufferData(String data, int offset) throws YubiHSMErrorException {
        return BufferCmd.loadData(deviceHandler, data, offset);
    }

    public int loadBufferData(byte[] data, int offset) throws YubiHSMErrorException {
        return BufferCmd.loadData(deviceHandler, data, offset);
    }

    public int loadRandomBufferData(int length, int offset) throws YubiHSMErrorException {
        return BufferCmd.loadRandomData(deviceHandler, length, offset);
    }

    public void exitMonitorDebugMode() {
        MonitorExitCmd.execute(deviceHandler);
    }

    public boolean drainData() {
        return deviceHandler.drain();
    }

    public DeviceHandler getRawDevice() {
        return deviceHandler;
    }
}
