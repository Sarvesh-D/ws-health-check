/**
 * JS for modules 
 */
var appModule = angular.module('appModule',['ngRoute','ngResource','ngTable','ngAnimate']);

appModule.config(function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: "envHealthDetails.html",
		controller: "envHealthDetailsController",
	});
}).run(function($animate) {
	//$animate.enabled(true);
}).animation('.danger', function() {
	return {
		enter : function(element, done) {
			jQuery('.danger').fadeOut('slow', function(){
				jQuery(this).fadeIn('slow', function(){
			        //blink(this);
			    });
			});
		}
	}
});