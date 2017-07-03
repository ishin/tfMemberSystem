//
//  RCPublicServiceMultiImgTxtCellHeaderCell.h
//  RongIMKit
//
//  Created by litao on 15/4/15.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMLib/RongIMLib.h>
#import "RCMessageCellDelegate.h"

@interface RCPublicServiceMultiImgTxtCellHeaderCell : UITableViewCell
@property (nonatomic, weak)id<RCPublicServiceMessageCellDelegate> publicServiceDelegate;
- (instancetype)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier;
@property(strong, nonatomic) RCRichContentMessage *richContent;
+ (CGFloat)getHeaderCellHeight;
@end
