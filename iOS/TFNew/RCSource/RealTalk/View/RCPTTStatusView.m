//
//  RCPTTStatusView.m
//  RongPTTKit
//
//  Created by Sin on 16/12/27.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCPTTStatusView.h"
#import "RCPTTCommonDefine.h"
#import "RCPTTUtilities.h"
#import "RCPTT.h"

@interface RCPTTStatusView ()
@property(nonatomic, strong) UILabel *statusLabel;
@property(nonatomic, strong) UIImageView *pttIcon;
@property(nonatomic, strong) UIImageView *moreIcon;

@property(nonatomic, strong) UILabel *expendLabel;
@property(nonatomic, strong) UIButton *cancelButton;
@property(nonatomic, strong) UIButton *joinButton;
@end

@implementation RCPTTStatusView

- (instancetype)initWithFrame:(CGRect)frame {
  self = [super initWithFrame:frame];
  if (self) {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(statusDidChange:) name:RCPTTStatusViewStatusChangeNotification object:nil];
    [self shouldHidden:YES];
    [self setup];
  }
  return self;
}

- (void)setup {
  UITapGestureRecognizer *tap =
  [[UITapGestureRecognizer alloc] initWithTarget:self
                                          action:@selector(onTaped:)];
  [self addGestureRecognizer:tap];
}

- (void)updateText:(NSString *)statusText {
  self.statusLabel.text = statusText;
}

#pragma mark - 未实现
- (void)onTaped:(id)sender {
  if(self.delegate && [self.delegate respondsToSelector:@selector(onShowPTTView)] && [RCPTT sharedRCPTT].isInSession){
    [self.delegate onShowPTTView];
  }
}

- (void)updatePTTStatus {
  switch ([self.delegate getPttSessionStatus]) {
    case RCPTTSessionStatusNotExistAndNotJoined://do nothing
      break;
    case RCPTTSessionStatusNotExistAndJoined://do nothing
      break;
    case RCPTTSessionStatusExistAndNotJoined:
      [self showExtendedView];
      [self shouldHidden:NO];
      break;
    case RCPTTSessionStatusExistAndJoined:
      [self showStatus];
      [self shouldHidden:NO];
      break;
    default:
      break;
  }
}

- (void)onCanelPressed:(id)sender {
  [self.delegate didHidden:YES];
}

- (void)onJoinPressed:(id)sender {
  [self.delegate onJoinPTT];
}

- (void)showStatus {
  for (UIView *subView in self.subviews) {
    [subView removeFromSuperview];
  }
  [self addSubview:self.statusLabel];
  [self addSubview:self.pttIcon];
  [self addSubview:self.moreIcon];
}

- (void)showExtendedView {
  for (UIView *subView in self.subviews) {
    [subView removeFromSuperview];
  }
  [self addSubview:self.expendLabel];
  [self addSubview:self.cancelButton];
  [self addSubview:self.joinButton];
}

- (UILabel *)statusLabel {
  if (!_statusLabel) {
    _statusLabel = [[UILabel alloc]
                    initWithFrame:CGRectMake(30, 0, self.frame.size.width - 60, 40)];
    _statusLabel.textAlignment = NSTextAlignmentCenter;
    _statusLabel.textColor = [UIColor whiteColor];
  }
  return _statusLabel;
}
- (UIImageView *)pttIcon {
  if (!_pttIcon) {
    _pttIcon =
    [[UIImageView alloc] initWithFrame:CGRectMake(10, 13, 10, 14)];
    [_pttIcon setImage:[RCPTTUtilities imageNamedInPTTBundle:@"white_ptt_icon"]];
  }
  return _pttIcon;
}
- (UIImageView *)moreIcon {
  if (!_moreIcon) {
    _moreIcon = [[UIImageView alloc]
                 initWithFrame:CGRectMake(self.frame.size.width - 20, 13, 10, 14)];
    [_moreIcon setImage:[RCPTTUtilities imageNamedInPTTBundle:@"ptt_arrow"]];
  }
  return _moreIcon;
}
- (UILabel *)expendLabel {
  if (!_expendLabel) {
    _expendLabel = [[UILabel alloc]
                    initWithFrame:CGRectMake(24, 0, self.frame.size.width - 48, 40)];
    _expendLabel.textAlignment = NSTextAlignmentCenter;
    _expendLabel.font = [UIFont systemFontOfSize:16.0f];
    _expendLabel.textColor = [UIColor whiteColor];
    [_expendLabel setText:@"加入可以使用对讲功能，确定加入？"];
    _expendLabel.numberOfLines = 0;
  }
  return _expendLabel;
}
- (UIButton *)cancelButton {
  if (!_cancelButton) {
    _cancelButton = [[UIButton alloc] initWithFrame:CGRectMake(79, 42, 50, 23)];
    [_cancelButton setTitle:@"取消" forState:UIControlStateNormal];
    _cancelButton.titleLabel.font = [UIFont systemFontOfSize:16.f];
    [_cancelButton
     setBackgroundImage:[RCPTTUtilities imageNamedInPTTBundle:@"ptt_share_button"]
     forState:UIControlStateNormal];
    [_cancelButton
     setBackgroundImage:[RCPTTUtilities imageNamedInPTTBundle:@"ptt_share_button_hover"]
     forState:UIControlStateHighlighted];
    [_cancelButton addTarget:self
                      action:@selector(onCanelPressed:)
            forControlEvents:UIControlEventTouchUpInside];
  }
  return _cancelButton;
}
- (UIButton *)joinButton {
  if (!_joinButton) {
    _joinButton = [[UIButton alloc]
                   initWithFrame:CGRectMake(self.frame.size.width - 50 - 79, 42, 50, 23)];
    [_joinButton setTitle:@"加入" forState:UIControlStateNormal];
    _joinButton.titleLabel.font = [UIFont systemFontOfSize:16.f];
    [_joinButton
     setBackgroundImage:[RCPTTUtilities imageNamedInPTTBundle:@"ptt_share_button"]
     forState:UIControlStateNormal];
    [_joinButton
     setBackgroundImage:[RCPTTUtilities imageNamedInPTTBundle:@"ptt_share_button_hover"]
     forState:UIControlStateHighlighted];
    [_joinButton addTarget:self
                    action:@selector(onJoinPressed:)
          forControlEvents:UIControlEventTouchUpInside];
  }
  return _joinButton;
}

- (void)statusDidChange:(NSNotification *)noti {
  //在talkVC消息的时候确定自己显示状态
  BOOL hasJoined = [RCPTT sharedRCPTT].isInSession;
  if(hasJoined){
    [self showExtendedView];
  }else {
    [self showExtendedView];
  }
  NSDictionary *statusDic = (NSDictionary *)noti.object;
  NSString *action = statusDic[@"action"];
  BOOL isHidden ;
  if([action isEqualToString:@"quit"]){
    isHidden = NO;
  }else {
    isHidden = NO;
    NSString *tips = [NSString stringWithFormat:@"正在使用对讲机功能"];
    [self updateText:tips];
  }
  [self shouldHidden:isHidden];
}

//在该方法中统一设置self的hidden并执行回调
- (void)shouldHidden:(BOOL)hidden {
  self.hidden = hidden;
  if(self.delegate &&[self.delegate respondsToSelector:@selector(didHidden:)]){
    [self.delegate didHidden:hidden];
  }
}

- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
