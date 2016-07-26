/**
 * JS for controllers 
 */

appModule.controller('serviceHealthDetailsController' , function($scope,$timeout,solveItWSHealthCheckService,NgTableParams) {

	var page_refresh_interval = 30000 // 30 seconds

	$scope.getServiceHealthDetails = function() {
		solveItWSHealthCheckService.query(function(servicesHealthDetails) {
			if(servicesHealthDetails.length == 0)
				$scope.noServices = true;
			else {
				$scope.servicesHealthDetailsView = [];
				angular.forEach(servicesHealthDetails,function(servicesHealthDetail) {
					var servicesHealthDetailView = configRespServiceObj(servicesHealthDetail);
					$scope.servicesHealthDetailsView.push(servicesHealthDetailView);
				});
			}
		}).$promise.then(function() {
			// set service health detail table data
			$scope.tableParams = configTableOptions();

			// auto refresh table data
			$timeout(function() {
				$scope.getServiceHealthDetails();
			}, page_refresh_interval);

			$scope.last_updated = new Date(); // set last updated/refreshed time
		});
	};

	function configTableOptions() {
		var initialParams = {
				count: 2 // initial page size
		};
		var initialSettings = {
				// page size buttons
				counts: [5,10],
				// determines the pager buttons
				paginationMaxBlocks: 10,
				paginationMinBlocks: 2,
				dataset: $scope.servicesHealthDetailsView
		};
		return new NgTableParams(initialParams, initialSettings);
	}

});