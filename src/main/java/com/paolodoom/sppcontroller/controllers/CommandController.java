/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.paolodoom.sppcontroller.configurations.AppConfiguration;
import com.paolodoom.sppcontroller.models.CommandMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 *
 * @author PaoloDooM
 */

@Controller
public class CommandController {
    
    private final SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    CommandController(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/commands")
    @SendTo("/topic/response")
    public CommandMessage command(CommandMessage message, Principal principal){
        switch(message.getType()){
            case "button":
                System.out.println("Pressed: " + message.getData());
                return new CommandMessage(message.getUuid(), message.getType(), "", 200, "acknowledge");
            default:
                return new CommandMessage(message.getUuid(), message.getType(), "", 400, "unimplemented method");
        }
    }
}
