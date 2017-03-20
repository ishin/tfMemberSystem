//
//  TeamOrg.m
//  Hint
//
//  Created by jack on 1/27/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import "TeamOrg.h"

@implementation TeamOrg

@synthesize _teamName;
@synthesize _membs;
@synthesize _isSelect;
@synthesize _teamId;
@synthesize _teamPId;
@synthesize _levelIndex;


- (id) initWithData:(NSDictionary*)data{
    
    if(self = [super init])
    {
        
        _teamCountOfMembs = 20;
        
        
    }
    
    return self;
}

@end
