//
//  RCPTTKitExtensionModule.h
//  RongPTTKit
//
//  Created by Sin on 16/12/26.
//  Copyright © 2016年 Sin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <RongIMKit/RongIMKit.h>

/**
 extension核心类，删除的话会导致无法正常启动ptt功能
 */
@interface RCPTTKitExtensionModule : NSObject<RongIMKitExtensionModule>
+ (instancetype)sharedRCPTTKitExtensionModule;
@end
