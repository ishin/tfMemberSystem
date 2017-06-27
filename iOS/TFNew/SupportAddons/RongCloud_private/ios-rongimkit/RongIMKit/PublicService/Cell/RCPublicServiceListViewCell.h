//
//  RCPublicServiceListViewCell.h
//  HelloIos
//
//  Created by litao on 15/4/9.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RCThemeDefine.h"
#import "RCloudImageView.h"
@interface RCPublicServiceListViewCell : UITableViewCell
@property(nonatomic, strong) RCloudImageView *headerImageView;
@property(nonatomic) RCUserAvatarStyle portraitStyle;
@property(nonatomic, strong) NSString *searchKey;
- (void)setName:(NSString *)name;
- (void)setDescription:(NSString *)description;

@end
