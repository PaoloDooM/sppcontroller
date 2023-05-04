import flet as ft
from models.Action import *

def actionCard(action: Action):
    return ft.Card(content=ft.ListTile(title=ft.Text(f'{action.button}'), subtitle=ft.Column([ft.Text(f'{action.type}'), ft.Text(f'{action.data}')])))