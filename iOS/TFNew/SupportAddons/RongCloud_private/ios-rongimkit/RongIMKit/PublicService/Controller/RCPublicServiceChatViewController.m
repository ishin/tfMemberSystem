//
//  RCPublicServiceChatViewController.m
//  RongIMKit
//
//  Created by litao on 15/6/12.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//
#import "RCPublicServiceChatViewController.h"
#import "RCMessageCellDelegate.h"

@interface RCPublicServiceChatViewController () <RCPublicServiceMessageCellDelegate>

@end

@implementation RCPublicServiceChatViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self notifyUpdateUnreadMessageCount];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)leftBarButtonItemPressed:(id)sender {
    //需要调用super的实现
    [super leftBarButtonItemPressed:sender];
    
    [self.navigationController popViewControllerAnimated:YES];
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
