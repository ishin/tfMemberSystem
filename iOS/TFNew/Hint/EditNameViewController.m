//
//  EditNameViewController.m
//  Hint
//
//  Created by jack on 9/9/16.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "EditNameViewController.h"
#import "UIButton+Color.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "WaitDialog.h"

@interface EditNameViewController () <UITextFieldDelegate>
{
    UITextField *_userName;
}
@end

@implementation EditNameViewController
@synthesize _u;
@synthesize _rawData;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    if([_rawData objectForKey:@"title"])
        self.title = [_rawData objectForKey:@"title"];
    
    
    UIImageView * tBg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 10, SCREEN_WIDTH, 50)];
    tBg.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:tBg];

    
    _userName = [[UITextField alloc] initWithFrame:CGRectMake(10,
                                                              20,
                                                              SCREEN_WIDTH-20-40,
                                                              31)];
    _userName.backgroundColor = [UIColor clearColor];
    _userName.font = [UIFont systemFontOfSize:16];
    _userName.text = @"";
    _userName.textAlignment = NSTextAlignmentLeft;
    _userName.textColor = COLOR_TEXT_A;
    [self.view addSubview:_userName];
    _userName.returnKeyType = UIReturnKeyDone;
    _userName.delegate = self;
    
   _userName.text = [_rawData objectForKey:@"value"];
    

    UIButton *btnClear = [UIButton buttonWithType:UIButtonTypeCustom];
    btnClear.frame = CGRectMake(CGRectGetMaxX(_userName.frame), CGRectGetMinY(_userName.frame)-10, 50, 50);
    [btnClear setImage:[UIImage imageNamed:@"tf_field_clear.png"] forState:UIControlStateNormal];
    [self.view addSubview:btnClear];
    [btnClear addTarget:self action:@selector(clearLoginNameEnter:) forControlEvents:UIControlEventTouchUpInside];
    
    
    UIButton *btnSignin = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSignin.frame = CGRectMake(0, 0, 50, 40);
    [btnSignin setTitle:@"完成" forState:UIControlStateNormal];
    btnSignin.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnSignin addTarget:self action:@selector(uploadAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSignin setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSignin];


}

- (void) clearLoginNameEnter:(id)sender{
    
    _userName.text = @"";
}



- (void) uploadAction:(UIButton*)sender{

    
    if([_userName.text length] == 0)
        return;
    
     [_rawData setValue:_userName.text forKey:@"value"];
    
    
    if([_rawData objectForKey:@"action"])
    {
        if(_http == nil)
        {
            _http = [[WebClient alloc] initWithDelegate:self];
        }
        
        _http._method = [_rawData objectForKey:@"action"];
        _http._httpMethod = @"POST";
        
        //User *u = [UserDefaultsKV getUser];
        
        NSMutableDictionary *param = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                      [_rawData objectForKey:@"groupid"],@"groupid",
                                      nil];
        
        if([_userName.text length])
        {
            [param setObject:_userName.text forKey:@"groupname"];
        }
        
        _http._requestParam = param;
        
        
        IMP_BLOCK_SELF(EditNameViewController);
        
        sender.enabled = NO;
        
        [_http requestWithSusessBlock:^(id lParam, id rParam) {
            
            NSString *response = lParam;
            // NSLog(@"%@", response);
            
            sender.enabled = YES;
            
            SBJson4ValueBlock block = ^(id v, BOOL *stop) {
                
                
                if([v isKindOfClass:[NSDictionary class]])
                {
                    int code = [[v objectForKey:@"code"] intValue];
                    
                    if(code == 1)
                    {
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
            
            sender.enabled = YES;
        }];
    }
    
    
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
