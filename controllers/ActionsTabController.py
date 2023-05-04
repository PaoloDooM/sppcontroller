class ActionsTabController:
    def __init__(self):
        self.changeActionsTab = None

    def setChangeActionsTab(self, changeActionsTab):
        self.changeActionsTab = changeActionsTab

    def setListView(self, value):
        if self.changeActionsTab == None:
            print('Error "changeActionsTab(listView)" not initialized')
        else:
            self.changeActionsTab(value)