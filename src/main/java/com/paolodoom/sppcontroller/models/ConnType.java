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
public enum ConnType {
    bluetooth {
        @Override
        public String toString() {
            return "Bluetooth";
        }
    },
    serial {
        @Override
        public String toString() {
            return "Serial";
        }
    };

    public String getView() {
        switch (this) {
            case bluetooth:
                return "/com/paolodoom/sppcontroller/views/connection/btConnView.fxml";
            case serial:
                return "/com/paolodoom/sppcontroller/views/connection/serialConnView.fxml";
            default:
                return null;
        }
    }
}
