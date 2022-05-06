/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.paolodoom.sppcontroller.controllers.connection.ConnectionController;
import com.paolodoom.sppcontroller.models.CustomTreeMap;
import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Disk;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.components.Mobo;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class ScreenController implements Initializable {

    Task sensorsTask;
    ConnectionController connectionController;
    Map<String, Double> sensorsData;
    boolean firstIteration;

    @FXML
    private Text sensorsDisplay;
    @FXML
    private TreeView<Map> treeView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createSensorsTask();
        new Thread(sensorsTask).start();
    }

    public Components periodicRead() {
        Components components = JSensors.get.components();
        sensorsData = new HashMap<>();
        List<Cpu> cpus = components.cpus;
        if (cpus != null) {
            for (int i = 0; i < cpus.size(); i++) {
                System.out.println("Found CPU component: " + cpus.get(i).name);

                if (cpus.get(i).sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = cpus.get(i).sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                    }

                    //Print fan speed
                    List<Fan> fans = cpus.get(i).sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }

                    List<Load> loads = cpus.get(i).sensors.loads;
                    for (final Load load : loads) {
                        System.out.println(load.name + ": " + load.value + " %");
                        if (load.name.contains("CPU Total")) {
                            sensorsData.put(load.name + "$" + i, load.value);
                        } else if (load.name.contains("Memory")) {
                            sensorsData.put(load.name, load.value);
                        }
                    }
                }
            }
        }

        List<Gpu> gpus = components.gpus;
        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                System.out.println("Found GPU component: " + gpu.name);

                if (gpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = gpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        if (temp.name.contains("GPU Core")) {
                            sensorsData.put(temp.name, temp.value);
                        }
                    }

                    //Print fan speed
                    List<Fan> fans = gpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }

                    List<Load> loads = gpu.sensors.loads;
                    for (final Load load : loads) {
                        System.out.println(load.name + ": " + load.value + " %");
                        if (load.name.contains("GPU Core")) {
                            sensorsData.put(load.name, load.value);
                        } else if (load.name.contains("Controller")) {
                            sensorsData.put(load.name, load.value);
                        } else if (load.name.contains("Memory")) {
                            sensorsData.put(load.name, load.value);
                        }
                    }
                }
            }
        }

        List<Disk> disks = components.disks;
        if (disks != null) {
            for (int i = 0; i < disks.size(); i++) {
                System.out.println("Found Disk component: " + disks.get(i).name);

                if (disks.get(i).sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = disks.get(i).sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                    }

                    //Print fan speed
                    List<Fan> fans = disks.get(i).sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }

                    List<Load> loads = disks.get(i).sensors.loads;
                    for (final Load load : loads) {
                        System.out.println(load.name + ": " + load.value + " %");
                    }
                }
            }
        }
        List<Mobo> mobos = components.mobos;
        if (mobos != null) {
            for (final Mobo mobo : mobos) {
                System.out.println("Found Mobo component: " + mobo.name);

                if (mobo.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = mobo.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                    }

                    //Print fan speed
                    List<Fan> fans = mobo.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }

                    List<Load> loads = mobo.sensors.loads;
                    for (final Load load : loads) {
                        System.out.println(load.name + ": " + load.value + " %");
                    }
                }
            }
        }
        System.out.println("------------------------------------------------------------");
        List<String> parsedData = dataParser(sensorsData);
        String dataString = "";
        for (String s : parsedData.subList(1, parsedData.size())) {
            dataString += s + "\n";
        }
        sensorsDisplay.setText(dataString);
        dataToLcd(parsedData);
        return components;
    }

    public void createSensorsTask() {
        firstIteration = true;
        if (sensorsTask == null) {
            sensorsTask = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    while (!isCancelled()) {
                        Components components = periodicRead();
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (firstIteration) {
                                        initializeviewTree(components);
                                        firstIteration = false;
                                    }
                                } finally {
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        Thread.sleep(1000);
                    }
                    return null;
                }
            };
            sensorsTask.setOnFailed(e -> {
                sensorsDisplay.setText("Error:\n" + e.toString());
            });
            sensorsTask.setOnCancelled(e -> {
                System.out.println("sensorsTasks canceled!");
            });
        }
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void stopSensorsTask() {
        if (sensorsTask != null) {
            if (sensorsTask.isRunning()) {
                sensorsTask.cancel(true);
            }
            sensorsTask = null;
        }
        sensorsDisplay.setText("Stopped");
    }

    public void initializeviewTree(Components components) {
        List<Cpu> cpus = components.cpus;
        List<Gpu> gpus = components.gpus;
        List<Disk> disks = components.disks;
        List<Mobo> mobos = components.mobos;

        TreeItem<Map> rootTree = new TreeItem<>(new CustomTreeMap("Sensors", Arrays.asList(0), false));

        if (cpus != null) {
            TreeItem<Map> deviceTree = new TreeItem<>(new CustomTreeMap("Cpus", Arrays.asList(0, 0), false));
            for (int i = 0; i < cpus.size(); i++) {
                TreeItem<Map> cpuTree = new TreeItem<>(new CustomTreeMap(cpus.get(i).name, Arrays.asList(0, 0, i), false));
                if (cpus.get(i).sensors != null) {
                    TreeItem<Map> loadTree = new TreeItem<>(new CustomTreeMap("Load", Arrays.asList(0, 0, i, 0), false));
                    for (int j = 0; j < cpus.get(i).sensors.loads.size(); j++) {
                        loadTree.getChildren().add(new TreeItem<>(new CustomTreeMap(cpus.get(i).sensors.loads.get(j).name, Arrays.asList(0, 0, i, 0, j), true)));
                    }
                    cpuTree.getChildren().add(loadTree);
                    TreeItem<Map> tempTree = new TreeItem<>(new CustomTreeMap("Temp", Arrays.asList(0, 0, i, 1), false));
                    for (int j = 0; j < cpus.get(i).sensors.temperatures.size(); j++) {
                        tempTree.getChildren().add(new TreeItem<>(new CustomTreeMap(cpus.get(i).sensors.temperatures.get(j).name, Arrays.asList(0, 0, i, 1, j), true)));
                    }
                    cpuTree.getChildren().add(tempTree);
                    TreeItem<Map> fanTree = new TreeItem<>(new CustomTreeMap("Fan", Arrays.asList(0, 0, i, 2), false));
                    for (int j = 0; j < cpus.get(i).sensors.fans.size(); j++) {
                        fanTree.getChildren().add(new TreeItem<>(new CustomTreeMap(cpus.get(i).sensors.fans.get(j).name, Arrays.asList(0, 0, i, 2, j), true)));
                    }
                    cpuTree.getChildren().add(fanTree);
                }
                deviceTree.getChildren().add(cpuTree);
            }
            rootTree.getChildren().add(deviceTree);
        }
        if (gpus != null) {
            TreeItem<Map> deviceTree = new TreeItem<>(new CustomTreeMap("Gpus", Arrays.asList(0, 1), false));
            for (int i = 0; i < gpus.size(); i++) {
                TreeItem<Map> gpuTree = new TreeItem<>(new CustomTreeMap(gpus.get(i).name, Arrays.asList(0, 1, i), false));
                if (gpus.get(i).sensors != null) {
                    TreeItem<Map> loadTree = new TreeItem<>(new CustomTreeMap("Load", Arrays.asList(0, 1, i, 0), false));
                    for (int j = 0; j < gpus.get(i).sensors.loads.size(); j++) {
                        loadTree.getChildren().add(new TreeItem<>(new CustomTreeMap(gpus.get(i).sensors.loads.get(j).name, Arrays.asList(0, 1, i, 0, j), true)));
                    }
                    gpuTree.getChildren().add(loadTree);
                    TreeItem<Map> tempTree = new TreeItem<>(new CustomTreeMap("Temp", Arrays.asList(0, 1, i, 1), false));
                    for (int j = 0; j < gpus.get(i).sensors.temperatures.size(); j++) {
                        tempTree.getChildren().add(new TreeItem<>(new CustomTreeMap(gpus.get(i).sensors.temperatures.get(j).name, Arrays.asList(0, 1, i, 1, j), true)));
                    }
                    gpuTree.getChildren().add(tempTree);
                    TreeItem<Map> fanTree = new TreeItem<>(new CustomTreeMap("Fan", Arrays.asList(0, 1, i, 2), false));
                    for (int j = 0; j < gpus.get(i).sensors.fans.size(); j++) {
                        fanTree.getChildren().add(new TreeItem<>(new CustomTreeMap(gpus.get(i).sensors.fans.get(j).name, Arrays.asList(0, 1, i, 2, j), true)));
                    }
                    gpuTree.getChildren().add(fanTree);
                }
                deviceTree.getChildren().add(gpuTree);
            }
            rootTree.getChildren().add(deviceTree);
        }
        if (disks != null) {
            TreeItem<Map> deviceTree = new TreeItem<>(new CustomTreeMap("Disks", Arrays.asList(0, 2), false));
            for (int i = 0; i < disks.size(); i++) {
                TreeItem<Map> diskTree = new TreeItem<>(new CustomTreeMap(disks.get(i).name + " " + i, Arrays.asList(0, 2, i), false));
                if (disks.get(i).sensors != null) {
                    TreeItem<Map> loadTree = new TreeItem<>(new CustomTreeMap("Load", Arrays.asList(0, 2, i, 0), false));
                    for (int j = 0; j < disks.get(i).sensors.loads.size(); j++) {
                        loadTree.getChildren().add(new TreeItem<>(new CustomTreeMap(disks.get(i).sensors.loads.get(j).name, Arrays.asList(0, 2, i, 0, j), true)));
                    }
                    diskTree.getChildren().add(loadTree);
                    TreeItem<Map> tempTree = new TreeItem<>(new CustomTreeMap("Temp", Arrays.asList(0, 2, i, 1), false));
                    for (int j = 0; j < disks.get(i).sensors.temperatures.size(); j++) {
                        tempTree.getChildren().add(new TreeItem<>(new CustomTreeMap(disks.get(i).sensors.temperatures.get(j).name, Arrays.asList(0, 2, i, 1, j), true)));
                    }
                    diskTree.getChildren().add(tempTree);
                    TreeItem<Map> fanTree = new TreeItem<>(new CustomTreeMap("Fan", Arrays.asList(0, 2, i, 2), false));
                    for (int j = 0; j < disks.get(i).sensors.fans.size(); j++) {
                        fanTree.getChildren().add(new TreeItem<>(new CustomTreeMap(disks.get(i).sensors.fans.get(j).name, Arrays.asList(0, 2, i, 2, j), true)));
                    }
                    diskTree.getChildren().add(fanTree);
                }
                deviceTree.getChildren().add(diskTree);
            }
            rootTree.getChildren().add(deviceTree);
        }
        if (mobos != null) {
            TreeItem<Map> deviceTree = new TreeItem<>(new CustomTreeMap("Mobo", Arrays.asList(0, 3), false));
            for (int i = 0; i < mobos.size(); i++) {
                TreeItem<Map> moboTree = new TreeItem<>(new CustomTreeMap(mobos.get(i).name == null ? "Generic Motherboard" : mobos.get(i).name, Arrays.asList(0, 3, i), false));
                if (mobos.get(i).sensors != null) {
                    TreeItem<Map> loadTree = new TreeItem<>(new CustomTreeMap("Load", Arrays.asList(0, 3, i, 0), false));
                    for (int j = 0; j < mobos.get(i).sensors.loads.size(); j++) {
                        loadTree.getChildren().add(new TreeItem<>(new CustomTreeMap(mobos.get(i).sensors.loads.get(j).name, Arrays.asList(0, 3, i, 0, j), true)));
                    }
                    moboTree.getChildren().add(loadTree);
                    TreeItem<Map> tempTree = new TreeItem<>(new CustomTreeMap("Temp", Arrays.asList(0, 3, i, 1), false));
                    for (int j = 0; j < mobos.get(i).sensors.temperatures.size(); j++) {
                        tempTree.getChildren().add(new TreeItem<>(new CustomTreeMap(mobos.get(i).sensors.temperatures.get(j).name, Arrays.asList(0, 3, i, 1, j), true)));
                    }
                    moboTree.getChildren().add(tempTree);
                    TreeItem<Map> fanTree = new TreeItem<>(new CustomTreeMap("Fan", Arrays.asList(0, 3, i, 2), false));
                    for (int j = 0; j < mobos.get(i).sensors.fans.size(); j++) {
                        fanTree.getChildren().add(new TreeItem<>(new CustomTreeMap(mobos.get(i).sensors.fans.get(j).name, Arrays.asList(0, 3, i, 2, j), true)));
                    }
                    moboTree.getChildren().add(fanTree);
                }
                deviceTree.getChildren().add(moboTree);
            }
            rootTree.getChildren().add(deviceTree);
        }
        //rootTree.setExpanded(true);
        treeView.setRoot(rootTree);
        //treeView.selectionModelProperty().get().selectFirst();
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSelectedItem(newValue));
    }

    private void updateSelectedItem(TreeItem<Map> newValue) {
        try {
            System.out.println("toggled: " + newValue.getValue().get("position"));
            System.out.println("interactive: " + newValue.getValue().get("interactive"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<String> dataParser(Map<String, Double> data) {
        final String placeHolder = "${CPULOAD}";
        List<String> strings = new ArrayList<>();
        strings.add("$cl$");
        strings.add("Processor");
        Double cpuLoadTotal = 0.0;
        int divider = 0;
        for (String k : data.keySet()) {
            if (k.contains("Load CPU Total")) {
                cpuLoadTotal += data.get(k);
                divider++;
                if (!strings.contains(placeHolder)) {
                    strings.add(placeHolder);
                }
            } else if (k.contains("Load Memory")) {
                strings.add("   RAM: " + String.format("%.2f", data.get(k)) + "%");
            } else if (k.contains("Temp GPU Core")) {
                strings.add("Graphic");
                strings.add("   Temp: " + String.format("%.2f", data.get(k)) + "C");
            } else if (k.contains("Load GPU Core")) {
                strings.add("   GPU: " + String.format("%.2f", data.get(k)) + "%");
            } else if (k.contains("Load GPU Memory Controller")) {
                //strings.add("   memCtrl: " + String.format("%.2f", data.get(k)) + "%");
            } else if (k.contains("Load GPU Memory")) {
                strings.add("   VRAM: " + String.format("%.2f", data.get(k)) + "%");
            }
        }
        strings.set(strings.indexOf(placeHolder), "   CPU: " + String.format("%.2f", cpuLoadTotal / divider) + "%");
        return strings;
    }

    private void dataToLcd(List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            connectionController.writeToLcd(data.get(i));
            if (i != 0 || i != data.size() - 1) {
                connectionController.writeToLcd("\r\n");
            }
        }
    }
}
