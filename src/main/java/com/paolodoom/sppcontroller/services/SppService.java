/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author PaoloDooM
 */
public class SppService {

    public List<SerialPort> getPorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        return Arrays.asList(ports);
    }

    public void connect(SerialPort readPort, SerialPort writePort) {
            readPort.openPort(1000);
            writePort.openPort(1000);
        }

    public void disconnect(SerialPort readPort, SerialPort writePort) {
        if (readPort != null) {
            readPort.closePort();
        }
        if (writePort != null) {
            writePort.closePort();
        }
    }

    public String read(SerialPort readPort) {
        readPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        try {
            byte[] readBuffer = new byte[4];
            int numRead = readPort.readBytes(readBuffer, readBuffer.length);
            return "Read " + numRead + " bytes: " + new String(readBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public void write(SerialPort writePort, String data) {
        double chunkSize = 4.0;
        byte[] writeBuffer = data.getBytes();
        double chunks = writeBuffer.length / chunkSize;
        writePort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        int iterations = ((int) chunks) + ((chunks - ((int) chunks)) > 0.0 ? 1 : 0);
        while (writeBuffer.length / chunkSize - ((int) writeBuffer.length / chunkSize) != 0.0) {
            int length = writeBuffer.length;
            writeBuffer = Arrays.copyOf(writeBuffer, length + 1);
            writeBuffer[length] = 0;
        }
        try {
            for (int i = 0; i < iterations; i++) {
                byte[] chunk = Arrays.copyOfRange(writeBuffer, i * ((int) chunkSize), (i * ((int) chunkSize)) + ((int) chunkSize));
                int numWrite = writePort.writeBytes(chunk, chunk.length);
                System.out.println("Write " + numWrite + " bytes.");
                System.out.println("Data " + new String(writeBuffer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
