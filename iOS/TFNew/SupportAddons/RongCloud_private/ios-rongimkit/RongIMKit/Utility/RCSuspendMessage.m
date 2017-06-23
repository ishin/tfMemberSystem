//
//  RCInterruptMessage.m
//  iOS-IMKit
//
//  Created by xugang on 15/1/12.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//


#import "RCSuspendMessage.h"

@implementation RCSuspendMessage

-(instancetype)init
{
    self = [super init];
    if(self)
    {
        //self.type = aType;
    }
    return self;
}
-(NSData *)encode {
//    NSDictionary* dataDict = @{@"type": @(self.type)};
//    NSError *error = nil;
//    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dataDict
//                                                       options:kNilOptions
//                                                         error:&error];
//    NSString *jsonString = [[NSString alloc] initWithData:jsonData
//                                                 encoding:NSUTF8StringEncoding];
//    
//    return [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    return nil;
}

-(void)decodeWithData:(NSData *)data {
   // __autoreleasing NSError* error = nil;
//    NSDictionary* jsonDict = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
//    if (error) {
//        self.rawJSONData = data; //[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];;
//    } else {
//        self.type = [jsonDict[@"type"] intValue];
//    }
}

+(RCMessagePersistent)persistentFlag {
    return MessagePersistent_NONE;
}

+(NSString *)getObjectName {
    return RCInterruptMessageTypeIdentifier;
}

@end
