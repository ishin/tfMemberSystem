//
//  ResetPwdViewController.m
//  Hint
//
//  Created by jack on 2/1/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "ResetPwdViewController.h"
#import "UIButton+Color.h"
#import "SBJson4.h"
#import "WaitDialog.h"
#import "ResetPwdStep2ViewController.h"

@interface ResetPwdViewController () <UITextFieldDelegate>
{
    
    UIButton *_btnPhoneNext;
    
    UITextField *_mobile;
    
    UILabel     *_countryName;
    UILabel     *_countryTelcode;
    
    UIScrollView *_content;
    
}
@property (nonatomic, strong) NSString *_countyCode;
@end

@implementation ResetPwdViewController
@synthesize _countyCode;

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
    
    
    UIImageView *loginPanIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"login_input_pan.png"]];
    [_content addSubview:loginPanIcon];
    loginPanIcon.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT*0.28);
    
    
    int h2 = CGRectGetHeight(loginPanIcon.frame)/2;
    
    float left = CGRectGetMinX(loginPanIcon.frame)+36;
    
    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_mobile2.png"]];
    [_content addSubview:icon];
    icon.center = CGPointMake(left, CGRectGetMinY(loginPanIcon.frame)+h2/2);
    
    UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(icon.frame)+10,
                                                            CGRectGetMinY(icon.frame), 80, 20)];
    tL.text = @"国家/地区";
    tL.font = [UIFont systemFontOfSize:14];
    [_content addSubview:tL];

    float realW = SCREEN_WIDTH - 2*left-10;
    
    
    _countryName = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tL.frame), CGRectGetMinY(icon.frame),
                                                             SCREEN_WIDTH - left - CGRectGetMaxX(tL.frame) - 10, 20)];
    _countryName.text = @"中国";
    _countryName.font = [UIFont systemFontOfSize:14];
    _countryName.textAlignment = NSTextAlignmentRight;
    [_content addSubview:_countryName];
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(left, CGRectGetMidY(loginPanIcon.frame), realW, 1)];
    line.backgroundColor = LINE_COLOR;
    [_content addSubview:line];
    
    
    _countryTelcode = [[UILabel alloc] initWithFrame:CGRectMake(left, CGRectGetMaxY(line.frame),
                                                             80, 20)];
    _countryTelcode.text = @"+86";
    _countryTelcode.font = [UIFont systemFontOfSize:14];
    [_content addSubview:_countryTelcode];
    _countryTelcode.center = CGPointMake(_countryTelcode.center.x, CGRectGetMinY(loginPanIcon.frame)+h2+h2/2-5);
    
    _mobile = [[UITextField alloc] initWithFrame:CGRectMake(left+40,
                                                            CGRectGetMinY(_countryTelcode.frame)-6,
                                                            realW-80,
                                                            31)];
    _mobile.backgroundColor = [UIColor clearColor];
    _mobile.clearButtonMode = UITextFieldViewModeNever;
    [_content addSubview:_mobile];
    _mobile.font = [UIFont systemFontOfSize:14];
    _mobile.delegate = self;
    _mobile.returnKeyType = UIReturnKeyDone;
    _mobile.textColor = [UIColor blackColor];
    _mobile.keyboardType = UIKeyboardTypePhonePad;
    _mobile.attributedPlaceholder = [[NSAttributedString alloc]
                                     initWithString:@"输入注册时的手机号"
                                     attributes:@{NSForegroundColorAttributeName: COLOR_TEXT_C}];
    
    
    
    UIButton *btnClear = [UIButton buttonWithType:UIButtonTypeCustom];
    btnClear.frame = CGRectMake(CGRectGetMaxX(_mobile.frame), CGRectGetMinY(_mobile.frame)-10, 50, 50);
    [btnClear setImage:[UIImage imageNamed:@"tf_field_clear.png"] forState:UIControlStateNormal];
    [self.view addSubview:btnClear];
    [btnClear addTarget:self action:@selector(clearLoginNameEnter:) forControlEvents:UIControlEventTouchUpInside];

    
    UIButton *btnSignin = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSignin.frame = CGRectMake((SCREEN_WIDTH-242)/2, SCREEN_HEIGHT*0.5, 242, 52);
    [self.view addSubview:btnSignin];
    [btnSignin setImage:[UIImage imageNamed:@"btn_verifycode.png"] forState:UIControlStateNormal];
    //[btnSignin setImage:[UIImage imageNamed:@"login_btn_down.png"] forState:UIControlStateHighlighted];
    
    [btnSignin setTitle:@"获取验证码" forState:UIControlStateNormal];
    btnSignin.titleLabel.font = [UIFont systemFontOfSize:16];
    [btnSignin addTarget:self action:@selector(verifyAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSignin setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];

    
    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
    backBtn.frame = CGRectMake(10, 20, 44, 44);
    [backBtn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];
    
    
    
 
}

- (void) backAction:(id)sender{
    
    [self.navigationController popViewControllerAnimated:YES];
}

- (void) clearLoginNameEnter:(id)sender{
    
    _mobile.text = @"";
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [textField resignFirstResponder];
    
    return YES;
}

- (void) verifyAction:(id)sender{
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES '\\\\d{11}'"];
    BOOL ok = [predicate evaluateWithObject:_mobile.text];
    
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
    
    
    
    IMP_BLOCK_SELF(ResetPwdViewController);
    
    if(_http == nil)
        _http = [[WebClient alloc] initWithDelegate:self];
    
    _http._httpMethod = @"GET";
    _http._method = API_GET_CODE;
    
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:_mobile.text forKey:@"phone"];
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
                    
                    [block_self nextStep];
                }
                else
                {
                    NSString *message = [v objectForKey:@"msg"];
                    
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                                    message:message
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
       
    }];
    
}


- (void) nextStep{
    
    ResetPwdStep2ViewController *step2 = [[ResetPwdStep2ViewController alloc] init];
    step2._cellphone = _mobile.text;
    [self.navigationController pushViewController:step2 animated:YES];
    
}


- (void) dealloc
{
    
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
