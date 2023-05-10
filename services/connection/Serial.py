import sys
import glob
import time
import serial
from services.sensors.Sensors import *


class SerialService:

    def __init__(self, sensorsService: SensorsService):
        self.baudrates = [50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200,
                          230400, 460800, 500000, 576000, 921600, 1000000, 1152000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000]
        self.sensorsService = sensorsService
        self.conn: serial.Serial = None

    @staticmethod
    def discoverSerialPorts():
        """ Lists serial port names

            :raises EnvironmentError:
                On unsupported or unknown platforms
            :returns:
                A list of the serial ports available on the system
        """
        if sys.platform.startswith('win'):
            ports = ['COM%s' % (i + 1) for i in range(256)]
        elif sys.platform.startswith('linux') or sys.platform.startswith('cygwin'):
            # this excludes your current terminal "/dev/tty"
            ports = glob.glob('/dev/tty[A-Za-z]*')
        elif sys.platform.startswith('darwin'):
            ports = glob.glob('/dev/tty.*')
        else:
            raise EnvironmentError('Unsupported platform')

        result = []
        for port in ports:
            try:
                s = serial.Serial(port=port, timeout=0.5)
                s.close()
                result.append(port)
            except (OSError, serial.SerialException):
                pass
        return result

    @staticmethod
    def stringCompleter(data):
        if len(data) % 8 == 0:
            return data
        else:
            return SerialService.stringCompleter("{0}\r".format(data))

    def connect(self, port, baudrate, actionsService, onError):
        if self.conn == None:
            self.conn = serial.Serial(port=port, baudrate=baudrate)
            self.conn.write(SerialService.stringCompleter("$cl$").encode())
            self.conn.flush()
            writeThread = threading.Thread(
                target=SerialService.writeTask, args=(self, onError), daemon=True)
            writeThread.start()
            readThread = threading.Thread(
                target=SerialService.readTask, args=(self, actionsService, onError), daemon=True)
            readThread.start()

    def disconnect(self):
        if self.conn != None:
            if (self.conn.is_open):
                self.conn.write(SerialService.stringCompleter("$dc$").encode())
                self.conn.flush()
                self.conn.close()
                self.conn = None

    @staticmethod
    def writeTask(self, onError):
        while self.conn != None:
            try:
                self.conn.write(SerialService.stringCompleter("$cl$").encode())
                self.conn.flush()
                self.conn.write(SerialService.stringCompleter("CPU: {0} {1}\r\n     {2} {3}\n\r\nRAM Usage: {4}%\n\r\nGPU: {5} {6}\r\n     {7} {8}".format(
                    self.sensorsService.sensors.cpuUsageToString(), self.sensorsService.sensors.cpuTempToString(), self.sensorsService.sensors.cpuPowerToString(), self.sensorsService.sensors.cpuClockToString(), self.sensorsService.sensors.ramUsageToString(), self.sensorsService.sensors.gpuUsageToString(), self.sensorsService.sensors.gpuTempToString(), self.sensorsService.sensors.gpuPowerToString() or self.sensorsService.sensors.gpuMemUsageToString(), self.sensorsService.sensors.gpuClockToString())).encode())
                self.conn.flush()
                print(f'Data sended, sleep: {self.sensorsService.writeInterval}')
                time.sleep(self.sensorsService.writeInterval)
            except:
                print("Error sending data through serial connection")
                onError()

    @staticmethod
    def readTask(self, actionsService, onError):
        while self.conn != None:
            try:
                actionsService.registerButtonEvent(
                    self.conn.read(size=4).decode('utf-8'))
            except:
                print("Error reading serial inputs")
                onError()
