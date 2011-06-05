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

/** <code>YubiHSM</code> */

public class YubiHSM  {
    /** Logger */
    private final Logger log = LoggerFactory.getLogger(YubiHSM.class);

    /** The device handler */
    private DeviceHandler deviceHandler;

    /** The hash length, default is 20 */
    public static int minHashLength = 20;

    /** Constructor */
    public YubiHSM(String device, int timeout) {
        deviceHandler = DeviceHandlerFactory.get(device, timeout);
        CommandHandler.reset(deviceHandler);
    }

    /**
     * Test the YubiHSM by sending a string that the YubiHSM will echo back.
     *
     * @param str the string that the YubiHSM should return
     * @return the the same string sent to the YubiHSM
     */
    public String echo(String str) {
        return EchoCmd.execute(deviceHandler, str);
    }

    /**
     * Get the firmware version and unqiue ID from the YubiHSM.
     *
     * @return a map with version, protocol and unique ID
     */
    public Map<String, String> info() {
        return SystemInfoCmd.execute(deviceHandler);
    }

    /**
     * Get the firmware verseion and unique ID from the YubiHSM (string representation).
     *
     * @return a string with version, protocol and unique ID
     */
    public String infoToString() {
        Map<String, String> info = SystemInfoCmd.execute(deviceHandler);

        return String.format("Version %s.%s.%s  Protocol=%s  SysId: %s", info.get("major"), info.get("minor"),
                                                                               info.get("build"), info.get("protocol"),
                                                                               info.get("sysid"));
    }

    /**
     * Generate AEAD block from the data for a specific key handle and nonce.
     *
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @param data is either a string or a YubiHSM YubiKey secret
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateAEAD(String nonce, int keyHandle, String data) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateAEAD(deviceHandler, nonce, keyHandle, data);
    }

    /**
     * Generate a random AEAD block using the YubiHSM internal TRNG.
     * To generate a secret for a YubiKey use public_id as nonce.
     *
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param length the resulting byte length of the AEAD
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateRandomAEAD(String nonce, int keyHandle, int length) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateRandomAEAD(deviceHandler, nonce, keyHandle, length);
    }

    /**
     * Generate AEAD block of data buffer for a specific key.
     * After a key has been loaded into the internal data buffer, this command can be
     * used a number of times to get AEADs of the data buffer for different key handles.
     * For example, to encrypt a YubiKey secrets to one or more Yubico KSM's that
     * all have a YubiHSM attached to them.
     *
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateBufferAEAD(String nonce, int keyHandle) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateBufferAEAD(deviceHandler, nonce, keyHandle);
    }

    /**
     * Validate an AEAD using the YubiHSM, matching it against some known plain text.
     * Matching is done inside the YubiHSM so the decrypted AEAD is never exposed.
     *
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param aead the AEAD
     * @param plaintext the plain text string
     * @return returns true if validation was a success, false if the validation failed
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public boolean validateAEAD(String nonce, int keyHandle, String aead, String plaintext) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return AEADCmd.validateAEAD(deviceHandler, nonce, keyHandle, aead, plaintext);
    }

    /**
     * Load data into the YubiHSMs internal buffer.
     *
     * @param data the data to load into the internal buffer
     * @param offset the offset where to load the data, if set to 0 the buffer will reset before loading the data
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     */
    public int loadBufferData(String data, int offset) throws YubiHSMErrorException {
        return BufferCmd.loadData(deviceHandler, data, offset);
    }

    /**
     * Load data into the YubiHSMs internal buffer.
     *
     * @param data the data to load into the internal buffer
     * @param offset the offset where to load the data, if set to 0 the buffer will reset before loading the data
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     */
    public int loadBufferData(byte[] data, int offset) throws YubiHSMErrorException {
        return BufferCmd.loadData(deviceHandler, data, offset);
    }

    /**
     * Load random data into the YubiHSMs internal buffer.
     *
     * @param length the length of the generated data
     * @param offset the offset where to load the data, if set to 0 the buffer will reset before loading the data
     * @return the length of the loaded buffer
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     */
    public int loadRandomBufferData(int length, int offset) throws YubiHSMErrorException {
        return BufferCmd.loadRandomData(deviceHandler, length, offset);
    }

    /**
     * Tell the YubiHSM to exit to configuration mode (requires 'debug' mode enabled).
     */
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

    /**
     * Drain all remaining output from the YubiHSM, used for debugging.
     *
     * @return true if successful, false otherwise.
     */
    public boolean drainData() {
        return deviceHandler.drain();
    }

    /**
     * Get the raw device, used for debugging.
     *
     * @return the device handler
     */
    public DeviceHandler getRawDevice() {
        return deviceHandler;
    }

    /**
     * Get the minimum hash length.
     *
     * @return the minimum hash length
     */
    public int getMinHashLength() {
        return minHashLength;
    }

    /**
     * Set the minimum hash length.
     * @param value the minimum hash length
     */
    public void setMinHashLength(int value) {
        minHashLength = value;
    }
}
