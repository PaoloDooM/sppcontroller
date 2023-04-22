/*-----------------------------------------------------
Author:  PaoloDooM
Date: 2022-04-29
Description: sppcontroller
Board: PIC18F2550
-----------------------------------------------------*/

#include <fonts/Wendy3x5.h>
#define OLED_SSD1306
#define OLED_128X64
#define NB 6
#define BS 8

int btnsAD[NB] = {10, 7, 6, 15, 14, 13}, i;
BOOL btnsSt[NB] = {false,   false,   false,   false,  false,  false};
char* btnsCm[NB] = {"$00$","$01$","$02$","$03$", "$04$", "$05$"};
const u8 intf = OLED_I2C1;

void setup() {
    pinMode(USERLED, OUTPUT);
    for(i = 0; i < NB; i++){
        pinMode(btnsAD[i], INPUT);
    }

    Serial.begin(9600);
    
    OLED.init(intf, 0x78);
    OLED.setFont(intf, Wendy3x5);
    OLED.clearScreen(intf);
    OLED.print(intf, "DEVICE: HC-06\r\n\r\nPASSWORD: 1234");
    OLED.displayOn(intf);
}

void readBtns(){
    for(i = 0; i < NB; i++){
        if(digitalRead(btnsAD[i])!=btnsSt[i]){
            if(!btnsSt[i]){
                toggle(USERLED);
                Serial.printf(btnsCm[i]);
                Serial.flush();
            }
            btnsSt[i]=!btnsSt[i];
         }
    }
}

void writeLcd(char* data){
    if(strstr(data, "$cl$") != NULL){
         OLED.clearScreen(intf);
    }else{
         for(i = 0; i<BS; i++){
            OLED.printChar(intf, data[i]);
        }
    }
}

void readData(){
    if(Serial.available()){
       char data[BS];
        for(i = 0; i<BS; i++){
            data[i] = Serial.readChar();
        }
        writeLcd(data);
    }
    OLED.refresh(intf);
}

void loop() {
    readBtns();
    readData();
}