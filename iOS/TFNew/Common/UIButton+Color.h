//
//  UIButton+Color.h
//  TestNewIOS7
//
//  Created by mac on 13-8-31.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIButton (ColorButton)

+ (id)buttonWithColor:(UIColor*)nomalColor selColor:(UIColor*)selColor;
- (void)setImageColor:(UIColor*)nomalColor;

@end
