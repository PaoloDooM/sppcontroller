/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.models;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author PaoloDooM
 */
public class CustomTreeMap extends HashMap{
    
    public CustomTreeMap(String title, List position, boolean interactive){
        this.put("title", title);
        this.put("position", position);
        this.put("interactive", interactive);
    }
    
    @Override
    public String toString(){
        return this.get("title").toString();
    }
}
