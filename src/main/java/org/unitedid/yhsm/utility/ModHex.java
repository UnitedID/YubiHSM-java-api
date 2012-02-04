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

public class ModHex {
    private static final String HEX = "0123456789abcdef";
    private static final String MODHEX = "cbdefghijklnrtuv";

    /**
     * Encode a hex string as modHex
     *
     * @param input a hex string
     * @return modHex string
     */
    public static String encode(String input) {
        return replaceEach(input, HEX, MODHEX);
    }

    /**
     * Decode modHex as hex
     *
     * @param input a modHex string
     * @return hex string
     */
    public static String decode(String input) {
        return replaceEach(input, MODHEX, HEX);
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

    /**
     * Replace characters in a string
     *
     * @param input String to replace characters in
     * @param searchList String of characters to search for
     * @param replacementList String of characters to replace with
     * @return modified string
     */
    private static String replaceEach(String input, String searchList, String replacementList) {
        StringBuilder buf = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            int index = searchList.indexOf(ch);
            if (index >= 0) {
                buf.append(replacementList.charAt(index));
            } else {
                buf.append(ch);
            }
        }

        return buf.toString();
    }
}
