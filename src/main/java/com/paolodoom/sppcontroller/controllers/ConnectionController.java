/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.paolodoom.sppcontroller.services.SppService;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Button refreshButton;

    SppService spp = new SppService();
    List<SerialPort> sps = Collections.emptyList();
    SerialPort receivePort, sendPort;
    Task task;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        retrievePorts();
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

    @FXML
    private void connect(ActionEvent event) {
        disconnect();
        spp.connect(receivePort, sendPort);
        createTask();
        new Thread(task).start();
    }

    @FXML
    private void disconnect(ActionEvent event) {
        disconnect();
    }

    @FXML
    private void refresh(ActionEvent event) {
        retrievePorts();
    }

    public void createTask() {
        if (task == null) {
            task = new Task<Void>() {
                @Override
                public Void call() {
                    while (true) {
                        if (!isCancelled()) {
                            debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - " + spp.read(receivePort));
                        }
                    }
                }
            };
        }
    }

    public void disconnect() {
        if (task != null) {
            if (task.isRunning()) {
                task.cancel(true);
            }
            spp.disconnect(receivePort, sendPort);
            task = null;
        }
        debugLog.clear();
    }
}
