import socket
from flask import Flask, request
import threading

def flaskStart():
    api.run(port=53000)

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.settimeout(0)
    try:
        # doesn't even have to be reachable
        s.connect(('10.254.254.254', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP
    
print(f'SPPController IP:  {get_ip()}')

api = Flask(__name__)

@api.route('/buttons', methods=['POST'])
def registerAction():
  btn = request.json['btn']
  print(f'last button pressed: {btn}')
  return 'Acknowledged'

flaskThread = threading.Thread(target=flaskStart, args=(), daemon=True)
flaskThread.start()