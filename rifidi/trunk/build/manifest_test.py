#!/usr/bin/env python

import unittest
import manifest

class manifest_test(unittest.TestCase):
    def set_version(self, v, m, mi, mic, qual):
        v.set_major(m)        
        v.set_minor(mi)
        v.set_micro(mic)
        v.set_qual(qual)
            
    def test_version(self):
        v = manifest.Version()
        v1 = manifest.Version()
        self.set_version(v, 1 , 3, 3, '7')
        self.set_version(v, 1, 3, 3, '7')
        self.assertEquals(True, v.isEqual(v1))
        self.assserEquals(False, v.isLess(v1))

        self.set_version(v, 1, 2, 3, '7')
        self.assserEquals(False, v.isEqual(v1))
        self.assserEquals(False, v.isLess(v1))
        self.assertEquals(True, v1.isLess(v))
    
        self.set_version(v, 1, 3, 2, '7')
        self.assserEquals(False, v.isEqual(v1))
        self.assserEquals(False, v.isLess(v1))
        self.assertEquals(True, v1.isLess(v))
        
        self.set_version(v, 1, 3, 3, '6')
        self.assserEquals(False, v.isEqual(v1))
        self.assserEquals(False, v.isLess(v1))
        self.assertEquals(True, v1.isLess(v))
        
        self.set_version(v, 3, 1, 3, '3')
        self.assserEquals(False, v.isEqual(v1))
        self.assserEquals(False, v.isLess(v1))
        self.assertEquals(False, v1.isLess(v))
            
    def test_import_package(self):
        i = manifest.ImportPackage('java.lang.whatever')
        v = manifest.Version()
        i.set_version_range(v, True, v, True)
        self.assertEquals(True, i.is_in_range(v))
        v1 = manifest.Version()
        self.set_version(v, 1, 3, 3, '7')
        self.set_version(v1, 3, 1, 4, '1')
        i.set_version_range(v, True, v1, False)
        self.assertEquals(True, i.is_in_range(v))
        self.assertEquals(False, i.is_in_range(v1))
        
        i.set_version_range(v, True, v1, True)
        self.assertEquals(True, i.is_in_range(v))
        self.assertEquals(True, i.is_in_range(v1))
        
        i.set_version_range(v, False, v1, True)
        self.assertEquals(False, i.is_in_range(v))
        self.assertEquals(True, i.is_in_range(v1))
        
        v2 = manifest.Version()
        self.set_version(v2, 1, 6, 1, '8')
        self.assertEquals(True, i.is_in_range(v2))
        i.set_version_range(v, False, v1, False)
        self.assertEquals(True, i.is_in_range(v2))
        i.set_version_range(v, True, v1, False)
        self.assertEquals(True, i.is_in_range(v2))
        i.set_version_range(v, True, v1, True)
        self.assertEquals(True, i.is_in_range(v2))
        
    def test_import_package_parser(self):
        parser = manifest.ManifestParser()
        test_string = 'Import-Package: org.java;version="11.2"'
        print test_string
        ast = manifest.abstract_syntax_tree()
        parser.parse(test_string, ast)
        
        test_string = 'Import-Package: org.java;version="[11.2,11.5.3)"'
        print test_string
        ast = manifest.abstract_syntax_tree()
        parser.parse(test_string, ast)
        
        test_string = 'Import-Package: org.java;resolution:=optional'
        print test_string
        ast = manifest.abstract_syntax_tree()
        parser.parse(test_string, ast)
            
        test_string = 'Import-Package: org.java;version="[11.2,11.5.3)";resolution:=optional'
        print test_string
        ast = manifest.abstract_syntax_tree()
        parser.parse(test_string, ast)
          
        real_test = 'Import-Package: com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";'+\
            'resolution:=optional,javax.jms;version="[1.1.0, 2.0.0)";'+\
            'resolution:=optional,javax.mail;version="[1.4.0, 2.0.0)";'+\
            'resolution:=optional,javax.mail.internet;version="[1.4.0, 2.0.0)";'+\
            'resolution:=optional,javax.management,javax.naming,'+\
            'javax.swing,javax.swing.border,javax.swing.event,'+\
            'javax.swing.table,javax.swing.text,javax.swing.tree,'+\
            'javax.xml.parsers,org.w3c.dom,org.xml.sax,org.xml.sax.helpers'
            
        print real_test
        ast = manifest.abstract_syntax_tree()
        parser.parse(real_test, ast)
        
    def number(self):
        parser = manifest.ManifestParser()
        test_string = '909'
        print test_string
        ast = manifest.abstract_syntax_tree()
        parser.parse(test_string, ast)
        self.assertEquals(ast.pop(), 'token')        

    def digit(self):
        parser = manifest.ManifestParser()
        test_string = '98a'
        print test_string
        ast = manifest.abstract_syntax_tree()
        parser.parse(test_string, ast)
        print ast.stack
        #self.assertEquals(ast.pop(), 'digit')    
    def manifest_version(self):
        parser = manifest.ManifestParser()
        
if __name__ == "__main__":
    unittest.main()
    #
    #def p_importpackage(self, p):
    #    '''importpackage : IMPORT_PACKAGE
    #                    | IMPORT_PACKAGE COLON imports'''
    #    print 'importpackage'
    #    return
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