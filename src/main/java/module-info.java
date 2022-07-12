open module com.paolodoom.sppcontroller {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires jSensors;
    requires bluecove;
    requires jbidibc.core;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.web;
    requires spring.security.core;
    requires spring.context;
    requires jjwt;
    requires org.apache.tomcat.embed.core;
    requires spring.security.config;
    requires spring.security.crypto;
    requires spring.security.web;
    requires spring.messaging;
    requires spring.websocket;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires spring.core;
    requires java.base;
    requires java.sql.rowset;
    requires java.desktop;

    exports com.paolodoom.sppcontroller;
    
    exports com.paolodoom.sppcontroller.controllers;
    
    exports com.paolodoom.sppcontroller.controllers.automation;
    
    exports com.paolodoom.sppcontroller.controllers.connection;
    
    exports com.paolodoom.sppcontroller.configurations;
}
