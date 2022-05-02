/*-----------------------------------------------------
Author:  PaoloDooM
Date: 2022-04-29
Description: sppcontroller
Board: PIC18F2550
-----------------------------------------------------*/

#define NB 4

int btnsAD[NB] = {17, 10, 11, 12}, i;
BOOL btnsSt[NB] = {false,   false,   false,   false};
char* btnsCm[NB] = {"$00$","$01$","$02$","$03$"};

void setup() {
    pinMode(USERLED, OUTPUT);
    for(i = 0; i < NB; i++){
        pinMode(btnsAD[i], INPUT);
    }
    Serial.begin(9600);
    lcdi2c.init(128, 64, 0x2C);
    lcdi2c.backlight();
    lcdi2c.autoscroll();
    lcdi2c.clear();
    lcdi2c.setCursor(62, 31);
    lcdi2c.printf("HC-06");
    lcdi2c.setCursor(58, 33);
    lcdi2c.printf("PASS:  1234");
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
    if(!strcmp(data, "$cl$")){
         lcdi2c.clear();
         lcdi2c.home();
    }else{
         lcdi2c.printf(data);
    }
}

void readData(){
    if(Serial.available()){
        writeLcd(Serial.getString());
    }
}
