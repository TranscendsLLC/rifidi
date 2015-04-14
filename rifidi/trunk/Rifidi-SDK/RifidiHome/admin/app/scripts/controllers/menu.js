/**
 * Created by tws on 09/01/2015.
 */

'use strict';
/**
 * @ngdoc function
 * @name rifidiApp.controller:MenuCtrl
 * @description
 * # AboutCtrl
 * Controller of the rifidiApp
 */
var module = angular.module('rifidiApp')
  .controller('MenuController', function ($rootScope, $scope, $http, $location, ngDialog, TreeViewPainting, commonVariableService,
                                          ServerService, SensorService, AppService, CommonService) {

        var getSuccessMessage = function () {
            return commonVariableService.getSuccessMessage();
        };

        var setSuccessMessage = function (msg) {
            if (msg != '') {
                commonVariableService.setSuccessMessage(msg);
            }
        };


        $scope.go = function ( path ) {
            $location.path( path );
        };


        $scope.elementSelected={};
        $scope.temporaryNode = {
              children: []
          };
        //$scope.propertyType="servers";

        //console.log("set propertyType = servers");
        $scope.mode = undefined;

        $scope.done = function () {
          /* reset */
          $scope.elementTree.currentNode.selected = undefined;
          $scope.elementTree.currentNode = undefined;
          $scope.mode = undefined;
        };

          /*
        $scope.callPaintTreeView = function(){
            paintTreeView();
        };
        */

        $scope.addServerDone = function () {
          console.log("addServerDone");
          /* add server */
          if( $scope.temporaryNode.elementName  ) {//TODO: Validate all values or use a require validation
              $scope.temporaryNode.elementType="server";
              $scope.temporaryNode.displayName= $scope.temporaryNode.elementName;
              console.log($scope.elementTree.currentNode);
              $scope.elementTree.currentNode.children.push( angular.copy($scope.temporaryNode) );
              //console.log("$scope.elementTree.currentNode.children");
              //console.log($scope.elementTree.currentNode.children);
          }
          /* reset */
          $scope.temporaryNode.elementType = "";
          $scope.temporaryNode.elementName = "";
          $scope.temporaryNode.displayName="";

            $scope.temporaryNode.ipAddress="";
            $scope.temporaryNode.restProtocol="";
            $scope.temporaryNode.restPort="";


            $scope.done();
        };

          var deleteSensor = function(){
              console.log("deleteSensor called");

              //call the rest operation to delete sensor
              $http.get($scope.elementSelected.host + '/deletereader/' + $scope.elementSelected.elementId)
                  .success(function (data, status, headers, config) {

                      var deleteReaderCommandResponse;
                      if (window.DOMParser) {
                          var parser = new DOMParser();
                          deleteReaderCommandResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                          deleteReaderCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                          deleteReaderCommandResponse.async = false;
                          deleteReaderCommandResponse.loadXML(data);
                      }

                      //get the xml response and extract the message response
                      var message = deleteReaderCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      //$scope.setCommandPropertiesResponseStatus.message = message;

                      if (message == 'Success') {
                          console.log("success deleting sensor");
                          $rootScope.operationSuccessMsg = "Success deleting sensor";
                          TreeViewPainting.paintTreeView();

                      } else {
                          var deleteReaderCommandDescription = deleteReaderCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                          //$scope.setCommandPropertiesResponseStatus.description = setCommandPropertiesDescription;
                          console.log("fail deleting sensor");
                          console.log(deleteReaderCommandDescription);

                          //show modal dialog with error
                          showErrorDialog('Error deleting sensor: ' + deleteReaderCommandDescription);

                      }


                  }).
                  error(function (data, status, headers, config) {
                      console.log("error deleting sensor");

                      //show modal dialog with error
                      showErrorDialog('Error deleting sensor');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });


          };

        var deleteReadZone = function(){
            console.log("deleteReadZone called");

            var host = angular.copy($scope.elementSelected.host);
            var appId = angular.copy($scope.elementSelected.appId);
            var readZone = angular.copy($scope.elementSelected.readzone);

            //call the rest operation to delete sensor
            $http.get(host + '/deleteReadZone/' + appId + "/" + readZone)
                .success(function (data, status, headers, config) {

                    var deleteReadZoneCommandResponse;
                    if (window.DOMParser) {
                        var parser = new DOMParser();
                        deleteReadZoneCommandResponse = parser.parseFromString(data, "text/xml");
                    }
                    else // Internet Explorer
                    {
                        deleteReadZoneCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                        deleteReadZoneCommandResponse.async = false;
                        deleteReadZoneCommandResponse.loadXML(data);
                    }

                    //get the xml response and extract the message response
                    var message = deleteReadZoneCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;


                    if (message == 'Success') {
                        console.log("success deleting readzone");
                        $rootScope.operationSuccessMsg = "Success deleting readzone";
                        TreeViewPainting.paintTreeView();

                    } else {
                        var deleteReadZoneCommandDescription = deleteReadZoneCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        console.log("fail deleting read zone");
                        console.log(deleteReadZoneCommandDescription);

                        //show modal dialog with error
                        showErrorDialog('Error deleting read zone: ' + deleteReadZoneCommandDescription);

                    }


                }).
                error(function (data, status, headers, config) {
                    console.log("error deleting read zone");

                    //show modal dialog with error
                    showErrorDialog('Error deleting read zone');


                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });


        };

          var deleteCommand = function(commandId){

              console.log("deleteCommand called");

              //call the rest operation to delete command
              $http.get($scope.elementSelected.host + '/deletecommand/' + commandId)
                  .success(function (data, status, headers, config) {

                      var deleteCommandCommandResponse;
                      if (window.DOMParser) {
                          var parser = new DOMParser();
                          deleteCommandCommandResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                          deleteCommandCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                          deleteCommandCommandResponse.async = false;
                          deleteCommandCommandResponse.loadXML(data);
                      }

                      //get the xml response and extract the message response
                      var message = deleteCommandCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      //$scope.setCommandPropertiesResponseStatus.message = message;

                      if (message == 'Success') {
                          console.log("success deleting command");

                          $rootScope.operationSuccessMsg = "Success deleting command";

                          //update scope success messsage
                          TreeViewPainting.paintTreeView();

                      } else {
                          var deleteCommandCommandDescription = deleteCommandCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;

                          console.log("fail deleting command");
                          console.log(deleteCommandCommandDescription);

                          //show modal dialog with error
                          showErrorDialog('Error deleting command: ' + deleteCommandCommandDescription);

                      }


                  }).
                  error(function (data, status, headers, config) {
                      console.log("error deleting command");

                      //show modal dialog with error
                      showErrorDialog('Error deleting command');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });

          };

          $scope.createSession = function(){

              console.log("createSession called");

              //call the rest operation to create session
              $http.get($scope.elementSelected.host + '/createsession/' + $scope.elementSelected.elementId)
                  .success(function (data, status, headers, config) {

                      var createSessionCommandResponse;
                      if (window.DOMParser) {
                          var parser = new DOMParser();
                          createSessionCommandResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                          createSessionCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                          createSessionCommandResponse.async = false;
                          createSessionCommandResponse.loadXML(data);
                      }

                      //get the xml response and extract the message response
                      var message = createSessionCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      //$scope.setCommandPropertiesResponseStatus.message = message;

                      if (message == 'Success') {
                          console.log("success creating session");
                          $rootScope.operationSuccessMsg = "Success creating session";
                          TreeViewPainting.paintTreeView();

                      } else {
                          var createSessionCommandDescription = createSessionCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                          console.log("fail creating session");
                          console.log(createSessionCommandDescription);

                          //show modal dialog with error
                          showErrorDialog('Error creating session: ' + createSessionCommandDescription);

                      }


                  }).
                  error(function (data, status, headers, config) {
                      console.log("error creating session");

                      //show modal dialog with error
                      showErrorDialog('Error creating session');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });
          };

          var deleteSession = function(){
              console.log("deleteSession called");

              //call the rest operation to delete session
              $http.get($scope.elementSelected.sensor.host + '/deletesession/' + $scope.elementSelected.sensor.elementId + "/" + $scope.elementSelected.sessionID)
                  .success(function (data, status, headers, config) {

                      var deleteSessionCommandResponse;
                      if (window.DOMParser) {
                          var parser = new DOMParser();
                          deleteSessionCommandResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                          deleteSessionCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                          deleteSessionCommandResponse.async = false;
                          deleteSessionCommandResponse.loadXML(data);
                      }

                      //get the xml response and extract the message response
                      var message = deleteSessionCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      if (message == 'Success') {
                          console.log("success deleting session");
                          $rootScope.operationSuccessMsg = "Success deleting session";
                          TreeViewPainting.paintTreeView();

                      } else {
                          var deleteSessionCommandDescription = deleteSessionCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                          console.log("fail deleting session");
                          console.log(deleteSessionCommandDescription);

                          //show modal dialog with error
                          showErrorDialog('Error deleting session: ' + deleteSessionCommandDescription);

                      }


                  }).
                  error(function (data, status, headers, config) {
                      console.log("error deleting session");

                      //show modal dialog with error
                      showErrorDialog('Error deleting session');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });


          };

          $scope.startSession = function(){
              console.log("startSession called");

              //call the rest operation to start session
              $http.get($scope.elementSelected.sensor.host + '/startsession/' + $scope.elementSelected.sensor.elementId + "/" + $scope.elementSelected.sessionID)
                  .success(function (data, status, headers, config) {

                      var startSessionCommandResponse;
                      if (window.DOMParser) {
                          var parser = new DOMParser();
                          startSessionCommandResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                          startSessionCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                          startSessionCommandResponse.async = false;
                          startSessionCommandResponse.loadXML(data);
                      }

                      //get the xml response and extract the message response
                      var message = startSessionCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      if (message == 'Success') {
                          console.log("success starting session");
                          $rootScope.operationSuccessMsg = "Success starting session";
                          TreeViewPainting.paintTreeView();

                      } else {
                          var startSessionCommandDescription = startSessionCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                          console.log("fail starting session");
                          console.log(startSessionCommandDescription);

                          //show modal dialog with error
                          showErrorDialog('Error starting session: ' + startSessionCommandDescription);

                      }


                  }).
                  error(function (data, status, headers, config) {
                      console.log("error starting session");

                      //show modal dialog with error
                      showErrorDialog('Error starting session');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });


          };

          $scope.stopSession = function(){
              console.log("stopSession called");

              //call the rest operation to stop session
              $http.get($scope.elementSelected.sensor.host + '/stopsession/' + $scope.elementSelected.sensor.elementId + "/" + $scope.elementSelected.sessionID)
                  .success(function (data, status, headers, config) {

                      var stopSessionCommandResponse;
                      if (window.DOMParser) {
                          var parser = new DOMParser();
                          stopSessionCommandResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                          stopSessionCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                          stopSessionCommandResponse.async = false;
                          stopSessionCommandResponse.loadXML(data);
                      }

                      //get the xml response and extract the message response
                      var message = stopSessionCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      if (message == 'Success') {
                          console.log("success stopping session");
                          $rootScope.operationSuccessMsg = "Success stopping session";
                          TreeViewPainting.paintTreeView();

                      } else {
                          var stopSessionCommandDescription = stopSessionCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                          console.log("fail stopping session");
                          console.log(stopSessionCommandDescription);

                          //show modal dialog with error
                          showErrorDialog('Error stopping session: ' + stopSessionCommandDescription);

                      }


                  }).
                  error(function (data, status, headers, config) {
                      console.log("error stopping session");

                      //show modal dialog with error
                      showErrorDialog('Error stopping session');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                  });


          };


          function showErrorDialog(errorMsg) {

              $scope.errorMsg = errorMsg;

              ngDialog.openConfirm({template: 'errorDialogTmpl.html',

                  scope: $scope, //Pass the scope object if you need to access in the template

                  showClose: false,

                  closeByEscape: true,

                  closeByDocument: false

              }).then(

                  function(value) {

                      //confirm operation
                      console.log("first");

                  },

                  function(value) {

                      //Cancel or do nothing
                      console.log("second");
                      console.log("value: " + value);




                  }

              );

          };

          $scope.openDeleteServerDialog = function() {

              ngDialog.openConfirm({template: 'deleteServerDialogTmpl.html',

                  scope: $scope, //Pass the scope object if you need to access in the template

                  showClose: false,

                  closeByEscape: true,

                  closeByDocument: false

              }).then(

                  function(value) {

                      //confirm operation
                      if (value == 'Delete'){
                          console.log("to delete");

                          //call delete server operation
                          deleteServer($scope.elementSelected);
                      }

                  },

                  function(value) {

                      //Cancel or do nothing
                      console.log("cancel");

                  }

              );

          };

        var deleteServer = function(serverElement){

            ServerService.callServerListService().
                success(function (data, status, headers, config) {

                    var serversOriginalData = angular.copy(data);

                    var dataToStore = "[";

                    //iterate the server list, and find the server that matches with serverElement, and remove it

                    serversOriginalData.forEach(function (serverOriginal) {

                        if (serverOriginal.displayName != serverElement.displayName){

                            dataToStore +=
                                '{'
                                + '"displayName": ' + '"' + serverOriginal.displayName + '",'
                                + '"restProtocol" : ' + '"' + serverOriginal.restProtocol + '",'
                                + '"ipAddress" : ' + '"' + serverOriginal.ipAddress + '",'
                                + '"restPort" : ' + '"' + serverOriginal.restPort + '"'
                                + '},';

                        }

                    });

                    //Quit the last semicolon ;
                    if (dataToStore.length > 0){
                        dataToStore = dataToStore.substring(0, dataToStore.length - 1);
                    }

                    dataToStore += "]";

                    console.log("dataToStore:");
                    console.log(dataToStore);

                    //call the rest command to store data
                    ServerService.callUpdateServersService(dataToStore).
                        success(function (data, status, headers, config) {

                            console.log("success response deleting server");

                            var xmlDeleteServerResponse;
                            if (window.DOMParser) {
                                var parser = new DOMParser();
                                xmlDeleteServerResponse = parser.parseFromString(data, "text/xml");
                            }
                            else // Internet Explorer
                            {
                                xmlDeleteServerResponse = new ActiveXObject("Microsoft.XMLDOM");
                                xmlDeleteServerResponse.async = false;
                                xmlDeleteServerResponse.loadXML(data);
                            }

                            //get the xml response and extract the values for properties
                            var deleteServerCommandMessage = xmlDeleteServerResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                            if (deleteServerCommandMessage == 'Success') {
                                console.log("Success deleting server");

                                $rootScope.operationSuccessMsg = "Success deleting server";

                                //refresh tree view
                                TreeViewPainting.paintTreeView();

                                //crearl element selection
                                $scope.elementTree.currentNode = "";
                                $scope.elementSelected = null;

                            } else {

                                var deleteServerCommandDescription = xmlDeleteServerResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                console.log("fail deleting server");
                                console.log(deleteServerCommandDescription);
                                showErrorDialog('Error deleting server: ' + deleteServerCommandDescription);
                            }

                        }).
                        error(function (data, status, headers, config) {
                            console.log("error deleting server");

                            showErrorDialog('Error deleting server');


                            // called asynchronously if an error occurs
                            // or server returns response with an error status.
                        });



                }).
                error(function (data, status, headers, config) {
                    console.log("error reading servers on saving server properties");


                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });

        };

        var openRestartAppsDialog = function(host, groupName) {

            ngDialog.openConfirm({template: 'restartAppsDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Restart'){
                        console.log("to restart");

                        //call start app operation
                        restartAppsIfRunning(host, groupName);
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        var openRestartAppDialog = function(host, app) {

            $scope.appToRestart = app;

            ngDialog.openConfirm({template: 'restartAppDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Restart'){
                        console.log("to restart");

                        //call start app operation
                        restartAppIfRunning(host, app);
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        var restartAppsIfRunning = function(host, groupName){

            console.log("restartAppsIfRunning");
            console.log("restartAppsIfRunning.groupName");
            console.log(groupName);

            //Get the applications belonging to this group

            AppService.callAppListService(host)
                .success(function (data, status, headers, config) {

                    console.log("restartAppsIfRunning. Success calling API for app list");

                    var apps = AppService.getAppsFromReceivedData(data, groupName);

                    //Restart the applications given by apps list

                    //Iterate the applications and restart if state is STARTED
                    apps.forEach( function (app) {

                        if (app.status == 'STARTED') {

                            console.log("restartAppsIfRunning. Going to stop app:");
                            console.log(app.number);

                            //Call the service to stop this app
                            AppService.callStopAppService(host, app.number)
                                .success(function (data, status, headers, config) {

                                    console.log("restartAppsIfRunning. Success calling stop app service");
                                    console.log("restartAppsIfRunning.status:");
                                    console.log(status);
                                    console.log("restartAppsIfRunning.headers:");
                                    console.log(headers);
                                    console.log("restartAppsIfRunning.config:");
                                    console.log(config);

                                    var appIdReturned = AppService.extractAppIdFromStopAppUrl(config.url);
                                    console.log("callStopAppService.appIdReturned:");
                                    console.log(appIdReturned);

                                    //decode the response to see if success
                                    var respMessage = CommonService.getElementValue(data, "message");

                                    if ( respMessage == 'Success' ){

                                        //Call the service to start app
                                        console.log("restartAppsIfRunning. Success stopping app. Going to start it");

                                        //Call the service to start this app
                                        AppService.callStartAppService(host, appIdReturned)
                                            .success(function (data, status, headers, config) {

                                                console.log("restartAppsIfRunning. Success calling start app service");

                                                //decode the response to see if success
                                                var respMessage = CommonService.getElementValue(data, "message");

                                                if ( respMessage == 'Success' ){

                                                    console.log("restartAppsIfRunning. Success stopping and restarting app");
                                                    //Add message to success mesagges area

                                                    //Get the app name grom apps vector, bases on received app id
                                                    var localAppName;
                                                    apps.forEach( function (app) {

                                                        if (app.number == appIdReturned){
                                                            localAppName = app.appName;
                                                        }

                                                    });
                                                    $rootScope.operationSuccessMsgs.push('Success restarting app ' + localAppName);

                                                } else {

                                                    //Fail stopping app
                                                    console.log("restartAppsIfRunning. Fail starting app with id: " + appIdReturned);
                                                    //Display modal dialog with error
                                                    var description = CommonService.getElementValue(data, "description");
                                                    showErrorDialog('Error starting app with id ' + appIdReturned + ': ' + description);

                                                }


                                            })
                                            .error(function (data, status, headers, config) {

                                                console.log("restartAppsIfRunning. Error starting app with id: " + appIdReturned);
                                                //Display modal dialog with error
                                                var description = CommonService.getElementValue(data, "description");
                                                showErrorDialog('Error starting app with id ' + appIdReturned + ': ' + description);

                                            });




                                    } else {

                                        //Fail stopping app
                                        console.log("restartAppsIfRunning. Fail stopping app with id: " + appIdReturned);

                                        //Display modal dialog with error
                                        var description = CommonService.getElementValue(data, "description");
                                        showErrorDialog('Error stopping app with id ' + appIdReturned + ': ' + description);

                                    }


                                })
                                .error(function (data, status, headers, config) {

                                    console.log("restartAppsIfRunning: Error stopping app");
                                    showErrorDialog('Error stopping app');

                                });

                        } else {

                            console.log("restartAppsIfRunning. NOT going to stop app:");
                            console.log(app.number);
                        }

                    });


                })
                .error(function (data, status, headers, config) {

                    console.log("restartAppsIfRunning: Error reading apps to restart");

                });

        };

        var restartAppIfRunning = function(host, app){

            console.log("restartAppIfRunning");
            console.log("restartAppIfRunning.app");
            console.log(app);


            if (app.status == 'STARTED') {

                console.log("restartAppIfRunning. Going to stop app:");
                console.log("app.number");
                console.log(app.number);

                //Call the service to stop this app
                AppService.callStopAppService(host, app.number)
                    .success(function (data, status, headers, config) {

                        //decode the response to see if success
                        var respMessage = CommonService.getElementValue(data, "message");

                        if ( respMessage == 'Success' ){

                            //Call the service to start app
                            console.log("restartAppIfRunning. Success stopping app. Going to start it");

                            //Call the service to start this app
                            AppService.callStartAppService(host, app.number)
                                .success(function (data, status, headers, config) {

                                    console.log("restartAppIfRunning. Success calling start app service");

                                    //decode the response to see if success
                                    var respMessage = CommonService.getElementValue(data, "message");

                                    if ( respMessage == 'Success' ){

                                        console.log("restartAppIfRunning. Success stopping and restarting app");
                                        //Add message to success messages area
                                        $rootScope.operationSuccessMsgs.push('Success restarting app ' + app.appName);

                                    } else {

                                        //Fail stopping app
                                        console.log('restartAppIfRunning. Fail starting app');
                                        //Display modal dialog with error
                                        var description = CommonService.getElementValue(data, "description");
                                        showErrorDialog('Error starting app: ' + description);

                                    }


                                })
                                .error(function (data, status, headers, config) {

                                    console.log('restartAppIfRunning. Error starting app');
                                    //Display modal dialog with error
                                    var description = CommonService.getElementValue(data, "description");
                                    showErrorDialog('Error starting app: ' + description);

                                });

                        } else {

                            //Fail stopping app
                            console.log('restartAppIfRunning. Fail stopping app');

                            //Display modal dialog with error
                            var description = CommonService.getElementValue(data, "description");
                            showErrorDialog('Error stopping app: ' + description);

                        }


                    })
                    .error(function (data, status, headers, config) {

                        console.log("restartAppIfRunning: Error stopping app");
                        showErrorDialog('Error stopping app');

                    });

           } else {

             console.log("restartAppIfRunning. NOT going to stop app");

           }

        };


        $scope.openStartAppDialog = function() {

            ngDialog.openConfirm({template: 'startAppDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Start'){
                        console.log("to start");

                        //call start app operation
                        startApp();
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        $scope.openStopAppDialog = function() {

            ngDialog.openConfirm({template: 'stopAppDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Stop'){
                        console.log("to stop");

                        //call start app operation
                        stopApp();
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        $scope.openDeleteReadZoneDialog = function() {

            ngDialog.openConfirm({template: 'deleteReadZoneDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Delete'){
                        console.log("to delete");

                        //call delete readzone operation
                        deleteReadZone();

                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };




        $scope.openSaveServerConfigDialog = function() {

            ngDialog.openConfirm({template: 'saveServerConfigDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Save'){
                        console.log("to save");

                        //call save server config operation
                        saveServerConfig();
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        $scope.openSaveAllServersConfigDialog = function() {

            ngDialog.openConfirm({template: 'saveAllServersConfigDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Save'){
                        console.log("to save");

                        //call save server config operation
                        saveAllServersConfig();
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        $scope.openDeleteCommandDialog = function() {

            ngDialog.openConfirm({template: 'deleteCommandDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Delete'){
                        console.log("to delete");

                        //call delete command operation
                        deleteCommand($scope.elementSelected.elementId);
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        $scope.openDeleteJobDialog = function() {

            ngDialog.openConfirm({template: 'deleteJobDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Delete'){
                        console.log("to delete");

                        //call delete command operation
                        deleteJob($scope.elementSelected.elementId);
                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };


          $scope.openDeleteSensorDialog = function() {

              ngDialog.openConfirm({template: 'deleteSensorDialogTmpl.html',

                  scope: $scope, //Pass the scope object if you need to access in the template

                  showClose: false,

                  closeByEscape: true,

                  closeByDocument: false

              }).then(

                  function(value) {

                      //confirm operation
                      if (value == 'Delete'){
                          console.log("to delete");

                          //call delete sensor operation
                          deleteSensor();

                      }

                  },

                  function(value) {

                      //Cancel or do nothing
                      console.log("cancel");

                  }

              );

          };

          $scope.openDeleteSessionDialog = function() {

              ngDialog.openConfirm({template: 'deleteSessionDialogTmpl.html',

                  scope: $scope, //Pass the scope object if you need to access in the template

                  showClose: false,

                  closeByEscape: true,

                  closeByDocument: false

              }).then(

                  function(value) {

                      //confirm operation
                      if (value == 'Delete'){
                          console.log("to delete");

                          //call delete sensor operation
                          deleteSession();

                      }

                  },

                  function(value) {

                      //Cancel or do nothing
                      console.log("cancel");

                  }

              );

          };

          $scope.openSaveServerPropertiesDialog = function() {

              ngDialog.openConfirm({template: 'saveServerPropertiesDialogTmpl.html',

                  scope: $scope, //Pass the scope object if you need to access in the template

                  showClose: false,

                  closeByEscape: true,

                  closeByDocument: false

                  /*
                  preCloseCallback: function(value) {

                      console.log("preCloseCallback value");
                      console.log(value);

                      if(confirm('Are you sure you want to close without saving your changes?')) {
                          return true;
                      }
                      return false;
                  }
                  */


              }).then(

                  function(value) {

                      //confirm operation
                      if (value == 'Save'){
                          console.log("to save");
                          console.log("old display name:");
                          console.log($scope.elementTree.currentNode.displayName);
                          console.log("new server values:");
                          console.log($scope.elementSelected);

                          //Validate server display name is unique only if display name has changed

                          console.log("$scope.elementTree.currentNode.displayName:");
                          console.log($scope.elementTree.currentNode.displayName);
                          console.log("$scope.elementSelected.displayName:");
                          console.log($scope.elementSelected.displayName);

                          if ($scope.elementTree.currentNode.displayName != $scope.elementSelected.displayName) {

                              console.log("displayName with change");

                              ServerService.callServerListService().
                                  success(function (data, status, headers, config) {

                                      var displayNameisUnique = true;

                                      data.forEach(function (server) {

                                          if (server.displayName == $scope.elementSelected.displayName) {

                                              //displayName already exists, then can not be created again
                                              displayNameisUnique = false;

                                          }

                                      });

                                      if (displayNameisUnique) {

                                          //save server properties
                                          saveServerProperties($scope.elementTree.currentNode.displayName, $scope.elementSelected);


                                      } else {

                                          console.log("duplicate server display name");

                                         // $scope.errorMessage = "Duplicate server display name " + $scope.elementSelected.displayName;

                                          showErrorDialog('Display name ' + $scope.elementSelected.displayName + ' already exists for server');


                                          //$scope.serverCreationStatus.status = 'Fail';
                                          //$scope.serverCreationStatus.message = 'Display name ' + $scope.serverToCreate.displayName + ' already exists';

                                      }

                                  }).
                                  error(function (data, status, headers, config) {
                                      console.log("error reading servers on modifying server properties");


                                      // called asynchronously if an error occurs
                                      // or server returns response with an error status.
                                  });
                          } else {

                              saveServerProperties($scope.elementTree.currentNode.displayName, $scope.elementSelected);

                          }

                      }

                  },

                  function(value) {

                      //Cancel or do nothing
                      console.log("cancel");

                  }

              );

          };


          $scope.deleteServer=function(){
              console.log("delete $scope.elementTree.currentNode")

              //console.log($scope.elementTree.currentNode);
              console.log("$scope.elementTree");
              console.log($scope.elementTree.currentNode);

              //ngDialog.open({ template: 'popupTmpl.html' });
              //ngDialog.open({ template: '<p>my template</p>', plain:true });

              /*
              ngDialog.open({
                  preCloseCallback: function(value) {
                      if(confirm('Are you sure you want to close without saving your changes?')) {
                          return true;
                      }
                      return false;
                  }
              });
              */

              /*
              ngDialog.open({
                  preCloseCallback: function(value) {
                      var nestedConfirmDialog = ngDialog.openConfirm({
                          template:'\
                <p>Are you sure you want to close the parent dialog?</p>\
                <div class="ngdialog-buttons">\
                    <button type="button" class="ngdialog-button ngdialog-button-secondary" ng-click="closeThisDialog(0)">No</button>\
                    <button type="button" class="ngdialog-button ngdialog-button-primary" ng-click="confirm(1)">Yes</button>\
                </div>',
                          plain: true
                      });

                      // NOTE: return the promise from openConfirm
                      return nestedConfirmDialog;
                  }
              });
              */




          };

        var saveServerProperties = function(oldServerName, server) {

            console.log("saveServerProperties");

            console.log("oldServerName:");
            console.log(oldServerName);
            console.log("server:");
            console.log(server);

            ServerService.callServerListService().
                success(function (data, status, headers, config) {

                    var serversOriginalData = angular.copy(data);

                    var dataToStore = "[";

                    //iterate the server list, and find the server that matches with oldServerName, and replace properties

                    serversOriginalData.forEach(function (serverOriginal) {

                        if (serverOriginal.displayName == oldServerName){

                            dataToStore +=
                                '{'
                                + '"displayName": ' + '"' + server.displayName + '",'
                                + '"restProtocol" : ' + '"' + server.restProtocol + '",'
                                + '"ipAddress" : ' + '"' + server.ipAddress + '",'
                                + '"restPort" : ' + '"' + server.restPort + '"'
                                + '},';

                        } else {

                            dataToStore +=
                                '{'
                                + '"displayName": ' + '"' + serverOriginal.displayName + '",'
                                + '"restProtocol" : ' + '"' + serverOriginal.restProtocol + '",'
                                + '"ipAddress" : ' + '"' + serverOriginal.ipAddress + '",'
                                + '"restPort" : ' + '"' + serverOriginal.restPort + '"'
                                + '},';

                        }



                    });

                    //Quit the last semicolon ;
                    if (dataToStore.length > 0){
                        dataToStore = dataToStore.substring(0, dataToStore.length - 1);
                    }

                    dataToStore += "]";

                    console.log("dataToStore:");
                    console.log(dataToStore);

                    //call the rest command to store data
                    ServerService.callUpdateServersService(dataToStore).
                        success(function (data, status, headers, config) {

                            console.log("success response editing server properties");

                            var xmlEditServerResponse;
                            if (window.DOMParser) {
                                var parser = new DOMParser();
                                xmlEditServerResponse = parser.parseFromString(data, "text/xml");
                            }
                            else // Internet Explorer
                            {
                                xmlEditServerResponse = new ActiveXObject("Microsoft.XMLDOM");
                                xmlEditServerResponse.async = false;
                                xmlEditServerResponse.loadXML(data);
                            }

                            //get the xml response and extract the values for properties
                            var editServerCommandMessage = xmlEditServerResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                            if (editServerCommandMessage == 'Success') {
                                console.log("Success editing server properties");

                                $rootScope.operationSuccessMsg = "Success editing server properties";

                                //refresh tree view
                                TreeViewPainting.paintTreeView();


                            } else {

                                var editServerCommandDescription = xmlEditServerResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                console.log("fail editing server");
                                console.log(editServerCommandDescription);
                                showErrorDialog('Error editing server properties: ' + editServerCommandDescription);
                            }

                        }).
                        error(function (data, status, headers, config) {
                            console.log("error editing server properties");

                            showErrorDialog('Error editing server properties');


                            // called asynchronously if an error occurs
                            // or server returns response with an error status.
                        });



                }).
                error(function (data, status, headers, config) {
                    console.log("error reading servers on saving server properties");


                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });


            /*
            console.log("serversOriginalData");
            console.log(serversOriginalData);
            console.log("serversOriginalData.length");
            console.log(serversOriginalData.length);
            */





            /*
            console.log("saveServerProperties op success");
            setSuccessMessage("Save server properties operation success");
            //$scope.operationSuccessMsg = "Save server properties operation success";
            $rootScope.operationSuccessMsg = getSuccessMessage();
            console.log("$rootScope.operationSuccessMsg:");
            console.log($rootScope.operationSuccessMsg);
            //$scope.$apply();
            */
        }

        var saveServerConfig = function(){
              console.log("saveServerConfig");
              console.log("$scope.elementSelected");
              console.log($scope.elementSelected);

            //call the save operation on selected server
            $http.get($scope.elementSelected.host + '/save')
                .success(function (data, status, headers, config) {

                    var saveCommandResponse;
                    if (window.DOMParser) {
                        var parser = new DOMParser();
                        saveCommandResponse = parser.parseFromString(data, "text/xml");
                    }
                    else // Internet Explorer
                    {
                        saveCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                        saveCommandResponse.async = false;
                        saveCommandResponse.loadXML(data);
                    }

                    //get the xml response and extract the message response
                    var message = saveCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    if (message == 'Success') {
                        console.log("success saving server config");

                        $rootScope.operationSuccessMsg = ("Success saving server config");

                        TreeViewPainting.paintTreeView();

                    } else {
                        var saveServerConfigCommandDescription = saveCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        console.log("fail saving server config");
                        console.log(saveServerConfigCommandDescription);

                        //show modal dialog with error
                        showErrorDialog('Error saving server config: ' + saveServerConfigCommandDescription);

                    }


                }).
                error(function (data, status, headers, config) {
                    console.log("error saving server config");

                    //show modal dialog with error
                    showErrorDialog('Error saving server config');


                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });

          }



        var saveAllServersConfig = function(){
            console.log("saveAllServersConfig");
            console.log("$scope.elementSelected");
            console.log($scope.elementSelected);

            $rootScope.operationSuccessMsgs = [];

            //call the save operation on every server

            $scope.elementList[0].children.forEach(function (server) {

                console.log("server");
                console.log(server);

                //call save server only if status is connected
                if (server.status == 'CONNECTED') {

                    $http.get(server.host + '/save')
                        .success(function (data, status, headers, config) {

                            var saveHost = headers('host');

                            var saveCommandResponse;
                            if (window.DOMParser) {
                                var parser = new DOMParser();
                                saveCommandResponse = parser.parseFromString(data, "text/xml");
                            }
                            else // Internet Explorer
                            {
                                saveCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                                saveCommandResponse.async = false;
                                saveCommandResponse.loadXML(data);
                            }

                            //get the xml response and extract the message response
                            var message = saveCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                            if (message == 'Success') {
                                console.log("success saving server config for server: " + saveHost);


                                //$scope.operationSuccessMsg = "Save server properties operation success";
                                $rootScope.operationSuccessMsgs.push("Success saving server config for server: " + saveHost);

                                TreeViewPainting.paintTreeView();

                            } else {
                                var saveServerConfigCommandDescription = saveCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                console.log("fail saving server config for server: " + saveHost);
                                console.log(saveServerConfigCommandDescription);

                                //show modal dialog with error
                                showErrorDialog("Error saving server config for server: " + saveHost + saveServerConfigCommandDescription);

                            }


                        }).
                        error(function (data, status, headers, config) {

                            var saveHost = headers('host');

                            console.log("error saving server config for server: " + saveHost);

                            //show modal dialog with error
                            showErrorDialog("Error saving server config for server: " + saveHost);


                            // called asynchronously if an error occurs
                            // or server returns response with an error status.
                        });

                } else {

                    console.log("not saving config for server " + server.host + " due to disconnected state");
                }

            });


        }

          //$scope.refreshMenuTreeView;

          //$scope.refreshMenuTreeView = function () {

          TreeViewPainting.paintTreeView();





          //}


          $scope.tabs = [
              { title:'Dynamic Title 1', content:'Dynamic content 1' },
              { title:'Dynamic Title 2', content:'Dynamic content 2', disabled: true }
          ];

          var tabsSensor=[
              { title:'Connection', content:'Dynamic content 1' },
              { title:'General', content:'Dynamic content 2'},
              { title:'General', content:'Dynamic content 2'}
          ];

          $scope.$watch( 'elementTree.currentNode', function( newObj, oldObj ) {

              $scope.go("/");

              console.log($scope.elementTree.currentNode);
              console.log("newObj:");
              console.log(newObj);
              //console.log("oldObj:");
              //console.log(oldObj);
              if( $scope.elementTree && angular.isObject($scope.elementTree.currentNode) ) {
                  $scope.elementSelected = angular.copy($scope.elementTree.currentNode);
                  //$scope.propertyType = angular.copy($scope.elementTree.currentNode.elementId);

                  //if selected element is sensor, then load the properties for this sensor
                  if ($scope.elementTree.currentNode.elementType == 'sensor'){
                      //load properties for this sensor

                      $http.get($scope.elementTree.currentNode.host + '/readermetadata')
                          .success(function(data, status, headers, config) {

                              var xmlMetadata;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlMetadata = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlMetadata = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlMetadata.async=false;
                                  xmlMetadata.loadXML(data);
                              }

                              //get the xml response and extract the values to construct the sensor properties for selected sensor

                              var sensorMetadataXmlVector = xmlMetadata.getElementsByTagName("reader");

                              for(var index = 0; index < sensorMetadataXmlVector.length; index++) {


                                  var propertiesXmlVector = sensorMetadataXmlVector[index].getElementsByTagName("property");
                                  var readerid = sensorMetadataXmlVector[index].getElementsByTagName("readerid")[0].childNodes[0].nodeValue;

                                  /*
                                   console.log("readerid: " + readerid.nodeValue);
                                   console.log("properties length: " + propertiesXmlVector.length);
                                   console.log("propertiesXmlVector");
                                   console.log(propertiesXmlVector);
                                   */

                                  //check if current command is the required one
                                  /*
                                   console.log("readerid:");
                                   console.log(readerid);
                                   console.log("$scope.selectedReaderType:");
                                   console.log($scope.selectedReaderType);
                                   console.log("selectedCommandInstance.factoryID:");
                                   console.log(selectedCommandInstance.factoryID);
                                   */

                                  if (readerid == $scope.elementTree.currentNode.factoryID){

                                      //Create the properties object for this sensor
                                      $scope.sensorProperties = {
                                          "readerid": readerid,
                                          "propertyCategoryList": []

                                      };

                                      console.log("$scope.sensorProperties:");
                                      console.log($scope.sensorProperties);


                                      //extract the properties
                                      for(var indexProp = 0; indexProp < propertiesXmlVector.length; indexProp++) {


                                          var name = propertiesXmlVector[indexProp].getElementsByTagName("name")[0].childNodes[0].nodeValue;
                                          var displayname = propertiesXmlVector[indexProp].getElementsByTagName("displayname")[0].childNodes[0].nodeValue;
                                          var defaultvalue;
                                          if (propertiesXmlVector[indexProp].getElementsByTagName('defaultvalue').length > 0){
                                              defaultvalue = propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue")[0].childNodes[0].nodeValue;
                                          }

                                          var description = propertiesXmlVector[indexProp].getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                          var type = propertiesXmlVector[indexProp].getElementsByTagName("type")[0].childNodes[0].nodeValue;
                                          var maxvalue;
                                          var minvalue;
                                          if (type.nodeValue == 'java.lang.Integer') {
                                              maxvalue = propertiesXmlVector[indexProp].getElementsByTagName("maxvalue")[0].childNodes[0].nodeValue;
                                              minvalue = propertiesXmlVector[indexProp].getElementsByTagName("minvalue")[0].childNodes[0].nodeValue;
                                          }
                                          var category = propertiesXmlVector[indexProp].getElementsByTagName("category")[0].childNodes[0].nodeValue;
                                          var strWritable = propertiesXmlVector[indexProp].getElementsByTagName("writable")[0].childNodes[0].nodeValue;
                                          var writable = false;
                                          if (strWritable == 'true'){
                                              writable = true;
                                          }

                                          /*
                                          console.log("propertiesXmlVector[indexProp].getElementsByTagName('writable')[0].childNodes[0].nodeValue:");
                                          console.log(propertiesXmlVector[indexProp].getElementsByTagName('writable')[0].childNodes[0].nodeValue);

                                          console.log("writable:");
                                          console.log(writable);
                                          */


                                          var ordervalue = propertiesXmlVector[indexProp].getElementsByTagName("ordervalue")[0].childNodes[0].nodeValue;

                                          //check if current category already exists in propertyCategories
                                          var existingCategory = false;

                                          $scope.sensorProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category == propertyCategory.category){
                                                  existingCategory = true;
                                              }

                                          });

                                          if (!existingCategory){

                                              var propertyCategory = {
                                                  "category": category,
                                                  "properties": []
                                              };

                                              $scope.sensorProperties.propertyCategoryList.push(propertyCategory);
                                          }

                                          /*
                                           var customdefaultvalue;

                                           if (type.nodeValue == 'java.lang.Integer'){
                                           customdefaultvalue = parseInt(defaultvalue.nodeValue);
                                           } else {
                                           customdefaultvalue = defaultvalue.nodeValue;
                                           }
                                           */

                                          var propertyElement = {
                                              "name": name,
                                              "displayname": displayname,
                                              "description": description,
                                              "type": type,
                                              "maxvalue": maxvalue,
                                              "minvalue": minvalue,
                                              "category": category,
                                              "writable": writable,
                                              "ordervalue": ordervalue,
                                              "value": ""
                                          };

                                          //Set the default value for property

                                          if (type == 'java.lang.Integer'){
                                              propertyElement.value = angular.copy(parseInt(defaultvalue));
                                              propertyElement.htmlType = 'number';
                                          } else {
                                              propertyElement.value = angular.copy(defaultvalue);
                                              propertyElement.htmlType = 'text';
                                          }

                                          //Add the property to appropriate property category list
                                          $scope.sensorProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category == propertyCategory.category){

                                                  propertyCategory.properties.push(angular.copy(propertyElement));

                                              }

                                          });

                                      }

                                      //Then, load the current values for every property

                                          //call the service to get properties of sensor instance
                                          $http.get($scope.elementTree.currentNode.host + '/getproperties/' + $scope.elementTree.currentNode.elementId)
                                              .success(function(data, status, headers, config) {

                                                  var xmlSensorProperties;
                                                  if (window.DOMParser)
                                                  {
                                                      var parser = new DOMParser();
                                                      xmlSensorProperties = parser.parseFromString(data,"text/xml");
                                                  }
                                                  else // Internet Explorer
                                                  {
                                                      xmlSensorProperties = new ActiveXObject("Microsoft.XMLDOM");
                                                      xmlSensorProperties.async=false;
                                                      xmlSensorProperties.loadXML(data);
                                                  }

                                                  //get the xml response and extract the values for properties
                                                  var propertiesXmlVector = xmlSensorProperties.getElementsByTagName("entry");

                                                  for(var indexPropertyValue = 0; indexPropertyValue < propertiesXmlVector.length; indexPropertyValue++) {

                                                      var key = propertiesXmlVector[indexPropertyValue].getElementsByTagName("key")[0].childNodes[0].nodeValue;
                                                      var value = propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes[0].nodeValue;

                                                      //Iterate the loaded properties and set this value when property key matches
                                                      $scope.sensorProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                                          //Iterate al categories to find this property and set the value
                                                          propertyCategory.properties.forEach(function (property) {

                                                              if (property.name == key){

                                                                  //Set the value for property

                                                                  if (property.type == 'java.lang.Integer'){
                                                                      property.value = angular.copy(parseInt(value));
                                                                  } else {
                                                                      property.value = angular.copy(value);
                                                                  }

                                                              }

                                                          });


                                                      });


                                                  }

                                              }).
                                              error(function(data, status, headers, config) {
                                                  console.log("error reading sensor properties");


                                                  // called asynchronously if an error occurs
                                                  // or server returns response with an error status.
                                              });



                                  }


                              }


                          })
                          .error(function(data, status, headers, config) {
                              console.log("error reading readermetadata for sensor wizard: command instance selection");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });



                      //if selected element is commandInstance, then load the properties for this command instance

                  } else if ($scope.elementSelected.elementType == 'commandInstance_sensor'){

                      console.log("element type commandInstance_sensor selected");
                      //load properties for this command type


                      //$scope.commandWizardData.commandInstance = selectedCommandInstance;
                      var host = angular.copy($scope.elementSelected.session.sensor.sensorManagementElement.host);
                      var readerType = angular.copy($scope.elementSelected.session.sensor.factoryID);
                      var commandType = angular.copy($scope.elementSelected.commandType);
                      var commandId = angular.copy($scope.elementSelected.elementId);

                      console.log("readerType:");
                      console.log(readerType);
                      console.log("commandId:");
                      console.log(commandId);

                      //Get the properties for the selected command type, from readermetadata
                      $http.get(host + '/readermetadata')
                          .success(function(data, status, headers, config) {

                              console.log("success calling: " + host + '/readermetadata');

                              var xmlMetadata;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlMetadata = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlMetadata = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlMetadata.async=false;
                                  xmlMetadata.loadXML(data);
                              }

                              //get the xml response and extract the values to construct the command properties for selected command type

                              var commandMetadataXmlVector = xmlMetadata.getElementsByTagName("command");

                              for(var index = 0; index < commandMetadataXmlVector.length; index++) {

                                  var propertiesXmlVector = commandMetadataXmlVector[index].getElementsByTagName("property");
                                  var id = commandMetadataXmlVector[index].getElementsByTagName("id")[0].childNodes[0];
                                  var readerID = commandMetadataXmlVector[index].getElementsByTagName("readerID")[0].childNodes[0];

                                  if (readerID.nodeValue == readerType && id.nodeValue ==  commandType){

                                      //console.log("created properties object" );

                                      //Create the properties object for this command
                                      $scope.commandProperties = {
                                          "readerID": readerID.nodeValue,
                                          "id": id.nodeValue,
                                          "propertyCategoryList": []

                                      };

                                      //extract the properties
                                      for(var indexProp = 0; indexProp < propertiesXmlVector.length; indexProp++) {

                                          var name = propertiesXmlVector[indexProp].getElementsByTagName("name")[0].childNodes[0];
                                          var displayname = propertiesXmlVector[indexProp].getElementsByTagName("displayname")[0].childNodes[0];
                                          var defaultvalue = {};
                                          if (propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue").length > 0){
                                              defaultvalue = propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue")[0].childNodes[0];
                                          }

                                          var description = propertiesXmlVector[indexProp].getElementsByTagName("description")[0].childNodes[0];
                                          var type = propertiesXmlVector[indexProp].getElementsByTagName("type")[0].childNodes[0];
                                          var maxvalue = 0;
                                          var minvalue = 0;
                                          if (type.nodeValue == 'java.lang.Integer') {
                                              maxvalue = propertiesXmlVector[indexProp].getElementsByTagName("maxvalue")[0].childNodes[0];
                                              minvalue = propertiesXmlVector[indexProp].getElementsByTagName("minvalue")[0].childNodes[0];
                                          }
                                          var category = propertiesXmlVector[indexProp].getElementsByTagName("category")[0].childNodes[0];
                                          var writable = propertiesXmlVector[indexProp].getElementsByTagName("writable")[0].childNodes[0];
                                          var ordervalue = propertiesXmlVector[indexProp].getElementsByTagName("ordervalue")[0].childNodes[0];

                                          //check if current category already exists in propertyCategories
                                          var existingCategory = false;

                                          $scope.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category.nodeValue == propertyCategory.category){
                                                  existingCategory = true;
                                              }

                                          });

                                          if (!existingCategory){

                                              var propertyCategory = {
                                                  "category": category.nodeValue,
                                                  "properties": []
                                              };

                                              $scope.commandProperties.propertyCategoryList.push(propertyCategory);
                                          }

                                          var propertyElement = {
                                              "name": name.nodeValue,
                                              "displayname": displayname.nodeValue,
                                              "description": description.nodeValue,
                                              "type": type.nodeValue,
                                              "maxvalue": maxvalue.nodeValue,
                                              "minvalue": minvalue.nodeValue,
                                              "category": category.nodeValue,
                                              "writable": writable.nodeValue,
                                              "ordervalue": ordervalue.nodeValue,
                                              "value": ""
                                              //,
                                              //"defaultvalue": ""
                                          };

                                          //console.log("propertyElement:");
                                          //console.log(propertyElement);

                                          //Set the default value for property
/*
                                          if (type.nodeValue == 'java.lang.Integer'){
                                              propertyElement.value = angular.copy(parseInt(defaultvalue.nodeValue));
                                              propertyElement.defaultvalue = angular.copy(parseInt(defaultvalue.nodeValue));
                                          } else {
                                              propertyElement.value = angular.copy(defaultvalue.nodeValue);
                                              propertyElement.defaultvalue = angular.copy(defaultvalue.nodeValue);
                                          }
*/
                                          //Add the property to appropriate property category list
                                          $scope.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category.nodeValue == propertyCategory.category){

                                                  propertyCategory.properties.push(angular.copy(propertyElement));

                                              }

                                          });

                                      }

                                      //Load the current values for every property

                                          //call the service to get properties of command instance
                                          $http.get(host + '/getproperties/' + commandId)
                                              .success(function(data, status, headers, config) {

                                                  var xmlCommandProperties;
                                                  if (window.DOMParser)
                                                  {
                                                      var parser = new DOMParser();
                                                      xmlCommandProperties = parser.parseFromString(data,"text/xml");
                                                  }
                                                  else // Internet Explorer
                                                  {
                                                      xmlCommandProperties = new ActiveXObject("Microsoft.XMLDOM");
                                                      xmlCommandProperties.async=false;
                                                      xmlCommandProperties.loadXML(data);
                                                  }

                                                  //get the xml response and extract the values for properties
                                                  var propertiesXmlVector = xmlCommandProperties.getElementsByTagName("entry");

                                                  for(var indexPropertyValue = 0; indexPropertyValue < propertiesXmlVector.length; indexPropertyValue++) {

                                                      var key = propertiesXmlVector[indexPropertyValue].getElementsByTagName("key")[0].childNodes[0];
                                                      var value = propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes[0];

                                                      //Iterate the loaded properties and set this value when property key matches
                                                      $scope.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                                          //Iterate al categories to find this property and set the value
                                                          propertyCategory.properties.forEach(function (property) {

                                                              if (property.name == key.nodeValue){

                                                                  //Set the value for property

                                                                  if (property.type == 'java.lang.Integer'){
                                                                      property.value = angular.copy(parseInt(value.nodeValue));
                                                                      property.htmlType = 'number';

                                                                  } else {
                                                                      property.value = angular.copy(value.nodeValue);
                                                                      property.htmlType = 'text';
                                                                  }

                                                              }

                                                          });


                                                      });


                                                  }

                                              }).
                                              error(function(data, status, headers, config) {
                                                  console.log("error reading command instance properties");


                                                  // called asynchronously if an error occurs
                                                  // or server returns response with an error status.
                                              });

                                  }


                              }


                          })
                          .error(function(data, status, headers, config) {
                              console.log("error reading readermetadata for command wizard: command instance selection");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });


                  } else if ($scope.elementSelected.elementType == 'commandInstance_commandManagement'){

                      console.log("element type commandInstance selected");
                      //load properties for this command type


                      //$scope.commandWizardData.commandInstance = selectedCommandInstance;
                      var host = angular.copy($scope.elementSelected.host);
                      var readerType = angular.copy($scope.elementSelected.factoryElement.readerTypeElement.factoryID);
                      var commandType = angular.copy($scope.elementSelected.factoryID);
                      var commandId = angular.copy($scope.elementSelected.commandID);

                      console.log("readerType:");
                      console.log(readerType);
                      console.log("commandId:");
                      console.log(commandId);

                      //Get the properties for the selected command type, from readermetadata
                      $http.get(host + '/readermetadata')
                          .success(function(data, status, headers, config) {

                              console.log("success calling: " + host + '/readermetadata');

                              var xmlMetadata;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlMetadata = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlMetadata = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlMetadata.async=false;
                                  xmlMetadata.loadXML(data);
                              }

                              //get the xml response and extract the values to construct the command properties for selected command type

                              var commandMetadataXmlVector = xmlMetadata.getElementsByTagName("command");

                              for(var index = 0; index < commandMetadataXmlVector.length; index++) {

                                  var propertiesXmlVector = commandMetadataXmlVector[index].getElementsByTagName("property");
                                  var id = commandMetadataXmlVector[index].getElementsByTagName("id")[0].childNodes[0];
                                  var readerID = commandMetadataXmlVector[index].getElementsByTagName("readerID")[0].childNodes[0];

                                  if (readerID.nodeValue == readerType && id.nodeValue ==  commandType){

                                      //Create the properties object for this command
                                      $scope.commandProperties = {
                                          "readerID": readerID.nodeValue,
                                          "id": id.nodeValue,
                                          "propertyCategoryList": []

                                      };

                                      //extract the properties
                                      for(var indexProp = 0; indexProp < propertiesXmlVector.length; indexProp++) {


                                          var name = propertiesXmlVector[indexProp].getElementsByTagName("name")[0].childNodes[0];
                                          var displayname = propertiesXmlVector[indexProp].getElementsByTagName("displayname")[0].childNodes[0];
                                          var defaultvalue = {};
                                          if (propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue").length > 0){
                                              defaultvalue = propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue")[0].childNodes[0];
                                          }

                                          var description = propertiesXmlVector[indexProp].getElementsByTagName("description")[0].childNodes[0];
                                          var type = propertiesXmlVector[indexProp].getElementsByTagName("type")[0].childNodes[0];
                                          var maxvalue = 0;
                                          var minvalue = 0;
                                          if (type.nodeValue == 'java.lang.Integer') {
                                              maxvalue = propertiesXmlVector[indexProp].getElementsByTagName("maxvalue")[0].childNodes[0];
                                              minvalue = propertiesXmlVector[indexProp].getElementsByTagName("minvalue")[0].childNodes[0];
                                          }
                                          var category = propertiesXmlVector[indexProp].getElementsByTagName("category")[0].childNodes[0];
                                          var writable = propertiesXmlVector[indexProp].getElementsByTagName("writable")[0].childNodes[0];
                                          var ordervalue = propertiesXmlVector[indexProp].getElementsByTagName("ordervalue")[0].childNodes[0];

                                          //check if current category already exists in propertyCategories
                                          var existingCategory = false;

                                          $scope.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category.nodeValue == propertyCategory.category){
                                                  existingCategory = true;
                                              }

                                          });

                                          if (!existingCategory){

                                              var propertyCategory = {
                                                  "category": category.nodeValue,
                                                  "properties": []
                                              };

                                              $scope.commandProperties.propertyCategoryList.push(propertyCategory);
                                          }

                                          var propertyElement = {
                                              "name": name.nodeValue,
                                              "displayname": displayname.nodeValue,
                                              "description": description.nodeValue,
                                              "type": type.nodeValue,
                                              "maxvalue": maxvalue.nodeValue,
                                              "minvalue": minvalue.nodeValue,
                                              "category": category.nodeValue,
                                              "writable": writable.nodeValue,
                                              "ordervalue": ordervalue.nodeValue,
                                              "value": ""
                                              //,
                                              //"defaultvalue": ""
                                          };

                                          //Set the default value for property
                                          /*
                                           if (type.nodeValue == 'java.lang.Integer'){
                                           propertyElement.value = angular.copy(parseInt(defaultvalue.nodeValue));
                                           propertyElement.defaultvalue = angular.copy(parseInt(defaultvalue.nodeValue));
                                           } else {
                                           propertyElement.value = angular.copy(defaultvalue.nodeValue);
                                           propertyElement.defaultvalue = angular.copy(defaultvalue.nodeValue);
                                           }
                                           */
                                          //Add the property to appropriate property category list
                                          $scope.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category.nodeValue == propertyCategory.category){

                                                  propertyCategory.properties.push(angular.copy(propertyElement));

                                              }

                                          });

                                      }

                                      //Load the current values for every property

                                      //call the service to get properties of command instance
                                      $http.get(host + '/getproperties/' + commandId)
                                          .success(function(data, status, headers, config) {

                                              var xmlCommandProperties;
                                              if (window.DOMParser)
                                              {
                                                  var parser = new DOMParser();
                                                  xmlCommandProperties = parser.parseFromString(data,"text/xml");
                                              }
                                              else // Internet Explorer
                                              {
                                                  xmlCommandProperties = new ActiveXObject("Microsoft.XMLDOM");
                                                  xmlCommandProperties.async=false;
                                                  xmlCommandProperties.loadXML(data);
                                              }

                                              //get the xml response and extract the values for properties
                                              var propertiesXmlVector = xmlCommandProperties.getElementsByTagName("entry");

                                              for(var indexPropertyValue = 0; indexPropertyValue < propertiesXmlVector.length; indexPropertyValue++) {

                                                  var key = propertiesXmlVector[indexPropertyValue].getElementsByTagName("key")[0].childNodes[0];
                                                  var value = propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes[0];

                                                  //Iterate the loaded properties and set this value when property key matches
                                                  $scope.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                                      //Iterate al categories to find this property and set the value
                                                      propertyCategory.properties.forEach(function (property) {

                                                          if (property.name == key.nodeValue){

                                                              //Set the value for property

                                                              if (property.type == 'java.lang.Integer'){
                                                                  property.value = angular.copy(parseInt(value.nodeValue));
                                                                  property.htmlType = 'number';

                                                              } else {
                                                                  property.value = angular.copy(value.nodeValue);
                                                                  property.htmlType = 'text';
                                                              }

                                                          }

                                                      });


                                                  });


                                              }

                                          }).
                                          error(function(data, status, headers, config) {
                                              console.log("error reading command instance properties");


                                              // called asynchronously if an error occurs
                                              // or server returns response with an error status.
                                          });

                                  }


                              }


                          })
                          .error(function(data, status, headers, config) {
                              console.log("error reading readermetadata for command wizard: command instance selection");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });




                  } else if ($scope.elementSelected.elementType === 'commandType'){

                      $scope.commandWizardData = {};
                      $scope.commandWizardData.commandProperties = {};

                      var host = angular.copy($scope.elementSelected.host);
                      var readerType = angular.copy($scope.elementSelected.readerFactoryID);
                      var factoryID = angular.copy($scope.elementSelected.factoryID);

                      //$scope.commandWizardData.commandInstance = selectedCommandInstance;

                      //Get the properties for the selected command type, from readermetadata
                      $http.get(host + '/readermetadata')
                          .success(function(data, status, headers, config) {

                              var xmlMetadata;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlMetadata = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlMetadata = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlMetadata.async=false;
                                  xmlMetadata.loadXML(data);
                              }

                              //get the xml response and extract the values to construct the command properties for selected command type

                              var commandMetadataXmlVector = xmlMetadata.getElementsByTagName("command");

                              for(var index = 0; index < commandMetadataXmlVector.length; index++) {

                                  var propertiesXmlVector = commandMetadataXmlVector[index].getElementsByTagName("property");
                                  var id = commandMetadataXmlVector[index].getElementsByTagName("id")[0].childNodes[0];
                                  var readerID = commandMetadataXmlVector[index].getElementsByTagName("readerID")[0].childNodes[0];

                                  if (readerID.nodeValue == readerType && id.nodeValue ==  factoryID){

                                      //Create the properties object for this command
                                      $scope.commandWizardData.commandProperties = {
                                          "readerID": readerID.nodeValue,
                                          "id": id.nodeValue,
                                          "propertyCategoryList": []

                                      };

                                      //extract the properties
                                      for(var indexProp = 0; indexProp < propertiesXmlVector.length; indexProp++) {


                                          var name = propertiesXmlVector[indexProp].getElementsByTagName("name")[0].childNodes[0];
                                          var displayname = propertiesXmlVector[indexProp].getElementsByTagName("displayname")[0].childNodes[0];
                                          var defaultvalue = {};
                                          if (propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue").length > 0){
                                              defaultvalue = propertiesXmlVector[indexProp].getElementsByTagName("defaultvalue")[0].childNodes[0];
                                          }

                                          var description = propertiesXmlVector[indexProp].getElementsByTagName("description")[0].childNodes[0];
                                          var type = propertiesXmlVector[indexProp].getElementsByTagName("type")[0].childNodes[0];
                                          var maxvalue = 0;
                                          var minvalue = 0;
                                          if (type.nodeValue == 'java.lang.Integer') {
                                              maxvalue = propertiesXmlVector[indexProp].getElementsByTagName("maxvalue")[0].childNodes[0];
                                              minvalue = propertiesXmlVector[indexProp].getElementsByTagName("minvalue")[0].childNodes[0];
                                          }
                                          var category = propertiesXmlVector[indexProp].getElementsByTagName("category")[0].childNodes[0];
                                          var writable = propertiesXmlVector[indexProp].getElementsByTagName("writable")[0].childNodes[0];
                                          var ordervalue = propertiesXmlVector[indexProp].getElementsByTagName("ordervalue")[0].childNodes[0];

                                          //check if current category already exists in propertyCategories
                                          var existingCategory = false;

                                          $scope.commandWizardData.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category.nodeValue == propertyCategory.category){
                                                  existingCategory = true;
                                              }

                                          });

                                          if (!existingCategory){

                                              var propertyCategory = {
                                                  "category": category.nodeValue,
                                                  "properties": []
                                              };

                                              $scope.commandWizardData.commandProperties.propertyCategoryList.push(propertyCategory);
                                          }

                                          var propertyElement = {
                                              "name": name.nodeValue,
                                              "displayname": displayname.nodeValue,
                                              "description": description.nodeValue,
                                              "type": type.nodeValue,
                                              "maxvalue": maxvalue.nodeValue,
                                              "minvalue": minvalue.nodeValue,
                                              "category": category.nodeValue,
                                              "writable": writable.nodeValue,
                                              "ordervalue": ordervalue.nodeValue,
                                              "value": "",
                                              "defaultvalue": ""
                                          };

                                          //Set the default value for property

                                          if (type.nodeValue == 'java.lang.Integer'){
                                              propertyElement.value = angular.copy(parseInt(defaultvalue.nodeValue));
                                              propertyElement.defaultvalue = angular.copy(parseInt(defaultvalue.nodeValue));
                                          } else {
                                              propertyElement.value = angular.copy(defaultvalue.nodeValue);
                                              propertyElement.defaultvalue = angular.copy(defaultvalue.nodeValue);
                                          }

                                          //Add the property to appropriate property category list
                                          $scope.commandWizardData.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                                              if (category.nodeValue == propertyCategory.category){

                                                  propertyCategory.properties.push(angular.copy(propertyElement));

                                              }

                                          });

                                      }



                                  }


                              }


                          })
                          .error(function(data, status, headers, config) {
                              console.log("error reading readermetadata for command wizard: command instance selection");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });



                      /*

                      //load the command instances for selected command type
                      console.log("commandType selected");

                      //call getGroupProperties operation
                      var host = angular.copy($scope.elementSelected.host);
                      var selectedFactoryID = angular.copy($scope.elementSelected.factoryID);
                      var readerType = angular.copy($scope.elementSelected.readerFactoryID);

                      console.log("host");
                      console.log(host);
                      console.log("selectedFactoryID");
                      console.log(selectedFactoryID);

                      //clear command instances list
                      $scope.commandWizardData = {};
                      $scope.commandWizardData.commandInstances = [];

                      //load the command instances for selected command type
                      $http.get(host + '/commands')
                          .success(function(data, status, headers, config) {

                              var xmlCommands;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlCommands = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlCommands = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlCommands.async=false;
                                  xmlCommands.loadXML(data);
                              }

                              //get the xml response and extract the values to construct the local command object
                              var commandXmlVector = xmlCommands.getElementsByTagName("command");

                              for(var index = 0; index < commandXmlVector.length; index++) {

                                  var commandID = commandXmlVector[index].getElementsByTagName("commandID")[0].childNodes[0];
                                  var factoryID = commandXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];

                                  if (factoryID.nodeValue == selectedFactoryID){

                                      //Add the command instance
                                      var commandInstanceElement = {
                                          "commandID": commandID.nodeValue,
                                          "factoryID": factoryID.nodeValue,
                                          "readerType": readerType,
                                          "host": host
                                      }

                                      $scope.commandWizardData.commandInstances.push(commandInstanceElement);
                                  }

                              }

                              //Add the New command instance label
                              var commandInstanceNewElement = {
                                  "commandID": "<New>",
                                  "factoryID": selectedFactoryID,
                                  "readerType": readerType,
                                  "host": host
                              }

                              $scope.commandWizardData.commandInstances.push(commandInstanceNewElement);

                              console.log("$scope.commandWizardData.commandInstances");
                              console.log($scope.commandWizardData.commandInstances);

                          })
                          .error(function(data, status, headers, config) {
                              console.log("error reading command instances for create command wizard");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });

                          */


                  } else if ($scope.elementSelected.elementType === 'appGroup'){

                     //load the app group properties
                     console.log("appGroup selected");

                     $scope.appGroupProperties = null;

                     //get the app id of the first app below this app group
                     var appId = $scope.elementSelected.children[0].children[0].number;

                      //call getGroupProperties operation
                      var host = angular.copy($scope.elementSelected.host);


                      $http.get(host + '/getGroupProperties/' + appId)
                          .success(function(data, status, headers, config) {

                              var xmlGroupProperties;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlGroupProperties = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlGroupProperties = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlGroupProperties.async=false;
                                  xmlGroupProperties.loadXML(data);
                              }

                              //get the xml response and extract the message response
                              if (xmlGroupProperties.getElementsByTagName("message").length > 0) {

                                  //the message tag appears in fail messages
                                  var message = xmlGroupProperties.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                                  if (message == 'Fail') {
                                      console.log("fail getting app group properties");
                                      var description = xmlGroupProperties.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                      $scope.getPropertiesErrorMsg = description;

                                  }
                              } else {

                                  //success return of properties

                                  //get the xml response and extract the values for properties
                                  var propertiesXmlVector = xmlGroupProperties.getElementsByTagName("entry");


                                  $scope.appGroupProperties = {
                                      "host": host,
                                      "appId": appId,
                                      "properties": []
                                  }


                                  for(var indexPropertyValue = 0; indexPropertyValue < propertiesXmlVector.length; indexPropertyValue++) {

                                      var key = propertiesXmlVector[indexPropertyValue].getElementsByTagName("key")[0].childNodes[0].nodeValue;
                                      var value = propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes[0].nodeValue;

                                      var propertyElement = {
                                          "key": key,
                                          "value": value
                                      }

                                      $scope.appGroupProperties.properties.push(propertyElement);

                                  }

                              }

                          }).
                          error(function(data, status, headers, config) {
                              console.log("error reading app group properties");


                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });


                  } else if ($scope.elementSelected.elementType === 'app'){

                      //load the app  properties
                      console.log("app selected");

                      $scope.appProperties = null;

                      //get the app id of the first app below this app group
                      var appId = $scope.elementSelected.number;

                      //call getAppProperties operation
                      var host = angular.copy($scope.elementSelected.host);


                      $http.get(host + '/getAppProperties/' + appId)
                          .success(function(data, status, headers, config) {

                              var xmlAppProperties;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlAppProperties = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlAppProperties = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlAppProperties.async=false;
                                  xmlAppProperties.loadXML(data);
                              }

                              //get the xml response and extract the message response
                              if (xmlAppProperties.getElementsByTagName("message").length > 0) {

                                  //the message tag appears in fail messages
                                  var message = xmlAppProperties.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                                  if (message == 'Fail') {
                                      console.log("fail getting app properties");
                                      var description = xmlAppProperties.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                      $scope.getPropertiesErrorMsg = description;

                                  }
                              } else {

                                  //success return of properties

                                  //get the xml response and extract the values for properties
                                  var propertiesXmlVector = xmlAppProperties.getElementsByTagName("entry");


                                  $scope.appProperties = {
                                      "host": host,
                                      "appId": appId,
                                      "properties": []
                                  }


                                  for(var indexPropertyValue = 0; indexPropertyValue < propertiesXmlVector.length; indexPropertyValue++) {

                                      var key = propertiesXmlVector[indexPropertyValue].getElementsByTagName("key")[0].childNodes[0].nodeValue;
                                      var value = propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes[0].nodeValue;

                                      var propertyElement = {
                                          "key": key,
                                          "value": value
                                      }

                                      $scope.appProperties.properties.push(propertyElement);

                                  }

                              }

                          }).
                          error(function(data, status, headers, config) {
                              console.log("error reading app properties");


                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });


                  } else if ($scope.elementSelected.elementType === 'readZone'){

                      //load the app properties
                      console.log("readZone selected");

                      $scope.readzoneProperties = null;
                      //$scope.sensors = [];

                      //get the app id
                      var appId = $scope.elementSelected.appId;

                      var readzone = angular.copy($scope.elementSelected.readzone);

                      //call getReadZoneProperties operation
                      var host = angular.copy($scope.elementSelected.host);

                      $http.get(host + '/getReadZoneProperties/' + appId + "/" + readzone)
                          .success(function(data, status, headers, config) {

                              var xmlReadzoneProperties;
                              if (window.DOMParser)
                              {
                                  var parser = new DOMParser();
                                  xmlReadzoneProperties = parser.parseFromString(data,"text/xml");
                              }
                              else // Internet Explorer
                              {
                                  xmlReadzoneProperties = new ActiveXObject("Microsoft.XMLDOM");
                                  xmlReadzoneProperties.async=false;
                                  xmlReadzoneProperties.loadXML(data);
                              }

                              //get the xml response and extract the message response
                              if (xmlReadzoneProperties.getElementsByTagName("message").length > 0) {

                                  //the message tag appears in fail messages
                                  var message = xmlReadzoneProperties.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                                  if (message == 'Fail') {
                                      console.log("fail getting readzone properties");
                                      var description = xmlReadzoneProperties.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                                      $scope.getPropertiesErrorMsg = description;

                                  }
                              } else {

                                  //success return of properties

                                  //get the xml response and extract the values for properties
                                  var propertiesXmlVector = xmlReadzoneProperties.getElementsByTagName("entry");

                                  $scope.readzoneProperties = {
                                      "host": host,
                                      "appId": appId,
                                      "readzone": readzone,
                                      "properties": []
                                  }


                                  for(var indexPropertyValue = 0; indexPropertyValue < propertiesXmlVector.length; indexPropertyValue++) {

                                      var key = propertiesXmlVector[indexPropertyValue].getElementsByTagName("key")[0].childNodes[0].nodeValue;
                                      var value = "";

                                      if ( propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes.length > 0 ){
                                          value = propertiesXmlVector[indexPropertyValue].getElementsByTagName("value")[0].childNodes[0].nodeValue;
                                      }

                                      var propertyElement = {
                                          "key": key,
                                          "value": value
                                      }

                                      $scope.readzoneProperties.properties.push(propertyElement);

                                  }
                                  //As readzones have four fixed properties: readerID antennas tagPattern matchPattern,
                                  //make sure if a readzone does not have a property, add it with empty value to UI
                                  var readerIDFound = false;
                                  var antennasFound = false;
                                  var tagPatternFound = false;
                                  var matchPatternFound = false;

                                  $scope.readzoneProperties.properties.forEach(function (property) {

                                      if (property.key == 'readerID') {
                                          readerIDFound = true;
                                      } else if (property.key == 'antennas') {
                                          antennasFound = true;
                                      } else if (property.key == 'tagPattern') {
                                          tagPatternFound = true;
                                      } else if (property.key == 'matchPattern') {
                                          matchPatternFound = true;
                                      }

                                  });

                                  //where property was not found, add it with empty value
                                  if (!readerIDFound){

                                      var propertyElement = {
                                          "key": 'readerID',
                                          "value": ''
                                      }

                                      $scope.readzoneProperties.properties.push(propertyElement);
                                  }

                                  if (!antennasFound){

                                      var propertyElement = {
                                          "key": 'antennas',
                                          "value": ''
                                      }

                                      $scope.readzoneProperties.properties.push(propertyElement);
                                  }

                                  if (!tagPatternFound){

                                      var propertyElement = {
                                          "key": 'tagPattern',
                                          "value": ''
                                      }

                                      $scope.readzoneProperties.properties.push(propertyElement);
                                  }

                                  if (!matchPatternFound){

                                      var propertyElement = {
                                          "key": 'matchPattern',
                                          "value": ''
                                      }

                                      $scope.readzoneProperties.properties.push(propertyElement);
                                  }

                                  //Set the help text for readzone properties
                                  $scope.readzoneProperties.properties = CommonService.setReadzonePropertiesHelpText( $scope.readzoneProperties.properties);

                              }

                          }).
                          error(function(data, status, headers, config) {
                              console.log("error reading readzone properties");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });

                      //load the sensors
                      SensorService.callSensorListService(host)
                          .success(function(data, status, headers, config) {

                              console.log("success reading sensors");
                              $scope.sensors = SensorService.getSensorsFromReceivedData(data);

                          })
                          .error(function(data, status, headers, config) {
                              console.log("error reading sensors");

                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });

                  }

                  //console.log("set 2 propertyType: " + $scope.propertyType);
                  $rootScope.operationSuccessMsg = null;
                  $rootScope.operationSuccessMsgs = [];
                  $scope.getPropertiesErrorMsg = null;



              }

              console.log("$scope.elementSelected:");
              console.log($scope.elementSelected);
          }, false);


        $scope.openSaveSensorPropertiesDialog = function(){

                ngDialog.openConfirm({template: 'saveSensorPropertiesDialogTmpl.html',

                    scope: $scope, //Pass the scope object if you need to access in the template

                    showClose: false,

                    closeByEscape: true,

                    closeByDocument: false

                }).then(

                    function(value) {

                        //confirm operation
                        if (value == 'Save'){
                            console.log("to save");

                            //call save sensor properties operation
                            saveSensorProperties($scope.elementTree.currentNode);

                        }

                    },

                    function(value) {

                        //Cancel or do nothing
                        console.log("cancel");

                    }

                );

        };

        $scope.openSaveReadzonePropertiesDialog = function(currentNode, readzoneProperties){

            var readzoneNode = angular.copy(currentNode);
            var readzoneProperties = angular.copy(readzoneProperties);

            ngDialog.openConfirm({template: 'saveReadzonePropertiesDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Save'){
                        console.log("to save");

                        //call save sensor properties operation
                        saveReadzoneProperties(readzoneNode, readzoneProperties);

                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        var saveSensorProperties = function(sensor){

            var strSensorProperties = "";

            for (var idxCat=0; idxCat < $scope.sensorProperties.propertyCategoryList.length; idxCat++){

                for (var idxProp=0; idxProp < $scope.sensorProperties.propertyCategoryList[idxCat].properties.length; idxProp++){

                    var property = $scope.sensorProperties.propertyCategoryList[idxCat].properties[idxProp];

                    console.log("property");
                    console.log(property);

                    //only update if writable
                    if (property.writable) {
                        strSensorProperties += property.name + "=" + property.value + ";"
                    }
                }

            }

            //Quit the last semicolon ;
            if (strSensorProperties.length > 0){
                strSensorProperties = strSensorProperties.substring(0, strSensorProperties.length - 1);
            }

            console.log("strSensorProperties");
            console.log(strSensorProperties);

            //call the rest operation to update sensor properties

            $http.get(sensor.host + '/setproperties/' + sensor.elementId + "/" + encodeURIComponent(strSensorProperties))
                .success(function (data, status, headers, config) {

                    var updateSensorPropertiesCommandResponse;
                    if (window.DOMParser) {
                        var parser = new DOMParser();
                        updateSensorPropertiesCommandResponse = parser.parseFromString(data, "text/xml");
                    }
                    else // Internet Explorer
                    {
                        updateSensorPropertiesCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                        updateSensorPropertiesCommandResponse.async = false;
                        updateSensorPropertiesCommandResponse.loadXML(data);
                    }

                    //get the xml response and extract the message response
                    var message = updateSensorPropertiesCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    if (message == 'Success') {
                        console.log("success updating sensor properties");
                        $scope.operationSuccessMsg = "Success saving sensor properties";
                        TreeViewPainting.paintTreeView();

                    } else {
                        var updateSensorPropertiesCommandDescription = updateSensorPropertiesCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        //$scope.setCommandPropertiesResponseStatus.description = setCommandPropertiesDescription;
                        console.log("fail updating sensor properties");
                        console.log(updateSensorPropertiesCommandDescription);

                        //show modal dialog with error
                        showErrorDialog('Error updating sensor properties: ' + updateSensorPropertiesCommandDescription);

                    }


                }).
                error(function (data, status, headers, config) {
                    console.log("error updating sensor properties");

                    //show modal dialog with error
                    showErrorDialog('Error updating sensor properties');


                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });

        }

        $scope.openSaveAppGroupPropertiesDialog = function(currentNode, appGroupProperties){

            //console.log("openSaveAppGroupPropertiesDialog.currentNode:");
            //console.log(currentNode);

            var groupName = angular.copy(currentNode.groupName);
            var appGroupProperties = angular.copy(appGroupProperties);

            ngDialog.openConfirm({template: 'saveAppGroupPropertiesDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Save'){
                        console.log("to save");

                        //call save sensor properties operation
                        saveAppGroupProperties(appGroupProperties, groupName);

                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        $scope.openSaveAppPropertiesDialog = function(appProperties, app){

            var appProperties = angular.copy(appProperties);
            var app = angular.copy(app);

            ngDialog.openConfirm({template: 'saveAppPropertiesDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Save'){
                        console.log("to save");

                        //call save sensor properties operation
                        saveAppProperties(appProperties, app);

                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };


        $scope.openSaveCommandInstancePropertiesDialog = function(commandProperties, host, commandId){

            console.log("commandProperties:");
            console.log(commandProperties);

            ngDialog.openConfirm({template: 'saveCommandInstancePropertiesDialogTmpl.html',

                scope: $scope, //Pass the scope object if you need to access in the template

                showClose: false,

                closeByEscape: true,

                closeByDocument: false

            }).then(

                function(value) {

                    //confirm operation
                    if (value == 'Save'){
                        console.log("to save");

                        //call save command properties operation
                        saveCommandInstanceProperties(commandProperties, host, commandId);

                    }

                },

                function(value) {

                    //Cancel or do nothing
                    console.log("cancel");

                }

            );

        };

        var saveCommandInstanceProperties = function(commandProperties, host, commandId){

           // console.log("commandElement:");
           // console.log(commandElement);
            console.log("commandProperties:");
            console.log(commandProperties);

            //call update command properties

            var strCommandProperties = "";

            for (var idxCat=0; idxCat < commandProperties.propertyCategoryList.length; idxCat++){

                //console.log("$scope.commandProperties.propertyCategoryList[idxCat]");
                //console.log($scope.commandProperties.propertyCategoryList[idxCat]);

                for (var idxProp=0; idxProp < commandProperties.propertyCategoryList[idxCat].properties.length; idxProp++){

                    strCommandProperties += commandProperties.propertyCategoryList[idxCat].properties[idxProp].name + "="
                    + commandProperties.propertyCategoryList[idxCat].properties[idxProp].value + ";"
                }

            }

            //Quit the last semicolon ;
            if (strCommandProperties.length > 0){
                strCommandProperties = strCommandProperties.substring(0, strCommandProperties.length - 1);
            }

            console.log("strCommandProperties");
            console.log(strCommandProperties);

                //console.log("going to set command properties: " + host + '/setproperties/' + $scope.selectedCommandInstance.commandID + '/' + strCommandProperties);
                $scope.setCommandPropertiesResponseStatus = {};

                $http.get(host + '/setproperties/' + commandId + '/' + strCommandProperties)
                    .success(function(data, status, headers, config) {

                        var xmlSetCommandPropertiesResponse;
                        if (window.DOMParser)
                        {
                            var parser = new DOMParser();
                            xmlSetCommandPropertiesResponse = parser.parseFromString(data,"text/xml");
                        }
                        else // Internet Explorer
                        {
                            xmlSetCommandPropertiesResponse = new ActiveXObject("Microsoft.XMLDOM");
                            xmlSetCommandPropertiesResponse.async=false;
                            xmlSetCommandPropertiesResponse.loadXML(data);
                        }


                        //get the xml response and extract the value
                        var message = xmlSetCommandPropertiesResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                        $scope.setCommandPropertiesResponseStatus.message = message;

                        if (message == 'Success') {
                            console.log("success setting properties for command");

                                $rootScope.operationSuccessMsg = "Success setting properties for command";
                                TreeViewPainting.paintTreeView();

                        } else {
                            var setCommandPropertiesDescription = xmlSetCommandPropertiesResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                            $scope.setCommandPropertiesResponseStatus.description = setCommandPropertiesDescription;
                            console.log("fail set command properties");
                            console.log(setCommandPropertiesDescription);

                            showErrorDialog('Error setting properties for command: ' + setCommandPropertiesDescription);
                        }


                    })
                    .error(function(data, status, headers, config) {
                        console.log("error setting properties for existing command");

                        showErrorDialog('Error setting properties for command');

                        // called asynchronously if an error occurs
                        // or server returns response with an error status.
                    });



        }

        var saveAppGroupProperties = function(appGroupProperties, groupName){

            // console.log("commandElement:");
            // console.log(commandElement);
            console.log("saveAppGroupProperties.appGroupProperties:");
            console.log(appGroupProperties);
            console.log("saveAppGroupProperties.groupName:");
            console.log(groupName);

            //call update command properties

            var strAppGroupProperties = "";

            for (var idxProp=0; idxProp < appGroupProperties.properties.length; idxProp++){

                strAppGroupProperties += appGroupProperties.properties[idxProp].key + "="
                + appGroupProperties.properties[idxProp].value + ";"
            }

            //Quit the last semicolon ;
            if (strAppGroupProperties.length > 0){
                strAppGroupProperties = strAppGroupProperties.substring(0, strAppGroupProperties.length - 1);
            }

            console.log("strAppGroupProperties");
            console.log(strAppGroupProperties);

            $scope.setAppGroupPropertiesResponseStatus = {};

            $http.get(appGroupProperties.host + '/setGroupProperties/' + appGroupProperties.appId + '/' + encodeURIComponent(strAppGroupProperties))
                .success(function(data, status, headers, config) {

                    var xmlSetAppGroupPropertiesResponse;
                    if (window.DOMParser)
                    {
                        var parser = new DOMParser();
                        xmlSetAppGroupPropertiesResponse = parser.parseFromString(data,"text/xml");
                    }
                    else // Internet Explorer
                    {
                        xmlSetAppGroupPropertiesResponse = new ActiveXObject("Microsoft.XMLDOM");
                        xmlSetAppGroupPropertiesResponse.async=false;
                        xmlSetAppGroupPropertiesResponse.loadXML(data);
                    }


                    //get the xml response and extract the value
                    var message = xmlSetAppGroupPropertiesResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    $scope.setAppGroupPropertiesResponseStatus.message = message;

                    if (message == 'Success') {
                        console.log("success setting properties for app group");

                        $rootScope.operationSuccessMsg = "Success setting properties for app group";
                        TreeViewPainting.paintTreeView();

                        //Show a modal dialog to confirm if user wants to restart apps in order for properties to take effect
                        openRestartAppsDialog(appGroupProperties.host, groupName);



                    } else {
                        var setAppGroupPropertiesDescription = xmlSetAppGroupPropertiesResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        $scope.setAppGroupPropertiesResponseStatus.description = setAppGroupPropertiesDescription;
                        console.log("fail set app group properties");
                        console.log(setAppGroupPropertiesDescription);

                        showErrorDialog('Error setting properties for app group: ' + setAppGroupPropertiesDescription);
                    }


                })
                .error(function(data, status, headers, config) {
                    console.log("error setting properties for app group");

                    showErrorDialog('Error setting properties for app group');

                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });



        }

        var saveAppProperties = function(appProperties, app){

            console.log("saveAppProperties:");
            console.log("appProperties:");
            console.log(appProperties);
            console.log("app:");
            console.log(app);

            //call update app properties

            var strAppProperties = "";

            for (var idxProp=0; idxProp < appProperties.properties.length; idxProp++){

                strAppProperties += appProperties.properties[idxProp].key + "="
                + appProperties.properties[idxProp].value + ";"
            }

            //Quit the last semicolon ;
            if (strAppProperties.length > 0){
                strAppProperties = strAppProperties.substring(0, strAppProperties.length - 1);
            }

            console.log("strAppProperties");
            console.log(strAppProperties);

            $scope.setAppPropertiesResponseStatus = {};

            $http.get(appProperties.host + '/setAppProperties/' + appProperties.appId + '/' + encodeURIComponent(strAppProperties))
                .success(function(data, status, headers, config) {

                    var xmlSetAppPropertiesResponse;
                    if (window.DOMParser)
                    {
                        var parser = new DOMParser();
                        xmlSetAppPropertiesResponse = parser.parseFromString(data,"text/xml");
                    }
                    else // Internet Explorer
                    {
                        xmlSetAppPropertiesResponse = new ActiveXObject("Microsoft.XMLDOM");
                        xmlSetAppPropertiesResponse.async=false;
                        xmlSetAppPropertiesResponse.loadXML(data);
                    }


                    //get the xml response and extract the value
                    var message = xmlSetAppPropertiesResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    $scope.setAppPropertiesResponseStatus.message = message;

                    if (message == 'Success') {
                        console.log("success setting properties for app");

                        $rootScope.operationSuccessMsg = "Success setting properties for app";
                        TreeViewPainting.paintTreeView();

                        //Show a modal dialog to confirm if user wants to restart app in order for properties to take effect
                        //Only in case app is running
                        if ( app.status == 'STARTED') {
                            openRestartAppDialog(appProperties.host, app);
                        }

                    } else {
                        var setAppPropertiesDescription = xmlSetAppPropertiesResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        $scope.setAppPropertiesResponseStatus.description = setAppPropertiesDescription;
                        console.log("fail set app properties");
                        console.log(setAppPropertiesDescription);

                        showErrorDialog('Error setting properties for app: ' + setAppPropertiesDescription);
                    }


                })
                .error(function(data, status, headers, config) {
                    console.log("error setting properties for app");

                    showErrorDialog('Error setting properties for app');

                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });



        };


        var saveReadzoneProperties = function(readzoneNode, readzoneProperties){

            console.log("saveReadzoneProperties:");
            console.log("saveReadzoneProperties.readzoneProperties:");
            console.log(readzoneProperties);

            var host = readzoneProperties.host;
            var appId = readzoneProperties.appId;
            var readzone = readzoneProperties.readzone;
            var groupName = readzoneNode.groupName;

            //call set readzone properties

            var strReadzoneProperties = "";

            for (var idxProp=0; idxProp < readzoneProperties.properties.length; idxProp++){

                var key = readzoneProperties.properties[idxProp].key;
                var value = readzoneProperties.properties[idxProp].value;

                //add property only if value is not empty
                if ( value != "" ) {

                    strReadzoneProperties += key + "=" + value + ";"
                }
            }

            //Quit the last semicolon ;
            if (strReadzoneProperties.length > 0){
                strReadzoneProperties = strReadzoneProperties.substring(0, strReadzoneProperties.length - 1);
            }

            console.log("strReadzoneProperties");
            console.log(strReadzoneProperties);

            //$scope.setReadzonePropertiesResponseStatus = {};

            $http.get(host + '/setReadZoneProperties/' + appId + '/' + readzone + '/' + encodeURIComponent(strReadzoneProperties))
                .success(function(data, status, headers, config) {

                    var xmlSetReadzonePropertiesResponse;
                    if (window.DOMParser)
                    {
                        var parser = new DOMParser();
                        xmlSetReadzonePropertiesResponse = parser.parseFromString(data,"text/xml");
                    }
                    else // Internet Explorer
                    {
                        xmlSetReadzonePropertiesResponse = new ActiveXObject("Microsoft.XMLDOM");
                        xmlSetReadzonePropertiesResponse.async=false;
                        xmlSetReadzonePropertiesResponse.loadXML(data);
                    }


                    //get the xml response and extract the value
                    var message = xmlSetReadzonePropertiesResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    //$scope.setReadzonePropertiesResponseStatus.message = message;

                    if (message == 'Success') {
                        console.log("success setting properties for readzone");

                        $rootScope.operationSuccessMsg = "Success setting properties for readzone";
                        TreeViewPainting.paintTreeView();

                        //Show a modal dialog to confirm if user wants to restart apps in order for properties to take effect
                        openRestartAppsDialog(host, groupName);

                    } else {

                        var setReadzonePropertiesDescription = xmlSetReadzonePropertiesResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        $scope.setReadzonePropertiesResponseStatus.description = setReadzonePropertiesDescription;
                        console.log("fail setting readzone properties");
                        console.log(setReadzonePropertiesDescription);

                        showErrorDialog('Error setting properties for readzone: ' + setReadzonePropertiesDescription);

                    }


                })
                .error(function(data, status, headers, config) {
                    console.log("error setting properties for readzone");

                    showErrorDialog('Error setting properties for readzone');

                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });



        };


        var startApp = function() {

            console.log("startApp");

            var host = $scope.elementSelected.host;
            var appId = $scope.elementSelected.number;

            $http.get(host + '/startapp/' + appId)
                .success(function(data, status, headers, config) {


                    var xmlStartApp;
                    if (window.DOMParser)
                    {
                        var parser = new DOMParser();
                        xmlStartApp = parser.parseFromString(data,"text/xml");
                    }
                    else // Internet Explorer
                    {
                        xmlStartApp = new ActiveXObject("Microsoft.XMLDOM");
                        xmlStartApp.async=false;
                        xmlStartApp.loadXML(data);
                    }

                    //get the xml response and extract the values
                    var startAppMessage = xmlStartApp.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    //$scope.commandWizardData.commandCreationResponseStatus.message = createCommandMessage;

                    if (startAppMessage == 'Success') {
                        console.log("success starting app");

                        $rootScope.operationSuccessMsg = "Success starting app";
                        TreeViewPainting.paintTreeView();

                    } else {
                        var startAppCommandDescription = xmlStartApp.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        //$scope.commandWizardData.commandCreationResponseStatus.description = createCommandDescription;
                        console.log("fail starting app");
                        console.log(startAppCommandDescription);
                        showErrorDialog('Error starting app: ' + startAppCommandDescription);
                    }

                })
                .error(function(data, status, headers, config) {
                    console.log("error staring app");

                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });

        };


        var stopApp = function() {

            console.log("stopApp");

            var host = $scope.elementSelected.host;
            var appId = $scope.elementSelected.number;

            $http.get(host + '/stopapp/' + appId)
                .success(function(data, status, headers, config) {


                    var xmlStopApp;
                    if (window.DOMParser)
                    {
                        var parser = new DOMParser();
                        xmlStopApp = parser.parseFromString(data,"text/xml");
                    }
                    else // Internet Explorer
                    {
                        xmlStopApp = new ActiveXObject("Microsoft.XMLDOM");
                        xmlStopApp.async=false;
                        xmlStopApp.loadXML(data);
                    }

                    //get the xml response and extract the values
                    var stopAppMessage = xmlStopApp.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                    //$scope.commandWizardData.commandCreationResponseStatus.message = createCommandMessage;

                    if (stopAppMessage == 'Success') {
                        console.log("success stopping app");

                        $rootScope.operationSuccessMsg = "Success stopping app";

                        //refresh tree view
                        TreeViewPainting.paintTreeView();

                    } else {
                        var stopAppCommandDescription = xmlStopApp.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        console.log("fail stopping app");
                        console.log(stopAppCommandDescription);
                        showErrorDialog('Error stopping app: ' + stopAppCommandDescription);
                    }

                })
                .error(function(data, status, headers, config) {
                    console.log("error stopping app");

                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });

        }



  });



















module.service('TreeViewPainting', function($http, $rootScope, ServerService, CommonService) {
       var service = {

         paintTreeView: function () {

            //Paint tree view logic
            console.log("paintTreeView function called");


             ServerService.callServerListService().
                 success(function (data, status, headers, config) {
                     console.log("funciono lectura servers ");
                     console.log("cantidad servidores:");
                     console.log(data.length);

                     $rootScope.servers = data;

                     var partialElementList = [{
                         "elementName": "Servers",
                         "elementId": "servers",
                         "elementType": "servers",
                         "collapsed": false,
                         "contextMenuId": "contextMenuServers",
                         "iconClass":"server",
                         "children": []
                     }];

                     data.forEach(function (server) {

                         server.collapsed = true;
                         server.elementName = server.displayName;
                         server.elementId = "server";
                         server.elementType = "server";
                         server.iconClass = "server-disconnected";
                         server.contextMenuId = "contextMenuServer";
                         server.children = [];
                         server.host = server.restProtocol + "://" + server.ipAddress + ":" + server.restPort;
                         server.status = 'CONNECTING';
                         server.allowSaveServerConfig = false;

                         partialElementList[0].children.push(server);

                         //After server is added, order the server list
                         partialElementList[0].children.sort( CommonService.compareElements );


                         //for each server make an asynchronous call to test whether ping operation returns success
                         $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/ping')
                             .success(function (data, status, headers, config) {

                                 var pingResponseHost = headers('host');

                                 var xmlPingTimestamp;
                                 if (window.DOMParser) {
                                     var parser = new DOMParser();
                                     xmlPingTimestamp = parser.parseFromString(data, "text/xml");
                                 }
                                 else // Internet Explorer
                                 {
                                     xmlPingTimestamp = new ActiveXObject("Microsoft.XMLDOM");
                                     xmlPingTimestamp.async = false;
                                     xmlPingTimestamp.loadXML(data);
                                 }

                                 //get the xml response and extract the ping timestamp value
                                 var timestampXmlVector = xmlPingTimestamp.getElementsByTagName("timestamp");

                                 var serverTimestamp = timestampXmlVector[0].childNodes[0].nodeValue;

                                 if (serverTimestamp) {
                                     console.log("server ping, ip: " + pingResponseHost + ", timestamp: " + serverTimestamp);

                                     //change server connecting status to connected

                                     partialElementList[0].children.forEach(function (server) {
                                         if (server.host == pingResponseHost) {

                                             //change server status
                                             server.status = 'CONNECTED';
                                             server.iconClass = "server-connected";
                                             server.allowSaveServerConfig = true;
                                         }
                                     });

                                 }

                             })
                             .error(function (data, status, headers, config) {
                                 console.log("error haciendo ping server.ipAddress: " + server.ipAddress);

                                 //server.iconClass = "server";

                                 // called asynchronously if an error occurs
                                 // or server returns response with an error status.

                                 //var pingResponseHost = headers('host');
                                 //console.log("headers in error calling ping:");
                                 //console.log(headers());

                                 //console.log("data in error calling ping:");
                                 //console.log(data);

                                 //console.log("status in error calling ping:");
                                 //console.log(status);

                                 console.log("config.url in error calling ping:");
                                 console.log(config.url);

                                 partialElementList[0].children.forEach(function (server) {

                                     //console.log("error calling ping operation, inside loop, server.host: "+ server.host + ", pingResponseHost: " + pingResponseHost);
                                     /*
                                      if (server.host == pingResponseHost){

                                      console.log("going to change status to unable to connect for server: " + server.host);
                                      //change server status
                                      server.status = 'UNABLE TO CONNECT';
                                      }
                                      */
                                 });

                             });


                         //start sensor management
                         //for each server, add the sensor management element

                         //sensor management element:
                         var sensorManagementElement = {
                             "host": server.host,
                             "restProtocol": server.restProtocol,
                             "ipAddress": server.ipAddress,
                             "restPort": server.restPort,
                             "elementName": "Sensor Management",
                             "elementId": "sensorManagement",
                             "elementType": "sensorManagement",
                             "collapsed": true,
                             "iconClass":"reader-cog",
                             "contextMenuId": "contextMenuSensorManagement",
                             "children": []
                         };
                         server.children.push(sensorManagementElement);

                         //for each server, connect and query the list of sensors and place them under sensor management element
                         $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/readers')
                             .success(function (data, status, headers, config) {

                                 var sensorsResponseHost = headers('host');

                                 var xmlSensors;
                                 if (window.DOMParser) {
                                     var parser = new DOMParser();
                                     xmlSensors = parser.parseFromString(data, "text/xml");
                                 }
                                 else // Internet Explorer
                                 {
                                     xmlSensors = new ActiveXObject("Microsoft.XMLDOM");
                                     xmlSensors.async = false;
                                     xmlSensors.loadXML(data);
                                 }

                                 //get the xml response and extract the values to construct the local sensor object
                                 var sensorXmlVector = xmlSensors.getElementsByTagName("sensor");

                                 for (var index = 0; index < sensorXmlVector.length; index++) {
                                     var serviceID = sensorXmlVector[index].getElementsByTagName("serviceID")[0].childNodes[0];
                                     var factoryID = sensorXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];

                                     //console.log("serviceID: " + serviceID.nodeValue);
                                     //console.log("factoryID: " + factoryID.nodeValue);

                                     var sensorElement = {
                                         "elementName": serviceID.nodeValue,
                                         "elementId": serviceID.nodeValue,
                                         "elementType": "sensor",
                                         "collapsed": true,
                                         "factoryID": factoryID.nodeValue,
                                         "contextMenuId": "contextMenuSensor",
                                         "iconClass":"reader",
                                         "allowCreateSession": false,
                                         "children": []
                                     };

                                     //for this responseHost search which sensorManagementElement is associated with, and associate the sensorElement
                                     //console.log("headers('host') from readers service: " + headers('host'));

                                     partialElementList[0].children.forEach(function (server) {
                                         if (server.host == sensorsResponseHost) {

                                             //add this sensor to the server
                                             sensorElement.host = server.host;
                                             sensorElement.sensorManagementElement = server.children[0];
                                             server.children[0].children.push(sensorElement);

                                             //After inserted the sensor element, order the list
                                             server.children[0].children.sort( CommonService.compareElements );
                                         }
                                     });

                                     //retrieve the sessions associated with this sensor element
                                     $http.get(sensorsResponseHost + '/readerstatus/' + serviceID.nodeValue)
                                         .success(function (data, status, headers, config) {

                                             var sessionsResponseHost = headers('host');

                                             //console.log("data for readerstatus from host: " + sessionsResponseHost + ": " + data);

                                             var xmlSessions;
                                             if (window.DOMParser) {
                                                 var parser = new DOMParser();
                                                 xmlSessions = parser.parseFromString(data, "text/xml");
                                             }
                                             else // Internet Explorer
                                             {
                                                 xmlSessions = new ActiveXObject("Microsoft.XMLDOM");
                                                 xmlSessions.async = false;
                                                 xmlSessions.loadXML(data);
                                             }

                                             //get the xml response and extract the values to construct the local session object

                                             var responseServiceId = xmlSessions.getElementsByTagName("sensor")[0].getElementsByTagName("serviceID")[0].childNodes[0].nodeValue;
                                             //console.log("responseServiceId: ");
                                             //console.log(responseServiceId);

                                             var sessionXmlVector = xmlSessions.getElementsByTagName("session");

                                             var allowCreateSession = (sessionXmlVector.length == 0);

                                             //search the reader with reader id = responseServiceId and assign allowCreateSession

                                             server.children[0].children.forEach(function (sensor) {

                                                 if (sensor.elementId == responseServiceId) {

                                                     sensor.allowCreateSession = allowCreateSession;
                                                 }

                                             });


                                             for (var index = 0; index < sessionXmlVector.length; index++) {
                                                 var sessionID = sessionXmlVector[index].getElementsByTagName("ID")[0].childNodes[0];
                                                 var sessionStatus = sessionXmlVector[index].getElementsByTagName("status")[0].childNodes[0];

                                                 //console.log("sessionID: " + sessionID.nodeValue);
                                                 //console.log("sessionStatus: " + sessionStatus.nodeValue);

                                                 var sessionElement = {
                                                     "sessionID": sessionID.nodeValue,
                                                     "elementName": "session " + sessionID.nodeValue,
                                                     "elementId": "session " + sessionID.nodeValue,
                                                     "collapsed": true,
                                                     "status": sessionStatus.nodeValue,
                                                     "contextMenuId": "contextMenuSession",
                                                     "allowStartSession": false,
                                                     "allowStopSession": false,
                                                     "children": []
                                                 };

                                                 //Assign the icon depending on status:
                                                 if (sessionStatus.nodeValue == 'CREATED' || sessionStatus.nodeValue == 'CLOSED'){
                                                     sessionElement.iconClass = 'link-red';
                                                     sessionElement.allowStartSession = true;
                                                 } else if (sessionStatus.nodeValue == 'CONNECTING'){
                                                     sessionElement.iconClass = 'link-yellow';
                                                     //not allowed to start neither to stop session, so states remains in false
                                                 }  else if (sessionStatus.nodeValue == 'PROCESSING'){
                                                     sessionElement.iconClass = 'link-green';
                                                     sessionElement.allowStopSession = true;
                                                 }





                                                 //extract the executing commands form this session and append as childern element
                                                 var executingCommandsXmlVector = xmlSessions.getElementsByTagName("executingcommand");

                                                 for (var index = 0; index < executingCommandsXmlVector.length; index++) {
                                                     var commandID = executingCommandsXmlVector[index].getElementsByTagName("commandID")[0].childNodes[0];
                                                     var factoryID = executingCommandsXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];
                                                     var commandInterval = executingCommandsXmlVector[index].getElementsByTagName("interval")[0].childNodes[0];

                                                     var commandElement = {
                                                         "elementName": commandID.nodeValue,
                                                         "elementId": commandID.nodeValue,
                                                         "collapsed": true,
                                                         "interval": commandInterval.nodeValue,
                                                         "iconClass": 'script-gear',
                                                         "session": sessionElement,
                                                         "elementType": "commandInstance_sensor",
                                                         "contextMenuId": "contextMenuCommand_sensor",
                                                         "commandType": factoryID.nodeValue,
                                                         "commandID": commandID.nodeValue,
                                                         "host": server.host,
                                                         "children": []
                                                     };

                                                     sessionElement.children.push(commandElement);

                                                     //Order the command element list after the element is added
                                                     sessionElement.children.sort( CommonService.compareElements );

                                                 }


                                                 //for this responseHost search which sensorElement is associated with, and associate the sensorElement
                                                 //console.log("headers('host') from sessions service: " + headers('host'));

                                                 partialElementList[0].children.forEach(function (server) {


                                                     if (server.host == sessionsResponseHost) {

                                                         //console.log("server.host == sessionsResponseHost:");
                                                         //console.log(server.host);

                                                         //console.log("XX server.host:");
                                                         //console.log(server.host);

                                                         //console.log("sessionsResponseHost:");
                                                         //console.log(sessionsResponseHost);

                                                         //responseServiceId is the service id that this response belongs to

                                                         //search for this server, which sensor is going to hold the session

                                                         server.children[0].children.forEach(function (sensor) {

                                                             //console.log("iter sensor:");
                                                             //console.log(sensor);
                                                             /*
                                                              console.log("sensor.elementId");
                                                              console.log(sensor.elementId);
                                                              console.log("responseServiceId");
                                                              console.log(responseServiceId);
                                                              console.log("-------------------------------------");
                                                              */


                                                             if (sensor.elementId == responseServiceId) {
                                                                 /*
                                                                  console.log("ZZ server.host:");
                                                                  console.log(server.host);

                                                                  console.log("sensor.elementId:");
                                                                  console.log(sensor.elementId);

                                                                  console.log("responseServiceId:");
                                                                  console.log(responseServiceId);
                                                                  */
                                                                 sessionElement.sensor = sensor;
                                                                 sensor.children.push(sessionElement);
                                                             }

                                                         });


                                                         //add this session to the session list of this sensor
                                                         //sensorElement.children.push(sessionElement);
                                                         //server.children[0].children.push(sensorElement);
                                                     }
                                                 });


                                             }


                                         })
                                         .error(function (data, status, headers, config) {
                                             console.log("error leyendo sesiones serviceID.nodeValue: " + serviceID.nodeValue);

                                             // called asynchronously if an error occurs
                                             // or server returns response with an error status.
                                         });


                                     //this.apps[this.apps.length] = {id:localId.textContent, number:localNumber.textContent, status:tLocalStatus.textContent};
                                 }


                             })
                             .error(function (data, status, headers, config) {
                                 console.log("error leyendo sensores server.ipAddress: " + server.ipAddress);

                                 // called asynchronously if an error occurs
                                 // or server returns response with an error status.
                             });

                         //end sensor management


                         //start command management

                         //for each server, add the command management element
                         //command management element:
                         var commandManagementElement = {
                             "host": server.host,
                             "elementName": "Command Management",
                             "elementId": "commandManagement",
                             "collapsed": true,
                             "iconClass": 'cog',
                             "children": []
                         };
                         server.children.push(commandManagementElement);


                         //call the readertypes operation
                         $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/readertypes')
                             .success(function (data, status, headers, config) {

                                 var readerTypesResponseHost = headers('host');

                                 var xmlReaderTypes;
                                 if (window.DOMParser) {
                                     var parser = new DOMParser();
                                     xmlReaderTypes = parser.parseFromString(data, "text/xml");
                                 }
                                 else // Internet Explorer
                                 {
                                     xmlReaderTypes = new ActiveXObject("Microsoft.XMLDOM");
                                     xmlReaderTypes.async = false;
                                     xmlReaderTypes.loadXML(data);
                                 }

                                 //get the xml response and extract the values to construct the reader type object
                                 var readerTypesXmlVector = xmlReaderTypes.getElementsByTagName("sensor");

                                 for (var index = 0; index < readerTypesXmlVector.length; index++) {

                                     var factoryID = readerTypesXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];
                                     var description = readerTypesXmlVector[index].getElementsByTagName("description")[0].childNodes[0];

                                     //Add the reader type to the command management element

                                     partialElementList[0].children.forEach(function (server) {

                                         if (server.host == readerTypesResponseHost) {

                                             //add the reader type under Command Management element

                                             //define an empty command structure to hold the current command
                                             var readerTypeElement = {
                                                 "elementName": factoryID.nodeValue + " Commands",
                                                 "elementId": factoryID.nodeValue + " Commands",
                                                 "collapsed": true,
                                                 "factoryID": factoryID.nodeValue,
                                                 "description": description.nodeValue,
                                                 "iconClass":"reader-cog",
                                                 "host": server.host,
                                                 "children": []
                                             };

                                             server.children[1].children.push(readerTypeElement);

                                             //Order the reader type list after the element is added
                                             server.children[1].children.sort( CommonService.compareElements );


                                         }

                                     });

                                 }

                                 //load the command types and place them under corresponding reader type

                                 $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/commandtypes')
                                     .success(function (data, status, headers, config) {

                                         var commandTypesResponseHost = headers('host');

                                         var xmlCommandTypes;
                                         if (window.DOMParser) {
                                             var parser = new DOMParser();
                                             xmlCommandTypes = parser.parseFromString(data, "text/xml");
                                         }
                                         else // Internet Explorer
                                         {
                                             xmlCommandTypes = new ActiveXObject("Microsoft.XMLDOM");
                                             xmlCommandTypes.async = false;
                                             xmlCommandTypes.loadXML(data);
                                         }

                                         //get the xml response and extract the values to construct the local command type object
                                         var commandTypeXmlVector = xmlCommandTypes.getElementsByTagName("command");


                                         for (var index = 0; index < commandTypeXmlVector.length; index++) {

                                             var factoryID = commandTypeXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];
                                             var description = commandTypeXmlVector[index].getElementsByTagName("description")[0].childNodes[0];
                                             var readerFactoryID = commandTypeXmlVector[index].getElementsByTagName("readerFactoryID")[0].childNodes[0];

                                             //Iterate the children of command management element associated to the server that is equals to the server
                                             //that sends this response, and add the command type under appropriate reader type

                                             partialElementList[0].children.forEach(function (server) {

                                                 if (server.host == commandTypesResponseHost) {

                                                     server.children[1].children.forEach(function (readerTypeElement) {

                                                         //console.log("evaluating readerFactoryElement.readerFactoryID: " + readerTypeElement.readerFactoryID);

                                                         if (readerTypeElement.factoryID == readerFactoryID.nodeValue) {

                                                             //define a command type and add it to this reader type                                                    //add the factory id element to this readerFactoryElement
                                                             var commandTypeElement = {
                                                                 "elementName": factoryID.nodeValue,
                                                                 "elementId": factoryID.nodeValue,
                                                                 "collapsed": true,
                                                                 "factoryID": factoryID.nodeValue,
                                                                 "description": description.nodeValue,
                                                                 "iconClass":"folder",
                                                                 "readerFactoryID": readerFactoryID.nodeValue,
                                                                 "elementType": "commandType",
                                                                 "contextMenuId": "contextMenuCommandType_commandManagement",
                                                                 "host": server.host,
                                                                 "readerTypeElement": readerTypeElement,
                                                                 "children": []

                                                             }

                                                             readerTypeElement.children.push(commandTypeElement);

                                                             //Order the command type list after the element is added
                                                             readerTypeElement.children.sort( CommonService.compareElements );

                                                         }

                                                     });

                                                 }
                                             });

                                         }

                                         //call the commands service to load the commands associated with each command type

                                         $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/commands')
                                             .success(function (data, status, headers, config) {

                                                 var commandsResponseHost = headers('host');

                                                 var xmlCommands;
                                                 if (window.DOMParser) {
                                                     var parser = new DOMParser();
                                                     xmlCommands = parser.parseFromString(data, "text/xml");
                                                 }
                                                 else // Internet Explorer
                                                 {
                                                     xmlCommands = new ActiveXObject("Microsoft.XMLDOM");
                                                     xmlCommands.async = false;
                                                     xmlCommands.loadXML(data);
                                                 }

                                                 //get the xml response and extract the values to construct the local command object
                                                 var commandXmlVector = xmlCommands.getElementsByTagName("command");

                                                 for (var index = 0; index < commandXmlVector.length; index++) {

                                                     var commandID = commandXmlVector[index].getElementsByTagName("commandID")[0].childNodes[0];
                                                     var factoryID = commandXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];

                                                     //Iterate the children of command factory id element associated to the server that is equals to the server
                                                     //that sends this response, and if factoryID are equal, then associate the command to that command factory

                                                     partialElementList[0].children.forEach(function (server) {

                                                         if (server.host == commandsResponseHost) {

                                                             //search for this server, if there exist a commandElement with this factoryID

                                                             server.children[1].children.forEach(function (readerFactoryElement) {

                                                                 readerFactoryElement.children.forEach(function (factoryElement) {

                                                                     if (factoryElement.factoryID == factoryID.nodeValue) {

                                                                         //define a command structure to hold the current command
                                                                         var commandElement = {
                                                                             "elementName": commandID.nodeValue,
                                                                             "elementId": commandID.nodeValue,
                                                                             "collapsed": true,
                                                                             "commandID": commandID.nodeValue,
                                                                             "factoryID": factoryID.nodeValue,
                                                                             "elementType": "commandInstance_commandManagement",
                                                                             "iconClass":"cog",
                                                                             "host": server.host,
                                                                             "contextMenuId": "contextMenuCommand_commandManagement",
                                                                             "factoryElement": factoryElement,
                                                                             "children": []
                                                                         };

                                                                         factoryElement.children.push(commandElement);

                                                                         //Order the command element list after the element is added
                                                                         factoryElement.children.sort( CommonService.compareElements );

                                                                     }

                                                                 });

                                                             });

                                                         }

                                                     });

                                                 }

                                             }).
                                             error(function (data, status, headers, config) {
                                                 console.log("error reading commands");


                                                 // called asynchronously if an error occurs
                                                 // or server returns response with an error status.
                                             });


                                     }).
                                     error(function (data, status, headers, config) {
                                         console.log("error reading command types");


                                         // called asynchronously if an error occurs
                                         // or server returns response with an error status.
                                     });


                             }).
                             error(function (data, status, headers, config) {
                                 console.log("error reading reader types");


                                 // called asynchronously if an error occurs
                                 // or server returns response with an error status.
                             });

                         //end command management



                         //start app management

                         //for each server, add the app management element
                         //app management element:
                         var appManagementElement = {
                             "elementName": "App Management",
                             "elementId": "App Management",
                             "collapsed": true,
                             "iconClass": "app-dev",
                             "children": []
                         };
                         server.children.push(appManagementElement);

                         //add application groups element

                         var appGroupsElement = {
                             "elementName": "App Groups",
                             "elementId": "App Group",
                             "collapsed": true,
                             "iconClass": "appgroups",
                             "children": []
                         };

                         appManagementElement.children.push(appGroupsElement);

                         //load app groups

                         //call apps command
                         $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/apps')

                             .success(function (data, status, headers, config) {

                                 var appsResponseHost = headers('host');

                                 var xmlApps;
                                 if (window.DOMParser) {
                                     var parser = new DOMParser();
                                     xmlApps = parser.parseFromString(data, "text/xml");
                                 }
                                 else // Internet Explorer
                                 {
                                     xmlApps = new ActiveXObject("Microsoft.XMLDOM");
                                     xmlApps.async = false;
                                     xmlApps.loadXML(data);
                                 }

                                 //get the xml response and extract the values to construct the app groups
                                 var appsXmlVector = xmlApps.getElementsByTagName("app");

                                 for (var index = 0; index < appsXmlVector.length; index++) {

                                     var id = appsXmlVector[index].getElementsByTagName("id")[0].childNodes[0];
                                     var number = appsXmlVector[index].getElementsByTagName("number")[0].childNodes[0];
                                     var status = appsXmlVector[index].getElementsByTagName("status")[0].childNodes[0];

                                     var groupName = id.nodeValue.split(":")[0];
                                     var appName = id.nodeValue.split(":")[1];

                                     //Iterate the children of app groups element associated to the server that is equals to the server
                                     //that sends this response, and if this app group does not exist under that structure, then create it

                                     partialElementList[0].children.forEach(function (server) {

                                         if (server.host == appsResponseHost) {

                                             //search for this server, if there exist an appGroupElement with this groupName

                                             var appGroupFound = false;

                                             server.children[2].children[0].children.forEach(function (appGroupElement) {

                                                 if (appGroupElement.groupName == groupName) {

                                                     console.log("readerFactoryFound");

                                                     appGroupFound = true;
                                                     //TODO How to break here this loop (when appGroupFound == true)

                                                 }

                                             });

                                             if (appGroupFound == false) {

                                                 //create the appGroupElement and add it
                                                 var appGroupElement = {
                                                     "elementName": groupName,
                                                     "elementId": groupName,
                                                     "collapsed": true,
                                                     "groupName": groupName,
                                                     "readzoneAppId": "",
                                                     "elementType":"appGroup",
                                                     "iconClass": "group",
                                                     "host":server.host,
                                                     "children": []
                                                 };

                                                 //Add Apps element label
                                                 var appsElement = {
                                                     "elementName": "Apps",
                                                     "elementId": "Apps",
                                                     "collapsed": true,
                                                     "groupName": groupName,
                                                     "iconClass": "apps",
                                                     "host": server.host,
                                                     "children": []
                                                 };

                                                 appGroupElement.children.push(appsElement);

                                                 //Add Readzones element label
                                                 var readZonesElement = {
                                                     "elementName": "ReadZones",
                                                     "elementId": "ReadZones",
                                                     "collapsed": true,
                                                     "groupName": groupName,
                                                     "iconClass": "readzones",
                                                     "host": server.host,
                                                     "appId": "",
                                                     "contextMenuId": "contextMenuReadZones",
                                                     "elementType": "readZones",
                                                     "children": []
                                                 };

                                                 //readZonesElement.appId = appGroupElement.readzoneAppId;

                                                 appGroupElement.children.push(readZonesElement);

                                                 server.children[2].children[0].children.push(angular.copy(appGroupElement));

                                                 //Order the app group list after the element is added
                                                 server.children[2].children[0].children.sort( CommonService.compareElements );


                                             }

                                             //After adding the appGroup if it did not exist, we have to associate the current
                                             //application to its corresponding app group

                                             //iterate the apps elements

                                             server.children[2].children[0].children.forEach(function (appGroupElement) {

                                                 if (appGroupElement.groupName == groupName) {

                                                     //add the app to this appGroup
                                                     var appElement = {
                                                         "elementName": appName,
                                                         "elementId": appName,
                                                         "collapsed": true,
                                                         "groupName": groupName,
                                                         "appName": appName,
                                                         "number": number.nodeValue,
                                                         "status": status.nodeValue,
                                                         "elementType": "app",
                                                         "contextMenuId": "contextMenuApp",
                                                         "host": appGroupElement.host,
                                                         "allowStartApp": false,
                                                         "allowStopApp": false,
                                                         "children": []

                                                     };

                                                     if ( appElement.status == 'STARTED' ){

                                                         appElement.allowStopApp = true;
                                                         appElement.iconClass = 'app-started';

                                                     } else if ( appElement.status == 'STOPPED' ) {

                                                         appElement.allowStartApp = true;
                                                         appElement.iconClass = 'app-stopped';

                                                     }

                                                     appGroupElement.children[0].children.push(appElement);

                                                     //Order the app list after the element is added
                                                     appGroupElement.children[0].children.sort( CommonService.compareElements );

                                                     //Add the application number to this application group in order to later add the associated read zones
                                                     if (appGroupElement.readzoneAppId == "") {
                                                         appGroupElement.readzoneAppId = appElement.number;
                                                     }

                                                     //Add the application id to the readzones element
                                                     if ( appGroupElement.children[1].appId == ""){
                                                         appGroupElement.children[1].appId = appElement.number;;
                                                     }


                                                 }

                                             });


                                         }
                                     });

                                 }

                                 //add the readzones

                                 //call readzones command
                                 //iterate the app groups an call the readzones command


                                 server.children[2].children[0].children.forEach(function (appGroupElement) {


                                     $http.get(server.restProtocol + "://" + server.ipAddress + ":" + server.restPort + '/getReadZones/' + appGroupElement.readzoneAppId)

                                         .success(function (data, status, headers, config) {

                                             //console.log("config en getreadzones:");
                                             //console.log(config);

                                             var successReadzoneAppId = config.url.substring(config.url.lastIndexOf("/") + 1, config.url.length);
                                             //console.log("successReadzoneAppId:" + successReadzoneAppId);

                                             var readzonesResponseHost = headers('host');

                                             var xmlReadzones;
                                             if (window.DOMParser) {
                                                 var parser = new DOMParser();
                                                 xmlReadzones = parser.parseFromString(data, "text/xml");
                                             }
                                             else // Internet Explorer
                                             {
                                                 xmlReadzones = new ActiveXObject("Microsoft.XMLDOM");
                                                 xmlReadzones.async = false;
                                                 xmlReadzones.loadXML(data);
                                             }

                                             //get the xml response and extract the values to construct the readzone element
                                             var readzonesXmlVector = xmlReadzones.getElementsByTagName("entry");


                                             partialElementList[0].children.forEach(function (server) {

                                                 if (server.host == readzonesResponseHost) {

                                                     //iterate to find the app group where to add this readzone
                                                     server.children[2].children[0].children.forEach(function (appGroupElement) {

                                                         if (appGroupElement.readzoneAppId == successReadzoneAppId) {

                                                             for (var index = 0; index < readzonesXmlVector.length; index++) {

                                                                 var readzone = readzonesXmlVector[index].getElementsByTagName("value")[0].childNodes[0].nodeValue;

                                                                 //create the readzoneElement and add it
                                                                 var readzoneElement = {
                                                                     "elementName": readzone,
                                                                     "elementId": readzone,
                                                                     "collapsed": true,
                                                                     "readzone": readzone,
                                                                     "iconClass": "readzone",
                                                                     "appId": appGroupElement.readzoneAppId,
                                                                     "contextMenuId": "contextMenuReadZone",
                                                                     "host": appGroupElement.host,
                                                                     "groupName": appGroupElement.groupName,
                                                                     "elementType": "readZone",
                                                                     "children": []
                                                                 };

                                                                 appGroupElement.children[1].children.push(angular.copy(readzoneElement));

                                                                 //Order the readzone list after the element is added
                                                                 appGroupElement.children[1].children.sort( CommonService.compareElements );
                                                             }

                                                         }

                                                     });


                                                 }

                                             });


                                         }).
                                         error(function (data, status, headers, config) {
                                             console.log("error reading readzones");


                                             // called asynchronously if an error occurs
                                             // or server returns response with an error status.
                                         });

                                 });


                             }).
                             error(function (data, status, headers, config) {
                                 console.log("paintTreeView. Error reading apps");
                                 console.log("paintTreeView. data:");
                                 console.log(data);


                                 // called asynchronously if an error occurs
                                 // or server returns response with an error status.
                             });


                     });

                     console.log("partialElementList");
                     console.log(partialElementList);

                     //scope.elementList = partialElementList;
                     $rootScope.elementList = partialElementList;


                     // this callback will be called asynchronously
                     // when the response is available
                 }).
                 error(function (data, status, headers, config) {
                     console.log("error leyendo servidores ");


                     // called asynchronously if an error occurs
                     // or server returns response with an error status.
                 });

             //Ini test
             console.log("$rootScope.elementList: ");
             console.log($rootScope.elementList);
             console.log("$rootScope: ");
             console.log($rootScope);
             console.log("$rootScope.Scope");
             console.log($rootScope.$Scope);
             //$rootScope.elementList[0].children[0].status = 'CONNECTING';
             //$rootScope.elementList[0].children[1].status = 'CONNECTING';
             //end test

             }

       }

       return service;
 });


