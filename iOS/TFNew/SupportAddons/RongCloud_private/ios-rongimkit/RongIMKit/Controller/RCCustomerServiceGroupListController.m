//
//  RCCustomerServiceGroupListController.m
//  RongIMKit
//
//  Created by 张改红 on 16/7/19.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCCustomerServiceGroupListController.h"
#import "RCKitCommonDefine.h"
#import "RCCustomerServiceGroupCell.h"
#import <RongIMLib/RongIMLib.h>

#define CellIdentifier @"customerGroupCell"

@interface RCCustomerServiceGroupListController ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,assign)NSInteger currentIndex;
@property (nonatomic,strong)UIButton *isSureButton;
@end

@implementation RCCustomerServiceGroupListController
- (instancetype)init{
    self = [super init];
    if (self) {
        self.groupList = [NSArray array];
        self.tableView.delegate = self;
        self.tableView.dataSource = self;
        self.currentIndex = 0;
    }
    return self;
}
- (void)viewDidLoad{
    [super viewDidLoad];
    self.title = @"请选择咨询内容";
    [self.tableView registerClass:[RCCustomerServiceGroupCell class] forCellReuseIdentifier:CellIdentifier];
    self.tableView.tableFooterView = [UIView new];
//    设置分割线颜色
    self.tableView.separatorColor = HEXCOLOR(0xdfdfdf);
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsMake(0,45, 0, 0)];
    }
    UIButton *leftbtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    leftbtn.frame= CGRectMake(3, 0, 60, 44);
    leftbtn.titleLabel.font = [UIFont systemFontOfSize:17.0];
    leftbtn.contentHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    [leftbtn setTitle:NSLocalizedStringFromTable(@"Cancel", @"RongCloudKit", nil) forState:UIControlStateNormal];
    [leftbtn addTarget:self action:@selector(dismissController) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *btn_left = [[UIBarButtonItem alloc] initWithCustomView:leftbtn];
    UIBarButtonItem *negativeSpacer = [[UIBarButtonItem alloc]
                                       initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace
                                       target:nil action:nil];
    negativeSpacer.width = -7;
    self.navigationItem.leftBarButtonItems = [NSArray arrayWithObjects:negativeSpacer, btn_left, nil];

    _isSureButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    _isSureButton.frame= CGRectMake(0, 0, 40, 44);
    _isSureButton.titleLabel.font = [UIFont systemFontOfSize:17.0];
    _isSureButton.contentHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    [_isSureButton setTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit", nil) forState:UIControlStateNormal];
    [_isSureButton addTarget:self action:@selector(dismissByselectedGroup) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *btn_right = [[UIBarButtonItem alloc] initWithCustomView:_isSureButton];
    UIBarButtonItem *rightNegativeSpacer = [[UIBarButtonItem alloc]
                                       initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace
                                       target:nil action:nil];
    rightNegativeSpacer.width = -5;
    self.navigationItem.rightBarButtonItems = [NSArray arrayWithObjects:rightNegativeSpacer, btn_right, nil];
    _isSureButton.enabled = NO;
}

- (void)dismissController{
  self.selectGroupBlock(nil);
  [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

- (void)dismissByselectedGroup{
    RCCustomerServiceGroupItem * group = self.groupList[self.currentIndex];
    self.selectGroupBlock(group.groupId);
  [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    return self.groupList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    RCCustomerServiceGroupCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    if (cell==nil) {
        cell=[[RCCustomerServiceGroupCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    RCCustomerServiceGroupItem *item = self.groupList[indexPath.row];
    cell.groupName.text = item.name;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    RCCustomerServiceGroupCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selected = YES;
    _isSureButton.enabled = YES;
    self.currentIndex = indexPath.row;
}

- (void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath{
    RCCustomerServiceGroupCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selected = NO;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 44;
}


@end
