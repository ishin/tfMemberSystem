//
//  RCMessageModelCell.h
//  RongIMKit
//
//  Created by xugang on 15/2/2.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCMessageBaseCell.h"
#import "RCloudImageView.h"
#import "RCThemeDefine.h"

@interface RCMessageTemplateCell : RCMessageBaseCell

@property (nonatomic, strong) RCloudImageView * userPortraitImageView;
@property (nonatomic, assign, setter = setPortraitStyle:) RCUserAvatarStyle portraitStyle;

- (void) setDataModel:(RCMessageModel *)model;


@end
