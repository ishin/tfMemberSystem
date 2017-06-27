//
//  RCMessageBaseCell.m
//  RongIMKit
//
//  Created by xugang on 15/1/28.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCMessageBaseCell.h"
#import "RCTipLabel.h"
#import "RCKitUtility.h"
#import "RCKitCommonDefine.h"

NSString *const KNotificationMessageBaseCellUpdateSendingStatus = @"KNotificationMessageBaseCellUpdateSendingStatus";

@interface RCMessageBaseCell ()

- (void)setBaseAutoLayout;

@end

@implementation RCMessageBaseCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
  referenceExtraHeight:(CGFloat)extraHeight {
  NSLog(@"Warning, you not implement sizeForMessageModel:withCollectionViewWidth:referenceExtraHeight: method for you custom cell %@", NSStringFromClass(self));
  return CGSizeMake(0, 0);
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setupMessageBaseCellView];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setupMessageBaseCellView];
    }
    return self;
}

-(void)setupMessageBaseCellView{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(messageCellUpdateSendingStatusEvent:)
                                                 name:KNotificationMessageBaseCellUpdateSendingStatus
                                               object:nil];
    self.model = nil;
    self.baseContentView = [[UIView alloc] initWithFrame:CGRectZero];
    _isDisplayReadStatus = NO;
    [self.contentView addSubview:_baseContentView];
}

- (void)setDataModel:(RCMessageModel *)model {
    self.model = model;
    self.messageDirection = model.messageDirection;
    _isDisplayMessageTime = model.isDisplayMessageTime;
    if (self.isDisplayMessageTime) {
        [self.messageTimeLabel setText:[RCKitUtility ConvertChatMessageTime:model.sentTime / 1000] dataDetectorEnabled:NO];
        if (RC_IOS_SYSTEM_VERSION_LESS_THAN(@"7.0")) {
            [self.messageTimeLabel setFont:[UIFont systemFontOfSize:10.0f]];
        }
    }

    [self setBaseAutoLayout];
}
- (void)setBaseAutoLayout {
    if (self.isDisplayMessageTime) {
        CGSize timeTextSize_ = [RCKitUtility getTextDrawingSize:self.messageTimeLabel.text font:[UIFont systemFontOfSize:12.f] constrainedSize:CGSizeMake(self.bounds.size.width, TIME_LABEL_HEIGHT)];
        timeTextSize_ = CGSizeMake(ceilf(timeTextSize_.width + 10), ceilf(timeTextSize_.height));
        
        self.messageTimeLabel.hidden = NO;
        [self.messageTimeLabel setFrame:CGRectMake((self.bounds.size.width - timeTextSize_.width) / 2, 22, timeTextSize_.width, TIME_LABEL_HEIGHT)];
        [_baseContentView setFrame:CGRectMake(0, 3+22 + TIME_LABEL_HEIGHT, self.bounds.size.width,
                                              self.bounds.size.height - (4+10 + TIME_LABEL_HEIGHT))];
    } else {
        if (_messageTimeLabel) {
            self.messageTimeLabel.hidden = YES;
        }
        [_baseContentView setFrame:CGRectMake(0, 0, self.bounds.size.width, self.bounds.size.height - (0))];
    }
}

- (void)messageCellUpdateSendingStatusEvent:(NSNotification *)notification {
    DebugLog(@"%s", __FUNCTION__);
}

//大量cell不显示时间，使用延时加载
- (RCTipLabel *)messageTimeLabel {
    if (!_messageTimeLabel) {
        _messageTimeLabel = [RCTipLabel greyTipLabel];
      _messageTimeLabel.font = [UIFont systemFontOfSize:12.f];
        [self.contentView addSubview:_messageTimeLabel];
    }
    return _messageTimeLabel;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
