import socket
from flask import Flask, request
import threading
import requests

api = Flask(__name__)

def flaskStart(port):
    api.run(port=port)

flaskThread = threading.Thread(target=flaskStart, args=[53000], daemon=True)
flaskThread.start()

class HTTPServices:

    def __init__(self, actionsService, sensorsService):
        self.hostIP = HTTPServices.getIP()
        self.actiosService = actionsService
        self.sensorsService = sensorsService
        self.sendInterval = 3
        self.clientIP = None
        self.clientPort = None
        
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

    @api.route('/buttons', methods=['POST'])
    def registerAction(self):
        btn = request.json['btn']
        print(f'last button pressed: {btn}')
        self.actionsService.registerButtonEvent(btn)
        return 'Acknowledged'
    
    @staticmethod
    def writeTask(self):
        while True:
            response = requests.post(url=f'http://{self.clientIP}:{self.clientPort}/display', data="CPU: {0} {1}\r\n     {2} {3}\n\r\nRAM Usage: {4}%\n\r\nGPU: {5} {6}\r\n     {7} {8}".format(
                        self.sensorsService.sensors.cpuUsageToString(), self.sensorsService.sensors.cpuTempToString(), self.sensorsService.sensors.cpuPowerToString(), self.sensorsService.sensors.cpuClockToString(), self.sensorsService.sensors.ramUsageToString(), self.sensorsService.sensors.gpuUsageToString(), self.sensorsService.sensors.gpuTempToString(), self.sensorsService.sensors.gpuPowerToString() or self.sensorsService.sensors.gpuMemUsageToString(), self.sensorsService.sensors.gpuClockToString()))
            print(response.text)
