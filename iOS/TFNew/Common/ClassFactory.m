//
//  ClassFactory.m
//  hkeeping
//
//  Created by jack on 2/24/14.
//  Copyright (c) 2015 G-Wearable Inc. All rights reserved..
//

#import "ClassFactory.h"

@implementation ClassFactory

+ (UILabel*) createLabelWith:(CGRect)frame font:(UIFont*)font textColor:(UIColor*)color{
    
    UILabel *tL = [[UILabel alloc] initWithFrame:frame];
    tL.backgroundColor = [UIColor clearColor];
    tL.textColor = color;
    tL.font = font;
   
    return tL;
   
}
@end
