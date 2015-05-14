'use strict';

/**
 * @ngdoc function
 * @name rifidiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the rifidiApp
 */
angular.module('rifidiApp')
  .controller('JobSubmissionWizardCtrl', function ($rootScope, $scope, $http, $routeParams, $location, ngDialog, commonVariableService,
    MenuService) {


      var getSuccessMessage = function () {
        return commonVariableService.getSuccessMessage();
      };

      var setSuccessMessage = function (msg) {
        if (msg != '') {
          commonVariableService.setSuccessMessage(msg);
        }
      };

      //retrieve the server data

      //var restProtocol = angular.copy($scope.elementSelected.sensor.sensorManagementElement.restProtocol);
      //var ipAddress = angular.copy($scope.elementSelected.sensor.sensorManagementElement.ipAddress);
      //var restPort = angular.copy($scope.elementSelected.sensor.sensorManagementElement.restPort);
      var readerType = angular.copy($scope.elementSelected.sensor.factoryID);

      var session = angular.copy($scope.elementSelected);

      //var host = restProtocol + "://" +  ipAddress + ":" + restPort;

      $scope.commandWizardData = {};
      $scope.commandWizardData.interval = 1000;
      $scope.commandWizardData.schedulingOption = "recurring";
      $scope.commandWizardData.session = session;

      $scope.go = function ( path ) {
        $location.path( path );
      };

      //load the command types

      //load command templates for selected reader type
      $http.get($scope.elementSelected.host + '/commandtypes')
          .success(function(data, status, headers, config) {


            var xmlCommandTypes;
            if (window.DOMParser)
            {
              var parser = new DOMParser();
              xmlCommandTypes = parser.parseFromString(data,"text/xml");
            }
            else // Internet Explorer
            {
              xmlCommandTypes = new ActiveXObject("Microsoft.XMLDOM");
              xmlCommandTypes.async=false;
              xmlCommandTypes.loadXML(data);
            }

            //get the xml response and extract the values to construct the local command type object
            var commandTypeXmlVector = xmlCommandTypes.getElementsByTagName("command");

            $scope.commandWizardData.commandTypes = [];

            for(var index = 0; index < commandTypeXmlVector.length; index++) {

              var factoryID = commandTypeXmlVector[index].getElementsByTagName("factoryID")[0].childNodes[0];
              var description = commandTypeXmlVector[index].getElementsByTagName("description")[0].childNodes[0];
              var readerFactoryID = commandTypeXmlVector[index].getElementsByTagName("readerFactoryID")[0].childNodes[0];

              if (readerFactoryID.nodeValue == readerType){

                //Add the command type
                var commandTypeElement = {
                  "factoryID": factoryID.nodeValue,
                  "description": description.nodeValue,
                  "readerFactoryID": readerFactoryID.nodeValue

                }

                $scope.commandWizardData.commandTypes.push(commandTypeElement);
              }

            }

          })
          .error(function(data, status, headers, config) {
            console.log("error reading command types for command wizard");

            // called asynchronously if an error occurs
            // or server returns response with an error status.
          });


      $scope.commandTypeSelectAction = function(selectedCommandType){

        //clear command instances list
        $scope.commandWizardData.commandInstances = [];
        //$scope.commandWizardData.commandType = selectedCommandType;

        //load the command instances for selected command type
        $http.get($scope.elementSelected.host + '/commands')
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

                //console.log("readerFactoryID.nodeValue:");
                //console.log(readerFactoryID.nodeValue);

                //console.log("$scope.selectedReaderType:");
                //console.log($scope.selectedReaderType);

                if (factoryID.nodeValue == selectedCommandType.factoryID){

                  //Add the command instance
                  var commandInstanceElement = {
                    "commandID": commandID.nodeValue,
                    "factoryID": factoryID.nodeValue
                  }

                  $scope.commandWizardData.commandInstances.push(commandInstanceElement);
                }

              }

              //Add the New command instance label
              var commandInstanceNewElement = {
                "commandID": "<New>",
                "factoryID": selectedCommandType.factoryID
              }

              $scope.commandWizardData.commandInstances.push(commandInstanceNewElement);

            })
            .error(function(data, status, headers, config) {
              console.log("error reading command instances for command wizard");

              // called asynchronously if an error occurs
              // or server returns response with an error status.
            });



      }


      $scope.commandInstanceSelectAction = function(selectedCommandInstance){

        $scope.commandWizardData.commandInstance = selectedCommandInstance;

        //Get the properties for the selected command type, from readermetadata
        $http.get($scope.elementSelected.host + '/readermetadata')
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

                if (readerID.nodeValue == readerType && id.nodeValue ==  $scope.commandWizardData.commandInstance.factoryID){

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

                  //If user selects a command instance (not the <New> option), then load the current values for every property
                  if ($scope.commandWizardData.commandInstance.commandID != '<New>'){

                    console.log("commandWizardData.commandInstance.commandID != '<New>'");


                    //call the service to get properties of command instance
                    $http.get($scope.elementSelected.host + '/getproperties/' + $scope.commandWizardData.commandInstance.commandID)
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
                            $scope.commandWizardData.commandProperties.propertyCategoryList.forEach(function (propertyCategory) {

                              //Iterate al categories to find this property and set the value
                              propertyCategory.properties.forEach(function (property) {

                                if (property.name == key.nodeValue){

                                  //Set the value for property

                                  if (property.type == 'java.lang.Integer'){
                                    property.value = angular.copy(parseInt(value.nodeValue));
                                  } else {
                                    property.value = angular.copy(value.nodeValue);
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


              }


            })
            .error(function(data, status, headers, config) {
              console.log("error reading readermetadata for command wizard: command instance selection");

              // called asynchronously if an error occurs
              // or server returns response with an error status.
            });

      }

      $scope.openSubmitJobDialog = function(){

        ngDialog.openConfirm({template: 'submitJobDialogTmpl.html',

          scope: $scope, //Pass the scope object if you need to access in the template

          showClose: false,

          closeByEscape: true,

          closeByDocument: false

        }).then(

            function(value) {

              //confirm operation
              if (value == 'Submit'){
                console.log("to submit");

                //submit the command
                submitJob();

              }

            },

            function(value) {

              //Cancel or do nothing
              console.log("cancel");

            }

        );

      };

      var submitJob = function(){

        var strCommandProperties = "";

        for (var idxCat=0; idxCat < $scope.commandWizardData.commandProperties.propertyCategoryList.length; idxCat++){

          //console.log("$scope.commandProperties.propertyCategoryList[idxCat]");
          //console.log($scope.commandProperties.propertyCategoryList[idxCat]);

          for (var idxProp=0; idxProp < $scope.commandWizardData.commandProperties.propertyCategoryList[idxCat].properties.length; idxProp++){

            strCommandProperties += $scope.commandWizardData.commandProperties.propertyCategoryList[idxCat].properties[idxProp].name + "="
            + $scope.commandWizardData.commandProperties.propertyCategoryList[idxCat].properties[idxProp].value + ";"
          }

        }

        //Quit the last semicolon ;
        if (strCommandProperties.length > 0){
          strCommandProperties = strCommandProperties.substring(0, strCommandProperties.length - 1);
        }

        console.log("strCommandProperties");
        console.log(strCommandProperties);

        //Check if need to create command
        if($scope.commandWizardData.commandInstance.commandID == '<New>'){

          //Create command
          console.log("going to create command");
          $scope.commandWizardData.commandCreationResponseStatus = {};

          //create command
          $http.get($scope.elementSelected.host + '/createcommand/' + $scope.commandWizardData.commandType.factoryID + "/" + encodeURIComponent(strCommandProperties))
              .success(function(data, status, headers, config) {

                console.log("success response creating command in wizard");

                var xmlCreateCommandResponse;
                if (window.DOMParser)
                {
                  var parser = new DOMParser();
                  xmlCreateCommandResponse = parser.parseFromString(data,"text/xml");
                }
                else // Internet Explorer
                {
                  xmlCreateCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                  xmlCreateCommandResponse.async=false;
                  xmlCreateCommandResponse.loadXML(data);
                }

                //get the xml response and extract the values for properties
                var createCommandMessage = xmlCreateCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                $scope.commandWizardData.commandCreationResponseStatus.message = createCommandMessage;

                if (createCommandMessage == 'Success') {
                  console.log("success creating command by wizard");

                  var commandID = xmlCreateCommandResponse.getElementsByTagName("commandID")[0].childNodes[0].nodeValue;

                  setSuccessMessage("Success creating command: " + commandID);
                  $rootScope.operationSuccessMsg = getSuccessMessage();

                  continueExecutingCommand(commandID);

                } else {
                  var createCommandDescription = xmlCreateCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                  $scope.commandWizardData.commandCreationResponseStatus.description = createCommandDescription;
                  console.log("fail creating command by wizard");
                  console.log(createCommandDescription);
                  showErrorDialog('Error creating command: ' + createCommandDescription);
                }


              }).
              error(function(data, status, headers, config) {
                console.log("error creating command in wizard");

                showErrorDialog('Error creating command');


                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });



        } else {

          console.log("NOT going to create command");
          //Set properties for already existing command instance

          //console.log("going to set command properties: " + host + '/setproperties/' + $scope.selectedCommandInstance.commandID + '/' + strCommandProperties);
          $scope.commandWizardData.setCommandPropertiesResponseStatus = {};

          $http.get($scope.elementSelected.host + '/setproperties/' + $scope.commandWizardData.commandInstance.commandID + '/' + encodeURIComponent(strCommandProperties))
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

                $scope.commandWizardData.setCommandPropertiesResponseStatus.message = message;

                if (message == 'Success') {
                  console.log("success setting properties for command in wizard");

                  if($rootScope.operationSuccessMsg != null){

                    $rootScope.operationSuccessMsg = angular.copy($rootScope.operationSuccessMsg) + ". Success setting properties for command";

                  } else {

                    $rootScope.operationSuccessMsg = angular.copy($rootScope.operationSuccessMsg) + ". Success setting properties for command";
                  }




                  //Can continue with next service call in wizard: execute command, but must ensure that asynchronous call
                  //was made and successfully finished
                  //canExecuteCommand = true;
                  continueExecutingCommand($scope.commandWizardData.commandInstance.commandID);


                } else {
                  var setCommandPropertiesDescription = xmlSetCommandPropertiesResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                  $scope.commandWizardData.setCommandPropertiesResponseStatus.description = setCommandPropertiesDescription;
                  console.log("fail set command properties by wizard");
                  console.log(setCommandPropertiesDescription);

                  showErrorDialog('Error setting properties for command: ' + setCommandPropertiesDescription);
                }


              })
              .error(function(data, status, headers, config) {
                console.log("error setting properties for existing command in wizard");

                showErrorDialog('Error setting properties for command');

                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });


        }

        function continueExecutingCommand(commandId){

          $scope.commandWizardData.executeCommandResponseStatus = {};

          console.log("continueExecutingCommand for command id: " + commandId);

          var repeatInterval = -1;

          if ($scope.commandWizardData.schedulingOption == "recurring"){
            repeatInterval = $scope.commandWizardData.interval;
          }

          console.log("session.sensor.elementId:");
          console.log(session.sensor.elementId);

          console.log("going to call execute command: " + $scope.elementSelected.host + '/executecommand/' + session.sensor.elementId + "/" + session.sessionID + "/" +  commandId + '/' + repeatInterval);

          $http.get($scope.elementSelected.host + '/executecommand/' + session.sensor.elementId + "/" + session.sessionID + "/" + commandId + '/' + repeatInterval)
              .success(function(data, status, headers, config) {

                var xmlExecuteCommandResponse;
                if (window.DOMParser)
                {
                  var parser = new DOMParser();
                  xmlExecuteCommandResponse = parser.parseFromString(data,"text/xml");
                }
                else // Internet Explorer
                {
                  xmlExecuteCommandResponse = new ActiveXObject("Microsoft.XMLDOM");
                  xmlExecuteCommandResponse.async=false;
                  xmlExecuteCommandResponse.loadXML(data);
                }


                //get the xml response and extract the value
                var message = xmlExecuteCommandResponse.getElementsByTagName("message")[0].childNodes[0].nodeValue;

                $scope.commandWizardData.executeCommandResponseStatus.message = message;

                if (message == 'Success') {
                  console.log("success executing command in wizard");

                  $rootScope.operationSuccessMsg = angular.copy($rootScope.operationSuccessMsg) + ". Success executing command";
                  MenuService.createUpdateMenu();

                } else {
                  var executeCommandDescription = xmlExecuteCommandResponse.getElementsByTagName("description")[0].childNodes[0].nodeValue;
                  $scope.commandWizardData.executeCommandResponseStatus.description = executeCommandDescription;
                  console.log("fail executing command by wizard");
                  console.log(executeCommandDescription);

                  showErrorDialog('Error executing command: ' + executeCommandDescription);

                }


              })
              .error(function(data, status, headers, config) {
                console.log("error executong command in wizard");

                showErrorDialog('Error executing command');

                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });

        }


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
