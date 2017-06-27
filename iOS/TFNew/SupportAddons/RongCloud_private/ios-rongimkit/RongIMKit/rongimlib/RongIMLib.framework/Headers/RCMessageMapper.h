//
//  RCMessageMapper.h
//  RongIM
//
//  Created by Gang Li on 10/15/14.
//  Copyright (c) 2014 RongCloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCMessageContent.h"
#import <UIKit/UIKit.h>
@interface RCMessageMapper : NSObject

+(instancetype)sharedMapper;
-(BOOL)registerMessageType:(Class)messageClass identifier:(NSString *)identifier;
-(NSString *)messageTypeIdentifierForClass:(Class)messageClass;
-(RCMessageContent *)messageContentWithTypeIdentifier:(NSString *) messageTypeIdenfigier fromData:(NSData *)jsonData;
-(Class)messageClassWithTypeIdenfifier:(NSString *)identifier;

@end

