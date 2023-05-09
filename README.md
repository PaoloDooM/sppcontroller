
<p align="center">
  <img src="https://github.com/PaoloDooM/sppcontroller/blob/main/assets/icons/icon-maskable-512.png?raw=true" alt="favicon.png" width="250" height="250"/>
</p>

<h2>SPPController</h2>

<p>A simple app written in python3 that sends and recieve data from microcontrollers via serial protocol, bluetooth and http.</p>
<p>The main functionality of this app is to write performance stats of the pc to the microcontroller LCD, and to recieve button inputs of the microcontroller to execute tasks or simulate keyboard/mouse inputs in the pc.</p>

<p>Following dependencies are needed:</p>
<ul>
    <li>pip install --user flet</li>
    <li>pip install --user psutil</li>
    <li>pip install --user GPUtil</li>
    <li>pip install --user pyadl</li>
    <li>pip install --user pyserial</li>
    <li>pip install --user pythonnet</li>
    <li>pip install --user dependency-injector</li>
    <li>pip install --user tinydb</li>
    <li>pip install --user pyinstaller</li>
    <li>pip install --user flask</li>
    <li>pip install --user requests</li>
    <li>pip install --user waitress</li>
    <li>pip install --user PyAutoGUI</li>
</ul>

<p>Run command:</p>
<ul>
  <li>flet run .\SPPController.py -d</li>
</ul>

<p>Compile command:</p>
<ul>
  <li>flet pack .\SPPController.py --icon .\assets\icon.ico --hidden-import dependency_injector.errors --hidden-import api.apis</li>
</ul>

<p>This app needs to be launched as administrator to read all performance stats.</p>

<h3>Usage instructions</h3>
<p>To launch programs, batches or shell scripts, you need to add an action with the type "Executable path" and the path to the executable plus arguments, like in this example:</p>
<pre>
  <code>
    C:\Users\PaoloDooM\AppData\Local\Discord\Update.exe --processStart Discord.exe
  </code>
</pre>

<p>To execute user keyboard/mouse inputs you need to add an action with the type "User input" and a json that describes the input sequence, this json must be an array of events, each event must contain a field named "type" that describes the input method, inputs methods can be: click, write, sleep or hotkey. Each type of input method have unique required fields, this example shows the required fields for each case:</p>
<pre>
  <code>
    [
      {"type":"click","x":300, "y":-300},
      {"type":"write","text":"abc123"},
      {"type":"sleep","interval":3},
      {"type":"hotkey","keys":["ctrl","g"]}
    ]
  </code>
</pre>