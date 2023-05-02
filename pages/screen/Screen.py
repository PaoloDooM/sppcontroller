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
                ft.Text("CPU usage: {0}%".format(sensors.cpu)),
                ft.Text("RAM usage: {0}%".format(sensors.ram)),
                ft.Text("GPU usage: {0}%".format(sensors.gpu))
            ],
            alignment=ft.MainAxisAlignment.CENTER
        )
        page.update()

    createSensorsDaemon(interval=1, sensors=sensors,
                        updateSensors=updateSensors)

    return sensorsWidget
