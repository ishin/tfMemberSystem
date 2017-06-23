//
//  RCPublicServiceMultiImgTxtCellContentCell.h
//  RongIMKit
//
//  Created by litao on 15/4/15.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMLib/RongIMLib.h>
#import "RCMessageCellDelegate.h"

@interface RCPublicServiceMultiImgTxtCellContentCell : UITableViewCell
@property(nonatomic) BOOL isShowline;
@property(strong, nonatomic) RCRichContentMessage *richContent;
@property (nonatomic, weak)id<RCPublicServiceMessageCellDelegate> publicServiceDelegate;
- (instancetype)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier;
+ (CGSize)getContentCellSize:(RCRichContentMessage *)richContent withWidth:(CGFloat)width;
@end
