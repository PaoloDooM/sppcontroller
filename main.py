import sys  # nopep8
sys.dont_write_bytecode = True  # nopep8

import flet as ft
from pages.MainLayout import *
from containers.Container import *
from services.connection.Http import *
import threading

def flaskStart():
    api.run(port=53000)


def main():
    ft.app(target=appInit, assets_dir="assets")


def appInit(page: ft.Page):
    page.title = "SPPController"
    page.vertical_alignment = ft.MainAxisAlignment.CENTER
    page.window_height = 500
    page.window_width = 500
    page.window_maximizable = False
    page.window_resizable = False
    page.add(mainLayoutView(page=page))


if __name__ == "__main__":
    container = Container()
    # container.config.api_key.from_env("API_KEY", required=True)
    # container.config.timeout.from_env("TIMEOUT", as_=int, default=5)
    # container.init_resources()
    container.wire(
        modules=["pages.connection.Connection", "pages.actions.ActionsList", "pages.actions.ActionsForm", "pages.MainLayout", "pages.actions.widgets.ActionCard"])
    # main(*sys.argv[1:])
    flaskThread = threading.Thread(target=flaskStart, args=(), daemon=True)
    flaskThread.start()
    main()
