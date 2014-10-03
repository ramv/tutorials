'use strict';
//retrieve a module object named cseApp
var cseapp = angular.module('cseApp');

cseapp.controller('AutocompleteCtrl', ['$scope', '$http',  '$compile', 
  function($scope, $http, $compile){
    $scope.categories = [];
    $scope.searchSubmenuView ="hidden";
    $scope.typeahead = null;
    
    // the submit method to handle the case when the user enters
    // immediately after entering the text
    $scope.submit = function(){
      $scope.searchSubmenuView ="hidden";
      var element = angular.element('#mainContainer');
      // fetch the template from the server
      $http.get('/views/home/search.html').success(function(response){
        console.log(response);
        element.replaceWith($compile(angular.element(response))($scope));
      });
    }
  }
]);

//setup a new directive for fetching the results in typeahead
cseapp.directive('autoComplete', [  '$http', '$compile',
                                    function( $http, $compile) {
  function link(scope, element, attrs) {
    scope.$watch(attrs.autoComplete, function(value) {
      if(value === null || value === "" ){
        scope.searchSubmenuView = "hidden";
        return;
      }

      var url = 'http://localhost:8080/api/v1/multi-search?q='+value;
      $http({method: 'GET', url: url})
          .success(function (response){
        var res = response;
        //console.log(res);
        scope.multiResults = res;
        scope.products = res.products;
        scope.coupons = res.coupons;
        scope.collections = res.collections;
        scope.users = res.users;
        scope.searchSubmenuView = "";
        scope.keywords=value;
      }).then(function (response) {
        scope.keywords = value;
        // get the menu bar template from the server.
        $http.get('/views/home/search_menu.html').success(function(response){
          console.log(response);
          element.replaceWith($compile(angular.element(response))(scope));
        });
      });
    });
  }
  return {
    link: link
  };
}]);