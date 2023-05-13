import flet as ft

draggableTarget = ft.Icon(ft.icons.CHEVRON_RIGHT, size=10, color='#9E9E9E')
emptyListTarget = ft.Text("Drag an element here!")

class DraggableData:
    def __init__(self, index, isRight):
        self.index = index
        self.isRight = isRight

def createDraggableTargets(page: ft.Page, isEmpty: bool, data: DraggableData, insertEvent):
    def drag_will_accept(e):
        src = page.get_control(e.src_id)
        if e.control.data.index != src.data.index:
            e.control.content = src.content_feedback
            e.control.update()

    def drag_accept(e: ft.DragTargetAcceptEvent):
        src = page.get_control(e.src_id)
        if e.control.data.index != src.data.index:
            insertEvent(src.data, e.control.data)
        page.update()

    def drag_leave(e):
        e.control.content = draggableTarget
        e.control.update()

    return ft.DragTarget(
        group="events",
        content=draggableTarget if not isEmpty else emptyListTarget,
        on_will_accept=drag_will_accept,
        on_accept=drag_accept,
        on_leave=drag_leave,
        data=data
    )