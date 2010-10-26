#!/usr/bin/env python

import os
import manifest
import subprocess

from optparse import OptionParser
from properties import *
from os.path import join, getsize

class AutoBuilderFactory:
    def __init__(self, options):
        self.options = options
        vlist = self.options.num_variables.split(',')
        pass
        clist = self.options.num_clauses.split(',')
        nl = self.options.num_literals
        #nv = self.options.num_variables
        #nc = self.options.num_clauses

class AutoBuildParameterParser:
    def __init__(self):
        self.args = None
        self.options = None
        self.parser = OptionParser()
        self.__configure_parser__()
        self.__parse_parameters__()
        assert self.options != None
        
    def __configure_parser__(self):
        pass
        #lhelp = 'Number of literals per clause bound; Default is 3'
        #Nhelp = 'Bounding number of variables; default value is 4'
        #Lhelp = 'Number of claueses; default value is 4'
        #fhelp = 'Number of forumlas per experiment; default value is 10'
        #ehelp = 'Experiment.  Determines with experiment to run; supported '\
        #        +'values are: sat and f-sat; default is: sat.  '
        #vhelp = 'Verbose.  Prints additional information for some commands.'
        #self.parser.add_option("-l", "--literals-per-clause-bound",
        #                       dest="num_literals", default=3, type=int, help=lhelp)
        #self.parser.add_option("-N", "--variable-range",
        #                       dest="num_variables", metavar="VARIABLES", type=str,
        #                       default='1,5', help=Nhelp)
        #self.parser.add_option("-L", "--clause-range", dest="num_clauses", metavar="CLAUSES",
        #                  type=str, default='1,5', help=Nhelp)
        #self.parser.add_option("-f", "--num-formulas", dest="num_formulas", default=10, type=int,
        #                  help=fhelp)
        #self.parser.add_option("-e", "--experiement", dest="command",
        #                  metavar="COMMAND", default='sat', type=str,
        #                  help=ehelp)
        #self.parser.add_option("-v", "--verbose", action="store_true", default=False,
        #                  dest="verbose", help=vhelp)
        
    def __parse_parameters__(self):
        (self.options, self.args) = self.parser.parse_args()
        #assert self.options.command == 'sat' or self.options.command == 'f-sat'
    def get_options(self):
        pass
        #assert self.options != None
        #return self.options


 
class Sources:
    def __init__(self):
        pass

class Jar:
    def __init__(self, static):
        self.static = static   


class Jars:
    def __init__(self):
        self.jars = []
        
    def __parse_manifest__(self, manifest_string):
        parser = manifest.ManifestParser()
        ast = manifest.abstract_syntax_tree()
        parser.parse(manifest_string, ast)
    
    def __extract_manifest__(self, root, file):
        ret = subprocess.call(['cp', join(root, file), '/tmp'])
        assert ret == 0
        os.chdir('/tmp')
        ret = subprocess.call(['jar', 'xf', file, 'META-INF/MANIFEST.MF'])
        f = open('META-INF/MANIFEST.MF', 'r')
        manifest_file = f.read()
        #print manifest_file
        #self.proc_object = Popen(self.args, stdout = self.output, stderr = self.output)
        #self.pid = self.proc_object.pid
        #time.sleep(1.0)
        #self.lock.notify()
        #self.lock.release()
        #self.exit_code = self.proc_object.wait()
        
        manifest = 'Import-Package: com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";'+\
            'resolution:=optional,javax.jms;version="[1.1.0, 2.0.0)";'+\
            'resolution:=optional,javax.mail;version="[1.4.0, 2.0.0)";'+\
            'resolution:=optional,javax.mail.internet;version="[1.4.0, 2.0.0)";'+\
            'resolution:=optional,javax.management,javax.naming,'+\
            'javax.swing,javax.swing.border,javax.swing.event,'+\
            'javax.swing.table,javax.swing.text,javax.swing.tree,'+\
            'javax.xml.parsers,org.w3c.dom,org.xml.sax,org.xml.sax.helpers'
        return manifest_file

        def map(self):
            pass
        
    def load(self):
        for i in self.jars:
            self.__parse_manifest__(self.__extract_manifest__(i[0], i[1]))
            
    def find_all(self, libs):
        for i in libs:
            for root, dirs, files in os.walk(i):
                for j in dirs:
                    self.find_all(j)
 
                #print root, "consumes",
                #print sum(getsize(join(root, name)) for name in files),
                #print "bytes in", len(files), "non-directory files"
                for file in files:
                    if file.endswith(r'.jar'):
                        self.jars.append((root, file))
                    
        #for i in libraries:
        #    print i
        #    os.chdir(i)
        #    os.
        #    self.proc_object = Popen(self.args, stdout = self.output, stderr = self.output)
        #self.pid = self.proc_object.pid
        #time.sleep(1.0)
        #self.lock.notify()
        #self.lock.release()
        #self.exit_code = self.proc_object.wait()
        #self.lock.acquire()
        #jar xf com.springsource.org.apache.log4j-1.2.15.jar  META-INF/MANIFEST.MF
        #pass

        
if __name__ == '__main__':
    print jar_path, src_path
    #parser = RandomSatParameterParser()
    #rsat_factory = RandomSatFactory(parser.get_options())
    jar_finder = Jars()
    jar_finder.find_all(jar_path)
    jar_finder.load()
    
