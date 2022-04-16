module com.paolodoom.sppcontroller {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.paolodoom.sppcontroller to javafx.fxml;
    exports com.paolodoom.sppcontroller;
    requires com.fazecast.jSerialComm;
    requires java.base;
    
    opens com.paolodoom.sppcontroller.controllers to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers;
    
    opens com.paolodoom.sppcontroller.controllers.automation to javafx.fxml;
    exports com.paolodoom.sppcontroller.controllers.automation;
}
