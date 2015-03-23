/*global angular, expect, inject, spyOn*/
'use strict';

var CUT_AND_DRY = {
		name: 'Cut and Dry',
		description: 'Self explanatory really'
	},

	q,
	controller,
	scope,
	deferredGetMethods,
	deferredGetMethod,
	methodsService,
	panelService;

describe('Methods View', function() {

	beforeEach(function() {
		module('methodsView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {

		q = $q;
		scope = $rootScope;

		methodsService = {
			getMethod: function() {
				deferredGetMethod = q.defer();
				return deferredGetMethod.promise;
			},
			getMethods: function() {
				deferredGetMethods = q.defer();
				return deferredGetMethods.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(methodsService, 'getMethod').and.callThrough();
		spyOn(panelService, 'showPanel');

		controller = $controller('MethodsController', {
			$scope: scope,
			methodsService: methodsService,
			panelService: panelService
		});
	}));

	it('should get methods', function() {
		deferredGetMethods.resolve([]);
		scope.$apply();
		expect(methodsService.getMethods).toHaveBeenCalled();
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected method to be null by default', function() {
		expect(scope.selectedMethod).toEqual(null);
	});

	describe('$scope.showMethodDetails', function() {

		it('should set the selected method to null before retrieving the selected method', function() {

			var selectedId = 123,
				panelName = 'methods',
				method = CUT_AND_DRY;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showMethodDetails();

			expect(scope.selectedMethod).toEqual(null);

			deferredGetMethod.resolve(method);
			scope.$apply();

			expect(scope.selectedMethod).toEqual(method);
		});

		it('should retrieve the selected method and display the panel', function() {

			var selectedId = 123,
				panelName = 'methods',
				method = CUT_AND_DRY;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showMethodDetails();
			deferredGetMethod.resolve(method);
			scope.$apply();

			expect(methodsService.getMethod).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedMethod).toEqual(method);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});

	describe('$scope.updateSelectedMethod', function() {

		it('should sync the updated method in the methods list', function() {

			var methodToUpdate = angular.copy(CUT_AND_DRY),
				newName = 'Not Cut and Dry';

			controller.methods = [{
				id: 1,
				name: methodToUpdate.name
			}];

			// Select our method for editing
			scope.selectedItem.id = 1;

			// "Update" our method
			methodToUpdate.name = newName;

			scope.updateSelectedMethod(methodToUpdate);

			expect(controller.methods[0].name).toEqual(newName);
		});

		it('should only update the method in the methods list matched by id', function() {

			var detailedMethodToUpdate = angular.copy(CUT_AND_DRY),

				displayMethodToLeaveAlone = {
					id: 2,
					name: 'Another Method'
				},

				displayMethodToUpdate = {
					id: 1,
					name: detailedMethodToUpdate.name
				},

				newName = 'Not Cut and Dry';

			controller.methods = [displayMethodToLeaveAlone, displayMethodToUpdate];

			// Select our method for editing
			scope.selectedItem.id = 1;

			// "Update" our method
			detailedMethodToUpdate.name = newName;

			scope.updateSelectedMethod(detailedMethodToUpdate);

			// Ensure non-matching method was left alone
			expect(controller.methods[0]).toEqual(displayMethodToLeaveAlone);
		});

		it('should not update any methods if there is no method in the list with a matching id', function() {

			var methodToUpdate = angular.copy(CUT_AND_DRY),

				nonMatchingMethod = {
					id: 1,
					name: 'Non Matching Method'
				},

				anotherNonMatchingMethod = {
					id: 2,
					name: 'Another Non Matching Method'
				};

			controller.methods = [nonMatchingMethod, anotherNonMatchingMethod];

			// Select a method not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;

			scope.updateSelectedMethod(methodToUpdate);

			// Ensure no updates happened
			expect(controller.methods[0]).toEqual(nonMatchingMethod);
			expect(controller.methods[1]).toEqual(anotherNonMatchingMethod);
		});
	});
});