//
//  RCKitUtility.m
//  iOS-IMKit
//
//  Created by xugang on 7/7/14.
//  Copyright (c) 2014 Heq.Shinoda. All rights reserved.
//

#import "RCKitUtility.h"
#import "RCIM.h"
#import "RCUserInfoCacheManager.h"
#import "RCKitCommonDefine.h"
#import "RCConversationModel.h"
#import "RCExtensionUtility.h"
#import <SafariServices/SafariServices.h>

@implementation RCKitUtility

+ (NSString *)localizedDescription:(RCMessageContent *)messageContent {
    NSString *objectName = [[messageContent class] getObjectName];
    return NSLocalizedStringFromTable(objectName, @"RongCloudKit", nil);
}

+ (NSString *)ConvertMessageTime:(long long)secs {
    NSString *timeText = nil;
  NSString *formatStringForHours = [NSDateFormatter dateFormatFromTemplate:@"j" options:0 locale:[NSLocale currentLocale]];
  NSRange containsA = [formatStringForHours rangeOfString:@"a"];
  
  BOOL hasAMPM = containsA.location != NSNotFound;
    NSDate *messageDate = [NSDate dateWithTimeIntervalSince1970:secs];
  NSDate *now = [NSDate date];
  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@"yyyy-M-d"];
  
  //是否同天
  NSString *strMsgDay = [formatter stringFromDate:messageDate];
  NSString *strToday = [formatter stringFromDate:now];
  
  //昨天
  NSDate *yesterday = [[NSDate alloc] initWithTimeIntervalSinceNow:-(24 * 60 * 60)];
  NSString *strYesterday = [formatter stringFromDate:yesterday];
  
  //是否是同月
  [formatter setDateFormat:@"yyyy-M"];
  NSString *strMsgDay1 = [formatter stringFromDate:messageDate];
  NSString *strToday1 = [formatter stringFromDate:now];
  
  //是否同年
  [formatter setDateFormat:@"yyyy"];
  NSString *strMsgDay2 = [formatter stringFromDate:messageDate];
  NSString *strToday2 = [formatter stringFromDate:now];
  
  NSString *formatStr = nil;
  if (hasAMPM){
    NSString *locale = NSLocalizedStringFromTable(@"locale", @"RongCloudKit", nil);
    [formatter setLocale:[[NSLocale alloc]initWithLocaleIdentifier:locale]];
    if ([locale isEqualToString:@"zh_CN"]) {
      if ([[self class] isBetweenFromHour:0 toHour:6 currentDate:messageDate]) {
        formatStr = @"凌晨 h:mm";
      }else if([[self class] isBetweenFromHour:6 toHour:12 currentDate:messageDate]){
        formatStr = @"上午 h:mm";
      }else if([[self class] isBetweenFromHour:12 toHour:13 currentDate:messageDate]){
        formatStr = @"中午 h:mm";
      }else if([[self class] isBetweenFromHour:13 toHour:18 currentDate:messageDate]){
        formatStr = @"下午 h:mm";
      }else if([[self class] isBetweenFromHour:18 toHour:24 currentDate:messageDate]){
        formatStr = @"晚上 h:mm";
      }
    }else{
      formatStr = @"h:mm a";
    }
  }else{
    formatStr = @"HH:mm";
  }
  if ([strMsgDay isEqualToString:strToday]) {
    [formatter setDateFormat:formatStr];
    return  timeText = [formatter stringFromDate:messageDate];
  } else if ([strMsgDay isEqualToString:strYesterday]) {
    return timeText = NSLocalizedStringFromTable(@"Yesterday", @"RongCloudKit", nil);
  }else if ([strMsgDay1 isEqualToString:strToday1]){
    NSDate *yesterday = [[NSDate alloc] initWithTimeIntervalSinceNow:-(7*24 * 60 * 60)];
    if ([[messageDate earlierDate:yesterday] isEqualToDate:yesterday]) {
      [formatter setDateFormat:@"eeee"];
      return  timeText = [formatter stringFromDate:messageDate];
    }
  }
  
  if ([strMsgDay2 isEqualToString:strToday2]){
    [formatter setDateFormat:NSLocalizedStringFromTable(@"SameYearDate", @"RongCloudKit", nil)];
    return timeText = [formatter stringFromDate:messageDate];
  }
  [formatter setDateFormat:NSLocalizedStringFromTable(@"chatListDate", @"RongCloudKit", nil)];
  return  timeText = [formatter stringFromDate:messageDate];
}

+ (NSString *)ConvertChatMessageTime:(long long)secs {
  NSString *timeText = nil;
  
  NSString *formatStringForHours = [NSDateFormatter dateFormatFromTemplate:@"j" options:0 locale:[NSLocale currentLocale]];
  NSRange containsA = [formatStringForHours rangeOfString:@"a"];
  BOOL hasAMPM = containsA.location != NSNotFound;
  
  NSDate *messageDate = [NSDate dateWithTimeIntervalSince1970:secs];
  NSDate *now = [NSDate date];
  NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
  [formatter setDateFormat:@"yyyy-M-d"];
  
  //是否同天
  NSString *strMsgDay = [formatter stringFromDate:messageDate];
  NSString *strToday = [formatter stringFromDate:now];
  NSDate *yesterday = [[NSDate alloc] initWithTimeIntervalSinceNow:-(24 * 60 * 60)];
  NSString *strYesterday = [formatter stringFromDate:yesterday];
  
  //是否是同月
  [formatter setDateFormat:@"yyyy-M"];
  NSString *strMsgDay1 = [formatter stringFromDate:messageDate];
  NSString *strToday1 = [formatter stringFromDate:now];
  
  NSString *formatStr = nil;
  if (hasAMPM){
    if ([[self class] isBetweenFromHour:0 toHour:6 currentDate:messageDate]) {
      formatStr = NSLocalizedStringFromTable(@"Dawn", @"RongCloudKit", nil);
    }else if([[self class] isBetweenFromHour:6 toHour:12 currentDate:messageDate]){
      formatStr = NSLocalizedStringFromTable(@"Forenoon", @"RongCloudKit", nil);
    }else if([[self class] isBetweenFromHour:12 toHour:13 currentDate:messageDate]){
      formatStr = NSLocalizedStringFromTable(@"Noon", @"RongCloudKit", nil);
    }else if([[self class] isBetweenFromHour:13 toHour:18 currentDate:messageDate]){
      formatStr = NSLocalizedStringFromTable(@"Afternoon", @"RongCloudKit", nil);
    }else if([[self class] isBetweenFromHour:18 toHour:24 currentDate:messageDate]){
      formatStr = NSLocalizedStringFromTable(@"Evening", @"RongCloudKit", nil);
    }
  }else{
    formatStr = @"HH:mm";
  }
  [formatter setDateFormat:formatStr];
  if ([strMsgDay isEqualToString:strToday]) {
    return  timeText = [formatter stringFromDate:messageDate];
  } else if ([strMsgDay isEqualToString:strYesterday]) {
    return timeText = [NSString stringWithFormat:@"%@ %@", NSLocalizedStringFromTable(@"Yesterday", @"RongCloudKit", nil),[formatter stringFromDate:messageDate]];
  }else if ([strMsgDay1 isEqualToString:strToday1]){
    NSDate *yesterday = [[NSDate alloc] initWithTimeIntervalSinceNow:-(7*24 * 60 * 60)];
    if ([[messageDate earlierDate:yesterday] isEqualToDate:yesterday]) {
      NSDateFormatter * dateFormatter = [[NSDateFormatter alloc] init];
      if (hasAMPM) {
        [dateFormatter setLocale:[[NSLocale alloc]initWithLocaleIdentifier:NSLocalizedStringFromTable(@"locale", @"RongCloudKit", nil)]];
      }
      [dateFormatter setDateFormat:[NSString stringWithFormat:@"eeee %@",formatStr]];
      return  timeText = [dateFormatter stringFromDate:messageDate];
    }
  }
  
  [formatter setDateFormat:[NSString stringWithFormat:@"%@ %@",NSLocalizedStringFromTable(@"chatDate", @"RongCloudKit", nil),formatStr]];
  return  timeText = [formatter stringFromDate:messageDate];
}

+ (BOOL)isBetweenFromHour:(NSInteger)fromHour toHour:(NSInteger)toHour currentDate:(NSDate *)currentDate{
  NSDate *date1 = [self getCustomDateWithHour:fromHour currentDate:currentDate];
  NSDate *date2 = [self getCustomDateWithHour:toHour currentDate:currentDate];
  if ([currentDate compare:date1] == NSOrderedDescending && ([currentDate compare:date2]==NSOrderedAscending || [currentDate compare:date1] == NSOrderedSame))
    return YES;
  return NO;
}

+ (NSDate *)getCustomDateWithHour:(NSInteger)hour currentDate:(NSDate *)currentDate{
  NSCalendar *currentCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
  NSDateComponents *currentComps;
  NSInteger unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSWeekdayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit;
  currentComps = [currentCalendar components:unitFlags fromDate:currentDate];
  //设置当天的某个点
  NSDateComponents *resultComps = [[NSDateComponents alloc] init];
  [resultComps setYear:[currentComps year]];
  [resultComps setMonth:[currentComps month]];
  [resultComps setDay:[currentComps day]];
  [resultComps setHour:hour];
  NSCalendar *resultCalendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
  return [resultCalendar dateFromComponents:resultComps];
}

+ (UIImage *)imageNamed:(NSString *)name ofBundle:(NSString *)bundleName {
  return [RCExtensionUtility imageNamed:name ofBundle:bundleName];
}

//导航使用
+ (UIImage *)createImageWithColor:(UIColor *)color {
    CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    return theImage;
}

+ (CGSize)getTextDrawingSize:(NSString *)text
                        font:(UIFont *)font
             constrainedSize:(CGSize)constrainedSize {
  return [RCExtensionUtility getTextDrawingSize:text font:font constrainedSize:constrainedSize];
}

+ (NSString *)formatMessage:(RCMessageContent *)messageContent {
    if ([messageContent respondsToSelector:@selector(conversationDigest)]) {
      NSString *formatedMsg = [messageContent performSelector:@selector(conversationDigest)];
      if (formatedMsg.length > 500) {
        formatedMsg = [formatedMsg substringToIndex:500];
        formatedMsg = [formatedMsg stringByAppendingString:@"..."];
      }
      return formatedMsg;
    } else if ([messageContent isMemberOfClass:RCDiscussionNotificationMessage.class]) {
        RCDiscussionNotificationMessage *notification = (RCDiscussionNotificationMessage *)messageContent;
        return [RCKitUtility __formatDiscussionNotificationMessageContent:notification];
    } else if ([messageContent isMemberOfClass:RCGroupNotificationMessage.class]) {
        RCGroupNotificationMessage *notification = (RCGroupNotificationMessage *)messageContent;
        return [RCKitUtility __formatGroupNotificationMessageContent:notification];
    } else if ([messageContent isMemberOfClass:RCRecallNotificationMessage.class]) {
        RCRecallNotificationMessage *notification = (RCRecallNotificationMessage *)messageContent;
        return [RCKitUtility __formatRCRecallNotificationMessageContent:notification];
    } else if ([messageContent isMemberOfClass:[RCContactNotificationMessage class]]) {
        RCContactNotificationMessage *notification = (RCContactNotificationMessage *)messageContent;
        return [RCKitUtility __formatContactNotificationMessageContent:notification];
    }else {
      return [RCKitUtility localizedDescription:messageContent];
    }
}

#pragma mark private method
+ (NSString *)__formatContactNotificationMessageContent:(RCContactNotificationMessage *)contactNotification {
    RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:contactNotification.sourceUserId];
    if (userInfo.name.length) {
      if ([contactNotification.operation isEqualToString:@"Request"]) {
        return [NSString stringWithFormat:NSLocalizedStringFromTable(@"FromFriendInvitation",@"RongCloudKit",nil),userInfo.name];
      }
      if ([contactNotification.operation isEqualToString:@"AcceptResponse"]) {
        return [NSString stringWithFormat:NSLocalizedStringFromTable(@"AcceptFriendRequest",@"RongCloudKit",nil)];
      }
    } else {
        return NSLocalizedStringFromTable(@"AddFriendInvitation",@"RongCloudKit",nil);
    }
    return nil;
}

+ (NSString *)__formatGroupNotificationMessageContent:(RCGroupNotificationMessage *)groupNotification {
    NSString *message = nil;
    
    NSData *jsonData = [groupNotification.data dataUsingEncoding:NSUTF8StringEncoding];
  if (jsonData == nil) {
    return nil;
  }
    NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
  NSString *operatorUserId = [dictionary[@"operatorUserId"] isKindOfClass:[NSString class]]? dictionary[@"operatorUserId"]:nil;
    NSString *nickName = [dictionary[@"operatorNickname"] isKindOfClass:[NSString class]]? dictionary[@"operatorNickname"]: nil;
    NSArray *targetUserNickName = [dictionary[@"targetUserDisplayNames"] isKindOfClass:[NSArray class]]? dictionary[@"targetUserDisplayNames"]: nil;
  NSArray *targetUserIds = [dictionary[@"targetUserIds"] isKindOfClass:[NSArray class]]? dictionary[@"targetUserIds"]: nil;
    if ([groupNotification.operatorUserId isEqualToString:[RCIM sharedRCIM].currentUserInfo.userId]) {
        nickName = NSLocalizedStringFromTable(@"You", @"RongCloudKit", nil);
    }
    if ([groupNotification.operation isEqualToString:@"Create"]) {
        message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupCreated", @"RongCloudKit", nil),nickName];
    } else if ([groupNotification.operation isEqualToString:@"Add"]) {
        if (targetUserNickName.count == 0) {
            message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupJoin", @"RongCloudKit", nil),nickName];
        } else {
            NSMutableString *names = [[NSMutableString alloc] init];
          NSMutableString *userIdStr = [[NSMutableString alloc] init];
            for (NSUInteger index = 0; index < targetUserNickName.count; index++) {
                [names appendString:targetUserNickName[index]];
                if (index != targetUserNickName.count - 1) {
                    [names appendString:NSLocalizedStringFromTable(@"punctuation", @"RongCloudKit", nil)];
                }
            }
          for (NSUInteger index = 0; index < targetUserIds.count; index++) {
            [userIdStr appendString:targetUserIds[index]];
            if (index != targetUserNickName.count - 1) {
              [userIdStr appendString:NSLocalizedStringFromTable(@"punctuation", @"RongCloudKit", nil)];
            }
          }
            if ([operatorUserId isEqualToString:userIdStr]) {
              message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupJoin", @"RongCloudKit", nil),nickName];
            }
            else
            {
                message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupInvited", @"RongCloudKit", nil),nickName,names];
            }
        }
    } else if ([groupNotification.operation isEqualToString:@"Quit"]) {
        message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupQuit", @"RongCloudKit", nil),nickName];
    } else if ([groupNotification.operation isEqualToString:@"Kicked"]) {
        NSMutableString *names = [[NSMutableString alloc] init];
        for (NSUInteger index = 0; index < targetUserNickName.count; index++) {
            [names appendString:targetUserNickName[index]];
            if (index != targetUserNickName.count - 1) {
                [names appendString:NSLocalizedStringFromTable(@"punctuation", @"RongCloudKit", nil)];
            }
        }
        message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupRemoved", @"RongCloudKit", nil),nickName,names];
    } else if ([groupNotification.operation isEqualToString:@"Rename"]) {
        NSString *groupName = [dictionary[@"targetGroupName"] isKindOfClass:[NSString class]]?dictionary[@"targetGroupName"]: nil;
        message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupChanged", @"RongCloudKit", nil),nickName,groupName];
    } else if ([groupNotification.operation isEqualToString:@"Dismiss"]) {
        message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"GroupDismiss", @"RongCloudKit", nil),nickName];
    }
    return message;
}

+ (NSString *)__formatDiscussionNotificationMessageContent:(RCDiscussionNotificationMessage *)discussionNotification {
    if (nil == discussionNotification) {
        DebugLog(@"[RongIMKit] : No userInfo in cache & db");
        return nil;
    }
    NSArray *operatedIds = nil;
    NSString *operationInfo = nil;

    //[RCKitUtility sharedInstance].discussionNotificationOperatorName = userInfo.name;
    switch (discussionNotification.type) {
    case RCInviteDiscussionNotification:
    case RCRemoveDiscussionMemberNotification: {
        NSString *trimedExtension = [discussionNotification.extension
            stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        NSArray *ids = [trimedExtension componentsSeparatedByString:@","];
        if (!ids || ids.count == 0) {
            ids = [NSArray arrayWithObject:trimedExtension];
        }
        operatedIds = ids;
    } break;
    case RCQuitDiscussionNotification:
        break;

    case RCRenameDiscussionTitleNotification:
    case RCSwichInvitationAccessNotification:
        operationInfo = discussionNotification.extension;
        break;

    default:
        break;
    }

    // NSString *format = nil;
    NSString *message = nil;
    NSString *target = nil;
    NSString *userId = [RCIMClient sharedRCIMClient].currentUserInfo.userId;
    if (operatedIds) {
        if (operatedIds.count == 1) {
            if ([operatedIds[0] isEqualToString:userId]) {
                target = NSLocalizedStringFromTable(@"You", @"RongCloudKit", nil);
            } else {
                RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:operatedIds[0]];
                if ([userInfo.name length]) {
                    target = userInfo.name;
                } else {
                    target = [[NSString alloc] initWithFormat:@"user<%@>", operatedIds[0]];
                }
            }
        } else {
            NSString *_members = NSLocalizedStringFromTable(@"MemberNumber", @"RongCloudKit", nil);
            target = [NSString stringWithFormat:@"%lu %@", (unsigned long)operatedIds.count, _members, nil];
            // target = [NSString stringWithFormat:NSLocalizedString(@"%d位成员", nil), operatedIds.count, nil];
        }
    }

    NSString *operator = discussionNotification.operatorId;
    if ([operator isEqualToString:userId]) {
        operator = NSLocalizedStringFromTable(@"You", @"RongCloudKit", nil);
    } else {
        RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:operator];
         if ([userInfo.name length]) {
            operator = userInfo.name;
        } else {
            operator = [[NSString alloc] initWithFormat:@"user<%@>", operator];
        }
    }
    switch (discussionNotification.type) {
    case RCInviteDiscussionNotification: {
        NSString *_invite = NSLocalizedStringFromTable(@"Invite", @"RongCloudKit", nil);
        NSString *_joinDiscussion = NSLocalizedStringFromTable(@"JoinDiscussion", @"RongCloudKit", nil);
            message = [NSString stringWithFormat:@"%@ %@ %@ %@",operator, _invite,target,_joinDiscussion, nil];
            //            format = NSLocalizedString(@"%@邀请%@加入了讨论组", nil);
            //            message = [NSString stringWithFormat:format, operator, target, nil];
    } break;
    case RCQuitDiscussionNotification: {
        NSString *_quitDiscussion = NSLocalizedStringFromTable(@"QuitDiscussion", @"RongCloudKit", nil);

        // format = NSLocalizedString(@"%@退出了讨论组", nil);
            message = [NSString stringWithFormat:@"%@ %@", operator,_quitDiscussion, nil];
    } break;

    case RCRemoveDiscussionMemberNotification: {
        // format = NSLocalizedString(@"%@被%@移出了讨论组", nil);
        NSString *_by = NSLocalizedStringFromTable(@"By", @"RongCloudKit", nil);
        NSString *_removeDiscussion = NSLocalizedStringFromTable(@"RemoveDiscussion", @"RongCloudKit", nil);
            message = [NSString stringWithFormat:@"%@ %@ %@ %@", operator,_by, target,_removeDiscussion,nil];
    } break;
    case RCRenameDiscussionTitleNotification: {
        // format = NSLocalizedString(@"%@修改讨论组为\"%@\"", nil);
        NSString *_modifyDiscussion = NSLocalizedStringFromTable(@"ModifyDiscussion", @"RongCloudKit", nil);
        target = operationInfo;
            message = [NSString stringWithFormat:@"%@ %@ \"%@\"", operator,_modifyDiscussion, target, nil];
    } break;
    case RCSwichInvitationAccessNotification: {
        // 1 for off, 0 for on
        BOOL canInvite = [operationInfo isEqualToString:@"1"] ? NO : YES;
        target = canInvite ? NSLocalizedStringFromTable(@"Open", @"RongCloudKit", nil)
                           : NSLocalizedStringFromTable(@"Close", @"RongCloudKit", nil);

        NSString *_inviteStatus = NSLocalizedStringFromTable(@"InviteStatus", @"RongCloudKit", nil);

        // format = NSLocalizedString(@"%@%@了成员邀请", nil);
        message =
            [NSString stringWithFormat:@"%@ %@ %@", operator, target, _inviteStatus, nil];
    }
    default:
        break;
    }
    return message;
}

+ (NSString *)__formatRCRecallNotificationMessageContent:
    (RCRecallNotificationMessage *)recallNotificationMessageNotification {
  if (!recallNotificationMessageNotification ||
      !recallNotificationMessageNotification.operatorId) {
    return nil;
  }

  NSString *currentUserId =
      [RCIMClient sharedRCIMClient].currentUserInfo.userId;
  NSString *operator= recallNotificationMessageNotification.operatorId;

  NSString *recall =
      NSLocalizedStringFromTable(@"MessageRecalled", @"RongCloudKit", nil);
  if ([operator isEqualToString:currentUserId]) {
    operator= NSLocalizedStringFromTable(@"You", @"RongCloudKit", nil);
  } else {
    RCUserInfo *userInfo = [[RCUserInfoCacheManager sharedManager] getUserInfo:operator];
    if ([userInfo.name length]) {
      operator= userInfo.name;
    } else {
      operator= [[NSString alloc] initWithFormat:@"user<%@>", operator];
    }
  }
  return [NSString stringWithFormat:@"%@%@",operator, recall, nil];
}

+ (BOOL)isVisibleMessage:(RCMessage *)message {
  if ([[message.content class] persistentFlag] & MessagePersistent_ISPERSISTED) {
    return YES;
  } else if (!message.content && message.messageId > 0 && [RCIM sharedRCIM].showUnkownMessage) {
    return YES;
  }
  return NO;
}

+ (BOOL)isUnkownMessage:(long)messageId content:(RCMessageContent *)content {
  if (!content && messageId > 0 && [RCIM sharedRCIM].showUnkownMessage) {
    return YES;
  }
  return NO;
}

+ (NSDictionary *)getNotificationUserInfoDictionary:(RCMessage *)message {
    return [RCKitUtility getNotificationUserInfoDictionary:message.conversationType fromUserId:message.senderUserId targetId:message.targetId objectName:message.objectName messageId:message.messageId];
}

+ (NSDictionary *)getNotificationUserInfoDictionary:(RCConversationType)conversationType fromUserId:(NSString *)fromUserId targetId:(NSString *)targetId objectName:(NSString *)objectName {
    
    return [RCKitUtility getNotificationUserInfoDictionary:conversationType fromUserId:fromUserId targetId:targetId objectName:objectName messageId:0];
}

+ (NSDictionary *)getNotificationUserInfoDictionary:(RCConversationType)conversationType fromUserId:(NSString *)fromUserId targetId:(NSString *)targetId objectName:(NSString *)objectName messageId:(long)messageId {
    NSString *type = @"PR";
    switch (conversationType) {
        case ConversationType_PRIVATE:
            type = @"PR";
            break;
        case ConversationType_GROUP:
            type = @"GRP";
            break;
        case ConversationType_DISCUSSION:
            type = @"DS";
            break;
        case ConversationType_CUSTOMERSERVICE:
            type = @"CS";
            break;
        case ConversationType_SYSTEM:
            type = @"SYS";
            break;
        case ConversationType_APPSERVICE:
            type = @"MC";
            break;
        case ConversationType_PUBLICSERVICE:
            type = @"MP";
            break;
        case ConversationType_PUSHSERVICE:
            type = @"PH";
            break;
        default:
            return nil;
    }
    return @{@"rc":@{@"cType":type, @"fId":fromUserId, @"oName":objectName, @"tId":targetId, @"mId":[NSString stringWithFormat:@"%ld" ,messageId]}};
}

+ (NSDictionary *)getNotificationUserInfoDictionary:(RCConversationType)conversationType fromUserId:(NSString *)fromUserId targetId:(NSString *)targetId messageContent:(RCMessageContent *)messageContent {
    NSString *type = @"PR";
    switch (conversationType) {
        case ConversationType_PRIVATE:
            type = @"PR";
            break;
        case ConversationType_GROUP:
            type = @"GRP";
            break;
        case ConversationType_DISCUSSION:
            type = @"DS";
            break;
        case ConversationType_CUSTOMERSERVICE:
            type = @"CS";
            break;
        case ConversationType_SYSTEM:
            type = @"SYS";
            break;
        case ConversationType_APPSERVICE:
            type = @"MC";
            break;
        case ConversationType_PUBLICSERVICE:
            type = @"MP";
            break;
        case ConversationType_PUSHSERVICE:
            type = @"PH";
            break;
        default:
            return nil;
    }
    return @{@"rc":@{@"cType":type, @"fId":fromUserId, @"oName":[[messageContent class] getObjectName], @"tId":targetId}};
}

+ (NSString *)getFileTypeIcon:(NSString *)fileType {
  return [RCExtensionUtility getFileTypeIcon:fileType];
}

+ (NSString *)getReadableStringForFileSize:(long long)byteSize {
  float kSize = (float)byteSize / 1024;
  if (kSize < 0) {
    kSize = 0;
  } else if (kSize >= 1024) {
    kSize = kSize / 1024;
    return [NSString stringWithFormat:@"%.2fM", kSize];
  }
  return [NSString stringWithFormat:@"%.2fK", kSize];
}

+ (UIImage *)defaultConversationHeaderImage:(RCConversationModel *)model {
  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_NORMAL) {
    if (model.conversationType == ConversationType_SYSTEM ||
        model.conversationType == ConversationType_PRIVATE ||
        model.conversationType == ConversationType_CUSTOMERSERVICE) {
      return IMAGE_BY_NAMED(@"default_portrait_msg");
    } else if (model.conversationType == ConversationType_GROUP) {
      return IMAGE_BY_NAMED(@"default_group_portrait");
    } else if (model.conversationType == ConversationType_DISCUSSION) {
      return IMAGE_BY_NAMED(@"default_discussion_portrait");
    }
  } else if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
    if (model.conversationType == ConversationType_PRIVATE ||
        model.conversationType == ConversationType_SYSTEM) {
      return IMAGE_BY_NAMED(@"default_portrait");
    } else if (model.conversationType == ConversationType_CUSTOMERSERVICE) {
      return IMAGE_BY_NAMED(@"portrait_kefu");
    } else if (model.conversationType == ConversationType_DISCUSSION) {
      return IMAGE_BY_NAMED(@"default_discussion_collection_portrait");
    } else if (model.conversationType == ConversationType_GROUP) {
      return IMAGE_BY_NAMED(@"default_collection_portrait");
    }
  } else if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_PUBLIC_SERVICE) {
    return IMAGE_BY_NAMED(@"default_portrait");
  }
  return IMAGE_BY_NAMED(@"default_portrait");
}

+ (NSString *)defaultTitleForCollectionConversation:(RCConversationType)conversationType {
  if (conversationType == ConversationType_PRIVATE) {
    return NSLocalizedStringFromTable(@"conversation_private_collection_title", @"RongCloudKit", nil);
  } else if (conversationType == ConversationType_DISCUSSION) {
    return NSLocalizedStringFromTable(@"conversation_discussion_collection_title", @"RongCloudKit", nil);
  } else if (conversationType == ConversationType_GROUP) {
    return NSLocalizedStringFromTable(@"conversation_group_collection_title", @"RongCloudKit", nil);
  } else if (conversationType == ConversationType_CUSTOMERSERVICE) {
    return NSLocalizedStringFromTable(@"conversation_customer_collection_title", @"RongCloudKit", nil);
  } else if (conversationType == ConversationType_SYSTEM) {
    return NSLocalizedStringFromTable(@"conversation_systemMessage_collection_title", @"RongCloudKit", nil);
  }
  return nil;
}

+ (int)getConversationUnreadCount:(RCConversationModel *)model {
  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
    return [[RCIMClient sharedRCIMClient] getUnreadCount:@[@(model.conversationType)]];
  } else {
    return [[RCIMClient sharedRCIMClient] getUnreadCount:model.conversationType targetId:model.targetId];
  }
}

+ (BOOL)getConversationUnreadMentionedStatus:(RCConversationModel *)model {
  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
    return
        [[RCIMClient sharedRCIMClient] getUnreadMentionedCount:@[ @(model.conversationType) ]] != 0;
  } else {
    return [[RCIMClient sharedRCIMClient] getConversation:model.conversationType
                                                 targetId:model.targetId]
        .hasUnreadMentioned;
  }
}

+ (void)syncConversationReadStatusIfEnabled:(RCConversation *)conversation {
  if (conversation.conversationType == ConversationType_PRIVATE &&
      [[RCIM sharedRCIM]
              .enabledReadReceiptConversationTypeList
          containsObject:@(conversation.conversationType)]) {
    [[RCIMClient sharedRCIMClient] sendReadReceiptMessage:conversation.conversationType
                                                 targetId:conversation.targetId
                                                     time:conversation.sentTime
                                                  success:nil
                                                    error:nil];
  } else if ((conversation.conversationType == ConversationType_PRIVATE &&
              ![[RCIM sharedRCIM]
                      .enabledReadReceiptConversationTypeList
                  containsObject:@(conversation.conversationType)]) ||
             conversation.conversationType == ConversationType_GROUP ||
             conversation.conversationType == ConversationType_DISCUSSION) {
    [[RCIMClient sharedRCIMClient] syncConversationReadStatus:conversation.conversationType
                                                     targetId:conversation.targetId
                                                         time:conversation.sentTime
                                                      success:nil
                                                        error:nil];
  }
}

+ (NSString *)getPinYinUpperFirstLetters:(NSString *)hanZi {
  return [RCExtensionUtility getPinYinUpperFirstLetters:hanZi];
}

+ (void)openURLInSafariViewOrWebView:(NSString *)url base:(UIViewController *)viewController {
  url = [self checkOrAppendHttpForUrl:url];
  if (![RCIM sharedRCIM].embeddedWebViewPreferred && RC_IOS_SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"9.0")) {
    SFSafariViewController *safari = [[SFSafariViewController alloc] initWithURL:[NSURL URLWithString:url]];
    [viewController presentViewController:safari animated:YES completion:nil];
  } else {
    UIViewController *webview = [[RCIMClient sharedRCIMClient] getPublicServiceWebViewController:url];
    [viewController.navigationController pushViewController:webview animated:YES];
  }
}

+ (NSString *)checkOrAppendHttpForUrl:(NSString *)url {
  if(![url hasPrefix:@"http://"] && ![url hasPrefix:@"https://"]){
    url = [NSString stringWithFormat:@"http://%@",url];
  }
  return url;
}

+ (BOOL)validateCellPhoneNumber:(NSString *)cellNum{
  /**
   * 手机号码
   * 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
   * 联通：130,131,132,152,155,156,185,186
   * 电信：133,1349,153,180,189
   */
  NSString * MOBILE = @"^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
  
  /**
   10         * 中国移动：China Mobile
   11         * 134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
   12         */
  NSString * CM = @"^1(34[0-8]|(3[5-9]|5[017-9]|8[278])\\d)\\d{7}$";
  
  /**
   15         * 中国联通：China Unicom
   16         * 130,131,132,152,155,156,185,186
   17         */
  NSString * CU = @"^1(3[0-2]|5[256]|8[56])\\d{8}$";
  
  /**
   20         * 中国电信：China Telecom
   21         * 133,1349,153,177,180,189
   22         */
  NSString * CT = @"^1((33|53|77|8[09])[0-9]|349)\\d{7}$";
  
  /**
   25         * 大陆地区固话及小灵通
   26         * 区号：010,020,021,022,023,024,025,027,028,029
   27         * 号码：七位或八位
   28         */
  // NSString * PHS = @"^0(10|2[0-5789]|\\d{3})\\d{7,8}$";
  
  NSPredicate *regextestmobile = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", MOBILE];
  
  NSPredicate *regextestcm = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", CM];
  
  NSPredicate *regextestcu = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", CU];
  
  NSPredicate *regextestct = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", CT];
  // NSPredicate *regextestPHS = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", PHS];
  
  if(([regextestmobile evaluateWithObject:cellNum] == YES)
     || ([regextestcm evaluateWithObject:cellNum] == YES)
     || ([regextestct evaluateWithObject:cellNum] == YES)
     || ([regextestcu evaluateWithObject:cellNum] == YES)){
    return YES;
  }else{
    return NO;
  }
}

+ (BOOL) validateEmail:(NSString *)email
{
  NSString *emailRegex =@"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
  NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
  return [emailTest evaluateWithObject:email];
}
@end
