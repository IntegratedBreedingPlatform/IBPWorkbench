/*global angular*/
'use strict';

(function() {
	var filterModule = angular.module('filter', ['panel', 'variableTypes', 'dataTypes', 'utilities', 'multiSelect',
		'ui.bootstrap']);

	filterModule.directive('omFilter', ['panelService', 'variableTypesService', 'serviceUtilities', 'dataTypesService',
		function(panelService, variableTypesService, serviceUtilities, dataTypesService)  {
			return {
				controller: function($scope) {
					$scope.smallPanelName = 'filters';
					$scope.data = {
						types: [],
						scaleDataTypes: [],
						calendarOpened1: false,
						calendarOpened2: false
					};

					$scope.addNewFilter = function() {
						panelService.showPanel($scope.smallPanelName);
					};

					$scope.isFilterActive = function() {
						var filterOptionsValued = $scope.filterOptions,
							variableTypesActive,
							scaleDataTypesActive,
							dateCreatedFromActive,
							dateCreatedToActive;

						if (!filterOptionsValued) {
							return false;
						}

						variableTypesActive = $scope.filterOptions.variableTypes && $scope.filterOptions.variableTypes.length !== 0;

						scaleDataTypesActive = !!$scope.filterOptions.scaleDataType;

						dateCreatedFromActive = $scope.filterOptions.dateCreatedFrom &&
							$scope.filterOptions.dateCreatedFrom.getTime !== undefined;

						dateCreatedToActive = $scope.filterOptions.dateCreatedTo &&
							$scope.filterOptions.dateCreatedTo.getTime !== undefined;

						return variableTypesActive || scaleDataTypesActive || dateCreatedFromActive || dateCreatedToActive;
					};

					variableTypesService.getTypes().then(function(types) {
						$scope.data.types = types;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					dataTypesService.getNonSystemDataTypes().then(function(types) {
						$scope.data.scaleDataTypes = $scope.data.scaleDataTypes.concat(types);
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					$scope.dateOptions = {
						formatYear: 'yy',
						startingDay: 1
					};

					$scope.today = function() {
						$scope.filterOptions.dateCreatedFrom = new Date();
						$scope.filterOptions.dateCreatedTo = new Date();
					};

					$scope.open1 = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.calendarOpened1 = true;
					};

					$scope.open2 = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.calendarOpened2 = true;
					};

					$scope.clear = function() {
						$scope.filterOptions.dateCreatedFrom = null;
						$scope.filterOptions.dateCreatedTo = null;
					};

					$scope.todaysDate = new Date();

				},
				restrict: 'E',
				scope: {
					filterOptions: '=omFilterOptions'
				},
				templateUrl: 'static/views/ontology/filter.html'
			};
		}
	]);

}());
