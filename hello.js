angular.module('demo', ['ngSanitize'])
.controller('Hello', function($scope, $http) {
  $scope.selList = [];
  $scope.sellList = ["a"];
  $scope.postResultMessage= ["acadia"];
  $scope.setRelSearch = false;
  $scope.pageInfo = [];
  $scope.flag = false;
  $scope.name="";
  $scope.firstPage="";

  $scope.settt = function(){
    console.log($scope.name);
      var str = $scope.name;
      //$scope.pageInfo= [];
      if($scope.setRelSearch == true){
        console.log("entered");
        if($scope.pageInfo.length>0){
          console.log("entered2");
          for(var i=0; i<$scope.pageInfo.length;i++){
            console.log("entered3");
            if($scope.pageInfo[i].relevant == true){
              console.log("----"+$scope.pageInfo[i].relevant);
              $scope.selList.push($scope.pageInfo[i].link);
            }
          }
        }
      }
      $scope.pageInfo= [];
      console.log($scope.selList);
  		$http.get('http://localhost:8084/query/qterm',{params:{name:$scope.name, doclist: $scope.selList, prox: $scope.setRelSearch }}).then(function (response) {
  			$scope.postResultMessage = response.data;


        $scope.flag = true;
        $scope.firstPage = "file:///Users/tejaswinijagarlamudi/Documents/IR3/"+$scope.postResultMessage[0].link;
        for(var i=0; i<$scope.postResultMessage.length;i++){
          $scope.pageInfo.push({relevant:false, notrelevant:false, link:$scope.postResultMessage[i].link , description: "<b>"+$scope.postResultMessage[i].content+"</b>", title:$scope.postResultMessage[i].title });
        }

        //console.log($scope.postResultMessage[0]);
  		}, function error(response) {
  			$scope.postResultMessage = "Error with status: " +  response.statusText;
  		});
    }

    $scope.relevantSearch = function(){
      $scope.setRelSearch = true;
      console.log("----"+$scope.pageInfo[0].relevant);
      $scope.settt();

    }


});
