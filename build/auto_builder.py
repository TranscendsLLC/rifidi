#!/usr/bin/env python

import os
import subprocess

import manifest
import manifest_parser

from optparse import OptionParser
from properties import *
from os.path import join, getsize

class Factory:
    def __init__(self, options):
        self.options = options
        #vlist = self.options.num_variables.split(',')
        #pass
        #clist = self.options.num_clauses.split(',')
        #nl = self.options.num_literals
        #nv = self.options.num_variables
        #nc = self.options.num_clauses
    def create_command():
        pass
    
class Parameters:
    def __init__(self):
        self.args = None
        self.options = None
        self.parser = OptionParser()
        self.__configure_parser__()
        self.__parse_parameters__()
#        assert self.options != None
    def __configure_parser__(self):
        display_jars_help = 'Displays the Java archive files found'
#        #Nhelp = 'Bounding number of variables; default value is 4'
#        #Lhelp = 'Number of claueses; default value is 4'
#        #fhelp = 'Number of forumlas per experiment; default value is 10'
#        #ehelp = 'Experiment.  Determines with experiment to run; supported '\
#        #        +'values are: sat and f-sat; default is: sat.  '
        verbose_help = 'Verbose.  Prints additional information for some commands.'
        self.parser.add_option("--display-jars", action="store_true",
                                default=False, dest="display_jars",
                                help=display_jars_help)
#        #                       dest="num_literals", default=3, type=int, help=lhelp)
#        #self.parser.add_option("-N", "--variable-range",
#        #                       dest="num_variables", metavar="VARIABLES", type=str,
#        #                       default='1,5', help=Nhelp)
#        #self.parser.add_option("-L", "--clause-range", dest="num_clauses", metavar="CLAUSES",
#        #                  type=str, default='1,5', help=Nhelp)
#        #self.parser.add_option("-f", "--num-formulas", dest="num_formulas", default=10, type=int,
#        #                  help=fhelp)
#        #self.parser.add_option("-e", "--experiement", dest="command",
#        #                  metavar="COMMAND", default='sat', type=str,
#        #                  help=ehelp)
#        #self.parser.add_option("-v", "--verbose", action="store_true", default=False,
#        #                  dest="verbose", help=vhelp)
#        
    def __parse_parameters__(self):
        (self.options, self.args) = self.parser.parse_args()
        #assert self.options.command == 'sat' or self.options.command == 'f-sat'
        

 
class Sources:
    def __init__(self):
        pass

class Jar:
    def __init__(self, static):
        self.static = static   


class Jars:
    def __init__(self):
        self.jar_files = []
        self.bundles = []
        
    def load(self):
        for root, file in self.jar_files:
            ret = subprocess.call(['cp', join(root, file), '/tmp'])
                
        os.chdir('/tmp')
       
        for root, file in self.jar_files:
    
            ret = subprocess.call(['jar', 'xf', file, 'META-INF/MANIFEST.MF'])
            assert ret == 0
            
            manifest_des = open('META-INF/MANIFEST.MF', 'r')
            manifest_file = manifest_des.read()
    
            parser = manifest.ManifestParser()
            bundle = parser.parse(manifest_file)
            bundle.root = root
            bundle.file = file
            self.bundles.append(bundle)
            
    def display(self):
        
        print '-'*80
        for i in self.bundles:
            i.display()
            print '-'*80
        
    def find(self, jar_path):
        for i in jar_path:
            #print 'jar_path: ', i
            for root, dirs, files in os.walk(i):
                #print 'root, dirs, files: ', root, dirs, files                 
                #print root, "consumes",
                #print sum(getsize(join(root, name)) for name in files),
                #print "bytes in", len(files), "non-directory files"
                for file in files:
                    if file.endswith(r'.jar'):
                        self.jar_files.append((root, file))
        #print self.jar_files
                        
def load_jars():
    jfinder = Jars()
    #print 'jar path', jar_path
    jfinder.find(jar_path)
    jfinder.load()
    jfinder.display()    
    
def load_sources():
    sfinder = Sources()
    sfinder.find(src_path)
    sfinder.load()
    sfinder.display()
    
if __name__ == '__main__':
#    print jar_path, src_path
    params = Parameters()
    if params.options.display_jars == True:
        load_jars()
    elif params.options.display_sources == True:
        load_sources()

    else:
        print 'auto_builder.py: Usage: auto_builder.py <command>'
