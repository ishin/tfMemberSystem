//
//  ContactsViewController.m
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "ContactsViewController.h"
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
#import "OrgGraphicViewController.h"
#import "SearchContactResultView.h"


@interface ContactsViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
    UIView              *_headerView;
    
    SearchContactResultView *_resultView;
    
}
@property (nonatomic, strong) NSMutableArray *_friendsList;
@property (nonatomic, strong) NSDictionary *_personInfo;
@property (nonatomic, strong) NSMutableArray *_headerDatas;
@property (nonatomic, strong) NSMutableArray *_datas;


@end

@implementation ContactsViewController
@synthesize _friendsList;
@synthesize _personInfo;
@synthesize _headerDatas;
@synthesize _datas;


- (void) viewWillAppear:(BOOL)animated{
    

    self.navigationController.navigationBarHidden = NO;
    
    
}



- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];

    
    UIButton *scanBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    scanBtn.frame = CGRectMake(0, 0, 30, 40);
    [scanBtn setImage:[UIImage imageNamed:@"contact_organize.png"] forState:UIControlStateNormal];
    [scanBtn addTarget:self action:@selector(searchOrg:) forControlEvents:UIControlEventTouchDown];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:scanBtn];
    
    UIButton *imsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    imsBtn.frame = CGRectMake(0, 0, 40, 40);
    [imsBtn setTitle:@"IMS" forState:UIControlStateNormal];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:imsBtn];
    
    
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
    

    self._datas = [NSMutableArray array];
    
    
//    org = [[TeamOrg alloc] init];
//    org._teamName = @"天坊食品制造有限公司";
//    [_datas addObject:org];
//    
//    org = [[TeamOrg alloc] init];
//    org._teamName = @"天坊影视有限公司";
//    [_datas addObject:org];
    
    
    _resultView = [[SearchContactResultView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-114-44)];
    _resultView._ctrl = self;
    _resultView._isChooseModel = NO;
    
    
    
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-114-44)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    
    [self layoutHeaderView];
    
    [self loadFriends];
    
    [self loadOrgData];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(refreshFriends:)
                                                 name:@"NotifyRefreshMyContacts"
                                               object:nil];
  
}

- (void) refreshFriends:(NSNotification*)notify{
    
    [self loadFriends];
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


- (void) layoutHeaderView{
    
    int count = 6;
    int top = 0;
    
    self._headerDatas = [[NSMutableArray alloc] init];
    
    _headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 110)];
    _headerView.backgroundColor = [UIColor whiteColor];
    _tableView.tableHeaderView = _headerView;
    
    
    [_headerDatas addObject:@{@"name":@"我的群组",@"type":@"message",@"icon":@"icon_m_friends.png"}];
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
        
        UIImageView* logo = [[UIImageView alloc] initWithFrame:CGRectMake(14, top+8, 40, 40)];
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
    
    if(sender.tag == 0)
    {
        MyGroupsViewController *myG = [[MyGroupsViewController alloc] init];
        myG.hidesBottomBarWhenPushed = YES;
        [self.navigationController pushViewController:myG animated:YES];
    }
    else if(sender.tag == 1)
    {
        MyRecContactsViewController *myRec = [[MyRecContactsViewController alloc] init];
        myRec.hidesBottomBarWhenPushed = YES;
        [self.navigationController pushViewController:myRec animated:YES];
        
    }
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
    
    UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(20,
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

#pragma mark UITableView delegate


- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    TeamOrg *org = [_datas objectAtIndex:indexPath.row];
    

    TeamMembersViewController *team = [[TeamMembersViewController alloc] init];
    team.hidesBottomBarWhenPushed = YES;
    team._treeLevel = [NSMutableArray arrayWithObject:@{@"title":@"联系人首页",@"controller":self}];
    team._teamOrg = org;
    [self.navigationController pushViewController:team animated:YES];
    
}

- (void) loginAction:(id)sender{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app switchLogin];
}



- (void) searchOrg:(id)sender{
    
    if([_datas count])
    {
        OrgGraphicViewController *search = [[OrgGraphicViewController alloc] init];
        search.hidesBottomBarWhenPushed = YES;
        search._team = [_datas objectAtIndex:0];
        [self.navigationController pushViewController:search animated:YES];

    }
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
    
    
    IMP_BLOCK_SELF(ContactsViewController);
    
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        _isLoading = NO;
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                if(code == 1)
                {
                    [block_self showMyFriends:[v objectForKey:@"text"]];
                }
                else if(code == 0)
                {
                    [block_self showMyFriends:nil];
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
    
    if(list)
    {
        for(NSDictionary *dic in list)
        {
            WSUser *user = [[WSUser alloc] initWithDictionary:dic];
            [_friendsList addObject:user];
            
            [[GoGoDB sharedDBInstance] insertAFriend:dic];
        }
    }
    
    
   
}




- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    [_searchBar setShowsCancelButton:YES animated:YES];
    
    [self.view addSubview:_resultView];
    
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
    
    
    NSMutableArray *results = [NSMutableArray arrayWithArray:frs];
    NSMutableDictionary *map = [NSMutableDictionary dictionary];
    
    
    for(WSUser *uu in frs)
    {
        id key = [NSNumber numberWithInt:uu.userId];
        
        [map setObject:@"1" forKey:key];
    }
    
    NSArray *orgps = [[GoGoDB sharedDBInstance] searchOrgPersonsWithKeyword:keywords];
    
    for(WSUser *uu in orgps)
    {
        id key = [NSNumber numberWithInt:uu.userId];
        
        if(![map objectForKey:key])
        {
            
            [map setObject:@"1" forKey:key];
            
            [results addObject:uu];
        }
        
        
    }
    
    if([results count])
    {
        [self.view addSubview:_resultView];
    }

    
    
    _resultView._mapSelect = nil;
    _resultView._results = results;
    [_resultView refreshData];
    
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
