//
//  RCSystemSoundPlayer.m
//  RongIMKit
//
//  Created by xugang on 15/1/22.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCSystemSoundPlayer.h"
#import <AVFoundation/AVFoundation.h>
#import <CoreAudio/CoreAudioTypes.h>
#import <AudioToolbox/AudioToolbox.h>
#import "RCVoicePlayer.h"
#import "RongExtensionKit.h"
#import "RCIM.h"
#import "RCKitCommonDefine.h"

#define kPlayDuration 0.9

static RCSystemSoundPlayer *rcSystemSoundPlayerHandler = nil;

@interface RCSystemSoundPlayer ()

@property(nonatomic, assign) SystemSoundID soundId;
@property(nonatomic, strong) NSString *soundFilePath;

@property(nonatomic, strong)NSString *targetId;
@property(nonatomic, assign)RCConversationType conversationType;
@property(atomic)BOOL isPlaying;
@end

@implementation RCSystemSoundPlayer

+ (RCSystemSoundPlayer *)defaultPlayer {

    @synchronized(self) {
        if (nil == rcSystemSoundPlayerHandler) {
            rcSystemSoundPlayerHandler = [[[self class] alloc] init];
            rcSystemSoundPlayerHandler.isPlaying = NO;
        }
    }

    return rcSystemSoundPlayerHandler;
}

- (void)setIgnoreConversationType:(RCConversationType)conversationType targetId:(NSString *)targetId {
    self.conversationType = conversationType;
    self.targetId = targetId;
}
- (void)resetIgnoreConversation {
    self.targetId = nil;
}

- (void)setSystemSoundPath:(NSString *)path {
    if (nil == path) {
        return;
    }

    _soundFilePath = path;
}
- (void)playSoundByMessage:(RCMessage *)rcMessage {
    if (rcMessage.conversationType == self.conversationType && [rcMessage.targetId isEqualToString:self.targetId]) {
        return;
    }

    [self needPlaySoundByMessage:rcMessage];
}
- (void)needPlaySoundByMessage:(RCMessage *)rcMessage {
    if (RCSDKRunningMode_Background == [RCIMClient sharedRCIMClient].sdkRunningMode) {
        return;
    }
    //如果来信消息时正在播放或录制语音消息
    if([RCVoicePlayer defaultPlayer].isPlaying || [RCVoiceRecorder defaultVoiceRecorder].isRecording){
        return;
    }
    
    if (self.isPlaying) {
        return;
    }
    
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];

    NSError *err = nil;
    [audioSession setCategory:AVAudioSessionCategoryPlayAndRecord  error:&err];

#if __IPHONE_OS_VERSION_MAX_ALLOWED < __IPHONE_7_0
    //是否扬声器播放
    UInt32 audioRouteOverride = kAudioSessionOverrideAudioRoute_Speaker;
    AudioSessionSetProperty(kAudioSessionProperty_OverrideAudioRoute, sizeof(audioRouteOverride), &audioRouteOverride);
#else
    [audioSession overrideOutputAudioPort:AVAudioSessionPortOverrideSpeaker error:nil];
#endif

    [audioSession setActive:YES error:&err];

    if (nil != err) {
        DebugLog(@"[RongIMKit]: Exception is thrown when setting audio session");
        return;
    }
    if (nil == _soundFilePath) {
        // no redefined path, use the default
        _soundFilePath = [[[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"RongCloud.bundle"]
            stringByAppendingPathComponent:@"sms-received.caf"];
    }

    if (nil != _soundFilePath) {
        OSStatus error =
            AudioServicesCreateSystemSoundID((__bridge CFURLRef)[NSURL fileURLWithPath:_soundFilePath], &_soundId);
        if (error != kAudioServicesNoError) { //获取的声音的时候，出现错误
            DebugLog(@"[RongIMKit]: Exception is thrown when creating system sound ID");
            return;
        }
        
        self.isPlaying = YES;
        if (RC_IOS_SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"9.0")) {
            AudioServicesPlaySystemSoundWithCompletion(_soundId, ^{
                self.isPlaying = NO;
            });
        } else {
            AudioServicesPlaySystemSound(_soundId);
            AudioServicesAddSystemSoundCompletion (_soundId, NULL, NULL, playSoundEnd, NULL);
        }
    } else {
        DebugLog(@"[RongIMKit]: Not found the related sound resource file in RongCloud.bundle");
    }
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

static void playSoundEnd(SystemSoundID mySSID, void *myself) {
    AudioServicesRemoveSystemSoundCompletion (mySSID);
    AudioServicesDisposeSystemSoundID(mySSID);
    
//    CFRelease(myself);
    [RCSystemSoundPlayer defaultPlayer].isPlaying = NO;
}

@end
