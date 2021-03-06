// Karma configuration
// Generated on Fri Jan 30 2015 13:34:45 GMT+1300 (New Zealand Daylight Time)

// Get the Chromium executable bundled in Puppeteer.
process.env.CHROME_BIN = require('puppeteer').executablePath();

module.exports = function(config) {
	'use strict';
	config.set({

		// base path that will be used to resolve all patterns (eg. files, exclude)
		basePath: '',

		// frameworks to use
		// available frameworks: https://npmjs.org/browse/keyword/karma-adapter
		frameworks: ['jasmine-jquery', 'jasmine'],

		// list of files / patterns to load in the browser
		files: [
			'src/js/lib/angular.min.js',
			'src/js/lib/angular-route.min.js',
			'src/js/lib/angular-messages.min.js',
			'src/js/lib/angular-sanitize.min.js',
			'src/js/lib/angular-ui-select.min.js',
			'src/js/lib/angular-debounce.min.js',
			'src/js/lib/angular-translate.min.js',
			'src/js/lib/angular-local-storage.min.js',
			'src/js/lib/angular-translate-loader-static-files.min.js',
			'src/js/lib/ui-bootstrap-custom-0.13.3.min.js',
			'src/js/lib/jquery-1.11.1.min.js',
			'src/js/lib/bootstrap.min.js',
			'src/js/lib/bootbox.min.js',
			'src/js/lib/ngBootbox.min.js',

			'test/lib/angular-mocks.js',
			'src/apps/**/*.js',
			'test/**/*.js',
			'../webapp/WEB-INF/static/views/**/*.html'
		],

		// list of files to exclude
		exclude: [
			'karma.conf*.js'
		],

		// preprocess matching files before serving them to the browser
		// available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
		preprocessors: {
			// generate js files from html templates
			'../webapp/WEB-INF/static/views/**/*.html': 'ng-html2js',
			'src/apps/**/*.js': ['coverage']
		},

		// test results reporter to use
		// possible values: 'dots', 'progress', 'nyan'
		// available reporters: https://npmjs.org/browse/keyword/karma-reporter
		reporters: ['nyan', 'coverage'],

		coverageReporter: {
			type: 'text-summary'
		},

		// web server port
		port: 9876,

		// enable / disable colors in the output (reporters and logs)
		colors: true,

		// level of logging
		// possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
		logLevel: config.LOG_INFO,

		// enable / disable watching file and executing tests whenever any file changes
		autoWatch: true,

		// start these browsers
		// available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
		browsers: ['ChromeHeadlessNoSandbox'],

		customLaunchers: {
			ChromeHeadlessNoSandbox: {
				base: 'ChromeHeadless',
				flags: ['--no-sandbox']
			}
		},

		// Continuous Integration mode
		// if true, Karma captures browsers, runs the tests and exits
		singleRun: false,

		// Increased the default timeout as it was timing out on Windows
		browserNoActivityTimeout: 30000,

		ngHtml2JsPreprocessor: {
			cacheIdFromPath: function(filepath) {
				// Convert template path from its file system path to the path expected by the javascript using the template
				return filepath.substr(filepath.indexOf('static'), filepath.length);
			},

			// setting this option will create only a single module that contains templates
			// from all the files, so you can load them all with module('templates')
			moduleName: 'templates'
		}
	});
};
