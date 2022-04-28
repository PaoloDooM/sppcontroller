package com.paolodoom.sppcontroller;

import com.paolodoom.sppcontroller.controllers.HomeController;
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
    private HomeController homeCtrl;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/paolodoom/sppcontroller/views/HomeView.fxml"));
        scene = new Scene(loader.load());
        homeCtrl = loader.getController();
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
    
    public static void iniApp(){
        launch();
    }

    @Override
    public void stop() {
        if(homeCtrl != null){
            homeCtrl.stopAll();
        }
    }
}
