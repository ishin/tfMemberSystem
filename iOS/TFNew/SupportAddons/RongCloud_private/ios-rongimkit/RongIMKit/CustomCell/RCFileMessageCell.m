//
//  RCFileMessageCell.m
//  RongIMKit
//
//  Created by liulin on 16/7/21.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCFileMessageCell.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import "RCIM.h"

@interface RCFileMessageCell ()

@property(nonatomic, strong) NSMutableArray *messageContentConstraint;

@end

@implementation RCFileMessageCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
         referenceExtraHeight:(CGFloat)extraHeight {
  CGFloat __messagecontentview_height =  71.f;
  
  __messagecontentview_height += extraHeight;
  
  return CGSizeMake(collectionViewWidth, __messagecontentview_height);
}

- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    [self initialize];
  }
  return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
  self = [super initWithCoder:aDecoder];
  if (self) {
    [self initialize];
  }
  return self;
}

- (void)initialize {
  [self.messageActivityIndicatorView removeFromSuperview];
  self.messageContentConstraint = [[NSMutableArray alloc] init];
  
  self.bubbleBackgroundView = [[UIImageView alloc] initWithFrame:CGRectZero];
  [self.messageContentView addSubview:self.bubbleBackgroundView];
  self.bubbleBackgroundView.layer.cornerRadius = 4;
  self.bubbleBackgroundView.layer.masksToBounds = YES;
  self.bubbleBackgroundView.translatesAutoresizingMaskIntoConstraints = NO;
  
  self.nameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
  [self.nameLabel setFont:[UIFont systemFontOfSize:16.f]];
  [self.bubbleBackgroundView addSubview:self.nameLabel];
  self.nameLabel.translatesAutoresizingMaskIntoConstraints = NO;
  self.nameLabel.textColor = HEXCOLOR(0x343434);
  self.nameLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
  
  self.sizeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
  [self.sizeLabel setFont:[UIFont systemFontOfSize:13.f]];
  [self.bubbleBackgroundView addSubview:self.sizeLabel];
  self.sizeLabel.translatesAutoresizingMaskIntoConstraints = NO;
  self.sizeLabel.textColor = HEXCOLOR(0xa8a8a8);
  
  self.typeIconView =
  [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 49, 49)];
  [self.bubbleBackgroundView addSubview:self.typeIconView];
  self.typeIconView.translatesAutoresizingMaskIntoConstraints = NO;
  
  self.progressView = [[UIProgressView alloc] initWithFrame:CGRectZero];
  [self.progressView setHidden:YES];
  [self.bubbleBackgroundView addSubview:self.progressView];
  self.progressView.translatesAutoresizingMaskIntoConstraints = NO;
  self.progressView.progressViewStyle = UIProgressViewStyleDefault;
  self.progressView.progressTintColor = HEXCOLOR(0x0195ff);
  
  self.cancelSendButton = [[UIButton alloc] initWithFrame:CGRectZero];
  [self.cancelSendButton setImage:[RCKitUtility imageNamed:@"cancelButton"
                                                  ofBundle:@"RongCloud.bundle"]
                         forState:UIControlStateNormal];
  [self.cancelSendButton addTarget:self
                            action:@selector(cancelSend)
                  forControlEvents:UIControlEventTouchUpInside];
  self.cancelSendButton.translatesAutoresizingMaskIntoConstraints = NO;
//  [self.baseContentView addSubview:self.cancelSendButton];
  [self displayCancelButton];
  self.cancelSendButton.hidden = YES;
  
  self.cancelLabel = [[UILabel alloc] initWithFrame:CGRectZero];
  self.cancelLabel.text = NSLocalizedStringFromTable(@"CancelSendFile",
                                                     @"RongCloudKit", nil);
  self.cancelLabel.textColor = HEXCOLOR(0xa8a8a8);
  self.cancelLabel.font = [UIFont systemFontOfSize:12.f];
  self.cancelLabel.translatesAutoresizingMaskIntoConstraints = NO;
  [self.bubbleBackgroundView addSubview:self.cancelLabel];
  self.cancelLabel.hidden = YES;
  
  self.bubbleBackgroundView.userInteractionEnabled = YES;
  UILongPressGestureRecognizer *longPress =
  [[UILongPressGestureRecognizer alloc]
   initWithTarget:self
   action:@selector(longPressed:)];
  [self.bubbleBackgroundView addGestureRecognizer:longPress];
  
  UITapGestureRecognizer *messageTap =
  [[UITapGestureRecognizer alloc] initWithTarget:self
                                          action:@selector(tapMessage:)];
  messageTap.numberOfTapsRequired = 1;
  messageTap.numberOfTouchesRequired = 1;
  [self.bubbleBackgroundView addGestureRecognizer:messageTap];
  
  self.bubbleBackgroundView.userInteractionEnabled = YES;
  
  NSDictionary *views = NSDictionaryOfVariableBindings(
                                                       _nameLabel, _sizeLabel, _typeIconView, _progressView);
  
  [self.bubbleBackgroundView
   addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:
                   @"V:|-10-[_typeIconView(49)]"
                                                          options:0
                                                          metrics:nil
                                                            views:views]];
  [self.bubbleBackgroundView
   addConstraints:[NSLayoutConstraint
                   constraintsWithVisualFormat:
                   @"H:[_typeIconView(49)]-10-[_nameLabel]-13-|"
                   options:0
                   metrics:nil
                   views:views]];
  [self.bubbleBackgroundView
   addConstraints:[NSLayoutConstraint
                   constraintsWithVisualFormat:@"V:|-10-[_nameLabel(20)]"
                   options:0
                   metrics:nil
                   views:views]];
  [self.bubbleBackgroundView
   addConstraints:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"H:[_typeIconView]-10-[_sizeLabel]"
    options:0
    metrics:nil
    views:views]];
  [self.bubbleBackgroundView
   addConstraints:[NSLayoutConstraint
                   constraintsWithVisualFormat:@"V:[_sizeLabel(12)]-10-|"
                   options:0
                   metrics:nil
                   views:views]];
  [self.bubbleBackgroundView
   addConstraints:[NSLayoutConstraint
                   constraintsWithVisualFormat:@"V:[_progressView(1)]-5-|"
                   options:0
                   metrics:nil
                   views:views]];
  [self.bubbleBackgroundView
   addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:
                   @"H:|-10-[_progressView]-17-|"
                                                          options:0
                                                          metrics:nil
                                                            views:views]];
}

- (void)tapMessage:(UIGestureRecognizer *)gestureRecognizer {
  if ([self.delegate respondsToSelector:@selector(didTapMessageCell:)]) {
    [self.delegate didTapMessageCell:self.model];
  }
}

- (void)longPressed:(id)sender {
  UILongPressGestureRecognizer *press = (UILongPressGestureRecognizer *)sender;
  if (press.state == UIGestureRecognizerStateEnded) {
    DebugLog(@"long press end");
    return;
  } else if (press.state == UIGestureRecognizerStateBegan) {
    [self.delegate didLongTouchMessageCell:self.model
                                    inView:self.bubbleBackgroundView];
  }
}

- (void)setDataModel:(RCMessageModel *)model {
  [super setDataModel:model];
  RCFileMessage *fileMessage = (RCFileMessage *)self.model.content;
  self.nameLabel.text = fileMessage.name;
  self.sizeLabel.text = [RCKitUtility getReadableStringForFileSize:fileMessage.size];
  NSString *fileTypeIcon = [RCKitUtility getFileTypeIcon:fileMessage.type];
  self.typeIconView.image =
  [RCKitUtility imageNamed:fileTypeIcon ofBundle:@"RongCloud.bundle"];
  [self setAutoLayout];
}

- (void)setAutoLayout {
  [self.cancelLabel removeFromSuperview];
  CGRect messageContentViewRect = self.messageContentView.frame;
  messageContentViewRect.size.width = (int)(self.baseContentView.bounds.size.width * 0.637) + 7;
  messageContentViewRect.size.height = 71;
  if (self.messageContentConstraint.count > 0) {
    [self.messageContentView removeConstraints:self.messageContentConstraint];
    [self.messageContentConstraint removeAllObjects];
  }
  
  [self.messageContentConstraint
   addObjectsFromArray:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"V:|[_bubbleBackgroundView]|"
    options:0
    metrics:nil
    views:NSDictionaryOfVariableBindings(
                                         _bubbleBackgroundView)]];
  
  if (MessageDirection_RECEIVE == self.messageDirection) {
    self.progressView.hidden = YES;
    self.cancelSendButton.hidden = YES;
    [self.cancelSendButton removeFromSuperview];
    self.messageContentView.frame = messageContentViewRect;
    self.bubbleBackgroundView.image =
    [RCKitUtility imageNamed:@"chat_from_bg_normal"
                    ofBundle:@"RongCloud.bundle"];
    UIImage *image = self.bubbleBackgroundView.image;
    self.bubbleBackgroundView.image = [self.bubbleBackgroundView.image
                                       resizableImageWithCapInsets:UIEdgeInsetsMake(image.size.height * 0.8,
                                                                                    image.size.width * 0.8,
                                                                                    image.size.height * 0.2,
                                                                                    image.size.width * 0.2)];
    
    self.messageContentConstraint = [NSMutableArray arrayWithArray:[NSLayoutConstraint
                                                                    constraintsWithVisualFormat:@"H:|[_bubbleBackgroundView]|"
                                                                    options:0
                                                                    metrics:nil
                                                                    views:NSDictionaryOfVariableBindings(_bubbleBackgroundView)]];
    
    [self.messageContentConstraint
     addObjectsFromArray:
     [NSLayoutConstraint
      constraintsWithVisualFormat:@"V:|[_bubbleBackgroundView]|"
      options:0
      metrics:nil
      views:NSDictionaryOfVariableBindings(
                                           _bubbleBackgroundView)]];
    
    [self.messageContentConstraint
     addObjectsFromArray:
     [NSLayoutConstraint
      constraintsWithVisualFormat:@"H:|-17-[_typeIconView]"
      options:0
      metrics:nil
      views:NSDictionaryOfVariableBindings(
                                           _nameLabel, _sizeLabel, _typeIconView, _progressView)]];
  } else {
    [self.cancelSendButton removeFromSuperview];
    self.progressView.hidden = YES;
    if (self.model.sentStatus == SentStatus_CANCELED) {
      [self displayCancelLabel];
      self.progressView.hidden = YES;
      self.cancelSendButton.hidden = YES;
      [self.cancelSendButton removeFromSuperview];
    }
    if (self.model.sentStatus == SentStatus_SENDING) {
      [self displayCancelButton];
      self.progressView.hidden = NO;
      self.cancelSendButton.hidden = NO;
    }
    if (self.model.sentStatus == SentStatus_SENT || self.model.sentStatus == SentStatus_FAILED || self.model.sentStatus == SentStatus_RECEIVED) {
      self.progressView.hidden = YES;
      self.cancelSendButton.hidden = YES;
      [self.cancelSendButton removeFromSuperview];
    }
    messageContentViewRect.origin.x = self.baseContentView.frame.size.width-(10+[RCIM sharedRCIM].globalMessagePortraitSize.width+6)-messageContentViewRect.size.width;
    self.messageContentView.frame = messageContentViewRect;
    self.bubbleBackgroundView.image =
    [RCKitUtility imageNamed:@"chat_to_bg_white"
                    ofBundle:@"RongCloud.bundle"];
    UIImage *image = self.bubbleBackgroundView.image;
    self.bubbleBackgroundView.image = [self.bubbleBackgroundView.image
                                       resizableImageWithCapInsets:UIEdgeInsetsMake(image.size.height * 0.8,
                                                                                    image.size.width * 0.2,
                                                                                    image.size.height * 0.2,
                                                                                    image.size.width * 0.8)];
    [self.messageContentConstraint
     addObjectsFromArray:
     [NSLayoutConstraint
      constraintsWithVisualFormat:@"H:|[_bubbleBackgroundView]|"
      options:0
      metrics:nil
      views:NSDictionaryOfVariableBindings(
                                           _bubbleBackgroundView)]];
    
    [self.messageContentConstraint
     addObjectsFromArray:
     [NSLayoutConstraint
      constraintsWithVisualFormat:@"H:|-10-[_typeIconView]"
      options:0
      metrics:nil
      views:NSDictionaryOfVariableBindings(
                                           _nameLabel, _sizeLabel, _typeIconView, _progressView)]];
  }
  
  [self.messageContentView addConstraints:self.messageContentConstraint];
}

- (void)messageCellUpdateSendingStatusEvent:(NSNotification *)notification {
  RCMessageCellNotificationModel *notifyModel = notification.object;
  NSInteger progress = notifyModel.progress;
  
  if (self.model.messageId == notifyModel.messageId) {
    DebugLog(@"messageCellUpdateSendingStatusEvent >%@ ",
             notifyModel.actionName);
    if ([notifyModel.actionName
         isEqualToString:CONVERSATION_CELL_STATUS_SEND_BEGIN]) {
      self.model.sentStatus = SentStatus_SENDING;
      [self updateStatusContentView:self.model];
      [self updateProgressView:progress];
    } else if ([notifyModel.actionName
                isEqualToString:CONVERSATION_CELL_STATUS_SEND_FAILED]) {
      self.model.sentStatus = SentStatus_FAILED;
      [self updateStatusContentView:self.model];
      [self updateProgressView:progress];
      dispatch_async(dispatch_get_main_queue(), ^{
        [self.cancelSendButton setHidden:YES];
      });
    } else if ([notifyModel.actionName
                isEqualToString:CONVERSATION_CELL_STATUS_SEND_SUCCESS]) {
      if (self.model.sentStatus != SentStatus_READ) {
        self.model.sentStatus = SentStatus_SENT;
        [self updateStatusContentView:self.model];
        [self updateProgressView:progress];
      }
      dispatch_async(dispatch_get_main_queue(), ^{
        [self.cancelSendButton setHidden:YES];
      });
    } else if ([notifyModel.actionName
                isEqualToString:CONVERSATION_CELL_STATUS_SEND_PROGRESS]) {
      [self updateProgressView:progress];
      dispatch_async(dispatch_get_main_queue(), ^{
        self.messageActivityIndicatorView.hidden = YES;
        self.cancelSendButton.hidden = NO;
      });
    } else if ([notifyModel.actionName
                isEqualToString:CONVERSATION_CELL_STATUS_SEND_CANCELED]) {
      self.model.sentStatus = SentStatus_CANCELED;
      [self updateStatusContentView:self.model];
      dispatch_async(dispatch_get_main_queue(), ^{
        [self.cancelSendButton setHidden:YES];
        self.progressView.hidden = YES;
        [self displayCancelLabel];
      });
    } else if (self.model.sentStatus == SentStatus_READ &&
               self.isDisplayReadStatus) {
      dispatch_async(dispatch_get_main_queue(), ^{
        self.progressView.hidden = YES;
        [self.progressView setProgress:0 animated:NO];
        self.messageHasReadStatusView.hidden = NO;
        self.messageFailedStatusView.hidden = YES;
        self.messageSendSuccessStatusView.hidden = YES;
        self.model.sentStatus = SentStatus_READ;
        [self updateStatusContentView:self.model];
      });
    }
  } 
}

- (void)updateProgressView:(NSUInteger)progress {
  dispatch_async(dispatch_get_main_queue(), ^{
    if (self.model.sentStatus == SentStatus_SENDING) {
      self.progressView.hidden = NO;
      [self.progressView setProgress:(float)progress / 100.f animated:YES];
      self.cancelSendButton.hidden = NO;
    } else {
      self.progressView.hidden = YES;
    }
  });
}

- (void)cancelSend {
  if ([self.delegate respondsToSelector:@selector(didTapCancelUploadButton:)]) {
    [self.delegate didTapCancelUploadButton:self.model];
  }
}

-(void)displayCancelLabel {
  [self.bubbleBackgroundView addSubview:self.cancelLabel];
  [self.messageContentConstraint
   addObjectsFromArray:
   [NSLayoutConstraint
    constraintsWithVisualFormat:@"H:[_cancelLabel]-16.5-|"
    options:0
    metrics:nil
    views:NSDictionaryOfVariableBindings(
                                         _nameLabel, _sizeLabel, _typeIconView, _progressView, _cancelLabel)]];
  [self.bubbleBackgroundView
   addConstraint:[NSLayoutConstraint constraintWithItem:_cancelLabel
                                              attribute:NSLayoutAttributeCenterY
                                              relatedBy:NSLayoutRelationEqual
                                                 toItem:self.sizeLabel
                                              attribute:NSLayoutAttributeCenterY
                                             multiplier:1
                                               constant:0]];
  [self.messageContentView addConstraints:self.messageContentConstraint];
  self.cancelLabel.hidden = NO;
}

-(void)displayCancelButton {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.baseContentView addSubview:self.cancelSendButton];
    RCContentView *messageContentView = self.messageContentView;
    [self.baseContentView
     addConstraints:[NSLayoutConstraint
                     constraintsWithVisualFormat:@"V:[_cancelSendButton(20)]"
                     options:0
                     metrics:nil
                     views:NSDictionaryOfVariableBindings(_cancelSendButton)]];
    
    [self.baseContentView
     addConstraints:[NSLayoutConstraint
                     constraintsWithVisualFormat:@"H:[_cancelSendButton(20)]-13-[messageContentView]"
                     options:0
                     metrics:nil
                     views:NSDictionaryOfVariableBindings(messageContentView,_cancelSendButton)]];
    
    [self.baseContentView
     addConstraint:[NSLayoutConstraint constraintWithItem:_cancelSendButton
                                                attribute:NSLayoutAttributeCenterY
                                                relatedBy:NSLayoutRelationEqual
                                                   toItem:self.bubbleBackgroundView
                                                attribute:NSLayoutAttributeCenterY
                                               multiplier:1
                                                 constant:0]];

  });
  
}

@end
