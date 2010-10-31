#!/usr/bin/env python

import os
import subprocess

import manifest
import manifest_parser

from optparse import OptionParser
from properties import *
from os.path import join, getsize
    
class Parameters:
    def __init__(self):
        self.args = None
        self.options = None
        self.parser = OptionParser(version="%prog 1.3.37")
        display_jars_help = 'Display Java archives'
        display_src_help = 'Display sources'
        display_dep_help = 'Display dependencies'
        check_dep_help = 'Check dependencies'
        gen_build_help = 'Generate build artifacts; if no options are given, '\
                         'then this command is executed; supported values'\
                         ' are: ant, make, maven; default value is: ant.'
        self.parser.add_option("-j", "--display-jars", action="store_true",
                                default=False, dest="display_jars",
                                help=display_jars_help)
        self.parser.add_option("-s", "--display-src", action="store_true",
                                default=False, dest="display_src",
                                help=display_src_help)
        self.parser.add_option("-d", "--display-dep", action="store_true",
                               default=False, dest="display_dep",
                               help=display_dep_help)
        self.parser.add_option("-c", "--check-dep", action="store_true",
                               default=False, dest="check_dep",
                               help=check_dep_help)
        self.parser.add_option("-g", "--gen-build",
                      dest="gen_build", metavar="BUILD-TYPE", type=str,
                      default='ant', help=gen_build_help)
                        
        (self.options, self.args) = self.parser.parse_args()
            
         
class Src:
    def __init__(self):
        self.src_manifests = []
        self.src_files = []
        self.bundles = []
        
    def load(self):
        for root, dir in self.src_manifests:
            #print join(root, dir, 'MANIFEST.MF')
            manifest_des = open(join(root, dir, 'MANIFEST.MF'), 'r')
            manifest_file = manifest_des.read()
            #print manifest_file
            parser = manifest.ManifestParser()
            bundle = parser.parse(manifest_file)
            bundle.root = root
            self.bundles.append(bundle)
            
    def display(self):
        for i in self.bundles:
            i.display()
        print '-'*80
        
    def find(self, src_path):
        for i in src_path:
            for root, dirs, files in os.walk(i):
                for dir in dirs:
                    if dir == 'META-INF':
                        self.src_manifests.append((root, dir))    
                        
                    
class Jars:
    def __init__(self):
        self.jar_files = []
        self.bundles = []
        
    def load(self):
        for root, file in self.jar_files:
            ret = subprocess.call(['cp', join(root, file), '/tmp'])
        cdir = os.getcwd()
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
            bundle.jar = True
            self.bundles.append(bundle)
        os.chdir(cdir)
            
    def display(self):
        for i in self.bundles:
            i.display()
            print '-'*80
        
    def find(self, jar_path):
        for i in jar_path:
            #print 'jar_path: ', i
            for root, dirs, files in os.walk(i):
                for file in files:
                    if file.endswith(r'.jar'):
                        self.jar_files.append((root, file))


class Dep:
    def __init__(self, jars, src):
        self.jars = jars
        self.src = src
        self.exports = {}
        slef.bundles = {}
        
    def __add_package__(packages, package, bundle):
        #package.name -> [(package, bundle), (package, bundle)]
        if package in packages:
            inserted = False
            for pentry, bentry in packages[package]:
                index = packages[package].index((pentry, bentry))
                assert index >= 0 and index <= len(package[package])
                if package.isEqual(pentry):
                    if bundle.jar == True:
                        packages[package].insert(index, (package, bundle))
                    else:
                        packages[package].insert(index+1, (package, bundle))
                    inserted = True
                    break
                elif package.isLess(pentry):
                    packages[package].insert(index, (package, bundle))
                    inserted = True
                    break
                
            if inserted == False:
                packages[package].append((package, bundle))
                    
        else:
            packages[package] = [(package, bundle)]
            
    def generate_deps(jars, src):
        exports = {}
        bundles = {}
        for bundle in src.bundles:
            bundles[bundle.sym_name] = bundle
            for package in bundle.epackages:
                self.add_package(exports, package, bundle)
                
        for bundle in jars.bundles:
            bundles[bundle.sym_name] = bundle
            print bundle.display()
            for package in bundle.epackages:
                self.add_package(exports, package, bundle)
                
        self.exports = exports
        self.bundles = bundles
        # package.name = [(pacakge, bundle), (package, bundle)]
        for bundle in src.bundles:
            pass
            
    def check_deps(jars, src):
        exports, bundles = generate_deps(jars, src)
        for i in src.bundles:
            pass
        
        print exports

def load_jars():
    jfinder = Jars()
    jfinder.find(jar_path)
    jfinder.load()
    return jfinder
    
def load_src():
    sfinder = Src()
    sfinder.find(src_path)
    sfinder.load()
    return sfinder
    
if __name__ == '__main__':
#    print jar_path, src_path
    jars = None
    src = None
    params = Parameters()

    if params.options.display_jars == True:
        jars = load_jars()
        jars.display()
 
    if params.options.display_src == True:
        src = load_src()
        src.display()
        
    if params.options.check_dep == True:
        if jars == None:
            jars = load_jars()
       
        if src == None:
            src = load_src()
        
        jars.display()
        src.display()
        
        
        deps = generate_deps(jars, src)
        
    if params.options.display_dep == True:
        assert False
        assert False
        
    if params.options.gen_build == True:
        assert False
        
    #else:
    #    params.parser.print_version()
    #    params.parser.print_help()