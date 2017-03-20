//
//  SHA1.m
//  steven
//
//  Created by Steven on 4/30/09.
//  Copyright 2009 steven. All rights reserved.
//

#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonHMAC.h>
#import <CommonCrypto/CommonCryptor.h>
#import "SHA1.h"
#import "Base64.h"

@implementation SHA1

+ (NSString*) SHA1Digest:(NSString *)src
{
    /*
    NSData *data = [src dataUsingEncoding:NSUTF8StringEncoding];
    unsigned char hash[CC_SHA1_DIGEST_LENGTH];
    (void) CC_SHA1( [data bytes], (CC_LONG)[data length], hash );
    NSData *resultD = ( [NSData dataWithBytes: hash length: CC_SHA1_DIGEST_LENGTH] );
    NSString *resultS = [Base64 stringByEncodingData:resultD];
    return resultS;
     */
    
    NSData *data = [src dataUsingEncoding:NSUTF8StringEncoding];
    unsigned char hash[CC_SHA1_DIGEST_LENGTH];
    (void) CC_SHA1( [data bytes], (CC_LONG)[data length], hash );
    
    NSMutableString* output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    
    for(int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++)
        [output appendFormat:@"%02x", hash[i]];
    
    return output;
    
}

+ (NSString *)hmac_sha1:(NSString *)key text:(NSString *)text{

	const char *cKey  = [key cStringUsingEncoding:NSUTF8StringEncoding];
    const char *cData = [text cStringUsingEncoding:NSUTF8StringEncoding];
	
	char cHMAC[CC_SHA1_DIGEST_LENGTH];
	
	CCHmac(kCCHmacAlgSHA1, cKey, strlen(cKey), cData, strlen(cData), cHMAC);
	
	NSData *HMAC = [[NSData alloc] initWithBytes:cHMAC length:CC_SHA1_DIGEST_LENGTH];
    NSString *hash = [Base64 stringByEncodingData:HMAC];

	return hash;
}
 

@end
