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

/** <code>Nonce</code> a class that represent an YSM_NONCE_GET */
public class Nonce {

    private int vtile;
    private int powerUpCount;
    private int nonceInt;
    private String nonce;

    public Nonce(int vtile, int powerUpCount, int nonceInt, String nonce) {
        this.vtile = vtile;
        this.powerUpCount = powerUpCount;
        this.nonceInt = nonceInt;
        this.nonce = nonce;
    }

    public int getVolative() {
        return vtile;
    }

    public int getPowerUpCount() {
        return powerUpCount;
    }

    public int getNonceInt() {
        return nonceInt;
    }

    public String getNonce() {
        return nonce;
    }

    public String toString() {
        return "Nonce: " + nonce + " Power up count: " + powerUpCount + " Volatile: " + vtile;
    }
}
