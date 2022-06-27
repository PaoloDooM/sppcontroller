/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paolodoom.sppcontroller.configurations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author PaoloDooM
 */
@Component
public class AppConfiguration {
    
    @Value("${dev.mode}")
    private boolean devMode;
    
    @Autowired
    public AppConfiguration() {
    }

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }
}
