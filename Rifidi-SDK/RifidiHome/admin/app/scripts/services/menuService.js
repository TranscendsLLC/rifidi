/**
 * Created by Alejandro on 07/04/2015.
 */

app.service('MenuService', function($rootScope, $http, $interval, ServerService, CommonService, SensorService, AppService){


    var intervalRef;
    var self = this;

    //Method that creates and updates the tree view menu
    this.createUpdateMenu = function(){

        console.log('createUpdateMenu');

        //in rootScope attribute we have the menu elements from servers root
        if ( !$rootScope.elementList ){

            $rootScope.elementList = [];

        }

        var serversElement = $rootScope.elementList[0];

        if ( !serversElement ){

            var serversElement = [{
                "elementName": "Servers",
                "elementId": "servers",
                "elementType": "servers",
                "collapsed": false,
                "contextMenuId": "contextMenuServers",
                "iconClass":"server",
                "children": []
            }];

            $rootScope.elementList = serversElement;

        }

        var menuServers = serversElement.children;

        if ( !menuServers ){

            menuServers = [];
            $rootScope.elementList[0].children = menuServers;

        }

        //console.log('menuServers: ');
        //console.log(menuServers);

        //Query the updated list of servers
        ServerService.callServerListService()
            .success(function (data, status, headers, config) {

                console.log('callServerListService.success response');

                //We need to compare this new received list of servers with servers inside menu

                //iterate over the received servers
                //if exists -> then update
                //if does not exist -> then add
                data.forEach(function (newServer) {

                    var newServerExists = false;

                    //iterate over the current list of servers in menu
                    menuServers.forEach(function (menuServer) {

                        if(!newServerExists && (newServer.displayName == menuServer.displayName) ){

                            newServerExists = true;

                            menuServer.restProtocol = newServer.restProtocol;
                            menuServer.ipAddress = newServer.ipAddress;
                            menuServer.restPort = newServer.restPort;
                            menuServer.host = newServer.restProtocol + '://' + newServer.ipAddress + ':' + newServer.restPort;

                        }

                    });

                    if ( !newServerExists ){

                        //then add new server to menu list
                        newServer.collapsed = true;
                        newServer.elementName = newServer.displayName;
                        newServer.elementId = "server";
                        newServer.elementType = "server";
                        newServer.contextMenuId = "contextMenuServer";
                        newServer.children = [];
                        newServer.host = newServer.restProtocol + "://" + newServer.ipAddress + ":" + newServer.restPort;

                        changeServerStatusToConnecting(newServer);

                        //console.log('going to add new server to menu');

                        menuServers.push(newServer);

                    }


                });

                //iterate over the menu servers
                //if exists -> then do nothing, because it was already updated in previous loop
                //if does not exist -> then delete from menu list of servers
                var serversToDelete = [];
                menuServers.forEach(function (menuServer) {

                    var menuServerExists = false;

                    //iterate over the new received server list
                    data.forEach(function (newServer) {

                        if( (newServer.displayName == menuServer.displayName) ){

                            menuServerExists = true;

                        }

                    });

                    if ( !menuServerExists ){

                        //then delete the server from menu (add to a list, and then when exit this menu servers loop, delete)
                        serversToDelete.push(angular.copy(menuServer));

                    }

                });

                if ( serversToDelete.length > 0 ){

                    //There is at least one server to delete, then delete

                    //iterate the list of menu servers, and delete
                    serversToDelete.forEach(function (serverToDelete) {

                        //iterate over the menu servers
                        var currentIndex = -1;
                        menuServers.forEach(function (menuServer) {

                            currentIndex++;
                            if( serverToDelete.displayName == menuServer.displayName ){

                                menuServers.splice(currentIndex,1);
                            }

                        });

                    });

                }

                //order the menu list of servers
                menuServers.sort( CommonService.compareElements );

                //call the method to update menu servers status
                updateMenuServersStatus();


            })
            . error(function (data, status, headers, config) {
                console.log("callServerListService.fail response");

            });


        //var serviceUrl = host + '/apps';
        //return $http({ method: 'GET', url: serviceUrl });

    };

    var changeServerStatusToConnecting = function(server){

        server.status = 'CONNECTING';
        server.tooltipText = 'Connecting';
        server.allowSaveServerConfig = false;
        server.iconClass = "server-disconnected";

    };

    var changeServerStatusToConnected = function(server) {

        server.status = 'CONNECTED';
        server.iconClass = "server-connected";
        server.allowSaveServerConfig = true;
        server.tooltipText = 'Connected';

    };


    //Method that updates the status of menu servers
    var updateMenuServersStatus = function(){

        console.log('updateMenuServersStatus');

        //get the reference to menu servers
        var menuServers = $rootScope.elementList[0].children;

        menuServers.forEach(function (menuServer) {

            var protocol = menuServer.restProtocol;
            var ipAddress = menuServer.ipAddress;
            var port = menuServer.restPort;

            //For each server make an asynchronous call to test whether the ping rest operation returns success
            ServerService.callPingServerService(protocol, ipAddress, port)
                .success(function (data, status, headers, config) {

                    //console.log('updateMenuServersStatus.callPingServerService.success response');
                    //console.log('updateMenuServersStatus.callPingServerService.headers:');
                    //console.log('config:');
                    //console.log(config);

                    var serverTimestamp = ServerService.getPingTimestampFromReceivedData(data);

                    //find the right server to change status to 'CONNECTED'

                    var originalUrl = config.url;
                    //extract the '/apps' suffix to get the host name
                    var pingResponseHost = getHostFromConfigPingResponse(config);
                    console.log('pingResponseHost:');
                    console.log(pingResponseHost);

                    if (serverTimestamp) {
                        console.log("server ping from host: " + pingResponseHost + ", timestamp: " + serverTimestamp);

                        //change server connecting status to connected

                        menuServers.forEach(function (serverToTest) {

                            //console.log('inside menuservers loop');
                            //console.log('serverToTest.host: ' + serverToTest.host);
                            //console.log('pingResponseHost: ' + pingResponseHost);

                            if (serverToTest.host == pingResponseHost) {

                                console.log('going to change state to connected, on host:');
                                console.log(pingResponseHost);
                                //change server status to connected
                                changeServerStatusToConnected(serverToTest);

                                //update sensors for this server
                                updateMenuSensors(serverToTest);

                                //update reader types this server
                                updateMenuReaderTypes(serverToTest);

                                //update apps for this server
                                updateMenuApps(serverToTest);

                            }
                        });

                    }


                })
                . error(function (data, status, headers, config) {

                    console.log('updateMenuServersStatus.callPingServerService error');
                    //change server status to 'CONNECTING'

                    var pingResponseHost = getHostFromConfigPingResponse(config);
                    console.log('pingErrorResponse.pingResponseHost:');
                    console.log(pingResponseHost);

                    //change server to connecting status

                    menuServers.forEach(function (server) {

                        if (server.host == pingResponseHost) {

                            console.log('going to change state to connecting, on host:');
                            console.log(pingResponseHost);
                            //change server status
                            changeServerStatusToConnecting(server);

                            //remove child nodes
                            removeChildNodes(server);
                        }
                    });
                });
        });
    };

    //Updates the menu list of sensors for server
    var updateMenuSensors = function(serverElement){

        //if there is no children elements, then create it
        if ( !serverElement.children ){

            serverElement.children = [];

        }

        //if there is no 'Sensor Management' element, then create it
        if ( !serverElement.children[0] ){

            //Create the sensor management element
            var sensorManagementElement = {
                "host": serverElement.host,
                "restProtocol": serverElement.restProtocol,
                "ipAddress": serverElement.ipAddress,
                "restPort": serverElement.restPort,
                "elementName": "Sensor Management",
                "elementId": "sensorManagement",
                "elementType": "sensorManagement",
                "collapsed": true,
                "iconClass":"reader-cog",
                "contextMenuId": "contextMenuSensorManagement",
                "server": serverElement,
                "children": []
            }

            serverElement.children[0] = sensorManagementElement;

        }

        //Refresh the list of sensors for this server
        var menuSensors = serverElement.children[0].children;

        //console.log('serverElement.children[0]');
        //console.log(serverElement.children[0]);

        //console.log('menu sensors');
        //console.log(menuSensors);

        //Query the updated list of sensors
        SensorService.callSensorListService(serverElement.host)
            .success(function (data, status, headers, config) {

                console.log('callSensorListService.success response');

                //We need to compare this new received list with sensors inside server
                var newSensorList = SensorService.getSensorsFromReceivedData(data);

                //console.log('menuSensors for host: ' + serverElement.host);
                //console.log(menuSensors);

                //iterate over the received sensors
                //if exists -> then update
                //if does not exist -> then add
                newSensorList.forEach(function (newSensor) {

                    var newSensorExists = false;

                    //iterate over the current list of sensors in menu
                    menuSensors.forEach(function (menuSensor) {

                        if(!newSensorExists && (newSensor.id == menuSensor.elementId) ){

                            newSensorExists = true;

                            menuSensor.factoryID = newSensor.type;

                        }

                    });

                    if ( !newSensorExists ){

                        //then add new server to menu list

                        //complete the attributes for new sensor to add

                        newSensor.host = serverElement.host;
                        newSensor.elementName = newSensor.id;
                        newSensor.elementId = newSensor.id;
                        newSensor.elementType = 'sensor';
                        newSensor.collapsed = true;
                        newSensor.factoryID = newSensor.type;
                        newSensor.contextMenuId = 'contextMenuSensor';
                        newSensor.iconClass = 'reader';
                        newSensor.allowCreateSession = false;
                        newSensor.children = [];

                        //console.log('going to add new sensor to menu on server: ' + serverElement.host);

                        menuSensors.push(newSensor);

                        //console.log('new menu sensors');
                        //console.log(menuSensors);

                    }

                });

                //iterate over the menu sensors to check what are deleted
                //if menu sensor exists -> then do nothing, because it was already updated in previous loop
                //if menu sensor does not exist -> then delete from menu list of sensors

                var sensorsToDelete = [];

                //console.log('checking sensors to delete');
                menuSensors.forEach(function (menuSensor) {

                    var menuSensorExists = false;

                    //console.log('menuSensor:');
                    //console.log(menuSensor);

                    //iterate over the new received sensor list
                    newSensorList.forEach(function (newSensor) {

                        //console.log('newSensor:');
                        //console.log(newSensor);

                        if( (newSensor.id == menuSensor.id) ){

                            //console.log('newSensor.id = menuSensor.id');
                            menuSensorExists = true;

                        } else {

                            //console.log('newSensor.id <> menuSensor.id');

                        }

                    });

                    if ( !menuSensorExists ){

                        //then delete the sensor from menu (add to a list, and then when exit this menu sensors loop, delete)
                        sensorsToDelete.push(angular.copy(menuSensor));

                    }

                });


                if ( sensorsToDelete.length > 0 ){

                    //There is at least one sensor to delete, then delete

                    //iterate the list of sensors to delete, and delete
                    sensorsToDelete.forEach(function (sensorToDelete) {

                        //iterate over the menu sensors
                        var currentIndex = -1;
                        menuSensors.forEach(function (menuSensor) {

                            currentIndex++;
                            if( sensorToDelete.id == menuSensor.id ){

                                menuSensors.splice(currentIndex,1);
                            }

                        });

                    });

                }


                //order the menu list of sensors
                menuSensors.sort( CommonService.compareElements );

                //for each sensor update the sessions and executing command instances
                menuSensors.forEach(function (menuSensor) {

                    updateSessions(menuSensor);

                });

            })
            . error(function (data, status, headers, config) {

                console.log('updateMenuSensors.fail response on server: ' + serverElement.host);

                //Delete the list of sensors node
                menuSensors = [];

            });



    };

    //Updates the list of reader types for server
    var updateMenuReaderTypes = function(serverElement){

        //if there is no children elements, then create it
        if ( !serverElement.children ){

            serverElement.children = [];

        }

        //if there is no 'Command Management' element, then create it
        if ( !serverElement.children[1] ){

            //Create the command management element
            var commandManagementElement = {
                "host": serverElement.host,
                "elementName": "Command Management",
                "elementId": "commandManagement",
                "collapsed": true,
                "iconClass": 'cog',
                "children": []
            };

            serverElement.children[1] = commandManagementElement;

        }

        //Refresh the list of command types for this server
        var menuReaderTypes = serverElement.children[1].children;

        //console.log('serverElement.children[1]');
        //console.log(serverElement.children[1]);

        //console.log('menu reader types');
        //console.log(menuReaderTypes);

        //Query the updated list of reader types
        SensorService.callReaderTypesService(serverElement.host)
            .success(function (data, status, headers, config) {

                console.log('callReaderTypesService.success response');

                //We need to compare this new received list with reader types inside server
                var newReaderTypeList = SensorService.getReaderTypesFromReceivedData(data);

                //console.log('menuReaderTypes for host: ' + serverElement.host);
                //console.log(menuReaderTypes);

                //iterate over the received reader types
                //if exists -> then update
                //if does not exist -> then add
                newReaderTypeList.forEach(function (newReaderType) {

                    var newReaderTypeExists = false;

                    //iterate over the current list of reader types in menu
                    menuReaderTypes.forEach(function (menuReaderType) {

                        if(!newReaderTypeExists && (newReaderType.factoryID == menuReaderType.factoryID) ){

                            newReaderTypeExists = true;

                            menuReaderType.description = newReaderType.description;

                        }

                    });

                    if ( !newReaderTypeExists ){

                        //then add new reader type to menu list

                        //complete the attributes for new reader type to add
                        newReaderType.elementName = newReaderType.factoryID + ' Commands';
                        newReaderType.elementId = newReaderType.factoryID + ' Commands';
                        newReaderType.collapsed = true;
                        newReaderType.id = newReaderType.factoryID;
                        newReaderType.iconClass = 'reader-cog';
                        newReaderType.host = serverElement.host;
                        newReaderType.children = [];

                        //console.log('going to add new reader type to menu on server: ' + serverElement.host);

                        menuReaderTypes.push(newReaderType);

                        //console.log('new menu reader types');
                        //console.log(menuReaderTypes);

                    }

                });



                //iterate over the menu reader types to check what are deleted
                //if menu reader type exists -> then do nothing, because it was already updated in previous loop
                //if menu reader type does not exist -> then delete from menu list of reader types

                var readerTypesToDelete = [];

                //console.log('checking reader types to delete');
                menuReaderTypes.forEach(function (menuReaderType) {

                    var menuReaderTypeExists = false;

                    //console.log('menuReaderTypeExists:');
                    //console.log(menuReaderTypeExists);

                    //iterate over the new received reader types list
                    newReaderTypeList.forEach(function (newReaderType) {

                        //console.log('newReaderType:');
                        //console.log(newReaderType);

                        if( (newReaderType.factoryID == menuReaderType.factoryID) ){

                            //console.log('newReaderType.factoryID = menuReaderType.factoryID');
                            menuReaderTypeExists = true;

                        } else {

                            //console.log('newReaderType.factoryID <> menuReaderType.factoryID');

                        }

                    });

                    if ( !menuReaderTypeExists ){

                        //then delete the reader type from menu (add to a list, and then when exit this menu reader types loop, delete)
                        readerTypesToDelete.push(angular.copy(menuReaderType));

                    }

                });

                if ( readerTypesToDelete.length > 0 ){

                    //There is at least one reader type to delete, then delete

                    //iterate the list of reader types to delete, and delete
                    readerTypesToDelete.forEach(function (readerTypeToDelete) {

                        //iterate over the menu reader types
                        var currentIndex = -1;
                        menuReaderTypes.forEach(function (menuReaderType) {

                            currentIndex++;
                            if( readerTypeToDelete.id == menuReaderType.id ){

                                menuReaderTypes.splice(currentIndex,1);
                            }

                        });

                    });

                }


                //order the menu list of reader types
                menuReaderTypes.sort( CommonService.compareElements );

                //for each reader type update the command types
                menuReaderTypes.forEach(function (menuReaderType) {

                    updateCommandTypes(menuReaderType);

                });



            })
            . error(function (data, status, headers, config) {

                //console.log('updateMenuReaderTypes.fail response on server: ' + serverElement.host);

                //Delete the list of reader types node
                menuReaderTypes = [];

            });

    };

    //Updates the list of apps for server
    var updateMenuApps = function(serverElement){

        console.log('updateMenuApps');

        //if there is no children elements, then create it
        if ( !serverElement.children ){

            //console.log('!serverElement.children');
            serverElement.children = [];

        } else {

            //console.log('serverElement.children');
        }


        //if there is no 'App Management' element, then create it
        var appManagementElement = serverElement.children[2];
        if ( !appManagementElement ){

            //console.log('!appManagementElement');

            //Create the app management element

            appManagementElement = {
                "elementName": "App Management",
                "elementId": "App Management",
                "collapsed": true,
                "iconClass": "app-dev",
                "children": [],
                "host": serverElement.host
            };

            //console.log('serverElement.children[2]:');
            //console.log(serverElement.children[2]);
            serverElement.children[2] = appManagementElement;

        } else {

            //console.log('appManagementElement');

        }
/*
        console.log('new appManagementElement:');
        console.log(appManagementElement);

        console.log('new serverElement.children[2]:');
        console.log(serverElement.children[2]);
*/

        // if there is no 'App Groups' element, then create it
        var appGroupsElement = appManagementElement.children[0];
        if ( !appGroupsElement ) {

            //console.log('!appGroupsElement');

            appGroupsElement = {
                "elementName": "App Groups",
                "elementId": "App Group",
                "collapsed": true,
                "iconClass": "appgroups",
                "children": []
            };

            appManagementElement.children[0] = appGroupsElement;

        } else {

            //console.log('appGroupsElement');

        }

        //Refresh the list of app groups for this server
        var menuAppGroups = appGroupsElement.children;

        //console.log('menuAppGroups:');
        //console.log(menuAppGroups);

        //Query the updated list of app groups
        AppService.callAppListService(serverElement.host)
            .success(function (data, status, headers, config) {

                console.log('callAppListService.success response');

                //We need to compare this new received list with app groups inside server
                var newAppGroupList = AppService.getAppGroupsFromReceivedData(data);

                //console.log('newAppGroupList: ');
                //console.log(newAppGroupList);

                //console.log('menuAppGroupList for host: ' + serverElement.host);
                //console.log(menuAppGroups);

                //iterate over the received app groups
                //if exists -> then update
                //if does not exist -> then add

                newAppGroupList.forEach(function (newAppGroup) {

                    var newAppGroupExists = false;

                    //iterate over the current list of app groups in menu
                    menuAppGroups.forEach(function (menuAppGroup) {

                        if(!newAppGroupExists && (newAppGroup.groupName == menuAppGroup.groupName) ){

                            newAppGroupExists = true;

                            //menuReaderType.description = newReaderType.description;

                        }

                    });

                    if ( !newAppGroupExists ){

                        //then add new app group to menu list

                        //complete the attributes for new app group to add

                        newAppGroup.elementName = newAppGroup.groupName;
                        newAppGroup.elementId = newAppGroup.groupName;
                        newAppGroup.collapsed = true;
                        //newAppGroup.readzoneAppId": "",
                        newAppGroup.elementType = 'appGroup';
                        newAppGroup.iconClass = 'group';
                        newAppGroup.host = serverElement.host;
                        newAppGroup.children = [];

                        //console.log('going to add new app group to menu on server: ' + serverElement.host);

                        menuAppGroups.push(newAppGroup);

                        //console.log('new menu app groups:');
                        //console.log(menuAppGroups);

                    }

                });

                //iterate over the menu app groups to check what are deleted
                //if menu app group exists -> then do nothing, because it was already updated in previous loop
                //if menu app group does not exist -> then delete from menu list of app groups

                var appGroupsToDelete = [];

                //console.log('checking app groups to delete');
                menuAppGroups.forEach(function (menuAppGroup) {

                    var menuAppGroupExists = false;

                    //console.log('menuAppGroupExists:');
                    //console.log(menuAppGroupExists);

                    //iterate over the new received app group list
                    newAppGroupList.forEach(function (newAppGroup) {

                        //console.log('newAppGroup:');
                        //console.log(newAppGroup);

                        if( newAppGroup.groupName == menuAppGroup.groupName ){

                            //console.log('newAppGroup.groupName == menuAppGroup.groupName');
                            menuAppGroupExists = true;

                        } else {

                            //console.log('newAppGroup.groupName <> menuAppGroup.groupName');

                        }

                    });

                    if ( !menuAppGroupExists ){

                        //then delete the app group from menu (add to a list, and then when exit this menu app group loop, delete)
                        appGroupsToDelete.push(angular.copy(menuAppGroup));

                    }

                });

                if ( appGroupsToDelete.length > 0 ){

                    //There is at least one app group to delete, then delete

                    //iterate the list of app groups to delete, and delete
                    appGroupsToDelete.forEach(function (appGroupToDelete) {

                        //iterate over the menu reader types
                        var currentIndex = -1;
                        menuAppGroups.forEach(function (menuAppGroup) {

                            currentIndex++;
                            if( appGroupToDelete.groupName == menuAppGroup.groupName ){

                                menuAppGroups.splice(currentIndex,1);
                            }

                        });

                    });

                }


                //order the menu list of app groups
                menuAppGroups.sort( CommonService.compareElements );

                //for each app group update the Apps and Readzones containers
                menuAppGroups.forEach(function (menuAppGroup) {

                    updateAppContainer(menuAppGroup, data);

                    updateReadzoneContainer(menuAppGroup);

                });

            })
            . error(function (data, status, headers, config) {

                //console.log('menuAppGroups.fail response on server: ' + serverElement.host);

                //Delete the list of app groups
                menuAppGroups = [];

            });


    };

    var updateAppContainer = function(menuAppGroup, appsData) {

        console.log('updateAppContainer');

        //if there is no children , then create it
        if (!menuAppGroup.children) {

            //console.log('!serverElement.children');
            menuAppGroup.children = [];

        } else {

            //console.log('serverElement.children');
        }


        //if there is no 'Apps' element, then create it
        var appsElement = menuAppGroup.children[0];
        if (!appsElement) {

            //console.log('!appsElement');

            //Create the apps element

            appsElement = {
                "elementName": 'Apps',
                "elementId": 'Apps',
                "collapsed": true,
                "groupName": menuAppGroup.groupName,
                "iconClass": 'apps',
                "host": menuAppGroup.host,
                "children": []
            };

            menuAppGroup.children[0] = appsElement;

        } else {

            //console.log('appManagementElement');

        }

        //Add the apps for this apps element
        updateApps(appsElement, appsData);
    };

    var updateApps = function(appsElement, appsData){

        console.log('updateApps');
        //console.log('appsElement: ');
        //console.log(appsElement);

        var menuApps = appsElement.children;
        //console.log('menuApps for host: ' + appsElement.host);
        //console.log(menuApps);

        //We need to compare this new received list with apps inside apps element
        var newAppList = AppService.getAppsFromReceivedData(appsData, appsElement.groupName);

        //iterate over the received apps
        //if exists -> then update
        //if does not exist -> then add
        newAppList.forEach(function (newApp) {

            var newAppExists = false;

            //iterate over the current list of apps for this apps element
            menuApps.forEach(function (menuApp) {

                if(!newAppExists && (newApp.appId == menuApp.appId) ){

                    newAppExists = true;

                    //update attributes for new app element
                    menuApp.status = newApp.status;
                    menuApp.appName = newApp.appName;

                }

            });

            if ( !newAppExists ){

                //then add new app to menu list

                //complete the attributes for new app to add

                newApp.elementName = newApp.appName;
                newApp.elementId = newApp.appName;
                newApp.collapsed = true;
                newApp.number = newApp.appId;
                newApp.elementType = 'app';
                newApp.contextMenuId = 'contextMenuApp';
                newApp.host = appsElement.host;
                newApp.tooltipText = newApp.status;
                newApp.allowStartApp = false;
                newApp.allowStopApp = false;
                newApp.children = [];

                //console.log('going to add new app to menu');

                menuApps.push(newApp);

                //console.log('new menu apps');
                //console.log(menuApps);

            }

        });

        //iterate over the apps to check what are deleted
        //if menu app exists -> then do nothing, because it was already updated in previous loop
        //if menu app does not exist -> then delete from menu list of apps

        var appsToDelete = [];

        //console.log('checking apps to delete on group: ' + appsElement.groupName);
        menuApps.forEach(function (menuApp) {

            var menuAppExists = false;

            //console.log('menuApp:');
            //console.log(menuApp);

            //iterate over the new received app list
            newAppList.forEach(function (newApp) {

                //console.log('newApp:');
                //console.log(newApp);

                if( (newApp.appId == menuApp.appId) ){

                    //console.log('newApp.appId == menuApp.appId');
                    menuAppExists = true;

                } else {

                    //console.log('newApp.appId <> menuApp.appId');

                }

            });

            if ( !menuAppExists ){

                //then delete the app from menu (add to a list, and then when exit this apps loop, delete)
                appsToDelete.push(angular.copy(menuApp));

            }

        });


        if ( appsToDelete.length > 0 ){

            //There is at least one app to delete, then delete

            //iterate the list of apps to delete, and delete
            appsToDelete.forEach(function (appToDelete) {

                //iterate over the menu apps
                var currentIndex = -1;
                menuApps.forEach(function (menuApp) {

                    currentIndex++;
                    if( appToDelete.appId == menuApp.appId ){

                        menuApps.splice(currentIndex,1);
                    }

                });

            });

        }


        //order the menu list of apps
        menuApps.sort( CommonService.compareElements );

        //Update app state related attributes

        menuApps.forEach(function (menuApp) {

            if ( menuApp.status == 'STARTED' ){

                menuApp.allowStopApp = true;
                menuApp.iconClass = 'app-started';

            } else if ( menuApp.status == 'STOPPED' ) {

                menuApp.allowStartApp = true;
                menuApp.iconClass = 'app-stopped';

            }

        });

    };

    var updateReadzoneContainer = function(menuAppGroup) {

        console.log('updateReadzoneContainer');

        //if there is no children , then create it
        if (!menuAppGroup.children) {

            //console.log('!serverElement.children');
            menuAppGroup.children = [];

        } else {

            //console.log('serverElement.children');
        }

        //if there is no 'ReadZones' element, then create it
        var readzonesElement = menuAppGroup.children[1];
        if (!readzonesElement) {

            //console.log('!readzonesElement');

            //Create the readzones element

            var readzonesElement = {
                "elementName": "ReadZones",
                "elementId": "ReadZones",
                "collapsed": true,
                "groupName": menuAppGroup.groupName,
                "iconClass": "readzones",
                "host": menuAppGroup.host,
                "appId": menuAppGroup.readzoneAppId,
                "contextMenuId": "contextMenuReadZones",
                "elementType": "readZones",
                "children": []
            };

            menuAppGroup.children[1] = readzonesElement;

        } else {

            //console.log('appManagementElement');

        }

        //Add the readzones for this readzones element
        updateReadzones(readzonesElement);
    };

    var updateReadzones = function(readzonesElement, appsData){

        console.log('updateReadzones');
        //console.log('readzonesElement: ');
        //console.log(readzonesElement);

        //var menuReadzones = readzonesElement.children;
        //console.log('menuReadzones for host: ' + readzonesElement.host);
        //console.log(menuReadzones);

        //if there is no children elements, then create it
        if ( !readzonesElement.children ){

            //console.log('!readzonesElement.children');
            readzonesElement.children = [];

        } else {

            //console.log('readzonesElement.children');
        }


        //Refresh the list of readzones for this group
        var menuReadzones = readzonesElement.children;

        //console.log('menuReadzones:');
        //console.log(menuReadzones);

        //Query the updated list of readzones
        AppService.callReadzoneListService(readzonesElement.host, readzonesElement.appId)
            .success(function (data, status, headers, config) {

                console.log('callReadzoneListService.success response');

                //We need to compare this new received list with readzones inside server
                var newReadzoneList = AppService.getReadzonesFromReceivedData(data);

                //console.log('newReadzoneList: ');
                //console.log(newReadzoneList);

                //console.log('menuReadzones for host: ' + readzonesElement.host);
                //console.log(menuReadzones);

                //iterate over the received readzones
                //if exists -> then update
                //if does not exist -> then add

                newReadzoneList.forEach(function (newReadzone) {

                    var newReadzoneExists = false;

                    //iterate over the current list of readzones in menu
                    menuReadzones.forEach(function (menuReadzone) {

                        if(!newReadzoneExists && (newReadzone.readzone == menuReadzone.readzone) ){

                            newReadzoneExists = true;

                            //menuReaderType.description = newReaderType.description;

                        }

                    });

                    if ( !newReadzoneExists ){

                        //then add new readzone to menu list

                        //complete the attributes for new readzone to add

                        newReadzone.elementName = newReadzone.readzone;
                        newReadzone.elementId = newReadzone.readzone;
                        newReadzone.collapsed = true;
                        newReadzone.iconClass = 'readzone';
                        newReadzone.appId = readzonesElement.appId;
                        newReadzone.contextMenuId = 'contextMenuReadZone';
                        newReadzone.host = readzonesElement.host;
                        newReadzone.groupName = readzonesElement.groupName;
                        newReadzone.elementType = 'readZone';
                        newReadzone.children = [];

                        //console.log('going to add new readzone to menu on server: ' + readzonesElement.host);

                        menuReadzones.push(newReadzone);

                        //console.log('new menu readzones:');
                        //console.log(menuReadzones);

                    }

                });

                //iterate over the menu readzones to check what are deleted
                //if menu readzone exists -> then do nothing, because it was already updated in previous loop
                //if menu readzone does not exist -> then delete from menu list of readzones

                var readzonesToDelete = [];

                //console.log('checking readzones to delete');
                menuReadzones.forEach(function (menuReadzone) {

                    var menuReadzoneExists = false;

                    //console.log('menuReadzoneExists:');
                    //console.log(menuReadzoneExists);

                    //iterate over the new received readzone list
                    newReadzoneList.forEach(function (newReadzone) {

                        //console.log('newReadzone:');
                        //console.log(newReadzone);

                        if( newReadzone.readzone == menuReadzone.readzone ){

                            //console.log('newReadzone.readzone == menuReadzone.readzone');
                            menuReadzoneExists = true;

                        } else {

                            //console.log('newReadzone.readzone <> menuReadzone.readzone');

                        }

                    });

                    if ( !menuReadzoneExists ){

                        //then delete the readzone from menu (add to a list, and then when exit this menu readzone loop, delete)
                        readzonesToDelete.push(angular.copy(menuReadzone));

                    }

                });

                if ( readzonesToDelete.length > 0 ){

                    //There is at least one readzone to delete, then delete

                    //iterate the list of readzones to delete, and delete
                    readzonesToDelete.forEach(function (readzoneToDelete) {

                        //iterate over the menu readzones
                        var currentIndex = -1;
                        menuReadzones.forEach(function (menuReadzone) {

                            currentIndex++;
                            if( readzoneToDelete.readzone == menuReadzone.readzone ){

                                menuReadzones.splice(currentIndex,1);
                            }

                        });

                    });

                }

                //order the menu list of readzones
                menuReadzones.sort( CommonService.compareElements );

            })
            . error(function (data, status, headers, config) {

                console.log('callReadzoneListService.fail response on server: ' + readzonesElement.host);

                //Delete the list of readzones
                menuReadzones = [];

            });

    };

    var updateCommandTypes = function(menuReaderType){

        console.log('updateCommandTypes');
        //console.log('menuReaderType: ');
        //console.log(menuReaderType);

        var menuCommandTypes = menuReaderType.children;
        //console.log('menuCommandTypes for readertype: ' + menuReaderType.id + ', and host: ' + menuReaderType.host);
        //console.log(menuCommandTypes);

        //Query the updated list of command types
        SensorService.callCommandTypesService(menuReaderType.host)
            .success(function (data, status, headers, config) {

                console.log('updateCommandTypes.success response');

                //We need to compare this new received list with command types inside sensor type
                var newCommandTypesList = SensorService.getCommandTypesFromReceivedData(data, menuReaderType.factoryID);

                //iterate over the received command types
                //if exists -> then update
                //if does not exist -> then add
                newCommandTypesList.forEach(function (newCommandType) {

                    var newCommandTypeExists = false;

                    //iterate over the current list of command types for this reader type
                    menuCommandTypes.forEach(function (menuCommandType) {

                        if(!newCommandTypeExists && (newCommandType.factoryID == menuCommandType.factoryID) ){

                            newCommandTypeExists = true;
                            menuCommandType.description = newCommandType.description;

                        }

                    });

                    if ( !newCommandTypeExists ){

                        //then add new command type to menu list

                        //complete the attributes for new command type to add

                        newCommandType.elementName = newCommandType.factoryID;
                        newCommandType.elementId = newCommandType.factoryID;
                        newCommandType.collapsed = true;
                        newCommandType.factoryID = newCommandType.factoryID;
                        newCommandType.iconClass = 'folder';
                        newCommandType.elementType = 'commandType';
                        newCommandType.contextMenuId = 'contextMenuCommandType_commandManagement';
                        newCommandType.host = menuReaderType.host;
                        newCommandType.readerTypeElement = 'readerTypeElement';
                        newCommandType.children = [];

                        //console.log('going to add new command type to menu on reader type: ' + menuReaderType.factoryID);

                        menuCommandTypes.push(newCommandType);

                        //console.log('new menu command types');
                        //console.log(menuCommandTypes);

                    }

                });

                //iterate over the menu command types to check what are deleted
                //if menu command type exists -> then do nothing, because it was already updated in previous loop
                //if menu command type does not exist -> then delete from menu list of command types

                var commandTypesToDelete = [];

                //console.log('checking command types to delete on reader type: ' + menuReaderType.id);
                menuCommandTypes.forEach(function (menuCommandType) {

                    var menuCommandTypeExists = false;

                    //console.log('menuCommandType:');
                    //console.log(menuCommandType);

                    //iterate over the new received command type list
                    newCommandTypesList.forEach(function (newCommandType) {

                        //console.log('newCommandType:');
                        //console.log(newCommandType);

                        if( (newCommandType.factoryID == menuCommandType.factoryID) ){

                            //console.log('newCommandType.factoryID == menuCommandType.factoryID');
                            menuCommandTypeExists = true;

                        } else {

                            //console.log('newCommandType.factoryID <> menuCommandType.factoryID');

                        }

                    });

                    if ( !menuCommandTypeExists ){

                        //then delete the command type from menu (add to a list, and then when exit this menu command type loop, delete)
                        commandTypesToDelete.push(angular.copy(menuCommandType));

                    }

                });


                if ( commandTypesToDelete.length > 0 ){

                    //There is at least one command type to delete, then delete

                    //iterate the list of command types to delete, and delete
                    commandTypesToDelete.forEach(function (commandTypeToDelete) {

                        //iterate over the menu command types
                        var currentIndex = -1;
                        menuCommandTypes.forEach(function (menuCommandType) {

                            currentIndex++;
                            if( commandTypeToDelete.factoryID == menuCommandType.factoryID ){

                                menuCommandTypes.splice(currentIndex,1);
                            }

                        });

                    });

                }


                //order the menu list of command types
                menuCommandTypes.sort( CommonService.compareElements );

                //for each command types, update the command instances
                menuCommandTypes.forEach(function (menuCommandType) {

                    updateCommandInstances(menuCommandType);

                });

            })
            . error(function (data, status, headers, config) {

                console.log('updateCommandTypes.fail response on reader type: ' + menuReaderType.id);

                //Delete the list of command types
                menuCommandTypes = [];

            });

    };

    var updateCommandInstances = function(menuCommandType){

        console.log('updateCommandInstances');
        //console.log('menuCommandType: ');
        //console.log(menuCommandType);

        var menuCommandInstances = menuCommandType.children;
        //console.log('menuCommandInstances for command type: ' + menuCommandType.factoryID + ', and host: ' + menuCommandType.host);
        //console.log(menuCommandInstances);

        //Query the updated list of command instances
        SensorService.callCommandInstancesService(menuCommandType.host)
            .success(function (data, status, headers, config) {

                console.log('updateCommandInstances.success response');

                //We need to compare this new received list with command instances inside sensor type
                var newCommandInstancesList = SensorService.getCommandInstancesFromReceivedData(data, menuCommandType.factoryID);

                //iterate over the received command instances
                //if exists -> then update
                //if does not exist -> then add
                newCommandInstancesList.forEach(function (newCommandInstance) {

                    var newCommandInstanceExists = false;

                    //iterate over the current list of command instances for this command type
                    menuCommandInstances.forEach(function (menuCommandInstance) {

                        if(!newCommandInstanceExists && (newCommandInstance.commandID == menuCommandInstance.commandID) ){

                            newCommandInstanceExists = true;

                        }

                    });

                    if ( !newCommandInstanceExists ){

                        //then add new command instance to menu list

                        //complete the attributes for new command instance to add

                        newCommandInstance.elementName = newCommandInstance.commandID;
                        newCommandInstance.elementId = newCommandInstance.commandID;
                        newCommandInstance.id = newCommandInstance.commandID;
                        newCommandInstance.collapsed = true;
                        newCommandInstance.commandID = newCommandInstance.commandID;
                        newCommandInstance.factoryID = newCommandInstance.factoryID;
                        newCommandInstance.elementType = 'commandInstance_commandManagement';
                        newCommandInstance.iconClass = 'cog';
                        newCommandInstance.host = menuCommandType.host;
                        newCommandInstance.contextMenuId = 'contextMenuCommand_commandManagement';
                        newCommandInstance.readerType = menuCommandType;
                        //newCommandInstance.factoryElement = ' factoryElement,
                        newCommandInstance.children = [];

                        //console.log('going to add new command instance to menu on command type: ' + newCommandInstance.commandID);

                        menuCommandInstances.push(newCommandInstance);

                        //console.log('new menu command instances');
                        //console.log(menuCommandInstances);

                    }

                });

                //iterate over the menu command instances to check what are deleted
                //if menu command instance exists -> then do nothing, because it was already updated in previous loop
                //if menu command instance does not exist -> then delete from menu list of command instances

                var commandInstancesToDelete = [];

                //console.log('checking command instances to delete on command type: ' + menuCommandType.factoryID);
                menuCommandInstances.forEach(function (menuCommandInstance) {

                    var menuCommandInstanceExists = false;

                    //console.log('menuCommandInstance:');
                    //console.log(menuCommandInstance);

                    //iterate over the new received command instance list
                    newCommandInstancesList.forEach(function (newCommandInstance) {

                        //console.log('newCommandInstance:');
                        //console.log(newCommandInstance);

                        if( (newCommandInstance.commandID == menuCommandInstance.commandID) ){

                            //console.log('newCommandInstance.commandID == menuCommandInstance.commandID');
                            menuCommandInstanceExists = true;

                        } else {

                            //console.log('newCommandInstance.commandID <> menuCommandInstance.commandID');

                        }

                    });

                    if ( !menuCommandInstanceExists ){

                        //then delete the command instance from menu (add to a list, and then when exit this menu command instance loop, delete)
                        commandInstancesToDelete.push(angular.copy(menuCommandInstance));

                    }

                });


                if ( commandInstancesToDelete.length > 0 ){

                    //There is at least one command instance to delete, then delete

                    //iterate the list of command instances to delete, and delete
                    commandInstancesToDelete.forEach(function (commandInstanceToDelete) {

                        //iterate over the menu command instances
                        var currentIndex = -1;
                        menuCommandInstances.forEach(function (menuCommandInstance) {

                            currentIndex++;
                            if( commandInstanceToDelete.commandID == menuCommandInstance.commandID ){

                                menuCommandInstances.splice(currentIndex,1);
                            }

                        });

                    });

                }

                //order the menu list of command instances
                menuCommandInstances.sort( CommonService.compareElements );

            })
            . error(function (data, status, headers, config) {

                console.log('updateCommandInstances.fail response on command type: ' + menuCommandType.id);

                //Delete the list of command types
                menuCommandInstances = [];

            });

    };



    var updateSessions = function(sensorElement){

        console.log('updateSessions');
        //console.log('sensorElement: ');
        //console.log(sensorElement);

        var menuSessions = sensorElement.children;
        //console.log('menuSessions for sensor: ' + sensorElement.id + ', and host: ' + sensorElement.host);
        //console.log(menuSessions);

        //Query the updated list of sessions
        SensorService.callReaderStatusService(sensorElement.host, sensorElement.id)
            .success(function (data, status, headers, config) {

                console.log('updateSessions.success response');

                //We need to compare this new received list with sessions inside sensor
                var readerStatus = SensorService.getReaderStatusFromReceivedData(data);
                var newSessionList = readerStatus.sessions;

                //iterate over the received sessions
                //if exists -> then update
                //if does not exist -> then add
                newSessionList.forEach(function (newSession) {

                    var newSessionExists = false;

                    //iterate over the current list of sessions for this sensorElement
                    menuSessions.forEach(function (menuSession) {

                        if(!newSessionExists && (newSession.id == menuSession.sessionID) ){

                            newSessionExists = true;
                            menuSession.status = newSession.status;

                            //Update menu executing commands for this session
                            updateExecutingCommandsForSession(menuSession, newSession.executingCommands);

                        }

                    });

                    if ( !newSessionExists ){

                        //then add new session to menu list

                        //complete the attributes for new session to add

                        newSession.sessionID = newSession.id;
                        newSession.sensor = sensorElement;
                        newSession.elementName = 'session ' + newSession.id;
                        newSession.elementId = 'session ' + newSession.id;
                        newSession.collapsed = true;
                        newSession.tooltipText = newSession.id;
                        newSession.contextMenuId = 'contextMenuSession';
                        newSession.allowStartSession = false;
                        newSession.allowStopSession = false;
                        newSession.children = [];
                        newSession.host = sensorElement.host;

                        //console.log('going to add new session to menu on sensor: ' + readerStatus.sensor.id);

                        menuSessions.push(newSession);

                        //console.log('new menu sessions');
                        //console.log(menuSessions);

                        //Update menu executing commands for this session
                        updateExecutingCommandsForSession(newSession, newSession.executingCommands);

                    }

                });

                //iterate over the menu sessions to check what are deleted
                //if menu session exists -> then do nothing, because it was already updated in previous loop
                //if menu session does not exist -> then delete from menu list of sessions

                var sessionsToDelete = [];

                //console.log('checking sessions to delete on sensor: ' + readerStatus.sensor.id);
                menuSessions.forEach(function (menuSession) {

                    var menuSessionExists = false;

                    //console.log('menuSession:');
                    //console.log(menuSession);

                    //iterate over the new received session list
                    newSessionList.forEach(function (newSession) {

                        //console.log('newSession:');
                        //console.log(newSession);

                        if( (newSession.id == menuSession.id) ){

                            //console.log('newSession.id == menuSession.id');
                            menuSessionExists = true;

                        } else {

                            //console.log('newSession.id <> menuSession.id');

                        }

                    });

                    if ( !menuSessionExists ){

                        //then delete the session from menu (add to a list, and then when exit this menu sessions loop, delete)
                        sessionsToDelete.push(angular.copy(menuSession));

                    }

                });


                if ( sessionsToDelete.length > 0 ){

                    //There is at least one session to delete, then delete

                    //iterate the list of sessions to delete, and delete
                    sessionsToDelete.forEach(function (sessionToDelete) {

                        //iterate over the menu sessions
                        var currentIndex = -1;
                        menuSessions.forEach(function (menuSession) {

                            currentIndex++;
                            if( sessionToDelete.id == menuSession.id ){

                                menuSessions.splice(currentIndex,1);
                            }

                        });

                    });

                }


                //order the menu list of sensors
                menuSessions.sort( CommonService.compareElements );

                //for each session, update the icons and state related attributes
                menuSessions.forEach(function (menuSession) {

                    menuSession.tooltipText = menuSession.status;

                    //Assign the icon depending on status:
                    if (menuSession.status == 'CREATED' || menuSession.status == 'CLOSED'){

                        menuSession.iconClass = 'link-red';
                        menuSession.allowStartSession = true;
                        //not allowed to stop session
                        menuSession.allowStopSession = false;

                    } else if (menuSession.status == 'CONNECTING' || menuSession.status == 'LOGGINGIN'){

                        menuSession.iconClass = 'link-yellow';
                        //not allowed to start session
                        menuSession.allowStartSession = false;
                        menuSession.allowStopSession = true;


                    }  else if (menuSession.status == 'PROCESSING'){

                        menuSession.iconClass = 'link-green';
                        //not allowed to start session
                        menuSession.allowStartSession = false;
                        menuSession.allowStopSession = true;

                    }

                });

                //If sensor has no session, then allow to create one session
                var allowCreateSession = (menuSessions.length == 0);
                sensorElement.allowCreateSession = allowCreateSession;


            })
            . error(function (data, status, headers, config) {

                console.log('updateMenuSessions.fail response on sensor: ' + sensorElement.id);

                //Delete the list of sessions node
                menuSessions = [];

            });

    };

    var updateExecutingCommandsForSession = function(sessionElement, newCommands){

        //todo
        //iterate over the session element commands to check what commands are new or deleted
        //if command exists -> then update properties with new command
        //if command does not exist -> then delete command from menu commands

        //We need to compare this new received list newCommands with commands inside sessionElement
        var menuCommands = sessionElement.children;

        //iterate over the received commands
        //if exists -> then update
        //if does not exist -> then add
        newCommands.forEach(function (newCommand) {

            var newCommandExists = false;

            //iterate over the current list of commands for this sessionElement
            menuCommands.forEach(function (menuCommand) {

                if(!newCommandExists && (newCommand.id == menuCommand.commandID) ){

                    newCommandExists = true;
                    menuCommand.interval = newCommand.interval;

                }

            });

            if ( !newCommandExists ){

                //then add new command instance to session

                //complete the attributes for new command to add

                newCommand.elementName = newCommand.id;
                newCommand.elementId = newCommand.id;
                newCommand.collapsed = true;
                newCommand.iconClass = 'script-gear';
                newCommand.session = sessionElement;
                newCommand.elementType = 'commandInstance_sensor';
                newCommand.contextMenuId = 'contextMenuCommand_sensor';
                newCommand.commandType = newCommand.type;
                newCommand.commandID = newCommand.id;
                newCommand.host = sessionElement.host;
                newCommand.children = [];

                //console.log('going to add new command instance to menu on session: ' + sessionElement.id);

                menuCommands.push(newCommand);

                //console.log('new menu commands');
                //console.log(menuCommands);

            }

        });

        //iterate over the menu commands to check what are deleted
        //if menu command exists -> then do nothing, because it was already updated in previous loop
        //if menu command does not exist -> then delete from menu list of commands

        var commandsToDelete = [];

        //console.log('checking commands to delete on session: ' + sessionElement.id);
        menuCommands.forEach(function (menuCommand) {

            var menuCommandExists = false;

            //console.log('menuCommand:');
            //console.log(menuCommand);

            //iterate over the new received command list
            newCommands.forEach(function (newCommand) {

                //console.log('newCommand:');
                //console.log(newCommand);

                if( (newCommand.id == menuCommand.id) ){

                    //console.log('newCommand.id == menuCommand.id');
                    menuCommandExists = true;

                } else {

                    //console.log('newCommand.id <> menuCommand.id');

                }

            });

            if ( !menuCommandExists ){

                //then delete the command from menu (add to a list, and then when exit this menu commands loop, delete)
                commandsToDelete.push(angular.copy(menuCommand));

            }

        });


        if ( commandsToDelete.length > 0 ){

            //There is at least one command to delete, then delete

            //iterate the list of commands to delete, and delete
            commandsToDelete.forEach(function (commandToDelete) {

                //iterate over the menu commands
                var currentIndex = -1;
                menuCommands.forEach(function (menuCommand) {

                    currentIndex++;
                    if( commandToDelete.id == menuCommand.id ){

                        menuCommands.splice(currentIndex, 1);
                    }

                });

            });

        }


        //order the menu list of commands
        menuCommands.sort( CommonService.compareElements );

    };

    var removeChildNodes = function(element){

        console.log('removeChildNodes');
        //console.log('element');
        //console.log(element);
        //console.log('element.children');
        //console.log(element.children);

        if (element && element.children){

            element.children = [];

        }

    };


    //Gets the host protocol://ipaddress:port from config returned by ping rest operation
    //it assumes the rest operation is like protocol://ipaddress:port/ping
    var getHostFromConfigPingResponse = function(config){

        return config.url.substring(0, config.url.lastIndexOf("/"));

    };

    this.resolveAutoRefresh = function(){

        console.log('resolveAutoRefresh');

        //call the rest service to query uiproperties and see if enable autorefresh
        ServerService.callUIPropertiesService()
            .success(function (data, status, headers, config) {

                console.log('success calling getuiproperties');
                console.log('data.enableAutoRefresh:');
                console.log(data.enableAutoRefresh);

                $rootScope.enableAutoRefresh = data.enableAutoRefresh;

                console.log('data.autoRefreshDelay:');
                console.log(data.autoRefreshDelay);

                if ( $rootScope.enableAutoRefresh == 'true' ){

                    var autoRefreshDelay = data.autoRefreshDelay;

                    if (autoRefreshDelay > 0){

                        //start interval
                        console.log('going to start interval at ' + autoRefreshDelay  + ' milliseconds');
                        intervalRef = $interval(callCreateUpdateMenu, autoRefreshDelay);

                    }

                } else {

                    //if interval is running then stop
                    console.log('if interval is running then stop');
                    if ( angular.isDefined( intervalRef ) ){

                        $interval.cancel( intervalRef );

                    }



                }

            })
            .error(function (data, status, headers, config) {

                console.log('error calling getuiproperties');

            });




    };


    var callCreateUpdateMenu = function(){

        self.createUpdateMenu();

    }



});
