//
//  EditPasswordViewController.m
//  Hint
//
//  Created by jack on 9/9/16.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "EditPasswordViewController.h"
#import "UIButton+Color.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "WaitDialog.h"

@interface EditPasswordViewController () <UITextFieldDelegate>
{
    UITextField *_oldPassword;
    UITextField *_password;
    UITextField *_password1;
    
    UIButton *btnSignin;
}
@end

@implementation EditPasswordViewController
@synthesize _u;

- (void) dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    self.title = @"重置密码";
    
    UIView *wbg = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 180)];
    wbg.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:wbg];
    
    UIImageView* tL = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_password2.png"]];
    [self.view addSubview:tL];
    tL.center = CGPointMake(25, 30);
    
    _oldPassword = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tL.frame)+10,
                                                              15,
                                                              SCREEN_WIDTH-90,
                                                              31)];
    _oldPassword.backgroundColor = [UIColor clearColor];
    _oldPassword.font = [UIFont systemFontOfSize:16];
    _oldPassword.placeholder = @"请输入当前密码";
    _oldPassword.textAlignment = NSTextAlignmentLeft;
    _oldPassword.textColor = COLOR_TEXT_A;
    [self.view addSubview:_oldPassword];
    _oldPassword.secureTextEntry = YES;
    _oldPassword.returnKeyType = UIReturnKeyDone;
    _oldPassword.delegate = self;
    
   
    tL = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_mima.png"]];
    [self.view addSubview:tL];
    tL.center = CGPointMake(25, 60+30);
    
    
    _password = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tL.frame)+10,
                                                              60+15,
                                                              SCREEN_WIDTH-90,
                                                              31)];
    _password.backgroundColor = [UIColor clearColor];
    _password.font = [UIFont systemFontOfSize:16];
    _password.placeholder = @"输入新密码（6-32位）";
    _password.textAlignment = NSTextAlignmentLeft;
    _password.textColor = COLOR_TEXT_A;
    [self.view addSubview:_password];
    _password.returnKeyType = UIReturnKeyDone;
    _password.delegate = self;
    _password.secureTextEntry = YES;
    
    
    tL = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_mima.png"]];
    [self.view addSubview:tL];
    tL.center = CGPointMake(25, 120+30);
    
    
    
    _password1 = [[UITextField alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tL.frame)+10,
                                                              120+15,
                                                              SCREEN_WIDTH-90,
                                                              31)];
    _password1.backgroundColor = [UIColor clearColor];
    _password1.font = [UIFont systemFontOfSize:16];
    _password1.placeholder = @"确认新密码（6-32位）";
    _password1.textAlignment = NSTextAlignmentLeft;
    _password1.textColor = COLOR_TEXT_A;
    [self.view addSubview:_password1];
    _password1.returnKeyType = UIReturnKeyDone;
    _password1.delegate = self;
    _password1.secureTextEntry = YES;
    
    
    
    btnSignin = [UIButton buttonWithColor:[UIColor whiteColor] selColor:LINE_COLOR];
    btnSignin.frame = CGRectMake(0, 180+40, SCREEN_WIDTH, 60);
    [self.view addSubview:btnSignin];
    btnSignin.layer.cornerRadius = 4;
    btnSignin.clipsToBounds = YES;
    [btnSignin setTitleColor:YELLOW_THEME_COLOR forState:UIControlStateNormal];
    [btnSignin setTitle:@"确认修改" forState:UIControlStateNormal];
    btnSignin.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnSignin addTarget:self action:@selector(uploadAction:) forControlEvents:UIControlEventTouchUpInside];
    
    btnSignin.enabled = NO;
    
    //btnSignin.center = CGPointMake((SCREEN_WIDTH - 140*2 - 10)/2+140/2, btnSignin.center.y);
//
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(textFieldChanged:)
                                                 name:UITextFieldTextDidChangeNotification
                                               object:nil];

    
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return YES;
}


- (void) textFieldChanged:(NSNotification *)notify{
    
    [self testLoginBtnEnabled];
    
}

- (void) testLoginBtnEnabled{
    
    if([_oldPassword.text length] && [_password.text length] && [_password1.text length])
    {
        btnSignin.enabled = YES;
    }
    else
    {
        btnSignin.enabled = NO;
    }
}

- (void) uploadAction:(UIButton*)btn{
    
    NSString *oldPwd = _oldPassword.text;
    NSString* recPwd = [UserDefaultsKV getUserPwd];
    
    if(![oldPwd isEqualToString:recPwd])
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"当前密码输入错误！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    
    NSString *pwd = _password.text;
    if([pwd length] < 3 || [pwd length] > 32)
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"请输入3-32位字母和数字组合！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    NSString *pwd1 = _password1.text;
    if([pwd1 length] < 3 || [pwd1 length] > 32)
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"请输入3-32位字母和数字组合！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    
    if(![pwd isEqualToString:pwd1])
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"两次输入的密码不一致！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    
    
    if(_http == nil)
        _http = [[WebClient alloc] initWithDelegate:self];
    
    _http._httpMethod = @"POST";
    _http._method = API_RESET_PASSWORD;
    
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    [params setObject:_u._account forKey:@"account"];
    [params setObject:@"CN" forKey:@"countrycode"];
   /// [params setObject:vcode forKey:@"textcode"];
    [params setObject:md5Encode(pwd) forKey:@"newpwd"];
    [params setObject:md5Encode(pwd) forKey:@"comparepwd"];
    
    
    _http._requestParam = params;
    
    
    IMP_BLOCK_SELF(EditPasswordViewController);
    
    btn.enabled = NO;
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        btn.enabled = YES;
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 1)
                {
                    //[block_self stopTimer];
                    
                    [[WaitDialog sharedAlertDialog] setTitle:@"密码已修改成功！"];
                    [[WaitDialog sharedAlertDialog] animateShow];
                    
                    
                    [[NSNotificationCenter defaultCenter] removeObserver:block_self];
                    
                    [block_self.navigationController popViewControllerAnimated:YES];
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
    }];
}

- (void)didReceiveMemoryWarning {
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
