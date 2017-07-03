//
//  RCConversationHeaderView.m
//  RongIMKit
//
//  Created by 岑裕 on 16/9/15.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCConversationHeaderView.h"
#import "RCIM.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"

@implementation RCConversationHeaderView

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
  self = [super initWithCoder:aDecoder];
  if (self) {
    [self initSubviewsLayout];
  }
  return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    [self initSubviewsLayout];
  }
  return self;
}

- (void)initSubviewsLayout {
  self.translatesAutoresizingMaskIntoConstraints = NO;
  self.backgroundColor = [UIColor clearColor];

  self.backgroundView = [[RCloudImageView alloc] initWithFrame:self.frame];
  self.backgroundView.translatesAutoresizingMaskIntoConstraints = NO;
  self.backgroundView.backgroundColor = [UIColor clearColor];
  [self addSubview:self.backgroundView];
  
  self.headerImageView = [[RCloudImageView alloc] initWithFrame:self.frame];
  self.headerImageView.translatesAutoresizingMaskIntoConstraints = NO;
  self.headerImageView.layer.cornerRadius = 4;
  self.headerImageView.layer.masksToBounds = YES;
  self.headerImageView.image = nil;
  self.headerImageView.placeholderImage = IMAGE_BY_NAMED(@"default_portrait");
  self.headerImageView.userInteractionEnabled = YES;
  [self.backgroundView addSubview:self.headerImageView];
  self.headerImageStyle = [RCIM sharedRCIM].globalConversationAvatarStyle;

  self.bubbleView = [[RCMessageBubbleTipView alloc]
      initWithParentView:self
               alignment:RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_RIGHT];
  self.bubbleView.bubbleTipBackgroundColor = HEXCOLOR(0xf43530);
  
  [self
   addConstraints:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"V:|[_backgroundView]|"
    options:0
    metrics:nil
    views:NSDictionaryOfVariableBindings(_backgroundView)]];
  [self
   addConstraints:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"H:|[_backgroundView]|"
    options:0
    metrics:nil
    views:NSDictionaryOfVariableBindings(_backgroundView)]];
  
  [self.backgroundView
   addConstraints:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"V:|[_headerImageView]|"
    options:0
    metrics:nil
    views:NSDictionaryOfVariableBindings(_headerImageView)]];
  [self.backgroundView
   addConstraints:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"H:|[_headerImageView]|"
    options:0
    metrics:nil
    views:NSDictionaryOfVariableBindings(_headerImageView)]];
}
- (void)setHeaderImageStyle:(RCUserAvatarStyle)headerImageStyle {
  _headerImageStyle = headerImageStyle;
  if (_headerImageStyle == RC_USER_AVATAR_RECTANGLE) {
    self.headerImageView.layer.cornerRadius = [[RCIM sharedRCIM] portraitImageViewCornerRadius];
  } else if (_headerImageStyle == RC_USER_AVATAR_CYCLE) {
    self.headerImageView.layer.cornerRadius =
        [[RCIM sharedRCIM] globalConversationPortraitSize].height / 2;
  }
}

- (void)updateBubbleUnreadNumber:(int)unreadNumber {
  [self.bubbleView setBubbleTipNumber:unreadNumber];
}

- (void)resetDefaultLayout:(RCConversationModel *)reuseModel {
  [self.headerImageView setPlaceholderImage:[RCKitUtility defaultConversationHeaderImage:reuseModel]];
  self.bubbleView.isShowNotificationNumber = YES;
}
@end
