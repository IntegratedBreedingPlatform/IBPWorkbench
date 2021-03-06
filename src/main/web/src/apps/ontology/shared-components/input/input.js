/*global angular*/
'use strict';

(function() {
	var inputModule = angular.module('input', ['formFields']);

	inputModule.directive('omInput', ['editable', 'disabled', function (editable, disabled) {
		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);
				$scope.disabled = disabled($scope);

				// We cannot assign values to one time binding scope properties that are not defined
				// on the directive instance, so instead we must use a different scope property
				// and just read from the initial property as to whether the value was given or not.
				$scope.required = $scope.omRequired || false;
				$scope.maxLength = $scope.omMaxLength || -1;
				$scope.disabling = $scope.disabling || false;
				$scope.regex = $scope.pattern ? new RegExp($scope.pattern) : /[\s\S]*/;
				$scope.defaultValue = $scope.omDefaultValue || "";

			}],
			restrict: 'E',
			scope: {
				name: '@omName',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				pattern: '@omPattern',
				// Use this syntax for optional one time binding properties
				omRequired: '@',
				omMaxLength: '@',
				disabling: '=omDisabled',
				omDefaultValue:'@'
			},
			templateUrl: 'static/views/ontology/input.html'
		};
	}]);

})();
