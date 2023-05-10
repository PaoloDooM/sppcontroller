import socket
import time
import threading
import requests


class HTTPService:
    def __init__(self, sensorsService):
        self.hostIP = HTTPService.getIP()
        self.sensorsService = sensorsService
        self.clientIP = None
        self.clientPort = None
        self.isConnected = False

    @staticmethod
    def getIP():
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.settimeout(0)
        try:
            # doesn't even have to be reachable
            s.connect(('10.254.254.254', 1))
            IP = s.getsockname()[0]
        except Exception:
            IP = '127.0.0.1'
        finally:
            s.close()
        print(f'SPPController IP:  {IP}')
        return IP

    @staticmethod
    def writeTask(self, onError):
        while self.isConnected:
            try:
                url = f'http://{self.clientIP}:{self.clientPort}/display'
                print(f'Requesting display to: {url}')
                response = requests.post(url=url, verify=False, params={"display": "CPU: {0} {1}\r\n          {2} {3}\r\nRAM Usage: {4}%\r\nGPU: {5} {6}\r\n          {7} {8}".format(
                    self.sensorsService.sensors.cpuUsageToString(), self.sensorsService.sensors.cpuTempToString(), self.sensorsService.sensors.cpuPowerToString(), self.sensorsService.sensors.cpuClockToString(), self.sensorsService.sensors.ramUsageToString(), self.sensorsService.sensors.gpuUsageToString(), self.sensorsService.sensors.gpuTempToString(), self.sensorsService.sensors.gpuPowerToString() or self.sensorsService.sensors.gpuMemUsageToString(), self.sensorsService.sensors.gpuClockToString())})
                print(f'{response.text}, sleep: {self.sensorsService.writeInterval}')
                time.sleep(self.sensorsService.writeInterval)
            except:
                print("Error sending data through HTTP connection")
                self.isConnected = False
                onError()

    def sendConnectionRequest(self):
        url = f'http://{self.clientIP}:{self.clientPort}/connect'
        print(f'Requesting connection to: {url}')
        response = requests.post(
            url=url, verify=False, params={"ipAddress": f'{self.hostIP}'})
        if response.status_code != 200:
            raise Exception(f"HTTP connection error: {response.status_code}")
        else:
            print(
                f"HTTP connection stablished: {response.status_code}->{response.text}")

    def sendDisconnectionRequest(self):
        url = f'http://{self.clientIP}:{self.clientPort}/connect'
        print(f'Requesting disconnection to: {url}')
        response = requests.post(
            url=url, verify=False)
        if response.status_code != 400:
            raise Exception(
                f"HTTP disconnection error: {response.status_code}")
        else:
            print(
                f"HTTP disconnection success: {response.status_code}->{response.text}")

    def connect(self, clientIP, clientPort, onError):
        self.clientIP = clientIP
        self.clientPort = clientPort
        if not self.isConnected:
            self.sendConnectionRequest()
            self.isConnected = True
            writeThread = threading.Thread(
                target=HTTPService.writeTask, args=(self, onError), daemon=True)
            writeThread.start()

    def disconnect(self, clientIP = None, clientPort = None):
        refresh = False
        if clientIP != None and clientPort != None:
            self.clientIP = clientIP
            self.clientPort = clientPort
            refresh = True
        if self.isConnected or refresh:
            self.sendDisconnectionRequest()
            self.isConnected = False
