import psutil
import GPUtil
from pyadl import *
from models.GpuTypes import *
from models.Sensors import *
import time
import threading
import clr


def getGpuType():
    if (GPUtil.showUtilization() != None):
        return GpuTypes.NVIDIA
    elif (len(ADLManager.getInstance().getDevices()) != 0):
        return GpuTypes.AMD
    else:
        return GpuTypes.NONE


sensors = Sensors(gpuType=getGpuType())


def sensorsTask(interval, sensors, updateSensors):
    clr.AddReference(r'libs/OpenHardwareMonitorLib')
    from OpenHardwareMonitor.Hardware import Computer  # type: ignore
    openHardwareMonitor = Computer()
    openHardwareMonitor.CPUEnabled = True
    openHardwareMonitor.GPUEnabled = True
    openHardwareMonitor.Open()

    sensors.setCpuPower(0)
    sensors.setGpuPower(0)
    sensors.setCpuTemp(0)
    sensors.setGpuTemp(0)

    while True:
        sensors.setCpuUsage(psutil.cpu_percent())
        sensors.setRamUsage(psutil.virtual_memory().percent)
        if (sensors.gpuType == GpuTypes.NVIDIA):
            sensors.setGpuUsage(GPUtil.showUtilization())
        elif (sensors.gpuType == GpuTypes.AMD):
            sensors.setGpuUsage(ADLManager.getInstance().getDevices()
                                [0].getCurrentUsage())

        try:
            for i in range(2):
                for a in range(0, len(openHardwareMonitor.Hardware[i].Sensors)):
                    print(
                        openHardwareMonitor.Hardware[i].Sensors[a].Identifier)
                    if "/power/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                        if (i == 0):
                            sensors.setCpuPower(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value() or 0)
                        else:
                            sensors.setGpuPower(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value() or 0)
                        print("    {0}W".format(
                            openHardwareMonitor.Hardware[i].Sensors[a].get_Value() or 0))
                        openHardwareMonitor.Hardware[i].Update()
                    elif "/temperature/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                        if (i == 0):
                            sensors.setCpuTemp(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value() or 0)
                        else:
                            sensors.setGpuTemp(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value() or 0)
                        print("    {0}C".format(
                            openHardwareMonitor.Hardware[i].Sensors[a].get_Value() or 0))
                        openHardwareMonitor.Hardware[i].Update()
        except:
            print("Error reading stats from OpenHardwareMonitor, did you launch the app as admin?")

        print("\nCPU Usage: {0}%, CPU Power: {1:.2f}W, CPU Temp: {2:.2f}C, RAM Usage: {3}%, {4} GPU Usage: {5}%, GPU Power: {6:.2f}W, GPU Temp: {7:.2f}C.\n".format(
            sensors.cpuUsage, sensors.cpuPower, sensors.cpuTemp, sensors.ramUsage, sensors.gpuType.name, sensors.gpuUsage, sensors.gpuPower, sensors.gpuTemp))

        updateSensors()
        time.sleep(interval)


def createSensorsDaemon(interval, sensors, updateSensors):
    sensorsThread = threading.Thread(target=sensorsTask, args=(
        interval, sensors, updateSensors), daemon=True)
    sensorsThread.start()
    return sensorsThread
