/*-----------------------------------------------------
Author:  PaoloDooM
Date: 2022-04-29
Description: sppcontroller
Board: PIC18F2550
-----------------------------------------------------*/

#include <fonts/font5x7.h>
#define OLED_SSD1306
#define OLED_128X64
#define NB 4

int btnsAD[NB] = {17, 10, 11, 12}, i;
BOOL btnsSt[NB] = {false,   false,   false,   false};
char* btnsCm[NB] = {"$00$","$01$","$02$","$03$"};
const u8 intf = OLED_I2C1;

void setup() {
    pinMode(USERLED, OUTPUT);
    for(i = 0; i < NB; i++){
        pinMode(btnsAD[i], INPUT);
    }
    Serial.begin(9600);
    
    OLED.init(intf, 0x78);
    OLED.setFont(intf, font5x7);
    OLED.clearScreen(intf);
    
    OLED.printf(intf, "DEVICE: HC-06\r\nPASSWORD: 1234");
    
    OLED.displayOn(intf);
}

void loop() {
    readBtns();
    readData();
}

readBtns(){
    for(i = 0; i < NB; i++){
        if(digitalRead(btnsAD[i])!=btnsSt[i]){
            if(!btnsSt[i]){
                toggle(USERLED);
                Serial.printf(btnsCm[i]);
            }
            btnsSt[i]=!btnsSt[i];
         }
    }
}

void writeLcd(char* data){
    if(strstr(data, "$cl$") != NULL){
         OLED.clearScreen(intf);
    }
    OLED.printf(intf, data);
}

void readData(){
    if(Serial.available()){
        writeLcd(Serial.getString());
    }
    OLED.refresh(intf);
}
