/**
 * 
 */

appModule.factory('wSHealthCheckFactory' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/service/health');
});

appModule.factory('wSHealthCheckFactory' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/env/health');
});