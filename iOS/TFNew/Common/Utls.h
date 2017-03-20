//
//  Utls.h
//  hkeeping
//
//  Created by jack on 2/24/14.
//  Copyright (c) 2015 G-Wearable Inc. All rights reserved..
//

#import <Foundation/Foundation.h>

@interface Utls : NSObject

+ (CGSize) testLabelTextSize:(NSString*)txt frame:(CGRect)frame font:(UIFont*)font;

+ (BOOL) validateEmail:(NSString*)emailStr;

+ (NSString *) macaddress;
+ (NSString*) clientVersion;

+ (UIColor *)groupMaskColorWithId:(int)groupId;

@end
