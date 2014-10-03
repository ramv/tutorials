// Declare app level module which depends on filters, and service'use strict';
//create the cseApp module. We will use this object to attach controllers
var cseapp = angular.module('cseApp', [
  'ngResource',
  'ngRoute'
]);


cseapp.config(['$routeProvider', '$locationProvider',  configureApp]);
function configureApp($routeProvider,$locationProvider) {
  $routeProvider
    .when('/', {
        templateUrl: 'views/home/main.html',
        controller: 'MainCtrl'
    })
    .when('/main/:sort', {
      templateUrl: 'views/home/main.html',
      controller: 'MainSortCtrl'
    })
    .otherwise({
        redirectTo: '/'
    });
}
