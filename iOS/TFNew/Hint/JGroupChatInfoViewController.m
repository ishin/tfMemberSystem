//
//  JGroupChatInfoViewController.m
//  Hint
//
//  Created by jack on 9/8/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "JGroupChatInfoViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "UIButton+Color.h"
#import "GoGoDB.h"
#import "WSUser.h"
#import "EditNameViewController.h"
#import "WSGroup.h"
#import "ChooseContactViewController.h"
#import "CMNavigationController.h"
#import "RCDSearchHistoryMessageController.h"
#import "PhotosViewController.h"
#import "GMembersViewController.h"
#import "ChooseManagerViewController.h"
#import "WaitDialog.h"

@interface JGroupChatInfoViewController ()<UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>
{
    UITableView *_tableView;
  
    UIView      *_headerView;
    
    BOOL        _myIsGroupManager;
    
    WebClient  *_httpUser;
    
    WebClient  *_client;
}
@property (nonatomic, strong) NSMutableDictionary *_groupNameData;

@end

@implementation JGroupChatInfoViewController
@synthesize _groupNameData;
@synthesize _membs;
@synthesize _group;


- (void) viewWillAppear:(BOOL)animated
{
    //[self reloadPersonInfo];
    
    self.navigationController.navigationBarHidden = NO;
    
    [self syncGroupMembers];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.navigationItem.title = @"聊天信息";
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    self._groupNameData = [NSMutableDictionary dictionary];
    [_groupNameData setObject:@"群名称" forKey:@"title"];
    [_groupNameData setObject:API_GROUP_NAME forKey:@"action"];
    
    if(_group.groupId)
        [_groupNameData setObject:_group.groupId forKey:@"groupid"];
    
    if(_group)
        [_groupNameData setObject:_group.groupName forKey:@"value"];
    

    _myIsGroupManager = NO;
    User *my = [UserDefaultsKV getUser];
    if([my._userId intValue] == _group.creator.userId)
    {
        _myIsGroupManager = YES;
    }
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    _headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 100)];
    _tableView.tableHeaderView = _headerView;
    _headerView.backgroundColor = [UIColor whiteColor];
    
    

    [self reloadData];
    
}

- (void) showGroupMembers{
    
    [[_headerView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    
    int w = 56;
    int space = (SCREEN_WIDTH - 40 - w*4)/3;
    int xx = 20;
    int top = 10;
    int maxY = 0;
    
    for(int i = 0; i < [_membs count]; i++)
    {
        WSUser *uu = [_membs objectAtIndex:i];
        
        UIImageView *actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(xx, top, 56, 56)];
        actorLogo.layer.cornerRadius = 28;
        actorLogo.clipsToBounds = YES;
        actorLogo.backgroundColor = [UIColor clearColor];
        actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
        
        [_headerView addSubview:actorLogo];
        
        [actorLogo setImageWithURL:[NSURL URLWithString:uu.avatarurl]
                  placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
        
        
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(xx - 10,
                                                                   top+60,
                                                                   CGRectGetWidth(actorLogo.frame)+20,
                                                                   20)];
        nameL.backgroundColor = [UIColor clearColor];
        [_headerView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:15];
        nameL.textAlignment = NSTextAlignmentCenter;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = uu.fullname;
        
        xx = CGRectGetMaxX(actorLogo.frame)+space;
        
        if(i > 0 && (i+1)%4 == 0)
        {
            top = CGRectGetMaxY(nameL.frame)+10;
            xx = 20;
        }
        
        maxY = CGRectGetMaxY(nameL.frame);

    }
    
    int iNext = (int)[_membs count]+1;
    
    UIButton *btnAdd = [UIButton buttonWithType:UIButtonTypeCustom];
    btnAdd.frame = CGRectMake(xx, top, 56, 56);
    [btnAdd setImage:[UIImage imageNamed:@"chat_group_memb_add.png"] forState:UIControlStateNormal];
    [_headerView addSubview:btnAdd];
    [btnAdd addTarget:self action:@selector(addContactToGroupAction:) forControlEvents:UIControlEventTouchUpInside];
    
    xx = CGRectGetMaxX(btnAdd.frame)+space;
    if(iNext%4 == 0)
    {
        top = CGRectGetMaxY(btnAdd.frame)+24;
        xx = 20;
    }
    maxY = CGRectGetMaxY(btnAdd.frame)+24;
    
    
    if(_myIsGroupManager)
    {
        UIButton *btnReduce = [UIButton buttonWithType:UIButtonTypeCustom];
        btnReduce.frame = CGRectMake(xx, top, 56, 56);
        [btnReduce setImage:[UIImage imageNamed:@"contact_reduce.png"] forState:UIControlStateNormal];
        [_headerView addSubview:btnReduce];
        [btnReduce addTarget:self action:@selector(reduceContactToGroupAction:) forControlEvents:UIControlEventTouchUpInside];
        
        maxY = CGRectGetMaxY(btnReduce.frame)+24;
    }
    
    _headerView.frame = CGRectMake(0, 0, SCREEN_WIDTH, maxY+10);
    
    
    _tableView.tableHeaderView = _headerView;
    
    [_tableView reloadData];
    
    
    
//    UIButton *btnAdd = [UIButton buttonWithType:UIButtonTypeCustom];
//    btnAdd.frame = CGRectMake(CGRectGetMaxX(actorLogo.frame)+20, 10, 60, 60);
//    [btnAdd setImage:[UIImage imageNamed:@"chat_group_memb_add.png"] forState:UIControlStateNormal];
//    [cell.contentView addSubview:btnAdd];
//    [btnAdd addTarget:self action:@selector(addContactToGroupAction:) forControlEvents:UIControlEventTouchUpInside];

}

- (void) reloadData{
    
    [self showGroupMembers];
    
    [_tableView reloadData];
    
}


- (void) syncGroupMembers{
    
    if(_httpUser == nil)
    {
        _httpUser = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpUser._method = API_GROUP_MEMBS;
    _httpUser._httpMethod = @"GET";
    
    
    _httpUser._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                               self._group.groupId,@"groupid",
                               nil];
    
    
    IMP_BLOCK_SELF(JGroupChatInfoViewController);
    
    [_httpUser requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    NSArray *membs = [v objectForKey:@"text"];
                    [block_self groupMembers:membs];
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

- (void) groupMembers:(NSArray*)list{
    
    [self._membs removeAllObjects];
    
    for(NSDictionary *dic in list)
    {
        WSUser *uu = [[WSUser alloc] initWithDictionary:dic];
        [_membs addObject:uu];
    }
    
    [self showGroupMembers];
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
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"群名称";
            
            UILabel* valueL = [[UILabel alloc] initWithFrame:CGRectMake(90,
                                                                        0,
                                                                        SCREEN_WIDTH-100, 60)];
            valueL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:valueL];
            valueL.font = [UIFont systemFontOfSize:16];
            valueL.textAlignment = NSTextAlignmentRight;
            valueL.textColor  = COLOR_TEXT_B;
            valueL.text = [_groupNameData objectForKey:@"value"];
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else
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
            nameL.text = @"群主管理权转让";
        }
    }
    else if(indexPath.section == 1)
    {
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    
        
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
            nameL.text = @"聊天文件";
            
            
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
            nameL.text = @"查找聊天内容";
            
    
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }
        else if(indexPath.row == 2)
        {
            cell.accessoryType = UITableViewCellAccessoryNone;
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                       0,
                                                                       120, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"清空聊天记录";
            
            
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
            nameL.text = @"消息免打扰";
            
            
            UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
            switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
            [switchCtrl addTarget:self action:@selector(changeRenmaiSwitch:) forControlEvents:UIControlEventValueChanged];
            [cell.contentView addSubview:switchCtrl];
            [switchCtrl setOn:NO];
            
            NSString *targetId = _group.groupId;
            [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:ConversationType_GROUP
                                                                    targetId:targetId
                                                                     success:^(RCConversationNotificationStatus nStatus) {
                                                                         
                                                                         if(nStatus == DO_NOT_DISTURB)
                                                                         {
                                                                             dispatch_async(dispatch_get_main_queue(), ^{
                                                                                 switchCtrl.on = YES;
                                                                             });
                                                                         }
                                                                         
                                                                     } error:^(RCErrorCode status) {
                                                                         
                                                                     }];

            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59, SCREEN_WIDTH, 1)];
            [cell.contentView addSubview:line];
            line.backgroundColor = LINE_COLOR;
        }


    }
    else if(indexPath.section == 3)
    {
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                   0,
                                                                   SCREEN_WIDTH-30, 60)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:16];
        nameL.textAlignment = NSTextAlignmentCenter;
        nameL.textColor  = YELLOW_THEME_COLOR;
        nameL.text = @"删除并退出";
    }
    else if(indexPath.section == 4)
    {
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(15,
                                                                   0,
                                                                   SCREEN_WIDTH-30, 60)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:16];
        nameL.textAlignment = NSTextAlignmentCenter;
        nameL.textColor  = YELLOW_THEME_COLOR;
        nameL.text = @"解散该群";
    }
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if(section == 0)
    {
        if(_myIsGroupManager)
            return 2;
    }
    
    if(section == 1){
        
        return 3;
    }
    
    return 1;
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    if(_myIsGroupManager)
        return 5;
    
    return 4;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return 60;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return 20;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 30)];
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
            EditNameViewController *edt = [[EditNameViewController alloc] init];
            edt._rawData = _groupNameData;
            [self.navigationController pushViewController:edt animated:YES];
        }
        else
        {
            ChooseManagerViewController *edt = [[ChooseManagerViewController alloc] init];
            edt._groupId = _group.groupId;
            edt._datas = _membs;
            edt._gCreatorId = _group.creator.userId;
            [self.navigationController pushViewController:edt animated:YES];
        }
    }
    else if(indexPath.section == 1)
    {
        if(indexPath.row == 2)
        {
            [self clearCache];
        }
        else if(indexPath.row == 1)
        {
            RCDSearchHistoryMessageController *searchViewController = [[RCDSearchHistoryMessageController alloc] init];
            searchViewController.conversationType = ConversationType_GROUP;
            searchViewController.targetId = _group.groupId;
            [self.navigationController pushViewController:searchViewController animated:YES];
            
        }
        else if(indexPath.row == 0)
        {
            PhotosViewController *photos = [[PhotosViewController alloc] init];
            photos._targetId =_group.groupId;
            photos.converType = ConversationType_GROUP;
            [self.navigationController pushViewController:photos animated:YES];
            
        }
    }
    else if(indexPath.section == 3)
    {
        [self quitGroupAsk];
    }
    else if(indexPath.section == 4)
    {
        [self releaseGroupAsk];
    }
}


- (void) quitGroupAsk{
    
    //API_LEFT_GROUP
    UIActionSheet *as = [[UIActionSheet alloc] initWithTitle:nil
                                                    delegate:self
                                           cancelButtonTitle:@"取消"
                                      destructiveButtonTitle:nil
                                           otherButtonTitles:@"删除并退出群组",nil];
    as.tag = 201702;
    [as showInView:self.view];
}

- (void) releaseGroupAsk{
    
    UIActionSheet *as = [[UIActionSheet alloc] initWithTitle:nil
                                                    delegate:self
                                           cancelButtonTitle:@"取消"
                                      destructiveButtonTitle:nil
                                           otherButtonTitles:@"解散群组",nil];
    as.tag = 201703;
    [as showInView:self.view];
}



- (void) changeRenmaiSwitch:(UISwitch*)switchCtrl{
    
    NSString *targetId = _group.groupId;
    if(switchCtrl.on)
    {
        [[RCIMClient sharedRCIMClient] setConversationNotificationStatus:ConversationType_GROUP
                                                                targetId:targetId
                                                               isBlocked:YES
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];
    }
    else
    {
        [[RCIMClient sharedRCIMClient] setConversationNotificationStatus:ConversationType_GROUP
                                                                targetId:targetId
                                                               isBlocked:NO
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];
    }
}

- (void) clearCache{
    
    UIActionSheet *as = [[UIActionSheet alloc] initWithTitle:nil
                                                    delegate:self
                                           cancelButtonTitle:@"取消"
                                      destructiveButtonTitle:nil
                                           otherButtonTitles:@"清空聊天记录",nil];
    as.tag = 201701;
    [as showInView:self.view];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(actionSheet.tag == 201701)
    {
        if(buttonIndex == 0)
        {
            NSString *targetId = _group.groupId;
            [[RCIMClient sharedRCIMClient] clearMessages:ConversationType_PRIVATE targetId:targetId];
            
        }
    }
    else if(actionSheet.tag == 201702)
    {
        if(buttonIndex == 0)
        {
            [self quitGroup];
        }
    }
    else if(actionSheet.tag == 201703)
    {
        if(buttonIndex == 0)
        {
            [self releaseGroup];
        }
    }
    
}

- (void) addContactToGroupAction:(id)sender{
    
    NSMutableArray *prevMembs = [NSMutableArray array];
    [prevMembs addObjectsFromArray:_membs];
    
    ChooseContactViewController *choose = [[ChooseContactViewController alloc] init];
    choose._prevMembs = prevMembs;
    choose._groupId = _group.groupId;
    CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:choose];
    [self presentViewController:navi
                       animated:YES
                     completion:^{
                         
                     }];
}

- (void) reduceContactToGroupAction:(id)sender{
    
    GMembersViewController *gm = [[GMembersViewController alloc] init];
    gm._membs = _membs;
    gm._group = _group;
    [self.navigationController pushViewController:gm animated:YES];
    
}

- (void) quitGroup{
    
    if(_client == nil)
    {
        _client = [[WebClient alloc] initWithDelegate:self];
    }
    
    _client._method = API_LEFT_GROUP;
    _client._httpMethod = @"GET";
    
    User *my = [UserDefaultsKV getUser];
    
    
    _client._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                               self._group.groupId,@"groupid",
                               [NSString stringWithFormat:@"[%@]", my._userId], @"groupids",
                               nil];
    
    
    IMP_BLOCK_SELF(JGroupChatInfoViewController);
    
    [[WaitDialog sharedDialog] setTitle:@"退出群组..."];
    [[WaitDialog sharedDialog] startLoading];
    
    [_client requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        [[WaitDialog sharedDialog] endLoading];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                   // NSArray *membs = [v objectForKey:@"text"];
                    [block_self successDoneQuit];
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
}

- (void) successDoneQuit{
    
    [[RCIMClient sharedRCIMClient] removeConversation:ConversationType_GROUP
                                             targetId:_group.groupId];

    [self.navigationController popToRootViewControllerAnimated:YES];
    
}

- (void) releaseGroup{
    
    if(_client == nil)
    {
        _client = [[WebClient alloc] initWithDelegate:self];
    }
    
    _client._method = API_RELEASE_GROUP;
    _client._httpMethod = @"GET";
    
    User *my = [UserDefaultsKV getUser];
    
    
    _client._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                             self._group.groupId,@"groupid",
                             my._userId, @"userid",
                             nil];
    
    
    IMP_BLOCK_SELF(JGroupChatInfoViewController);
    
    [[WaitDialog sharedDialog] setTitle:@"解散群组..."];
    [[WaitDialog sharedDialog] startLoading];
    
    [_client requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        [[WaitDialog sharedDialog] endLoading];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    // NSArray *membs = [v objectForKey:@"text"];
                    [block_self successDoneQuit];
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
