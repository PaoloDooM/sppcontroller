import flet as ft
from models.ActionTypes import *
from dependency_injector.wiring import Provide, inject
from services.actions.Executer import *
from containers.Container import *

lastButtonWidget = ft.Container(
    alignment=ft.alignment.center,
    content=ft.Text(f'Last button pressed: {None}')
)

@inject
def actionsListView(page, executer: Executer = Provide[Container.executer], actionsTabController: ActionsTabController = Provide[Container.actionsTabController]):
    def updateLastButtonWidget(button):
        lastButtonWidget.content = ft.Text(f'Last button pressed: {button}')
        page.update()

    def addActionsView(e):
        actionsTabController.setListView(False)

    executer.addButtonEventCallback(updateLastButtonWidget)

    return ft.Stack(
        [
            lastButtonWidget,
            ft.Container(
                content=ft.FloatingActionButton(
                    icon=ft.icons.ADD, on_click=addActionsView, bgcolor='#8a2be2'),
                alignment=ft.alignment.bottom_right,
            )
        ],
    )
