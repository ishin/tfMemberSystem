'use strict'

/*eslint-env browser, node */
/*global Electron:false*/

// Support local cookies
Electron.require('electron-cookies')
delete Electron.require

function setStatus(status) {
  var loadFail = document.querySelector('#load-fail')

  switch (status) {
    case 0:
      loadFail.className = 'hide'
      break
    case -1:
      loadFail.className = ''
      break
  }
}

function setVersion() {
  document.querySelector('#version').innerText = `${Electron.appInfo.name} ${Electron.appInfo.version}`
}

function startApp() {
  if (!navigator.onLine) {
    setStatus(-1)
    return
  }

  setStatus(0)

  var appUrl = Electron.configInfo.APP_ONLINE + '?r=' + Math.random()
  var Utils = Electron.remote.require('./utils')
// console.log('1', appUrl)
  appUrl = 'http://www.baidu.com' + '?r=' + Math.random()
  fetch(appUrl)
    .then(function (resp) {
      setStatus(1)
// console.log('2', resp)
      if (!resp.ok) {
        let extra = {}
        // Object.keys(resp).forEach(key => ['url', 'status', 'statusText', 'headers', 'bodyUsed', 'size', 'ok', 'timeout', 'json', 'text'].includes(key) && (extra[key] = resp[key]))
        // Utils.handleError('Response is not ok', extra)
      }

      window.location = appUrl
      // window.location.href = 'http://www.google.com'
      // console.log('Electron.remote.getCurrentWindow()', Electron.remote.getCurrentWindow())
      // Electron.remote.getCurrentWindow().loadUrl(appUrl)
    })
    .catch(function (err) {
      setStatus(-1)
      console.log('error  dddd ', err)
      Utils.handleError(err)
    })
}

function bootstrap() {
  setVersion()
  document.querySelector('#retry').onclick = startApp
}

bootstrap()
startApp()

if (!navigator.onLine) {
  window.addEventListener('online', startApp)
}
