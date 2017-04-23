//
//  RCPTT.m
//  RongPTTKit
//
//  Created by Sin on 16/12/29.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCPTT.h"

static NSString *RCPTTSessionStatusChangeNotification = @"RCPTTSessionStatusChangeNotification";

@interface RCPTT ()<RCPTTLibDelegate>
@property (nonatomic,assign)BOOL isInSession;

@property (nonatomic,strong) RCPTTSession *lastSession;
@end

@implementation RCPTT
+ (instancetype)sharedRCPTT {
  static RCPTT *ptt = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    ptt = [[self alloc] init];
    [ptt rcinit];
  });
  return ptt;
}

- (void)rcinit {
  self.statusViewColor = [UIColor colorWithRed:135/255.0 green:206/255.0 blue:235/255.0 alpha:1];
  self.headerViewColor = [UIColor colorWithRed:0.5 green:0.5 blue:0.5 alpha:0.5];
  self.isInSession = NO;
  [[RCPTTClient sharedPTTClient] setDelegate:self];
}

- (RCPTTSession *)currentSession {
  return [[RCPTTClient sharedPTTClient] getSession:self.conversationType targetId:self.targetId];
}

#pragma mark --
- (void)joinSession:(RCConversationType)conversationType
           targetId:(NSString *)targetId
            success:(void (^)(NSArray *participants))successBlock
              error:(void (^)())errorBlock {
  __weak typeof(self) ws = self;
  [[RCPTTClient sharedPTTClient] joinSession:conversationType targetId:targetId success:^(NSArray *participants) {
    ws.isInSession = YES;
    ws.lastSession = ws.currentSession;
    successBlock(participants);
  } error:^(RCPTTErrorCode code) {
    ws.isInSession = NO;
    errorBlock();
  }];
}

- (void)leaveSession:(RCConversationType)conversationType
            targetId:(NSString *)targetId
             success:(void (^)())successBlock
               error:(void (^)())errorBlock {
  __weak typeof(self) ws = self;
  self.isInSession = NO;//当离开session的时候，立即将isInSession设置为no，保证能立马更新UI
  self.lastSession = nil;
  [[RCPTTClient sharedPTTClient] leaveSession:conversationType targetId:targetId success:^{
    ws.isInSession = NO;
    successBlock();
  } error:^(RCPTTErrorCode code){
    //TODO
//    ws.isInSession = YES; ? 是否在失败的时候重新设置为yes？
    errorBlock();
  }];
}
  

- (void)startSpeak:(RCConversationType)conversationType
          targetId:(NSString *)targetId
           success:(void (^)(long))successBlock
             error:(void (^)(RCPTTErrorCode))errorBlock {
  [[RCPTTClient sharedPTTClient] startSpeak:conversationType targetId:targetId success:successBlock error:errorBlock];
}

- (void)stopSpeak:(RCConversationType)conversationType
         targetId:(NSString *)targetId
          success:(void (^)())successBlock
            error:(void (^)())errorBlock {
  [[RCPTTClient sharedPTTClient] stopSpeak:conversationType targetId:targetId success:successBlock error:errorBlock];
}

#pragma mark -- delegate
- (void)setDelegate:(id<RCPTTKitDelegate>)delegate {
  _delegate = delegate;
}

- (void)sessionDidStart:(RCPTTSession *)session {
  [self postNotificationWithStatus:NO session:session];
  if(self.delegate && [self.delegate respondsToSelector:@selector(ptt:sessionDidStart:)]){
    [self.delegate ptt:self sessionDidStart:session];
  }
}

- (void)participantsDidChange:(NSArray *)userIds inPTTSession:(RCPTTSession *)session {
  if(self.delegate && [self.delegate respondsToSelector:@selector(ptt:participantsDidChange:inPTTSession:)]){
    [self.delegate ptt:self participantsDidChange:userIds inPTTSession:session];
  }
}

- (void)micHolderDidChange:(NSString *)userId inPTTSession:(RCPTTSession *)session {
  if(self.delegate && [self.delegate respondsToSelector:@selector(ptt:micHolderDidChange:inPTTSession:)]){
    [self.delegate ptt:self micHolderDidChange:userId inPTTSession:session];
  }
}

- (void)sessionDidTerminate:(RCPTTSession *)session {
  [self postNotificationWithStatus:YES session:session];
  if(self.delegate && [self.delegate respondsToSelector:@selector(ptt:sessionDidTerminate:)]){
    [self.delegate ptt:self sessionDidTerminate:session];
  }
}

- (void)speakTimeDidExpire:(RCPTTSession *)session {
  if(self.delegate && [self.delegate respondsToSelector:@selector(ptt:speakTimeDidExpire:)]){
    [self.delegate ptt:self speakTimeDidExpire:session];
  }
}

- (void)postNotificationWithStatus:(BOOL)isEnd session:(RCPTTSession *)session{
  dispatch_async(dispatch_get_main_queue(), ^{
    NSDictionary *objDic = @{@"action":isEnd?@"end":@"start",@"session":session};
    [[NSNotificationCenter defaultCenter] postNotificationName:RCPTTSessionStatusChangeNotification object:objDic];
  });
}

@end
