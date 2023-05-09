import flet as ft
import math
from dependency_injector.wiring import Provide, inject
from containers.Container import *

@inject
def actionCard(action: Action, changeActionsTab, refreshActionsList, persistence: Persistence = Provide[Container.persistence]):
    def deleteAction(e):
        persistence.deleteAction(button=action.button)
        refreshActionsList(persistence.getActions())

    def editAction(e):
        changeActionsTab(False, action)

    return ft.Card(
        content=ft.ListTile(
            leading=ft.Column(
                [
                    ft.Container(
                        content=ft.Text(
                            f'{action.button}'),
                        alignment=ft.alignment.center,
                        width=60,
                        height=30
                    ),
                    ft.Container(
                        content=ft.Divider(),
                        alignment=ft.alignment.center,
                        width=60,
                        height=5
                    )
                ],
                alignment={ft.MainAxisAlignment.CENTER,
                           ft.CrossAxisAlignment.START},
                rotate=ft.Rotate(
                    angle=1.5 * math.pi,
                    alignment=ft.alignment.center,
                ),
            ),
            title=ft.Text(f'{action.type.label()}'),
            subtitle=ft.Text(f'{action.data}', tooltip=f'{action.data}', overflow=ft.TextOverflow.ELLIPSIS, max_lines=1),
            trailing=ft.Column(
                [
                    ft.IconButton(
                        icon=ft.icons.EDIT_ROUNDED,
                        icon_color="pink600",
                        tooltip="Edit",
                        icon_size=20,
                        height=20,
                        style=ft.ButtonStyle(
                            padding=ft.padding.all(0),
                        ),
                        on_click=editAction
                    ),
                    ft.IconButton(
                        icon=ft.icons.DELETE_ROUNDED,
                        icon_color="amber600",
                        icon_size=20,
                        height=20,
                        tooltip="Delete",
                        style=ft.ButtonStyle(
                            padding=ft.padding.all(0),
                        ),
                        on_click=deleteAction
                    ),
                ],
                alignment=ft.MainAxisAlignment.CENTER,
            )
        )
    )
