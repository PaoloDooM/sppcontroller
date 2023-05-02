import psutil
import GPUtil
from pyadl import *
from models.GpuTypes import *
from models.Sensors import *
import time
import threading


def getGpuType():
    if (GPUtil.showUtilization() != None):
        return GpuTypes.NVIDIA
    elif (len(ADLManager.getInstance().getDevices()) != 0):
        return GpuTypes.AMD
    else:
        return GpuTypes.NONE


sensors = Sensors(gpuType=getGpuType())


def sensorsTask(interval, sensors, updateSensors):
    while True:
        sensors.setCpu(psutil.cpu_percent())
        sensors.setRam(psutil.virtual_memory().percent)
        if (sensors.gpuType == GpuTypes.NVIDIA):
            sensors.setGpu(GPUtil.showUtilization())
        elif (sensors.gpuType == GpuTypes.AMD):
            sensors.setGpu(ADLManager.getInstance().getDevices()
                           [0].getCurrentUsage())
        print("CPU: {0}%, RAM: {1}%, {3} GPU: {2}%.".format(
            sensors.cpu, sensors.ram, sensors.gpu, sensors.gpuType.name))
        updateSensors()
        time.sleep(interval)


def createSensorsDaemon(interval, sensors, updateSensors):
    sensorsThread = threading.Thread(target=sensorsTask, args=(
        interval, sensors, updateSensors), daemon=True)
    sensorsThread.start()
    return sensorsThread
