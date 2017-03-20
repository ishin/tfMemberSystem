//
//  SettingsViewController.m
//  Hint
//
//  Created by jack on 2/5/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "SettingsViewController.h"
#import "EditPasswordViewController.h"
#import "UserDefaultsKV.h"
#import "MsgSettingsViewController.h"
#import "EditPasswordViewController.h"


@interface SettingsViewController () <UITableViewDataSource, UITableViewDelegate, UIActionSheetDelegate>
{
    UITableView *_tableView;
}
@end

@implementation SettingsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"设置";
    
    
    self.view.backgroundColor = RGB(0xf8, 0xf8, 0xf8);
   
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
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
        
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        
        if(indexPath.row == 0)
        {
    
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"新消息通知";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        else if(indexPath.row == 1)
        {
         
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"重置密码";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        else if(indexPath.row == 2)
        {

            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"自定义快捷键设置";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        
    }
    else if(indexPath.section == 1)
    {
        cell.backgroundColor = [UIColor whiteColor];
        
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
    
        if(indexPath.row == 0)
        {
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"清空聊天记录";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        else if(indexPath.row == 1)
        {
            
            cell.accessoryType = UITableViewCellAccessoryNone;
       
            
            
            UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                       0,
                                                                       SCREEN_WIDTH-60, 60)];
            nameL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:nameL];
            nameL.font = [UIFont systemFontOfSize:16];
            nameL.textAlignment = NSTextAlignmentLeft;
            nameL.textColor  = COLOR_TEXT_A;
            nameL.text = @"开启悬浮球";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
            
            UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
            switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
            [switchCtrl setOn:[[NSUserDefaults standardUserDefaults] boolForKey:@"xfq_on_off"]];
            [switchCtrl addTarget:self action:@selector(changeXFQSwitch:) forControlEvents:UIControlEventValueChanged];
            [cell.contentView addSubview:switchCtrl];
            
            int i_online_status = [[[NSUserDefaults standardUserDefaults] objectForKey:@"xfq_on_off"] intValue];
            if(i_online_status > 0)
            {
                [switchCtrl setOn:YES];
            }
        }
        
    }
    else if(indexPath.section == 2)
    {
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                   0,
                                                                   SCREEN_WIDTH-20, 60)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:16];
        nameL.textAlignment = NSTextAlignmentCenter;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = @"退出登录";
        nameL.textColor = RGB(0xff, 0xa2, 0x56);
    }
    
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if(section == 0)
        return 3;
    
    if(section == 1)
        return 2;
    
    return 1;
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 3;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
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
            MsgSettingsViewController *msgSet = [[MsgSettingsViewController alloc] init];
            [self.navigationController pushViewController:msgSet animated:YES];
        }
        else if(indexPath.row == 1)
        {
            EditPasswordViewController *edt = [[EditPasswordViewController alloc] init];
            edt._u = [UserDefaultsKV getUser];
            [self.navigationController pushViewController:edt animated:YES];
        }

    }
    else if(indexPath.section == 1)
    {
        if(indexPath.row == 0)
        {
            [self clearCache];
        }
        
    }
    else if(indexPath.section == 2)
    {
        
        User *u = [UserDefaultsKV getUser];
        if(!u)
        {
            AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
            [app switchLogin];
        }
        else
        {
            [self exitApp:nil];
        }
    }
   
    
}

- (void) changeRenmaiSwitch:(UISwitch*)switchCtrl{
    
    
    [[NSUserDefaults standardUserDefaults] setObject:[NSNumber numberWithBool:switchCtrl.on]
                                              forKey:@"i_online_status"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    
    [_tableView reloadData];
    
}

- (void) changeXFQSwitch:(UISwitch*)switchCtrl{
    
    
    [[NSUserDefaults standardUserDefaults] setObject:[NSNumber numberWithBool:switchCtrl.on]
                                              forKey:@"xfq_on_off"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    
    [_tableView reloadData];
    
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app checkXFQ];
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
        
        [self clearCacheFiles];
    }
   
}

//清理缓存
- (void)clearCacheFiles {
    
    NSArray *arr =  [[RCIMClient sharedRCIMClient] getConversationList:@[@(ConversationType_PRIVATE),
                                                                         @(ConversationType_DISCUSSION),
                                                                         @(ConversationType_APPSERVICE),
                                                                         @(ConversationType_PUBLICSERVICE),
                                                                         @(ConversationType_GROUP),
                                                                         @(ConversationType_SYSTEM)
                                                                         ]];
    for(RCConversation *model in arr)
    {
        
        [[RCIMClient sharedRCIMClient] clearMessages:model.conversationType targetId:model.targetId];
        [[RCIMClient sharedRCIMClient] removeConversation:model.conversationType targetId:model.targetId];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"Refresh_Unread_message_count"
                                                        object:nil];
    
    
    [self clearCacheSuccess];
    
    /*
    return;
    
    dispatch_async(
                   dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                       
                       //这里清除 Library/Caches 里的所有文件，融云的缓存文件及图片存放在 Library/Caches/RongCloud 下
                       NSString *cachPath = [NSSearchPathForDirectoriesInDomains(
                                                                                 NSCachesDirectory, NSUserDomainMask, YES) objectAtIndex:0];
                       NSArray *files =
                       [[NSFileManager defaultManager] subpathsAtPath:cachPath];
                       
                       for (NSString *p in files) {
                           NSError *error;
                           NSString *path = [cachPath stringByAppendingPathComponent:p];
                           if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
                               [[NSFileManager defaultManager] removeItemAtPath:path error:&error];
                           }
                       }
                       [self performSelectorOnMainThread:@selector(clearCacheSuccess)
                                              withObject:nil
                                           waitUntilDone:YES];
                   });
     */
}


- (void)clearCacheSuccess {
    UIAlertView *alertView =
    [[UIAlertView alloc] initWithTitle:nil
                               message:@"清理成功！"
                              delegate:nil
                     cancelButtonTitle:@"确定"
                     otherButtonTitles:nil, nil];
    [alertView show];
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
