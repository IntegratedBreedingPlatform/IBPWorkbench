/*global angular, inject, expect, spyOn*/
'use strict';

describe('Categories module', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		scope,
		isolateScope,
		directiveElement,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};

		angular.mock.module('templates');
		module('categories');
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	function compileDirective(attrs) {
		inject(function($compile) {
			directiveElement = $compile('<om-categories ng-model="model" om-property="validValues"' + attrs + '></om-categories>')(scope);
		});

		scope.$digest();

		isolateScope = directiveElement.isolateScope();
	}

	it('should create a categories array with an empty category if not present and categorical is true', function() {
		scope.model = {};

		compileDirective('om-categorical="false"');

		isolateScope.categorical = true;
		isolateScope.$apply();

		expect(angular.equals(isolateScope.model[isolateScope.property].categories, [{ editable: true }])).toBe(true);
	});

	describe('$scope.addCategory', function() {

		it('should add an empty category to the categories array on the scale object', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: '1',
						description: 'Very low',
						editable: true
					}]
				}
			};

			compileDirective();

			isolateScope.addCategory(fakeEvent);

			expect(scope.model.validValues.categories.length).toEqual(2);
			expect(scope.model.validValues.categories[1]).toEqual({name: '', description: '', editable: true});
		});
	});

	describe('$scope.removeCategory', function() {

		it('should remove the category with the specified label', function() {

			var cat1 = {
					label: 'a',
					value: 'value a',
					editable: true
				},
				cat2 = {
					label: 'b',
					value: 'value b',
					editable: true
				};

			scope.model = {
				validValues: {
					categories: [cat1, cat2]
				}
			};

			compileDirective();

			isolateScope.removeCategory(fakeEvent, cat1.label, cat1);

			expect(scope.model.validValues.categories.length).toEqual(1);
			expect(scope.model.validValues.categories[0]).toEqual(cat2);
		});

		it('should not remove the category if there is only 1 category left in the list', function() {

			var cat1 = {
					label: 'a',
					value: 'value a',
					editable: true
				};

			scope.model = {
				validValues: {
					categories: [cat1]
				}
			};

			compileDirective();

			isolateScope.removeCategory(fakeEvent, cat1.label, cat1);

			expect(scope.model.validValues.categories.length).toEqual(1);
		});
	});

	describe('Categories validation', function() {

		function compileForm(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				directiveElement = $compile(
					'<form name="testForm" novalidate>' +
						'<om-categories name="omCategories" ng-model="model" om-property="validValues" ' + attrs + '></om-categories>' +
					'</form>'
					)(scope);
			});

			scope.$digest();
		}

		it('should set the widget to be valid if the selected data type is not categorical', function() {

			scope.model = {};

			compileForm('om-categorical="false"');

			expect(scope.testForm.$valid).toBe(true);
		});

		it('should not validate the categories if the form is not valid', function() {
			scope.model = {};

			inject(function($compile) {
				directiveElement = $compile(
					'<om-categories name="omCategories" ng-model="model" om-property="validValues" om-categorical="true"></om-categories>'
					)(scope);
			});
			scope.$digest();
			isolateScope = directiveElement.isolateScope();
			isolateScope.categoriesForm.$setValidity('mymin', false);
			spyOn(isolateScope, 'validateCategories');
			scope.model.validValues.categories = [{
				description: 'new description but no name'
			}];
			scope.$digest();
			expect(isolateScope.validateCategories).not.toHaveBeenCalled();
		});

		it('should set the widget to be valid if the specified model is undefined or null', function() {

			scope.model = undefined;
			compileForm('om-categorical="true"');

			expect(scope.testForm.$valid).toBe(true);

			scope.model = null;
			compileForm('om-categorical="true"');

			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the emptyValue error to be true if there is a category with no name', function() {

			scope.model = {
				validValues: {
					categories: [{
						description: 'description but no name'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				emptyValue: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the emptyValue error to be true if there is an empty description', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: 'name but no description'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				emptyValue: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the nonUniqueName error to be true if there are two categories with the same name', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: 'name',
						description: 'description 1'
					},
					{
						name: 'name',
						description: 'description 2'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				nonUniqueName: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the widget to be invalid if there are two categories with the same description', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: 'name 1',
						description: 'description'
					},
					{
						name: 'name 2',
						description: 'description'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				nonUniqueValue: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});
	});
});
