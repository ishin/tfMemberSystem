//
//  DataSync.h
//  Hint
//
//  Created by jack on 1/16/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import <Foundation/Foundation.h>



@interface DataSync : NSObject
{
    
}


+ (DataSync*)sharedDataSync;


- (void) syncMyContacts;
- (void) syncMyGroups;
- (void) syncTFOrgs;

@end
