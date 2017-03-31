//
//  UIButton+Color.m
//  TestNewIOS7
//
//  Created by mac on 13-8-31.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import "UIButton+Color.h"
#import "UIImage+Color.h"


@implementation UIButton (ColorButton)

+ (id)buttonWithColor:(UIColor*)nomalColor selColor:(UIColor*)selColor
{
    UIButton *imgBtn = [UIButton buttonWithType:UIButtonTypeCustom];

    if(nomalColor)
    {
        UIImage *nimage = [UIImage imageWithColor:nomalColor andSize:CGSizeMake(1, 1)];
        [imgBtn setBackgroundImage:nimage forState:UIControlStateNormal];
    }
    
    if(selColor)
    {
        UIImage *simage = [UIImage imageWithColor:selColor andSize:CGSizeMake(1, 1)];
        [imgBtn setBackgroundImage:simage forState:UIControlStateHighlighted];
        [imgBtn setBackgroundImage:simage forState:UIControlStateSelected];
        [imgBtn setBackgroundImage:simage forState:UIControlStateDisabled];
    }
    
    return imgBtn;
}

- (void)setImageColor:(UIColor*)nomalColor{
    
    if(nomalColor)
    {
        UIImage *nimage = [UIImage imageWithColor:nomalColor andSize:CGSizeMake(1, 1)];
        [self setBackgroundImage:nimage forState:UIControlStateNormal];
    }

}
@end
