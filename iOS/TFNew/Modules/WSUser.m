//
//  WSUser.m
//  ws
//
//  Created by jack on 1/17/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import "WSUser.h"
#import "SBJson4.h"


@interface WSUser ()
{
    WebClient *_http;
    
    WebClient *_gpsUpdate;
    
    NSTimer *_timer;
}

@end

@implementation WSUser

@synthesize realname;
@synthesize _isSelect;
@synthesize _latitude;
@synthesize _longitude;

- (id) initWithDictionary:(NSDictionary*)data{
    
    if(self = [super init])
    {
        self.userId = [[data objectForKey:@"id"] intValue];
        
        self.account        = [data objectForKey:@"account"];
        
        if([data objectForKey:@"name"])
            self.fullname       = [data objectForKey:@"name"];
        else if([data objectForKey:@"fullname"])
            self.fullname       = [data objectForKey:@"fullname"];
        
        self.realname       = self.fullname;
        self.cellphone      = [data objectForKey:@"mobile"];
        self.email          = [data objectForKey:@"email"];
        self.avatarurl      = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [data objectForKey:@"logo"]];
        self.address        = [data objectForKey:@"address"];
        
        self.companyname    = [data objectForKey:@"organname"];
        self.telphone       = [data objectForKey:@"telephone"];
        self.ranktitle      = [data objectForKey:@"postitionname"];

        
        self.ctime          = [data objectForKey:@"ctime"];
      
    }
    
    
    return self;
}

- (void) updateWithDictionary:(NSDictionary*)data{
    
    
    self.account        = [data objectForKey:@"account"];
    if([data objectForKey:@"name"])
        self.fullname       = [data objectForKey:@"name"];
    else if([data objectForKey:@"fullname"])
        self.fullname       = [data objectForKey:@"fullname"];
    
    self.realname       = self.fullname;
    self.cellphone      = [data objectForKey:@"mobile"];
    self.email          = [data objectForKey:@"email"];
    self.avatarurl      = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [data objectForKey:@"logo"]];
    self.address        = [data objectForKey:@"address"];
    
    self.companyname    = [data objectForKey:@"organname"];
    self.telphone       = [data objectForKey:@"telephone"];
    self.ranktitle      = [data objectForKey:@"postitionname"];
    
    
    self.ctime          = [data objectForKey:@"ctime"];
    
    
}

- (void) addMyFriend:(NSString*)friendAccount{
    
    if(self.account == nil || friendAccount == nil)
        return;
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._httpMethod = @"POST";
    _http._method = API_INVITE_FRIEND;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];

    [params setObject:self.account forKey:@"account"];
    [params setObject:friendAccount forKey:@"friend"];
    
    
    _http._requestParam = params;
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        
        
    } FailBlock:^(id lParam, id rParam) {
        
       
        
    }];

}

- (void) startGpsUpdage{
    
    if(_gpsUpdate == nil)
    {
        _gpsUpdate = [[WebClient alloc] initWithDelegate:self];
    }
    
    _gpsUpdate._httpMethod = @"GET";
    _gpsUpdate._method = @"/map!getLocation";
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    [params setObject:[NSString stringWithFormat:@"%d", self.userId] forKey:@"userid"];
    [params setObject:[NSString stringWithFormat:@"%d", self.userId] forKey:@"targetid"];
    [params setObject:@"2" forKey:@"type"];
    
    _gpsUpdate._requestParam = params;
    
    IMP_BLOCK_SELF(WSUser);
    
    [_gpsUpdate requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    NSArray *text = [v objectForKey:@"text"];
                    
                    if([text isKindOfClass:[NSArray class]] && [text count])
                    {
                        NSDictionary *dic = [text objectAtIndex:0];
                        block_self._longitude = [[dic objectForKey:@"longtitude"] doubleValue];
                        block_self._latitude = [[dic objectForKey:@"latitude"] doubleValue];
                        
                        [block_self notifyGPSUpdate];
                    }
                    else
                    {
                        
                    }
                    
                    
                }
                
                return;
            }
            
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            
            
            
            NSLog(@"OOPS: %@", err);
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];

        
    } FailBlock:^(id lParam, id rParam) {
        
        
        
    }];

}

- (void) notifyGPSUpdate{
    
    if(_timer && [_timer isValid])
    {
        [_timer invalidate];
        _timer = nil;
    }
    
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"GPS_Notify_update" object:self];
    
    _timer = [NSTimer scheduledTimerWithTimeInterval:30
                                              target:self
                                            selector:@selector(startGpsUpdage)
                                            userInfo:nil
                                             repeats:NO];
    
    
}

- (void) stopGpsUpdate{
    
    if(_timer && [_timer isValid])
    {
        [_timer invalidate];
        _timer = nil;
    }
}


@end
