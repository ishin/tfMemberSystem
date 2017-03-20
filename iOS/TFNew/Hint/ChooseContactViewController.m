//
//  ChooseContactViewController.m
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "ChooseContactViewController.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "MenuView.h"
#import "WSUser.h"
#import "ShootQRCode.h"
#import "GoGoDB.h"
#import "UserDefaultsKV.h"
#import "UIButton+Color.h"
#import "UILabel+ContentSize.h"
#import "UIImage+Color.h"
#import "MyGroupsViewController.h"
#import "MyRecContactsViewController.h"
#import "TeamMembersViewController.h"
#import "TeamOrg.h"
#import "WaitDialog.h"
#import "SearchContactResultView.h"


@interface ChooseContactViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, MyRecContactsViewControllerDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
    UIView              *_headerView;
    
    UIView              *_membsChoosedPannel;
    UIScrollView        *_membsScroll;
    UILabel             *_membsNumL;
    
    WebClient           *_client;
    
    SearchContactResultView *_resultView;
    
}
@property (nonatomic, strong) NSMutableArray *_friendsList;
@property (nonatomic, strong) NSDictionary *_personInfo;
@property (nonatomic, strong) NSMutableArray *_headerDatas;
@property (nonatomic, strong) NSMutableArray *_datas;

@property (nonatomic, strong) NSMutableArray *_membs;
@property (nonatomic, strong) NSMutableDictionary *_mapSelect;

@property (nonatomic, strong) NSMutableArray *_membsOfFriendSelect;

@end

@implementation ChooseContactViewController
@synthesize _friendsList;
@synthesize _personInfo;
@synthesize _headerDatas;
@synthesize _datas;
@synthesize _membsOfFriendSelect;
@synthesize _membs;
@synthesize _prevMembs;

@synthesize _mapSelect;
@synthesize _groupId;

- (void) viewWillAppear:(BOOL)animated{
    
    self.navigationController.navigationBarHidden = NO;
    
}

- (void) dealloc
{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) backAction:(id)sender{
    
    [self hiddenMembersPannel];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];

    
    
    UIButton *btnCancel = [UIButton buttonWithType:UIButtonTypeCustom];
    btnCancel.frame = CGRectMake(0, 0, 50, 40);
    [btnCancel setTitle:@"取消" forState:UIControlStateNormal];
    btnCancel.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnCancel addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnCancel setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnCancel];
   
    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
    backBtn.frame = CGRectMake(0, 0, 25, 44);
    [backBtn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    self.navigationItem.leftBarButtonItem = backBarButtonItem;
    
    UIView *footer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 44)];
    footer.backgroundColor = [UIColor whiteColor];
    
    
    UIView *colorLine = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 44)];
    colorLine.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    [footer addSubview:colorLine];
    
    _searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(5, 2, SCREEN_WIDTH-10, 40)];
    _searchBar.delegate = self;
    _searchBar.barTintColor = RGB(0xf2, 0xf2, 0xf2);
    //_searchBar.placeholder = @"Search";
    _searchBar.placeholder = @"搜索";
    _searchBar.backgroundColor = [UIColor clearColor];
    _searchBar.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _searchBar.searchBarStyle = UISearchBarStyleProminent;
    _searchBar.backgroundImage = [UIImage imageWithColor:RGB(0xf2, 0xf2, 0xf2) andSize:CGSizeMake(1, 1)];
    
    [footer addSubview:_searchBar];
    
    [self.view addSubview:footer];
    
    
    _resultView = [[SearchContactResultView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44-216)];
    
    

    self._datas = [NSMutableArray array];
    
    [self loadOrgData];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    self._membs = [NSMutableArray array];
    self._membsOfFriendSelect = [NSMutableArray array];
    self._mapSelect = [NSMutableDictionary dictionary];
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    _membsChoosedPannel = [[UIView alloc] initWithFrame:CGRectMake(0, SCREEN_HEIGHT, SCREEN_WIDTH, 125)];
    UIImageView *bg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"group_membs_pannel.png"]];
    [_membsChoosedPannel addSubview:bg];
    [app.window addSubview:_membsChoosedPannel];
    
    
    _membsScroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 50, SCREEN_WIDTH, 70)];
    _membsScroll.backgroundColor = [UIColor clearColor];
    [_membsChoosedPannel addSubview:_membsScroll];
    
    UIButton *btnGroupOK = [UIButton buttonWithType:UIButtonTypeCustom];
    btnGroupOK.frame = CGRectMake(SCREEN_WIDTH-120, 0, 120, 50);
    [_membsChoosedPannel addSubview:btnGroupOK];
    btnGroupOK.backgroundColor = [UIColor clearColor];
    [btnGroupOK addTarget:self action:@selector(createGroup:) forControlEvents:UIControlEventTouchUpInside];
    
    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"group_ensure.png"]];
    [btnGroupOK addSubview:icon];
    icon.center = CGPointMake(25, 30);
    
    _membsNumL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(icon.frame)+5, 10, 80, 40)];
    [btnGroupOK addSubview:_membsNumL];
    _membsNumL.text = @"完成（0）";
    _membsNumL.font = [UIFont systemFontOfSize:13];
    
    [self layoutHeaderView];
    
    
    [self loadFriends];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(refereshSelectPerson:)
                                                 name:@"NotifyRefreshContactSelection"
                                               object:nil];

}


- (void) loadOrgData{
    
    NSArray *tm1 = [[GoGoDB sharedDBInstance] queryOrgUnitsByPid:0];
    if([tm1 count])
    {
        NSDictionary *tm = [tm1 objectAtIndex:0];
        
        int pid = [[tm objectForKey:@"id"] intValue];
        
        TeamOrg *org = [[TeamOrg alloc] init];
        org._teamName = [tm objectForKey:@"name"];
        org._teamId = [[tm objectForKey:@"id"] intValue];
        org._teamPId = 0;
        org._levelIndex = 0;
        [_datas addObject:org];
        
        
        NSArray *tma = [[GoGoDB sharedDBInstance] queryOrgUnitsByPid:pid];
        
        NSMutableArray *membs = [NSMutableArray array];
        org._membs = membs;
        for(NSDictionary *dic in tma)
        {
            int flag = [[dic objectForKey:@"flag"] intValue];
            if(flag == 0)
            {
                TeamOrg *orgSub = [[TeamOrg alloc] init];
                orgSub._teamName = [dic objectForKey:@"name"];
                orgSub._teamPId = [[dic objectForKey:@"pid"] intValue];
                orgSub._teamId = [[dic objectForKey:@"id"] intValue];
                orgSub._levelIndex = org._levelIndex + 1;
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

- (void) showMembersPannel{
    
    [self showMembs];
    
    [UIView animateWithDuration:0.25
                     animations:^{
                         
                         _membsChoosedPannel.frame = CGRectMake(0, SCREEN_HEIGHT-125, SCREEN_WIDTH, 125);
                         
                     } completion:^(BOOL finished) {
                         
                     }];
}

- (void) hiddenMembersPannel{
    
    [UIView animateWithDuration:0.25
                     animations:^{
                         
                         _membsChoosedPannel.frame = CGRectMake(0, SCREEN_HEIGHT, SCREEN_WIDTH, 125);
                         
                     } completion:^(BOOL finished) {
                         
                     }];
}

- (void) showMembs{
    
    _membsNumL.text = [NSString stringWithFormat:@"完成（%d）", (int)[_membs count]];
    
    [[_membsScroll subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    int xx = 15;
    int w = 46;
    for(WSUser *u in _membs)
    {
        UIImageView *avatar = [[UIImageView alloc] initWithFrame:CGRectMake(xx, 5, w, w)];
        avatar.layer.cornerRadius = w/2;
        avatar.clipsToBounds = YES;
        avatar.layer.contentsGravity = kCAGravityResizeAspectFill;
        [_membsScroll addSubview:avatar];
        [avatar setImageWithURL:[NSURL URLWithString:u.avatarurl]
                         placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
        
        
        UILabel* tL = [[UILabel alloc] initWithFrame:CGRectMake(xx-10,
                                                                CGRectGetMaxY(avatar.frame)+2,
                                                                w+20, 20)];
        tL.backgroundColor = [UIColor clearColor];
        [_membsScroll addSubview:tL];
        tL.font = [UIFont systemFontOfSize:11];
        tL.textAlignment = NSTextAlignmentCenter;
        tL.textColor  = COLOR_TEXT_A;
        
        tL.text = u.fullname;
        
        xx+=w;
        xx+=12;
    }
    
    _membsScroll.contentSize = CGSizeMake(xx, _membsScroll.frame.size.height);
        
}

- (void) createGroup:(id)sender{
    
    
    if(_client == nil)
    {
        _client = [[WebClient alloc] initWithDelegate:self];
    }
    
    //API_CREATE_GROUP
    if(_client == nil)
        _client = [[WebClient alloc] initWithDelegate:self];
    
    _client._httpMethod = @"POST";
    
    User *u = [UserDefaultsKV getUser];
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    if(_groupId)
    {
         _client._method = API_JOIN_GROUP;
        
        [params setObject:_groupId forKey:@"groupid"];
    }
    else
    {
        _client._method = API_CREATE_GROUP;
        
        
        [params setObject:u._userId forKey:@"userid"];
        
    }
    
    NSString *groupids = [NSString stringWithFormat:@"[%@", u._userId];
    for(WSUser *uu in _membs)
    {
        groupids = [NSString stringWithFormat:@"%@,%d", groupids, uu.userId];
    }
    for(WSUser *uu in _prevMembs)
    {
        groupids = [NSString stringWithFormat:@"%@,%d", groupids, uu.userId];
    }
    
    groupids = [NSString stringWithFormat:@"%@]", groupids];
    
    [params setObject:groupids forKey:@"groupids"];
  
    

    _client._requestParam = params;
    
    
    
    IMP_BLOCK_SELF(ChooseContactViewController);
    
    /*
     {"code":"200","text":{"annexlong":0,"code":"G10_1485090443","createdate":"20170122","creatorId":10,"id":7,"listorder":0,"name":"N0001,jack1,jack2","notice":"","space":100,"spaceuse":0,"volume":1000,"volumeuse":3}}
     */
    
    
    [[WaitDialog sharedDialog] setTitle:@"加载中..."];
    [[WaitDialog sharedDialog] startLoading];
    
    [_client requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        _isLoading = NO;
        
        [[WaitDialog sharedDialog] endLoading];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 200)
                {
                    [block_self saveGroup:nil];
                }
                else
                {
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                                    message:@"出错了"
                                                                   delegate:nil
                                                          cancelButtonTitle:@"OK"
                                                          otherButtonTitles:nil, nil];
                    [alert show];
                }
                
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
        
        _isLoading = NO;
        
        [[WaitDialog sharedDialog] endLoading];
    }];
    
}

- (void) saveGroup:(NSDictionary*)group{

    [_membs removeAllObjects];

    [self hiddenMembersPannel];
    
    [self dismissViewControllerAnimated:YES completion:nil];
    
}

- (void) layoutHeaderView{
    
    int count = 6;
    int top = 0;
    
    self._headerDatas = [[NSMutableArray alloc] init];
    
    _headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 60)];
    _headerView.backgroundColor = [UIColor whiteColor];
    _tableView.tableHeaderView = _headerView;
    
    
    [_headerDatas addObject:@{@"name":@"常用联系人",@"type":@"tzrm",@"icon":@"icon_m_group.png"}];
    
    
    count = (int)[_headerDatas count];

    for(int i = 0; i < [_headerDatas count]; i++)
    {
        NSDictionary* item =  [_headerDatas objectAtIndex:i];
        
        UIButton *btnRow = [UIButton buttonWithColor:[UIColor clearColor] selColor:LINE_COLOR];
        btnRow.frame = CGRectMake(0, top, SCREEN_WIDTH, 50);
        [_headerView addSubview:btnRow];
        btnRow.tag = i;
        
        NSString *icon = [item objectForKey:@"icon"];
        
        UIImageView* logo = [[UIImageView alloc] initWithFrame:CGRectMake(20, top+8, 40, 40)];
        logo.layer.contentsGravity = kCAGravityCenter;
        [_headerView addSubview:logo];
        [logo setImage:[UIImage imageNamed:icon]];
        
        
        UILabel* tL = [[UILabel alloc] initWithFrame:CGRectMake(65,
                                                                top,
                                                                SCREEN_WIDTH-70, 50)];
        tL.backgroundColor = [UIColor clearColor];
        [_headerView addSubview:tL];
        tL.font = [UIFont systemFontOfSize:15];
        tL.textAlignment = NSTextAlignmentLeft;
        tL.textColor  = COLOR_TEXT_A;
        tL.numberOfLines = 2;
        
        
        tL.text = [item objectForKey:@"name"];
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, top+49, SCREEN_WIDTH, 1)];
        line.backgroundColor = LINE_COLOR;
        [_headerView addSubview:line];
        
        
        [btnRow addTarget:self action:@selector(viewRequest:) forControlEvents:UIControlEventTouchUpInside];
        
        
        top+=50;
        
    }
    
    _headerView.frame = CGRectMake(0, 0, SCREEN_WIDTH, count*50 + 10);
    
    
    top = count*50;
    
    UILabel* bgL = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                             top,
                                                             SCREEN_WIDTH, 10)];
    bgL.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    [_headerView addSubview:bgL];
    
    
    [_tableView reloadData];
    
}

- (void) viewRequest:(UIButton*)sender{
    
    MyRecContactsViewController *myRec = [[MyRecContactsViewController alloc] init];
    myRec.hidesBottomBarWhenPushed = YES;
    myRec._isChooseModel = YES;
    myRec.delegate_ = self;
    myRec._mapSelect = _mapSelect;
    [self.navigationController pushViewController:myRec animated:YES];
    
}


#pragma mark UITableView dataSource
- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIndentifier = @"UserCell";
    UITableViewCell *cell = (UITableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1
                                      reuseIdentifier:CellIndentifier];
    }
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    [[cell.contentView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    TeamOrg *org = [_datas objectAtIndex:indexPath.row];
    
    
    UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(32,
                                                               0,
                                                               SCREEN_WIDTH-60, 50)];
    nameL.backgroundColor = [UIColor clearColor];
    [cell.contentView addSubview:nameL];
    nameL.font = [UIFont systemFontOfSize:15];
    nameL.textAlignment = NSTextAlignmentLeft;
    nameL.textColor  = COLOR_TEXT_A;
    nameL.text = org._teamName;
    
    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", (int)[org._membs count]];
    cell.detailTextLabel.font = [UIFont systemFontOfSize:15];
    

    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = CGRectMake(0, 0, 100, 50);
    [cell.contentView addSubview:btn];
    [btn addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
    btn.tag = indexPath.row;
    
    UIImageView *select = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"add_c_normal.png"]];
    [cell.contentView addSubview:select];
    select.center = CGPointMake(18, 25);
  

    if(org._isSelect)
    {
        select.image = [UIImage imageNamed:@"add_c_selected.png"];
    }
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 49.5, SCREEN_WIDTH, 0.5)];
    line.backgroundColor = LINE_COLOR;
    [cell.contentView addSubview:line];
    
    return cell;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [_datas count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return 50;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 50)];
    header.backgroundColor = [UIColor whiteColor];

    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_org.png"]];
    [header addSubview:icon];
    icon.frame = CGRectMake(20, 5, 40, 40);
    icon.layer.contentsGravity = kCAGravityCenter;
    
    UILabel* tL = [[UILabel alloc] initWithFrame:CGRectMake(65,
                                                            0,
                                                            SCREEN_WIDTH-70, 50)];
    tL.backgroundColor = [UIColor clearColor];
    [header addSubview:tL];
    tL.font = [UIFont systemFontOfSize:15];
    tL.textAlignment = NSTextAlignmentLeft;
    tL.textColor  = COLOR_TEXT_A;
    tL.text = @"所有联系人";
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 49, SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [header addSubview:line];
    
    return header;
}


#pragma mark UITableView delegate


- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    TeamOrg *org = [_datas objectAtIndex:indexPath.row];
    

    TeamMembersViewController *team = [[TeamMembersViewController alloc] init];
    team.hidesBottomBarWhenPushed = YES;
    team._treeLevel = [NSMutableArray arrayWithObject:@{@"title":@"联系人首页", @"controller":self}];
    team._teamOrg = org;
    team._isChooseModel = YES;
    
    [self.navigationController pushViewController:team animated:YES];
    
}

- (void) loginAction:(id)sender{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app switchLogin];
}



- (void) doSetOrgSelect:(BOOL)sel team:(TeamOrg*)team{
    
    for(TeamOrg *t in team._membs)
    {
        if([t isKindOfClass:[TeamOrg class]])
        {
            t._isSelect = sel;
            
            [self doSetOrgSelect:sel team:t];
        }
        else
        {
            WSUser *uu = (WSUser*)t;
            uu._isSelect = sel;
        }
    }

}


- (void) querySelectUsers:(TeamOrg*)org{
    
    for(TeamOrg *t in org._membs)
    {
        if([t isKindOfClass:[WSUser class]])
        {
            WSUser *uu = (WSUser*)t;
            if(uu._isSelect)
            {
                id key = [NSNumber numberWithInt:uu.userId];
                if(![_mapSelect objectForKey:key])
                {
                    [_membs addObject:uu];
                }
            }
        }
        else
        {
            [self querySelectUsers:t];
        }
    }
}

- (void) prepareSelectData{
    
    [_membs removeAllObjects];
    if([_membsOfFriendSelect count])
    {
        [_membs addObjectsFromArray:_membsOfFriendSelect];
    }
    
    if([_datas count])
    {
        TeamOrg *org = [_datas objectAtIndex:0];
        
        [self querySelectUsers:org];
    }
    
    
    if([_membs count])
    {
        [self showMembersPannel];
    }
    else
    {
        [self hiddenMembersPannel];
    }
    
    [_tableView reloadData];
    
}

- (void) refereshSelectPerson:(NSNotification *)notify{
    
    WSUser *uu = notify.object;
    
    if(uu)
    {
        if([uu isKindOfClass:[WSUser class]])
        {
            id key = [NSNumber numberWithInt:uu.userId];
            
            if(uu._isSelect)
            {
                
                if(![_mapSelect objectForKey:key])
                {
                    if(uu.familiy == 1)
                    {
                        [_membsOfFriendSelect addObject:uu];
                    }
                    [_membs addObject:uu];
                    [_mapSelect setObject:uu forKey:key];
                    
                }
            }
            else
            {
                WSUser *obj = [_mapSelect objectForKey:key];
                [_membsOfFriendSelect removeObject:obj];
                [_membs removeObject:obj];
                [_mapSelect removeObjectForKey:key];
            }
            
        }
        
        if([_membs count])
        {
            [self showMembersPannel];
        }
        else
        {
            [self hiddenMembersPannel];
        }
        
        [_tableView reloadData];
    }
    else
    {
        [self prepareSelectData];
    }
    
}

- (void) buttonClicked:(UIButton*)sender{
    
    
    TeamOrg *org = [_datas objectAtIndex:sender.tag];
    if(org._isSelect)
    {
        org._isSelect = NO;
    }
    else
    {
        org._isSelect = YES;
    }
    
    
    [self doSetOrgSelect:org._isSelect team:org];
    
    [self prepareSelectData];
    
    
}

- (void) didChoosedPerson:(id)person{
    
    WSUser *uu = person;
    
    id key = [NSNumber numberWithInt:uu.userId];
    if(![_mapSelect objectForKey:key])
    {
        [_membsOfFriendSelect addObject:person];
        [_membs addObject:person];
        [_mapSelect setObject:person forKey:key];
    }

    
    if([_membs count])
    {
        [self showMembersPannel];
    }
    else
    {
        [self hiddenMembersPannel];
    }
}
- (void) didCancelChoosedPerson:(id)person{
    
    WSUser *uu = person;
    
    id key = [NSNumber numberWithInt:uu.userId];
    if([_mapSelect objectForKey:key])
    {
        WSUser *tmp = [_mapSelect objectForKey:key];
        [_membsOfFriendSelect removeObject:tmp];
        [_membs removeObject:tmp];
        
        [_mapSelect removeObjectForKey:key];
    }
    
    if([_membs count])
    {
        [self showMembersPannel];
    }
    else
    {
        [self hiddenMembersPannel];
    }
}


- (void) searchOrg:(id)sender{
    
 //
    
    
}




- (void) loadFriends{
    
    if(_isLoading)
        return;
    _isLoading = YES;
    
    //
    if(_http == nil)
        _http = [[WebClient alloc] initWithDelegate:self];
    
    _http._httpMethod = @"GET";
    _http._method = API_USER_FRINEDS;
    
    
    User *u = [UserDefaultsKV getUser];
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:u._account forKey:@"account"];
    _http._requestParam = params;
    
    
    IMP_BLOCK_SELF(ChooseContactViewController);
    
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        _isLoading = NO;
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSArray class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 1)
                {
                    
                    [block_self showMyFriends:[v objectForKey:@"text"]];
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
        
        _isLoading = NO;
    }];
}


- (void) showMyFriends:(NSArray *)list{
    
    self._friendsList = [[NSMutableArray alloc] init];
    
    [[GoGoDB sharedDBInstance] deleteFriends];
    
    for(NSDictionary *dic in list)
    {
        WSUser *user = [[WSUser alloc] initWithDictionary:dic];
        [_friendsList addObject:user];
        
        [[GoGoDB sharedDBInstance] insertAFriend:dic];
        
        ///[_membs addObject:user];
    }
    
   
}





- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    [_searchBar setShowsCancelButton:YES animated:YES];
    
}
- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    
    if([searchBar.text length] == 0)
    {
        [_searchBar setShowsCancelButton:NO animated:YES];
        
        if([_resultView superview])
            [_resultView removeFromSuperview];
    }
    
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    
    _searchBar.text=  @"";
    [_searchBar setShowsCancelButton:NO animated:YES];
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];
    
    if([_resultView superview])
        [_resultView removeFromSuperview];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    
    NSString *searchText = searchBar.text;
    
    if([searchText length] > 0)
    {
        
        [self doSearch:searchText];
        
    }

}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    
    
    if([searchText length] > 0)
    {
        
        [self doSearch:searchText];
        
    }
    
    
}

- (void) doSearch:(NSString*)keywords{
    
   
    NSArray *frs = [[GoGoDB sharedDBInstance] searchFriendsWithKeyword:keywords];
    
    if([frs count])
    {
        [self.view addSubview:_resultView];
    }
    
    
    NSMutableArray *results = [NSMutableArray arrayWithArray:frs];
    NSMutableDictionary *map = [NSMutableDictionary dictionary];
    
    
    for(WSUser *uu in frs)
    {
        id key = [NSNumber numberWithInt:uu.userId];
        if([_mapSelect objectForKey:key])
        {
            uu._isSelect = YES;
        }
        
        [map setObject:@"1" forKey:key];
    }
    
    NSArray *orgps = [[GoGoDB sharedDBInstance] searchOrgPersonsWithKeyword:keywords];
    
    for(WSUser *uu in orgps)
    {
        id key = [NSNumber numberWithInt:uu.userId];
        
        if(![map objectForKey:key])
        {
            if([_mapSelect objectForKey:key])
            {
                uu._isSelect = YES;
            }
            
            [map setObject:@"1" forKey:key];
            
            [results addObject:uu];
        }
        
        
    }
    
    _resultView._mapSelect = _mapSelect;
    _resultView._results = results;
    [_resultView refreshData];
    
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
