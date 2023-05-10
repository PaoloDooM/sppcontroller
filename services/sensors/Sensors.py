import psutil
import GPUtil
from models.GpuTypes import *
from models.Sensors import *
import time
import threading
import clr


class SensorsService:
    def __init__(self):
        gpuType = GpuTypes.NONE
        try:
            if (len(GPUtil.getGPUs()) != 0):
                gpuType = GpuTypes.NVIDIA
            else:
                from pyadl import ADLManager
                if (len(ADLManager.getInstance().getDevices()) != 0):
                    gpuType = GpuTypes.AMD
        except Exception as e:
            print(f'{e}\nImpossible to determine GpuType')
        self.sensors = Sensors(gpuType=gpuType)
        self.sensorsThread: threading.Thread = None
        self.readInterval = 2
        self.writeInterval = 3
        self.readCallbacks = []

    def addReadCallback(self, callback):
        self.readCallbacks.append(callback)

    @staticmethod
    def sensorsTask(self):
        clr.AddReference(r'libs/OpenHardwareMonitorLib')
        from OpenHardwareMonitor.Hardware import Computer  # type: ignore
        openHardwareMonitor = Computer()
        openHardwareMonitor.CPUEnabled = True
        openHardwareMonitor.GPUEnabled = True
        openHardwareMonitor.Open()

        if (self.sensors.gpuType == GpuTypes.AMD):
            from pyadl import ADLManager

        def getMaxCpuClock(cpuClocks):
            cpuClocks.sort(reverse=True)
            return cpuClocks[0]

        while True:
            self.sensors.setCpuUsage(psutil.cpu_percent())
            self.sensors.setRamUsage(psutil.virtual_memory().percent)
            if (self.sensors.gpuType == GpuTypes.NVIDIA):
                self.sensors.setGpuUsage(GPUtil.getGPUs()[0].load)
                self.sensors.setGpuMemUsage(GPUtil.getGPUs()[0].memoryUtil)
            elif (self.sensors.gpuType == GpuTypes.AMD):
                self.sensors.setGpuUsage(ADLManager.getInstance().getDevices()
                                         [0].getCurrentUsage())

            try:
                for i in range(2):
                    cpuClocks = []
                    for a in range(0, len(openHardwareMonitor.Hardware[i].Sensors)):
                        # print(
                        # openHardwareMonitor.Hardware[i].Sensors[a].Identifier)
                        # print("    {0}".format(
                        # openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                        if (i == 0):
                            if "/clock" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                                cpuClocks.append(
                                    openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                                # print("    {0}Mhz".format(
                                # openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                                openHardwareMonitor.Hardware[i].Update()
                        else:
                            if "/clock/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                                self.sensors.setGpuClock(
                                    openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                                # print("    {0}Mhz".format(
                                # openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                                openHardwareMonitor.Hardware[i].Update()
                        if "/power/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                            if (i == 0):
                                self.sensors.setCpuPower(
                                    openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                            else:
                                self.sensors.setGpuPower(
                                    openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                                # print("    {0}W".format(
                                # openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                            openHardwareMonitor.Hardware[i].Update()
                        elif "/temperature/0" in str(openHardwareMonitor.Hardware[i].Sensors[a].Identifier):
                            if (i == 0):
                                self.sensors.setCpuTemp(
                                    openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                            else:
                                self.sensors.setGpuTemp(
                                    openHardwareMonitor.Hardware[i].Sensors[a].get_Value())
                                # print("    {0}C".format(
                                # openHardwareMonitor.Hardware[i].Sensors[a].get_Value()))
                            openHardwareMonitor.Hardware[i].Update()
                    if len(cpuClocks) != 0:
                        self.sensors.setCpuClock(getMaxCpuClock(cpuClocks))
            except:
                print(
                    "Error reading stats from OpenHardwareMonitor, did you launch the app as admin?, did you force close?")

            print("\nCPU Usage: {0}%, CPU Clock: {1}, CPU Power: {2}W, CPU Temp: {3}C, RAM Usage: {4}%\n{5} GPU Usage: {6}%, GPU Clock: {7}, GPU Power: {8}W, GPU Temp: {9}C, GPU Mem Usage: {10}%.\n".format(
                self.sensors.cpuUsage, self.sensors.cpuClock, self.sensors.cpuPower, self.sensors.cpuTemp, self.sensors.ramUsage, self.sensors.gpuType.name, self.sensors.gpuUsage, self.sensors.gpuClock, self.sensors.gpuPower, self.sensors.gpuTemp, self.sensors.gpuMemUsage))

            for callback in self.readCallbacks:
                callback(self.sensors)
            print(f'Reading sensors, sleep: {self.readInterval}')
            time.sleep(self.readInterval)

    def createSensorsDaemon(self):
        if self.sensorsThread == None:
            self.sensorsThread = threading.Thread(
                target=SensorsService.sensorsTask, args=[self], daemon=True)
            self.sensorsThread.start()
