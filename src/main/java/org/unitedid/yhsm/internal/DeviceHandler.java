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

import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeviceHandler {
    /** Logger */
    private final Logger log = LoggerFactory.getLogger(DeviceHandler.class);

    /** The YubiHSM device */
    private SerialPort device;

    private OutputStream writeStream;
    private InputStream readStream;

    private int readBytes = 0;
    private int writtenBytes = 0;

    /** Constructor */
    DeviceHandler(String deviceName, int timeout) {
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(deviceName);
            device = (SerialPort) portId.open("YHSM", 1);
            device.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            device.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            writeStream = device.getOutputStream();
            readStream = device.getInputStream();
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e ) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void write(byte[] data) {
        try {
            writtenBytes += data.length;
            writeStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected byte[] read(int readNumBytes) {
        byte[] data = new byte[readNumBytes];
        try {
            readBytes += readStream.read(data, 0, readNumBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    protected int available() {
        try {
            return readStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void flush() throws IOException {
        writeStream.flush();
    }

    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    protected void finalize() throws Throwable {
        try {
            readStream.close();
            writeStream.close();
            device.close();
        } finally {
            super.finalize();
        }
    }
}

