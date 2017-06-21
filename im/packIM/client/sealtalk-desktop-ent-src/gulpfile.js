'use strict';

var gulp = require('gulp');
var sequence = require('gulp-sequence');
var clean = require('gulp-clean');
var fs = require('fs-extra');
var argv = require('yargs')
      .option('platform', {
        alias: 'p',
        describe: 'choose a platform',
        choices: ['mac', 'darwin', 'windows','win', 'win32', 'win64'],
        default: 'darwin'
      })
      .argv;
var path = require('path');
var packager = require('electron-packager');
var builder = require('electron-builder');
var electronInstaller = require('electron-winstaller');

var packageJSON = require('./package.json');
var config = require('./config.js');
var CURRENT_ENVIRONMENT = 'development';
var finalAppPaths = [];
const zip = require('gulp-zip');
var childProcess = require('child_process');


gulp.task('zip', () => {
  var fileName = 'SealTalk-' + config.VERSION + '-darwin-x64.zip';
	return gulp.src('build/SealTalk-Ent-darwin-x64/SealTalk_Ent.app')
		.pipe(zip(fileName))
		.pipe(gulp.dest('dist/osx'));
});

gulp.task('default', ['serve']);

gulp.task('cleanup:build', function() {
  var osInfo = getOSInfo();
  var arch = osInfo.arch;
  var platform = osInfo.platform;
  var src = './build/' + config.PRODUCTNAME + '-' + platform + '-' + arch;
  src = './build';
  return gulp
    .src([src], {
      read: false
    })
    .pipe(clean());
});

// gulp.task('test', function(done) {
//   builder({
//     'appPath': 'build/SealTalk-darwin-x64/SealTalk.app',
//     'platform': 'osx',
//     'out': 'dist/osx',
//     'overwrite': true,
//     'config': {
//       "osx" : {
//         "title": "SealTalk",
//         "background": "res/bg.png",
//         "icon": "res/app.icns",
//         "icon-size": 80,
//         "title": "SealTalk_by_RongCloud_1_0_2",
//         "contents": [
//           { "x": 438, "y": 160, "type": "link", "path": "/Applications" },
//           { "x": 192, "y": 160, "type": "file" }
//         ]
//       }
//     }
//   }, function(error, appPaths) {
//     if (error) {
//       console.log(error);
//       process.exit(1);
//     }
//     else {
//       // TODO
//       // we should support to build all platforms at once later !
//       // something like [ 'build/Kaku-darwin-x64' ]
//       finalAppPaths = appPaths;
//       done();
//     }
//   });
// });

gulp.task('package', function(done) {
  var devDependencies = packageJSON.devDependencies;
  var devDependenciesKeys = Object.keys(devDependencies);
  var ignoreFiles = [
    // 'build',
    'dist',
    'dist2',
    'script',
    'notice.txt',
    'gulpfile.js',
    'builder.json',
    'gruntfile.js',
    '.npminstall',
    'index.html',
    'index1.html',
    'app.js',
    '配置说明.txt'
  ];

  devDependenciesKeys.forEach(function(key) {
    ignoreFiles.push('node_modules/' + key);
  });
  var osInfo = getOSInfo();
  var arch = osInfo.arch;
  var platform = osInfo.platform;

  // We will keep all stuffs in dist/ instead of src/ for production
  var iconFolderPath = './res';
  var iconPath;
  var productName = config.PRODUCTNAME;
  productName += '-' + platform + '-' + arch;
  if (platform === 'darwin') {
    iconPath = path.join(iconFolderPath, config.MAC.APPICON);
    ignoreFiles.push('js/child.js');
  }
  else {
    iconPath = path.join(iconFolderPath, config.WIN.APPICON);
  }

  var ignorePath = ignoreFiles.join('|');
  var ignoreRegexp = new RegExp(ignorePath, 'ig');
  // var unpackRegexp = new RegExp(['screenshot.framework','RongIMLib.node'], 'ig');
  // var unpackRegexp = new RegExp(['*.node'], 'ig');

  packager({
    'dir': './',
    //'name': config.PRODUCTNAME,
    'name': config.APPNAME,
    'platform': platform,
    'asar': false,
    // 'asar-unpack': 'RongIMLib.node',
    // 'asar-unpack-dir': 'node_modules/screenshot.framework',
    'arch': arch,
    'version': config.PACKAGE.RUNTIMEVERSION,
    'out': './build',
    'icon': iconPath,
    'app-bundle-id': config.MAC.APP_BUNDLE_ID,   // OS X only
    'app-version': config.VERSION,
    'build-version': config.MAC.CF_BUNDLE_VERSION,
    'helper-bundle-id': config.MAC.HELPER_BUNDLE_ID, // OS X only
    'ignore': ignoreRegexp,
    'overwrite': true,
    'prune': true,
    'app-copyright': config.PACKAGE.COPYRIGHT,
    // 'osx-sign': true,
    //'osx-sign': {
     //  'identity': 'Developer ID Application: Beijing Rong Cloud Network Technology CO., LTD (CQJSB93Y3D)'
    //},
    // 'all': true,
    
    'protocols': [{
        name: config.PROTOCAL,
        schemes: [config.PROTOCAL]
     }],
    'win32metadata': {
      'CompanyName': config.AUTHOR,
      'FileDescription': config.DESCRIPTION,
      'OriginalFilename': 'atom.exe',
      'ProductName': config.PRODUCTNAME,
      'InternalName': config.PRODUCTNAME
    }
  }, function(error, appPaths) {
    if (error) {
      console.log(error);
      process.exit(1);
    }
    else {
      // TODO
      // we should support to build all platforms at once later !
      // something like [ 'build/Kaku-darwin-x64' ]
      finalAppPaths = appPaths;
      done();
    }
  });
});

gulp.task('post-package', function(done) {
  var currentLicenseFile = path.join(__dirname, 'LICENSE');

  var promises = finalAppPaths.map(function(appPath) {
    var targetLicenseFile = path.join(appPath, 'LICENSE');
    var promise = new Promise(function(resolve, reject) {
      fs.copy(currentLicenseFile, targetLicenseFile, function(error) {
        if (error) {
          reject(error);
        }
        else {
          resolve();
        }
      });
    });
    return promise;
  });

  Promise.all(promises).then(function() {
    done();
  }).catch(function(error) {
    console.log(error)
    process.exit(1);
  });
});

gulp.task('build', function(callback) {
  var osInfo = getOSInfo();
  var tasks = [
    'cleanup:build',
    'package',
    'post-package'
  ];
  // if(osInfo.platform == 'darwin'){
  //   tasks.push('zip')
  // }

  sequence(
    tasks
  )(callback);
});

gulp.task('createWindowsInstaller', function(done) {
  var osInfo = getOSInfo();
  var appDirectory = './build/' + config.PRODUCTNAME + '-win32-' + osInfo.arch;
  var outputDirectory = './dist/installer_' + osInfo.arch;
  var resultPromise = electronInstaller.createWindowsInstaller({
      appDirectory: appDirectory,
      outputDirectory: outputDirectory,
      authors: config.AUTHOR,
      exe: config.PRODUCTNAME + '.exe',
      setupIcon: './res/app.ico',
      setupExe: config.PRODUCTNAME + '_by_' + config.AUTHOR + '_' + config.VERSION + '.exe',
      noMsi: 'true',
      iconUrl: config.WIN.ICON_URL,
      loadingGif: config.WIN.LOADING_GIF
  });
  resultPromise.then(() => console.log("It worked!"), (e) => console.log(`No dice: ${e.message}`));
});

function getOSInfo(){
  var arch = process.arch || 'ia32';
  var platform = argv.platform || process.platform;
  platform = platform.toLowerCase();
  // platform = argv.p;
  switch (platform) {
    case 'mac':
    case 'darwin':
      platform = 'darwin';
      arch = 'x64';
      break;
    case 'freebsd':
    case 'linux':
      platform = 'linux';
      break;
    case 'linux32':
      platform = 'linux';
      arch = 'ia32';
      break;
    case 'linux64':
      platform = 'linux';
      arch = 'x64';
      break;
    case 'win':
    case 'win32':
    case 'windows':
      platform = 'win32';
      arch = 'ia32';
      break;
    case 'win64':
        platform = 'win32';
        arch = 'x64';
        break;
    default:
      console.log('We don\'t support your platform ' + platform);
      process.exit(1);
      break;
  }
  return {platform:platform, arch:arch};
}


function installerMac () {
  return new Promise((resolve, reject) => {
    console.log('begin make installerMac')
    var cmd = 'rm -rf ./dist/osx/SealTalk_Ent.dmg && electron-builder \"build/SealTalk_Ent-darwin-x64/SealTalk_Ent.app\" --platform=osx --out=\"dist/osx\" --config=builder.json --overwrite'

    childProcess.exec(cmd, (error, stdout, stderr) => {
      if (error) {
        reject('installerMac failed' + error)
      } else {
        resolve()
      }
    })
  })
}

gulp.task('installerMac', function (cb) {
  var cmd = 'rm -rf ./dist/osx/SealTalk_Ent.dmg && electron-builder \"build/SealTalk_Ent-darwin-x64/SealTalk_Ent.app\" --platform=osx --out=\"dist/osx\" --config=builder.json --overwrite'

    childProcess.exec(cmd, (err, stdout, stderr) => {
    console.log(stdout);
    console.log(stderr);
    cb(err);
  });
})
