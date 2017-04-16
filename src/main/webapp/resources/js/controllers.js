/**
 * JS for controllers 
 */

/**
 * Controller for Getting Environment Health Details. (Near Real Time. Max delay of 15 mins)
 */
appModule.controller('envHealthDetailsController',
				function($scope, $rootScope, $timeout, envDetailsFactory,
						envHealthDetailsService, utilityService, coreConstants) {
	
	$rootScope.version = coreConstants.VERSION;

	var page_refresh_interval = coreConstants.PAGE_REFRESH_INTERVAL;

	$scope.getEnvHealthDetails = function() {
		envDetailsFactory.get(function(data) {
			if(data.environments.length == 0)
				$scope.noEnvs = true;
			else {
				$scope.envHealthDetailsView = utilityService.configEnvHealthDetailsView(data.environments);
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

/**
 * Controller for Populating Component Service Details. Depends on envHealthDetailsController. (Near Real Time. Max delay of 15 mins)
 */
appModule.controller('componentHealthDetailsController', function($scope,$rootScope,
		$routeParams, envHealthDetailsService, componentDetailsService) {
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
					componentDetailsService.setComponentDetails(component);
				}
			});
		}
	});
});

/**
 * Controller for Getting Service Health Details. (Real Time)
 */
appModule.controller('serviceHealthDetailsController',
				function($scope, $routeParams, $timeout, serviceDetailsFactory,
						componentDetailsService, utilityService, coreConstants) {
	
	var page_refresh_interval = coreConstants.PAGE_REFRESH_INTERVAL;
	
	$scope.getServiceDeails = function() {
		component = componentDetailsService.getComponentDetails();
		$scope.env = component.env;
		$scope.component = component.name;
		$scope.desc = $routeParams.desc;
		$scope.uri = $routeParams.uri;
		
		var serviceTimes = [];
		
		serviceDetailsFactory.get({
			env : component.env,
			provider : component.name,
			uri : $routeParams.uri
		}, function(data) {
			angular.forEach(data.serviceTimes,function(serviceTime) {
				serviceTimes.push(serviceTime);
			});
		}).$promise.then(function() {

			utilityService.renderChart(utilityService.transformDataForChart(serviceTimes));
			// auto refresh table data
			$timeout(function() {
				$scope.getServiceDeails();
			}, page_refresh_interval);

			$scope.lastUpdated = new Date(); // set last updated/refreshed time
		});
	}
});