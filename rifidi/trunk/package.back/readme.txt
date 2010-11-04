matt@matt-desktop ~/Documents/package $ vim readme.txt
matt@matt-desktop ~/Documents/package $ cd package/
matt@matt-desktop ~/Documents/package/package $ dpkg -b data rifidi.deb
dpkg-deb: building package `rifidi' in `rifidi.deb'.

matt@matt-desktop ~/Documents/package/package $ sudo dpkg -i rifidi.deb 
Selecting previously deselected package rifidi.
(Reading database ... 119614 files and directories currently installed.)
Unpacking rifidi (from rifidi.deb) ...
Setting up rifidi (1.2) ...
 System start/stop links for /etc/init.d/rifidiserver already exist.
 * Starting Rifidi Edge Server rifidiserver                                                                                                                   * Rifidi Edge Server started                                                                                                                         [ OK ] 

matt@matt-desktop ~/Documents/package/package $ ps ax | ps rifidi



w00t!1!11!
