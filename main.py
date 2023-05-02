import sys

sys.dont_write_bytecode = True

import flet as ft
from pages.screen.Screen import *
from pages.connection.Connection import *


def main(page: ft.Page):
    page.title = "SPPController"
    page.vertical_alignment = ft.MainAxisAlignment.CENTER
    page.window_height=400
    page.window_width=400
    page.window_maximizable=False
    page.window_resizable=False

    t = ft.Tabs(
        selected_index=0,
        animation_duration=300,
        tabs=[
            ft.Tab(
                text="Screen",
                content=screenContent(page=page)
            ),
            ft.Tab(
                text="Connection",
                content=connectionContent(page=page)
            ),
        ],
        expand=1,
    )

    page.add(t)


ft.app(target=main, assets_dir="assets")