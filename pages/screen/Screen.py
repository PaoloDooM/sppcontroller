import flet as ft
from models.Sensors import Sensors
from services.sensors.Sensors import *


sensorsWidget = ft.Container(
    alignment=ft.alignment.center,
    content=ft.Text("Initializing")
)


def screenView(page):
    def updateSensors():
        sensorsWidget.content = ft.Column(
            [
                ft.Text("CPU: {0} {1}".format(sensors.cpuUsageToString(), sensors.cpuTempToString())),
                ft.Text("         {0} {1}".format(sensors.cpuPowerToString(), sensors.cpuClockToString())),
                ft.Text("RAM Usage: {0}".format(sensors.ramUsage)),
                ft.Text("GPU: {0} {1}".format(sensors.gpuUsageToString(), sensors.gpuTempToString())),
                ft.Text("         {0} {1}".format(sensors.gpuPowerToString() or sensors.gpuMemUsageToString(), sensors.gpuClockToString())),
            ],
            alignment=ft.MainAxisAlignment.CENTER
        )
        page.update()

    createSensorsDaemon(interval=1, sensors=sensors,
                        updateSensors=updateSensors)

    return sensorsWidget
