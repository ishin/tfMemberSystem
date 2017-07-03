//
//  RCPublicServiceProfileOwnerCell.h
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RCPublicServiceProfileViewController.h"

@interface RCPublicServiceProfileOwnerCell : UITableViewCell
- (void)setTitle:(NSString *)title
         Content:(NSString *)content
             url:(NSString *)urlString
        delegate:(id<RCPublicServiceProfileViewUrlDelegate>)delegate;
@end
