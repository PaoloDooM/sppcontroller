class Sensors:
    def __init__(self, gpuType):
        self.cpu = None
        self.ram = None
        self.gpu = None
        self.gpuType = gpuType

    def setCpu(self, cpu):
        self.cpu = cpu

    def setRam(self, ram):
        self.ram = ram

    def setGpu(self, gpu):
        self.gpu = gpu