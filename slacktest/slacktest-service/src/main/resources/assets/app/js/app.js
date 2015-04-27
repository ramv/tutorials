// Declare app level module which depends on filters, and services
angular.module('slacktest', ['ngResource', 'ngRoute', 'ui.bootstrap', 'ui.date', 'ab-base64', 'ngSanitize'])
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/home/home.html',
        controller: 'HomeController'})
      .otherwise({redirectTo: '/'});
  }]);
