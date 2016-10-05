/**
 * 
 */

appModule.factory('envDetailsFactory' , function($resource, coreConstants) {
	return $resource('http://'+coreConstants.HOST_SERVER+':'+coreConstants.HOST_PORT+'/'+coreConstants.CONTEXT+'/env/health');
});

appModule.factory('serviceDetailsFactory' , function($resource, coreConstants) {
	return $resource('http://'+coreConstants.HOST_SERVER+':'+coreConstants.HOST_PORT+'/'+coreConstants.CONTEXT+'/service/health');
});