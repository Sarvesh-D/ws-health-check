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