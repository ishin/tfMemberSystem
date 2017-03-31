//
//  AudioPlayer.h
//  CakeLove
//
//  Created by radar on 10-2-26.
//  Copyright 2010 RED/SAFI. All rights reserved.
//


#import <Foundation/Foundation.h>
#import <AudioToolbox/AudioToolbox.h>
#import <AVFoundation/AVFoundation.h>

@protocol AudioPlayerDelegate

@optional
- (void) didFinishPlaying;

@end



@interface AudioPlayer : NSObject <AVAudioPlayerDelegate> {

	//id delegate_;
	
	AVAudioPlayer *_player;
	BOOL _bLoop;
}
@property (nonatomic, weak) id delegate_;
+ (AudioPlayer *)sharedAudioPlayer;


#pragma mark -
-(void)PlayAudio:(NSString*)audio withType:(NSString*)type withLoop:(BOOL)bLoop;
-(void)StopAudioPlayer;

@end
