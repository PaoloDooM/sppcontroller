/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.automation;

import com.paolodoom.sppcontroller.models.Automation;
import com.paolodoom.sppcontroller.models.AutomationType;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
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
    @FXML
    private AnchorPane automationView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 0; i < 4; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationCard.fxml"));
                automationBox.getChildren().add(loader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void addAutomation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationForm.fxml"));
            automationView.getChildren().add(loader.load());
            AutomationFormController automationFormController = loader.getController();
            automationFormController.setAutomationForm(null);
            automationFormController.setAutomationConroller(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void execAutomation(String button) throws IOException {
        if (button.contains("$03$")) {
            Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\PaoloDooM\\Desktop\\internet-fix.bat");
        }
        if (button.contains("$02$")) {
            Runtime.getRuntime().exec("cmd /c start \"\" \"D:\\pixel AVD shortcut\\Pixel_AVD.bat\"");
        }
        if (button.contains("$01$")) {
            Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\PaoloDooM\\AppData\\Local\\Discord\\Update.exe --processStart Discord.exe");
        }
        if (button.contains("$00$")) {
            Runtime.getRuntime().exec("cmd /c start \"\" \"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" https://github.com/PaoloDooM/sppcontroller");
        }
    }

    public void addAutomationCard(Automation automation) {
        try {
            automationView.getChildren().remove(automationView.getChildren().size() - 1);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationCard.fxml"));
            automationBox.getChildren().add(loader.load());
            AutomationCardController automationCardController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnToViewMode() {
        automationView.getChildren().remove(automationView.getChildren().size() - 1);
    }
}
