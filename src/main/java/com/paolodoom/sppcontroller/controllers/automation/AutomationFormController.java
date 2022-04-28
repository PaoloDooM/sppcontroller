/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.automation;

import com.paolodoom.sppcontroller.models.Automation;
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
    @FXML
    private TextField buttonField;
    
    AutomationType selectedType = AutomationType.values()[0];
    AutomationController automationController;

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
        menuType.setText(selectedType.toString());
        setInputType(selectedType);
    }
        
    private void setAutomationType(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        menuType.setText(source.getText());
        Arrays.asList(AutomationType.values()).forEach(type->{
            if(type.toString().equals(source.getText())){
                setInputType(type);
            }
        });
    }
    
    public void setAutomationForm(Automation automation){
        if(automation == null){
            formLabel.setText("Add Item");
        }else{
            formLabel.setText("Edit Item");
            buttonField.setText(automation.getButton());
            setInputType(automation.getType());
            switch(selectedType){
                case keyCombination:
                    inputField.setText(String.join("", automation.getKeyCombination()));
                    break;
                case executable:
                    inputField.setText(automation.getPath());
                    break;
            }
        }
    }
    
    public void setInputType(AutomationType type){
        selectedType = type;
        switch(type){
            case keyCombination:
                inputFieldLabel.setText("Key combination");
                inputField.clear();
                break;
            case executable:
                inputFieldLabel.setText("Executable path");
                inputField.clear();
                break;
        }
    }
    
    public void setAutomationConroller(AutomationController automationController){
        this.automationController = automationController;
    }

    @FXML
    private void addCard(ActionEvent event) {
        switch(selectedType){
            case keyCombination:
                automationController.addAutomationCard(new Automation(selectedType, inputField.getText().split("%+"), buttonField.getText()));
                break;
            case executable:
                automationController.addAutomationCard(new Automation(selectedType, inputField.getText(), buttonField.getText()));
                break;
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        automationController.returnToViewMode();
    }
}
