import subprocess

def execute(action: str):
    try:
        subprocess.call(action)
    except Exception as e:
        print(f"{e}")
        raise Exception(f"Error calling executable")
