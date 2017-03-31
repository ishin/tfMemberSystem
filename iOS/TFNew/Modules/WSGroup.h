//
//  WSUser.h
//  ws
//
//  Created by jack on 1/17/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WSUser.h"


@interface WSGroup : NSObject
{
  
}

@property (nonatomic, strong) NSString *groupName;
@property (nonatomic, strong) NSString *groupId;
@property (nonatomic, strong) NSString *groupCode;
@property (nonatomic, strong) NSString *createDate;
@property (nonatomic, strong) WSUser *creator;
@property (nonatomic, strong) NSMutableArray *_membs;


- (id) initWithDictionary:(NSDictionary*)data;

@end
