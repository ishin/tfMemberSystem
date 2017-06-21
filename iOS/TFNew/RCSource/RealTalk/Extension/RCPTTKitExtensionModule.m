//
//  RCPTTKitExtensionModule.m
//  RongPTTKit
//
//  Created by Sin on 16/12/26.
//  Copyright © 2016年 Sin. All rights reserved.
//

#import "RCPTTKitExtensionModule.h"
//#import "RCPTTTalkViewController.h"
#import "RCPTTStatusView.h"
#import "RCPTTUtilities.h"
#import "RCPTT.h"
#import <RongPTTLib/RongPTTLib.h>
#import "RCPTTBeginMessageCell.h"
#import "RCPTTEndMessageCell.h"

@interface RCPTTKitExtensionModule ()<RCPTTStatusViewDelegate>
@property (nonatomic, strong) RCChatSessionInputBarControl *chatBarControl;
@property (nonatomic, assign) RCConversationType conversationType;
@property (nonatomic, copy) NSString *targetId;
@property (nonatomic, assign) KBottomBarStatus currentStatus;
@property (nonatomic, strong) RCPTTStatusView *pttStatusView;
@property (nonatomic, strong) UIView *extensionView;
@end

@implementation RCPTTKitExtensionModule
+ (instancetype)sharedRCPTTKitExtensionModule {
  static RCPTTKitExtensionModule *module = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    module = [[self alloc] init];
  });
  return module;
}

- (instancetype)init
{
  self = [super init];
  if (self) {
      [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(pttstatusViewUpdate:) name:RCPTTSessionStatusChangeNotification object:nil];
  }
  return self;
}

- (void)pttstatusViewUpdate:(NSNotification *)notification {
  if([notification.name isEqualToString:RCPTTSessionStatusChangeNotification]){
    if([notification.object isKindOfClass:[NSDictionary class]]){
      NSDictionary *dic = (NSDictionary *)notification.object;
      NSString *action = dic[@"action"];
      RCPTTSession *session = dic[@"session"];
      if(!(session.conversationType == self.conversationType && [session.targetId isEqualToString:self.targetId])){
        return;
      }
      if([action isEqualToString:@"start"]){
        [self.extensionView addSubview:self.pttStatusView ];
        [self.pttStatusView updatePTTStatus];
      }else if([action isEqualToString:@"end"]){
        [self.pttStatusView removeFromSuperview];
      }
    }
  }
  
}

#pragma RCExtensionModule
+ (instancetype)loadRongExtensionModule {
  return [self sharedRCPTTKitExtensionModule];
}

- (void)didConnect:(NSString *)userId {
  [RCPTT sharedRCPTT].currentUserId = userId;
}

- (NSArray<RCExtensionPluginItemInfo *> *)getPluginBoardItemInfoList:(RCConversationType)conversationType
                                                            targetId:(NSString *)targetId {
  
  self.conversationType = conversationType;
  self.targetId = targetId;
  [[RCPTT sharedRCPTT] setValue:@(conversationType) forKey:@"conversationType"];
  [[RCPTT sharedRCPTT] setValue:targetId forKey:@"targetId"];
  
  NSMutableArray *itemList = [[NSMutableArray alloc] init];
  
//  RCExtensionPluginItemInfo *pttItem = [[RCExtensionPluginItemInfo alloc] init];
//  pttItem.image = [RCPTTUtilities imageNamed:@"actionbar_location_icon" ofBundle:@"RongCloud.bundle"];
//  pttItem.title = @"对讲机";
//  __weak typeof(self) ws = self;
//  pttItem.tapBlock = ^(RCChatSessionInputBarControl *chatSessionInputBar){
//    NSLog(@"ptt taped");
//    ws.chatBarControl = chatSessionInputBar;
//    [ws presentToTalkView];
//  };
//  //pttItem.tag = PLUGIN_BOARD_ITEM_PTT_TAG;
//  [itemList addObject:pttItem];
  return [itemList copy];
}

- (void)extensionViewWillAppear:(RCConversationType)conversationType
                       targetId:(NSString *)targetId
                  extensionView:(UIView *)extensionView {
    self.extensionView = extensionView;
    [self.pttStatusView removeFromSuperview];
    [self.extensionView addSubview:self.pttStatusView];
    
    BOOL hasSession = [self checkCurrentConversationisInSession];
    if(hasSession){
        [self.pttStatusView updatePTTStatus];
    }else {
        [self.pttStatusView removeFromSuperview];
    }
}

- (void)extensionViewWillDisappear:(RCConversationType)conversationType
                          targetId:(NSString *)targetId {
    [self.pttStatusView removeFromSuperview];
    self.extensionView.frame = CGRectZero;
}

- (void)containerViewWillDestroy:(RCConversationType)conversationType
                        targetId:(NSString *)targetId {
    RCPTTSession *session = [RCPTT sharedRCPTT].currentSession;
    if(session.conversationType == conversationType && [session.targetId isEqualToString:targetId]){
        if([session.participants containsObject:[RCPTT sharedRCPTT].currentUserId]){
            [[RCPTT sharedRCPTT] leaveSession:conversationType targetId:[RCPTT sharedRCPTT].currentUserId success:nil error:nil];
        }
    }
}

- (NSArray<RCExtensionMessageCellInfo *> *)getMessageCellInfoList:(RCConversationType)conversationType
                                                         targetId:(NSString *)targetId {
  RCExtensionMessageCellInfo *beginCellInfo = [[RCExtensionMessageCellInfo alloc] init];
  beginCellInfo.messageContentClass = [RCPTTBeginMessage class];
  beginCellInfo.messageCellClass = [RCPTTBeginMessageCell class];
  RCExtensionMessageCellInfo *endCellInfo = [[RCExtensionMessageCellInfo alloc] init];
  endCellInfo.messageContentClass = [RCPTTEndMessage class];
  endCellInfo.messageCellClass = [RCPTTEndMessageCell class];
  return @[beginCellInfo, endCellInfo];
}

- (RCPTTStatusView *)pttStatusView {
  if(!_pttStatusView){
    _pttStatusView = [[RCPTTStatusView alloc]initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 40)];
    _pttStatusView.backgroundColor = [RCPTT sharedRCPTT].statusViewColor;
    _pttStatusView.delegate = self;
  }
  return _pttStatusView;
}

#pragma mark RCPTTStatusViewDelegate
- (void)onJoinPTT {
  [self presentToTalkView];
}
- (void)onShowPTTView {
  [self presentToTalkView];
}

- (RCPTTSessionStatus)getPttSessionStatus {
  RCPTT *ptt = [RCPTT sharedRCPTT];
  if([self checkCurrentConversationisInSession]){
    if(ptt.isInSession){
      return RCPTTSessionStatusExistAndJoined;
    }else {
      return RCPTTSessionStatusExistAndNotJoined;
    }
  }else {
    if(ptt.isInSession){
      return RCPTTSessionStatusNotExistAndJoined;
    }else {
      return RCPTTSessionStatusNotExistAndNotJoined;
    }
  }
}

- (BOOL)checkCurrentConversationisInSession {
  RCPTT *ptt = [RCPTT sharedRCPTT];
  if(ptt.currentSession){
    return YES;
  }
  return NO;
}

//如果statusView隐藏那么extensionView的frame变为CGRectZero，否则设置有效的frame
- (void)didHidden:(BOOL)isHidden {
    isHidden = YES;
    
  BOOL hasJoined = [RCPTT sharedRCPTT].isInSession;
  CGFloat height = hasJoined?40:75;
  self.pttStatusView.frame = CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, height);
  CGRect frame = isHidden? CGRectZero : CGRectMake(0, 64, [UIScreen mainScreen].bounds.size.width, height);
  self.extensionView.frame = frame;
  if(isHidden){
    [self.pttStatusView removeFromSuperview];
  }
}

- (void)presentToTalkView {
  
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:RCPTTSessionStatusChangeNotification object:nil];
}
 
@end
