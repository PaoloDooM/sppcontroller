import flet as ft
from models.AutomationEventTypes import *


eventWidgetsList = None

def eventsView(page):

    global eventWidgetsList

    eventsList = []

    chipSize = [100, 20, 10]

    def getEventsChips(event=None):
        if (event != None):
            def edit(e):
                pass

            def delete(e):
                global eventWidgetsList
                eventsList.remove(event)
                eventWidgetsList = getEventsList()
                page.update()
            return ft.Draggable(
                group="events",
                content=ft.Container(
                    width=chipSize[0],
                    height=chipSize[1],
                    bgcolor=event["type"].getColor(),
                    border_radius=chipSize[1]/2,
                    content=ft.Row(
                        [
                            ft.IconButton(icon=ft.icons.EDIT, on_click=edit),
                            ft.Text(f'{event["type"].getLabel()}'),
                            ft.IconButton(icon=ft.icons.CLOSE,
                                          on_click=delete),
                        ]
                    )
                ),
                content_feedback=ft.Container(
                    width=chipSize[0],
                    height=chipSize[1],
                    bgcolor=event["type"].getColor(),
                    opacity=0.5,
                    border_radius=chipSize[1]/2,
                    content=ft.Row(
                        [
                            ft.IconButton(icon=ft.icons.EDIT, on_click=edit),
                            ft.Text(f'{event["type"].getLabel()}'),
                            ft.IconButton(icon=ft.icons.CLOSE,
                                          on_click=delete),
                        ]
                    )
                ),
                data=event
            )
        eventsTypeList = []
        for eventType in AutomationEventTypes:
            eventsTypeList.append(
                ft.Draggable(
                    group="events",
                    content=ft.Container(
                        width=chipSize[0],
                        height=chipSize[1],
                        bgcolor=eventType.getColor(),
                        border_radius=chipSize[1]/2,
                        content=ft.Row(
                            [
                                ft.Text(f'{eventType.getLabel()}')
                            ]
                        )
                    ),
                    content_feedback=ft.Container(
                        width=chipSize[0],
                        height=chipSize[1],
                        bgcolor=eventType.getColor(),
                        opacity=0.5,
                        border_radius=chipSize[1]/2,
                        content=ft.Row(
                            [
                                ft.Text(f'{eventType.getLabel()}')
                            ]
                        )
                    ),
                    data={"type": eventType, "index": -1, "attributes": []}
                )
            )
        return eventsTypeList

    def drag_will_accept(e):
        e.control.content.border = ft.border.all(
            2, ft.colors.BLACK45)
        e.control.update()

    def drag_accept(e: ft.DragTargetAcceptEvent):
        global eventWidgetsList
        src = page.get_control(e.src_id)
        e.control.content.bgcolor = src.content.bgcolor
        e.control.content.border = None
        eventsList.append(src.data["type"])
        eventWidgetsList = getEventsList()
        page.update()

    def drag_leave(e):
        e.control.content.border = None
        e.control.update()

    def getEventsList():
        print(f'{eventsList}')
        dragTargetList = [
            ft.DragTarget(
                group="events",
                content=ft.Container(
                    width=50,
                    height=50,
                    bgcolor=ft.colors.BLUE_GREY_100,
                    border_radius=25,
                ),
                on_will_accept=drag_will_accept,
                on_accept=drag_accept,
                on_leave=drag_leave,
            )
        ]
        if len(eventsList) == 0:
            return dragTargetList
        else:
            for event in eventsList:
                dragTargetList.append(getEventsChips({'type': event}))
            return dragTargetList
        
    eventWidgetsList = getEventsList()

    return ft.ListView(
        [
            ft.Row(
                controls=[
                    ft.Column(controls=getEventsChips()),
                    ft.Column(controls=eventWidgetsList),
                ],
            ),
        ],
        height=180
    )
