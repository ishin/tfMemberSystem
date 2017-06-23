//
//  RCMessageModelCell.m
//  RongIMKit
//
//  Created by xugang on 15/2/2.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCMessageTemplateCell.h"
#import "RCKitUtility.h"
#import "RCUserInfoLoader.h"

/**
 *  开发者实现自定义消息使用
 */
@interface RCMessageTemplateCell () <RCUserInfoLoaderObserver>

@property (nonatomic, strong) NSArray * H_layoutConstrait_userProtraitImageView;
@property (nonatomic, strong) NSArray * V_layoutConstrait_userProtraitImageView;


@end

@implementation RCMessageTemplateCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.userPortraitImageView = [[RCloudImageView alloc]initWithPlaceholderImage:[RCKitUtility imageNamed:@"default_portrait" ofBundle:@"RongCloud.bundle"]];
    }
    return self;
}
-(void)setPortraitStyle:(RCUserAvatarStyle)portraitStyle
{
    _portraitStyle = portraitStyle;
    
    if (_portraitStyle == RCUserAvatarRectangle) {
        self.userPortraitImageView.layer.cornerRadius = 4;
    }
    if (_portraitStyle == RCUserAvatarCycle) {
        self.userPortraitImageView.layer.cornerRadius = 22.5f;
    }
    self.userPortraitImageView.layer.masksToBounds = YES;
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    DebugLog(@"%s", __FUNCTION__);
}
//- (void)applyLayoutAttributes:(UICollectionViewLayoutAttributes *)layoutAttributes
//{
//    [super applyLayoutAttributes:layoutAttributes];
//    DebugLog(@"%s", __FUNCTION__);
//    //这句基类已经实现了
//    //self.customLayoutAttributes = (RCMessageCollectionViewLayoutAttributes *)layoutAttributes;
//    
//}
- (void)setDataModel:(RCMessageModel *)model
{
    [super setDataModel:model];
    
    //如果是客服，跟换默认头像
    if (ConversationType_CUSTOMERSERVICE == model.conversationType) {
        [self.userPortraitImageView setPlaceholderImage:[RCKitUtility imageNamed:@"portrait_kefu" ofBundle:@"RongCloud.bundle"]];
    }
    
    [[RCUserInfoLoader sharedUserInfoLoader]removeObserver:self ];
    RCUserInfo *userInfo = [[RCUserInfoLoader sharedUserInfoLoader]loadUserInfo:model.targetId observer:self];
    if (userInfo) {
        model.userInfo = userInfo;
        [self.userPortraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
    }
    [self setTemplateAutoLayout];
}

- (void)setTemplateAutoLayout
{
  
    
    if (self.H_layoutConstrait_userProtraitImageView ||
        self.V_layoutConstrait_userProtraitImageView)
    {
        return;
    }
    
    NSDictionary *bindingViews = NSDictionaryOfVariableBindings(_userPortraitImageView);
    
    //receiver
    if (MessageDirection_RECEIVE == self.messageDirection) {
        
        self.H_layoutConstrait_userProtraitImageView = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[_userPortraitImageView(==width)]"
                                                                                           options:0
                                                                                           metrics:@{@"width":@(self.customLayoutAttributes.portraitImageViewSize.width)}
                                                                                             views:bindingViews];
        self.V_layoutConstrait_userProtraitImageView = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|-25-[_userPortraitImageView(>=height)]"
                                                                                           options:0
                                                                                           metrics:@{@"height":@(self.customLayoutAttributes.portraitImageViewSize.height)}
                                                                                             views:bindingViews];
    }else{
        self.H_layoutConstrait_userProtraitImageView = [NSLayoutConstraint constraintsWithVisualFormat:@"H:[_userPortraitImageView(==width)]-10-|"
                                                                                           options:0
                                                                                           metrics:@{@"width":@(self.customLayoutAttributes.portraitImageViewSize.width)}
                                                                                             views:bindingViews];
        self.V_layoutConstrait_userProtraitImageView = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|-25-[_userPortraitImageView(>=height)]"
                                                                                           options:0
                                                                                           metrics:@{@"height":@(self.customLayoutAttributes.portraitImageViewSize.height)}
                                                                                             views:bindingViews];
    }
}

#pragma mark <RCUserInfoLoaderObserver>
- (void)userInfoDidLoad:(NSNotification *)notification
{
    __weak typeof(&*self) __blockSelf = self;
    
    RCUserInfo *userInfo = notification.object;
    
    if (userInfo) {
        self.model.userInfo = userInfo;
        dispatch_async(dispatch_get_main_queue(), ^{
            [__blockSelf.userPortraitImageView setImageURL:[NSURL URLWithString:userInfo.portraitUri]];
        });
    }
}

-(void)userInfoFailToLoad:(NSNotification *)notification
{
    DebugLog(@"[RongIMKit]: %s", __FUNCTION__);
}

@end
