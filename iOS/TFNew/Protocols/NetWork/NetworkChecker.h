//
//  NetworkChecker.h
//
//  Created by Radar on 10-6-9.
//  Copyright 2010 Radar. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Reachability.h"


@interface NetworkChecker : NSObject {

	//Reachability *internetReach;
	NetworkStatus _networkStatus;
}

@property (nonatomic, strong) Reachability *internetReach;

+(NetworkChecker*)sharedNetworkChecker;


#pragma mark -
#pragma mark out use functions
-(NetworkStatus)networkStatus;


@end
