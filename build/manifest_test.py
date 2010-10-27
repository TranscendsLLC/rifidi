#!/usr/bin/env python

import unittest
import manifest
import manifest_parser

class manifest_ast_test(unittest.TestCase):
    def test_simple(self):
        ast = manifest.Ast()
        test = 'org.java.junit'
        parser = manifest_parser.ManifestParser()
        parser.parse(test, ast)

class manifest_test(unittest.TestCase):
    def set_version(self, v, m, mi, mic, qual):
        v.set_major(m)        
        v.set_minor(mi)
        v.set_micro(mic)
        v.set_qual(qual)
        return v
            
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
 
if __name__ == "__main__":
    unittest.main()
