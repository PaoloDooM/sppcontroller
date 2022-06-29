/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class HttpConnController implements Initializable {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reset();
    }

    public String getIp() {
        return ip.getText() == null ? "" : ip.getText();
    }

    public String getPort() {
        return port.getText() == null ? "" : port.getText();
    }

    public void reset() {
        ip.setText("192.168.150.110");
        port.setText("80");
    }
    
    public void setDissabled(boolean val){
        ip.setDisable(val);
        port.setDisable(val);
    }

}
