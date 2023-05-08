from models.ActionTypes import ActionTypes


class Action:
    def __init__(self, button, type: ActionTypes, data):
        self.button = button
        self.type = type
        self.data = data

    def toJson(self):
        return {'button': self.button, 'type': int(self.type), 'data': self.data}
