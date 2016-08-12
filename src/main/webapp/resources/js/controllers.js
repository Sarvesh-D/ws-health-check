/**
 * JS for controllers 
 */

appModule.controller('serviceHealthDetailsController' , function($scope,$timeout,solveItWSHealthCheckFactory,NgTableParams) {

	var page_refresh_interval = 900000 // 15 minutes

	$scope.getServiceHealthDetails = function() {
		solveItWSHealthCheckFactory.query(function(servicesHealthDetails) {
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

			$scope.lastUpdated = new Date(); // set last updated/refreshed time
		});
	};

	function configTableOptions() {
		var initialParams = {
				count: 5 // initial page size
		};
		var initialSettings = {
				// page size buttons
				counts: [5,10,15,20],
				// determines the pager buttons
				paginationMaxBlocks: 10,
				paginationMinBlocks: 2,
				dataset: $scope.servicesHealthDetailsView
		};
		return new NgTableParams(initialParams, initialSettings);
	}

});


appModule.controller('envHealthDetailsController' , function($scope,$timeout,solveItWSHealthCheckFactory,envHealthDetailsService) {

	var page_refresh_interval = 900000 // 15 minutes

	$scope.getEnvHealthDetails = function() {
		solveItWSHealthCheckFactory.query(function(envHealthDetails) {
			if(envHealthDetails.length == 0)
				$scope.noEnvs = true;
			else {
				$scope.envHealthDetailsView = configEnvHealthDetailsView(envHealthDetails);
				envHealthDetailsService.setEnvHealthDetails($scope.envHealthDetailsView);
			}
		}).$promise.then(function() {
			// auto refresh table data
			$timeout(function() {
				$scope.getEnvHealthDetails();
			}, page_refresh_interval);

			$scope.lastUpdated = new Date(); // set last updated/refreshed time
			envHealthDetailsService.setLastUpdated($scope.lastUpdated);
		});
	};
});

appModule.controller('componentHealthDetailsController' , function($scope,$routeParams,envHealthDetailsService) {
	$scope.componentServiceDetails = [];
	$scope.lastUpdated = envHealthDetailsService.getLastUpdated();
	$scope.env = $routeParams.env;
	$scope.component = $routeParams.component;
	$scope.version = $routeParams.version;
	
	var envHealthDetails = envHealthDetailsService.getEnvHealthDetails();
	angular.forEach(envHealthDetails,function(envHealthDetail) {
		if(envHealthDetail.name == $routeParams.env) {
			angular.forEach(envHealthDetail.components,function(component) {
				if(component.name == $routeParams.component) {
					angular.forEach(component.services,function(service) {
						$scope.componentServiceDetails.push(service);
					});
				}
			});
		}
	});
});