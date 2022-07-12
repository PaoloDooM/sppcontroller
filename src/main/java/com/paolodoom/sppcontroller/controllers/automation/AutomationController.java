/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.automation;

import com.paolodoom.sppcontroller.models.Automation;
import com.paolodoom.sppcontroller.models.AutomationType;
import com.paolodoom.sppcontroller.services.PersistanceService;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.servlet.Filter;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class AutomationController implements Initializable {
    
    static final String automationObjKey = "automationObj";
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
        try {
            for (Automation automation : PersistanceService.getAllAutomations()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationCard.fxml"));
                    Node autoCard = loader.load();
                    autoCard.getProperties().put(automationObjKey, automation);
                    automationBox.getChildren().add(autoCard);
                    AutomationCardController cardController = loader.getController();
                    cardController.setAutomation(automation);
                    cardController.setAutomationController(this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(AutomationController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void execAutomation(String button) throws Exception {
        for(Node node : automationBox.getChildren()){
            Automation automation = ((Automation) node.getProperties().get(automationObjKey));
            if(automation.getButton().contains(button)){
                if(automation.getType() == AutomationType.executable){
                    Runtime.getRuntime().exec("cmd /c start \"\" \"" + automation.getPath() + "\"");
                }else{
                    throw new UnsupportedOperationException("Unimplemented");
                }
            }
        }
    }

    public void addAutomationCard(Automation automation) {
        try {
            PersistanceService.insertAutomation(automation);
            automationView.getChildren().remove(automationView.getChildren().size() - 1);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationCard.fxml"));
            Node autoCard = loader.load();
            autoCard.getProperties().put(automationObjKey, automation);
            automationBox.getChildren().add(autoCard);
            AutomationCardController automationCardController = loader.getController();
            automationCardController.setAutomation(automation);
            automationCardController.setAutomationController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnToViewMode() {
        automationView.getChildren().remove(automationView.getChildren().size() - 1);
    }

    public void deleteAutomationCard(Automation automation) throws Exception {
        PersistanceService.deleteAutomation(automation);
        automationBox.getChildren().removeIf(e -> ((Automation) e.getProperties().get(automationObjKey)).getId() == automation.getId());
    }
}
