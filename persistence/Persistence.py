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
        if button == None:
            return self.db.all()
        else:
            actionQuery = Query()
            return self.db.search(actionQuery.button == button)