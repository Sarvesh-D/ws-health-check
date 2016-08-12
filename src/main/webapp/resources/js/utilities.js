/**
 * JS for common utility functions 
 */
function configRespServiceObj(service) {
	var serviceDetailView = {
			environment : service.environment,
			provider : service.provider, 
			description : service.description,
			uri : service.uri,
			status : service.status
	};
	return serviceDetailView;
}

function configEnvHealthDetailsView(envHealthDetails) {
	var envHealthDetailsView = [];
	
	angular.forEach(envHealthDetails,function(envHealthDetail) {
		var componentDetailsView = [];
		angular.forEach(envHealthDetail.components,function(component) {
			var componentDetailView = {
					name : component.name,
					version : component.version,
					env: component.environment,
					services : component.services,
					status : 'UP',
					downServices : 0
			}
			angular.forEach(componentDetailView.services,function(service) {
				if(service.status == 'DOWN')
					componentDetailView.downServices++;
			});
			if(componentDetailView.downServices > 0)
				componentDetailView.status = 'DOWN';
			
			componentDetailsView.push(componentDetailView);
		});
		
		var envHealthDetailView = {
				name : envHealthDetail.name,
				components: componentDetailsView
		};
		
		envHealthDetailsView.push(envHealthDetailView);
	
	});
	
	/*envHealthDetails.forEach(envHealthDetail) {
		var componentDetailsView = [];
		envHealthDetail.components.forEach(component) {
			var componentDetailView = {
					name : component.name,
					services : component.services,
					status : 'UP',
					downServices : 0
			}
			componentDetailView.services.forEach(service) {
				if(service.status == 'DOWN')
					componentDetailView.downServices++;
			}
			if(componentDetailView.downServices > 0)
				componentDetailView.status = 'DOWN';
			
			componentDetailsView.push(componentDetailView);
		}
		
		var envDetailView = {
				name : envHealthDetail.name,
				components: componentDetailsView
		};
	}*/
	
	return envHealthDetailsView;
}