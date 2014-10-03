'use strict';
//retrieve a module object named cseApp
var cseapp = angular.module('cseApp');

cseapp.controller('MainCtrl', ['$scope', '$http', '$location', function($scope, $http, $location){
  $scope.categories = [];
  var url = 'http://localhost:8080/api/v1/search';
  var sortingMenu = {
    "oldest_first" : "",
    "newest_first" : "",
    "highest_rated" : "current",
    "lowest_rated" : ""
  };
  console.log(url);
  $http({method: 'GET', url: url})
    .success(function (response){
      var res = response;
      console.log(res);
      $scope.products = res;
      $scope.sortingMenu = sortingMenu;
      $scope.urlPrefix = $location.path();
    });
}]);