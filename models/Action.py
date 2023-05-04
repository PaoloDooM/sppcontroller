class Action:
    def __init__(self, button, type, data):
        self.button = button
        self.type = type
        self.data = data

    def toJson(self):
        return {'button': self.button, 'type': self.type, 'data': self.data}
