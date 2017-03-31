//
//  ShootQRCode.m
//  Hint
//
//  Created by jack on 12/14/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "ShootQRCode.h"
#import "ZBarReaderViewController.h"
#import "CMNavigationController.h"
#import "MyCodeViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "WaitDialog.h"

static ShootQRCode* shootInstance = nil;


@interface ShootQRCode ()<ZBarReaderDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>
{
    WebClient *_httpChecker;
    
    UIImagePickerController *_imagePicker;
}
@property (nonatomic, strong) ZBarReaderViewController *_zbar;
@property (nonatomic, strong) NSString *r_qrcode;
@end


@implementation ShootQRCode
@synthesize _viewController;
@synthesize _zbar;
@synthesize r_qrcode;

+ (ShootQRCode*)sharedShootCodeInstance{
    

    if(shootInstance == nil){
        shootInstance = [[ShootQRCode alloc] init];
    }
    return shootInstance;
}


- (id) init
{
    
    if(self = [super init])
    {
        
    }
    
    
    return self;
}

- (void) showMyCode:(UIViewController*)sender{
    
    self._viewController = sender;
    
    MyCodeViewController *myCode = [[MyCodeViewController alloc] init];
    myCode._isShootMyCodePresent = YES;
    // present and release the controller
    [self._viewController presentViewController:myCode animated:YES completion:^{
        
    }];
}


- (void) initCameraShoot{
    
    
    self._zbar = [[ZBarReaderViewController alloc] init];
    _zbar.title = @"扫描二维码";
    _zbar.readerDelegate = self;
    _zbar.showsZBarControls = NO;
    _zbar.cameraFlashMode = UIImagePickerControllerCameraFlashModeOff;
    _zbar.supportedOrientationsMask = ZBarOrientationMask(UIInterfaceOrientationPortrait);
    
    ZBarImageScanner *scanner = _zbar.scanner;
    // TODO: (optional) additional reader configuration here
    
    // EXAMPLE: disable rarely used I2/5 to improve performance
    [scanner setSymbology: ZBAR_I25
                   config: ZBAR_CFG_ENABLE
                       to: 0];
    
    UIView* cameraShowView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    [_zbar.view addSubview:cameraShowView];
    
    float subW = 250;
    float subH = 250;
    
    UIImageView* shoot_image = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, subW, subH)];
    [cameraShowView addSubview:shoot_image];
    shoot_image.backgroundColor = [UIColor clearColor];
    shoot_image.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT/2-20);
    
    
    UIImageView *maskTop = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, CGRectGetMinY(shoot_image.frame))];
    maskTop.backgroundColor = RGBA(0, 0, 0, 0.5);
    [cameraShowView addSubview:maskTop];
    UIImageView *maskBottom = [[UIImageView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(shoot_image.frame),
                                                                            SCREEN_WIDTH,SCREEN_HEIGHT-CGRectGetMaxY(shoot_image.frame))];
    maskBottom.backgroundColor = RGBA(0, 0, 0, 0.5);
    [cameraShowView addSubview:maskBottom];
    
    UIImageView *maskLeft = [[UIImageView alloc] initWithFrame:CGRectMake(0, CGRectGetMinY(shoot_image.frame),
                                                                          CGRectGetMinX(shoot_image.frame),
                                                                          CGRectGetHeight(shoot_image.frame))];
    maskLeft.backgroundColor = RGBA(0, 0, 0, 0.5);
    [cameraShowView addSubview:maskLeft];
    
    UIImageView *maskRight = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(shoot_image.frame),
                                                                           CGRectGetMinY(shoot_image.frame),
                                                                           SCREEN_WIDTH - CGRectGetWidth(shoot_image.frame),
                                                                           CGRectGetHeight(shoot_image.frame))];
    maskRight.backgroundColor = RGBA(0, 0, 0, 0.5);
    [cameraShowView addSubview:maskRight];
    
    /////0
    UILabel *l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(maskLeft.frame),
                                                            CGRectGetMinY(maskLeft.frame),
                                                            20, 1)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(maskLeft.frame),
                                                   CGRectGetMinY(maskLeft.frame),
                                                   1, 20)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    
    ////1
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(maskRight.frame)-20,
                                                   CGRectGetMinY(maskLeft.frame),
                                                   20, 1)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(maskRight.frame)-1,
                                                   CGRectGetMinY(maskLeft.frame),
                                                   1, 20)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    
    ////2
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(maskLeft.frame),
                                                   CGRectGetMinY(maskBottom.frame)-1,
                                                   20, 1)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(maskLeft.frame),
                                                   CGRectGetMinY(maskBottom.frame)-20,
                                                   1, 20)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    
    
    ////
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(maskRight.frame)-20,
                                                   CGRectGetMinY(maskBottom.frame)-1,
                                                   20, 1)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    l1 = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(maskRight.frame)-1,
                                                   CGRectGetMinY(maskBottom.frame)-20,
                                                   1, 20)];
    l1.backgroundColor = [UIColor greenColor];
    [cameraShowView addSubview:l1];
    
    
    UILabel *maskCover = [[UILabel alloc] initWithFrame:CGRectMake(0, SCREEN_HEIGHT-60-44, SCREEN_WIDTH, 60)];
    maskCover.backgroundColor = THEME_COLOR;
    maskCover.alpha = 0.8;
    [_zbar.view addSubview:maskCover];
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    //[cancelBtn setTitle:[[UserDefaultsKV sharedUserDefaultsKV] lanWithKey:@"Cancel"] forState:UIControlStateNormal];
    //cancelBtn.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [cancelBtn setImage:[UIImage imageNamed:@"iconfont-quxiao.png"] forState:UIControlStateNormal];
    cancelBtn.frame = CGRectMake(50, SCREEN_HEIGHT-50-44, SCREEN_WIDTH-100, 40);
    cancelBtn.layer.cornerRadius = 5;
    cancelBtn.clipsToBounds = YES;
    [_zbar.view addSubview:cancelBtn];
    [cancelBtn addTarget:self action:@selector(cancelZbarController) forControlEvents:UIControlEventTouchUpInside];
    
    UIButton *photoAlbum = [UIButton buttonWithType:UIButtonTypeCustom];
    [photoAlbum setImage:[UIImage imageNamed:@"iconfont-xiangce.png"] forState:UIControlStateNormal];
    photoAlbum.frame = CGRectMake(SCREEN_WIDTH-50, SCREEN_HEIGHT-55-44, 44, 50);
    
    [_zbar.view addSubview:photoAlbum];
    [photoAlbum addTarget:self action:@selector(openAlbum:) forControlEvents:UIControlEventTouchUpInside];
    
    
    
    // present and release the controller
    _zbar.hidesBottomBarWhenPushed = YES;
    [self._viewController.navigationController pushViewController:_zbar animated:YES];
    
    //_viewController.navigationController.navigationBarHidden = YES;
    
}

- (void) shootCode:(UIViewController*)sender{
    
    self._viewController = sender;
    
    if(_zbar && [_zbar.view superview])
        return;
    
    
    [self initCameraShoot];
    
}

- (void)cancelZbarController {
    
    //test;
   // NSString *qr = @"http://www.hint.com/qr/c+IkDIhhNvW5QA2XlshKlolBerB6g5t28kK3WYX3N/E=";
    
    
    [self._viewController.navigationController popViewControllerAnimated:YES];
    self._zbar = nil;

    //[self parseQRCodeString:@"Jh2uegdGl7Kw-SRaQ0ogig"];
}


- (void) imagePickerController: (UIImagePickerController*) reader didFinishPickingMediaWithInfo: (NSDictionary*) info {
    
    if(reader == _imagePicker)
    {
        ZBarImageScanner *scann = [[ZBarImageScanner alloc] init];
        [scann setSymbology: ZBAR_I25
                     config: ZBAR_CFG_ENABLE
                         to: 0];
        
        UIImage *img = [info objectForKey:UIImagePickerControllerEditedImage];
        ZBarImage *barImg = [[ZBarImage alloc] initWithCGImage:img.CGImage size:img.size];
        
        [scann scanImage:barImg];
        
        id<NSFastEnumeration> results = scann.results;
        ZBarSymbol *symbol = nil;
        for(symbol in results)
            // EXAMPLE: just grab the first barcode
            break;
        NSString *barcode_scan = symbol.data;
        //NSLog(@"%@", barcode_scan);
        
        IMP_BLOCK_SELF(ShootQRCode);

        [self._viewController dismissViewControllerAnimated:YES completion:^{
            
            [block_self parseQRCodeString:barcode_scan];
        }];
//
        
        
        return;
    }
    
    // ADD: get the decode results
    id<NSFastEnumeration> results =
    [info objectForKey: ZBarReaderControllerResults];
    ZBarSymbol *symbol = nil;
    for(symbol in results)
        // EXAMPLE: just grab the first barcode
        break;
    
    
    NSString *barcode_scan = symbol.data;
    
    [self parseQRCodeString:barcode_scan];
    
//    [self._viewController.navigationController popViewControllerAnimated:YES];
//    self._zbar = nil;
//    
    
    
    
}


- (void) parseQRCodeString:(NSString*)qr{
    
    NSString *qrcode = qr;
    
    NSRange range = [qr rangeOfString:URL_QR_HOST];
    if(range.location != NSNotFound)
    {
        qrcode = [qr substringFromIndex:(range.location+range.length)];
    }
    
    self.r_qrcode = qrcode;
    
    [self testQRCode:qrcode];
    
    
   // [self sendReg:qr];
}


- (void) testQRCode:(NSString*)qrcode{
    
    
    
    
}


- (void) processScanResult:(NSDictionary*)result{
    
   
   
}


- (void) sendReg:(NSString*)qrcode{
    
    
    if(_httpChecker == nil)
    {
        _httpChecker = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpChecker._method = API_INVITE_FRIEND;
    _httpChecker._httpMethod = @"POST";
    
    User *u = [UserDefaultsKV getUser];
    
    NSMutableDictionary *param = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                  u._authtoken,@"token",
                                  qrcode, @"invitee",
                                  @"QR",@"type",
                                  nil];
    
    _httpChecker._requestParam = param;
    
    
    IMP_BLOCK_SELF(ShootQRCode);
    
    [_httpChecker requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 0)
                {
                    [[NSNotificationCenter defaultCenter] postNotificationName:@"RefreshContactListNotify" object:nil];
                    
                    [[WaitDialog sharedAlertDialog] setTitle:@"已加为好友"];
                    [[WaitDialog sharedAlertDialog] animateShow];
                    
                }
            }
            
            [block_self cancelZbarController];
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            
            
            [block_self cancelZbarController];
            NSLog(@"OOPS: %@", err);
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];
        
        
    } FailBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        [block_self cancelZbarController];
    }];
    
}


- (void) openAlbum:(id)sender{
    
    if(_imagePicker)
        _imagePicker = nil;
    
    _imagePicker = [[UIImagePickerController alloc] init];
    _imagePicker.delegate = self;
    _imagePicker.allowsEditing = NO;
    _imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self._viewController presentViewController:_imagePicker animated:YES completion:nil];
}





@end
