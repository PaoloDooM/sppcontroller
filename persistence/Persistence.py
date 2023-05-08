from tinydb import TinyDB, Query
from tinydb.operations import delete
from models.Action import *

class Persistence:
    def __init__(self, db: TinyDB):
        self.db = db

    def deleteAction(self, button):
        actionQuery = Query()
        self.db.remove(actionQuery.button == button)

    def upsertAction(self, action: Action):
        actionQuery = Query()
        self.db.upsert(action.toJson(), actionQuery.button == action.button)

    def getActions(self, button = None):
        result = []
        if button == None:
            result = self.db.all()
        else:
            actionQuery = Query()
            result = self.db.search(actionQuery.button == button)
        actions = []
        for action in result:
            actions.append(Action(button=action['button'], type=list(ActionTypes)[int(action['type'])], data=action['data']))
        return actions