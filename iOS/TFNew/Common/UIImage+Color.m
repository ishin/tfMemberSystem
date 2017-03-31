//
//  UIImage+Color.m
//  TestNewIOS7
//
//  Created by mac on 13-8-31.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import "UIImage+Color.h"

@implementation UIImage (ColorImage)


+ (UIImage*)imageWithColor:(UIColor *)color andSize:(CGSize)size
{
    UIImage *img = nil;
    
    //NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    
    CGRect rect = CGRectMake(0, 0, size.width, size.height);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context,
                                   color.CGColor);
    CGContextFillRect(context, rect);
    img = UIGraphicsGetImageFromCurrentImageContext();
    //[img retain];
    UIGraphicsEndImageContext();
    
    //[pool release];
    
    return img;
}


@end
