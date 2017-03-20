//
//  WSUser.h
//  ws
//
//  Created by jack on 1/17/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SSUser.h"


@interface WSUser : SSUser
{
  
}

@property (nonatomic, strong) NSString *realname;
@property (nonatomic, assign) BOOL _isSelect;
@property (nonatomic, assign) double _latitude;
@property (nonatomic, assign) double _longitude;

- (id) initWithDictionary:(NSDictionary*)data;


- (void) addMyFriend:(NSString*)friendAccount;

- (void) startGpsUpdage;
- (void) stopGpsUpdate;

@end
