'use strict';

/**
 * @ngdoc function
 * @name rifidiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the rifidiApp
 */
angular.module('rifidiApp')
  .controller('CommonController', function ($scope, commonVariableService) {

    $scope.operationSuccessMsg = commonVariableService.getSuccessMessage();

  });
