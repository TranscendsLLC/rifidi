#!/usr/bin/env python

import base_parser

class abstract_syntax_tree:
    def __init__(self):
        return
    
class ManifestParser(base_parser.Parser):
    tokens = ('IMPORT_PACKAGE', 'EXPORT_PACKAGE', 'BUNDLE_VERSION', 'BUNDLE_SYMBOLICNAME',
              'MANIFEST_VERSION', 'BUILD_JDK', 'BUNDLE_CLASSPATH', 'BUILD_BY',
              'BUNDLE_NAME' ,'CREATED_BY', 'BUNDLE_VENDOR', 'BUNDLE_MANIFESTVERSION',
              'ARCHIVER_VERSION','COLON', 'COMMA', 'SEMI_COLON', 'PACKAGE',
              'VERSION', 'USES', 'EQUAL', 'RES')

    t_IMPORT_PACKAGE = r'Import\-Package'
    t_EXPORT_PACKAGE = r'Export\-Package'
    t_BUNDLE_VERSION = r'Bundle\-Version'
    
    t_BUNDLE_SYMBOLICNAME = r'Bundle\-SymbolicName'
    t_BUILD_JDK = r'Build\-JDK'
    t_BUNDLE_CLASSPATH = r'Bundle\-Classpath'
    t_BUNDLE_NAME = r'Bundle\-Name'
    
    t_MANIFEST_VERSION=r'Manifest\-Version.*'
    t_BUILD_BY=r'Build\-By.*'
    t_CREATED_BY=r'Created\-By.*'
    t_BUNDLE_VENDOR=r'Bundle\-Vendor.*'
    t_BUNDLE_MANIFESTVERSION=r'Bundle\-ManifestVersion.*'
    t_ARCHIVER_VERSION=r'Archiver\-Version.*'

    t_COLON = r'\:'
    t_VERSION = r'version'
    t_USES = r'uses'
    t_RES = r'resolution'
    t_COMMA = r'\,'
    t_SEMI_COLON = r'\;'
    t_PACKAGE = r'[a-zA-Z_][a-zA-Z0-9\_]+(\.[a-zA-Z_0-9][a-zA-Z_0-9]*)*'
    t_EQUAL = r'='
    t_ignore = " \t"
    
    def t_newline(self, t):
        r'\n+'
        t.lexer.lineno += t.value.count("\n")
    def t_error(self, t):
        print "Illegal character '%s'" % t.value[0]
        t.lexer.skip(1)

    def p_importpackage(self, p):
        '''importpackage : IMPORT_PACKAGE'''
#                        | IMPORT_PACKAGE COLON imports'''
        print 'importpackage'
        return
    #
    #def p_imports(self, p):
    #    '''imports : import
    #                | import COMMA import'''
    #    return
    #def p_import(self, p):
    #    '''import : package_names
    #            | package_names SEMI_COLON parameter'''
    #    print 'import'
    #    return;
    #
    #def p_package_names(self, p):
    #    '''package_names : package_name
    #                | package_name SEMI_COLON package_name'''
    #    print 'package names'
    #    return
    #
    #def p_package_name(self, p):
    #    '''package_name : PACKAGE
    #                    | PACKAGE SEMI_COLON PACKAGE'''
    #    print 'package name'
    #    return
    #
    #def p_parameter(self, p):
    #    '''parameter : COMMA'''
    #    print 'parameter '
    #    return
    #
    #def p_unused(self, p):
    #    '''unused :  MANIFEST_VERSION
    #            | BUILD_BY
    #            | CREATED_BY
    #            | BUNDLE_VENDOR
    #            | BUNDLE_MANIFESTVERSION
    #            | ARCHIVER_VERSION'''
    #    print 'unused'
    #    return  
    def p_error(self, p):
        if p:
            print "Syntax error at '%s'" % p.value
        else:
            print "Syntax error at EOF"
        raise SyntaxError   
    def parse(self, word, ast):
        assert ast != None
        assert word != None
        self.syntax_tree = ast
        self.run_parser(word)

if __name__ == '__main__':
    #parser = ManifestParser()
    #import_package = 'Import-Package: org.sun.jdmk.com'
    #print import_package
    
    #'com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";resolution:=optional,'+\
    #'javax.jms;version="[1.1.0, 2.0.0)";resolution:=optional,'+\
    #'javax.mail;version="[1.4.0, 2.0.0)";resolution:=optional,'+\
    #'javax.mail.internet;version="[1.4.0, 2.0.0)";resolution:=optional,'+\
    #'javax.management,javax.naming,javax.swing,javax.swing.border,'+\
    #'javax.swing.event,javax.swing.table,javax.swing.text,javax.swing.tree,'+\
    #'javax.xml.parsers,org.w3c.dom,org.xml.sax,org.xml.sax.helpers'
    #ast = abstract_syntax_tree()
    #parser.parse(import_package, ast)
    done
