//
//  MsgSettingsViewController.m
//  Hint
//
//  Created by jack on 2/5/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "MsgSettingsViewController.h"
#import "EditPasswordViewController.h"
#import "UserDefaultsKV.h"
#import "MsgSettingsViewController.h"


@interface MsgSettingsViewController () <UITableViewDataSource, UITableViewDelegate>
{
    UITableView *_tableView;
}
@end

@implementation MsgSettingsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"新消息通知设置";
    
    
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
            
            UILabel* valueL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                       0,
                                                                       SCREEN_WIDTH-25, 60)];
            valueL.backgroundColor = [UIColor clearColor];
            [cell.contentView addSubview:valueL];
            valueL.font = [UIFont systemFontOfSize:16];
            valueL.textAlignment = NSTextAlignmentRight;
            valueL.textColor  = COLOR_TEXT_B;
            valueL.text = @"已开启";

            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
        }
        
    }
    else if(indexPath.section == 1)
    {
        cell.backgroundColor = [UIColor whiteColor];
        
        
        
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
            nameL.text = @"声音";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
            UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
            switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
            [switchCtrl setOn:[[NSUserDefaults standardUserDefaults] boolForKey:@"Voice_Switch_onoff"]];
            [switchCtrl addTarget:self action:@selector(changeVoiceSwitch:) forControlEvents:UIControlEventValueChanged];
            [cell.contentView addSubview:switchCtrl];
            
            //            int i_online_status = [[[NSUserDefaults standardUserDefaults] objectForKey:@"i_online_status"] intValue];
            //            if(i_online_status > 0)
            //            {
            //                [switchCtrl setOn:YES];
            //            }
            
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
            nameL.text = @"振动";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
            UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
            switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
            [switchCtrl setOn:[[NSUserDefaults standardUserDefaults] boolForKey:@"Shake_Switch_onoff"]];
            [switchCtrl addTarget:self action:@selector(changeShakeSwitch:) forControlEvents:UIControlEventValueChanged];
            [cell.contentView addSubview:switchCtrl];
            
            
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
            nameL.text = @"语言对讲提示音";
            
            UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
            line.backgroundColor = LINE_COLOR;
            [cell.contentView addSubview:line];
            
            UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
            switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
            [switchCtrl setOn:[[NSUserDefaults standardUserDefaults] boolForKey:@"Renmai_Switch"]];
            [switchCtrl addTarget:self action:@selector(changeRenmaiSwitch:) forControlEvents:UIControlEventValueChanged];
            [cell.contentView addSubview:switchCtrl];
            
            
        }
        
    }
    else if(indexPath.section == 2)
    {
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                   0,
                                                                   SCREEN_WIDTH-60, 60)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:16];
        nameL.textAlignment = NSTextAlignmentLeft;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = @"消息免打扰";
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 59.5, SCREEN_WIDTH, 0.5)];
        line.backgroundColor = LINE_COLOR;
        [cell.contentView addSubview:line];
        
        UISwitch *switchCtrl = [[UISwitch alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
        switchCtrl.tag = 201704;
        switchCtrl.center = CGPointMake(SCREEN_WIDTH - 50, 30);
        
        [switchCtrl addTarget:self action:@selector(changeRenmaiSwitch:) forControlEvents:UIControlEventValueChanged];
        [cell.contentView addSubview:switchCtrl];
        
        [switchCtrl setOn:NO];
        
        
        [[RCIMClient sharedRCIMClient] getNotificationQuietHours:^(NSString *startTime, int spansMin) {
            
            if(spansMin > 0)
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    switchCtrl.on = YES;
                });
            }
            
        } error:^(RCErrorCode status) {
            
        }];
    }
    
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if(section == 0)
        return 1;
    
    if(section == 1)
        return 3;
    
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
    return 40;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    
    if(section == 2)
        return 30;
    
    return 0;
}

- (nullable UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    
    if(section == 2)
    {
        UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 30)];
        header.backgroundColor = [UIColor clearColor];
        
        UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, SCREEN_WIDTH-20, 30)];
        tL.font = [UIFont systemFontOfSize:12];
        tL.text = @"设置消息的通知推送和声音提示";
        [header addSubview:tL];
        tL.textColor = COLOR_TEXT_2B;
        
        return header;
    }
    
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    if(section == 0)
        return nil;
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 40)];
    header.backgroundColor = [UIColor clearColor];
    
    if(section == 1)
    {
        UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, SCREEN_WIDTH-20, 30)];
        tL.font = [UIFont systemFontOfSize:12];
        tL.text = @"请在“设置”－“通知”中进行修改";
        [header addSubview:tL];
        tL.textColor = COLOR_TEXT_2B;
    }
    
    return header;
}

#pragma mark UITableView delegate



- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    
    if(indexPath.section == 0)
    {
        
        if(indexPath.row == 0)
        {
           
        }
        
    }
    else if(indexPath.section == 1)
    {
        
        
    }
    
    
}


- (void) changeVoiceSwitch:(UISwitch*)switchCtrl{
    
    [[NSUserDefaults standardUserDefaults] setObject:[NSNumber numberWithBool:switchCtrl.on]
                                              forKey:@"Voice_Switch_onoff"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}

- (void) changeShakeSwitch:(UISwitch*)switchCtrl{
    
    [[NSUserDefaults standardUserDefaults] setObject:[NSNumber numberWithBool:switchCtrl.on]
                                              forKey:@"Shake_Switch_onoff"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}


- (void) changeRenmaiSwitch:(UISwitch*)switchCtrl{
    
    if(switchCtrl.tag == 201704)
    {
        int mins = 24*60 - 1;
        //__weak typeof(&*self) blockSelf = self;
        if(switchCtrl.on)
        {
            [[RCIMClient sharedRCIMClient] setNotificationQuietHours:@"00:01:00"
                                                            spanMins:mins
                                                             success:^{
                                                             }
                                                               error:^(RCErrorCode status) {
                                                                   dispatch_async(dispatch_get_main_queue(), ^{
                                                                       UIAlertView *alert =
                                                                       [[UIAlertView alloc] initWithTitle:@"提示"
                                                                                                  message:@"设置失败"
                                                                                                 delegate:nil
                                                                                        cancelButtonTitle:@"取消"
                                                                                        otherButtonTitles:nil, nil];
                                                                       [alert show];
                                                                       switchCtrl.on = NO;
                                                                   });
                                                               }];
        }
        else
        {
            [[RCIMClient sharedRCIMClient] removeNotificationQuietHours:^{
                
            } error:^(RCErrorCode status) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    UIAlertView *alert =
                    [[UIAlertView alloc] initWithTitle:@"提示"
                                               message:@"关闭失败"
                                              delegate:nil
                                     cancelButtonTitle:@"取消"
                                     otherButtonTitles:nil, nil];
                    [alert show];
                    switchCtrl.on = YES;
                });
            }];
        }
    }
    
    
    
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
