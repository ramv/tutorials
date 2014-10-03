'use strict';
//retrieve a module object named cseApp
var cseapp = angular.module('cseApp');

cseapp.controller('MainSortCtrl', ['$scope', '$http', '$location','$routeParams', function($scope, $http, $location, $routeParams){
  $scope.categories = [];
  var url = 'http://localhost:8080/api/v1/search?sort='+$routeParams.sort;
  var sortingMenu = {
    "oldest_first" : "",
    "newest_first" : "",
    "highest_rated" : "",
    "lowest_rated" : ""
  };
  sortingMenu[$routeParams.sort] ="current";
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
