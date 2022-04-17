/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.models;

import com.paolodoom.sppcontroller.models.AutomationType;

/**
 *
 * @author PaoloDooM
 */
public class Automation {
    AutomationType type;
    String[] keyCombination;
    String path;
    String button;

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

    public AutomationType getType() {
        return type;
    }

    public String[] getKeyCombination() {
        return keyCombination;
    }

    public String getPath() {
        return path;
    }

    public String getButton() {
        return button;
    }

    public void setType(AutomationType type) {
        this.type = type;
    }

    public void setKeyCombination(String[] keyCombination) {
        this.keyCombination = keyCombination;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setButton(String button) {
        this.button = button;
    }
}
