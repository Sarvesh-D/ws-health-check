/**
 * 
 */
var solveItWSHealthCheckService = angular.module('appModule');

solveItWSHealthCheckService.factory('solveItWSHealthCheckService' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/service/health');
});

solveItWSHealthCheckService.factory('solveItEnvHealthCheckService' , function($resource) {
	return $resource('http://localhost:8080/ws-health-check/env/health');
});