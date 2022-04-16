/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.automation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class AutomationController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private ScrollPane automationPane;
    @FXML
    private VBox automationBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void addAutomation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationCard.fxml"));
            automationBox.getChildren().add(loader.load());
            AutomationCardController automationCardController = loader.getController();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void execAutomation(String button) throws IOException {
        if(button.contains("bytes: 03")){
            Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\PaoloDooM\\Desktop\\internet-fix.bat");
        }
        if(button.contains("bytes: 02")){
            Runtime.getRuntime().exec("cmd /c start \"\" \"D:\\pixel AVD shortcut\\Pixel_AVD.bat\"");
        }
        if(button.contains("bytes: 01")){
            Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\PaoloDooM\\AppData\\Local\\Discord\\Update.exe --processStart Discord.exe");
        }
    }
}
