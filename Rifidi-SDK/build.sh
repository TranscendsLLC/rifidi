#! /bin/sh
### BEGIN INIT INFO
# Provides:          Rifidi Edge Server
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Author : Manoj Lakhtariya
# Short-Description: compile the Rifidi Edge Server
# Description:       This file is the maven comile script for the Rifidi Edge Server
### END INIT INFO
#

# compile all modules

#mvn clean install
#cd ..
#cd org.rifidi.edge.api
#mvn clean install
#cd ..
#cd org.rifidi.edge.init
#mvn clean install
#cd ..
#cd org.rifidi.edge
#mvn clean install
#cd ..
#cd org.rifidi.edge.adapter.llrp
#mvn clean install
#cd ..
#cd org.rifidi.edge.adapter.opticon
#mvn clean install
#cd ..
#cd org.rifidi.edge.tools
#mvn clean install
#cd ..


#cleanup the current bundles
#cd Rifidi-SDK/
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge-5.4.0.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge.adapter.llrp-2.0.7.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge.adapter.opticon-1.0.1.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge.api-1.3.0.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge.init-1.0.1.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge.tools-1.0.0.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge.adapter.thingmagic6-1.0.0.jar
#sudo rm -f rifidi/debian/usr/local/sbin/rifidi/plugins/org.rifidi.edge-5.4.0.jar

# copy new bundles to /Rifidi_SDK/rifidi/usr/local/sbin/rifidi/plugins

#find ~/.m2/repository/org/rifidi/ -name '*.jar' -exec cp '{}' ./rifidi/debian/usr/local/sbin/rifidi/plugins \;

# build .deb package in /Rifidi_SDK/rifidi folder

cd rifidi/
dpkg-deb --build debian

mv debian.deb ./rifidi-edge-3.8.1-linux.gtk.x86.deb
