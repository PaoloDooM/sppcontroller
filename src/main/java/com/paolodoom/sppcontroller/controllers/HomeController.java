/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.paolodoom.sppcontroller.controllers.connection.ConnectionController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import com.paolodoom.sppcontroller.controllers.automation.AutomationController;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class HomeController implements Initializable {

    @FXML
    private AnchorPane automartionTab;
    @FXML
    private AnchorPane screenTab;
    @FXML
    private AnchorPane configurationTab;
    @FXML
    private AnchorPane homeView;

    private AutomationController automationController;
    private ScreenController screenController;
    private ConnectionController connectionController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationView.fxml"));
            automartionTab.getChildren().add(loader.load());
            automationController = loader.getController();

            loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/connection/ConnectionView.fxml"));
            configurationTab.getChildren().add(loader.load());
            connectionController = loader.getController();
            connectionController.setAutomationController(automationController);

            loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/ScreenView.fxml"));
            screenTab.getChildren().add(loader.load());
            screenController = loader.getController();
            screenController.setConnectionController(connectionController);
            
            connectionController.setScreenController(screenController);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stopAll(){
        if(screenController!=null){
            screenController.stopSensorsTask();
        }
        if(connectionController!=null){
            connectionController.disconnect();
        } 
    }
}
