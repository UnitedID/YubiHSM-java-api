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
    public static int minHashLength = 20;

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

    public boolean validateAEAD(String nonce, int keyHandle, String aead, String plaintext) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
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

    /**
     * Generate HMAC SHA1 using a key handle in the YubiHSM.
     *
     * @param data the data used to generate the SHA1
     * @param keyHandle the key handle to use in the YubiHSM
     * @param last set to false to not get a hash generated for the initial request
     * @param toBuffer set to true to get the SHA1 stored into the internal buffer, for use in some other cryptographic operations.
     * @return a map containing status and SHA1 hash
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateHMACSHA1(String data, int keyHandle, boolean last, boolean toBuffer) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return HMACCmd.generateHMACSHA1(deviceHandler, data, keyHandle, (byte) 0, last, toBuffer);
    }

    /**
     * Generate HMAC SHA1 using a key handle in the YubiHSM.
     *
     * @param data the data used to generate the SHA1
     * @param keyHandle the key handle to use in the YubiHSM
     * @param flags set custom flags to be used when generating a SHA1, if set to (byte) 0 defaults will be used.
     * @param last set to false to not get a hash generated for the initial request
     * @param toBuffer set to true to get the SHA1 stored into the internal buffer, for use in some other cryptographic operations.
     * @return a map containing status and SHA1 hash
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateHMACSHA1(String data, int keyHandle, byte flags, boolean last, boolean toBuffer) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return HMACCmd.generateHMACSHA1(deviceHandler, data, keyHandle, flags, last, toBuffer);
    }

    /**
     * Add more input to the HMAC SHA1, used after calling {@link #generateHMACSHA1} with last set to false.
     *
     * @param data the data to add before generating SHA1
     * @param keyHandle the key handle to use in the YubiHSM
     * @param last set to false to not get a hash generated after this call
     * @param toBuffer set to true to get the SHA1 stored into the internal buffer, for use in some other cryptographic operations.
     * @return a map containing status and SHA1 hash
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateHMACSHA1Next(String data, int keyHandle, boolean last, boolean toBuffer) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return HMACCmd.next(deviceHandler, data, keyHandle, last, toBuffer);
    }

    public boolean drainData() {
        return deviceHandler.drain();
    }

    public DeviceHandler getRawDevice() {
        return deviceHandler;
    }

    public int getMinHashLength() {
        return minHashLength;
    }

    public void setMinHashLength(int value) {
        minHashLength = value;
    }
}
