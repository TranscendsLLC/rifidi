#!/usr/bin/env python

import manifest

import subprocess
import os
import re

from conf import jar_path, src_path
from optparse import OptionParser
from os.path import join, abspath

def compare(first, second):
    missing = []
    for i in first:
        found = False
        
        for j in second:
            if i == j:
                found = True
                break
        if found == False:
            missing.append(i)
    return missing

def find(dir):
    jar_files = {}
    print 'jar_path: ', dir
    for root, dirs, files in os.walk(dir):
            for file in files:
                if file.endswith(r'.jar'):
                    jar_files[file] = file
    return jar_files
    
def compare_paths(jar_path, jar_path1):
    jar_set = find(jar_path)       
    jar_set1 = find(jar_path1)
    
    missing = compare(jar_set.keys(), jar_set1.keys())
    print '-'*80
    print jar_path+' contains the following bundles that '+jar_path1+' does not:'
    for i in missing:
        print '\t'+i
    print '-'*80
    missing = compare(jar_set1.keys(), jar_set.keys())        
    print jar_path1+' contains the following bundles that '+jar_path+' does not:'
    for i in missing:
        print '\t'+i
    print '-'*80    

def parse_params():
    parser = OptionParser(version="%prog 1.3.37")
    (options, args) = parser.parse_args()
    #assert options.keys().__len__() == 0
    print args
    return args
        
        
if __name__ == '__main__':
    usage = 'usage: compare.py <directory> <directory>'
    args = parse_params()
    print args
    if args.__len__() != 2:
        print usage
    compare_paths(args[0], args[1])
