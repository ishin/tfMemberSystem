//
//  RCPublicServiceProfileRcvdMsgCell.h
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMLib/RongIMLib.h>
@interface RCPublicServiceProfileRcvdMsgCell : UITableViewCell
@property(nonatomic, strong) RCPublicServiceProfile *serviceProfile;
- (void)setTitleText:(NSString *)title;
- (void)setOn:(BOOL)enableNotification;
@end
