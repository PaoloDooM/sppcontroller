import flet as ft

lastButtonPressed = ft.Container(
    alignment=ft.alignment.center,
    content=ft.Text('Last button pressed: None')
)

def actionsContent(page, executer):
    def updateActions(button):
        global lastButtonPressed
        lastButtonPressed.content = ft.Text(f'Last button pressed: {button}')
        page.update()

    executer.setUpdateActions(updateActions)

    return ft.Column(
        [
            lastButtonPressed
        ],
        alignment=ft.MainAxisAlignment.CENTER
    )