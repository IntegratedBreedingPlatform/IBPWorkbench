/*global angular*/
'use strict';

(function() {
	var scaleDetailsModule = angular.module('scaleDetails', ['formFields', 'input', 'textArea', 'select', 'scales', 'dataTypes',
			'utilities', 'categories', 'panel']),
		DELAY = 400,
		NUM_EDITABLE_FIELDS = 3;

	scaleDetailsModule.directive('omScaleDetails', ['scalesService', 'serviceUtilities', 'formUtilities', 'panelService',
		'dataTypesService', '$timeout',
		function(scalesService, serviceUtilities, formUtilities, panelService, dataTypesService, $timeout) {

			// Reset any errors we're showing the user
			function resetErrors($scope) {
		 		$scope.clientErrors = {};
		 		$scope.serverErrors = {};
		 	}

			return {
				controller: function($scope) {
					$scope.editing = false;
					$scope.showRangeWidget = false;
					$scope.showCategoriesWidget = false;

					$scope.$watch('selectedScale', function(scale) {
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);

						// If a confirmation handler was in effect, get rid of it
						if ($scope.deny) {
							$scope.deny();
						}
						$scope.model = angular.copy(scale);
						$scope.deletable = scale && scale.deletable || false;
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.editableFields.length < NUM_EDITABLE_FIELDS;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.scaleId = selected && selected.id || null;
					}, true);

					$scope.$watch('model.dataType', function(newType) {
						if (newType) {
							$scope.showRangeWidget = newType.name === 'Numeric';
							$scope.showCategoriesWidget = newType.name === 'Categorical';
						}
					});

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

					$scope.editScale = function(e) {
						e.preventDefault();
						resetErrors($scope);

						dataTypesService.getDataTypes().then(function(types) {
							$scope.types = types;
						}, function(response) {
							$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						});

						$scope.editing = true;
					};

					$scope.deleteScale = function(e, id) {
						e.preventDefault();
						resetErrors($scope);

						formUtilities.confirmationHandler($scope, 'confirmDelete').then(function() {
							scalesService.deleteScale(id).then(function() {
								// Remove scale on parent scope if we succeeded
								panelService.hidePanel();
								$scope.updateSelectedScale();
							}, function() {
								$scope.clientErrors.failedToDelete = true;
							});
						});
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						resetErrors($scope);

						// The user hasn't changed anything
						if (angular.equals($scope.model, $scope.selectedScale)) {
							$scope.editing = false;
						} else {
							formUtilities.confirmationHandler($scope, 'confirmCancel').then(function() {
								$scope.editing = false;
								$scope.model = angular.copy($scope.selectedScale);
							});
						}
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();
						resetErrors($scope);

						if ($scope.sdForm.$valid) {
							$scope.submitted = true;
							$timeout(function() {
								if ($scope.submitted) {
									$scope.showThrobber = true;
								}
							}, DELAY);

							scalesService.updateScale(id, model).then(function() {

								// Update scale on parent scope if we succeeded
								$scope.updateSelectedScale(model);

								$scope.editing = false;
								resetSubmissionState();
							}, function(response) {
								resetSubmissionState();
								$scope.sdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
							});
						}
					};

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'sdForm');
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/scaleDetails.html'
			};
	}]);
})();
