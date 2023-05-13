from models.AutomationEventTypes import *

class UserInputEvent:
    def __init__(self, index: int, eventType: AutomationEventTypes, attributes):
        self.index = index
        self.eventType = eventType
        self.attributes = attributes