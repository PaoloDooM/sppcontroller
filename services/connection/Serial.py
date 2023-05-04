import sys
import glob
import time
import serial
from services.sensors.Sensors import *

baudrates = [50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200,
             230400, 460800, 500000, 576000, 921600, 1000000, 1152000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000]


def serial_ports():
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


def connect(port, baudrate, executer):
    conn = serial.Serial(port=port, baudrate=baudrate)
    conn.write(stringCompleter("$cl$").encode())
    conn.flush()
    writeThread = threading.Thread(
        target=writeTask, args=(3, conn, sensors), daemon=True)
    writeThread.start()
    readThread = threading.Thread(
        target=readTask, args=(conn, executer), daemon=True)
    readThread.start()


def stringCompleter(data):
    if len(data) % 8 == 0:
        return data
    else:
        return stringCompleter("{0}\r".format(data))


def writeTask(interval, conn, sensors):
    while True:
        conn.write(stringCompleter("$cl$").encode())
        conn.flush()
        conn.write(stringCompleter("CPU: {0} {1}\r\n     {2} {3}\n\r\nRAM Usage: {4}%\n\r\nGPU: {5} {6}\r\n     {7} {8}".format(
            sensors.cpuUsageToString(), sensors.cpuTempToString(), sensors.cpuPowerToString(), sensors.cpuClockToString(), sensors.ramUsageToString(), sensors.gpuUsageToString(), sensors.gpuTempToString(), sensors.gpuPowerToString() or sensors.gpuMemUsageToString(), sensors.gpuClockToString())).encode())
        conn.flush()
        time.sleep(interval)

def readTask(conn, executer):
    while True:
        try:
            executer.registerButtonEvent(conn.read(size=4).decode('utf-8'))
        except:
            print("Error reading serial inputs")
