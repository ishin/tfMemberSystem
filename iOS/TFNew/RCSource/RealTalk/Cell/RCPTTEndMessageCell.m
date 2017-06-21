//
//  RCPTTEndMessageCell.m
//  RongPTTKit
//
//  Created by Sin on 17/1/16.
//  Copyright © 2017年 RongCloud. All rights reserved.
//

#import "RCPTTEndMessageCell.h"
#import <RongPTTLib/RongPTTLib.h>

#define Default_Text @"语音对讲已结束"

@interface RCPTTEndMessageCell ()

@property (nonatomic, strong) NSMutableSet *relatedUserIdList;

@end

@implementation RCPTTEndMessageCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
         referenceExtraHeight:(CGFloat)extraHeight {
  
  NSString *localizedMessage = Default_Text;
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
    self.tipMessageLabel = [RCTipLabel greyTipLabel];
    self.tipMessageLabel.userInteractionEnabled = YES;
    [self.baseContentView addSubview:self.tipMessageLabel];
    self.tipMessageLabel.marginInsets = UIEdgeInsetsMake(0.5f, 0.5f, 0.5f, 0.5f);
  }
  return self;
}

- (void)setDataModel:(RCMessageModel *)model {
  [super setDataModel:model];
  
  CGFloat maxMessageLabelWidth = self.baseContentView.bounds.size.width - 30 * 2;
  
  
  self.tipMessageLabel.text = Default_Text;
  
  NSString *__text = self.tipMessageLabel.text;
  CGSize __textSize = [RCKitUtility getTextDrawingSize:__text font:[UIFont systemFontOfSize:14.0f] constrainedSize:CGSizeMake(maxMessageLabelWidth, MAXFLOAT)];
  __textSize = CGSizeMake(ceilf(__textSize.width), ceilf(__textSize.height));
  CGSize __labelSize = CGSizeMake(__textSize.width + 10, __textSize.height + 6);
  
  self.tipMessageLabel.frame = CGRectMake((self.baseContentView.bounds.size.width - __labelSize.width) / 2.0f - 5, 10,__labelSize.width+10, __labelSize.height);
}
@end
