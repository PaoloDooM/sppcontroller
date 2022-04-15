package com.paolodoom.sppcontroller;

import com.fazecast.jSerialComm.SerialPort;
import com.paolodoom.sppcontroller.controllers.HomeController;
import com.paolodoom.sppcontroller.services.SppService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static HomeController homeController = new HomeController();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/com/paolodoom/sppcontroller/views/HomeView"), 600, 450);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws Exception {
        /*SppService spp = new SppService();
        List<SerialPort> sps = spp.getPorts();
        sps.forEach(sp -> {
            System.out.println(sp.getDescriptivePortName() + "\n"
                    + sp.getPortDescription() + "\n"
                    + sp.getSystemPortName() + "\n" //COM#
                    + sp.getPortLocation() + "\n"
                    + sp.getPortLocation() + "\n-");
        });
        SerialPort readPort = null, writePort = null;
        for (SerialPort sp : sps) {
            switch (sp.getSystemPortName()) {
                case "COM9":
                    readPort = sp;
                    break;
                case "COM8":
                    writePort = sp;
                    break;
            }
        }
        if (readPort == null || writePort == null) {
            throw new Exception("Ports not found!");
        }
        spp.connect(readPort, writePort);
        spp.read(readPort);*/
        launch();
    }

}
