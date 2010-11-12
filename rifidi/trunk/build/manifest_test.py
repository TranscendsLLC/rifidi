#!/usr/bin/env python

import unittest
import manifest
import sys

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
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())

            elif i.name == 'org.apache.log4j.chainsaw':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
                
            elif i.name == 'org.apache.log4j.config':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.helpers':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())    
            elif i.name == 'org.apache.log4j.jdbc':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.jmx':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.config':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.util':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.viewer':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.lf5.viewer.categoryexplorer':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
        
            elif i.name == 'org.apache.log4j.lf5.viewer.configure':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())        
        
            elif i.name == 'org.apache.log4j.lf5.viewer.images':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.net':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.nt':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.or':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.or.jms':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.or.sax':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.spi':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.varia':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
            elif i.name == 'org.apache.log4j.xml':
                self.assertEquals(True, i.b_inclusive)
                self.assertEquals('1.2.15', i.b_version.__str__())
                self.assertEquals(False, i.e_inclusive)
                self.assertEquals(str(sys.maxint),i.e_version.__str__())
                
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
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'javax.naming':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'javax.swing':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)                    
                    
                elif i.name == 'javax.swing.border':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)                    
                    
                elif i.name == 'javax.swing.event':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)                    
                    
                elif i.name == 'javax.swing.table':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'javax.swing.text':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'javax.swing.tree':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)

                elif i.name == 'javax.xml.parsers':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'org.w3c.dom':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'org.xml.sax':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                elif i.name == 'org.xml.sax.helpers':
                    self.assertEquals(True, i.b_inclusive)
                    self.assertEquals('0', i.b_version.major)
                    self.assertEquals(True, i.e_inclusive)
                    self.assertEquals(str(sys.maxint),i.e_version.major)
                    
                else:
                    print i.name
                    self.assertTrue(False)  
    
    def test_bundle_version(self):
        test = 'Bundle-Version: 3.5.2.R35x_v20100126\n'
        p = manifest.ManifestParser()
        bundle = p.parse(test)
        self.assertEquals(bundle.version.__str__(), '3.5.2.R35x_v20100126')
    def test_export_package(self):
        test = 'Export-Package: org.apache.commons.pool;version=1.4, org.apache.common\r\n s.pool.impl;version=1.4'
        p = manifest.ManifestParser()
        bundle = p.parse(test)
        self.assertEquals(2, bundle.epackages.__len__())
        self.assertEquals(bundle.epackages[0].name, 'org.apache.commons.pool')
        self.assertEquals(bundle.epackages[0].b_version.__str__(), '1.4')
        self.assertEquals(bundle.epackages[0].e_version.__str__(), str(sys.maxint))
        self.assertEquals(bundle.epackages[1].name, 'org.apache.commons.pool.impl')
        self.assertEquals(bundle.epackages[1].b_version.__str__(), '1.4')
        self.assertEquals(bundle.epackages[1].e_version.__str__(), str(sys.maxint))
        
        
    def test_version(self):
        v = manifest.Version()
        v1 = manifest.Version()
        v = self.set_version(v, 1 , 3, 3, '7')
        v1 = self.set_version(v1, 1, 3, 3, '7')
        self.assertEquals(True, v.is_equal(v1))
        self.assertEquals(False, v.is_less(v1))
        
        self.set_version(v, 1, 2, 3, '7')
        
        self.assertEquals(False, v.is_equal(v1))
        self.assertEquals(False, v.is_less(v1))
        self.assertEquals(True, v1.is_less(v))
        
        self.set_version(v, 1, 3, 2, '7')
        self.assertEquals(False, v.is_equal(v1))
        self.assertEquals(False, v.is_less(v1))
        self.assertEquals(True, v1.is_less(v))
        
        self.set_version(v, 1, 3, 3, '6')
        self.assertEquals(False, v.is_equal(v1))
        self.assertEquals(False, v.is_less(v1))
        self.assertEquals(True, v1.is_less(v))
        
        self.set_version(v, 3, 1, 3, '3')
        self.assertEquals(False, v.is_equal(v1))
        self.assertEquals(True, v.is_less(v1))
        self.assertEquals(False, v1.is_less(v))
        
        v2 = manifest.Version()
        v3 = manifest.Version()

        v2.set_major(3)
        v2.set_minor(3)
        v2.set_micro(0)

        v3.set_major(3)
        v3.set_minor(3)
        

        
        self.assertEquals(False, v3.is_less(v2))
        self.assertEquals(False, v2.is_less(v3))
        self.assertEquals(True, v2.is_equal(v3))
        self.assertEquals(True, v3.is_equal(v2))
        
        
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
        
        
        v2 = manifest.Version()
        v3 = manifest.Version()

        v2.set_major(3)
        v2.set_minor(3)
        v2.set_micro(0)

        v3.set_major(3)
        v3.set_minor(3)
        
        m = manifest.Package('java.is.teh.s0x0r')
        m1 = manifest.Package('java.is.teh.s0x0r')
        
        m.set_version_range(v2, True, v2, True)
        m1.set_version_range(v3, True, v3, True)
        
        self.assertEquals(True, m.is_in_range(m1.b_version))
        self.assertEquals(True, m1.is_in_range(m.b_version))
        
        
if __name__ == "__main__":
    unittest.main()
