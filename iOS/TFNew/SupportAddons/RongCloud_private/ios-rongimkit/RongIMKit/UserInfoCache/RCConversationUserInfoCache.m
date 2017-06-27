//
//  RCConversationUserInfoCache.m
//  RongIMKit
//
//  Created by 岑裕 on 16/1/22.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCConversationUserInfoCache.h"
#import "RCConversationInfo.h"
#import "RCloudImageLoader.h"
#import "RCloudFMDB.h"
#import "RCUserInfoCacheManager.h"
#import "RCThreadSafeMutableDictionary.h"

@interface RCConversationUserInfoCache ()

//key:GUID(conversationType;;;targetId), value:(NSMutableDictionary(key:userId, value:userInfo))
@property (nonatomic, strong) RCThreadSafeMutableDictionary *cache;

@end

@implementation RCConversationUserInfoCache

+(instancetype)sharedCache {
    static RCConversationUserInfoCache *defaultCache = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if(!defaultCache) {
            defaultCache = [[RCConversationUserInfoCache alloc] init];
            defaultCache.cache = [[RCThreadSafeMutableDictionary alloc] init];
        }
    });
    return defaultCache;
}

-(RCUserInfo *)getUserInfo:(NSString *)userId
          conversationType:(RCConversationType)conversationType
                  targetId:(NSString *)targetId {
    NSString *conversationGUID = [RCConversationInfo getConversationGUID:conversationType targetId:targetId];
    NSMutableDictionary *cacheUserInfoList = self.cache[conversationGUID];
    RCUserInfo *cacheUserInfo = nil;
    
    if (cacheUserInfoList && cacheUserInfoList[userId]) {
        cacheUserInfo = cacheUserInfoList[userId];
    } else {
        //线程同步读取
        RCUserInfo *dbUserInfo = [rcUserInfoReadDBHelper selectUserInfoFromDB:userId conversationType:conversationType targetId:targetId];
        if (dbUserInfo) {
            NSMutableDictionary *cacheUserInfoList = self.cache[conversationGUID];
            if (!cacheUserInfoList) {
                NSMutableDictionary *cacheUserInfoList = [[NSMutableDictionary alloc] init];
                [self.cache setObject:cacheUserInfoList forKey:conversationGUID];
            }
            [cacheUserInfoList setObject:dbUserInfo forKey:userId];
            cacheUserInfo = dbUserInfo;
        }
    }
    return cacheUserInfo;
}

-(void)updateUserInfo:(RCUserInfo *)userInfo
            forUserId:(NSString *)userId
     conversationType:(RCConversationType)conversationType
             targetId:(NSString *)targetId {
    NSString *conversationGUID = [RCConversationInfo getConversationGUID:conversationType targetId:targetId];
    NSMutableDictionary *cacheUserInfoList = self.cache[conversationGUID];
    RCUserInfo *cacheUserInfo = nil;
    if (cacheUserInfoList && cacheUserInfoList[userId]) {
        cacheUserInfo = cacheUserInfoList[userId];
    }
    
    if (![userInfo isEqual:cacheUserInfo]) {
        NSMutableDictionary *cacheUserInfoList = self.cache[conversationGUID];
        if (!cacheUserInfoList) {
            cacheUserInfoList = [[NSMutableDictionary alloc] init];
            [self.cache setObject:cacheUserInfoList forKey:conversationGUID];
        }
        [cacheUserInfoList setObject:userInfo forKey:userId];
        
        __weak typeof(self) weakSelf = self;
        dispatch_async(rcUserInfoDBQueue, ^{
            [rcUserInfoWriteDBHelper replaceUserInfoFromDB:userInfo
                                                   forUserId:userId
                                            conversationType:conversationType
                                                    targetId:targetId];
            [weakSelf.updateDelegate onConversationUserInfoUpdate:userInfo
                                                   inConversation:conversationType
                                                         targetId:targetId];
        });
    }
}

-(void)clearConversationUserInfoNetworkCacheOnly:(NSString *)userId
                                conversationType:(RCConversationType)conversationType
                                        targetId:(NSString *)targetId {
    NSString *conversationGUID = [RCConversationInfo getConversationGUID:conversationType targetId:targetId];
    NSMutableDictionary *cacheUserInfoList = self.cache[conversationGUID];
    RCUserInfo *cacheUserInfo = nil;
    if (cacheUserInfoList && cacheUserInfoList[userId]) {
        cacheUserInfo = cacheUserInfoList[userId];
    }
    
    if (!cacheUserInfo) {
        __weak typeof(self) weakSelf = self;
        dispatch_async(rcUserInfoDBQueue, ^{
            RCUserInfo *dbUserInfo = [rcUserInfoWriteDBHelper selectUserInfoFromDB:userId conversationType:conversationType targetId:targetId];
            [weakSelf deleteImageCache:dbUserInfo];
        });
    } else {
        [self deleteImageCache:cacheUserInfo];
    }
}

-(void)clearConversationUserInfo:(NSString *)userId
                conversationType:(RCConversationType)conversationType
                        targetId:(NSString *)targetId {
    NSString *conversationGUID = [RCConversationInfo getConversationGUID:conversationType targetId:targetId];
    NSMutableDictionary *cacheUserInfoList = self.cache[conversationGUID];
    RCUserInfo *cacheUserInfo = nil;
    if (cacheUserInfoList && cacheUserInfoList[userId]) {
        cacheUserInfo = cacheUserInfoList[userId];
    }
    
    if (!cacheUserInfo) {
        __weak typeof(self) weakSelf = self;
        dispatch_async(rcUserInfoDBQueue, ^{
            RCUserInfo *dbUserInfo = [rcUserInfoWriteDBHelper selectUserInfoFromDB:userId conversationType:conversationType targetId:targetId];
            [weakSelf deleteImageCache:dbUserInfo];
            [rcUserInfoWriteDBHelper deleteConversationUserInfoFromDB:userId
                                                       conversationType:conversationType
                                                               targetId:targetId];
        });
    } else {
        [self deleteImageCache:cacheUserInfo];
    }
}

-(void)clearAllConversationUserInfo {
    for (NSDictionary *cacheUserInfoList in [self.cache allValues]) {
        for (RCUserInfo *cacheUserInfo in [cacheUserInfoList allValues]) {
            [self deleteImageCache:cacheUserInfo];
        }
    }
    [self.cache removeAllObjects];
    
    __weak typeof(self) weakSelf = self;
    dispatch_async(rcUserInfoDBQueue, ^{
        NSArray *dbUserInfoList = [rcUserInfoWriteDBHelper selectAllConversationUserInfoFromDB];
        for (RCUserInfo *dbUserInfo in dbUserInfoList) {
            [weakSelf deleteImageCache:dbUserInfo];
        }
        [rcUserInfoWriteDBHelper deleteAllConversationUserInfoFromDB];
    });
}



#pragma mark - image cache
-(void)deleteImageCache:(RCUserInfo *)userInfo {
    if ([userInfo.portraitUri length] > 0) {
        [[RCloudImageLoader sharedImageLoader] clearCacheForURL:[NSURL URLWithString:userInfo.portraitUri]];
    }
}

@end
