/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.connection;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.MenuButton;
/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class BtConnController implements Initializable {


    @FXML
    private MenuButton devicesDrop;

    public MenuButton getDevicesDrop() {
        return devicesDrop;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
