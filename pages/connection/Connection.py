import flet as ft
from models.ConnectionTypes import ConnectionTypes
import threading
from dependency_injector.wiring import Provide, inject
from containers.Container import *
from pages.utils import *
import re

discoveringLabel = "Discovering ports..."


def createConnectionTypesOptions():
    options = []
    for option in ConnectionTypes:
        options.append(ft.dropdown.Option(
            text=option.name, key=f'{option.value}'))
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
    label="Connection type"
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
textIP = ft.TextField(
    label="IP", value="192.168.150.100", visible=False)
textPort = ft.TextField(
    label="Port", value="80", visible=False)


@inject
def connectionView(page, serialService: SerialService = Provide[Container.serialService], httpService: HTTPService = Provide[Container.httpService]):

    def onError():
        try:
            if f'{connectionTypeWidget.value}' == f'{ConnectionTypes.SERIAL.value}':
                serialService.disconnect()
            elif f'{connectionTypeWidget.value}' == f'{ConnectionTypes.HTTP.value}':
                httpService.disconnect()
        except:
            print(f"Error disconecting from {list(ConnectionTypes)[int(connectionTypeWidget.value)].name}")
        displayErrorSnackBar(page, "Connection closed!")
        connectionTypeWidget.disabled = False
        disconnectButton.visible = False
        connectButton.visible = True
        portDropdownWidget.disabled = False
        refreshButton.disabled = False
        connectButton.disabled = False
        baudrateDropdownWidget.disabled = False
        textPort.disabled = False
        textIP.disabled = False
        page.update()

    def httpConnectionVerify(ip, port):
        errors = []
        if len(ip) == 0:
            errors.append('IP field port cannot be empty')
        elif not bool(re.match(r"[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}", ip)):
            errors.append('Wrong syntax on IP field')
        if len(f'{port}' if port != None else "") == 0:
            errors.append('Port field cannot be empty')
        elif not (f'{port}' if port != None else "").isnumeric():
            errors.append('Port field must be numeric')
        return errors

    def httpConnect(e):
        errors = httpConnectionVerify(
            textIP.value, textPort.value)
        if len(errors) != 0:
            displayErrorSnackBar(page, "\n".join(errors))
            return
        try:
            connectionTypeWidget.disabled = True
            portDropdownWidget.disabled = True
            refreshButton.disabled = True
            connectButton.disabled = True
            baudrateDropdownWidget.disabled = True
            textIP.disabled = True
            textPort.disabled = True
            httpService.connect(
                textIP.value, textPort.value, onError)
            connectButton.visible = False
            disconnectButton.visible = True
            page.update()
        except:
            connectButton.visible = True
            disconnectButton.visible = False
            portDropdownWidget.disabled = False
            refreshButton.disabled = False
            connectButton.disabled = False
            baudrateDropdownWidget.disabled = False
            connectionTypeWidget.disabled = False
            textIP.disabled = False
            textPort.disabled = False
            page.update()
            displayErrorSnackBar(page, "Connection Failed!")

    def httpDisconnect(e):
        try:
            httpService.disconnect()
            connectButton.visible = True
            disconnectButton.visible = False
            portDropdownWidget.disabled = False
            refreshButton.disabled = False
            connectButton.disabled = False
            baudrateDropdownWidget.disabled = False
            connectionTypeWidget.disabled = False
            textIP.disabled = False
            textPort.disabled = False
            page.update()
        except:
            displayErrorSnackBar(page, "Disconnection Failed!")

    def onConecctionTypeChange(e):
        print(f"->{connectionTypeWidget.value}")
        if (f'{connectionTypeWidget.value}' == f'{ConnectionTypes.SERIAL.value}'):
            textIP.visible = False
            textPort.visible = False
            portDropdownWidget.visible = True
            baudrateDropdownWidget.visible = True
            refreshButton.visible = True
            connectButton.on_click = serialConnect
            disconnectButton.on_click = serialDisconnect
        elif (f'{connectionTypeWidget.value}' == f'{ConnectionTypes.HTTP.value}'):
            textIP.visible = True
            textPort.visible = True
            portDropdownWidget.visible = False
            baudrateDropdownWidget.visible = False
            refreshButton.visible = False
            connectButton.on_click = httpConnect
            disconnectButton.on_click = httpDisconnect
        page.update()

    connectionTypeWidget.on_change = onConecctionTypeChange

    def portDropdown(page, portDropdownWidget):
        portDropdownWidget.options = [
            ft.dropdown.Option(text=discoveringLabel)]
        portDropdownWidget.value = discoveringLabel
        page.update()
        portDropdownWidget.options = createPortOptions(
            serialService.discoverSerialPorts())
        connectionTypeWidget.disabled = False
        portDropdownWidget.disabled = False
        portDropdownWidget.value = None
        refreshButton.disabled = False
        connectButton.disabled = False
        textIP.disabled = False
        textPort.disabled = False
        page.update()

    def serialConnectionVerify(port, baudrate):
        errors = []
        if len(port) == 0:
            errors.append('COM port cannot be empty')
        if len(f'{baudrate}' if baudrate != None else "") == 0:
            errors.append('Baudrate cannot be empty')
        return errors

    def serialDisconnect(e):
        try:
            serialService.disconnect()
            connectButton.visible = True
            disconnectButton.visible = False
            portDropdownWidget.disabled = False
            refreshButton.disabled = False
            connectButton.disabled = False
            baudrateDropdownWidget.disabled = False
            connectionTypeWidget.disabled = False
            textIP.disabled = False
            textPort.disabled = False
            page.update()
        except:
            displayErrorSnackBar(page, "Disconnection Failed!")

    def serialConnect(e):
        errors = serialConnectionVerify(
            portDropdownWidget.value, baudrateDropdownWidget.value)
        if len(errors) != 0:
            displayErrorSnackBar(page, "\n".join(errors))
            return
        try:
            connectionTypeWidget.disabled = True
            portDropdownWidget.disabled = True
            refreshButton.disabled = True
            connectButton.disabled = True
            baudrateDropdownWidget.disabled = True
            textIP.disabled = True
            textPort.disabled = True
            print("{0} - {1}".format(portDropdownWidget.value,
                                     baudrateDropdownWidget.value))
            serialService.connect(port=portDropdownWidget.value,
                                  baudrate=baudrateDropdownWidget.value, onError=onError)
            connectButton.visible = False
            disconnectButton.visible = True
            page.update()
        except:
            connectButton.visible = True
            disconnectButton.visible = False
            portDropdownWidget.disabled = False
            refreshButton.disabled = False
            connectButton.disabled = False
            baudrateDropdownWidget.disabled = False
            connectionTypeWidget.disabled = False
            textIP.disabled = False
            textPort.disabled = False
            page.update()
            displayErrorSnackBar(page, "Connection Failed!")

    baudrateDropdownWidget.options = createBaudrateOptions(
        serialService.baudrates)
    portsThread = threading.Thread(target=portDropdown, args=(
        page, portDropdownWidget), daemon=True)
    portsThread.start()
    connectionTypeWidget.value = f'{ConnectionTypes.SERIAL.value}'

    def refreshPorts(e):
        portDropdownWidget.disabled = True
        refreshButton.disabled = True
        connectButton.disabled = True
        connectionTypeWidget.disabled = True
        textIP.disabled = True
        textPort.disabled = True
        portsThread = threading.Thread(target=portDropdown, args=(
            page, portDropdownWidget), daemon=True)
        portsThread.start()
        page.update()

    refreshButton.on_click = refreshPorts
    connectButton.on_click = serialConnect
    disconnectButton.on_click = serialDisconnect

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
                    textIP,
                    textPort
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
