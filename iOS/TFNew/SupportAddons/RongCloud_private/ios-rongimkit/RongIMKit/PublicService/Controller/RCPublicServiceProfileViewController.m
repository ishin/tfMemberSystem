//
//  RCPublicServiceProfileViewController.m
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//
#import <objc/runtime.h>
#import "RCConversationViewController.h"
#import "RCPublicServiceProfileViewController.h"
#import "RCPublicServiceProfilePlainCell.h"
#import "RCPublicServiceProfileOwnerCell.h"
#import "RCPublicServiceProfileTelCell.h"
#import "RCPublicServiceProfileRcvdMsgCell.h"
#import "RCPublicServiceProfileUrlCell.h"
#import "RCPublicServiceProfileActionCell.h"
#import "RCPublicServiceViewConstants.h"
#import "RCConversationViewController.h"
#import "RCPublicServiceChatViewController.h"

#import "RCKitCommonDefine.h"
#import "RCloudImageView.h"
#import "RCKitUtility.h"
#import "RCExtensionUtility.h"

@interface RCPublicServiceProfileViewController () <UITableViewDataSource, UIActionSheetDelegate,
                                                 RCPublicServiceProfileViewUrlDelegate, RCPublicServiceProfileActionDelegate>
@property(nonatomic, strong) NSArray *cellCollections; // array of array of cells
@property(nonatomic, strong) RCPublicServiceProfileActionCell *actionCell;
@property(nonatomic, strong) RCPublicServiceProfileRcvdMsgCell *rcvdMsgCell;
@end

@implementation RCPublicServiceProfileViewController

// main section of PA, include descrptions, owner,
#define RC_PUBLIC_ACCOUNT_INFO_VIEW_SECTION_TYPE_MAIN 0
// Business scope
#define RC_PUBLIC_ACCOUNT_INFO_VIEW_SECTION_TYPE_BUSINESS 1
// receive msg or not
#define RC_PUBLIC_ACCOUNT_INFO_VIEW_SECTION_TYPE_MESSAGE_SETTING 2
// MSG histroy, location
#define RC_PUBLIC_ACCOUNT_INFO_VIEW_SECTION_TYPE_MORE 3
// follow or enter conversation, depend on follow status
#define RC_PUBLIC_ACCOUNT_INFO_VIEW_SECTION_TYPE_ACTION 4

- (instancetype)init {
    self = [super init];

    if (self) {
        [self setup];
    }

    return self;
}
- (void)onAction {
}
- (void)setup {
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.tableView setBackgroundColor:[UIColor groupTableViewBackgroundColor]];
}
- (void)viewDidLoad {
    [super viewDidLoad];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.tableView reloadData];
    self.tableView.backgroundColor = RGBCOLOR(248, 248, 248);
    self.tableView.tableFooterView = [UIView new];
}

- (void)setServiceProfile:(RCPublicServiceProfile *)serviceProfile {
    _serviceProfile = serviceProfile;

    //    _serviceProfile.descriptions=@"公司办公企业办办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业";
    //    _serviceProfile.owner=@"北京爱还是觉得快放办公企业办公企业办公企业办公企业办公企业办公企业办公企业办公企业假";
    //    _serviceProfile.ownerUrl=@"http://www.baidu.com";
    //    _serviceProfile.serviceTel=@"1234567891234";
    //    _serviceProfile.histroyMsgUrl=@"http://www.baidu.com";
    //    _serviceProfile.scope=@"互联网/软联网/软件开发";
    self.tableView.tableHeaderView = [self getTableViewHeader];
    //    if (_serviceProfile.followed) {
    //        UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithTitle:@"..." style:UIBarButtonItemStylePlain
    //        target:self action:@selector(onOptionButtonPressed)];
    //        self.navigationItem.rightBarButtonItem = item;
    //    }else
    //    {
    //        self.navigationItem.rightBarButtonItem = nil;
    //    }
    self.navigationItem.title = self.serviceProfile.name;
}

- (void)onOptionButtonPressed {
    UIActionSheet *actionSheet =
        [[UIActionSheet alloc] initWithTitle:nil
                                    delegate:self
                           cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel",@"RongCloudKit",nil)
                      destructiveButtonTitle:nil
                           otherButtonTitles:NSLocalizedStringFromTable(@"Share",@"RongCloudKit",nil), NSLocalizedStringFromTable(@"Report",@"RongCloudKit",nil),NSLocalizedStringFromTable(@"ClearHistory",@"RongCloudKit",nil),NSLocalizedStringFromTable(@"Unfollow",@"RongCloudKit",nil), nil];
    actionSheet.actionSheetStyle = UIActionSheetStyleBlackOpaque;
    [actionSheet showInView:self.view];
}

- (NSArray *)cellCollections {
    if (!_cellCollections) {
        NSMutableArray *collections = [[NSMutableArray alloc] init];
        NSMutableArray *mainSection = [[NSMutableArray alloc] init];

        if (self.serviceProfile.introduction || self.serviceProfile.owner || self.serviceProfile.publicServiceTel) { // for main
            if (self.serviceProfile.introduction) {
                RCPublicServiceProfilePlainCell *cell = [[RCPublicServiceProfilePlainCell alloc] init];

                [cell setTitle:NSLocalizedStringFromTable(@"Introduced",@"RongCloudKit",nil) Content:self.serviceProfile.introduction];
                cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, cell.bounds.size.width);
                [mainSection addObject:cell];
            }

            if (self.serviceProfile.owner) {
                RCPublicServiceProfileOwnerCell *cell = [[RCPublicServiceProfileOwnerCell alloc] init];
                [cell setTitle:NSLocalizedStringFromTable(@"AccountType",@"RongCloudKit",nil) Content:self.serviceProfile.owner url:self.serviceProfile.ownerUrl delegate:self];
                cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, cell.bounds.size.width);
                [mainSection addObject:cell];
            }

            if (self.serviceProfile.publicServiceTel) {
                RCPublicServiceProfileTelCell *cell = [[RCPublicServiceProfileTelCell alloc] init];
                [cell setTitle:NSLocalizedStringFromTable(@"ServicePhone",@"RongCloudKit",nil) Content:self.serviceProfile.publicServiceTel];
                [mainSection addObject:cell];
            }
        }

        if (self.serviceProfile.scope) { // for business
            // NSMutableArray *businessSection = [[NSMutableArray alloc] init];

            if (self.serviceProfile.introduction) {
                RCPublicServiceProfilePlainCell *cell = [[RCPublicServiceProfilePlainCell alloc] init];

                [cell setTitle:NSLocalizedStringFromTable(@"BusinessScope",@"RongCloudKit",nil) Content:self.serviceProfile.scope];
                [mainSection addObject:cell];
            }
        }

        if (self.serviceProfile.followed) { // for msg settings
            RCPublicServiceProfileRcvdMsgCell *cell = [[RCPublicServiceProfileRcvdMsgCell alloc] init];

            [cell setTitleText:NSLocalizedStringFromTable(@"NewMessageNotification",@"RongCloudKit",nil)];
            self.rcvdMsgCell = cell;
            cell.serviceProfile = self.serviceProfile;
            if (1) {
                __weak RCPublicServiceProfileViewController *weakSelf = self;
                [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:(RCConversationType)self.serviceProfile.publicServiceType targetId:self.serviceProfile.publicServiceId success:^(RCConversationNotificationStatus nStatus) {
                    BOOL enableNotification = NO;
                    if (nStatus == NOTIFY) {
                        enableNotification = YES;
                    }
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.rcvdMsgCell setOn:enableNotification];
                    });
                } error:^(RCErrorCode status) {
                    
                }];
            }

            [mainSection addObject:cell];
        }

        if (self.serviceProfile.histroyMsgUrl) {
            RCPublicServiceProfileUrlCell *cell = [[RCPublicServiceProfileUrlCell alloc] init];

            [cell setTitle:NSLocalizedStringFromTable(@"ViewHistory",@"RongCloudKit",nil) url:self.serviceProfile.histroyMsgUrl delegate:self];
            [mainSection addObject:cell];
        }
        [collections addObject:mainSection];
        NSMutableArray *actionSection = [[NSMutableArray alloc] init];
        RCPublicServiceProfileActionCell *cell = [[RCPublicServiceProfileActionCell alloc] init];

        if (self.serviceProfile.followed || self.serviceProfile.isGlobal) {
            [cell setTitleText:NSLocalizedStringFromTable(@"EnterOfficialAccount",@"RongCloudKit",nil) andBackgroundColor:RGBCOLOR(83, 213, 105)];
        } else {
            [cell setTitleText:NSLocalizedStringFromTable(@"Attention",@"RongCloudKit",nil) andBackgroundColor:RGBCOLOR(83, 213, 105)];
        }
        __weak typeof(self)weakSelf = self;
        cell.onClickEvent = ^{
          if (weakSelf.serviceProfile.followed || weakSelf.serviceProfile.isGlobal) {
              [weakSelf enterPublicServiceConversation];
          } else {
              [weakSelf subscribePublicService];
          }
        };
        self.actionCell = cell;
        [actionSection addObject:cell];

        RCPublicServiceProfileActionCell *unSubscribeCell = [[RCPublicServiceProfileActionCell alloc] init];

        if (self.serviceProfile.followed && !self.serviceProfile.isGlobal) {
            [unSubscribeCell setTitleText:NSLocalizedStringFromTable(@"Unfollow",@"RongCloudKit",nil) andBackgroundColor:RGBCOLOR(228, 54, 62)];
            unSubscribeCell.onClickEvent = ^{
              [weakSelf unsubscribePublicService];
            };
            [actionSection addObject:unSubscribeCell];
        }
        [collections addObject:actionSection];

        _cellCollections = collections;
    }
    return _cellCollections;
}

- (UIView *)getTableViewHeader {
    UIView *container = [[UIView alloc]
        initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, RCPublicServiceProfileHeaderPaddingTop +
                                                                       RCPublicServiceProfileHeaderImageHeigh +
                                                                       RCPublicServiceProfileHeaderPaddingBottom)];
    container.backgroundColor = [UIColor whiteColor];
    RCloudImageView *headerImageView = [[RCloudImageView alloc]
        initWithFrame:CGRectMake(RCPublicServiceProfileHeaderPaddingLeft, RCPublicServiceProfileHeaderPaddingTop,
                                 RCPublicServiceProfileHeaderImageWidth, RCPublicServiceProfileHeaderImageHeigh)];

    headerImageView.placeholderImage = IMAGE_BY_NAMED(@"default_portrait");
    [headerImageView setImageURL:[NSURL URLWithString:self.serviceProfile.portraitUrl]];
    headerImageView.layer.masksToBounds = YES;
    if (!self.portraitStyle) {
        headerImageView.layer.cornerRadius = 30;
    } else {
        if (_portraitStyle == RC_USER_AVATAR_RECTANGLE) {
            headerImageView.layer.cornerRadius = 4;
        } else if (_portraitStyle == RC_USER_AVATAR_CYCLE) {
            headerImageView.layer.cornerRadius = 30;
        }
    }

    CGFloat headlineFontSize = [UIFont preferredFontForTextStyle:UIFontTextStyleHeadline].lineHeight;
    CGFloat midLineHeigh = RCPublicServiceProfileHeaderPaddingTop + RCPublicServiceProfileHeaderImageHeigh / 2;
    UILabel *nameLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(RCPublicServiceProfileHeaderPaddingLeft + RCPublicServiceProfileHeaderImageWidth +
                                     RCPublicServiceProfileCellPaddingLeft,
                                 midLineHeigh - headlineFontSize,
                                 self.tableView.frame.size.width - RCPublicServiceProfileHeaderPaddingLeft -
                                     RCPublicServiceProfileHeaderImageWidth,
                                 headlineFontSize)];

    nameLabel.numberOfLines = 1;
    nameLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    nameLabel.font = [UIFont systemFontOfSize:RCPublicServiceProfileBigFont];
    nameLabel.textColor = [UIColor blackColor];
    nameLabel.text = self.serviceProfile.name;

    CGFloat subheadlineFontSize = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline].lineHeight;
    UILabel *userIdLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(RCPublicServiceProfileHeaderPaddingLeft + RCPublicServiceProfileHeaderImageWidth +
                                     RCPublicServiceProfileCellPaddingLeft,
                                 midLineHeigh + 5,
                                 self.tableView.frame.size.width - RCPublicServiceProfileHeaderPaddingLeft -
                                     RCPublicServiceProfileHeaderImageWidth,
                                 subheadlineFontSize)];
    userIdLabel.numberOfLines = 1;
    userIdLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    userIdLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
    userIdLabel.text = [NSString stringWithFormat:NSLocalizedStringFromTable(@"PublicNum",@"RongCloudKit",nil), self.serviceProfile.publicServiceId];
    userIdLabel.font = [UIFont systemFontOfSize:RCPublicServiceProfileSmallFont];
    userIdLabel.textColor = [UIColor grayColor];

    [container addSubview:nameLabel];
    [container addSubview:userIdLabel];
    [container addSubview:headerImageView];

    return container;
}
- (void)subscribePublicService {
    __weak RCPublicServiceProfileViewController *weakSelf = self;

    [RCExtensionUtility showProgressViewFor:self.tableView text:NSLocalizedStringFromTable(@"Wait",@"RongCloudKit",nil) animated:YES];
    [[RCIMClient sharedRCIMClient] subscribePublicService:self.serviceProfile.publicServiceType publicServiceId:self.serviceProfile.publicServiceId success:^{
        if (!weakSelf.serviceProfile.followed) {
            weakSelf.serviceProfile.followed = YES;
            dispatch_async(dispatch_get_main_queue(), ^{
                weakSelf.serviceProfile = weakSelf.serviceProfile;
                weakSelf.cellCollections = nil;
                [weakSelf cellCollections];
                [weakSelf.tableView reloadData];
            });
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [RCExtensionUtility hideProgressViewFor:weakSelf.tableView animated:YES];
        });
    } error:^(RCErrorCode status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [RCExtensionUtility hideProgressViewFor:weakSelf.tableView animated:YES];
        });
    }];
}
- (void)unsubscribePublicService {
    __weak RCPublicServiceProfileViewController *weakSelf = self;


    [RCExtensionUtility showProgressViewFor:self.tableView text:NSLocalizedStringFromTable(@"Wait",@"RongCloudKit",nil) animated:YES];
  
    [[RCIMClient sharedRCIMClient] unsubscribePublicService:self.serviceProfile.publicServiceType publicServiceId:self.serviceProfile.publicServiceId success:^{
        if (weakSelf.serviceProfile.followed) {
            weakSelf.serviceProfile.followed = NO;
            dispatch_async(dispatch_get_main_queue(), ^{
                if (!weakSelf.serviceProfile.followed) {
                    NSUInteger count = weakSelf.navigationController.viewControllers.count;
                    if (count > 1) {
                        UIViewController *preVC =
                        weakSelf.navigationController.viewControllers[count - 2];
                        if ([preVC isKindOfClass:[RCConversationViewController class]]) {
                            [weakSelf.navigationController popToRootViewControllerAnimated:YES];
                            return;
                        }
                    }
                }
                weakSelf.serviceProfile = weakSelf.serviceProfile;
                weakSelf.cellCollections = nil;
                [weakSelf cellCollections];
                [weakSelf.tableView reloadData];
            });
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [RCExtensionUtility hideProgressViewFor:weakSelf.tableView animated:YES];
        });
    } error:^(RCErrorCode status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [RCExtensionUtility hideProgressViewFor:weakSelf.tableView animated:YES];
        });
    }];
}
- (void)enterPublicServiceConversation {
    if (self.fromConversation) {
        [self.navigationController popViewControllerAnimated:YES];
    } else {
        RCConversationViewController *vc =
            [[RCPublicServiceChatViewController alloc] initWithConversationType:(RCConversationType)self.serviceProfile.publicServiceType
                                                                  targetId:self.serviceProfile.publicServiceId];
        [self.navigationController pushViewController:vc animated:YES];
    }
}
#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSArray *sections = self.cellCollections[section];
    return sections.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSArray *sections = self.cellCollections[indexPath.section];
    UITableViewCell *cell = sections[indexPath.row];
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];

    return cell;
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.cellCollections.count;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSArray *sections = self.cellCollections[indexPath.section];
    UITableViewCell *cell = sections[indexPath.row];
    if (cell.frame.size.height < 44)
        return 44;
    return cell.frame.size.height;
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 20;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (section == 0) {

        return nil;
    } else {
        UILabel *header = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 100, 20)];
        //[header setBackgroundColor:[UIColor grayColor]];
        return header;
    }
    return nil;
}
#pragma mark - UIActionSheetDelegate
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    //    if (buttonIndex == 0) {
    // NSLog(@"clicked button %ld", buttonIndex);
    //    }else if (buttonIndex == 1) {
    //        [self showAlert:@"第一项"];
    //    }else if(buttonIndex == 2) {
    //        [self showAlert:@"第二项"];
    //    }else if(buttonIndex == 3) {
    //        [self showAlert:@"取消"];
    //    }
    if (buttonIndex == 3) {
        [self unsubscribePublicService];
    }
}
- (void)willPresentActionSheet:(UIActionSheet *)actionSheet {
    for (UIView *subViwe in actionSheet.subviews) {
        if ([subViwe isKindOfClass:[UIButton class]]) {
            UIButton *button = (UIButton *)subViwe;
            DebugLog(@"button text is %@", button.titleLabel.text);
            //[button setTitleColor:[UIColor colorWithHexString:BLUE_GREEN_COLOR] forState:UIControlStateNormal];
        }
    }
}

#pragma mark - RCPublicServiceProfileViewUrlDelegate
- (void)gotoUrl:(NSString *)url {
  [RCKitUtility openURLInSafariViewOrWebView:url base:self];
}

@end
