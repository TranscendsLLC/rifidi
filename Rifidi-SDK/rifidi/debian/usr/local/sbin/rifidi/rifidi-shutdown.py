#!/usr/bin/env python
 
import os
import sys
import re
import time
import signal
from os.path import join
import subprocess
from subprocess import Popen, PIPE
from telnetlib import Telnet
 
if __name__ == '__main__':
    
    try :
        r1 = os.popen('curl -X POST http://localhost:8111/shutdown').read()
        if "Success" in r1:
            print('Rifidi has successfully shutdown.')
    except Exception as e:
        print('Exception when shutting down server: ' + str(e))
 