/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.automation;

import com.paolodoom.sppcontroller.models.Automation;
import com.paolodoom.sppcontroller.models.AutomationType;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class AutomationCardController implements Initializable {

    Automation automation = null;
    AutomationController autoCtrl;

    @FXML
    private Label titleLabel;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label textLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleLabel.setText("");
        textLabel.setText("");
    }

    public void setAutomation(Automation automation) {
        this.automation = automation;
        titleLabel.setText("Button: " + automation.getButton());
        switch (automation.getType()) {
            case executable:
                textLabel.setText("Exec = \"" + automation.getPath() + "\"");
                break;
            case keyCombination:
                textLabel.setText("Keys = " + String.join("~", automation.getKeyCombination()) + ".");
                break;
            case mouseEvents:
                textLabel.setText("Events = " + String.join("~", automation.getKeyCombination()) + ".");
                break;
        }
    }

    @FXML
    private void edit(ActionEvent event) {
        autoCtrl.editAutomation(automation);
    }

    @FXML
    private void delete(ActionEvent event) {
        try {
            autoCtrl.deleteAutomationCard(automation);
        } catch (Exception ex) {
            Logger.getLogger(AutomationCardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAutomationController(AutomationController autoCtrl) {
        this.autoCtrl = autoCtrl;
    }

}
