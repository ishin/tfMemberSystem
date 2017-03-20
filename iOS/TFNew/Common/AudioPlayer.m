//
//  AudioPlayer.m
//  CakeLove
//
//  Created by radar on 10-2-26.
//  Copyright 2010 RED/SAFI. All rights reserved.
//

#import "AudioPlayer.h"

static AudioPlayer *_sharedAudioPlayer;


@implementation AudioPlayer
@synthesize delegate_;

+ (AudioPlayer *)sharedAudioPlayer
{
	if (!_sharedAudioPlayer) {
		_sharedAudioPlayer.delegate_ = nil;
		_sharedAudioPlayer = [[AudioPlayer alloc] init];
	}
	return _sharedAudioPlayer;
}

- (void)dealloc
{
    
}






#pragma mark -
-(void)PlayAudio:(NSString*)audio withType:(NSString*)type withLoop:(BOOL)bLoop
{
	if(_player != nil && _player.playing)
	{
		[_player stop];
	}
	
	NSURL *fileURL = [[NSURL alloc] initFileURLWithPath: [[NSBundle mainBundle] pathForResource:audio ofType:type]];
	if(fileURL == nil) return;
	
	if(_player == nil)
	{
		_player = [[AVAudioPlayer alloc] init];
	}
	
	_player = [_player initWithContentsOfURL:fileURL error:nil];
	_player.delegate = self;
	
	//[fileURL release];
	
	_bLoop = bLoop;
	[_player play];
}
-(void)StopAudioPlayer
{
	if(_player == nil) return;
	if(_player.playing)
	{
		[_player stop];
	}
}




#pragma mark -
- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag
{
	if(delegate_ && [delegate_ respondsToSelector:@selector(didFinishPlaying)]){
		[delegate_ didFinishPlaying];
	}
	if(!_bLoop) return;
	
	[NSThread sleepForTimeInterval:10.0];
	[_player play];
}




@end
