import flet as ft
from pages.actions.ActionsList import *
from pages.actions.ActionsForm import *
from pages.connection.Connection import *
from pages.screen.Screen import *
from controllers.ActionsTabController import *
from dependency_injector.wiring import Provide, inject

actionsTab = None


@inject
def mainLayoutView(page, actionsTabController: ActionsTabController = Provide[Container.actionsTabController]):

    def changeActionsTab(listView):
        if listView:
            actionsTab.content = actionsListView(page)
        else:
            actionsTab.content = actionsFormView(page)
        page.update()

    actionsTabController.setChangeActionsTab(changeActionsTab)
    actionsTab = ft.Tab(
        text="Actions",
        content=actionsListView(page)
    )

    return ft.Tabs(
        selected_index=0,
        animation_duration=300,
        tabs=[
            actionsTab,
            ft.Tab(
                text="Screen",
                content=screenView(page=page)
            ),
            ft.Tab(
                text="Connection",
                content=connectionView(page=page,)
            ),
        ],
        expand=1,
    )
