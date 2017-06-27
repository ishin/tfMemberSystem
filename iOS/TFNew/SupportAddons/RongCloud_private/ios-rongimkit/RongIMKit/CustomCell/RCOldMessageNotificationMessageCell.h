//
//  RCOldMessageNotificationMessageCell.h
//  RongIMKit
//
//  Created by 杜立召 on 15/8/24.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCMessageBaseCell.h"

@interface RCOldMessageNotificationMessageCell : RCMessageBaseCell

/**
 *  tipMessage显示Label
 */
@property(strong, nonatomic) RCTipLabel *tipMessageLabel;

/**
 *  设置消息数据模型
 *
 *  @param model 消息数据模型
 */
- (void)setDataModel:(RCMessageModel *)model;

@end
