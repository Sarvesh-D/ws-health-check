/**
 * JS for modules 
 */
var appModule = angular.module('appModule',['ngRoute','ngResource','ngTable']);

appModule.config(function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: "envHealthDetails.html",
		controller: "envHealthDetailsController",
	}).
	when('/component/details/:env/:component', {
		templateUrl: "componentHealthDetails.html",
		controller: "componentHealthDetailsController",
	});
});