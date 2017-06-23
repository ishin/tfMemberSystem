//
//  RCUserInfoCacheManager.m
//  RongIMKit
//
//  Created by 岑裕 on 16/1/22.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCUserInfoCacheManager.h"

NSString *const RCKitDispatchUserInfoUpdateNotification = @"RCKitDispatchUserInfoUpdateNotification";
NSString *const RCKitDispatchGroupUserInfoUpdateNotification = @"RCKitDispatchGroupUserInfoUpdateNotification";
NSString *const RCKitDispatchGroupInfoUpdateNotification = @"RCKitDispatchGroupInfoUpdateNotification";

@interface RCUserInfoCacheManager () <RCUserInfoUpdateDelegate, RCConversationUserInfoUpdateDelegate, RCConversationInfoUpdateDelegate>

@property (nonatomic, strong) dispatch_queue_t requestQueue;

@end

@implementation RCUserInfoCacheManager

+(instancetype)sharedManager {
    static RCUserInfoCacheManager *defaultManager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if(!defaultManager) {
            defaultManager = [[RCUserInfoCacheManager alloc] init];
            defaultManager.groupUserInfoEnabled = NO;
            defaultManager.requestQueue = dispatch_queue_create("cn.rongcloud.userInfoRequsetQueue", NULL);
            defaultManager.dbQueue = dispatch_queue_create("cn.rongcloud.userInfoDBQueue", NULL);
            [RCUserInfoCache sharedCache].updateDelegate = defaultManager;
            [RCConversationUserInfoCache sharedCache].updateDelegate = defaultManager;
            [RCConversationInfoCache sharedCache].updateDelegate = defaultManager;
        }
    });
    return defaultManager;
}

#pragma mark - DB Path

-(void)setCurrentUserId:(NSString *)currentUserId {
    if ([RCIM sharedRCIM].enablePersistentUserInfoCache
        && ![currentUserId isEqualToString:_currentUserId]) {
        __weak typeof(self) weakSelf = self;
        dispatch_async(self.dbQueue, ^{
            NSString *documentPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0];
            NSString *storagePath = [[[documentPath stringByAppendingPathComponent:self.appKey] stringByAppendingPathComponent:currentUserId] stringByAppendingPathComponent:@"IMKitUserInfoCache"];
          
            if (weakSelf.writeDBHelper) {
                [self.writeDBHelper closeDBIfNeed];
                self.writeDBHelper = nil;
            }
            weakSelf.writeDBHelper = [[RCUserInfoCacheDBHelper alloc] initWithPath:storagePath];
            
            if (weakSelf.readDBHelper) {
                [self.readDBHelper closeDBIfNeed];
                self.readDBHelper = nil;
            }
            weakSelf.readDBHelper = [[RCUserInfoCacheDBHelper alloc] initWithPath:storagePath];
        });
    }
    
    _currentUserId = currentUserId;
}

-(void)dealloc {
    [self.writeDBHelper closeDBIfNeed];
    self.writeDBHelper = nil;
    [self.readDBHelper closeDBIfNeed];
    self.readDBHelper = nil;
}

#pragma mark - UserInfo
-(RCUserInfo *)getUserInfo:(NSString *)userId {
    if (userId) {
        RCUserInfo *cacheUserInfo = [[RCUserInfoCache sharedCache] getUserInfo:userId];
        if (!cacheUserInfo
            && [RCIM sharedRCIM].userInfoDataSource
            && [[RCIM sharedRCIM].userInfoDataSource respondsToSelector:@selector(getUserInfoWithUserId:completion:)]) {
            dispatch_async(self.requestQueue, ^{
                __weak typeof(self) weakSelf = self;
                [[RCIM sharedRCIM].userInfoDataSource getUserInfoWithUserId:userId
                                                                 completion:^(RCUserInfo *userInfo) {
                                                                     [weakSelf updateUserInfo:userInfo forUserId:userId];
                                                                 }];
            });
        }
        return cacheUserInfo;
    } else {
        return nil;
    }
}

-(void)getUserInfo:(NSString *)userId
          complete:(void(^)(RCUserInfo *userInfo))completeBlock {
    if (userId) {
        RCUserInfo *cacheUserInfo = [[RCUserInfoCache sharedCache] getUserInfo:userId];
        if (cacheUserInfo) {
            completeBlock(cacheUserInfo);
        } else if ([RCIM sharedRCIM].userInfoDataSource
                   && [[RCIM sharedRCIM].userInfoDataSource respondsToSelector:@selector(getUserInfoWithUserId:completion:)]) {
            dispatch_async(self.requestQueue, ^{
                __weak typeof(self) weakSelf = self;
                [[RCIM sharedRCIM].userInfoDataSource getUserInfoWithUserId:userId
                                                                 completion:^(RCUserInfo *userInfo) {
                                                                     [weakSelf updateUserInfo:userInfo forUserId:userId];
                                                                     completeBlock(userInfo);
                                                                 }];
            });
        } else {
            completeBlock(nil);
        }
    } else {
        completeBlock(nil);
    }
}

-(RCUserInfo *)getUserInfoFromCacheOnly:(NSString *)userId {
    if (userId) {
        return [[RCUserInfoCache sharedCache] getUserInfo:userId];
    } else {
        return nil;
    }
}

-(void)updateUserInfo:(RCUserInfo *)userInfo
            forUserId:(NSString *)userId {
    if (userId && userInfo) {
        [[RCUserInfoCache sharedCache] updateUserInfo:userInfo forUserId:userId];
    } else if (!userId && userInfo.userId) {
        [[RCUserInfoCache sharedCache] updateUserInfo:userInfo forUserId:userInfo.userId];
    }
}

-(void)clearUserInfoNetworkCacheOnly:(NSString *)userId {
    if (userId) {
        [[RCUserInfoCache sharedCache] clearUserInfoNetworkCacheOnly:userId];
    }
}

-(void)clearUserInfo:(NSString *)userId {
    if (userId) {
        [[RCUserInfoCache sharedCache] clearUserInfo:userId];
    }
}

-(void)clearAllUserInfo {
    [[RCUserInfoCache sharedCache] clearAllUserInfo];
}

#pragma mark - GroupUserInfo (sugar for ConversationUserInfo)

-(RCUserInfo *)getUserInfo:(NSString *)userId
                 inGroupId:(NSString *)groupId {
    if (!self.groupUserInfoEnabled) {
        return [self getUserInfo:userId];
    }
    
    if (userId && groupId) {
        RCUserInfo *cacheUserInfo = [[RCConversationUserInfoCache sharedCache]
                                     getUserInfo:userId
                                     conversationType:ConversationType_GROUP
                                     targetId:groupId];
        if (!cacheUserInfo
            && [RCIM sharedRCIM].groupUserInfoDataSource
            && [[RCIM sharedRCIM].groupUserInfoDataSource respondsToSelector:@selector(getUserInfoWithUserId:inGroup:completion:)]) {
            dispatch_async(self.requestQueue, ^{
                __weak typeof(self) weakSelf = self;
                [[RCIM sharedRCIM].groupUserInfoDataSource
                 getUserInfoWithUserId:userId
                 inGroup:groupId
                 completion:^(RCUserInfo *userInfo) {
                     if (!userInfo) {
                         userInfo = [[RCUserInfo alloc] initWithUserId:userId name:nil portrait:nil];
                     }
                     [weakSelf updateUserInfo:userInfo forUserId:userId inGroup:groupId];
                 }];
            });
        }
        return [self fallBackOrdinaryUserInfo:cacheUserInfo forUserId:userId];
    } else {
        return nil;
    }
}

-(void)getUserInfo:(NSString *)userId
         inGroupId:(NSString *)groupId
          complete:(void(^)(RCUserInfo *userInfo))completeBlock {
    if (!self.groupUserInfoEnabled) {
        [self getUserInfo:userId complete:^(RCUserInfo *userInfo) {
            completeBlock(userInfo);
        }];
    }
    
    if (userId && groupId) {
        RCUserInfo *cacheUserInfo = [[RCConversationUserInfoCache sharedCache]
                                     getUserInfo:userId
                                     conversationType:ConversationType_GROUP
                                     targetId:groupId];
        if (cacheUserInfo) {
            [self fallBackOrdinaryUserInfo:cacheUserInfo
                                 forUserId:userId
                                  complete:^(RCUserInfo *userInfo) {
                                      completeBlock(cacheUserInfo);
                                  }];
        } else if ([RCIM sharedRCIM].groupUserInfoDataSource
                   && [[RCIM sharedRCIM].groupUserInfoDataSource respondsToSelector:@selector(getUserInfoWithUserId:inGroup:completion:)]) {
            dispatch_async(self.requestQueue, ^{
                __weak typeof(self) weakSelf = self;
                [[RCIM sharedRCIM].groupUserInfoDataSource
                 getUserInfoWithUserId:userId
                 inGroup:groupId
                 completion:^(RCUserInfo *userInfo) {
                     if (!userInfo) {
                         userInfo = [[RCUserInfo alloc] initWithUserId:userId name:nil portrait:nil];
                     }
                     [weakSelf updateUserInfo:userInfo forUserId:userId inGroup:groupId];
                     [weakSelf fallBackOrdinaryUserInfo:userInfo
                                              forUserId:userId
                                               complete:^(RCUserInfo *userInfo) {
                                                   completeBlock(userInfo);
                                               }];
                 }];
            });
        } else {
            [self getUserInfo:userId
                     complete:^(RCUserInfo *userInfo) {
                         completeBlock(userInfo);
                     }];
        }
    } else {
        completeBlock(nil);
    }
}

-(RCUserInfo *)getUserInfoFromCacheOnly:(NSString *)userId
                              inGroupId:(NSString *)groupId {
    if (!self.groupUserInfoEnabled) {
        return [self getUserInfoFromCacheOnly:userId];
    }
    
    if (userId && groupId) {
        RCUserInfo *cacheUserInfo = [[RCConversationUserInfoCache sharedCache]
                                     getUserInfo:userId
                                     conversationType:ConversationType_GROUP
                                     targetId:groupId];
        return [self fallBackOrdinaryUserInfoFromCacheOnly:cacheUserInfo forUserId:userId];
    } else {
        return nil;
    }
}

//同步回落
-(RCUserInfo *)fallBackOrdinaryUserInfo:(RCUserInfo *)tempUserInfo
                                forUserId:(NSString *)userId {
    if (!tempUserInfo) {
        return [self getUserInfo:userId];
    }
    
    if ([tempUserInfo.name length] <= 0 || [tempUserInfo.portraitUri length] <= 0) {
        RCUserInfo *ordinaryUserInfo = [self getUserInfo:userId];
        if ([tempUserInfo.name length] <= 0) {
            tempUserInfo.name = ordinaryUserInfo.name;
        }
        if ([tempUserInfo.portraitUri length] <= 0) {
            tempUserInfo.portraitUri = ordinaryUserInfo.portraitUri;
        }
    }
    return tempUserInfo;
}

-(RCUserInfo *)fallBackOrdinaryUserInfoFromCacheOnly:(RCUserInfo *)tempUserInfo
                                           forUserId:(NSString *)userId {
    if (!tempUserInfo) {
        return [self getUserInfoFromCacheOnly:userId];
    }
    
    if ([tempUserInfo.name length] <= 0 || [tempUserInfo.portraitUri length] <= 0) {
        RCUserInfo *ordinaryUserInfo = [self getUserInfo:userId];
        if ([tempUserInfo.name length] <= 0) {
            tempUserInfo.name = ordinaryUserInfo.name;
        }
        if ([tempUserInfo.portraitUri length] <= 0) {
            tempUserInfo.portraitUri = ordinaryUserInfo.portraitUri;
        }
    }
    return tempUserInfo;
}

//异步回落
-(void)fallBackOrdinaryUserInfo:(RCUserInfo *)tempUserInfo
                                forUserId:(NSString *)userId
                         complete:(void(^)(RCUserInfo *userInfo))completeBlock{
    if (!tempUserInfo) {
        [self getUserInfo:userId
                 complete:^(RCUserInfo *userInfo) {
                     completeBlock(userInfo);
                 }];
    }
    
    if (!tempUserInfo.name || !tempUserInfo.portraitUri) {
        [self getUserInfo:userId
                 complete:^(RCUserInfo *userInfo) {
                     if (!tempUserInfo.name) {
                         tempUserInfo.name = userInfo.name;
                     }
                     if (!tempUserInfo.portraitUri) {
                         tempUserInfo.portraitUri = userInfo.portraitUri;
                     }
                     completeBlock(tempUserInfo);
                 }];
    }
}

-(void)updateUserInfo:(RCUserInfo *)userInfo
            forUserId:(NSString *)userId
              inGroup:(NSString *)groupId {
    if (groupId) {
        if (userId && userInfo) {
            [[RCConversationUserInfoCache sharedCache] updateUserInfo:userInfo
                                                            forUserId:userId
                                                     conversationType:ConversationType_GROUP
                                                             targetId:groupId];
        } else if (!userId && userInfo.userId) {
            [[RCConversationUserInfoCache sharedCache] updateUserInfo:userInfo
                                                            forUserId:userInfo.userId
                                                     conversationType:ConversationType_GROUP
                                                             targetId:groupId];
        }
    }
}

-(void)clearGroupUserInfoNetworkCacheOnly:(NSString *)userId
                                  inGroup:(NSString *)groupId {
    if (userId && groupId) {
        [[RCConversationUserInfoCache sharedCache]
         clearConversationUserInfoNetworkCacheOnly:userId
         conversationType:ConversationType_GROUP
         targetId:groupId];
    }
}

-(void)clearGroupUserInfo:(NSString *)userId inGroup:(NSString *)groupId {
    if (userId && groupId) {
        [[RCConversationUserInfoCache sharedCache]
         clearConversationUserInfo:userId
         conversationType:ConversationType_GROUP
         targetId:groupId];
    }
}

-(void)clearAllGroupUserInfo {
    [[RCConversationUserInfoCache sharedCache] clearAllConversationUserInfo];
}

#pragma mark - GroupInfo (sugar for ConversationInfo)

-(RCGroup *)getGroupInfo:(NSString *)groupId {
    if (groupId) {
        RCConversationInfo *cacheConversationInfo =
        [[RCConversationInfoCache sharedCache] getConversationInfo:ConversationType_GROUP
                                                          targetId:groupId];
        if (!cacheConversationInfo
            && [RCIM sharedRCIM].groupInfoDataSource
            && [[RCIM sharedRCIM].groupInfoDataSource respondsToSelector:@selector(getGroupInfoWithGroupId:completion:)]) {
            dispatch_async(self.requestQueue, ^{
                __weak typeof(self) weakSelf = self;
                [[RCIM sharedRCIM].groupInfoDataSource
                 getGroupInfoWithGroupId:groupId
                 completion:^(RCGroup *groupInfo) {
                     [weakSelf updateGroupInfo:groupInfo forGroupId:groupId];
                 }];
            });
        }
        return [cacheConversationInfo translateToGroupInfo];
    } else {
        return nil;
    }
}

-(void)getGroupInfo:(NSString *)groupId
           complete:(void(^)(RCGroup *groupInfo))completeBlock {
    if (groupId) {
        RCConversationInfo *cacheConversationInfo =
        [[RCConversationInfoCache sharedCache] getConversationInfo:ConversationType_GROUP
                                                          targetId:groupId];
        if (cacheConversationInfo) {
            completeBlock([cacheConversationInfo translateToGroupInfo]);
        } else if ([RCIM sharedRCIM].groupInfoDataSource
                   && [[RCIM sharedRCIM].groupInfoDataSource respondsToSelector:@selector(getGroupInfoWithGroupId:completion:)]) {
            dispatch_async(self.requestQueue, ^{
                __weak typeof(self) weakSelf = self;
                [[RCIM sharedRCIM].groupInfoDataSource
                 getGroupInfoWithGroupId:groupId
                 completion:^(RCGroup *groupInfo) {
                     [weakSelf updateGroupInfo:groupInfo forGroupId:groupId];
                     completeBlock([cacheConversationInfo translateToGroupInfo]);
                 }];
            });
        } else {
            completeBlock(nil);
        }
    } else {
        completeBlock(nil);
    }
}

-(RCGroup *)getGroupInfoFromCacheOnly:(NSString *)groupId {
    if (groupId) {
        RCConversationInfo *cacheConversationInfo =
        [[RCConversationInfoCache sharedCache] getConversationInfo:ConversationType_GROUP
                                                          targetId:groupId];
        return [cacheConversationInfo translateToGroupInfo];
    } else {
        return nil;
    }
}

-(void)updateGroupInfo:(RCGroup *)groupInfo
            forGroupId:(NSString *)groupId {
    if (groupId && groupInfo) {
        [[RCConversationInfoCache sharedCache]
         updateConversationInfo:[[RCConversationInfo alloc] initWithGroupInfo:groupInfo]
         conversationType:ConversationType_GROUP
         targetId:groupId];
    } else if (!groupId && groupInfo.groupId) {
        [[RCConversationInfoCache sharedCache]
         updateConversationInfo:[[RCConversationInfo alloc] initWithGroupInfo:groupInfo]
         conversationType:ConversationType_GROUP
         targetId:groupInfo.groupId];
    }
}

-(void)clearGroupInfoNetworkCacheOnly:(NSString *)groupId {
    if (groupId) {
        [[RCConversationInfoCache sharedCache] clearConversationInfoNetworkCacheOnly:ConversationType_GROUP targetId:groupId];
    }
}

-(void)clearGroupInfo:(NSString *)groupId {
    if (groupId) {
        [[RCConversationInfoCache sharedCache] clearConversationInfo:ConversationType_GROUP
                                                            targetId:groupId];
    }
}

-(void)clearAllGroupInfo {
    [[RCConversationInfoCache sharedCache] clearAllConversationInfo];
}

#pragma mark - Post Notification
-(void)onUserInfoUpdate:(RCUserInfo *)userInfo {
    if (userInfo.userId) {
        [[NSNotificationCenter defaultCenter]
         postNotificationName:RCKitDispatchUserInfoUpdateNotification
         object:@{@"userId":userInfo.userId,
                  @"userInfo":userInfo}];
    }
}

-(void)onConversationUserInfoUpdate:(RCUserInfo *)userInfo
                     inConversation:(RCConversationType)conversationType
                           targetId:(NSString *)targetId {
    if (conversationType == ConversationType_GROUP && userInfo.userId) {
        [[NSNotificationCenter defaultCenter]
         postNotificationName:RCKitDispatchGroupUserInfoUpdateNotification
         object:@{@"userId":userInfo.userId,
                  @"userInfo":userInfo,
                  @"inGroupId":targetId}];
    }
}

-(void)onConversationInfoUpdate:(RCConversationInfo *)conversationInfo {
    if (conversationInfo.conversationType == ConversationType_GROUP && conversationInfo.targetId) {
        RCGroup *cacheGroupInfo = [conversationInfo translateToGroupInfo];
        [[NSNotificationCenter defaultCenter]
         postNotificationName:RCKitDispatchGroupInfoUpdateNotification
         object:@{@"groupId":cacheGroupInfo.groupId,
                  @"groupInfo":cacheGroupInfo}];
    }
}

@end
