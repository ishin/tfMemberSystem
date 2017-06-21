'use strict'

const electron = require('electron')
const app = electron.app
const path = require('path')
// const raven = require('raven')
const json = require('./package.json')
// const ravenClient = new raven.Client(json.pubuim.ravenUrl, {
//   logger: 'javascript',
//   maxMessageLength: 1000
// })
const dialog = electron.dialog
const iconPath = path.resolve(__dirname, './res/app.png')
exports.handleError = function (error, extra, isShowError) {
   console.log('err', error, extra);
  // if (typeof extra === "boolean") {
  //   isShowError = extra
  //   extra = {}
  // }
  //
  // // Handle the error
  // try {
  //   ravenClient.captureError(error, {
  //     extra: extra || {},
  //     tags: {
  //       version: json.version
  //     }
  //   })
  // } catch (err) {
  //   console.log('Raven error', err)
  // }
  //
  // if (isShowError) exports.showError(error)
}

// exports.showError = function (error) {
//   dialog.showErrorBox('应用出了点问题，我们会尽快解决', [
//     error.toString(),
//     "\n",
//     "如果影响到您使用请尽快联系我们",
//     "微博: rongcloudim",
//     "微信: rongcloudim",
//     "邮箱: support@rongcloud.cn"
//   ].join("\n"))
// }

exports.showError = function (error) {
  dialog.showErrorBox(app.__('main.UncaughtException.Title'), [
    error.toString(),
    "\n",
    app.__('main.UncaughtException.Content'),
    app.__('main.UncaughtException.Website') + ": http://www.rongcloud.cn",
    app.__('main.UncaughtException.Email') + ": support@rongcloud.cn"
  ].join("\n"))
}

exports.showMessage = function (type, message, title, detail) {
  dialog.showMessageBox({
    type: type,
    buttons: ['OK'],
    icon: iconPath,
    message: message,
    title: title,
    detail: detail
  })
}


exports.getNameByUrl = function (field, url) {
    var href = url ? url : window.location.href;
    var reg = new RegExp( '[?&]' + field + '=([^&#]*)', 'i' );
    var string = reg.exec(href);
    return string ? decodeURIComponent(decodeURIComponent(string[1])) : null;
}

exports.getDirByUrl = function (url) {
    console.log('getDirByUrl');
  var re = /([\w\d_-]*)\.?[^\\\/]*$/i;
    console.log(url.match(re)[1]);
  return url.match(re)[1];
}

exports.getSavePath = function (url) {
console.log('82',url);
  var fileName = this.getNameByUrl('attname', url);
    console.log('84');
    console.log(fileName);
  var savePath = path.join(this.getDirByUrl(url), fileName);
    console.log('86');
  return savePath;
}
