//
//  RCConversationViewController.m
//  RongIMKit
//
//  Created by xugang on 15/1/22.
//  Copyright (c) 2015年 RongCloud. All AVrights reserved.
//

#import "RCConversationViewController.h"
#import "RCMessageCell.h"
#import "RCTextMessageCell.h"
#import "RCImageMessageCell.h"
#import "RCVoiceMessageCell.h"
#import "RCRichContentMessageCell.h"
#import "RCLocationMessageCell.h"
#import "RCMessageModel.h"
#import "RCIM.h"
#import "RCImageSlideController.h"
#import "RCVoicePlayer.h"
#import "RCLocationViewController.h"
#import "RCTipMessageCell.h"
#import "RCKitUtility.h"
#import "RCConversationCollectionViewHeader.h"
#import "RCKitCommonDefine.h"
#import "RCSystemSoundPlayer.h"
#import "RCSettingViewController.h"
#import <RongIMLib/RongIMLib.h>
#import "RCPublicServiceMultiImgTxtCell.h"
#import "RCPublicServiceImgTxtMsgCell.h"
#import "RCPublicServiceProfileViewController.h"
#import "RCSystemSoundPlayer.h"
#import "RCOldMessageNotificationMessage.h"
#import "RCOldMessageNotificationMessageCell.h"
#import <objc/runtime.h>
#import <AVFoundation/AVFoundation.h>
#import "RCCustomerServiceMessageModel.h"
#import "RCRobotEvaluationView.h"
#import "RCAdminEvaluationView.h"
#import "RCCSAlertView.h"
#import <CoreText/CoreText.h>
#import "RCUserInfoCacheManager.h"
#import "RCRecallMessageImageView.h"
#import "RCFilePreviewViewController.h"
#import "RCCustomerServiceGroupListController.h"
#import "RCFileMessageCell.h"
#import "RCUnknownMessageCell.h"
#import "RongIMKitExtensionManager.h"
#import <SafariServices/SafariServices.h>
#import "RCCSEvaluateView.h"
#import "RCCSLeaveMessageController.h"
#import "RCCSPullLeaveMessageCell.h"
#import "RCExtensionService.h"

@interface RCConversationViewController () <
    UICollectionViewDelegate, UICollectionViewDataSource,
    UICollectionViewDelegateFlowLayout, RCMessageCellDelegate,
    RCChatSessionInputBarControlDelegate, UIGestureRecognizerDelegate,
    UIScrollViewDelegate, UINavigationControllerDelegate, RCPublicServiceMessageCellDelegate,RCTypingStatusDelegate,UIAlertViewDelegate,
RCAdminEvaluationViewDelegate,
RCRobotEvaluationViewDelegate,
RCCSAlertViewDelegate, UIActionSheetDelegate, RCChatSessionInputBarControlDataSource>

@property(nonatomic, strong)RCConversationCollectionViewHeader *collectionViewHeader;
@property(nonatomic)KBottomBarStatus currentBottomBarStatus;
@property(nonatomic)BOOL isLoading;
@property(nonatomic, strong)RCMessageModel *longPressSelectedModel;
@property(nonatomic, assign)BOOL isConversationAppear;
@property(nonatomic, assign)BOOL isTakeNewPhoto;
@property(nonatomic, assign)BOOL isNeedScrollToButtom;
@property(nonatomic, assign)BOOL isChatRoomHistoryMessageLoaded;

@property (nonatomic, strong)RCDiscussion *currentDiscussion;

@property (nonatomic, strong)UIImageView *unreadRightBottomIcon;
@property (nonatomic, assign)NSInteger unreadNewMsgCount;

@property (nonatomic, assign)NSInteger scrollNum;
@property (nonatomic, assign)NSInteger sendOrReciveMessageNum;//记录新收到和自己新发送的消息数，用于计算加载历史消息时插入“以上是历史消息”cell 的位置

@property(nonatomic,assign)BOOL isClear;

@property (nonatomic, strong)UIView *typingStatusView;
@property (nonatomic, strong)UILabel *typingStatusLabel;
@property (nonatomic, strong)dispatch_queue_t rcTypingMessageQueue;
@property (nonatomic, strong)NSMutableArray *typingMessageArray;
@property (nonatomic, strong)NSTimer *typingStatusTimer;
@property (nonatomic,copy)NSString *typingUserStr;
@property (nonatomic,copy)NSString *navigationTitle;
@property (nonatomic, strong)UITapGestureRecognizer *resetBottomTapGesture;

@property (nonatomic)BOOL loadHistoryMessageFromRemote;

@property(nonatomic, strong)RCCustomerServiceConfig *config;
@property(nonatomic, strong)RCCSAlertView *csAlertView;
@property (nonatomic, strong)NSDate *csEnterDate;
@property(nonatomic)RCCustomerServiceStatus currentServiceStatus;
@property(nonatomic)BOOL humanEvaluated;

@property(nonatomic, strong)RCRecallMessageImageView *rcImageProressView;
@property(nonatomic, assign)BOOL hasReceiveNewMessage;
@property(nonatomic, strong)NSArray *unreadMentionedMessages;
@property(nonatomic, strong)dispatch_queue_t appendMessageQueue;
@property (nonatomic, strong)NSArray<RCExtensionMessageCellInfo *> *extensionMessageCellInfoList;
@property(nonatomic, strong)NSMutableDictionary *cellMsgDict;
//@property(nonatomic, strong) NSTimer *hideReceiptButtonTimer;//群回执定时消失timer
@property (nonatomic, strong)NSTimer *notReciveMessageAlertTimer;//长时间没有收到消息的计时器
@property (nonatomic, strong)NSTimer *notSendMessageAlertTimer;//长时间没有发送消息的计时器

/*!
 是否开启客服超时提醒
 
 @discussion 默认值为NO。
 开启该提示功能之后，在客服会话页面长时间没有说话或者收到对方的消息，会插入一条提醒消息
 */
@property(nonatomic, assign) BOOL enableCustomerServiceOverTimeRemind;

/*!
 客服长时间没有收到消息超时提醒时长
 
 @discussion 默认值60秒。
 开启enableCustomerServiceOverTimeRemind之后，在客服会话页面，时长 customerServiceReciveMessageOverTimeRemindTimer 没有收到对方的消息，会插入一条提醒消息
 */
@property(nonatomic, assign) int customerServiceReciveMessageOverTimeRemindTimer;

/*!
 客服长时间没有收到消息超时提醒内容
 
 开启enableCustomerServiceOverTimeRemind之后，在客服会话页面，时长 customerServiceSendMessageOverTimeRemindTimer 没有说话，会插入一条提醒消息
 */
@property (nonatomic,copy)NSString *customerServiceReciveMessageOverTimeRemindContent;

/*!
 客服长时间没有发送消息超时提醒时长
 
 @discussion 默认值60秒。
 开启enableCustomerServiceOverTimeRemind之后，在客服会话页面，时长 customerServiceSendMessageOverTimeRemindTimer 没有说话，会插入一条提醒消息
 */
@property(nonatomic, assign) int customerServiceSendMessageOverTimeRemindTimer;

/*!
 客服长时间没有发送消息超时提醒内容
 
 开启enableCustomerServiceOverTimeRemind之后，在客服会话页面，时长 customerServiceSendMessageOverTimeRemindTimer 没有说话，会插入一条提醒消息
 */
@property (nonatomic,copy)NSString *customerServiceSendMessageOverTimeRemindContent;

@property(nonatomic, strong)RCCSEvaluateView *evaluateView;
@end

static NSString *const rcUnknownMessageCellIndentifier =
    @"rcUnknownMessageCellIndentifier";


bool isCanSendTypingMessage = YES;
@implementation RCConversationViewController

- (id)initWithConversationType:(RCConversationType)conversationType
                      targetId:(NSString *)targetId {
  self = [super init];
  if (self) {
    self.conversationType = conversationType;
    self.targetId = targetId;
  }
  return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
  self = [super initWithCoder:aDecoder];
  if (self) {
    [self rcinit];
  }
  return self;
}

- (id)initWithNibName:(NSString *)nibNameOrNil
               bundle:(NSBundle *)nibBundleOrNil {
  self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
  if (self) {
    [self rcinit];
  }
  return self;
}

- (void)rcinit {
  _isLoading = NO;
  _isConversationAppear = NO;
  self.conversationDataRepository = [[NSMutableArray alloc] init];
  self.conversationMessageCollectionView = nil;
  self.targetId = nil;
    self.customerServiceReciveMessageOverTimeRemindTimer = 20;
    self.customerServiceSendMessageOverTimeRemindTimer = 10;
    //self.enableCustomerServiceOverTimeRemind = YES;
  _userName = nil; //废弃
  self.currentBottomBarStatus = KBottomBarDefaultStatus;
  [self registerNotification];

  self.displayUserNameInCell = YES;
  self.defaultInputType = RCChatSessionInputBarInputText;
  self.defaultHistoryMessageCountOfChatRoom = 10;
  self.enableContinuousReadUnreadVoice = YES;
  self.isClear = NO;
  self.typingMessageArray = [[NSMutableArray alloc]init];
  self.loadHistoryMessageFromRemote = NO;
  self.appendMessageQueue = dispatch_queue_create("", DISPATCH_QUEUE_SERIAL);
  self.cellMsgDict = [[NSMutableDictionary alloc] init];
  self.csEvaInterval = 60;
}

- (void)setDefaultHistoryMessageCountOfChatRoom:(int)defaultHistoryMessageCountOfChatRoom {
  if(RC_IOS_SYSTEM_VERSION_LESS_THAN(@"8.0") && defaultHistoryMessageCountOfChatRoom > 30){
    defaultHistoryMessageCountOfChatRoom = 30;
  }
  _defaultHistoryMessageCountOfChatRoom = defaultHistoryMessageCountOfChatRoom;
}

- (void)registerNotification {

  //注册接收消息
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(didReceiveMessageNotification:)
     name:RCKitDispatchMessageNotification
     object:nil];
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(didSendingMessageNotification:)
     name:@"RCKitSendingMessageNotification"
     object:nil];
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(receiveMessageHasReadNotification:)
     name:RCLibDispatchReadReceiptNotification
     object:nil];
    
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(handleAppResume)
     name:UIApplicationWillEnterForegroundNotification
     object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWillResignActive)
                                                 name:UIApplicationWillResignActiveNotification object:nil];

  
  [[NSNotificationCenter defaultCenter]addObserver:self
                                          selector:@selector(didReceiveRecallMessageNotification:)
                                              name:RCKitDispatchRecallMessageNotification
                                            object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onReceiveMessageReadReceiptResponse:) name:RCKitDispatchMessageReceiptResponseNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onReceiveMessageReadReceiptRequest:) name:RCKitDispatchMessageReceiptRequestNotification object:nil];
}

- (void)registerClass:(Class)cellClass forMessageClass:(Class)messageClass {
  [self.conversationMessageCollectionView registerClass:cellClass
                             forCellWithReuseIdentifier:[messageClass getObjectName]];
  [self.cellMsgDict setObject:cellClass forKey:[messageClass getObjectName]];
}

- (void)registerClass:(Class)cellClass forCellWithReuseIdentifier:(NSString *)identifier {
  [self.conversationMessageCollectionView registerClass:cellClass
                             forCellWithReuseIdentifier:identifier];
}
- (void)viewDidLoad {
  [super viewDidLoad];
    
  self.rcImageProressView = [[RCRecallMessageImageView alloc] initWithFrame:CGRectMake(0, 0, 135, 135)];
  //-----
  // Do any additional setup after loading the view.
  // self.edgesForExtendedLayout = UIRectEdgeBottom | UIRectEdgeTop;
  if (RC_IOS_SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
    // 左滑返回 和 按住事件冲突
    self.extendedLayoutIncludesOpaqueBars = YES;
    self.automaticallyAdjustsScrollViewInsets = NO;
  }
  [self initializedSubViews];

  if (!(self.conversationType == ConversationType_CHATROOM)) {
    //非聊天室加载历史数据
    [self loadLatestHistoryMessage];
    self.unReadMessage = [[RCIMClient sharedRCIMClient]
                          getUnreadCount:self.conversationType
                          targetId:self.targetId];
  } else {
    //聊天室从服务器拉取消息，设置初始状态为为加载完成
    self.isChatRoomHistoryMessageLoaded = NO;
  }
  if ([RCIM sharedRCIM].enableMessageMentioned &&
      (self.conversationType == ConversationType_GROUP ||
       self.conversationType == ConversationType_DISCUSSION)) {
    self.chatSessionInputBarControl.isMentionedEnabled = YES;
    _unreadMentionedMessages = [[RCIMClient sharedRCIMClient]getUnreadMentionedMessages:self.conversationType targetId:self.targetId];
  }
  
  dispatch_async(dispatch_get_global_queue(0, 0), ^{
    [[RCIMClient sharedRCIMClient] clearMessagesUnreadStatus:self.conversationType
                                                    targetId:self.targetId];
  });

    __weak RCConversationViewController *weakSelf = self;
  if (ConversationType_CHATROOM == self.conversationType) {
    [[RCIMClient sharedRCIMClient] joinChatRoom:self.targetId
        messageCount:self.defaultHistoryMessageCountOfChatRoom
        success:^{

        }
        error:^(RCErrorCode status) {
          dispatch_async(dispatch_get_main_queue(), ^{
            if (status == KICKED_FROM_CHATROOM) {
                [weakSelf alertErrorAndLeft:
                 NSLocalizedStringFromTable(@"JoinChatRoomRejected", @"RongCloudKit", nil)];
            } else {
                [weakSelf alertErrorAndLeft:
                 NSLocalizedStringFromTable(@"JoinChatRoomFailed", @"RongCloudKit", nil)];
            }
          });
        }];
  }
  
    
  if (ConversationType_CUSTOMERSERVICE == self.conversationType) {
      [self.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlDefaultType style:RC_CHAT_INPUT_BAR_STYLE_CONTAINER];
      
      if (!self.csInfo) {
          self.csInfo = [RCCustomerServiceInfo new];
          self.csInfo.userId  = [RCIMClient sharedRCIMClient].currentUserInfo.userId;
          self.csInfo.nickName = [RCIMClient sharedRCIMClient].currentUserInfo.name;
          self.csInfo.portraitUrl = [RCIMClient sharedRCIMClient].currentUserInfo.portraitUri;
      }
      
      [[RCIMClient sharedRCIMClient] startCustomerService:self.targetId info:self.csInfo onSuccess:^(RCCustomerServiceConfig *config) {
          weakSelf.config = config;
          weakSelf.csEnterDate = [[NSDate alloc] init];
          [self startNotSendMessageAlertTimer];
          [self startNotReciveMessageAlertTimer];
        if (config.disableLocation) {
          [weakSelf.chatSessionInputBarControl.pluginBoardView removeItemWithTag:PLUGIN_BOARD_ITEM_LOCATION_TAG];
        }
        if (config.evaEntryPoint == RCCSEvaExtention) {
          [weakSelf.chatSessionInputBarControl.pluginBoardView insertItemWithImage:[RCKitUtility imageNamed:@"Comment" ofBundle:@"RongCloud.bundle"] title:@"评价" tag:PLUGIN_BOARD_ITEM_EVA_TAG];
        }
      } onError:^(int errorCode, NSString *errMsg) {
          [weakSelf customerServiceWarning:errMsg.length ? errMsg : @"连接客服失败!" quitAfterWarning:YES needEvaluate:NO needSuspend:NO];
      } onModeType:^(RCCSModeType mode) {
          weakSelf.currentServiceStatus = RCCustomerService_NoService;
          [weakSelf onCustomerServiceModeChanged:mode];
          switch (mode) {
              case RC_CS_RobotOnly:
                  [weakSelf.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlDefaultType style:RC_CHAT_INPUT_BAR_STYLE_CONTAINER];
                  weakSelf.currentServiceStatus = RCCustomerService_RobotService;
                  break;
              case RC_CS_HumanOnly: {
                  weakSelf.currentServiceStatus = RCCustomerService_HumanService;
                  RCChatSessionInputBarControlStyle style = RC_CHAT_INPUT_BAR_STYLE_SWITCH_CONTAINER_EXTENTION;
                  [weakSelf.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlDefaultType style:style];
              }
                  break;
              case RC_CS_RobotFirst:
                  [weakSelf.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlCSRobotType style:RC_CHAT_INPUT_BAR_STYLE_CONTAINER];
                  weakSelf.currentServiceStatus = RCCustomerService_RobotService;
                  break;
              case RC_CS_NoService: {
                  RCChatSessionInputBarControlStyle style = RC_CHAT_INPUT_BAR_STYLE_SWITCH_CONTAINER_EXTENTION;
                  [weakSelf.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlDefaultType style:style];
                  weakSelf.currentServiceStatus = RCCustomerService_NoService;
              }
                  break;
              default:
                  break;
          }
          [weakSelf resetBottomBarStatus];
      } onPullEvaluation:^(NSString *dialogId) {
           [weakSelf resetBottomBarStatus];
//          if ([weakSelf.csEnterDate timeIntervalSinceNow] < -60 && !weakSelf.humanEvaluated && weakSelf.config.evaEntryPoint == RCCSEvaLeave) {
//              weakSelf.humanEvaluated = YES;
//              [weakSelf commentCustomerServiceWithStatus:weakSelf.currentServiceStatus commentId:dialogId quitAfterComment:NO];
//          }
        [weakSelf showEvaView];
      } onSelectGroup:^(NSArray<RCCustomerServiceGroupItem *> *groupList) {
        [self onSelectCustomerServiceGroup:groupList result:^(NSString *groupId) {
          [[RCIMClient sharedRCIMClient] selectCustomerServiceGroup:self.targetId withGroupId:groupId];
        }];
      } onQuit:^(NSString *quitMsg) {
        [weakSelf customerServiceWarning:quitMsg.length ? quitMsg : @"客服会话已结束!" quitAfterWarning:YES needEvaluate:YES needSuspend:YES];
      }];
  }

  self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]
      initWithImage:[UIImage imageNamed:@"Setting"]
              style:UIBarButtonItemStylePlain
             target:self
             action:@selector(rightBarButtonItemClicked:)];
    
    if (self.conversationType == ConversationType_DISCUSSION) {
        [[RCIMClient sharedRCIMClient] getDiscussion:self.targetId success:^(RCDiscussion *discussion) {
            self.currentDiscussion = discussion;
        } error:^(RCErrorCode status) {
            
        }];
    }
    if(ConversationType_APPSERVICE==self.conversationType || ConversationType_PUBLICSERVICE==self.conversationType)
    {
        RCPublicServiceProfile *profile = [[RCIMClient sharedRCIMClient] getPublicServiceProfile:(RCPublicServiceType)self.conversationType publicServiceId:self.targetId];
        if (profile.menu.menuItems) {
            [self.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlPubType style:RC_CHAT_INPUT_BAR_STYLE_SWITCH_CONTAINER_EXTENTION];
            self.chatSessionInputBarControl.publicServiceMenu = profile.menu;
        }
        RCPublicServiceCommandMessage *entryCommond = [RCPublicServiceCommandMessage messageWithCommand:@"entry" data:nil];
        [self sendMessage:entryCommond pushContent:nil];
    }
    
    _resetBottomTapGesture =
    [[UITapGestureRecognizer alloc]
     initWithTarget:self
     action:@selector(tap4ResetDefaultBottomBarStatus:)];
    [_resetBottomTapGesture setDelegate:self];
  
  NSString *draft =
  [[RCIMClient sharedRCIMClient] getTextMessageDraft:self.conversationType
                                            targetId:self.targetId];
  self.chatSessionInputBarControl.draft = draft;
}

- (void)onSelectCustomerServiceGroup:(NSArray *)groupList result:(void (^)(NSString *groupId))resultBlock {
  NSMutableArray *__groupList = [NSMutableArray array];
  for (RCCustomerServiceGroupItem *item in groupList) {
    if (item.online) {
      [__groupList addObject:item];
    }
  }
  if (__groupList && __groupList.count > 0) {
    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
      RCCustomerServiceGroupListController *customerGroupListController =  [[RCCustomerServiceGroupListController alloc] init];
      UINavigationController *rootVC = [[UINavigationController alloc] initWithRootViewController:customerGroupListController];
      customerGroupListController.groupList = __groupList;
      [customerGroupListController setSelectGroupBlock:^(NSString *groupId) {
        resultBlock(groupId);
      }];
      [weakSelf presentViewController:rootVC animated:YES completion:nil];
    });
  }else{
    resultBlock(nil);
  }
}

- (void)alertErrorAndLeft:(NSString *)errorInfo {
  UIAlertView *alert = [[UIAlertView alloc] initWithTitle:errorInfo
                                                  message:nil
                                                 delegate:nil
                                        cancelButtonTitle:nil
                                        otherButtonTitles:nil];
  [NSTimer scheduledTimerWithTimeInterval:1.0f
                                   target:self
                                 selector:@selector(cancelAlertAndGoBack:)
                                 userInfo:alert
                                  repeats:NO];
  [alert show];
}

- (void)cancelAlertAndGoBack:(NSTimer *)scheduledTimer {
  UIAlertView *alert = (UIAlertView *)(scheduledTimer.userInfo);
  [alert dismissWithClickedButtonIndex:0 animated:NO];
  [self.navigationController popViewControllerAnimated:YES];
}

- (void)rightBarButtonItemClicked:(id)sender {
  if (ConversationType_APPSERVICE == self.conversationType ||
      ConversationType_PUBLICSERVICE == self.conversationType) {
    RCPublicServiceProfile *serviceProfile = [[RCIMClient sharedRCIMClient]
        getPublicServiceProfile:(RCPublicServiceType)self.conversationType
                publicServiceId:self.targetId];

    RCPublicServiceProfileViewController *infoVC =
        [[RCPublicServiceProfileViewController alloc] init];
    infoVC.serviceProfile = serviceProfile;
    infoVC.fromConversation = YES;
    [self.navigationController pushViewController:infoVC animated:YES];
  } else {
    RCSettingViewController *settingVC = [[RCSettingViewController alloc] init];
    settingVC.conversationType = self.conversationType;
    settingVC.targetId = self.targetId;
      __weak typeof(self)weakSelf = self;
      settingVC.clearHistoryCompletion = ^(BOOL isSuccess) {
          if (isSuccess) {
              [weakSelf.conversationDataRepository removeAllObjects];
              dispatch_async(dispatch_get_main_queue(), ^{
                  [weakSelf.conversationMessageCollectionView reloadData];
              });
          }
      };
    [self.navigationController pushViewController:settingVC animated:YES];
  }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
  
    if(self.unReadMessage > 0){
        [self syncReadStatus];
      [self sendReadReceipt];
    }
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(receivePlayVoiceFinishNotification:)
     name:@"kRCPlayVoiceFinishNotification"
     object:nil];
    self.navigationController.interactivePopGestureRecognizer.delaysTouchesBegan=NO;
    _resetBottomTapGesture =
    [[UITapGestureRecognizer alloc]
     initWithTarget:self
     action:@selector(tap4ResetDefaultBottomBarStatus:)];
    [_resetBottomTapGesture setDelegate:self];
    [self.conversationMessageCollectionView
     addGestureRecognizer:_resetBottomTapGesture];

    [self.chatSessionInputBarControl containerViewWillAppear];
  
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(currentViewFrameChange:) name:UIApplicationWillChangeStatusBarFrameNotification object:nil];


  [[RCSystemSoundPlayer defaultPlayer] setIgnoreConversationType:self.conversationType targetId:self.targetId];
    //NSLog(@"%ld",(unsigned long)self.conversationDataRepository.count);
    if (self.conversationDataRepository.count == 0 && _unReadButton !=nil) {
        [_unReadButton removeFromSuperview];
        _unReadMessage = 0;
    }
    self.scrollNum = 0;
  if(_unReadMessage > 10 && _unReadMessage <= 150 && self.enableUnreadMessageIcon == YES){
      [self setupUnReadMessageView];
   }

  if (_unreadMentionedMessages) {
    for (int j = 0; j<_unreadMentionedMessages.count; j++) {
      RCMessage *mentionedMsg = [self.unreadMentionedMessages objectAtIndex:j];
      BOOL isFindMentionedMessage = NO;
      for (int i = 0; i < self.conversationDataRepository.count; i++) {
        RCMessage *rcMsg = [self.conversationDataRepository objectAtIndex:i];
        RCMessageModel *model = [RCMessageModel modelWithMessage:rcMsg];
        if (model.messageId == mentionedMsg.messageId) {
          NSIndexPath *indexPath =
          [NSIndexPath indexPathForRow:i inSection:0];
          [self.conversationMessageCollectionView
           scrollToItemAtIndexPath:indexPath
           atScrollPosition:UICollectionViewScrollPositionTop
           animated:NO];
          isFindMentionedMessage = YES;
          break;
        }
      }
      if (isFindMentionedMessage) {
        break;
      }
    }
    
  }
    [[RongIMKitExtensionManager sharedManager] extensionViewWillAppear:self.conversationType targetId:self.targetId extensionView:self.extensionView];
}


-(void)currentViewFrameChange:(NSNotification *)notification {
  [self.chatSessionInputBarControl containerViewSizeChanged];
}


- (void)setupUnReadMessageView{
    if (_unReadButton !=nil) {
        [_unReadButton removeFromSuperview];
    }
    _unReadButton = [UIButton new];
    _unReadButton.frame = CGRectMake(0,76,0,42);
    [_unReadButton setBackgroundImage:[RCKitUtility imageNamed:@"up" ofBundle:@"RongCloud.bundle"] forState:UIControlStateNormal];
    self.unReadMessageLabel = [[UILabel alloc] initWithFrame:CGRectMake(17+9+11,0,0,self.unReadButton.frame.size.height)];
    NSString *stringUnread=[NSString stringWithFormat:NSLocalizedStringFromTable(@"Right_unReadMessage",@"RongCloudKit",nil),(long)_unReadMessage];
    self.unReadMessageLabel.text = stringUnread;
    self.unReadMessageLabel.font=[UIFont systemFontOfSize:14.0];
    self.unReadMessageLabel.textColor=[UIColor colorWithRed:1/255.0f green:149/255.0f blue:255/255.0f alpha:1];
    self.unReadMessageLabel.textAlignment = NSTextAlignmentCenter;
    [_unReadButton addSubview:self.unReadMessageLabel];
    [_unReadButton addTarget:self action:@selector(didTipUnReadButton:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_unReadButton];
    [_unReadButton bringSubviewToFront:self.conversationMessageCollectionView];
    [self labelAdaptive:self.unReadMessageLabel];
}

- (void)labelAdaptive:(UILabel *)sender{
    CGRect rect = [sender.text boundingRectWithSize:CGSizeMake(2000,sender.frame.size.height) options:(NSStringDrawingUsesLineFragmentOrigin) attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]} context:nil];
    CGRect temp = sender.frame;
    temp.size.width = rect.size.width;
    sender.frame = temp;
    CGRect temBut = self.unReadButton.frame;
    temBut.size.width = temp.size.width+9+17+10+11;
    temBut.origin.x = self.view.frame.size.width-temBut.size.width;
    self.unReadButton.frame = temBut;
    UIImage *image = [RCKitUtility imageNamed:@"up" ofBundle:@"RongCloud.bundle"];
    image = [image
                   resizableImageWithCapInsets:UIEdgeInsetsMake(image.size.width * 0.2, image.size.width * 0.8,
                                                                image.size.width * 0.2, image.size.width * 0.2) resizingMode:UIImageResizingModeStretch];
    [self.unReadButton setBackgroundImage:image forState:UIControlStateNormal];
    UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(17.5,(42-8.5)/2,9,8.5)];
    imageView.image = [RCKitUtility imageNamed:@"arrow" ofBundle:@"RongCloud.bundle"];
    [self.unReadButton addSubview:imageView];
}

- (void)didTipUnReadButton:(UIButton *)sender{
    [sender removeFromSuperview];
    long lastMessageId = -1;
    if (self.conversationDataRepository.count > 0) {
        RCMessageModel *model = [self.conversationDataRepository objectAtIndex:0];
        lastMessageId = model.messageId;
    }
    NSArray *__messageArray =
    [[RCIMClient sharedRCIMClient] getHistoryMessages:_conversationType
                                             targetId:_targetId
                                      oldestMessageId:lastMessageId
                                                count:(int)self.unReadMessage - 10];
    [self sendReadReceiptResponseForMessages:__messageArray];
    for (int i = 0; i < __messageArray.count; i++) {
        RCMessage *rcMsg = [__messageArray objectAtIndex:i];
        RCMessageModel *model = [RCMessageModel modelWithMessage:rcMsg];
        [self pushOldMessageModel:model];
    }
    self.unReadMessage = 0;
    if (self.unReadButton != nil && self.enableUnreadMessageIcon) {
        RCOldMessageNotificationMessage *oldMessageTip=[[RCOldMessageNotificationMessage alloc] init];
        RCMessage *oldMessage = [[RCMessage alloc] initWithType:self.conversationType targetId:self.targetId direction:MessageDirection_SEND messageId:-1 content:oldMessageTip];
        RCMessageModel *model = [RCMessageModel modelWithMessage:oldMessage];
        RCMessageModel *lastMessageModel = [self.conversationDataRepository objectAtIndex:0];
        model.messageId = lastMessageModel.messageId;
        [self.conversationDataRepository insertObject:model atIndex:0];
        [self.unReadButton removeFromSuperview];
        self.unReadButton = nil;
    }

    [self figureOutAllConversationDataRepository];
    [self.conversationMessageCollectionView reloadData];
    [self.conversationMessageCollectionView scrollToItemAtIndexPath:[NSIndexPath indexPathForItem:0 inSection:0] atScrollPosition:UICollectionViewScrollPositionTop animated:YES];
}

- (void)viewDidAppear:(BOOL)animated {
  [super viewDidAppear:animated];
  DebugLog(@"view=>%@", self.view);
  DebugLog(@"conversationMessageCollectionView=>%@",
           self.conversationMessageCollectionView);
  _isConversationAppear = YES;

  [self.chatSessionInputBarControl containerViewDidAppear];
  self.navigationTitle = self.navigationItem.title;
  [[RCIMClient sharedRCIMClient]setRCTypingStatusDelegate:self];

}
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
  
    if(_hasReceiveNewMessage){
       [self syncReadStatus];
    }
    self.unreadMentionedMessages = nil;
    [[NSNotificationCenter defaultCenter]
     removeObserver:self
     name:@"kRCPlayVoiceFinishNotification"
     object:nil];
    
    [self.conversationMessageCollectionView removeGestureRecognizer:_resetBottomTapGesture];
    [self.conversationMessageCollectionView
     addGestureRecognizer:_resetBottomTapGesture];
  [[RCSystemSoundPlayer defaultPlayer] resetIgnoreConversation];
  [[NSNotificationCenter defaultCenter]
      postNotificationName:kNotificationStopVoicePlayer
                    object:nil];
  _isConversationAppear = NO;
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        [[RCIMClient sharedRCIMClient] clearMessagesUnreadStatus:self.conversationType
                                                        targetId:self.targetId];
    });
   NSString *draft = self.chatSessionInputBarControl.draft;
    
    if(draft && [draft length] > 0) {
      [[RCIMClient sharedRCIMClient] saveTextMessageDraft:self.conversationType
                                                 targetId:self.targetId
                                                  content:draft];
    } else {
        NSString *draftInDB =
        [[RCIMClient sharedRCIMClient] getTextMessageDraft:self.conversationType
                                                  targetId:self.targetId];
        if (draftInDB && draftInDB.length > 0) {
            [[RCIMClient sharedRCIMClient] clearTextMessageDraft:self.conversationType
                                                   targetId:self.targetId];
        }
    }

  [self.chatSessionInputBarControl cancelVoiceRecord];
  [[RCIMClient sharedRCIMClient]setRCTypingStatusDelegate:nil];
  self.navigationItem.title = self.navigationTitle;
  [self.chatSessionInputBarControl containerViewWillDisappear];
  [[RongIMKitExtensionManager sharedManager] extensionViewWillDisappear:self.conversationType targetId:self.targetId];

}

- (void)dealloc {
    [self quitConversationViewAndClear];
//    if (self.hideReceiptButtonTimer) {
//        [self.hideReceiptButtonTimer invalidate];
//        self.hideReceiptButtonTimer = nil;
//    }
}

- (void)leftBarButtonItemPressed:(id)sender {
    [self quitConversationViewAndClear];
}

// 清理环境（退出讨论组、移除监听等）
- (void)quitConversationViewAndClear {
    if (!self.isClear) {
        
        [[RongIMKitExtensionManager sharedManager] containerViewWillDestroy:self.conversationType targetId:self.targetId];
        
        if (self.conversationType == ConversationType_CHATROOM) {
            [[RCIMClient sharedRCIMClient] quitChatRoom:self.targetId
                                                success:^{
                                                    
                                                } error:^(RCErrorCode status) {
                                                    
                                                }];
        }
        self.conversationMessageCollectionView.dataSource = nil;
        self.conversationMessageCollectionView.delegate = nil;
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        self.isClear = YES;
        
        [self stopNotReciveMessageAlertTimer];
        [self stopNotSendMessageAlertTimer];
    }
    [self stopNotReciveMessageAlertTimer];
    [self stopNotSendMessageAlertTimer];
}

- (void)initializedSubViews {
  // init collection view
  if (nil == self.conversationMessageCollectionView) {

    self.customFlowLayout = [[UICollectionViewFlowLayout alloc] init];
    _customFlowLayout.minimumLineSpacing = 0.0f;
    _customFlowLayout.sectionInset = UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f);
    _customFlowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;

    self.view.backgroundColor = [UIColor whiteColor];
    CGRect _conversationViewFrame = self.view.bounds;

    CGFloat _conversationViewFrameY = 20.0f;
    if (self.navigationController &&
        self.navigationController.navigationBar.hidden == NO) {
      _conversationViewFrameY = 64.0f;
    }
      
      if (RC_IOS_SYSTEM_VERSION_LESS_THAN(@"7.0")) {
          
          _conversationViewFrame.origin.y = 0;
      }else
      {
          _conversationViewFrame.origin.y = _conversationViewFrameY;
      }
      
    _conversationViewFrame.size.height = self.view.bounds.size.height -
                                         RC_ChatSessionInputBar_Height -
                                         _conversationViewFrameY;

    self.conversationMessageCollectionView =
        [[UICollectionView alloc] initWithFrame:_conversationViewFrame
                           collectionViewLayout:self.customFlowLayout];
    [self.conversationMessageCollectionView
        setBackgroundColor:RGBCOLOR(235, 235, 235)];
    self.conversationMessageCollectionView.showsHorizontalScrollIndicator = NO;
    self.conversationMessageCollectionView.alwaysBounceVertical = YES;

    self.collectionViewHeader = [[RCConversationCollectionViewHeader alloc]
        initWithFrame:CGRectMake(0, -40, self.view.bounds.size.width, 40)];
    _collectionViewHeader.tag = 1999;
    [self.conversationMessageCollectionView addSubview:_collectionViewHeader];

    [self registerClass:[RCTextMessageCell class] forMessageClass:[RCTextMessage class]];
    [self registerClass:[RCImageMessageCell class] forMessageClass:[RCImageMessage class]];
    [self registerClass:[RCVoiceMessageCell class] forMessageClass:[RCVoiceMessage class]];
    [self registerClass:[RCRichContentMessageCell class] forMessageClass:[RCRichContentMessage class]];
    [self registerClass:[RCLocationMessageCell class] forMessageClass:[RCLocationMessage class]];
    
    [self registerClass:[RCTipMessageCell class] forMessageClass:[RCInformationNotificationMessage class]];
    [self registerClass:[RCTipMessageCell class] forMessageClass:[RCDiscussionNotificationMessage class]];
    [self registerClass:[RCTipMessageCell class] forMessageClass:[RCGroupNotificationMessage class]];
    [self registerClass:[RCTipMessageCell class] forMessageClass:[RCRecallNotificationMessage class]];
    [self registerClass:[RCCSPullLeaveMessageCell class] forMessageClass:[RCCSPullLeaveMessage class]];
    
    
    [self registerClass:[RCPublicServiceMultiImgTxtCell class] forMessageClass:[RCPublicServiceMultiRichContentMessage class]];
    [self registerClass:[RCPublicServiceImgTxtMsgCell class] forMessageClass:[RCPublicServiceRichContentMessage class]];
    [self registerClass:[RCUnknownMessageCell class] forCellWithReuseIdentifier:rcUnknownMessageCellIndentifier];
    [self registerClass:[RCOldMessageNotificationMessageCell class] forMessageClass:[RCOldMessageNotificationMessage class]];
    [self registerClass:[RCFileMessageCell class] forMessageClass:[RCFileMessage class]];
    
    self.extensionMessageCellInfoList = [[RongIMKitExtensionManager sharedManager] getMessageCellInfoList:self.conversationType targetId:self.targetId];
    for (RCExtensionMessageCellInfo *cellInfo in self.extensionMessageCellInfoList) {
      [self registerClass:cellInfo.messageCellClass forMessageClass:cellInfo.messageContentClass];
    }

    self.conversationMessageCollectionView.dataSource = self;
    self.conversationMessageCollectionView.delegate = self;

    [self.view addSubview:self.conversationMessageCollectionView];
  }
}


- (UIImageView *)unreadRightBottomIcon {
    if (!_unreadRightBottomIcon) {
        UIImage *msgCountIcon = [RCKitUtility imageNamed:@"bubble" ofBundle:@"RongCloud.bundle"];
        _unreadRightBottomIcon = [[UIImageView alloc]initWithFrame:CGRectMake(self.view.frame.size.width-5.5-35,self.chatSessionInputBarControl.frame.origin.y-12-35,35,35)];
        _unreadRightBottomIcon.userInteractionEnabled = YES;
        _unreadRightBottomIcon.image = msgCountIcon;
        //        _unreadRightBottomIcon.translatesAutoresizingMaskIntoConstraints = NO;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tabRightBottomMsgCountIcon:)];
        [_unreadRightBottomIcon addGestureRecognizer:tap];
        _unreadRightBottomIcon.hidden = YES;
        [self.view addSubview:_unreadRightBottomIcon];
    }
    return _unreadRightBottomIcon;
}

- (UILabel *)unReadNewMessageLabel {
    if (!_unReadNewMessageLabel) {
        _unReadNewMessageLabel = [[UILabel alloc]initWithFrame:_unreadRightBottomIcon.bounds];
        _unReadNewMessageLabel.backgroundColor = [UIColor clearColor];
        _unReadNewMessageLabel.font = [UIFont systemFontOfSize:12.0f];
        _unReadNewMessageLabel.textAlignment = NSTextAlignmentCenter;
        _unReadNewMessageLabel.textColor = [UIColor whiteColor];
        _unReadNewMessageLabel.center = CGPointMake(_unReadNewMessageLabel.frame.size.width/2, _unReadNewMessageLabel.frame.size.height/2-2.5 );
        [self.unreadRightBottomIcon addSubview:_unReadNewMessageLabel];
    }
    return _unReadNewMessageLabel;
}

- (RCChatSessionInputBarControl *)chatSessionInputBarControl {
  if (!_chatSessionInputBarControl) {
    _chatSessionInputBarControl = [[RCChatSessionInputBarControl alloc]
           initWithFrame:CGRectMake(0, self.view.bounds.size.height -
                                           RC_ChatSessionInputBar_Height,
                                    self.view.bounds.size.width,
                                    RC_ChatSessionInputBar_Height)
       withContainerView:self.view
             controlType:RCChatSessionInputBarControlDefaultType
            controlStyle:RC_CHAT_INPUT_BAR_STYLE_SWITCH_CONTAINER_EXTENTION
        defaultInputType:self.defaultInputType];
    _chatSessionInputBarControl.conversationType = self.conversationType;
    _chatSessionInputBarControl.targetId = self.targetId;
    _chatSessionInputBarControl.delegate = self;
    _chatSessionInputBarControl.dataSource = self;

    [self.view addSubview:_chatSessionInputBarControl];
  }
  return _chatSessionInputBarControl;
}

//接口向后兼容[[++
- (RCEmojiBoardView *)emojiBoardView {
    return self.chatSessionInputBarControl.emojiBoardView;
}

- (void)setEmojiBoardView:(RCEmojiBoardView *)emojiBoardView {
  self.chatSessionInputBarControl.emojiBoardView = emojiBoardView;
}

- (RCPluginBoardView *)pluginBoardView {
    return self.chatSessionInputBarControl.pluginBoardView;
}

- (void)setPluginBoardView:(RCPluginBoardView *)pluginBoardView {
  self.chatSessionInputBarControl.pluginBoardView = pluginBoardView;
}
//接口向后兼容--]]

- (void)setDefaultInputType:(RCChatSessionInputBarInputType)defaultInputType {
  _defaultInputType = defaultInputType;
  if (_chatSessionInputBarControl) {
      [_chatSessionInputBarControl setDefaultInputType:defaultInputType];
  }
}

//
- (void)updateUnreadMsgCountLabel {
    if (self.unreadNewMsgCount == 0) {
        self.unreadRightBottomIcon.hidden = YES;
    } else {
        self.unreadRightBottomIcon.hidden = NO;
        self.unReadNewMessageLabel.text = (self.unreadNewMsgCount > 99) ?  @"99+" : [NSString stringWithFormat:@"%li", (long)self.unreadNewMsgCount];
    }
}

- (void) checkVisiableCell
{
    NSIndexPath *lastPath = [self getLastIndexPathForVisibleItems];
    if (lastPath.row >= self.conversationDataRepository.count - self.unreadNewMsgCount || lastPath == nil || [self isAtTheBottomOfTableView] ) {
        self.unreadNewMsgCount = 0;
        [self updateUnreadMsgCountLabel];
    }
}

- (NSIndexPath *)getLastIndexPathForVisibleItems {
    NSArray *visiblePaths = [self.conversationMessageCollectionView indexPathsForVisibleItems];
    
    if (visiblePaths.count == 0) {
        return nil;
    } else if(visiblePaths.count == 1) {
        return (NSIndexPath *)[visiblePaths firstObject];
    }
    
    NSArray *sortedIndexPaths = [visiblePaths sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSIndexPath *path1 = (NSIndexPath *)obj1;
        NSIndexPath *path2 = (NSIndexPath *)obj2;
        return [path1 compare:path2];
    }];
    
   return (NSIndexPath *)[sortedIndexPaths lastObject];
}

#pragma mark <UIScrollViewDelegate>
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
  if (self.chatSessionInputBarControl.currentBottomBarStatus != KBottomBarDefaultStatus && self.chatSessionInputBarControl.currentBottomBarStatus != KBottomBarRecordStatus) {
    [self.chatSessionInputBarControl resetToDefaultStatus];
  }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
  if (self.enableNewComingMessageIcon == YES || self.unreadNewMsgCount != 0) {
    [self checkVisiableCell];
  }
  
  if (scrollView.contentOffset.y < -5.0f) {
    [self.collectionViewHeader startAnimating];
  }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView
                  willDecelerate:(BOOL)decelerate {
  if (scrollView.contentOffset.y < -15.0f && !_isLoading) {
    _isLoading = YES;
    [self performSelector:@selector(loadMoreHistoryMessage) withObject:nil afterDelay:0.4f];
  } else {
    [self.collectionViewHeader stopAnimating];
  }
}

- (void)scrollToBottomAnimated:(BOOL)animated {
  if ([self.conversationMessageCollectionView numberOfSections] == 0) {
    return;
  }

  NSUInteger finalRow = MAX(0, [self.conversationMessageCollectionView numberOfItemsInSection:0] - 1);

  if (0 == finalRow) {
    return;
  }
  
  NSIndexPath *finalIndexPath = [NSIndexPath indexPathForItem:finalRow inSection:0];
  [self.conversationMessageCollectionView scrollToItemAtIndexPath:finalIndexPath atScrollPosition:UICollectionViewScrollPositionBottom
                                                         animated:animated];
}

#pragma mark <UICollectionViewDataSource>
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
  return self.conversationDataRepository.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
  RCMessageModel *model = [self.conversationDataRepository objectAtIndex:indexPath.row];

  if (model.messageDirection == MessageDirection_RECEIVE) {
    model.isDisplayNickname = self.displayUserNameInCell;
  } else {
    model.isDisplayNickname = NO;
  }
  
  RCMessageContent *messageContent = model.content;
  RCMessageBaseCell *cell = nil;
  NSString *objName = [[messageContent class] getObjectName];
  if (self.cellMsgDict[objName]) {
    cell = [collectionView dequeueReusableCellWithReuseIdentifier:objName forIndexPath:indexPath];
    
    if ([messageContent isMemberOfClass: [RCPublicServiceMultiRichContentMessage class]]) {
      [(RCPublicServiceMultiImgTxtCell *)cell setPublicServiceDelegate:(id<RCPublicServiceMessageCellDelegate>)self];
    } else if ([messageContent isMemberOfClass:[RCPublicServiceRichContentMessage class]]) {
      [(RCPublicServiceImgTxtMsgCell *)cell setPublicServiceDelegate:(id<RCPublicServiceMessageCellDelegate>)self];
    }
    [cell setDataModel:model];
    [cell setDelegate:self];
  } else if (!messageContent && [RCIM sharedRCIM].showUnkownMessage) {
    cell = [self rcUnkownConversationCollectionView:collectionView
                             cellForItemAtIndexPath:indexPath];
    [cell setDataModel:model];
    [cell setDelegate:self];
  } else {
    cell = [self rcConversationCollectionView:collectionView
                       cellForItemAtIndexPath:indexPath];
  }
  


  
  if (self.conversationType == ConversationType_PRIVATE && [[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(model.conversationType)]) {
    cell.isDisplayReadStatus = YES;
  }
  //接口向后兼容 [[++
  [self performSelector:@selector(willDisplayConversationTableCell:atIndexPath:) withObject:cell withObject:indexPath];
  //接口向后兼容 --]]
  [self willDisplayMessageCell:cell atIndexPath:indexPath];
  return cell;
}

#pragma mark <UICollectionViewDelegateFlowLayout>
- (CGSize)collectionView:(UICollectionView *)collectionView
                  layout:(UICollectionViewLayout *)collectionViewLayout
  sizeForItemAtIndexPath:(NSIndexPath *)indexPath {

  RCMessageModel *model = [self.conversationDataRepository objectAtIndex:indexPath.row];
  if (model.cellSize.height > 0 && !(model.conversationType == ConversationType_CUSTOMERSERVICE && [model.content isKindOfClass:[RCTextMessage class]])) {
    return model.cellSize;
  }
  
  RCMessageContent *messageContent = model.content;
  NSString *objectName = [[messageContent class] getObjectName];
  Class cellClass = self.cellMsgDict[objectName];
  if (class_getClassMethod(cellClass,@selector(sizeForMessageModel:withCollectionViewWidth:referenceExtraHeight:))) {

    CGFloat extraHeight = [self referenceExtraHeight:cellClass messageModel:model];
    CGSize size = [cellClass sizeForMessageModel:model withCollectionViewWidth:collectionView.frame.size.width referenceExtraHeight:extraHeight];
    
    if (size.width != 0 && size.height != 0) {
      model.cellSize = size;
      return size;
    }
  }

  if (!messageContent && [RCIM sharedRCIM].showUnkownMessage) {
    CGSize _size = [self rcUnkownConversationCollectionView:collectionView layout:collectionViewLayout sizeForItemAtIndexPath:indexPath];
    _size.height += [self referenceExtraHeight:RCUnknownMessageCell.class messageModel:model];
    model.cellSize = _size;
  } else {
    CGSize _size = [self rcConversationCollectionView:collectionView
                                               layout:collectionViewLayout
                               sizeForItemAtIndexPath:indexPath];
    DebugLog(@"%@", NSStringFromCGSize(_size));
    model.cellSize = _size;
  }

  return model.cellSize;
}

- (RCMessageBaseCell *)rcUnkownConversationCollectionView:(UICollectionView *)collectionView
                                   cellForItemAtIndexPath:(NSIndexPath *)indexPath {
  RCMessageModel *model =
  [self.conversationDataRepository objectAtIndex:indexPath.row];
  RCMessageCell *__cell = [collectionView dequeueReusableCellWithReuseIdentifier:rcUnknownMessageCellIndentifier
                                                                    forIndexPath:indexPath];
  [__cell setDataModel:model];
  return __cell;
}

- (CGSize)rcUnkownConversationCollectionView:(UICollectionView *)collectionView
                                      layout:(UICollectionViewLayout *)collectionViewLayout
                      sizeForItemAtIndexPath:(NSIndexPath *)indexPath {

  CGFloat __width = CGRectGetWidth(collectionView.frame);
  CGFloat maxMessageLabelWidth = __width - 30 * 2;
  NSString *localizedMessage = NSLocalizedStringFromTable(@"unknown_message_cell_tip", @"RongCloudKit", nil);
  CGSize __textSize = [RCKitUtility getTextDrawingSize:localizedMessage font:[UIFont systemFontOfSize:14] constrainedSize:CGSizeMake(maxMessageLabelWidth, 2000)];
  __textSize = CGSizeMake(ceilf(__textSize.width), ceilf(__textSize.height));
  CGSize __labelSize = CGSizeMake(__textSize.width + 5, __textSize.height + 6);
  return CGSizeMake(collectionView.bounds.size.width, __labelSize.height);
}

- (BOOL)isExtensionCell:(RCMessageContent *)messageContent {
  for (RCExtensionMessageCellInfo *cellInfo in self.extensionMessageCellInfoList) {
    if (cellInfo.messageContentClass == [messageContent class]) {
      return YES;
    }
  }
  return NO;
}

- (CGSize)collectionView:(UICollectionView *)collectionView
                  layout:(UICollectionViewLayout *)collectionViewLayout
referenceSizeForHeaderInSection:(NSInteger)section {
  // show showLoadEarlierMessagesHeader
  return CGSizeZero;
}

- (CGFloat)referenceExtraHeight:(Class)cellClass messageModel:(RCMessageModel *)model {
  CGFloat extraHeight = 0;
  if ([cellClass isSubclassOfClass:RCMessageBaseCell.class]) {
    extraHeight += 10; // up padding
    extraHeight += 10; // down padding
    
    // time label height
    if (model.isDisplayMessageTime) {
      extraHeight += 45;
    }
  }
  if ([cellClass isSubclassOfClass:RCMessageCell.class]) {
    // name label height
    if (model.isDisplayNickname &&
        model.messageDirection == MessageDirection_RECEIVE) {
      extraHeight += 16;
    }
  }
  return extraHeight;
}

#pragma mark <UICollectionViewDelegate>
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
}

- (void)figureOutAllConversationDataRepository {
  for (int i = 0; i < self.conversationDataRepository.count; i++) {
    RCMessageModel *model = [self.conversationDataRepository objectAtIndex:i];
    if (0 == i) {
      model.isDisplayMessageTime = YES;
    } else if (i > 0) {
      RCMessageModel *pre_model =
          [self.conversationDataRepository objectAtIndex:i - 1];

      long long previous_time = pre_model.sentTime;

      long long current_time = model.sentTime;

      long long interval = current_time - previous_time > 0
                               ? current_time - previous_time
                               : previous_time - current_time;
      if (interval / 1000 <= 3*60) {
          if (model.isDisplayMessageTime && model.cellSize.height > 0) {
              CGSize size = model.cellSize;
              size.height = model.cellSize.height-45;
              model.cellSize = size;
          }
        model.isDisplayMessageTime = NO;
      } else if(![model.content isKindOfClass:[RCOldMessageNotificationMessage class]]) {
        if (!model.isDisplayMessageTime && model.cellSize.height > 0) {
          CGSize size = model.cellSize;
          size.height = model.cellSize.height + 45;
          model.cellSize = size;
        }
        model.isDisplayMessageTime = YES;
      }
    }
      if ([model.content isKindOfClass:[RCOldMessageNotificationMessage class]]) {
          model.isDisplayMessageTime = NO;
      }
  }
}

- (void)figureOutLatestModel:(RCMessageModel *)model {
  if (_conversationDataRepository.count > 0) {

    RCMessageModel *pre_model = [self.conversationDataRepository
        objectAtIndex:_conversationDataRepository.count - 1];

    long long previous_time = pre_model.sentTime;


    long long current_time = model.sentTime;

    long long interval = current_time - previous_time > 0
                             ? current_time - previous_time
                             : previous_time - current_time;
    if (interval / 1000 <= 3 * 60) {
      model.isDisplayMessageTime = NO;
    } else {
      model.isDisplayMessageTime = YES;
    }
  } else {
    model.isDisplayMessageTime = YES;
  }
}

- (void)appendAndDisplayMessage:(RCMessage *)rcMessage {
  if (!rcMessage) {
    return;
  }
  dispatch_async(self.appendMessageQueue, ^{
    dispatch_async(dispatch_get_main_queue(), ^{
      RCMessageModel *model = [RCMessageModel modelWithMessage:rcMessage];
      [self figureOutLatestModel:model];
      if ([self appendMessageModel:model]) {
        self.sendOrReciveMessageNum++;//记录新收到和自己新发送的消息数，用于计算加载历史消息时插入“以上是历史消息”cell 的位置
        NSIndexPath *indexPath =
        [NSIndexPath indexPathForItem:self.conversationDataRepository.count - 1
                            inSection:0];
        if ([self.conversationMessageCollectionView numberOfItemsInSection:0] !=
            self.conversationDataRepository.count - 1) {
          NSLog(@"Error, datasource and collectionview are inconsistent!!");
          [self.conversationMessageCollectionView reloadData];
          return;
        }
        [self.conversationMessageCollectionView
         insertItemsAtIndexPaths:[NSArray arrayWithObject:indexPath]];
        
        if ([self isAtTheBottomOfTableView] || self.isNeedScrollToButtom) {
          [self scrollToBottomAnimated:YES];
          self.isNeedScrollToButtom=NO;
        }
      }
    });
    [NSThread sleepForTimeInterval:0.01];
  });
}

- (BOOL)appendMessageModel:(RCMessageModel *)model {
  long newId = model.messageId;
  for (RCMessageModel *__item in self.conversationDataRepository) {

    /*
     * 当id为－1时，不检查是否重复，直接插入
     * 该场景用于插入临时提示。
     */
    if (newId == -1) {
      break;
    }
    if (newId == __item.messageId) {
      return NO;
    }
  }

  if (newId != -1
      && !(!model.content && model.messageId > 0 && [RCIM sharedRCIM].showUnkownMessage)
      && !([[model.content class] persistentFlag] & MessagePersistent_ISPERSISTED)) {
    return NO;
  }

  if (model.messageDirection == MessageDirection_RECEIVE) {
    model.isDisplayNickname = self.displayUserNameInCell;
  } else {
    model.isDisplayNickname = NO;
  }
  [self.conversationDataRepository addObject:model];
  return YES;
}

- (void)pushOldMessageModel:(RCMessageModel *)model {
  if (!(!model.content && model.messageId > 0 && [RCIM sharedRCIM].showUnkownMessage)
      && !([[model.content class] persistentFlag] & MessagePersistent_ISPERSISTED)) {
    return;
  }


  long ne_wId = model.messageId;
  for (RCMessageModel *__item in self.conversationDataRepository) {

    if (ne_wId == __item.messageId && ne_wId != -1) {
      return;
    }
  }
  if (model.messageDirection == MessageDirection_RECEIVE) {
    model.isDisplayNickname = self.displayUserNameInCell;
  } else {
    model.isDisplayNickname = NO;
  }
  [self.conversationDataRepository insertObject:model atIndex:0];
}

- (void)loadLatestHistoryMessage {
  self.loadHistoryMessageFromRemote = NO;
  NSArray *__messageArray =
      [[RCIMClient sharedRCIMClient] getLatestMessages:self.conversationType
                                              targetId:self.targetId
                                                 count:10];
    [self sendReadReceiptResponseForMessages:__messageArray];
    if (__messageArray.count < 10) {
        self.loadHistoryMessageFromRemote = YES;
    }

  for (int i = 0; i < __messageArray.count; i++) {
    RCMessage *rcMsg = [__messageArray objectAtIndex:i];
    RCMessageModel *model = [RCMessageModel modelWithMessage:rcMsg];
      if ([model isKindOfClass:[RCCustomerServiceMessageModel class]]) {
          RCCustomerServiceMessageModel *csModel = (RCCustomerServiceMessageModel *)model;
          [csModel disableEvaluate];
      }
      
    [self pushOldMessageModel:model];
     
  }
    //开启群回执，最后一条发送的文本消息两分钟内可以选择请求回执(暂时去掉，多端存在问题，另一端收到自己发送的消息时不显示，但是重新进来如果小于两分钟就会显示)
    if ([[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)] && (self.conversationType == ConversationType_DISCUSSION || self.conversationType == ConversationType_GROUP)) {
        NSTimeInterval nowTime = [[NSDate date] timeIntervalSince1970] * 1000;
//        RCMessageModel *canReceiptMessageModel;
        int len = (int)self.conversationDataRepository.count-1;
        for(int i = len; i >= 0; i--){
            RCMessageModel *model = self.conversationDataRepository[i];
            
            if (model.messageDirection == MessageDirection_SEND ) {
                if (((nowTime - model.sentTime) < 1000 * 60 * 2) && [model.content isKindOfClass:[RCTextMessage class]] && model.sentTime && !model.readReceiptInfo) {
                    model.isCanSendReadReceipt = YES;
                    if (!model.readReceiptInfo) {
                        model.readReceiptInfo = [[RCReadReceiptInfo alloc]init];
                    }
//                    canReceiptMessageModel = model;
//                    self.hideReceiptButtonTimer = [NSTimer scheduledTimerWithTimeInterval:60.0f * 2
//                                                     target:self
//                                                   selector:@selector(hideReceiptButton)
//                                                   userInfo:nil
//                                                    repeats:NO];

                }
                break;
            }
        }
    }

  [self figureOutAllConversationDataRepository];
}

//-(void)hideReceiptButton{
//    if ([RCIM sharedRCIM].enableReadReceipt && (self.conversationType == ConversationType_DISCUSSION || self.conversationType == ConversationType_GROUP)) {
//        NSMutableArray *array = [NSMutableArray array];
//        for(int i = self.conversationDataRepository.count-1 ; i >= 0 ; i--){
//            RCMessageModel *model = self.conversationDataRepository[i];
//            
//            if (model.messageDirection == MessageDirection_SEND && model.isCanSendReadReceipt) {
//                model.isCanSendReadReceipt = NO;
//                NSIndexPath *indexPath = [NSIndexPath indexPathForRow:i inSection:0];
//                [array addObject:indexPath];
//                __weak typeof(self) weakSelf = self;
//                dispatch_async(dispatch_get_main_queue(), ^{
//                    [weakSelf.conversationMessageCollectionView reloadItemsAtIndexPaths:array];
//                });
//                return;
//            }
//        }
//    }
//
//}

- (void)loadMoreHistoryMessage {
    static BOOL msgRoamingServiceAvailable = YES;
  long lastMessageId = -1;
  long long recordTime = 0;
  if (self.conversationDataRepository.count > 0) {
    for (RCMessageModel *model in self.conversationDataRepository) {
      if (![model.content isKindOfClass:[RCOldMessageNotificationMessage class]]) {
        lastMessageId = model.messageId;
        recordTime = model.sentTime;
        break;
      }
    }
  }

    NSArray *__messageArray;
    if (self.loadHistoryMessageFromRemote && msgRoamingServiceAvailable && self.conversationType != ConversationType_CHATROOM && self.conversationType != ConversationType_APPSERVICE && self.conversationType != ConversationType_PUBLICSERVICE) {
        __weak typeof(self)weakSelf = self;
        [[RCIMClient sharedRCIMClient] getRemoteHistoryMessages:self.conversationType targetId:self.targetId recordTime:recordTime count:10 success:^(NSArray *messages) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (messages.count) {
                    [weakSelf handleMessagesAfterLoadMore:messages];
                }
                [weakSelf.collectionViewHeader stopAnimating];
                weakSelf.isLoading = NO;
            });
        } error:^(RCErrorCode status) {
            if (status == MSG_ROAMING_SERVICE_UNAVAILABLE) {
                msgRoamingServiceAvailable = NO;
            }
            [weakSelf.collectionViewHeader stopAnimating];
            weakSelf.isLoading = NO;
            NSLog(@"load remote history message failed(%zd)", status);
        }];
    } else {
    __messageArray =
      [[RCIMClient sharedRCIMClient] getHistoryMessages:_conversationType
                                               targetId:_targetId
                                        oldestMessageId:lastMessageId
                                                  count:10];
        [self sendReadReceiptResponseForMessages:__messageArray];
        if (__messageArray.count < 10) {
            self.loadHistoryMessageFromRemote = YES;
        }
        [self handleMessagesAfterLoadMore:__messageArray];
        _isLoading = NO;
        [self.collectionViewHeader stopAnimating];
    }
}
- (void)handleMessagesAfterLoadMore:(NSArray *)__messageArray {
    for (int i = 0; i < __messageArray.count; i++) {
        RCMessage *rcMsg = [__messageArray objectAtIndex:i];
        RCMessageModel *model = [RCMessageModel modelWithMessage:rcMsg];
        if ([model isKindOfClass:[RCCustomerServiceMessageModel class]]) {
            RCCustomerServiceMessageModel *csModel = (RCCustomerServiceMessageModel *)model;
            [csModel disableEvaluate];
        }
        [self pushOldMessageModel:model];
    }
    
    self.scrollNum++;
    if (self.scrollNum * 10 + 10> self.unReadMessage) {
        
        if (self.unReadButton != nil && self.enableUnreadMessageIcon) {
            NSInteger index = self.conversationDataRepository.count - self.sendOrReciveMessageNum - self.unReadMessage ;
            
            if (self.conversationDataRepository.count>index) {
                RCOldMessageNotificationMessage *oldMessageTip=[[RCOldMessageNotificationMessage alloc] init];
                RCMessage *oldMessage = [[RCMessage alloc] initWithType:self.conversationType targetId:self.targetId direction:MessageDirection_SEND messageId:-1 content:oldMessageTip];
                RCMessageModel *model = [RCMessageModel modelWithMessage:oldMessage];
                RCMessageModel *lastMessageModel = [self.conversationDataRepository objectAtIndex:index];
                model.messageId = lastMessageModel.messageId;
                [self.conversationDataRepository insertObject:model atIndex:index];
            }
            
            [self.unReadButton removeFromSuperview];
            self.unReadButton = nil;
        }
        self.unReadMessage = 0;
        
    }
    [self figureOutAllConversationDataRepository];
    [self.conversationMessageCollectionView reloadData];
    
      if (_conversationDataRepository != nil &&
          _conversationDataRepository.count > 0 &&
          [self.conversationMessageCollectionView numberOfItemsInSection:0] >=
              __messageArray.count - 1) {
        NSIndexPath *indexPath =
            [NSIndexPath indexPathForRow:__messageArray.count - 1 inSection:0];
        [self.conversationMessageCollectionView
            scrollToItemAtIndexPath:indexPath
                   atScrollPosition:UICollectionViewScrollPositionTop
                           animated:NO];
      }
}

- (void)willDisplayMessageCell:(RCMessageBaseCell *)cell atIndexPath:(NSIndexPath *)indexPath {
}

//历史遗留接口
- (void)willDisplayConversationTableCell:(RCMessageBaseCell *)cell
                             atIndexPath:(NSIndexPath *)indexPath {

}

- (RCMessageBaseCell *)rcConversationCollectionView:
                           (UICollectionView *)collectionView
                             cellForItemAtIndexPath:(NSIndexPath *)indexPath {

  RCMessageModel *model =
      [self.conversationDataRepository objectAtIndex:indexPath.row];
  // RCMessageContent *messageContent = model.content;
  RCMessageCell *__cell = [collectionView
      dequeueReusableCellWithReuseIdentifier:rcUnknownMessageCellIndentifier
                                forIndexPath:indexPath];
  [__cell setDataModel:model];
  return __cell;
}

- (CGSize)
rcConversationCollectionView:(UICollectionView *)collectionView
                      layout:(UICollectionViewLayout *)collectionViewLayout
      sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
  RCMessageModel *model =
      [self.conversationDataRepository objectAtIndex:indexPath.row];
  CGFloat __width = CGRectGetWidth(collectionView.frame);
  CGFloat __height = 0;
  CGFloat maxMessageLabelWidth = __width - 30 * 2;
  NSString *localizedMessage = NSLocalizedStringFromTable(
      @"unknown_message_cell_tip", @"RongCloudKit", nil);
  CGSize __textSize = [RCKitUtility getTextDrawingSize:localizedMessage font:[UIFont systemFontOfSize:14] constrainedSize:CGSizeMake(maxMessageLabelWidth, 2000)];
  __textSize = CGSizeMake(ceilf(__textSize.width), ceilf(__textSize.height));
  CGSize __labelSize = CGSizeMake(__textSize.width + 5, __textSize.height + 5);

  //上边距
  __height = __height + 10;

  if (model.isDisplayMessageTime) {
    __height = __height + 20 + 25;
  }
  __height = __height + __labelSize.height;
  //下边距
  __height = __height + 10;
  return CGSizeMake(collectionView.bounds.size.width, __height);
}

//点击cell
- (void)didTapMessageCell:(RCMessageModel *)model {
  DebugLog(@"%s", __FUNCTION__);
  if (nil == model) {
    return;
  }

  RCMessageContent *_messageContent = model.content;

  if ([_messageContent isMemberOfClass:[RCImageMessage class]]) {
    [self presentImagePreviewController:model];

  } else if ([_messageContent isMemberOfClass:[RCVoiceMessage class]]) {
    RCMessageModel *rcMsg;
    for (int i= 0; i < self.conversationDataRepository.count; i++) {
      rcMsg = [self.conversationDataRepository objectAtIndex:i];
      if (model.messageId == rcMsg.messageId) {
        rcMsg.receivedStatus = ReceivedStatus_LISTENED;
        dispatch_async(dispatch_get_main_queue(), ^{
          [[NSNotificationCenter defaultCenter] postNotificationName:kNotificationPlayVoice
                                                              object:@(model.messageId)];

        });
        break;
      }
    }
  } else if ([_messageContent isMemberOfClass:[RCLocationMessage class]]) {
    // Show the location view controller
    RCLocationMessage *locationMessage = (RCLocationMessage *)(_messageContent);
    [self presentLocationViewController:locationMessage];
  } else if ([_messageContent isMemberOfClass:[RCTextMessage class]]) {
    // link

    // phoneNumber
  } else if ([self isExtensionCell:_messageContent]) {
    [[RongIMKitExtensionManager sharedManager] didTapMessageCell:model];
  }
  else if ([_messageContent isMemberOfClass:[RCFileMessage class]]) {
    [self presentFilePreviewViewController:model];
  } else if ([_messageContent isMemberOfClass:[RCCSPullLeaveMessage class]]){
    if (self.config.leaveMessageType == RCCSLMNative && self.config.leaveMessageNativeInfo.count > 0) {
      RCCSLeaveMessageController *leaveMsgVC = [[RCCSLeaveMessageController alloc] init];
      leaveMsgVC.leaveMessageConfig = self.config.leaveMessageNativeInfo;
      leaveMsgVC.targetId = self.targetId;
      leaveMsgVC.conversationType = self.conversationType;
      __weak typeof(self) weakSelf = self;
      [leaveMsgVC setLeaveMessageSuccess:^{
        RCInformationNotificationMessage *warningMsg =
        [RCInformationNotificationMessage
         notificationWithMessage:@"您已提交留言。" extra:nil];
        RCMessage *savedMsg = [[RCIMClient sharedRCIMClient]
                      insertOutgoingMessage:weakSelf.conversationType targetId:weakSelf.targetId
                      sentStatus:SentStatus_SENT content:warningMsg];
        [weakSelf appendAndDisplayMessage:savedMsg];
      }];
      [self.navigationController pushViewController:leaveMsgVC animated:YES];
    }else if(self.config.leaveMessageType == RCCSLMWeb){
      [RCKitUtility openURLInSafariViewOrWebView:self.config.leaveMessageWebUrl base:self];
    }
  }
}

- (void)didTapUrlInMessageCell:(NSString *)url model:(RCMessageModel *)model {
  [RCKitUtility openURLInSafariViewOrWebView:url base:self];
}

- (void)didTapPhoneNumberInMessageCell:(NSString *)phoneNumber
                                 model:(RCMessageModel *)model {
  NSString *phoneStr = [phoneNumber stringByReplacingOccurrencesOfString:@" " withString:@""];
  [[UIApplication sharedApplication] openURL:[NSURL URLWithString:phoneStr]];
}

//点击头像
- (void)didTapCellPortrait:(NSString *)userId{
}

- (BOOL)canRecallMessageOfModel:(RCMessageModel *)model {
  long long cTime = [[NSDate date] timeIntervalSince1970]*1000;
  long long interval = cTime - model.sentTime > 0 ? cTime - model.sentTime : model.sentTime - cTime;
  return (interval <= [RCIM sharedRCIM].maxRecallDuration * 1000 && model.messageDirection == MessageDirection_SEND && [RCIM sharedRCIM].enableMessageRecall && model.sentStatus != SentStatus_SENDING && model.sentStatus != SentStatus_FAILED && model.sentStatus != SentStatus_CANCELED && (model.conversationType == ConversationType_PRIVATE || model.conversationType == ConversationType_GROUP || model.conversationType == ConversationType_DISCUSSION));
}

- (NSArray<UIMenuItem *> *)getLongTouchMessageCellMenuList:(RCMessageModel *)model {
  UIMenuItem *copyItem = [[UIMenuItem alloc]
                          initWithTitle:NSLocalizedStringFromTable(@"Copy", @"RongCloudKit", nil)
                          action:@selector(onCopyMessage:)];
  UIMenuItem *deleteItem = [[UIMenuItem alloc]
                            initWithTitle:NSLocalizedStringFromTable(@"Delete", @"RongCloudKit", nil)
                            action:@selector(onDeleteMessage:)];
  
  UIMenuItem *recallItem = [[UIMenuItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Recall", @"RongCloudKit", nil)
                                                      action:@selector(onRecallMessage:)];

  if ([model.content isMemberOfClass:[RCTextMessage class]]) {
    if ([self canRecallMessageOfModel:model]) {
      return [NSArray arrayWithObjects:copyItem, deleteItem, recallItem,nil];
    } else {
      return [NSArray arrayWithObjects:copyItem, deleteItem, nil];
    }
  } else {
    if ([self canRecallMessageOfModel:model]) {
      return [NSArray arrayWithObjects:deleteItem, recallItem,nil];
    } else {
      return @[ deleteItem ];
    }
  }
}

//长按消息内容
- (void)didLongTouchMessageCell:(RCMessageModel *)model inView:(UIView *)view {
  self.chatSessionInputBarControl.inputTextView.disableActionMenu = YES;
  self.longPressSelectedModel = model;

  CGRect rect = [self.view convertRect:view.frame fromView:view.superview];

  UIMenuController *menu = [UIMenuController sharedMenuController];
  [menu setMenuItems:[self getLongTouchMessageCellMenuList:model]];
  [menu setTargetRect:rect inView:self.view];
  [menu setMenuVisible:YES animated:YES];
}

- (void)didTapCancelUploadButton:(RCMessageModel *)model {
  [self cancelUploadMedia:model];
}

- (void)cancelUploadMedia:(RCMessageModel *)model {
  dispatch_async(dispatch_get_main_queue(), ^{
    [[RCIM sharedRCIM] cancelSendMediaMessage:model.messageId];
  });
}
/**
 *  UIResponder
 *
 *  @return
 */
- (BOOL)canBecomeFirstResponder {
  return YES;
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender {
  return [super canPerformAction:action withSender:sender];
}

- (NSInteger)findDataIndexFromMessageList:(RCMessageModel *)model {
  NSInteger index = 0;
  for (int i = 0; i < self.conversationDataRepository.count; i++) {
    RCMessageModel *msg = (self.conversationDataRepository)[i];
    if (msg.messageId == model.messageId &&  ![msg.content isKindOfClass:[RCOldMessageNotificationMessage class]]) {
      index = i;
      break;
    }
  }
  return index;
}

- (void)resendMessage:(RCMessageContent *)messageContent {
  if ([messageContent isMemberOfClass:RCImageMessage.class]) {
    RCImageMessage *imageMessage = (RCImageMessage *)messageContent;
    imageMessage.originalImage =
        [UIImage imageWithContentsOfFile:imageMessage.imageUrl];
    [self sendMessage:imageMessage pushContent:nil];
  } else if ([messageContent isMemberOfClass:RCFileMessage.class]) {
    RCFileMessage *fileMessage = (RCFileMessage *)messageContent;
    [self sendMessage:fileMessage pushContent:nil];
  } else {
    [self sendMessage:messageContent pushContent:nil];
  }
}

- (void)didTapmessageFailedStatusViewForResend:(RCMessageModel *)model {
  // resending message.
  DebugLog(@"%s", __FUNCTION__);

  RCMessageContent *content = model.content;
  long msgId = model.messageId;
  NSIndexPath *indexPath =
      [NSIndexPath indexPathForItem:[self findDataIndexFromMessageList:model]
                          inSection:0];
  [[RCIMClient sharedRCIMClient] deleteMessages:@[ @(msgId) ]];
  [self.conversationDataRepository removeObject:model];
  [self.conversationMessageCollectionView
      deleteItemsAtIndexPaths:[NSArray arrayWithObject:indexPath]];

    self.isNeedScrollToButtom=YES;
  [self resendMessage:content];
}

/**
 *  打开大图。开发者可以重写，自己下载并且展示图片。默认使用内置controller
 *
 *  @param imageMessageContent 图片消息内容
 */
- (void)presentImagePreviewController:(RCMessageModel *)model {
  RCImageSlideController *_imagePreviewVC =
      [[RCImageSlideController alloc] init];
  _imagePreviewVC.messageModel = model;

  UINavigationController *nav = [[UINavigationController alloc]
      initWithRootViewController:_imagePreviewVC];

  if (self.navigationController) {
    //导航和原有的配色保持一直
    UIImage *image = [self.navigationController.navigationBar
        backgroundImageForBarMetrics:UIBarMetricsDefault];

    [nav.navigationBar setBackgroundImage:image
                            forBarMetrics:UIBarMetricsDefault];
  }

  [self presentViewController:nav animated:YES completion:nil];
}

/**
 *  打开地理位置。开发者可以重写，自己根据经纬度打开地图显示位置。默认使用内置地图
 *
 *  @param locationMessageContent 位置消息
 */
- (void)presentLocationViewController:
    (RCLocationMessage *)locationMessageContent {
  //默认方法跳转
  RCLocationViewController *locationViewController =
      [[RCLocationViewController alloc] init];
  locationViewController.locationName = locationMessageContent.locationName;
  locationViewController.location = locationMessageContent.location;
  UINavigationController *navc = [[UINavigationController alloc]
      initWithRootViewController:locationViewController];
  if (self.navigationController) {
    //导航和原有的配色保持一直
    UIImage *image = [self.navigationController.navigationBar
        backgroundImageForBarMetrics:UIBarMetricsDefault];

    [navc.navigationBar setBackgroundImage:image
                             forBarMetrics:UIBarMetricsDefault];
  }
  [self presentViewController:navc animated:YES completion:NULL];
}

- (void)presentFilePreviewViewController:(RCMessageModel *)model {
    RCFilePreviewViewController *fileViewController = [[RCFilePreviewViewController alloc] init];
    fileViewController.messageModel = model;
    [self.navigationController pushViewController:fileViewController animated:YES];
}

- (void)updateForMessageSendOut:(RCMessage *)message {
    if ([message.content isKindOfClass:[RCImageMessage class]]) {
        RCImageMessage *img = (RCImageMessage *)message.content;
        img.originalImage = nil;
    }
    
    __weak typeof(&*self) __weakself = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        RCMessage *tempMessage = [__weakself willAppendAndDisplayMessage:message];
        [__weakself appendAndDisplayMessage:tempMessage];
    });
}

- (void)updateForMessageSendProgress:(int)progress messageId:(long)messageId {
    RCMessageCellNotificationModel *notifyModel = [[RCMessageCellNotificationModel alloc] init];
    notifyModel.actionName = CONVERSATION_CELL_STATUS_SEND_PROGRESS;
    notifyModel.messageId = messageId;
    notifyModel.progress = progress;
    
    dispatch_async(dispatch_get_main_queue(),^{
        [[NSNotificationCenter defaultCenter] postNotificationName:KNotificationMessageBaseCellUpdateSendingStatus
                                                            object:notifyModel];
    });
}

- (void)updateForMessageSendSuccess:(long)messageId content:(RCMessageContent *)content{
    DebugLog(@"message<%ld> send succeeded ", messageId);
    [self startNotSendMessageAlertTimer];
    RCMessageCellNotificationModel *notifyModel = [[RCMessageCellNotificationModel alloc] init];
    notifyModel.actionName = CONVERSATION_CELL_STATUS_SEND_SUCCESS;
    notifyModel.messageId = messageId;
    
    __weak typeof(&*self) __weakself = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        for (RCMessageModel *model in __weakself.conversationDataRepository) {
            if (model.messageId == messageId) {
                model.sentStatus = SentStatus_SENT;
                if (model.messageId > 0) {
                    RCMessage *message = [[RCIMClient sharedRCIMClient]getMessage:model.messageId];
                    if (message) {
                        model.sentTime = message.sentTime;
                        model.messageUId = message.messageUId;
                    }
                }
                break;
            }
        }
      if ([content isMemberOfClass:[RCTextMessage class]]) {
        if ([[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)] && (self.conversationType == ConversationType_GROUP || self.conversationType == ConversationType_DISCUSSION)) {
          int len = (int)self.conversationDataRepository.count-1;
          for(int i = len; i >= 0; i--){
            RCMessageModel *model = self.conversationDataRepository[i];
            if (model.messageId == messageId) {
              model.isCanSendReadReceipt = YES;
              if (!model.readReceiptInfo) {
                model.readReceiptInfo = [[RCReadReceiptInfo alloc]init];
              }
            }else{
              model.isCanSendReadReceipt = NO;
            }
          }
        }
      }
        [[NSNotificationCenter defaultCenter] postNotificationName:KNotificationMessageBaseCellUpdateSendingStatus
                                                            object:notifyModel];
        if ([[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)] && (self.conversationType == ConversationType_DISCUSSION || self.conversationType == ConversationType_GROUP)) {
            NSDictionary *statusDic = @{@"targetId":self.targetId,
                                        @"conversationType":@(self.conversationType),
                                        @"messageId": @(messageId)};
            [[NSNotificationCenter defaultCenter] postNotificationName:@"KNotificationMessageBaseCellUpdateCanReceiptStatus"
                                                                object:statusDic];
        }
        dispatch_after(
                       // 0.3s之后再刷新一遍，防止没有Cell绘制太慢
                       dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)),
                       dispatch_get_main_queue(), ^{
                           if ([[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)] && (self.conversationType == ConversationType_DISCUSSION || self.conversationType == ConversationType_GROUP)) {
                               NSDictionary *statusDic = @{@"targetId":self.targetId,
                                                           @"conversationType":@(self.conversationType),
                                                           @"messageId": @(messageId)};
                               [[NSNotificationCenter defaultCenter] postNotificationName:@"KNotificationMessageBaseCellUpdateCanReceiptStatus"
                                                                                   object:statusDic];
                           }
                           [[NSNotificationCenter defaultCenter]
                            postNotificationName:KNotificationMessageBaseCellUpdateSendingStatus
                            object:notifyModel];
                       });
        if(__weakself.chatSessionInputBarControl.inputTextView.text && __weakself.chatSessionInputBarControl.inputTextView.text.length > 0){
            [__weakself.chatSessionInputBarControl.emojiBoardView enableSendButton:YES];
        }else{
            [__weakself.chatSessionInputBarControl.emojiBoardView enableSendButton:NO];
        }
    });
    
    [self didSendMessage:0 content:content];
  
    if ([content isKindOfClass:[RCImageMessage class]]) {
        RCImageMessage *imageMessage = (RCImageMessage *)content;
        if (self.enableSaveNewPhotoToLocalSystem && _isTakeNewPhoto) {
            UIImage *image = [UIImage imageWithContentsOfFile:imageMessage.imageUrl];
            imageMessage = [RCImageMessage messageWithImage:image];
            [self saveNewPhotoToLocalSystemAfterSendingSuccess:imageMessage.originalImage];
        }
    }
    
}

- (void)updateForMessageSendError:(RCErrorCode)nErrorCode
                        messageId:(long)messageId
                          content:(RCMessageContent *)content {
    DebugLog(@"message<%ld> send failed error code %d", messageId, (int)nErrorCode);
    
    RCMessageCellNotificationModel *notifyModel = [[RCMessageCellNotificationModel alloc] init];
    notifyModel.actionName = CONVERSATION_CELL_STATUS_SEND_FAILED;
    notifyModel.messageId = messageId;
    
    __weak typeof(&*self) __weakself = self;
    dispatch_after(
                   // 发送失败0.3s之后再刷新，防止没有Cell绘制太慢
                   dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 0.3f),
                   dispatch_get_main_queue(), ^{
                       for (RCMessageModel *model in __weakself.conversationDataRepository) {
                           if (model.messageId == messageId) {
                               model.sentStatus = SentStatus_FAILED;
                               break;
                           }
                       }
                       [[NSNotificationCenter defaultCenter]
                        postNotificationName:
                        KNotificationMessageBaseCellUpdateSendingStatus
                        object: notifyModel];
                   });
    
    [self didSendMessage:nErrorCode content:content];
    
    RCInformationNotificationMessage *informationNotifiMsg = nil;
    if (NOT_IN_DISCUSSION == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:
                                NSLocalizedStringFromTable(@"NOT_IN_DISCUSSION", @"RongCloudKit",nil)
                                                                                   extra:nil];
    } else if (NOT_IN_GROUP == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:
                                NSLocalizedStringFromTable(@"NOT_IN_GROUP", @"RongCloudKit", nil)
                                                                                   extra:nil];
    } else if (NOT_IN_CHATROOM == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:
                                NSLocalizedStringFromTable(@"NOT_IN_CHATROOM", @"RongCloudKit", nil)
                                                                                   extra:nil];
    } else if (REJECTED_BY_BLACKLIST == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:
                                NSLocalizedStringFromTable(@"Message rejected", @"RongCloudKit", nil)
                                                                                   extra:nil];
    } else if (FORBIDDEN_IN_GROUP == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:
                                NSLocalizedStringFromTable(@"FORBIDDEN_IN_GROUP", @"RongCloudKit", nil)
                                                                                   extra:nil];
    } else if (FORBIDDEN_IN_CHATROOM == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:NSLocalizedStringFromTable(@"ForbiddenInChatRoom", @"RongCloudKit", nil)
                                                                                   extra:nil];
    } else if (KICKED_FROM_CHATROOM == nErrorCode) {
        informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:NSLocalizedStringFromTable(@"KickedFromChatRoom", @"RongCloudKit", nil)
                                                                                   extra:nil];
    }
    if (nil != informationNotifiMsg) {
        __block RCMessage *tempMessage = [[RCIMClient sharedRCIMClient] insertOutgoingMessage:self.conversationType targetId:self.targetId sentStatus:SentStatus_SENT content:informationNotifiMsg];
        dispatch_async(dispatch_get_main_queue(), ^{
            tempMessage = [__weakself willAppendAndDisplayMessage:tempMessage];
            if (tempMessage) {
                [__weakself appendAndDisplayMessage:tempMessage];
            }
        });
    }
}

- (void)updateForMessageSendCanceled:(long)messageId
                             content:(RCMessageContent *)content {
  DebugLog(@"message<%ld> canceled", messageId);
  
  RCMessageCellNotificationModel *notifyModel = [[RCMessageCellNotificationModel alloc] init];
  notifyModel.actionName = CONVERSATION_CELL_STATUS_SEND_CANCELED;
  notifyModel.messageId = messageId;
  
  __weak typeof(&*self) __weakself = self;
  dispatch_after(
                 // 发送失败0.3s之后再刷新，防止没有Cell绘制太慢
                 dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 0.3f),
                 dispatch_get_main_queue(), ^{
                   for (RCMessageModel *model in __weakself.conversationDataRepository) {
                     if (model.messageId == messageId) {
                       model.sentStatus = SentStatus_CANCELED;
                       break;
                     }
                   }
                   [[NSNotificationCenter defaultCenter]
                    postNotificationName:
                    KNotificationMessageBaseCellUpdateSendingStatus
                    object: notifyModel];
                 });
  
  [self didCancelMessage:content];
}

- (void)sendMessage:(RCMessageContent *)messageContent
        pushContent:(NSString *)pushContent {
    if (self.targetId == nil) {
        return;
    }
  
    messageContent = [self willSendMessage:messageContent];
    if (messageContent == nil) {
        return;
    }
  
  if ([messageContent isKindOfClass:[RCImageMessage class]]
      || [messageContent isKindOfClass:[RCFileMessage class]]) {
    [[RCIM sharedRCIM] sendMediaMessage:self.conversationType
                               targetId:self.targetId
                                content:messageContent
                            pushContent:pushContent
                               pushData:nil
                               progress:nil
                                success:nil
                                  error:nil
                                 cancel:nil];
  } else {
    [[RCIM sharedRCIM] sendMessage:self.conversationType
                        targetId:self.targetId
                         content:messageContent
                     pushContent:pushContent
                        pushData:nil
                         success:nil
                           error:nil];
  }
}

- (void)sendMediaMessage:(RCMessageContent *)messageContent
             pushContent:(NSString *)pushContent {
  if (!self.targetId) {
    return;
  }
  
  messageContent = [self willSendMessage:messageContent];
  if (messageContent == nil) {
    return;
  }
  
  [[RCIM sharedRCIM] sendMediaMessage:self.conversationType
                             targetId:self.targetId
                              content:messageContent
                          pushContent:pushContent
                             pushData:nil
                             progress:nil
                              success:nil
                                error:nil
                               cancel:nil];
}

- (void)sendMediaMessage:(RCMessageContent *)messageContent
             pushContent:(NSString *)pushContent
               appUpload:(BOOL)appUpload {
  if (!appUpload) {
    [self sendMessage:messageContent pushContent:pushContent];
    return;
  }
  
  __weak typeof(&*self) __weakself = self;
  
  RCMessage *rcMessage =
  [[RCIMClient sharedRCIMClient]
   sendMediaMessage:self.conversationType
   targetId:self.targetId
   content:messageContent
   pushContent:pushContent
   pushData:@""
   uploadPrepare:^(RCUploadMediaStatusListener *uploadListener) {
     [__weakself uploadMedia:uploadListener.currentMessage
              uploadListener:uploadListener];
   } progress:^(int progress, long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_SENDING),
                                 @"progress": @(progress)};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   } success:^(long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_SENT),
                                 @"content":messageContent};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   } error:^(RCErrorCode errorCode, long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_FAILED),
                                 @"error": @(errorCode),
                                 @"content":messageContent};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   } cancel:^(long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_CANCELED),
                                 @"content":messageContent};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   }];
  
  [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                      object:rcMessage
                                                    userInfo:nil];
  
}

- (void)uploadMedia:(RCMessage *)message
     uploadListener:(RCUploadMediaStatusListener *)uploadListener {
  uploadListener.errorBlock(-1);
  NSLog(@"error, App应该实现uploadMedia:uploadListener:函数用来上传媒体");
  //    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
  //        int i = 0;
  //        for (i = 0; i < 100; i++) {
  //            uploadListener.updateBlock(i);
  //            [NSThread sleepForTimeInterval:0.2];
  //        }
  //        uploadListener.successBlock(@"http://www.rongcloud.cn/images/newVersion/bannerInner.png?0717");
  //    });
}

//接口向后兼容 [[++
- (void)sendImageMessage:(RCImageMessage *)imageMessage
             pushContent:(NSString *)pushContent {
  [self sendMessage:imageMessage pushContent:pushContent];
}

- (void)sendImageMessage:(RCImageMessage *)imageMessage pushContent:(NSString *)pushContent appUpload:(BOOL)appUpload {
  if (!appUpload) {
    [self sendMessage:imageMessage pushContent:pushContent];
    return;
  }
  
  __weak typeof(&*self) __weakself = self;
  
  RCMessage *rcMessage =
  [[RCIMClient sharedRCIMClient]
   sendMediaMessage:self.conversationType
   targetId:self.targetId
   content:imageMessage
   pushContent:pushContent
   pushData:@""
   uploadPrepare:^(RCUploadMediaStatusListener *uploadListener) {
     [__weakself uploadMedia:uploadListener.currentMessage
              uploadListener:uploadListener];
   } progress:^(int progress, long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_SENDING),
                                 @"progress": @(progress)};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   } success:^(long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_SENT),
                                 @"content":imageMessage};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   } error:^(RCErrorCode errorCode, long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_FAILED),
                                 @"error": @(errorCode),
                                 @"content":imageMessage};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   } cancel:^(long messageId) {
     NSDictionary *statusDic = @{@"targetId":self.targetId,
                                 @"conversationType":@(self.conversationType),
                                 @"messageId": @(messageId),
                                 @"sentStatus": @(SentStatus_CANCELED),
                                 @"content":imageMessage};
     [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                         object:nil
                                                       userInfo:statusDic];
   }];
  
  [[NSNotificationCenter defaultCenter] postNotificationName:@"RCKitSendingMessageNotification"
                                                      object:rcMessage
                                                    userInfo:nil];
}

- (void)uploadImage:(RCMessage *)message uploadListener:(RCUploadImageStatusListener *)uploadListener {
  uploadListener.errorBlock(-1);
  NSLog(@"error, App应该实现uploadImage函数用来上传图片");
  //    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
  //        int i = 0;
  //        for (i = 0; i < 100; i++) {
  //            uploadListener.updateBlock(i);
  //            [NSThread sleepForTimeInterval:0.2];
  //        }
  //        uploadListener.successBlock(@"http://www.rongcloud.cn/images/newVersion/bannerInner.png?0717");
  //    });
}
//接口向后兼容 --]]

- (void)receiveMessageHasReadNotification:(NSNotification *)notification {
  NSNumber *ctype = [notification.userInfo objectForKey:@"cType"];
  NSNumber *time = [notification.userInfo objectForKey:@"messageTime"];
  NSString *targetId = [notification.userInfo objectForKey:@"tId"];
  
  if (ctype.intValue == (int)self.conversationType &&
      [targetId isEqualToString:self.targetId]) {
    // TODO:通知UI消息已读
    dispatch_async(dispatch_get_main_queue(), ^{
      for (RCMessageModel *model in self.conversationDataRepository) {
        if (model.messageDirection == MessageDirection_SEND &&
            model.sentTime <= time.longLongValue &&
            model.sentStatus == SentStatus_SENT) {
          RCMessageCellNotificationModel *notifyModel =
          [[RCMessageCellNotificationModel alloc] init];
          notifyModel.actionName = CONVERSATION_CELL_STATUS_SEND_HASREAD;
          model.sentStatus = SentStatus_READ;
          notifyModel.messageId = model.messageId;
          [[NSNotificationCenter defaultCenter]
           postNotificationName:
           KNotificationMessageBaseCellUpdateSendingStatus
           object:notifyModel];
        }
      }
    });
  }
}

- (void)didReceiveRecallMessageNotification:(NSNotification *)notification {
  dispatch_async(dispatch_get_main_queue(), ^{
    if([RCVoicePlayer defaultPlayer].isPlaying
       && [RCVoicePlayer defaultPlayer].messageId == [notification.object longValue])
    {
      [[RCVoicePlayer defaultPlayer] stopPlayVoice];
    }
    
    [self reloadRecalledMessage:[notification.object longValue]];
  });
}

- (void)didReceiveMessageNotification:(NSNotification *)notification {
  __block RCMessage *rcMessage = notification.object;
  RCMessageModel *model = [RCMessageModel modelWithMessage:rcMessage];
    NSDictionary *leftDic = notification.userInfo;
    //进入聊天室第一次拉取消息完成需要滑动到最下方
    if (self.conversationType == ConversationType_CHATROOM && !self.isChatRoomHistoryMessageLoaded) {
        
        if (leftDic && [leftDic[@"left"] isEqual:@(0)]) {
            self.isNeedScrollToButtom = YES;
            self.isChatRoomHistoryMessageLoaded = YES;
        }
    }

  if (model.conversationType == self.conversationType &&
      [model.targetId isEqual:self.targetId]) {
    [self startNotReciveMessageAlertTimer];
    if (self.isConversationAppear) {
        if (self.conversationType != ConversationType_CHATROOM && rcMessage.messageId > 0) {
            [[RCIMClient sharedRCIMClient] setMessageReceivedStatus:rcMessage.messageId receivedStatus:ReceivedStatus_READ];
        }
    }
      Class messageContentClass = model.content.class;
      
      NSInteger persistentFlag = [messageContentClass persistentFlag];
      //如果开启消息回执，收到消息要发送已读消息，发送失败存入数据库
    if (leftDic && [leftDic[@"left"] isEqual:@(0)]) {
      if (self.isConversationAppear &&
          [self.targetId isEqualToString:model.targetId] &&
          self.conversationType == model.conversationType &&
          model.messageDirection == MessageDirection_RECEIVE &&
          (persistentFlag & MessagePersistent_ISPERSISTED)) {
        if ([[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)] && self.conversationType == ConversationType_PRIVATE) {
          [[RCIMClient sharedRCIMClient] sendReadReceiptMessage:self.conversationType
                                                       targetId:self.targetId
                                                           time:model.sentTime
                                                        success:nil
                                                          error:nil];
        }
      }
    }
    _hasReceiveNewMessage = YES;

    __weak typeof(&*self) __blockSelf = self;

    dispatch_async(dispatch_get_main_queue(), ^{
        [self clearOldestMessagesWhenMemoryWarning];
        rcMessage = [__blockSelf willAppendAndDisplayMessage:rcMessage];
        if (rcMessage) {
            [__blockSelf appendAndDisplayMessage:rcMessage];
            UIMenuController *menu = [UIMenuController sharedMenuController];
            menu.menuVisible=NO;
            // 是否显示右下未读消息数
            if (self.enableNewComingMessageIcon == YES && (persistentFlag & MessagePersistent_ISPERSISTED)) {
                if (![self isAtTheBottomOfTableView]) {
                    self.unreadNewMsgCount ++ ;
                    [self updateUnreadMsgCountLabel];
                }
            }
        }
      });
  } else {
    [self notifyUpdateUnreadMessageCount];
  }
}


//数量不可能无限制的大，这里限制收到消息过多时，就对显示消息数量进行限制。
//用户可以手动下拉更多消息，查看更多历史消息。
-(void)clearOldestMessagesWhenMemoryWarning {
    if (self.conversationDataRepository.count>300) {
        NSRange range = NSMakeRange(0, 200);
        [self.conversationDataRepository removeObjectsInRange:range];
        [self.conversationMessageCollectionView reloadData];
    }
}


- (void)didSendingMessageNotification:(NSNotification *)notification {
    RCMessage *rcMessage = notification.object;
    NSDictionary *statusDic = notification.userInfo;
    
    if (rcMessage) {
        // 插入消息
        if (rcMessage.conversationType == self.conversationType
            && [rcMessage.targetId isEqual:self.targetId]) {
            [self updateForMessageSendOut:rcMessage];
        }
    } else if (statusDic) {
        // 更新消息状态
        NSNumber *conversationType = statusDic[@"conversationType"];
        NSString *targetId = statusDic[@"targetId"];
        if (conversationType.intValue == self.conversationType
            && [targetId isEqual:self.targetId]) {
            NSNumber *messageId = statusDic[@"messageId"];
            NSNumber *sentStatus = statusDic[@"sentStatus"];
            if (sentStatus.intValue == SentStatus_SENDING) {
                NSNumber *progress = statusDic[@"progress"];
                [self updateForMessageSendProgress:progress.intValue messageId:messageId.longValue];
            } else if (sentStatus.intValue == SentStatus_SENT) {
                RCMessageContent *content = statusDic[@"content"];
                [self updateForMessageSendSuccess:messageId.longValue
                                          content:content];
            } else if (sentStatus.intValue == SentStatus_FAILED) {
                NSNumber *errorCode = statusDic[@"error"];
                RCMessageContent *content = statusDic[@"content"];
                [self updateForMessageSendError:errorCode.intValue
                                      messageId:messageId.longValue
                                        content:content];
            } else if (sentStatus.intValue == SentStatus_CANCELED) {
              RCMessageContent *content = statusDic[@"content"];
              [self updateForMessageSendCanceled:messageId.longValue content:content];
            }
        }
    }
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning];
  // Dispose of any resources that can be recreated.
}

- (RCMessageContent *)willSendMessage:(RCMessageContent *)message {
  DebugLog(@"super %s", __FUNCTION__);
  return message;
}

- (RCMessage *)willAppendAndDisplayMessage:(RCMessage *)message {
  DebugLog(@"super %s", __FUNCTION__);
  return message;
}

- (void)didSendMessage:(NSInteger)status
               content:(RCMessageContent *)messageContent {
  DebugLog(@"super %s, %@", __FUNCTION__, messageContent);
}

- (void)didCancelMessage:(RCMessageContent *)messageContent {
  DebugLog(@"super %s, %@", __FUNCTION__, messageContent);
}

#pragma mark <RCChatSessionInputBarControlDataSource>

- (void)getSelectingUserIdList:
            (void (^)(NSArray<NSString *> *userIdList))completion
                   functionTag:(NSInteger)functionTag {
  switch (functionTag) {
  case INPUT_MENTIONED_SELECT_TAG: {
    if (self.conversationType == ConversationType_DISCUSSION) {
      [[RCIMClient sharedRCIMClient] getDiscussion:self.targetId
          success:^(RCDiscussion *discussion) {
            completion(discussion.memberIdList);
          }
          error:^(RCErrorCode status) {
            completion(nil);
          }];
    } else if (self.conversationType == ConversationType_GROUP) {
      if ([[RCIM sharedRCIM].groupMemberDataSource
           respondsToSelector:@selector(getAllMembersOfGroup:result:)]) {
        [[RCIM sharedRCIM]
         .groupMemberDataSource
         getAllMembersOfGroup:self.targetId
         result:^(NSArray<NSString *> *userIdList) {
           completion(userIdList);
         }];
      } else {
        completion(nil);
      }
    }
  } break;
  default: { completion(nil); } break;
  }
}

- (RCUserInfo *)getSelectingUserInfo:(NSString *)userId {
  if (self.conversationType == ConversationType_GROUP) {
    return [[RCUserInfoCacheManager sharedManager] getUserInfo:userId inGroupId:self.targetId];
  } else {
    return [[RCUserInfoCacheManager sharedManager] getUserInfo:userId];
  }
}

#pragma mark <RCChatSessionInputBarControlDelegate>

- (void)chatInputBar:(RCChatSessionInputBarControl *)chatInputBar shouldChangeFrame:(CGRect)frame {
  CGRect collectionViewRect = self.conversationMessageCollectionView.frame;
  collectionViewRect.size.height =
      CGRectGetMinY(frame) - collectionViewRect.origin.y;
  [self.conversationMessageCollectionView setFrame:collectionViewRect];
  [self.unreadRightBottomIcon setFrame:CGRectMake(self.view.frame.size.width-5.5-35,self.chatSessionInputBarControl.frame.origin.y-12-35,35,35)];
  [self scrollToBottomAnimated:NO];
}

- (void)inputTextViewDidTouchSendKey:(UITextView *)inputTextView {
  RCTextMessage *rcTextMessage = [RCTextMessage messageWithContent:inputTextView.text];

  rcTextMessage.mentionedInfo = self.chatSessionInputBarControl.mentionedInfo;
  [self sendMessage:rcTextMessage pushContent:nil];
}

- (void)inputTextView:(UITextView *)inputTextView
    shouldChangeTextInRange:(NSRange)range
            replacementText:(NSString *)text {
  if ([RCIM sharedRCIM].enableTypingStatus && ![text isEqualToString:@"\n"]) {
    [[RCIMClient sharedRCIMClient]
        sendTypingStatus:self.conversationType
                targetId:self.targetId
             contentType:[RCTextMessage getObjectName]];
  }
}

-(void)setChatSessionInputBarStatus:(KBottomBarStatus)inputBarStatus animated:(BOOL)animated {
  [self.chatSessionInputBarControl updateStatus:inputBarStatus animated:animated];
}

- (void)pluginBoardView:(RCPluginBoardView *)pluginBoardView
     clickedItemWithTag:(NSInteger)tag {
  switch (tag) {
    case PLUGIN_BOARD_ITEM_ALBUM_TAG: {
      [self openSystemAlbum];
    } break;
    case PLUGIN_BOARD_ITEM_CAMERA_TAG: {
      [self openSystemCamera];
    } break;
    case PLUGIN_BOARD_ITEM_LOCATION_TAG: {
      [self openLocationPicker];
    } break;
    case PLUGIN_BOARD_ITEM_FILE_TAG: {
      [self openFileSelector];
    } break;
    case PLUGIN_BOARD_ITEM_EVA_TAG: {
      if (self.config.evaType == EVA_UNIFIED) {
        [self showEvaView];
      }
    } break;
    default: {
      [self openDynamicFunction:tag];
    } break;
  }
}

- (void)presentViewController:(UIViewController *)viewController
                  functionTag:(NSInteger)functionTag {
  switch (functionTag) {
  case PLUGIN_BOARD_ITEM_ALBUM_TAG:
  case PLUGIN_BOARD_ITEM_CAMERA_TAG:
  case PLUGIN_BOARD_ITEM_LOCATION_TAG:
  case PLUGIN_BOARD_ITEM_FILE_TAG:
  case INPUT_MENTIONED_SELECT_TAG: {
    [self.navigationController presentViewController:viewController
                                            animated:YES
                                          completion:nil];
  } break;
  default: { } break; }
}

- (void)openSystemAlbum {
  [self.chatSessionInputBarControl openSystemAlbum];
}

- (void)openSystemCamera {
  [self.chatSessionInputBarControl openSystemCamera];
}

- (void)openLocationPicker {
  [self.chatSessionInputBarControl openLocationPicker];
}

- (void)openFileSelector {
  [self.chatSessionInputBarControl openFileSelector];
}

- (void)showEvaView{
  self.evaluateView = [[RCCSEvaluateView alloc] initWithFrame:CGRectZero showSolveView:self.config.reportResolveStatus];
  __weak typeof(self) weakSelf = self;
  [self.evaluateView setEvaluateResult:^(int source, int solveStatus, NSString *suggest) {
    [[RCIMClient sharedRCIMClient] evaluateCustomerService:weakSelf.targetId dialogId:nil starValue:source suggest:suggest resolveStatus:solveStatus];
  }];
  [self.evaluateView show];
}

- (void)openDynamicFunction:(NSInteger)functionTag {
  [self.chatSessionInputBarControl openDynamicFunction:functionTag];
}

- (void)emojiView:(RCEmojiBoardView *)emojiView
  didTouchedEmoji:(NSString *)touchedEmoji {
  
  if ([RCIM sharedRCIM].enableTypingStatus) {
    [[RCIMClient sharedRCIMClient] sendTypingStatus:self.conversationType
                                           targetId:self.targetId
                                        contentType:[RCTextMessage getObjectName]];
  }
  
}

- (void)emojiView:(RCEmojiBoardView *)emojiView
didTouchSendButton:(UIButton *)sendButton {
  
  RCTextMessage *rcTextMessage = [RCTextMessage
                                  messageWithContent:self.chatSessionInputBarControl.inputTextView.text];
  rcTextMessage.mentionedInfo = self.chatSessionInputBarControl.mentionedInfo;
  
  [self sendMessage:rcTextMessage pushContent:nil];
}

//语音消息开始录音
- (void)recordDidBegin {
  [[NSNotificationCenter defaultCenter] postNotificationName:kNotificationStopVoicePlayer object:nil];
  
  if ([RCIM sharedRCIM].enableTypingStatus) {
    [[RCIMClient sharedRCIMClient] sendTypingStatus:self.conversationType
                                           targetId:self.targetId
                                        contentType:[RCVoiceMessage getObjectName]];
  }
  
  [self onBeginRecordEvent];
}

//语音消息录音结束
- (void)recordDidEnd:(NSData *)recordData duration:(long)duration error:(NSError *)error {
  if (error == nil) {
    RCVoiceMessage *voiceMessage = [RCVoiceMessage messageWithAudio:recordData
                                                           duration:duration];
    [self sendMessage:voiceMessage pushContent:nil];
  }
  
  [self onEndRecordEvent];
}

//接口向后兼容[[++
- (void)onBeginRecordEvent {
}

-(void)onEndRecordEvent {
}
//接口向后兼容--]]

- (void)fileDidSelect:(NSArray *)filePathList {
  [self becomeFirstResponder];
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    for (NSString *filePath in filePathList) {
      RCFileMessage *fileMessage = [RCFileMessage messageWithFile:filePath];
      [[RCIM sharedRCIM] sendMediaMessage:self.conversationType
                                 targetId:self.targetId
                                  content:fileMessage
                              pushContent:nil
                                 pushData:nil
                                 progress:nil
                                  success:nil
                                    error:nil
                                   cancel:nil];
      [NSThread sleepForTimeInterval:0.5];
    }
  });
}

- (void)imageDidSelect:(NSArray *)selectedImages fullImageRequired:(BOOL)full {
  [self becomeFirstResponder];
  _isTakeNewPhoto = NO;
  //耗时操作异步执行，以免阻塞主线程
  __weak RCConversationViewController *weakSelf = self;
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    for (int i = 0; i < selectedImages.count; i++) {
      UIImage *image = [selectedImages objectAtIndex:i];
      RCImageMessage *imagemsg = [RCImageMessage messageWithImage:image];
      imagemsg.full = full;
      [weakSelf sendMessage:imagemsg pushContent:nil];
      [NSThread sleepForTimeInterval:0.5];
    }
  });
}

- (void)locationDidSelect:(CLLocationCoordinate2D)location
             locationName:(NSString *)locationName
            mapScreenShot:(UIImage *)mapScreenShot {
  [self becomeFirstResponder];
  RCLocationMessage *locationMessage =
  [RCLocationMessage messageWithLocationImage:mapScreenShot
                                     location:location
                                 locationName:locationName];
  [self sendMessage:locationMessage pushContent:nil];
}

//选择相册图片或者拍照回调
- (void)imageDidCapture:(UIImage *)image {
  [self becomeFirstResponder];
  RCImageMessage *imageMessage = [RCImageMessage messageWithImage:image];
  _isTakeNewPhoto = YES;
  [self sendMessage:imageMessage pushContent:nil];
}


-(void)sendTypingStatusTimerFired{
    isCanSendTypingMessage = YES;
}


- (void)tabRightBottomMsgCountIcon:(UIGestureRecognizer *)gesture {
    if (gesture.state == UIGestureRecognizerStateEnded) {
        
        [self scrollToBottomAnimated:YES];
    }
}

- (void)tap4ResetDefaultBottomBarStatus:
    (UIGestureRecognizer *)gestureRecognizer {
  if (gestureRecognizer.state == UIGestureRecognizerStateEnded) {
    if (self.chatSessionInputBarControl.currentBottomBarStatus != KBottomBarDefaultStatus && self.chatSessionInputBarControl.currentBottomBarStatus != KBottomBarRecordStatus) {
      [self.chatSessionInputBarControl resetToDefaultStatus];
    }
  }
}

/**
 *  复制
 *
 *  @param sender
 */
- (void)onCopyMessage:(id)sender {
  // self.msgInputBar.msgColumnTextView.disableActionMenu = NO;
  self.chatSessionInputBarControl.inputTextView.disableActionMenu = NO;
  UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
  // RCMessageCell* cell = _RCMessageCell;
  //判断是否文本消息
  if ([_longPressSelectedModel.content isKindOfClass:[RCTextMessage class]]) {
    RCTextMessage *text = (RCTextMessage *)_longPressSelectedModel.content;
    [pasteboard setString:text.content];
  }
}
/**
 *  删除
 *
 *  @param sender
 */
- (void)onDeleteMessage:(id)sender {
  // self.msgInputBar.msgColumnTextView.disableActionMenu = NO;
  self.chatSessionInputBarControl.inputTextView.disableActionMenu = NO;
  // RCMessageCell* cell = _RCMessageCell;
  RCMessageModel *model = _longPressSelectedModel;
  // RCMessageContent *content = _longPressSelectedModel.content;
  
    //删除消息时如果是当前播放的消息就停止播放
    if ([RCVoicePlayer defaultPlayer].isPlaying
        && [RCVoicePlayer defaultPlayer].messageId == model.messageId) {
        [[RCVoicePlayer defaultPlayer] stopPlayVoice];
    }
  [self deleteMessage:model];
}
- (void)reloadRecalledMessage:(long)recalledMsgId {
  int index = -1;
  RCMessageModel *msgModel;
  for (int i = 0; i< self.conversationDataRepository.count; i++)
  {
    msgModel = [self.conversationDataRepository objectAtIndex:i];
    if (msgModel.messageId == recalledMsgId) {
      index = i;
      break;
    }
  }
  
  if (index >= 0) {
    NSIndexPath *indexPath =
    [NSIndexPath indexPathForItem:[self findDataIndexFromMessageList:msgModel]
                        inSection:0];
    
    [self.conversationDataRepository removeObject:msgModel];
    RCMessage *newMsg = [[RCIMClient sharedRCIMClient] getMessage:recalledMsgId];
    RCMessageModel *newModel = [RCMessageModel modelWithMessage:newMsg];
    newModel.isDisplayMessageTime = msgModel.isDisplayMessageTime;
    newModel.isDisplayNickname = msgModel.isDisplayNickname;
    [self.conversationDataRepository insertObject:newModel atIndex:index];
    [self.conversationMessageCollectionView reloadItemsAtIndexPaths:@[indexPath]];
  }
}
/**
 *  撤回消息
 *
 *  @param sender
 */
- (void)onRecallMessage:(id)sender {
  self.chatSessionInputBarControl.inputTextView.disableActionMenu = NO;
  RCMessageModel *model = _longPressSelectedModel;
  [self recallMessage:model.messageId];
}

- (void)recallMessage:(long)messageId {
  RCMessage *msg = [[RCIMClient sharedRCIMClient]getMessage:messageId];
  if (msg.messageDirection != MessageDirection_SEND && msg.sentStatus != SentStatus_SENT) {
    NSLog(@"错误，只有发送成功的消息才能撤回！！！");
    return;
  }
  
  [self.view addSubview:self.rcImageProressView];
  [self.rcImageProressView setCenter:CGPointMake(self.view.bounds.size.width / 2, self.view.bounds.size.height / 2)];
  [self.rcImageProressView startAnimating];
    NSString *recallMessagePushContent = [NSString stringWithFormat:@"%@%@",[RCIM sharedRCIM].currentUserInfo.name, NSLocalizedStringFromTable(@"MessageRecalled", @"RongCloudKit", nil), nil];
  [[RCIMClient sharedRCIMClient]recallMessage:msg pushContent:recallMessagePushContent success:^(long messageId) {
    dispatch_async(dispatch_get_main_queue(), ^{
      if([RCVoicePlayer defaultPlayer].isPlaying
         && [RCVoicePlayer defaultPlayer].messageId == msg.messageId)
      {
        [[RCVoicePlayer defaultPlayer] stopPlayVoice];
      }
      
      [self reloadRecalledMessage:messageId];
      
      [self.rcImageProressView stopAnimating];
      [self.rcImageProressView removeFromSuperview];
    });
  } error:^(RCErrorCode errorcode) {
    dispatch_async(dispatch_get_main_queue(), ^{
        
        [self.rcImageProressView stopAnimating];
        [self.rcImageProressView removeFromSuperview];
        
        NSString *errorMsg = NSLocalizedStringFromTable(@"MessageRecallFailed", @"RongCloudKit", nil);
        NSString *Ok = NSLocalizedStringFromTable(@"Ok", @"RongCloudKit", nil);
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle:nil
                                   message:errorMsg
                                  delegate:nil
                         cancelButtonTitle:Ok
                         otherButtonTitles:nil, nil];
        
        [alert show];
      
    });
  }];
}

- (void)deleteMessage:(RCMessageModel *)model {
  long msgId = model.messageId;
  NSIndexPath *indexPath =
      [NSIndexPath indexPathForItem:[self findDataIndexFromMessageList:model]
                          inSection:0];
    [[RCIMClient sharedRCIMClient] deleteMessages:@[ @(msgId) ]];
    [self.conversationDataRepository removeObject:model];
    [self.conversationMessageCollectionView
     deleteItemsAtIndexPaths:[NSArray arrayWithObject:indexPath]];
    for (int i = 0; i < self.conversationDataRepository.count; i++) {
        RCMessageModel *msg = (self.conversationDataRepository)[i];
        if ([msg.content isKindOfClass:[RCOldMessageNotificationMessage class]]) {
            //如果“以上是历史消息”RCOldMessageNotificationMessage 上面或者下面没有消息了，把RCOldMessageNotificationMessage也删除
            if (self.conversationDataRepository.count <=i+1|| (i==0&& self.scrollNum>0)) {
                 NSIndexPath *oldMsgIndexPath =[NSIndexPath indexPathForItem:i
                                    inSection:0];
                [self.conversationDataRepository removeObject:msg];
                [self.conversationMessageCollectionView
                 deleteItemsAtIndexPaths:[NSArray arrayWithObject:oldMsgIndexPath]];
                
                //删除“以上是历史消息”之后，会话的第一条消息显示时间，并且调整高度
                if(i==0 && self.conversationDataRepository.count>0){
                    RCMessageModel *topMsg = (self.conversationDataRepository)[0];
                    topMsg.isDisplayMessageTime =YES;
                    topMsg.cellSize =CGSizeMake(topMsg.cellSize.width, topMsg.cellSize.height+30);
                    RCMessageCell * __cell = (RCMessageCell *)[self.conversationMessageCollectionView cellForItemAtIndexPath:[NSIndexPath indexPathForItem:0
                                                                                                                                                 inSection:0]];
                    if (__cell) {
                        [__cell setDataModel:topMsg];
                    }
                    [self.conversationMessageCollectionView reloadData];
                }
            }
            break;
        }
    }
}

- (void)notifyUnReadMessageCount:(NSInteger)count {
}

/**
 *  设置头像样式
 *
 *  @param avatarStyle avatarStyle
 */
- (void)setMessageAvatarStyle:(RCUserAvatarStyle)avatarStyle {
  [RCIM sharedRCIM].globalMessageAvatarStyle = avatarStyle;
}
/**
 *  设置头像大小
 *
 *  @param size size
 */
- (void)setMessagePortraitSize:(CGSize)size {
  [RCIM sharedRCIM].globalMessagePortraitSize = size;
}

- (void)notifyUpdateUnreadMessageCount {
  __weak typeof(&*self) __weakself = self;
  int count = 0;
  if (self.displayConversationTypeArray) {
    count = [[RCIMClient sharedRCIMClient]
        getUnreadCount:self.displayConversationTypeArray];
  }
  else {
    return;
  }

  dispatch_async(dispatch_get_main_queue(), ^{
    NSString *backString = nil;
    if (count > 0 && count < 1000) {
      backString =
          [NSString stringWithFormat:NSLocalizedStringFromTable(
                                         @"Back(%d)", @"RongCloudKit", nil),
                                     count];
    } else if (count >= 1000) {
      backString =
          NSLocalizedStringFromTable(@"Back(...)", @"RongCloudKit", nil);
    } else {
      backString = NSLocalizedStringFromTable(@"Back", @"RongCloudKit", nil);
    }

    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    backBtn.frame = CGRectMake(0, 6, 72, 23);
    UIImageView *backImg = [[UIImageView alloc]
        initWithImage:IMAGE_BY_NAMED(@"navigator_btn_back")];
    backImg.frame = CGRectMake(-10, 0, 22, 22);
    [backBtn addSubview:backImg];
    UILabel *backText =
        [[UILabel alloc] initWithFrame:CGRectMake(12, 0, 70, 22)];
    backText.text = backString;
    backText.font = [UIFont systemFontOfSize:15];
    [backText setBackgroundColor:[UIColor clearColor]];
    [backText setTextColor:[RCIM sharedRCIM].globalNavigationBarTintColor];
    [backBtn addSubview:backText];
      if (self.conversationType == ConversationType_CUSTOMERSERVICE) {
          [backBtn addTarget:__weakself
                      action:@selector(customerServiceLeftCurrentViewController)
            forControlEvents:UIControlEventTouchUpInside];
      } else {
          [backBtn addTarget:__weakself
                      action:@selector(leftBarButtonItemPressed:)
            forControlEvents:UIControlEventTouchUpInside];
      }

    UIBarButtonItem *leftButton =
        [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    [__weakself.navigationItem setLeftBarButtonItem:leftButton];
  });
}

- (void)saveNewPhotoToLocalSystemAfterSendingSuccess:(UIImage *)newImage {
}

- (BOOL)isAtTheBottomOfTableView {
    if (self.conversationMessageCollectionView.contentSize.height <= self.conversationMessageCollectionView.frame.size.height) {
        return YES;
    }

    if(self.conversationMessageCollectionView.contentOffset.y +200 >= (self.conversationMessageCollectionView.contentSize.height - self.conversationMessageCollectionView.frame.size.height)) {
        return YES;
    }else{
        return NO;
    }
}

//修复ios7下不断下拉加载历史消息偶尔崩溃的bug
- (BOOL)collectionView:(UICollectionView *)collectionView shouldHighlightItemAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

-(void)receivePlayVoiceFinishNotification:(NSNotification *)notification {
    if (self.enableContinuousReadUnreadVoice) {
        long messageId = [notification.object longValue];
        RCConversationType conversationType = [notification.userInfo[@"conversationType"] longValue];
        NSString *targetId = notification.userInfo[@"targetId"];
        
        if (messageId > 0
            && conversationType == self.conversationType
            && [targetId isEqualToString:self.targetId]) {
            
            RCMessageModel *rcMsg;
            int index =0;
            for (int i = 0; i < self.conversationDataRepository.count; i++) {
                rcMsg = [self.conversationDataRepository objectAtIndex:i];
                if (messageId < rcMsg.messageId
                    && [rcMsg.content isMemberOfClass:[RCVoiceMessage class]]
                    &&rcMsg.receivedStatus != ReceivedStatus_LISTENED
                    && rcMsg.messageDirection == MessageDirection_RECEIVE) {
                    index = i;
                    break;
                }
            }
            
            if (index != 0) {
                NSIndexPath *indexPath = [NSIndexPath indexPathForItem:index inSection:0] ;
                RCVoiceMessageCell *__cell = (RCVoiceMessageCell *)[self.conversationMessageCollectionView cellForItemAtIndexPath:indexPath];
                //如果是空说明被回收了，重新dequeue一个cell
                
                rcMsg.receivedStatus = ReceivedStatus_LISTENED;
                if (__cell) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        rcMsg.receivedStatus = ReceivedStatus_LISTENED;
                        [__cell setDataModel:rcMsg];
                        [__cell playVoice];
                    });
                } else {
                    __cell = (RCVoiceMessageCell *)[self.conversationMessageCollectionView dequeueReusableCellWithReuseIdentifier:[[RCVoiceMessage class] getObjectName] forIndexPath:indexPath];
                   
                    dispatch_async(dispatch_get_main_queue(), ^{
                        rcMsg.receivedStatus = ReceivedStatus_LISTENED;
                        [__cell setDataModel:rcMsg];
                        [__cell setDelegate:self];
                        [__cell playVoice];
                    });
                }
            }
        }
    }
    
}
- (void)onPublicServiceMenuItemSelected:(RCPublicServiceMenuItem *)selectedMenuItem {
    if (selectedMenuItem.type == RC_PUBLIC_SERVICE_MENU_ITEM_VIEW) {
      [RCKitUtility openURLInSafariViewOrWebView:selectedMenuItem.url base:self];
    }
    
    RCPublicServiceCommandMessage *command = [RCPublicServiceCommandMessage messageFromMenuItem:selectedMenuItem];
    if (command) {
        [[RCIMClient sharedRCIMClient] sendMessage:self.conversationType targetId:self.targetId content:command pushContent:nil pushData:nil success:^(long messageId) {
            
        } error:^(RCErrorCode nErrorCode, long messageId) {
            
        }];
    }
}

- (void)didTapUrlInPublicServiceMessageCell:(NSString *)url model:(RCMessageModel *)model {
  UIViewController *viewController = nil;
  url = [RCKitUtility checkOrAppendHttpForUrl:url];
  if (![RCIM sharedRCIM].embeddedWebViewPreferred && RC_IOS_SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"9.0")) {
    viewController = [[SFSafariViewController alloc] initWithURL:[NSURL URLWithString:url]];
  } else {
    viewController = [[RCIMClient sharedRCIMClient] getPublicServiceWebViewController:url];
  }
  [self didTapImageTxtMsgCell:url webViewController:viewController];
}

#pragma mark override
- (void)didTapImageTxtMsgCell:(NSString *)tapedUrl webViewController:(UIViewController *)rcWebViewController {
  if ([rcWebViewController isKindOfClass:[SFSafariViewController class]]) {
    [self presentViewController:rcWebViewController animated:YES completion:nil];
  } else {
    UIWindow *window = [[UIApplication sharedApplication] delegate].window;
    UINavigationController *navigationController = (UINavigationController *)window.rootViewController;
    [navigationController pushViewController:rcWebViewController animated:YES];
  }
}

- (void)resetBottomBarStatus {
  [self.chatSessionInputBarControl resetToDefaultStatus];
}

/****************** Custom Service Code Begin ******************/
- (void)robotSwitchButtonDidTouch {
    if (self.conversationType == ConversationType_CUSTOMERSERVICE) {
        [[RCIMClient sharedRCIMClient] switchToHumanMode:self.targetId];
        [self startNotSendMessageAlertTimer];
        [self startNotReciveMessageAlertTimer];
    }
}

- (void)switchRobotInputType:(BOOL)isRobotType {
    if (isRobotType) {
        [self.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlCSRobotType style:RC_CHAT_INPUT_BAR_STYLE_CONTAINER];
    } else {
      [self.chatSessionInputBarControl setInputBarType:RCChatSessionInputBarControlDefaultType style:RC_CHAT_INPUT_BAR_STYLE_SWITCH_CONTAINER_EXTENTION];
    }
}

- (void)didTapCustomerService:(RCMessageModel *)model RobotResoluved:(BOOL)isResolved {
    RCCustomerServiceMessageModel *csModel = (RCCustomerServiceMessageModel *)model;
    csModel.aleardyEvaluated = YES;
    [[RCIMClient sharedRCIMClient] evaluateCustomerService:model.targetId knownledgeId:csModel.evaluateId robotValue:YES suggest:nil];
    NSUInteger index = [self.conversationDataRepository indexOfObject:model];
    NSIndexPath *path = [NSIndexPath indexPathForRow:index inSection:0];
    [self.conversationMessageCollectionView reloadItemsAtIndexPaths:@[path]];
}

- (void)suspendCustomerService {
    [[RCIMClient sharedRCIMClient] stopCustomerService:self.targetId];
}

- (void)leftCustomerServiceWithEvaluate:(BOOL)needEvaluate {
    if (needEvaluate) {
        if ([self.csEnterDate timeIntervalSinceNow] >= -(self.csEvaInterval)) {
            needEvaluate = NO;
        }
        if (self.currentServiceStatus == RCCustomerService_RobotService && self.config.robotSessionNoEva) {
            needEvaluate = NO;
        } else if (self.currentServiceStatus == RCCustomerService_HumanService && self.config.humanSessionNoEva) {
            needEvaluate = NO;
        }
        
        if (self.humanEvaluated) {
            needEvaluate = NO;
        }
    }
    
    if (needEvaluate && self.currentServiceStatus != RCCustomerService_NoService && self.config.evaEntryPoint == RCCSEvaLeave) {
        [self resetBottomBarStatus];
        if (self.currentServiceStatus == RCCustomerService_HumanService) {
            self.humanEvaluated = YES;
        }
        [self commentCustomerServiceWithStatus:self.currentServiceStatus commentId:nil quitAfterComment:YES];
    } else {
        [self leftBarButtonItemPressed:nil];
    }
}

- (void)commentCustomerServiceWithStatus:(RCCustomerServiceStatus)serviceStatus commentId:(NSString *)commentId quitAfterComment:(BOOL)isQuit {
    if (serviceStatus == RCCustomerService_HumanService) {
        RCAdminEvaluationView *eva = [[RCAdminEvaluationView alloc] initWithDelegate:self];
        eva.quitAfterEvaluation = isQuit;
        eva.dialogId = commentId;
        [eva show];
    } else if (serviceStatus == RCCustomerService_RobotService) {
        RCRobotEvaluationView *eva = [[RCRobotEvaluationView alloc] initWithDelegate:self];
        eva.quitAfterEvaluation = isQuit;
        eva.knownledgeId = commentId;
        [eva show];
    }
}

- (void)customerServiceLeftCurrentViewController {
    if (self.conversationType == ConversationType_CUSTOMERSERVICE) {
        [self suspendCustomerService];
        [self leftCustomerServiceWithEvaluate:YES];
    } else {
        [self leftBarButtonItemPressed:nil];
    }
}

- (void)customerServiceWarning:(NSString *)warning quitAfterWarning:(BOOL)quit needEvaluate:(BOOL)needEvaluate needSuspend:(BOOL)needSuspend {
  [self.evaluateView hide];
    if (self.csAlertView) {
        [self.csAlertView dismissWithClickedButtonIndex:0];
        self.csAlertView = nil;
    }
    
    [self resetBottomBarStatus];

    RCCSAlertView *alert = [[RCCSAlertView alloc] initWithTitle:nil warning:warning delegate:self];
    int tag = 0;
    if (quit) {
        tag = 1;
    }
    if (needEvaluate) {
        tag = tag | (1 <<1);
    }
    if (needSuspend) {
        tag = tag | (1 << 2);
    }
    alert.tag = tag;
    self.csAlertView = alert;
    [alert show];
}

- (void)onCustomerServiceModeChanged:(RCCSModeType)newMode {
    
}

#pragma mark - RCCSAlertViewDelegate
- (void)willCSAlertViewDismiss:(RCCSAlertView *)view{
    if (view.tag & (1 << 2)) {
        [self suspendCustomerService];
    }
    if (view.tag & 0x001) {
        [self leftCustomerServiceWithEvaluate:((view.tag & (1 << 1)) > 0)];
    }
}

#pragma mark - RCAdminEvaluationViewDelegate
- (void)adminEvaluateViewCancel:(RCAdminEvaluationView *)view {
    if (view.quitAfterEvaluation) {
        [self leftBarButtonItemPressed:nil];
    }
}

- (void)adminEvaluateView:(RCAdminEvaluationView *)view didEvaluateValue:(int)starValues {
    if (starValues >= 0) {
        [[RCIMClient sharedRCIMClient] evaluateCustomerService:self.targetId dialogId:view.dialogId humanValue:starValues+1 suggest:nil];
    }
    if (view.quitAfterEvaluation) {
        [self leftBarButtonItemPressed:nil];
    }
}

#pragma mark - RCRobotEvaluationViewDelegate
- (void)robotEvaluateViewCancel:(RCRobotEvaluationView *)view {
    if (view.quitAfterEvaluation) {
        [self leftBarButtonItemPressed:nil];
    }
}

- (void)robotEvaluateView:(RCRobotEvaluationView *)view didEvaluateValue:(BOOL)isResolved{
    [[RCIMClient sharedRCIMClient] evaluateCustomerService:self.targetId knownledgeId:view.knownledgeId robotValue:isResolved suggest:nil];
    if (view.quitAfterEvaluation) {
        [self leftBarButtonItemPressed:nil];
    }
}
/****************** Custom Service Code End   ******************/

- (void)onTypingStatusChanged:(RCConversationType)conversationType
                     targetId:(NSString *)targetId
                       status:(NSArray *)userTypingStatusList {
    if (conversationType == self.conversationType
        && [targetId isEqualToString:self.targetId]&&[RCIM sharedRCIM].enableTypingStatus) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (userTypingStatusList == nil || userTypingStatusList.count == 0) {
                self.navigationItem.title = self.navigationTitle;
            } else {
                RCUserTypingStatus *typingStatus = (RCUserTypingStatus *)userTypingStatusList[0];
                if ([typingStatus.contentType isEqualToString:[RCTextMessage getObjectName]]) {
                    self.navigationItem.title = NSLocalizedStringFromTable(@"typing", @"RongCloudKit", nil);
                }else if ([typingStatus.contentType isEqualToString:[RCVoiceMessage getObjectName]]){
                    self.navigationItem.title = NSLocalizedStringFromTable(@"Speaking", @"RongCloudKit", nil);
                }
                
            }
        });
    }
}

- (void)handleAppResume {
  [self.conversationMessageCollectionView reloadData];
  [self syncReadStatus];
}

-(void)handleWillResignActive{
  [self.chatSessionInputBarControl endVoiceRecord];
}

- (void)didLongPressCellPortrait:(NSString *)userId {
  if ([userId isEqualToString:[RCIM sharedRCIM].currentUserInfo.userId]) {
    return;
  }

  [self.chatSessionInputBarControl addMentionedUser:[[RCUserInfoCacheManager sharedManager]
                                           getUserInfo:userId]];
  [self.chatSessionInputBarControl.inputTextView becomeFirstResponder];
}
//遍历@列表，根据修改字符的范围更新@信息的range
//- (void)updateAllMentionedRangeInfo:(NSRange)changedRange {
//  for (RCMentionedStringRangeInfo *mentionedInfo in self.chatSessionInputBarControl.mentionedRangeInfoList) {
//    NSRange mentionedStrRange = mentionedInfo.range;
//    if (mentionedStrRange.location >= changedRange.location) {
//      mentionedInfo.range =
//      NSMakeRange(mentionedInfo.range.location + changedRange.length,
//                  mentionedInfo.range.length);
//    }
//  }
//}


#pragma mark - 回执请求及响应处理， 同步阅读状态
- (void)sendReadReceipt {
  if (self.conversationType == ConversationType_PRIVATE &&
      [[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)]) {

    for (long i = self.conversationDataRepository.count - 1; i >= 0; i--) {
      RCMessageModel *model = self.conversationDataRepository[i];
      if (model.messageDirection == MessageDirection_RECEIVE) {
        [[RCIMClient sharedRCIMClient] sendReadReceiptMessage:self.conversationType
                                                     targetId:self.targetId
                                                         time:model.sentTime
                                                      success:nil
                                                        error:nil];
        break;
      }
    }
  }
}

- (void)syncReadStatus {
  if (![RCIM sharedRCIM].enableSyncReadStatus)
    return;
  
  //单聊如果开启了已读回执，同步阅读状态功能可以复用已读回执，不需要发送同步命令。
  if((self.conversationType == ConversationType_PRIVATE &&
     ![[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)])
     || self.conversationType == ConversationType_GROUP
     || self.conversationType == ConversationType_DISCUSSION){
    for (long i = self.conversationDataRepository.count - 1; i >= 0; i--) {
      RCMessageModel *model = self.conversationDataRepository[i];
      if (model.messageDirection == MessageDirection_RECEIVE) {
        [[RCIMClient sharedRCIMClient] syncConversationReadStatus:self.conversationType
                                                        targetId:self.targetId
                                                            time:model.sentTime
                                                         success:nil
                                                           error:nil];
        break;
      }
    }
  }
}


/**
 *  收到回执消息的响应，更新这条消息的已读数
 *
 *  @param notification notification description
 */
- (void)onReceiveMessageReadReceiptResponse:(NSNotification *)notification{
    NSDictionary *dic = notification.object;
    if ([self.targetId isEqualToString:dic[@"targetId"]] && self.conversationType == [dic[@"conversationType"] intValue]) {
        for (int i = 0;i < self.conversationDataRepository.count;i++) {
            RCMessageModel *model = self.conversationDataRepository[i];
            if ([model.messageUId isEqualToString:dic[@"messageUId"]]) {
                NSDictionary *readerList = dic[@"readerList"];
                model.readReceiptCount = readerList.count;
                model.readReceiptInfo = [[RCReadReceiptInfo alloc]init];
                model.readReceiptInfo.isReceiptRequestMessage = YES;
                model.readReceiptInfo.userIdList = [NSMutableDictionary dictionaryWithDictionary:readerList];
                RCMessageCellNotificationModel *notifyModel = [[RCMessageCellNotificationModel alloc] init];
                notifyModel.actionName = CONVERSATION_CELL_STATUS_SEND_READCOUNT;
                notifyModel.messageId = model.messageId;
                notifyModel.progress = readerList.count;
                
                dispatch_async(dispatch_get_main_queue(),^{
                    [[NSNotificationCenter defaultCenter] postNotificationName:KNotificationMessageBaseCellUpdateSendingStatus
                                                                        object:notifyModel];
                });
            }
        }
    }
}

/**
 *  收到消息请求回执，如果当前列表中包含需要回执的messageUId，发送回执响应
 *
 *  @param notification notification description
 */
- (void)onReceiveMessageReadReceiptRequest:(NSNotification *)notification{
    NSDictionary *dic = notification.object;
    if ([self.targetId isEqualToString:dic[@"targetId"]] && self.conversationType == [dic[@"conversationType"] intValue]){
        NSMutableArray *array = [NSMutableArray array];
        for (int i = 0;i < self.conversationDataRepository.count;i++) {
            RCMessageModel *model = self.conversationDataRepository[i];
            if ([model.messageUId isEqualToString:dic[@"messageUId"]] && model.messageDirection == MessageDirection_RECEIVE) {
                RCMessage *msg = [[RCIMClient sharedRCIMClient]getMessage:model.messageId];
                NSArray *msgList = [NSArray arrayWithObject:msg];
                [[RCIMClient sharedRCIMClient]sendReadReceiptResponse:self.conversationType targetId:self.targetId messageList:msgList success:^{
                    
                } error:^(RCErrorCode nErrorCode) {
                    
                }];
                if (!model.readReceiptInfo) {
                    model.readReceiptInfo = [[RCReadReceiptInfo alloc]init];
                }
                model.readReceiptInfo.isReceiptRequestMessage = YES;
                model.readReceiptInfo.hasRespond = YES;
                NSIndexPath *indexPath = [NSIndexPath indexPathForRow:i inSection:0];
                [array addObject:indexPath];
            }
            __weak typeof(self) weakSelf = self;
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf.conversationMessageCollectionView reloadItemsAtIndexPaths:array];
            });
        }

    }
}

/**
 *  需要发送回执响应
 *
 *  @param array 需要回执响应的消息的列表
 */
- (void)sendReadReceiptResponseForMessages:(NSArray *)array {
    if ([[RCIM sharedRCIM].enabledReadReceiptConversationTypeList containsObject:@(self.conversationType)]) {
        NSMutableArray *readReceiptarray = [NSMutableArray array];
        for (int i = 0; i < array.count; i++) {
            RCMessage *rcMsg = [array objectAtIndex:i];
            if (rcMsg.readReceiptInfo && rcMsg.readReceiptInfo.isReceiptRequestMessage &&!rcMsg.readReceiptInfo.hasRespond && rcMsg.messageDirection == MessageDirection_RECEIVE) {
                [readReceiptarray addObject:rcMsg];
            }
        }

        if (readReceiptarray && readReceiptarray.count > 0) {
            [[RCIMClient sharedRCIMClient] sendReadReceiptResponse:self.conversationType targetId:self.targetId messageList:readReceiptarray success:nil error:nil];
        }
    }
}

- (void)stopNotSendMessageAlertTimer {
    if (_notSendMessageAlertTimer) {
        if (_notSendMessageAlertTimer.valid) {
            [_notSendMessageAlertTimer invalidate];
        }
        _notSendMessageAlertTimer = nil;
    }
}

- (void)stopNotReciveMessageAlertTimer {
    if (_notReciveMessageAlertTimer) {
        if (_notReciveMessageAlertTimer.valid) {
            [_notReciveMessageAlertTimer invalidate];
        }
        _notReciveMessageAlertTimer = nil;
    }
}

/**
 *  开始长时间没有收到消息的timer监听
 *
 */
- (void)startNotReciveMessageAlertTimer {
    if (self.conversationType != ConversationType_CUSTOMERSERVICE) {
        return;
    }
    if (self.config.adminTipTime > 0 && self.config.adminTipWord.length > 0) {
        self.customerServiceReciveMessageOverTimeRemindTimer = self.config.adminTipTime * 60;
        self.customerServiceReciveMessageOverTimeRemindContent = self.config.adminTipWord;
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_notReciveMessageAlertTimer) {
                if (_notReciveMessageAlertTimer.valid) {
                    [_notReciveMessageAlertTimer invalidate];
                }
                _notReciveMessageAlertTimer = nil;
            }
            if (!_notReciveMessageAlertTimer) {
                _notReciveMessageAlertTimer = [NSTimer scheduledTimerWithTimeInterval:self.customerServiceReciveMessageOverTimeRemindTimer
                                                                               target:self
                                                                             selector:@selector(longTimeNotReciveMessageAlert)
                                                                             userInfo:nil
                                                                              repeats:YES];
            }

        });
    }
}

/**
 *  开始长时间没有发送消息的timer监听
 *
 */
- (void)startNotSendMessageAlertTimer {
    if (self.conversationType != ConversationType_CUSTOMERSERVICE) {
        return;
    }
    if (self.config.userTipTime > 0 && self.config.userTipWord.length > 0) {
        self.customerServiceSendMessageOverTimeRemindTimer = self.config.userTipTime * 60;
        self.customerServiceSendMessageOverTimeRemindContent = self.config.userTipWord;
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_notSendMessageAlertTimer) {
                if (_notSendMessageAlertTimer.valid) {
                    [_notSendMessageAlertTimer invalidate];
                }
                _notSendMessageAlertTimer = nil;
            }
            _notSendMessageAlertTimer = [NSTimer scheduledTimerWithTimeInterval:self.customerServiceSendMessageOverTimeRemindTimer
                                                                         target:self
                                                                       selector:@selector(longTimeNotSendMessageAlert)
                                                                       userInfo:nil
                                                                        repeats:YES];
        });
    }
}

/**
 *  长时间没有收到消息的超时提醒
 *
 */
-(void)longTimeNotReciveMessageAlert{
    if(self.currentServiceStatus == RCCustomerService_HumanService){
        RCInformationNotificationMessage *informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:self.customerServiceReciveMessageOverTimeRemindContent extra:nil];
        __block RCMessage *tempMessage = [[RCMessage alloc] initWithType:self.conversationType
                                                              targetId:self.targetId
                                                             direction:MessageDirection_SEND
                                                             messageId:-1
                                                               content:informationNotifiMsg];
        dispatch_async(dispatch_get_main_queue(), ^{
            tempMessage = [self willAppendAndDisplayMessage:tempMessage];
            if (tempMessage) {
                [self appendAndDisplayMessage:tempMessage];
            }
            [self stopNotReciveMessageAlertTimer];
        });
    }else{
        [self stopNotReciveMessageAlertTimer];
    }
}

/**
 *  长时间没有发送消息的超时提醒
 *
 */
-(void)longTimeNotSendMessageAlert{
    if(self.currentServiceStatus == RCCustomerService_HumanService){
        RCInformationNotificationMessage *informationNotifiMsg = [RCInformationNotificationMessage notificationWithMessage:self.customerServiceSendMessageOverTimeRemindContent extra:nil];
        __block RCMessage *tempMessage = [[RCMessage alloc] initWithType:self.conversationType
                                                      targetId:self.targetId
                                                     direction:MessageDirection_SEND
                                                     messageId:-1
                                                       content:informationNotifiMsg];
        dispatch_async(dispatch_get_main_queue(), ^{
            tempMessage = [self willAppendAndDisplayMessage:tempMessage];
            if (tempMessage) {
                [self appendAndDisplayMessage:tempMessage];
            }
            
            [self stopNotSendMessageAlertTimer];
        });
    }else{
        [self stopNotReciveMessageAlertTimer];
    }
}

- (UIView *)extensionView {
    if (!_extensionView) {
        _extensionView = [[UIView alloc]init];
        [self.view addSubview:_extensionView];
    }
    return _extensionView;
}
@end

