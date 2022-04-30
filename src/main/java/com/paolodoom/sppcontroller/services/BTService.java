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
    String hc05Url = "btspp://hc05Addr:1;authenticate=false;encrypt=false;master=false";
    List<RemoteDevice> devices;
    OutputStream dos;
    InputStream dis;
    StreamConnection sconn;

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
        System.out.println(device.getBluetoothAddress());
        String url = hc05Url.replace("hc05Addr", device.getBluetoothAddress());
        System.out.println(device);

        //if you know your hc05Url this is all you need:
        sconn = (StreamConnection) Connector.open(url);
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
    }

    public void write(String data) throws Exception {
        dos.write(data.getBytes());
        dos.flush();
    }

    public String read() throws Exception {
        byte[] b = dis.readNBytes(4);
        System.out.println("4: " + (new String(b)));
        return "Read: " + (new String(b));
    }

    public void go() throws Exception {
        //scan for all devices:
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                try {
                    String name = btDevice.getFriendlyName(false);
                    System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
                    if (name.matches("HC.*")) {
                        hc05device = btDevice;
                        System.out.println("got it!");
                    }
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

        //PaoloDooM: fails in my system, needs a method for get UUID vals.
        //search for services:
        UUID uuid = new UUID(0x1101); //scan for btspp://... services (as HC-05 offers it)
        UUID[] searchUuidSet = new UUID[]{uuid};
        int[] attrIDs = new int[]{
            0x0100 // service name
        };
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet,
                hc05device, new DiscoveryListener() {
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
                for (int i = 0; i < servRecord.length; i++) {
                    hc05Url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (hc05Url != null) {
                        break; //take the first one
                    }
                }
            }
        });

        while (!scanFinished) {
            Thread.sleep(500);
        }

        System.out.println(hc05device.getBluetoothAddress());
        hc05Url = hc05Url.replace("hc05Addr", hc05device.getBluetoothAddress());
        System.out.println(hc05Url);

        //if you know your hc05Url this is all you need:
        StreamConnection streamConnection = (StreamConnection) Connector.open(hc05Url);
        OutputStream os = streamConnection.openOutputStream();
        InputStream is = streamConnection.openInputStream();

        os.write("1111".getBytes()); //just send '1' to the device
        os.flush();

        byte[] b = is.readNBytes(4);
        System.out.println("4:" + (new String(b)));

        os.close();
        is.close();
        streamConnection.close();
    }
}
