/**
 * JS for controllers 
 */

appModule.controller('serviceHealthDetailsController' , function($scope,$timeout,solveItWSHealthCheckService) {

	var page_refresh_interval = 30000 // 30 seconds

	/**
	 * ANGULAR RESTFUL CALLS
	 * 
	 */
	$scope.getServiceHealthDetails = function() {
		var servicesHealthDetails = solveItWSHealthCheckService.query(function() {
			$scope.servicesHealthDetailsView = [];
			$scope.noServices = servicesHealthDetails.length == 0;
			angular.forEach(servicesHealthDetails,function(servicesHealthDetail) {
				var servicesHealthDetailView = configRespServiceObj(servicesHealthDetail);
				$scope.servicesHealthDetailsView.push(servicesHealthDetailView);
			});

		});
		
		// auto refresh
		$timeout(function() {
			$scope.getServiceHealthDetails();
		}, page_refresh_interval);
		
		$scope.last_updated = new Date();
	};


});