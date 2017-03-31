//
//  SetUInfoViewController.m
//  Hint
//
//  Created by jack on 9/8/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "SetUInfoViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "UIButton+Color.h"
#import "GoGoDB.h"
#import "WSUser.h"
#import "WaitDialog.h"


@interface SetUInfoViewController ()<UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextFieldDelegate>
{
    UITableView *_tableView;
    
    UIImagePickerController *_imagePicker;
    
    UIImageView* _actorLogo;
    
    UIView *_footer;
    UIButton *_opBtn;
    
    WebClient *_httpChecker;
    
    UITextField *_uName;
    UITextField *_uEmail;
    UITextField *_uMobile;
    UITextField *_uTel;
    
    UITextField *_uCompany;
    UITextField *_uTitle;
    
    UIView *_maskView;
    UIImageView *_avatarView;
}
@property (nonatomic, strong) UIImage *_avataImg;
@property (nonatomic, strong) User *_user;
@property (nonatomic, strong) NSMutableArray *_datas;


@end

@implementation SetUInfoViewController
@synthesize _avataImg;
@synthesize _user;
@synthesize _datas;


- (void) viewWillAppear:(BOOL)animated
{
    //[self reloadPersonInfo];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.navigationItem.title = @"个人信息";
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH - 20 - 70, 10, 60, 60)];
    _actorLogo.layer.cornerRadius = 30;
    _actorLogo.clipsToBounds = YES;
    _actorLogo.backgroundColor = [UIColor clearColor];
    _actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    
    
    [self createField];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    

    _maskView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _maskView.backgroundColor = RGBA(0, 0, 0, 0.4);
    
    _avatarView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_WIDTH)];
    [_maskView addSubview:_avatarView];
    _avatarView.center = CGPointMake(SCREEN_WIDTH/2, (SCREEN_HEIGHT-64)/2-32);
    _avatarView.backgroundColor = RGB(56, 62, 98);
    _avatarView.layer.contentsGravity = kCAGravityResizeAspectFill;
    
    [self reloadData];
    
    
    
    
   // [self checkIsFriends];
    
    
    //[self isFriend];
}

- (void) createField{
    
    _uName = [[UITextField alloc] initWithFrame:CGRectMake(80,
                                                          14,
                                                          SCREEN_WIDTH-90,
                                                          31)];
    _uName.backgroundColor = [UIColor clearColor];
    _uName.font = [UIFont systemFontOfSize:14];
    _uName.textAlignment = NSTextAlignmentLeft;
    _uName.textColor = COLOR_TEXT_2B;
    _uName.returnKeyType = UIReturnKeyDone;
    _uName.delegate = self;
    
    _uEmail = [[UITextField alloc] initWithFrame:CGRectMake(80,
                                                             14,
                                                             SCREEN_WIDTH-90,
                                                             31)];
    _uEmail.backgroundColor = [UIColor clearColor];
    _uEmail.font = [UIFont systemFontOfSize:14];
    _uEmail.textAlignment = NSTextAlignmentLeft;
    _uEmail.textColor = COLOR_TEXT_2B;
    _uEmail.returnKeyType = UIReturnKeyDone;
    _uEmail.delegate = self;
    
    _uMobile = [[UITextField alloc] initWithFrame:CGRectMake(80,
                                                           14,
                                                           SCREEN_WIDTH-90,
                                                           31)];
    _uMobile.backgroundColor = [UIColor clearColor];
    _uMobile.font = [UIFont systemFontOfSize:14];
    _uMobile.textAlignment = NSTextAlignmentLeft;
    _uMobile.textColor = COLOR_TEXT_2B;
    _uMobile.returnKeyType = UIReturnKeyDone;
    _uMobile.delegate = self;
    
    _uTel = [[UITextField alloc] initWithFrame:CGRectMake(80,
                                                             14,
                                                             SCREEN_WIDTH-90,
                                                             31)];
    _uTel.backgroundColor = [UIColor clearColor];
    _uTel.font = [UIFont systemFontOfSize:14];
    _uTel.textAlignment = NSTextAlignmentLeft;
    _uTel.textColor = COLOR_TEXT_2B;
    _uTel.returnKeyType = UIReturnKeyDone;
    _uTel.delegate = self;
    
    _uCompany = [[UITextField alloc] initWithFrame:CGRectMake(80,
                                                          14,
                                                          SCREEN_WIDTH-90,
                                                          31)];
    _uCompany.backgroundColor = [UIColor clearColor];
    _uCompany.font = [UIFont systemFontOfSize:14];
    _uCompany.textAlignment = NSTextAlignmentLeft;
    _uCompany.textColor = COLOR_TEXT_2B;
    _uCompany.returnKeyType = UIReturnKeyDone;
    _uCompany.delegate = self;
    
    _uTitle = [[UITextField alloc] initWithFrame:CGRectMake(80,
                                                          14,
                                                          SCREEN_WIDTH-90,
                                                          31)];
    _uTitle.backgroundColor = [UIColor clearColor];
    _uTitle.font = [UIFont systemFontOfSize:14];
    _uTitle.textAlignment = NSTextAlignmentLeft;
    _uTitle.textColor = COLOR_TEXT_2B;
    _uTitle.returnKeyType = UIReturnKeyDone;
    _uTitle.delegate = self;
    
    _uName.tag = 1;
    _uMobile.tag = 1;
    _uEmail.tag = 1;
    _uTel.tag = 1;
    
    _uCompany.tag = 2;
    _uTitle.tag = 3;
    
    _uName.userInteractionEnabled = NO;
    _uMobile.userInteractionEnabled = NO;
    _uEmail.userInteractionEnabled = NO;
    _uTel.userInteractionEnabled = NO;
    
    _uCompany.userInteractionEnabled = NO;
    _uTitle.userInteractionEnabled = NO;
    
}


- (void) reloadPersonInfo{
    
    /*
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    _http._httpMethod = @"GET";
    
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           _userid, @"uid",
                           nil];
    
    
    IMP_BLOCK_SELF(UserInfoViewController);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 0)
                {
                    
                    block_self._user = [[User alloc] initWithDicionary:v];
                    _user._avatar = [v objectForKey:@"avatarurl"];
                    _user._userName = [v objectForKey:@"fullname"];
                    _user._ctime = [v objectForKey:@"ctime"];
                    
                    if(_wsUser)
                    {
                        _user._ctime = _wsUser.ctime;
                    }
                    
                    [block_self._user updateUserInfo:v];
                    
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
     
     */
}

- (void) reloadData{
    
    RCUserInfo *user = [[RCUserInfo alloc] init];
    user.userId = _user._userId;
    user.name = _user._userName;
    user.portraitUri = _user._avatar;
    [[GoGoDB sharedDBInstance] saveUserInfo:user];
    
    self._user = [UserDefaultsKV getUser];
    
//    self._datas = [[NSMutableArray alloc] init];
//    
//    [_datas addObject:@{@"type":@"avatar"}];
//    [_datas addObject:@{@"type":@"name"}];
//    
//    
//    if([_user.companyname length])
//    {
//        [_datas addObject:@{@"type":@"info", @"title":@"公司", @"value":_user.companyname}];
//    }
//    
//    if([_user.companyname length])
//    {
//        [_datas addObject:@{@"type":@"info", @"title":@"职位", @"value":_user.ranktitle}];
//    }
//    if(_user._ctime)
//    {
//        NSDateFormatter *fm = [[NSDateFormatter alloc] init];
//        [fm setDateFormat:@"yyyy-MM-dd"];
//        NSDate *d = [NSDate dateWithTimeIntervalSince1970:[_user._ctime intValue]];
//        
//        NSString *str = [fm stringFromDate:d];
//        
//        [_datas addObject:@{@"type":@"info", @"title":@"注册时间", @"value":str}];
//        
//    }
    
    
    [_tableView reloadData];
    
}

#pragma mark UITableView dataSource
- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIndentifier = @"UserCell";
    UITableViewCell *cell = (UITableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:CellIndentifier];
    }
    
    cell.accessoryType = UITableViewCellAccessoryNone;
    
    [[cell.contentView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    if(indexPath.section == 0)
    {
        if(indexPath.row == 0)
        {
            //个人信息，头像，账号
            
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    
            
            [cell.contentView addSubview:_actorLogo];
            
            [_actorLogo setImageWithURL:[NSURL URLWithString:_user._avatar]
                       placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
            
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       10,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont boldSystemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"头像";
            
            UILabel* markL = [[UILabel alloc] initWithFrame:CGRectMake(80,
                                                                       10,
                                                                       120, 60)];
            markL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:markL];
            markL.font = [UIFont systemFontOfSize:14];
            markL.textAlignment = NSTextAlignmentLeft;
            markL.textColor  = COLOR_TEXT_B;
            markL.text = @"编辑";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 99, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        
    }
    else if(indexPath.section == 1)
    {
        if(indexPath.row == 0)
        {
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"姓名";
            
    
            [cell.contentView addSubview:_uName];
            _uName.text = _user._userName;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else if(indexPath.row == 1)
        {
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"邮箱";
            
            
            [cell.contentView addSubview:_uEmail];
            _uEmail.text = _user._email;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else if(indexPath.row == 2)
        {
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"手机";
            
            
            [cell.contentView addSubview:_uMobile];
            _uMobile.text = _user._cellphone;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else if(indexPath.row == 3)
        {
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"电话";
            
            
            [cell.contentView addSubview:_uTel];
            _uTel.text = _user.telephone;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
    }
    else if(indexPath.section == 2)
    {
        if(indexPath.row == 0)
        {
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"公司";
            
            
            [cell.contentView addSubview:_uCompany];
            _uCompany.text = _user.companyname;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else if(indexPath.row == 1)
        {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"地址";
            
            
            UILabel* addressL = [[UILabel alloc] initWithFrame:CGRectMake(80,
                                                                       0,
                                                                       SCREEN_WIDTH-100, 60)];
            addressL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:addressL];
            addressL.font = [UIFont systemFontOfSize:14];
            addressL.textAlignment = NSTextAlignmentLeft;
            addressL.textColor  = COLOR_TEXT_A;
            addressL.text = _user.address;

            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }

    }
    else{
        
        if(indexPath.row == 0)
        {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"部门";
            
            
            UILabel* dptL = [[UILabel alloc] initWithFrame:CGRectMake(80,
                                                                          0,
                                                                          SCREEN_WIDTH-100, 60)];
            dptL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:dptL];
            dptL.font = [UIFont systemFontOfSize:14];
            dptL.textAlignment = NSTextAlignmentLeft;
            dptL.textColor  = COLOR_TEXT_A;
            dptL.text = @"产品部";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else if(indexPath.row == 1)
        {
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"职位";
            
            
            [cell.contentView addSubview:_uTitle];
            _uTitle.text = _user.ranktitle;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }

    }
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if(section == 0)
        return 1;
    if(section == 1)
        return 4;
    
    return 2;
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 4;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if(indexPath.section == 0)
    {
        if(indexPath.row == 0)
            return 80;
    }
    
    return 60;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    if(section == 0)
        return 0;
    
    return 20;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    if(section == 0)
        return nil;
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 30)];
    header.backgroundColor = [UIColor clearColor];
    return header;
}


#pragma mark UITableView delegate



- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if(indexPath.section == 0)
    {
        [_avatarView setImageWithURL:[NSURL URLWithString:_user._avatar]];
        [self.view addSubview:_maskView];
        
        [self chooseAvatar:nil];
        
    }
}



- (void) chooseAvatar:(id)sender{
    
    if(_imagePicker == nil)
    {
        _imagePicker = [[UIImagePickerController alloc] init];
        _imagePicker.delegate = self;
        _imagePicker.allowsEditing = YES;
        
    }
    
    [[UINavigationBar appearance] setTintColor:THEME_COLOR];
    
    
    UIActionSheet *as = [[UIActionSheet alloc] initWithTitle:nil
                                                    delegate:self
                                           cancelButtonTitle:@"取消"
                                      destructiveButtonTitle:nil
                                           otherButtonTitles:@"拍照",@"从相册选取",nil];
    [as showInView:self.view];

    
    
}



- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(buttonIndex == 0)
    {
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
        {
            
            _imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
            [self presentViewController:_imagePicker animated:YES
                             completion:^{
                             }];

        }
        else
            
        {
            [_maskView removeFromSuperview];
        }
        
    }
    else if(buttonIndex == 1)
    {
        _imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentViewController:_imagePicker animated:YES
                         completion:^{
                             
                         }];
    }
    else
    {
        [_maskView removeFromSuperview];
    }
}


/**** Image Picker Delegates ******/
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    if(picker == _imagePicker)
    {
        UIImage *image = [info objectForKey:UIImagePickerControllerEditedImage];
        if(image)
        {
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                // 耗时的操作
                
                UIImage *img = [self imageWithImage:image scaledToSize:CGSizeMake(640, 640)];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    // 更新界面
                    self._avataImg = img;
                    [_actorLogo setImage:img];
                    [_avatarView setImage:img];
                    
                    [self uploadAvatar];
                    
                });
            });
        }
        
        
        
    }
    
    [picker dismissViewControllerAnimated:YES completion:NULL];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissViewControllerAnimated:YES completion:NULL];
}

- (UIImage*)imageWithImage:(UIImage*)image scaledToSize:(CGSize)newSize
{
    if(image==nil)return nil;
    
    float width = image.size.width;
    float height = image.size.height;
    float x, x1, y1;
    
    if(width > height)
    {
        x1 = width / newSize.height;
        y1 = height / newSize.width;
    }
    else
    {
        x1 = width / newSize.width;
        y1 = height / newSize.height;
    }
    
    x = (x1 > y1) ? x1:y1;
    
    if(x < 1.0)return image;
    
    if(fabs(x-1.0) < 0.0001)return image;
    
    CGSize s = CGSizeMake(width/x,height/x);
    
    
    UIGraphicsBeginImageContext(s);
    [image drawInRect:CGRectMake(0,0,s.width,s.height)];
    
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return newImage;
}

- (void) uploadAvatar{
    
    
    if(self._avataImg == nil)
        return;
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = @"/upload!uploadUserLogoNotCut";
    _http._httpMethod = @"POST";
    
    NSMutableDictionary *param = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                  _user._userId,@"userid",
                                  nil];
    
    if(_avataImg)
    {
        [param setObject:@"file" forKey:@"filename"];
        [param setObject:_avataImg forKey:@"image"];
    }
    
    _http._requestParam = param;
    
    
    IMP_BLOCK_SELF(SetUInfoViewController);
    
    [[WaitDialog sharedDialog] setTitle:@"上传中"];
    [[WaitDialog sharedDialog] startLoading];
    
    [_http requestWithSusessBlockWithImage:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        [[WaitDialog sharedDialog] endLoading];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 0)
                {
                    
                    block_self._user._avatar = [v objectForKey:@"avatarurl"];
                    [UserDefaultsKV saveUser:_user];
                    
                    
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
        
        [[WaitDialog sharedDialog] endLoading];
        
    }];
    
    [_maskView removeFromSuperview];
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    
   CGRect rc = [_tableView rectForSection:textField.tag];
    
    int max = CGRectGetMaxY(rc) + 50 + 40;
    
    //int max = CGRectGetMaxY(textField.frame) + 50 + 40;
    
    if(SCREEN_HEIGHT - 64 - max < 216)
    {
        int y = max - SCREEN_HEIGHT + 216 + 64;
        
        _tableView.contentOffset = CGPointMake(0, y);
    }
    
    
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    return YES;
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
