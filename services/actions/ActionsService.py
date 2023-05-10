from models.ActionTypes import ActionTypes
from persistence.Persistence import Persistence
from services.actions.Executer import *
from services.actions.Robot import *
from pages.utils import *


class ActionsService:
    def __init__(self):
        self.buttonEventCallbacks = []
        self.actions = []
        self.page = None
        self.clientPassword = None

    def setClientPassword(self, password):
        self.clientPassword = password

    def setPage(self, page):
        self.page = page

    def setActions(self, actions):
        self.actions = actions

    def addButtonEventCallback(self, callback):
        self.buttonEventCallbacks.append(callback)

    def registerButtonEvent(self, button):
        print(f'last button pressed: {button}')

        try:
            for action in self.actions:
                if action.button == button:
                    if (action.type == ActionTypes.EXECUTABLE_PATH):
                        execute(action=action.data)
                    elif (action.type == ActionTypes.USER_INPUT):
                        automate(action=action.data)
        except Exception as e:
            if (self.page != None):
                displayErrorSnackBar(page=self.page, error=f"{e}")
            else:
                print(f"{e}")

        for callback in self.buttonEventCallbacks:
            callback(button)
