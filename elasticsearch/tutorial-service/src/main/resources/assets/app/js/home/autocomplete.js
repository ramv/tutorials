'use strict';
//retrieve a module object named cseApp
var cseapp = angular.module('cseApp');

var template = '<div>'+
                    '<div class="searchHeadings">Products</div>'+
                   '<ul>'+
                        '<li ng-repeat="product in products" style="border-bottom:none;padding-top:4px; padding-bottom:4px;">'+
                            '<div class="searchImageContainer left" style="margin-right: 10px; width: 40px; text-align: center;">'+
                                '<img style="max-height: 40px;max-width: 40px;" src="{{product.image_url}}">'+
                            '</div>'+
                            '<a href="/#/products/{{product._id}}">{{product.description}}</a>'+
                            '<div class="clearfix"></div>'+
                        '</li>'+
                    '</ul>'+
                '</div>'+
                '<div>'+
                   '<div class="searchHeadings">Collections</div>'+
                   '<ul>'+
                        '<li ng-repeat="collection in collections" style="border-bottom:none;padding-top:4px; padding-bottom:4px;">'+
                            '<div class="searchImageContainer left" style="margin-right: 10px; width: 40px; text-align: center;">'+
                                '<img style="max-height: 40px;max-width: 40px;" src="{{collection.latest_pics[0]}}">'+
                            '</div>'+
                            '<a href="/#/collection/{{collection._id}}">{{collection.name}}</a>'+
                            '<div class="clearfix"></div>'+
                        '</li>'+
                    '</ul>'+
                '</div>'+
                '<div>'+
                   '<div class="searchHeadings">Users</div>'+
                   '<ul>'+
                        '<li ng-repeat="user in users" style="border-bottom:none;padding-top:4px; padding-bottom:4px;">'+
                            '<div class="searchImageContainer left" style="margin-right: 10px; width: 40px; text-align: center;">'+
                                '<img style="max-height: 40px;max-width: 40px;" src="{{user.pics[0]}}">'+
                            '</div>'+
                            '<a href="/#/users/{{user._id}}">{{user.firstname}} {{user.lastname}}</a>'+
                            '<div class="clearfix"></div>'+
                        '</li>'+
                    '</ul>'+
                '</div>'+
                '<div>'+
                   '<div class="searchHeadings">Coupons</div>'+
                   '<ul>'+
                        '<li ng-repeat="coupon in coupons" style="border-bottom:none;padding-top:4px; padding-bottom:4px;">'+
                            '<div class="searchImageContainer left" style="margin-right: 10px; width: 40px; text-align: center;">'+
                                '<img style="max-height: 40px;max-width: 40px;" src="{{coupon.MERCHANT_URL}}">'+
                            '</div>'+
                            '<a href="/#/coupons/{{coupon._id}}">{{coupon.TITLE}}</a>'+
                            '<div class="clearfix"></div>'+
                        '</li>'+
                    '</ul>'+
                '</div>';

cseapp.controller('AutocompleteCtrl', ['$scope', '$http', '$rootScope', '$compile', function($scope, $http, $rootScope, $compile){
  $scope.categories = [];
  $scope.searchSubmenuView ="hidden";
  $scope.typeahead = null;

  $scope.showResults= function(set){
    if(set === 'users'){
        angular.element('.userSearchResults').class('row');
        angular.element('.userSearchResults').class('row');
        angular.element('.userSearchResults').class('row');
        angular.element('.userSearchResults').class('row');
    }

  }
  $scope.submit = function(){
    var url = 'http://localhost:8080/api/v1/multi-search?q='+$scope.keywords;
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
        $scope.searchMenu = {
            products: "current",
            users: "",
            collections: "",
            coupons: ""
        };
    }).then(function (response) {
      if(!$rootScope.hasOwnProperty('appData')){
          $rootScope.appData = {};
      }
      //angular.element('#autoSearchResultsContainer')
      $scope.searchSubmenuView ="hidden";
      var element = angular.element('#mainContainer');

      $http.get('/views/home/search.html').success(function(response){
        console.log(response);
        element.replaceWith($compile(angular.element(response))($scope));
      });

    });
  }
}]);

//setup a new directive for fetching the results in typeahead
cseapp.directive('autoComplete', [  '$http', '$compile', '$rootScope',
                                    function( $http, $compile, $rootScope) {
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
        if(!$rootScope.hasOwnProperty('appData')){
            $rootScope.appData = {};
        }
        $rootScope.appData['keywords'] = value;
        scope.keywords = value;
        //  dataService.set('keywords', value);
        console.log(JSON.stringify($rootScope.appData));
        element.replaceWith($compile(angular.element(template))(scope));
      });
    });
    console.log('in link');
  }
  
  
  return {
    link: link,
    template: template
  };
}]);