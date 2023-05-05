import flet as ft
from models.ActionTypes import *
from dependency_injector.wiring import Provide, inject
from services.actions.Executer import *
from containers.Container import *


def getActionTypeOptions():
    options = []
    for type in ActionTypes:
        options.append(
            ft.dropdown.Option(type.name.replace("_", " ")))
    return options


textFieldButton = ft.TextField(label="Button", value=None)
dropdownActionType = ft.Dropdown(
    options=getActionTypeOptions(),
)
textFieldAction = ft.TextField(label="Action")


@inject
def actionsFormView(page, executer: Executer = Provide[Container.executer], actionsTabController: ActionsTabController = Provide[Container.actionsTabController], persistence: Persistence = Provide[Container.persistence]):

    def updateTextFieldButton(button):
        textFieldButton.value = f'{button}'
        page.update()

    def cancel_button_clicked(e):
        actionsTabController.setListView(True)

    def save_button_clicked(e):
        persistence.upsertAction(Action(
            button=textFieldButton.value, type=dropdownActionType.value, data=textFieldAction.value))
        actionsTabController.setListView(True)

    cancelButton = ft.ElevatedButton("Cancel", on_click=cancel_button_clicked)
    saveButton = ft.ElevatedButton("Save", on_click=save_button_clicked)

    executer.addButtonEventCallback(updateTextFieldButton)

    return ft.Column(
        [
            textFieldButton, dropdownActionType, textFieldAction,
            ft.Container(content=ft.Row(
                [
                    cancelButton, saveButton
                ],
                alignment=ft.alignment.center
            ),
                alignment=ft.alignment.bottom_center
            )
        ],
        alignment=ft.MainAxisAlignment.CENTER
    )
