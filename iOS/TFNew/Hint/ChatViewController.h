//
//  ChatViewController.h
//  WMeeting
//
//  Created by jack on 9/14/16.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMKit/RongIMKit.h>

@interface ChatViewController : RCConversationViewController
{
    int _groupType;
}
@property (nonatomic, strong) NSString *_userId;
@property (nonatomic, strong) NSString *_userName;

@property (nonatomic, strong) id _targetUser;

//0 -- Default
//1 -- Class group
//2 -- Normal group
@property (nonatomic, assign) int _groupType;

@property (nonatomic, assign) BOOL _enterCallUI;

@end
