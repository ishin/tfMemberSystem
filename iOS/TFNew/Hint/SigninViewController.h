//
//  SigninViewController.h
//  Hint
//
//  Created by jack on 8/17/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "BaseViewController.h"

@interface SigninViewController : BaseViewController<UITextFieldDelegate>
{
    UITextField *_loginName;
    UITextField *_loginPwd;
    
    UIImageView *_iconName;
    UIImageView *_iconPwd;
    
    UILabel *_alert;
    
    WebClient *_httpGraph;
    
    WebClient *_httpSocialLogin;
}
@property (nonatomic, strong) UIImage *_backgroundImg;

@end
