import flet as ft
from dependency_injector.wiring import Provide, inject
from containers.Container import *
from pages.actions.widgets.ActionCard import *

lastButtonWidget = ft.Container(
    alignment=ft.alignment.top_left,
    content=ft.Text(f'Last button pressed: {None}')
)

actionCardsList = ft.ListView(controls=[], padding=ft.padding.only(bottom=75))


@inject
def actionsListView(page, changeActionsTab, actionsService: ActionsService = Provide[Container.actionsService], persistence: Persistence = Provide[Container.persistence]):

    actionsService.setPage(page=page)

    def updateLastButtonWidget(button):
        lastButtonWidget.content = ft.Text(f'Last button pressed: {button}')
        page.update()

    def refreshActionsList(actions):
        actionCardsList.controls = buildActionCards(actions)
        page.update()

    def buildActionCards(actions: Action):
        actionsService.setActions(actions)
        cards = []
        for action in actions:
            cards.append(actionCard(
                action, changeActionsTab=changeActionsTab, refreshActionsList=refreshActionsList))
        return cards

    actionsService.addButtonEventCallback(updateLastButtonWidget)
    actionCardsList.controls = buildActionCards(persistence.getActions())

    return ft.Stack(
        [
            ft.Container(
                padding=ft.padding.only(top=5),
                content=lastButtonWidget,
                alignment=ft.alignment.top_left,
            ),
            ft.Container(
                padding=ft.padding.only(top=30),
                content=actionCardsList,
                alignment=ft.alignment.bottom_center,
            )
        ],
    )
