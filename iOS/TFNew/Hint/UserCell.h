//
//  UserCell.h
//  ZHEvent
//
//  Created by jack on 8/29/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SSUser;

@interface UserCell : UITableViewCell
{
    
}
@property (nonatomic, readonly) UIButton *_btnAdd;

- (void) fillData:(SSUser*) person;

@end
