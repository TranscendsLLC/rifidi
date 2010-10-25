#!/usr/bin/env python

import base_parser
        
class Bundle:
    def __init__(self):
        self.ipackages = []
        self.epackages = []
    def add_ipackage(self, i):
        self.ipackages.add(i)
    def add_epackage(self, e):
        self.epackages.add(e)
    
class Version:
    def __init__(self):
        self.major = 0
        self.minor = 0
        self.micro = 0
        self.qual = '0'
            
    def set_major(self, major):
        self.major = major
            
    def set_minor(self, minor):
        self.minor = minor
            
    def set_micro(self, micro):
        self.micro = micro
            
    def set_qual(self, qual):
        self.qual = qual
            
    def isLess(self, version):
        if self.major > version.major:
            return True
        elif self.minor > version.minor:
            return True
        elif self.micro > version.micro:
            return True
        elif self.qual > version.qual:
            return True
        else:
            return False
            
    def isEqual(self, version):
        if self.major == version.major and self.minor == version.minor and \
           self.micro == version.micro and self.qual == version.qual:
            return True
            
class ImportPackage:
    def __init__(self, name):
        self.name = name
        self.b_version = None
        self.e_version = None
        self.b_inclusive = False
        self.e_inclusive = False
            
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
            
class Bundle:
    def __init__(self, name, version):
        self.name = name
        self.version = version
        
    def add_exported_package(self, package):
        self.exports.add(package)
        
    def add_import_package(self, package):
        self.imports.add(packages)

class abstract_syntax_tree:
    def __init__(self):
        self.stack = []
    def push(self, top):
        print 'abstract_syntax_tree.push: ', top
        self.stack.append(top)
    def pop(self):
        top = self.stack.pop()
        print 'abstract_syntax_tree.pop: ', top
        return top

class ManifestParser(base_parser.Parser):    
    
    reserved = {
        'Import-Package:' : 'IMPORT_PACKAGE'
    }
    tokens = ('DOT','COLON', 'COMMA', 'SEMI_COLON', 'QUOTE', 'LPAREN', 'RPAREN',
              'RANGLE', 'LANGLE', 'NUMBER', 'HEADER', 'ID', 'TOKEN',
              'EQUAL') + tuple(reserved.values())
    t_COLON = r'\:'
    t_COMMA = r'\,'
    t_DOT = r'\.'
    t_SEMI_COLON = r'\;'
    t_EQUAL = r'='
    t_LANGLE = r'\['
    t_RANGLE = r'\]'
    t_LPAREN = r'\('
    t_RPAREN = r'\)'
    t_QUOTE = r'\"'
    t_ignore = " \t"
    
    def t_newline(self, t):
        r'\n+'
        t.lexer.lineno += t.value.count("\n")
    def t_error(self, t):
        print "Illegal character '%s'" % t.value[0]
        t.lexer.skip(1)
    
    def t_NUMBER(self, t):
        r'[0-9]+'
        print 't_NUMBER'
        return t
    
    def t_HEADER(self, t):
        r'[a-zA-Z_][a-zA-Z_0-9]*\-[a-zA-Z_][a-zA-Z_0-9]*\:'
        t.type = ManifestParser.reserved.get(t.value, 'HEADER')
        print 't_HEADER ', t.value, t.type
        return t
    
    def t_ID(self, t):
        r'[a-zA-Z_][a-zA-Z_0-9]*'    
        t.type = ManifestParser.reserved.get(t.value, 'ID')
        print 't_ID', t.value, t.type
        return t
    
    def t_TOKEN(self, t):
        r'[a-zA-Z0-9_-]+'
        t.type = ManifestParser.reserved.get(t.value, 'TOKEN')
        print 't_TOKEN ', t.value, t.type
        return t 
    
    def p_manifest(self, p):
        '''manifest : header
                    | manifest header '''
        pass
        print 'done'
    
    def p_header(self, p):
       '''header : import_package
                | HEADER'''
       print 'header done'
    
    def p_import_package(self, p):
        '''import_package : IMPORT_PACKAGE imports'''
        print 'importpackage'
    
    def p_imports(self, p):
        '''imports : import
                   | imports COMMA import'''
    
    def p_import(self, p):
        '''import : package_names
                   | package_names SEMI_COLON parameter'''
        print 'imports....'

    def p_package_names(self, p):
        '''package_names : package_name
                         | package_names SEMI_COLON package_name'''
        print 'package names...'

    def p_package_name(self, p):
        '''package_name : ID
                        | package_name DOT ID'''
        print 'package name'
        
    def p_parameter(self, p):
        '''parameter : version
                     | directive
                     | parameter SEMI_COLON version
                     | parameter SEMI_COLON directive'''
        print 'parameter'
    def p_directive(self, p):
        '''directive : TOKEN COLON EQUAL TOKEN
                     | TOKEN COLON EQUAL ID
                     | ID COLON EQUAL TOKEN
                     | ID COLON EQUAL ID'''
        print 'directive, being dropped on the floor'

    def p_version(self, p):
        '''version : TOKEN EQUAL version_string
                    | ID EQUAL version_string'''
        print 'version number'

    def p_version_string(self, p):
        '''version_string : QUOTE version_number QUOTE
                        | QUOTE LPAREN version_number COMMA version_number RPAREN QUOTE
                        | QUOTE LPAREN version_number COMMA version_number RANGLE QUOTE
                        | QUOTE LANGLE version_number COMMA version_number RANGLE QUOTE
                        | QUOTE LANGLE version_number COMMA version_number RPAREN QUOTE'''
        print "version string"
        
    def p_version_number(self, p):
        '''version_number : NUMBER
                          | NUMBER DOT NUMBER
                          | NUMBER DOT NUMBER DOT NUMBER
                          | NUMBER DOT NUMBER DOT NUMBER DOT TOKEN'''
        print 'version number'

    def p_error(self, p):
        if p:
            print "Syntax error at '%s'" % p.value
        else:
            print "Syntax error at EOF"
        raise SyntaxError   
    def parse(self, word, ast):
        assert ast != None
        assert word != None
        self.ast = ast
        self.run_parser(word)

if __name__ == '__main__':
    pass
