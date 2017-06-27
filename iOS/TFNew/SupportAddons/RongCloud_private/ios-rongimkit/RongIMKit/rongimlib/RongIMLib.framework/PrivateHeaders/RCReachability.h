//
//  RCReachability.h
//  RCReachability
//
//  Created by MiaoGuangfa on 12/26/14.
//  Copyright (c) 2014 MiaoGuangfa. All rights reserved.
//
#ifndef __RCReachability
#define __RCReachability
#import <Foundation/Foundation.h>
#import <SystemConfiguration/SystemConfiguration.h>
#import <netinet/in.h>
#import "RongIMClient.h"


extern NSString *const kRCReachabilityChangedNotification;

@interface RCReachability : NSObject

/*!
 * Use to check the reachability of a given host name.
 */
+ (RCReachability *)reachabilityWithHostName:(NSString *)hostName;

/*!
 * Use to check the reachability of a given IP address.
 */
+ (RCReachability *)reachabilityWithAddress:(const struct sockaddr_in *)hostAddress;

/*!
 * Checks whether the default route is available. Should be used by applications that do not connect to a particular host.
 */
+ (RCReachability *)reachabilityForInternetConnection;

/*!
 * Checks whether a local WiFi connection is available.
 */
+ (RCReachability *)reachabilityForLocalWiFi;

/*!
 * Start listening for reachability notifications on the current run loop.
 */
- (BOOL)startNotifier;
- (void)stopNotifier;

- (RCNetworkStatus)currentReachabilityStatus;

/*!
 * WWAN may be available, but not active until a connection has been established. WiFi may require a connection for VPN on Demand.
 */
- (BOOL)connectionRequired;

@end
#endif