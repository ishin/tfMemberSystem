//
//  DataSync.m
//  Hint
//
//  Created by jack on 1/16/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import "DataSync.h"
#import "WebClient.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "NSDate-Helper.h"
#import "GoGoDB.h"
#import "WaitDialog.h"

@interface DataSync ()
{
    WebClient *_syncClient;
    WebClient *_client;
    WebClient *_orgClient;
}

@end


@implementation DataSync


static DataSync* dSyncInstance = nil;

+ (DataSync*)sharedDataSync{
    
    if(dSyncInstance == nil){
        dSyncInstance = [[DataSync alloc] init];
    }
    
    return dSyncInstance;
}

- (id) init
{
    
    if(self = [super init])
    {
      
    }
    
    return self;
    
}


- (void) syncMyContacts{
    
    if(_syncClient == nil)
        _syncClient = [[WebClient alloc] initWithDelegate:self];
    
    _syncClient._httpMethod = @"GET";
    _syncClient._method = API_USER_FRINEDS;
    
    User *u = [UserDefaultsKV getUser];
    if(u == nil)
        return;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:u._account forKey:@"account"];
    _syncClient._requestParam = params;
    
    IMP_BLOCK_SELF(DataSync);

    [_syncClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 1)
                {
                    NSArray *listing = [v objectForKey:@"text"];
                    [block_self processFriends:listing];
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

- (void) processFriends:(NSArray*)listing{
 
    [[GoGoDB sharedDBInstance] deleteFriends];
    
    for(NSDictionary *dic in listing)
    {
        [[GoGoDB sharedDBInstance] insertAFriend:dic];
    }
    
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"ReceivedNameCardsSyncMessagesNotify" object:nil];
}



- (void) syncMyGroups{


    if(_client == nil)
    {
        _client = [[WebClient alloc] initWithDelegate:self];
    }
    
    _client._method = API_GROUP_LISTING;
    _client._httpMethod = @"GET";
    
    User *u = [UserDefaultsKV getUser];
    
    //scope = 0全部，1-公司，2-活动
    NSMutableDictionary *param = [NSMutableDictionary dictionary];
    
    if(u._userId)
    {
        [param setObject:u._userId forKey:@"userid"];
    }
    
    _client._requestParam = param;
    
    IMP_BLOCK_SELF(DataSync);
    
    [_client requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        

        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    NSArray *groups = [v objectForKey:@"text"];
                    [block_self saveGroups:groups];
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

- (void) saveGroups:(NSArray*)list{
 
    for(NSDictionary *dic in list)
    {
        [[GoGoDB sharedDBInstance] saveGroupInfo:dic];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"GroupsSyncMessagesNotify" object:nil];
}

- (void) syncTFOrgs{
    
    if(_orgClient == nil)
        _orgClient = [[WebClient alloc] initWithDelegate:self];
    
    _orgClient._httpMethod = @"GET";
    _orgClient._method = @"/branch!getBranchTreeAndMember";
    
    IMP_BLOCK_SELF(DataSync);
    
    [_orgClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSArray class]])
            {
                [block_self saveOrgs:v];
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

- (void) saveOrgs:(NSArray*)list{
    
    [[GoGoDB sharedDBInstance] deleteAllOrgs];
    
    for(NSDictionary *dic in list)
    {
        [[GoGoDB sharedDBInstance] insertOrgUnit:dic];
    }
}

@end
