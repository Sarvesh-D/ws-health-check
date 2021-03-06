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

appModule.service('componentDetailsService', function() {

	this.component = '';

	this.getComponentDetails = function() {
		return this.component;
	};

	this.setComponentDetails = function(component) {
		this.component = component;
	}
});

appModule.service('utilityService', function(coreConstants,$filter) {
	
	var self = this;

	this.configRespServiceObj = function(service) {
		var serviceDetailView = {
				environment : service.provider.environment.name,
				provider : service.provider.name, 
				description : service.description,
				uri : service.uri,
				overallStatus : ''
		};
		switch(service.overallStatus) {
		case coreConstants.STATUS.RED: 
			serviceDetailView.overallStatus = coreConstants.COLOR.RED;
			break;
		case coreConstants.STATUS.AMBER:	
			serviceDetailView.overallStatus = coreConstants.COLOR.ORANGE; 
			break;
		case coreConstants.STATUS.GREEN:	
			serviceDetailView.overallStatus = coreConstants.COLOR.GREEN; 
			break;
		}
		return serviceDetailView;
	}

	this.configEnvHealthDetailsView = function(envHealthDetails) {
		var envHealthDetailsView = [];

		angular.forEach(envHealthDetails,function(envHealthDetail) {
			var componentDetailsView = [];
			angular.forEach(envHealthDetail.components,function(component) {
				var componentDetailView = {
						name : component.name,
						env: component.environment.name,
						services : [],
						overallStatus : '',
						downServices : component.downServices.length
				}
				angular.forEach(component.services,function(service) {
					componentDetailView.services.push(self.configRespServiceObj(service));
				});
				switch (component.overallStatus) {
				case coreConstants.STATUS.RED: 
					componentDetailView.overallStatus = coreConstants.COLOR.RED;
					break;
				case coreConstants.STATUS.AMBER:	
					componentDetailView.overallStatus = coreConstants.COLOR.ORANGE; 
					break;
				case coreConstants.STATUS.GREEN:	
					componentDetailView.overallStatus = coreConstants.COLOR.GREEN; 
					break;
				}
				componentDetailsView.push(componentDetailView);
			});

			var envHealthDetailView = {
					name : envHealthDetail.name,
					components: componentDetailsView
			};

			envHealthDetailsView.push(envHealthDetailView);

		});

		return envHealthDetailsView;
	}

	this.transformDataForChart = function(data) {
		var transformedData = [];
		angular.forEach(data, function(entry) {
			var transformedEntry = {
					label : $filter('date')(entry.time, 'HH:mm'),
					value : entry.status == coreConstants.SERVICE_STATUS.UP ? coreConstants.CHART_PROPERTY.Y_AXIS_MAX_VALUE : coreConstants.CHART_PROPERTY.Y_AXIS_MIN_VALUE, 
							color : entry.status == coreConstants.SERVICE_STATUS.UP ? coreConstants.COLOR.HEX.GREEN : coreConstants.COLOR.HEX.RED
			}
			transformedData.push(transformedEntry);
		});
		if(transformedData.length <= coreConstants.CHART_PROPERTY.X_AXIS_MAX_VALUE/4)
			return this.applyChartPadding(transformedData, transformedData.length, coreConstants.CHART_PROPERTY.X_AXIS_MAX_VALUE);
		return transformedData;
	}

	this.applyChartPadding = function(chartData, minOffset, maxOffset) {
		for (var i = minOffset; i <= maxOffset; i++) {
			var transformedEntry = {
					label : '',
					value : '', 
					color : ''
			}
			chartData.push(transformedEntry);
		}
		return chartData;

	}

	this.renderChart = function renderChart(data) {
		FusionCharts.ready(function(){
			var revenueChart = new FusionCharts({
				"type": coreConstants.CHART_PROPERTY.TYPE,
				"renderAt": "chartContainer",
				"width": coreConstants.CHART_PROPERTY.WIDTH,
				"height": coreConstants.CHART_PROPERTY.HEIGHT,
				"dataFormat": "json",
				"dataSource": {
					"chart": {
						"caption": coreConstants.CHART_PROPERTY.CAPTION,
						"subcaption": coreConstants.CHART_PROPERTY.SUB_CAPTION,
						"xAxisName": coreConstants.CHART_PROPERTY.X_AXIS_NAME,
						"yAxisName": coreConstants.CHART_PROPERTY.Y_AXIS_NAME,
						"adjustDiv":coreConstants.CHART_PROPERTY.ADJUST_DIV,
						"yAxisMinValue": coreConstants.CHART_PROPERTY.Y_AXIS_MIN_VALUE,
						"yAxisMaxValue": coreConstants.CHART_PROPERTY.Y_AXIS_MAX_VALUE,
						"numDivLines":coreConstants.CHART_PROPERTY.NUM_DIV_LINES,
						//"canvasBgColor": coreConstants.CHART_PROPERTY.CANVAS_BG_COLOR,
						"canvasBgAlpha": coreConstants.CHART_PROPERTY.CANVAS_BG_ALPHA,
						"showCanvasBorder": coreConstants.CHART_PROPERTY.SHOW_CANVAS_BORDER,
						"theme": coreConstants.CHART_PROPERTY.THEME,
						"usePlotGradientColor": coreConstants.CHART_PROPERTY.USE_PLOT_GRADIENT_COLOR,
						"showPlotBorder": coreConstants.CHART_PROPERTY.SHOW_PLOT_BORDER,
						"showValues": coreConstants.CHART_PROPERTY.SHOW_VALUES,
						"exportEnabled": "1"
					},
					"data": data
				}
			});

			revenueChart.render();
		});
	}
});