#!/usr/bin/env python

import ply.lex as lex
import ply.yacc as yacc
import re

class Bundle:
    def __init__(self):
        self.path = None
        self.sym_name = ''
        self.ipackages = []
        self.epackages = []
        
    def add_ipackage(self, i):
        
        self.ipackages.append(i)
    
    def add_epackage(self, e):
        
        self.epackages.append(e)
            
        
class Package:
    def __init__(self, name):
        self.name = name
        self.b_version = None
        self.e_version = None
        self.b_inclusive = False
        self.e_inclusive = False
    
    def __str__(self):
        string = ''
        if self.b_version == None and self.e_version == None:
            return string
            
        assert self.b_version != None and self.e_version != None
            
        if self.b_inclusive:
            string += '['
        else:
            string += '('
            
        string += self.b_version.__str__() +','+ self.e_version.__str__()
        
        if self.e_inclusive:
            string += '['
        else:
            string += '('
            
        return string
       
    def set_version_range(self, bversion, b_inc, eversion, e_inc):
        assert isinstance(bversion, Version) and isinstance(eversion, Version)
        self.b_version = bversion
        self.b_inclusive = b_inc
        self.e_version = eversion
        self.e_inclusive = e_inc
            
    def is_in_range(self, version):
        
        if self.b_version == None and self.e_version == None:
            return True
        
        if not version.isLess(self.b_version) \
            and not (self.b_inclusive and version.isEqual(self.b_version)):
            return False
        elif not self.e_version.isLess(version) \
            and not(self.e_inclusive and self.e_version.isEqual(version)):
            return False
        else:
            return True
                
            
class Version:
    def __init__(self):
        self.major = 0
        self.major_set = False
        self.minor = 0
        self.minor_set = False
        self.micro = 0
        self.micro_set = False        
        self.qual = '0'
        self.qual_set = False        

    def __str__(self):
        string = ''
        if(self.major_set):
            string += str(self.major)
        else:
            return string
        
        if(self.minor_set):
            string = string + '.' +str(self.minor)
        else:
            return string
        
        if(self.micro_set):
            string = string + '.' +str(self.micro)
        else:
            return string
        
        if(self.qual_set):
            string = string + '.' +str(self.qual)
            
        return string        

    def set_major(self, major):
        self.major_set = True
        self.major = major
            
    def set_minor(self, minor):
        self.minor_set = True
        self.minor = minor
            
    def set_micro(self, micro):
        self.micro_set = True
        self.micro = micro
            
    def set_qual(self, qual):
        self.qual_set = True
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
            
          
class Ast:
    def __init__(self):
        self.bundle = Bundle()
            
    def packages(self, p):
        #print ' packages '
        print p[0], p[1], p[2] 
        assert len(p) == 3 and p[2] != None
        
        packages = p[2]
        cmd = p[1]

        for i in packages:            
            if cmd == 'Import-Package:':
                self.bundle.add_ipackage(i)
                #print '---- adding import package ----', i
            elif cmd == 'Export-Package:':
                self.bundle.add_epackage(i)
                #print '---- adding export package ----', i                        
            else:
                assert False
            
    def bundle_symbolic_name(self, p):
        print ' bundle symbolic name '
        assert len(p) == 3 or len(p) == 4
        if len(p) == 3:
            self.bundle.sym_name = p[2]
            print self.bundle.sym_name        
        
    def ex_im_ports(self, p):
        #print ' _ports '
        assert len(p) == 2 or len(p) == 4
        if len(p) == 2:
            p[0] = p[1]
        else:
            #print p[1], p[2], p[3]
            assert p[1] != None
            p[1].extend(p[3])
            p[0] = p[1]
            
    def ex_im_port(self, p):
        #print ' _import '
        assert len(p) == 2 or len(p) == 4
        if len(p) == 2:
            p[0] = p[1]
        else:
            assert len(p[1]) == 1 or p[3] == None
            if p[3] != None:
                assert len(p[3]) == 4
                print p[1], p[3] 
                p[1][0].set_version_range(p[3][0], p[3][1], p[3][2], p[3][3])
            p[0] = p[1]
        
    def package_names(self, p):
        #print ' package-names '
        if len(p) == 2:
            p[0] = [Package(p[1]),]
        else:
            assert len(p) == 4
            p[0] = p[1].append(Package(p[3]))
            
    def package_name(self, p):
        #print 'package_name'
        if len(p) == 4:
            p[0] = p[1]+p[2]+p[3]
        else:
            assert len(p) == 2
            p[0] = p[1]
    def parameter(self, p):
        #print 'parameter ', p[1], len(p)
        assert len(p) == 2 or len(p) == 4
        assert p[0] == None or p[3] == None
        # XXX - this is a hack
        if p[1] != None:
            p[0] = p[1]
        elif len(p) == 4 and p[3] != None:
            p[0] = p[3]
        #print '----------------', p[0]
        
    def version(self, p):
        print ' version '
        assert len(p) == 4
        p[0] = p[3]   
        
    def version_string(self, p):
        assert len(p) == 4 or len(p) == 8
        print ' version string', p[0], p[1], p[2], p[3]
        if len(p) == 4:
            p[0] = [p[2], True, p[2], True]
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
    
    def directive(self, p):
        pass
        
class ManifestParser:    
    precedence = ()
    reserved = {
        'Import-Package:' : 'IMPORT_PACKAGE',
        'Export-Package:' : 'EXPORT_PACKAGE',
        'Bundle-SymbolicName:': 'BUNDLE_SYMBOLIC_NAME',
        'Bundle-Version:' : 'BUNDLE_VERSION',
        'Bundle-Name:' : 'BUNDLE_NAME',
    }
        
    tokens = ('DOT','COLON', 'COMMA', 'SEMI_COLON', 'QUOTE', 'LPAREN', 'RPAREN',
              'RANGLE', 'LANGLE', 'NUMBER', 'HEADER', 'ID', 'TOKEN',
              'SLASH', 'EQUAL', 'PERCENT', 'PLUS', 'DOLLAR')+ tuple(reserved.values())
    
    t_COLON = r'\:'
    t_COMMA = r'\,'
    t_DOT = r'\.'
    t_SEMI_COLON = r'\;'
    t_EQUAL = r'='
    t_LANGLE = r'\['
    t_RANGLE = r'\]'
    t_LPAREN = r'\('
    t_RPAREN = r'\)'
    t_SLASH = r'\/'
    t_QUOTE = r'\"'
    t_PERCENT = r'%'
    t_PLUS = r'\+'
    t_DOLLAR = '\$'
    t_ignore = " \t"
    
    def __init__(self, **kw):
        #self.debug = kw.get('debug', 0)
        self.names = { }
        try:
            modname = os.path.split(os.path.splitext(__file__)[0])[1] + \
            "_" + self.__class__.__name__
        except:
            modname = "parser"+"_"+self.__class__.__name__
        #self.debugfile = modname + ".dbg"
        #self.tabmodule = modname + "_" + "parsetab"
        #print self.debugfile, self.tabmodule

        lex.lex(module=self)#, debug=self.debug)
        yacc.yacc(module=self)#,
                  #debug=self.debug,
                  #debugfile=self.debugfile,
                  #tabmodule=self.tabmodule)    
        
    def t_error(self, t):
        print 'Illegal character t.value[0] --->'#,t,'<----'
        t.lexer.skip(1)
            
    def t_NUMBER(self, t):
        r'[0-9]+'
        print 't_NUMBER'
        return t
    
    def t_HEADER(self, t):
        r'^[a-zA-Z_0-9]*\-[a-zA-Z_][a-zA-Z_0-9]*\:'
        t.type = ManifestParser.reserved.get(t.value, 'HEADER')
        print 't_HEADER ', t.value, t.type
        return t    

    def t_ID(self, t):
        r'[a-zA-Z_][a-zA-Z_0-9\$]*'    
        t.type = ManifestParser.reserved.get(t.value, 'ID')
        print 't_ID', t.value, t.type
        return t
            
    def t_TOKEN(self, t):
        r'[a-zA-Z0-9_-][a-zA-Z0-9-_\$\+\=]*'
        t.type = ManifestParser.reserved.get(t.value, 'TOKEN')
        print 't_TOKEN ', t.value, t.type
        return t
            
    def p_header(self, p):
        '''header : packages
                 | bundle_symbolic_name
                 | bundle_version
                 | bundle_name'''
        pass

    def p_bundle_version(self, p):
        '''bundle_version : BUNDLE_VERSION version_number'''
        assert False
        
    def p_bundle_name(self, p):
        '''bundle_name : BUNDLE_NAME'''
        assert False

    def p_bundle_symbolic_name(self, p):
        '''bundle_symbolic_name : BUNDLE_SYMBOLIC_NAME package_name
                                |  bundle_symbolic_name SEMI_COLON parameter'''
        self.ast.bundle_symbolic_name(p)
    #    
    #def p_jdk_version(self, p):
    #    '''jdk_version : TOKEN
    #           | ID
    #           | jdk_version DOT
    #           | jdk_version ID
    #           | jdk_version TOKEN
    #           | jdk_version COMMA
    #           | jdk_version NUMBER
    #           | jdk_version SLASH'''
    #    self.ast.jdk_version(p)
    #    
    #def p_url(self, p):
    #    '''url : ID COLON SLASH SLASH
    #        | url ID
    #        | url TOKEN
    #        | url DOT ID
    #        | url DOT TOKEN
    #        | url SLASH ID
    #        | url SLASH TOKEN'''
    #    self.ast.url(p)
    #    
    
    def p_packages(self, p):
        '''packages : IMPORT_PACKAGE imports
                            | EXPORT_PACKAGE imports'''
        self.ast.packages(p)
            
    def p_ex_im_ports(self, p):
        '''imports : import
                   | imports COMMA import'''
        self.ast.ex_im_ports(p)
            
    def p_ex_im_port(self, p):
        '''import : package_names
                   | package_names SEMI_COLON parameter'''
        self.ast.ex_im_port(p)
            
    def p_package_names(self, p):
        '''package_names : package_name
                         | package_names SEMI_COLON package_name
                         | package_names SEMI_COLON parameter'''
        self.ast.package_names(p)
            
    def p_package_name(self, p):
        '''package_name : ID
                        | package_name DOT ID'''
        self.ast.package_name(p)
            
    def p_parameter(self, p):
        '''parameter : version
                     | directive
                     | parameter SEMI_COLON version
                     | parameter SEMI_COLON directive'''
        self.ast.parameter(p)
            
    def p_directive(self, p):
        '''directive : TOKEN COLON EQUAL TOKEN
                     | TOKEN COLON EQUAL ID
                     | ID COLON EQUAL TOKEN
                     | ID COLON EQUAL ID
                     | ID COLON EQUAL QUOTE unused_package_name QUOTE
                     | ID TOKEN COLON EQUAL QUOTE unused_package_name QUOTE
                     | ID TOKEN COLON EQUAL ID'''
        print 'directive'
        self.ast.directive(p)

    def p_unused_package_name(self, p):
        '''unused_package_name : package_name
                               | unused_package_name COMMA package_name'''
        print 'unused package name'
            
    def p_version(self, p):
        '''version : TOKEN EQUAL version_string
                    | ID EQUAL version_string'''
        self.ast.version(p)
            
    def p_version_string(self, p):
        '''version_string : QUOTE version_number QUOTE
                        | QUOTE LPAREN version_number COMMA version_number RPAREN QUOTE
                        | QUOTE LPAREN version_number COMMA version_number RANGLE QUOTE
                        | QUOTE LANGLE version_number COMMA version_number RANGLE QUOTE
                        | QUOTE LANGLE version_number COMMA version_number RPAREN QUOTE'''
        self.ast.version_string(p)
            
    def p_version_number(self, p):
        '''version_number : NUMBER
                          | NUMBER DOT NUMBER
                          | NUMBER DOT NUMBER DOT NUMBER
                          | NUMBER DOT NUMBER DOT NUMBER DOT TOKEN
                          | NUMBER DOT NUMBER DOT NUMBER DOT ID'''
        self.ast.version_number(p)
            
    def p_error(self, p):
        if p:
            print "Syntax error at '%s'" % p.value
        else:
            print "Syntax error at EOF"
        raise SyntaxError
    
    def parse(self, manifest):
        assert manifest != None
        
        manifest = re.sub(r'\r\n ', '', manifest)
        headers = re.split(r'\r\n', manifest)
        
        self.ast = Ast() 
        
        for header in headers:
            if header.startswith('Import-Package:') \
                or header.startswith('Export-Package:') \
                or header.startswith('Bundle-SymbolicName:'):            
                #print header
                yacc.parse(header)
        return self.ast.bundle
        
    
if __name__ == '__main__':
    pass
