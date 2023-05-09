import time
import pyautogui
import json


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


def positionSanitizer(current, to, max):
    res = current + to
    if res < 0:
        return 0
    elif res > max:
        return max
    return res


def automate(action):
    try:
        events = json.loads(action)
        for event in events:
            if event['type'] == 'hotkey':
                pyautogui.hotkey(*event["keys"])
            elif event['type'] == 'click':
                currentPos: pyautogui.Point = pyautogui.position()
                maxPos: pyautogui.Size = pyautogui.size()
                pyautogui.moveTo(
                    x=positionSanitizer(current=currentPos.x, to=int(event["x"]), max=maxPos.width), y=positionSanitizer(current=currentPos.y, to=int(event["y"]), max=maxPos.height))
                pyautogui.click()
            elif event['type'] == 'write':
                pyautogui.write(event['text'])
            elif event['type'] == 'sleep':
                time.sleep(float(event['interval']))
    except Exception as e:
        print(f"{e}")
        raise Exception(f"Error on user input automation")


def automationVerify(action):
    try:
        events = json.loads(action)
    except Exception as e:
        raise Exception(f"Error decoding json: {e}")

    if type(events) != list:
        raise Exception('Action must be a list of jsons events')

    errors = []

    for i, event in enumerate(events):
        if event['type'] == 'hotkey':
            if (event["keys"] == None):
                errors.append(
                    f'keys value missing for event \"{event["type"]}\" on index: {i}')
            elif type(event["keys"]) != list:
                errors.append(
                    f'keys must be a list for event \"{event["type"]}\" on index: {i}')
            else:
                for j, key in enumerate(event["keys"]):
                    try:
                        acceptedKeys.index(key)
                    except:
                        errors.append(
                            f'Non accepted key in index {j} for event \"{event["type"]}\" on index: {i}')
        elif event['type'] == 'click':
            if event["x"] == None:
                errors.append(
                    f'x value missing for event \"{event["type"]}\" on index: {i}')
            else:
                try:
                    int(event["x"])
                except:
                    errors.append(
                        f'x value must be a integer for event \"{event["type"]}\" on index: {i}')
            if event["y"] == None:
                errors.append(
                    f'y value missing for event \"{event["type"]}\" on index: {i}')
            else:
                try:
                    int(event["y"])
                except:
                    errors.append(
                        f'y value must be a integer for event \"{event["type"]}\" on index: {i}')
        elif event['type'] == 'write':
            if event['text'] == None:
                errors.append(
                    f'text value missing for event \"{event["type"]}\" on index: {i}')
            elif type(event['text']) != str:
                errors.append(
                    f'text value must be a String for event \"{event["type"]}\" on index: {i}')
        elif event['type'] == 'sleep':
            if event['interval'] == None:
                errors.append(
                    f'interval value missing for event \"{event["type"]}\" on index: {i}')
            else:
                try:
                    float(event['interval'])
                except:
                    errors.append(
                        f'interval value must be numeric for event \"{event["type"]}\" on index: {i}')
        else:
            errors.append(f'Unsuported event \"{event["type"]}\" on index {i}')

        if (len(errors) != 0):
            raise Exception("\n".join(errors))
