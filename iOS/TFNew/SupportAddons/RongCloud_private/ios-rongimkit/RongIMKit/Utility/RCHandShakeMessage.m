//
//  RCHandShakeMessage.m
//  iOS-IMKit
//
//  Created by Heq.Shinoda on 14-6-30.
//  Copyright (c) 2014年 Heq.Shinoda. All rights reserved.
//

#import "RCHandShakeMessage.h"

@implementation RCHandShakeMessage

-(RCHandShakeMessage*)init
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
//    __autoreleasing NSError* error = nil;
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
    return RCHandShakeMessageTypeIdentifier;
}

@end
