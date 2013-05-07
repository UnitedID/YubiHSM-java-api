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

package org.unitedid.yhsm.utility;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class ModHexTest {
    private final String hex = "cc55115abb";
    private final String modHex = "rrggbbglnn";
    private final Long decimal = 877600529083L;

    @Test
    public void testEncodeHex() {
        assertEquals(ModHex.encode(hex), modHex);
    }

    @Test
    public void testDecodeHex() {
        assertEquals(ModHex.decode(modHex), hex);
    }

    @Test
    public void testModHexToDecimal() {
        assertEquals(ModHex.toDecimal(modHex), decimal);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testModHexInvalidInput() {
        ModHex.encode("xxxx");
    }
}

