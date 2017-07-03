//
//  RCUnknownMessage.h
//  RongIMLib
//
//  Created by xugang on 15/1/24.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCMessageContent.h"
#define RCUnknownMessageTypeIdentifier             @"RC:UnknownMsg"

/**
 *  未知消息，所有未注册，未实现的消息都会通过未知消息返回
 */
@interface RCUnknownMessage : RCMessageContent


@end
