//
//  RCMessageCell.m
//  RongIMKit
//
//  Created by xugang on 15/1/28.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCMessageCell.h"
#import "RCKitUtility.h"
#import "RCTipLabel.h"
#import "RCKitUtility.h"
#import "RCloudImageView.h"
#import "RCKitCommonDefine.h"
#import "RCUserInfoCacheManager.h"
#import <RongIMLib/RongIMLib.h>

NSString *const KNotificationMessageBaseCellUpdateCanReceiptStatus = @"KNotificationMessageBaseCellUpdateCanReceiptStatus";

@interface RCMessageCell ()

@property (nonatomic,strong)UILabel *hasReadLabel;

//- (void) configure;
- (void)setCellAutoLayout;

@end

// static int indexCell = 1;

@implementation RCMessageCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setupMessageCellView];
    }
    return self;
}
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setupMessageCellView];
    }
    return self;
}
- (void)setupMessageCellView
{
    _isDisplayNickname = NO;
    self.delegate = nil;
    
    self.portraitImageView = [[RCloudImageView alloc]
                              initWithPlaceholderImage:[RCKitUtility imageNamed:@"default_portrait_msg" ofBundle:@"RongCloud.bundle"]];
    
    self.messageContentView = [[RCContentView alloc] initWithFrame:CGRectZero];
    self.statusContentView = [[UIView alloc] initWithFrame:CGRectZero];
    
    self.nicknameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.nicknameLabel.backgroundColor = [UIColor clearColor];
    [self.nicknameLabel setFont:[UIFont systemFontOfSize:12.0f]];
    [self.nicknameLabel setTextColor:[UIColor grayColor]];
    
    //点击头像
    UITapGestureRecognizer *portraitTap =
    [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapUserPortaitEvent:)];
    portraitTap.numberOfTapsRequired = 1;
    portraitTap.numberOfTouchesRequired = 1;
    [self.portraitImageView addGestureRecognizer:portraitTap];
    
    UILongPressGestureRecognizer *portraitLongPress =
    [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressUserPortaitEvent:)];
    [self.portraitImageView addGestureRecognizer:portraitLongPress];
    
    
    self.portraitImageView.userInteractionEnabled = YES;
    
    [self.baseContentView addSubview:self.portraitImageView];
    [self.baseContentView addSubview:self.messageContentView];
    [self.baseContentView addSubview:self.statusContentView];
    [self.baseContentView addSubview:self.nicknameLabel];
    [self setPortraitStyle:[RCIM sharedRCIM].globalMessageAvatarStyle];
    
    self.statusContentView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 25, 25)];
    _statusContentView.backgroundColor = [UIColor clearColor];
    [self.baseContentView addSubview:_statusContentView];
    
    self.hasReadLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.hasReadLabel.font = [UIFont systemFontOfSize:9.0f];
    self.hasReadLabel.textColor = HEXCOLOR(0x96c4ec);
    self.hasReadLabel.text = NSLocalizedStringFromTable(@"read", @"RongCloudKit", nil);
    [self.baseContentView addSubview:self.hasReadLabel];
    self.hasReadLabel.hidden = YES;
    
    self.messageFailedStatusView = [[UIButton alloc] initWithFrame:CGRectMake(0, 2.5, 20, 20)];
    [_messageFailedStatusView
     setImage:[RCKitUtility imageNamed:@"sendMsg_failed_tip" ofBundle:@"RongCloud.bundle"]
     forState:UIControlStateNormal];
    [self.statusContentView addSubview:_messageFailedStatusView];
    _messageFailedStatusView.hidden = YES;
    [_messageFailedStatusView addTarget:self
                                 action:@selector(didclickMsgFailedView:)
                       forControlEvents:UIControlEventTouchUpInside];
    
    self.messageActivityIndicatorView =
    [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [self.statusContentView addSubview:_messageActivityIndicatorView];
    _messageActivityIndicatorView.hidden = YES;
    self.messageHasReadStatusView = [[UIView alloc] initWithFrame:CGRectMake(9, 0, 25, 25)];
    UIImageView *hasReadView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 1, 12, 13)];
    hasReadView.contentMode = UIViewContentModeScaleAspectFill;
    [hasReadView setImage:IMAGE_BY_NAMED(@"message_read_status")];
    [self.messageHasReadStatusView addSubview:hasReadView] ;
    [self.statusContentView addSubview:self.messageHasReadStatusView];
    self.messageHasReadStatusView.hidden = YES;
  
    self.messageSendSuccessStatusView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 40, 20)];
    UILabel *sendSuccessLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 40, 20)];
    //        sendSuccessLabel.text = NSLocalizedStringFromTable(@"MessageHasSend", @"RongCloudKit",
    //                                                           nil);
    sendSuccessLabel.font = [UIFont systemFontOfSize:14];
    sendSuccessLabel.textColor = HEXCOLOR(0x8c8c8c);
    [self.messageSendSuccessStatusView addSubview:sendSuccessLabel] ;
    [self.statusContentView addSubview:self.messageSendSuccessStatusView];
    self.messageSendSuccessStatusView.hidden = YES;
    
    self.receiptView = [[UIButton alloc] initWithFrame:CGRectMake(0,0, 23, 19)];
    self.receiptView.contentEdgeInsets = UIEdgeInsetsMake(5, 9.5, 0, 0);
    [self.receiptView setImage:[RCKitUtility imageNamed:@"receipt" ofBundle:@"RongCloud.bundle"] forState:UIControlStateNormal];
    [self.receiptView setImage:[RCKitUtility imageNamed:@"receipt_hover" ofBundle:@"RongCloud.bundle"] forState:UIControlStateHighlighted];
    [self.baseContentView addSubview:self.receiptView];
    self.receiptView.hidden = YES;
    [self.receiptView addTarget:self action:@selector(enableShowReceiptView:) forControlEvents:UIControlEventTouchUpInside];
    
    self.receiptCountLabel = [[UILabel alloc] initWithFrame:CGRectMake(-10,10, 50, 20)];
    self.receiptCountLabel.textAlignment = NSTextAlignmentRight;
    self.receiptCountLabel.font = [UIFont systemFontOfSize:10.0f];
    self.receiptCountLabel.textColor = HEXCOLOR(0x96c4ec);
    [self.baseContentView addSubview:self.receiptCountLabel];
    self.receiptCountLabel.hidden = YES;
//  self.receiptCountLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *clickReceiptCountView =
  [[UITapGestureRecognizer alloc] initWithTarget:self
                                          action:@selector(clickReceiptCountView:)];
  [self.receiptCountLabel addGestureRecognizer:clickReceiptCountView];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onUserInfoUpdate:)
                                                 name:RCKitDispatchUserInfoUpdateNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onGroupUserInfoUpdate:)
                                                 name:RCKitDispatchGroupUserInfoUpdateNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onReceiptStatusUpdate:)
                                                 name:KNotificationMessageBaseCellUpdateCanReceiptStatus
                                               object:nil];
    
    __weak typeof(&*self) __blockself = self;
    [self.messageContentView registerFrameChangedEvent:^(CGRect frame) {
        if (__blockself.model) {
            if (__blockself.model.messageDirection == MessageDirection_SEND) {
                __blockself.statusContentView.frame = CGRectMake(
                                                                 frame.origin.x - 10 - 25, frame.origin.y + (frame.size.height - 25) / 2.0f, 25, 25);
                __blockself.receiptCountLabel.frame = CGRectMake(CGRectGetMinX(frame)-50-4,CGRectGetMinY(frame)+CGRectGetHeight(frame)-10-20, 50, 20);;
                __blockself.receiptView.frame = CGRectMake(CGRectGetMinX(frame)-19-8,frame.origin.y+10, 23, 19);
                
                __blockself.hasReadLabel.frame = CGRectZero;
            } else {
                __blockself.statusContentView.frame = CGRectZero;
                __blockself.hasReadLabel.frame = CGRectMake(CGRectGetMaxX(frame)+4, CGRectGetMaxY(frame)-25, 40, 25);
            }
        }
        
    }];
}
- (void)setPortraitStyle:(RCUserAvatarStyle)portraitStyle {
    _portraitStyle = portraitStyle;

    if (_portraitStyle == RC_USER_AVATAR_RECTANGLE) {
        self.portraitImageView.layer.cornerRadius = [[RCIM sharedRCIM] portraitImageViewCornerRadius];
    }
    if (_portraitStyle == RC_USER_AVATAR_CYCLE) {
        self.portraitImageView.layer.cornerRadius = [[RCIM sharedRCIM] globalMessagePortraitSize].height/2;
    }
    self.portraitImageView.layer.masksToBounds = YES;
}
//- (void)prepareForReuse
//{
//    [super prepareForReuse];
//
//}

- (void)setDataModel:(RCMessageModel *)model {
    [super setDataModel:model];
    self.receiptView.hidden = YES;
    self.receiptCountLabel.hidden = YES;
    self.messageFailedStatusView.hidden = YES;
    if (model.readReceiptInfo.isReceiptRequestMessage && model.messageDirection == MessageDirection_SEND) {
        self.receiptCountLabel.hidden = NO;
        self.receiptCountLabel.userInteractionEnabled = YES;
        self.receiptCountLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"readNum", @"RongCloudKit", nil),self.model.readReceiptCount];
    }else{
        self.receiptCountLabel.hidden = YES;
        self.receiptCountLabel.userInteractionEnabled = NO;
        self.receiptCountLabel.text = nil;
    }
  
  if (model.messageDirection == MessageDirection_SEND && model.sentStatus == SentStatus_SENT) {
    if (model.isCanSendReadReceipt) {
      self.receiptView.hidden = NO;
      self.receiptCountLabel.hidden = YES;
    }else{
      self.receiptView.hidden = YES;
      self.receiptCountLabel.hidden = NO;
    }
  }

  
    if (model.readReceiptInfo.hasRespond) {
        self.hasReadLabel.hidden = YES;
    }else{
        self.hasReadLabel.hidden = YES;
        
    }

    self.messageSendSuccessStatusView.hidden = YES;
    self.messageHasReadStatusView.hidden = YES;

    _isDisplayNickname = model.isDisplayNickname;
    
    // DebugLog(@"%s", __FUNCTION__);
    //如果是客服，跟换默认头像
    if (ConversationType_CUSTOMERSERVICE == model.conversationType) {
        if (model.messageDirection == MessageDirection_RECEIVE) {
            [self.portraitImageView setPlaceholderImage:[RCKitUtility imageNamed:@"portrait_kefu" ofBundle:@"RongCloud.bundle"]];
            
            model.userInfo = model.content.senderUserInfo;
            if (model.content.senderUserInfo != nil) {
                [self.portraitImageView setImageURL:[NSURL URLWithString:model.content.senderUserInfo.portraitUri]];
                [self.nicknameLabel setText:model.content.senderUserInfo.name];
            } else {
                [self.portraitImageView setImage:[RCKitUtility imageNamed:@"portrait_kefu" ofBundle:@"RongCloud.bundle"]];
                [self.nicknameLabel setText:nil];
            }
        } else {
            RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:model.senderUserId];
            model.userInfo = userInfo;
            [self.portraitImageView setPlaceholderImage:[RCKitUtility imageNamed:@"default_portrait_msg" ofBundle:@"RongCloud.bundle"]];
            if (userInfo) {
                [self.portraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
                [self.nicknameLabel setText:userInfo.name];
            } else {
                [self.portraitImageView setImageURL:nil];
                [self.nicknameLabel setText:nil];
            }
        }
    } else if (ConversationType_APPSERVICE == model.conversationType ||
               ConversationType_PUBLICSERVICE == model.conversationType) {
        if (model.messageDirection == MessageDirection_RECEIVE) {
            RCPublicServiceProfile *serviceProfile =
            [[RCIMClient sharedRCIMClient] getPublicServiceProfile:(RCPublicServiceType)model.conversationType publicServiceId:model.senderUserId];
            model.userInfo = model.content.senderUserInfo;
            if (serviceProfile) {
                [self.portraitImageView setImageURL:[NSURL URLWithString:serviceProfile.portraitUrl]];
                [self.nicknameLabel setText:serviceProfile.name];
            }
        } else {
            RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:model.senderUserId];
            model.userInfo = userInfo;
            if (userInfo) {
                [self.portraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
                [self.nicknameLabel setText:userInfo.name];
            }else {
                [self.portraitImageView setImageURL:nil];
                [self.nicknameLabel setText:nil];
            }
        }
    } else if (ConversationType_GROUP == model.conversationType) {
        RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:model.senderUserId inGroupId:self.model.targetId];
        model.userInfo = userInfo;
        if (userInfo) {
            [self.portraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
            [self.nicknameLabel setText:userInfo.name];
        } else {
            [self.portraitImageView setImageURL:nil];
            [self.nicknameLabel setText:nil];
        }
    } else {
        RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:model.senderUserId];
        model.userInfo = userInfo;
        if (userInfo) {
            [self.portraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
            [self.nicknameLabel setText:userInfo.name];
        } else {
            [self.portraitImageView setImageURL:nil];
            [self.nicknameLabel setText:nil];
        }
    }

    [self setCellAutoLayout];
}
- (void)setCellAutoLayout {

    _messageContentViewWidth = 200;
    // receiver
    if (MessageDirection_RECEIVE == self.messageDirection) {
        self.nicknameLabel.hidden = !self.isDisplayNickname;
        CGFloat portraitImageX = 10;
        self.portraitImageView.frame = CGRectMake(portraitImageX, 10, [RCIM sharedRCIM].globalMessagePortraitSize.width,[RCIM sharedRCIM].globalMessagePortraitSize.height);
        self.nicknameLabel.frame =
            CGRectMake(portraitImageX + self.portraitImageView.bounds.size.width + 17,9, 200, 14);

        CGFloat messageContentViewY = 10;
        if (self.isDisplayNickname) {
            messageContentViewY = 10 + 12 + 4;
        }
        self.messageContentView.frame =
            CGRectMake(portraitImageX + self.portraitImageView.bounds.size.width + HeadAndContentSpacing, messageContentViewY,
                       _messageContentViewWidth, self.baseContentView.bounds.size.height - (messageContentViewY));
    } else { // owner
        self.nicknameLabel.hidden = YES;
        CGFloat portraitImageX =
            self.baseContentView.bounds.size.width - ([RCIM sharedRCIM].globalMessagePortraitSize.width + 10);
        self.portraitImageView.frame = CGRectMake(portraitImageX, 10, [RCIM sharedRCIM].globalMessagePortraitSize.width,
                                                  [RCIM sharedRCIM].globalMessagePortraitSize.height);

        self.messageContentView.frame =
            CGRectMake(self.baseContentView.bounds.size.width -
                           (_messageContentViewWidth + HeadAndContentSpacing + [RCIM sharedRCIM].globalMessagePortraitSize.width + 10),
                       10, _messageContentViewWidth, self.baseContentView.bounds.size.height - (10));
    }

    [self updateStatusContentView:self.model];
}

- (void)updateStatusContentView:(RCMessageModel *)model {
    self.messageSendSuccessStatusView.hidden = YES;
    self.messageHasReadStatusView.hidden = YES;
    self.messageActivityIndicatorView.hidden = YES;
    if (model.messageDirection == MessageDirection_RECEIVE) {
        self.statusContentView.hidden = YES;
        return;
    } else {
        self.statusContentView.hidden = NO;
    }
    __weak typeof(&*self) __blockSelf = self;

    dispatch_async(dispatch_get_main_queue(), ^{

      if (__blockSelf.model.sentStatus == SentStatus_SENDING) {
          __blockSelf.messageFailedStatusView.hidden = YES;
          __blockSelf.messageHasReadStatusView.hidden = YES;
          __blockSelf.messageSendSuccessStatusView.hidden = YES;
          if (__blockSelf.messageActivityIndicatorView) {
              __blockSelf.messageActivityIndicatorView.hidden = NO;
              if (__blockSelf.messageActivityIndicatorView.isAnimating == NO) {
                  [__blockSelf.messageActivityIndicatorView startAnimating];
              }
          }

      } else if (__blockSelf.model.sentStatus == SentStatus_FAILED) {
          __blockSelf.messageFailedStatusView.hidden = NO;
          __blockSelf.messageHasReadStatusView.hidden = YES;
          __blockSelf.messageSendSuccessStatusView.hidden = YES;
          if (__blockSelf.messageActivityIndicatorView) {
              __blockSelf.messageActivityIndicatorView.hidden = YES;
              if (__blockSelf.messageActivityIndicatorView.isAnimating == YES) {
                  [__blockSelf.messageActivityIndicatorView stopAnimating];
              }
          }
      } else if (__blockSelf.model.sentStatus == SentStatus_CANCELED) {
        __blockSelf.messageFailedStatusView.hidden = YES;
        __blockSelf.messageHasReadStatusView.hidden = YES;
        __blockSelf.messageSendSuccessStatusView.hidden = YES;
        if (__blockSelf.messageActivityIndicatorView) {
          __blockSelf.messageActivityIndicatorView.hidden = YES;
          if (__blockSelf.messageActivityIndicatorView.isAnimating == YES) {
            [__blockSelf.messageActivityIndicatorView stopAnimating];
          }
        }
      } else if (__blockSelf.model.sentStatus == SentStatus_SENT) {
          __blockSelf.messageFailedStatusView.hidden = YES;
          if (__blockSelf.messageActivityIndicatorView) {
              __blockSelf.messageActivityIndicatorView.hidden = YES;
              if (__blockSelf.messageActivityIndicatorView.isAnimating == YES) {
                  [__blockSelf.messageActivityIndicatorView stopAnimating];
              }
          }
        __blockSelf.messageSendSuccessStatusView.hidden = NO;
        
          if (model.isCanSendReadReceipt) {
            self.receiptView.hidden = NO;
            self.receiptCountLabel.hidden = YES;
          }else{
            self.receiptView.hidden = YES;
            self.receiptCountLabel.hidden = NO;
          }
        
      }//更新成已读状态
      else if (__blockSelf.model.sentStatus == SentStatus_READ && self.isDisplayReadStatus && __blockSelf.model.conversationType == ConversationType_PRIVATE) {
          __blockSelf.messageHasReadStatusView.hidden = NO;
          __blockSelf.statusContentView.frame = CGRectMake(self.messageContentView.frame.origin.x - 25 , self.messageContentView.frame.size.height-3  , 10, 10);
          
          __blockSelf.messageFailedStatusView.hidden = YES;
          __blockSelf.messageSendSuccessStatusView.hidden = YES;
          if (__blockSelf.messageActivityIndicatorView) {
              __blockSelf.messageActivityIndicatorView.hidden = YES;
              if (__blockSelf.messageActivityIndicatorView.isAnimating == YES) {
                  [__blockSelf.messageActivityIndicatorView stopAnimating];
              }
          }

      }
    });
}

#pragma mark private
- (void)tapUserPortaitEvent:(UIGestureRecognizer *)gestureRecognizer {
    __weak typeof(&*self) weakSelf = self;
    if ([self.delegate respondsToSelector:@selector(didTapCellPortrait:)]) {
        [self.delegate didTapCellPortrait:weakSelf.model.senderUserId];
    }
}

- (void)longPressUserPortaitEvent:(UIGestureRecognizer *)gestureRecognizer {
    __weak typeof(&*self) weakSelf = self;
    if (gestureRecognizer.state == UIGestureRecognizerStateBegan) {
        if ([self.delegate respondsToSelector:@selector(didLongPressCellPortrait:)]) {
            [self.delegate didLongPressCellPortrait:weakSelf.model.senderUserId];
        }
    }
}

- (void)imageMessageSendProgressing:(NSInteger)progress {
}


-(void)onReceiptStatusUpdate:(NSNotification *)notification{
    // 更新消息状态
    NSDictionary *statusDic = notification.object;
    NSUInteger conversationType = [statusDic[@"conversationType"] integerValue];
    NSString *targetId = statusDic[@"targetId"];
    long messageId = [statusDic[@"messageId"] longValue];
    if (self.model.conversationType == conversationType && [self.model.targetId isEqualToString:targetId]) {
        if ( messageId == self.model.messageId && [self.model.content isKindOfClass:[RCTextMessage class]]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                self.receiptView.hidden = NO;
                self.receiptCountLabel.hidden = YES;
                self.model.isCanSendReadReceipt = YES;
            });
        }else{
            dispatch_async(dispatch_get_main_queue(), ^{
                self.receiptView.hidden = YES;
                self.receiptCountLabel.hidden = NO;
                self.model.isCanSendReadReceipt = NO;
            });
        }

    }
}
- (void)messageCellUpdateSendingStatusEvent:(NSNotification *)notification {

    RCMessageCellNotificationModel *notifyModel = notification.object;

    if (self.model.messageId == notifyModel.messageId) {
        DebugLog(@"messageCellUpdateSendingStatusEvent >%@ ", notifyModel.actionName);
        if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_BEGIN]) {
            self.model.sentStatus = SentStatus_SENDING;
            [self updateStatusContentView:self.model];

        } else if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_FAILED]) {
            self.model.sentStatus = SentStatus_FAILED;
            [self updateStatusContentView:self.model];
        } else if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_CANCELED]) {
          self.model.sentStatus = SentStatus_CANCELED;
          [self updateStatusContentView:self.model];
        } else if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_SUCCESS]) {
            if (self.model.sentStatus != SentStatus_READ) {
                self.model.sentStatus = SentStatus_SENT;
                [self updateStatusContentView:self.model];
            }
        } else if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_PROGRESS]) {
            [self imageMessageSendProgressing:notifyModel.progress];
        } else if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_HASREAD] && [[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.model.conversationType)] && self.model.conversationType == ConversationType_PRIVATE) {
            self.model.sentStatus = SentStatus_READ;
            [self updateStatusContentView:self.model];
        }
        else if ([notifyModel.actionName isEqualToString:CONVERSATION_CELL_STATUS_SEND_READCOUNT] && [[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.model.conversationType)] && (self.model.conversationType == ConversationType_GROUP || self.model.conversationType == ConversationType_DISCUSSION)) {
            self.receiptView.hidden = YES;
            self.receiptCountLabel.hidden = NO;
            self.receiptCountLabel.userInteractionEnabled = YES;
            self.receiptCountLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"readNum", @"RongCloudKit", nil),notifyModel.progress];

        }

    }
}

- (void)didclickMsgFailedView:(UIButton *)button {
    if (self.delegate) {
        if ([self.delegate respondsToSelector:@selector(didTapmessageFailedStatusViewForResend:)]) {
            [self.delegate didTapmessageFailedStatusViewForResend:self.model];
        }
    }
}

#pragma mark - UserInfo Update
- (void)onUserInfoUpdate:(NSNotification *)notification {
    NSDictionary *userInfoDic = notification.object;
    if ([self.model.senderUserId isEqualToString:userInfoDic[@"userId"]]) {
        if (self.model.conversationType == ConversationType_GROUP) {
            //重新取一下混合的用户信息
            RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:self.model.senderUserId inGroupId:self.model.targetId];
            [self updateUserInfoUI:userInfo];
        } else if (self.model.messageDirection == MessageDirection_SEND) {
            [self updateUserInfoUI:userInfoDic[@"userInfo"]];
        } else if (self.model.conversationType != ConversationType_APPSERVICE
                   && self.model.conversationType != ConversationType_PUBLICSERVICE) {
            if (self.model.conversationType == ConversationType_CUSTOMERSERVICE
                && self.model.content.senderUserInfo) {
                return;
            }
            [self updateUserInfoUI:userInfoDic[@"userInfo"]];
        }
    }
}

- (void)enableShowReceiptView:(UIButton *)sender{
    self.receiptView.hidden = YES;
    self.model.isCanSendReadReceipt = NO;
    if (!self.model.messageUId) {
        RCMessage *message = [[RCIMClient sharedRCIMClient]getMessage:self.model.messageId];
        if (message) {
            [self sendMessageReadReceiptRequest:message.messageUId];
        }
    }else{
        [self sendMessageReadReceiptRequest:self.model.messageUId];
    }
}

- (void)sendMessageReadReceiptRequest:(NSString *)messageUId{
    RCMessage *message = [[RCIMClient sharedRCIMClient]getMessage:self.model.messageId];
    if (message) {
        [[RCIMClient sharedRCIMClient] sendReadReceiptRequest:message success:^{
            dispatch_async(dispatch_get_main_queue(), ^{
                self.receiptCountLabel.hidden = NO;
                self.receiptCountLabel.userInteractionEnabled = YES;
                self.receiptCountLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"readNum", @"RongCloudKit", nil),0];
                if (!self.model.readReceiptInfo) {
                    self.model.readReceiptInfo = [[RCReadReceiptInfo alloc]init];
                }
                self.model.readReceiptInfo.isReceiptRequestMessage = YES;
            });
        } error:^(RCErrorCode nErrorCode) {
            
        }];
    }
    
}

- (void)onGroupUserInfoUpdate:(NSNotification *)notification {
    if (self.model.conversationType == ConversationType_GROUP) {
        NSDictionary *groupUserInfoDic = (NSDictionary *)notification.object;
        if ([self.model.targetId isEqualToString:groupUserInfoDic[@"inGroupId"]]
            && [self.model.senderUserId isEqualToString:groupUserInfoDic[@"userId"]]) {
            //重新取一下混合的用户信息
            RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:self.model.senderUserId inGroupId:self.model.targetId];
            [self updateUserInfoUI:userInfo];
        }
    }
}

-(void)updateUserInfoUI:(RCUserInfo *)userInfo {
    self.model.userInfo = userInfo;
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        if (userInfo.portraitUri.length > 0) {
            [weakSelf.portraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
        }
        [weakSelf.nicknameLabel setText:userInfo.name];
    });
}

- (void)clickReceiptCountView:(id)sender {
  if ([self.delegate respondsToSelector:@selector(didTapReceiptCountView:)]) {
    if (self.receiptCountLabel.text != nil) {
      [self.delegate didTapReceiptCountView:self.model];
    }
    return;
  }
}

-(void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 向后兼容
-(UIButton *)groupAndDiscussionReceiptView {
  return self.receiptView;
}
-(void)setGroupAndDiscussionReceiptView:(UIButton *)groupAndDiscussionReceiptView {
  self.receiptView = groupAndDiscussionReceiptView;
}
-(UILabel *)groupAndDiscussionReceiptCountView {
  return self.receiptCountLabel;
}
-(void)setGroupAndDiscussionReceiptCountView:(UILabel *)groupAndDiscussionReceiptCountView {
  self.receiptCountLabel = groupAndDiscussionReceiptCountView;
}

@end
