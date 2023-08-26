import copy
import flet as ft
from models.AutomationEventTypes import *
from models.UserInputEvent import *
from pages.actions.widgets.DraggableChips import *
from pages.actions.widgets.DraggableTargets import *


def userInputTool(page, actionData=[]):
    eventsWidgetList = ft.GridView(
        controls=[],
        expand=1,
        runs_count=3,
        max_extent=100,
        child_aspect_ratio=1.0,
        spacing=5,
        run_spacing=5
    )
    
    userInputEvents: list = actionData

    def remove(event):
        pass

    def launchEdit(event):
        pass

    def insertEvent(draggable: UserInputEvent, target: int):
        if draggable.index < 0:
            draggableCopy = copy.copy(draggable)
            draggableCopy.setIndex(target)
            userInputEvents.insert(target, draggableCopy)
            eventsWidgetList.controls = generateEventsWidgetsList(
                len(eventsWidgetList.controls) == 0)
            page.update()
        else:
            toMove: UserInputEvent = userInputEvents[draggable.index]
            userInputEvents.remove(toMove)
            toMove.setIndex(target)
            userInputEvents.insert(target, toMove)
            eventsWidgetList.controls = generateEventsWidgetsList(
                len(eventsWidgetList.controls) == 0)
            page.update()


    def generateInputTypesMenu():
        menu = []
        for inputType in AutomationEventTypes:
            menu.append(createDraggableChip(event=UserInputEvent(
                index=-1, eventType=inputType, attributes={})))
        return menu

    def generateEventsWidgetsList(isEmpty):
        if (isEmpty):
            return [createDraggableTarget(page=page, isEmpty=True, data=0, insertEvent=insertEvent)]
        events = []
        for i, input in enumerate(userInputEvents):
            event = copy.copy(input)
            event.setIndex(i)
            events.append(createDraggableTarget(
                page=page, isEmpty=False, data=i, insertEvent=insertEvent))
            events.append(createDraggableChip(
                event=event, remove=remove, launchEdit=launchEdit))
        events.append(createDraggableTarget(
            page=page, isEmpty=False, data=len(userInputEvents), insertEvent=insertEvent))
        return events

    def navigateBack(e):
        page.views.pop()
        page.update()

    eventsWidgetList.controls = generateEventsWidgetsList(
        len(eventsWidgetList.controls) == 0)

    return ft.Stack(
        [ft.Row(
            [
                # ft.ListView(
                # expand=1,
                # height=500,
                # controls=[
                ft.Column(controls=generateInputTypesMenu()),
                # ]
                # ),
                # ft.ListView(
                # expand=1,
                # height=500,
                # controls=[
                eventsWidgetList,
            ]),
                # ]
                # ),
                # ft.ListView(
                # expand=1,
                # height=500,
                # controls=[
                    ft.Container(content=
                ft.ElevatedButton(
                    text="Back", on_click=navigateBack),alignment=ft.alignment.bottom_right
                    )
                # ],
                # ),
            ],
        )


def userInputPreview(page: ft.Page, actionData=[]):

    def generateEventsPreview():
        if (len(actionData) == 0):
            return ft.Container(content=ft.Text("EMPTY", weight=ft.FontWeight.BOLD,), alignment=ft.alignment.center)
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
