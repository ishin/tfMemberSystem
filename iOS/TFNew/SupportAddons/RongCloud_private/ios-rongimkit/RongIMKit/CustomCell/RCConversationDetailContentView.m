//
//  RCConversationDetailContentView.m
//  RongIMKit
//
//  Created by 岑裕 on 16/9/15.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCConversationDetailContentView.h"
#import "RCIM.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"

@interface RCConversationDetailContentView ()
@property(nonatomic, strong) NSArray *constraints;
@property(nonatomic, strong) NSString *prefixName;
@end

@implementation RCConversationDetailContentView
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

  self.messageContentLabel = [[UILabel alloc] init];
  self.messageContentLabel.backgroundColor = [UIColor clearColor];
  self.messageContentLabel.font = [UIFont systemFontOfSize:14];
  self.messageContentLabel.textColor = HEXCOLOR(0x999999);
  self.messageContentLabel.translatesAutoresizingMaskIntoConstraints = NO;
  [self addSubview:self.messageContentLabel];

  self.hightlineLabel = [[UILabel alloc] init];
  self.hightlineLabel.backgroundColor = [UIColor clearColor];
  self.hightlineLabel.font = [UIFont systemFontOfSize:14];
  self.hightlineLabel.textColor = HEXCOLOR(0x999999);
  self.hightlineLabel.translatesAutoresizingMaskIntoConstraints = NO;
  [self addSubview:self.hightlineLabel];

  self.sentStatusView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 16, 16)];
  self.sentStatusView.image = IMAGE_BY_NAMED(@"message_fail");
  [self addSubview:self.sentStatusView];

  [self addConstraint:[NSLayoutConstraint constraintWithItem:self.messageContentLabel
                                                   attribute:NSLayoutAttributeCenterY
                                                   relatedBy:NSLayoutRelationEqual
                                                      toItem:self
                                                   attribute:NSLayoutAttributeCenterY
                                                  multiplier:1
                                                    constant:0]];
  [self addConstraint:[NSLayoutConstraint constraintWithItem:self.hightlineLabel
                                                   attribute:NSLayoutAttributeCenterY
                                                   relatedBy:NSLayoutRelationEqual
                                                      toItem:self
                                                   attribute:NSLayoutAttributeCenterY
                                                  multiplier:1
                                                    constant:0]];
  [self addConstraint:[NSLayoutConstraint constraintWithItem:self.sentStatusView
                                                   attribute:NSLayoutAttributeCenterY
                                                   relatedBy:NSLayoutRelationEqual
                                                      toItem:self
                                                   attribute:NSLayoutAttributeCenterY
                                                  multiplier:1
                                                    constant:0]];
}

- (void)updateContent:(RCConversationModel *)model prefixName:(NSString *)prefixName {
  self.prefixName = prefixName;
  [self updateContent:model];
}

- (void)updateContent:(RCConversationModel *)model {
  if (model.draft.length > 0) {
    self.sentStatusView.hidden = YES;
    self.hightlineLabel.text = NSLocalizedStringFromTable(@"Draft", @"RongCloudKit", nil);
    self.hightlineLabel.textColor = HEXCOLOR(0xcc3333);
  } else if (model.lastestMessageDirection == MessageDirection_SEND &&
             model.sentStatus == SentStatus_FAILED) {
    self.sentStatusView.hidden = NO;
    self.hightlineLabel.text = nil;
  } else if (model.hasUnreadMentioned) {
    self.sentStatusView.hidden = YES;
    self.hightlineLabel.text = NSLocalizedStringFromTable(@"HaveMentioned", @"RongCloudKit", nil);
    self.hightlineLabel.textColor = HEXCOLOR(0xcc3333);
  } else {
    self.sentStatusView.hidden = YES;
    self.hightlineLabel.text = nil;
  }

  NSString *messageContent = nil;
  if (model.draft.length > 0) {
    messageContent = model.draft;
  } else if (model.lastestMessageId > 0) {
    if (self.prefixName.length == 0 || model.lastestMessageDirection == MessageDirection_SEND || [model.lastestMessage isMemberOfClass:[RCRecallNotificationMessage class]]  ) {
      messageContent = [self formatMessageContent:model];
    } else {
      messageContent =
          [NSString stringWithFormat:@"%@:%@", self.prefixName, [self formatMessageContent:model]];
    }
  }
  if(messageContent == nil){
    messageContent = @"";
  } else {
    messageContent = [self getOneLineString:messageContent];
  }
  
  NSMutableAttributedString *attibuteText = [[NSMutableAttributedString alloc] initWithString:messageContent];
  if(model.draft.length == 0 && model.lastestMessageId > 0 && [model.lastestMessage isKindOfClass:[RCVoiceMessage class]] && model.receivedStatus != ReceivedStatus_LISTENED && model.lastestMessageDirection == MessageDirection_RECEIVE) {
    NSRange range = NSMakeRange(0, 0);
    if (self.prefixName.length == 0 || messageContent.length == 0) {
      range = NSMakeRange(0, messageContent.length);
    } else {
      range = NSMakeRange(self.prefixName.length + 1, [self formatMessageContent:model].length);
    }
    [attibuteText addAttribute:NSForegroundColorAttributeName value:HEXCOLOR(0xcc3333) range:range];
  }
  self.messageContentLabel.attributedText = attibuteText;
  [self updateLayout];
}

- (void)updateLayout {
  if (self.constraints) {
    [self removeConstraints:self.constraints];
  }

  NSString *layoutFormat = nil;
  if (!self.sentStatusView.hidden) {
    layoutFormat = @"H:|-0-[_sentStatusView(width)]-3.5-[_messageContentLabel]-0-|";
  } else if (self.hightlineLabel.text.length > 0) {
    layoutFormat = @"H:|-0-[_hightlineLabel(width)]-3.5-[_messageContentLabel]-0-|";
  } else {
    layoutFormat = @"H:|-0-[_messageContentLabel]-0-|";
  }
  
  self.constraints = [NSLayoutConstraint
      constraintsWithVisualFormat:layoutFormat
                          options:0
                      metrics:@{@"width": @([self getLeftViewWidth])}
                            views:NSDictionaryOfVariableBindings(_sentStatusView, _hightlineLabel,
                                                                 _messageContentLabel)];
  [self addConstraints:self.constraints];

  [self setNeedsUpdateConstraints];
  [self updateConstraintsIfNeeded];
  [self layoutIfNeeded];
}

- (NSString *)formatMessageContent:(RCConversationModel *)model {
  if ([RCKitUtility isUnkownMessage:model.lastestMessageId content:model.lastestMessage] &&
      [RCIM sharedRCIM].showUnkownMessage) {
    return NSLocalizedStringFromTable(@"unknown_message_cell_tip", @"RongCloudKit", nil);
  } else {
    return [RCKitUtility formatMessage:model.lastestMessage];
  }
}

- (NSString *)getOneLineString:(NSString *)oldString {
  NSString *newString = [oldString stringByReplacingOccurrencesOfString:@"\r\n" withString:@" "];
  newString = [newString stringByReplacingOccurrencesOfString:@"\n" withString:@" "];
  newString = [newString stringByReplacingOccurrencesOfString:@"\r" withString:@" "];
  return newString;
}

- (void)resetDefaultLayout:(RCConversationModel *)reuseModel {
  self.hightlineLabel.text = nil;
  self.messageContentLabel.attributedText = nil;
  self.sentStatusView.hidden = YES;
}

- (CGFloat)getLeftViewWidth {
  if (!self.sentStatusView.hidden) {
    return self.sentStatusView.image.size.width;
  } else if (self.hightlineLabel.text.length > 0) {
    CGSize size = [RCKitUtility getTextDrawingSize:self.hightlineLabel.text font:self.hightlineLabel.font constrainedSize:CGSizeMake(MAXFLOAT, self.bounds.size.height)];
    return ceilf(size.width);
  } else {
    return 0;
  }
}

@end
