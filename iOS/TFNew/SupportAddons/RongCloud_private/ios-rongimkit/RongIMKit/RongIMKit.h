//
//  RongIMKit.h
//  RongIMKit
//
//  Created by xugang on 15/1/13.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

//! Project version number for RongIMKit.
FOUNDATION_EXPORT double RongIMKitVersionNumber;

//! Project version string for RongIMKit.
FOUNDATION_EXPORT const unsigned char RongIMKitVersionString[];

#ifdef DEBUG
#define DebugLog( s, ... ) NSLog( @"[%@:(%d)] %@", [[NSString stringWithUTF8String:__FILE__] lastPathComponent], __LINE__, [NSString stringWithFormat:(s), ##__VA_ARGS__] )
#else
#define DebugLog( s, ... )
#endif

/// IMKit核心类
#import "RCIM.h"
/// 会话列表相关类
#import "RCConversationListViewController.h"
#import "RCPublicServiceListViewController.h"
/// 会话页面相关类
#import "RCConversationViewController.h"
#import "RCPublicServiceSearchViewController.h"
#import "RCPublicServiceChatViewController.h"
#import "RCImagePreviewController.h"
#import "RCImageSlideController.h"
#import "RCLocationPickerViewController.h"
/// 会话列表Cell相关类
#import "RCConversationBaseCell.h"
#import "RCConversationCell.h"
#import "RCConversationModel.h"
/// 消息Cell相关类
#import "RCMessageBaseCell.h"
#import "RCMessageCell.h"
#import "RCTipMessageCell.h"
#import "RCUnknownMessageCell.h"
#import "RCVoiceMessageCell.h"
#import "RCRichContentMessageCell.h"
#import "RCImageMessageCell.h"
#import "RCLocationMessageCell.h"
#import "RCTextMessageCell.h"
#import "RCMessageCellDelegate.h"
#import "RCMessageModel.h"
#import "RCMessageCellNotificationModel.h"
#import "RCImageMessageProgressView.h"
#import "RCFileMessageCell.h"

/// 工具类
#import "RCKitUtility.h"
#import "RCThemeDefine.h"
/// 其他
#import "RCBaseViewController.h"
#import "RCTextView.h"
#import "RCContentView.h"
#import "RCAttributedLabel.h"
#import "RCTipLabel.h"
#import "RCMessageBubbleTipView.h"
#import "RCSettingViewController.h"
#import "RCConversationSettingTableViewHeader.h"
#import "RCPublicServiceProfileViewController.h"
#import "RongIMKitExtensionModule.h"
#import "RCEmoticonTabSource.h"
/// VoIPCall
