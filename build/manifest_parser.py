#!/usr/bin/env python

import ply.lex as lex
import ply.yacc as yacc

class DefaultAst:
    def __init__(self):
        self.stack = []
    def manifest(self, p):
        #print ' manifest '
        self.stack.append('manifest')
    def header(self, p):
        #print ' header '
        self.stack.append('header')
    def bundle_symbolic_name(self, p):
        #print ' bundle symbolic name '
        self.stack.append('bundle_symbolic_name')
    def not_used(self, p):
        #print ' not used '
        self.stack.append('not_used')
    def bundle_activator(self, p):
        self.stack.append('bundle_activator')
    def url(self, p):
        self.stack.append('url')
    def jdk_version(self, p):
        #print ' jdk version '
        self.stack.append('jdk_version')
    def export_package(self, p):
        #print ' export package '
        self.stack.append('export_package')
    def exports(self, p):
        #print ' exports '
        self.stack.append('exports')
    def export(self, p):
        self.stack.append('export')
    def import_package(self, p):
        #print ' import package '
        self.stack.append('import_package')
    def imports(self, p):
        #print ' imports '
        self.stack.append('imports')
    def _import(self, p):
        #print ' _import '
        self.stack.append('import')
    def package_names(self, p):
        #print ' package-names '
        self.stack.append('package_names')
    def package_name(self, p):
        #print ' package_name '
        self.stack.append('package_name')
    def parameter(self, p):
        #print 'parameter '
        self.stack.append('parameter')
    def directive(self, p):
        #print 'directive'
        self.stack.append('directive')
    def version(self, p):
        #print ' version '
        self.stack.append('version')
    def version_string(self, p):
        #print ' version string'
        self.stack.append('version_string')
    def version_number(self, p):
        #print ' version number '
        self.stack.append('version_number')
        
class ManifestParser:    
    precedence = ()
    reserved = {
        'Import-Package:' : 'IMPORT_PACKAGE',
        'Export-Package:' : 'EXPORT_PACKAGE',
        'Manifest-Version:' : 'MANIFEST_VERSION',
        'Bundle-DocUrl:' : 'BUNDLE_DOC_URL',
        'Bundle-Activator:' : 'BUNDLE_ACTIVATOR',
        'Main-Class:' : 'MAIN_CLASS',
        'Bundle-Localization:' : 'BUNDLE_LOCALIZATION',
        'Bundle-RequiredExecutionEnvironment:' : 'BUNDLE_REQUIRED_EXE_ENV',
        'Bundle-SymbolicName:': 'BUNDLE_SYMBOLIC_NAME',
        'Bundle-Version:' : 'BUNDLE_VERSION',
        'Bundle-Description:' : 'BUNDLE_DESCRIPTION',
        'Bundle-Vendor:' :  'BUNDLE_VENDOR',
        'Bundle-Name:' : 'BUNDLE_NAME'
    }
        
    tokens = ('DOT','COLON', 'COMMA', 'SEMI_COLON', 'QUOTE', 'LPAREN', 'RPAREN',
              'RANGLE', 'LANGLE', 'NUMBER', 'HEADER', 'HEADER1', 'ID', 'TOKEN','SLASH',
              'EQUAL', 'PERCENT', 'PLUS', 'DOLLAR', 'newline', 'newline1')+ tuple(reserved.values())
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
        
    def t_newline1(self, t):
        r'[\r\n]+'
        t.lexer.lineno += t.value.count("\n")
             
    def t_newline(self, t):
        r'[\n]+'
        t.lexer.lineno += t.value.count("\n")
        
    def t_error(self, t):
        print 'Illegal character --->t.value[0]<----'
        t.lexer.skip(1)
            
    def t_NUMBER(self, t):
        r'[0-9]+'
        print 't_NUMBER'
        return t
    #
    #def t_VERSION(self, t):
    #    r'version='
    #    return t
    #
    def t_HEADER(self, t):
        r'^[a-zA-Z_][a-zA-Z_0-9(\n )]*\-[a-zA-Z_(\n )][a-zA-Z_0-9(\n )]*\:'
        t.type = ManifestParser.reserved.get(t.value, 'HEADER')
        print 't_HEADER ', t.value, t.type
        return t
    
    def t_HEADER1(self, t):
        r'^[a-zA-Z_][a-zA-Z_0-9]+\:'
        t.type = ManifestParser.reserved.get(t.value, 'HEADER1')
        print 't_HEADER1 ', t.value, t.type
        return t
        
    def t_ID(self, t):
        r'[a-zA-Z_][a-zA-Z_0-9(\n )\$]*'    
        t.type = ManifestParser.reserved.get(t.value, 'ID')
        print 't_ID', t.value, t.type
        return t
            
    def t_TOKEN(self, t):
        r'[a-zA-Z0-9_-][a-zA-Z0-9-_(\n )\$\+\=]*'
        t.type = ManifestParser.reserved.get(t.value, 'TOKEN')
        print 't_TOKEN ', t.value, t.type
        return t
            
    def p_manifest(self, p):
        '''manifest : header
                    | manifest header '''
        self.ast.manifest(p)
            
    def p_header(self, p):
        '''header : import_package
                 | bundle_symbolic_name
                 | not_used'''
        self.ast.header(p)
#                 | export_package        
    def p_bundle_symbolic_name(self, p):
        '''bundle_symbolic_name : BUNDLE_SYMBOLIC_NAME package_name
                                |  bundle_symbolic_name SEMI_COLON parameter'''
        self.ast.bundle_symbolic_name(p)
    
    def p_not_used(self, p):
        '''not_used : MANIFEST_VERSION version_number
                    | BUNDLE_DOC_URL url
                    | MAIN_CLASS package_name
                    | BUNDLE_LOCALIZATION ID
                    | BUNDLE_REQUIRED_EXE_ENV jdk_version
                    | BUNDLE_VENDOR PERCENT
                    | BUNDLE_VERSION version_number
                    | BUNDLE_DESCRIPTION PERCENT
                    | bundle_activator
                    | HEADER
                    | HEADER PERCENT
                    | HEADER NUMBER
                    | HEADER ID
                    | HEADER TOKEN
                    | HEADER package_name
                    | HEADER1 package_name
                    | not_used DOLLAR
                    | not_used package_name
                    | not_used PLUS
                    | not_used SLASH
                    | not_used EQUAL
                    | not_used NUMBER
                    | not_used ID
                    | not_used TOKEN
                    | not_used COMMA'''
        self.ast.not_used(p)
        print 'not used'
    
    def p_bundle_activator(self, p):
        '''bundle_activator : BUNDLE_ACTIVATOR package_name
                            | bundle_activator package_name'''
        self.ast.bundle_activator(p)
        
    def p_jdk_version(self, p):
        '''jdk_version : TOKEN
               | ID
               | jdk_version DOT
               | jdk_version ID
               | jdk_version TOKEN
               | jdk_version COMMA
               | jdk_version NUMBER
               | jdk_version SLASH'''
        self.ast.jdk_version(p)
        
    def p_url(self, p):
        '''url : ID COLON SLASH SLASH
            | url ID
            | url TOKEN
            | url DOT ID
            | url DOT TOKEN
            | url SLASH ID
            | url SLASH TOKEN'''
        self.ast.url(p)
    #    
    #def p_export_package(self, p):
    #    '''export_package : 
    #    self.ast.export_package(p)
    #    
    #def p_exports(self, p):
    #    '''exports : export
    #               | exports COMMA export'''
    #    self.ast.exports(p)
    #def p_export(self, p):
    #    '''export : package_names'''
    #    self.ast.export(p)
    
    def p_import_package(self, p):
        '''import_package : IMPORT_PACKAGE imports
                            | EXPORT_PACKAGE imports'''
        self.ast.import_package(p)
            
    def p_imports(self, p):
        '''imports : import
                   | imports COMMA import'''
        self.ast.imports(p)
            
    def p_import(self, p):
        '''import : package_names
                   | package_names SEMI_COLON parameter'''
        self.ast._import(p)
            
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
    def parse(self, sentence, ast):
        assert ast != None
        assert sentence != None
        self.ast = ast
        yacc.parse(sentence)
    
            
if __name__ == '__main__':
    pass
