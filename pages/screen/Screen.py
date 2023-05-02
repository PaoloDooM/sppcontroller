import flet as ft
from models.Sensors import Sensors
from services.sensors.Sensors import *


sensorsWidget = ft.Container(
    alignment=ft.alignment.center,
    content=ft.Text("Initializing")
)


def screenContent(page):
    def updateSensors():
        sensorsWidget.content = ft.Column(
            [
                ft.Text("CPU: {0}% {1:.2f}C".format(sensors.cpuUsage, sensors.cpuTemp)),
                ft.Text("         {0:.2f}W".format(sensors.cpuPower)),
                ft.Text("RAM Usage: {0}%".format(sensors.ramUsage)),
                ft.Text("GPU: {0}% {1:.2f}C".format(sensors.gpuUsage, sensors.gpuTemp)),
                ft.Text("         {0:.2f}W".format(sensors.gpuPower)),
            ],
            alignment=ft.MainAxisAlignment.CENTER
        )
        page.update()

    createSensorsDaemon(interval=1, sensors=sensors,
                        updateSensors=updateSensors)

    return sensorsWidget
