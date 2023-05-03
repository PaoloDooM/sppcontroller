class Executer:
    def __init__(self):
        self.updateActions = None

    def setUpdateActions(self, updateActions):
        self.updateActions = updateActions

    def executeAction(self, button):
        if(self.updateActions != None):
            print(f'last button pressed: {button}')
            self.updateActions(button)
        else:
            print('Error "updateActions(button)" not initialized')

