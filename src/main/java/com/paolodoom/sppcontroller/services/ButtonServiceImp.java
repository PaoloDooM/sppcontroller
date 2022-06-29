/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author PaoloDooM
 */
@Service
public class ButtonServiceImp implements ButtonService {
    
    private final List<String> buttons = new LinkedList<String>(Arrays.asList());

    @Override
    public void setButton(String btn) {
        buttons.add(btn);
    }

    @Override
    public String getButton() {
        if(buttons.isEmpty()){
            return null;
        }
        return buttons.remove(buttons.size()-1);
    }
    
    @Override
    public int getButtonsLength(){
        return buttons.size();
    }
    
    @Override
    public void clear(){
        buttons.clear();
    }
    
}
