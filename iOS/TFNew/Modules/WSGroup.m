//
//  WSGroup.m
//  ws
//
//  Created by jack on 1/17/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import "WSGroup.h"

@interface WSGroup ()
{
   // WebClient *_http;
}

@end

@implementation WSGroup

@synthesize groupName;
@synthesize groupCode;
@synthesize groupId;
@synthesize createDate;
@synthesize creator;
@synthesize _membs;

- (id) initWithDictionary:(NSDictionary*)data{
    
    if(self = [super init])
    {
        self.groupCode = [data objectForKey:@"code"];
        self.groupName = [data objectForKey:@"name"];
        self.createDate = [data objectForKey:@"createdate"];
        
        int gid = [[data objectForKey:@"GID"] intValue];
        self.groupId = [NSString stringWithFormat:@"%d", gid];
        
        
        self.creator = [[WSUser alloc] init];
        
        //SB接口，获取群组列表接口返回mid=创建者id，而群组信息接口返回id=创建者id
        if([data objectForKey:@"mid"])
        {
            creator.userId = [[data objectForKey:@"mid"] intValue];
        }
        else
        {
            creator.userId = [[data objectForKey:@"id"] intValue];
        }
        creator.fullname       = [data objectForKey:@"fullname"];
        creator.avatarurl      = [NSString stringWithFormat:@"%@/upload/images/%@",
                                  WEB_API_URL,
                                  [data objectForKey:@"logo"]];
        creator.cellphone = [data objectForKey:@"mobile"];
        creator.account = [data objectForKey:@"account"];
        creator.email  = [data objectForKey:@"email"];
        
    }
    
    
    return self;
}

- (void) updateDictionary:(NSDictionary*)data{
    
    self.groupCode = [data objectForKey:@"code"];
    self.groupName = [data objectForKey:@"name"];
    self.createDate = [data objectForKey:@"createdate"];
    
    self.creator = [[WSUser alloc] init];
    
    //SB接口，获取群组列表接口返回mid=创建者id，而群组信息接口返回id=创建者id
    if([data objectForKey:@"mid"])
    {
        creator.userId = [[data objectForKey:@"mid"] intValue];
    }
    else
    {
        creator.userId = [[data objectForKey:@"id"] intValue];
    }
    creator.fullname       = [data objectForKey:@"fullname"];
    creator.avatarurl      = [NSString stringWithFormat:@"%@/upload/images/%@",
                              WEB_API_URL,
                              [data objectForKey:@"logo"]];
    creator.cellphone = [data objectForKey:@"mobile"];
    creator.account = [data objectForKey:@"account"];
    creator.email  = [data objectForKey:@"email"];
}

@end
