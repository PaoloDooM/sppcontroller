/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.automation;

import com.paolodoom.sppcontroller.models.AutomationType;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class AutomationFormController implements Initializable {

    @FXML
    private Label inputFieldLabel;
    @FXML
    private TextField inputField;
    @FXML
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label formLabel;
    @FXML
    private MenuButton menuType;
    
    AutomationType selectedType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuType.getItems().clear();
        Arrays.asList(AutomationType.values()).forEach(type->{
            MenuItem mi = new MenuItem(type.toString());
            mi.setOnAction(this::setAutomationType);
            menuType.getItems().add(mi);
        });
    }
        
    private void setAutomationType(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        menuType.setText(source.getText());
        Arrays.asList(AutomationType.keyCombination).forEach(type->{
            if(type.toString().equals(source.getText())){
                selectedType = type;
            }
        });
    }
}
