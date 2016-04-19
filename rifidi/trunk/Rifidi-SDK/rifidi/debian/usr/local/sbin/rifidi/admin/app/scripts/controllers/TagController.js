'use strict';

/**
 * @ngdoc function
 * @name rifidiApp.controller:TagCtrl
 * @description
 * # TagCtrl
 * Controller of the rifidiApp
 */
angular.module('rifidiApp')
  .controller('TagCtrl', function ($scope, ngDialog, TagService) {

    $scope.tags = [];
    
    $scope.getCurrentTags = function(){
    
    	var host = angular.copy($scope.elementSelected.host);
    	var readerId = angular.copy($scope.elementSelected.elementId);
    	
    	TagService.callCurrentTagsService(host, readerId)
            .success(function (data, status, headers, config) {

              $scope.tags = TagService.getTagsFromReceivedData(data);
              console.log("tags:");
              console.log($scope.tags);

            })
            . error(function (data, status, headers, config) {

              console.log("error getting current tags");

              showErrorDialog('Error getting current tags');

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
