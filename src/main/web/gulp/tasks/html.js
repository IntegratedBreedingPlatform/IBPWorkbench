'use strict';

var changed = require('gulp-changed'),
	dest = '../webapp/WEB-INF/pages',
	gulp = require('gulp');

gulp.task('html', function() {
	return gulp.src('src/pages/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});