//
//  RCConversationSettingTableViewHeaderItem.h
//  RongIMKit
//
//  Created by Liv on 15/3/25.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RCloudImageView;

@protocol RCConversationSettingTableViewHeaderItemDelegate;

@interface RCConversationSettingTableViewHeaderItem : UICollectionViewCell

@property(nonatomic, strong) RCloudImageView *ivAva;
@property(nonatomic, strong) UILabel *titleLabel;
@property(nonatomic, strong) UIButton *btnImg;
@property(nonatomic, copy) NSString *userId;
@property(nonatomic, weak) id<RCConversationSettingTableViewHeaderItemDelegate> delegate;

@end

@protocol RCConversationSettingTableViewHeaderItemDelegate <NSObject>

- (void)deleteTipButtonClicked:(RCConversationSettingTableViewHeaderItem *)item;

@end
