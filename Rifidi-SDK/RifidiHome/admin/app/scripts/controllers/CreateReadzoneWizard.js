'use strict';

/**
 * @ngdoc function
 * @name rifidiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the rifidiApp
 */
angular.module('rifidiApp')
  .controller('CreateReadzoneWizardCtrl', function ($rootScope, $scope, $http, $routeParams, $location, ngDialog, commonVariableService
                                                    , CommonService, SensorService, AppService) {


      $scope.booleanValues = CommonService.getBooleanValues();

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

      initWizardData();


      function initWizardData() {

        $scope.readzoneProperties = null;

        //get the app id
        var appId = $scope.elementSelected.appId;

        //call getReadZoneProperties operation
        var host = angular.copy($scope.elementSelected.host);

        $scope.readzoneProperties = {
          "host": host,
          "appId": appId,
          "readzone": "",
          "properties": []
        }

        //As readzones have four fixed properties: readerID antennas tagPattern matchPattern,
        //then create a list with those properties

        var propertyElement = {
          "key": 'readerID',
          "value": ''
        }

        $scope.readzoneProperties.properties.push(angular.copy(propertyElement));

        propertyElement = {
          "key": 'antennas',
          "value": ''
        }

        $scope.readzoneProperties.properties.push(angular.copy(propertyElement));

        var propertyElement = {
          "key": 'tagPattern',
          "value": ''
        }

        $scope.readzoneProperties.properties.push(angular.copy(propertyElement));

        var propertyElement = {
          "key": 'matchPattern',
          "value": ''
        }

        $scope.readzoneProperties.properties.push(angular.copy(propertyElement));

        //Set the help text for readzone properties
        $scope.readzoneProperties.properties = CommonService.setReadzonePropertiesHelpText( $scope.readzoneProperties.properties);

        //Init sensors
        initSensors(host);


      };

      function initSensors(host){

        SensorService.callSensorListService(host)
            .success(function (data, status, headers, config) {

              $scope.sensors = SensorService.getSensorsFromReceivedData(data);

            })
            . error(function (data, status, headers, config) {

              console.log("error getting sensors");

              showErrorDialog('Error getting sensors');

            });
      };


      $scope.openCreateReadzoneDialog = function(){

        ngDialog.openConfirm({template: 'createReadzoneDialogTmpl.html',

          scope: $scope, //Pass the scope object if you need to access in the template

          showClose: false,

          closeByEscape: true,

          closeByDocument: false

        }).then(

            function(value) {

              //confirm operation
              if (value == 'Create'){
                console.log("to create");

                //submit the command
                createReadzone();

              }

            },

            function(value) {

              //Cancel or do nothing
              console.log("cancel");

            }

        );

      };

      var createReadzone = function() {

        var host = $scope.readzoneProperties.host;
        var appId = $scope.readzoneProperties.appId;
        var readzone = $scope.readzoneProperties.readzone;

        var groupName = angular.copy($scope.elementSelected.groupName);

        //call create readzone properties

        var strReadzoneProperties = "";

        for (var idxProp=0; idxProp < $scope.readzoneProperties.properties.length; idxProp++){

          var key = $scope.readzoneProperties.properties[idxProp].key;
          var value = $scope.readzoneProperties.properties[idxProp].value;

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

        //Create readzone
        console.log("going to create readzone");
        //$scope.commandWizardData.commandCreationResponseStatus = {};

        //create readzone
        $http.get(host + '/addReadZone/' + appId + '/' + readzone + '/' + strReadzoneProperties)
            .success(function (data, status, headers, config) {

              console.log("success response adding readzone");

              var xmlAddReadzoneResponse;
              if (window.DOMParser) {
                var parser = new DOMParser();
                xmlAddReadzoneResponse = parser.parseFromString(data, "text/xml");
              }
              else // Internet Explorer
              {
                xmlAddReadzoneResponse = new ActiveXObject("Microsoft.XMLDOM");
                xmlAddReadzoneResponse.async = false;
                xmlAddReadzoneResponse.loadXML(data);
              }

              //get the xml response and extract the values for properties
              var addReadzoneCommandMessage = xmlAddReadzoneResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

              //$scope.commandWizardData.commandCreationResponseStatus.message = createCommandMessage;

              if (addReadzoneCommandMessage == 'Success') {
                console.log("success adding readzone");

                setSuccessMessage("Success adding readzone");
                $rootScope.operationSuccessMsg = getSuccessMessage();

              //Show a modal dialog to confirm if user wants to restart apps in order for properties to take effect
              openRestartAppsDialog(host, groupName);

              } else {

                var addReadzoneCommandDescription = xmlAddReadzoneResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                //$scope.commandWizardData.commandCreationResponseStatus.description = createCommandDescription;
                console.log("fail adding readzone");
                console.log(addReadzoneCommandDescription);
                showErrorDialog('Error adding readzone: ' + addReadzoneCommandDescription);
              }


            }).
            error(function (data, status, headers, config) {
              console.log("error adding readzone");

              showErrorDialog('Error adding readzone');


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


  });
