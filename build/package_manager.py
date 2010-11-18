#!/usr/bin/env python

import os
import re
import subprocess
from os.path import join, abspath
from optparse import OptionParser

from conf import deploy, exports, launchers, lib, configurations, debian

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
            print 'cp -r '+join(lib_root,bundle_dir)+' '+join(target_dir, 'rifidi', 'plugins')
            
            ret = subprocess.call(['cp', '-r', join(lib_root,bundle_dir),
                             join(target_dir,'rifidi','plugins')])
            assert ret == 0
        for bundle in bundles:
            assert not re.search('.svn', bundle_dir)
            print 'cp '+join(lib_root,bundle)+' '+join(target_dir,'rifidi','plugins')
            
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
            
            
def construct_rifidi():
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

               
def debian_packages():    
    for config in configurations:
        start = join('tmp', 'deb')        
        print 'rm -rf '+ join(start, config)
        print 'mkdir -p '+ join(start, config)
        retrm = subprocess.call(['rm', '-rf', join(start, config)])
        retmk = subprocess.call(['mkdir','-p', join(start, config)])
        assert not retrm and not retmk
        
        for root, export_dirs, files in os.walk(exports):
            for export_dir in export_dirs:
                if not re.search('.svn', export_dir) and re.search('linux', export_dir):
                    data_dir = join(start, config, export_dir, 'data')
                    sbin_dir = join('usr', 'local', 'sbin')
                    src_rifidi_dir = join(deploy, config, export_dir, 'rifidi')
                    
                    print 'mkdir -p '+join(data_dir, sbin_dir)
                    print 'cp -r '+join(debian, 'DEBIAN')+' '+data_dir
                    print 'ln -s '+ join(os.getcwd(), src_rifidi_dir) +' '+join(data_dir, sbin_dir)
                    #return
                    retmk = subprocess.call(['mkdir', '-p', join(data_dir, sbin_dir, 'rifidi')])
                    retcp = subprocess.call(['cp', '-r', join(debian, 'DEBIAN'), data_dir])
                    # we don't copy all the files
                    for rifidi_root, rifidi_dirs, rifidi_files in os.walk(join(os.getcwd(), src_rifidi_dir)):
                        for rifidi_dir in rifidi_dirs:
                            rifidi_path = join(rifidi_root, rifidi_dir)
                            start_index = rifidi_path.rfind('rifidi')
                            print 'mkdir '+join(data_dir, sbin_dir,rifidi_path[start_index:])
                            retmk = subprocess.call(['mkdir', join(data_dir, sbin_dir,rifidi_path[start_index:])])
                            assert not retmk
                            
                        for rifidi_file in rifidi_files:
                            rifidi_path = join(rifidi_root, rifidi_file)
                            start_index = rifidi_path.rfind('rifidi')
                            assert start_index != -1
                            print 'ln -s '+ join(os.getcwd(), rifidi_root, rifidi_file)+' '+\
                                    join(data_dir, sbin_dir,rifidi_path[start_index:])
                            retln = subprocess.call(['ln', join(os.getcwd(), rifidi_root, rifidi_file),
                                                    join(data_dir, sbin_dir,rifidi_path[start_index:])])
                            assert not retln
                            
                    for droot, ddirs, dfiles in os.walk(join(data_dir, 'DEBIAN')):
                        for ddir in ddirs:
                            if re.search('.svn', ddir):
                                retrm = subprocess.call(['rm', '-rf',
                                                          join(droot, ddir)])
                                assert not retrm
                        
                    current = os.getcwd()
                    os.chdir(join(start, config, export_dir))
                    print os.getcwd()
                    print 'dpkg --build data '+join(current, 'debian-packages')
                    retdpg = subprocess.call(['dpkg', '--build', 'data',
                                              join(current, 'debian-packages',
                                                   'rifidi-'+config+'-'+export_dir+'.deb')])
                    os.chdir(current)
                    assert not retmk and not retcp and not retln
            

def cleanup_debian():
    retrm = subprocess.call(['rm', '-rf', join('tmp', 'deb')])

def rpm_packages():
    raise NotImplementedError()
def cleanup_windoze():
    raise NotImplementedError()

def macosx_packages():
    raise NotImplementedError()
def cleanup_macosx():
    raise NotImplementedError()

def windoze_packages():
    raise NotImplementedError()
def cleanup_windoze():
    raise NotImplementedError()

class Parameters:
    def __init__(self):
        self.args = None
        self.options = None
        self.parser = OptionParser(version="%prog 1.3.37", usage="%prog [options]")
        
        construct_help = 'Construct a Rifidi Edge Server'
        debian_help = 'Construct a Debain Package of the Rifidi Edge Server; requires a constructed Rifidi Edge Server'
        rpm_help = 'Construct an RPM; requires a constructed Rifidi Edge Server'
        windoze_help = 'Construct a Windoze Installer; requires a constructed Rifidi Edge Server'
        macosx_help = 'Construct a Mac OS X package; requires a constructed Rifidi Edge Server'
        nocleanup_help = 'Do not cleanup; default is to cleanup'
        
        self.parser.add_option("-c", "--construct", action="store_true",
                                default=False, dest="construct",
                                help=construct_help)
        self.parser.add_option("-d", "--debian", action="store_true",
                                default=False, dest="debian",
                                help=debian_help)
        self.parser.add_option("-r", "--rpm", action="store_true",
                                default=False, dest="rpm",
                                help=rpm_help)
        self.parser.add_option("-w", "--windoze", action="store_true",
                                default=False, dest="windoze",
                                help=windoze_help)
        self.parser.add_option("-m", "--maxosx", action="store_true",
                                default=False, dest="macosx",
                                help=macosx_help)
        self.parser.add_option("-n", "--no-cleanup", action="store_true",
                                default=False, dest="no_cleanup",
                                help=macosx_help)
        (self.options, self.args) = self.parser.parse_args()
                
            
            

if __name__ == '__main__':
        
    params = Parameters()
    cmd_set = False
    if params.options.construct:
        construct_rifidi()
        if not params.options.no_cleanup:
            cleanup_consturct_rifidi()
        cmd_set = True
        
    if params.options.debian:
        debian_packages()
        if not params.options.no_cleanup:
            cleanup_debian()
        cmd_set = True
    if params.options.rpm:
        rpm_packages()
        if not params.options.no_cleanup:
            cleanup_rpm()
        cmd_set = True        
    if params.options.windoze:
        windoze_packages()
        if not params.options.no_cleanup:
            cleanup_windoze()
        cmd_set = True        
    if params.options.macosx:
        macosx_packages()
        if not params.options.no_cleanup:
            cleanup_macosx()
        cmd_set = True    
    
    if not cmd_set:
        params.parser.print_help()
        

