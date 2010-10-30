#!/usr/bin/env python

import unittest
import manifest
import manifest_parser

class manifest_ast_test(unittest.TestCase):
#   Manifest-Version: 1.0

#Bundle-Classpath: .
#Built-By: ubuntu
#Bundle-Name: Apache Log4J
#Created-By: Apache Maven
#Bundle-Vendor: SpringSource
#Bundle-Version: 1.2.15
#Build-Jdk: 1.6.0
#Bundle-ManifestVersion: 2
#Import-Package: com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";resolution:
# =optional,javax.jms;version="[1.1.0, 2.0.0)";resolution:=optional,jav
# ax.mail;version="[1.4.0, 2.0.0)";resolution:=optional,javax.mail.inte
# rnet;version="[1.4.0, 2.0.0)";resolution:=optional,javax.management,j
# avax.naming,javax.swing,javax.swing.border,javax.swing.event,javax.sw
# ing.table,javax.swing.text,javax.swing.tree,javax.xml.parsers,org.w3c
# .dom,org.xml.sax,org.xml.sax.helpers
#Bundle-SymbolicName: com.springsource.org.apache.log4j
 
    def test_multiple_headers(self):
        pass
 
    def test_export_package(self):
        test = 'Export-Package: org.apache.log4j;version="1.2.15";uses:="org.apache.lo'+\
        ' g4j.helpers,org.apache.log4j.or,org.apache.log4j.spi",org.apache.log4'+\
        ' j.chainsaw;version="1.2.15";uses:="javax.swing,javax.swing.event,java'+\
        ' x.xml.parsers,org.apache.log4j,org.apache.log4j.spi,org.xml.sax",org.'+\
        ' apache.log4j.config;version="1.2.15";uses:="org.apache.log4j",org.apa'+\
        ' che.log4j.helpers;version="1.2.15";uses:="org.apache.log4j,org.apache'+\
        ' .log4j.spi",org.apache.log4j.jdbc;version="1.2.15";uses:="org.apache.'+\
        ' log4j.spi",org.apache.log4j.jmx;version="1.2.15";uses:="javax.managem'+\
        ' ent,org.apache.log4j",org.apache.log4j.lf5;version="1.2.15";uses:="or'+\
        ' g.apache.log4j.lf5.viewer,org.apache.log4j.spi",org.apache.log4j.lf5.'+\
        ' config;version="1.2.15",org.apache.log4j.lf5.util;version="1.2.15"'
        
        ast = manifest.Ast()
        parser = manifest_parser.ManifestParser()
        parser.parse(test, ast)
       
       
        for j in ast.bundles:
            for i in j.ipackages: 
                if i.name =='org.apache.log4j':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())

                elif i.name == 'org.apache.log4j.chainsaw':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())
                    
                elif i.name == 'org.apache.log4j.config':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())
                    
                elif i.name == 'org.apache.log4j.helpers':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())    
                    
                elif i.name == 'org.apache.log4j.jdbc':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())
                    
                elif i.name == 'org.apache.log4j.jmx':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())
                    
                elif i.name == 'org.apache.log4j.lf5':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())
                
                elif i.name == 'org.apache.log4j.lf5':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.2.15', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('1.2.15',i.e_version.__str__())
                else:
                    print i.name
                    self.assertTrue(False)
                   
        
#    def test_import_package(self):
#        test = 'Import-Package: com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";'+\
#            'resolution:=optional,javax.jms;version="[1.1.0, 2.0.0)";'+\
#            'resolution:=optional,javax.mail;version="[1.4.0, 2.0.0)";'+\
#            'resolution:=optional,javax.mail.internet;version="[1.4.0, 2.0.0)";'+\
#            'resolution:=optional,javax.management,javax.naming,'+\
#            'javax.swing,javax.swing.border,javax.swing.event,'+\
#            'javax.swing.table,javax.swing.text,javax.swing.tree,'+\
#            'javax.xml.parsers,org.w3c.dom,org.xml.sax,org.xml.sax.helpers'            
#
#        ast = manifest.Ast()
#        parser = manifest_parser.ManifestParser()
#        parser.parse(test, ast)
#
#        for j in ast.bundles:
#            for i in j.ipackages: 
#                if i.name =='com.sun.jdmk.comm':
#                    self.assertEquals(True, i.b_inclusive)
#                    self.assertEquals('5.1.0', i.b_version.__str__())
#                    self.assertEquals(True, i.e_inclusive)
#                    self.assertEquals('5.1.0',i.e_version.__str__())
#
#                elif i.name == 'javax.jms':
#                    self.assertEquals(True, i.b_inclusive)
#                    self.assertEquals('1.1.0', i.b_version.__str__())
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals('2.0.0',i.e_version.__str__())
#
#                elif i.name =='javax.mail':
#                    self.assertEquals(True, i.b_inclusive)
#                    self.assertEquals('1.4.0', i.b_version.__str__())
#                    self.assertEquals(False, i.e_inclusive)
#                    self.assertEquals('2.0.0',i.e_version.__str__())
#
#                elif i.name == 'javax.mail.internet':
#                    self.assertEquals(True, i.b_inclusive)
#                    self.assertEquals('1.4.0', i.b_version.__str__())
#                    self.assertEquals(False, i.e_inclusive)
#                    self.assertEquals('2.0.0',i.e_version.__str__())
#
#                elif i.name == 'javax.management':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False, i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                elif i.name == 'javax.naming':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False, i.e_inclusive)
#                    self.assertEquals(None,i.e_version)                    
#
#                elif i.name == 'javax.swing':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False, i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#
#                elif i.name == 'javax.swing.border':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                elif i.name == 'javax.swing.event':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                elif i.name == 'javax.swing.table':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#
#                elif i.name == 'javax.swing.text':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#
#                elif i.name == 'javax.swing.tree':
#                    
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)                    
#                
#                elif i.name == 'javax.xml.parsers':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                elif i.name == 'org.w3c.dom':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                elif i.name == 'org.xml.sax':    
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                elif i.name == 'org.xml.sax.helpers':
#                    self.assertEquals(False, i.b_inclusive)
#                    self.assertEquals(None, i.b_version)
#                    self.assertEquals(False,i.e_inclusive)
#                    self.assertEquals(None,i.e_version)
#                    
#                else:
#                    print i.name
#                    self.assertTrue(False)
#
#
#
#class manifest_test(unittest.TestCase):
#    def set_version(self, v, m, mi, mic, qual):
#        v.set_major(m)        
#        v.set_minor(mi)
#        v.set_micro(mic)
#        v.set_qual(qual)
#        return v
#           
#    def test_version(self):
#        v = manifest.Version()
#        v1 = manifest.Version()
#        v = self.set_version(v, 1 , 3, 3, '7')
#        v1 = self.set_version(v1, 1, 3, 3, '7')
#        self.assertEquals(True, v.isEqual(v1))
#        self.assertEquals(False, v.isLess(v1))
#        
#        self.set_version(v, 1, 2, 3, '7')
#        
#        self.assertEquals(False, v.isEqual(v1))
#        self.assertEquals(False, v.isLess(v1))
#        self.assertEquals(True, v1.isLess(v))
#        
#        self.set_version(v, 1, 3, 2, '7')
#        self.assertEquals(False, v.isEqual(v1))
#        self.assertEquals(False, v.isLess(v1))
#        self.assertEquals(True, v1.isLess(v))
#        
#        self.set_version(v, 1, 3, 3, '6')
#        self.assertEquals(False, v.isEqual(v1))
#        self.assertEquals(False, v.isLess(v1))
#        self.assertEquals(True, v1.isLess(v))
#        
#        self.set_version(v, 3, 1, 3, '3')
#        self.assertEquals(False, v.isEqual(v1))
#        self.assertEquals(True, v.isLess(v1))
#        self.assertEquals(False, v1.isLess(v))
#            
#    def test_import_package(self):
#        i = manifest.Package('java.lang.whatever')
#        v = manifest.Version()
#        i.set_version_range(v, True, v, True)
#        self.assertEquals(True, i.is_in_range(v))
#        v1 = manifest.Version()
#        self.set_version(v, 1, 3, 3, '7')
#        self.set_version(v1, 3, 1, 4, '1')
#        i.set_version_range(v, True, v1, False)
#        self.assertEquals(True, i.is_in_range(v))
#        self.assertEquals(False, i.is_in_range(v1))
#        
#        i.set_version_range(v, True, v1, True)
#        self.assertEquals(True, i.is_in_range(v))
#        self.assertEquals(True, i.is_in_range(v1))
#        
#        i.set_version_range(v, False, v1, True)
#        self.assertEquals(False, i.is_in_range(v))
#        self.assertEquals(True, i.is_in_range(v1))
#        
#        v2 = manifest.Version()
#        self.set_version(v2, 1, 6, 1, '8')
#        self.assertEquals(True, i.is_in_range(v2))
#        i.set_version_range(v, False, v1, False)
#        self.assertEquals(True, i.is_in_range(v2))
#        i.set_version_range(v, True, v1, False)
#        self.assertEquals(True, i.is_in_range(v2))
#        i.set_version_range(v, True, v1, True)
#        self.assertEquals(True, i.is_in_range(v2))
#        i1 = manifest.Package('org.eclipse.sucks.my.b41135')
#        
#        self.assertEquals(True, i.is_in_range(v))
#        self.assertEquals(True, i.is_in_range(v1))
#        self.assertEquals(True, i.is_in_range(v2))       
#        
 
if __name__ == "__main__":
    unittest.main()
