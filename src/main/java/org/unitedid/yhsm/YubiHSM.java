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

import static org.unitedid.yhsm.utility.Utils.*;

/** <code>YubiHSM</code> the main class to use for YubiHSM commands */
public class YubiHSM  {
    /** Logger */
    private final Logger log = LoggerFactory.getLogger(YubiHSM.class);

    /** The device handler */
    private DeviceHandler deviceHandler;

    /** The hash length, default is 20 */
    public static int minHashLength = 20;

    /* YubiHSM sysinfo cache */
    private Map<String, String> info;

    /**
     * Constructor
     *
     * @param device the YubiHSM device name ie /dev/ttyACM0
     * @param timeout the command read timeout
     * @throws YubiHSMErrorException if the YubiHSM reset command fail
     */
    public YubiHSM(String device, float timeout) throws YubiHSMErrorException {
        deviceHandler = DeviceHandlerFactory.get(device, timeout);
        CommandHandler.reset(deviceHandler);
        info = SystemInfoCmd.execute(deviceHandler);
    }

    /**
     * Test the YubiHSM by sending a string that the YubiHSM will echo back.
     *
     * @param str the string that the YubiHSM should return
     * @return the the same string sent to the YubiHSM
     * @throws YubiHSMErrorException if the YubiHSM echo command fail
     */
    public String echo(String str) throws YubiHSMErrorException {
        return EchoCmd.execute(deviceHandler, str);
    }

    /**
     * Get the firmware version and unique ID from the YubiHSM.
     *
     * @return a map with version, protocol and unique ID
     * @throws YubiHSMErrorException if the YubiHSM info command fail
     */
    public Map<String, String> info() throws YubiHSMErrorException {
        return info;
    }

    /**
     * Get the firmware version and unique ID from the YubiHSM (string representation).
     *
     * @return a string with version, protocol and unique ID
     * @throws YubiHSMErrorException if the YubiHSM info command fail
     */
    public String infoToString() throws YubiHSMErrorException {
        return String.format("Version %s.%s.%s  Protocol=%s  SysId: %s", info.get("major"), info.get("minor"),
                info.get("build"), info.get("protocol"),
                info.get("sysid"));
    }

    /**
     * Generate AEAD block from the data for a specific key handle and nonce.
     *
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @param data is the data to turn into an AEAD
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateAEAD(String nonce, int keyHandle, byte[] data) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateAEAD(deviceHandler, nonce, keyHandle, data);
    }

    /**
     * Generate AEAD block from the data for a specific key handle and nonce.
     *
     * @param nonce the nonce
     * @param keyHandle the key to use
     * @param data is the data to turn into an AEAD
     * @return a hash map with the AEAD and nonce
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public Map<String, String> generateAEAD(String nonce, int keyHandle, String data) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AEADCmd.generateAEAD(deviceHandler, nonce, keyHandle, data.getBytes());
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
     * Generate AEAD block which can be used for OATH-HOTP OTP validation, see <code>validateOathHOTP</code>.
     *
     * @param nonce the nonce
     * @param keyHandle the key handle with permission to generateBufferAEAD
     * @param tokenSeed the OATH HOTP token seed
     * @return returns an AEAD
     * @throws YubiHSMInputException thrown if an argument fail to validate
     * @throws YubiHSMErrorException thrown if an error have occurred
     * @throws YubiHSMCommandFailedException thrown if the YubiHSM fail to execute a command
     */
    public String generateOathHotpAEAD(String nonce, int keyHandle, String tokenSeed) throws YubiHSMInputException, YubiHSMErrorException, YubiHSMCommandFailedException {
        if (tokenSeed.length() != 40)
            throw new YubiHSMInputException("Seed is not of required length, got " + tokenSeed.length() + " but expected 40");

        byte[] seed = hexToByteArray(tokenSeed);
        byte[] flag = leIntToBA(0x10000); // Generate HMAC SHA1 Flag
        loadBufferData(concatAllArrays(seed, flag), 0);

        return generateBufferAEAD(nonce, keyHandle).get("aead");
    }

    /**
     * Validate an AEAD using the YubiHSM, matching it against some known plain text.
     * Matching is done inside the YubiHSM so the decrypted AEAD is never exposed.
     *
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param aead the AEAD (hex string)
     * @param plaintext the plain text data
     * @return returns true if validation was a success, false if the validation failed
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public boolean validateAEAD(String nonce, int keyHandle, String aead, byte[] plaintext) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return AEADCmd.validateAEAD(deviceHandler, nonce, keyHandle, aead, plaintext);
    }

    /**
     * Validate an AEAD using the YubiHSM, matching it against some known plain text.
     * Matching is done inside the YubiHSM so the decrypted AEAD is never exposed.
     *
     * @param nonce the nonce or public_id
     * @param keyHandle the key to use
     * @param aead the AEAD (hex string)
     * @param plaintext the plain text data
     * @return returns true if validation was a success, false if the validation failed
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMInputException if an argument does not validate
     */
    public boolean validateAEAD(String nonce, int keyHandle, String aead, String plaintext) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return AEADCmd.validateAEAD(deviceHandler, nonce, keyHandle, aead, plaintext.getBytes());
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
     *
     * @throws YubiHSMErrorException if the YubiHSM exit monitor command fail
     */
    public void exitMonitorDebugMode() throws YubiHSMErrorException {
        MonitorExitCmd.execute(deviceHandler);
    }

    /**
     * Load the content of an AEAD into the phantom key handle 0xffffffff.
     *
     * @param nonce the nonce
     * @param keyHandle the key handle with permission to use YSM_TEMP_KEY_LOAD
     * @param aead the AEAD to load into the phantom key handle
     * @return returns true if the AEAD was successfully loaded
     * @throws YubiHSMCommandFailedException command fail exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException argument exceptions
     */
    public boolean loadTemporaryKey(String nonce, int keyHandle, String aead) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return LoadTemporaryKeyCmd.execute(deviceHandler, nonce, keyHandle, aead);
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
        return HMACCmd.generateHMACSHA1(deviceHandler, data.getBytes(), keyHandle, (byte) 0, last, toBuffer);
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
    public Map<String, String> generateHMACSHA1(byte[] data, int keyHandle, boolean last, boolean toBuffer) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
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
        return HMACCmd.generateHMACSHA1(deviceHandler, data.getBytes(), keyHandle, flags, last, toBuffer);
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
    public Map<String, String> generateHMACSHA1(byte[] data, int keyHandle, byte flags, boolean last, boolean toBuffer) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
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
        return HMACCmd.next(deviceHandler, data.getBytes(), keyHandle, last, toBuffer);
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
    public Map<String, String> generateHMACSHA1Next(byte[] data, int keyHandle, boolean last, boolean toBuffer) throws YubiHSMInputException, YubiHSMCommandFailedException, YubiHSMErrorException {
        return HMACCmd.next(deviceHandler, data, keyHandle, last, toBuffer);
    }

    /**
     * AES ECB encrypt a plaintext string using a specific key handle.
     *
     * @param keyHandle the key handle to use when encrypting AES ECB
     * @param plaintext the plaintext string
     * @return a hash string in hex format
     * @throws YubiHSMInputException if an argument does not validate
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    public String encryptAES_ECB(String plaintext, int keyHandle) throws YubiHSMErrorException, YubiHSMInputException, YubiHSMCommandFailedException {
        return AESECBCmd.encrypt(deviceHandler, keyHandle, plaintext);
    }

    /**
     * AES ECB decrypt a cipher text using a specific key handle.
     *
     * @param keyHandle the key handle to use when decrypting AES ECB
     * @param cipherText the cipher string
     * @return a plaintext string
     * @throws YubiHSMInputException if an argument does not validate
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    public String decryptAES_ECB(String cipherText, int keyHandle) throws YubiHSMErrorException, YubiHSMInputException, YubiHSMCommandFailedException {
        return AESECBCmd.decrypt(deviceHandler, keyHandle, cipherText);
    }

    /**
     * AES ECB decrypt a cipher text using a specific key handle, and then compare it with the supplied plaintext.
     *
     * @param keyHandle the key handle to use when comparing AES ECB cipher with plaintext
     * @param cipherText the cipher string
     * @param plaintext the plaintext string
     * @return true if successful, false if not successful
     * @throws YubiHSMInputException if an argument does not validate
     * @throws YubiHSMErrorException if validation fail for some values returned by the YubiHSM
     * @throws YubiHSMCommandFailedException if the YubiHSM fail to execute the command
     */
    public boolean compareAES_ECB(int keyHandle, String cipherText, String plaintext) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return AESECBCmd.compare(deviceHandler, keyHandle, cipherText, plaintext);
    }

    /**
     * Generic key store unlock method that calls the appropriate unlock function for this YubiHSM.
     *
     * @param password the Master key/HSM password in hex format
     * @return true if unlock/decrypt was successful, otherwise an YubiHSMCommandFailedException is thrown
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException argument exception
     *
     * @see #keyStoreDecrypt(String)
     * @see #keyStorageUnlock(String)
     */
    public boolean unlock(String password) throws YubiHSMErrorException, YubiHSMCommandFailedException, YubiHSMInputException {
        if (info().get("major").equals("0")) {
            return keyStorageUnlock(password);
        } else {
            return keyStoreDecrypt(password);
        }
    }

    /**
     * Decrypt the YubiHSM key storage using the Master key.
     *
     * @param key the Master key in hex format (see output of automatic Master key generation during HSM configuration)
     * @return true if unlock was successful, otherwise an YubiHSMCommandFailedException is thrown
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException argument exception
     */
    public boolean keyStoreDecrypt(String key) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return KeyStoreDecryptCmd.execute(deviceHandler, key);
    }

    /**
     * Unlock the YubiHSM key storage using the HSM password.
     *
     * @param password the password in hex format (see output of automatic password generation during HSM configuration)
     * @return true if unlock was successful, otherwise an YubiHSMCommandFailedException is thrown
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException argument exception
     */
    public boolean keyStorageUnlock(String password) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return KeyStorageUnlockCmd.execute(deviceHandler, password);
    }

    /**
     * Have the YubiHSM unlock the HSM operations (those involving the keystore) with a YubiKey OTP.
     *
     * @param device the YubiHSM device
     * @param publicId the YubiKey public id (in hex)
     * @param otp the YubiKey OTP (in hex)
     * @return true if unlock was successful
     * @throws YubiHSMErrorException error exceptions
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     */
    public boolean unlockOtp(String publicId, String otp) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return HsmUnlockCmd.unlockOtp(deviceHandler, publicId, otp);
    }

    /**
     * Validate OATH-HOTP OTP by a token whose seed is available to the YubiHSM through an AEAD.
     *
     * @param hsm the current hsm object
     * @param keyHandle a keyHandle with the permission YSM_TEMP_KEY_LOAD enabled
     * @param nonce the nonce used to generate the AEAD
     * @param aead the AEAD based on the token seed
     * @param counter the current OTP counter
     * @param otp the token OTP
     * @param lookAhead the number of iterations to run to find the current users OTP
     * @return return next counter value on success, 0 if the OTP couldn't be validated
     * @throws YubiHSMInputException argument exceptions
     * @throws YubiHSMCommandFailedException command failed exception
     * @throws YubiHSMErrorException error exception
     */
    public int validateOathHOTP(YubiHSM hsm, int keyHandle, String nonce, String aead, int counter, String otp, int lookAhead) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return OathHOTPCmd.validateOTP(hsm, keyHandle, nonce, aead, counter, otp, lookAhead);
    }


    /**
     * Get a nonce from the YubiHSM. Increment the nonce by the number supplied as increment.
     * To get the current nonce send 0 as increment.
     *
     * @param increment the increment (short)
     * @return returns a Nonce class
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMCommandFailedException command failed exception
     */
    public Nonce getNonce(short increment) throws YubiHSMErrorException, YubiHSMCommandFailedException {
        return NonceGetCmd.execute(deviceHandler, increment);
    }

    /**
     * Tell the YubiHSM to generate a number of random bytes.
     *
     * @param bytes the number of bytes to generate
     * @return returns a byte array of random bytes
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMInputException invalid argument exception
     */
    public byte[] getRandom(int bytes) throws YubiHSMErrorException, YubiHSMInputException {
        return RandomCmd.execute(deviceHandler, bytes);
    }

    /**
     *  Provide YubiHSM DRBG_CTR with a new seed.
     *  The seed is a string of a length 32.
     *
     * @param seed the seed string with a length of 32
     * @return return true on success, otherwise a YubiHSMCommandFailedException is thrown
     * @throws YubiHSMInputException argument exception
     * @throws YubiHSMErrorException error exception
     * @throws YubiHSMCommandFailedException command failed exception
     */
    public boolean randomReseed(String seed) throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        return RandomReseedCmd.execute(deviceHandler, seed);
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
