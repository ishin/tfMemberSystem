//
//  RCJUser.h
//  hkeeping
//
//  Created by jack on 6/2/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <RongIMKit/RCIM.h>


//typeof (void (^)(RCUserInfo *userInfo))

@interface RCJUser : NSObject
{
    
}
@property (nonatomic, strong) NSMutableDictionary* _userInfo;
@property (nonatomic, strong) NSString *_userId;

@property (nonatomic, strong) NSMutableDictionary* _groupInfo;
@property (nonatomic, strong) NSString *_goupId;
//@property (nonatomic, copy)  complete;

- (void) loadUserInfo:(NSString *)userId completion:(void (^)(RCUserInfo *userInfo))completion;

- (void) loadGroupInfo:(NSString *)groupId completion:(void (^)(RCUserInfo *userInfo))completion;

@end
