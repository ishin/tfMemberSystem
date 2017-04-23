//
//  TFMsgSound.m
//  Hint
//
//  Created by chen jack on 2017/4/21.
//  Copyright © 2017年 jack. All rights reserved.
//

#import "TFMsgSound.h"
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AudioToolbox/AudioToolbox.h>
@interface TFMsgSound ()
{
    SystemSoundID soundID;
    
}
@property (nonatomic,assign) BOOL isON;

@end

@implementation TFMsgSound
static TFMsgSound *_sharedInstance;
static TFMsgSound *_sharedInstanceForSound;
+(id)sharedInstanceForVibrate
{
    
    @synchronized ([TFMsgSound class]) {
        
        if (_sharedInstance == nil) {
            
            _sharedInstance = [[TFMsgSound alloc] initForPlayingVibrate];
            
        }
    }
    return _sharedInstance;
    
}
+ (id) sharedInstanceForSound
{
    @synchronized ([TFMsgSound class]) {
        
        if (_sharedInstanceForSound == nil) {
            
            _sharedInstanceForSound = [[TFMsgSound alloc] initForPlayingSystemSoundEffectWith:@"sms-received" ofType:@"caf"];
            
        }
    }
    return _sharedInstanceForSound;
}
-(id)initForPlayingVibrate
{
    self=[super init];
    
    if(self){
        
        soundID=kSystemSoundID_Vibrate;
    }
    return self;
}

-(id)initForPlayingSystemSoundEffectWith:(NSString *)resourceName ofType:(NSString *)type
{
    self=[super init];
    
    if(self){
        
        //        NSString *path=[[NSBundle bundleWithIdentifier:@"com.apple.UIKit"] pathForResource:resourceName ofType:type];
         NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"RongCloud" ofType:@"bundle"];
        NSString *path= [bundlePath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@",resourceName,type]];

        if(path){
            
            SystemSoundID theSoundID;
            
            OSStatus error =AudioServicesCreateSystemSoundID((__bridge CFURLRef)[NSURL fileURLWithPath:path],&theSoundID);
            
            if(error == kAudioServicesNoError){
                
                soundID=theSoundID;
            }else{
                
                NSLog(@"Failed to create sound");
                
            }
            
        }
        
    }
    return  self;
}
-(id)initForPlayingSoundEffectWith:(NSString *)filename
{
    self=[super init];
    if(self){
        
        NSURL *fileURL=[[NSBundle mainBundle]URLForResource:filename withExtension:nil];
        if(fileURL!=nil){
            
            SystemSoundID theSoundID;
            
            OSStatus error=AudioServicesCreateSystemSoundID((__bridge CFURLRef)fileURL, &theSoundID);
            
            if(error ==kAudioServicesNoError){
                
                soundID=theSoundID;
            }else{
                
                NSLog(@"Failed to create sound");
                
            }
        }
    }
    
    return self;
    
}
-(void)play
{
    AudioServicesPlaySystemSound(soundID);
}
-(void)cancleSound
{
    _sharedInstance=nil;
    //AudioServicesRemoveSystemSoundCompletion(soundID);
}
-(void)dealloc
{
    
    AudioServicesDisposeSystemSoundID(soundID);
}
@end
