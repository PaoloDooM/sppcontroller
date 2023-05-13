import flet as ft
from models.AutomationEventTypes import *
from models.UserInputEvent import *
from pages.actions.widgets.DraggableChips import *
from pages.actions.widgets.DraggableTargets import *

def userInputTool(page, actionData = []):
    eventsWidgetList = ft.Column(controls=[])
    userInputEvents = actionData

    def remove():
        pass

    def launchEdit():
        pass

    def insertEvent(draggable: UserInputEvent, target: DraggableData):
        index = target.index + (1 if target.isRight else 0)
        draggable.index = index
        userInputEvents.insert(index, draggable)
        eventsWidgetList.controls = generateEventsWidgetsList(len(eventsWidgetList.controls) == 0)
        page.update()


    def generateInputTypesMenu():
        menu = []
        for inputType in AutomationEventTypes:
            menu.append(createDraggableChip(event=UserInputEvent(
                index=-1, eventType=inputType, attributes={})))
        return menu

    def generateEventsWidgetsList(isEmpty):
        if(isEmpty):
            return [createDraggableTargets(page=page, isEmpty=True, data=DraggableData(index=0,isRight=False), insertEvent=insertEvent)]
        events = []
        for event in userInputEvents:
            events.append(createDraggableTargets(page=page, isEmpty=False, data=DraggableData(index=event.index,isRight=False), insertEvent=insertEvent))
            events.append(createDraggableChip(event=event, remove=remove, launchEdit=launchEdit))
            events.append(createDraggableTargets(page=page, isEmpty=False, data=DraggableData(index=event.index,isRight=True), insertEvent=insertEvent))
        return events

    def navigateBack(e):
        page.views.pop()
        page.update()

    eventsWidgetList.controls = generateEventsWidgetsList(len(eventsWidgetList.controls) == 0)

    return ft.Container(
        content=ft.Row(
            [
                #ft.ListView(
                    #expand=1,
                    #height=500,
                    #controls=[
                        ft.Column(controls=generateInputTypesMenu()),
                    #]
                #),
                #ft.ListView(
                    #expand=1,
                    #height=500,
                    #controls=[
                        eventsWidgetList,
                    #]
                #),
                #ft.ListView(
                    #expand=1,
                    #height=500,
                    #controls=[
                        ft.ElevatedButton(
                            text="Back", on_click=navigateBack)
                    #],
                #),
            ],
        )
    )


def userInputPreview(page: ft.Page, actionData = []):

    def generateEventsPreview():
        if(len(actionData) == 0):
            return ft.Container(content=ft.Text("EMPTY"), alignment=ft.alignment.center)
        events = []
        for event in actionData:
            events.append(createDraggableChip(event=UserInputEvent(
                index=-1, eventType=event.eventType, attributes=event.Attributes)))
        return ft.ListView(
            expand=1,
            height=500,
            controls=[
                ft.Column(controls=events)
            ]
        )

    def navigateEventTool(e):
        page.views.append(userInputTool(page))
        page.update()

    return ft.Container(
        content=ft.Stack(
            [
                generateEventsPreview(),
                ft.Container(
                    content=ft.IconButton(
                        icon=ft.icons.EDIT, on_click=navigateEventTool, bgcolor='#123456',),

                    alignment=ft.alignment.bottom_right
                )
            ],
            height=160,
        ),
        padding=ft.padding.only(top=10)
    )
