//
//  MyCodeViewController.m
//  Hint
//
//  Created by jack on 11/11/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "MyCodeViewController.h"
#import "QREncoder.h"
#import "DataMatrix.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"

@interface MyCodeViewController ()

@end

@implementation MyCodeViewController
@synthesize _u;
@synthesize _isShootMyCodePresent;
@synthesize _noteMark;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.view.backgroundColor = RGB(46, 49, 50);
    
    NSString *qr = @"";
    if(_u == nil)
    {
        User *u = [UserDefaultsKV getUser];
        self._u = u;
        
        qr = _u._qr;
    }
    
    if([qr length] == 0)
    {
        [self reloadPersonInfo];
    }
    else
    {
        [self showMyInfo:qr];
    }
    
    
    self.title = @"我的二维码";
}


- (void) showMyInfo:(NSString*)qr{
    
    int h = SCREEN_HEIGHT - 64;
    
    int ww = SCREEN_WIDTH - 30 - 30;
    int wh = SCREEN_WIDTH - 30 - 30 + 120;
    
    UIView *whiteView = [[UIView alloc] initWithFrame:CGRectMake(15, 0, SCREEN_WIDTH-30,wh)];
    whiteView.backgroundColor = [UIColor whiteColor];
    whiteView.layer.cornerRadius = 2;
    whiteView.clipsToBounds = YES;
    [self.view addSubview:whiteView];
    whiteView.center = CGPointMake(SCREEN_WIDTH/2, h/2);
    
    UIImageView *qrcode = [[UIImageView alloc] initWithFrame:CGRectMake(20, 80, ww-10, ww-10)];
    [whiteView addSubview:qrcode];
    qrcode.layer.contentsGravity = kCAGravityCenter;
    //qrcode.backgroundColor = [UIColor redColor];
    

    
    DataMatrix *data_matrix = [QREncoder encodeWithECLevel:QR_ECLEVEL_AUTO version:QR_VERSION_AUTO string:qr];
    qrcode.image = [QREncoder renderDataMatrix:data_matrix imageDimension:150];
    
    UIImageView* actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(20, 10, 50, 50)];
    actorLogo.layer.cornerRadius = 2;
    actorLogo.clipsToBounds = YES;
    actorLogo.backgroundColor = [UIColor clearColor];
    actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    [whiteView addSubview:actorLogo];
    [actorLogo setImageWithURL:[NSURL URLWithString:_u._avatar]
              placeholderImage:[UIImage imageNamed:@"defalut_avatar.png"]];
    
    
    UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(actorLogo.frame)+10,
                                                               10,
                                                               120, 25)];
    nameL.backgroundColor = [UIColor clearColor];
    [whiteView addSubview:nameL];
    nameL.font = [UIFont boldSystemFontOfSize:16];
    nameL.textAlignment = NSTextAlignmentLeft;
    nameL.textColor  = COLOR_TEXT_A;
    nameL.text = _u._userName;
    
    UILabel* cmpL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(actorLogo.frame)+10,
                                                              35,
                                                              SCREEN_WIDTH - (CGRectGetMaxX(actorLogo.frame)+20) , 20)];
    cmpL.backgroundColor = [UIColor clearColor];
    [whiteView addSubview:cmpL];
    cmpL.font = [UIFont systemFontOfSize:15];
    cmpL.textAlignment = NSTextAlignmentLeft;
    cmpL.textColor  = COLOR_TEXT_A;
    cmpL.text = [NSString stringWithFormat:@"%@ %@", _u.companyname, _u.ranktitle];
    
    
    UILabel* alertL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                CGRectGetMaxY(qrcode.frame)+10,
                                                                CGRectGetWidth(whiteView.frame)-20, 30)];
    alertL.backgroundColor = [UIColor clearColor];
    [whiteView addSubview:alertL];
    alertL.font = [UIFont systemFontOfSize:12];
    alertL.textAlignment = NSTextAlignmentCenter;
    alertL.textColor  = COLOR_TEXT_B;
    alertL.text = @"扫描二维码，加我为好友";
    
    if(_noteMark)
    {
        alertL.text = _noteMark;
    }
    
    if(_isShootMyCodePresent)
    {
        UILabel *maskCover = [[UILabel alloc] initWithFrame:CGRectMake(0, SCREEN_HEIGHT-60, SCREEN_WIDTH, 60)];
        maskCover.backgroundColor = THEME_COLOR;
        maskCover.alpha = 0.8;
        [self.view addSubview:maskCover];
        
        
        UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [cancelBtn setImage:[UIImage imageNamed:@"iconfont-quxiao.png"] forState:UIControlStateNormal];
        cancelBtn.frame = CGRectMake(50, SCREEN_HEIGHT-50, SCREEN_WIDTH-100, 40);
        cancelBtn.layer.cornerRadius = 5;
        cancelBtn.clipsToBounds = YES;
        [self.view addSubview:cancelBtn];
        [cancelBtn addTarget:self action:@selector(cancelController) forControlEvents:UIControlEventTouchUpInside];

    }
    
    
}

- (void) cancelController{
    
    [self dismissViewControllerAnimated:YES completion:^{
        
    }];
}

- (void) reloadPersonInfo{
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_USER_PROFILE;
    _http._httpMethod = @"GET";
    
    User *u = [UserDefaultsKV getUser];
    self._u = u;
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           u._authtoken,@"token",
                           nil];
    
    
    IMP_BLOCK_SELF(MyCodeViewController);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 0)
                {
                    _u._avatar = [v objectForKey:@"avatarurl"];
                    _u._userName = [v objectForKey:@"fullname"];
                    _u._qr = [v objectForKey:@"appuid"];
                    
                    [block_self reloadData];
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

- (void) reloadData{
    
    [UserDefaultsKV saveUser:_u];
    
    NSString *qr = @"";
    if(_u)
    {
        qr = _u._qr;
    }

    if([qr length])
        [self showMyInfo:qr];
    
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
