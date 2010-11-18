#!/usr/bin/env python

import manifest

import os
import subprocess

from conf import jar_path, src_path, bundle_dirs, do_not_package_libs

from optparse import OptionParser
from os.path import join, abspath

class Gen:
    def __init__(self, deps):
        self.jars = deps.jars
        self.src = deps.src.bundles
        self.exports = deps.exports
        self.bundles = deps.bundles
        self.required_jars = deps.required_jars.values()
        self.target_platform = deps.target_platform
    
    def __build_classpath__(self, bundle):
        if bundle.classpath == None:
            bundle.classpath = {}
               
            for lib in bundle.extra_libs.keys():
                bundle.classpath[lib] = lib
                #print 'EXTRA LIBS #####################', bundle.extra_libs
                
            for dep in bundle.deps:
                if dep.classpath == None:
                    self.__build_classpath__(dep)
                    
                for clazz1 in dep.classpath.keys():
                    bundle.classpath[clazz1] = clazz1
    
                clazz = ''              
                clazz += join(dep.root, dep.file)
                if not dep.jar:
                    clazz += '/bin'
                    
                if not (clazz in bundle.classpath):
                    bundle.classpath[clazz] = clazz
    
    def generate_build_files(self):
        
        master_build_file = '<project name="rifidi-build" default="compile" basedir=".">\n'
        master_build_file += '\t<property name="lib" value="./lib" />\n'        
        master_build_file += '\t<target name="init">\n'+\
                             '\t\t<delete dir="${lib}" />\n'+\
                             '\t\t<mkdir dir="${lib}" />\n'+\
                             '\t</target>\n'
        master_compile = '\t<target name="compile">\n'
        master_clean = '\t<target name="clean" description="clean up">\n'
        #master_clean
        master_lint = '\t<target name="lint" description="run lint" >\n'
        master_package = '\t<target name="package" description="packages bundles" depends="init">\n'

        for bundle in self.src:
            assert bundle.root != ''
            home = os.getcwd()
            os.chdir(bundle.root)
            build_xml = open('./build.xml', 'w')
            build_xml.write('<?xml version="1.0"?>\n')
            build_xml.write('<project name="'+str(bundle.sym_name)+'" default="compile" '+\
                            'basedir="'+str(home)+'">\n')
        
            build_xml.write('\t<property name="lib" value="'+str(home)+'/lib" />\n')
            build_xml.write('\t<property name="src" value="'+str(bundle.root)+'/src" />\n')
            build_xml.write('\t<property name="build" value="'+str(bundle.root)+'/bin" />\n')
            build_xml.write('\t<property name="manifest" value="'+str(bundle.root)+'/META-INF/MANIFEST.MF" />\n')
            build_xml.write('\t<property name="metainf" value="'+str(bundle.root)+'/META-INF" />\n')
            build_xml.write('\t<property name="bundle" value="'+str(home)+'/lib/'+str(bundle.sym_name)+'_'+\
                            bundle.version.__str__()+'.jar" />\n')
            
            self.__build_classpath__(bundle)
            
            build_xml.write('\t<path id="classpath">\n')
            for location in bundle.classpath.keys():
                build_xml.write('\t\t<pathelement location="'+str(location)+'"/>\n')
            build_xml.write('\t</path>\n')

            build_xml.write('\t<target name="init" depends="clean">\n'+\
                            '\t\t<tstamp />\n\t\t<mkdir dir="${build}" />\n'+\
                            '\t</target>\n')
            
            build_xml.write('\t<target name="clean" description="clean up">\n'+\
                            '\t\t<delete dir="${build}" />\n'+\
                            '\t</target>\n')
        
            build_xml.write('\t<target name="compile" depends="init">\n')
            build_xml.write('\t\t<javac srcdir="${src}" destdir="${build}" '+\
                            'classpathRef="classpath"/>\n')
            build_xml.write('\t</target>\n')

            build_xml.write('\t<target name="lint" depends="init">\n')
            build_xml.write('\t\t<javac srcdir="${src}" destdir="${build}" '+\
                            'classpathRef="classpath">\n')
            build_xml.write('\t\t\t<compilerarg value="-Xlint"/>\n')
            build_xml.write('\t\t</javac>\n')
            build_xml.write('\t</target>\n')
            
            build_xml.write('\t<target name="package" depends="compile">\n')
            
            build_xml.write('\t\t<jar destfile="${bundle}" basedir="${build}" manifest="${manifest}">\n')
            build_xml.write('\t\t\t<metainf dir="${metainf}"/>\n')
            build_xml.write('\t\t</jar>\n')
    
            build_xml.write('\t</target>\n')
            
            build_xml.write('</project>\n')
            master_compile += '\t\t<ant dir="'+bundle.root+'" target="compile" /> \n'
            master_clean += '\t\t<ant dir="'+bundle.root+'" target="clean" /> \n'
            master_lint += '\t\t<ant dir="'+bundle.root+'" target="lint" /> \n'
            master_package += '\t\t<ant dir="'+bundle.root+'" target="package" /> \n'
            build_xml.close()
            
            os.chdir(home)
        master_compile += '\t</target>\n'
        master_clean += '\t</target>\n'
        master_lint += '\t</target>\n'
        
        #for jar_bundle in self.required_jars:
        #    print join (jar_bundle.root, jar_bundle.file)
        #    master_package += '\t\t<copy file="'+join(jar_bundle.root, jar_bundle.file)+'" todir="${lib}" overwrite="true" />\n'
        for root, file, is_dir in self.target_platform.values():
            #master_package += '\t\t<echo> copying '+join(root,file)+' </echo>\n'
            if is_dir:
                #print join(root, file)
                master_package+= '\t\t<mkdir dir="${lib}/'+file+'"/>\n'
                master_package += '\t\t<copy todir="${lib}/'+file+'"><fileset dir="'+join(root,file)+'"/></copy>\n'
            else:
                master_package += '\t\t<copy file="'+join(root,file)+'" todir="${lib}" overwrite="true" />\n'
        master_package += '\t</target>\n'
        
        master_build_file += master_clean
        master_build_file += master_compile
        master_build_file += master_lint
        master_build_file += master_package
        
        master_build_file += '\n</project>\n'
        master_build_xml = open('./build.xml', 'w')
        master_build_xml.write(master_build_file)

class Dep:
    def __init__(self, jars, src, target):
        self.jars = jars
        self.src = src
        self.exports = {}
        self.bundles = {}
        self.required_jars = {}
        self.target_platform = target
        
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
            
    def __partially_order__(self, bundle):
        ret = False
        for dep_bundle in bundle.deps:
            for dep_dep_bundle in dep_bundle.deps:
                if dep_dep_bundle == bundle:
                    print 'ERROR: circular dependencies are not supported.'
                    assert False
                        
                #print 'bundle ', bundle, bundle.sym_name, '=', bundle.build_level                
                #print 'dep bundle ', dep_bundle, dep_bundle.sym_name,'=', dep_bundle.build_level
                
                
            if dep_bundle.build_level >= bundle.build_level and not dep_bundle.jar:
                #print 'matched: ', bundle.sym_name, ' deps on ', dep_bundle.sym_name
                bundle.build_level = dep_bundle.build_level + 1
                ret = True
        return ret      
        
    def sort(self):
        #for bundle in src.bundles:
        #    print bundle.sym_name, bundle.build_level
        h4x0r = True
        while h4x0r:
            h4x0r = False
            for bundle in src.bundles:
                if self.__partially_order__(bundle):
                    h4x0r = True
                   
        src.bundles = sorted(src.bundles, key=lambda bundle : bundle.build_level)
        
        for bundle in src.bundles:
            #print bundle.sym_name, bundle.build_level
            pass
        
        return True
        
    def resolve(self):
        exports = {}
        bundles = {}
        for bundle in src.bundles:
            #print bundle.sym_name   
            assert not bundle.sym_name in bundles 
            bundles[bundle.sym_name] = bundle
                
            for package in bundle.epackages:
                self.__add_package__(exports, package, bundle)
        #assert False
        
        #print bundles
            
        for bundle in jars.bundles:
            #print '--->'+str(bundle.sym_name)+'<---', bundle
            if not bundle.sym_name in bundles:
                bundles[bundle.sym_name] = bundle
            else:
                #print 'Bundle '+str(bundle.sym_name)+' found both binary and src; using the src version (this should be an option)'
                assert join(bundle.root, bundle.file) in self.target_platform
                del self.target_platform[join(bundle.root,bundle.file)]
                
            #print bundle.display()
            for package in bundle.epackages:
                self.__add_package__(exports, package, bundle)
        
        #assert False
        #print bundles
        required_jars = {}
        # package.name = [(pacakge, bundle), (package, bundle)]
        for bundle in src.bundles:
            for required_bundle_info in bundle.rbundles:
                if required_bundle_info.name in bundles and \
                    required_bundle_info.is_in_range(\
                        bundles[required_bundle_info.name].version):
                    #print 'adding dep '+str(required_bundle_info.name)+\
                    #      '-'+str(bundles[required_bundle_info.name].version),' to ',\
                    #              bundle.sym_name
                    #print 'Adding the dep bundle = ', required_bundle_info.name, bundles[required_bundle_info.name]
                    
                    bundle.add_dep(bundles[required_bundle_info.name])
                    if bundles[required_bundle_info.name].jar:
                        required_jars[bundles[required_bundle_info.name].sym_name] =\
                        bundles[required_bundle_info.name]
                        
                        
            for package in bundle.ipackages:
                found = False
                version_found = []
                if package.name in exports:
                    for ex_package, ex_bundle in exports[package.name]:
                        #if package.name == 'javax.jms':
                            #import pdb
                            #pdb.set_trace()
                        if package.is_in_range(ex_package.b_version):
                            found = True
                            #print 'adding dep '+ex_bundle.sym_name+' to '+bundle.sym_name, 'because of package ', package.name
                            bundle.add_dep(ex_bundle)
                            if ex_bundle.jar:
                                required_jars[ex_bundle.sym_name] = ex_bundle
                        else:
                            version_found.append(ex_package)
                            #print ' pde build doesnt do the right thing either'
                            
                        #else:
                            #import pdb
                            #pdb.set_trace()
                        #    if package.is_in_range(ex_package.b_version):
                        #        pass
                    if not found:
                        found_str = ''
                        for i in version_found:
                            found_str += i.__str__() + ', '
                            
                        print 'ERROR: cannot find the correct version of '+package.name+\
                        ' for '+bundle.sym_name+'; requires '+package.__str__()+\
                        ' found = '+found_str
                        return False
                        
                else:
                    import re
                    print re.match(r'javax.xml.namespace', str(exports))
                    print 'ERROR: cannot resolve package: ', package.name\
                    +' for bundle '+bundle.sym_name+'; skipping it'
                    #return False
                    
        #print required_jars
        #self.required_jars = required_jars
        #assert False
        return True
        
        
class Jars:
    def __init__(self):
        self.jar_files = []
        self.bundles = []
        self.unique_bundles = {}
        self.target_platform = {}
        
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
                #print 'Bundle '+join(root, file)+' has no symbolic name; skipping it'
                if not (bundle.file == 'aspectjrt.jar' or \
                        bundle.file == 'aspectjweaver.jar' or\
                        bundle.file == 'cglib-nodep_2.2.jar' or \
                        bundle.file == 'RXTXcomm.jar'):
                        
                    print '------------------------------------------------------------------------------------>'+str(bundle.file)+'<---'
                    assert False
                    
                continue
                

                
            assert bundle.sym_name != ''
            assert not bundle.sym_name in self.unique_bundles
            self.unique_bundles[bundle.sym_name] = bundle
            self.bundles.append(bundle)
            if not(bundle.file in do_not_package_libs):
                self.target_platform[join(bundle.root,bundle.file)] =\
                    (bundle.root, bundle.file, False)
                
        os.chdir(cdir)
            
    def display(self):
        for i in self.bundles:
            i.display()
            print '-'*80
        
    def find(self, jar_path):
        for i in jar_path:
            #print 'jar_path: ', i
            for root, dirs, files in os.walk(i):
                for dir in dirs:
                    if dir in bundle_dirs:
                        self.target_platform[join(root,dir)] = (root, dir, True)
                for file in files:
                    if file.endswith(r'.jar'):
                        self.jar_files.append((root, file))
                        
                        
       
class Src:
    def __init__(self):
        self.src_manifests = []
        self.src_files = []
        self.bundles = []
        
    def find_libs(self, path):
        libs = {}
        for root, dirs, files in os.walk(path):
            for file in files:
                if file.endswith(r'.jar'):
                    libs[join(root, file)] = join(root, file)
        return libs
        
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
                #print libs
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
                        #print libs
                        #assert False
                        
                manifest += (libs,)
                self.src_manifests.append(manifest)
                    
                    
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
        
        deps = Dep(jars, src, jars.target_platform)
        assert deps.resolve()
        assert deps.sort()
        cmd_set = True
        
    if params.options.build_gen or not cmd_set:
        if jars == None:
            jars = load_jars()
       
        if src == None:
            src = load_src()
        
        if deps == None:
            deps = Dep(jars, src, jars.target_platform)
            assert deps.resolve()
            assert deps.sort()
        gen = Gen(deps)
        gen.generate_build_files()
        
    #else:
    #    params.parser.print_version()
    #    params.parser.print_help()
