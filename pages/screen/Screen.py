import flet as ft
from dependency_injector.wiring import Provide, inject
from containers.Container import *
from pages.utils import *

textReadInterval = ft.TextField(
    label="Read interval (Seconds)", expand=1)
textWriteInterval = ft.TextField(
    label="Write interval (Seconds)", expand=1)
sensorsWidget = ft.Column(
    [
        ft.Container(content=ft.ProgressRing(width=32, height=32,
                     stroke_width=2), alignment=ft.alignment.center),
        ft.Container(content=ft.Text("Initializing..."),
                     alignment=ft.alignment.center)
    ],
    alignment=ft.MainAxisAlignment.CENTER,
)
resetButton = ft.ElevatedButton("Reset")
setButton = ft.ElevatedButton("Set")


@inject
def screenView(page, sensorsService: SensorsService = Provide[Container.sensorsService]):
    def updateSensors(sensors):
        sensorsWidget.controls = [
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
        ]
        sensorsWidget.alignment = ft.MainAxisAlignment.NONE
        page.update()

    sensorsService.addReadCallback(updateSensors)
    sensorsService.createSensorsDaemon()

    textReadInterval.value = f'{sensorsService.readInterval}'
    textWriteInterval.value = f'{sensorsService.writeInterval}'

    def verifyIntervals():
        errors = []
        if len(textReadInterval.value) == 0:
            errors.append("Read interval cannot be empty")
        elif not textReadInterval.value.isnumeric():
            errors.append("Read interval must be an integer")
        if len(textWriteInterval.value) == 0:
            errors.append("Write interval cannot be empty")
        elif not textWriteInterval.value.isnumeric():
            errors.append("Write interval must an integer")
        return errors

    def setIntervals(e):
        errors = verifyIntervals()
        if len(errors) != 0:
            displayErrorSnackBar(page, "\n".join(errors))
        else:
            sensorsService.readInterval = int(textReadInterval.value)
            sensorsService.writeInterval = int(textWriteInterval.value)

    def resetIntervals(e):
        textReadInterval.value = '2'
        sensorsService.readInterval = 2
        textWriteInterval.value = '3'
        sensorsService.writeInterval = 3

    setButton.on_click = setIntervals
    resetButton.on_click = resetIntervals

    return ft.Column(
        [
            ft.Container(
                padding=ft.padding.only(top=25, left=25, right=25),
                content=ft.Row(
                    [
                        textReadInterval,
                        ft.Container(width=25),
                        textWriteInterval
                    ],
                    alignment=ft.MainAxisAlignment.SPACE_EVENLY
                )
            ),
            ft.Container(
                alignment=ft.alignment.center,
                content=ft.Container(
                    content=sensorsWidget,
                    bgcolor='#000000',
                    width=150,
                    height=150
                )
            ),
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
