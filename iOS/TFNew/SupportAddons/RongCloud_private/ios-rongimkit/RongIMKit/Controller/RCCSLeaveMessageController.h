//
//  RCCSLeaveMessageController.h
//  RongIMKit
//
//  Created by 张改红 on 2016/12/5.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMLib/RongIMLib.h>
#import "RCConversationViewController.h"
@interface RCCSLeaveMessageController : UITableViewController
@property (nonatomic,strong)NSArray <RCCSLeaveMessageItem *> *leaveMessageConfig;
@property (nonatomic,strong)NSString *targetId;
@property (nonatomic,assign)RCConversationType conversationType;
@property (nonatomic,copy) void (^leaveMessageSuccess)();
@end
