//
//  ProfileViewController.m
//  Hint
//
//  Created by jack on 9/8/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "ProfileViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "EditNameViewController.h"
#import "MyCodeViewController.h"


@interface ProfileViewController ()<UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>
{
    UITableView *_tableView;
    
    UIImagePickerController *_imagePicker;
    
    UIImageView* _actorLogo;
}
@property (nonatomic, strong) UIImage *_avataImg;
@property (nonatomic, strong) NSMutableArray *_sectionData;

@end

@implementation ProfileViewController
@synthesize _user;
@synthesize _avataImg;
@synthesize _sectionData;


- (void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
   
        
    self.navigationController.navigationBarHidden = NO;
        
    
    [self reloadPersonInfo];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.navigationItem.title = @"个人信息";
    
    self.view.backgroundColor = RGB(0xf8, 0xf8, 0xf8);
  
    
    _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH - 20 - 90, 10, 80, 80)];
    _actorLogo.layer.cornerRadius = 40;
    _actorLogo.clipsToBounds = YES;
    _actorLogo.backgroundColor = [UIColor clearColor];
    _actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)
                                              style:UITableViewStyleGrouped];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    _imagePicker = [[UIImagePickerController alloc] init];
    _imagePicker.delegate = self;
    _imagePicker.allowsEditing = YES;
    
    
    [self initData];
    
}


- (void) initData{
    
    _sectionData = [[NSMutableArray alloc] init];
    
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setObject:@"base" forKey:@"type"];
    [dic setObject:@4 forKey:@"row"];
    [_sectionData addObject:dic];
    
    
    dic = [[NSMutableDictionary alloc] init];
    [dic setObject:@"card" forKey:@"type"];
    [dic setObject:@0 forKey:@"row"];
    [_sectionData addObject:dic];
    
    
}


-(void) refreshDataContent {

    
    NSMutableDictionary *mdic = [_sectionData objectAtIndex:0];
    NSMutableArray *rows = [[NSMutableArray alloc] init];
    
    //企业名称
    //名字
    NSString *nameStr       = @"";
    NSString *CompanyName   = @"";
    NSString *rank          = @"";
    NSString *Mobile        = @"";
    NSString *email         = @"";
    NSString *address       = @"";
    if(_user)
    {
        nameStr         = _user._userName;
        CompanyName     = _user.companyname;
        rank            = _user.ranktitle;
        Mobile          = _user._cellphone;
        email           = _user._email;
        address         = _user.address;
    }

    NSString *avatar = _user._avatar;
    
    if(avatar == nil)
        avatar = @"";
    [rows addObject:@{@"title":@"头像", @"height":[NSNumber numberWithInt:100], @"action":@"avatar"}];
    
    if(nameStr == nil)
        nameStr = @"";
    [rows addObject:@{@"title":@"名字",@"value":nameStr, @"height":[NSNumber numberWithInt:50],@"action":@"fullname"}];
    
    [rows addObject:@{@"title":@"我的二维码", @"action":@"qrcode", @"height":[NSNumber numberWithInt:50]}];
    
    [mdic setObject:rows forKey:@"rows"];
    
    
    mdic = [_sectionData objectAtIndex:1];
    rows = [[NSMutableArray alloc] init];
    
    
    
    if(CompanyName == nil)
        CompanyName = @"";
    [rows addObject:@{@"title":@"公司名称",@"value":CompanyName, @"height":[NSNumber numberWithInt:50],@"action":@"company"}];
    
    if(rank == nil)
        rank = @"";
    [rows addObject:@{@"title":@"职位", @"value":rank, @"action":@"title", @"height":[NSNumber numberWithInt:50]}];
    
    if(Mobile == nil)
        Mobile = @"";
    [rows addObject:@{@"title":@"手机号", @"value":Mobile, @"action":@"cellphone", @"height":[NSNumber numberWithInt:50]}];
    
    if(email == nil)
        email = @"";
    [rows addObject:@{@"title":@"邮箱", @"value":email, @"action":@"email", @"height":[NSNumber numberWithInt:50]}];
    
    if(address == nil)
        address = @"";
    [rows addObject:@{@"title":@"地址", @"value":address, @"action":@"address", @"height":[NSNumber numberWithInt:50]}];
 
    
    [mdic setObject:rows forKey:@"rows"];
    
    [_tableView reloadData];
    
}


- (void) reloadPersonInfo{
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_USER_PROFILE;
    _http._httpMethod = @"GET";
    
    User *u = [UserDefaultsKV getUser];
    self._user = u;
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           u._authtoken,@"token",
                           nil];
    
    
    IMP_BLOCK_SELF(ProfileViewController);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 0)
                {
                    
                    [_user updateUserInfo:v];
                    
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
    
    [UserDefaultsKV saveUser:_user];
    
    
    RCUserInfo *user = [[RCUserInfo alloc] initWithUserId:_user._authtoken name:_user._userName portrait:nil];
    [RCIM sharedRCIM].currentUserInfo = user;
    user.portraitUri = _user._avatar;
    [RCIMClient sharedRCIMClient].currentUserInfo = user;

    
    [self refreshDataContent];
    
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
    
    
    
    NSDictionary *dic = [_sectionData objectAtIndex:indexPath.section];
    NSArray *rows = [dic objectForKey:@"rows"];
    if(indexPath.row < [rows count])
    {
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        NSDictionary *data = [rows objectAtIndex:indexPath.row];
        NSString *action = [data objectForKey:@"action"];
        int height = [[data objectForKey:@"height"] intValue];
        
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                   0,
                                                                   120, 50)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:15];
        nameL.textAlignment = NSTextAlignmentLeft;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = [data objectForKey:@"title"];
        
        if([data objectForKey:@"value"])
        {
            UILabel* valueL = [[UILabel alloc] initWithFrame:CGRectMake(100,
                                                                       0,
                                                                       SCREEN_WIDTH-130, height)];
            valueL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:valueL];
            valueL.font = [UIFont systemFontOfSize:15];
            valueL.textAlignment = NSTextAlignmentRight;
            valueL.textColor  = [UIColor blackColor];
            valueL.text = [data objectForKey:@"value"];
        }
        
        if([action isEqualToString:@"avatar"])
        {
            [cell.contentView addSubview:_actorLogo];
            
            [_actorLogo setImageWithURL:[NSURL URLWithString:_user._avatar]
                       placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
            
            
            nameL.frame = CGRectMake(15, 20, 120, 60);

        }
        else if([action isEqualToString:@"qrcode"])
        {
            UIImageView* logo = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"iconfont-erweima.png"]];
            [cell.contentView addSubview:logo];
            logo.center = CGPointMake(SCREEN_WIDTH-40, 25);
        }
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(10, height-1, SCREEN_WIDTH-10, 1)];
        line.backgroundColor = LINE_COLOR;
        [cell.contentView addSubview:line];
        
    }

    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    NSDictionary *sec = [_sectionData objectAtIndex:section];
    NSArray *rows = [sec objectForKey:@"rows"];
    
    return [rows count];
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return [_sectionData count];
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    NSDictionary *sec = [_sectionData objectAtIndex:indexPath.section];
    NSArray *rows = [sec objectForKey:@"rows"];
  
    NSDictionary *row = [rows objectAtIndex:indexPath.row];
    
    int height = [[row objectForKey:@"height"] intValue];

    return height;
}



- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    if(section == 0)
        return 20;
    return 1;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 20)];
    header.backgroundColor = [UIColor clearColor];
    return header;
}


#pragma mark UITableView delegate



- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    NSDictionary *dic = [_sectionData objectAtIndex:indexPath.section];
    NSArray *rows = [dic objectForKey:@"rows"];
    if(indexPath.row < [rows count])
    {
        NSDictionary *data = [rows objectAtIndex:indexPath.row];
        NSString *action = [data objectForKey:@"action"];
        
        if([action isEqualToString:@"avatar"])
        {
            [self chooseAvatar:nil];
        }
        else if([action isEqualToString:@"fullname"] ||
                [action isEqualToString:@"company"] ||
                [action isEqualToString:@"title"] ||
                [action isEqualToString:@"cellphone"] ||
                [action isEqualToString:@"email"] ||
                [action isEqualToString:@"address"])
        {
            EditNameViewController *eidt = [[EditNameViewController alloc] init];
            eidt._u = self._user;
            eidt._rawData = data;
            [self.navigationController pushViewController:eidt animated:YES];
        }
        else if([action isEqualToString:@"qrcode"])
        {
          [self showMyCode];
        }
       
    }
    
    
}


- (void) showMyCode{
    
    MyCodeViewController *mycode = [[MyCodeViewController alloc] init];
    mycode._u = self._user;
    [self.navigationController pushViewController:mycode animated:YES];
}


- (void) editSchoolAccount{
    
     
}

- (void) editFullname{
    
    EditNameViewController *eidt = [[EditNameViewController alloc] init];
    eidt._u = self._user;
    [self.navigationController pushViewController:eidt animated:YES];
}

- (void) chooseAvatar:(id)sender{
    
    if(_imagePicker == nil)
    {
        _imagePicker = [[UIImagePickerController alloc] init];
        _imagePicker.delegate = self;
        _imagePicker.allowsEditing = YES;
    
    }
    
    [[UINavigationBar appearance] setTintColor:THEME_COLOR];
    
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
        UIActionSheet *as = [[UIActionSheet alloc] initWithTitle:nil
                                                        delegate:self
                                               cancelButtonTitle:@"取消"
                                          destructiveButtonTitle:nil
                                               otherButtonTitles:@"直接拍照",@"从相册中选取",nil];
        [as showInView:self.view];
        
    }
    else
    {
        _imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentViewController:_imagePicker animated:YES
                         completion:^{
                             
                         }];
    }
    
    
}



- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(buttonIndex == 0)
    {
        _imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        [self presentViewController:_imagePicker animated:YES
                         completion:^{
                         }];
        
    }
    if(buttonIndex == 1)
    {
        _imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentViewController:_imagePicker animated:YES
                         completion:^{
                             
                         }];
    }
}


/**** Image Picker Delegates ******/
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage *image = [info objectForKey:UIImagePickerControllerEditedImage];
    if(image)
    {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            // 耗时的操作
            
            UIImage *img = [self imageWithImage:image scaledToSize:CGSizeMake(320, 320)];
            
            dispatch_async(dispatch_get_main_queue(), ^{
                // 更新界面
                self._avataImg = img;
                [_actorLogo setImage:img];
                
                [self uploadAvatar];
                
            });
        });
    }
    
    
    [_imagePicker dismissViewControllerAnimated:YES completion:NULL];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [_imagePicker dismissViewControllerAnimated:YES completion:NULL];
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
    
    _http._method = API_USER_PROFILE;
    _http._httpMethod = @"POST";
    
    User *u = [UserDefaultsKV getUser];
    
    NSMutableDictionary *param = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                  u._authtoken,@"token",
                                  nil];

    if(_avataImg)
    {
        [param setObject:@"fileavatar" forKey:@"filename"];
        [param setObject:_avataImg forKey:@"image"];
    }
    
    _http._requestParam = param;
    
    
    IMP_BLOCK_SELF(ProfileViewController);
  
    [_http requestWithSusessBlockWithImage:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
    
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
        
    }];
}


- (void) changeFacebookSwitch:(UISwitch*)sender{
    
}

- (void) changeTwitterSwitch:(UISwitch*)sender{
    
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
