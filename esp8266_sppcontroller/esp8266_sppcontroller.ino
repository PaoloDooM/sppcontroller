#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <Wire.h>
#include <SSD1306Wire.h>
#include <WebSocketsClient.h>
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
unsigned long messageInterval = 5000, lastUpdate = millis();
bool connected = false, startWS = false;
String token = "", serverIp = "";

ESP8266WebServer server(80);
SSD1306Wire display(0x3C, D4, D3);
WebSocketsClient webSocket;

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
      String json = http.getString();
      // Allocate the JSON document
      //
      // Inside the brackets, 256 is the capacity of the memory pool in bytes.
      // Don't forget to change this value to match your JSON document.
      // Use https://arduinojson.org/v6/assistant to compute the capacity.
      StaticJsonDocument<300> doc;
      DeserializationError error = deserializeJson(doc, json);
      if (error) {
        Serial.print(F("deserializeJson() failed: "));
        Serial.println(error.f_str());
        clearWsCfg();
        server.send(500, "text/plain", "deserializeJson() failed");
      } else {
        token = String(doc["token"]);
        Serial.print("Authenticated: ");
        Serial.println(json);
        startWS = true;
        server.send(200, "text/plain", serverIp + " = " + token);
      }
    } else {
      clearWsCfg();
      server.send(httpCode, "text/plain", "Authentication fail");
    }
    http.end();
  }
}

void clearWsCfg() {
  serverIp = "";
  token = "";
}

void sendMessage(String & msg) {
    webSocket.sendTXT(msg.c_str(), msg.length() + 1);
}

void webSocketEvent(WStype_t type, uint8_t * payload, size_t length) {
  switch (type) {
    case WStype_DISCONNECTED:
      Serial.println("[WSc] Disconnected!\n");
      connected = false;
      break;
    case WStype_CONNECTED: {
        Serial.print("[WSc] Connected to url: ");
        Serial.println((char*) payload);
        connected = true;

        String msg = "CONNECT\r\naccept-version:1.1,1.0\r\nheart-beat:10000,10000\r\n\r\n";
        sendMessage(msg);

        // send message to server when Connected
        Serial.println("[WSc] SENT: Connected");;
      }
      break;
    case WStype_TEXT:{
        // #####################
        // handle STOMP protocol
        // #####################

        String text = (char*) payload;
      
        Serial.print("[WSc] RESPONSE: ");
        Serial.println(text);

        if (text.startsWith("CONNECTED")) {

          // subscribe to some channels

          String msg = "SUBSCRIBE\nid:sub-0\ndestination:/topic/response\n\n";
          sendMessage(msg);
          delay(1000);

          // and send a message

          msg = "SEND\ndestination:/app/commands\n\n{\"uuid\":\"0\",\"type\":\"button\",\"data\":\"$00$\",\"code\":\"200\",\"message\":\"\"}";
          sendMessage(msg);
          delay(1000);
                    
        } else {

        // do something with messages
                    
        }
      }
      break;
    case WStype_BIN:
      Serial.print("[WSc] get binary length: ");
      Serial.println(length);
      hexdump(payload, length);
      break;
    case WStype_PING:
      // pong will be send automatically
      Serial.println("[WSc] get ping\n");
      break;
    case WStype_PONG:
      // answer to a ping we send
      Serial.println("[WSc] get pong\n");
      break;
  }
}

void readBtns() {
  for (i = 0; i < NB; i++) {
    if (digitalRead(btnsAd[i]) != btnsSt[i]) {
      if (!btnsSt[i]) {
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
  if (!serverIp.isEmpty() && !token.isEmpty()) {
    if (startWS) {
      Serial.println("Starting WS");
      webSocket.begin(serverIp, 53000, "/websocket");
      String authHeader = "Authorization: Bearer " + token;
      int authHeader_len = authHeader.length() + 1;
      char authHeader_buf[authHeader_len];
      authHeader.toCharArray(authHeader_buf, authHeader_len);
      webSocket.setExtraHeaders(authHeader_buf);
      webSocket.onEvent(webSocketEvent);
      startWS = false;
    }
    webSocket.loop();
    if (connected && lastUpdate + messageInterval < millis()) {
      Serial.println("[WSc] SENT: Simple js client message!!");
      String msg = "SEND\ndestination:/app/commands\n\n{\"uuid\":\"0\",\"type\":\"button\",\"data\":\"$01$\",\"code\":\"200\",\"message\":\"\"}";
      sendMessage(msg);
      lastUpdate = millis();
    }
  }
  MDNS.update();
}
