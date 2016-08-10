/**
 * 
 */
appModule.service('envHealthDetailsService', function() {
	
	this.envHealthDetails = '';
	this.lastUpdated = '';
	
	this.getEnvHealthDetails = function() {
		return this.envHealthDetails;
	};
	
	this.setEnvHealthDetails = function(envHealthDetails) {
		this.envHealthDetails = envHealthDetails;
	};
	
	this.getLastUpdated = function() {
		return this.lastUpdated;
	}
	
	this.setLastUpdated = function(lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
});