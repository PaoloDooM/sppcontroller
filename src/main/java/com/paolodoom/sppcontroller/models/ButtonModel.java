/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.models;

import java.io.Serializable;

/**
 *
 * @author PaoloDooM
 */
public class ButtonModel implements Serializable {

    private static final long serialVersionUID = 5426468931005150707L;

    private String btn;
    
    public ButtonModel() {
    
    }

    public ButtonModel(String btn) {
        this.btn = btn;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }
}
