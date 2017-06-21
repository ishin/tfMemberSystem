# sealtalk-desktop

## Support OS

We do support Windows, Mac OS X

## Supported Languages

+ 简体中文
+ ... keep adding :)

## Setup Environment

Because we use npm to maintain our third party libraries, you have to make sure before doing anything, these needed stuffs are all installed already.

```
  npm install
  electron main.js --disable-native  或者   npm start
```
- 特别说明

  a. 运行前务必认真读取 [PC端IM打包说明](http://web.hitalk.im/docs/desktop-build-introduction.html)
  
  b. 运行前务必将 config.js 中配置参数修改,REPORT_URL: crash report 地址, APP_ONLINE: 网站地址
  
  c. OS X 打包前需要安装签名文件,并正确配置 script/codesign.bash 中签名参数,参考 [https://pracucci.com/atom-electron-signing-mac-app.html](https://pracucci.com/atom-electron-signing-mac-app.html)

- 打包

    Mac

    ```
    gulp build -p mac
    ```
    Windows

    ```
    gulp build -p win32
    ```

- 制作安装包:

    Mac

    ```
    npm run installer:mac
    ```
    Windows
    
        打开 inno setup 项目文件,编译制作安装包.

        也可参照 [PC端IM打包说明](http://web.hitalk.im/docs/desktop-build-introduction.html) 中第 7 步操作.

- 发布(打包+签名+安装包):

    Mac

    ```
    npm run release:mac
    ```
    ```
