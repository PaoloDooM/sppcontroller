class Sensors:
    def __init__(self, gpuType):
        self.cpuUsage = None
        self.cpuPower = None
        self.cpuTemp = None
        self.ramUsage = None
        self.gpuUsage = None
        self.gpuPower = None
        self.gpuTemp = None
        self.gpuType = gpuType

    def setCpuUsage(self, cpuUsage):
        self.cpuUsage = cpuUsage

    def setCpuPower(self, cpuPower):
        self.cpuPower = cpuPower

    def setCpuTemp(self, cpuTemp):
        self.cpuTemp = cpuTemp

    def setRamUsage(self, ramUsage):
        self.ramUsage = ramUsage

    def setGpuUsage(self, gpuUsage):
        self.gpuUsage = gpuUsage
    
    def setGpuPower(self, gpuPower):
        self.gpuPower = gpuPower

    def setGpuTemp(self, gpuTemp):
        self.gpuTemp = gpuTemp