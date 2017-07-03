//
//  RCCSLeaveMsgCell.h
//  RongIMKit
//
//  Created by 张改红 on 2016/12/7.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RongIMKit.h"
#import "RCAttributedLabel.h"
@interface RCCSPullLeaveMessageCell : RCMessageBaseCell
@property (nonatomic,strong) RCAttributedLabel *contentLabel;
- (void)setDataModel:(RCMessageModel *)model;
@end
