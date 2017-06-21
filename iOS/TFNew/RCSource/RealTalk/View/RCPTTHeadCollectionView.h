//
//  RCPTTHeadCollectionView.h
//  RongPTTKit
//
//  Created by Sin on 16/12/27.
//  Copyright © 2016年 Sin. All rights reserved.
//

#import <RongIMKit/RongIMKit.h>
#import <UIKit/UIKit.h>

#pragma mark delegate
@protocol RCPTTHeadCollectionTouchDelegate <NSObject>
- (void)onUserSelected:(RCUserInfo *)user atIndex:(NSUInteger)index;
@optional
- (BOOL)quitButtonPressed;
- (BOOL)backButtonPressed;
@end

@interface RCPTTHeadCollectionView : UIView
@property(nonatomic, strong) UIButton *quitButton;
@property(nonatomic, strong) UIButton *backButton;
@property(nonatomic, assign) RCUserAvatarStyle avatarStyle;
@property(nonatomic, weak) id<RCPTTHeadCollectionTouchDelegate> touchDelegate;

#pragma mark init
- (instancetype)initWithFrame:(CGRect)frame
                 participants:(NSArray *)userIds
                touchDelegate:touchDelegate;

- (instancetype)initWithFrame:(CGRect)frame
                 participants:(NSArray *)userIds
                touchDelegate:touchDelegate
              userAvatarStyle:(RCUserAvatarStyle)avatarStyle;

#pragma mark user source processing
- (BOOL)participantsUpdate:(NSArray <NSString *> *)userIds;
- (BOOL)participantJoin:(NSString *)userId;
- (BOOL)participantQuit:(NSString *)userId;
- (BOOL)michHolderUpdate:(NSString *)micHolder;
- (NSArray *)getParticipantsUserInfo;
- (void)updateTitleLabel:(NSString *)userId;
@end
