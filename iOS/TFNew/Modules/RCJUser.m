//
//  RCJUser.m
//  hkeeping
//
//  Created by jack on 6/2/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "RCJUser.h"
#import "WebClient.h"
#import "SBJson4.h"
#import <RongIMKit/RCIM.h>
#import "GoGoDB.h"

@interface RCJUser ()
{
    WebClient *_http;
    
    BOOL _isLoading;
}

@end

@implementation RCJUser
@synthesize _userId;
@synthesize _userInfo;

@synthesize _groupInfo;
@synthesize _goupId;

- (void) loadUserInfo:(NSString *)userId completion:(void (^)(RCUserInfo *userInfo))completion{
    
    if(_isLoading)
        return;
    _isLoading = YES;
    
    self._userId = userId;
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_USER_PROFILE;
    _http._httpMethod = @"GET";
    
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           _userId,@"userid",
                           nil];
    
    
    IMP_BLOCK_SELF(RCJUser);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        _isLoading = NO;
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    block_self._userInfo = v;
                    
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = _userId;
                    user.name = [v objectForKey:@"name"];
                    user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@",
                                        WEB_API_URL,
                                        [v objectForKey:@"logo"]];
                    
                    [block_self saveUserToLocal:user];

                    if (completion) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            completion(user);
                        });
                    }
                    
                    
                    ///return completion(user);
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
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        _isLoading = NO;
        
    }];
}

- (void) saveUserToLocal:(RCUserInfo*)user{
    
    [[GoGoDB sharedDBInstance] saveUserInfo:user];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"Refresh_Chat_List" object:nil];
    
}

- (void) loadGroupInfo:(NSString *)groupId completion:(void (^)(RCUserInfo *userInfo))completion{
    
    self._goupId = groupId;
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_GROUP_INFO;
    _http._httpMethod = @"GET";
    
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           _goupId,@"groupid",
                           nil];
    
    
    IMP_BLOCK_SELF(RCJUser);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    
                    NSMutableDictionary *ginfo = [v objectForKey:@"text"];
                    
                    [block_self saveGroupToLocal:ginfo];
                    
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = _goupId;
                    user.name = [ginfo objectForKey:@"name"];
                    user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@",
                                        WEB_API_URL, [ginfo objectForKey:@"logo"]];

                    if (completion) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            completion(user);
                        });
                    }
                    //return completion(user);
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
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        
    }];
}


- (void) saveGroupToLocal:(NSMutableDictionary*)ginfo
{
    self._userInfo = ginfo;
    [[GoGoDB sharedDBInstance] saveGroupInfo:ginfo];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"Refresh_Chat_List" object:nil];

}

@end
