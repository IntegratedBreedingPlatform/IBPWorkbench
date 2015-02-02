/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = '../static/views/components',
		app = angular.module('ontology', ['ngRoute', 'variables', 'properties']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/properties', {
				controller: 'PropertiesController',
				controllerAs: 'propsCtrl',
				templateUrl: VIEWS_LOCATION + '/properties/propertiesView.html'
			})
			.when('/variables', {
				controller: 'VariablesController',
				controllerAs: 'varsCtrl',
				templateUrl: VIEWS_LOCATION + '/variables/variablesView.html'
			});
	}]);
}());
