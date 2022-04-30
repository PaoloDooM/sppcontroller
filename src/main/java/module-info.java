module com.paolodoom.sppcontroller {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires java.base;
    requires jSensors;
    requires bluecove;

    opens com.paolodoom.sppcontroller to javafx.fxml;
    exports com.paolodoom.sppcontroller;
    
    opens com.paolodoom.sppcontroller.controllers to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers;
    
    opens com.paolodoom.sppcontroller.controllers.automation to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers.automation;
}
