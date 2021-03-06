/*global angular*/
'use strict';

(function() {
	var tagSelect = angular.module('tagSelect', ['formFields', 'clickAway', 'selectScroll', 'utilities']);

	tagSelect.directive('omTagSelect', ['editable', 'selectScroll', 'ieUtilities', function(editable, selectScroll, ieUtilities) {
		var MAX_LENGTH = 100,
			MIN_LENGTH = 2;

		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);
			}],

			link: function(scope, elm, attrs, ctrl) {
				var listElement = elm.find('ul'),
					rawListElement = listElement[0];

				scope.suggestions = angular.copy(scope.options);
				scope.searchText = '';
				scope.textTooLong = false;
				scope.selectedIndex = -1;

				ieUtilities.addIeClearInputHandler(elm, function() {
					scope.hideSuggestions();
					scope.$apply();
				});

				// Set the input to contain the text of the selected item from the suggestions
				scope.$watch('selectedIndex', function(index) {
					if (index !== -1 && scope.suggestions.length > 0) {
						scope.searchText = scope.suggestions[index];
					}
				});

				scope.$watch('model[property]', function(items) {
					ctrl.$setValidity('emptyValue', true);

					if (items && items.length < 1) {
						ctrl.$setValidity('emptyValue', false);
					}

				}, true);

				scope.$watch('searchText', function(text) {
					ctrl.$setValidity('maxlength', true);

					if (text && scope.textTooLong) {
						ctrl.$setValidity('maxlength', false);
					}
				});

				scope.$watch('tagSelectForm.omTagSelectText.$touched', function(touched) {
					if (touched) {
						ctrl.$setTouched();
					}
				});

				scope.addToSelectedItems = function(index) {
					var itemToAdd = scope.suggestions[index];

					// Allow the user to add the text they have entered as an item without having
					// to select it from the list, so long as it is of valid length
					if (scope.searchText && !itemToAdd && scope.itemIsValidLength) {
						itemToAdd = scope.searchText;
					}

					// Add the item if it hasn't already been added
					if (itemToAdd && scope.model[scope.property].indexOf(itemToAdd) === -1) {
						scope.model[scope.property].push(itemToAdd);
						return true;
					}

					return false;
				};

				scope.search = function() {
					scope.itemIsValidLength = scope.searchText.length >= MIN_LENGTH && scope.searchText.length <= MAX_LENGTH;

					scope.suggestions = angular.copy(scope.options);

					// Add the search term text that the user has entered into the start of the
					// suggestions list so that they can add it if no suitable suggestion is found
					if (scope.searchText && scope.suggestions.indexOf(scope.searchText) === -1 && scope.itemIsValidLength) {
						scope.suggestions.unshift(scope.searchText);
					}

					// Only return options that match the search term
					scope.suggestions = scope.suggestions.filter(function(value) {

						var lowerValue = value.toLowerCase(),
							lowerSearchText = scope.searchText.toLowerCase();

						return lowerValue.indexOf(lowerSearchText) !== -1;
					});

					// Only return options that haven't already been selected
					scope.suggestions = scope.suggestions.filter(function(value) {

						return scope.model[scope.property].indexOf(value) === -1;
					});

					// Indicate whether the text is too long so that the validation styling can be added appropriately
					scope.textTooLong = scope.searchText.length > MAX_LENGTH;
					scope.selectedIndex = -1;
				};

				scope.checkKeyDown = function(event) {
					var itemAdded;

					if (event.keyCode === 40) {
						// Down key, increment selectedIndex
						event.preventDefault();

						// Load the suggestions if the user presses down with an empty input
						if (scope.selectedIndex === -1) {
							scope.showSuggestions();
						}

						if (scope.selectedIndex + 1 < scope.suggestions.length) {
							scope.selectedIndex++;
						}
						selectScroll.ensureHighlightVisible(listElement, rawListElement, scope.selectedIndex);

					} else if (event.keyCode === 38) {
						// Up key, decrement selectedIndex
						event.preventDefault();

						if (scope.selectedIndex - 1 > -1) {
							scope.selectedIndex--;
						}
						selectScroll.ensureHighlightVisible(listElement, rawListElement, scope.selectedIndex);

					} else if (event.keyCode === 13) {
						// Enter pressed, select item
						event.preventDefault();

						itemAdded = scope.addToSelectedItems(scope.selectedIndex);
						if (itemAdded) {
							scope.hideSuggestions();
						}

					} else if (event.keyCode === 27) {
						// Escape pressed, close suggestions
						event.preventDefault();
						if (!scope.isSuggestionBoxClosed()) {
							event.stopPropagation();
						}
						scope.hideSuggestions();

					} else if (event.keyCode === 9) {
						// Tab pressed, close suggestions and continue with event
						scope.hideSuggestions();
					}
				};

				scope.onClick = function(index) {
					scope.addToSelectedItems(index);
					scope.hideSuggestions();
				};

				scope.removeItem = function(event, index) {
					event.preventDefault();
					scope.model[scope.property].splice(index, 1);
				};

				scope.isSuggestionBoxClosed = function() {
					return scope.selectedIndex === -1 && scope.suggestions.length === 0;
				};

				scope.toggleSuggestions = function() {
					if (scope.isSuggestionBoxClosed()) {
						scope.showSuggestions();
					} else {
						scope.hideSuggestions();
					}
				};

				scope.showSuggestions = function() {
					scope.search();
					scope.suggestionsShown = true;
				};

				scope.hideSuggestions = function() {
					selectScroll.resetScroll(rawListElement);
					scope.suggestions = [];
					scope.selectedIndex = -1;
					scope.searchText = '';
					scope.textTooLong = false;
					scope.suggestionsShown = false;
					elm.find('input')[0].focus();
				};

				scope.formatForDisplay = function(items) {
					return items ? items.join(', ') : '';
				};

			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel',
				options: '=omOptions',
				property: '@omProperty'
			},
			templateUrl:'static/views/ontology/tagSelect.html'
		};
	}]);

}());
