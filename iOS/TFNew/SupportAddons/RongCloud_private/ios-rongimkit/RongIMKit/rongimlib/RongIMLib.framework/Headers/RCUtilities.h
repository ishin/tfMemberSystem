//
//  RCUtilities.h
//  RongCloud
//
//  Created by Heq.Shinoda on 14-5-15.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//

#ifndef __RCUtilities
#define __RCUtilities

#import <UIKit/UIKit.h>
#import "RCMessageContent.h"

#define __BASE64( text )        [CommonFunc base64StringFromText:text]
#define __TEXT( base64 )        [CommonFunc textFromBase64String:base64]

@class RCMessageContent;

typedef uint32_t CCAlgorithm;
@interface RCUtilities : NSObject
+ (NSArray *)methodsInClass:(Class)aClass;
+ (NSArray *)iVarsInClass:(Class)aClass;
+ (NSString*)currentSystemTime;
//+ (NSString *)getDeviceModel;

//码值sever对应
//    if ([sDeviceModel isEqual:@"i386"])      return @"Simulator";  //iPhone Simulator
//    if ([sDeviceModel isEqual:@"iPhone1,1"]) return @"iPhone1G";   //iPhone 1G
//    if ([sDeviceModel isEqual:@"iPhone1,2"]) return @"iPhone3G";   //iPhone 3G
//    if ([sDeviceModel isEqual:@"iPhone2,1"]) return @"iPhone4";  //iPhone 3GS
//    if ([sDeviceModel isEqual:@"iPhone3,1"]) return @"iPhone4";  //iPhone 4 - AT&T
//    if ([sDeviceModel isEqual:@"iPhone3,2"]) return @"iPhone4";  //iPhone 4 - Other carrier
//    if ([sDeviceModel isEqual:@"iPhone3,3"]) return @"iPhone4";    //iPhone 4 - Other carrier
//    if ([sDeviceModel isEqual:@"iPhone4,1"]) return @"iPhone4S";   //iPhone 4S
//    if ([sDeviceModel isEqual:@"iPod1,1"])   return @"iPod1stGen"; //iPod Touch 1G
//    if ([sDeviceModel isEqual:@"iPod2,1"])   return @"iPod2ndGen"; //iPod Touch 2G
//    if ([sDeviceModel isEqual:@"iPod3,1"])   return @"iPod3rdGen"; //iPod Touch 3G
//    if ([sDeviceModel isEqual:@"iPod4,1"])   return @"iPod4thGen"; //iPod Touch 4G
//    if ([sDeviceModel isEqual:@"iPad1,1"])   return @"iPadWiFi";   //iPad Wifi
//    if ([sDeviceModel isEqual:@"iPad1,2"])   return @"iPad3G";     //iPad 3G
//    if ([sDeviceModel isEqual:@"iPad2,1"])   return @"iPad2";      //iPad 2 (WiFi)
//    if ([sDeviceModel isEqual:@"iPad2,2"])   return @"iPad2";      //iPad 2 (GSM)
//    if ([sDeviceModel isEqual:@"iPad2,3"])   return @"iPad2";      //iPad 2 (CDMA)
//
//    NSString *aux = [[sDeviceModel componentsSeparatedByString:@","] objectAtIndex:0];
//
//    //If a newer version exist
//    if ([aux rangeOfString:@"iPhone"].location!=NSNotFound) {
//        int version = [[aux stringByReplacingOccurrencesOfString:@"iPhone" withString:@""] intValue];
//        if (version == 3) return @"iPhone4";
//        if (version >= 4) return @"iPhone4s";
//
//    }
//    if ([aux rangeOfString:@"iPod"].location!=NSNotFound) {
//        int version = [[aux stringByReplacingOccurrencesOfString:@"iPod" withString:@""] intValue];
//        if (version >=4) return @"iPod4thGen";
//    }
//    if ([aux rangeOfString:@"iPad"].location!=NSNotFound) {
//        int version = [[aux stringByReplacingOccurrencesOfString:@"iPad" withString:@""] intValue];
//        if (version ==1) return @"iPad3G";
//        if (version >=2) return @"iPad2";
//    }
//    //If none was found, send the original string
//    return sDeviceModel;

//Base64 Encode & Decode
/******************************************************************************
 函数名称 : + (NSData *)dataWithBase64EncodedString:(NSString *)string
 函数描述 : base64格式字符串转换为文本数据
 输入参数 : (NSString *)string
 输出参数 : N/A
 返回参数 : (NSData *)
 备注信息 :
 ******************************************************************************/
+ (NSData *)dataWithBase64EncodedString:(NSString *)string;
/******************************************************************************
 函数名称 : + (NSString *)base64EncodedStringFrom:(NSData *)data
 函数描述 : 文本数据转换为base64格式字符串
 输入参数 : (NSData *)data
 输出参数 : N/A
 返回参数 : (NSString *)
 备注信息 :
 ******************************************************************************/
+ (NSString *)base64EncodedStringFrom:(NSData *)data;
/******************************************************************************
 函数名称 : + (NSData *)DESEncrypt:(NSData *)data WithKey:(NSString *)key
 函数描述 : 文本数据进行DES加密
 输入参数 : (NSData *)data
 (NSString *)key
 输出参数 : N/A
 返回参数 : (NSData *)
 备注信息 : 此函数不可用于过长文本
 ******************************************************************************/
+ (NSData *)DESEncrypt:(NSData *)data WithKey:(NSString *)key;
/******************************************************************************
 函数名称 : + (NSData *)DESEncrypt:(NSData *)data WithKey:(NSString *)key
 函数描述 : 文本数据进行DES解密
 输入参数 : (NSData *)data
 (NSString *)key
 输出参数 : N/A
 返回参数 : (NSData *)
 备注信息 : 此函数不可用于过长文本
 ******************************************************************************/
+ (NSData *)DESDecrypt:(NSData *)data WithKey:(NSString *)key;

+ (NSString *)base64StringFromText:(NSString *)text;
+ (NSString *)textFromBase64String:(NSString *)base64;

//+ (NSString *)obtainLegalUTF8String:(char *)rawstr length:(int)length;

+ (UIImage *)scaleImage:(UIImage *)image toScale:(float)scaleSize;
+ (UIImage *)imageByScalingAndCropSize:(UIImage *)image targetSize:(CGSize)targetSize;
+ (NSData *)compressedImageWithMaxDataLength:(UIImage*)image maxDataLength:(CGFloat)maxDataLength;
+ (NSData *)compressedImageAndScalingSize:(UIImage*)image targetSize:(CGSize)targetSize maxDataLen:(CGFloat)maxDataLen;
+ (NSData *)compressedImageAndScalingSize:(UIImage*)image targetSize:(CGSize)targetSize percent:(CGFloat)percent;

+ (BOOL)excludeBackupKeyForURL:(NSURL *)storageURL;
+ (NSString *)applicationDocumentsDirectory;
+ (NSString *)rongDocumentsDirectory;
+ (NSString *)rongImageCacheDirectory;

/**
 *  获取当前运营商名称
 *
 *  @return 当前运营商名称
 */
+ (NSString*) currentCarrier;

/**
 *  获取当前网络类型
 *
 *  @return 当前网络类型
 */
+ (NSString *) currentNetWork;

/**
 *  获取系统版本
 *
 *  @return 系统版本
 */
+ (NSString *) currentSystemVersion;

/**
 *  获取设备型号
 *
 *  @return 设备型号
 */
+ (NSString *) currentDeviceModel;

@end
#endif