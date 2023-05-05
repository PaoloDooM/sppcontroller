import flet as ft
from models.ConnectionTypes import ConnectionTypes
from services.connection.Serial import *
import threading
from dependency_injector.wiring import Provide, inject
from services.actions.Executer import *
from containers.Container import *


def createConnectionTypesOptions():
    options = []
    for option in ConnectionTypes:
        options.append(ft.dropdown.Option(option.name))
    return options


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


def portDropdown(page, portDropdownWidget):
    portDropdownWidget.content = ft.Dropdown(
        options=createPortOptions(serial_ports()),
        label="COM port"    
    )
    portDropdownWidget.padding = ft.padding.all(0)
    page.update()


connectionTypeWidget = ft.Dropdown(
    options=createConnectionTypesOptions(),
    disabled=True
)
portDropdownWidget = ft.Container(
    content=ft.Text(
        "Initializing"
    ),
    padding=ft.padding.symmetric(vertical=30),
    alignment=ft.alignment.center
)
baudrateDropdownWidget = ft.Dropdown(options=createBaudrateOptions(baudrates), label="Baudrate")


@inject
def connectionView(page, executer: Executer = Provide[Container.executer]):

    def serialConnect(e):
        print("{0} - {1}".format(portDropdownWidget.content.value,
              baudrateDropdownWidget.value))
        connect(port=portDropdownWidget.content.value,
                baudrate=baudrateDropdownWidget.value, executer=executer)

    portsThread = threading.Thread(target=portDropdown, args=(
        page, portDropdownWidget), daemon=True)
    portsThread.start()

    connectionTypeWidget.value = ConnectionTypes.SERIAL.name

    return ft.Column(
        [
            ft.Container(
                alignment=ft.alignment.center,
                content=connectionTypeWidget,
                padding=ft.padding.only(top=25)
            ),
            ft.Column(
                [
                    portDropdownWidget,
                    baudrateDropdownWidget,
                ]
            ),
            ft.Container(
                content=ft.ElevatedButton(
                    content=ft.Text("Connect"),
                    on_click=serialConnect
                ),
                alignment=ft.alignment.center
            ),
        ],
        alignment=ft.MainAxisAlignment.SPACE_BETWEEN
    )
