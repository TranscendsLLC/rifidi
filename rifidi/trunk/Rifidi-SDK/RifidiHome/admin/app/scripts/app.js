'use strict';

/**
 * @ngdoc overview
 * @name rifidiApp
 * @description
 * # rifidiApp
 *
 * Main module of the application.
 */
var app = angular
  .module('rifidiApp', [
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'angularTreeview',
    'ui.bootstrap',
    'mgo-angular-wizard',
    'directive.contextMenu',
    //'ngContextMenu',
    'ngDialog'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/sensorWizard/:restProtocol/:ipAddress/:restPort', {
        templateUrl: 'views/sensorWizard.html',
        controller: 'SensorWizardCtrl'
      })
      .when('/jobSubmissionWizard', {
        templateUrl: 'views/jobSubmissionWizard.html',
        controller: 'JobSubmissionWizardCtrl'
      })
      .when('/serverWizard', {
        templateUrl: 'views/serverWizard.html',
        controller: 'ServerWizardCtrl'
      })
      .when('/createCommandWizard', {
        templateUrl: 'views/createCommandWizard.html',
        controller: 'CreateCommandWizardCtrl'
      })
      .when('/createReadzoneWizard', {
        templateUrl: 'views/createReadzoneWizard.html',
        controller: 'CreateReadzoneWizardCtrl'
      })
    .otherwise({
        redirectTo: '/'
      });
  })

    .service('commonVariableService', function () {

      var successMsg = null;

      return {
        getSuccessMessage:function () {
          return successMsg;
        },
        setSuccessMessage:function (msg) {
          successMsg = msg;
        },
        deleteNote:function (id) {
        }
      };
    })

    .service('TreeViewService', function () {

      var successMsg = null;

      return {
        repaintTreeView:function () {
          //return successMsg;

        }


        /*
        ,

        setSuccessMessage:function (msg) {
          successMsg = msg;
        },
        deleteNote:function (id) {
        }
        */
      };
    })

;
