import flet as ft
from pages.actions.ActionsList import *
from pages.actions.ActionsForm import *
from pages.connection.Connection import *
from pages.screen.Screen import *
from controllers.ActionsTabController import *
from dependency_injector.wiring import Provide, inject

tabView = ft.Tabs(
    selected_index=0,
    animation_duration=300,
    tabs=[],
    expand=1
)
actionsTab = None
addActionsFAB = ft.FloatingActionButton(
    icon=ft.icons.ADD, bgcolor='#8a2be2'
)


@inject
def mainLayoutView(page, actionsTabController: ActionsTabController = Provide[Container.actionsTabController]):

    def changeActionsTab(listView):
        if listView:
            page.floating_action_button = addActionsFAB
            actionsTab.content = actionsListView(page)
        else:
            page.floating_action_button = None
            actionsTab.content = actionsFormView(page)
        page.update()

    def addActionsFormView(e):
        actionsTabController.setListView(False)
        page.update()

    def onTabChange(e):
        changeActionsTab(True)
        if tabView.selected_index == 0:
            page.floating_action_button = addActionsFAB
        else:
            page.floating_action_button = None
        page.update()

    addActionsFAB.on_click = addActionsFormView
    page.floating_action_button = addActionsFAB

    actionsTabController.setChangeActionsTab(changeActionsTab)
    actionsTab = ft.Tab(
        text="Actions",
        content=actionsListView(page)
    )

    tabView.on_change = onTabChange
    tabView.tabs = [
        actionsTab,
        ft.Tab(
            text="Screen",
            content=screenView(page=page)
        ),
        ft.Tab(
            text="Connection",
            content=connectionView(page=page,)
        )
    ]

    return tabView
