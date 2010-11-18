#!/usr/bin/env python

################################################################################
#
# Auto Builder configuration settings
#
################################################################################

jar_path = [
'../Rifidi-SDK/lib/']

bundle_dirs = [
'org.aspectj.runtime_1.6.4.20090304172355',
'org.aspectj.weaver_1.6.4.20090304172355',
'org.rifidi.gnu.io_2.1.7'
]

src_path = [

'../com.ambient.labtrack',
'../com.ambient.labtrack.messages',
'../org.rifidi.edge.api',
'../org.rifidi.edge.init',
'../org.rifidi.edge.core',
'../org.rifidi.edge.app.diag']

do_not_package_libs = [
'org.eclipse.swt.gtk.linux.s390x_3.5.0.v3550b.jar',
'org.eclipse.compare.win32_1.0.0.I20090430-0408.jar',
'org.eclipse.swt.gtk.linux.x86_3.5.2.v3557f.jar',
'org.eclipse.jdt.launching.macosx_3.2.0.v20090527.jar',
'org.eclipse.core.net.linux.x86_1.1.0.I20081021.jar',
'org.eclipse.swt.motif.aix.ppc_3.5.2.v3557f.jar cglib-nodep_2.2.jar',
'org.eclipse.core.filesystem.qnx.x86_1.0.0.v20080604-1400.jar',
'org.eclipse.update.core.win32_3.2.100.v20080107.jar',
'org.eclipse.swt.gtk.solaris.x86_3.5.2.v3557f.jar', 
'org.eclipse.equinox.security.win32.x86_1.0.100.v20090520-1800.jar',
'org.eclipse.core.filesystem.linux.x86_1.3.0.R35x_v20091203-1235.jar', 
'org.eclipse.swt.gtk.solaris.sparc_3.5.2.v3557f.jar', 
'org.eclipse.equinox.security.macosx_1.100.0.v20090520-1800.jar',
'org.springframework.oxm-3.0.5.RELEASE.jar', 
'org.eclipse.core.filesystem.win32.x86_1.1.0.v20080604-1400.jar',
'com.springsource.org.junit_4.8.1.jar',
'org.rifidi.edge.readerplugin.acura.source_1.0.1.jar',
'org.eclipse.swt.win32.win32.x86_3.5.2.v3557f.jar',
'org.eclipse.swt.photon.qnx.x86_3.5.2.v3557f.jar',
'org.eclipse.swt.gtk.linux.s390_3.5.0.v3550b.jar',
'org.rifidi.edge.readerplugin.serialsensor_1.0.0.201009301229.jar', 
'org.eclipse.swt.motif.hpux.PA_RISC_3.5.0.HEAD.jar',
'org.eclipse.swt.gtk.linux.ppc_3.5.2.v3557f.jar',
'org.eclipse.swt.win32.win32.x86_64_3.5.2.v3557f.jar',
'org.rifidi.gnu.io.buildh4x0r_2.1.7.jar',
'org.eclipse.core.filesystem.linux.x86_64_1.1.0.R35x_v20091203-1235.jar',
'org.rifidi.edge.readerplugin.serialsensor.source_1.0.0.201009301229.jar',
'org.eclipse.ui.win32_3.2.100.v20090429-1800.jar', 
'org.eclipse.ui.cocoa_1.0.0.I20090525-2000.jar',
'org.eclipse.swt.wpf.win32.x86_3.5.2.v3557f.jar',
'org.eclipse.update.core.linux_3.2.100.v20081008.jar', 
'org.eclipse.swt.gtk.linux.x86_64_3.5.2.v3557f.jar', 
'org.eclipse.swt.motif.solaris.sparc_3.5.2.v3557f.jar',
'org.eclipse.swt.motif.hpux.ia64_32_3.5.2.v3557f.jar',
'org.eclipse.core.filesystem.aix.ppc_1.0.0.R35x_v20091203-1235.jar', 
'org.eclipse.core.filesystem.linux.ppc_1.0.100.v20080604-1400.jar',
'org.eclipse.core.filesystem.hpux.ia64_32_1.0.0.v20080604-1400.jar', 
'org.eclipse.swt.cocoa.macosx_3.5.2.v3557f.jar',
'org.eclipse.swt.cocoa.macosx.x86_64_3.5.2.v3557f.jar',
'org.eclipse.swt.motif.linux.x86_3.5.2.v3557f.jar',
'org.eclipse.ui.carbon_4.0.0.I20090525-2000.jar',
'org.eclipse.core.net.win32.x86_64_1.0.0.I20090306-1030.jar', 
'org.eclipse.core.filesystem.win32.x86_64_1.1.0.v20090316-0910.jar', 
'org.eclipse.core.net.win32.x86_1.0.0.I20080909.jar',
'org.eclipse.core.filesystem.macosx_1.2.0.R35x_v20091203-1235.jar', 
'org.eclipse.core.filesystem.hpux.PA_RISC_1.0.0.v20080604-1400.jar',
'org.eclipse.swt.carbon.macosx_3.5.2.v3557f.jar',
'org.eclipse.core.filesystem.solaris.sparc_1.1.0.R35x_v20091203-1235.jar',
'org.rifidi.edge.readerplugin.acura_1.0.1.jar',
'org.eclipse.jdt.launching.ui.macosx_1.0.0.v20090527.jar']

################################################################################
#
# Package Manager configuration settings
#
################################################################################

launchers = ['../Rifidi-SDK/launchers']
config_top_dir = 'config'
lib = 'lib'
debian_dir = 'DEBIAN'
deploy = 'deploy'
platforms = 'platform-dep'
configurations = ['ambient', 'edge']

