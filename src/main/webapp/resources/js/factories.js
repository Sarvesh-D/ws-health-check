/**
 * 
 */

appModule.factory('solveItWSHealthCheckFactory' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/service/health');
});

appModule.factory('solveItWSHealthCheckFactory' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/env/health');
});