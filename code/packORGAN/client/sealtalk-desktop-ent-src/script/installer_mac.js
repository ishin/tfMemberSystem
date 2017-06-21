var builder = require('electron-builder');
const path = require('path')
const rimraf = require('rimraf')
const packageJson = require('../package.json');
const Config = require('../config.js')

var buildJson = {
  "osx" : {
    "title": Config.APPNAME,
    "background": path.join(__dirname, '..', 'res', Config.MAC.BACKGROUND),
    "icon": path.join(__dirname, '..', 'res', Config.MAC.APP),
    "icon-size": 80,
    "contents": [
      { "x": 438, "y": 160, "type": "link", "path": "/Applications" },
      { "x": 192, "y": 160, "type": "file" }
    ]
  }
}
var options = {
    platform: 'osx',
    out: 'dist/osx',
    config: buildJson,
    appPath: 'build/' + Config.APPNAME + '-darwin-x64/' + Config.APPNAME + '.app',
    basePath: '..',
    overwrite: true
};

// create the installer
var electronBuilder = builder.init();
// electronBuilder.build(options, function(){
//     // other stuff here
// });

function deleteOutputFolder () {
  return new Promise((resolve, reject) => {
    rimraf(path.join(__dirname, '..', 'dist', 'osx'), (error) => {
      error ? reject(error) : resolve()
    })
  })
}

function createMacInstaller () {
  return new Promise((resolve, reject) => {
  	electronBuilder.build(options, function(error){
       error ? reject(error) : resolve()
    });
  })
}


deleteOutputFolder()
  .then(createMacInstaller)
  .catch((error) => {
    console.error(error.message || error)
    process.exit(1)
  })