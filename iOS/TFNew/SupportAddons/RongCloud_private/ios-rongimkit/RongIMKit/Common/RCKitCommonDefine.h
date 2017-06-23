//
//  RCKitCommonDefine.h
//  RongIMKit
//
//  Created by xugang on 15/1/22.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#pragma mark - Color

#define RGBCOLOR(r, g, b) [UIColor colorWithRed:(r) / 255.0f green:(g) / 255.0f blue:(b) / 255.0f alpha:1]
#define RGBACOLOR(r, g, b, a) [UIColor colorWithRed:(r) / 255.0f green:(g) / 255.0f blue:(b) / 255.0f alpha:(a)]
#define HEXCOLOR(rgbValue)                                                                                             \
    [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16)) / 255.0                                               \
                    green:((float)((rgbValue & 0xFF00) >> 8)) / 255.0                                                  \
                     blue:((float)(rgbValue & 0xFF)) / 255.0                                                           \
                    alpha:1.0]

#pragma mark - System Version

#define RC_IOS_SYSTEM_VERSION_EQUAL_TO(v)                                      \
  ([[[UIDevice currentDevice] systemVersion]                                   \
       compare:v                                                               \
       options:NSNumericSearch] == NSOrderedSame)
#define RC_IOS_SYSTEM_VERSION_GREATER_THAN(v)                                  \
  ([[[UIDevice currentDevice] systemVersion]                                   \
       compare:v                                                               \
       options:NSNumericSearch] == NSOrderedDescending)
#define RC_IOS_SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)                      \
  ([[[UIDevice currentDevice] systemVersion]                                   \
       compare:v                                                               \
       options:NSNumericSearch] != NSOrderedAscending)
#define RC_IOS_SYSTEM_VERSION_LESS_THAN(v)                                     \
  ([[[UIDevice currentDevice] systemVersion]                                   \
       compare:v                                                               \
       options:NSNumericSearch] == NSOrderedAscending)
#define RC_IOS_SYSTEM_VERSION_LESS_THAN_OR_EQUAL_TO(v)                         \
  ([[[UIDevice currentDevice] systemVersion]                                   \
       compare:v                                                               \
       options:NSNumericSearch] != NSOrderedDescending)

#pragma mark - 已废弃接口

//当前版本
#define IOS_FSystenVersion ([[[UIDevice currentDevice] systemVersion] floatValue])
#define IOS_DSystenVersion ([[[UIDevice currentDevice] systemVersion] doubleValue])
#define IOS_SSystemVersion ([[UIDevice currentDevice] systemVersion])

#define IMAGENAEM(Value)                                                                                               \
    [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:NSLocalizedString(Value, nil) ofType:nil]]

#define USE_BUNDLE_RESOUCE 1

#if USE_BUNDLE_RESOUCE
#define IMAGE_BY_NAMED(value)                                                  \
  [RCKitUtility imageNamed:(value) ofBundle:@"RongCloud.bundle"]
#else
#define IMAGE_BY_NAMED(value)                                                  \
  [UIImage imageNamed:NSLocalizedString((value), nil)]
#endif // USE_BUNDLE_RESOUCE

#define SCREEN_HEIGHT [[UIScreen mainScreen] bounds].size.height
#define SCREEN_WIDTH [[UIScreen mainScreen] bounds].size.width

#define APP_SCREEN_HEIGHT [[UIScreen mainScreen] applicationFrame].size.height
#define APP_SCREEN_WIDTH [[UIScreen mainScreen] applicationFrame].size.width

//当前语言
#define CURRENTLANGUAGE ([[NSLocale preferredLanguages] objectAtIndex:0])

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
#define RC_MULTILINE_TEXTSIZE(text, font, maxSize, mode) [text length] > 0 ? [text \
boundingRectWithSize:maxSize options:(NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading) \
attributes:@{NSFontAttributeName:font} context:nil].size : CGSizeZero;
#else
#define RC_MULTILINE_TEXTSIZE(text, font, maxSize, mode) [text length] > 0 ? [text \
sizeWithFont:font constrainedToSize:maxSize lineBreakMode:mode] : CGSizeZero;
#endif

// 大于等于IOS7
#define RC_MULTILINE_TEXTSIZE_GEIOS7(text, font, maxSize) [text length] > 0 ? [text \
boundingRectWithSize:maxSize options:(NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading) \
attributes:@{NSFontAttributeName:font} context:nil].size : CGSizeZero;

// 小于IOS7
#define RC_MULTILINE_TEXTSIZE_LIOS7(text, font, maxSize, mode) [text length] > 0 ? [text \
sizeWithFont:font constrainedToSize:maxSize lineBreakMode:mode] : CGSizeZero;
