/**
 * JS for modules 
 */
var appModule = angular.module('appModule',['ngRoute','ngResource','ngTable']);

appModule.config(function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: "envHealthDetails.html",
		controller: "envHealthDetailsController",
	});
});