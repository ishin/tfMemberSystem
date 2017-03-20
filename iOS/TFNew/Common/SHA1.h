//
//  SHA1.h
//  steven
//
//  Created by Steven on 4/30/09.
//  Copyright 2009 steven. All rights reserved.
//




@interface SHA1 : NSObject {}

+ (NSString *) SHA1Digest:(NSString *)src;
+ (NSString *)hmac_sha1:(NSString *)skey text:(NSString *)stext;
@end
