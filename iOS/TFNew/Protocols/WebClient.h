//
//  WebClient.h
//  HomeSearch
//
//  Created by Jack chen on 12/25/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "iHttpAdmin.h"
#import "Configure.h"

////
@protocol WebClientDelegate;


@interface WebClient : NSObject<iHttpAdminDelegate> {

	id				delegate;

	NSDictionary	*apiInfo_;

	iHttpAdmin		*_httpAdmin;
	
    RequestBlock    _successBlock;
    RequestBlock    _failBlock;
    RequestBlock    _progressBlock;
  
}

@property (nonatomic, strong) NSDictionary *_requestParam;
@property (nonatomic, strong) NSString *_method;
@property (nonatomic, strong) NSString *statues_code;

///GET or POST
@property (nonatomic, strong) NSString *_httpMethod;

@property (nonatomic, copy) RequestBlock _successBlock;
@property (nonatomic, copy) RequestBlock _failBlock;
@property (nonatomic, copy) RequestBlock _progressBlock;

- (id)initWithDelegate:(id)aDelegate;

- (void)requestWithJSONBodySusessBlock:(RequestBlock) susessBlock FailBlock:(RequestBlock)failBlock;
- (void)requestWithSusessBlock:(RequestBlock) susessBlock FailBlock:(RequestBlock)failBlock;
- (void)requestWithSusessBlockWithImage:(RequestBlock)susessBlock FailBlock:(RequestBlock)failBlock;
- (void)postImageWithInfo:(NSDictionary*)info;

- (void)postImageToBCRService:(NSDictionary*)info photo:(UIImage *)photo;


@end


