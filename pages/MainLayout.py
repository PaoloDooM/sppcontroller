import flet as ft
from pages.actions.ActionsList import *
from pages.actions.ActionsForm import *
from pages.connection.Connection import *
from pages.screen.Screen import *
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
listView = True


def mainLayoutView(page):

    def changeActionsTab(value, action=None):
        global listView
        listView = value
        if value:
            page.floating_action_button = addActionsFAB
            actionsTab.content = actionsListView(page, changeActionsTab)
        else:
            page.floating_action_button = None
            actionsTab.content = actionsFormView(page, changeActionsTab, action)
        page.update()

    def addActionsFormView(e):
        changeActionsTab(False)

    def onTabChange(e):
        if tabView.selected_index == 0 and listView:
            page.floating_action_button = addActionsFAB
        else:
            page.floating_action_button = None
        page.update()

    addActionsFAB.on_click = addActionsFormView
    page.floating_action_button = addActionsFAB

    actionsTab = ft.Tab(
        text="Actions",
        content=actionsListView(page=page, changeActionsTab=changeActionsTab)
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
