import flet as ft

draggableTarget = ft.Icon(ft.icons.ARROW_RIGHT, size=40, color='#9E9E9E')
emptyListTarget = ft.Text("Drag an element here!",
                          weight=ft.FontWeight.BOLD, color='#9E9E9E')


def createDraggableTargets(page: ft.Page, isEmpty: bool, data: int, insertEvent):
    def drag_will_accept(e):
        e.control.content.color = '#5e5e5e'
        e.control.update()

    def drag_accept(e: ft.DragTargetAcceptEvent):
        src = page.get_control(e.src_id)
        if e.control.data != src.data.index:
            insertEvent(src.data, e.control.data)
        e.control.content.color = '#9E9E9E'
        e.control.update()

    def drag_leave(e):
        e.control.content.color = '#9E9E9E'
        e.control.update()

    return ft.DragTarget(
        group="events",
        content=draggableTarget if not isEmpty else emptyListTarget,
        on_will_accept=drag_will_accept,
        on_accept=drag_accept,
        on_leave=drag_leave,
        data=data
    )
