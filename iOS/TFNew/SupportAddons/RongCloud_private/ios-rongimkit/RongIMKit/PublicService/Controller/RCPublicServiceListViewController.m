//
//  RCPublicServiceListViewController.m
//  RongIMKit
//
//  Created by litao on 15/4/20.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCPublicServiceListViewController.h"
#import "RCPublicServiceListViewCell.h"
#import "RCConversationViewController.h"
#import <RongIMLib/RongIMLib.h>
#import "RCPublicServiceChatViewController.h"
#import "RCKitUtility.h"


@interface RCPublicServiceListViewController ()
//#字符索引对应的user object
@property(nonatomic, strong) NSMutableArray *tempOtherArr;
@property(nonatomic, strong) NSMutableArray *friends;
@property(nonatomic, strong) NSArray *keys;
@property(nonatomic, assign) BOOL hideSectionHeader;

@end

@implementation RCPublicServiceListViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;

    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    self.navigationItem.title = NSLocalizedStringFromTable(@"OfficialAccounts",@"RongCloudKit",nil);

    self.tableView.tableFooterView = [UIView new];
    [self setTitle:NSLocalizedStringFromTable(@"PublicService", @"RongCloudKit", nil)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self getAllData];
}

/**
 *  initial data
 */
- (void)getAllData {
    NSArray *result = [[RCIMClient sharedRCIMClient] getPublicServiceList];
    _friends = [NSMutableArray arrayWithArray:result];

    //如果
    if (_friends.count < 10) {
        self.hideSectionHeader = YES;
    }

    _keys = @[
        @"A",
        @"B",
        @"C",
        @"D",
        @"E",
        @"F",
        @"G",
        @"H",
        @"I",
        @"J",
        @"K",
        @"L",
        @"M",
        @"N",
        @"O",
        @"P",
        @"Q",
        @"R",
        @"S",
        @"T",
        @"U",
        @"V",
        @"W",
        @"X",
        @"Y",
        @"Z",
        @"#"
    ];
    _allFriends = [NSMutableDictionary new];
    _allKeys = [NSMutableArray new];
    //    [self removeSelectedUsers:_seletedUsers];

    //    static NSMutableDictionary *staticDic = nil;
    //    if (staticDic.count) {
    //        _allFriends = [NSMutableDictionary dictionaryWithDictionary:staticDic];
    //        return;
    //    }
    dispatch_async(dispatch_get_global_queue(0, 0), ^{

      _allFriends = [self sortedArrayWithPinYinDic:_friends];
      dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];

      });
    });
}
#pragma mark - 拼音排序

/**
 *  根据转换拼音后的字典排序
 *
 *  @param pinyinDic 转换后的字典
 *
 *  @return 对应排序的字典
 */
- (NSMutableDictionary *)sortedArrayWithPinYinDic:(NSArray *)friends {
    if (!friends)
        return nil;

    NSMutableDictionary *returnDic = [NSMutableDictionary new];
    _tempOtherArr = [NSMutableArray new];
    BOOL isReturn = NO;

    for (NSString *key in _keys) {

        if ([_tempOtherArr count]) {
            isReturn = YES;
        }

        NSMutableArray *tempArr = [NSMutableArray new];
        for (RCPublicServiceProfile *user in friends) {

            NSString *pyResult = [RCKitUtility getPinYinUpperFirstLetters:user.name];
            if(!pyResult || [pyResult length]<1)
                continue;
            NSString *firstLetter = [pyResult substringToIndex:1];
            if ([firstLetter isEqualToString:key]) {
                [tempArr addObject:user];
            }

            if (isReturn)
                continue;
            char c = [pyResult characterAtIndex:0];
            if (isalpha(c) == 0) {
                [_tempOtherArr addObject:user];
            }
        }
        if ([tempArr count])
            [returnDic setObject:tempArr forKey:key];
    }
    if ([_tempOtherArr count])
        [returnDic setObject:_tempOtherArr forKey:@"#"];

    _allKeys = [[returnDic allKeys] sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {

      return [obj1 compare:obj2 options:NSNumericSearch];
    }];

    return returnDic;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *reusableCellWithIdentifier = @"RCPublicServiceListViewCell";
    RCPublicServiceListViewCell *cell = [tableView dequeueReusableCellWithIdentifier:reusableCellWithIdentifier];
    if (!cell) {
        cell = [[RCPublicServiceListViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                                  reuseIdentifier:reusableCellWithIdentifier];
    }

    NSString *key = [_allKeys objectAtIndex:indexPath.section];
    NSArray *arrayForKey = [_allFriends objectForKey:key];

    RCPublicServiceProfile *user = arrayForKey[indexPath.row];
    if (user) {
        [cell setName:user.name];
        [cell setDescription:user.introduction];
        [cell.headerImageView setImageURL:[NSURL URLWithString:user.portraitUrl]];
    }

    return cell;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSString *key = [_allKeys objectAtIndex:section];

    NSArray *arr = [_allFriends objectForKey:key];

    return [arr count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {

    return [_allKeys count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 72.f;
}

// pinyin index
- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView {

    if (self.hideSectionHeader) {
        return nil;
    }
    return _allKeys;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (self.hideSectionHeader) {
        return nil;
    }

    NSString *key = [_allKeys objectAtIndex:section];
    return key;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *key = [_allKeys objectAtIndex:indexPath.section];
    NSArray *arrayForKey = [_allFriends objectForKey:key];

    RCPublicServiceProfile *user = arrayForKey[indexPath.row];
    if (user) {
        RCConversationViewController *_conversationVC = [[RCPublicServiceChatViewController alloc] init];
        _conversationVC.conversationType = (RCConversationType)user.publicServiceType;
        _conversationVC.targetId = user.publicServiceId;
        //接口向后兼容 [[++
        [_conversationVC performSelector:@selector(setUserName:) withObject:user.name];
        //接口向后兼容 --]]
        _conversationVC.title = user.name;
        [self.navigationController pushViewController:_conversationVC animated:YES];
    }
}

@end
