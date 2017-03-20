//
//  TeamOrg.h
//  Hint
//
//  Created by jack on 1/27/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TeamOrg : NSObject
{
    int _teamId;
    
    int _teamCountOfMembs;
    
    
}

@property (nonatomic, strong) NSString *_teamName;
@property (nonatomic, strong) NSArray *_membs;
@property (nonatomic, assign) BOOL _isSelect;
@property (nonatomic, assign) int _teamId;
@property (nonatomic, assign) int _teamPId;
@property (nonatomic, assign) int _levelIndex;

- (id) initWithData:(NSDictionary*)data;

@end
