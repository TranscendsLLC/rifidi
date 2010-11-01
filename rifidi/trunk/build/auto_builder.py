#!/usr/bin/env python

import manifest

import os
import subprocess

from conf import jar_path, src_path
from optparse import OptionParser
from os.path import join, abspath

class Gen:
    def __init__(self, deps):
        self.jars = deps.jars
        self.src = deps.src.bundles
        self.exports = deps.exports
        self.bundles = deps.bundles
        
    def generate_build_files(self):
        for bundle in self.src:
            assert bundle.root != ''
            home = os.getcwd()
            os.chdir(bundle.root)
            build_xml = open('./build.xml', 'w')
            build_xml.write('<?xml version="1.0"?>\n')
            build_xml.write('<project name="'+str(bundle.sym_name)+'" default="compile" '+\
                            'basedir="'+str(home)+'">\n')
            build_xml.write('<property name="src" value="'+str(bundle.root)+'/src"/>\n')
            build_xml.write('<property name="build" value="'+str(bundle.root)+'/bin" />\n')
            build_xml.write('<target name="compile">\n')
            classpath='classpath="'
            for dep in bundle.deps:
                print dep.sym_name
                assert dep.jar
                classpath += join(dep.root, dep.file) +':'
            print classpath
            build_xml.write('\t<javac srcdir="${src}" destdir="${build}" '+\
                            classpath+'"/>\n')
            build_xml.write('</target>\n')
            build_xml.write('</project>')
            build_xml.close()
            os.chdir(home)


class Dep:
    def __init__(self, jars, src):
        self.jars = jars
        self.src = src
        self.exports = {}
        self.bundles = {}
    
    def sort(self):
        for bundle in src.bundles:
            print bundle.sym_name, bundle.build_level
        
        for bundle in src.bundles:
            for dep_bundle in bundle.deps:
                for dep_dep_bundle in dep_bundle.deps:
                    if dep_dep_bundle == bundle:
                        print 'ERROR: circular dependencies are not supported.'
                        return False

                print 'bundle ', bundle, bundle.sym_name, '=', bundle.build_level                
                print 'dep bundle ', dep_bundle, dep_bundle.sym_name,'=', dep_bundle.build_level

                
                if dep_bundle.build_level <= bundle.build_level:
                    print 'matched', dep_bundle.sym_name
                    dep_bundle.build_level = bundle.build_level + 1
        src.bundles = sorted(src.bundles, key=lambda bundle : bundle.build_level)
        
        for bundle in src.bundles:
            print bundle.sym_name, bundle.build_level
            
        return True
    
    def __add_package__(self, packages, package, bundle):
        #package.name -> [(package, bundle), (package, bundle)]
        if package.name in packages:
            inserted = False
            for pentry, bentry in packages[package.name]:
                index = packages[package.name].index((pentry, bentry))
                assert index >= 0 and index <= len(packages[package.name])

                if package.b_version.is_equal(pentry.b_version):
                    if bundle.jar:
                        packages[package.name].insert(index, (package, bundle))
                    else:
                        packages[package.name].insert(index+1, (package, bundle))
                    inserted = True
                    break
                elif package.b_version.is_less(pentry.b_version):
                    packages[package.name].insert(index, (package, bundle))
                    inserted = True
                    break
                
            if inserted == False:
                packages[package.name].append((package, bundle))
                    
        else:
            packages[package.name] = [(package, bundle)]
        
    def resolve(self):
        exports = {}
        bundles = {}
        for bundle in src.bundles:
            assert not bundle.sym_name in bundles 
            bundles[bundle.sym_name] = bundle
                
            for package in bundle.epackages:
                self.__add_package__(exports, package, bundle)
                
        #print bundles
            
        for bundle in jars.bundles:
            print ' ------ bundles ---->', bundles, '<------------------'
            print '--->'+str(bundle.sym_name)+'<---', bundle
            assert not bundle.sym_name in bundles 
            bundles[bundle.sym_name] = bundle
            #print bundle.display()
            for package in bundle.epackages:
                self.__add_package__(exports, package, bundle)
                
        #print bundles
        
        # package.name = [(pacakge, bundle), (package, bundle)]
        for bundle in src.bundles:
            for required_bundle_info in bundle.rbundles:
                if required_bundle_info.name in bundles and \
                    required_bundle_info.is_in_range(\
                        bundles[required_bundle_info.name].version):
                    print 'adding dep '+str(required_bundle_info.name)+\
                          '-'+str(bundles[required_bundle_info.name].version),' to ',\
                                  bundle.sym_name
                    #print 'Adding the dep bundle = ', required_bundle_info.name, bundles[required_bundle_info.name]
                    
                    bundle.add_dep(bundles[required_bundle_info.name])
                                    
            for package in bundle.ipackages:
                found = False
                found = []
                if package.name in exports:
                    for ex_package, ex_bundle in exports[package.name]:
                        if package.is_in_range(ex_package.b_version):
                            found = True
                            print 'adding dep '+ex_bundle.sym_name+' to '+bundle.sym_name
                            bundle.add_dep(ex_bundle)
                        else:
                            found.append(ex_package.b_version) 
                        #else:
                            #import pdb
                            #pdb.set_trace()
                        #    if package.is_in_range(ex_package.b_version):
                        #        pass
                    if not found:
                        found_str = ''
                        for i in found:
                            found_str += i.b_version.__str__() + ', '
                            
                        print 'ERROR: cannot find the correct version of '+package.name+\
                        ' for '+bundle.sym_name+'; requires '+package.__str__()+\
                        ' found = '+found
                        
                        
                        return False
                        
                else:
                    print 'ERROR: cannot resolve package: ', package.name\
                    +' for bundle '+bundle.sym_name+' the packages does not exist'
                    return False
        return True
        
        
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
            #print manifest_file
            parser = manifest.ManifestParser()
            bundle = parser.parse(manifest_file)
            bundle.root = root
            bundle.file = file
            bundle.jar = True
            if bundle.sym_name == '':
                print 'Bundle '+join(root, file)+' has no symbolic name; skipping'
                continue
            assert bundle.sym_name != ''
            self.bundles.append(bundle)
        os.chdir(cdir)
            
    def display(self):
        for i in self.bundles:
            i.display()
            print '-'*80
        
    def find(self, jar_path):
        for i in jar_path:
            print 'jar_path: ', i
            for root, dirs, files in os.walk(i):
                for file in files:
                    if file.endswith(r'.jar'):
                        self.jar_files.append((root, file))
                        
                        
       
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
            print bundle, bundle.sym_name
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
                                            
    
class Parameters:
    def __init__(self):
        self.args = None
        self.options = None
        self.parser = OptionParser(version="%prog 1.3.37")
        display_jars_help = 'Display Java archives'
        display_src_help = 'Display sources'
        check_dep_help = 'Check dependencies'
        build_gen_help = 'Generate build artifacts'
        #; if no options are given, '\
        #                 'then this command is executed; supported values'\
        #                 ' are: ant, make, maven; default value is: ant.'
        self.parser.add_option("-j", "--display-jars", action="store_true",
                                default=False, dest="display_jars",
                                help=display_jars_help)
        self.parser.add_option("-s", "--display-src", action="store_true",
                                default=False, dest="display_src",
                                help=display_src_help)
        self.parser.add_option("-c", "--check-dep", action="store_true",
                               default=False, dest="check_dep",
                               help=check_dep_help)
        self.parser.add_option("-b", "--build-gen", action="store_true",
                               default=False, dest="build_gen",
                               help=build_gen_help)
        
        #self.parser.add_option("-g", "--gen-build",
        #              dest="gen_build", metavar="BUILD-TYPE", type=str,
        #              default='ant', help=gen_build_help)
                        
        (self.options, self.args) = self.parser.parse_args()
            
 

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

def convert_paths(jar_path, src_path):
    index = 0
    for path in jar_path:
        jar_path[index] = abspath(path)
        index += 1
    index = 0
    for path in src_path:
        src_path[index] = abspath(path)
        index += 1
        
if __name__ == '__main__':
    jars = None
    src = None
    deps = None
    cmd_set = False        
    params = Parameters()
    
    if params.options.display_jars:
        print '-'*80
        jars = load_jars()
        jars.display()
        cmd_set = True
 
    if params.options.display_src:
        if not cmd_set:
            print '-'*80
        src = load_src()
        src.display()
        cmd_set = True
        
    if params.options.check_dep:
        if not jars:
            jars = load_jars()
       
        if not src:
            src = load_src()
        
        #jars.display()
        #src.display()
        
        deps = Dep(jars, src)
        assert deps.resolve()
        assert deps.sort()
        cmd_set = True
        
    if params.options.build_gen or not cmd_set:
        if jars == None:
            jars = load_jars()
       
        if src == None:
            src = load_src()
        
        if deps == None:
            deps = Dep(jars, src)
            assert deps.resolve()
            assert deps.sort()
        gen = Gen(deps)
        gen.generate_build_files()
        
    #else:
    #    params.parser.print_version()
    #    params.parser.print_help()
