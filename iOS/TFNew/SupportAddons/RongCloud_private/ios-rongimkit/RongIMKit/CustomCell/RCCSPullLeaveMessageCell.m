//
//  RCCSLeaveMsgCell.m
//  RongIMKit
//
//  Created by 张改红 on 2016/12/7.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCCSPullLeaveMessageCell.h"
#import "RCKitUtility.h"
#import "RCKitCommonDefine.h"

@implementation RCCSPullLeaveMessageCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
         referenceExtraHeight:(CGFloat)extraHeight {
  
  RCMessageContent *notification = model.content;
  NSString *localizedMessage = [RCKitUtility formatMessage:notification];
  CGFloat maxMessageLabelWidth = collectionViewWidth - 30 * 2;
  CGSize __textSize = [RCKitUtility getTextDrawingSize:localizedMessage font:[UIFont systemFontOfSize:14.f] constrainedSize:CGSizeMake(maxMessageLabelWidth, MAXFLOAT)];
  __textSize = CGSizeMake(ceilf(__textSize.width), ceilf(__textSize.height));
  CGSize __labelSize =
  CGSizeMake(__textSize.width + 5, __textSize.height + 6);
  
  CGFloat __height  = __labelSize.height;
  
  __height += extraHeight;
  
  return CGSizeMake(collectionViewWidth, __height);
}

- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    self.contentLabel = [[RCAttributedLabel alloc] init];
    self.contentLabel.font = [UIFont systemFontOfSize:14.f];
    self.contentLabel.numberOfLines = 0;
    self.contentLabel.lineBreakMode = NSLineBreakByCharWrapping;
    self.contentLabel.textAlignment = NSTextAlignmentCenter;
    self.contentLabel.textColor = [UIColor whiteColor];
    self.contentLabel.layer.masksToBounds = YES;
    self.contentLabel.layer.cornerRadius = 5.f;
    self.contentLabel.backgroundColor = HEXCOLOR(0xc9c9c9);
    self.contentLabel.delegate=self;
    [self.baseContentView addSubview:self.contentLabel];
    self.contentLabel.userInteractionEnabled = YES;
  }
  return self;
}

- (void)setDataModel:(RCMessageModel *)model {
  [super setDataModel:model];
  RCMessageContent *message = model.content;
  CGFloat maxMessageLabelWidth = self.baseContentView.bounds.size.width - 30 * 2;
  NSString *text = nil;
  if ([message isMemberOfClass:[RCCSPullLeaveMessage class]]) {
    text = [RCKitUtility formatMessage:message];
  }
  
  CGSize __textSize = [RCKitUtility getTextDrawingSize:text font:[UIFont systemFontOfSize:14.0f] constrainedSize:CGSizeMake(maxMessageLabelWidth, MAXFLOAT)];
  __textSize = CGSizeMake(ceilf(__textSize.width), ceilf(__textSize.height));
  CGSize __labelSize = CGSizeMake(__textSize.width + 10, __textSize.height + 6);
  self.contentLabel.frame = CGRectMake((self.baseContentView.bounds.size.width - __labelSize.width) / 2.0f - 5, 10,__labelSize.width+10, __labelSize.height);

  NSTextCheckingResult *textCheckingResult = [NSTextCheckingResult linkCheckingResultWithRange:[text rangeOfString:@"留言"] URL:[NSURL URLWithString:nil]];
  self.contentLabel.text = text;
  [self.contentLabel.attributedStrings addObject:textCheckingResult];
  [self.contentLabel setTextHighlighted:YES atPoint:CGPointMake(0, 3)];
}

- (void)attributedLabel:(RCAttributedLabel *)label didSelectLinkWithURL:(NSURL *)url{
  if (self.delegate && [self.delegate respondsToSelector:@selector(didTapMessageCell:)]) {
    [self.delegate didTapMessageCell:self.model];
  }
}
@end
