/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Disk;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author PaoloDooM
 */
public class ScreenController implements Initializable {

    Task sensorsTask;
    ConnectionController connectionController;

    @FXML
    private Text sensorsDisplay;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createSensorsTask();
        new Thread(sensorsTask).start();
    }

    public void periodicRead() {
        Components components = JSensors.get.components();
        sensorsDisplay.setText("");
        List<Cpu> cpus = components.cpus;
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                System.out.println("Found CPU component: " + cpu.name);
                sensorsDisplay.setText(sensorsDisplay.getText() + cpu.name + "\n");

                if (cpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        sensorsDisplay.setText(sensorsDisplay.getText() + "   " + temp.name + ": " + temp.value + " C" + "\n");
                    }

                    //Print fan speed
                    List<Fan> fans = cpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                        sensorsDisplay.setText(sensorsDisplay.getText() + "   " + fan.name + ": " + fan.value + " RPM" + "\n");
                    }

                    List<Load> loads = cpu.sensors.loads;
                    for (final Load load : loads) {
                        System.out.println(load.name + ": " + load.value + " %");
                        sensorsDisplay.setText(sensorsDisplay.getText() + "   " + load.name + ": " + load.value + " %" + "\n");
                    }
                }
            }
        }

        List<Gpu> gpus = components.gpus;
        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                System.out.println("Found GPU component: " + gpu.name);
                sensorsDisplay.setText(sensorsDisplay.getText() + gpu.name + "\n");

                if (gpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = gpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        sensorsDisplay.setText(sensorsDisplay.getText() + "   " + temp.name + ": " + temp.value + " C" + "\n");
                    }

                    //Print fan speed
                    List<Fan> fans = gpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                        sensorsDisplay.setText(sensorsDisplay.getText() + "   " + fan.name + ": " + fan.value + " RPM" + "\n");
                    }

                    List<Load> loads = gpu.sensors.loads;
                    for (final Load load : loads) {
                        System.out.println(load.name + ": " + load.value + " %");
                        sensorsDisplay.setText(sensorsDisplay.getText() + "   " + load.name + ": " + load.value + " %" + "\n");
                    }
                }
            }

            List<Disk> disks = components.disks;
            if (disks != null) {
                int i = 0;
                for (final Disk disk : disks) {
                    System.out.println("Found Disk component: " + disk.name);
                    sensorsDisplay.setText(sensorsDisplay.getText() + disk.name + " " + i +"\n");

                    if (disk.sensors != null) {
                        System.out.println("Sensors: ");

                        //Print temperatures
                        List<Temperature> temps = disk.sensors.temperatures;
                        for (final Temperature temp : temps) {
                            System.out.println(temp.name + ": " + temp.value + " C");
                            sensorsDisplay.setText(sensorsDisplay.getText() + "   " + temp.name + ": " + temp.value + " C" + "\n");
                        }

                        //Print fan speed
                        List<Fan> fans = disk.sensors.fans;
                        for (final Fan fan : fans) {
                            System.out.println(fan.name + ": " + fan.value + " RPM");
                            sensorsDisplay.setText(sensorsDisplay.getText() + "   " + fan.name + ": " + fan.value + " RPM" + "\n");
                        }

                        List<Load> loads = disk.sensors.loads;
                        for (final Load load : loads) {
                            System.out.println(load.name + ": " + load.value + " %");
                            sensorsDisplay.setText(sensorsDisplay.getText() + "   " + load.name + ": " + load.value + " %" + "\n");
                        }
                        i++;
                    }
                }
            }
        }
        System.out.println("------------------------------------------------------------");
    }

    public void createSensorsTask() {
        if (sensorsTask == null) {
            sensorsTask = new Task<Void>() {
                @Override
                public Void call() {
                    while (!isCancelled()) {
                        try {
                            periodicRead();
                            //TODO: maybe timer
                            Thread. sleep(200);
                        } catch (Exception ex) {
                            sensorsDisplay.setText("Error:\n" + ex.toString());
                        }
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
}
