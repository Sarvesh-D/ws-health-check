/**
 * JS for modules 
 */
var appModule = angular.module('appModule',['ngRoute','ngResource']);

appModule.config(function($routeProvider) {
	$routeProvider.
		when('/', {
			templateUrl: "serviceHealthDetails.html",
			controller: "serviceHealthDetailsController",
		})
});