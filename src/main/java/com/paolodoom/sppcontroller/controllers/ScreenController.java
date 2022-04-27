/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
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
                if (cpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        sensorsDisplay.setText(sensorsDisplay.getText()+ temp.name + ": " + temp.value + " C" +"\n");
                    }

                    //Print fan speed
                    List<Fan> fans = cpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                        sensorsDisplay.setText(sensorsDisplay.getText()+ fan.name + ": " + fan.value + " RPM" +"\n");
                    }
                }
            }
        }
        List<Gpu> gpus = components.gpus;
        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                System.out.println("Found CPU component: " + gpu.name);
                if (gpu.sensors != null) {
                    System.out.println("Sensors: ");

                    //Print temperatures
                    List<Temperature> temps = gpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        sensorsDisplay.setText(sensorsDisplay.getText()+ temp.name + ": " + temp.value + " C" +"\n");
                    }

                    //Print fan speed
                    List<Fan> fans = gpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                        sensorsDisplay.setText(sensorsDisplay.getText()+ fan.name + ": " + fan.value + " RPM" +"\n");
                    }
                }
            }
        }
    }
    
    public void createSensorsTask() {
        if (sensorsTask == null) {
            sensorsTask = new Task<Void>() {
                @Override
                public Void call() {
                    while (true) {
                        if (!isCancelled()) {
                            try {
                                periodicRead();
                                Thread. sleep(1000);
                            } catch (InterruptedException ex) {
                                sensorsDisplay.setText("Error:\n" + ex.toString());
                            }
                        }
                    }
                }
            };
            sensorsTask.setOnFailed(e -> {
                sensorsDisplay.setText("Error:\n" + e.toString());
            });
        }
    }
        
    public void setConnectionController(ConnectionController connectionController){
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
