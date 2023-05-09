from flask import Flask, Response, request
from waitress import serve
import threading
from dependency_injector.wiring import Provide, inject
from containers.Container import *


api = Flask(__name__)
def flaskStart(api, port):
    print(f"Starting server on port: {port}")
    serve(api, host="0.0.0.0", port=port)
flaskThread = threading.Thread(target=flaskStart, args=[api, 53000], daemon=True)
flaskThread.start()

@api.route('/buttons', methods=['POST'])
@inject
def registerAction(actionsService: ActionsService = Provide[Container.actionsService]):
    btn = request.json['btn']
    actionsService.registerButtonEvent(btn)
    return Response(response='Acknowledged', content_type="text/plain", status=200)

@api.route('/authenticate', methods=['POST'])
def authenticate():
    if ("9DTUdnKN5z4jPVaKYAdBjX7C" == request.json["password"] and request.json["username"] == "sppcontroller"):
        print(f'HTTP connection authenticated')
        return Response(response='Bearer TODO', content_type="text/plain", status=200)
    else:
        print(f'Failed authentication on HTTP connection')
        return Response(response='Unauthorized', content_type="text/plain", status=401)