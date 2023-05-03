import psutil
import GPUtil
from models.GpuTypes import *
from models.Sensors import *
import time
import threading
import clr


gpuType = GpuTypes.NONE
if (len(GPUtil.getGPUs()) != 0):
    gpuType = GpuTypes.NVIDIA
else:
    from pyadl import *
    if (len(ADLManager.getInstance().getDevices()) != 0):
        gpuType = GpuTypes.AMD


sensors = Sensors(gpuType=gpuType)

def getMaxCpuClock(cpuClocks):
    cpuClocks.sort(reverse=True)
    return cpuClocks[0]
    

def sensorsTask(interval, sensors, updateSensors):
    clr.AddReference(r'libs/OpenHardwareMonitorLib')
    from OpenHardwareMonitor.Hardware import Computer  # type: ignore
    openHardwareMonitor = Computer()
    openHardwareMonitor.CPUEnabled = True
    openHardwareMonitor.GPUEnabled = True
    openHardwareMonitor.Open()

    while True:
        sensors.setCpuUsage(psutil.cpu_percent())
        sensors.setRamUsage(psutil.virtual_memory().percent)
        if (sensors.gpuType == GpuTypes.NVIDIA):
            sensors.setGpuUsage(GPUtil.getGPUs()[0].load)
            sensors.setGpuMemUsage(GPUtil.getGPUs()[0].memoryUtil)
        elif (sensors.gpuType == GpuTypes.AMD):
            sensors.setGpuUsage(ADLManager.getInstance().getDevices()
                                [0].getCurrentUsage())

        try:
            for i in range(2):
                cpuClocks = []
                for a in range(0, len(openHardwareMonitor.Hardware[i].Sensors)):
                    #print(
                        #openHardwareMonitor.Hardware[i].Sensors[a].Identifier)
                    if (i == 0):
                        if "/clock" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                            cpuClocks.append(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                            #print("    {0}".format(
                                #openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                            openHardwareMonitor.Hardware[i].Update()
                    else:
                        if "/clock/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                            sensors.setGpuClock(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                            #print("    {0}".format(
                                #openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                            openHardwareMonitor.Hardware[i].Update()
                    if "/power/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                        if (i == 0):
                            sensors.setCpuPower(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                        else:
                            sensors.setGpuPower(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                        #print("    {0}W".format(
                            #openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                        openHardwareMonitor.Hardware[i].Update()
                    elif "/temperature/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                        if (i == 0):
                            sensors.setCpuTemp(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                        else:
                            sensors.setGpuTemp(
                                openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                        #print("    {0}C".format(
                            #openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                        openHardwareMonitor.Hardware[i].Update()
                if len(cpuClocks) != 0:
                    sensors.setCpuClock(getMaxCpuClock(cpuClocks))
        except:
            print(
                "Error reading stats from OpenHardwareMonitor, did you launch the app as admin?")

        print("\nCPU Usage: {0}%, CPU Clock: {1}, CPU Power: {2}W, CPU Temp: {3}C, RAM Usage: {4}%\n{5} GPU Usage: {6}%, GPU Clock: {7}, GPU Power: {8}W, GPU Temp: {9}C, GPU Mem Usage: {10}%.\n".format(
            sensors.cpuUsage, sensors.cpuClock, sensors.cpuPower, sensors.cpuTemp, sensors.ramUsage, sensors.gpuType.name, sensors.gpuUsage, sensors.gpuClock, sensors.gpuPower, sensors.gpuTemp, sensors.gpuMemUsage))

        updateSensors()
        time.sleep(interval)


def createSensorsDaemon(interval, sensors, updateSensors):
    sensorsThread = threading.Thread(target=sensorsTask, args=(
        interval, sensors, updateSensors), daemon=True)
    sensorsThread.start()
    return sensorsThread
