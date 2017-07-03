//
//  RongIMUpdater.h
//  RongIMLib
//
//  Created by xugang on 14/12/26.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//
#ifndef __RongIMUpdater
#define __RongIMUpdater
#import <Foundation/Foundation.h>
#import "RCStatusDefine.h"
#import "RCDiscussion.h"

void __getDiscussionRemote(NSString *discussionId, void (^completion)(RCDiscussion* dInfo) , void (^error)(RCErrorCode status));

#endif


