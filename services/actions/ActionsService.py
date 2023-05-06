class ActionsService:
    def __init__(self):
        self.buttonEventCallbacks = []

    def addButtonEventCallback(self, callback):
        self.buttonEventCallbacks.append(callback)

    def registerButtonEvent(self, button):
        print(f'last button pressed: {button}')
        for callback in self.buttonEventCallbacks:
            callback(button)