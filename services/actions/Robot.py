import time
import pyautogui
import json
from models.AutomationEventTypes import *


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
                pyautogui.hotkey(
                    *event["keys"], interval=float(event['interval']) if 'interval' in event else 0.0)
            elif event['type'] == 'click':
                currentPos: pyautogui.Point = pyautogui.position()
                maxPos: pyautogui.Size = pyautogui.size()
                pyautogui.moveTo(
                    x=positionSanitizer(current=currentPos.x, to=int(event["x"]), max=maxPos.width), y=positionSanitizer(current=currentPos.y, to=int(event["y"]), max=maxPos.height), duration=float(event['interval']) if 'interval' in event else 0.0, tween=pyautogui.easeInElastic)
                pyautogui.click()
            elif event['type'] == 'write':
                pyautogui.write(event['text'], interval=float(
                    event['interval']) if 'interval' in event else 0.0)
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
            if 'interval' in event:
                try:
                    float(event['interval'])
                except:
                    errors.append(
                        f'interval value must be numeric for event \"{event["type"]}\" on index: {i}')
            if not 'keys' in event:
                errors.append(
                    f'keys value missing for event \"{event["type"]}\" on index: {i}')
            elif type(event["keys"]) != list:
                errors.append(
                    f'keys must be a list for event \"{event["type"]}\" on index: {i}')
            elif len(event['keys']) == 0:
                errors.append(
                    f'keys must not be empty for event \"{event["type"]}\" on index: {i}')
            else:
                for j, key in enumerate(event["keys"]):
                    try:
                        acceptedKeys.index(key)
                    except:
                        errors.append(
                            f'Non accepted key in index {j} for event \"{event["type"]}\" on index: {i}')
        elif event['type'] == 'click':
            if 'interval' in event:
                try:
                    float(event['interval'])
                except:
                    errors.append(
                        f'interval value must be numeric for event \"{event["type"]}\" on index: {i}')
            if not "x" in event:
                errors.append(
                    f'x value missing for event \"{event["type"]}\" on index: {i}')
            else:
                try:
                    int(event["x"])
                except:
                    errors.append(
                        f'x value must be a integer for event \"{event["type"]}\" on index: {i}')
            if not "y" in event:
                errors.append(
                    f'y value missing for event \"{event["type"]}\" on index: {i}')
            else:
                try:
                    int(event["y"])
                except:
                    errors.append(
                        f'y value must be a integer for event \"{event["type"]}\" on index: {i}')
        elif event['type'] == 'write':
            if 'interval' in event:
                try:
                    float(event['interval'])
                except:
                    errors.append(
                        f'interval value must be numeric for event \"{event["type"]}\" on index: {i}')
            if not 'text' in event:
                errors.append(
                    f'text value missing for event \"{event["type"]}\" on index: {i}')
            elif type(event['text']) != str:
                errors.append(
                    f'text value must be a String for event \"{event["type"]}\" on index: {i}')
        elif event['type'] == 'sleep':
            if not 'interval' in event:
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
