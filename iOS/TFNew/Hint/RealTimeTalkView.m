//
//  RealTimeTalkView.m
//  Hint
//
//  Created by jack on 1/12/17.
//  Copyright © 2017 jack. All rights reserved.
//

#import "RealTimeTalkView.h"
#import "WSUser.h"
#import <RongPTTKit/RongPTTKit.h>
//#import <RongPTTKit/RongPTTKit.h>
#import <AVFoundation/AVFoundation.h>


@interface RealTimeTalkView () <RCPTTKitDelegate>
{
    
    UIImageView *_avatar;
    
    UIImageView *_avatarSmall;
    
    UILabel* _rankL;
    UILabel* _nameL;
    
    UIButton *_callBtn;
    
    UIButton *_offBtn;
    UIButton *_mikeBtn;
    
    int _mike;
}
@property(nonatomic,strong) RCPTT *_ptt;
@property (nonatomic, strong) UIButton *_voiceBtn;

@end

@implementation RealTimeTalkView
@synthesize _targetUser;
@synthesize _ptt;
@synthesize _voiceBtn;


- (id) initWithFrame:(CGRect)frame{
    
    self = [super initWithFrame:frame];
    if (self) {
      

        self.backgroundColor = [UIColor whiteColor];
        
        UIImageView *mask = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, frame.size.height*0.6)];
        [self addSubview:mask];
        mask.image = [UIImage imageNamed:@"talk_voice_background.png"];
        //        mask.alpha = 0.8;

        _avatar = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, frame.size.height*0.6)];
        _avatar.layer.contents = kCAGravityResizeAspectFill;
        _avatar.contentMode = UIViewContentModeScaleAspectFill;
        [self addSubview:_avatar];
        _avatar.clipsToBounds = YES;
        _avatar.tag = 101010;
        

        _avatar.alpha = 0.2;
//        
        UIImageView *avatarmask = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"talk_voice_portrait.png"]];
        [self addSubview:avatarmask];
        
        int w = CGRectGetWidth(avatarmask.frame);
        
        _avatarSmall = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, w-8, w-8)];
        _avatarSmall.layer.cornerRadius = w/2-4;
        _avatarSmall.clipsToBounds = YES;
        _avatarSmall.contentMode = UIViewContentModeScaleAspectFill;
        [self addSubview:_avatarSmall];
        //_avatarSmall.layer.contents = kCAGravityResizeAspectFill;
        _avatarSmall.center = CGPointMake(SCREEN_WIDTH/2, 150);
        avatarmask.center = _avatarSmall.center;
        
        
        _rankL = [[UILabel alloc] initWithFrame:CGRectMake(10,CGRectGetMaxY(_avatarSmall.frame)+20,
                                                           SCREEN_WIDTH-20,
                                                                   20)];
        _rankL.backgroundColor = [UIColor clearColor];
        [self addSubview:_rankL];
        _rankL.font = [UIFont boldSystemFontOfSize:18];
        _rankL.textAlignment = NSTextAlignmentCenter;
        _rankL.textColor  = [UIColor whiteColor];
        
        _nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,CGRectGetMaxY(_rankL.frame)+10,
                                                           SCREEN_WIDTH-20,
                                                           20)];
        _nameL.backgroundColor = [UIColor clearColor];
        [self addSubview:_nameL];
        _nameL.font = [UIFont boldSystemFontOfSize:18];
        _nameL.textAlignment = NSTextAlignmentCenter;
        _nameL.textColor  = [UIColor whiteColor];
        
        _callBtn =  [UIButton buttonWithType:UIButtonTypeCustom];
        _callBtn.frame = CGRectMake(0, 0, 168, 168);
        [self addSubview:_callBtn];
        [_callBtn setImage:[UIImage imageNamed:@"talk_voice_normal.png"] forState:UIControlStateNormal];
        _callBtn.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT*0.8);
        [_callBtn addTarget:self action:@selector(startRCPTT) forControlEvents:UIControlEventTouchDown];
        
        self._voiceBtn  =  [UIButton buttonWithType:UIButtonTypeCustom];
        _voiceBtn.frame = CGRectMake(0, 0, 168, 168);
        [self addSubview:_voiceBtn];
        [_voiceBtn setImage:[UIImage imageNamed:@"talk_voice_normal.png"] forState:UIControlStateNormal];
        _voiceBtn.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT*0.8);
        _voiceBtn.hidden = YES;
        [_voiceBtn addTarget:self action:@selector(voiceButtonTaped) forControlEvents:UIControlEventTouchDown];
        [_voiceBtn addTarget:self action:@selector(voiceButtonTapedStop) forControlEvents:UIControlEventTouchUpInside|UIControlEventTouchDragExit];
        
        
        _offBtn =  [UIButton buttonWithType:UIButtonTypeCustom];
        _offBtn.frame = CGRectMake(0, 0, 60, 60);
        [self addSubview:_offBtn];
        [_offBtn setImage:[UIImage imageNamed:@"talk_voice_pressing.png"] forState:UIControlStateNormal];
        _offBtn.center = CGPointMake(SCREEN_WIDTH/2, CGRectGetMaxY(_avatar.frame));
        [_offBtn addTarget:self action:@selector(overAction:) forControlEvents:UIControlEventTouchUpInside];
        
        _mikeBtn =  [UIButton buttonWithType:UIButtonTypeCustom];
        _mikeBtn.frame = CGRectMake(0, 0, 60, 60);
        [self addSubview:_mikeBtn];
        [_mikeBtn setImage:[UIImage imageNamed:@"talk_voice_Private-mode.png"] forState:UIControlStateNormal];
        _mikeBtn.center = CGPointMake(SCREEN_WIDTH/4+5, CGRectGetMaxY(_avatar.frame));
        [_mikeBtn addTarget:self action:@selector(mikeAction:) forControlEvents:UIControlEventTouchUpInside];
        _mike = 0;
        
        
        if ([[[AVAudioSession sharedInstance] category] isEqualToString:AVAudioSessionCategoryPlayback])
        {
            //听筒模式
            _mike = 1;
            [_mikeBtn setImage:[UIImage imageNamed:@"talk_voice_Private-mode.png"] forState:UIControlStateNormal];
            
        }
        else
        {
            
            //扬声器播放
            [_mikeBtn setImage:[UIImage imageNamed:@"talk_voice_Hands-free.png"] forState:UIControlStateNormal];
            _mike = 0;
            
        }

    
    }
    
    return self;
}


- (void) overAction:(id)sender{
    
    [self endRCPTT];
}



- (void) endRCPTT{
    
    RCPTT *pttInstance = [RCPTT sharedRCPTT];
    if(pttInstance.isInSession && [pttInstance.lastSession isEqual:pttInstance.currentSession]){
        [pttInstance leaveSession:pttInstance.conversationType targetId:pttInstance.targetId success:^{
            NSLog(@"leave session success %s",__func__);
        } error:^{
            NSLog(@"leave session error %s",__func__);
        }];
    }
   
    
    _callBtn.hidden = NO;
    _voiceBtn.hidden = YES;
    
    [self failedCall];
}

- (void) startRCPTT{
    
    [_callBtn setImage:[UIImage imageNamed:@"talk_voice_bule-call.png"] forState:UIControlStateNormal];
    _callBtn.userInteractionEnabled = NO;
    
    
    self._ptt = [RCPTT sharedRCPTT];
    self._ptt.delegate = self;
    
    IMP_BLOCK_SELF(RealTimeTalkView);

    if(!self._ptt.isInSession && !self._ptt.lastSession){
        [self._ptt joinSession:self._ptt.conversationType targetId:self._ptt.targetId success:^(NSArray *participants) {
            NSLog(@"current participants %@",participants);
           // [ws.headCollectionView participantsUpdate:participants];
            [block_self performSelector:@selector(prepareSpeak) withObject:nil];
            
        } error:^{
            NSLog(@"join session error");
            [block_self failedCall];
        }];
    }else if (![self._ptt.lastSession isEqual:self._ptt.currentSession]){
        //[self showAlertView:@"Error" message:@"你已经加入了一个语音对讲"];
        NSLog(@"你已经加入了一个语音对讲");
        [self failedCall];
    }

}

- (void) failedCall{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
    [_callBtn setImage:[UIImage imageNamed:@"talk_voice_normal.png"] forState:UIControlStateNormal];
    _callBtn.userInteractionEnabled = YES;
        
    });

}


- (void) prepareUI{
    
    NSString *str = _targetUser.avatarurl;
    //str = [str stringByReplacingOccurrencesOfString:@"100x100.jpg" withString:@""];
    [_avatar setImageWithURL:[NSURL URLWithString:str]
            placeholderImage:nil];
    
    [_avatarSmall setImageWithURL:[NSURL URLWithString:_targetUser.avatarurl]
                 placeholderImage:nil];
    
     _rankL.text = _targetUser.ranktitle;
    _nameL.text = _targetUser.fullname;
}

- (void) prepareSpeak{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        _callBtn.hidden = YES;
        
        _voiceBtn.hidden = NO;
        [_voiceBtn setImage:[UIImage imageNamed:@"talk_voice_green-Connect.png"] forState:UIControlStateNormal];

        
    });
    
    
}

- (void) stopSpeak{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
    [_voiceBtn setImage:[UIImage imageNamed:@"talk_voice_bule-call.png"] forState:UIControlStateNormal];
        
         });
}

- (void)voiceButtonTaped {
    
    //self.hasStartAnimation = YES;
    __weak typeof(self) weakSelf = self;
    [self._ptt startSpeak:self._ptt.conversationType targetId:self._ptt.targetId success:^(long messageId) {
       // [weakSelf performSelector:@selector(prepareSpeak) withObject:nil];
    } error:^(RCPTTErrorCode code) {
        //[self showAlertView:nil message:@"抢麦失败"];
        NSLog(@"抢麦失败");
         [weakSelf performSelector:@selector(stopSpeak) withObject:nil];
    }];
    
}

- (void)voiceButtonTapedStop {
    
    [self._ptt stopSpeak:self._ptt.conversationType targetId:self._ptt.targetId success:^{
        
    } error:^{
        
    }];
   // [self stopSpeak];
    
 }



- (void) mikeAction:(id)sender{
    
    
    if ([[[AVAudioSession sharedInstance] category] isEqualToString:AVAudioSessionCategoryPlayback])
        
    {
        
        //切换为听筒播放
        
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord error:nil];
        
        //[self showTipInfo:@"切换为听筒模式"];
        
        _mike = 0;
        [_mikeBtn setImage:[UIImage imageNamed:@"talk_voice_Hands-free.png"] forState:UIControlStateNormal];
        
    }
    
    else
        
    {
        
        //切换为扬声器播放
        
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        
        //[self showTipInfo:@"切换为扬声器模式"];
        
        _mike = 1;
        [_mikeBtn setImage:[UIImage imageNamed:@"talk_voice_Private-mode.png"] forState:UIControlStateNormal];
    }
    
    
}


- (BOOL)isInSession:(RCPTTSession *)session {
    RCPTT *ptt = [RCPTT sharedRCPTT];
    if(ptt.conversationType == session.conversationType && [ptt.targetId isEqualToString:session.targetId]){
        return YES;
    }
    return NO;
}

#pragma mark - RCPTTKitDelegate

- (void)ptt:(RCPTT *)ptt sessionDidStart:(RCPTTSession *)session {
    if([self isInSession:session]){
        NSLog(@"%s",__func__);
    }
    
}

- (void)ptt:(RCPTT *)ptt participantsDidChange:(NSArray *)userIds inPTTSession:(RCPTTSession *)session {
    if([self isInSession:session]){
        NSLog(@"participant did change %s %@",__func__,userIds);
        //__weak typeof(self) ws = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            //[ws.headCollectionView participantsUpdate:userIds];
        });
    }
    
}

- (void)ptt:(RCPTT *)ptt micHolderDidChange:(NSString *)userId inPTTSession:(RCPTTSession *)session {
    if([self isInSession:session]){
        //__weak typeof(self) ws = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            //[ws.headCollectionView updateTitleLabel:userId];
        });
        NSLog(@"mic holder changed %s %@",__func__,userId);
    }
}

- (void)ptt:(RCPTT *)ptt sessionDidTerminate:(RCPTTSession *)session {
    if([self isInSession:session]){
        NSLog(@"对讲结束");
    }
}

- (void)ptt:(RCPTT *)ptt speakTimeDidExpire:(RCPTTSession *)session {
    if([self isInSession:session]){
        NSLog(@"对讲超时");
    }
}

@end
