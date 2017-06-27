//
//  RCOldMessageNotificationMessageCell.m
//  RongIMKit
//
//  Created by 杜立召 on 15/8/24.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCOldMessageNotificationMessageCell.h"
#import "RCTipLabel.h"
#import "RCKitUtility.h"
#import "RCKitCommonDefine.h"

@interface RCOldMessageNotificationMessageCell()
@property(nonatomic,strong)UIView *leftView;
@property(nonatomic,strong)UIView *rightView;
@end

@implementation RCOldMessageNotificationMessageCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
  referenceExtraHeight:(CGFloat)extraHeight {
  CGFloat height = 40.f;
  return CGSizeMake(collectionViewWidth, height);
}
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        RCTipLabel *tip = [[RCTipLabel alloc] init];
        tip.marginInsets = UIEdgeInsetsMake(5.f, 5.f, 5.f, 5.f);
        tip.textColor = HEXCOLOR(0xBBBBBB);
        tip.numberOfLines = 0;
        tip.lineBreakMode = NSLineBreakByCharWrapping;
        tip.textAlignment = NSTextAlignmentCenter;
        tip.font = [UIFont systemFontOfSize:12.5f];
        tip.layer.masksToBounds = YES;
        tip.layer.cornerRadius = 5.f;
        self.tipMessageLabel = tip;
        [self.baseContentView addSubview:self.tipMessageLabel];
        self.tipMessageLabel.marginInsets = UIEdgeInsetsMake(0.5f, 0.5f, 0.5f, 0.5f);
        self.leftView = [[UIView alloc] init];
        self.leftView.backgroundColor= HEXCOLOR(0xBBBBBB);
        self.leftView.alpha = 0.5;
        [self.baseContentView addSubview:self.leftView];
        self.rightView = [[UIView alloc] init];
        self.rightView.backgroundColor= HEXCOLOR(0xBBBBBB);
        self.rightView.alpha = 0.5;
        [self.baseContentView addSubview:self.rightView];
    }
    return self;
}

- (void)setDataModel:(RCMessageModel *)model {
    [super setDataModel:model];
    CGFloat maxMessageLabelWidth = [self labelWiden:self.tipMessageLabel];
    NSString *__text = NSLocalizedStringFromTable(@"HistoryMessageTip",@"RongCloudKit",nil);
    CGSize __textSize = [RCKitUtility getTextDrawingSize:__text font:[UIFont systemFontOfSize:12.5f] constrainedSize:CGSizeMake(maxMessageLabelWidth, MAXFLOAT)];
    __textSize = CGSizeMake(ceilf(__textSize.width), ceilf(__textSize.height));
    CGSize __labelSize = CGSizeMake(__textSize.width + 10, __textSize.height + 6);
    self.tipMessageLabel.text = __text ;
    self.tipMessageLabel.frame = CGRectMake((self.baseContentView.bounds.size.width - __labelSize.width) / 2.0f, 10,__labelSize.width, __labelSize.height);
    
    [self.leftView setFrame: CGRectMake(10,CGRectGetMidY(self.tipMessageLabel.frame)-0.5, CGRectGetMinX(self.tipMessageLabel.frame)-17,1)];
    
    [self.rightView setFrame: CGRectMake(CGRectGetMaxX(self.tipMessageLabel.frame)+7, CGRectGetMinY(self.leftView.frame), CGRectGetWidth(self.baseContentView.frame)-7-CGRectGetMaxX(self.tipMessageLabel.frame)-10, 1)];
}

- (CGFloat)labelWiden:(UILabel *)sender{
    CGRect rect = [sender.text boundingRectWithSize:CGSizeMake(2000,sender.frame.size.height) options:(NSStringDrawingUsesLineFragmentOrigin) attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]} context:nil];
    return  rect.size.width;
}

@end
