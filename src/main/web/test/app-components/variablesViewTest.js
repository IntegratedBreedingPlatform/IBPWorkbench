/*global angular, expect, inject, spyOn, jasmine*/
'use strict';

describe('Variables Controller', function() {

	// This format is what is returned by a getVariables() call
	var PLANT_VIGOR = {
		id: 1,
		name: 'Plant Vigor',
		alias: '',
		description: 'A little vigourous',
		propertySummary: {
			id: 1,
			name: 'Plant Vigor'
		},
		methodSummary: {
			id: 1,
			name: 'Visual assessment at seedling stage'
		},
		scaleSummary: {
			id: 1,
			name: 'Score'
		},
		variableTypes: [{
			id: 1,
			name: 'Analysis',
			description: ''
		}],
		favourite: true
	},

	PLANT_VIGOR_DETAILED,
	PLANT_VIGOR_CONVERTED,

	q,
	controller,
	scope,
	timeout,

	variablesService,
	panelService,

	deferredGetVariable,
	deferredUpdateVariable,
	deferredGetVariables,
	deferredGetFavVariables;

	// This format is what is returned by a getVariable() call (singular)
	PLANT_VIGOR_DETAILED = angular.copy(PLANT_VIGOR);
	PLANT_VIGOR_CONVERTED = angular.copy(PLANT_VIGOR);

	PLANT_VIGOR_DETAILED.scale = {
		id: 1,
		name: 'Score',
		dataType: {
			id: 2,
			name: 'Numeric'
		},
		validValues: {
			min: 1,
			max: 5
		}
	};

	delete PLANT_VIGOR_CONVERTED.favourite;
	PLANT_VIGOR_CONVERTED['action-favourite'] = {};
	PLANT_VIGOR_CONVERTED['action-favourite'].iconValue = 'star';

	delete PLANT_VIGOR_DETAILED.id;
	delete PLANT_VIGOR_DETAILED.scaleSummary;

	PLANT_VIGOR_DETAILED.metadata = {
		deletable: true,
		editableFields: ['name', 'description']
	};

	beforeEach(function() {
		module('variablesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope, $timeout) {
		q = $q;
		scope = $rootScope;
		timeout = $timeout;

		variablesService = {
			getVariable: function() {
				deferredGetVariable = q.defer();
				return deferredGetVariable.promise;
			},
			updateVariable: function() {
				deferredUpdateVariable = q.defer();
				return deferredUpdateVariable.promise;
			},
			getVariables: function() {
				deferredGetVariables = q.defer();
				return deferredGetVariables.promise;
			},
			getFavouriteVariables: function() {
				deferredGetFavVariables = q.defer();
				return deferredGetFavVariables.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(variablesService, 'getVariable').and.callThrough();
		spyOn(variablesService, 'getVariables').and.callThrough();
		spyOn(variablesService, 'getFavouriteVariables').and.callThrough();
		spyOn(panelService, 'showPanel');

		controller = $controller('VariablesController', {
			$scope: scope,
			variablesService: variablesService,
			panelService: panelService
		});

		spyOn(controller, 'transformToDisplayFormat').and.callThrough();
		spyOn(controller, 'transformVariableToDisplayFormat').and.callThrough();
		spyOn(controller, 'transformDetailedVariableToDisplayFormat').and.callThrough();
	}));

	describe('ctrl.transformVariableToDisplayFormat', function() {

		it('should transform a variable summary into display format', function() {
			var rawVariable = PLANT_VIGOR,
			transformedVariable = {
				id: PLANT_VIGOR.id,
				name: PLANT_VIGOR.name,
				alias: PLANT_VIGOR.alias,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name,
				'action-favourite': PLANT_VIGOR.favourite ? { iconValue: 'star' } : { iconValue: 'star-empty' }
			};

			expect(controller.transformVariableToDisplayFormat(rawVariable)).toEqual(transformedVariable);
		});

		it('should default values to empty strings if they are not present', function() {
			var rawVariable = angular.copy(PLANT_VIGOR),
			transformedVariable;

			// Null out some values
			rawVariable.propertySummary = null;
			rawVariable.methodSummary = null;
			rawVariable.scaleSummary = null;
			rawVariable.favourite = false;

			transformedVariable = {
				id: PLANT_VIGOR.id,
				name: PLANT_VIGOR.name,
				alias: PLANT_VIGOR.alias,
				property: '',
				method: '',
				scale: '',
				'action-favourite': { iconValue: 'star-empty' }
			};
			expect(controller.transformVariableToDisplayFormat(rawVariable)).toEqual(transformedVariable);
		});

	});

	describe('ctrl.transformDetailedVariableToDisplayFormat', function() {

		it('should transform a detailed variable into display format', function() {
			var newId = 3,
				transformedVariable = {
					id: newId,
					name: PLANT_VIGOR_DETAILED.name,
					alias: PLANT_VIGOR_DETAILED.alias,
					property: PLANT_VIGOR_DETAILED.propertySummary.name,
					method: PLANT_VIGOR_DETAILED.methodSummary.name,
					scale: PLANT_VIGOR_DETAILED.scale.name,
					'action-favourite': PLANT_VIGOR_DETAILED.favourite ? { iconValue: 'star' } : { iconValue: 'star-empty' }
				};

			expect(controller.transformDetailedVariableToDisplayFormat(PLANT_VIGOR_DETAILED, newId)).toEqual(transformedVariable);
		});

		it('should default values to empty strings if they are not present', function() {
			var rawVariable = angular.copy(PLANT_VIGOR_DETAILED),
				transformedVariables;

			// Null out some values
			rawVariable.alias = null;
			rawVariable.propertySummary = null;
			rawVariable.methodSummary = null;
			rawVariable.scale = null;

			transformedVariables = {
				id: PLANT_VIGOR_DETAILED.id,
				name: PLANT_VIGOR_DETAILED.name,
				alias: '',
				property: '',
				method: '',
				scale: '',
				'action-favourite': { iconValue: 'star' }
			};
			expect(controller.transformDetailedVariableToDisplayFormat(rawVariable)).toEqual(transformedVariables);
		});

		it('should transform variables into display format', function() {
			var jsonData = [PLANT_VIGOR];

			deferredGetVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getVariables).toHaveBeenCalled();
			expect(controller.transformToDisplayFormat).toHaveBeenCalledWith(jsonData, jasmine.any(Function));
		});

		it('should show a message if there are no variables returned', function() {
			var jsonData = [];

			deferredGetVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getVariables).toHaveBeenCalled();
			expect(controller.showNoVariablesMessage).toBe(true);
		});

		it('should transform favourite variables into display format', function() {
			var jsonData = [PLANT_VIGOR];

			deferredGetFavVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getFavouriteVariables).toHaveBeenCalled();
			expect(controller.transformToDisplayFormat).toHaveBeenCalledWith(jsonData, jasmine.any(Function));
		});

		it('should show a message if there are no favourite variables returned', function() {
			var jsonData = [];

			deferredGetFavVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getFavouriteVariables).toHaveBeenCalled();
			expect(controller.showNoFavouritesMessage).toBe(true);
		});

		it('should set the selected item to be an object with an id property set to null by default', function() {
			expect(scope.selectedItem).toEqual({id: null});
		});

		it('should set the selected variable to be an empty object by default', function() {
			expect(scope.selectedVariable).toEqual({});
		});

		it('should show the all variables throbber after a delay', function() {
			timeout.flush();
			expect(controller.showAllVariablesThrobber).toBe(true);
		});

		it('should show the favourites throbber after a delay', function() {
			timeout.flush();
			expect(controller.showFavouritesThrobber).toBe(true);
		});

	});

	describe('ctrl.addAliasToTableIfPresent', function() {

		it('should not change colHeaders if it already contains alias', function() {
			var variables = [PLANT_VIGOR],
			colHeaders = ['name', 'alias'];

			controller.colHeaders = colHeaders;
			controller.addAliasToTableIfPresent(variables);

			expect(controller.colHeaders).toBe(colHeaders);
		});

		it('should add alias to colHeaders if a variable has an alias', function() {
			var variables = [{
				name: PLANT_VIGOR.name,
				alias: 'PlVgr'
			}];

			controller.colHeaders = ['name', 'description'];
			controller.addAliasToTableIfPresent(variables);

			expect(controller.colHeaders).toEqual(['name', 'alias', 'description']);
		});

		it('should not add alias to colHeaders if no variable has an alias', function() {
			var variables = [PLANT_VIGOR],
			colHeaders = ['name', 'description'];

			controller.colHeaders = colHeaders;
			controller.addAliasToTableIfPresent(variables);

			expect(controller.colHeaders).toBe(colHeaders);
		});
	});

	describe('$scope.showVariableDetails', function() {

		beforeEach(function() {
			scope.selectedItem.id = 123;
			scope.panelName = 'variables';
		});

		it('should set the selected variable to null before retrieving the selected property', function() {
			scope.showVariableDetails();

			expect(scope.selectedVariable).toEqual(null);

			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
		});

		it('should retrieve the selected variable and display the panel', function() {
			scope.showVariableDetails();
			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(variablesService.getVariable).toHaveBeenCalledWith(scope.selectedItem.id);
			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
			expect(panelService.showPanel).toHaveBeenCalledWith(scope.panelName);
		});
	});

	describe('$scope.toggleFavourite', function() {

		var favouriteVariable,
			nonFavouriteVariable;

		beforeEach(function() {
			scope.selectedItem.id = 123;

			favouriteVariable = {
				id: scope.selectedItem.id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name,
				'action-favourite': { iconValue: 'star' }
			};
			nonFavouriteVariable = {
				id: scope.selectedItem.id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name,
				'action-favourite': { iconValue: 'star-empty' }
			};

			spyOn(scope, 'updateSelectedVariable').and.callThrough();
		});

		it('should change the icon of the selected variable to the opposite one (star to star-empty)', function() {
			controller.variables = [favouriteVariable];
			scope.toggleFavourite();

			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(nonFavouriteVariable);

			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(variablesService.getVariable).toHaveBeenCalledWith(scope.selectedItem.id);
			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
		});
		it('should change the icon of the selected variable to the opposite one (star-empty to star)', function() {
			controller.variables = [nonFavouriteVariable];

			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();
			scope.toggleFavourite();

			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(favouriteVariable);
		});
	});

	describe('$scope.updateSelectedVariable', function() {

		it('should sync the updated variable in the variables list', function() {

			var updateSelectedVariable = angular.copy(PLANT_VIGOR_CONVERTED),
			newName = 'Not Plant Vigor',
			id = 1;

			controller.variables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name
			}];

			controller.favouriteVariables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name
			}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Update" our variable
			updateSelectedVariable.name = newName;

			scope.updateSelectedVariable(updateSelectedVariable);

			expect(controller.variables[0].name).toEqual(newName);
			expect(controller.favouriteVariables[0].name).toEqual(newName);
		});

		it('should remove the updated variable in the variables list if the variable is undefined', function() {

			var id = 1;

			controller.variables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name
			}];

			controller.favouriteVariables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name
			}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Delete" our variable
			scope.updateSelectedVariable();

			expect(controller.variables.length).toEqual(0);
			expect(controller.favouriteVariables.length).toEqual(0);
		});

		it('should remove an updated variable from the favourites list if it is no longer a favourite', function() {

			var updateSelectedVariable = angular.copy(PLANT_VIGOR_DETAILED),
			id = 1;

			controller.variables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name,
				'action-favourite': PLANT_VIGOR.favourite ? { iconValue: 'star' } : { iconValue: 'star-empty' }
			}];

			controller.favouriteVariables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.propertySummary.name,
				method: PLANT_VIGOR.methodSummary.name,
				scale: PLANT_VIGOR.scaleSummary.name,
				'action-favourite': PLANT_VIGOR.favourite ? { iconValue: 'star' } : { iconValue: 'star-empty' }
			}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Update" our variable
			updateSelectedVariable.favourite = false;

			scope.updateSelectedVariable(updateSelectedVariable);

			expect(controller.variables.length).toEqual(1);
			expect(controller.favouriteVariables.length).toEqual(0);

			expect(controller.variables[0]['action-favourite']).toEqual({ iconValue: 'star-empty' });
		});

		it('should only update the variable in the variables list matched by id', function() {

			var detailedVariableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),

			displayVariableToLeaveAlone = {
				id: 2,
				name: 'A Name',
				property: 'A Property',
				method: 'A Method',
				scale: 'A Scale'
			},

			newName = 'Not Plant Vigor';

			controller.variables = [displayVariableToLeaveAlone];
			controller.favouriteVariables = [displayVariableToLeaveAlone];

			// Select our variable for editing
			scope.selectedItem.id = 1;

			// "Update" our variable
			detailedVariableToUpdate.name = newName;

			scope.updateSelectedVariable(detailedVariableToUpdate);

			// Ensure non-matching variable was left alone
			expect(controller.variables[0]).toEqual(displayVariableToLeaveAlone);
			expect(controller.favouriteVariables[0]).toEqual(displayVariableToLeaveAlone);
		});

		it('should not update variables list if there is no variable in the list with a matching id, but should update favourites list',
		function() {

			var variableToUpdate = angular.copy(PLANT_VIGOR_CONVERTED),

			nonMatchingVariable = {
				id: 1,
				name: 'test1',
				property: 'A Property',
				method: 'A Method',
				scale: 'A Scale'
			},

			anotherNonMatchingVariable = {
				id: 2,
				name: 'test2',
				property: 'Another Property',
				method: 'Another Method',
				scale: 'Another Scale'
			};

			controller.variables = [nonMatchingVariable, anotherNonMatchingVariable];
			controller.favouriteVariables = [nonMatchingVariable, anotherNonMatchingVariable];

			// Select a property not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;
			variableToUpdate.id = 3;

			scope.updateSelectedVariable(variableToUpdate);

			// Ensure no updates happened
			expect(controller.variables[0]).toEqual(nonMatchingVariable);
			expect(controller.variables[1]).toEqual(anotherNonMatchingVariable);
			expect(controller.favouriteVariables[0]).toEqual(variableToUpdate);
			expect(controller.favouriteVariables[1]).toEqual(nonMatchingVariable);
			expect(controller.favouriteVariables[2]).toEqual(anotherNonMatchingVariable);
		});
	});

});
