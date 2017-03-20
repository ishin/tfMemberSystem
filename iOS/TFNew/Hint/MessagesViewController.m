//
//  MessagesViewController.m
//  Hint
//
//  Created by jack on 6/16/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "MessagesViewController.h"
#import "UserDefaultsKV.h"
#import "WebClient.h"
#import "SBJson4.h"
#import "UIButton+Color.h"
#import "HTopicCell.h"
#import "ChatViewController.h"
#import "ShootQRCode.h"
#import "MenuView.h"
#import "UserInfoViewController.h"
#import "GoGoDB.h"
#import "SearchContactViewController.h"
#import "ChooseContactViewController.h"
#import "CMNavigationController.h"
#import "UIImage+Color.h"
#import "RCDSearchViewController.h"
#import "RCPTTCallAcionView.h"


@interface MessagesViewController () <UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, RCDSearchViewDelegate, RCPTTCallAcionViewDelegate, TFCellEventViewDelegate>
{
    

    WebClient *_http;
    WebClient *_httpUser;
    
    UILabel *_numberL;
    MenuView *_menu;
    BOOL _isLoading;
    UITableView *_tableView;
    
    UIImageView *_noMsg;
    
    UISearchBar *_searchBar;
    
    RCPTTCallAcionView *_callAlert;
}
@property (nonatomic, strong) NSMutableArray *_friendsList;
@property (nonatomic, strong) NSArray *displayConversationTypeArray;
@property (nonatomic, strong) NSMutableArray *conversationListDataSource;
@property (nonatomic, strong) UINavigationController *searchNavigationController;

@end

@implementation MessagesViewController
@synthesize _friendsList;
@synthesize displayConversationTypeArray;
@synthesize conversationListDataSource;
@synthesize searchNavigationController;

- (void) viewWillAppear:(BOOL)animated
{
    if([UserDefaultsKV getUser])
    {

        [self updateBadgeValueForTabBarItem];
    }
    
    self.navigationController.navigationBarHidden = NO;
    
    [super viewWillAppear:animated];
    
    if(_callAlert == nil)
    {
        _callAlert = [[RCPTTCallAcionView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
        _callAlert.delegate_ = self;
    
    }
}
    

-(id)initWithCoder:(NSCoder *)aDecoder
{
    self =[super initWithCoder:aDecoder];
    if (self) {
  
        self.displayConversationTypeArray = @[@(ConversationType_PRIVATE),
                                              @(ConversationType_DISCUSSION),
                                              @(ConversationType_APPSERVICE),
                                              @(ConversationType_PUBLICSERVICE),
                                              @(ConversationType_GROUP)//@(ConversationType_SYSTEM)
                                              ];
        
        
    }
    return self;
}



- (void)updateBadgeValueForTabBarItem
{
    __weak typeof(self) __weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        int count = [[RCIMClient sharedRCIMClient] getUnreadCount:self.displayConversationTypeArray];
        if (count>0) {
            __weakSelf.tabBarItem.badgeValue = [[NSString alloc]initWithFormat:@"%d",count];
        }else
        {
            __weakSelf.tabBarItem.badgeValue = nil;
        }
        
    });
    
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"Refresh_Unread_message_count"
                                                        object:nil];
    
    [self refreshChatList:nil];
    
}

    
- (void) notifyPTTCall:(NSNotification*)notify{
    
    NSDictionary *data = notify.object;
    if(data)
    {
        _callAlert._data = data;
        NSString *userid = [data objectForKey:@"userid"];
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [_callAlert fillUser:userid];
            
            AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
            [app.window addSubview:_callAlert];
            [_callAlert animatedShow];
            
        });
        
        
        
    }
    else
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            
        [_callAlert stopAction:nil];
            
        });
        
       
    }
    
    
}

- (void) didTouchJCActionButtonIndex:(int)index{
    
    

    NSDictionary *dic = _callAlert._data;
    if(dic)
    {
        int type = [[dic objectForKey:@"type"] intValue];
        NSString *targetId = [dic objectForKey:@"targetId"];
        
        
        AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        [app switchAtTabIndex:0];
        
        [self.navigationController popToRootViewControllerAnimated:NO];
 
        ChatViewController *conversationVC = [[ChatViewController alloc]init];
        if(type == 1)
            conversationVC.conversationType = ConversationType_PRIVATE;
        else
            conversationVC.conversationType = ConversationType_GROUP;
        conversationVC.targetId = targetId;
        conversationVC._userName = @"";
        conversationVC.hidesBottomBarWhenPushed = YES;
        
        if(type == 1)
        {
            conversationVC._groupType = 1;
        }
        conversationVC._enterCallUI = YES;
        
        [self.navigationController pushViewController:conversationVC animated:YES];

    }
   
    
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];

    
    if(IOS7_OR_LATER){
        self.edgesForExtendedLayout = UIRectEdgeNone;
    }
 
    if([UserDefaultsKV getUser])
    {
        
        UIButton *scanBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        scanBtn.frame = CGRectMake(0, 0, 30, 40);
        [scanBtn setImage:[UIImage imageNamed:@"friend_add.png"] forState:UIControlStateNormal];
        [scanBtn addTarget:self action:@selector(searchFriend:) forControlEvents:UIControlEventTouchDown];
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:scanBtn];
        

        UIButton *imsBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        imsBtn.frame = CGRectMake(0, 0, 40, 40);
        [imsBtn setTitle:@"IMS" forState:UIControlStateNormal];
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:imsBtn];
        
        
        _noMsg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"no_message_img.png"]];
       
        
        self.view.backgroundColor = [UIColor whiteColor];
        
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-114)
                                                  style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.backgroundColor = [UIColor whiteColor];
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [self.view addSubview:_tableView];
 
        [self fillTableFooter];
        
        //设置要显示的会话类型
        self.displayConversationTypeArray = @[@(ConversationType_PRIVATE),
                                              @(ConversationType_DISCUSSION),
                                              @(ConversationType_APPSERVICE),
                                              @(ConversationType_PUBLICSERVICE),
                                              @(ConversationType_GROUP)//@(ConversationType_SYSTEM)
                                              ];
        
        
        [self refreshChatList:nil];
        
        [self reloadPersonInfo];
        
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(refreshChatList:)
                                                     name:@"Refresh_Chat_List"
                                                   object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(notifyPTTCall:)
                                                     name:@"PTT_call_notify"
                                                   object:nil];
        
    }
    else
    {
       
   }
    
    
   
    
}


- (void) fillTableFooter{
    
    
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

    _tableView.tableHeaderView = footer;
    
}


#pragma mark - UISearchBarDelegate
- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    [self.navigationController setNavigationBarHidden:YES animated:YES];
    RCDSearchViewController *searchViewController = [[RCDSearchViewController alloc] init];
    self.searchNavigationController = [[CMNavigationController alloc] initWithRootViewController:searchViewController];
    searchViewController.delegate = self;
    [self presentViewController:self.searchNavigationController animated:NO completion:nil];
    
   // [self.navigationController.view addSubview:self.searchNavigationController.view];
}

- (void)onSearchCancelClick{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.searchNavigationController dismissViewControllerAnimated:NO completion:nil];
        //[self.searchNavigationController removeFromParentViewController];
        [self.navigationController setNavigationBarHidden:NO animated:YES];
    });
}



- (void) loginAction:(id)sender{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app switchLogin];
    
}



- (void) searchFriend:(id)sender{
    
    if(_menu == nil)
    {
        __block MessagesViewController *bself = self;
        _menu = [[MenuView alloc] initWithFrame_Menu:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
        [_menu setMenuClickedBlock:^(NSInteger index) {
            
            [bself processSelectedItem:(int)index];
            
        }];
    }
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    [app.window addSubview:_menu];
    _menu.alpha = 0.0;
    
    [UIView beginAnimations:nil context:nil];
    _menu.alpha = 1.0;
    [UIView commitAnimations];
    
}


- (void) processSelectedItem:(int)index{
    
    
    if(index == 0)
    {
        ChooseContactViewController *choose = [[ChooseContactViewController alloc] init];
        CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:choose];
        [self presentViewController:navi
                           animated:YES
                         completion:^{
                             
                         }];
    }
    else if(index == 1)
    {
        SearchContactViewController *search = [[SearchContactViewController alloc] init];
        CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:search];
        [self presentViewController:navi
                           animated:YES
                         completion:^{
                             
                         }];
        
    }

}


- (void) shootCode:(id)sender{
    
    [[ShootQRCode sharedShootCodeInstance] showMyCode:self];
    
}



- (void) refreshChatList:(NSNotification *)notify{
    
    
    if(self.conversationListDataSource ==  nil)
        self.conversationListDataSource  = [NSMutableArray array];
    
    NSArray *arr =  [[RCIMClient sharedRCIMClient] getConversationList:displayConversationTypeArray];
    [self.conversationListDataSource removeAllObjects];
    
    for(RCConversation *rc in arr)
    {
        if([rc.objectName isEqualToString:@"RC:ContactNtf"] || [rc.objectName isEqualToString:@"RC:CmdMsg"])
        {
            continue;
        }
        
        [conversationListDataSource addObject:rc];
    }
    
    
    if([conversationListDataSource count] == 0)
    {
        [_tableView addSubview:_noMsg];
        _noMsg.center = CGPointMake(SCREEN_WIDTH/2, 200);
    }
    else
    {
        [_noMsg removeFromSuperview];
    }
    
    
    [_tableView reloadData];
    
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [conversationListDataSource count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return [self rcConversationListTableView:tableView cellForRowAtIndexPath:indexPath];
    
}

- (RCConversationBaseCell *)rcConversationListTableView:(UITableView *)tableView
                                  cellForRowAtIndexPath:(NSIndexPath *)indexPath;
{
    HTopicCell *cell = [[HTopicCell alloc] initWithStyle:UITableViewCellStyleDefault
                                         reuseIdentifier:@"TopicCell"];
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    if(indexPath.row < [self.conversationListDataSource count])
    {
        RCConversationModel *model = self.conversationListDataSource[indexPath.row];
        [cell fillData:model];
        
        cell._rowBtn.tag = indexPath.row;
        [cell._rowBtn addTarget:self action:@selector(rowClicked:) forControlEvents:UIControlEventTouchUpInside];
        cell._rowBtn._actionObj = self;
        //cell._rowBtn._delAction = @selector(deleteConv:);
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 70;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
   
    
}

- (void) rowClicked:(UIButton*)sender{
    
    if(sender.tag < [self.conversationListDataSource count])
    {
        RCConversationModel *model = self.conversationListDataSource[sender.tag];
        
        ChatViewController *conversationVC = [[ChatViewController alloc]init];
        conversationVC.conversationType = model.conversationType;
        conversationVC.targetId = model.targetId;
        conversationVC._userName = model.conversationTitle;
        conversationVC.hidesBottomBarWhenPushed = YES;
        
        if(model.conversationType == ConversationType_GROUP)
        {
            conversationVC._groupType = 1;
        }
        
        [self.navigationController pushViewController:conversationVC animated:YES];
        
    }
}

/*
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if(editingStyle == UITableViewCellEditingStyleDelete)
    {
        if(indexPath.row < [self.conversationListDataSource count])
        {
            RCConversationModel *model = self.conversationListDataSource[indexPath.row];
            [[RCIMClient sharedRCIMClient] removeConversation:model.conversationType targetId:model.targetId];
            [self refreshChatList:nil];
            
        }
    }
    
}
*/

- (void) didDeleteConversation:(int)tag{
    
    int tIdx = tag;
    if(tIdx < [self.conversationListDataSource count])
    {
        RCConversationModel *model = self.conversationListDataSource[tIdx];
        [[RCIMClient sharedRCIMClient] removeConversation:model.conversationType targetId:model.targetId];
        [self refreshChatList:nil];
        
    }
}
- (void) didSetMessageNotificationStatus:(int)tag{
    
     int tIdx = tag;
    if(tIdx < [self.conversationListDataSource count])
    {
        RCConversationModel *model = self.conversationListDataSource[tIdx];
        
        IMP_BLOCK_SELF(MessagesViewController);
   
        NSString *targetId = model.targetId;
        [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:model.conversationType
                                                                targetId:targetId
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                     if(nStatus == DO_NOT_DISTURB)
                                                                     {
                                                                        [block_self changeRenmaiSwitch:NO
                                                                                                  type:model.conversationType
                                                                                              targetId:targetId];
                                                                     }
                                                                     else
                                                                     {
                                                                         [block_self changeRenmaiSwitch:YES
                                                                                                   type:model.conversationType
                                                                                               targetId:targetId];
                                                                     }
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];
    }
}


- (void) changeRenmaiSwitch:(BOOL)on type:(RCConversationType)type targetId:(NSString*)targetId{
    
    IMP_BLOCK_SELF(MessagesViewController);
    
    [[RCIMClient sharedRCIMClient] setConversationNotificationStatus:type
                                                            targetId:targetId
                                                           isBlocked:on
                                                             success:^(RCConversationNotificationStatus nStatus) {
                                                                 
                                                                 [block_self refresh];
                                                                 
                                                             } error:^(RCErrorCode status) {
                                                                 
                                                             }];
}

- (void) refresh{
    
    
    [_tableView reloadData];
    
    //[_tableView setContentOffset:CGPointMake(0, 10) animated:YES];
    //[_tableView setContentOffset:CGPointMake(0, 0) animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void) reloadPersonInfo{
    
    if(_httpUser == nil)
    {
        _httpUser = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpUser._method = API_USER_PROFILE;
    _httpUser._httpMethod = @"GET";
    
    User *u = [UserDefaultsKV getUser];
    
    
    _httpUser._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                               u._userId,@"userid",
                               nil];
    
    
    // IMP_BLOCK_SELF(MessagesViewController);
    
    [_httpUser requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    u._avatar = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [v objectForKey:@"logo"]];
                    u._userName = [v objectForKey:@"name"];
                    
                    [u updateUserInfo:v];
                    
                    [UserDefaultsKV saveUser:u];
                    
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = u._userId;
                    user.name = u._userName;
                    user.portraitUri = u._avatar;
                    [[GoGoDB sharedDBInstance] saveUserInfo:user];
                    
                    //[block_self refreshConversationTableViewIfNeeded];
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



- (void) dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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
