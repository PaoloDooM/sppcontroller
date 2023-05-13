from enum import Enum
import pytweening
import random

acceptedKeys = ['\t', '\n', '\r', ' ', '!', '"', '#', '$', '%', '&', "'", '(',
                ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~',
                'accept', 'add', 'alt', 'altleft', 'altright', 'apps', 'backspace',
                'browserback', 'browserfavorites', 'browserforward', 'browserhome',
                'browserrefresh', 'browsersearch', 'browserstop', 'capslock', 'clear',
                'convert', 'ctrl', 'ctrlleft', 'ctrlright', 'decimal', 'del', 'delete',
                'divide', 'down', 'end', 'enter', 'esc', 'escape', 'execute', 'f1', 'f10',
                'f11', 'f12', 'f13', 'f14', 'f15', 'f16', 'f17', 'f18', 'f19', 'f2', 'f20',
                'f21', 'f22', 'f23', 'f24', 'f3', 'f4', 'f5', 'f6', 'f7', 'f8', 'f9',
                'final', 'fn', 'hanguel', 'hangul', 'hanja', 'help', 'home', 'insert', 'junja',
                'kana', 'kanji', 'launchapp1', 'launchapp2', 'launchmail',
                'launchmediaselect', 'left', 'modechange', 'multiply', 'nexttrack',
                'nonconvert', 'num0', 'num1', 'num2', 'num3', 'num4', 'num5', 'num6',
                'num7', 'num8', 'num9', 'numlock', 'pagedown', 'pageup', 'pause', 'pgdn',
                'pgup', 'playpause', 'prevtrack', 'print', 'printscreen', 'prntscrn',
                'prtsc', 'prtscr', 'return', 'right', 'scrolllock', 'select', 'separator',
                'shift', 'shiftleft', 'shiftright', 'sleep', 'space', 'stop', 'subtract', 'tab',
                'up', 'volumedown', 'volumemute', 'volumeup', 'win', 'winleft', 'winright', 'yen',
                'command', 'option', 'optionleft', 'optionright']


class MouseButtons(Enum):
    LEFT = 0
    MIDDLE = 1
    RIGHT = 2

    def getLabel(self):
        if self.value == 0:
            return 'left'
        elif self.value == 1:
            return 'middle'
        elif self.value == 2:
            return 'right'


class MouseButtons(Enum):
    LEFT = 0
    MIDDLE = 1
    RIGHT = 2

    def getLabel(self):
        if self.value == 0:
            return 'left'
        elif self.value == 1:
            return 'middle'
        elif self.value == 2:
            return 'right'


class AutomationEventTypes(Enum):
    MOVE_TO = 0
    CLICK = 1
    SCROLL = 2
    MOUSE_DOWN = 3
    MOUSE_UP = 4
    TYPE_WRITE = 5
    HOTKEY = 6
    KEYDOWN = 7
    KEYUP = 8
    LOCATE_AND_CLICK = 9

    def getLabel(self):
        if self.value == 0:
            return 'Move to'
        elif self.value == 1:
            return 'Click'
        elif self.value == 2:
            return 'Scroll'
        elif self.value == 3:
            return 'Click down'
        if self.value == 4:
            return 'Click up'
        elif self.value == 5:
            return 'Write'
        elif self.value == 6:
            return 'Hotkey'
        elif self.value == 7:
            return 'Key down'
        if self.value == 8:
            return 'Key up'
        elif self.value == 9:
            return 'Move to img'

    def getColor(self):
        if self.value == 0:
            return '#FF1493'
        elif self.value == 1:
            return '#9400D3'
        elif self.value == 2:
            return '#B22222'
        elif self.value == 3:
            return '#FF8C00'
        if self.value == 4:
            return '#2E8B57'
        elif self.value == 5:
            return '#006f6f'
        elif self.value == 6:
            return '#00008B'
        elif self.value == 7:
            return '#4B0082'
        if self.value == 8:
            return '#DAA520'
        elif self.value == 9:
            return '#5A5A5A'

    def haveCofidence(self):
        if self.value == 5:
            return True
        return False

    def haveImage(self):
        if self.value == 5:
            return True
        return False

    def haveIndex(self):
        if self.value == 5:
            return True
        return False

    def haveButton(self):
        if self.value == 1:
            return True
        elif self.value == 3:
            return True
        elif self.value == 4:
            return True
        elif self.value == 9:
            return True
        return False

    def haveText(self):
        if self.value == 5:
            return True
        return False

    def haveKeys(self):
        if self.value == 6:
            return True
        return False

    def clicks(self):
        if self.value == 1:
            return True
        elif self.value == 2:
            return True
        elif self.value == 9:
            return True
        return False

    def haveKey(self):
        if self.value == 7:
            return True
        elif self.value == 8:
            return True
        return False

    def haveCoordinates(self):
        if self.value == 0:
            return True
        elif self.value == 1:
            return True
        elif self.value == 2:
            return True
        elif self.value == 3:
            return True
        elif self.value == 4:
            return True
        return False

    def haveDuration(self):
        if self.value == 0:
            return True
        elif self.value == 1:
            return True
        elif self.value == 3:
            return True
        elif self.value == 4:
            return True
        elif self.value == 9:
            return True
        return False

    def haveInterval(self):
        if self.value == 1:
            return True
        elif self.value == 5:
            return True
        elif self.value == 6:
            return True
        elif self.value == 9:
            return True
        return False

    def getRandomMS():
        return random.randint(200, 1000) / 1000

    def getRandomTween():
        tweens = [
            pytweening.easeInQuad,
            pytweening.easeOutQuad,
            pytweening.easeInOutQuad,
            pytweening.easeInCubic,
            pytweening.easeOutCubic,
            pytweening.easeInOutCubic,
            pytweening.easeInQuart,
            pytweening.easeOutQuart,
            pytweening.easeInOutQuart,
            pytweening.easeInQuint,
            pytweening.easeOutQuint,
            pytweening.easeInOutQuint,
            pytweening.easeInSine,
            pytweening.easeOutSine,
            pytweening.easeInOutSine,
            pytweening.easeInExpo,
            pytweening.easeOutExpo,
            pytweening.easeInOutExpo,
            pytweening.easeInCirc,
            pytweening.easeOutCirc,
            pytweening.easeInOutCirc,
            pytweening.easeInElastic,
            pytweening.easeOutElastic,
            pytweening.easeInOutElastic,
            pytweening.easeInBack,
            pytweening.easeOutBack,
            pytweening.easeInOutBack,
            pytweening.easeInBounce,
            pytweening.easeOutBounce,
            pytweening.easeInOutBounce
        ]
        return tweens[random.randint(0, len(tweens)-1)]
