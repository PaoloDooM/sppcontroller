import flet as ft
from models.ActionTypes import *

lastButtonPressed = None
lastButtonWidget = ft.Container(
    alignment=ft.alignment.center,
    content=ft.Text('Last button pressed: None')
)
actionsContent = ft.Column(
    [],
    alignment=ft.MainAxisAlignment.CENTER
)
textFieldButton = ft.TextField(label="Button")
dropdownActionType = ft.Dropdown(
    options=[],
)
textFieldAction = ft.TextField(label="Input")

def actionFormPage(page):
    def cancel_button_clicked(e):
        actionsContent.controls = [lastButtonWidget]
        page.update()
    textFieldButton.value = lastButtonPressed
    cancelButton = ft.ElevatedButton("Cancel", on_click=cancel_button_clicked)
    return [textFieldButton, dropdownActionType, textFieldAction, ft.Row([cancelButton, ft.ElevatedButton("Save"),], alignment=ft.alignment.center)]

def actionsPage(page, executer):
    def updateActions(button):
        global lastButtonPressed
        lastButtonWidget.content = ft.Text(f'Last button pressed: {button}')
        lastButtonPressed = button
        textFieldButton.value = button
        page.update()
    def fab_pressed(e):
        actionsContent.controls = actionFormPage(page=page)
        page.update()
    for type in ActionTypes:
        dropdownActionType.options.append(ft.dropdown.Option(type.name.replace("_", " ")))
    executer.setUpdateActions(updateActions)
    actionsContent.controls = [lastButtonWidget]
    return ft.Stack(
        [
            ft.Container(
                content=ft.FloatingActionButton(icon=ft.icons.ADD, on_click=fab_pressed, bgcolor='#8a2be2'),
                alignment=ft.alignment.bottom_right,
            ),
            actionsContent
        ],
    )