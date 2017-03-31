//
//  UserInfoViewController.m
//  Hint
//
//  Created by jack on 9/8/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "UserInfoViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "UIButton+Color.h"
#import "GoGoDB.h"
#import "WSUser.h"
#import "ChatViewController.h"


@interface UserInfoViewController ()<UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITextFieldDelegate>
{
    UITableView *_tableView;
    
    UIImagePickerController *_imagePicker;
    
    UIImageView* _actorLogo;
    
    UIView *_footer;
    UIButton *_opBtn;
    
    WebClient *_httpChecker;
    WebClient *_opClient;
    
    UITextField *_uName;
    UITextField *_uEmail;
    UITextField *_uMobile;
    UITextField *_uTel;
    
    UITextField *_uCompany;
    UITextField *_uTitle;
    
    UIView *_maskView;
    UIImageView *_avatarView;
    
    int _shipStatus;
}
@property (nonatomic, strong) UIImage *_avataImg;
@property (nonatomic, strong) NSMutableArray *_datas;

@end

@implementation UserInfoViewController
@synthesize _avataImg;
@synthesize _user;
@synthesize _datas;
@synthesize _userId;
@synthesize _isFriend;

- (void) viewWillAppear:(BOOL)animated
{
    //[self reloadPersonInfo];
    
    self.navigationController.navigationBarHidden = NO;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.navigationItem.title = @"个人信息";
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(10, 10, 60, 60)];
    _actorLogo.layer.cornerRadius = 30;
    _actorLogo.clipsToBounds = YES;
    _actorLogo.backgroundColor = [UIColor clearColor];
    _actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    
    
    _shipStatus = 0;
    
    [self createField];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    

    _maskView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _maskView.backgroundColor = RGBA(0, 0, 0, 0.4);
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapped:)];
    tap.cancelsTouchesInView = NO;
    [_maskView addGestureRecognizer:tap];
    
    _avatarView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_WIDTH)];
    [_maskView addSubview:_avatarView];
    _avatarView.center = CGPointMake(SCREEN_WIDTH/2, (SCREEN_HEIGHT-64)/2-32);
    _avatarView.backgroundColor = RGB(56, 62, 98);
    _avatarView.layer.contentsGravity = kCAGravityResizeAspectFill;
    
    
    if(_user == nil && _userId)
    {
        [self reloadPersonInfo];
    }
    else
    {
         self._userId = [NSString stringWithFormat:@"%d", _user.userId];
        [self reloadData];
    }
    
   
    
    
    _footer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 80)];
    
    _opBtn = [UIButton buttonWithColor:THEME_RED_COLOR selColor:nil];
    _opBtn.frame = CGRectMake(22, 20, SCREEN_WIDTH-44, 40);
    _opBtn.layer.cornerRadius = 3;
    _opBtn.clipsToBounds = YES;
    [_footer addSubview:_opBtn];
    [_opBtn setTitle:@"发消息" forState:UIControlStateNormal];
    _opBtn.titleLabel.font = [UIFont boldSystemFontOfSize:16];
    [_opBtn addTarget:self action:@selector(chatWithMyFriend:) forControlEvents:UIControlEventTouchUpInside];
    
    
    [self checkIsFriends];
    
    
    //[self isFriend];
}

- (void) notFriend{
    
    _shipStatus = 405;
    
    _tableView.tableFooterView = nil;
    
    [self reloadData];
}
- (void) isFriend{
    
    _shipStatus = 200;
    
    User *u = [UserDefaultsKV getUser];
    if([u._userId intValue] != [_userId intValue])
    {
        _tableView.tableFooterView = _footer;
        
    }
    
    [self reloadData];
}

- (void) checkIsFriends{
    
    if(_httpChecker == nil)
    {
        _httpChecker = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpChecker._method = API_FRIENDS_SHIP;
    _httpChecker._httpMethod = @"GET";
    
    
    User *u = [UserDefaultsKV getUser];
    
    NSMutableDictionary *param = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                  u._authtoken,@"token",
                                  u._userId, @"userid",
                                  _userId, @"friendid",
                                  nil];
    
    _httpChecker._requestParam = param;
    
    
    
    
    IMP_BLOCK_SELF(UserInfoViewController);
    
    [_httpChecker requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                //int code = [[v objectForKey:@"code"] intValue];
                
                if([v objectForKey:@"text"])
                {
                    NSString* text = [v objectForKey:@"text"];
                    if([text isEqualToString:@"false"])
                    {
                        [block_self notFriend];
                    }
                    else if([text isEqualToString:@"true"])
                    {
                        [block_self isFriend];
                    }
                    
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


- (void) chatWithMyFriend:(id)sender{
    
    
    ChatViewController *conversationVC = [[ChatViewController alloc]init];
    conversationVC.conversationType = ConversationType_PRIVATE;
    conversationVC.targetId = _userId;
    conversationVC.title = @"聊天";
    //conversationVC.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:conversationVC animated:YES];
    
}

- (void) tapped:(id)sender{
    
    [_maskView removeFromSuperview];
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
    
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    _http._httpMethod = @"GET";
    _http._method = API_USER_PROFILE;
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           _userId, @"userid",
                           nil];
    
    
    IMP_BLOCK_SELF(UserInfoViewController);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    
                    block_self._user = [[WSUser alloc] initWithDictionary:v];
                   
                    
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
            
            //cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    
            
            [cell.contentView addSubview:_actorLogo];
            
            [_actorLogo setImageWithURL:[NSURL URLWithString:_user.avatarurl]
                       placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
            
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(80,
                                                                       14,
                                                                       SCREEN_WIDTH-90,
                                                                       56)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont boldSystemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = _user.fullname;
            
            User *u = [UserDefaultsKV getUser];
            if([u._userId intValue] != [_userId intValue])
            {
                
                if(_shipStatus == 200)
                {
                    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
                    btn.frame = CGRectMake(SCREEN_WIDTH - 120, 18, 114, 48);
                    [btn setImage:[UIImage imageNamed:@"delete_friend_btn.png"] forState:UIControlStateNormal];
                    [cell.contentView addSubview:btn];
                    [btn addTarget:self action:@selector(delContact:) forControlEvents:UIControlEventTouchUpInside];
                    
                    UILabel* btnL = [[UILabel alloc] initWithFrame:CGRectMake(40,
                                                                              0,
                                                                              114-40,
                                                                              40)];
                    btnL.backgroundColor = [UIColor clearColor];
                    [btn addSubview:btnL];
                    btnL.font = [UIFont systemFontOfSize:13];
                    btnL.textAlignment = NSTextAlignmentLeft;
                    //btnL.textColor  = COLOR_TEXT_A;
                    btnL.text = @"删联系人";
                }
                else if(_shipStatus == 405)
                {
                    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
                    btn.frame = CGRectMake(SCREEN_WIDTH - 120, 18, 114, 48);
                    [btn setImage:[UIImage imageNamed:@"add_friend_btn.png"] forState:UIControlStateNormal];
                    [cell.contentView addSubview:btn];
                    [btn addTarget:self action:@selector(addContact:) forControlEvents:UIControlEventTouchUpInside];
                    
                    UILabel* btnL = [[UILabel alloc] initWithFrame:CGRectMake(40,
                                                                              0,
                                                                              114-40,
                                                                              40)];
                    btnL.backgroundColor = [UIColor clearColor];
                    [btn addSubview:btnL];
                    btnL.font = [UIFont systemFontOfSize:13];
                    btnL.textAlignment = NSTextAlignmentLeft;
                    //btnL.textColor  = COLOR_TEXT_A;
                    btnL.text = @"加联系人";
                }
            }
        
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
            nameL.text = @"邮箱";
            
            
            [cell.contentView addSubview:_uEmail];
            _uEmail.text = _user.email;
            
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
            nameL.text = @"手机";
            
            
            [cell.contentView addSubview:_uMobile];
            _uMobile.text = _user.cellphone;
            
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
            nameL.text = @"电话";
            
            
            [cell.contentView addSubview:_uTel];
            _uTel.text = _user.telphone;
            
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
        return 3;
    
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
        NSString *str = _user.avatarurl;
        //str = [str stringByReplacingOccurrencesOfString:@"100x100.jpg" withString:@""];
        [_avatarView setImageWithURL:[NSURL URLWithString:str]];
        [self.view addSubview:_maskView];
        
       
        
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(alertView.tag == 201701)
    {
        if(alertView.cancelButtonIndex != buttonIndex)
        {
            [self addContactConfirm];
        }
    }
    else if(alertView.tag == 201702)
    {
        if(alertView.cancelButtonIndex != buttonIndex)
        {
            [self delContactConfirm];
        }
    }
    
}



- (void) addContact:(id)sender
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                    message:@"添加好友？"
                                                   delegate:self
                                          cancelButtonTitle:@"取消"
                                          otherButtonTitles:@"发送", nil];
    alert.tag = 201701;
    [alert show];
    
}

- (void) addContactConfirm{
    
    if(_opClient == nil)
    {
        _opClient = [[WebClient alloc] initWithDelegate:self];
    }
    
    _opClient._httpMethod = @"GET";
    _opClient._method = API_INVITE_FRIEND;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    User *u = [UserDefaultsKV getUser];
    
    
    [params setObject:u._account forKey:@"account"];
    [params setObject:_user.account forKey:@"friend"];
    
    IMP_BLOCK_SELF(UserInfoViewController);
    
    _opClient._requestParam = params;
    
    [_opClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    [block_self isFriend];
                    
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                                    message:@"已添加到常用联系人"
                                                                   delegate:nil
                                                          cancelButtonTitle:@"OK"
                                                          otherButtonTitles:nil, nil];
                    [alert show];
                    
                    [[NSNotificationCenter defaultCenter] postNotificationName:@"NotifyRefreshMyContacts" object:nil];
                }
                else
                {
                    [block_self notFriend];
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
        
        
        
    }];
}


- (void) delContact:(id)sender
{
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                    message:@"解除好友？"
                                                   delegate:self
                                          cancelButtonTitle:@"取消"
                                          otherButtonTitles:@"发送", nil];
    alert.tag = 201702;
    [alert show];
    
}

- (void) delContactConfirm{
    
    if(_opClient == nil)
    {
        _opClient = [[WebClient alloc] initWithDelegate:self];
    }
    
    _opClient._httpMethod = @"GET";
    _opClient._method = API_DEL_FRIEND;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    User *u = [UserDefaultsKV getUser];
    
    
    [params setObject:u._account forKey:@"account"];
    [params setObject:_user.account forKey:@"friend"];
    
    IMP_BLOCK_SELF(UserInfoViewController);
    
    _opClient._requestParam = params;
    
    [_opClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    [block_self notFriend];
                    
                    [block_self didDeleteFriend];
                    
                }
                else
                {
                    [block_self isFriend];
                }
                
                //[[NSNotificationCenter defaultCenter] postNotificationName:@"NotifyRefreshMyContacts" object:nil];
                
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
        
        
        
    }];
}

- (void) didDeleteFriend{
    
    [[GoGoDB sharedDBInstance] deleteFriendByUid:[NSString stringWithFormat:@"%d", _user.userId]];
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
