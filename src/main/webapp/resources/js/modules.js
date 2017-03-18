/**
 * JS for modules 
 */
var appModule = angular.module('appModule',['ngRoute','ngResource','ngTable','ng-fusioncharts']);

appModule.config(function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: "envHealthDetails.html",
		controller: "envHealthDetailsController",
	}).
	when('/component/details/:env/:component', {
		templateUrl: "componentHealthDetails.html",
		controller: "componentHealthDetailsController",
	}).
	when('/service/details/:desc/:uri*', {
		templateUrl: "serviceHealthDetails.html",
		controller: "serviceHealthDetailsController",
	});
});