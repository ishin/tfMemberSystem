//
//  MeViewController.m
//  Hint
//
//  Created by jack on 6/16/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "MeViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "ProfileViewController.h"
#import "UIButton+Color.h"
#import "ShootQRCode.h"
#import "EditPasswordViewController.h"
#import "MenuView.h"
#import "SettingsViewController.h"
#import "GoGoDB.h"
#import "SettingsViewController.h"
#import "SetUInfoViewController.h"
#import "TeamMembersViewController.h"
#import "TeamOrg.h"
#import "WSUser.h"


@interface MeViewController () <UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>
{
    UITableView *_tableView;
    
    MenuView *_menu;
    
    WebClient *_statusClient;
    
    WebClient *_personClient;
    
    UILabel *_alertPoint;
    
    UIImageView *_headerImgView;
    
    UIImageView* u_actorLogo;
    UILabel* u_nameL;
    UIImageView *u_gener;
    UIImageView *iconwhite;
    
    TeamOrg *_tfOrg;
}
@property (nonatomic, strong) User *_user;
@property (nonatomic, strong) NSDictionary *_personInfo;

@end

@implementation MeViewController

@synthesize _user;
@synthesize _personInfo;


- (void) viewWillAppear:(BOOL)animated
{
    User *u = [UserDefaultsKV getUser];
    if(u)
    {
        self._user = u;
        [self reloadData];
        
        [self reloadPersonInfo];
    
    }
    
    if(_tableView)
        [_tableView reloadData];
    
    if ([[[UIDevice currentDevice] systemVersion] compare:@"7.0" options:NSNumericSearch] != NSOrderedAscending)
    {
        if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
            self.navigationController.interactivePopGestureRecognizer.delegate = nil;
        }
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    

    self.view.backgroundColor = RGB(0xf8, 0xf8, 0xf8);
    
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];

    
    [self loadOrgData];
    
    //个人信息，头像，账号
    u_actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(10, 15, 60, 60)];
    u_actorLogo.layer.cornerRadius = 30;
    u_actorLogo.clipsToBounds = YES;
    u_actorLogo.backgroundColor = [UIColor clearColor];
    u_actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    u_actorLogo.image = [UIImage imageNamed:@"default_avatar.png"];
    
    
    u_nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(u_actorLogo.frame)+10,
                                                        15,
                                                        SCREEN_WIDTH-110, 60)];
    u_nameL.backgroundColor = [UIColor clearColor];
    u_nameL.font = [UIFont systemFontOfSize:16];
    u_nameL.textAlignment = NSTextAlignmentLeft;
    u_nameL.textColor  = COLOR_TEXT_A;
    
    
    
    u_gener = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"gender_male.png"]];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-50)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    UIButton *imsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    imsBtn.frame = CGRectMake(0, 0, 40, 40);
    [imsBtn setTitle:@"IMS" forState:UIControlStateNormal];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:imsBtn];

    
    
    
}


- (void) loadOrgData{
    
    NSArray *tm1 = [[GoGoDB sharedDBInstance] queryOrgUnitsByPid:0];
    if([tm1 count])
    {
        NSDictionary *tm = [tm1 objectAtIndex:0];
        
        int pid = [[tm objectForKey:@"id"] intValue];
        
        _tfOrg = [[TeamOrg alloc] init];
        _tfOrg._teamName = [tm objectForKey:@"name"];
        _tfOrg._teamId = [[tm objectForKey:@"id"] intValue];
        _tfOrg._teamPId = 0;
        _tfOrg._levelIndex = 0;
        
        NSArray *tma = [[GoGoDB sharedDBInstance] queryOrgUnitsByPid:pid];
        
        NSMutableArray *membs = [NSMutableArray array];
        _tfOrg._membs = membs;
        for(NSDictionary *dic in tma)
        {
            int flag = [[dic objectForKey:@"flag"] intValue];
            if(flag == 0)
            {
                TeamOrg *orgSub = [[TeamOrg alloc] init];
                orgSub._teamName = [dic objectForKey:@"name"];
                orgSub._teamPId = [[dic objectForKey:@"pid"] intValue];
                orgSub._teamId = [[dic objectForKey:@"id"] intValue];
                orgSub._levelIndex = _tfOrg._levelIndex + 1;
                [membs addObject:orgSub];
                
                [self queryTeamMembers:orgSub];
            }
            else
            {
                WSUser *uu = [[WSUser alloc] initWithDictionary:dic];
                [membs addObject:uu];
            }
        }
    }
    
    [_tableView reloadData];
}

- (void) queryTeamMembers:(TeamOrg *)team{
    
    NSArray *tma = [[GoGoDB sharedDBInstance] queryOrgUnitsByPid:team._teamId];
    
    NSMutableArray *membs = [NSMutableArray array];
    team._membs = membs;
    
    for(NSDictionary *dic in tma)
    {
        int flag = [[dic objectForKey:@"flag"] intValue];
        if(flag == 0)
        {
            TeamOrg *org = [[TeamOrg alloc] init];
            org._teamName = [dic objectForKey:@"name"];
            org._teamPId = [[dic objectForKey:@"pid"] intValue];
            org._teamId = [[dic objectForKey:@"id"] intValue];
            org._levelIndex = team._levelIndex + 1;
            [membs addObject:org];
            
            [self queryTeamMembers:org];
        }
        else
        {
            WSUser *uu = [[WSUser alloc] initWithDictionary:dic];
            [membs addObject:uu];
        }
    }
    
    
}

- (void) loginAction:(id)sender{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app switchLogin];
}


- (void) exitApp:(id)sender{
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                    message:@"确认要退出登录吗？"
                                                   delegate:self
                                          cancelButtonTitle:@"取消"
                                          otherButtonTitles:@"退出", nil];
    alert.tag = 2017;
    [alert show];
    
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(alertView.tag == 2017)
    {
        if(alertView.cancelButtonIndex != buttonIndex)
        {
            
            AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
            [app didLogout];
            
        }
    }
    else if(alertView.tag == 2018)
    {
        if(alertView.cancelButtonIndex != buttonIndex)
        {
            
            AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
            [app switchLogin];
            
        }
    }
    else if(alertView.tag == 2016)
    {
            }
    
}

- (void) reloadPersonInfo{
    
    User *u = [UserDefaultsKV getUser];
    if(!u)
        return;
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_USER_PROFILE;
    _http._httpMethod = @"GET";
    
    
    self._user = u;
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           u._userId,@"userid",
                           nil];
    
    
    IMP_BLOCK_SELF(MeViewController);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id res, BOOL *stop) {
            
            
            if([res isKindOfClass:[NSDictionary class]])
            {
                
                int code = [[res objectForKey:@"id"] intValue];
                
                if(code)
                {
                    [_user updateUserInfo:res];
                    
                    [block_self reloadData];
                    
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = u._userId;
                    user.name = u._userName;
                    user.portraitUri = u._avatar;
                    [[GoGoDB sharedDBInstance] saveUserInfo:user];
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
    
    
    [self refreshTable];
    
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
        cell.backgroundColor = [UIColor whiteColor];
        
        if(indexPath.row == 0)
        {
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
            
            
            [cell.contentView addSubview:u_actorLogo];
            [cell.contentView addSubview:u_nameL];
            [cell.contentView addSubview:u_gener];
        }
       
    }
    else if(indexPath.section == 1)
    {
        cell.backgroundColor = [UIColor whiteColor];
        
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        
        if(indexPath.row == 0)
        {
            UIImageView* logo = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_org.png"]];
            [cell.contentView addSubview:logo];
            logo.center = CGPointMake(40, 35);
            
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(logo.frame)+10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = _tfOrg._teamName;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        else if(indexPath.row > 0)
        {
            UIImageView* logo = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_dpt.png"]];
            [cell.contentView addSubview:logo];
            logo.center = CGPointMake(40, 30);
            
            TeamOrg *t = [_tfOrg._membs objectAtIndex:indexPath.row-1];
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(logo.frame)+10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = t._teamName;
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        
    }
    else if(indexPath.section == 2)
    {
        cell.backgroundColor = [UIColor whiteColor];
        
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        
        UIImageView* logo = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_setting.png"]];
        [cell.contentView addSubview:logo];
        logo.center = CGPointMake(40, 35);
        
        
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(logo.frame)+10,
                                                                   0,
                                                                   SCREEN_WIDTH-60, 60)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:16];
        nameL.textAlignment = NSTextAlignmentLeft;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = @"设置";
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
        line.backgroundColor = LINE_COLOR;
        [cell.contentView addSubview:line];
        
        line = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 0.5)];
        line.backgroundColor = LINE_COLOR;
        [cell.contentView addSubview:line];
        
    }
    
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
   
    if(section == 1)
    {
        
        if(_tfOrg)
        {
            return 1+[_tfOrg._membs count];
        }
        
        
        return 0;
    }
        //return 3;
    
    return 1;
   
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 3;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if(indexPath.section == 0)
        return 90;
    
    return 60;
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    if(section == 0)
        return 0;
    
    return 20;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    
    return 0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    if(section == 0)
        return nil;
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 20)];
    header.backgroundColor = [UIColor clearColor];
    return header;
}

#pragma mark UITableView delegate



- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    
    if(indexPath.section == 0)
    {
        
       if(indexPath.row == 0)
       {
           SetUInfoViewController *setu = [[SetUInfoViewController alloc] init];
           setu.hidesBottomBarWhenPushed = YES;
           [self.navigationController pushViewController:setu animated:YES];
       }
        
    }
    else if(indexPath.section == 1)
    {
        if(indexPath.row == 0){
            

            TeamMembersViewController *team = [[TeamMembersViewController alloc] init];
            team.hidesBottomBarWhenPushed = YES;
            team._treeLevel = [NSMutableArray arrayWithObject:@{@"title":@"我的", @"controller":self}];
            team._teamOrg = _tfOrg;
            [self.navigationController pushViewController:team animated:YES];

        }
        else
        {
            TeamOrg *t = [_tfOrg._membs objectAtIndex:indexPath.row-1];
            
            
            TeamMembersViewController *team = [[TeamMembersViewController alloc] init];
            team.hidesBottomBarWhenPushed = YES;
            team._treeLevel = [NSMutableArray arrayWithObject:@{@"title":@"我的", @"controller":self}];
            team._teamOrg = t;
            [self.navigationController pushViewController:team animated:YES];
            

        }
       
    }
    else if(indexPath.section == 2)
    {
       
        SettingsViewController *setting = [[SettingsViewController alloc] init];
        setting.hidesBottomBarWhenPushed = YES;
        [self.navigationController pushViewController:setting animated:YES];
    }
       
}

- (void) moreAction:(id)sender{
    
    User *u = [UserDefaultsKV getUser];
    if(!u)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                        message:@"您还没有登录，请先登录。"
                                                       delegate:self
                                              cancelButtonTitle:@"取消"
                                              otherButtonTitles:@"登录", nil];
        alert.tag = 2018;
        [alert show];
        
        return;
    }
    
    ProfileViewController *userc = [[ProfileViewController alloc] init];
    userc.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:userc animated:YES];
}



- (void) refreshTable{
    
    [u_actorLogo setImageWithURL:[NSURL URLWithString:_user._avatar]
                placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
    
    
    u_nameL.text = _user._userName;
    
    CGSize s = [u_nameL.text sizeWithAttributes:@{NSFontAttributeName:u_nameL.font}];
    
    u_gener.center = CGPointMake(CGRectGetMinX(u_nameL.frame)+s.width+20, u_nameL.center.y);
    
    if([_user.gender intValue] == 1)
    {
        u_gener.image = [UIImage imageNamed:@"gender_male.png"];
    }
    else
    {
        u_gener.image = [UIImage imageNamed:@"gender_female.png"];
    }
    
    [_tableView reloadData];
   
}


- (void) dealloc
{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
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
