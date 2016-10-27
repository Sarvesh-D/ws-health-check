/**
 * 
 */
appModule.constant('coreConstants', {
	'PAGE_REFRESH_INTERVAL':900000, // 15 minutes
	'COLOR':{
		'RED':'RED',
		'ORANGE':'ORANGE',
		'GREEN':'GREEN',
		'HEX': {
			'RED':'#FF0000',
			'GREEN':'#00FF00'
		}
	},
	'SERVICE_STATUS':{
		'UP':'UP',
		'DOWN':'DOWN'
	},
	'COMPONENT_STATUS':{
		'RED':'RED',
		'AMBER':'AMBER',
		'GREEN':'GREEN'
	},
	'CHART_PROPERTY':{
		'TYPE': 'column2d',
		'WIDTH': '100%',
		'HEIGHT': '100%',
		'CAPTION': 'Service Details',
		'SUB_CAPTION': new Date().toLocaleDateString(),
		'X_AXIS_NAME': 'Time',
		'Y_AXIS_NAME': 'Status',
		'ADJUST_DIV':0,
		'Y_AXIS_MIN_VALUE': '-1',
		'Y_AXIS_MAX_VALUE': '1',
		'X_AXIS_MAX_VALUE': 96,
		'NUM_DIV_LINES':0,
		'CANVAS_BG_COLOR': '#000000',
		'CANVAS_BG_ALPHA': 0,
		'SHOW_CANVAS_BORDER': '0',
		'THEME': 'carbon',
		'USE_PLOT_GRADIENT_COLOR': '0',
		'SHOW_PLOT_BORDER': '0',
		'SHOW_VALUES': '0'
	},
	'CONTEXT':'ws-health-check',
	'HOST_SERVER':'localhost',
	'HOST_PORT':'8080',
	'VERSION':'Beta'
});
