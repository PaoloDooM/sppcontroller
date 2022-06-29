/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.controllers;

import com.paolodoom.sppcontroller.models.ButtonModel;
import com.paolodoom.sppcontroller.services.ButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author PaoloDooM
 */

@RestController
@CrossOrigin
public class ButtonsController {
    private final ButtonService btnSrvc;
    
    @Autowired
    public ButtonsController(ButtonService btnSrvc){
        this.btnSrvc = btnSrvc;
    }

    @RequestMapping(value = "/buttons", method = RequestMethod.POST)
    public ResponseEntity<?> setButton(@RequestBody ButtonModel button) {

        System.out.println("Button pressed: " + button.getBtn());
        btnSrvc.setButton(button.getBtn());

        return ResponseEntity.ok("Acknowledge");
    }
}
