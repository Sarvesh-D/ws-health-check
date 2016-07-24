/**
 * JS for controllers 
 */

appModule.controller('serviceHealthDetailsController' , function($scope,solveItWSHealthCheckService) {
	
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
	};
	
		
});