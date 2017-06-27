//
//  RCPublicServiceProfileUrlCell.h
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RCPublicServiceProfileViewController.h"

@interface RCPublicServiceProfileUrlCell : UITableViewCell
- (void)setTitle:(NSString *)title url:(NSString *)urlString delegate:(id<RCPublicServiceProfileViewUrlDelegate>)delegate;
@end
