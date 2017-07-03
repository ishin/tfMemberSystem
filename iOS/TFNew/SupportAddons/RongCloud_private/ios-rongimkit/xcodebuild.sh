#!/bin/sh

BIN_DIR="bin"
if [ ! -d "$BIN_DIR" ]; then
mkdir -p "$BIN_DIR"
fi

BIN_DIR_TMP="bin_tmp"
if [ ! -d "$BIN_DIR_TMP" ]; then
mkdir -p "$BIN_DIR_TMP"
fi

DST_DIR="./../ios-rongimdemo/framework"

if [ ! -d "$DST_DIR" ]; then
mkdir -p "$DST_DIR"
fi

DST_DIR2="./../ios-rongcallkit/framework"
if [ ! -d "$DST_DIR2" ]; then
mkdir -p "$DST_DIR2"
fi

# copy resource to chatroom demo
DST_DIR3="./../ios-rongChatroomDemo/framework"
if [ ! -d "$DST_DIR3" ]; then
mkdir -p "$DST_DIR3"
fi

DST_DIR4="./../../ios-quick-start-demo/CustomerServiceDemo/framework"
if [ ! -d "$DST_DIR4" ]; then
mkdir -p "$DST_DIR4"
fi

DST_DIR5="./../ios-hybrid/apicloud/development/UZApp/RongCloudLib"
if [ ! -d "$DST_DIR5" ]; then
mkdir -p "$DST_DIR5"
fi

DST_DIR6="./../ios-hybrid/apicloud/module/RongCloud/RongCloudLib"
if [ ! -d "$DST_DIR6" ]; then
mkdir -p "$DST_DIR6"
fi

DST_DIR7="./../ios-hybrid/cordova/Plugin/src/ios"
if [ ! -d "$DST_DIR7" ]; then
mkdir -p "$DST_DIR7"
fi

DST_DIR8="./../ios-hybrid/cordova/CordovaDemo/platforms/ios/CordovaDemo/Plugins/cn.rongcloud.imlib"
if [ ! -d "$DST_DIR8" ]; then
mkdir -p "$DST_DIR8"
fi

DST_DIR9="./../ios-rongiflykit/framework"
if [ ! -d "$DST_DIR9" ]; then
mkdir -p "$DST_DIR9"
fi

DST_DIR10="./../ios-rongpttkit/framework"
if [ ! -d "$DST_DIR10" ]; then
mkdir -p "$DST_DIR10"
fi


cp -af ./${TARGET_NAME}/Resource/RongCloud.bundle ${BIN_DIR}/
cp -af ./ExtensionKit/Resource/RongExtensionKit.bundle/* ${BIN_DIR}/RongCloud.bundle/
cp -af ./ExtensionKit/Resource/Emoji.plist ${BIN_DIR}/
cp -af ./${TARGET_NAME}/Resource/en.lproj ${BIN_DIR}/
cp -af ./${TARGET_NAME}/Resource/zh-Hans.lproj ${BIN_DIR}/
cat ./ExtensionKit/Resource/en.lproj/RongExtensionKit.strings >> ${BIN_DIR}/en.lproj/RongCloudKit.strings
cat ./ExtensionKit/Resource/zh-Hans.lproj/RongExtensionKit.strings >> ${BIN_DIR}/zh-Hans.lproj/RongCloudKit.strings


cp -af ${BUILT_PRODUCTS_DIR}/${TARGET_NAME}.framework/ ${BIN_DIR_TMP}/${PLATFORM_NAME}-${TARGET_NAME}.framework
cp -af ${BUILT_PRODUCTS_DIR}/${TARGET_NAME}.framework/ ${BIN_DIR}/${TARGET_NAME}.framework
lipo -create $BIN_DIR_TMP/*-${TARGET_NAME}.framework/${TARGET_NAME} -output ${BIN_DIR}/${TARGET_NAME}.framework/${TARGET_NAME}


cp -af ${BIN_DIR}/* ${DST_DIR}/
cp -af ${BIN_DIR}/* ${DST_DIR2}/
cp -af ${BIN_DIR}/* ${DST_DIR3}/
cp -af ${BIN_DIR}/* ${DST_DIR4}/
cp -af ${BIN_DIR}/* ${DST_DIR5}/
cp -af ${BIN_DIR}/* ${DST_DIR6}/
cp -af ${BIN_DIR}/* ${DST_DIR7}/
cp -af ${BIN_DIR}/* ${DST_DIR8}/
cp -af ${BIN_DIR}/* ${DST_DIR9}/
cp -af ${BIN_DIR}/* ${DST_DIR10}/
