//
//  Utls.m
//  hkeeping
//
//  Created by jack on 2/24/14.
//  Copyright (c) 2015 G-Wearable Inc. All rights reserved..
//

#import "Utls.h"
#import "UILabel+ContentSize.h"
#include <sys/socket.h>
#include <sys/sysctl.h>
#include <net/if.h>
#include <net/if_dl.h>

@implementation Utls

+ (NSString*) clientVersion{
    
    
    return @"1";
    
}

// Return the local MAC addy
// Courtesy of FreeBSD hackers email list
// Accidentally munged during previous update. Fixed thanks to erica sadun & mlamb.
+ (NSString *) macaddress{
    
    int                 mib[6];
    size_t              len;
    char                *buf;
    unsigned char       *ptr;
    struct if_msghdr    *ifm;
    struct sockaddr_dl  *sdl;
    
    mib[0] = CTL_NET;
    mib[1] = AF_ROUTE;
    mib[2] = 0;
    mib[3] = AF_LINK;
    mib[4] = NET_RT_IFLIST;
    
    if ((mib[5] = if_nametoindex("en0")) == 0) {
        printf("Error: if_nametoindex error\n");
        return NULL;
    }
    
    if (sysctl(mib, 6, NULL, &len, NULL, 0) < 0) {
        printf("Error: sysctl, take 1\n");
        return NULL;
    }
    
    if ((buf = malloc(len)) == NULL) {
        printf("Could not allocate memory. error!\n");
        return NULL;
    }
    
    if (sysctl(mib, 6, buf, &len, NULL, 0) < 0) {
        printf("Error: sysctl, take 2");
        free(buf);
        return NULL;
    }
    
    ifm = (struct if_msghdr *)buf;
    sdl = (struct sockaddr_dl *)(ifm + 1);
    ptr = (unsigned char *)LLADDR(sdl);
    NSString *outstring = [NSString stringWithFormat:@"%02X:%02X:%02X:%02X:%02X:%02X",
                           *ptr, *(ptr+1), *(ptr+2), *(ptr+3), *(ptr+4), *(ptr+5)];
    free(buf);
    
    return outstring;
}



+ (CGSize) testLabelTextSize:(NSString*)txt frame:(CGRect)frame font:(UIFont*)font{
    
    UILabel *tL = [[UILabel alloc] initWithFrame:frame];
    tL.backgroundColor = [UIColor clearColor];
    tL.font = font;
    tL.text = txt;
    tL.numberOfLines = 0;
    tL.lineBreakMode = NSLineBreakByWordWrapping;
    
    CGSize size = [tL contentSize];
    
    return size;
    
}

+ (BOOL) validateEmail:(NSString*)emailStr {
    if (emailStr == nil || [emailStr isEqualToString:@""] || [emailStr length] == 0) {
        return NO;
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES '\\\\w+([-+.]\\\\w+)*@\\\\w+([-.]\\\\w+)*\\\\.\\\\w+([-.]\\\\w+)*'"];
    BOOL ok = [predicate evaluateWithObject:emailStr];
    return ok;
}

+ (UIColor *)groupMaskColorWithId:(int)groupId{
    
    NSMutableArray *_colors = [NSMutableArray array];
    
    [_colors addObject:RGBA(0xc8, 0xb6, 0xee, 0.9)];
    [_colors addObject:RGBA(0x9f, 0xb7, 0xe3, 0.9)];
    [_colors addObject:RGBA(0x7b, 0xd3, 0xd4, 0.9)];
    [_colors addObject:RGBA(0xbd, 0xe7, 0xd4, 0.9)];
    [_colors addObject:RGBA(0xf4, 0xe0, 0x9d, 0.9)];
    [_colors addObject:RGBA(0xf7, 0xba, 0x8a, 0.9)];
    
    [_colors addObject:RGBA(0x86, 0x55, 0x73, 0.9)];
    [_colors addObject:RGBA(0x51, 0x5e, 0x8a, 0.9)];
    [_colors addObject:RGBA(0x87, 0xcb, 0xe2, 0.9)];
    [_colors addObject:RGBA(0xd8, 0xd1, 0xb8, 0.9)];
    [_colors addObject:RGBA(0xc5, 0x95, 0x8a, 0.9)];
    [_colors addObject:RGBA(0xf2, 0x81, 0x86, 0.9)];

    int count = (int)[_colors count];
    
    int idx = groupId%count;
    
    return [_colors objectAtIndex:idx];
}

@end
