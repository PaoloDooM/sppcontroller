from flask import Flask, Response, request
from waitress import serve
import threading
from dependency_injector.wiring import Provide, inject
from containers.Container import *
from flask_jwt_extended import JWTManager, create_access_token, jwt_required
import uuid


api = Flask(__name__)
api.config["JWT_SECRET_KEY"] = str(uuid.uuid4())
JWTManager(api)


def flaskStart(api, port):
    print(f"Starting server on port: {port}")
    serve(api, host="0.0.0.0", port=port)


flaskThread = threading.Thread(target=flaskStart, args=[
                               api, 53000], daemon=True)
flaskThread.start()


@api.route('/buttons', methods=['POST'])
@jwt_required()
@inject
def registerAction(actionsService: ActionsService = Provide[Container.actionsService]):
    if "btn" in request.json:
        actionsService.registerButtonEvent(request.json['btn'])
        return Response(response='Acknowledged', content_type="text/plain", status=200)
    print('Bad params on HTTP \"/buttons\"')
    return Response(response='Bad params', content_type="text/plain", status=400)


@api.route('/authenticate', methods=['POST'])
@inject
def authenticate(actionsService: ActionsService = Provide[Container.actionsService]):
    if not "password" in request.json or not "username" in request.json:
        print('Bad params on HTTP connection')
        return Response(response='Bad params', content_type="text/plain", status=400)
    elif (actionsService.clientPassword == request.json["password"] and request.json["username"] == "sppcontroller"):
        print('HTTP connection authenticated')
        return Response(response=create_access_token(request.json["username"]), content_type="text/plain", status=200)
    print('Failed authentication on HTTP connection')
    return Response(response='Unauthorized', content_type="text/plain", status=401)
