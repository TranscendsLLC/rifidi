#!/usr/bin/env python

import os
from os.path import join
from telnetlib import Telnet

if __name__ == '__main__':
    telnet = Telnet('localhost', '2020')
    actual = telnet.read_eager()
    telnet.write('close\r\n')
    