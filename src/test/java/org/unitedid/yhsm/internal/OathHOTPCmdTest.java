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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitedid.yhsm.SetupCommon;
import org.unitedid.yhsm.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class OathHOTPCmdTest extends SetupCommon {

    private String nonce = "f1f2f3f4f5f6";
    private String aead;
    private int keyHandle = 8192;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        String seed = "3132333435363738393031323334353637383930";
        aead = hsm.generateOathHotpAEAD(nonce, keyHandle, seed);
        assertTrue(hsm.loadTemporaryKey(nonce, keyHandle, aead));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testOathHotpValues() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        List<OathHotpValueMap> testValueList = new ArrayList<OathHotpValueMap>();
        testValueList.add(new OathHotpValueMap(0, "cc93cf18508d94934c64b65d8ba7667fb7cde4b0", "755224"));
        testValueList.add(new OathHotpValueMap(1, "75a48a19d4cbe100644e8ac1397eea747a2d33ab", "287082"));
        testValueList.add(new OathHotpValueMap(2, "0bacb7fa082fef30782211938bc1c5e70416ff44", "359152"));
        testValueList.add(new OathHotpValueMap(3, "66c28227d03a2d5529262ff016a1e6ef76557ece", "969429"));
        testValueList.add(new OathHotpValueMap(4, "a904c900a64b35909874b33e61c5938a8e15ed1c", "338314"));
        testValueList.add(new OathHotpValueMap(30, "543c61f8f9aeb35f6dbc3a6847c3fe288cc0ee4c", "026920"));

        for (OathHotpValueMap o : testValueList) {
            String hmac = hsm.generateHMACSHA1(Utils.longToByteArray(o.getCounter()), Defines.YSM_TEMP_KEY_HANDLE, true, false).get("hash");
            assertEquals(o.getHmac(), hmac);
            assertEquals(o.getOtp(), OathHOTPCmd.truncate(hmac, 6));
        }
    }

    @Test
    public void testOathHotpValidation() throws YubiHSMCommandFailedException, YubiHSMErrorException, YubiHSMInputException {
        List<OathHotpCodeMap> testCodeList = new ArrayList<OathHotpCodeMap>();
        testCodeList.add(new OathHotpCodeMap(1, 0, "755224", 1));
        testCodeList.add(new OathHotpCodeMap(4, 0, "969429", 4));
        testCodeList.add(new OathHotpCodeMap(0, 0, "969429", 3));
        testCodeList.add(new OathHotpCodeMap(5, 3, "338314", 2));
        testCodeList.add(new OathHotpCodeMap(31, 30, "026920", 1));

        for (OathHotpCodeMap o : testCodeList) {
            assertEquals(o.getExpectedCounter(), hsm.validateOathHOTP(hsm, keyHandle, nonce, aead, o.getCounter(), o.getOtp(), o.getLookAhead()));
        }
    }

    @Test
    public void testOathHotpTruncateHMACLength() throws YubiHSMInputException {
        thrown.expect(YubiHSMInputException.class);
        OathHOTPCmd.truncate("cccccccccccccccccccccccccccccccccccccccccc", 6);
    }
}

class OathHotpValueMap {

    private int counter;
    private String hmac;
    private String otp;

    OathHotpValueMap(int counter, String hmac, String otp) {
        this.counter = counter;
        this.hmac = hmac;
        this.otp = otp;
    }

    public int getCounter() {
        return counter;
    }

    public String getHmac() {
        return hmac;
    }

    public String getOtp() {
        return otp;
    }
}

class OathHotpCodeMap {

    private int expectedCounter;
    private int counter;
    private String otp;
    private int lookAhead;

    OathHotpCodeMap(int expectedCounter, int counter, String otp, int lookAhead) {
        this.expectedCounter = expectedCounter;
        this.counter = counter;
        this.otp = otp;
        this.lookAhead = lookAhead;
    }

    public int getExpectedCounter() {
        return expectedCounter;
    }

    public int getCounter() {
        return counter;
    }

    public String getOtp() {
        return otp;
    }

    public int getLookAhead() {
        return lookAhead;
    }
}
