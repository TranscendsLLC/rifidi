'use strict';

/**
 * @ngdoc function
 * @name rifidiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the rifidiApp
 */
angular.module('rifidiApp')
  .controller('CreateReadzoneWizardCtrl', function ($rootScope, $scope, $http, $routeParams, $location, ngDialog, commonVariableService) {


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

                //continueExecutingCommand(commandID);

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
      }



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
