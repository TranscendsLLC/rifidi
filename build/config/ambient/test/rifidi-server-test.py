#!/usr/bin/env python

import os
import re
import time
import unittest
import subprocess

from os.path import join
from telnetlib import Telnet
from subprocess import Popen, PIPE

class RifidiSmokeTest(unittest.TestCase):
    
    def test_install(self):    
        ret = subprocess.call(['test', '-e', '/etc/init.d/rifidi-server'])
        ret1 = subprocess.call(['test', '-d', '/usr/local/sbin/rifidi'])
        ret2 = subprocess.call(['test', '-e', '/usr/local/sbin/rifidi/rifidi-server'])
        assert not ret and not ret1 and not ret2
    
    #    def test_init_script(self):
    #        ret = subprocess.call(['/etc/init.d/rifidi-server', 'stop'])
    #        time.sleep(1.0)
    #        ret = subprocess.call(['/etc/init.d/rifidi-server', 'stop'])
    #        time.sleep(1.0)
    #
    #        p1 = Popen(['ps','ax'], stdout=PIPE)
    #        p2 = Popen(['grep', 'rifidi'], stdin=p1.stdout, stdout=PIPE)
    #        output = p2.communicate()[0]
    #        output = output.split('\n')
    #        for i in output:
    #            if i == '':
    #                continue
    #            elif not re.search('grep', i) and not re.search('python', i):
    #                self.fail()
    #        
    #        ret = subprocess.call(['sudo', '/etc/init.d/rifidi-server', 'start'])        
    #        p1 = Popen(['ps','ax'], stdout=PIPE)
    #        p2 = Popen(['grep', 'rifidi'], stdin=p1.stdout, stdout=PIPE)
    #        output = p2.communicate()[0]
    #        output = output.split('\n')
    #        count = 0
    # #       print output
    ##        time.sleep(20.0)
    #        for i in output:
    #            if not re.search('grep', i) and not re.search('python', i) and not i == '':
    #                count += 1
    #        self.assertEquals(2,count)

    def test_rifidi_server(self):
        self.port = '2020'
        self.host = 'localhost'
        telnet = Telnet(self.host, self.port)
        
        #telnet.write('\r\n')
        #expected = '\r\nosgi> '
        actual = telnet.read_eager()
        #self.assertEqual(expected, actual)
        expected = {}
        expected['Rifidi App: Diagnostic:GPIO (STOPPED)'] = False
        expected['Rifidi App: Diagnostic:Serial (STOPPED)'] = False
        expected['Rifidi App: Diagnostic:Tags (STARTED)'] = False
        expected['Rifidi App: Diagnostic:TagGenerator (STARTED)'] = False
        expected['Rifidi App: AppService:ReadZones (STARTED)'] = False
        expected['Rifidi App: AppService:SensorStatus (STARTED)'] = False
        expected['Rifidi App: AppService:UniqueTagInterval (STARTED)'] = False
        expected['Rifidi App: AppService:StableSet (STARTED)'] = False
        expected['Rifidi App: AppService:UniqueTagBatchInterval (STARTED)'] = False
        expected['Rifidi App: Monitoring:ReadZones (STARTED)'] = False
        expected['Rifidi App: Monitoring:Tags (STARTED)'] = False
        expected['Rifidi App: Monitoring:SensorStatus (STARTED)'] = False
        expected['Rifidi App: Ambient:Association (STARTED)'] = False
        expected['Rifidi App: Ambient:DBApp (STARTED)'] = False
        expected['Rifidi App: Ambient:MessageReceiver (STARTED)'] = False
        expected['Rifidi App: Ambient:SensorDownAlert (STARTED)'] = False
        expected['Rifidi App: Ambient:TagCountApp (STARTED)'] = False
        expected['Rifidi App: Ambient:StationStatusAlert (STARTED)'] = False
        expected['Rifidi App: Ambient:Grouping (STARTED)'] = False
        expected['Rifidi App: Ambient:TagReadApp (STARTED)'] = False
        expected['Rifidi App: Ambient:Receiving (STARTED)'] = False
        expected['Rifidi App: Ambient:Portal (STOPPED)'] = False
        expected['Rifidi App: Ambient:ReceiveTest (STARTED)'] = False
        telnet.write('apps\r\n')
        time.sleep(2.5)
            
        done = False
        while not done:
            value = ''
            value = telnet.read_eager()
            actual += value
            if value == '':
                done = True
                
        actual = actual.replace('osgi>', '')
        actual = re.sub(r'[0-9]+:', '', actual, count=0)
        actual = actual.split('\r\n')
        for i in range(0, actual.__len__()-1):
            actual[i] = re.sub(r'^ ', '', actual[i])
        
        for i in actual:
            if i in expected:
                expected[i] = True
                
        for i in expected.keys():
            self.assertTrue(i)
            
if __name__ == '__main__':
    unittest.main() 