/*global angular*/
'use strict';

(function() {
	var app = angular.module('scalesView', ['scales', 'scaleDetails', 'list', 'panel', 'utilities', 'search']),
		DELAY = 400;

	function transformScaleToDisplayFormat(scale, id) {
		return {
			id: scale.id || id,
			name: scale.name,
			description: scale.description || '',
			dataType: scale.dataType.name
		};
	}

	function transformToDisplayFormat(scales) {
		return scales.map(transformScaleToDisplayFormat);
	}

	app.controller('ScalesController', ['$scope', 'scalesService', 'panelService', '$timeout', 'collectionUtilities', '$filter',
		function($scope, scalesService, panelService, $timeout, collectionUtilities, $filter) {
			var ctrl = this,
				orderCategoryByFilter = $filter('ifNumericOrderBy');

			ctrl.scales = [];
			ctrl.showThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'description', 'dataType'];

			$scope.filterByProperties = ctrl.colHeaders;
			$scope.panelName = 'scales';

			$timeout(function() {
				ctrl.showThrobber = true;
			}, DELAY);

			scalesService.getScales().then(function(scales) {
				ctrl.scales = transformToDisplayFormat(scales);
				if (ctrl.scales.length === 0) {
					ctrl.showNoItemsMessage = true;
				}
			}, function() {
				ctrl.problemGettingList = true;
			}).finally(function() {
				ctrl.showThrobberWrapper = false;
			});

			$scope.showScaleDetails = function() {
				// Ensure the previously selected scale doesn't show in the panel before we've retrieved the new one
				$scope.selectedScale = null;

				scalesService.getScale($scope.selectedItem.id).then(function(scale) {
					if (scale.validValues && scale.validValues.categories) {
						scale.validValues.categories = orderCategoryByFilter(scale.validValues.categories, 'name');
					}
					$scope.selectedScale = scale;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedScale = function(updatedScale) {

				var selectedIndex = -1,
					transformedScale = updatedScale && transformScaleToDisplayFormat(updatedScale, $scope.selectedItem.id);

				ctrl.scales.some(function(scale, index) {
					if (scale.id === $scope.selectedItem.id) {
						selectedIndex = index;
						return true;
					}
				});

				// Not much we can really do if we don't find it in the list. Just don't update.
				if (selectedIndex !== -1) {
					if (transformedScale) {
						ctrl.scales[selectedIndex] = transformedScale;
						collectionUtilities.sortByName(ctrl.scales);
						$scope.selectedScale = updatedScale;
					} else {
						ctrl.scales.splice(selectedIndex, 1);
					}
				}
			};

			// An object only containing the selected item's id. This format is required for passing to the list directive.
			$scope.selectedItem = {id: null};
			// Contains the entire selected scale object once it has been updated.
			$scope.selectedScale = null;
		}
	]);
}());
