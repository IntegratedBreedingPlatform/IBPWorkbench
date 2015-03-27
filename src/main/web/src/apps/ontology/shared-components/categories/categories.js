/*global angular*/
'use strict';

(function() {
	var categoriesModule = angular.module('categories', ['formFields']);

	categoriesModule.directive('omCategories', function(editable) {
		return {
			require: 'ngModel',
			restrict: 'E',
			scope: {
				categorical: '=omCategorical',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel'
			},
			controller: function($scope) {
				// Categories will be added to a 'categories' property on the specified property. If either the
				// property or the categories are not present, instantiate them
				if ($scope.model) {
					$scope.model[$scope.property] = $scope.model[$scope.property] || {};
					$scope.model[$scope.property].categories = $scope.model[$scope.property].categories || [{}];
				}

				$scope.editable = editable($scope);

				$scope.addCategory = function(e) {
					e.preventDefault();
					$scope.model[$scope.property].categories.push({});
				};

				$scope.removeCategory = function(e, index) {
					e.preventDefault();
					if ($scope.model[$scope.property].categories.length >= 2) {
						$scope.model[$scope.property].categories.splice(index, 1);
					}
				};
			},

			link: function(scope, elm, attrs, ctrl) {
				var resetValidity = function() {
					ctrl.$setValidity('emptyValue', true);
					ctrl.$setValidity('nonUniqueName', true);
					ctrl.$setValidity('nonUniqueValue', true);
				};

				scope.$watch('categorical', function (categorical) {
					if (!categorical) {
						resetValidity();
					}
				});

				scope.$watch('model[property].categories', function (data) {
					var names = [],
						values = [];

					resetValidity();

					if (!scope.categorical || !data) {
						return;
					}

					data.some(function(category) {
						if (!category.name || !category.description) {
							ctrl.$setValidity('emptyValue', false);
							return true;
						}

						if (names.indexOf(category.name) !== -1) {
							ctrl.$setValidity('nonUniqueName', false);
							return true;
						}

						if (values.indexOf(category.description) !== -1) {
							ctrl.$setValidity('nonUniqueValue', false);
							return true;
						}

						names.push(category.name);
						values.push(category.description);
					});
				}, true);
			},

			templateUrl: 'static/views/ontology/categories.html'
		};
	});
}());
