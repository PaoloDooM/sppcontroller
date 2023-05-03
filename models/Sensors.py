class Sensors:
    def __init__(self, gpuType):
        self.cpuUsage = None
        self.cpuClock = None
        self.cpuPower = None
        self.cpuTemp = None
        self.ramUsage = None
        self.gpuUsage = None
        self.cpuClock = None
        self.gpuPower = None
        self.gpuTemp = None
        self.gpuMemUsage = None
        self.gpuType = gpuType

    def setCpuUsage(self, cpuUsage):
        self.cpuUsage = cpuUsage

    def setCpuClock(self, cpuClock):
        self.cpuClock = cpuClock

    def setCpuPower(self, cpuPower):
        self.cpuPower = cpuPower

    def setCpuTemp(self, cpuTemp):
        self.cpuTemp = cpuTemp

    def setRamUsage(self, ramUsage):
        self.ramUsage = ramUsage

    def setGpuUsage(self, gpuUsage):
        self.gpuUsage = gpuUsage

    def setGpuClock(self, gpuClock):
        self.gpuClock = gpuClock
    
    def setGpuPower(self, gpuPower):
        self.gpuPower = gpuPower

    def setGpuTemp(self, gpuTemp):
        self.gpuTemp = gpuTemp

    def setGpuMemUsage(self, gpuMemUsage):
        self.gpuMemUsage = gpuMemUsage

    def cpuUsageToString(self):
        if self.cpuUsage == None:
            return None
        return f'{self.cpuUsage:.1f}%'
    
    def cpuClockToString(self):
        if self.cpuClock == None:
            return None
        return f'{self.cpuClock:.0f}Mhz'
    
    def cpuPowerToString(self):
        if self.cpuPower == None:
            return None
        return f'{self.cpuPower:.1f}W'
    
    def cpuTempToString(self):
        if self.cpuTemp == None:
            return None
        return f'{self.cpuTemp:.1f}C'
    
    def ramUsageToString(self):
        if self.ramUsage == None:
            return None
        return f'{self.ramUsage:.1f}%'
    
    def gpuUsageToString(self):
        if self.gpuUsage == None:
            return None
        return f'{self.gpuUsage:.1f}%'
    
    def gpuClockToString(self):
        if self.gpuClock == None:
            return None
        return f'{self.gpuClock:.0f}Mhz'
    
    def gpuPowerToString(self):
        if self.gpuPower == None:
            return None
        return f'{self.gpuPower:.1f}W'
        
    def gpuTempToString(self):
        if self.gpuTemp == None:
            return None
        return f'{self.gpuTemp:.1f}C'
        
    def gpuMemUsageToString(self):
        if self.gpuMemUsage == None:
            return None
        return f'{self.gpuMemUsage:.1f}%'