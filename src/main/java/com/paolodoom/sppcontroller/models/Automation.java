/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.models;

/**
 *
 * @author PaoloDooM
 */
public class Automation {
    final AutomationType type;
    final String[] keyCombination;
    final String path;
    final String button;

    public Automation(AutomationType type, String[] keyCombination, String button) {
        this.type = type;
        this.keyCombination = keyCombination;
        this.button = button;
        this.path = null;
    }
    
    public Automation(AutomationType type, String path, String button) {
        this.type = type;
        this.path = path;
        this.button = button;
        this.keyCombination = null;
    }
}
