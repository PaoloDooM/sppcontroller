import flet as ft

def displayErrorSnackBar(page, error: str):
    page.snack_bar = ft.SnackBar(content=ft.Text(
        error, color='#ffffff', weight=ft.FontWeight.BOLD), bgcolor='#9c0044')
    page.snack_bar.open = True
    page.update()