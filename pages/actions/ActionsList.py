import flet as ft
from models.ActionTypes import *
from dependency_injector.wiring import Provide, inject
from services.actions.Executer import *
from containers.Container import *
from pages.actions.widgets.ActionCard import *

lastButtonWidget = ft.Container(
    alignment=ft.alignment.top_left,
    content=ft.Text(f'Last button pressed: {None}')
)

actionCardsList = ft.ListView(controls=[])


@inject
def actionsListView(page, executer: Executer = Provide[Container.executer], actionsTabController: ActionsTabController = Provide[Container.actionsTabController], persistence: Persistence = Provide[Container.persistence]):
    def updateLastButtonWidget(button):
        lastButtonWidget.content = ft.Text(f'Last button pressed: {button}')
        page.update()

    def buildActionCards(actions):
        cards = []
        for action in actions:
            cards.append(actionCard(
                Action(button=action['button'], type=action['type'], data=action['data'])))
        return cards

    executer.addButtonEventCallback(updateLastButtonWidget)
    actionCardsList.controls = buildActionCards(persistence.getActions())

    return ft.Column(
        [
            ft.Container(
                content=lastButtonWidget,
                alignment=ft.alignment.top_left,
            ),
            actionCardsList,
        ],
    )
