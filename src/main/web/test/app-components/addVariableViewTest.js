/*global expect, inject, spyOn*/
'use strict';

describe('Add Variable View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			description: 'A little vigourous',
			propertyId: 1,
			methodId: 1,
			scaleId: 1,
			variableTypeIds: [
				1
			]
		},

		variablesService = {},
		propertiesService = {},
		methodsService = {},
		scalesService = {},

		variableStateService = {
			updateInProgress: function() {},
			getVariableState: function() {},
			storeVariableState: function() {},
			reset: function() {}
		},

		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},

		PERCENTAGE = {
			name: 'Percentage',
			dataType: {
				id: 2,
				name: 'Numeric'
			}
		},

		CATEGORICAL = {
			name: 'Categorical Scale',
			dataType: {
				id: 1,
				name: 'Categorical'
			}
		},

		deferred = [],
		deferredAddVariable,

		q,
		location,
		scope,
		controllerFn,

		controller;

	function fakePromise() {
		return function() {
			var defer = q.defer();
			deferred.push(defer);
			return defer.promise;
		};
	}

	function compileController() {
		controller = controllerFn('AddVariableController', {
			$scope: scope,
			$location: location,
			variablesService: variablesService,
			propertiesService: propertiesService,
			methodsService: methodsService,
			scalesService: scalesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		deferred.forEach(function(d) {
			d.resolve();
		});

		scope.$apply();
	}

	beforeEach(function() {
		module('addVariable');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller) {
		q = $q;
		location = $location;
		scope = $rootScope;
		controllerFn = $controller;

		propertiesService.getProperties = fakePromise();
		methodsService.getMethods = fakePromise();
		scalesService.getScales = fakePromise();
		variablesService.getTypes = fakePromise();

		// We want a little more control over when this gets resolved
		variablesService.addVariable = function() {
			deferredAddVariable = q.defer();
			return deferredAddVariable.promise;
		};

		spyOn(variableStateService, 'reset');
		spyOn(variableStateService, 'storeVariableState');

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScales').and.callThrough();

		spyOn(variablesService, 'addVariable').and.callThrough();
		spyOn(variablesService, 'getTypes').and.callThrough();

		spyOn(location, 'path');
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');
	}));

	describe('by default', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should hide the range widget by default', function() {
			expect(scope.showRangeWidget).toBe(false);
		});

		it('should show the range widget if the variable changes to have a Numeric data type', function() {
			scope.variable.scale = PERCENTAGE;
			scope.$apply();

			expect(scope.showRangeWidget).toBe(true);
		});

		it('should hide the range widget if the variable changes to have a non Numeric data type', function() {
			scope.variable.scale = CATEGORICAL;
			scope.$apply();

			expect(scope.showRangeWidget).toBe(false);
		});
	});

	describe('when a variable update is in progress', function() {

		var state = {
			variable: 'variable',
			scopeData: 'data'
		};

		beforeEach(function() {
			// Pretend an edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'getVariableState').and.returnValue(state);

			compileController();
		});

		it('should set the variable and data properties on the $scope', function() {
			expect(scope.variable).toEqual(state.variable);
			expect(scope.scopeData).toEqual(state.data);
		});

		it('should not get property, method, scale or variable type data from services', function() {
			expect(propertiesService.getProperties.calls.count()).toEqual(0);
			expect(methodsService.getMethods.calls.count()).toEqual(0);
			expect(scalesService.getScales.calls.count()).toEqual(0);
			expect(variablesService.getTypes.calls.count()).toEqual(0);
		});
	});

	describe('when a variable update is not in progress', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should get property, method, scale or variable type data from services', function() {
			expect(propertiesService.getProperties).toHaveBeenCalled();
			expect(methodsService.getMethods).toHaveBeenCalled();
			expect(scalesService.getScales).toHaveBeenCalled();
			expect(variablesService.getTypes).toHaveBeenCalled();
		});
	});

	describe('$scope.saveVariable', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();

			// Set the form to be valid
			scope.avForm = {
				$valid: true
			};
		});

		it('should call the variables service to save the variable', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);
			expect(variablesService.addVariable).toHaveBeenCalledWith(PLANT_VIGOR);
		});

		it('should not call the variables service if the form is not valid', function() {
			// Set the form to be invalid
			scope.avForm.$valid = false;
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			expect(variablesService.addVariable.calls.count()).toEqual(0);
		});

		it('should handle any errors and not redirect if the save was not successful', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			deferredAddVariable.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
			expect(location.path.calls.count()).toEqual(0);
		});

		it('should reset the state of any stored variable after a successful save', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			deferredAddVariable.resolve();
			scope.$apply();

			expect(variableStateService.reset).toHaveBeenCalled();
		});

		it('should redirect to /variables after a successful save', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			deferredAddVariable.resolve();
			scope.$apply();

			expect(location.path).toHaveBeenCalledWith('/variables');
		});
	});

	describe('$scope.addNew', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should store the variable state and data from the scope', function() {
			var variable = {
				name: 'variable'
			},
			data = {
				someData: {}
			};

			scope.variable = variable;
			scope.data = data;

			scope.addNew(fakeEvent, '');
			expect(variableStateService.storeVariableState).toHaveBeenCalledWith(variable, data);
		});

		it('should redirect to /variables after a successful state save', function() {
			var path = 'path';

			scope.addNew(fakeEvent, path);
			expect(location.path).toHaveBeenCalledWith('/add/' + path);
		});
	});
});
