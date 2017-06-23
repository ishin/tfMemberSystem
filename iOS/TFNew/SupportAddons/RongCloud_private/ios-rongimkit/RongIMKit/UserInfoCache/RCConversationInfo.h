//
//  RCConversationInfo.h
//  RongIMKit
//
//  Created by 岑裕 on 16/1/22.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <RongIMLib/RongIMLib.h>

@interface RCConversationInfo : NSObject

@property (nonatomic, strong) NSString *targetId;
@property (nonatomic, assign) RCConversationType conversationType;
@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSString *portraitUri;

- (instancetype)initWithConversationId:(NSString *)targetId
                      conversationType:(RCConversationType)conversationType
                                  name:(NSString *)name
                           portraitUri:(NSString *)portraitUri;

-(instancetype)initWithGroupInfo:(RCGroup *)groupInfo;

-(RCGroup *)translateToGroupInfo;

+(NSString *)getConversationGUID:(RCConversationType)conversationType
                        targetId:(NSString *)targetId;

@end
