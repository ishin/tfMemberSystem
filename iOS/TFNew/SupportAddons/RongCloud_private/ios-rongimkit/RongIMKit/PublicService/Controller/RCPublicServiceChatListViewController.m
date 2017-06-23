//
//  RCPublicServiceChatListViewController.m
//  RongIMKit
//
//  Created by litao on 15/4/12.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import "RCPublicServiceChatListViewController.h"
#import "RCPublicServiceSearchViewController.h"
#import "RCPublicServiceSearchViewController.h"

@implementation RCPublicServiceChatListViewController

/**
 *  此处使用storyboard初始化，代码初始化当前类时*****必须要设置会话类型和聚合类型*****
 *
 *  @param aDecoder aDecoder description
 *
 *  @return return value description
 */
- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setup];
    }
    return self;
}

- (instancetype)init {
    self = [super init];

    if (self) {
        [self setup];
    }

    return self;
}

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];

    if (self) {
        [self setup];
    }

    return self;
}

- (void)setup {
    //设置要显示的会话类型
    [self setDisplayConversationTypes:@[ @(ConversationType_APPSERVICE), @(ConversationType_PUBLICSERVICE) ]];

    //分组会话类型
    //        [self setCollectionConversationType:@[@(ConversationType_PRIVATE)]];
}
- (void)viewDidLoad {
    [super viewDidLoad];

    //设置tableView样式
    // self.conversationListTableView.separatorColor = [UIColor colorWithHexString:@"dfdfdf" alpha:1.0f];
    self.conversationListTableView.tableFooterView = [UIView new];
    self.conversationListTableView.tableHeaderView =
        [[UIView alloc] initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 12)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];

    self.tabBarController.navigationItem.title = NSLocalizedStringFromTable(@"OfficialAccounts",@"RongCloudKit",nil)
    
;

    //自定义rightBarButtonItem
    UIButton *rightBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 19, 19)];
    [rightBtn setImage:[UIImage imageNamed:@"add"] forState:UIControlStateNormal];
    [rightBtn addTarget:self action:@selector(addPublicService:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithCustomView:rightBtn];
    [rightBtn setTintColor:[UIColor whiteColor]];
    self.tabBarController.navigationItem.rightBarButtonItem = rightButton;
}

/**
 *  点击进入会话页面
 *
 *  @param conversationModelType 会话类型
 *  @param model                 会话数据
 *  @param indexPath             indexPath description
 */
- (void)onSelectedTableRow:(RCConversationModelType)conversationModelType
         conversationModel:(RCConversationModel *)model
               atIndexPath:(NSIndexPath *)indexPath {
    //#warning add push public account conversation vc here!
    //    if (conversationModelType == ConversationModelType_Normal) {
    //        RCDChatViewController *_conversationVC = [[RCDChatViewController alloc]init];
    //        _conversationVC.conversationType = model.conversationType;
    //        _conversationVC.targetId = model.targetId;
    //        _conversationVC.targetName = model.conversationTitle;
    //        _conversationVC.title = model.conversationTitle;
    //        _conversationVC.conversation = model;
    //
    //        [self.navigationController pushViewController:_conversationVC animated:YES];
    //    }

    //聚合会话类型，此处自定设置。
    //    if (conversationModelType == ConversationModelType_Collection) {
    //
    //        RCDChatListViewController *temp = [[RCDChatListViewController alloc] init];
    //        NSArray *array = [NSArray arrayWithObject:[NSNumber numberWithInt:model.conversationType]];
    //        [temp setDisplayConversationTypes:array];
    //        [temp setCollectionConversationType:nil];
    //        [self.navigationController pushViewController:temp animated:YES];
    //    }
}

- (void)addPublicService:(id)sender {
    RCPublicServiceSearchViewController *searchFirendVC = [[RCPublicServiceSearchViewController alloc] init];
    [self.navigationController pushViewController:searchFirendVC animated:YES];
}
@end
