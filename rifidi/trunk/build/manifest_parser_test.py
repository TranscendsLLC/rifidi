#!/usr/bin/env python

import unittest
import manifest_parser

class ManifestParserTest(unittest.TestCase):
    
    def test_manifest_version(self):        
        test_string = 'Manifest-Version: 1.0\n'   
        parser = manifest_parser.ManifestParser()
        print test_string
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
        self.assertEquals('version_number', ast.stack.pop())
        #'version_number', 'not_used', 'header', 'manifest' 
        
    def test_bundle_doc_url(self):        
        test_string = 'Bundle-DocUrl: http://www.eclipse.org\n'
        parser = manifest_parser.ManifestParser()
#        print test_string
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
        self.assertEquals('url', ast.stack.pop())
        self.assertEquals('url', ast.stack.pop())
        self.assertEquals('url', ast.stack.pop())
        self.assertEquals('url', ast.stack.pop())
        
        test_string = 'Bundle-DocUrl: http://www.eclipse.org\n'
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
       
    def test_main_class(self):        
        test_string = 'Main-Class: org.eclipse.core.runtime.adaptor.EclipseStarter\n'
        parser = manifest_parser.ManifestParser()
    #    print test_string
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
    #    print 'a-'*80
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
    #  'package_name', 'package_name', 'not_used', 'header', 'manifest'
        
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())        
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
    #   package_name', 'package_name', 'package_name', 'package_name', 
    #   print ast.stack
        
    def test_bundle_localization(self):        
        test_string = 'Bundle-Localization: systembundle\n'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
        
    def test_bundle_required_exe_env(self):        
        test_string = 'Bundle-RequiredExecutionEnvironment: J2SE-1.5,OSGi/Minimum-1.2\n'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
#'not_used', 'header', 'manifest']
        
        self.assertEquals('jdk_version', ast.stack.pop())
        self.assertEquals('jdk_version', ast.stack.pop())                
        self.assertEquals('jdk_version', ast.stack.pop())
        self.assertEquals('jdk_version', ast.stack.pop())
        self.assertEquals('jdk_version', ast.stack.pop())                
#'jdk_version', 'jdk_version', 'jdk_version', 'jdk_version', 'jdk_version', 
        
        self.assertEquals('jdk_version', ast.stack.pop())
        self.assertEquals('jdk_version', ast.stack.pop())                
        self.assertEquals('jdk_version', ast.stack.pop())
        self.assertEquals('jdk_version', ast.stack.pop())
        self.assertEquals('jdk_version', ast.stack.pop())                
        self.assertEquals('jdk_version', ast.stack.pop())
#'jdk_version', 'jdk_version', 'jdk_version', 'jdk_version', 'jdk_version', 'jdk_version', 
        
    def test_bundle_symbolic_name(self):        
        test_string = 'Bundle-SymbolicName: org.eclipse.osgi; singleton:=true\n'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())        
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('bundle_symbolic_name', ast.stack.pop())
# 'bundle_symbolic_name', 'header', 'manifest']
        self.assertEquals('parameter', ast.stack.pop())
        self.assertEquals('directive', ast.stack.pop())
        self.assertEquals('bundle_symbolic_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())        
        self.assertEquals('package_name', ast.stack.pop())        
#'package_name', 'package_name', 'package_name', 'bundle_symbolic_name', 'directive', 'parameter',
        
    def test_bundle_activator(self):        
        test_string = 'Bundle-Activator: org.eclipse.osgi.framework.internal.core.SystemBundleActivator\n'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
        self.assertEquals('bundle_activator', ast.stack.pop())
        
# 'bundle_activator', 'not_used', 'header', 'manifest']
        
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())        
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        
#'package_name', 'package_name', 'package_name', 'package_name', 'package_name', 'package_name', 'package_name',
        test_string ='Bundle-Activator: org.eclipse.osgi.framework.internal.core.SystemBundl\n\v\f\x02'+' eActivator\n'
        parser = manifest_parser.ManifestParser()
        
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('not_used', ast.stack.pop())
        self.assertEquals('bundle_activator', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())        
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())
        print ast.stack        
    # 'package_name', 'package_name', 'package_name', 'package_name', 'package_name', 'package_name', 
        
    def test_bundle_version(self):        
        test_string = 'Bundle-Version: 3.5.2.R35x_v20100126\n'
        
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
#        self.assertEquals('', ast.stack.pop())
        
    def test_bundle_description(self):        
        test_string = 'Bundle-Description: %systemBundle\n'
        
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
#        self.assertEquals('', ast.stack.pop())
        
    def test_bundle_vendor(self):        
        test_string = 'Bundle-Vendor: %eclipse.org\n'
        
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
#        self.assertEquals('', ast.stack.pop())
        
    def test_manifest_version(self):        
        test_string = 'Bundle-ManifestVersion: 2\r\n'

        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        #self.assertEquals('', ast.stack.pop())
        
        
    def test_other(self):        
        test_string = 'Eclipse-ExtensibleAPI: true\r\n'
        
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        #self.assertEquals('', ast.stack.pop())
        
        test_string = 'Eclipse-SystemBundle: true\r\n'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        #self.assertEquals('', ast.stack.pop())
        
        test_string = 'Export-Service: org.osgi.service.packageadmin.PackageAdmin,org.osgi.se\n\v\f\x02'+\
                      ' rvice.permissionadmin.PermissionAdmin,org.osgi.service.startlevel.Sta\n\v\f\x02'+\
                      ' rtLevel,org.eclipse.osgi.service.debug.DebugOptions\r\n'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        
        test_string = 'Bundle-Copyright: %copyright'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        #
        test_string = 'Name: org/eclipse/osgi/framework/internal/core/AbstractBundle$2.class'
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        #
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        
        test_string = 'SHA1-Digest: +12NVGN/07NmVNXWMm+vjJdS69A='
        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
        #print '-'*80
        #print test_string
        #print ast.stack.pop()
        #print '-'*80
        
    def test_bundle_version(self):        
        test_string = 'Bundle-Version: 3.5.2.R35x_v20100126'

        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        
#        print '-'*80
#        print test_string
#        print ast.stack.pop()
#        print '-'*80
#        self.assertEquals('', ast.stack.pop())

    def test_export_package(self):        
        test_string = 'Export-Package: org.eclipse.osgi.event;version="1.0",org.eclipse.osgi.\n'+\
' framework.console;version="1.0",org.eclipse.osgi.framework.eventmgr;v\n'+\
' ersion="1.2",org.eclipse.osgi.framework.log;version="1.0",org.eclipse\n'+\
' .osgi.launch; version="1.0",org.eclipse.osgi.service.datalocation;ver\n'+\
' sion="1.2",org.eclipse.osgi.service.debug;version="1.1",org.eclipse.o\n'+\
' sgi.service.environment;version="1.2",org.eclipse.osgi.service.locali\n'+\
' zation;version="1.0",org.eclipse.osgi.service.pluginconversion;versio\n'+\
' n="1.0",org.eclipse.osgi.service.resolver;version="1.3",org.eclipse.o\n'+\
' sgi.service.runnable;version="1.1",org.eclipse.osgi.service.security;\n'+\
'  version="1.0",org.eclipse.osgi.service.urlconversion;version="1.0",o\n'+\
' rg.eclipse.osgi.signedcontent; version="1.0",org.eclipse.osgi.storage\n'+\
' manager;version="1.0",org.eclipse.osgi.util;version="1.1",org.osgi.fr\n'+\
' amework;version="1.5",org.osgi.framework.launch; version="1.0",org.os\n'+\
' gi.framework.hooks.service; version="1.0",org.osgi.service.condpermad\n'+\
' min;version="1.1",org.osgi.service.framework; version="1.0"; x-intern\n'+\
' al:=true,org.osgi.service.packageadmin;version="1.2",org.osgi.service\n'+\
' .permissionadmin;version="1.2",org.osgi.service.startlevel;version="1\n'+\
' .1",org.osgi.service.url;version="1.0",org.osgi.util.tracker;version=\n'+\
' "1.4.0",org.osgi.util.tracker;version="1.4.2",org.eclipse.core.runtim\n'+\
' e.adaptor;x-friends:="org.eclipse.core.runtime",org.eclipse.core.runt\n'+\
' ime.internal.adaptor;x-internal:=true,org.eclipse.core.runtime.intern\n'+\
' al.stats;x-friends:="org.eclipse.core.runtime",org.eclipse.osgi.basea\n'+\
' daptor;x-internal:=true,org.eclipse.osgi.baseadaptor.bundlefile;x-int\n'+\
' ernal:=true,org.eclipse.osgi.baseadaptor.hooks;x-internal:=true,org.e\n'+\
' clipse.osgi.baseadaptor.loader;x-internal:=true,org.eclipse.osgi.fram\n'+\
' ework.adaptor;x-internal:=true,org.eclipse.osgi.framework.debug;x-int\n'+\
' ernal:=true,org.eclipse.osgi.framework.internal.core;x-internal:=true\n'+\
' ,org.eclipse.osgi.framework.internal.protocol;x-internal:=true,org.ec\n'+\
' lipse.osgi.framework.internal.protocol.bundleentry;x-internal:=true,o\n'+\
' rg.eclipse.osgi.framework.internal.protocol.bundleresource;x-internal\n'+\
' :=true,org.eclipse.osgi.framework.internal.protocol.reference;x-inter\n'+\
' nal:=true,org.eclipse.osgi.framework.internal.reliablefile;x-internal\n'+\
' :=true,org.eclipse.osgi.framework.util;x-internal:=true,org.eclipse.o\n'+\
' sgi.internal.baseadaptor;x-internal:=true,org.eclipse.osgi.internal.c\n'+\
' omposite; x-internal:=true,org.eclipse.osgi.internal.loader;x-interna\n'+\
' l:=true,org.eclipse.osgi.internal.loader.buddy; x-internal:=true,org.\n'+\
' eclipse.osgi.internal.module;x-internal:=true,org.eclipse.osgi.intern\n'+\
' al.profile;x-internal:=true,org.eclipse.osgi.internal.resolver;x-inte\n'+\
' rnal:=true,org.eclipse.osgi.internal.serviceregistry; x-internal:=tru\n'+\
' e,org.eclipse.osgi.internal.permadmin;x-internal:=true,org.eclipse.os\n'+\
' gi.internal.provisional.service.security; x-friends:="org.eclipse.equ\n'+\
' inox.security.ui";version="1.0.0",org.eclipse.osgi.internal.provision\n'+\
' al.verifier;x-friends:="org.eclipse.update.core,org.eclipse.ui.workbe\n'+\
' nch,org.eclipse.equinox.p2.artifact.repository",org.eclipse.osgi.inte\n'+\
' rnal.service.security;x-friends:="org.eclipse.equinox.security.ui",or\n'+\
' g.eclipse.osgi.internal.signedcontent; x-internal:=true,org.eclipse.o\n'+\
' sgi.service.internal.composite; x-internal:=true\n'

        parser = manifest_parser.ManifestParser()
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)

        print '-'*80
        print test_string
        #print ast.stack.pop()
        print '-'*80

    def test_import_package_parser(self):
        parser = manifest_parser.ManifestParser()
        test_string = 'Import-Package: org.java;version="11.2"'
        ast = manifest_parser.DefaultAst()
        parser.parse(test_string, ast)
        self.assertEquals('manifest', ast.stack.pop())
        self.assertEquals('header', ast.stack.pop())
        self.assertEquals('import_package', ast.stack.pop())
        self.assertEquals('imports', ast.stack.pop())
        self.assertEquals('import', ast.stack.pop())
        self.assertEquals('parameter', ast.stack.pop())
        self.assertEquals('version', ast.stack.pop())
        self.assertEquals('version_string', ast.stack.pop())
        self.assertEquals('version_number', ast.stack.pop())
        self.assertEquals('package_names', ast.stack.pop())
        self.assertEquals('package_name', ast.stack.pop())

        self.assertEquals('package_name', ast.stack.pop())
        
        real_test = 'Import-Package: com.sun.jdmk.comm;version="[5.1.0, 5.1.0]";'+\
            'resolution:=optional,javax.jms;version="[1.1.0, 2.0.0)";'+\
            'resolution:=optional,javax.mail;version="[1.4.0, 2.0.0)";'+\
            'resolution:=optional,javax.mail.internet;version="[1.4.0, 2.0.0)";'+\
            'resolution:=optional,javax.management,javax.naming,'+\
            'javax.swing,javax.swing.border,javax.swing.event,'+\
            'javax.swing.table,javax.swing.text,javax.swing.tree,'+\
            'javax.xml.parsers,org.w3c.dom,org.xml.sax,org.xml.sax.helpers'            
        ast = manifest_parser.DefaultAst()
        parser.parse(real_test, ast)
        self.assertEqual('manifest', ast.stack.pop())
        self.assertEqual('header', ast.stack.pop())
        self.assertEqual('import_package', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        
        self.assertEqual('package_names', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('package_name', ast.stack.pop())
        self.assertEqual('imports', ast.stack.pop())                        
        self.assertEqual('import', ast.stack.pop())        

if __name__ == "__main__":
    unittest.main()
