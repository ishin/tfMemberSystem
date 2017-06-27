//
//  RCVoicePlayer.m
//  RongIMKit
//
//  Created by xugang on 15/1/22.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCVoicePlayer.h"
#import <AVFoundation/AVFoundation.h>
#import <CoreAudio/CoreAudioTypes.h>
#import <UIKit/UIKit.h>
#import "RCIM.h"

static BOOL bSensorStateStart = YES;
static RCVoicePlayer *rcVoicePlayerHandler = nil;

@interface RCVoicePlayer () <AVAudioPlayerDelegate>

@property(nonatomic, strong) AVAudioPlayer *audioPlayer;
@property(nonatomic) BOOL isPlaying;
@property(nonatomic, weak) id<RCVoicePlayerObserver> voicePlayerObserver;
@property(nonatomic) RCMessageDirection messageDirection;
@property(nonatomic) NSString *playerCategory;
- (void)enableProximityMonitoring;
- (void)setDefaultAudioSession:(NSString *)category;
- (void)disableProximityMonitoring;
- (BOOL)startPlayVoice:(NSData *)data;
@end

@implementation RCVoicePlayer

+ (RCVoicePlayer *)defaultPlayer {
    @synchronized(self) {
        if (nil == rcVoicePlayerHandler) {
            rcVoicePlayerHandler = [[[self class] alloc] init];
            rcVoicePlayerHandler.playerCategory = AVAudioSessionCategoryPlayback;
        }
    }
    return rcVoicePlayerHandler;
}
- (void)setDefaultAudioSession :(NSString *)category{
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
    DebugLog(@"[RongIMKit]: [audioSession category ] %@",[audioSession category ]);
//    //默认情况下扬声器播放，如果当前audioSession状态是AVAudioSessionCategoryRecord，证明正在录音，不要设置category
//    if(![[audioSession category ] isEqualToString:AVAudioSessionCategoryRecord])
    [audioSession setCategory:category error:nil];//2016-12-05,edit by dulizhao ,设置category，在手机静音的情况下也可播放声音。
    [audioSession setActive:YES error:nil];
}

//处理监听触发事件
- (void)sensorStateChange:(NSNotification *)notification {
    if (bSensorStateStart) {
        bSensorStateStart = NO;

        dispatch_async(dispatch_get_main_queue(), ^{
            if ([[UIDevice currentDevice] proximityState] == YES) {
                self.playerCategory = AVAudioSessionCategoryPlayAndRecord;
                DebugLog(@"[RongIMKit]: Device is close to user");
                [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord error:nil];
            } else {
                DebugLog(@"[RongIMKit]: Device is not close to user");
                self.playerCategory = AVAudioSessionCategoryPlayback;
                [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
            }
            bSensorStateStart = YES;
        });
    }
}

- (BOOL)playVoice:(RCConversationType)conversationType
         targetId:(NSString *)targetId
        messageId:(long)messageId
        direction:(RCMessageDirection)messageDirection
        voiceData:(NSData *)data
         observer:(id<RCVoicePlayerObserver>)observer {
    self.voicePlayerObserver = observer;
    self.messageId = messageId;
    self.conversationType = conversationType;
    self.targetId = targetId;
    [self enableProximityMonitoring];
    [self setDefaultAudioSession:_playerCategory];
    
    self.messageDirection = messageDirection;
    return [self startPlayVoice:data];
}

//停止播放
- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    DebugLog(@"%s", __FUNCTION__);

    self.isPlaying = self.audioPlayer.playing;
    [self disableProximityMonitoring];

    // notify at the end
    if ([self.voicePlayerObserver respondsToSelector:@selector(PlayerDidFinishPlaying:)]) {
        [self.voicePlayerObserver PlayerDidFinishPlaying:flag];
    }

    // set the observer to nil
    self.voicePlayerObserver = nil;
    self.audioPlayer = nil;
    if(![RCIM sharedRCIM].isExclusiveSoundPlayer){
        [[AVAudioSession sharedInstance] setActive:NO
                                       withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
                                             error:nil];
    }else{
        AVAudioSession *audioSession = [AVAudioSession sharedInstance];
        [audioSession setCategory:AVAudioSessionCategoryAmbient error:nil];
        [audioSession setActive:YES error:nil];
    }

    
    if (self.messageDirection == MessageDirection_RECEIVE
        && [RCIMClient sharedRCIMClient].sdkRunningMode == RCSDKRunningMode_Foreground) {
            [self performSelector:@selector(sendPlayFinishNotification) withObject:nil afterDelay:0.3f];
    }
    
}

-(void)sendPlayFinishNotification{
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter]
         postNotificationName:@"kRCPlayVoiceFinishNotification"
         object:@(self.messageId)
         userInfo:@{@"conversationType":@(self.conversationType),
                    @"targetId":self.targetId}];
    });
}
//播放错误
- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError *)error {
    DebugLog(@"%s", __FUNCTION__);

    
    // do something
    self.isPlaying = self.audioPlayer.playing;
    [self disableProximityMonitoring];

    // notify at the end
    if ([self.voicePlayerObserver respondsToSelector:@selector(audioPlayerDecodeErrorDidOccur:)]) {
        [self.voicePlayerObserver audioPlayerDecodeErrorDidOccur:error];
    }
    self.voicePlayerObserver = nil;
    self.audioPlayer = nil;
    if(![RCIM sharedRCIM].isExclusiveSoundPlayer){
        [[AVAudioSession sharedInstance] setActive:NO
                                       withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
                                             error:nil];
    }else{
        AVAudioSession *audioSession = [AVAudioSession sharedInstance];
        [audioSession setCategory:AVAudioSessionCategoryAmbient error:nil];
        [audioSession setActive:YES error:nil];
    }

}

- (BOOL)startPlayVoice:(NSData *)data {
    NSError *error = nil;

    self.audioPlayer = [[AVAudioPlayer alloc] initWithData:data error:&error];
    self.audioPlayer.delegate = self;
    self.audioPlayer.volume = 1.0;

    BOOL ready = NO;
    if (!error) {

        DebugLog(@"[RongIMKit]: init AudioPlayer %@", error);

        ready = [self.audioPlayer prepareToPlay];
        DebugLog(@"[RongIMKit]: prepare audio player %@", ready ? @"success" : @"failed");
        ready = [self.audioPlayer play];
        DebugLog(@"[RongIMKit]: async play is %@", ready ? @"success" : @"failed");
    }
    self.isPlaying = self.audioPlayer.playing;
    DebugLog(@"self.isPlaying > %d", self.isPlaying);
    DebugLog(@"[RongIMKit]: [audioSession category ] %@",[[AVAudioSession sharedInstance] category ]);
    return ready;
}

- (void)stopPlayVoice {
    if (nil != self.audioPlayer && self.audioPlayer.playing) {
        [self.audioPlayer stop];
        self.audioPlayer = nil;

        [self disableProximityMonitoring];
    }
    self.messageId = -1;
    self.isPlaying = self.audioPlayer.playing;
    self.voicePlayerObserver = nil;
    if(![RCIM sharedRCIM].isExclusiveSoundPlayer){
        [[AVAudioSession sharedInstance] setActive:NO
                                       withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
                                             error:nil];
    }else{
        AVAudioSession *audioSession = [AVAudioSession sharedInstance];
        [audioSession setCategory:AVAudioSessionCategoryAmbient error:nil];
        [audioSession setActive:YES error:nil];
    }

    
    

}
- (void)enableProximityMonitoring {
    [[UIDevice currentDevice]
        setProximityMonitoringEnabled:YES]; //建议在播放之前设置yes，播放结束设置NO，这个功能是开启红外感应
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIDeviceProximityStateDidChangeNotification
                                                  object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(sensorStateChange:)
                                                 name:UIDeviceProximityStateDidChangeNotification
                                               object:nil];
}
- (void)disableProximityMonitoring {
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC));
    dispatch_after(time, dispatch_get_main_queue(), ^(void){
        if(!self.isPlaying){
            self.playerCategory = AVAudioSessionCategoryPlayback;
            [[UIDevice currentDevice] setProximityMonitoringEnabled:NO];
            [[NSNotificationCenter defaultCenter] removeObserver:self
                                                            name:UIDeviceProximityStateDidChangeNotification
                                                          object:nil];
        }
    });
 
}

@end
