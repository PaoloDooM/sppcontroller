import flet as ft
from models.ConnectionTypes import ConnectionTypes
from services.connection.Serial import *
import threading
from dependency_injector.wiring import Provide, inject
from containers.Container import *
from pages.utils import *

discoveringLabel = "Discovering ports..."

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


connectionTypeWidget = ft.Dropdown(
    options=createConnectionTypesOptions(),
    disabled=True
)
portDropdownWidget = ft.Dropdown(
    options=[ft.dropdown.Option(text=discoveringLabel)],
    value=discoveringLabel,
    label="COM port",
    disabled=True,
)
baudrateDropdownWidget = ft.Dropdown(options=[], label="Baudrate")
refreshButton = ft.ElevatedButton(
    content=ft.Text("Refresh"),
    disabled=True
)
connectButton = ft.ElevatedButton(
    content=ft.Text("Connect"),
    disabled=True
)
disconnectButton = ft.ElevatedButton(
    content=ft.Text("Disconnect"),
    visible=False
)


@inject
def connectionView(page, actionsService: ActionsService = Provide[Container.actionsService], serialService: SerialService = Provide[Container.serialService]):

    def portDropdown(page, portDropdownWidget):
        portDropdownWidget.options = [ft.dropdown.Option(text=discoveringLabel)]
        portDropdownWidget.value = discoveringLabel
        page.update()
        portDropdownWidget.options = createPortOptions(serialService.discoverSerialPorts())
        portDropdownWidget.disabled = False
        portDropdownWidget.value = None
        refreshButton.disabled = False
        connectButton.disabled = False
        page.update()

    def serialConnectionVerify(port, baudrate):
        errors = []
        if len(port) == 0:
            errors.append('COM port cannot be empty')
        if len(f'{baudrate}' if baudrate != None else "") == 0:
            errors.append('Baudrate cannot be empty')
        return errors
    
    def onError():
        disconnectButton.visible = False
        connectButton.visible = True
        serialService.disconnect()
        displayErrorSnackBar(page, "Connection closed!")
        portDropdownWidget.disabled = False
        refreshButton.disabled = False
        connectButton.disabled = False
        baudrateDropdownWidget.disabled = False
        page.update()

    def serialDisconnect(e):
        serialService.disconnect()
        connectButton.visible = True
        disconnectButton.visible = False
        portDropdownWidget.disabled = False
        refreshButton.disabled = False
        connectButton.disabled = False
        baudrateDropdownWidget.disabled = False

    def serialConnect(e):
        errors = serialConnectionVerify(portDropdownWidget.value, baudrateDropdownWidget.value)
        if len(errors) != 0:
            displayErrorSnackBar(page, "\n".join(errors))
            return
        try:
            portDropdownWidget.disabled = True
            refreshButton.disabled = True
            connectButton.disabled = True
            baudrateDropdownWidget.disabled = True
            print("{0} - {1}".format(portDropdownWidget.value,
                    baudrateDropdownWidget.value))
            serialService.connect(port=portDropdownWidget.value,
                                    baudrate=baudrateDropdownWidget.value, onError=onError)
            connectButton.visible = False
            disconnectButton.visible = True
            page.update()
        except:
            displayErrorSnackBar(page, "Connection Failed!")

    baudrateDropdownWidget.options = createBaudrateOptions(
        serialService.baudrates)
    portsThread = threading.Thread(target=portDropdown, args=(
        page, portDropdownWidget), daemon=True)
    portsThread.start()
    connectionTypeWidget.value = ConnectionTypes.SERIAL.name

    def refreshPorts(e):
        portDropdownWidget.disabled = True
        refreshButton.disabled = True
        connectButton.disabled = True
        portsThread = threading.Thread(target=portDropdown, args=(
            page, portDropdownWidget), daemon=True)
        portsThread.start()
        page.update()


    refreshButton.on_click=refreshPorts
    connectButton.on_click=serialConnect
    disconnectButton.on_click=serialDisconnect
   

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
            ft.Row(
                [
                    refreshButton,
                    connectButton,
                    disconnectButton
                ],
                alignment=ft.MainAxisAlignment.SPACE_EVENLY
            ),
        ],
        alignment=ft.MainAxisAlignment.SPACE_BETWEEN
    )
