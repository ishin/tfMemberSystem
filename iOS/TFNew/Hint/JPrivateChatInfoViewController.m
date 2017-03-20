//
//  JPrivateChatInfoViewController.m
//  Hint
//
//  Created by jack on 9/8/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "JPrivateChatInfoViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "UIButton+Color.h"
#import "GoGoDB.h"
#import "WSUser.h"
#import "ChooseContactViewController.h"
#import "CMNavigationController.h"
#import "UserInfoViewController.h"
#import "RCDSearchHistoryMessageController.h"
#import "PhotosViewController.h"


@interface JPrivateChatInfoViewController ()<UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>
{
    UITableView *_tableView;
  
}



@end

@implementation JPrivateChatInfoViewController

@synthesize _targetUser;



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
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    

    [self reloadData];
    
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
        
        UIImageView *actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(20, 10, 56, 56)];
        actorLogo.layer.cornerRadius = 28;
        actorLogo.clipsToBounds = YES;
        actorLogo.backgroundColor = [UIColor clearColor];
        actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;

        [cell.contentView addSubview:actorLogo];
        
        [actorLogo setImageWithURL:[NSURL URLWithString:_targetUser.avatarurl]
                  placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
        
        
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                   70,
                                                                   CGRectGetWidth(actorLogo.frame)+20,
                                                                   20)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:15];
        nameL.textAlignment = NSTextAlignmentCenter;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = _targetUser.fullname;
        
        UIButton *btnAdd = [UIButton buttonWithType:UIButtonTypeCustom];
        btnAdd.frame = CGRectMake(CGRectGetMaxX(actorLogo.frame)+20, 10, 60, 60);
        [btnAdd setImage:[UIImage imageNamed:@"chat_group_memb_add.png"] forState:UIControlStateNormal];
        [cell.contentView addSubview:btnAdd];
        [btnAdd addTarget:self action:@selector(addContactToGroupAction:) forControlEvents:UIControlEventTouchUpInside];
        
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
            nameL.text = @"清空聊天记录";
            
            
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
            nameL.text = @"消息免打扰";
            
            
            
            
            
            UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
            switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
            [switchCtrl addTarget:self action:@selector(changeRenmaiSwitch:) forControlEvents:UIControlEventValueChanged];
            [cell.contentView addSubview:switchCtrl];
            [switchCtrl setOn:NO];
            
            NSString *targetId = [NSString stringWithFormat:@"%d", self._targetUser.userId];
            [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:ConversationType_PRIVATE
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
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if(section == 0)
        return 1;
    
    return 2;
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 3;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if(indexPath.section == 0)
    {
        if(indexPath.row == 0)
            return 100;
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
        UserInfoViewController *uinfo = [[UserInfoViewController alloc] init];
        uinfo._user = _targetUser;
        [self.navigationController pushViewController:uinfo animated:YES];

    }
    else if(indexPath.section == 1)
    {
        if(indexPath.row == 1)
        {
            RCDSearchHistoryMessageController *searchViewController = [[RCDSearchHistoryMessageController alloc] init];
            searchViewController.conversationType = ConversationType_PRIVATE;
            searchViewController.targetId = [NSString stringWithFormat:@"%d", self._targetUser.userId];
            [self.navigationController pushViewController:searchViewController animated:YES];

        }
        else if(indexPath.row == 0)
        {
            
            PhotosViewController *photos = [[PhotosViewController alloc] init];
            photos._targetId =[NSString stringWithFormat:@"%d", self._targetUser.userId];
            photos.converType = ConversationType_PRIVATE;
            [self.navigationController pushViewController:photos animated:YES];
            
           
        }
    }
    else if(indexPath.section == 2)
    {
        if(indexPath.row == 0)
        {
            [self clearCache];
        }
    }
}


- (void) changeRenmaiSwitch:(UISwitch*)switchCtrl{
    
    NSString *targetId = [NSString stringWithFormat:@"%d", self._targetUser.userId];
    if(switchCtrl.on)
    {
        [[RCIMClient sharedRCIMClient] setConversationNotificationStatus:ConversationType_PRIVATE
                                                                targetId:targetId
                                                               isBlocked:YES
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];
    }
    else
    {
        [[RCIMClient sharedRCIMClient] setConversationNotificationStatus:ConversationType_PRIVATE
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
    [as showInView:self.view];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(buttonIndex == 0)
    {
        NSString *targetId = [NSString stringWithFormat:@"%d", self._targetUser.userId];
        [[RCIMClient sharedRCIMClient] clearMessages:ConversationType_PRIVATE targetId:targetId];
        
    }
    
}



- (void) addContactToGroupAction:(id)sender{
    
    NSMutableArray *prevMembs = [NSMutableArray array];
    [prevMembs addObject:_targetUser];
    
    ChooseContactViewController *choose = [[ChooseContactViewController alloc] init];
    choose._prevMembs = prevMembs;
    CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:choose];
    [self presentViewController:navi
                       animated:YES
                     completion:^{
                         
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
