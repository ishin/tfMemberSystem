//
//  RCConversationListViewController.m
//  RongIMKit
//
//  Created by xugang on 15/1/22.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCConversationCell.h"
#import "RCConversationCellUpdateInfo.h"
#import "RCConversationListViewController.h"
#import "RCConversationViewController.h"
#import "RCIM.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import "RCNetworkIndicatorView.h"

@interface RCConversationListViewController () <UITableViewDataSource, UITableViewDelegate,
                                                RCConversationCellDelegate>

@property(nonatomic, assign) BOOL isConverstaionListAppear;
@property(nonatomic, assign) BOOL isWaitingForForceRefresh;
@property(nonatomic, strong) UIView *connectionStatusView;
@property(nonatomic, strong) UIView *navigationTitleView;
@property(nonatomic, strong) dispatch_queue_t updateEventQueue;

@end

@implementation RCConversationListViewController

#pragma mark - 初始化
- (instancetype)initWithDisplayConversationTypes:(NSArray *)displayConversationTypeArray
                      collectionConversationType:(NSArray *)collectionConversationTypeArray {
  self = [super init];
  if (self) {
    self.displayConversationTypeArray = displayConversationTypeArray;
    self.collectionConversationTypeArray = collectionConversationTypeArray;
  }
  return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
  self = [super initWithCoder:aDecoder];
  if (self) {
    [self rcinit];
  }
  return self;
}

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
  self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
  if (self) {
    [self rcinit];
  }
  return self;
}

- (void)rcinit {
  self.updateEventQueue = dispatch_queue_create("cn.rongcloud.conversation.updateEventQueue", NULL);
  self.isConverstaionListAppear = NO;
  self.isEnteredToCollectionViewController = NO;
  self.isShowNetworkIndicatorView = YES;
  self.showConversationListWhileLogOut = YES;
  self.cellBackgroundColor = [UIColor whiteColor];
  self.topCellBackgroundColor = RGBCOLOR(0xf2, 0xfa, 0xff);
}

- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - View
- (void)viewDidLoad {
  [super viewDidLoad];

  if ([self respondsToSelector:@selector(setExtendedLayoutIncludesOpaqueBars:)]) {
    self.extendedLayoutIncludesOpaqueBars = YES;
  }
  self.conversationListDataSource = [[NSMutableArray alloc] init];
  self.conversationListTableView =
      [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
  self.conversationListTableView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
  if ([self.conversationListTableView respondsToSelector:@selector(setSeparatorInset:)]) {
    self.conversationListTableView.separatorInset = UIEdgeInsetsMake(0, 10, 0, 0);
  }
  if ([self.conversationListTableView respondsToSelector:@selector(setLayoutMargins:)]) {
    self.conversationListTableView.layoutMargins = UIEdgeInsetsMake(0, 10, 0, 0);
  }
  self.conversationListTableView.dataSource = self;
  self.conversationListTableView.delegate = self;
  [self.view addSubview:self.conversationListTableView];

  [self registerObserver];
}

- (void)viewWillAppear:(BOOL)animated {
  [super viewWillAppear:animated];

  [self updateNetworkIndicatorView];
  [self refreshConversationTableViewIfNeeded];
  self.isConverstaionListAppear = YES;
}

- (void)viewDidAppear:(BOOL)animated {
  [super viewDidAppear:animated];

  [self updateConnectionStatusView];
}

- (void)viewWillDisappear:(BOOL)animated {
  [super viewWillDisappear:animated];

  self.isConverstaionListAppear = NO;
  [self.conversationListTableView setEditing:NO];
}

#pragma mark - 监听
- (void)registerObserver {
  [[NSNotificationCenter defaultCenter]
      addObserver:self
         selector:@selector(onConnectionStatusChangedNotification:)
             name:RCKitDispatchConnectionStatusChangedNotification
           object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(didReceiveMessageNotification:)
                                               name:RCKitDispatchMessageNotification
                                             object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(didReceiveReadReceiptNotification:)
                                               name:RCLibDispatchReadReceiptNotification
                                             object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(didReceiveRecallMessageNotification:)
                                               name:RCKitDispatchRecallMessageNotification
                                             object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(refreshConversationTableViewIfNeeded)
                                               name:UIApplicationWillEnterForegroundNotification
                                             object:nil];
}

- (void)onConnectionStatusChangedNotification:(NSNotification *)status {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self updateConnectionStatusView];
    [self updateNetworkIndicatorView];
  });
}

- (void)didReceiveMessageNotification:(NSNotification *)notification {
  dispatch_async(self.updateEventQueue, ^{
    RCMessage *message = notification.object;
    int left = [notification.userInfo[@"left"] intValue];
    
    if (self.isConverstaionListAppear) {
      if (left >= 30) {
        //收大量消息的时候，等待left=0再强制刷新
        self.isWaitingForForceRefresh = YES;
      } else if (left == 0) {
        //left=0必须强制刷新，否则增量更新时如果在同时写数据库可能不准确
        self.isWaitingForForceRefresh = NO;
        [self refreshConversationTableViewIfNeeded];
      } else if (!self.isWaitingForForceRefresh) {
        if ([self.displayConversationTypeArray containsObject:@(message.conversationType)] &&
            [RCKitUtility isVisibleMessage:message]) {
          dispatch_async(dispatch_get_main_queue(), ^{
            RCConversationModel *matchingModel = nil;
            for (RCConversationModel *model in self.conversationListDataSource) {
              if ([model isMatching:message.conversationType targetId:message.targetId]) {
                matchingModel = model;
                break;
              }
            }
            
            if (matchingModel) {
              [matchingModel updateWithMessage:message];
              NSUInteger oldIndex = [self.conversationListDataSource indexOfObject:matchingModel];
              NSUInteger newIndex =
              [self getFirstModelIndex:matchingModel.isTop sentTime:matchingModel.sentTime];
              
              if (oldIndex == newIndex) {
                [self.conversationListTableView
                 reloadRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:newIndex inSection:0] ]
                 withRowAnimation:UITableViewRowAnimationAutomatic];
              } else {
                [self.conversationListDataSource removeObjectAtIndex:oldIndex];
                [self.conversationListDataSource insertObject:matchingModel atIndex:newIndex];
                
                [self.conversationListTableView beginUpdates];
                [self.conversationListTableView
                 deleteRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:oldIndex inSection:0] ]
                 withRowAnimation:UITableViewRowAnimationAutomatic];
                [self.conversationListTableView
                 insertRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:newIndex inSection:0] ]
                 withRowAnimation:UITableViewRowAnimationAutomatic];
                [self.conversationListTableView endUpdates];
              }
            } else {
              RCConversation *conversation =
              [[RCIMClient sharedRCIMClient] getConversation:message.conversationType
                                                    targetId:message.targetId];
              RCConversationModel *newModel =
              [[RCConversationModel alloc] initWithConversation:conversation extend:nil];
              if ([self.collectionConversationTypeArray containsObject:@(conversation.conversationType)]) {
                newModel.conversationModelType = RC_CONVERSATION_MODEL_TYPE_COLLECTION;
              }
              newModel.topCellBackgroundColor = self.topCellBackgroundColor;
              newModel.cellBackgroundColor = self.cellBackgroundColor;
              NSUInteger newIndex =
              [self getFirstModelIndex:newModel.isTop sentTime:newModel.sentTime];
              [self.conversationListDataSource insertObject:newModel atIndex:newIndex];
              [self.conversationListTableView
               insertRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:newIndex inSection:0] ]
               withRowAnimation:UITableViewRowAnimationAutomatic];
            }
            
            [self updateEmptyConversationView];
          });
        }
      }
    }
    
    if (left == 0) {
      [self notifyUpdateUnreadMessageCount];
    }

  });
}

- (void)didReceiveReadReceiptNotification:(NSNotification *)notification {
  dispatch_async(self.updateEventQueue, ^{
    RCConversationType conversationType =
    (RCConversationType)[notification.userInfo[@"cType"] integerValue];
    long long readTime = [notification.userInfo[@"messageTime"] longLongValue];
    NSString *targetId = notification.userInfo[@"tId"];
    NSString *senderUserId = notification.userInfo[@"fId"];
    
    if ([self.displayConversationTypeArray containsObject:@(conversationType)] &&
        [[RCIM sharedRCIM]
         .enabledReadReceiptConversationTypeList containsObject:@(conversationType)]) {
          dispatch_async(dispatch_get_main_queue(), ^{
            for (RCConversationModel *model in self.conversationListDataSource) {
              if ([model isMatching:conversationType targetId:targetId]) {
                if ([senderUserId isEqualToString:[RCIMClient sharedRCIMClient].currentUserInfo.userId]) {
                  if (model.unreadMessageCount != 0) {
                    int unreadMessageCount = model.unreadMessageCount;
                    if (model.lastestMessageDirection == MessageDirection_RECEIVE && model.sentTime <= readTime) {
                      unreadMessageCount = 0;
                    } else {
                      unreadMessageCount = [RCKitUtility getConversationUnreadCount:model];
                    }
                    
                    if (unreadMessageCount != model.unreadMessageCount) {
                      model.unreadMessageCount = unreadMessageCount;
                      RCConversationCellUpdateInfo *updateInfo = [[RCConversationCellUpdateInfo alloc] init];
                      updateInfo.model = model;
                      updateInfo.updateType = RCConversationCell_UnreadCount_Update;
                      [[NSNotificationCenter defaultCenter]
                       postNotificationName:RCKitConversationCellUpdateNotification
                       object:updateInfo
                       userInfo:nil];
                    }
                  }
                  
                  if (model.hasUnreadMentioned) {
                    BOOL hasUnreadMentioned = [RCKitUtility getConversationUnreadMentionedStatus:model];
                    
                    if (hasUnreadMentioned != model.hasUnreadMentioned) {
                      model.hasUnreadMentioned = hasUnreadMentioned;
                      RCConversationCellUpdateInfo *updateInfo = [[RCConversationCellUpdateInfo alloc] init];
                      updateInfo.model = model;
                      updateInfo.updateType = RCConversationCell_MessageContent_Update;
                      [[NSNotificationCenter defaultCenter]
                       postNotificationName:RCKitConversationCellUpdateNotification
                       object:updateInfo
                       userInfo:nil];
                    }
                  }
                } else {
                  if (model.lastestMessageDirection == MessageDirection_SEND &&
                      model.sentTime <= readTime && model.sentStatus != SentStatus_READ) {
                    model.sentStatus = SentStatus_READ;
                    RCConversationCellUpdateInfo *updateInfo = [[RCConversationCellUpdateInfo alloc] init];
                    updateInfo.model = model;
                    updateInfo.updateType = RCConversationCell_SentStatus_Update;
                    [[NSNotificationCenter defaultCenter]
                     postNotificationName:RCKitConversationCellUpdateNotification
                     object:updateInfo
                     userInfo:nil];
                  }
                }
                
                [self notifyUpdateUnreadMessageCount];
                
                break;
              }
            }
          });
        }
  });
}

- (void)didReceiveRecallMessageNotification:(NSNotification *)notification {
  dispatch_async(self.updateEventQueue, ^{
    long messageId = [notification.object longValue];
    
    dispatch_async(dispatch_get_main_queue(), ^{
      for (RCConversationModel *model in self.conversationListDataSource) {
        if (model.lastestMessageId == messageId) {
          RCConversationCellUpdateInfo *updateInfo = [[RCConversationCellUpdateInfo alloc] init];
          
          RCConversation *conversation = [[RCIMClient sharedRCIMClient]getConversation:model.conversationType targetId:model.targetId];
          model.lastestMessage = conversation.lastestMessage;
          updateInfo.model = model;
          updateInfo.updateType = RCConversationCell_MessageContent_Update;
          [[NSNotificationCenter defaultCenter]
           postNotificationName:RCKitConversationCellUpdateNotification
           object:updateInfo
           userInfo:nil];
          break;
        }
      }
    });
  });
}

- (void)refreshConversationTableViewIfNeeded {
  [self forceLoadConversationModelList:^(NSMutableArray *modelList) {
    dispatch_async(dispatch_get_main_queue(), ^{
      self.conversationListDataSource = modelList;
      [self.conversationListTableView reloadData];
      [self updateEmptyConversationView];
    });
  }];
}

- (void)forceLoadConversationModelList:(void (^)(NSMutableArray *modelList))completion {
  dispatch_async(self.updateEventQueue, ^{
    NSMutableArray *modelList = [[NSMutableArray alloc] init];

    if (self.showConversationListWhileLogOut ||
        [[RCIM sharedRCIM] getConnectionStatus] != ConnectionStatus_SignUp) {
      NSArray *conversationList =
      [[RCIMClient sharedRCIMClient] getConversationList:self.displayConversationTypeArray];
      for (RCConversation *conversation in conversationList) {
        RCConversationModel *model =
        [[RCConversationModel alloc] initWithConversation:conversation extend:nil];
        model.topCellBackgroundColor = self.topCellBackgroundColor;
        model.cellBackgroundColor = self.cellBackgroundColor;
        [modelList addObject:model];
      }
    }
    
    modelList = [self willReloadTableData:modelList];
    modelList =
        [self collectConversation:modelList collectionTypes:self.collectionConversationTypeArray];

    completion(modelList);
  });
}

- (NSMutableArray *)collectConversation:(NSMutableArray *)modelList
                        collectionTypes:(NSArray *)collectionTypes {
  if (collectionTypes.count == 0) {
    return modelList;
  }

  NSMutableDictionary *collectedModelDict = [[NSMutableDictionary alloc] init];
  for (RCConversationModel *model in modelList.copy) {
    if ([collectionTypes containsObject:@(model.conversationType)]) {
      RCConversationModel *collectedModel = collectedModelDict[@(model.conversationType)];
      if (collectedModel) {
        collectedModel.unreadMessageCount += model.unreadMessageCount;
        collectedModel.hasUnreadMentioned |= model.hasUnreadMentioned;
        collectedModel.isTop |= model.isTop;
        [modelList removeObject:model];
      } else {
        model.conversationModelType = RC_CONVERSATION_MODEL_TYPE_COLLECTION;
        [collectedModelDict setObject:model forKey:@(model.conversationType)];
      }
    }
  }

  return modelList;
}

- (NSUInteger)getFirstModelIndex:(BOOL)isTop sentTime:(long long)sentTime {
  if (isTop || self.conversationListDataSource.count == 0) {
    return 0;
  } else {
    for (NSUInteger index = 0; index < self.conversationListDataSource.count; index++) {
      RCConversationModel *model = self.conversationListDataSource[index];
      if (model.isTop == isTop && sentTime >= model.sentTime) {
        return index;
      }
    }
    return self.conversationListDataSource.count - 1;
  }
}

#pragma mark - TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
  return self.conversationListDataSource.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  RCConversationModel *model = self.conversationListDataSource[indexPath.row];

  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_CUSTOMIZATION) {
    RCConversationBaseCell *userCustomCell =
        [self rcConversationListTableView:tableView cellForRowAtIndexPath:indexPath];
    userCustomCell.selectionStyle = UITableViewCellSelectionStyleDefault;
    [userCustomCell setDataModel:model];
    [self willDisplayConversationTableCell:userCustomCell atIndexPath:indexPath];

    return userCustomCell;
  } else {
    static NSString *cellReuseIndex = @"rc.conversationList.cellReuseIndex";
    RCConversationCell *cell = [tableView dequeueReusableCellWithIdentifier:cellReuseIndex];
    if (!cell) {
      cell = [[RCConversationCell alloc] initWithStyle:UITableViewCellStyleDefault
                                       reuseIdentifier:cellReuseIndex];
    }
    cell.delegate = self;
    cell.selectionStyle = UITableViewCellSelectionStyleDefault;
    [cell setDataModel:model];
    [self willDisplayConversationTableCell:cell atIndexPath:indexPath];

    return cell;
  }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
  RCConversationModel *model = self.conversationListDataSource[indexPath.row];
  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_CUSTOMIZATION) {
    return [self rcConversationListTableView:tableView heightForRowAtIndexPath:indexPath];
  } else {
    return [RCIM sharedRCIM].globalConversationPortraitSize.height + 18.5f;
  }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
  RCConversationModel *model = self.conversationListDataSource[indexPath.row];

  if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_PUBLIC_SERVICE) {
    NSLog(@"从SDK 2.3.0版本开始, 公众号会话点击处理放到demo中处理, "
          @"请参考RCDChatListViewController文件中的onSelectedTableRow函数");
  }
  [self onSelectedTableRow:model.conversationModelType
         conversationModel:model
               atIndexPath:indexPath];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
  if (self.isShowNetworkIndicatorView && !self.networkIndicatorView.hidden) {
    return self.networkIndicatorView.bounds.size.height;
  } else {
    return 0;
  }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
  if (self.isShowNetworkIndicatorView && !self.networkIndicatorView.hidden) {
    return self.networkIndicatorView;
  } else {
    return nil;
  }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
  return YES;
}

- (void)tableView:(UITableView *)tableView
    commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
     forRowAtIndexPath:(NSIndexPath *)indexPath {
  if (editingStyle == UITableViewCellEditingStyleDelete) {
    RCConversationModel *model = self.conversationListDataSource[indexPath.row];

    if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_NORMAL ||
        model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_PUBLIC_SERVICE) {
      [[RCIMClient sharedRCIMClient] removeConversation:model.conversationType
                                               targetId:model.targetId];
      [self.conversationListDataSource removeObjectAtIndex:indexPath.row];
      [self.conversationListTableView deleteRowsAtIndexPaths:@[ indexPath ]
                                            withRowAnimation:UITableViewRowAnimationFade];
    } else if (model.conversationModelType == RC_CONVERSATION_MODEL_TYPE_COLLECTION) {
      [[RCIMClient sharedRCIMClient] clearConversations:@[ @(model.conversationType) ]];
      [self.conversationListDataSource removeObjectAtIndex:indexPath.row];
      [self.conversationListTableView deleteRowsAtIndexPaths:@[ indexPath ]
                                            withRowAnimation:UITableViewRowAnimationFade];
    } else {
      [self rcConversationListTableView:tableView
                     commitEditingStyle:editingStyle
                      forRowAtIndexPath:indexPath];
    }

    [self didDeleteConversationCell:model];
    [self notifyUpdateUnreadMessageCount];

    if (self.isEnteredToCollectionViewController && self.conversationListDataSource.count == 0) {
      [self.navigationController popViewControllerAnimated:YES];
    } else {
      [self updateEmptyConversationView];
    }
  } else {
    NSLog(@"editingStyle %zd is unsupported.", editingStyle);
  }
}

#pragma mark - View Setter&Getter
- (RCNetworkIndicatorView *)networkIndicatorView {
  if (!_networkIndicatorView) {
    _networkIndicatorView = [[RCNetworkIndicatorView alloc]
        initWithText:NSLocalizedStringFromTable(@"ConnectionIsNotReachable", @"RongCloudKit", nil)];
    _networkIndicatorView.backgroundColor = HEXCOLOR(0xffdfdf);
    [_networkIndicatorView setFrame:CGRectMake(0, 0, self.view.bounds.size.width, 40)];
    _networkIndicatorView.hidden = YES;
  }
  return _networkIndicatorView;
}

- (UIView *)connectionStatusView {
  if (!_connectionStatusView) {
    _connectionStatusView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 200, 44)];

    UIActivityIndicatorView *indicatorView = [[UIActivityIndicatorView alloc] init];
    [indicatorView startAnimating];
    [_connectionStatusView addSubview:indicatorView];

    NSString *loading = NSLocalizedStringFromTable(@"Connecting...", @"RongCloudKit", nil);
    CGSize textSize =
        [RCKitUtility getTextDrawingSize:loading
                                    font:[UIFont systemFontOfSize:16]
                         constrainedSize:CGSizeMake(_connectionStatusView.frame.size.width, 2000)];

    CGRect frame =
        CGRectMake((_connectionStatusView.frame.size.width -
                    (indicatorView.frame.size.width + textSize.width + 3)) /
                       2,
                   (_connectionStatusView.frame.size.height - indicatorView.frame.size.height) / 2,
                   indicatorView.frame.size.width, indicatorView.frame.size.height);
    indicatorView.frame = frame;
    frame = CGRectMake(indicatorView.frame.origin.x + 14 + indicatorView.frame.size.width,
                       (_connectionStatusView.frame.size.height - textSize.height) / 2,
                       textSize.width, textSize.height);
    UILabel *label = [[UILabel alloc] initWithFrame:frame];
    [label setFont:[UIFont systemFontOfSize:16]];
    [label setText:loading];
    [label setTextColor:[UIColor whiteColor]];
    [_connectionStatusView addSubview:label];
  }
  return _connectionStatusView;
}

@synthesize emptyConversationView = _emptyConversationView;
- (UIView *)emptyConversationView {
  if (!_emptyConversationView) {
    _emptyConversationView = [[UIImageView alloc] initWithImage:IMAGE_BY_NAMED(@"no_message_img")];
    _emptyConversationView.center = self.view.center;
    CGRect emptyRect = _emptyConversationView.frame;
    emptyRect.origin.y -= 36;
    [_emptyConversationView setFrame:emptyRect];
    [self.conversationListTableView addSubview:_emptyConversationView];
  }
  return _emptyConversationView;
}

- (void)setEmptyConversationView:(UIView *)emptyConversationView {
  if (_emptyConversationView) {
    [_emptyConversationView removeFromSuperview];
  }
  _emptyConversationView = emptyConversationView;
  [self.view addSubview:_emptyConversationView];
}

- (void)updateNetworkIndicatorView {
  RCConnectionStatus status = [[RCIMClient sharedRCIMClient] getConnectionStatus];

  BOOL needReloadTableView = NO;
  if (status == ConnectionStatus_NETWORK_UNAVAILABLE || status == ConnectionStatus_UNKNOWN ||
      status == ConnectionStatus_Unconnected) {
    if (self.networkIndicatorView.hidden) {
      needReloadTableView = YES;
    }
    self.networkIndicatorView.hidden = NO;
  } else if (status != ConnectionStatus_Connecting) {
    if (!self.networkIndicatorView.hidden) {
      needReloadTableView = YES;
    }
    self.networkIndicatorView.hidden = YES;
  }

  if (needReloadTableView) {
    [self.conversationListTableView reloadData];
  }
}

- (void)updateConnectionStatusView {
  if (self.isEnteredToCollectionViewController || !self.showConnectingStatusOnNavigatorBar ||
      !self.isConverstaionListAppear) {
    return;
  }

  RCConnectionStatus status = [[RCIMClient sharedRCIMClient] getConnectionStatus];
  if (status == ConnectionStatus_Connecting) {
    [self showConnectingView];
  } else {
    [self hideConnectingView];
  }

  //接口向后兼容 [[++
  [self performSelector:@selector(updateConnectionStatusOnNavigatorBar)];
  //接口向后兼容 --]]
}

- (void)showConnectingView {
  UINavigationItem *visibleNavigationItem = nil;
  if (self.tabBarController) {
    visibleNavigationItem = self.tabBarController.navigationItem;
  } else if (self.navigationItem) {
    visibleNavigationItem = self.navigationItem;
  }

  if (visibleNavigationItem) {
    if (![visibleNavigationItem.titleView isEqual:self.connectionStatusView]) {
      self.navigationTitleView = visibleNavigationItem.titleView;
      visibleNavigationItem.titleView = self.connectionStatusView;
    }
  }
}

- (void)hideConnectingView {
  UINavigationItem *visibleNavigationItem = nil;
  if (self.tabBarController) {
    visibleNavigationItem = self.tabBarController.navigationItem;
  } else if (self.navigationItem) {
    visibleNavigationItem = self.navigationItem;
  }

  if (visibleNavigationItem) {
    if ([visibleNavigationItem.titleView isEqual:self.connectionStatusView]) {
      visibleNavigationItem.titleView = self.navigationTitleView;
    } else {
      self.navigationTitleView = visibleNavigationItem.titleView;
    }
  }

  //接口向后兼容 [[++
  [self performSelector:@selector(setNavigationItemTitleView)];
  //接口向后兼容 --]]
}

- (void)updateEmptyConversationView {
  if (self.conversationListDataSource.count == 0) {
    self.emptyConversationView.hidden = NO;
  } else {
    self.emptyConversationView.hidden = YES;
  }
}

- (void)setIsConverstaionListAppear:(BOOL)isConverstaionListAppear {
  _isConverstaionListAppear = isConverstaionListAppear;
  if (!_isConverstaionListAppear) {
    [self hideConnectingView];
  }
}

- (void)setShowConnectingStatusOnNavigatorBar:(BOOL)showConnectingStatusOnNavigatorBar {
  _showConnectingStatusOnNavigatorBar = showConnectingStatusOnNavigatorBar;
  if (!_showConnectingStatusOnNavigatorBar) {
    [self hideConnectingView];
  }
}

#pragma mark - 钩子
- (void)notifyUpdateUnreadMessageCount {
}
- (void)didTapCellPortrait:(RCConversationModel *)model {
}
- (void)didLongPressCellPortrait:(RCConversationModel *)model {
}
- (NSMutableArray *)willReloadTableData:(NSMutableArray *)dataSource {
  return dataSource;
}
- (void)willDisplayConversationTableCell:(RCConversationBaseCell *)cell
                             atIndexPath:(NSIndexPath *)indexPath {
}
- (void)onSelectedTableRow:(RCConversationModelType)conversationModelType
         conversationModel:(RCConversationModel *)model
               atIndexPath:(NSIndexPath *)indexPath {
}
- (void)didDeleteConversationCell:(RCConversationModel *)model {
}
- (void)rcConversationListTableView:(UITableView *)tableView
                 commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
                  forRowAtIndexPath:(NSIndexPath *)indexPath {
}
- (RCConversationBaseCell *)rcConversationListTableView:(UITableView *)tableView
                                  cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  return nil;
}
- (CGFloat)rcConversationListTableView:(UITableView *)tableView
               heightForRowAtIndexPath:(NSIndexPath *)indexPath {
  return 64.5f;
}

#pragma mark - 向后兼容
- (void)resetConversationListBackgroundViewIfNeeded {
}
- (void)updateConnectionStatusOnNavigatorBar {
}
- (void)setNavigationItemTitleView {
}
- (void)setDisplayConversationTypes:(NSArray *)conversationTypeArray {
  self.displayConversationTypeArray = conversationTypeArray;
}
- (void)setCollectionConversationType:(NSArray *)conversationTypeArray {
  self.collectionConversationTypeArray = conversationTypeArray;
}
- (void)setConversationAvatarStyle:(RCUserAvatarStyle)avatarStyle {
  [RCIM sharedRCIM].globalConversationAvatarStyle = avatarStyle;
}
- (void)setConversationPortraitSize:(CGSize)size {
  [RCIM sharedRCIM].globalConversationPortraitSize = size;
}
- (void)refreshConversationTableViewWithConversationModel:(RCConversationModel *)conversationModel {
  dispatch_async(dispatch_get_main_queue(), ^{
    RCConversationModel *matchingModel = nil;
    for (RCConversationModel *model in self.conversationListDataSource) {
      if ([model isMatching:conversationModel.conversationType
                   targetId:conversationModel.targetId]) {
        matchingModel = model;
        break;
      }
    }

    if (matchingModel) {
      NSUInteger oldIndex = [self.conversationListDataSource indexOfObject:matchingModel];
      NSUInteger newIndex =
          [self getFirstModelIndex:matchingModel.isTop sentTime:matchingModel.sentTime];

      if (oldIndex == newIndex) {
        [self.conversationListTableView
            reloadRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:newIndex inSection:0] ]
                  withRowAnimation:UITableViewRowAnimationAutomatic];
      } else {
        [self.conversationListDataSource removeObjectAtIndex:oldIndex];
        [self.conversationListDataSource insertObject:matchingModel atIndex:newIndex];

        [self.conversationListTableView beginUpdates];
        [self.conversationListTableView
            deleteRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:oldIndex inSection:0] ]
                  withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.conversationListTableView
            insertRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:newIndex inSection:0] ]
                  withRowAnimation:UITableViewRowAnimationAutomatic];
        [self.conversationListTableView endUpdates];
      }
    } else {
      NSUInteger newIndex =
          [self getFirstModelIndex:conversationModel.isTop sentTime:conversationModel.sentTime];
      [self.conversationListDataSource insertObject:conversationModel atIndex:newIndex];
      [self.conversationListTableView
          insertRowsAtIndexPaths:@[ [NSIndexPath indexPathForRow:newIndex inSection:0] ]
                withRowAnimation:UITableViewRowAnimationAutomatic];
    }
    
    [self updateEmptyConversationView];
  });
}

@end
