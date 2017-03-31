//
//  NetworkChecker.m
//
//  Created by Radar on 10-6-9.
//  Copyright 2010 Radar. All rights reserved.
//

#import "NetworkChecker.h"


static NetworkChecker * _sharedNetworkChecker;


@implementation NetworkChecker

@synthesize internetReach;


+(NetworkChecker*)sharedNetworkChecker
{
	if (!_sharedNetworkChecker) {
		_sharedNetworkChecker = [[NetworkChecker alloc] init];
	}
	return _sharedNetworkChecker;
}


-(void)dealloc{
	//[internetReach release];
	//[_sharedNetworkChecker release];
	[[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
	//[super dealloc];
}




#pragma mark -
#pragma mark out use functions
-(NetworkStatus)networkStatus
{
	if (self.internetReach == nil)
	{
		[[NSNotificationCenter defaultCenter] addObserver: self selector: @selector(reachabilityChanged:) name: kReachabilityChangedNotification object: nil];
		
		self.internetReach = [Reachability reachabilityForInternetConnection];
		[self.internetReach startNotifer];
		
		_networkStatus = [self.internetReach currentReachabilityStatus];
	}
	
	return _networkStatus;
}




#pragma mark -
#pragma mark notification functions
//Reachability
-(void)reachabilityChanged: (NSNotification* )note
{
	Reachability* curReach = [note object];
	NSParameterAssert([curReach isKindOfClass: [Reachability class]]);
	_networkStatus = [curReach currentReachabilityStatus];
}




@end
