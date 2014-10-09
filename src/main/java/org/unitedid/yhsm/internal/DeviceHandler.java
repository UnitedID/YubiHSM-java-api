/*
 * Copyright (c) 2011 - 2014 United ID.
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

import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.unitedid.yhsm.utility.Utils.concatAllArrays;

public class DeviceHandler {
    /** Logger */
    private final Logger log = LoggerFactory.getLogger(DeviceHandler.class);

    /** The YubiHSM device */
    private SerialPort device;

    private int readBytes = 0;
    private int writtenBytes = 0;

    private float timeout = 0.5f;

    /**
     * Constructor
     *
     * @param deviceName the YubiHSM device name
     */
    DeviceHandler(String deviceName) throws YubiHSMErrorException {
        device = new SerialPort(deviceName);
        try {
            device.openPort();
            device.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            device.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (SerialPortException e) {
            throw new YubiHSMErrorException("Failed to open device " + deviceName, e);
        }
    }

    public void write(byte[] data) {
        try {
            writtenBytes += data.length;
            device.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public byte[] read(int readNumBytes) {
        byte[] data = new byte[readNumBytes];
        try {
            data = device.readBytes(readNumBytes);
            readBytes += data.length;
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        return data;
    }

    public int available() {
        try {
            return device.getInputBufferBytesCount();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean drain() {
        byte[] buffer = new byte[0];
        while(available() > 0) {
            byte[] b = read(1);
            if ((char)b[0] == '\r') {
                log.info("Drained: {}", new String(buffer, 0, buffer.length)); //TODO: Do we really need to log this? If not the loop can be simplified.
                buffer = new byte[0];
            } else {
                buffer = concatAllArrays(buffer, b);
            }
        }
        return true;
    }

    public void flush() throws IOException {
        try {
            device.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public float getTimeout() {
        return timeout;
    }

    public void setTimeout(float timeout) {
        this.timeout = timeout;
    }

    public int getReadBytes() {
        return readBytes;
    }

    public int getWrittenBytes() {
        return writtenBytes;
    }

    public String getPortName() {
        return device.getPortName();
    }

    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    protected void finalize() throws Throwable {
        try {
            device.closePort();
        } finally {
            super.finalize();
        }
    }
}

