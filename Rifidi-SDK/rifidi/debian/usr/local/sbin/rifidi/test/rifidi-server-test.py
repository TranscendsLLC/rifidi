#!/usr/bin/env python

import subprocess
import unittest
import time
import os

from os.path import join
from telnetlib import Telnet

class RifidiSmokeTest(unittest.TestCase):
    
    def test_install(self):    
        ret = subprocess.call(['test', '-e', '/etc/init.d/rifidi-server'])
        ret1 = subprocess.call(['test', '-d', '/usr/local/sbin/rifidi'])
        ret2 = subprocess.call(['test', '-e', '/usr/local/sbin/rifidi/rifidi-server'])
        assert not ret and not ret1 and not ret2
    
    def test_init_script(self):
        #print 'here'
        #ret = subprocess.call(['sudo', '/etc/init.d/rifidi-server', 'stop'])
        #assert not ret
        pass
    
    def test_rifidi_server(self):
        
        ret = subprocess.call(['sudo', '/etc/init.d/rifidi-server', 'stop'])
        ret1 = subprocess.call(['sudo', '/etc/init.d/rifidi-server', 'start'])
        assert not ret and not ret1
        
        self.port = '2020'
        self.host = 'localhost'
        telnet = Telnet(self.host, self.port)
        
        #telnet.write('\r\n')
        expected = '\r\nosgi> '
        actual = telnet.read_eager()
        self.assertEqual(expected, actual)
        
        expected = '\r\nosgi> 0:Rifidi App: Diagnostic:GPIO (STOPPED)\r\n'+\
                    '1:Rifidi App: Diagnostic:Serial (STOPPED)\r\n'+\
                    '2:Rifidi App: Diagnostic:Tags (STARTED)\r\n'+\
                    '3:Rifidi App: Diagnostic:TagGenerator (STARTED)\r\n'+\
                    '4:Rifidi App: AppService:ReadZones (STARTED)\r\n'+\
                    '5:Rifidi App: AppService:SensorStatus (STARTED)\r\n'+\
                    '6:Rifidi App: AppService:UniqueTagInterval (STARTED)\r\n'+\
                    '7:Rifidi App: AppService:StableSet (STARTED)\r\n'+\
                    '8:Rifidi App: AppService:UniqueTagBatchInterval (STARTED)\r\n'+\
                    '9:Rifidi App: Monitoring:ReadZones (STARTED)\r\n'+\
                    '10:Rifidi App: Monitoring:Tags (STARTED)\r\n'+\
                    '11:Rifidi App: Monitoring:SensorStatus (STARTED)\r\n'+\
                    'osgi> '
                        
        telnet.write('apps\r\n')
        time.sleep(2.5)
            
        done = False
        while not done:
            value = ''
            value = telnet.read_eager()
            actual += value
            if value == '':
                done = True
        self.assertEqual(expected, actual)
            
        #print '--->'+actual+'<---'
            
            
if __name__ == '__main__':
    unittest.main() 