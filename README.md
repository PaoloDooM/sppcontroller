
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
</ul>

<p>Run command:</p>
<ul>
  <li>flet run .\main.py</li>
</ul>

<p>Compile command:</p>
<ul>
  <li>flet pack .\main.py --icon .\assets\icon.ico</li>
</ul>

<p>This app needs to be launched as administrator to read all performance stats.</p>