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
public enum AutomationType {
    keyCombination {
        @Override
        public String toString() {
            return "Key combination";
        }
    },
    executable {
        @Override
        public String toString() {
            return "Executable";
        }
    },
    mouseEvents {
        @Override
        public String toString() {
            return "Mouse Events";
        }
    }
}
