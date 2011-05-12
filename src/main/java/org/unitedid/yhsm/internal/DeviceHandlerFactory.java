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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DeviceHandlerFactory {
    /** Logger */
    private final Logger log = LoggerFactory.getLogger(DeviceHandlerFactory.class);

    private static Map<String, DeviceHandler> deviceHandlerFactoryMap = new HashMap<String, DeviceHandler>();

    private DeviceHandlerFactory() {}

    public static DeviceHandler get(String device, int timeout) {
        synchronized (deviceHandlerFactoryMap) {
            DeviceHandler deviceHandler = deviceHandlerFactoryMap.get(device);
            if (deviceHandler == null) {
                deviceHandler = new DeviceHandler(device, timeout);
                deviceHandlerFactoryMap.put(device, deviceHandler);
            }
            return deviceHandler;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
