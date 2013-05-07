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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Defines {

    private Defines() {}

    /** Size definitions */
    final static public byte YSM_MAX_PKT_SIZE = 0x60;
    final static public int YSM_AEAD_NONCE_SIZE = 6;
    final static public int YSM_AEAD_MAC_SIZE = 8;
    final static public int YSM_DATA_BUF_SIZE = 64;
    final static public int YSM_MAX_KEY_SIZE = 32;
    final static public int YSM_AEAD_MAX_SIZE = YSM_DATA_BUF_SIZE + YSM_AEAD_MAC_SIZE;
    final static public int YSM_SHA1_HASH_SIZE = 20;
    final static public int YSM_PUBLIC_ID_SIZE = 6;
    final static public int YSM_OTP_SIZE = 16;
    final static public int YSM_BLOCK_SIZE = 16;
    final static public int UID_SIZE = 6;
    final static public int KEY_SIZE = 16;
    final static public int YSM_CTR_DRBG_SEED_SIZE = 32;
    final static public int YSM_YUBIKEY_AEAD_SIZE = KEY_SIZE + UID_SIZE + YSM_AEAD_MAC_SIZE;

    /**
     * HMAC flags
     */
    final static public byte YSM_HMAC_SHA1_RESET = 0x01;
    final static public byte YSM_HMAC_SHA1_FINAL = 0x02;
    final static public byte YSM_HMAC_SHA1_TO_BUFFER = 0x04;

    /**
     * Command codes
     */
    final static public byte YSM_NULL = 0x00;
    final static public byte YSM_AEAD_GENERATE = 0x01;
    final static public byte YSM_BUFFER_AEAD_GENERATE = 0x02;
    final static public byte YSM_RANDOM_AEAD_GENERATE = 0x03;
    final static public byte YSM_AEAD_DECRYPT_CMP = 0x04;
    final static public byte YSM_DB_YUBIKEY_AEAD_STORE = 0x05;
    final static public byte YSM_AEAD_YUBIKEY_OTP_DECODE = 0x06;
    final static public byte YSM_DB_OTP_VALIDATE = 0x07;
    final static public byte YSM_AES_ECB_BLOCK_ENCRYPT = 0x0d;
    final static public byte YSM_AES_ECB_BLOCK_DECRYPT = 0x0e;
    final static public byte YSM_AES_ECB_BLOCK_DECRYPT_CMP = 0x0f;
    final static public byte YSM_HMAC_SHA1_GENERATE = 0x10;
    final static public byte YSM_TEMP_KEY_LOAD = 0x11;
    final static public byte YSM_BUFFER_LOAD = 0x20;
    final static public byte YSM_BUFFER_RANDOM_LOAD = 0x21;
    final static public byte YSM_NONCE_GET = 0x22;
    final static public byte YSM_ECHO = 0x23;
    final static public byte YSM_RANDOM_GENERATE = 0x24;
    final static public byte YSM_RANDOM_RESEED = 0x25;
    final static public byte YSM_SYSTEM_INFO_QUERY = 0x26;
    final static public byte YSM_KEY_STORAGE_UNLOCK = 0x27;  /* Deprecated in 1.0 */
    final static public byte YSM_HSM_UNLOCK = 0x28;
    final static public byte YSM_KEY_STORE_DECRYPT = 0x29;
    final static public byte YSM_MONITOR_EXIT = 0x7f;

    /**
     * Other
     */
    final static public byte YSM_TEMP_KEY_HANDLE = 0xffffffff;


    final static public Map<Integer, String> COMMAND_TO_STRING = Collections.unmodifiableMap(new HashMap<Integer, String>() {{
        put(0x00, "YSM_NULL");
        put(0x01, "YSM_AEAD_GENERATE");
        put(0x02, "YSM_BUFFER_AEAD_GENERATE");
        put(0x03, "YSM_RANDOM_AEAD_GENERATE");
        put(0x04, "YSM_AEAD_DECRYPT_CMP");
        put(0x05, "YSM_DB_YUBIKEY_AEAD_STORE");
        put(0x06, "YSM_AEAD_YUBIKEY_OTP_DECODE");
        put(0x07, "YSM_DB_OTP_VALIDATE");
        put(0x0d, "YSM_AES_ECB_BLOCK_ENCRYPT");
        put(0x0e, "YSM_AES_ECB_BLOCK_DECRYPT");
        put(0x0f, "YSM_AES_ECB_BLOCK_DECRYPT_CMP");
        put(0x10, "YSM_HMAC_SHA1_GENERATE");
        put(0x11, "YSM_TEMP_KEY_LOAD");
        put(0x20, "YSM_BUFFER_LOAD");
        put(0x21, "YSM_BUFFER_RANDOM_LOAD");
        put(0x22, "YSM_NONCE_GET");
        put(0x23, "YSM_ECHO");
        put(0x24, "YSM_RANDOM_GENERATE");
        put(0x25, "YSM_RANDOM_RESEED");
        put(0x26, "YSM_SYSTEM_INFO_QUERY");
        put(0x27, "YSM_KEY_STORAGE_UNLOCK");
        put(0x28, "YSM_HSM_UNLOCK");
        put(0x29, "YSM_KEY_STORE_DECRYPT");
    }});

    /**
     * Respond codes
     */
    final static public Map<Integer, String> COMMAND_TO_STATUS = Collections.unmodifiableMap(new HashMap<Integer, String>() {{
        put(0x80, "YSM_STATUS_OK");
        put(0x81, "YSM_KEY_HANDLE_INVALID");
        put(0x82, "YSM_AEAD_INVALID");
        put(0x83, "YSM_OTP_INVALID");
        put(0x84, "YSM_OTP_REPLAY");
        put(0x85, "YSM_ID_DUPLICATE");
        put(0x86, "YSM_ID_NOT_FOUND");
        put(0x87, "YSM_DB_FULL");
        put(0x88, "YSM_MEMORY_ERROR");
        put(0x89, "YSM_FUNCTION_DISABLED");
        put(0x8a, "YSM_KEY_STORAGE_LOCKED");
        put(0x8b, "YSM_MISMATCH");
        put(0x8c, "YSM_INVALID_PARAMETER");
    }});

    /** Last command executed successfully */
    final static public byte YSM_STATUS_OK = (byte) 0x80;
    final static public byte YSM_RESPONSE = (byte) 0x80;
    final static public byte YSM_OTP_INVALID = (byte) 0x83;
    final static public byte YSM_MISMATCH = (byte) 0x8b;
    final static public byte YSM_KEY_STORAGE_LOCKED = (byte) 0x8a;


    final public static String getCommandString(byte b) {
        return COMMAND_TO_STRING.get((int)b);
    }

    final public static String getCommandStatus(byte b) {
        return COMMAND_TO_STATUS.get(((int)b & 0xff));
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
