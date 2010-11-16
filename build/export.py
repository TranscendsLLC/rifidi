#!/usr/bin/env python

import os
import re
import subprocess
from os.path import join, abspath

launchers = ['../Rifidi-SDK/launchers']

lib = 'lib'
deploy = 'deploy1/'
exports = 'export/'
configurations = ['ambient-edge', 'edge']

def copy_launchers():
    for path in launchers:
        for root, dirs, files in os.walk(path):
            for dir in dirs:
                if dir == '.svn':
                    continue
                else:
                    print dir
                ret = subprocess.call(['rm', '-rf', join(lib, dir)])
                assert ret == 0
                    
                print 'cp -r '+join(root,dir)+' '+join(lib,dir)
                ret = subprocess.call(['cp','-r', join(root, dir), lib])
                assert ret == 0
                for sub_root, sub_dirs, sub_files in os.walk(join(lib,dir)):
                    for sub_dir in sub_dirs:
                        if re.search('.svn', sub_dir):
                            ret = subprocess.call(['rm', '-rf', join(sub_root,sub_dir)])
                            
            break

def setup_config_dir(config):
    ret = subprocess.call(['rm', '-rf', join(deploy, config)])
    assert ret == 0
    ret = subprocess.call(['mkdir', join(deploy, config)])
    assert ret == 0    

def copy_eclipse_export_artifacts(root, target, export_dir):
    target_dir = join(target,export_dir)    
    ret = subprocess.call(['rm', '-rf', target_dir])
    assert ret == 0
    print 'cp -r '+join(root,export_dir)+' '+target_dir
    ret = subprocess.call(['cp','-r', join(root, export_dir), target])
    assert ret == 0
        
    for target_root, exported_dirs, exported_files in os.walk(target_dir):
        for exported_dir in exported_dirs:
            if re.search('.svn', join(target_root, exported_dir)):
                ret = subprocess.call(['rm', '-rf', join(target_root,exported_dir)])
                
def copy_bundles(target, export_dir):
    target_dir = join(target,export_dir)
    
    for lib_root, bundle_dirs, bundles in os.walk(lib):
        for bundle_dir in bundle_dirs:
            assert not re.search('.svn', bundle_dir)
            ret = subprocess.call(['cp', '-r', join(lib_root,bundle_dir),
                             join(target_dir,'rifidi','plugins')])
            assert ret == 0
        for bundle in bundles:
            assert not re.search('.svn', bundle_dir)
            ret = subprocess.call(['cp', join(lib_root,bundle),
                             join(target_dir,'rifidi','plugins')])
            assert ret == 0
        break

def copy_config_dir(target, export_dir, config_root, config_dirs, config_files):
    target_dir = join(target,export_dir)
    
    for config_dir in config_dirs:
        if re.search('.svn', config_dir):
            continue

        ret = subprocess.call(['rm', '-rf', join(target_dir, 'rifidi', config_dir)])
        assert ret == 0
    
        ret = subprocess.call(['cp', '-r', join(config_root,config_dir),
                             join(target_dir, 'rifidi', config_dir)])
        assert ret == 0
        for config_dir_root, config_dir_dirs, config_dir_file in \
                            os.walk(join(target_dir, 'rifidi',config_dir)):
            for config_dir_dir in config_dir_dirs:
                if re.search('.svn', config_dir_dir):
                    ret = subprocess.call(['rm', '-rf', join(config_dir_root, config_dir_dir)])
                
    for config_file in config_files:
        ret = subprocess.call(['rm', '-rf', join(target_dir, 'rifidi', config_file)])
        assert ret == 0
        ret = subprocess.call(['cp', join(config_root,config_file),
                             join(target_dir,'rifidi', config_file)])
        assert ret == 0
                    
                        
def build_edge_server():
    ret = subprocess.call(['ant', 'package'])
    assert ret == 0

    copy_launchers()

    for config in configurations:
        setup_config_dir(config)
        target = join(deploy, config)
        for root, export_dirs, files in os.walk(exports):
            for export_dir in export_dirs:
                if re.search('.svn', export_dir):
                    continue
                copy_eclipse_export_artifacts(root, target, export_dir)
                copy_bundles(target, export_dir)
                for config_root, config_dirs, config_files in os.walk(config):
                    copy_config_dir(target,export_dir,
                                    config_root, config_dirs, config_files)
                    break
            break
                
            
        
if __name__ == '__main__':
    build_edge_server()

    import sys
    sys.exit()
