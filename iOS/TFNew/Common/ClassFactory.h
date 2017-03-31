//
//  ClassFactory.h
//  hkeeping
//
//  Created by jack on 2/24/14.
//  Copyright (c) 2015 G-Wearable Inc. All rights reserved..
//

#import <Foundation/Foundation.h>

@interface ClassFactory : NSObject

+ (UILabel*) createLabelWith:(CGRect)frame font:(UIFont*)font textColor:(UIColor*)color;

@end
