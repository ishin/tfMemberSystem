//
//  RCConversationCell.m
//  RongIMKit
//
//  Created by xugang on 15/1/24.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCConversationCell.h"
#import "RCConversationCellUpdateInfo.h"
#import "RCConversationDetailContentView.h"
#import "RCConversationHeaderView.h"
#import "RCConversationStatusView.h"
#import "RCIM.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import "RCUserInfoCacheManager.h"
#import "RCloudImageView.h"
#import <RongIMLib/RongIMLib.h>

@interface RCConversationCell ()

@property(nonatomic, strong) RCConversationHeaderView *headerView;
@property(nonatomic, strong) RCConversationDetailContentView *detailContentView;
@property(nonatomic, strong) RCConversationStatusView *statusView;

@end

@implementation RCConversationCell

#pragma mark - 初始化
- (instancetype)initWithStyle:(UITableViewCellStyle)style
              reuseIdentifier:(NSString *)reuseIdentifier {
  self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
  if (self) {
    self.enableNotification = YES;
    [self initCellLayout];
    [self registerObserver];
  }
  return self;
}

- (void)initCellLayout {
  self.selectionStyle = UITableViewCellSelectionStyleNone;
  self.selectedBackgroundView = [[UIView alloc] initWithFrame:self.frame];
  self.selectedBackgroundView.backgroundColor = HEXCOLOR(0xf5f5f5);

  self.headerView = [[RCConversationHeaderView alloc]
      initWithFrame:CGRectMake(0, 0, [RCIM sharedRCIM].globalConversationPortraitSize.width,
                               [RCIM sharedRCIM].globalConversationPortraitSize.height)];
  [self.headerView
     addGestureRecognizer:[[UILongPressGestureRecognizer alloc]
                           initWithTarget:self
                           action:@selector(headerImageDidLongPress)]];
  [self.headerView addGestureRecognizer:[[UITapGestureRecognizer alloc]
                                                initWithTarget:self
                                                action:@selector(headerImageDidTap)]];
  [self.contentView addSubview:self.headerView];

  self.conversationTitle = [[UILabel alloc] init];
  self.conversationTitle.translatesAutoresizingMaskIntoConstraints = NO;
  self.conversationTitle.backgroundColor = [UIColor clearColor];
  self.conversationTitle.font = [UIFont systemFontOfSize:16];
  self.conversationTitle.textColor = HEXCOLOR(0x000000);
  [self.contentView addSubview:self.conversationTitle];

  self.messageCreatedTimeLabel = [[UILabel alloc] init];
  self.messageCreatedTimeLabel.translatesAutoresizingMaskIntoConstraints = NO;
  self.messageCreatedTimeLabel.backgroundColor = [UIColor clearColor];
  self.messageCreatedTimeLabel.font = [UIFont systemFontOfSize:14];
  self.messageCreatedTimeLabel.textColor = HEXCOLOR(0x999999);
  ;
  self.messageCreatedTimeLabel.textAlignment = NSTextAlignmentRight;
  [self.contentView addSubview:self.messageCreatedTimeLabel];

  self.detailContentView = [[RCConversationDetailContentView alloc] init];
  [self.contentView addSubview:self.detailContentView];

  self.statusView = [[RCConversationStatusView alloc] init];
  [self.contentView addSubview:self.statusView];

  NSDictionary *cellSubViews = NSDictionaryOfVariableBindings(
      _headerView, _conversationTitle, _messageCreatedTimeLabel, _detailContentView, _statusView);
  [self.contentView
      addConstraints:[NSLayoutConstraint
                         constraintsWithVisualFormat:@"H:|-10-[_headerView(width)]-9.5-"
                                                     @"[_conversationTitle]-5-"
                                                     @"[_messageCreatedTimeLabel(==100)]-9-|"
                                             options:0
                                             metrics:@{
                                               @"width" :
                                                   @([RCIM sharedRCIM]
                                                         .globalConversationPortraitSize.width)
                                             }
                                               views:cellSubViews]];
  [self.contentView
      addConstraints:[NSLayoutConstraint
                         constraintsWithVisualFormat:@"V:[_headerView(height)]"
                                             options:0
                                             metrics:@{
                                               @"height" :
                                                   @([RCIM sharedRCIM]
                                                         .globalConversationPortraitSize.height)
                                             }
                                               views:cellSubViews]];
  [self.contentView
      addConstraints:
          [NSLayoutConstraint
              constraintsWithVisualFormat:@"V:|-11-[_conversationTitle]-(>=0)-[_detailContentView]-11-|"
                                  options:0
                                  metrics:nil
                                    views:cellSubViews]];
  [self.contentView
      addConstraints:[NSLayoutConstraint
                         constraintsWithVisualFormat:@"V:|-9-[_messageCreatedTimeLabel]"
                                             options:0
                                             metrics:nil
                                               views:cellSubViews]];
  [self.contentView
      addConstraints:[NSLayoutConstraint
                         constraintsWithVisualFormat:
                             @"H:[_headerView]-9.5-[_detailContentView]-(>=0)-[_statusView(55)]-5-|"
                                             options:0
                                             metrics:nil
                                               views:cellSubViews]];
  [self.contentView addConstraint:[NSLayoutConstraint constraintWithItem:self.statusView
                                              attribute:NSLayoutAttributeCenterY
                                              relatedBy:NSLayoutRelationEqual
                                                 toItem:self.detailContentView
                                              attribute:NSLayoutAttributeCenterY
                                             multiplier:1
                                               constant:0]];
  
  [self.contentView addConstraint:[NSLayoutConstraint constraintWithItem:_headerView
                                                               attribute:NSLayoutAttributeCenterY
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:self.contentView
                                                               attribute:NSLayoutAttributeCenterY
                                                              multiplier:1
                                                                constant:0]];

  [self setNeedsUpdateConstraints];
}

- (void)registerObserver {
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(onUserInfoUpdate:)
                                               name:RCKitDispatchUserInfoUpdateNotification
                                             object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(onGroupUserInfoUpdate:)
                                               name:RCKitDispatchGroupUserInfoUpdateNotification
                                             object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(onGroupInfoUpdate:)
                                               name:RCKitDispatchGroupInfoUpdateNotification
                                             object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(updateCellIfNeed:)
                                               name:RCKitConversationCellUpdateNotification
                                             object:nil];
}

- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Model处理&显示
- (void)setDataModel:(RCConversationModel *)model {
  [super setDataModel:model];
  
  [self resetDefaultLayout:model];

  if (self.model.isTop) {
    self.contentView.backgroundColor = self.topCellBackgroundColor;
  } else {
    self.contentView.backgroundColor = self.cellBackgroundColor;
  }

  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_NORMAL) {
    if (model.conversationType == ConversationType_PRIVATE ||
        model.conversationType == ConversationType_CUSTOMERSERVICE ||
        model.conversationType == ConversationType_SYSTEM) {
      RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:model.targetId];
      if (userInfo) {
        self.headerView.headerImageView.imageURL = [NSURL URLWithString:userInfo.portraitUri];
        [self updateConversationTitle:userInfo.name];
      }
      [self.detailContentView updateContent:model prefixName:nil];
    } else if (model.conversationType == ConversationType_GROUP) {
      RCGroup *groupInfo = [[RCUserInfoCacheManager sharedManager] getGroupInfo:model.targetId];
      if (groupInfo) {
        self.headerView.headerImageView.imageURL = [NSURL URLWithString:groupInfo.portraitUri];
        [self updateConversationTitle:groupInfo.groupName];
      }

      if (self.hideSenderName) {
        [self.detailContentView updateContent:model prefixName:nil];
      } else {
        RCUserInfo *userInfo =
            [[RCUserInfoCacheManager sharedManager] getUserInfo:model.senderUserId
                                                      inGroupId:model.targetId];
        [self.detailContentView updateContent:model prefixName:userInfo.name];
      }
    } else if (model.conversationType == ConversationType_DISCUSSION) {
      [self
          updateConversationTitle:NSLocalizedStringFromTable(@"DISCUSSION", @"RongCloudKit", nil)];
      [[RCIMClient sharedRCIMClient]
          getDiscussion:model.targetId
                success:^(RCDiscussion *discussion) {
                  dispatch_async(dispatch_get_main_queue(), ^{
                    if ([model isEqual:self.model] && discussion) {
                      [self updateConversationTitle:discussion.discussionName];
                    }
                  });
                }
                  error:nil];

      if (self.hideSenderName) {
        [self.detailContentView updateContent:model prefixName:nil];
      } else {
        RCUserInfo *userInfo =
            [[RCUserInfoCacheManager sharedManager] getUserInfo:model.senderUserId
                                                      inGroupId:model.targetId];
        [self.detailContentView updateContent:model prefixName:userInfo.name];
      }
    }
  } else if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
    [self
        updateConversationTitle:[RCKitUtility
                                    defaultTitleForCollectionConversation:model.conversationType]];

    if (model.conversationType == ConversationType_PRIVATE ||
        model.conversationType == ConversationType_CUSTOMERSERVICE ||
        model.conversationType == ConversationType_SYSTEM) {
      RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:model.targetId];
      [self.detailContentView updateContent:model prefixName:userInfo.name];
    } else if (model.conversationType == ConversationType_GROUP) {
      RCGroup *group = [[RCUserInfoCacheManager sharedManager] getGroupInfo:model.targetId];
      [self.detailContentView updateContent:model prefixName:group.groupName];
    } else if (model.conversationType == ConversationType_DISCUSSION) {
      [self.detailContentView updateContent:model prefixName:nil];
      [[RCIMClient sharedRCIMClient] getDiscussion:model.targetId
                                           success:^(RCDiscussion *discussion) {
                                             dispatch_async(dispatch_get_main_queue(), ^{
                                               if ([model isEqual:self.model] && discussion) {
                                                 [self.detailContentView
                                                     updateContent:model
                                                        prefixName:discussion.discussionName];
                                               }
                                             });
                                           }
                                             error:nil];
    }
  } else if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_PUBLIC_SERVICE) {
    RCPublicServiceProfile *serviceProfile = [[RCIMClient sharedRCIMClient]
        getPublicServiceProfile:(RCPublicServiceType)model.conversationType
                publicServiceId:model.targetId];
    if (serviceProfile) {
      self.headerView.headerImageView.imageURL = [NSURL URLWithString:serviceProfile.portraitUrl];
      [self updateConversationTitle:serviceProfile.name];
    }
    [self.detailContentView updateContent:model prefixName:serviceProfile.name];
  }

  [self.headerView updateBubbleUnreadNumber:(int)model.unreadMessageCount];

  self.messageCreatedTimeLabel.text = [RCKitUtility ConvertMessageTime:model.sentTime / 1000];

  [self.statusView updateNotificationStatus:model];
  [self.statusView updateReadStatus:model];
}

- (void)resetDefaultLayout:(RCConversationModel *)reuseModel {
  _hideSenderName = [self hideSenderNameForDefault:reuseModel];
  self.topCellBackgroundColor = reuseModel.topCellBackgroundColor;
  self.cellBackgroundColor = reuseModel.cellBackgroundColor;
  
  [self.headerView resetDefaultLayout:reuseModel];
  self.conversationTitle.text = nil;
  self.messageCreatedTimeLabel.text = nil;
  [self.detailContentView resetDefaultLayout:reuseModel];
  [self.statusView resetDefaultLayout:reuseModel];
}

- (void)updateConversationTitle:(NSString *)text {
  self.model.conversationTitle = text;
  self.conversationTitle.text = self.model.conversationTitle;
}

- (BOOL)hideSenderNameForDefault:(RCConversationModel *)model {
  if ([model.objectName isEqualToString:@"RC:RcNtf"] ||
      ([RCKitUtility isUnkownMessage:model.lastestMessageId content:model.lastestMessage] &&
       [RCIM sharedRCIM].showUnkownMessage)) {
    return YES;
  } else {
    return NO;
  }
}

- (void)setHideSenderName:(BOOL)hideSenderName {
  BOOL updateSenderName = (hideSenderName != _hideSenderName);
  _hideSenderName = hideSenderName;

  if (updateSenderName) {
    if (_hideSenderName) {
      [self.detailContentView updateContent:self.model prefixName:nil];
    } else if (self.model.conversationType == ConversationType_GROUP) {
      RCUserInfo *userInfo =
          [[RCUserInfoCacheManager sharedManager] getUserInfo:self.model.senderUserId
                                                    inGroupId:self.model.targetId];
      [self.detailContentView updateContent:self.model prefixName:userInfo.name];
    } else if (self.model.conversationType == ConversationType_DISCUSSION) {
      RCUserInfo *userInfo =
          [[RCUserInfoCacheManager sharedManager] getUserInfo:self.model.senderUserId
                                                    inGroupId:self.model.targetId];
      [self.detailContentView updateContent:self.model prefixName:userInfo.name];
    }
  }
}

#pragma mark - UserInfo Update
- (void)onUserInfoUpdate:(NSNotification *)notification {
  NSDictionary *userInfoDic = notification.object;
  RCUserInfo *updateUserInfo = userInfoDic[@"userInfo"];
  NSString *updateUserId = userInfoDic[@"userId"];

  dispatch_async(dispatch_get_main_queue(), ^{
    if (self.model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_NORMAL) {
      if ([updateUserId isEqualToString:self.model.targetId] &&
          (self.model.conversationType == ConversationType_PRIVATE ||
           self.model.conversationType == ConversationType_CUSTOMERSERVICE ||
           self.model.conversationType == ConversationType_SYSTEM)) {
        self.headerView.headerImageView.imageURL = [NSURL URLWithString:updateUserInfo.portraitUri];
        [self updateConversationTitle:updateUserInfo.name];
      } else if ([updateUserId isEqualToString:self.model.senderUserId] &&
                 self.model.conversationType == ConversationType_GROUP) {
        if (!self.hideSenderName) {
          RCUserInfo *userInfo =
              [[RCUserInfoCacheManager sharedManager] getUserInfo:self.model.senderUserId
                                                        inGroupId:self.model.targetId];
          [self.detailContentView updateContent:self.model prefixName:userInfo.name];
        }
      } else if ([updateUserId isEqualToString:self.model.senderUserId] &&
                 self.model.conversationType == ConversationType_DISCUSSION) {
        if (!self.hideSenderName) {
          [self.detailContentView updateContent:self.model prefixName:updateUserInfo.name];
        }
      }
    } else if (self.model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
      if ([updateUserId isEqualToString:self.model.targetId] &&
          (self.model.conversationType == ConversationType_PRIVATE ||
           self.model.conversationType == ConversationType_CUSTOMERSERVICE ||
           self.model.conversationType == ConversationType_SYSTEM)) {
        [self.detailContentView updateContent:self.model prefixName:updateUserInfo.name];
      }
    }
  });
}

- (void)onGroupUserInfoUpdate:(NSNotification *)notification {
  NSDictionary *groupUserInfoDic = (NSDictionary *)notification.object;
  NSString *groupId = groupUserInfoDic[@"inGroupId"];
  NSString *userId = groupUserInfoDic[@"userId"];

  dispatch_async(dispatch_get_main_queue(), ^{
    if (self.model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_NORMAL &&
        self.model.conversationType == ConversationType_GROUP &&
        [self.model.targetId isEqualToString:groupId] &&
        [self.model.senderUserId isEqualToString:userId]) {
      if (!self.hideSenderName) {
        RCUserInfo *userInfo =
            [[RCUserInfoCacheManager sharedManager] getUserInfo:self.model.senderUserId
                                                      inGroupId:self.model.targetId];
        [self.detailContentView updateContent:self.model prefixName:userInfo.name];
      }
    }
  });
}

- (void)onGroupInfoUpdate:(NSNotification *)notification {
  NSDictionary *groupInfoDic = (NSDictionary *)notification.object;
  RCGroup *groupInfo = groupInfoDic[@"groupInfo"];

  dispatch_async(dispatch_get_main_queue(), ^{
    if (self.model.conversationType == ConversationType_GROUP &&
        [self.model.targetId isEqualToString:groupInfo.groupId]) {
      if (self.model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_NORMAL) {
        self.headerView.headerImageView.imageURL = [NSURL URLWithString:groupInfo.portraitUri];
        [self updateConversationTitle:groupInfo.groupName];
      } else if (self.model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
        [self.detailContentView updateContent:self.model prefixName:groupInfo.groupName];
      }
    }
  });
}

- (void)updateCellIfNeed:(NSNotification *)notification {
  RCConversationCellUpdateInfo *updateInfo = notification.object;

  if ([updateInfo.model isEqual:self.model]) {
    dispatch_async(dispatch_get_main_queue(), ^{
      if (updateInfo.updateType == RCConversationCell_MessageContent_Update) {
        [self.detailContentView updateContent:self.model];
      } else if (updateInfo.updateType == RCConversationCell_SentStatus_Update) {
        [self.statusView updateReadStatus:self.model];
      } else if (updateInfo.updateType == RCConversationCell_UnreadCount_Update) {
        [self.headerView updateBubbleUnreadNumber:(int)self.model.unreadMessageCount];
      }
    });
  }
}

#pragma mark - 回调
- (void)headerImageDidTap {
  if ([self.delegate respondsToSelector:@selector(didTapCellPortrait:)]) {
    [self.delegate didTapCellPortrait:self.model];
  }
}

- (void)headerImageDidLongPress {
  if ([self.delegate respondsToSelector:@selector(didLongPressCellPortrait:)]) {
    [self.delegate didLongPressCellPortrait:self.model];
  }
}

#pragma mark - 向后兼容
- (void)setHeaderImageViewBackgroundView:(UIView *)headerImageViewBackgroundView {
  self.headerView.backgroundView = headerImageViewBackgroundView;
}
- (UIView *)headerImageViewBackgroundView {
  return self.headerView.backgroundView;
}
- (void)setHeaderImageView:(RCloudImageView *)headerImageView {
  self.headerView.headerImageView = headerImageView;
}
- (RCloudImageView *)headerImageView {
  return self.headerView.headerImageView;
}
- (void)setBubbleTipView:(RCMessageBubbleTipView *)bubbleTipView {
  self.headerView.bubbleView = bubbleTipView;
}
- (RCMessageBubbleTipView *)bubbleTipView {
  return self.headerView.bubbleView;
}
- (void)setConversationStatusImageView:(UIImageView *)conversationStatusImageView {
  self.statusView.conversationNotificationStatusView = conversationStatusImageView;
}
- (UIImageView *)conversationStatusImageView {
  return self.statusView.conversationNotificationStatusView;
}
- (void)setLastSendMessageStatusView:(UIImageView *)lastSendMessageStatusView {
  self.statusView.messageReadStatusView = lastSendMessageStatusView;
}
- (UIImageView *)lastSendMessageStatusView {
  return self.statusView.messageReadStatusView;
}
- (void)setMessageContentLabel:(UILabel *)messageContentLabel {
  self.detailContentView.messageContentLabel = messageContentLabel;
}
- (UILabel *)messageContentLabel {
  return self.detailContentView.messageContentLabel;
}
- (void)setEnableNotification:(BOOL)enableNotification {
  dispatch_async(dispatch_get_main_queue(), ^{
    self.statusView.conversationNotificationStatusView.hidden = enableNotification;
  });
}
- (BOOL)enableNotification {
  return self.statusView.conversationNotificationStatusView.hidden;
}
- (void)setPortraitStyle:(RCUserAvatarStyle)portraitStyle {
  [self setHeaderImagePortraitStyle:portraitStyle];
}
- (void)setHeaderImagePortraitStyle:(RCUserAvatarStyle)portraitStyle {
  _portraitStyle = portraitStyle;
  [self.headerView setHeaderImageStyle:_portraitStyle];
}
- (void)setIsShowNotificationNumber:(BOOL)isShowNotificationNumber {
  self.headerView.bubbleView.isShowNotificationNumber = isShowNotificationNumber;
}
- (BOOL)isShowNotificationNumber {
  return self.headerView.bubbleView.isShowNotificationNumber;
}
@end
