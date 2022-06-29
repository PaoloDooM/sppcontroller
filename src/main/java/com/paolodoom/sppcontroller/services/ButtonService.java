/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;

/**
 *
 * @author PaoloDooM
 */
public interface ButtonService {
    public void setButton(String btn);
    public String getButton();
    public int getButtonsLength();
    public void clear();
}
