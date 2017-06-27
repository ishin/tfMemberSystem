//
//  RCPublicServiceProfileActionCell.h
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol RCPublicServiceProfileActionDelegate
- (void)onAction;
@end

@interface RCPublicServiceProfileActionCell : UITableViewCell
- (void)setTitleText:(NSString *)title andBackgroundColor:(UIColor *)color;

typedef void (^clickDone)();

@property(nonatomic, copy) clickDone onClickEvent;

@end
