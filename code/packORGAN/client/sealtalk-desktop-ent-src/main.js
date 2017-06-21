'use strict'
// 自动更新
// if (require('electron-squirrel-startup')) return
const electron = require('electron')
const app = electron.app
const BrowserWindow = electron.BrowserWindow
const globalShortcut = electron.globalShortcut
const ipcMain = electron.ipcMain
const Menu = electron.Menu
const MenuItem = electron.MenuItem
const shell = electron.shell
const Tray = electron.Tray
const dialog = electron.dialog
const clipboard = electron.clipboard	
const nativeImage = electron.nativeImage
const path = require('path')
const fs = require('fs')
const jsonfile = require('jsonfile')
const i18n = require("i18n")
const initSize = {width: 1280, height:800}
const json = require('./package.json')
const Config = require('./config.js')

// Platform flag.
const platform = {
  OSX: process.platform === 'darwin',
  Windows: process.platform === 'win32',
  Linux: process.platform === 'linux'
}

// 自动更新
// const UpdateController = !platform.Linux ? require('./auto-updater') : null
const Utils = require('./utils')

i18n.configure({
    locales:['en', 'zh-CN'],
    directory: __dirname + '/locales',
    defaultLocale: 'zh-CN',
    objectNotation: true ,
    register: app,
    // syncFiles: true,
    api: {
      '__': 't',
      '__n': 'tn'
    }
});

// A global reference of the window object.
let mainWindow = null
let forceQuit = false
let tray = null
let bounceID = undefined
let blink = null
let updateManager = null
let isManualClose = false
let myScreen = null
// console.log(app.getName())


electron.crashReporter.start({
  productName: Config.PRODUCTNAME,
  companyName: Config.AUTHOR,
  submitURL: `${Config.REPORT_URL}/post`,
  autoSubmit: true
})

// console.log(app)
//console.log(app.getPath('userData'))

// var addon = require('RongIMLib')
// addon.initWithAppkey("n19jmcy59f1q9");
// Only support Windows and OSX

/*if ((platform.Windows || platform.OSX) && process.argv.indexOf('--disable-native') === -1) {
    if (platform.OSX) {
      myScreen = require('nodobjc')
      // 以下当asar为true时用
      // var modulePath = app.getName() == 'Electron' ? './node_modules/screenshot.framework' : app.getAppPath() + '.unpacked/node_modules/screenshot.framework'
      // 以下当asar为false时用
      var modulePath = app.getName() == 'Electron' ? './node_modules/screenshot.framework' : app.getAppPath() + '/node_modules/screenshot.framework'

      myScreen.import(modulePath);
    }

    if (platform.Windows) {
      myScreen = require('screenshot')
    }
}*/


if (platform.Windows) {
  app.setAppUserModelId(Config.WIN.APP_USER_MODEL_ID)
}

process.on('error', function(err) {
  console.log(err);
});

// Set forceQuit flag when quit.
app.on('before-quit', () => {
  forceQuit = true
})

// Show main window when activate app icon.
app.on('activate', () => {
  if (mainWindow) {
     mainWindow.show()
  }
})
// app.setAsDefaultProtocolClient(Config.PROTOCAL)

var urlParam;
app.on('open-url', function(event, url) {
    event.preventDefault();
    urlParam = url;

});

// Ready to create browser window.
app.on('ready', () => {

  const screen = electron.screen
  let workAreaSize = screen.getPrimaryDisplay().workAreaSize
  let savedBounds = loadWindowBounds()
  let downloadSavePath = app.getPath('downloads') + '/' + Config.AUTHOR;
  //console.log('+++++++++++++++++');
  //console.log(downloadSavePath);

  // Create the browser window.
  mainWindow = new BrowserWindow(
    {
      x: (workAreaSize.width - initSize.width)/2,
      y: (workAreaSize.height - initSize.height)/2,
      //x: (workAreaSize.width - initSize.width)/2,
      //y: (workAreaSize.height - initSize.height)/2,
      //width: savedBounds.width || initSize.width,
      //height: savedBounds.height || initSize.height,
      minWidth: 500,
      minHeight: 500,
	  width:1280,height:800,
      titleBarStyle: 'hidden',
      icon: path.join(__dirname, 'res', Config.WINICON),
      title: app.getName(),
      show: false,
      'webPreferences': {
        preload: path.join(__dirname, 'js', 'preload.js'),
        nodeIntegration: false,
        allowDisplayingInsecureContent: true,
        // webSecurity: false,
        plugins: true
      }
    })


    // mainWindow.once('ready-to-show', () => {
    //   mainWindow.show()
    // })

  mainWindow.webContents.session.on('will-download', (event, item, webContents) => {
    let _url = item.getURL();
    var finalURL = Utils.getSavePath(_url);
    if(finalURL.split('\\').length>=3){
      var newfinal = finalURL.split('\\');
      newfinal.shift();
      finalURL = newfinal.join('\\');
    }
    let savePath = path.join(downloadSavePath, finalURL);
    //console.log(event,item);
    // var rightNow = new Date();
    // var folderDate = rightNow.toISOString().slice(0,10).replace(/-/g,"");

    const totalBytes = item.getTotalBytes();
    item.setSavePath(savePath);

    item.on('updated', (event, state) => {

        mainWindow.setProgressBar(item.getReceivedBytes() / totalBytes);

        mainWindow.webContents.send('chDownloadProgress', _url, state, item.getReceivedBytes()/item.getTotalBytes() * 100)

        if (state === 'interrupted') {
          alert('文件上传终止')
          console.log('Download is interrupted but can be resumed')
        } else if (state === 'progressing') {
          if (item.isPaused()) {
            alert('文件上传暂停')
            console.log('Download is paused')
          } else {
            // console.log(`Received bytes: ${item.getReceivedBytes()}`)
          }
        }
    })
    item.once('done', (event, state) => {
      if (!mainWindow.isDestroyed()) {
           mainWindow.setProgressBar(-1);
           mainWindow.webContents.send('chDownloadState', _url, state)
      }

      if (state === 'completed') {
        // console.log('Download successfully')
        // console.log(`getSavePaths: ${item.getSavePath()}`);  //这里可以得到另存为的路径
        //shell.openItem(savePath);
      } else {
        console.log(`Download failed: ${state}`)
      }
    })
  })

  mainWindow.loadURL(Config.APP_ONLINE + '?r=' + Math.random())

  // mainWindow.loadURL('https://qgy18.com/download/')

  // shell.openItem('/Users/zy/Downloads/捕捉儿童敏感期.pdf');
  // shell.showItemInFolder('/Users/zy/Downloads/捕捉儿童敏感期.pdf');
  // shell.openExternal("mailto:zhengyi@rongcloud.cn")
// mainWindow.loadURL('http://wslmac.58.com:9028/index.html?r=' + Math.random())

  // mainWindow.loadURL("file://" + path.join(__dirname, 'index.html'));
  // mainWindow.loadURL(Config.APP_ONLINE, {"extraHeaders" : "pragma: no-cache\n"})
  // mainWindow.loadURL("http://localhost:8010")
  // mainWindow.loadURL(Config.APP_ONLINE + '?r=' + Math.random())

  // Hide window when the window will be closed otherwise quit app.
  mainWindow.on('close', (event) => {
    if (mainWindow.isFullScreen()) {
      return
    }

    if (!forceQuit) {
      event.preventDefault()
      // mainWindow.blurWebView()
      mainWindow.hide()
    }

    if (forceQuit) {
      if(blink){
         clearInterval(blink)
      }
      if(platform.Windows && myScreen){
        myScreen.exit_shot();
      }
      if(mainWindow){
        mainWindow.webContents.send('lougout')
      }
    }
    // Save window bounds info when closing.
    saveWindowBounds()
  })

  // Dereference the window object when the window is closed.
  mainWindow.on('closed', () => {
    mainWindow.removeAllListeners()
    mainWindow = null
  })

  ipcMain.on('unread-message-count-changed', (event, arg) => {
    let number = parseInt(arg, 10)
    let iconFile
    number = isNaN(number) ? 0 : number

    if (platform.OSX) {
      setBadge(number)
    }
    else if (platform.Windows){
      setTray(number)
    }
  })

  ipcMain.on('logRequest', (event) => {
    event.sender.send('logOutput', urlParam)
  })

  ipcMain.on('notification-click', () => {
    if (mainWindow) {
  //console.log(222);
       mainWindow.show()
    }
  })

  //该方法暂停用
  ipcMain.on('kicked-off', () => {
      console.log('kicked-off')
      if (platform.OSX){
          bounceID = app.dock.bounce('informational')

      } else if (platform.Windows){
        var options = {
            icon: path.join(__dirname, 'res/Windows_icon.png'),
            title: "Basic Notification ad",
            content: "hah adfasfd"
        }
        tray.displayBalloon(options)
          // tray.displayBalloon(path.join(__dirname, 'res/Windows_Remind_icon.png'), 'SealTalk信息提示','您的账号在其他地方登陆!')
      }
  })

  ipcMain.on('webQuit', () => {
    if (platform.OSX){
         setBadge(0)
         tray.setImage(path.join(__dirname, 'res', 'Mac_Template.png'))
    } else if (platform.Windows){
         setTray(0)
    }
  })

  ipcMain.on('screenShot', () => {
      takeScreenshot();
  })

  ipcMain.on('displayBalloon', (event, title, opt) => {
    displayBalloon(title, opt.body)
    tray.on('balloon-click', () => {
      if (mainWindow) {
         mainWindow.show()
         mainWindow.webContents.send('balloon-click', opt)
      }
    })
  })

  ipcMain.on('ondragstart', (event, filePath) => {
    event.sender.startDrag({
      file: filePath,
      icon: path.join(__dirname, 'res', 'Mac_Remind_icon.png')
    })
  })

  const webContents = mainWindow.webContents

  webContents.on('did-finish-load', function() {
    mainWindow.show();
  });
  //
  webContents.on('did-fail-load', function() {
    // console.log('>>>>>>>>>>>>>>>>>>>page failed');
    // mainWindow.loadURL(Config.APP_ONLINE)
    //reload();
  });

  webContents.on('new-window', (event, url) => {
    event.preventDefault()
    shell.openExternal(url)
  })

  /* 开启后，将进行页面无法跳转
  // Prevent load a new page when accident.
  webContents.on('will-navigate', (event, url) => {
    event.preventDefault()
  })
   */
   
  // Injects CSS into the current web page.
  webContents.on('dom-ready', () => {
    webContents.insertCSS(fs.readFileSync(path.join(__dirname, 'res', 'browser.css'), 'utf8'))
    webContents.executeJavaScript(fs.readFileSync(path.join(__dirname, 'js', 'postload.js'), 'utf8'))

/*    if (!UpdateController || updateManager) return
    updateManager = new UpdateController(app.getVersion())

    // Show dialog to install
    updateManager.on('update-downloaded', function (releaseInfo) {
      let ret = dialog.showMessageBox(mainWindow, {
        type: 'info',
        buttons: ['取消', '安装并重启'],
        icon: path.join(__dirname, 'res/app.png'),
        message: '发现更新：' + releaseInfo.releaseVersion,
        title: '应用更新',
        detail: releaseInfo.releaseNotes || ''
      })
      if (ret === 1) {
        // isManualClose = true
        forceQuit = true
        updateManager.install()
      }
    })

    updateManager.on('state-changed', function (state) {
      if (platform.Windows) {
        displayBalloon('自动更新中...', state)
      }
   })*/

  })

  bindGlobalShortcuts()
  initMenu()
  initTray()

  // process.crash()
})

// Open account settings panel on account settings menu item selected.
app.on('menu.main.account_settings', () => {
  if (mainWindow) {
    mainWindow.show()
    mainWindow.webContents.send('menu.main.account_settings')
  }
})

// Set language.
app.on('menu.view.languages', (lang) => {
  // mainWindow.loadURL('https://web.hitalk.im/?lang=' + lang)
  i18n.setLocale(lang)
  initMenu()
})

// Focus on search input element.
app.on('menu.edit.search', () => {
  // mainWindow.webContents.send('main', 'menu.edit.search')
  mainWindow.webContents.send('menu.edit.search')
})

// Reload page on reload menu item selected.
app.on('menu.edit.reload', () => {
  if (mainWindow) {
    mainWindow.show()
    mainWindow.webContents.reloadIgnoringCache()
  }
})

// Open homepage on homeplage menu item selected.
app.on('menu.help.homepage', () => {
  shell.openExternal(Config.HOME)
})


// 自动更新
// app.on('menu.checkUpdate', function () {
//   if (!updateManager) return
//   if(updateManager.state == "downloading"){
//     Utils.showMessage('info', '正在更新', '正在更新', "当前更新状态: 正在下载中")
//     return
//   }
//   if (updateManager.state !== 'idle' && updateManager.state !== 'no-update-available') return
//   updateManager.check()
// })


app.on('menu.edit.takeScreenshot', function () {
  takeScreenshot()
})

app.on('browser-window-blur', () => {
  globalShortcut.unregisterAll()
})

app.on('browser-window-focus', () => {
  if (platform.OSX) {
    setBadge(0)
  }
  else if (platform.Windows){
    setTray(0)
  }
  bindGlobalShortcuts();
})

//let shouldQuit = app.makeSingleInstance((argv, workingDirectory) => {
//  // Someone tried to run a second instance, we should focus our window
//  //if (mainWindow) {
//  //  mainWindow.show()
//  //}
//  return true
//})

//if (shouldQuit) {
//  app.quit()
//}

function setBadge (unreadCount) {
  let text

  if (unreadCount < 1) {
    text = ''
  } else if (unreadCount > 99) {
    text = '99+'
  } else {
    text = unreadCount.toString()
  }

  app.dock.setBadge(text)
  tray.setTitle(text == '' ? '' : text)
}

// Set tray icon on Windows.闪烁
function setTray (unreadCount) {
  let iconFile = [Config.WIN.TRAY_OFF,Config.WIN.TRAY]
  let flag

  if(unreadCount > 0){
    if(!blink){
      blink = setInterval(function
          (){
        flag = !flag
        tray.setImage(path.join(__dirname, 'res', iconFile[flag ? 1 : 0]))
      },500)
    }
  }
  else{
     if(blink){
        clearInterval(blink)
     }
     blink = null
     tray.setImage(path.join(__dirname, 'res', iconFile[1]))
  }
}

// Initialize menu.
function initMenu () {
  let menuTemplate

  if (platform.OSX) {
    menuTemplate = require('./js/menu_osx')(i18n.getLocale())
    const menu = Menu.buildFromTemplate(menuTemplate)

    Menu.setApplicationMenu(menu)
  }
  else if (platform.Windows) {
    // menuTemplate = require('./js/menu_win')
  }
}

// Initialize tray icon on Windows.
function initTray () {
  let iconFile = platform.OSX ? Config.MAC.TRAY : Config.WIN.TRAY

  tray = new Tray(path.join(__dirname, 'res', iconFile))
  tray.setToolTip('SealTalk')
  tray.on('click', () => {
    if (mainWindow) {
       mainWindow.show()
    }
  })

  // tray.on('balloon-click', () => {
  //   if (mainWindow) {
  //      mainWindow.show()
  //      mainWindow.webContents.send('balloon-click')
  //   }
  // })

  if (platform.Windows) {
    const contextMenu = Menu.buildFromTemplate([
      {
        label: app.__('winTrayMenus.Open'),
        click () {
          if (mainWindow) {
            mainWindow.show()
          }
        }
      },
      //{
      //  label: app.__('winTrayMenus.CheckUpdate'),
      //  click () {
      //    app.emit('menu.checkUpdate')
      //  }
      //},
      {
        type: 'separator'
      }, {
        label: app.__('winTrayMenus.Exit'),
        click () {
          app.quit()
        }
      }
    ])
    tray.setContextMenu(contextMenu)
  }

  if (platform.OSX) {
     tray.setPressedImage(path.join(__dirname, 'res', Config.MAC.PRESSEDIMAGE))
  }
}

// Save window bounds info to setting file.
function saveWindowBounds () {
  let bounds = mainWindow.getBounds()
  jsonfile.writeFile(path.join(app.getPath('userData'), 'settings.json'), bounds)
}

// Load window bounds info from setting file. create an empty file when not exist
function loadWindowBounds () {
  let bounds = null
  let src = path.join(__dirname, 'settings.json')
  let dest = path.join(app.getPath('userData'), 'settings.json')

  try{
    if(fileExists(dest)){
      try{
         bounds = jsonfile.readFileSync(dest)
      }
      catch(err){
        bounds = {"x": 0, "y": 0, "width": 0, "height": 0}
      }
    }
    else{
      // console.log('not exist')
      bounds = {"x": 0, "y": 0, "width": 0, "height": 0}
      fs.closeSync(fs.openSync(dest, 'w'));
    }
  }
  catch (err){
    Utils.showError(err)
  }
  return bounds
}

function fileExists(filePath)
{
    try
    {
        return fs.statSync(filePath).isFile()
    }
    catch (err)
    {
        return false
    }
}

function toggleDevTools () {
  mainWindow.toggleDevTools()
}

function searchFriend () {
  app.emit('menu.edit.search')
}

function reload () {
  app.emit('menu.edit.reload')
}

function displayBalloon(title, msg) {
  var options = {
      icon: path.join(__dirname, 'res', Config.WIN.BALLOON_ICON),
      title: title,
      content: msg
  }
  tray.displayBalloon(options)
  tray.on('balloon-click', (opt) => {
    if (mainWindow) {
       mainWindow.show()
       mainWindow.webContents.send('balloon-click', opt)
    }
  })

}

process.on('uncaughtException', function (error) {
  // Utils.handleError(error)
  Utils.showError(error)
})

function takeScreenshot() {
   if (!myScreen) return
   try {
  //    myScreen.screenShot(function(self,arg){
  //      if(!arg) return
  //      var str = arg.toString();
  //      str = str.substr(1,str.length-2);
  //      var reg = /\s/g;
  //      str = str.replace(reg, "");
  //      // console.log(str);
  //      var buff = new Buffer(str, 'hex');
   //
  //      clipboard.writeImage(nativeImage.createFromBuffer(buff), "image/png")
  //      if (mainWindow) {
  //        mainWindow.show()
  //        mainWindow.webContents.send('screenshot')
  //      }
  //    });

      if(platform.OSX){
          myScreen.screenshot('screenCapture',myScreen(function(self,arg){
             if(!arg || arg.toString() == '<00>') return
             var str = arg.toString();
             str = str.substr(1,str.length-2);
             var reg = /\s/g;
             str = str.replace(reg, "");
             var buff = new Buffer(str, 'hex');
             // var buff = new Buffer(arg);
             clipboard.clear();
             clipboard.writeImage(nativeImage.createFromBuffer(buff), "image/png")
             // var image = nativeImage.createFromPath('/Users/zy/Desktop/cut.png');
             if (mainWindow) {
               mainWindow.show()
               mainWindow.webContents.send('screenshot')
             }

          },['@',['@','@']]));
      }

      if(platform.Windows){
        // myScreen.screencapture((data) => {
        //    var buff = new Buffer(data);
        //      clipboard.clear()
        //      clipboard.writeImage(nativeImage.createFromBuffer(buff), "image/png")
        //      if (mainWindow) {
        //        mainWindow.show()
        //        mainWindow.webContents.send('screenshot')
        //      }
        //
        // });
        var cp = require('child_process')
    		var n = cp.fork(path.join(__dirname, 'js', 'child.js'))

    		n.on('message', function(data) {
    			 var buff = new Buffer(data);
                 // clipboard.clear()
                 // clipboard.writeImage(nativeImage.createFromBuffer(buff), "image/png")
                 if (mainWindow) {
                   mainWindow.show()
                   mainWindow.webContents.send('screenshot')
                 }
    		});
    		n.send('takeScreenshot');
      }

    // try {
    //   myScreen(function (err, buff) {
    //     if (err || !buff) return
    //     clipboard.writeImage(nativeImage.createFromBuffer(buff), "image/png")
    //     if (mainWindow) {
    //       mainWindow.show()
    //       mainWindow.webContents.send('screenshot')
    //     }
    //   })
    // } catch (error) {
    //   Utils.handleError(error)
    // }

    //  screenshot(function (err, buff) {
    //    if (err || !buff) return
    //    clipboard.writeImage(nativeImage.createFromBuffer(buff), "image/png")
    //    mainWindow.show()
    //    mainWindow.webContents.send('slave', 'screenshot')
    //  })
   } catch (error) {
     Utils.handleError(error)
   }
 }

 function bindGlobalShortcuts(){
   if (platform.OSX) {
     globalShortcut.register('CTRL+CMD+SHIFT+I', toggleDevTools)
     // globalShortcut.register('CTRL+CMD+S', takeScreenshot)
     // globalShortcut.register('CTRL+CMD+S', copyFilesToClipboard(paths))
   } else {
     globalShortcut.register('CTRL+ALT+SHIFT+I', toggleDevTools)
     globalShortcut.register('CTRL+F', searchFriend)
     globalShortcut.register('CTRL+R', reload)
     globalShortcut.register('CTRL+ALT+S', takeScreenshot)
   }
 }

//  var $ = require('nodobjc')

// // var paths = ['/file-path-1', '/file-path-2']
// var paths = ['/Users/zy/Desktop/Web/融云webSdk问题集锦.txt', '/Users/zy/Documents/Homepage.pdf']
// // copyFilesToClipboard(paths);

// function copyFilesToClipboard (paths) {
// console.log('copyFilesToClipboard');
//     $.framework('Foundation')
//     $.framework('AppKit')

//     var pasteboard = $.NSPasteboard('generalPasteboard');
//     var changeCount = pasteboard('clearContents');
//     var filesToCopy = $.NSMutableArray('alloc')('init');

//     paths.forEach(function (image) {
//       var string = $.NSString('stringWithUTF8String', image);
//       filesToCopy('addObject', $.NSURL('alloc')('initFileURLWithPath', string));
//     });

//     pasteboard('writeObjects', filesToCopy);
// };

// TODO: Kicked by other client, alert a notification.
