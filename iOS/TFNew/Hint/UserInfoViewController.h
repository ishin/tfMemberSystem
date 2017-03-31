//
//  UserInfoViewController.h
//  Hint
//
//  Created by jack on 2/5/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "BaseViewController.h"

@class WSUser;
@interface UserInfoViewController : BaseViewController
{
    
}
@property (nonatomic, strong) WSUser *_user;
@property (nonatomic, strong) NSString *_userId;
@property (nonatomic, assign) BOOL _isFriend;

@end
