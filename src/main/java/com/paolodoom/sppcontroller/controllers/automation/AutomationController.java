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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class AutomationController implements Initializable {

    Map keyMap = new HashMap();
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
        keysDiscovery();
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

    public void editAutomation(Automation automation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationForm.fxml"));
            automationView.getChildren().add(loader.load());
            AutomationFormController automationFormController = loader.getController();
            automationFormController.setAutomationForm(automation);
            automationFormController.setAutomationConroller(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void execAutomation(String button) throws Exception {
        for (Node node : automationBox.getChildren()) {
            Automation automation = ((Automation) node.getProperties().get(automationObjKey));
            if (automation.getButton().contains(button)) {
                switch (automation.getType()) {
                    case executable:
                        Runtime.getRuntime().exec("cmd /c start \"\" \"" + automation.getPath() + "\"");
                        break;
                    case keyCombination:
                        execKeyCombination(automation.getKeyCombination());
                        break;
                    case mouseEvents:
                        execMouseEvents(automation.getKeyCombination());
                        break;
                }
            }
        }
    }

    private void execKeyCombination(List<String> keys) {
        try {
            Robot robot = new Robot();

            for (String key : keys) {
                System.out.println("Presing " + key);
                robot.keyPress((int) keyMap.get(key.toUpperCase()));
            }

            for (String key : keys) {
                System.out.println("Releasing " + key);
                robot.keyRelease((int) keyMap.get(key.toUpperCase()));
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void execMouseEvents(List<String> events) {
        try {
            Robot robot = new Robot();
            for (String event : events) {
                if (event.toLowerCase().equals("reset")) {
                    robot.mouseMove(0, 0);
                } else if (event.toLowerCase().contains("move")) {
                    String[] data = event.split(":");
                    Point location = MouseInfo.getPointerInfo().getLocation();
                    robot.mouseMove(location.x + Integer.parseInt(data[1]), location.y + Integer.parseInt(data[2]));
                } else if (event.toLowerCase().contains("click")) {
                    int button;
                    switch (event.split(":")[1]) {
                        case "1":
                            button = MouseEvent.BUTTON1_DOWN_MASK;
                            break;
                        case "2":
                            button = MouseEvent.BUTTON2_DOWN_MASK;
                            break;
                        case "3":
                            button = MouseEvent.BUTTON3_DOWN_MASK;
                            break;
                        default:
                            button = InputEvent.BUTTON1_DOWN_MASK;
                    }
                    robot.mousePress(button);
                    robot.delay(30);
                    robot.mouseRelease(button);
                }
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void keysDiscovery() {
        System.out.println("\n\n\nKEYBOARD\n");
        for (int i = 0; i < 1000000; ++i) {
            String text = java.awt.event.KeyEvent.getKeyText(i);
            if (!text.contains("Unknown keyCode: ")) {
                System.out.println("" + i + " -- " + text);
                keyMap.put(text.toUpperCase(), i);
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

    public void editAutomationCard(Automation automation) {
        try {
            PersistanceService.updateAutomation(automation);
            automationView.getChildren().remove(automationView.getChildren().size() - 1);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paolodoom/sppcontroller/views/automation/AutomationCard.fxml"));
            Node autoCard = loader.load();
            autoCard.getProperties().put(automationObjKey, automation);
            int index = -1;
            for (int i = automationBox.getChildren().size() - 1; i >= 0; i--) {
                if (((Automation) automationBox.getChildren().get(i).getProperties().get(automationObjKey)).getId() == automation.getId()) {
                    index = i;
                }
            }
            automationBox.getChildren().remove(index);
            automationBox.getChildren().add(index, autoCard);
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
