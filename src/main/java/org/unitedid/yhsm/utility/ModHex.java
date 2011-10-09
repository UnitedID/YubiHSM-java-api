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

package org.unitedid.yhsm.utility;

import org.apache.commons.lang.StringUtils;

public class ModHex {
    private static final String[] HEX = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private static final String[] MODHEX = {"c", "b", "d", "e", "f", "g", "h", "i", "j", "k", "l", "n", "r", "t", "u", "v"};

    /**
     * Encode a hex string as modHex
     *
     * @param input a hex string
     * @return modHex string
     */
    public static String encode(String input) {
        return StringUtils.replaceEach(input, HEX, MODHEX);
    }

    /**
     * Decode modHex as hex
     *
     * @param input a modHex string
     * @return hex string
     */
    public static String decode(String input) {
        return StringUtils.replaceEach(input, MODHEX, HEX);
    }

    /**
     * Decode a modHex string as a decimal number
     *
     * @param input a modHex string
     * @return long number
     */
    public static Long toDecimal(String input) {
        return Long.decode("#" + decode(input));
    }
}
