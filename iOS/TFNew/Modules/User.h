//
//  User.h
//  hkeeping
//
//  Created by jack on 3/3/14.
//  Copyright (c) 2015 G-Wearable Inc. All rights reserved..
//

#import <Foundation/Foundation.h>


@interface User : NSObject

@property (nonatomic, strong) NSString* _userId;

@property (nonatomic, strong) NSString *_account;

///Token
@property (nonatomic, strong) NSString *_authtoken;

///名字
@property (nonatomic, strong) NSString *_userName;

///邮箱
@property (nonatomic, strong) NSString *_email;


@property (nonatomic, strong) NSString *_avatar;

@property (nonatomic, strong) NSString *_source;

@property (nonatomic, strong) NSString *_cellphone;


@property (nonatomic, strong) NSString *address;
@property (nonatomic, strong) NSString *companyname;
@property (nonatomic, strong) NSString *ranktitle;
@property (nonatomic, strong) NSString *telephone;

@property (nonatomic, strong) NSString *_qr;

@property (nonatomic, strong) id _ctime;

@property (nonatomic, strong) id gender;

- (id) initWithDicionary:(NSDictionary*)dic;

- (void) updateUserInfo:(NSDictionary*)dic;

@end
