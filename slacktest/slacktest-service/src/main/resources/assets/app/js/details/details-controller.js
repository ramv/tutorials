angular.module('slacktest')
  .controller('DetailsCtrl',
              [ '$scope', '$http', '$compile', '$sanitize','base64',
                '$location', '$anchorScroll', '$timeout',
                function ($scope, $http, $compile, $sanitize, base64, $location, $anchorScroll, $timeout) {

    /**
     * The function that handles the submission
     */
    $scope.submit = function(){
      var url = 'http://localhost:8080/api/v1/html_details?url='+encodeURIComponent($scope.url);
      console.log("fetching data for "+url);
      $http({method: 'GET', url: url})
        .success(function (response){
          //console.log(response);
          $scope.html_details = response;
          $scope.tags = response.tags;
          $scope.html =base64.decode(response.html);
          //console.log($scope.html);
          $scope.submitSuccess = true;
          $scope.submitSuccessMsg = "Successfully fetched details of URL "+url;
        }).error(function (data, status, headers, config){
          $scope.submitFailure = true;
          $scope.submitFailureMsg = "Status: "+status+" data: "+data;
        });

      var element = angular.element('#homeContent');

      $http.get('views/home/details.html')
        .success(function(response){
          //console.log(response);\
          element.replaceWith($compile(angular.element(response))($scope));
          Prism.highlightAll();
        })
        .error(function(response){
          console.log('Error: '+response);
        });
    };

    /**
     * Function that calls the highlighter to syntax highlight the
     * HTML source after it loaded.
     */
    $scope.highlightTags = function(tagElem){
      Prism.highlightAll(false, function(){
          var sourceDiv = document.getElementById("source_div");
          sourceDiv.style.visibility ="visible";
          var elems = document.getElementsByClassName("tag");
          var tagName = tagElem.tag.name;
          var scrollSet = false;
          for(var i=0; i < elems.length; i++){
            var elem = elems[i];
            var name = elem.textContent;
            var re = new RegExp("\\W"+tagName);
            if(re.exec(name)){
              elem.style.backgroundColor = "#FFD700";
              if(scrollSet == false){
                elem.id="scroll_position";
              }
              scrollSet= true;
            }
          }
          if(scrollSet == true){

             // set the location.hash to the id of
             // the element you wish to scroll to.
             $location.hash('scroll_position');

             //scroll to the position refrerred to by scroll_position
             $anchorScroll();
          }
      });

    };
  }]);