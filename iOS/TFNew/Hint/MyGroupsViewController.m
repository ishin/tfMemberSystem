//
//  MyGroupsViewController.m
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "MyGroupsViewController.h"
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
#import "ChatViewController.h"
#import "WSGroup.h"
#import "Utls.h"
#import "DataSync.h"
#import "JRCDSearchView.h"


@interface MyGroupsViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, JRCDSearchViewDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
    JRCDSearchView      *_searchView;
}
@property (nonatomic, strong) NSMutableArray *_myGroups;
@property (nonatomic, strong) NSMutableArray *_myJoinGroups;

@property (nonatomic, strong) WSGroup *_selectGroup;

@end

@implementation MyGroupsViewController
@synthesize _myGroups;
@synthesize _myJoinGroups;
@synthesize _forwardMsgs;

@synthesize _selectGroup;

- (void) viewWillAppear:(BOOL)animated{
    

    self.navigationController.navigationBarHidden = NO;
    
    [[DataSync sharedDataSync] syncMyGroups];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.navigationItem.title = @"我的群组";
    

    
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
    
    
    _searchView = [[JRCDSearchView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)];
    _searchView.delegate = self;
    _searchView._naviCtrl = self.navigationController;
        
    self._myGroups = [NSMutableArray array];
    self._myJoinGroups = [NSMutableArray array];
    
    User *my = [UserDefaultsKV getUser];
    NSArray *groups = [[GoGoDB sharedDBInstance] queryAllGroups];
    
    for(NSDictionary *dic in groups)
    {
        WSGroup *g = [[WSGroup alloc] initWithDictionary:dic];
        
        int cid = g.creator.userId;
        if(cid == [my._userId intValue])
        {
            [_myGroups addObject:g];
        }
        else
        {
            [_myJoinGroups addObject:g];
        }
        
    }

    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(notifyRefreshGroups:)
                                                 name:@"GroupsSyncMessagesNotify"
                                               object:nil];
  
}


- (void) notifyRefreshGroups:(NSNotification *)notify{
    
    self._myGroups = [NSMutableArray array];
    self._myJoinGroups = [NSMutableArray array];
    
    User *my = [UserDefaultsKV getUser];
    NSArray *groups = [[GoGoDB sharedDBInstance] queryAllGroups];
    
    for(NSDictionary *dic in groups)
    {
        WSGroup *g = [[WSGroup alloc] initWithDictionary:dic];
        
        int cid = g.creator.userId;
        if(cid == [my._userId intValue])
        {
            [_myGroups addObject:g];
        }
        else
        {
            [_myJoinGroups addObject:g];
        }
        
    }
    
    [_tableView reloadData];
}


#pragma mark UITableView dataSource
- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIndentifier = @"UserCell";
    UITableViewCell *cell = (UITableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1
                                      reuseIdentifier:CellIndentifier];
    }
    cell.accessoryType = UITableViewCellAccessoryNone;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    [[cell.contentView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    
    UIButton *btn = [UIButton buttonWithColor:[UIColor whiteColor] selColor:LINE_COLOR];
    [cell.contentView addSubview:btn];
    btn.tag = indexPath.section * 10000 + indexPath.row;
    btn.frame = CGRectMake(0, 0, SCREEN_WIDTH, 60);
    [btn addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
    
    if(indexPath.section == 0)
    {
        
        if(indexPath.row < [_myGroups count])
        {
            WSGroup *group = [_myGroups objectAtIndex:indexPath.row];
            
            UIImageView *actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(10, 8, 44, 44)];
            actorLogo.layer.cornerRadius = 22;
            actorLogo.clipsToBounds = YES;
            actorLogo.backgroundColor = [UIColor clearColor];
            actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
            [cell.contentView addSubview:actorLogo];
            
            NSString *gName = group.groupName;
            NSString *showName = @"";
            
            if([gName length] > 1)
                showName = [gName substringWithRange:NSMakeRange(1, 1)];
            else if([gName length] == 1)
                showName = gName;
            
            
            UILabel *mask = [[UILabel alloc] initWithFrame:actorLogo.bounds];
            [actorLogo addSubview:mask];
            mask.backgroundColor = [Utls groupMaskColorWithId:[group.groupId intValue]];
            mask.textColor = [UIColor whiteColor];
            mask.text = showName;
            mask.font = [UIFont boldSystemFontOfSize:24];
            mask.layer.cornerRadius = 22;
            mask.clipsToBounds = YES;
            mask.textAlignment = NSTextAlignmentCenter;
            
            [actorLogo setImageWithURL:[NSURL URLWithString:group.creator.avatarurl]];
            
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(60,
                                                                       10,
                                                                       SCREEN_WIDTH-60, 20)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:15];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = gName;
            
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
        }

        
    }
    else if(indexPath.section == 1)
    {
        
        if(indexPath.row < [_myJoinGroups count])
        {
            WSGroup *group = [_myJoinGroups objectAtIndex:indexPath.row];
            
            UIImageView *actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(10, 8, 44, 44)];
            actorLogo.layer.cornerRadius = 22;
            actorLogo.clipsToBounds = YES;
            actorLogo.backgroundColor = [UIColor clearColor];
            actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
            [cell.contentView addSubview:actorLogo];
            
            NSString *gName = group.groupName;
            NSString *showName = @"";
            
            if([gName length] > 1)
                showName = [gName substringWithRange:NSMakeRange(1, 1)];
            else if([gName length] == 1)
                showName = gName;
            
            UILabel *mask = [[UILabel alloc] initWithFrame:actorLogo.bounds];
            [actorLogo addSubview:mask];
            mask.backgroundColor = [Utls groupMaskColorWithId:[group.groupId intValue]];
            mask.textColor = [UIColor whiteColor];
            mask.text = showName;
            mask.font = [UIFont boldSystemFontOfSize:24];
            mask.layer.cornerRadius = 22;
            mask.clipsToBounds = YES;
            mask.textAlignment = NSTextAlignmentCenter;
            
            [actorLogo setImageWithURL:[NSURL URLWithString:group.creator.avatarurl]];
            
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(60,
                                                                       10,
                                                                       SCREEN_WIDTH-60, 20)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:15];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = gName;
            
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
        }
    }
    
    
    
    
    return cell;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if(section == 0)
        return [_myGroups count];
    
    return [_myJoinGroups count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 2;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60;
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{

    return 50;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 50)];
    header.backgroundColor = [UIColor whiteColor];
    
    UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(20, 0, SCREEN_WIDTH-20, 50)];
    tL.font = [UIFont systemFontOfSize:14];
    [header addSubview:tL];
    tL.textColor = COLOR_TEXT_A;

    if(section == 0)
    {
        tL.text = @"我建的组";
        
    }
    else if(section == 1)
    {
        
        tL.text = @"我加入的";
        
    }
    
    CGSize s = [tL.text sizeWithAttributes:@{NSFontAttributeName:tL.font}];
    float width = s.width;
    CGRect rc = tL.frame;
    rc.size.width = width+5;
    tL.frame = rc;
    
    UIImageView *expand = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"normal_expand.png"]];
    [header addSubview:expand];
    expand.center = CGPointMake(CGRectGetMaxX(rc)+10, 25);
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 49.5, SCREEN_WIDTH, 0.5)];
    line.backgroundColor = LINE_COLOR;
    [header addSubview:line];
    
    return header;
}

#pragma mark UITableView delegate


- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    
    
}

- (void) buttonAction:(UIButton*)sender{
    
    int section = (int)sender.tag / 10000;
    int row = (int)sender.tag % 10000;
 
    WSGroup *group = nil;
    if(section == 0)
    {
        if(row < [_myGroups count])
            group = [_myGroups objectAtIndex:row];
    }
    else
    {
        if(row < [_myJoinGroups count])
            group = [_myJoinGroups objectAtIndex:row];
    }
    
    if(group)
    {
        
        if([_forwardMsgs count])
        {
            self._selectGroup = group;
            
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"发送到"
                                                            message:[NSString stringWithFormat:@"%@", group.groupName]
                                                           delegate:self
                                                  cancelButtonTitle:@"取消"
                                                  otherButtonTitles:@"发送", nil];
            alert.tag = 201701;
            [alert show];
        }
        else
        {
            NSString *gid = group.groupId;
            
            ChatViewController *conversationVC = [[ChatViewController alloc]init];
            conversationVC.conversationType = ConversationType_GROUP;
            conversationVC.targetId = gid;
            conversationVC._userName = group.groupName;
            conversationVC.hidesBottomBarWhenPushed = YES;
            
            conversationVC._groupType = 1;
            conversationVC._targetUser = group;
            
            [self.navigationController pushViewController:conversationVC animated:YES];

        }
 
    }
    
   
}


- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(alertView.tag == 201701 && buttonIndex != alertView.cancelButtonIndex)
    {
        for(RCMessage *msg in _forwardMsgs)
        {
            
            RCImageMessage *imgMsg = (RCImageMessage*)msg.content;
            
            NSString *_imageUrl = imgMsg.imageUrl;
            
            NSRange range = [_imageUrl rangeOfString:@"http"];
            if(range.location == NSNotFound)
            {
                imgMsg = [RCImageMessage messageWithImage:[UIImage imageWithContentsOfFile:_imageUrl]];
            }
            
            [[RCIMClient sharedRCIMClient] sendMediaMessage:ConversationType_GROUP
                                                   targetId:_selectGroup.groupId
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



- (void) searchOrg:(id)sender{
    
//    SearchFriendsViewController *search = [[SearchFriendsViewController alloc] init];
//    search.hidesBottomBarWhenPushed = YES;
//    [self.navigationController pushViewController:search animated:YES];
//
    
    
}


- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    [_searchBar setShowsCancelButton:YES animated:YES];
    
    [self.view addSubview:_searchView];
    
}
- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    
    if([searchBar.text length] == 0)
    {
        [_searchBar setShowsCancelButton:NO animated:YES];
        
        if([_searchView superview])
            [_searchView removeFromSuperview];
    }
    
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    
    _searchBar.text=  @"";
    [_searchBar setShowsCancelButton:NO animated:YES];
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];
    
    if([_searchView superview])
        [_searchView removeFromSuperview];
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

- (void) doSearch:(NSString*)searchTxt{
    
    [_searchView searchGroupWithKeywords:searchTxt];
}

- (void) onSearchCancelClick{
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];

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
