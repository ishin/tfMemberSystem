//
//  SigninViewController.m
//  Hint
//
//  Created by jack on 8/17/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "SigninViewController.h"
#import "UIButton+Color.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "UIImage+ImageEffects.h"
#import "UILabel+ContentSize.h"
#import "ResetPwdViewController.h"
#import "WaitDialog.h"



@interface SigninViewController ()
{
    UIView *_maskView;
    
    int keyboard_space;
    
    UIButton *btnSignin;
}
@end

@implementation SigninViewController
@synthesize _backgroundImg;


- (void) backAction:(id)sender{
    
    [self.navigationController popViewControllerAnimated:YES];
    
}


- (void) viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBarHidden = YES;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.title = @"登录";
    
    
    UIImageView *bg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"sign_in.jpg"]];
    [self.view addSubview:bg];
    bg.frame = CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    //bg.userInteractionEnabled = YES;
    
    
    UIImageView *bgIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"tf_logo.png"]];
    [self.view addSubview:bgIcon];
    bgIcon.center = CGPointMake(SCREEN_WIDTH/2, 64+60);
    bgIcon.layer.cornerRadius = 8;
    bgIcon.clipsToBounds = YES;
    
    
    _maskView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    [self.view addSubview:_maskView];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(onTapSelected:)];
    [_maskView addGestureRecognizer:tap];
    
    
    UIImageView *loginPanIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"login_input_pan.png"]];
    [_maskView addSubview:loginPanIcon];
    loginPanIcon.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT*0.45);
    
    
    int h2 = CGRectGetHeight(loginPanIcon.frame)/2;
    
    float left = CGRectGetMinX(loginPanIcon.frame)+36;
    
    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_mobile.png"]];
    [_maskView addSubview:icon];
    icon.center = CGPointMake(left, CGRectGetMinY(loginPanIcon.frame)+h2/2);
    
    
    
    float realW = SCREEN_WIDTH - 2*left-10;
    
    _loginName = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(icon.frame)+10,
                                                               CGRectGetMinY(icon.frame)-8,
                                                               realW,
                                                               31)];
    _loginName.backgroundColor = [UIColor clearColor];
    _loginName.clearButtonMode = UITextFieldViewModeNever;
    [_maskView addSubview:_loginName];
    _loginName.font = [UIFont systemFontOfSize:14];
    _loginName.delegate = self;
    _loginName.returnKeyType = UIReturnKeyDone;
    _loginName.textColor = [UIColor blackColor];
    
    _loginName.attributedPlaceholder = [[NSAttributedString alloc]
                                        initWithString:@"用户名称"
                                        attributes:@{NSForegroundColorAttributeName: COLOR_TEXT_C}];
    
    int top = CGRectGetMaxY(_loginName.frame);
    
    UIButton *btnClear = [UIButton buttonWithType:UIButtonTypeCustom];
    btnClear.frame = CGRectMake(CGRectGetMaxX(_loginName.frame)-30, CGRectGetMinY(_loginName.frame)-10, 50, 50);
    [btnClear setImage:[UIImage imageNamed:@"tf_field_clear.png"] forState:UIControlStateNormal];
    [_maskView addSubview:btnClear];
    [btnClear addTarget:self action:@selector(clearLoginNameEnter:) forControlEvents:UIControlEventTouchUpInside];
    
    
    
    icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_mima.png"]];
    [_maskView addSubview:icon];
    icon.center = CGPointMake(left, CGRectGetMinY(loginPanIcon.frame)+h2+h2/2-5);
    
    
    _loginPwd = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(icon.frame)+10,
                                                              CGRectGetMinY(icon.frame)-5,
                                                              realW,
                                                              31)];
    _loginPwd.backgroundColor = [UIColor clearColor];
    _loginPwd.clearButtonMode = UITextFieldViewModeNever;
    [_maskView addSubview:_loginPwd];
    _loginPwd.font = [UIFont systemFontOfSize:14];
    _loginPwd.secureTextEntry = YES;
    _loginPwd.delegate = self;
    _loginPwd.returnKeyType = UIReturnKeyDone;
    _loginPwd.textColor = [UIColor blackColor];
    
    _loginPwd.attributedPlaceholder = [[NSAttributedString alloc]
                                       initWithString:@"输入密码"
                                       attributes:@{NSForegroundColorAttributeName: COLOR_TEXT_C}];
    
    top = CGRectGetMaxY(_loginPwd.frame);
    
    btnClear = [UIButton buttonWithType:UIButtonTypeCustom];
    btnClear.frame = CGRectMake(CGRectGetMaxX(_loginPwd.frame)-30, CGRectGetMinY(_loginPwd.frame)-10, 50, 50);
    [btnClear setImage:[UIImage imageNamed:@"tf_field_clear.png"] forState:UIControlStateNormal];
    [_maskView addSubview:btnClear];
    [btnClear addTarget:self action:@selector(clearLoginPwdEnter:) forControlEvents:UIControlEventTouchUpInside];
    
    UILabel* getPwd = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(_loginPwd.frame), CGRectGetMaxY(loginPanIcon.frame)+10,
                                                                90,
                                                                31)];
    getPwd.backgroundColor = [UIColor clearColor];
    getPwd.font = [UIFont systemFontOfSize:13];
    getPwd.textColor = COLOR_TEXT_B;
    getPwd.textAlignment = NSTextAlignmentLeft;
    getPwd.numberOfLines = 2;
    getPwd.text = @"忘记密码";
    [_maskView addSubview:getPwd];
    
    UIButton *btnPwd = [UIButton buttonWithType:UIButtonTypeCustom];
    btnPwd.frame = getPwd.frame;
    [_maskView addSubview:btnPwd];
    [btnPwd addTarget:self action:@selector(getPassword:) forControlEvents:UIControlEventTouchUpInside];

    icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_getpwd.png"]];
    [_maskView addSubview:icon];
    icon.center = CGPointMake(left, getPwd.center.y);
    
    
    
    
    btnSignin = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSignin.frame = CGRectMake((SCREEN_WIDTH-242)/2, CGRectGetMaxY(btnPwd.frame)+25, 242, 52);
    [_maskView addSubview:btnSignin];
    [btnSignin setImage:[UIImage imageNamed:@"login_btn_normal.png"] forState:UIControlStateNormal];
    [btnSignin setImage:[UIImage imageNamed:@"login_btn_down.png"] forState:UIControlStateHighlighted];
    
    [btnSignin setTitle:@"登录" forState:UIControlStateNormal];
    btnSignin.titleLabel.font = [UIFont systemFontOfSize:16];
    [btnSignin addTarget:self action:@selector(loginAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSignin setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    
    keyboard_space = 256 - (SCREEN_HEIGHT - CGRectGetMaxY(btnSignin.frame));
    
    

    User *u = [UserDefaultsKV getUser];
    if(u)
    {
        _loginName.text = [UserDefaultsKV getAccount];
        _loginPwd.text = [UserDefaultsKV getUserPwd];
    }
    else
    {
        _loginName.text = [UserDefaultsKV getAccount];
        
       
    }
    
    [self testLoginBtnEnabled];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(textFieldChanged:)
                                                 name:UITextFieldTextDidChangeNotification
                                               object:nil];
    
    
}

- (void) onTapSelected:(id)sender{
    
    if([_loginName isFirstResponder])
    {
        [_loginName resignFirstResponder];
    }
    if([_loginPwd isFirstResponder])
    {
        [_loginPwd resignFirstResponder];
    }
    
}

- (void) clearLoginNameEnter:(id)sender{
    
    _loginName.text = @"";
}

- (void) clearLoginPwdEnter:(id)sender{
    
    _loginPwd.text = @"";
    
}


- (void) didRegSuccess:(NSNotification*) notify{
    
    _loginName.text = [UserDefaultsKV getRegPhone];
    _loginPwd.text = @"";
}

- (void) textFieldChanged:(NSNotification *)notify{
    
    [self testLoginBtnEnabled];

}


- (void) testLoginBtnEnabled{
    
    if([_loginPwd.text length] && [_loginName.text length])
    {
        btnSignin.enabled = YES;
    }
    else
    {
        btnSignin.enabled = NO;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    
    if(keyboard_space > 0)
    {
        [UIView beginAnimations:nil context:nil];
        
        _maskView.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT/2 - keyboard_space);
        
        [UIView commitAnimations];
    }
    
    
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    
    [UIView beginAnimations:nil context:nil];
    
    _maskView.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
    
    [UIView commitAnimations];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{

    if(textField == _loginName)
    {
        [_loginPwd becomeFirstResponder];
    }
    else if(textField == _loginPwd)
    {
        [textField resignFirstResponder];
        
        [self loginAction:btnSignin];
        
    }
    
    return YES;
}


- (void) loginAction:(UIButton*)btn{

    
    if([_loginName.text length] < 3)
    {
        UIAlertView *alert  = [[UIAlertView alloc] initWithTitle:@""
                                                         message:@"请输入正确的用户名！"
                                                        delegate:nil
                                               cancelButtonTitle:@"确定"
                                               otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    NSString *pwd = _loginPwd.text;
    
    if([pwd length] == 0)
    {
        UIAlertView *alert  = [[UIAlertView alloc] initWithTitle:@""
                                                         message:@"请输入密码！"
                                                        delegate:nil
                                               cancelButtonTitle:@"确定"
                                               otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    if([pwd length] < 3 || [pwd length] > 32)
    {
        UIAlertView *alert  = [[UIAlertView alloc] initWithTitle:@""
                                                         message:@"请输入长度为6-32位的密码！"
                                                        delegate:nil
                                               cancelButtonTitle:@"确定"
                                               otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    if([_loginName isFirstResponder])
        [_loginName resignFirstResponder];
    if([_loginPwd isFirstResponder])
        [_loginPwd resignFirstResponder];
    
    
    if(_http == nil)
        _http = [[WebClient alloc] initWithDelegate:self];
    
    _http._httpMethod = @"GET";
    _http._method = API_LOGIN;
    
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:_loginName.text forKey:@"account"];
    [params setObject:md5Encode(_loginPwd.text) forKey:@"userpwd"];
    [params setObject:@"CN" forKey:@"countrycode"];
    _http._requestParam = params;
    
    
    IMP_BLOCK_SELF(SigninViewController);
    
    btn.enabled = NO;
    
    [[WaitDialog sharedDialog] setTitle:@"登录中..."];
    [[WaitDialog sharedDialog] startLoading];
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        btn.enabled = YES;
        
        [[WaitDialog sharedDialog] endLoading];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 1)
                {
                    
                    NSDictionary *value = [v objectForKey:@"text"];
                    
                    User *u = [[User alloc] initWithDicionary:value];

                    [UserDefaultsKV saveUser:u];
                    [UserDefaultsKV saveMyAccount:_loginName.text];
                    [UserDefaultsKV saveUserPwd:_loginPwd.text];
                    
                    
                    //[block_self checkInfoFinishStatus:v user:u];
                    [block_self didLogin];
                }
                else
                {
                    NSString *msg = [v objectForKey:@"errorMessage"];
                    if(msg == nil)
                    {
                        NSDictionary *value = [v objectForKey:@"text"];
                        
                       msg = [value objectForKey:@"context"];
                        
                    }
                    
                    
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                                    message:msg
                                                                   delegate:nil
                                                          cancelButtonTitle:@"OK"
                                                          otherButtonTitles:nil, nil];
                    [alert show];
                
                }
                return;
            }
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            NSLog(@"OOPS: %@", err);
            
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];
        
        
    } FailBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        btn.enabled = YES;
        
        [[WaitDialog sharedDialog] endLoading];
    }];
    
}



- (void) didLogin
{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app didLogin];
    
}



- (void) getPassword:(id)sedner{
    
    ResetPwdViewController *ctrl = [[ResetPwdViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void) dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
