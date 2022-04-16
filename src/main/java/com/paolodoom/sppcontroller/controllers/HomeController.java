/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

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
            
            loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/ConnectionView.fxml"));
            configurationTab.getChildren().add(loader.load());
            connectionController = loader.getController();
            connectionController.setAutomationController(automationController);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
