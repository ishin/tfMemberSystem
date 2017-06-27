//
//  RCCommonDefine.h
//  RongCloud
//
//  Created by Heq.Shinoda on 14-4-21.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//

#ifndef __RCCommonDefine
#define __RCCommonDefine

#import <Foundation/Foundation.h>

//---------------Macro Definination---------//
/**************************************************
 Description: Used for ARC mode or not.
 Author:    Hequn
 ***************************************************/
//- ARC not used -
#if ! __has_feature(objc_arc)
#define RCAutorelease(__obj) ([__obj autorelease]);
#define RCReturnAutoreleased RCAutorelease

#define RCRetain(__obj) ([__obj retain]);
#define RCReturnRetained RCRetain

#define RCRelease(__obj) ([__obj release]);
#else//__has_feature(objc_arc)
//- ARC used -
#define RCAutorelease(__obj)
#define RCReturnAutoreleased(__obj) (__obj)

#define RCRetain(__obj)
#define RCReturnRetained(__obj) (__obj)

#define RCRelease(__obj)
#endif//__has_feature(objc_arc)

#define SAFE_DELETE(p)  do\
    { \
        if(p != nil) \
        {\
            [p RCRelease]; \
            p = nil; \
        }\
    }while(0)


#ifdef DEBUG
#define DebugLog( s, ... ) NSLog( @"[%@:(%d)] %@", [[NSString stringWithUTF8String:__FILE__] lastPathComponent], __LINE__, [NSString stringWithFormat:(s), ##__VA_ARGS__] )
#else
#define DebugLog( s, ... )
#endif


#endif
