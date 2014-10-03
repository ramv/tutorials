//retrieve a module object named cseApp
var cseapp = angular.module('cseApp');
cseapp.controller('SearchResultsCtrl', ['$scope', '$http', '$rootScope', function($scope, $http, $rootScope){
    $scope.searchSubmenuView ="hidden";
    var keywords = $rootScope.appData["keywords"];
    console.log('retrieved keywords:'+JSON.stringify(keywords));
    if(keywords !== ''){    
      var url = 'http://localhost:8080/api/v1/multi-search?q='+keywords;
      $http({method: 'GET', url: url})
        .success(function (response){
          var res = response;
          //console.log(res);
          $scope.multiResults = res;
          $scope.products = res.products;
          $scope.coupons = res.coupons;
          $scope.collections = res.collections;
          $scope.users = res.users;
          $scope.searchSubmenuView = "";
      });
    }
}]);