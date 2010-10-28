#!/usr/bin/env python

import manifest_parser

class Ast(manifest_parser.DefaultAst):
    def __init__(self):
        self.bundles = []
        manifest_parser.DefaultAst.__init__(self)
    def __add_packages__(self, bundle, cmd, packages):
        if(cmd == 'import-package'):
            print packages
            for i in packages:
                bundle.add_ipackage(i)
                print '---- adding package ----', i
        else:
            assert False
              
    def manifest(self, p):
        assert len(p) == 2 or len(p) == 3
        if len(p) == 2:
            p[0] = Bundle()
            if p[1] != None:
                self.__add_packages__(p[0], p[1][0], p[1][1])
                self.bundles.append(p[0])
        else:
            assert p[1] != None
            self.__add_packages__(p[1], p[2][0], p[2][1])
            
    def header(self, p):
        assert len(p) == 2
        if p[1] != None:
            p[0] = p[1]
            
    def bundle_symbolic_name(self, p):
        #print ' bundle symbolic name '
        self.stack.append('bundle_symbolic_name')
    def export_package(self, p):
        #print ' export package '
        self.stack.append('export_package')
    def exports(self, p):
        #print ' exports '
        self.stack.append('export')
    def import_package(self, p):
        print ' import package '
        print p[0], p[1], p[2] 
        assert len(p) == 3 and p[2] != None
        p[0] = ('import-package', p[2])
        self.stack.append('import_package')
    def imports(self, p):
        print ' imports '
        assert len(p) == 2 or len(p) == 4
        if len(p) == 2:
            p[0] = p[1]
        else:
            print p[1], p[2], p[3]
            assert p[1] != None
#                p[0] = [p[3],]
 #           else:
            p[1].extend(p[3])
            p[0] = p[1]
            
        self.stack.append('imports')
            
    def _import(self, p):
        print ' _import '
        assert len(p) == 2 or len(p) == 4
        if len(p) == 2:
            p[0] = p[1]
        else:
            assert len(p[1]) == 1 or p[3] == None
            if p[3] != None:
                assert len(p[3]) == 4
                p[1][0].set_version_range(p[3][0], p[3][1], p[3][2], p[3][3])
            p[0] = p[1]
        self.stack.append('import')
        assert p[0] != None
        
    def package_names(self, p):
        print ' package-names '
        if len(p) == 2:
            p[0] = [Package(p[1]),]
        else:
            assert len(p) == 4
            p[0] = p[1].append(Package(p[3]))
        self.stack.append('package_names')
    def package_name(self, p):
        print 'package_name'
        if len(p) == 4:
            p[0] = p[1]+p[2]+p[3]
        else:
            assert len(p) == 2
            p[0] = p[1]
    def parameter(self, p):
        #print 'parameter ', p[1], len(p)
        assert len(p) == 2 or len(p) == 4
        # XXX - this is a hack
        if p[1] != None:
            p[0] = p[1]
        elif p[3] != None:
            p[0] = p[3]
        #print '----------------', p[0]
        self.stack.append('parameter')
    def version(self, p):
        print ' version '
        assert len(p) == 4
        p[0] = p[3]   
        self.stack.append('version')
    def version_string(self, p):
        assert len(p) == 4 or len(p) == 8
        print ' version string'
        if len(p) == 4:
            p[0] = [p[1], True, p[1], True]
        elif len(p) == 8 and p[2] == '(' and p[6] == ')':
            p[0] = [p[3], False, p[5], False]
        elif len(p) == 8 and p[2] == '(' and p[6] == ']':
            p[0] = [p[3], False, p[5], True]
        elif len(p) == 8 and p[2] == '[' and p[6] == ')':
            p[0] = [p[3], True, p[5], False]
        elif len(p) == 8 and p[2] == '[' and p[6] == ']':
            p[0] = [p[3], True, p[5], True]            
        else:
            print p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], len(p)
            assert False
        self.stack.append('version_string')
        
    def version_number(self, p):
        assert len(p) <= 8
        print ' version number '
        p[0] = Version()
        if len(p) >= 2:
            p[0].set_major(p[1])
        if len(p) >= 4:
            p[0].set_minor(p[3])
        if len(p) >= 6:
            p[0].set_micro(p[5])
        if len(p) == 8:
            p[0].set_qual(p[7])
        self.stack.append('version_number')

class Bundle:
    def __init__(self):
        self.path_info = None
        self.ipackages = []
        self.epackages = []
    def add_ipackage(self, i):
        self.ipackages.append(i)
    def add_epackage(self, e):
        self.epackages.append(e)
    
    
class Version:
    def __init__(self):
        self.major = 0
        self.minor = 0
        self.micro = 0
        self.qual = '0'
    
    def type(self):
        return 'version'
    
    def set_major(self, major):
        self.major = major
            
    def set_minor(self, minor):
        self.minor = minor
            
    def set_micro(self, micro):
        self.micro = micro
            
    def set_qual(self, qual):
        self.qual = qual
            
    def isLess(self, version):
        if self.major < version.major:
            return False
        elif self.major > version.major:
            return True
        elif self.minor < version.minor:
            return False
        elif self.minor > version.minor:
            return True
        elif self.micro < version.micro:
            return False
        elif self.micro > version.micro:
            return True
        elif self.qual <= version.qual:
            return False
        else:
            return True
            
    def isEqual(self, version):
        if self.major == version.major and self.minor == version.minor and \
           self.micro == version.micro and self.qual == version.qual:
            return True
        else:
            return False
            
class Package:
    def __init__(self, name):
        self.name = name
        self.b_version = None
        self.e_version = None
        self.b_inclusive = False
        self.e_inclusive = False
    
    def type(self):
        return 'package'
            
    def set_version_range(self, bversion, b_inc, eversion, e_inc):
        assert isinstance(bversion, Version) and isinstance(eversion, Version)
        self.b_version = bversion
        self.b_inclusive = b_inc
        self.e_version = eversion
        self.e_inclusive = e_inc
            
    def is_in_range(self, version):
        if not version.isLess(self.b_version) \
            and not (self.b_inclusive and version.isEqual(self.b_version)):
            return False
        elif not self.e_version.isLess(version) \
            and not(self.e_inclusive and self.e_version.isEqual(version)):
            return False
        else:
            return True
        
        
if __name__ == '__main__':
    pass
