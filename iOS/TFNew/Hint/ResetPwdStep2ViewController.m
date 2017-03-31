//
//  ResetPwdStep2ViewController.m
//  Hint
//
//  Created by jack on 2/1/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "ResetPwdStep2ViewController.h"
#import "UIButton+Color.h"
#import "SBJson4.h"
#import "WaitDialog.h"

@interface ResetPwdStep2ViewController () <UITextFieldDelegate>
{
    
    UIButton *_btnPhoneNext;
    
    UITextField *_password;
    UITextField *_password1;
    UITextField *_verifycode;
    
    UILabel     *_countryName;
    UILabel     *_countryTelcode;
    
    UIScrollView *_content;
    
    UIButton *btnVerify;
    UILabel  *btnTL;
    NSTimer  *_timer;
    int secondsCount;
    
    WebClient *_submit;
}
@property (nonatomic, strong) NSString *_countyCode;
@end

@implementation ResetPwdStep2ViewController
@synthesize _countyCode;
@synthesize _cellphone;

//- (void) viewWillAppear:(BOOL)animated
//{
//    self.navigationController.navigationBarHidden = NO;
//}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"重置密码";
    
    UIImageView *bg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"sign_in.jpg"]];
    [self.view addSubview:bg];
    bg.frame = CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
  
    
    _content = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    [self.view addSubview:_content];
    
    
    UIImageView *loginPanIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"verifycode_pan.png"]];
    [_content addSubview:loginPanIcon];
    
    
    int h2 = CGRectGetHeight(loginPanIcon.frame)/2;
   
    
    loginPanIcon.center = CGPointMake(SCREEN_WIDTH/2, h2 + 64);
    
    float left = CGRectGetMinX(loginPanIcon.frame)+36;
    
    int h3 = (CGRectGetHeight(loginPanIcon.frame)-9)/3;
    
    float realW = SCREEN_WIDTH - 2*left-10;
    
    
    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_mobile2.png"]];
    [_content addSubview:icon];
    icon.center = CGPointMake(left, CGRectGetMinY(loginPanIcon.frame)+h3/2);
    
    _verifycode = [[UITextField alloc] initWithFrame:CGRectMake(left+20,
                                                            CGRectGetMinY(icon.frame)-6,
                                                            realW-100,
                                                            31)];
    _verifycode.backgroundColor = [UIColor clearColor];
    _verifycode.clearButtonMode = UITextFieldViewModeNever;
    [_content addSubview:_verifycode];
    _verifycode.font = [UIFont systemFontOfSize:14];
    _verifycode.delegate = self;
    _verifycode.returnKeyType = UIReturnKeyDone;
    _verifycode.textColor = [UIColor blackColor];
    _verifycode.keyboardType = UIKeyboardTypePhonePad;
    _verifycode.attributedPlaceholder = [[NSAttributedString alloc]
                                     initWithString:@"您收到的验证码"
                                     attributes:@{NSForegroundColorAttributeName: COLOR_TEXT_C}];

   
    btnVerify = [UIButton buttonWithType:UIButtonTypeCustom];
    btnVerify.frame = CGRectMake(CGRectGetMaxX(_verifycode.frame)-10, CGRectGetMinY(_verifycode.frame)-1, 120, 33);
    [_content addSubview:btnVerify];
    [btnVerify addTarget:self action:@selector(verifyAction:) forControlEvents:UIControlEventTouchUpInside];
    
    btnTL = [[UILabel alloc] initWithFrame:btnVerify.bounds];
    btnTL.backgroundColor = [UIColor clearColor];
    [btnVerify addSubview:btnTL];
    btnTL.textColor = COLOR_TEXT_A;
    btnTL.font = [UIFont systemFontOfSize:14];
    btnTL.textAlignment = NSTextAlignmentCenter;
    btnTL.adjustsFontSizeToFitWidth = YES;
    btnTL.text = @"重获验证码（60）";

    

    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(left-35, h3, realW+25, 1)];
    line.backgroundColor = LINE_COLOR;
    [loginPanIcon addSubview:line];
    
    icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_password2.png"]];
    [_content addSubview:icon];
    icon.center = CGPointMake(left, CGRectGetMinY(loginPanIcon.frame)+h3+h3/2);
    
    _password = [[UITextField alloc] initWithFrame:CGRectMake(left+20,
                                                                CGRectGetMinY(icon.frame)-6,
                                                                realW-40,
                                                                31)];
    _password.backgroundColor = [UIColor clearColor];
    _password.clearButtonMode = UITextFieldViewModeNever;
    [_content addSubview:_password];
    _password.font = [UIFont systemFontOfSize:14];
    _password.delegate = self;
    _password.returnKeyType = UIReturnKeyDone;
    _password.textColor = [UIColor blackColor];
    _password.attributedPlaceholder = [[NSAttributedString alloc]
                                         initWithString:@"新密码（6-32位）"
                                         attributes:@{NSForegroundColorAttributeName: COLOR_TEXT_C}];
    
    UIButton *btnClear = [UIButton buttonWithType:UIButtonTypeCustom];
    btnClear.frame = CGRectMake(CGRectGetMaxX(_password.frame), CGRectGetMinY(_password.frame)-10, 50, 50);
    [btnClear setImage:[UIImage imageNamed:@"tf_field_clear.png"] forState:UIControlStateNormal];
    [_content addSubview:btnClear];
    [btnClear addTarget:self action:@selector(clearPasswordEnter:) forControlEvents:UIControlEventTouchUpInside];
    

    
    line = [[UILabel alloc] initWithFrame:CGRectMake(left-35, h3*2, realW+25, 1)];
    line.backgroundColor = LINE_COLOR;
    [loginPanIcon addSubview:line];
    
    icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_password2.png"]];
    [_content addSubview:icon];
    icon.center = CGPointMake(left, CGRectGetMinY(loginPanIcon.frame)+h3*2+h3/2);
    
    _password1 = [[UITextField alloc] initWithFrame:CGRectMake(left+20,
                                                              CGRectGetMinY(icon.frame)-6,
                                                              realW-40,
                                                              31)];
    _password1.backgroundColor = [UIColor clearColor];
    _password1.clearButtonMode = UITextFieldViewModeNever;
    [_content addSubview:_password1];
    _password1.font = [UIFont systemFontOfSize:14];
    _password1.delegate = self;
    _password1.returnKeyType = UIReturnKeyDone;
    _password1.textColor = [UIColor blackColor];
    _password1.attributedPlaceholder = [[NSAttributedString alloc]
                                       initWithString:@"重复新密码（6-32位）"
                                       attributes:@{NSForegroundColorAttributeName: COLOR_TEXT_C}];

    btnClear = [UIButton buttonWithType:UIButtonTypeCustom];
    btnClear.frame = CGRectMake(CGRectGetMaxX(_password1.frame), CGRectGetMinY(_password1.frame)-10, 50, 50);
    [btnClear setImage:[UIImage imageNamed:@"tf_field_clear.png"] forState:UIControlStateNormal];
    [_content addSubview:btnClear];
    [btnClear addTarget:self action:@selector(clearPassword1Enter:) forControlEvents:UIControlEventTouchUpInside];
    
    
    UIButton *btnSignin = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSignin.frame = CGRectMake((SCREEN_WIDTH-242)/2, SCREEN_HEIGHT*0.5, 242, 52);
    [self.view addSubview:btnSignin];
    [btnSignin setImage:[UIImage imageNamed:@"btn_submit.png"] forState:UIControlStateNormal];
    //[btnSignin setImage:[UIImage imageNamed:@"login_btn_down.png"] forState:UIControlStateHighlighted];
    
    [btnSignin addTarget:self action:@selector(submitAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSignin setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];

    
    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
    backBtn.frame = CGRectMake(10, 20, 44, 44);
    [backBtn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];
    
    
    btnVerify.enabled = NO;
    secondsCount = 60;
    _timer = [NSTimer scheduledTimerWithTimeInterval:1
                                              target:self
                                            selector:@selector(timeCount)
                                            userInfo:nil
                                             repeats:NO];

 
}


- (void) verifyAction:(id)sender{
    
    
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES '\\\\d{11}'"];
    BOOL ok = [predicate evaluateWithObject:_cellphone];
    
    if(!ok)
    {
        UIAlertView *alert  = [[UIAlertView alloc] initWithTitle:@""
                                                         message:@"请输入正确的手机号！"
                                                        delegate:nil
                                               cancelButtonTitle:@"确定"
                                               otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    
    if(_timer && [_timer isValid])
    {
        [_timer invalidate];
        _timer = nil;
    }
    
    btnVerify.enabled = NO;
    secondsCount = 60;
    _timer = [NSTimer scheduledTimerWithTimeInterval:1
                                              target:self
                                            selector:@selector(timeCount)
                                            userInfo:nil
                                             repeats:NO];
    
    
    if(_http == nil)
        _http = [[WebClient alloc] initWithDelegate:self];
    
    _http._httpMethod = @"GET";
    _http._method = API_GET_CODE;
    
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:_cellphone forKey:@"phone"];
    [params setObject:@"CN" forKey:@"countrycode"];
    _http._requestParam = params;

    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 1)
                {
                    
                    
                    
                }
                else
                {
                    
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
        
        [_timer invalidate];
        _timer = nil;
        
        btnVerify.enabled = YES;
        //[btnVerify setTitle:@"发送验证码" forState:UIControlStateNormal];
        btnTL.text = @"获取验证码";
    }];
    
}

- (void) showWait{
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       NSLog(@"%d", secondsCount);
                       //[btnVerify setTitle:[NSString stringWithFormat:@"%d秒后重发", secondsCount] forState:UIControlStateNormal];
                       //[btnVerify setNeedsDisplay];
                       if(secondsCount == 0)
                       {
                           btnTL.text = @"获取验证码";
                       }
                       else
                       {
                           btnTL.text = [NSString stringWithFormat:@"重获验证码（%d）", secondsCount];
                       }
                   });
    
    
}

- (void) timeCount{
    
    secondsCount--;
    
    [self performSelectorOnMainThread:@selector(showWait) withObject:nil waitUntilDone:NO];
    
    
    
    if(secondsCount <= 0)
    {
        btnVerify.enabled = YES;
        // [btnVerify setTitle:@"发送验证码" forState:UIControlStateNormal];
        btnTL.text = @"发送验证码";
        if([_timer isValid])
            [_timer invalidate];
        _timer = nil;
    }
    else
    {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1
                                                  target:self
                                                selector:@selector(timeCount)
                                                userInfo:nil
                                                 repeats:NO];
    }
}

- (void) stopTimer{
    
    if([_timer isValid])
        [_timer invalidate];
    _timer = nil;
}



- (void) backAction:(id)sender{
    
     [self stopTimer];
    
    [self.navigationController popViewControllerAnimated:YES];
    
   
}

- (void) clearPasswordEnter:(id)sender{
    
    _password.text = @"";
}
- (void) clearPassword1Enter:(id)sender{
    
    _password1.text = @"";
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [textField resignFirstResponder];
    
    return YES;
}



- (void) submitAction:(UIButton*)btn{
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES '\\\\d{11}'"];
    BOOL ok = [predicate evaluateWithObject:_cellphone];
    
    if(!ok)
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"请输入正确的手机号！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    
    NSString *vcode = _verifycode.text;
    if([vcode length] == 0)
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"请输入验证码！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    
    NSString *pwd = _password.text;
    if([pwd length] < 6 || [pwd length] > 32)
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"请输入6-32位字母和数字组合！"];
        [[WaitDialog sharedAlertDialog] animateShow];
        
        return;
    }
    NSString *pwd1 = _password1.text;
    if([pwd1 length] < 6 || [pwd1 length] > 32)
    {
        [[WaitDialog sharedAlertDialog] setTitle:@"请输入6-32位字母和数字组合！"];
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
    
    [params setObject:_cellphone forKey:@"account"];
    [params setObject:@"CN" forKey:@"countrycode"];
    [params setObject:vcode forKey:@"textcode"];
    [params setObject:md5Encode(pwd) forKey:@"newpwd"];
    [params setObject:md5Encode(pwd) forKey:@"comparepwd"];
    
    
    _http._requestParam = params;
    
    
    IMP_BLOCK_SELF(ResetPwdStep2ViewController);
    
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
                    [block_self stopTimer];
                    
                    [[WaitDialog sharedAlertDialog] setTitle:@"密码已修改成功！"];
                    [[WaitDialog sharedAlertDialog] animateShow];
                    
                    
                    [block_self.navigationController popToRootViewControllerAnimated:YES];
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


- (void) dealloc
{
    [self stopTimer];
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
