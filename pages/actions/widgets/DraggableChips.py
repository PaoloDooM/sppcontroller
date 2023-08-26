import flet as ft
from models.UserInputEvent import *

width = 100
height = 20
radius = height * 0.5
contentSize = height * 0.6


def createDraggableChip(event: UserInputEvent, remove=None, launchEdit=None):
    def edit(e):
        if event.index >= 0:
            launchEdit(event)

    def delete(e):
        if event.index >= 0:
            remove(event)

    return ft.Draggable(
        group="events",
        content=ft.Container(
            width=width,
            height=height,
            bgcolor=event.eventType.getColor(),
            border_radius=radius,
            content=ft.Row(
                [
                    ft.IconButton(icon=ft.icons.EDIT, on_click=edit,
                                  icon_size=contentSize, height=contentSize*2, width=contentSize*2, visible=True if event.index >= 0 else False),
                    ft.Text(value=f'{event.eventType.getLabel()}', size=contentSize,
                            overflow=ft.TextOverflow.FADE, weight=ft.FontWeight.BOLD, text_align=ft.TextAlign.CENTER),
                    ft.IconButton(icon=ft.icons.CLOSE, on_click=delete,
                                  icon_size=contentSize, height=contentSize*2, width=contentSize*2, visible=True if event.index >= 0 else False),
                ],
                alignment=ft.MainAxisAlignment.SPACE_EVENLY
            )
        ),
        content_feedback=ft.Container(
            width=width,
            height=height,
            bgcolor=event.eventType.getColor(),
            opacity=0.5,
            border_radius=radius,
            content=ft.Row(
                [
                    ft.IconButton(icon=ft.icons.EDIT, on_click=edit,
                                  icon_size=contentSize, height=contentSize*2, width=contentSize*2, visible=True if event.index >= 0 else False),
                    ft.Text(value=f'{event.eventType.getLabel()}', size=contentSize,
                            overflow=ft.TextOverflow.FADE, weight=ft.FontWeight.BOLD, text_align=ft.TextAlign.CENTER),
                    ft.IconButton(icon=ft.icons.CLOSE, on_click=delete,
                                  icon_size=contentSize, height=contentSize*2, width=contentSize*2, visible=True if event.index >= 0 else False),
                ],
                alignment=ft.MainAxisAlignment.SPACE_EVENLY
            )
        ),
        data=event
    )
