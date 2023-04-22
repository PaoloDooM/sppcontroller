/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;

import com.paolodoom.sppcontroller.components.ApplicationContextHolder;
import com.paolodoom.sppcontroller.utils.ParameterStringBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author PaoloDooM
 */
public class HttpService {

    private String ip = "";
    private String port = "";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void connect(String ip, String port) {
        this.ip = ip;
        this.port = port;
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("ipAddress", getLocalIpAddress());

            URL url = new URL("http://" + ip + ":" + port + "/connect");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "*/*");

            con.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            int code = con.getResponseCode();
            System.out.println(code);
            StringBuilder response;

            if (code < 300) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"));
                response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(HttpService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        write(Arrays.asList(this.ip));
        this.ip = "";
        this.port = "";
    }

    public void write(List<String> data) {
        data.remove(0);
        String display = "";
        for (String val : data) {
            display += "~" + val.trim();
        }
        display = display.replaceFirst("~", "");
        System.out.println("Sending: " + display);
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("display", display);

            URL url = new URL("http://" + ip + ":" + port + "/display");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "*/*");

            con.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            int code = con.getResponseCode();
            System.out.println(code);
            StringBuilder response;

            if (code < 300) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"));
                response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(HttpService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String read() throws InterruptedException{
        //for(int i = 0; i < btnSrvc.getButtonsLength(); i++){
        String btn;
        do{
            ButtonService btnSrvc = ApplicationContextHolder.getContext().getBean(ButtonService.class);
            btn = btnSrvc.getButton();
            Thread.sleep(100);
        }while(btn==null);
        return btn;
        //}
    }

    public String getLocalIpAddress() {
        try {
            String ip = Inet4Address.getLocalHost().getHostAddress();
            System.out.println("localIp: " + ip);
            return "192.168.150.100";
        } catch (Exception e) {
            System.out.println("Error getting local ip:\n" + e.getMessage());
            return "";
        }
    }

    public String getIpAddress() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            System.out.println("Error getting ip:\n" + e.getMessage());
            return "";
        }
    }
}
