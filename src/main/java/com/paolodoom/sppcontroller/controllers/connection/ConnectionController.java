/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers.connection;

import com.fazecast.jSerialComm.SerialPort;
import com.paolodoom.sppcontroller.controllers.ScreenController;
import com.paolodoom.sppcontroller.controllers.automation.AutomationController;
import com.paolodoom.sppcontroller.models.ConnType;
import com.paolodoom.sppcontroller.models.ConnectionState;
import com.paolodoom.sppcontroller.services.SppService;
import com.paolodoom.sppcontroller.services.BTService;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javax.bluetooth.RemoteDevice;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class ConnectionController implements Initializable {

    @FXML
    private TextArea debugLog;
    @FXML
    private Button connectionButton;
    @FXML
    private Button refreshButton;

    SppService spp = new SppService();
    BTService bt = new BTService();
    List<SerialPort> sps = Collections.emptyList();
    List<RemoteDevice> devices = Collections.emptyList();
    RemoteDevice selectedDevice;
    SerialPort receivePort, sendPort;
    Task readTask, connectionTask;
    AutomationController automationController;
    ScreenController screenController;
    ConnectionState connectionButtonState = ConnectionState.disconnected;
    ConnType selectedConnType = ConnType.bluetooth;
    BtConnController btCtrl;
    SerialConnController serialCtrl;
    AnchorPane btConnView;
    AnchorPane serialConnView;
    String readedString = "";

    @FXML
    private MenuButton connectionTypeMenu;
    @FXML
    private AnchorPane connTypeAnchor;

    public void setAutomationController(AutomationController automationController) {
        this.automationController = automationController;
    }

    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Arrays.asList(ConnType.values()).forEach(t -> {
                MenuItem typeItem = new MenuItem(t.toString());
                typeItem.setOnAction(this::setConnType);
                connectionTypeMenu.getItems().add(typeItem);
            });
            connectionTypeMenu.setText(ConnType.bluetooth.toString());

            loadConnCtrlView();
            switchConnCtrlView();

            setStateConnectionButton(connectionButtonState);
            retrievePorts();
            retrieveDevices();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setConnType(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        Arrays.asList(ConnType.values()).forEach(t -> {
            if (source.getText().equals(t.toString())) {
                selectedConnType = t;
                connectionTypeMenu.setText(source.getText());
                switchConnCtrlView();
            }
        });
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
        if (readTask != null) {
            if (readTask.isRunning()) {
                readTask.cancel(true);
            }
            readTask = null;
        }
        if (screenController != null) {
            screenController.stopSensorsTask();
        }
        if (connectionTask != null) {
            if (connectionTask.isRunning()) {
                connectionTask.cancel(true);
            }
            connectionTask = null;
        }

        switch (selectedConnType) {
            case bluetooth:
                try {
                bt.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            break;
            case serial:
                spp.disconnect(receivePort, sendPort);
                break;
        }

        debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Disconnected\n");
        //debugLog.clear();
        setStateConnectionButton(ConnectionState.disconnected);
    }

    @FXML
    private void refresh(ActionEvent event) {
        switch (selectedConnType) {
            case bluetooth:
                try {
                retrieveDevices();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            break;

            case serial:
                retrievePorts();
                break;
        }
    }

    public void retrievePorts() {
        sps = spp.getPorts();
        serialCtrl.getReceiveDrop().getItems().setAll(Collections.emptyList());
        serialCtrl.getSendDrop().getItems().setAll(Collections.emptyList());
        sps.forEach(sp -> {
            MenuItem receiveItem = new MenuItem(sp.getSystemPortName());
            MenuItem sendItem = new MenuItem(sp.getSystemPortName());
            receiveItem.setOnAction(this::setReceivePort);
            sendItem.setOnAction(this::setSendPort);
            serialCtrl.getReceiveDrop().getItems().add(receiveItem);
            serialCtrl.getSendDrop().getItems().add(sendItem);
        });
    }

    public void retrieveDevices() throws Exception {
        devices = bt.getDevices();
        btCtrl.getDevicesDrop().getItems().setAll(Collections.emptyList());
        devices.forEach(d -> {
            String name = "Error!";
            try {
                name = d.getFriendlyName(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            MenuItem deviceItem = new MenuItem(name);
            deviceItem.setOnAction(this::setDevice);
            btCtrl.getDevicesDrop().getItems().add(deviceItem);
        });
    }

    private void setDevice(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        devices.forEach(d -> {
            String name = "Error!";
            try {
                name = d.getFriendlyName(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (source.getText().equals(name)) {
                selectedDevice = d;
                btCtrl.getDevicesDrop().setText(source.getText());
            }
        });
    }

    private void setReceivePort(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        sps.forEach(sp -> {
            if (source.getText().equals(sp.getSystemPortName())) {
                receivePort = sp;
                serialCtrl.getReceiveDrop().setText(source.getText());
            }
        });
    }

    private void setSendPort(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        sps.forEach(sp -> {
            if (source.getText().equals(sp.getSystemPortName())) {
                sendPort = sp;
                serialCtrl.getSendDrop().setText(source.getText());
            }
        });
    }

    private void setStateConnectionButton(ConnectionState state) {
        switch (state) {
            case connecting:
                serialCtrl.getSendDrop().setDisable(true);
                serialCtrl.getReceiveDrop().setDisable(true);
                btCtrl.getDevicesDrop().setDisable(true);
                connectionTypeMenu.setDisable(true);
                connectionButton.setDisable(true);
                connectionButton.setText("Connecting");
                connectionButton.setStyle("-fx-background-color: grey;");
                break;
            case connected:
                serialCtrl.getSendDrop().setDisable(true);
                serialCtrl.getReceiveDrop().setDisable(true);
                btCtrl.getDevicesDrop().setDisable(true);
                connectionTypeMenu.setDisable(true);
                connectionButton.setDisable(false);
                connectionButton.setText("Disconnect");
                connectionButton.setStyle("-fx-background-color: red;");
                break;
            case disconnected:
                serialCtrl.getSendDrop().setDisable(false);
                serialCtrl.getReceiveDrop().setDisable(false);
                btCtrl.getDevicesDrop().setDisable(false);
                connectionTypeMenu.setDisable(false);
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
                public Void call() throws Exception {
                    while (true) {
                        //TODO: replace global var for local var
                        readedString = "Null";
                        if (!isCancelled()) {
                            switch (selectedConnType) {
                                case bluetooth:
                                    readedString = bt.read();
                                    break;
                                case serial:
                                    readedString = spp.read(receivePort);
                                    break;
                            }
                            final CountDownLatch latch = new CountDownLatch(1);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        try {
                                            debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - " + readedString + (readedString.contains("\n") ? "" : "\n"));
                                            automationController.execAutomation(readedString);
                                        } catch (IOException ex) {
                                            debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Read " + ex.toString() + "\n");
                                        }
                                    } finally {
                                        latch.countDown();
                                    }
                                }
                            });
                            latch.await();
                        }
                    }
                }
            };
            readTask.setOnFailed(e -> {
                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Read failed\n");
            });
        }
    }

    public void createConnectionTask() {
        if (connectionTask == null) {
            try {
                switch (selectedConnType) {
                    case bluetooth:
                        debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Connecting: " + selectedDevice.getFriendlyName(false) + "\n");
                        break;
                    case serial:
                        debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Connecting TX: " + sendPort.getSystemPortName() + " RX: " + receivePort.getSystemPortName() + "\n");
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            connectionTask = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    if (!isCancelled()) {
                        switch (selectedConnType) {
                            case bluetooth:
                                try {
                                bt.connect(selectedDevice);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                            case serial:
                                spp.connect(receivePort, sendPort);
                                break;
                        }
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    switch (selectedConnType) {
                                        case bluetooth:
                                            debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Connected: " + selectedDevice.getFriendlyName(false) + "\n");
                                            break;
                                        case serial:
                                            debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Connected TX: " + sendPort.getSystemPortName() + " RX: " + receivePort.getSystemPortName() + "\n");
                                            break;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                } finally {
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                    }
                    return null;
                }
            };
            connectionTask.setOnFailed(e -> {
                connectionTask.cancel(true);
                connectionTask = null;
                setStateConnectionButton(ConnectionState.disconnected);
                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Connection failed\n");
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

    public void writeToLcd(String data) {
        try {
            if (ConnectionState.connected == connectionButtonState) {
                switch (selectedConnType) {
                    case bluetooth:
                        bt.write(data);
                        break;
                    case serial:
                        spp.write(sendPort, data);
                        break;
                }
                debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Write " + data + "\n");
            }
        } catch (Exception e) {
            debugLog.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - Write failed\n");
        }
    }

    public void loadConnCtrlView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ConnType.bluetooth.getView()));
        btConnView = loader.load();
        btCtrl = loader.getController();

        loader = new FXMLLoader(getClass().getResource(ConnType.serial.getView()));
        serialConnView = loader.load();
        serialCtrl = loader.getController();

        connTypeAnchor.getChildren().add(btConnView);
    }

    public void switchConnCtrlView() {
        connTypeAnchor.getChildren().remove(0);
        switch (selectedConnType) {
            case bluetooth:
                connTypeAnchor.getChildren().add(btConnView);
                break;
            case serial:
                connTypeAnchor.getChildren().add(serialConnView);
                break;
        }
    }
}
