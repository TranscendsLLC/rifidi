#!/usr/bin/env python

Manifest-Version: 1.0
Export-Package: org.apache.log4j;version="1.2.15";uses:="org.apache.lo
 g4j.helpers,org.apache.log4j.or,org.apache.log4j.spi",org.apache.log4
 j.chainsaw;version="1.2.15";uses:="javax.swing,javax.swing.event,java
 x.xml.parsers,org.apache.log4j,org.apache.log4j.spi,org.xml.sax",org.
 apache.log4j.config;version="1.2.15";uses:="org.apache.log4j",org.apa
 che.log4j.helpers;version="1.2.15";uses:="org.apache.log4j,org.apache
 .log4j.spi",org.apache.log4j.jdbc;version="1.2.15";uses:="org.apache.
 log4j.spi",org.apache.log4j.jmx;version="1.2.15";uses:="javax.managem
 ent,org.apache.log4j",org.apache.log4j.lf5;version="1.2.15";uses:="or
 g.apache.log4j.lf5.viewer,org.apache.log4j.spi",org.apache.log4j.lf5.
 config;version="1.2.15",org.apache.log4j.lf5.util;version="1.2.15";us
 es:="org.apache.log4j.lf5,org.apache.log4j.lf5.viewer",org.apache.log
 4j.lf5.viewer;version="1.2.15";uses:="javax.swing,javax.swing.event,j
 avax.swing.table,org.apache.log4j.lf5,org.apache.log4j.lf5.util,org.a
 pache.log4j.lf5.viewer.categoryexplorer",org.apache.log4j.lf5.viewer.
 categoryexplorer;version="1.2.15";uses:="javax.swing,javax.swing.even
 t,javax.swing.tree,org.apache.log4j.lf5",org.apache.log4j.lf5.viewer.
 configure;version="1.2.15";uses:="javax.swing.tree,org.apache.log4j.l
 f5.viewer,org.w3c.dom",org.apache.log4j.lf5.viewer.images;version="1.
 2.15",org.apache.log4j.net;version="1.2.15";uses:="javax.jms,javax.ma
 il,javax.mail.internet,javax.naming,org.apache.log4j,org.apache.log4j
 .spi,org.w3c.dom",org.apache.log4j.nt;version="1.2.15";uses:="org.apa
 che.log4j,org.apache.log4j.spi",org.apache.log4j.or;version="1.2.15";
 uses:="org.apache.log4j.spi",org.apache.log4j.or.jms;version="1.2.15"
 ,org.apache.log4j.or.sax;version="1.2.15",org.apache.log4j.spi;versio
 n="1.2.15";uses:="org.apache.log4j,org.apache.log4j.or",org.apache.lo
 g4j.varia;version="1.2.15";uses:="org.apache.log4j,org.apache.log4j.s
 pi",org.apache.log4j.xml;version="1.2.15";uses:="javax.xml.parsers,or
 g.apache.log4j,org.apache.log4j.config,org.apache.log4j.spi,org.w3c.d
 om,org.xml.sax"
Bundle-Classpath: .
Built-By: ubuntu
Bundle-Name: Apache Log4J
Created-By: Apache Maven
Bundle-Vendor: SpringSource
Bundle-Version: 1.2.15
Build-Jdk: 1.6.0
Bundle-ManifestVersion: 2
Import-Package: com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";resolution:
 =optional,javax.jms;version="[1.1.0, 2.0.0)";resolution:=optional,jav
 ax.mail;version="[1.4.0, 2.0.0)";resolution:=optional,javax.mail.inte
 rnet;version="[1.4.0, 2.0.0)";resolution:=optional,javax.management,j
 avax.naming,javax.swing,javax.swing.border,javax.swing.event,javax.sw
 ing.table,javax.swing.text,javax.swing.tree,javax.xml.parsers,org.w3c
 .dom,org.xml.sax,org.xml.sax.helpers
Bundle-SymbolicName: com.springsource.org.apache.log4j
Archiver-Version: Plexus Archiver


    def p_manifest(self, p):
        '''manifest : directive
                    | manifest directive'''
    def p_directive(self, p):
        '''directive : "ID DASH ID COLON imports'''
        if p[0]  == 'Import' and p[2] == 'Package':
            print 'Import package'
        else:
            print 'Other keyword directive'
            p[0] = None

    def p_imports(self, p):
        '''imports : import
                | import COMMA import'''
        print 'imports'

    def p_import(self, p):
        '''import : package_names
            | package_names SEMI_COLON parameter'''
        print 'import'
        
    def p_package_names(self, p):
        '''package_names : package_name
                        | package_name SEMI_COLON package_name'''
        print 'package fucking name'
        
    def p_package_name(self, p):
        '''package_name : ID
                    | ID DOT ID'''
        print 'package name'

    def p_parameter(self, p):
        '''parameter : '''
        print parmemeter

    def p_final(self, p):
 #        '''final : ID DASH COLON COMMA SEMI_COLON EQUAL DOT
        '''final : ID DASH COLON COMMA SEMI_COLON EQUAL DOT
                | DASH
                | COLON 
                | COMMA
                | SEMI_COLON
                | EQUAL
                | DOT'''
        print 'match'


class formula_factory:
    def create_formula(self, phi):
        parser = formulae_parser.parser()
        handler = formula_ast()
        #print ' parse formula: ', phi
        parser.parse(phi, handler)
        formula = handler.get_tree()
        #pdb.set_trace()
        return formula


class formula_ast(formulae_parser.default_syntax_tree):
    def __init__(self):
        self.log = logging.getLogger('root.formulea.formula_ast')
        self.root = None

    def get_tree(self):
        return self.root

    def lookup(self, element, root):
        ret = None
        if type(root) != formula:
            return ret
        elif root.get_children().__len__() == 0:
            assert root.get_type() == PROPOSITION or root.get_type() == CONSTANT
            if root.get_formula() == element:
                ret = root
        else:
            for i in root.get_children():
                ret = self.lookup(element, root)
                if ret != None:
                    return ret
        return ret

    def binary(self, lhs, operator, rhs, type):
        if type == IMPLIES:
            lhs = lhs.negate()
            operator = '|'
            type = DISJUNCTION
        self.log.debug('parse-binary: lhs-operand-rhs-type %s %s %s %s', lhs,
                       operator, rhs, type)
        bin = binary(lhs, operator, rhs, type)
        self.root = bin
        return bin

    def unary(self, operator, operand, type):
        self.log.debug('parse-unary: operator-operand-type %s %s %s', operator,
                       operand, type)
        if operator == '{F}':
            self.root = self.binary(self.constant('-T-', True), '{U}', operand,
                                    UNTIL)
        elif operator == '{G}':
            self.root = self.binary(self.constant('-F-', False), '{R}', operand,
                                    RELEASES)
        elif operator == '~' and operand.get_composition() == COMPOSITE:
            #print ' here '
            self.root = operand.negate()
        else:
            self.root = unary(operator, operand, type)
        return self.root

    def prop(self, element):
        prop = self.lookup(element, self.root)
        if prop == None:
            prop = proposition(element, False)
        self.log.debug('parse-prop: %s:%s', prop, element)
        self.root = prop
        return prop

    def constant(self, element, value):
        const = self.lookup(element, self.root)
        if const == None:
            const = constant(element, value)
        assert const.get_value() == value
        self.log.debug('parse-constant: %s %s',const,  element)
        self.root = const
        return const

    def subformula(self, lparen, formula, rparen):
        sub = subformula(formula)
        self.root = sub
        self.log.debug('parse-enclosed-formula: %s:%s:%s', lparen, sub, rparen)
        return sub


class subformulae_visitor:

    def __init__(self):
        self.subformulae = ()

    def find(self, declf):
        ret = False
        for i in self.subformulae:
            if declf == i:
                ret = True
        return ret

    def visit_decl(self, declf):
        #found = self.find(declf)
        #if not found:
        self.subformulae += (declf,)

    def visit_unary(self, unaryf):
        assert unaryf.get_children().__len__() == 1
        self.subformulae += (unaryf,)
        if unaryf.get_type() != NEGATION:
            self.visit_node(unaryf.get_children()[0])


    def visit_subformula(self, subf):
        assert subf.get_children().__len__() == 1
        self.visit_node(subf.get_children()[0])
        return

    def visit_binary(self, binaryf):
        assert binaryf.get_children().__len__() == 2
        self.subformulae += (binaryf,)
        self.visit_node(binaryf.get_children()[0])
        self.visit_node(binaryf.get_children()[1])

    def visit_node(self, node):
        node.accept(self)




class Package:
    def __init__(self, name, version):
        self.name = name
        self.version = version

class Bundle:
    def __init__(self, name, version):
        self.name = name
        self.version = version
        
    def add_exported_package(self, package):
        self.exports.add(package)
        
    def add_imported_package(self, package):
        self.imports.add(packages)
        


class ManifestParser:
    def __init__(self, file):
        self.file = file
    def parse(self):
        return
    def getRequiredBundles(self):
        return
    def getRequiredPackages(self):
        return
    def getExportedPackages(self):
        return
