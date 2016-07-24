/**
 * 
 */
var solveItWSHealthCheckService = angular.module('appModule');

solveItWSHealthCheckService.factory('solveItWSHealthCheckService' , function($resource) {
	return $resource('http://localhost:8081/ws-health-check/service/health');
});