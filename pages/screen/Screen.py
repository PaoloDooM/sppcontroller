import flet as ft
from models.Sensors import Sensors
from services.sensors.Sensors import *


textReadInterval = ft.TextField(
    label="Read interval (Seconds)", value=1, disabled=True, expand=1)
textSendInterval = ft.TextField(
    label="Send interval (Seconds)", value=3, disabled=True, expand=1)
sensorsWidget = ft.Container(
    alignment=ft.alignment.center,
    content=ft.Text("Initializing")
)
resetButton = ft.ElevatedButton("Reset")
setButton = ft.ElevatedButton("Set")


def screenView(page):
    def updateSensors():
        sensorsWidget.content = ft.Container(
            ft.Column(
                [
                    ft.Text(" CPU: {0} {1}".format(
                        sensors.cpuUsageToString(), sensors.cpuTempToString())),
                    ft.Text("          {0} {1}".format(
                        sensors.cpuPowerToString(), sensors.cpuClockToString())),
                    ft.Text(" RAM Usage: {0}".format(
                        sensors.ramUsage)),
                    ft.Text(" GPU: {0} {1}".format(
                        sensors.gpuUsageToString(), sensors.gpuTempToString())),
                    ft.Text("          {0} {1}".format(sensors.gpuPowerToString(
                    ) or sensors.gpuMemUsageToString(), sensors.gpuClockToString())),
                ],
            ),
            bgcolor='#000000',
            width=150,
            height=150
        )
        page.update()

    createSensorsDaemon(interval=1, sensors=sensors,
                        updateSensors=updateSensors)
    return ft.Column(
        [
            ft.Container(
                padding=ft.padding.only(top=25, left=25, right=25),
                content=ft.Row(
                    [
                        textReadInterval,
                        ft.Container(width=25),
                        textSendInterval
                    ],
                    alignment=ft.MainAxisAlignment.SPACE_EVENLY
                )
            ),
            sensorsWidget,
            ft.Row(
                [
                    resetButton,
                    setButton
                ],
                alignment=ft.MainAxisAlignment.SPACE_EVENLY
            )
        ],
        alignment=ft.MainAxisAlignment.SPACE_BETWEEN
    )
