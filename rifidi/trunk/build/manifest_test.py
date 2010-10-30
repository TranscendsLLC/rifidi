#!/usr/bin/env python

import unittest
import manifest
import manifest_parser

class manifest_test(unittest.TestCase):
    def set_version(self, v, m, mi, mic, qual):
        v.set_major(m)        
        v.set_minor(mi)
        v.set_micro(mic)
        v.set_qual(qual)
        return v
        
    def test_manifest_parser(self):
        test_manifest_file = open(\
            './com.springsource.org.apache.log4j-1.2.15.manifest.mf', 'r')
        test = test_manifest_file.read()
    
        parser = manifest.ManifestParser()
        bundle = parser.parse(test)
       
        for i in bundle.epackages: 
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
            elif i.name == 'org.apache.log4j.lf5.config':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.util':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.viewer':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.viewer.categoryexplorer':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
        
            elif i.name == 'org.apache.log4j.lf5.viewer.configure':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())        
        
            elif i.name == 'org.apache.log4j.lf5.viewer.images':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.net':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.nt':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.or':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.or.jms':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.or.sax':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.spi':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.varia':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
            elif i.name == 'org.apache.log4j.xml':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(True, i.e_inclusive)
                self.assertEquals('1.2.15',i.e_version.__str__())
                
            else:
                print i.name
                self.assertTrue(False)
                
            for i in bundle.ipackages: 
                if i.name =='com.sun.jdmk.comm':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('5.1.0', i.b_version.__str__())
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals('5.1.0',i.e_version.__str__())

                elif i.name == 'javax.jms':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.1.0', i.b_version.__str__())
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals('2.0.0',i.e_version.__str__())

                elif i.name =='javax.mail':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.4.0', i.b_version.__str__())
                    self.assertEquals(False, i.e_inclusive)
                    self.assertEquals('2.0.0',i.e_version.__str__())

                elif i.name == 'javax.mail.internet':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('1.4.0', i.b_version.__str__())
                    self.assertEquals(False, i.e_inclusive)
                    self.assertEquals('2.0.0',i.e_version.__str__())

                elif i.name == 'javax.management':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False, i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                elif i.name == 'javax.naming':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False, i.e_inclusive)
                    self.assertEquals(None,i.e_version)                    

                elif i.name == 'javax.swing':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False, i.e_inclusive)
                    self.assertEquals(None,i.e_version)

                elif i.name == 'javax.swing.border':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                elif i.name == 'javax.swing.event':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                elif i.name == 'javax.swing.table':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)

                elif i.name == 'javax.swing.text':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)

                elif i.name == 'javax.swing.tree':
                    
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)                    
                
                elif i.name == 'javax.xml.parsers':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                elif i.name == 'org.w3c.dom':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                elif i.name == 'org.xml.sax':    
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                elif i.name == 'org.xml.sax.helpers':
                    self.assertEquals(False, i.b_inclusive)
                    self.assertEquals(None, i.b_version)
                    self.assertEquals(False,i.e_inclusive)
                    self.assertEquals(None,i.e_version)
                    
                else:
                    print i.name
                    self.assertTrue(False)  
                               
    def test_version(self):
        v = manifest.Version()
        v1 = manifest.Version()
        v = self.set_version(v, 1 , 3, 3, '7')
        v1 = self.set_version(v1, 1, 3, 3, '7')
        self.assertEquals(True, v.isEqual(v1))
        self.assertEquals(False, v.isLess(v1))
        
        self.set_version(v, 1, 2, 3, '7')
        
        self.assertEquals(False, v.isEqual(v1))
        self.assertEquals(False, v.isLess(v1))
        self.assertEquals(True, v1.isLess(v))
        
        self.set_version(v, 1, 3, 2, '7')
        self.assertEquals(False, v.isEqual(v1))
        self.assertEquals(False, v.isLess(v1))
        self.assertEquals(True, v1.isLess(v))
        
        self.set_version(v, 1, 3, 3, '6')
        self.assertEquals(False, v.isEqual(v1))
        self.assertEquals(False, v.isLess(v1))
        self.assertEquals(True, v1.isLess(v))
        
        self.set_version(v, 3, 1, 3, '3')
        self.assertEquals(False, v.isEqual(v1))
        self.assertEquals(True, v.isLess(v1))
        self.assertEquals(False, v1.isLess(v))
            
    def test_package(self):
        i = manifest.Package('java.lang.whatever')
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
        i1 = manifest.Package('org.eclipse.sucks.my.b41135')
        
        self.assertEquals(True, i.is_in_range(v))
        self.assertEquals(True, i.is_in_range(v1))
        self.assertEquals(True, i.is_in_range(v2))       
        
 
if __name__ == "__main__":
    unittest.main()
