import flet as ft
from dependency_injector.wiring import Provide, inject
from dependency_injector.errors import *
from containers.Container import *
from pages.utils import *


def getActionTypeOptions():
    options = []
    for type in ActionTypes:
        options.append(
            ft.dropdown.Option(text=type.label(), key=type.value))
    return options


@inject
def actionsFormView(page, changeActionsTab, action: Action = None, actionsService: ActionsService = Provide[Container.actionsService], persistence: Persistence = Provide[Container.persistence]):

    textFieldButton = ft.TextField(
        label="Button", value=None if action == None else action.button, disabled=action != None)
    dropdownActionType = ft.Dropdown(
        options=getActionTypeOptions(),
        label="Action type",
        value=None if action == None else action.type.value
    )
    textFieldAction = ft.TextField(
        label="Action", value=None if action == None else action.data)

    def updateTextFieldButton(button):
        textFieldButton.value = f'{button}'
        page.update()

    def cancel_button_clicked(e):
        changeActionsTab(True)

    def save_button_clicked(e):
        try:
            actionVerifier()
            persistence.upsertAction(Action(
                button=textFieldButton.value, type=dropdownActionType.value, data=textFieldAction.value))
            changeActionsTab(True)
        except Exception as e:
            displayErrorSnackBar(page, str(e))

    def actionVerifier():
        errors = []
        if len(textFieldButton.value) == 0:
            errors.append("Button field cannot be empty")
        if dropdownActionType.value == None:
            errors.append("Action type cannot be empty")
        if len(textFieldAction.value) == 0:
            errors.append("Action field cannot be empty")
        if len(errors) != 0:
            raise Exception("\n".join(errors))

    cancelButton = ft.ElevatedButton("Cancel", on_click=cancel_button_clicked)
    saveButton = ft.ElevatedButton(
        "Add" if action == None else "Save", on_click=save_button_clicked)

    actionsService.addButtonEventCallback(updateTextFieldButton)

    return ft.Column(
        [
            ft.Container(
                content=ft.Column(
                    [
                        textFieldButton, dropdownActionType, textFieldAction,
                    ],
                ),
                padding=ft.padding.only(top=25)
            ),
            ft.Row(
                [
                    cancelButton, saveButton
                ],
                alignment=ft.MainAxisAlignment.SPACE_EVENLY,
            )
        ],
        alignment=ft.MainAxisAlignment.SPACE_BETWEEN
    )
