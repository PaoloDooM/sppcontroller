/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.models;

import com.paolodoom.sppcontroller.models.AutomationType;
import java.util.List;

/**
 *
 * @author PaoloDooM
 */
public class Automation {
    int id;
    AutomationType type;
    List<String> keyCombination;
    String path;
    String button;

    public Automation(AutomationType type, List<String> keyCombination, String button) {
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
    
    public Automation(int id, AutomationType type, List<String> keyCombination, String button) {
        this.id = id;
        this.type = type;
        this.keyCombination = keyCombination;
        this.button = button;
        this.path = null;
    }
    
    public Automation(int id, AutomationType type, String path, String button) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.button = button;
        this.keyCombination = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public AutomationType getType() {
        return type;
    }

    public List<String> getKeyCombination() {
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

    public void setKeyCombination(List<String> keyCombination) {
        this.keyCombination = keyCombination;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setButton(String button) {
        this.button = button;
    }
}
