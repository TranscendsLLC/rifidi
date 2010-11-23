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
'../com.ambient.labtrack.test',
'../com.ambient.labtrack.messages',
'../org.rifidi.edge.api',
'../org.rifidi.edge.init',
'../org.rifidi.edge.core',
'../org.rifidi.edge.app.diag',
'../org.rifidi.edge.adapter.llrp']

do_not_package_libs = [
'com.springsource.org.junit_4.8.1.jar',
'org.rifidi.edge.readerplugin.acura.source_1.0.1.jar',
'org.rifidi.edge.readerplugin.serialsensor_1.0.0.201009301229.jar', 
'org.rifidi.gnu.io.buildh4x0r_2.1.7.jar',
'org.rifidi.edge.readerplugin.acura_1.0.1.jar'
]

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

