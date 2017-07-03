//
//  RCIM.m
//  RongIMKit
//
//  Created by xugang on 15/1/13.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCIM.h"
#import "RCKitUtility.h"
#import "RCLocalNotification.h"
#import "RCSystemSoundPlayer.h"
#import "RCOldMessageNotificationMessage.h"
#import "RCUserInfoCacheManager.h"
#import "RCUserInfoUpdateMessage.h"
#import "RCloudImageView.h"
#import "RongIMKitExtensionManager.h"
#import "RongExtensionKit.h"

NSString *const RCKitDispatchMessageNotification = @"RCKitDispatchMessageNotification";
NSString *const RCKitDispatchTypingMessageNotification = @"RCKitDispatchTypingMessageNotification";
NSString *const RCKitSendingMessageNotification = @"RCKitSendingMessageNotification";
NSString *const RCKitDispatchConnectionStatusChangedNotification = @"RCKitDispatchConnectionStatusChangedNotification";
NSString *const RCKitDispatchRecallMessageNotification = @"RCKitDispatchRecallMessageNotification";
NSString *const RCKitDispatchReadReceiptNotification = @"RCKitDispatchReadReceiptNotification";

NSString *const RCKitDispatchDownloadMediaNotification = @"RCKitDispatchDownloadMediaNotification";
NSString *const RCKitDispatchMessageReceiptRequestNotification = @"RCKitDispatchMessageReceiptRequestNotification";

NSString *const RCKitDispatchMessageReceiptResponseNotification = @"RCKitDispatchMessageReceiptResponseNotification";

@interface RCIM () <RCIMClientReceiveMessageDelegate, RCConnectionStatusChangeDelegate>
@property(nonatomic, strong) NSString *appKey;
@property(nonatomic, assign) BOOL isNotificationQuiet;

@property(nonatomic, assign)BOOL hasNotifydExtensionModuleUserId;
@property(nonatomic, strong)NSString *token;
@property(nonatomic, strong)NSMutableArray *downloadingMeidaMessageIds;
@end

static RCIM *__rongUIKit = nil;
@implementation RCIM

+ (instancetype)sharedRCIM {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      if (__rongUIKit == nil) {
          __rongUIKit = [[RCIM alloc] init];
          __rongUIKit.userInfoDataSource = nil;
          __rongUIKit.groupUserInfoDataSource = nil;
          __rongUIKit.groupInfoDataSource = nil;
          __rongUIKit.receiveMessageDelegate = nil;
          __rongUIKit.isNotificationQuiet = NO;
          __rongUIKit.disableMessageNotificaiton = NO;
          __rongUIKit.disableMessageAlertSound = [[NSUserDefaults standardUserDefaults] boolForKey:@"rcMessageBeep"];
          __rongUIKit.enableMessageAttachUserInfo = NO;
          __rongUIKit.globalMessagePortraitSize = CGSizeMake(40, 40);
          __rongUIKit.globalConversationPortraitSize = CGSizeMake(46, 46);
          __rongUIKit.globalMessageAvatarStyle = RC_USER_AVATAR_RECTANGLE;
          __rongUIKit.globalConversationAvatarStyle = RC_USER_AVATAR_RECTANGLE;
          __rongUIKit.globalNavigationBarTintColor=[UIColor whiteColor];
          __rongUIKit.portraitImageViewCornerRadius = 5;
          __rongUIKit.maxVoiceDuration = 60;
          __rongUIKit.enablePersistentUserInfoCache = NO;
          __rongUIKit.enableMessageRecall = NO;
          __rongUIKit.enableMessageMentioned = NO;
          __rongUIKit.maxRecallDuration = 120;
          __rongUIKit.enableReadReceipt = NO;
          __rongUIKit.hasNotifydExtensionModuleUserId = NO;
          __rongUIKit.enabledReadReceiptConversationTypeList = nil;
          __rongUIKit.downloadingMeidaMessageIds = [[NSMutableArray alloc] init];
          [[RongIMKitExtensionManager sharedManager] loadAllExtensionModules];
      }
    });
    return __rongUIKit;
}
- (void)setDisableMessageAlertSound:(BOOL)disableMessageAlertSound
{
    [[NSUserDefaults standardUserDefaults] setBool:disableMessageAlertSound forKey:@"rcMessageBeep"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    _disableMessageAlertSound = disableMessageAlertSound;
}
- (void)setGlobalMessagePortraitSize:(CGSize)globalMessagePortraitSize {
    CGFloat width = globalMessagePortraitSize.width;
    CGFloat height = globalMessagePortraitSize.height;


    _globalMessagePortraitSize.width = width;
    _globalMessagePortraitSize.height = height;
}
- (void)setGlobalConversationPortraitSize:(CGSize)globalConversationPortraitSize {
    CGFloat width = globalConversationPortraitSize.width;
    CGFloat height = globalConversationPortraitSize.height;


    if (height < 36.0f) {
        height = 36.0f;
    }

    _globalConversationPortraitSize.width = width;
    _globalConversationPortraitSize.height = height;
}

- (void)setCurrentUserInfo:(RCUserInfo *)currentUserInfo {
    [[RCIMClient sharedRCIMClient] setCurrentUserInfo:currentUserInfo];
    
    if (currentUserInfo) {
        [[RCUserInfoCacheManager sharedManager] updateUserInfo:currentUserInfo forUserId:currentUserInfo.userId];
    }
}

- (RCUserInfo *)currentUserInfo {
    return [RCIMClient sharedRCIMClient].currentUserInfo;
}

- (void)setGroupUserInfoDataSource:(id<RCIMGroupUserInfoDataSource>)groupUserInfoDataSource {
    _groupUserInfoDataSource = groupUserInfoDataSource;
    if (groupUserInfoDataSource) {
        [RCUserInfoCacheManager sharedManager].groupUserInfoEnabled = YES;
    }
}

- (void)initWithAppKey:(NSString *)appKey {
    if ([self.appKey isEqual:appKey]) {
        NSLog(@"Warning:请不要重复调用Init！！！");
        return;
    }
    
    self.appKey = appKey;
    [[RCIMClient sharedRCIMClient] initWithAppKey:appKey];

    [self registerMessageType:[RCOldMessageNotificationMessage class]];
    
    // listen receive message
    [[RCIMClient sharedRCIMClient] setReceiveMessageDelegate:self object:nil];
    [[RCIMClient sharedRCIMClient] setRCConnectionStatusChangeDelegate:self];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(resetNotificationQuietStatus)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(resetNotificationQuietStatus)
                                                 name:UIApplicationWillEnterForegroundNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(resetNotificationQuietStatus)
                                                 name:@"RCLibDispatchResetNotificationQuietStatusNotification"
                                               object:nil];
  //接口向后兼容[[++
  [[NSNotificationCenter defaultCenter]addObserver:self
                                          selector:@selector(receiveMessageHasReadNotification:)
                                              name:RCLibDispatchReadReceiptNotification
                                            object:nil];
  //接口向后兼容--]]

    [self registerMessageType:RCUserInfoUpdateMessage.class];
    [RCUserInfoCacheManager sharedManager].appKey = appKey;

    [[RongIMKitExtensionManager sharedManager] initWithAppKey:appKey];
}

- (void)resetNotificationQuietStatus {
    [[RCIMClient sharedRCIMClient] getNotificationQuietHours:^(NSString *startTime, int spansMin) {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"HH:mm:ss"];
        if (startTime && startTime.length != 0) {
            NSDate *startDate = [dateFormatter dateFromString:startTime];
            NSDate *endDate = [startDate dateByAddingTimeInterval:spansMin * 60];
            NSString *nowDateString = [dateFormatter stringFromDate:[NSDate date]];
            NSDate *nowDate = [dateFormatter dateFromString:nowDateString];
            
            NSDate *earDate = [startDate earlierDate:nowDate];
            NSDate *laterDate = [endDate laterDate:nowDate];
            if (([startDate isEqualToDate:earDate] && [endDate isEqualToDate:laterDate]) || [nowDate isEqualToDate:startDate] || [nowDate isEqualToDate:endDate]) {
                self.isNotificationQuiet = YES;
            } else {
                self.isNotificationQuiet = NO;
            }
        } else {
            self.isNotificationQuiet = NO;
        }
    } error:^(RCErrorCode status) {
        
    }];
}

- (void)registerMessageType:(Class)messageClass {
    [[RCIMClient sharedRCIMClient] registerMessageType:messageClass];
}

//接口向后兼容＋＋
- (void)setEnableReadReceipt:(BOOL)enableReadReceipt {
  _enableReadReceipt = enableReadReceipt;
  if (enableReadReceipt && self.enabledReadReceiptConversationTypeList.count == 0) {
    self.enabledReadReceiptConversationTypeList = @[@(ConversationType_PRIVATE)];
  }
}
//接口向后兼容--

- (void)connectWithToken:(NSString *)token
                 success:(void (^)(NSString *userId))successBlock
                   error:(void (^)(RCConnectErrorCode status))errorBlock
          tokenIncorrect:(void (^)())tokenIncorrectBlock {

  self.hasNotifydExtensionModuleUserId = NO;
  self.token = token;
    [[RCIMClient sharedRCIMClient] connectWithToken:token
        success:^(NSString *userId) {
            [RCUserInfoCacheManager sharedManager].currentUserId = userId;
            if (successBlock) {
                successBlock(userId);
            }
          dispatch_async(dispatch_get_main_queue(), ^{
            if (!self.hasNotifydExtensionModuleUserId) {
              self.hasNotifydExtensionModuleUserId = YES;
              dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                [[RongIMKitExtensionManager sharedManager] didConnect:[RCIMClient sharedRCIMClient].currentUserInfo.userId];
              });
            }
          });
        }
        error:^(RCConnectErrorCode status) {
            if ([RCIMClient sharedRCIMClient].currentUserInfo.userId) {
                [RCUserInfoCacheManager sharedManager].currentUserId = [RCIMClient sharedRCIMClient].currentUserInfo.userId;
            }
            if(errorBlock!=nil)
                errorBlock(status);
        }
        tokenIncorrect:^() {
          self.hasNotifydExtensionModuleUserId = NO;
          [[RongIMKitExtensionManager sharedManager] didDisconnect];
          if (tokenIncorrectBlock) {
            tokenIncorrectBlock();
          }
        }];
}

/**
 *  断开连接。
 *
 *  @param isReceivePush 是否接收回调。
 */
- (void)disconnect:(BOOL)isReceivePush {
  self.hasNotifydExtensionModuleUserId = NO;
  [[RongIMKitExtensionManager sharedManager] didDisconnect];
  [[RCIMClient sharedRCIMClient] disconnect:isReceivePush];
}

/**
 *  断开连接。
 */
- (void)disconnect {
  [self disconnect:YES];
}

/**
 *  Log out。不会接收到push消息。
 */
- (void)logout {
  [self disconnect:NO];
}

- (void)getNotificationInfo:(RCMessage *)message result:(void (^)(NSString *senderName, NSString *formatedMessage))resultBlock {
  __block NSString *showMessage = nil;
  if (self.showUnkownMessageNotificaiton && message.objectName &&
      !message.content) {
    showMessage = NSLocalizedStringFromTable(@"unknown_message_notification_tip", @"RongCloudKit", nil);
  } else if (message.content.mentionedInfo.isMentionedMe) {
    if (!message.content.mentionedInfo.mentionedContent) {
      showMessage = [RCKitUtility formatMessage:message.content];
    } else {
      showMessage = message.content.mentionedInfo.mentionedContent;
    }
  } else {
    showMessage = [RCKitUtility formatMessage:message.content];
  }
  
  if ((ConversationType_GROUP == message.conversationType)) {
    [[RCUserInfoCacheManager sharedManager]
     getGroupInfo:message.targetId
     complete:^(RCGroup *groupInfo) {
       if (nil == groupInfo) {
         return;
       }
       
       if (message.content.mentionedInfo.isMentionedMe) {
         if (!message.content.mentionedInfo.mentionedContent) {
           showMessage = [NSString
                          stringWithFormat:@"%@%@:%@",
                          NSLocalizedStringFromTable(@"HaveMentionedForNotification",
                                                     @"RongCloudKit", nil),
                          groupInfo.groupName,
                          showMessage];
         }
       } else {
         showMessage = [NSString stringWithFormat:@"%@:%@",
                        groupInfo
                        .groupName,
                        showMessage];;
       }
       resultBlock(groupInfo.groupName, showMessage);
       
     }];
  } else if (ConversationType_DISCUSSION == message.conversationType) {
    [[RCIMClient sharedRCIMClient] getDiscussion:message.targetId
                                         success:^(RCDiscussion *discussion) {
                                           if (nil == discussion) {
                                             return;
                                           }
                                           
                                           if (message.content.mentionedInfo.isMentionedMe) {
                                             if (!message.content.mentionedInfo.mentionedContent) {
                                               showMessage = [NSString stringWithFormat:@"%@%@:%@",
                                                              NSLocalizedStringFromTable(
                                                                                         @"HaveMentionedForNotification",
                                                                                         @"RongCloudKit", nil),
                                                              discussion.discussionName,
                                                              showMessage];
                                             }
                                           } else {
                                             showMessage = [NSString stringWithFormat:@"%@:%@",
                                                            discussion.discussionName,
                                                            showMessage];
                                           }
                                           resultBlock(discussion.discussionName, showMessage);
                                           
                                         }
                                           error:^(RCErrorCode status){
                                             
                                           }];
  } else if (ConversationType_CUSTOMERSERVICE == message.conversationType) {
    NSString *customeServiceName = message.content.senderUserInfo.name != nil
    ? message.content.senderUserInfo.name
    : @"客服";
    
    showMessage = [NSString stringWithFormat:@"%@:%@",
                   customeServiceName,
                   showMessage];
    resultBlock(customeServiceName, showMessage);
    
  } else if (ConversationType_APPSERVICE == message.conversationType ||
             ConversationType_PUBLICSERVICE == message.conversationType) {
    RCPublicServiceProfile *serviceProfile = [[RCIMClient sharedRCIMClient]
                                              getPublicServiceProfile:(RCPublicServiceType)message.conversationType
                                              publicServiceId:message.targetId];
    
    if (serviceProfile) {
      
      showMessage = [NSString
                     stringWithFormat:@"%@:%@",
                     serviceProfile.name,
                     showMessage];
      resultBlock(serviceProfile.name, showMessage);
    }
  } else if (ConversationType_SYSTEM == message.conversationType) {
    [[RCUserInfoCacheManager sharedManager]
     getUserInfo:message.targetId
     complete:^(RCUserInfo *userInfo) {
       
       if (userInfo) {
         showMessage = [NSString stringWithFormat:@"%@:%@", userInfo.name, showMessage];
       }
       resultBlock(userInfo.name, showMessage);
     }];
  } else {
    [[RCUserInfoCacheManager sharedManager]
     getUserInfo:message.targetId
     complete:^(RCUserInfo *userInfo) {
       if (nil == userInfo) {
         return;
       }
       
       showMessage = [NSString
                      stringWithFormat:@"%@:%@",
                      userInfo.name,
                      showMessage];
       resultBlock(userInfo.name, showMessage);
       
     }];
  }
}

- (void)postNotification:(RCMessage *)message dictionary:(NSDictionary *)dictionary {
  [self getNotificationInfo:message result:^(NSString *senderName, NSString *formatedMessage) {
    if ([[RongIMKitExtensionManager sharedManager] handleNotificationForMessageReceived:message from:senderName userInfo:dictionary]) {
      return;
    }
    
    if ([self.receiveMessageDelegate
         respondsToSelector:@selector(onRCIMCustomLocalNotification:withSenderName:)]) {
           if([self.receiveMessageDelegate onRCIMCustomLocalNotification:message
                                                                     withSenderName:senderName])
             return;
         }
    
    if (formatedMessage.length) {
      [[RCLocalNotification defaultCenter] postLocalNotification:formatedMessage userInfo:dictionary];
    }
    
  }];
}

- (void)onReceived:(RCMessage *)message left:(int)nLeft object:(id)object {

    if (message.content.senderUserInfo.userId) {
        if (![message.content.senderUserInfo.userId isEqualToString:[RCIMClient sharedRCIMClient].currentUserInfo.userId]) {
            if (message.content.senderUserInfo.name.length > 0
                || message.content.senderUserInfo.portraitUri.length > 0) {
              if (message.content.senderUserInfo.portraitUri == nil
                  || [RCUtilities isLocalPath:message.content.senderUserInfo.portraitUri]) {
                RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfoFromCacheOnly:message.content.senderUserInfo.userId];
                if (userInfo) {
                  message.content.senderUserInfo.portraitUri = [userInfo.portraitUri copy];
                }
              }
              [[RCUserInfoCacheManager sharedManager] updateUserInfo:message.content.senderUserInfo forUserId:message.content.senderUserInfo.userId];
            }
        }
    }
    
    if ([message.content isMemberOfClass:[RCUserInfoUpdateMessage class]]) {
        RCUserInfoUpdateMessage *userInfoMesasge = (RCUserInfoUpdateMessage *)message.content;
        if ([userInfoMesasge.userInfoList count] > 0) {
            for (RCUserInfo *userInfo in userInfoMesasge.userInfoList) {
                if (![userInfo.userId isEqualToString:[RCIMClient sharedRCIMClient].currentUserInfo.userId] && ![[RCUserInfoCacheManager sharedManager] getUserInfo:userInfo.userId]) {
                    if (userInfo.name.length > 0 || userInfo.portraitUri.length > 0) {
                        [[RCUserInfoCacheManager sharedManager] updateUserInfo:userInfo forUserId:userInfo.userId];
                    }
                }
            }
        }
        return;
    }
  
  if (message.conversationType == ConversationType_APPSERVICE || message.conversationType == ConversationType_PUBLICSERVICE) {
    if (![[RCIMClient sharedRCIMClient] getConversation:message.conversationType targetId:message.targetId]) {
      //如果收到了公众账号消息, 但是没有取到相应的公众账号信息, 导致没有创建会话, 这时候先不进行任何UI刷新
      return;
    }
  }
    
    NSDictionary *dic_left = @{ @"left" : @(nLeft) };
    if ([self.receiveMessageDelegate respondsToSelector:@selector(onRCIMReceiveMessage:left:)]) {
        [self.receiveMessageDelegate onRCIMReceiveMessage:message left:nLeft];
    }
    
    // dispatch message
  
    [[RongIMKitExtensionManager sharedManager] onMessageReceived:message];
  
    [[NSNotificationCenter defaultCenter] postNotificationName:RCKitDispatchMessageNotification
                                                            object:message
                                                          userInfo:dic_left];
    //发出去的消息，不需要本地铃声和通知
    if (message.messageDirection == MessageDirection_SEND) {
        return;
    }
    
    
    BOOL isCustomMessageAlert = YES;

    if (!([[message.content class] persistentFlag] & MessagePersistent_ISPERSISTED)) {
        isCustomMessageAlert = NO;
    }
    if (self.showUnkownMessageNotificaiton && message.messageId > 0 && !message.content) {
        isCustomMessageAlert = YES;
    }
  
    if (0 == nLeft && [RCIMClient sharedRCIMClient].sdkRunningMode == RCSDKRunningMode_Foreground && !self.disableMessageAlertSound && !self.isNotificationQuiet && isCustomMessageAlert) {
               //获取接受到会话
        if ([[RongIMKitExtensionManager sharedManager] handleAlertForMessageReceived:message]) {
            return;
        }
        if (message.content.mentionedInfo.isMentionedMe) {
          BOOL appComsumed = NO;
          if ([self.receiveMessageDelegate
                  respondsToSelector:@selector(onRCIMCustomAlertSound:)]) {
            appComsumed =
                [self.receiveMessageDelegate onRCIMCustomAlertSound:message];
          }
          if (!appComsumed) {

            if (![message.content
                    isKindOfClass:[RCDiscussionNotificationMessage class]]) {
              [[RCSystemSoundPlayer defaultPlayer] playSoundByMessage:message];
            }
          }
        } else {

          [[RCIMClient sharedRCIMClient]
              getConversationNotificationStatus:message.conversationType
              targetId:message.targetId
              success:^(RCConversationNotificationStatus nStatus) {

                if (NOTIFY == nStatus) {
                  BOOL appComsumed = NO;
                  if ([self.receiveMessageDelegate
                          respondsToSelector:@selector(
                                                 onRCIMCustomAlertSound:)]) {
                    appComsumed = [self.receiveMessageDelegate
                        onRCIMCustomAlertSound:message];
                  }
                  if (!appComsumed) {

                    if (![message.content
                            isKindOfClass:
                                [RCDiscussionNotificationMessage class]]) {
                      [[RCSystemSoundPlayer defaultPlayer]
                          playSoundByMessage:message];
                    }
                  }
                }

              }
              error:^(RCErrorCode status){

              }];
        }
    }

    if (0 == nLeft && !self.disableMessageNotificaiton && !self.isNotificationQuiet && [RCIMClient sharedRCIMClient].sdkRunningMode == RCSDKRunningMode_Background && isCustomMessageAlert) {

        //聊天室消息不做本地通知
        if (ConversationType_CHATROOM == message.conversationType)
            return;
        NSDictionary *dictionary = [RCKitUtility getNotificationUserInfoDictionary:message];
        if (message.content.mentionedInfo.isMentionedMe) {
            [self postNotification:message dictionary:dictionary];
        }else{
            [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:message.conversationType
                targetId:message.targetId
                success:^(RCConversationNotificationStatus nStatus) {
                    if (NOTIFY == nStatus) {
                      [self postNotification:message dictionary:dictionary];
                  }
            }
            error:^(RCErrorCode status){

            }];
        }
    }
}

- (void)onMessageRecalled:(long)messageId {
  [[NSNotificationCenter defaultCenter] postNotificationName:RCKitDispatchRecallMessageNotification
                                                      object:@(messageId)
                                                    userInfo:nil];
  
  if ([self.receiveMessageDelegate respondsToSelector:@selector(onRCIMMessageRecalled:)]) {
    [self.receiveMessageDelegate onRCIMMessageRecalled:messageId];
  }
}



- (void)onMessageReceiptResponse:(RCConversationType)conversationType targetId:(NSString *)targetId messageUId:(NSString *)messageUId readerList:(NSDictionary *)userIdList{
    NSDictionary *statusDic = @{@"targetId":targetId,
                                @"conversationType":@(conversationType),
                                @"messageUId": messageUId,
                                @"readerList":userIdList};
    [[NSNotificationCenter defaultCenter] postNotificationName:RCKitDispatchMessageReceiptResponseNotification
                                                        object:statusDic
                                                      userInfo:nil];
}

-(void)onMessageReceiptRequest:(RCConversationType)conversationType targetId:(NSString *)targetId messageUId:(NSString *)messageUId{
    if(messageUId){
        NSDictionary *statusDic = @{@"targetId":targetId,
                                    @"conversationType":@(conversationType),
                                    @"messageUId": messageUId};
        [[NSNotificationCenter defaultCenter] postNotificationName:RCKitDispatchMessageReceiptRequestNotification
                                                            object:statusDic
                                                          userInfo:nil];
    }
}


/**
 *  网络状态变化。
 *
 *  @param status 网络状态。
 */
- (void)onConnectionStatusChanged:(RCConnectionStatus)status {
  if (status == ConnectionStatus_KICKED_OFFLINE_BY_OTHER_CLIENT || status == ConnectionStatus_SignUp || status == ConnectionStatus_TOKEN_INCORRECT) {
    self.hasNotifydExtensionModuleUserId = NO;
    [[RongIMKitExtensionManager sharedManager] didDisconnect];
  }
  
    if (/*ConnectionStatus_NETWORK_UNAVAILABLE != status && */ConnectionStatus_UNKNOWN != status &&
        ConnectionStatus_Unconnected != status) {
        [[NSNotificationCenter defaultCenter] postNotificationName:RCKitDispatchConnectionStatusChangedNotification
                                                        object:[NSNumber numberWithInt:status]];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self performSelector:@selector(delayNotifyUnConnectedStatus) withObject:nil afterDelay:5];
        });
    }
  
  dispatch_async(dispatch_get_main_queue(), ^{
    if (status == ConnectionStatus_Connected && !self.hasNotifydExtensionModuleUserId) {
      self.hasNotifydExtensionModuleUserId = YES;
      dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [[RongIMKitExtensionManager sharedManager] didConnect:[RCIMClient sharedRCIMClient].currentUserInfo.userId];
      });
    }
  });
  
    if (self.connectionStatusDelegate) {
        [self.connectionStatusDelegate onRCIMConnectionStatusChanged:status];
    }
}

/*!
 获取当前SDK的连接状态
 
 @return 当前SDK的连接状态
 */
- (RCConnectionStatus)getConnectionStatus {
    return [[RCIMClient sharedRCIMClient] getConnectionStatus];
}

- (void)delayNotifyUnConnectedStatus {
    RCConnectionStatus status = [[RCIMClient sharedRCIMClient] getConnectionStatus];
    if (ConnectionStatus_NETWORK_UNAVAILABLE == status || ConnectionStatus_UNKNOWN == status ||
        ConnectionStatus_Unconnected == status) {
        [[NSNotificationCenter defaultCenter] postNotificationName:RCKitDispatchConnectionStatusChangedNotification
                                                            object:[NSNumber numberWithInt:status]];
    }
}

#pragma mark - UserInfo&GroupInfo&GroupUserInfo
-(void)setenablePersistentUserInfoCache:(BOOL)enablePersistentUserInfoCache {
    _enablePersistentUserInfoCache = enablePersistentUserInfoCache;
    if (enablePersistentUserInfoCache
        && [RCIMClient sharedRCIMClient].currentUserInfo.userId) {
        [RCUserInfoCacheManager sharedManager].currentUserId = [RCIMClient sharedRCIMClient].currentUserInfo.userId;
    }
}

-(RCUserInfo *)getUserInfoCache:(NSString *)userId {
    return [[RCUserInfoCacheManager sharedManager] getUserInfoFromCacheOnly:userId];
}

- (void)refreshUserInfoCache:(RCUserInfo *)userInfo
                  withUserId:(NSString *)userId {
//    [[RCUserInfoCacheManager sharedManager] clearUserInfoNetworkCacheOnly:userId];
    [[RCUserInfoCacheManager sharedManager] updateUserInfo:userInfo
                                                 forUserId:userId];
}

- (void)clearUserInfoCache {
    [[RCUserInfoCacheManager sharedManager] clearAllUserInfo];
}

-(RCGroup *)getGroupInfoCache:(NSString *)groupId {
    return [[RCUserInfoCacheManager sharedManager] getGroupInfoFromCacheOnly:groupId];
}

- (void)refreshGroupInfoCache:(RCGroup *)groupInfo
                  withGroupId:(NSString *)groupId {
    [[RCUserInfoCacheManager sharedManager] updateGroupInfo:groupInfo
                                                 forGroupId:groupId];
}

- (void)clearGroupInfoCache {
    [[RCUserInfoCacheManager sharedManager] clearAllGroupInfo];
}

-(RCUserInfo *)getGroupUserInfoCache:(NSString *)userId
                         withGroupId:(NSString *)groupId {
    return [[RCUserInfoCacheManager sharedManager] getUserInfoFromCacheOnly:userId
                                                                  inGroupId:groupId];
}

- (void)refreshGroupUserInfoCache:(RCUserInfo *)userInfo
                       withUserId:(NSString *)userId
                      withGroupId:(NSString *)groupId {
    [[RCUserInfoCacheManager sharedManager] updateUserInfo:userInfo
                                                 forUserId:userId
                                                   inGroup:groupId];
}

- (void)clearGroupUserInfoCache {
    [[RCUserInfoCacheManager sharedManager] clearAllGroupUserInfo];
}

- (RCMessage *)sendMessage:(RCConversationType)conversationType
                  targetId:(NSString *)targetId
                   content:(RCMessageContent *)content
               pushContent:(NSString *)pushContent
                  pushData:(NSString *)pushData
                   success:(void (^)(long messageId))successBlock
                     error:(void (^)(RCErrorCode nErrorCode,
                                     long messageId))errorBlock {
  [self attachCurrentUserInfo:content];
    
    RCMessage *rcMessage = [[RCIMClient sharedRCIMClient]
                            sendMessage:conversationType
                            targetId:targetId
                            content:content
                            pushContent:pushContent
                            pushData:pushData
                            success:^(long messageId) {
                                NSDictionary *statusDic = @{@"targetId":targetId,
                                                            @"conversationType":@(conversationType),
                                                            @"messageId": @(messageId),
                                                            @"sentStatus": @(SentStatus_SENT),
                                                            @"content":content};
                                [[NSNotificationCenter defaultCenter]
                                 postNotificationName:RCKitSendingMessageNotification
                                 object:nil
                                 userInfo:statusDic];
                              if(successBlock) {
                                successBlock(messageId);
                              }
                            } error:^(RCErrorCode nErrorCode, long messageId) {
                                NSDictionary *statusDic = @{@"targetId":targetId,
                                                            @"conversationType":@(conversationType),
                                                            @"messageId": @(messageId),
                                                            @"sentStatus": @(SentStatus_FAILED),
                                                            @"error": @(nErrorCode),
                                                            @"content":content};
                                [[NSNotificationCenter defaultCenter]
                                 postNotificationName:RCKitSendingMessageNotification
                                 object:nil
                                 userInfo:statusDic];
                                if (errorBlock) {
                                    errorBlock(nErrorCode,messageId);
                                }
                                
                            }];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:RCKitSendingMessageNotification
                                                        object:rcMessage
                                                      userInfo:nil];
    return rcMessage;
}

- (RCMessage *)sendDirectionalMessage:(RCConversationType)conversationType
                             targetId:(NSString *)targetId
                         toUserIdList:(NSArray *)userIdList
                              content:(RCMessageContent *)content
                          pushContent:(NSString *)pushContent
                             pushData:(NSString *)pushData
                              success:(void (^)(long messageId))successBlock
                                error:(void (^)(RCErrorCode nErrorCode, long messageId))errorBlock {
  [self attachCurrentUserInfo:content];
  
  RCMessage *rcMessage = [[RCIMClient sharedRCIMClient]
                          sendDirectionalMessage:conversationType
                          targetId:targetId
                          toUserIdList:userIdList
                          content:content
                          pushContent:pushContent
                          pushData:pushData
                          success:^(long messageId) {
                            NSDictionary *statusDic = @{@"targetId":targetId,
                                                        @"conversationType":@(conversationType),
                                                        @"messageId": @(messageId),
                                                        @"sentStatus": @(SentStatus_SENT),
                                                        @"content":content};
                            [[NSNotificationCenter defaultCenter]
                             postNotificationName:RCKitSendingMessageNotification
                             object:nil
                             userInfo:statusDic];
                            if(successBlock) {
                              successBlock(messageId);
                            }
                          } error:^(RCErrorCode nErrorCode, long messageId) {
                            NSDictionary *statusDic = @{@"targetId":targetId,
                                                        @"conversationType":@(conversationType),
                                                        @"messageId": @(messageId),
                                                        @"sentStatus": @(SentStatus_FAILED),
                                                        @"error": @(nErrorCode),
                                                        @"content":content};
                            [[NSNotificationCenter defaultCenter]
                             postNotificationName:RCKitSendingMessageNotification
                             object:nil
                             userInfo:statusDic];
                            if (errorBlock) {
                              errorBlock(nErrorCode,messageId);
                            }
                            
                          }];
  
  [[NSNotificationCenter defaultCenter] postNotificationName:RCKitSendingMessageNotification
                                                      object:rcMessage
                                                    userInfo:nil];
  return rcMessage;
}

- (RCMessage *)sendImageMessage:(RCConversationType)conversationType
                       targetId:(NSString *)targetId
                        content:(RCMessageContent *)content
                    pushContent:(NSString *)pushContent
                       pushData:(NSString *)pushData
                       progress:(void (^)(int progress, long messageId))progressBlock
                        success:(void (^)(long messageId))successBlock
                          error:(void (^)(RCErrorCode errorCode, long messageId))errorBlock {
  [self attachCurrentUserInfo:content];
    
    RCMessage *rcMessage = [[RCIMClient sharedRCIMClient]
                            sendImageMessage:conversationType
                            targetId:targetId
                            content:content
                            pushContent:pushContent
                            pushData:pushData
                            progress:^(int progress, long messageId) {
                                NSDictionary *statusDic = @{@"targetId":targetId,
                                                            @"conversationType":@(conversationType),
                                                            @"messageId": @(messageId),
                                                            @"sentStatus": @(SentStatus_SENDING),
                                                            @"progress": @(progress)};
                                [[NSNotificationCenter defaultCenter]
                                 postNotificationName:RCKitSendingMessageNotification
                                 object:nil
                                 userInfo:statusDic];
                                if (progressBlock) {
                                    progressBlock(progress, messageId);
                                }
                                
                            } success:^(long messageId) {
                                NSDictionary *statusDic = @{@"targetId":targetId,
                                                            @"conversationType":@(conversationType),
                                                            @"messageId": @(messageId),
                                                            @"sentStatus": @(SentStatus_SENT),
                                                            @"content":content};
                                [[NSNotificationCenter defaultCenter]
                                 postNotificationName:RCKitSendingMessageNotification
                                 object:nil
                                 userInfo:statusDic];
                                if (successBlock) {
                                    successBlock(messageId);
                                }
                                
                            } error:^(RCErrorCode errorCode, long messageId) {
                                NSDictionary *statusDic = @{@"targetId":targetId,
                                                            @"conversationType":@(conversationType),
                                                            @"messageId": @(messageId),
                                                            @"sentStatus": @(SentStatus_FAILED),
                                                            @"error": @(errorCode),
                                                            @"content":content};
                                [[NSNotificationCenter defaultCenter]
                                 postNotificationName:RCKitSendingMessageNotification
                                 object:nil
                                 userInfo:statusDic];
                                if (errorBlock) {
                                    errorBlock(errorCode, messageId);
                                }
                                
                            }];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:RCKitSendingMessageNotification
                                                        object:rcMessage
                                                      userInfo:nil];
    
    return rcMessage;
}

- (void)downloadMediaMessage:(long)messageId
                    progress:(void (^)(int progress))progressBlock
                     success:(void (^)(NSString *mediaPath))successBlock
                       error:(void (^)(RCErrorCode errorCode))errorBlock
                      cancel:(void (^)())cancelBlock {
  if ([self.downloadingMeidaMessageIds containsObject:@(messageId)]) {
    return;
  }

  [self.downloadingMeidaMessageIds addObject:@(messageId)];

  [[RCIMClient sharedRCIMClient] downloadMediaMessage:messageId
      progress:^(int progress) {
        NSDictionary *statusDic = @{
          @"messageId" : @(messageId),
          @"type" : @"progress",
          @"progress" : @(progress)
        };
        [[NSNotificationCenter defaultCenter]
            postNotificationName:RCKitDispatchDownloadMediaNotification
                          object:nil
                        userInfo:statusDic];
        if (progressBlock) {
          progressBlock(progress);
        }
      }
      success:^(NSString *mediaPath) {
        [self.downloadingMeidaMessageIds removeObject:@(messageId)];

        NSDictionary *statusDic = @{
          @"messageId" : @(messageId),
          @"type" : @"success",
          @"mediaPath" : mediaPath
        };
        [[NSNotificationCenter defaultCenter]
            postNotificationName:RCKitDispatchDownloadMediaNotification
                          object:nil
                        userInfo:statusDic];
        if (successBlock) {
          successBlock(mediaPath);
        }
      }
      error:^(RCErrorCode errorCode) {
        [self.downloadingMeidaMessageIds removeObject:@(messageId)];

        NSDictionary *statusDic = @{
          @"messageId" : @(messageId),
          @"type" : @"error",
          @"errorCode" : @(errorCode)
        };
        [[NSNotificationCenter defaultCenter]
            postNotificationName:RCKitDispatchDownloadMediaNotification
                          object:nil
                        userInfo:statusDic];
        if (errorBlock) {
          errorBlock(errorCode);
        }
      }
      cancel:^{
        [self.downloadingMeidaMessageIds removeObject:@(messageId)];

        NSDictionary *statusDic = @{
          @"messageId" : @(messageId),
          @"type" : @"cancel"
        };
        [[NSNotificationCenter defaultCenter]
            postNotificationName:RCKitDispatchDownloadMediaNotification
                          object:nil
                        userInfo:statusDic];
        if (cancelBlock) {
          cancelBlock();
        }
      }];
}

- (BOOL)cancelDownloadMediaMessage:(long)messageId {
  return [[RCIMClient sharedRCIMClient] cancelDownloadMediaMessage:messageId];
}

- (RCMessage *)sendMediaMessage:(RCConversationType)conversationType
                       targetId:(NSString *)targetId
                        content:(RCMessageContent *)content
                    pushContent:(NSString *)pushContent
                       pushData:(NSString *)pushData
                       progress:(void (^)(int progress, long messageId))progressBlock
                        success:(void (^)(long messageId))successBlock
                          error:(void (^)(RCErrorCode errorCode, long messageId))errorBlock
                         cancel:(void (^)(long messageId))cancelBlock {
  [self attachCurrentUserInfo:content];
  
  RCMessage *rcMessage = [[RCIMClient sharedRCIMClient]
                          sendMediaMessage:conversationType
                          targetId:targetId
                          content:content
                          pushContent:pushContent
                          pushData:pushData
                          progress:^(int progress, long messageId) {
                            NSDictionary *statusDic = @{@"targetId":targetId,
                                                        @"conversationType":@(conversationType),
                                                        @"messageId": @(messageId),
                                                        @"sentStatus": @(SentStatus_SENDING),
                                                        @"progress": @(progress)};
                            [[NSNotificationCenter defaultCenter]
                             postNotificationName:RCKitSendingMessageNotification
                             object:nil
                             userInfo:statusDic];
                            if (progressBlock) {
                              progressBlock(progress, messageId);
                            }
                            
                          } success:^(long messageId) {
                            NSDictionary *statusDic = @{@"targetId":targetId,
                                                        @"conversationType":@(conversationType),
                                                        @"messageId": @(messageId),
                                                        @"sentStatus": @(SentStatus_SENT),
                                                        @"content":content};
                            [[NSNotificationCenter defaultCenter]
                             postNotificationName:RCKitSendingMessageNotification
                             object:nil
                             userInfo:statusDic];
                            if (successBlock) {
                              successBlock(messageId);
                            }
                            
                          } error:^(RCErrorCode errorCode, long messageId) {
                            NSDictionary *statusDic = @{@"targetId":targetId,
                                                        @"conversationType":@(conversationType),
                                                        @"messageId": @(messageId),
                                                        @"sentStatus": @(SentStatus_FAILED),
                                                        @"error": @(errorCode),
                                                        @"content":content};
                            [[NSNotificationCenter defaultCenter]
                             postNotificationName:RCKitSendingMessageNotification
                             object:nil
                             userInfo:statusDic];
                            if (errorBlock) {
                              errorBlock(errorCode, messageId);
                            }
                          } cancel:^(long messageId) {
                            NSDictionary *statusDic = @{@"targetId":targetId,
                                                        @"conversationType":@(conversationType),
                                                        @"messageId": @(messageId),
                                                        @"sentStatus": @(SentStatus_CANCELED),
                                                        @"content":content};
                            [[NSNotificationCenter defaultCenter]
                             postNotificationName:RCKitSendingMessageNotification
                             object:nil
                             userInfo:statusDic];
                            if (cancelBlock) {
                              cancelBlock(messageId);
                            }
                          }];
  
  [[NSNotificationCenter defaultCenter] postNotificationName:RCKitSendingMessageNotification
                                                      object:rcMessage
                                                    userInfo:nil];
  
  return rcMessage;
}

- (BOOL)cancelSendMediaMessage:(long)messageId {
  return [[RCIMClient sharedRCIMClient] cancelSendMediaMessage:messageId];
}

- (void)setMaxVoiceDuration:(NSUInteger)maxVoiceDuration {
    if (maxVoiceDuration < 5 || maxVoiceDuration > 300) {
        return;
    }
    _maxVoiceDuration = maxVoiceDuration;
}

#pragma mark - Discussion
- (void)sendUserInfoUpdateMessageForDiscussion:(NSString *)discussionId
                                    userIdList:(NSArray *)userIdList {
    NSMutableArray *userInfoList = [[NSMutableArray alloc] init];
    for (NSString *userId in userIdList) {
        RCUserInfo *cacheUserInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:userId];
        if (cacheUserInfo.name.length > 0 || cacheUserInfo.portraitUri.length > 0) {
            [userInfoList addObject:cacheUserInfo];
        }
    }
    RCUserInfoUpdateMessage *message = [[RCUserInfoUpdateMessage alloc]
                                        initWithUserInfoList:userInfoList];
  [self attachCurrentUserInfo:message];
    
    [self sendMessage:ConversationType_DISCUSSION
             targetId:discussionId
              content:message
          pushContent:nil
             pushData:nil
              success:^(long messageId) {
                  
              } error:^(RCErrorCode nErrorCode, long messageId) {
                  
              }];
}

- (void)createDiscussion:(NSString *)name
              userIdList:(NSArray *)userIdList
                 success:(void (^)(RCDiscussion *discussion))successBlock
                   error:(void (^)(RCErrorCode status))errorBlock {
    [[RCIMClient sharedRCIMClient] createDiscussion:name
                                         userIdList:userIdList
                                            success:^(RCDiscussion *discussion) {
                                                [self sendUserInfoUpdateMessageForDiscussion:discussion.discussionId userIdList:discussion.memberIdList];
                                              if (successBlock) {
                                                successBlock(discussion);
                                              }
                                            } error:^(RCErrorCode status) {
                                              if (errorBlock) {
                                                errorBlock(status);
                                              }
                                            }];
}

- (void)addMemberToDiscussion:(NSString *)discussionId
                   userIdList:(NSArray *)userIdList
                      success:(void (^)(RCDiscussion *discussion))successBlock
                        error:(void (^)(RCErrorCode status))errorBlock {
    [self sendUserInfoUpdateMessageForDiscussion:discussionId userIdList:userIdList];
    
    [[RCIMClient sharedRCIMClient] addMemberToDiscussion:discussionId
                                              userIdList:userIdList
                                                 success:^(RCDiscussion *discussion) {
                                                   if (successBlock) {
                                                     successBlock(discussion);
                                                   }
                                                 } error:^(RCErrorCode status) {
                                                   if (errorBlock) {
                                                     errorBlock(status);
                                                   }
                                                 }];
}

- (void)removeMemberFromDiscussion:(NSString *)discussionId
                            userId:(NSString *)userId
                           success:(void (^)(RCDiscussion *discussion))successBlock
                             error:(void (^)(RCErrorCode status))errorBlock {
    [self sendUserInfoUpdateMessageForDiscussion:discussionId userIdList:@[userId]];
    
    [[RCIMClient sharedRCIMClient] removeMemberFromDiscussion:discussionId
                                                       userId:userId
                                                      success:^(RCDiscussion *discussion) {
                                                        if (successBlock) {
                                                          successBlock(discussion);
                                                        }
                                                      } error:^(RCErrorCode status) {
                                                        if (errorBlock) {
                                                          errorBlock(status);
                                                        }
                                                      }];
}

- (void)quitDiscussion:(NSString *)discussionId
               success:(void (^)(RCDiscussion *discussion))successBlock
                 error:(void (^)(RCErrorCode status))errorBlock {
    [self sendUserInfoUpdateMessageForDiscussion:discussionId userIdList:nil];
    
    [[RCIMClient sharedRCIMClient] quitDiscussion:discussionId
                                          success:^(RCDiscussion *discussion) {
                                            if (successBlock) {
                                              successBlock(discussion);
                                            }
                                          } error:^(RCErrorCode status) {
                                            if (errorBlock) {
                                              errorBlock(status);
                                            }
                                          }];
}

- (void)getDiscussion:(NSString *)discussionId
              success:(void (^)(RCDiscussion *discussion))successBlock
                error:(void (^)(RCErrorCode status))errorBlock {
    [[RCIMClient sharedRCIMClient] getDiscussion:discussionId
                                         success:^(RCDiscussion *discussion) {
                                           if (successBlock) {
                                             successBlock(discussion);
                                           }
                                         } error:^(RCErrorCode status) {
                                           if (errorBlock) {
                                             errorBlock(status);
                                           }
                                         }];
}

- (void)setDiscussionName:(NSString *)discussionId
                     name:(NSString *)discussionName
                  success:(void (^)())successBlock
                    error:(void (^)(RCErrorCode status))errorBlock {
    [self sendUserInfoUpdateMessageForDiscussion:discussionId userIdList:nil];
    
    [[RCIMClient sharedRCIMClient] setDiscussionName:discussionId
                                                name:discussionName
                                             success:^{
                                               if (successBlock) {
                                                 successBlock();
                                               }
                                             } error:^(RCErrorCode status) {
                                               if (errorBlock) {
                                                 errorBlock(status);
                                               }
                                             }];
}

- (void)setDiscussionInviteStatus:(NSString *)discussionId
                           isOpen:(BOOL)isOpen
                          success:(void (^)())successBlock
                            error:(void (^)(RCErrorCode status))errorBlock {
    [self sendUserInfoUpdateMessageForDiscussion:discussionId userIdList:nil];
    
    [[RCIMClient sharedRCIMClient] setDiscussionInviteStatus:discussionId
                                                      isOpen:isOpen
                                                     success:^{
                                                       if (successBlock) {
                                                         successBlock();
                                                       }
                                                     } error:^(RCErrorCode status) {
                                                       if (errorBlock) {
                                                         errorBlock(status);
                                                       }
                                                     }];
}

//接口向后兼容[[++
- (void)receiveMessageHasReadNotification:(NSNotification *)notification {
  [[NSNotificationCenter defaultCenter]
   postNotificationName:RCKitDispatchReadReceiptNotification
   object:nil
   userInfo:notification.userInfo];
}
//接口向后兼容--]]

- (void)setScheme:(NSString *)scheme forExtensionModule:(NSString *)moduleName {
  [[RongIMKitExtensionManager sharedManager] setScheme:scheme forModule:moduleName];
}

- (BOOL)openExtensionModuleUrl:(NSURL *)url {
  return [[RongIMKitExtensionManager sharedManager] onOpenUrl:url];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)attachCurrentUserInfo:(RCMessageContent *)content {
  if ([RCIM sharedRCIM].enableMessageAttachUserInfo && !content.senderUserInfo) {
    content.senderUserInfo = [[RCUserInfo alloc] init];
    content.senderUserInfo.userId = [RCIMClient sharedRCIMClient].currentUserInfo.userId;
    content.senderUserInfo.name = [RCIMClient sharedRCIMClient].currentUserInfo.name;
    if ([RCUtilities isLocalPath:[RCIMClient sharedRCIMClient].currentUserInfo.portraitUri]) {
      content.senderUserInfo.portraitUri = nil;
    } else {
      content.senderUserInfo.portraitUri = [RCIMClient sharedRCIMClient].currentUserInfo.portraitUri;
    }
  }
}
@end
