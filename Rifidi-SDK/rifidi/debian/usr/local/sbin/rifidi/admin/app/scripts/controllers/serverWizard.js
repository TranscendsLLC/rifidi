'use strict';

/**
 * @ngdoc function
 * @name rifidiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the rifidiApp
 */
angular.module('rifidiApp')
  .controller('ServerWizardCtrl', function ($rootScope, $scope, $http, $routeParams, $location, ngDialog,
                                            commonVariableService, ServerService, MenuService) {

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

      $scope.serverToCreate = {};
      $scope.serverCreationStatus = {};

      $scope.serverProtocols = [{"protocol": "http"}, {"protocol":"https"}];

      var defaultPort = 8111;
      $scope.serverToCreate.restPort = angular.copy(defaultPort);


      var createServer = function(){
        console.log("create server");

        $scope.serverCreationStatus = {};

        console.log($scope.serverToCreate);

        //Validate display name does not exist
          ServerService.callServerListService().
            success(function (data, status, headers, config) {
              console.log("worked servers load on server creation");

              var displayNameisUnique = true;

              var serversOriginalData = angular.copy(data);

              data.forEach(function (server) {

                if (server.displayName == $scope.serverToCreate.displayName){

                  //displayName already exists, then can not be created again
                  displayNameisUnique = false;
                }

              });

              if (displayNameisUnique){

                //create server
                //iterate original server list
                var dataToStore = "[";

                serversOriginalData.forEach(function (server) {

                  dataToStore +=
                  '{'
                  + '"displayName": ' + '"' + server.displayName + '",'
                  + '"restProtocol" : ' + '"' + server.restProtocol + '",'
                  + '"ipAddress" : ' + '"' + server.ipAddress + '",'
                  + '"restPort" : ' + '"' + server.restPort + '"'
                  + '},';

                });

                console.log("dataToStore:");
                console.log(dataToStore);

                //add the data of server to create
                dataToStore +=
                    '{'
                    + '"displayName": ' + '"' + $scope.serverToCreate.displayName + '",'
                    + '"restProtocol" : ' + '"' + $scope.serverToCreate.restProtocol.protocol + '",'
                    + '"ipAddress" : ' + '"' + $scope.serverToCreate.ipAddress + '",'
                    + '"restPort" : ' + '"' + $scope.serverToCreate.restPort + '"'
                    + '}]';

                console.log("dataToStore:");
                console.log(dataToStore);

                //call the rest command to store data
                  ServerService.callUpdateServersService(dataToStore).
                    success(function (data, status, headers, config) {

                      console.log("success response adding server");

                      var xmlAddServerResponse;
                      if (window.DOMParser) {
                        var parser = new DOMParser();
                        xmlAddServerResponse = parser.parseFromString(data, "text/xml");
                      }
                      else // Internet Explorer
                      {
                        xmlAddServerResponse = new ActiveXObject("Microsoft.XMLDOM");
                        xmlAddServerResponse.async = false;
                        xmlAddServerResponse.loadXML(data);
                      }

                      //get the xml response and extract the values for properties
                      var addServerCommandMessage = xmlAddServerResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                      if (addServerCommandMessage == 'Success') {
                        console.log("Success adding server");

                        setSuccessMessage("Success adding server");
                        $rootScope.operationSuccessMsg = getSuccessMessage();

                        //refresh tree view
                        //TreeViewPainting.paintTreeView();
                        MenuService.createUpdateMenu();


                      } else {

                        var addServerCommandDescription = xmlAddServerResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                        console.log("fail adding server");
                        console.log(addServerCommandDescription);
                        showErrorDialog('Error adding server: ' + addServerCommandDescription);
                      }

                    }).
                    error(function (data, status, headers, config) {
                      console.log("error adding server");

                      showErrorDialog('Error adding server');


                      // called asynchronously if an error occurs
                      // or server returns response with an error status.
                    });



              } else {

                //do not create server, and display error message
                //$scope.serverCreationStatus.status = 'Fail';
                //$scope.serverCreationStatus.message = 'Display name ' + $scope.serverToCreate.displayName + ' already exists';
                showErrorDialog('Error adding server: ' + 'Display name ' + $scope.serverToCreate.displayName + ' already exists');

              }

            }).
            error(function (data, status, headers, config) {
              console.log("error reading servers on creating server");


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

      $scope.openCreateServerDialog = function() {

        ngDialog.openConfirm({template: 'createServerDialogTmpl.html',

          scope: $scope, //Pass the scope object if you need to access in the template

          showClose: false,

          closeByEscape: true,

          closeByDocument: false

        }).then(

            function(value) {

              //confirm operation
              if (value == 'Create'){
                console.log("to create");

                //call create server operation
                createServer();

              }

            },

            function(value) {

              //Cancel or do nothing
              console.log("cancel");

            }

        );

      };

      $scope.submitForm = function(isValid) {

        // check to make sure the form is completely valid
        if (isValid) {
          alert('our form is amazing');
        }

      };


  });
