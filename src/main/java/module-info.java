module com.paolodoom.sppcontroller {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires jSensors;
    requires bluecove;
    requires jbidibc.core;

    opens com.paolodoom.sppcontroller to javafx.fxml;
    exports com.paolodoom.sppcontroller;
    
    opens com.paolodoom.sppcontroller.controllers to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers;
    
    opens com.paolodoom.sppcontroller.controllers.automation to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers.automation;
    
    opens com.paolodoom.sppcontroller.controllers.connection to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers.connection;
}
