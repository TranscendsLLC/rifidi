#!/usr/bin/env python

import os
import re
import subprocess
from os.path import join, abspath

launchers = ['../Rifidi-SDK/eclipse-libs/plugins/']

lib = 'lib/'
deploy = 'deploy1/'
sources = ['export/linux.gtk.x86_64/']
configurations = ['ambient-edge']

class Builder:
    def __init__(self, launchers, sources, configurations, lib, deploy):
        self.launchers = launchers
        self.sources = sources
        self.configurations = configurations
        self.lib = lib
        self.deploy = deploy
        
    def copy_binaries(self, root, file, type):
        print 'binaries'
        if type == 'dir':
            self.cdir = join(self.lib, file)
            ret = subprocess.call(['mkdir', self.cdir])
            assert ret == 0
        elif type == 'file':
            ret = subprocess.call(['cp', join(root, file), join(self.cdir, file)])
            print 'file'
        else:
            assert False
            
    def copy_configs(self, root, file, type):
        print 'configs'
        if type == 'dir':
            print 'dir'
        elif type == 'file':
            print 'file'    

    def copy(self, source, handler):
        for path in source:
            for root, dirs, files in os.walk(path):
                for dir in dirs:
                    if re.search('.svn', join(root, dir)):
                        continue
                    else:
                        print join(root,dir)
                        handler(root, dir, 'dir')                    
                for file in files:
                    if re.search('.svn', join(root, file)):
                        continue
                    else:
                        print join(root, file)
                        handler(root, file, 'file')

                        
                        
    def doit(self):
        self.copy(launchers, self.copy_binaries)
        #self.copy(sources, self.copy_configs)
        
    def load(self):
        for root, dir, libs in self.src_manifests:
            #print join(root, dir, 'MANIFEST.MF')
            manifest_des = open(join(root, dir, 'MANIFEST.MF'), 'r')
            manifest_file = manifest_des.read()
            #print manifest_file
            parser = manifest.ManifestParser()
            bundle = parser.parse(manifest_file)
            bundle.root = root
            #print bundle, bundle.sym_name
            if libs.keys().__len__() > 0:
                print libs
                bundle.extra_libs = libs
                #assert False
            self.bundles.append(bundle)
            
    def display(self):
        for i in self.bundles:
            i.display()
            print '-'*80
        
    def find(self, src_path):
        for i in src_path:
            for root, dirs, files in os.walk(i):
                
                libs = {}
                manifest = ()
                manifest_found = False
                
                if 'META-INF' in dirs:
                    manifest_found = True
                else:
                    continue
                    
                for dir in dirs:
                    if dir == 'META-INF':
                        manifest = (root, dir)
                    if dir == 'lib':
                        libs = self.find_libs(join(root, dir))
                        print libs
                        #assert False

# cp -r ../Rifidi-SDK/eclipse-libs/plugins/org.eclipse.equinox.launcher.cocoa.macosx_1.0.1.R35x_v20090707 ../Rifidi-SDK/eclipse-libs/plugins/org.eclipse.equinox.launcher.cocoa.macosx.x86_64_1.0.1.R35x_v20090707 ../Rifidi-SDK/eclipse-libs/plugins/org.eclipse.equinox.launcher.gtk.linux.x86_1.0.200.v20090520 ../Rifidi-SDK/eclipse-libs/plugins/org.eclipse.equinox.launcher.gtk.linux.x86_64_1.0.200.v20090519 ../Rifidi-SDK/eclipse-libs/plugins/org.eclipse.equinox.launcher.win32.win32.x86_1.0.200.v20090519 ../Rifidi-SDK/eclipse-libs/plugins/org.eclipse.equinox.launcher.win32.win32.x86_64_1.0.200.v20090519 
#  cp -r lib/* 

if __name__ == '__main__':
    builder = Builder(launchers, sources, configurations,lib, deploy)
    builder.doit()