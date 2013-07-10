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

import java.util.HashMap;
import java.util.Map;

import static org.unitedid.yhsm.internal.Defines.*;
import static org.unitedid.yhsm.utility.Utils.*;

public class SystemInfoCmd {

    private int majorVersion;
    private int minorVersion;
    private int buildVersion;
    private String protocol;
    private String sysId;

    /**
     * Constructor to initiate a class with HSM system information
     *
     * @param device the deviceHandler
     * @throws YubiHSMErrorException
     */
    public SystemInfoCmd(DeviceHandler device) throws YubiHSMErrorException {
        parseResult(CommandHandler.execute(device, YSM_SYSTEM_INFO_QUERY, new byte[0], true));
    }

    /**
     * Gets the major version
     *
     * @return the major version
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Gets the minor version
     *
     * @return the minor version
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets the build version
     *
     * @return the build version
     */
    public int getBuildVersion() {
        return buildVersion;
    }

    /**
     * Gets the protocol version
     *
     * @return the protocol version
     */

    public String getProtocol() {
        return protocol;
    }

    /**
     * Gets the unique id of the device
     *
     * @return the unique id
     */
    public String getSysId() {
        return sysId;
    }

    /**
     * Gets the version in a string representation
     *
     * @return the version
     */
    public String getVersion() {
        return String.format("%s.%s.%s", majorVersion, minorVersion, buildVersion);
    }

    /**
     * Gets the version, protocol and unique id in a string representation
     *
     * @return the device system information
     */
    public String getSystemInfo() {
        return String.format("Version %s.%s.%s  Protocol=%s  SysId: %s",
                majorVersion, minorVersion, buildVersion, protocol, sysId);
    }


    /**
     * Parses the system info query result from the HSM
     *
     * @param data the result from the YSM_SYSTEM_INFO_QUERY
     */
    private void parseResult(byte[] data) {
        majorVersion =  new Integer(String.valueOf(data[0]));
        minorVersion = new Integer(String.valueOf(data[1]));
        buildVersion = new Integer(String.valueOf(data[2]));
        protocol = String.valueOf(data[3]);
        sysId = "0x" + byteArrayToHex(new String(data, 4, 12).getBytes());
    }
}
