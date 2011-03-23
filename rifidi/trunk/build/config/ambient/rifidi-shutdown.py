#!/usr/bin/env python

import os
import sys
import re
from os.path import join
import subprocess
from subprocess import Popen, PIPE
from telnetlib import Telnet

if __name__ == '__main__':

    skipgrep=1

    # try a nice shutdown
    try :
        telnet = Telnet('localhost', '2020')
        actual = telnet.read_eager()
        telnet.write('close\r\n')
    except:
        pass

    # kill anything that is left
    p1 = Popen(['ps','ax'], stdout=PIPE)
    p2 = Popen(['grep', 'rifidi'], stdin=p1.stdout, stdout=PIPE)
    output = p2.communicate()[0]
    output = output.split('\n')
    #print('output: ',output,'\n')
    if output:
        for i in output:
            #print('i: ',i,'\n')
            if (skipgrep==1):
                skipgrep=0
                #continue
            else:
                pid = i.strip().split(' ')
                assert pid.__len__() > 1
                #print pid[1]
                out = os.system('sudo kill -9 '+ str(pid[0]))
                #print out
