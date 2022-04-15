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

import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class ConnectionController implements Initializable {

    @FXML
    private MenuButton receiveDrop;
    @FXML
    private MenuButton sendDrop;
    @FXML
    private Button connect;
    @FXML
    private Button disconnect;
    @FXML
    private TextArea debugLog;
    @FXML
    private Button refresh;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
