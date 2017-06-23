//
//  RongIMClient.h
//  RongIMLib
//
//  Created by xugang on 14/12/23.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//

#ifndef __RongIMClient
#define __RongIMClient
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "RCCommonDefine.h"
#import "RCStatusDefine.h"
#import "RCMessage.h"
#import "RCUserInfo.h"

@class RCConversation;
@class RCDiscussion;


UIKIT_EXTERN NSString *const KNotificationclearTheConversationMessages;

@protocol RongIMClientReceiveMessageDelegate <NSObject>

/**
 *  收到消息的处理。
 *
 *  @param message 收到的消息实体。
 *  @param object  调用对象。
 */
-(void)onReceived:(RCMessage*)message
             left:(int)nLeft
           object:(id)object;

@end

/**
 *  连接状态监听器，以获取连接相关状态。
 */
@protocol RCConnectionStatusChangeDelegate <NSObject>

/**
 *  网络状态变化。
 *
 *  @param status 网络状态。
 */
-(void)onConnectionStatusChanged:(RCConnectionStatus)status;
@end



/**
 *  SDK运行状态
 */
typedef NS_ENUM(NSUInteger, RCSDKRunningMode)
{
    /**
     *  后台运行
     */
    RCSDKRunningMode_Backgroud=0,
    /**
     *  前台运行
     */
    RCSDKRunningMode_Foregroud=1
};

/**
 *  当前网络状态
 */
typedef NS_ENUM(NSUInteger, RCNetworkStatus){
    /**
     *  不可用
     */
    RC_NotReachable        = 0,
    /**
     *  wifi
     */
    RC_ReachableViaWiFi,
    /**
     *  4G
     */
    RC_ReachableViaLTE,
    /**
     *  3G
     */
    RC_ReachableVia3G,
    /**
     *  2G
     */
    RC_ReachableVia2G
};



@interface RongIMClient : NSObject

@property (nonatomic,strong,readonly) NSString* currentAppkey;
@property (nonatomic,strong,readonly) NSString* currentToken;
@property (nonatomic,strong) NSString* currentUserId;
@property (nonatomic,assign,readonly) RCSDKRunningMode sdkRunningMode;

/**
 *  获取通讯能力库的核心类单例。
 *
 *  @return 通讯能力库的核心类单例。
 */
+(instancetype)sharedClient;

/**
 初始化 SDK。
 
 @param appKey   从开发者平台申请的应用 appKey。
 @param deviceToken 用于 Apple Push Notification Service 的设备唯一标识。
 */
-(void)init:(NSString*)appKey deviceToken:(NSString*)deviceToken;

/**
 *  注册消息类型，如果对消息类型进行扩展，可以忽略此方法。
 *
 *  @param messageClass   消息类型名称，对应的继承自 RCMessageContent 的消息类型。
 */

-(void)registerMessageType:(Class)messageClass;

/**
 设置DeviceToken，请在连接之前调用，否则无法上传token
 
 @param deviceToken 从苹果服务器获取的设备唯一标识
 */
-(void)setDeviceToken:(NSString*)deviceToken;
/**
 建立连接。
 
 @param token      从服务端获取的用户身份令牌（Token）。
 @param completion 调用完成的处理。
 @param error      调用返回的错误信息。
 */
-(void)connectWithToken:(NSString*)token
                success:(void (^)(NSString* userId))successBlock
                  error:(void (^)(RCConnectErrorCode status))errorBlock;


/**
 *  重新连接服务器。
 *
 *  @param successBlock 重连成功回调
 *  @param errorBlock   重连失败回调
 */
-(void)reconnect:(void (^)(NSString* userId))successBlock
           error:(void (^)(RCConnectErrorCode status))errorBlock;

/**
 *  断开连接。
 *
 *  @param isReceivePush 是否接收回调。
 */
-(void)disconnect:(BOOL)isReceivePush;

/**
 *  断开连接。
 */
-(void)disconnect;



/**
 *  设置接收消息的监听器。
 *
 *  所有接收到的消息、通知、状态都经由此处设置的监听器处理。包括私聊消息、讨论组消息、群组消息、聊天室消息以及各种状态。
 *
 *  @param delegate 接收消息的监听器。
 *  @param userData 用户自定义数据，该值会在 delegate 中返回。
 */
-(void)setReceiveMessageDelegate:(id<RongIMClientReceiveMessageDelegate>)delegate
                          object:(id)userData;


/**
 *  设置连接状态变化的监听器。
 *
 *  @param delegate 连接状态变化的监听器。
 */
-(void)setRCConnectionStatusChangeDelegate:(id<RCConnectionStatusChangeDelegate>)delegate;

/**
 *  发送消息。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *  @param conversationType 会话类型。
 *  @param content          消息内容。
 *  @param completion       调用完成的处理。
 *  @param error            调用返回的错误信息。
 *
 *  @return 发送的消息实体。
 */
-(RCMessage*)sendMessage:(RCConversationType)conversationType
                targetId:(NSString*)targetId
                 content:(RCMessageContent*)content
                 success:(void (^)(long messageId))successBlock
                   error:(void (^)(RCErrorCode nErrorCode, long messageId))errorBlock;

/**
 *  发送图片消息，上传图片并且发送，使用该方法，默认原图会上传到融云的服务，并且发送消息,如果使用普通的sendMessage方法，需要自己实现上传图片，并且添加ImageMessage的URL之后发送
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *  @param content          消息内容
 *  @param progressBlock    进度块
 *  @param successBlock     成功处理块
 *  @param errorBlock       失败处理块
 *
 *  @return 发送的消息实体。
 */
-(RCMessage*)sendImageMessage:(RCConversationType)conversationType
                    targetId:(NSString*)targetId
                     content:(RCMessageContent*)content
                    progress:(void (^)(int nProgress,long messageId))progressBlock
                      sucess:(void(^)(long messageId))successBlock
                       error:(void(^)(RCErrorCode errorCode, long messageId))errorBlock;

/**
 *  下载图片
 *
 *  @param conversationType 会话类型
 *  @param targetId         标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *  @param mediaType        媒体类型，目前支持图片
 *  @param mediaUrl         媒体URL
 *  @param progressBlock    回调进度
 *  @param successBlock     成功处理块
 *  @param errorBlock       失败处理块
 */
-(void)downloadMediaFile:(RCConversationType)conversationType
                targetId:(NSString*)targetId
               mediaType:(RCMediaType)mediaType
                mediaUrl:(NSString*)mediaUrl
                progress:(void (^)(int nProgress))progressBlock
                  sucess:(void(^)(NSString* mediaPath))successBlock
                   error:(void(^)(RCErrorCode errorCode))errorBlock;

/**
 *  获取用户信息。该方法sdk2.0弃用
 *
 *  如果本地缓存中包含用户信息，则从本地缓存中直接获取，否则将访问融云服务器获取用户登录时注册的信息；<br/>
 *  但如果该用户如果从来没有登录过融云服务器，返回的用户信息会为空值。
 *
 *  @param userId     用户 Id。
 *  @param completion 调用完成的处理。
 *  @param error      调用返回的错误信息。
 */
-(void)getUserInfo:(NSString*)userId
        completion:(void (^)(RCUserInfo* userInfo))completion
             error:(void (^)(RCErrorCode status))error;


/**
 *  获取会话列表。
 *
 *  会话列表按照时间从前往后排列，如果有置顶会话，则置顶会话在前。
 *  @param conversationTypeList 会话类型数组，存储对象为NSNumber类型 type类型为int
 *  @return 会话列表。
 */
- (NSArray *) getConversationList:(NSArray *)conversationTypeList;

/**
 *  获取会话信息。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         会话 Id。
 *
 *  @return 会话信息。
 */
-(RCConversation*)getConversation:(RCConversationType)conversationType
                         targetId:(NSString*)targetId;

/**
 *  从会话列表中移除某一会话，但是不删除会话内的消息。
 *
 *  如果此会话中有新的消息，该会话将重新在会话列表中显示，并显示最近的历史消息。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *
 *  @return 是否移除成功。
 */
-(BOOL)removeConversation:(RCConversationType)conversationType
                 targetId:(NSString*)targetId;

/**
 *  设置某一会话为置顶或者取消置顶。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *  @param isTop            是否置顶。
 *
 *  @return 是否设置成功。
 */
-(BOOL)setConversationToTop:(RCConversationType)conversationType
                   targetId:(NSString*)targetId isTop:(BOOL)isTop;

/**
 *  获取所有未读消息数。
 *
 *  @return 未读消息数。
 */
-(int)getTotalUnreadCount;

/**
 *  获取来自某用户（某会话）的未读消息数。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *
 *  @return 未读消息数。
 */
-(int)getUnreadCount:(RCConversationType)conversationType
            targetId:(NSString*)targetId;

/**
 *  获取某会话类型的未读消息数.
 *
 *  @param conversationTypes 会话类型, 存储对象为NSNumber type类型为int
 *
 *  @return 未读消息数。
 */
-(int)getUnreadCount:(NSArray*)conversationTypes;

/**
 *  获取最新消息记录。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。
 *  @param count            要获取的消息数量。
 *
 *  @return 最新消息记录，按照时间顺序从新到旧排列。
 */
-(NSArray*)getLatestMessages:(RCConversationType)conversationType
                    targetId:(NSString*)targetId
                       count:(int)count;

/**
 *  获取历史消息记录。
 *
 *  @param conversationType 会话类型。不支持传入 RCConversationType.CHATROOM。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *  @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息。
 *  @param count            要获取的消息数量。
 *
 *  @return 历史消息记录，按照时间顺序新到旧排列。
 */
-(NSArray*)getHistoryMessages:(RCConversationType)conversationType
                     targetId:(NSString*)targetId
              oldestMessageId:(long)oldestMessageId
                        count:(int)count;

/**
 *  删除指定的一条或者一组消息。
 *
 *  @param messageIds 要删除的消息 Id 列表, 存储对象为NSNumber messageId 类型为long。
 *
 *  @return 是否删除成功。
 */
-(BOOL)deleteMessages:(NSArray*)messageIds;

/**
 *  清空某一会话的所有聊天消息记录。
 *
 *  @param conversationType 会话类型。不支持传入 RCConversationType.CHATROOM。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *
 *  @return 是否清空成功。
 */
-(BOOL)clearMessages:(RCConversationType)conversationType
            targetId:(NSString*)targetId;

/**
 *  清除消息未读状态。
 *
 *  @param conversationType 会话类型。不支持传入 RCConversationType.CHATROOM。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *
 *  @return 是否清空成功。
 */
-(BOOL)clearMessagesUnreadStatus:(RCConversationType)conversationType
                        targetId:(NSString*)targetId;

/**
 *  清空会话列表
 *
 *  @param conversationTypeList 会话类型列表，存储对象为NSNumber, type类型为int
 *
 *
 *  @return 操作结果
 */
-(BOOL)clearConversations:(NSArray *)conversationTypeList;

/**
 *  设置消息的附加信息，此信息只保存在本地。
 *
 *  @param messageId 消息 Id。
 *  @param value     消息附加信息。
 *
 *  @return 是否设置成功。
 */
-(BOOL)setMessageExtra:(long)messageId
                 value:(NSString*)value;

/**
 *  设置接收到的消息状态。
 *
 *  @param messageId      消息 Id。
 *  @param receivedStatus 接收到的消息状态。
 */
-(BOOL)setMessageReceivedStatus:(long)messageId
                 receivedStatus:(RCReceivedStatus)receivedStatus;

/**
 *  获取某一会话的文字消息草稿。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *
 *  @return 草稿的文字内容。
 */
-(NSString*)getTextMessageDraft:(RCConversationType)conversationType
                       targetId:(NSString*)targetId;

/**
 *  保存文字消息草稿。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *  @param content          草稿的文字内容。
 *
 *  @return 是否保存成功。
 */
-(BOOL)saveTextMessageDraft:(RCConversationType)conversationType
                   targetId:(NSString*)targetId content:(NSString*)content;

/**
 *  清除某一会话的文字消息草稿。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id 或聊天室 Id。
 *
 *  @return 是否清除成功。
 */
-(BOOL)clearTextMessageDraft:(RCConversationType)conversationType
                    targetId:(NSString*)targetId;

/**
 *  获取讨论组信息和设置。
 *
 *  @param discussionId     讨论组 Id。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)getDiscussion:(NSString*)discussionId
          completion:(void (^)(RCDiscussion* discussion))successBlock
               error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  设置讨论组名称
 *
 *  @param targetId         讨论组 Id。
 *  @param discussionName   讨论组名称。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)setDiscussionName:(NSString*)targetId name:(NSString*)discussionName
              completion:(void (^)())successBlock
                   error:(void (^)(RCErrorCode status))errorBlock;


/**
 *  创建讨论组。
 *
 *  @param name       讨论组名称，如：当前所有成员的名字的组合。
 *  @param userIdList 讨论组成员 Id 列表。
 *  @param completion 调用完成的处理。
 *  @param error      调用返回的错误信息。
 */
-(void)createDiscussion:(NSString *)name
             userIdList:(NSArray *)userIdList
             completion:(void (^)(RCDiscussion* discussion))completion
                  error:(void (^)(RCErrorCode status))error;


/**
 *  邀请一名或者一组用户加入讨论组。
 *
 *  @param discussionId 讨论组 Id。
 *  @param userIdList   邀请的用户 Id 列表。
 *  @param completion   调用完成的处理。
 *  @param error        调用返回的错误信息。
 */
-(void)addMemberToDiscussion:(NSString*)discussionId
                  userIdList:(NSArray*)userIdList
                  completion:(void (^)(RCDiscussion* discussion))completion
                       error:(void (^)(RCErrorCode status))error;

/**
 *  供创建者将某用户移出讨论组。
 *
 *  移出自己或者调用者非讨论组创建者将产生错误。
 *
 *  @param discussionId 讨论组 Id。
 *  @param userId       用户 Id。
 *  @param completion   调用完成的处理。
 *  @param error        调用返回的错误信息。
 */
-(void)removeMemberFromDiscussion:(NSString*)discussionId
                           userId:(NSString*)userId
                       completion:(void (^)(RCDiscussion* discussion))completion
                            error:(void (^)(RCErrorCode status))error;

/**
 *  退出当前用户所在的某讨论组。
 *
 *  @param discussionId 讨论组 Id。
 *  @param completion   调用完成的处理。
 *  @param error        调用返回的错误信息。
 */
-(void)quitDiscussion:(NSString*)discussionId
           completion:(void (^)(RCDiscussion* discussion))completion
                error:(void (^)(RCErrorCode status))error;

/**
 *  获取会话消息提醒状态。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)getConversationNotificationStatus:(RCConversationType)conversationType
                                targetId:(NSString*)targetId
                              completion:(void (^)(RCConversationNotificationStatus nStatus))successBlock
                                   error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  设置会话消息提醒状态。
 *
 *  @param conversationType 会话类型。
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *  @param isBlocked        是否屏蔽。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)setConversationNotificationStatus:(RCConversationType)conversationType
                                targetId:(NSString*)targetId
                               isBlocked:(BOOL)isBlocked
                              completion:(void (^)(RCConversationNotificationStatus nStatus))successBlock error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  设置讨论组成员邀请权限。
 *
 *  @param targetId         目标 Id。根据不同的 conversationType，可能是聊天 Id、讨论组 Id、群组 Id。
 *  @param isOpen           开放状态，默认开放。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)setDiscussionInviteStatus:(NSString*)targetId
                          isOpen:(BOOL)isOpen
                      completion:(void (^)())successBlock
                           error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  同步当前用户的群组信息。
 *
 *  @param groupList        群组对象列表。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)syncGroups:(NSArray*)groupList
       completion:(void (^)())successBlock
            error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  加入群组。
 *
 *  @param groupId          群组Id。
 *  @param groupName        群组名称。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)joinGroup:(NSString*)groupId groupName:(NSString*)groupName
      completion:(void (^)())successBlock
           error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  退出群组。
 *
 *  @param groupId          群组Id。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)quitGroup:(NSString *)groupId
      completion:(void (^)())successBlock
           error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  加入聊天室。
 *
 *  @param targetId         聊天室ID。
 *  @param messageCount     进入聊天室获取获取多少条历史信息。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */
-(void)joinChatRoom:(NSString* )targetId
       messageCount:(int)messageCount
         completion:(void (^)())successBlock
              error:(void (^)(RCErrorCode status))errorBlock;

/**
 *  退出聊天室。
 *
 *  @param targetId         聊天室ID。
 *  @param successBlock     调用完成的处理。
 *  @param errorBlock       调用返回的错误信息。
 */

-(void)quitChatRoom:(NSString* )targetId
         completion:(void (^)())successBlock
              error:(void (^)(RCErrorCode status))errorBlock;


/**
 *  获取当前网络状态
 *
 *  @return 当前网络状态
 */
-(RCNetworkStatus)getCurrentNetworkStatus;

/**
 *  加入黑名单
 *
 *  @param userId   用户id
 *  @param completion 加入黑名单成功。
 *  @param error      加入黑名单失败。
 */
-(void)addToBlacklist:(NSString *)userId
           completion:(void(^)())completion
                error:(void(^)(RCErrorCode status))error;

/**
 *  移出黑名单
 *
 *  @param userId   用户id
 *  @param completion 移出黑名单成功。
 *  @param error      移出黑名单失败。
 */
-(void) removeFromBlacklist:(NSString *)userId
                 completion:(void(^)())completion
                      error:(void(^)(RCErrorCode status))error;

/**
 *  获取用户黑名单状态
 *
 *  @param userId   用户id
 *  @param completion 获取用户黑名单状态成功。bizStatus 0-在黑名单，101-不在黑名单
 *  @param error      获取用户黑名单状态失败。
 */
-(void) getBlacklistStatus:(NSString *)userId
                completion:(void(^)(int bizStatus))completion
                     error:(void(^)(RCErrorCode status))error;

/**
 *  获取黑名单列表
 *
 *  @param completion 黑名单列表，多个id以回车分割
 *  @param error      获取用户黑名单状态失败
 */

-(void) getBlacklist:(void(^)(NSArray *blockUserIds))completion
               error:(void(^)(RCErrorCode status))error;
@end
#endif
