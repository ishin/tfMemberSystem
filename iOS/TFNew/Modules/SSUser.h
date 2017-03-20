//
//  SSUser.h
//  ws
//
//  Created by jack on 1/17/16.
//  Copyright (c) 2016 jack. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface SSUser : NSObject
{
    int userId;
}

@property (nonatomic, assign) int userId;
@property (nonatomic, strong) NSString *account;
@property (nonatomic, strong) NSString *fullname;
@property (nonatomic, strong) NSString *cellphone;
@property (nonatomic, strong) NSString *email;
@property (nonatomic, strong) NSString *avatarurl;
@property (nonatomic, strong) NSString *address;
@property (nonatomic, strong) NSString *companyname;
@property (nonatomic, strong) NSString *ranktitle;
@property (nonatomic, strong) NSString *telphone;

@property (nonatomic, strong) NSString *tags;

@property (nonatomic, strong) id ctime;

@property (nonatomic, assign) int familiy;

- (void) updateWithDictionary:(NSDictionary*)data;

@end
