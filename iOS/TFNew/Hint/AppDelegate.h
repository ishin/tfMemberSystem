//
//  AppDelegate.h
//  Hint
//
//  Created by jack on 1/16/17.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMKit/RongIMKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

- (void) didLogin;
- (void) didLogout;
- (void)getUserInfoWithUserId:(NSString *)userId completion:(void (^)(RCUserInfo *userInfo))completion;
- (void)getGroupInfoWithUserId:(NSString *)targetId completion:(void (^)(RCUserInfo *userInfo))completion;

- (void) loginRongCloud;

- (void) autoLogin;

- (void) switchLogin;
- (void) skipLogin;

- (void) checkXFQ;
- (void) pushToChat:(NSString*)targetId type:(int)type;
- (void) switchAtTabIndex:(int)index;
@end

