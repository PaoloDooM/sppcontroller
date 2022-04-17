/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.paolodoom.sppcontroller.controllers.automation.AutomationController;
import com.paolodoom.sppcontroller.models.ConnectionState;
import com.paolodoom.sppcontroller.services.SppService;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
    private TextArea debugLog;
    @FXML
    private Button connectionButton;
    @FXML
    private Button refreshButton;

    SppService spp = new SppService();
    List<SerialPort> sps = Collections.emptyList();
    SerialPort receivePort, sendPort;
    Task readTask, writeTask, connectionTask;
    AutomationController automationController;
    ConnectionState connectionButtonState = ConnectionState.disconnected;

    public void setAutomationController(AutomationController automationController) {
        this.automationController = automationController;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setStateConnectionButton(connectionButtonState);
        retrievePorts();
    }

    @FXML
    private void connection(ActionEvent event) {
        switch (connectionButtonState) {
            case disconnected:
                setStateConnectionButton(ConnectionState.connecting);
                createConnectionTask();
                new Thread(connectionTask).start();
                break;
            case connected:
                disconnect();
                break;
        }
    }

    public void disconnect() {
        if (connectionTask != null) {
            if (connectionTask.isRunning()) {
                connectionTask.cancel(true);
            }
            connectionTask = null;
        }
        if (readTask != null) {
            if (readTask.isRunning()) {
                readTask.cancel(true);
            }
            readTask = null;
        }
        spp.disconnect(receivePort, sendPort);
        debugLog.clear();
        setStateConnectionButton(ConnectionState.disconnected);
    }

    @FXML
    private void refresh(ActionEvent event) {
        retrievePorts();
    }

    public void retrievePorts() {
        sps = spp.getPorts();
        receiveDrop.getItems().setAll(Collections.emptyList());
        sendDrop.getItems().setAll(Collections.emptyList());
        sps.forEach(sp -> {
            MenuItem receiveItem = new MenuItem(sp.getSystemPortName());
            MenuItem sendItem = new MenuItem(sp.getSystemPortName());
            receiveItem.setOnAction(this::setReceivePort);
            sendItem.setOnAction(this::setSendPort);
            receiveDrop.getItems().add(receiveItem);
            sendDrop.getItems().add(sendItem);
        });
    }

    private void setReceivePort(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        sps.forEach(sp -> {
            if (source.getText().equals(sp.getSystemPortName())) {
                receivePort = sp;
                receiveDrop.setText(source.getText());
            }
        });
    }

    private void setSendPort(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        sps.forEach(sp -> {
            if (source.getText().equals(sp.getSystemPortName())) {
                sendPort = sp;
                sendDrop.setText(source.getText());
            }
        });
    }

    private void setStateConnectionButton(ConnectionState state) {
        switch (state) {
            case connecting:
                sendDrop.setDisable(true);
                receiveDrop.setDisable(true);
                connectionButton.setDisable(true);
                connectionButton.setText("Connecting");
                connectionButton.setStyle("-fx-background-color: grey;");
                break;
            case connected:
                sendDrop.setDisable(true);
                receiveDrop.setDisable(true);
                connectionButton.setDisable(false);
                connectionButton.setText("Disconnect");
                connectionButton.setStyle("-fx-background-color: red;");
                break;
            case disconnected:
                sendDrop.setDisable(false);
                receiveDrop.setDisable(false);
                connectionButton.setDisable(false);
                connectionButton.setText("Connect");
                connectionButton.setStyle("-fx-background-color: green;");
                break;
        }
        connectionButtonState = state;
    }

    public void createReadTask() {
        if (readTask == null) {
            readTask = new Task<Void>() {
                @Override
                public Void call() {
                    while (true) {
                        if (!isCancelled()) {
                            try {
                                String readedString = spp.read(receivePort);
                                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - " + readedString + (readedString.contains("\n") ? "" : "\n"));
                                automationController.execAutomation(readedString);
                            } catch (IOException ex) {
                                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - " + ex.toString() + "\n");
                            }
                        }
                    }
                }
            };
            readTask.setOnFailed(e -> {
                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - Read failed\n");
            });
        }
    }

    public void createConnectionTask() {
        if (connectionTask == null) {
            connectionTask = new Task<Void>() {
                @Override
                public Void call() {
                    if (!isCancelled()) {
                        debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - Connecting TX: " + sendPort.getSystemPortName() + " RX: " + receivePort.getSystemPortName() + "\n");
                        spp.connect(receivePort, sendPort);
                        debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - Connected TX: " + sendPort.getSystemPortName() + " RX: " + receivePort.getSystemPortName() + "\n");
                    }
                    return null;
                }
            };
            connectionTask.setOnFailed(e -> {
                connectionTask.cancel(true);
                connectionTask = null;
                setStateConnectionButton(ConnectionState.disconnected);
                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - Connection failed\n");
            });
            connectionTask.setOnSucceeded(e -> {
                connectionTask.cancel(true);
                connectionTask = null;
                setStateConnectionButton(ConnectionState.connected);
                createReadTask();
                new Thread(readTask).start();
            });
        }
    }
}
