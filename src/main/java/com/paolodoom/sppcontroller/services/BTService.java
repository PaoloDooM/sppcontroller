/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author PaoloDooM
 */
public class BTService {

    boolean scanFinished = false;
    RemoteDevice hc05device;
    String hc05Url;
    List<RemoteDevice> devices;
    OutputStream dos;
    InputStream dis;
    StreamConnection sconn;
    public final float chunkSize = 4;

    public List<RemoteDevice> getDevices() throws Exception {
        devices = new ArrayList<>();
        //scan for all devices:
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                try {
                    String name = btDevice.getFriendlyName(false);
                    System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
                    devices.add(btDevice);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void inquiryCompleted(int discType) {
                scanFinished = true;
            }

            @Override
            public void serviceSearchCompleted(int transID, int respCode) {
            }

            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        });
        while (!scanFinished) {
            //this is easier to understand (for me) as the thread stuff examples from bluecove
            Thread.sleep(500);
        }
        return devices;
    }

    public void connect(RemoteDevice device) throws Exception {
        UUID uuid = new UUID(0x1101); //scan for btspp://... services (as HC-05 offers it)
        UUID[] searchUuidSet = new UUID[]{uuid};
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(null, searchUuidSet,
                device, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            }

            @Override
            public void inquiryCompleted(int discType) {
            }

            @Override
            public void serviceSearchCompleted(int transID, int respCode) {
                scanFinished = true;
            }

            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                Arrays.asList(servRecord).forEach(s -> {
                    if (hc05Url == null) {
                        hc05Url = s.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    }
                    System.out.println("Service: " + s.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
                });
            }
        });
        while (!scanFinished) {
            Thread.sleep(500);
        }

        System.out.println(device.getBluetoothAddress());
        System.out.println(hc05Url);

        //if you know your hc05Url this is all you need:
        sconn = (StreamConnection) Connector.open(hc05Url);
        dos = sconn.openOutputStream();
        dis = sconn.openInputStream();
    }

    public void disconnect() throws Exception {
        if (dos != null) {
            dos.close();
            dos = null;
        }
        if (dis != null) {
            dis.close();
            dis = null;
        }
        if (sconn != null) {
            sconn.close();
            sconn = null;
        }
        hc05Url = null;
    }

    public void write(String data) throws Exception {
        byte[] writeBuffer = data.getBytes();
        double chunks = writeBuffer.length / chunkSize;
        int iterations = ((int) chunks) + ((chunks - ((int) chunks)) > 0.0 ? 1 : 0);
        for (int i = 0; i < iterations; i++) {
            byte[] chunk = Arrays.copyOfRange(writeBuffer, i * ((int) chunkSize), (i * ((int) chunkSize)) + ((int) chunkSize));
            dos.write(data.getBytes());
            dos.flush();
            System.out.println("Write " + chunk.length + " bytes.");
            System.out.println("Data " + new String(chunk));
        }
    }

    public String read() throws Exception {
        byte[] b = dis.readNBytes((int) chunkSize);
        System.out.println("Read " + b.length + " bytes: " + (new String(b)));
        return "Read: " + (new String(b));
    }
}
