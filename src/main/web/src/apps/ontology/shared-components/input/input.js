/*global angular*/
'use strict';

(function() {
	var inputModule = angular.module('input', ['formFields']);

	inputModule.directive('omInput', ['editable', function(editable) {
		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);
				$scope.required = $scope.required || false;
				$scope.maxLength = $scope.maxLength || -1;
				$scope.regex = $scope.pattern ? new RegExp($scope.pattern) : /[\s\S]*/;
			}],
			restrict: 'E',
			scope: {
				name: '@omName',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				required: '@omRequired',
				maxLength: '@omMaxLength',
				pattern: '@omPattern'
			},
			templateUrl: 'static/views/ontology/input.html'
		};
	}]);

})();
