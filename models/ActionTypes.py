from enum import Enum

class ActionTypes(Enum):
    EXECUTABLE_PATH = 0
    USER_INPUT = 1

    def label(self):
        if self.value == 0:
            return "Executable path"
        elif self.value == 1:
            return "User input"