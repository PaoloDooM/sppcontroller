import flet as ft
from services.connection.Serial import *
import threading
from dependency_injector.wiring import Provide, inject
from services.actions.Executer import *
from containers.Container import *


def createBaudrateOptions(baudrates):
    options = []
    for baudrate in baudrates:
        options.append(ft.dropdown.Option(baudrate))
    return options


def createPortOptions(ports):
    print(ports)
    options = []
    for port in ports:
        options.append(ft.dropdown.Option(port))
    return options


def baudrateDropdown():
    return


def portDropdown(page, portDropdownWidget):
    portDropdownWidget.content = ft.Dropdown(
        options=createPortOptions(serial_ports()))
    page.update()


portDropdownWidget = ft.Container(content=ft.Text(
    "Initializing"), alignment=ft.alignment.center, padding=ft.padding.symmetric(vertical=25))
baudrateDropdownWidget = ft.Dropdown(options=createBaudrateOptions(baudrates))


@inject
def connectionView(page, executer: Executer = Provide[Container.executer]):

    def serialConnect(e):
        print("{0} - {1}".format(portDropdownWidget.content.value,
                                 baudrateDropdownWidget.value))
        connect(port=portDropdownWidget.content.value,
                baudrate=baudrateDropdownWidget.value,
                executer=executer
                )

    portsThread = threading.Thread(target=portDropdown, args=(
        page, portDropdownWidget), daemon=True)
    portsThread.start()
    return ft.Container(
        alignment=ft.alignment.center,
        content=ft.Column([
            portDropdownWidget,
            baudrateDropdownWidget,
            ft.Container(content=ft.ElevatedButton(content=ft.Text("Connect"),
                                                   on_click=serialConnect), alignment=ft.alignment.center, padding=ft.padding.symmetric(vertical=25))
        ],
            alignment=ft.MainAxisAlignment.CENTER)
    )
