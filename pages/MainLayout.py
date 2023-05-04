import flet as ft
from pages.actions.Actions import *
from pages.connection.Connection import *
from pages.screen.Screen import *


def mainLayoutView(page):
    return ft.Tabs(
        selected_index=0,
        animation_duration=300,
        tabs=[
            ft.Tab(
                text="Actions",
                content=actionsView(page=page)
            ),
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
