/**
 * 
 */

appModule.factory('solveItWSHealthCheckFactory' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/service/health');
});

appModule.factory('solveItWSHealthCheckFactory' , function($resource) {
	return $resource('http://10.36.131.183:8080/ws-health-check/env/health');
});