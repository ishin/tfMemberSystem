//
//  ForwardContactViewController.m
//  Hint
//
//  Created by jack on 3/11/17.
//  Copyright © 2016 jack. All rights reserved.
//

#import "ForwardContactViewController.h"
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
#import "HTopicCell.h"
#import "WaitDialog.h"
#import "SearchContactResultView.h"


@interface ForwardContactViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, MyRecContactsViewControllerDelegate>
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

@property (nonatomic, strong) NSMutableArray *conversationListDataSource;
@property (nonatomic, strong) RCConversationModel *_curSelectMode;

@end

@implementation ForwardContactViewController
@synthesize _friendsList;
@synthesize _personInfo;
@synthesize _headerDatas;
@synthesize _datas;

@synthesize conversationListDataSource;
@synthesize _selectedImages;
@synthesize _curSelectMode;


- (void) viewWillAppear:(BOOL)animated{
    
    self.navigationController.navigationBarHidden = NO;
    
}

- (void) dealloc
{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) backAction:(id)sender{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];

    self.title = @"发送到";
    
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
    
    
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    [self loadOrgData];
    
    [self layoutHeaderView];
    
    [self loadFriends];
}


- (void) loadOrgData{
    
    if(self.conversationListDataSource ==  nil)
        self.conversationListDataSource  = [NSMutableArray array];
    
    NSArray *arr =  [[RCIMClient sharedRCIMClient] getConversationList:@[@(ConversationType_PRIVATE),
                                                                         @(ConversationType_DISCUSSION),
                                                                         @(ConversationType_APPSERVICE),
                                                                         @(ConversationType_PUBLICSERVICE),
                                                                         @(ConversationType_GROUP)//@(ConversationType_SYSTEM)
                                                                         ]];
    [self.conversationListDataSource removeAllObjects];
    
    for(RCConversation *rc in arr)
    {
        if([rc.objectName isEqualToString:@"RC:ContactNtf"] || [rc.objectName isEqualToString:@"RC:CmdMsg"])
        {
            continue;
        }
        
        [conversationListDataSource addObject:rc];
    }

    
    [_tableView reloadData];
}
- (void) layoutHeaderView{
    
    int count = 6;
    int top = 0;
    
    self._headerDatas = [[NSMutableArray alloc] init];
    
    _headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 101)];
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
    
    _headerView.frame = CGRectMake(0, 0, SCREEN_WIDTH, count*50+1);
    
    
    top = count*50;
    
    UILabel* bgL = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                             top,
                                                             SCREEN_WIDTH, 1)];
    bgL.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    [_headerView addSubview:bgL];
    
    
    [_tableView reloadData];
    
}

- (void) viewRequest:(UIButton*)sender{
    
    if(sender.tag == 0)
    {
        MyGroupsViewController *myG = [[MyGroupsViewController alloc] init];
        myG.hidesBottomBarWhenPushed = YES;
        myG._forwardMsgs = _selectedImages;
        [self.navigationController pushViewController:myG animated:YES];
    }
    else if(sender.tag == 1)
    {
        MyRecContactsViewController *myRec = [[MyRecContactsViewController alloc] init];
        myRec.hidesBottomBarWhenPushed = YES;
        myRec._forwardMsgs = _selectedImages;
        [self.navigationController pushViewController:myRec animated:YES];
        
    }
}


#pragma mark UITableView dataSource
- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    HTopicCell *cell = [[HTopicCell alloc] initWithStyle:UITableViewCellStyleDefault
                                         reuseIdentifier:@"TopicCell"];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    if(indexPath.row < [self.conversationListDataSource count])
    {
        RCConversationModel *model = self.conversationListDataSource[indexPath.row];
        [cell fillData:model];
        
        cell._rowBtn.tag = indexPath.row;
        [cell._rowBtn addTarget:self action:@selector(rowClicked:) forControlEvents:UIControlEventTouchUpInside];
        //cell._rowBtn._actionObj = self;
        //cell._rowBtn._delAction = @selector(deleteConv:);
    }

    
    return cell;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [conversationListDataSource count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 70;
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return 50;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 50)];
    header.backgroundColor = [UIColor whiteColor];

    
    UILabel* tL = [[UILabel alloc] initWithFrame:CGRectMake(30,
                                                            0,
                                                            SCREEN_WIDTH-70, 50)];
    tL.backgroundColor = [UIColor clearColor];
    [header addSubview:tL];
    tL.font = [UIFont systemFontOfSize:15];
    tL.textAlignment = NSTextAlignmentLeft;
    tL.textColor  = COLOR_TEXT_A;
    tL.text = @"最近";
    
    
    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"normal_expand.png"]];
    [header addSubview:icon];
    icon.frame = CGRectMake(60, 5, 40, 40);
    icon.layer.contentsGravity = kCAGravityCenter;
    
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 49, SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [header addSubview:line];
    
    return header;
}


#pragma mark UITableView delegate


- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
      
}

- (void) rowClicked:(UIButton*)sender{
    
    if(sender.tag < [self.conversationListDataSource count])
    {
        RCConversationModel *model = self.conversationListDataSource[sender.tag];
        
        self._curSelectMode = model;
        

        if([_selectedImages count])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"发送到"
                                                            message:[NSString stringWithFormat:@"%@", model.conversationTitle]
                                                           delegate:self
                                                  cancelButtonTitle:@"取消"
                                                  otherButtonTitles:@"发送", nil];
            alert.tag = 201701;
            [alert show];
        }
        
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(alertView.tag == 201701 && buttonIndex != alertView.cancelButtonIndex)
    {
        for(RCMessage *msg in _selectedImages)
        {
            
            RCImageMessage *imgMsg = (RCImageMessage*)msg.content;
            
            NSString *_imageUrl = imgMsg.imageUrl;
            
            NSRange range = [_imageUrl rangeOfString:@"http"];
            if(range.location == NSNotFound)
            {
                imgMsg = [RCImageMessage messageWithImage:[UIImage imageWithContentsOfFile:_imageUrl]];
            }
            
            [[RCIMClient sharedRCIMClient] sendMediaMessage:_curSelectMode.conversationType
                                                   targetId:_curSelectMode.targetId
                                                    content:imgMsg
                                                pushContent:nil
                                                   pushData:nil
                                                   progress:^(int progress, long messageId) {
                                                       
                                                   } success:^(long messageId) {
                                                       
                                                       NSLog(@"200 OK");
                                                       
                                                   } error:^(RCErrorCode errorCode, long messageId) {
                                                       
                                                       NSLog(@"111");
                                                       
                                                   } cancel:^(long messageId) {
                                                       
                                                       NSLog(@"000");
                                                   }];
            
        }
    }
    
}

- (void) loginAction:(id)sender{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app switchLogin];
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
    
    
    IMP_BLOCK_SELF(ForwardContactViewController);
    
    
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
