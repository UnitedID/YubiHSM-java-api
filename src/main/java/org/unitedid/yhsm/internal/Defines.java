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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Defines {

    private Defines() {}

    /** Size definitions */
    final static public byte YSM_MAX_PKT_SIZE = 0x60;
    final static public byte YSM_AEAD_NONCE_SIZE = 6;

    /**
     * Command codes
     */
    final static public byte YSM_NULL = 0x00;
    final static public byte YSM_AEAD_GENERATE = 0x01;
    final static public byte YSM_BUFFER_AEAD_GENERATE = 0x02;
    final static public byte YSM_RANDOM_AEAD_GENERATE = 0x03;
    final static public byte YSM_AEAD_DECRYPT_CMP = 0x04;
    final static public byte YSM_BUFFER_LOAD = 0x20;
    final static public byte YSM_BUFFER_RANDOM_LOAD = 0x21;
    final static public byte YSM_ECHO = 0x23;
    final static public byte YSM_SYSTEM_INFO_QUERY = 0x26;

    final static public Map<Integer, String> COMMAND_TO_STRING = Collections.unmodifiableMap(new HashMap<Integer, String>() {{
        put(0x00, "YSM_NULL");
        put(0x01, "YSM_AEAD_GENERATE");
        put(0x02, "YSM_BUFFER_AEAD_GENERATE");
        put(0x03, "YSM_RANDOM_AEAD_GENERATE");
        put(0x04, "YSM_AEAD_DECRYPT_CMP");
        put(0x20, "YSM_BUFFER_LOAD");
        put(0x21, "YSM_BUFFER_RANDOM_LOAD");
        put(0x23, "YSM_ECHO");
        put(0x26, "YSM_SYSTEM_INFO_QUERY");
    }});

    /**
     * Respond codes
     */
    final static public Map<Integer, String> COMMAND_TO_STATUS = Collections.unmodifiableMap(new HashMap<Integer, String>() {{
        put(0x80, "YSM_STATUS_OK");
        put(0x81, "YSM_KEY_HANDLE_INVALID");
        put(0x84, "YSM_OTP_REPLAY");
    }});

    /** Last command executed successfully */
    final static public byte YSM_STATUS_OK = (byte) 0x80;
    final static public byte YSM_RESPONSE = (byte) 0x80;
    final static public byte YSM_MISMATCH = (byte) 0x8b;


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
