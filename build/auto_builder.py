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
#<?xml version="1.0" encoding="UTF-8"?>
#<project name="BundleName" default="deploy" basedir=".">
#    <property name="version" value="1.0.0" />
#
#
#
#    classpath = '<path id="classpath.buildtime"> \n'+\
#                '<pathelement location="/home/to/equinox/equinox.jar" />\n'+\
#                '<pathelement location="/home/to/dependency.jar" />\n'+\
#                '</path>\n'
#
#
#    <target name="compile" depends="init" description="compile the source ">
#        <javac srcdir="${src}" destdir="${build}" classpathref="classpath.buildtime" />
#    </target>
#
#    <target name="dist" depends="compile" description="generate the distribution">
#        <mkdir dir="${dist}"/>
#        <jar jarfile="${dist}/${ant.project.name}-${version}.jar" basedir="${build}" manifest="META-INF/MANIFEST.MF">
#            <manifest>
#                <attribute name="Bundle-Name" value="${ant.project.name}"/>
#                <attribute name="Bundle-Version" value="${version}"/>
#            </manifest>
#        </jar>
#    </target>
#
#    <target name="deploy" depends="dist">
#        <copy file="${dist}/${buildfilename}" todir="${deployloc}" overwrite="true"/>
#    </target>
#
#    clean = '<target name="clean" description="clean up">\n'+\
#             '<delete dir="${build}" />' +\
#            '</target> '\

    def __build_classpath__(self, bundle):
        if bundle.classpath == None:
            bundle.classpath = {}
               
            for lib in bundle.extra_libs.keys():
                bundle.classpath[lib] = lib
                print bundle.extra_libs
                #assert False
                
            for dep in bundle.deps:
                #print dep.sym_name
                    
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
                            
        master_compile = '\t<target name="compile">\n'
        master_clean = '\t<target name="clean" description="clean up" >\n'

        for bundle in self.src:
            assert bundle.root != ''
            home = os.getcwd()
            os.chdir(bundle.root)
            build_xml = open('./build.xml', 'w')
            build_xml.write('<?xml version="1.0"?>\n')
            build_xml.write('<project name="'+str(bundle.sym_name)+'" default="compile" '+\
                            'basedir="'+str(home)+'">\n')
            build_xml.write('\t<property name="src" value="'+str(bundle.root)+'/src"/>\n')
            build_xml.write('\t<property name="build" value="'+str(bundle.root)+'/bin" />\n')
            #build_xml.write('\t<property name="dist" location="dist" />\n')
            #build_xml.write('<property name="dep-loc" location='str(home)+bundles'/>')
            build_xml.write('\t<target name="init" depends="clean">\n'+\
                            '\t\t<tstamp />\n\t\t<mkdir dir="${build}" />\n'+\
                            '\t</target>\n')
            build_xml.write('\t<target name="clean" description="clean up">\n'+\
                            '\t\t<delete dir="${build}" />\n'+\
                            '\t</target>\n')
                
            build_xml.write('\t<target name="compile" depends="init">\n')
                
            self.__build_classpath__(bundle)
                
            classpath='classpath="'
            for clazz in bundle.classpath.keys():
                classpath += clazz+':'        
                
            classpath = classpath.rstrip(':')
                
            build_xml.write('\t\t<javac srcdir="${src}" destdir="${build}" '+\
                            classpath+'"/>\n')
            build_xml.write('\t</target>\n')
            build_xml.write('</project>')
            #master_build_file += '\t\t<echo message="'+bundle.root+'" /> \n' 
            master_compile += '\t\t<ant dir="'+bundle.root+'" /> \n'
            master_clean += '\t\t<ant dir="'+bundle.root+'" target="clean" /> \n'
            build_xml.close()
            
            os.chdir(home)
        master_compile += '\t</target>\n'
        master_clean += '\t</target>\n'
        
        master_build_file += master_clean
        master_build_file += master_compile
        
        master_build_file += '\n</project>\n'
        master_build_xml = open('./build.xml', 'w')
        master_build_xml.write(master_build_file)

class Dep:
    def __init__(self, jars, src):
        self.jars = jars
        self.src = src
        self.exports = {}
        self.bundles = {}
    
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
                print 'matched: ', bundle.sym_name, ' deps on ', dep_bundle.sym_name
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
            print bundle.sym_name, bundle.build_level
            
        return True
    
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
            #print ' ------ bundles ---->', bundles, '<------------------'
            #print '--->'+str(bundle.sym_name)+'<---', bundle
            if not bundle.sym_name in bundles:
                bundles[bundle.sym_name] = bundle
            else:
                print 'Bundle '+str(bundle.sym_name)+'found both binary and src; using the src version (this should be an option)'
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
                print package.name
                
                found = False
                version_found = []
                if package.name in exports:
                    for ex_package, ex_bundle in exports[package.name]:
                        #if package.name == 'javax.jms':
                            #import pdb
                            #pdb.set_trace()
                        if package.is_in_range(ex_package.b_version):
                            found = True
                            print 'adding dep '+ex_bundle.sym_name+' to '+bundle.sym_name, 'because of package ', package.name
                            bundle.add_dep(ex_bundle)
                        else:
                            version_found.append(ex_package)
                            print ' pde build doesnt do the right thing either'
                            
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
                if bundle.file == 'aspectjrt.jar' or \
                    bundle.file == 'aspectjweaver.jar' or\
                    bundle.file == 'cglib-nodep-2.2.jar' or \
                    bundle.file == 'RXTXcomm.jar':
                    pass
                else:
                    print '--->'+str(bundle.file)+'<---'
                    assert False
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
            print bundle, bundle.sym_name, libs
            
	    if libs.keys().__len__() > 0:
                print libs
                bundle.extra_libs = libs
                #assert False
            self.bundles.append(bundle)
            
    def display(self):
        for i in self.bundles:
            i.display()
            print '-'*80
    
    def find_lib(self, path):
        libs = []
        for root, dirs, files in os.walk(path):
            for file in files:
                if file.endswith(r'.jar'):
                    libs.append(join(root, file))
        return libs
            
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
