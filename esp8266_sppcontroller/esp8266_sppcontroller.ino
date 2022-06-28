#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <Wire.h>
#include <SSD1306Wire.h>
#include "RouterConfig.h"

#ifndef STASSID || STAPSK
#define STASSID "wifiName"
#define STAPSK  "wifiPass"
#endif

#define NB 6

int btnsSt[NB] = {0, 0, 0, 0, 0, 0}, i;
char* btnsCm[NB] = {"$00$", "$01$", "$02$", "$03$", "$04$", "$05$"};
const char* ssid = STASSID;
const char* password = STAPSK;
const int btnsAd[NB] = {D0, D1, D2, D5, D6, D7}, led = 13;
String token = "", serverIp = "";

ESP8266WebServer server(80);
SSD1306Wire display(0x3C, D4, D3);

void handleRoot() {
  digitalWrite(led, 1);
  server.send(200, "text/plain", "Running");
  digitalWrite(led, 0);
}

void handleNotFound() {
  digitalWrite(led, 1);
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i = 0; i < server.args(); i++) {
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
  digitalWrite(led, 0);
}

void handleConnect() {
  if (server.arg("ipAddress") == "") {
    clearWsCfg();
    server.send(400, "text/plain", "Bad request");
  } else {
    HTTPClient http;
    WiFiClient client;
    serverIp = server.arg("ipAddress");
    http.begin(client, "http://" + serverIp + ":53000/authenticate");
    http.addHeader("Content-Type", "application/json");
    int httpCode = http.POST("{\"password\":\"9DTUdnKN5z4jPVaKYAdBjX7C\",\"username\":\"sppcontroller\"}");
    if (httpCode == 200) {
      token = http.getString();
      Serial.print("Authenticated: ");
      Serial.println(token);
      server.send(200, "text/plain", serverIp + " = " + token);
    } else {
      clearWsCfg();
      server.send(httpCode, "text/plain", "Authentication fail");
    }
    http.end();
  }
}

void sendBtn(String btn) {
  if (!serverIp.isEmpty() && !token.isEmpty()) {
    HTTPClient http;
    WiFiClient client;
    http.begin(client, "http://" + serverIp + ":53000/buttons");
    http.addHeader("Content-Type", "application/json");
    http.addHeader("Authorization", "Bearer " + token);
    int httpCode = http.POST("{\"btn\":\"" + btn + "\"}");
    if (httpCode == 200) {
      Serial.print("Button sended: ");
      Serial.println(btn);
    } else {
      Serial.println("Button sended: Fail");
    }
    http.end();
  }
}

void clearWsCfg() {
  serverIp = "";
  token = "";
}

void readBtns() {
  for (i = 0; i < NB; i++) {
    if (digitalRead(btnsAd[i]) != btnsSt[i]) {
      if (!btnsSt[i]) {
        sendBtn(btnsCm[i]);
        display.clear();
        display.drawString(0, 0, WiFi.localIP().toString());
        display.drawString(0, 30, btnsCm[i]);
        display.display();
      }
      btnsSt[i] = !btnsSt[i];
    }
  }
}

void setup(void) {
  pinMode(led, OUTPUT);
  digitalWrite(led, 0);
  Serial.begin(115200);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("");

  display.init();
  display.flipScreenVertically();
  display.displayOn();
  display.setFont(ArialMT_Plain_16);
  display.drawString(0, 0, "Initializing");
  display.display();

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  display.clear();
  display.drawString(0, 0, WiFi.localIP().toString());
  display.display();

  if (MDNS.begin("esp8266")) {
    Serial.println("MDNS responder started");
  }

  server.on("/", handleRoot);
  server.onNotFound(handleNotFound);
  server.on("/connect", handleConnect);

  server.begin();
  Serial.println("HTTP server started");
}

void loop(void) {
  server.handleClient();
  readBtns();
  MDNS.update();
}
