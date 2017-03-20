//
//  ChatViewController.m
//  WMeeting
//
//  Created by jack on 9/14/16.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "ChatViewController.h"
#import "GoGoDB.h"
#import "PhotoMessageViewController.h"
#import "WSUser.h"
#import "JPrivateChatInfoViewController.h"
#import "JGroupChatInfoViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "RealTimeTalkView.h"
#import "UserInfoViewController.h"
#import "WSGroup.h"

#import "RealTimeLocationEndCell.h"
#import "RealTimeLocationStartCell.h"
#import "RealTimeLocationStatusView.h"
#import "RealTimeLocationViewController.h"

#import "CMNavigationController.h"

@interface ChatViewController ()<UIScrollViewDelegate, RCRealTimeLocationObserver, RealTimeLocationStatusViewDelegate, UIActionSheetDelegate>
{
    UIButton *btnSlice;
    WebClient *_httpUser;
    WebClient *_httpGroup;
    
    RealTimeTalkView *_realtimeTalkView;
    
    UIButton *_tab0;
    UIButton *_tab1;
    UIButton *_tab2;
    
    int _tabIndex;
    
    UIView *_titleBarView;
    UIImageView *_naviImgBg;
    
    UIScrollView *_content;
}
@property (nonatomic, strong) NSString *_imgeUrl;
@property (nonatomic, strong) NSMutableArray *_groupMembers;
@property (nonatomic, strong) WSGroup *_targetGroup;

@property(nonatomic, weak) id<RCRealTimeLocationProxy> _realTimeLocation;
@property(nonatomic, strong)
RealTimeLocationStatusView *_realTimeLocationStatusView;


@end

@implementation ChatViewController
@synthesize _userId;
@synthesize _userName;
@synthesize _groupType;
@synthesize _imgeUrl;
@synthesize _targetUser;
@synthesize _groupMembers;
@synthesize _targetGroup;

@synthesize _realTimeLocation;
@synthesize _realTimeLocationStatusView;

@synthesize _enterCallUI;

//@synthesize _forwardMsgs;

- (void) dealloc
{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    
}

- (void) viewWillAppear:(BOOL)animated
{
    if ([[[UIDevice currentDevice] systemVersion] compare:@"7.0" options:NSNumericSearch] != NSOrderedAscending)
    {
        if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
            self.navigationController.interactivePopGestureRecognizer.delegate = nil;
        }
    }
    
    self.navigationController.navigationBarHidden = YES;
    
    [super viewWillAppear:animated];
    
   // [self.conversationMessageCollectionView reloadData];
}

- (void) viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    self.navigationController.navigationBarHidden = NO;
}

- (void) backAction:(id)sender{
    
    [_realtimeTalkView endRCPTT];
    
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    if(_userName){
        self.title = _userName;
    }
    else
    {
        self.title = @"聊天";
    }

    [self.chatSessionInputBarControl.pluginBoardView removeItemAtIndex:3];
    
    _content = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-50)];
    [self.view insertSubview:_content atIndex:0];
    _content.bounces = NO;
    //_content.backgroundColor = [UIColor redColor];
    
    
    UIImage *themeImg = [UIImage imageNamed:@"navi_image.png"];
    UIImageView *digb = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 64)];
    [_content addSubview:digb];
    digb.image = themeImg;
    digb = [[UIImageView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH*2, 0, SCREEN_WIDTH, 64)];
    [_content addSubview:digb];
    digb.image = themeImg;

    [_content addSubview:self.conversationMessageCollectionView];
    
    _content.contentSize = CGSizeMake(SCREEN_WIDTH*3, SCREEN_HEIGHT-64);
    _content.pagingEnabled = YES;
    _content.showsHorizontalScrollIndicator = NO;
    _content.delegate = self;
    
    
    
   // UIImage *themeImg = [UIImage imageNamed:@"navi_image.png"];
    _titleBarView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 64)];
    [self.view addSubview:_titleBarView];
    _naviImgBg = [[UIImageView alloc] initWithFrame:_titleBarView.bounds];
    [_titleBarView addSubview:_naviImgBg];
    _naviImgBg.image = themeImg;
    _titleBarView.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.1];
    

    
    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
    backBtn.frame = CGRectMake(0, 20, 44, 44);
    [backBtn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [_titleBarView addSubview:backBtn];
    
    
    
    _realtimeTalkView = [[RealTimeTalkView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT-50)];
    [_content addSubview:_realtimeTalkView];
    
    
    
    self.conversationMessageCollectionView.backgroundColor = [UIColor clearColor];
    //
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(refreshChatList:)
                                                 name:@"Refresh_Chat_List"
                                               object:nil];
    
    
    
    //Right Control Bar
    {
        UIView *rightView = [[UIView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH-95, 20, 85, 40)];
        
        UIButton *locBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        locBtn.frame = CGRectMake(0, 0, 40, 40);
        [locBtn setImage:[UIImage imageNamed:@"icon_location.png"] forState:UIControlStateNormal];
        [locBtn addTarget:self action:@selector(refLocation:) forControlEvents:UIControlEventTouchDown];
        [rightView addSubview:locBtn];
        

        UIButton *contactBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        contactBtn.frame = CGRectMake(45, 0, 40, 40);
        [contactBtn setImage:[UIImage imageNamed:@"obj_contact.png"] forState:UIControlStateNormal];
        [contactBtn addTarget:self action:@selector(chatInfo:) forControlEvents:UIControlEventTouchDown];
        [rightView addSubview:contactBtn];
        
        
        [_titleBarView addSubview:rightView];
        
      
    }
    
    
    
    UIView *typeHeaderView = [[UIView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH/4, 20, SCREEN_WIDTH/2, 40)];
    typeHeaderView.backgroundColor = [UIColor clearColor];
    

    int w = SCREEN_WIDTH/2;
    int w1 = 20;
    
    ////
    _tab0 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tab0.frame = CGRectMake(0, 1, w1, 38);
    [typeHeaderView addSubview:_tab0];
    [_tab0 setImage:[UIImage imageNamed:@"chat_switch.png"] forState:UIControlStateNormal];
    
    
    _tab1 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tab1.frame = CGRectMake(w, 1, w1, 38);
    [typeHeaderView addSubview:_tab1];
    [_tab1 setImage:[UIImage imageNamed:@"talkback_switch.png"] forState:UIControlStateNormal];
    
    _tab2 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tab2.frame = CGRectMake(w, 1, w1, 38);
    [typeHeaderView addSubview:_tab2];
    [_tab2 setImage:[UIImage imageNamed:@"call_chat_switch"] forState:UIControlStateNormal];
    
    _tab1.center = CGPointMake(w/2, 20);
    _tab0.center = CGPointMake(CGRectGetMinX(_tab1.frame)-w1/2-4, 20);
    _tab2.center = CGPointMake(CGRectGetMaxX(_tab1.frame)+w1/2+4, 20);
    
    _tab1.alpha = 0.6;
    _tab2.alpha = 0.6;
    
    _tabIndex = 0;
    
    [_titleBarView addSubview:typeHeaderView];
    
    _tab0.tag = 0;
    _tab1.tag = 1;
    _tab2.tag = 2;
    [_tab0 addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_tab1 addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_tab2 addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    
    /*******************实时地理位置共享***************/
    
    /*
    [self registerClass:[RealTimeLocationStartCell class]
        forMessageClass:[RCRealTimeLocationStartMessage class]];
    [self registerClass:[RealTimeLocationEndCell class]
        forMessageClass:[RCRealTimeLocationEndMessage class]];
    
    __weak typeof(&*self) weakSelf = self;
    [[RCRealTimeLocationManager sharedManager]
     getRealTimeLocationProxy:self.conversationType
     targetId:self.targetId
     success:^(id<RCRealTimeLocationProxy> realTimeLocation) {
         weakSelf._realTimeLocation = realTimeLocation;
         [weakSelf._realTimeLocation addRealTimeLocationObserver:self];
         [weakSelf updateRealTimeLocationStatus];
     }
     error:^(RCRealTimeLocationErrorCode status) {
         NSLog(@"get location share failure with code %d", (int)status);
     }];
    */
    /******************实时地理位置共享**************/

    
    if(self.conversationType == ConversationType_PRIVATE)
    {
        [self reloadPersonInfo];
    }
    else
    {
        [self loadGroupInfo];
        [self loadGroupMembers];
    }
 
 
    
    if(_enterCallUI)
    {
        [_content setContentOffset:CGPointMake(SCREEN_WIDTH, 0)];
        
        [_realtimeTalkView startRCPTT];
    }
    
}


- (void) forwardMessages{
    
    /*
    for(RCMessage *msg in _forwardMsgs)
    {
        
        [[RCIMClient sharedRCIMClient] sendMediaMessage:self.conversationType
                                               targetId:self.targetId
                                                content:msg.content
                                            pushContent:nil
                                               pushData:nil
                                               progress:^(int progress, long messageId) {
                                                   
                                               } success:^(long messageId) {
                                                   
                                               } error:^(RCErrorCode errorCode, long messageId) {
                                                   
                                               } cancel:^(long messageId) {
                                                   
                                               }];
     
    }
     */
    
}

- (void)pluginBoardView:(RCPluginBoardView *)pluginBoardView
     clickedItemWithTag:(NSInteger)tag {
    switch (tag) {
        case PLUGIN_BOARD_ITEM_LOCATION_TAG: {
            if (1) {
                UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                              initWithTitle:nil
                                              delegate:self
                                              cancelButtonTitle:@"取消"
                                              destructiveButtonTitle:nil
                                              otherButtonTitles:@"发送位置", @"位置实时共享", nil];
                [actionSheet showInView:self.view];
            } else {
                [super pluginBoardView:pluginBoardView clickedItemWithTag:tag];
            }
            
        } break;
        default:
            [super pluginBoardView:pluginBoardView clickedItemWithTag:tag];
            break;
    }
}

#pragma mark - UIActionSheet Delegate
- (void)actionSheet:(UIActionSheet *)actionSheet
clickedButtonAtIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: {
            [super pluginBoardView:self.pluginBoardView
                clickedItemWithTag:PLUGIN_BOARD_ITEM_LOCATION_TAG];
        } break;
        case 1: {
            [self showRealTimeLocationViewController];
        } break;
    }
}

- (void) tabButtonClicked:(UIButton*)sender{
    
    int bTag = (int)sender.tag;
    
    if(_tabIndex != bTag)
    {
        _tabIndex = bTag;
        
        [_content setContentOffset:CGPointMake(_tabIndex*SCREEN_WIDTH, 0)];
        
        [self doChatContextSwitch];
    }
}

- (void) scrollViewDidScroll:(UIScrollView *)scrollView
{
    if(scrollView != _content)
        return;
    
    CGFloat pageWidth = scrollView.frame.size.width;
    //NSLog(@"pageHeight = %f", pageHeight);
    int pageIndex = floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    
    if(_tabIndex != pageIndex)
    {
        _tabIndex = pageIndex;
        
        [self doChatContextSwitch];
    }
    
}


- (void) doChatContextSwitch{
    
    if(_tabIndex == 0)
    {
        _naviImgBg.hidden = NO;
        
        [_realtimeTalkView endRCPTT];
        
        _tab0.alpha = 1.0;
        _tab1.alpha = 0.6;
        _tab2.alpha = 0.6;
        
        self.chatSessionInputBarControl.hidden = NO;
        
        [UIView animateWithDuration:0.25 animations:^{
            self.chatSessionInputBarControl.alpha = 1.0;
            _naviImgBg.alpha = 1;
        } completion:^(BOOL finished) {
            
        }];
    }
    else if(_tabIndex == 1)
    {
        
        //[_realtimeTalkView startRCPTT];
        
        _tab0.alpha = 0.6;
        _tab1.alpha = 1.0;
        _tab2.alpha = 0.6;
        
        [UIView animateWithDuration:0.25 animations:^{
            self.chatSessionInputBarControl.alpha = 0.0;
            _naviImgBg.alpha = 0.2;
        } completion:^(BOOL finished) {
            self.chatSessionInputBarControl.hidden = YES;
            _naviImgBg.hidden = YES;
        }];
    }
    else if(_tabIndex == 2)
    {
        _naviImgBg.hidden = NO;
        
        [_realtimeTalkView endRCPTT];
        
        _tab0.alpha = 0.6;
        _tab1.alpha = 0.6;
        _tab2.alpha = 1.0;
        
        [UIView animateWithDuration:0.25 animations:^{
            self.chatSessionInputBarControl.alpha = 0.0;
            _naviImgBg.alpha = 1;
        } completion:^(BOOL finished) {
            self.chatSessionInputBarControl.hidden = YES;
        }];
    }
}


- (void) reloadPersonInfo{
    
    if(_httpUser == nil)
    {
        _httpUser = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpUser._method = API_USER_PROFILE;
    _httpUser._httpMethod = @"GET";
    
   // User *u = [UserDefaultsKV getUser];
    
    
    _httpUser._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                               //u._authtoken,@"token",
                                self.targetId,@"userid",
                               nil];
    
    
    IMP_BLOCK_SELF(ChatViewController);
    
    [_httpUser requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    block_self._targetUser = [[WSUser alloc] initWithDictionary:v];
                    
                    [block_self updateTargetUser];
                }
                
                return;
            }
            
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            
            
            
            NSLog(@"OOPS: %@", err);
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];
        
        
    } FailBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        
    }];
}


- (void) updateTargetUser{
    
    NSMutableArray * last5_chat = [[NSMutableArray alloc] init];
    NSMutableArray *last = [[NSUserDefaults standardUserDefaults] objectForKey:@"last5_chat_sync"];
    if(last)
    {
        [last5_chat addObjectsFromArray:last];
    }

    if(self.conversationType == ConversationType_PRIVATE)
    {
        _realtimeTalkView._targetUser = self._targetUser;
        [_realtimeTalkView prepareUI];
        
        WSUser *uu = self._targetUser;
        
        NSString *url = uu.avatarurl;
     
        
        NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:
                             self.targetId,@"id",
                             @"0",@"type",
                             url,@"avatarurl",
                             nil];
        
        for(int i = 0; i < [last5_chat count]; i++)
        {
            NSDictionary *od = [last5_chat objectAtIndex:i];
            int type = [[od objectForKey:@"type"] intValue];
            NSString *tid = [od objectForKey:@"id"];
            if(type == 0 && [tid isEqualToString:self.targetId])
            {
                [last5_chat removeObjectAtIndex:i];
                break;
            }
        }
        
        [last5_chat insertObject:dic atIndex:0];
        
        if([last5_chat count] > 5)
        {
            [last5_chat removeLastObject];
        }
        
    }
    else
    {
        _realtimeTalkView._targetUser = self._targetGroup.creator;
        [_realtimeTalkView prepareUI];
        
        WSUser *uu = self._targetGroup.creator;
        
        NSString *url = uu.avatarurl;
        
        
        
        NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:
                             self.targetId,@"id",
                             @"1",@"type",
                             uu.fullname,@"name",
                             url,@"avatarurl",
                             nil];
        
        for(int i = 0; i < [last5_chat count]; i++)
        {
            NSDictionary *od = [last5_chat objectAtIndex:i];
            int type = [[od objectForKey:@"type"] intValue];
            NSString *tid = [od objectForKey:@"id"];
            if(type == 1 && [tid isEqualToString:self.targetId])
            {
                [last5_chat removeObjectAtIndex:i];
                break;
            }
        }
        
        [last5_chat insertObject:dic atIndex:0];
        
        if([last5_chat count] > 5)
        {
            [last5_chat removeLastObject];
        }

    }
    
    
    [[NSUserDefaults standardUserDefaults] setObject:last5_chat forKey:@"last5_chat_sync"];
    [[NSUserDefaults standardUserDefaults] synchronize];

}

- (void) loadGroupInfo{
    
    if(_httpGroup == nil)
    {
        _httpGroup = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpGroup._method = API_GROUP_INFO;
    _httpGroup._httpMethod = @"GET";
    
    // User *u = [UserDefaultsKV getUser];
    
    
    _httpGroup._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                               //u._authtoken,@"token",
                               self.targetId,@"groupid",
                               nil];
    
    
    IMP_BLOCK_SELF(ChatViewController);
    
    [_httpGroup requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    NSDictionary *text = [v objectForKey:@"text"];
                    block_self._targetGroup = [[WSGroup alloc] initWithDictionary:text];
                    
                    [block_self updateTargetUser];
                }
                
                return;
            }
            
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            
            
            
            NSLog(@"OOPS: %@", err);
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];
        
        
    } FailBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        
    }];
}

- (void) loadGroupMembers{
    
    if(_httpUser == nil)
    {
        _httpUser = [[WebClient alloc] initWithDelegate:self];
    }
    
    _httpUser._method = API_GROUP_MEMBS;
    _httpUser._httpMethod = @"GET";
    

    _httpUser._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                               self.targetId,@"groupid",
                               nil];
    
    
    IMP_BLOCK_SELF(ChatViewController);
    
    [_httpUser requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    NSArray *membs = [v objectForKey:@"text"];
                    [block_self groupMembers:membs];
                }
                
                return;
            }
            
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            
            
            
            NSLog(@"OOPS: %@", err);
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];
        
        
    } FailBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        
    }];
}

- (void) groupMembers:(NSArray*)list{
    
    self._groupMembers = [NSMutableArray array];
    
    for(NSDictionary *dic in list)
    {
        WSUser *uu = [[WSUser alloc] initWithDictionary:dic];
        [_groupMembers addObject:uu];
    }
}

- (void) refLocation:(id)sender{
    
    
//    for(UIView *sub in [self.view subviews])
//    {
//        NSLog(@"%@", [sub class]);
//    }
    
   [self showRealTimeLocationViewController];
    
}

- (void) chatInfo:(id)sender{
    
    if(self.conversationType == ConversationType_PRIVATE)
    {
        JPrivateChatInfoViewController *jp = [[JPrivateChatInfoViewController alloc] init];
        jp._targetUser = self._targetUser;
        [self.navigationController pushViewController:jp animated:YES];
    }
    else if(self.conversationType == ConversationType_GROUP)
    {
        if([_groupMembers count] && _targetGroup)
        {
            JGroupChatInfoViewController *jp = [[JGroupChatInfoViewController alloc] init];
            jp._membs = _groupMembers;
            jp._group = _targetGroup;
            [self.navigationController pushViewController:jp animated:YES];

        }
        
    }

}




- (void) refreshChatList:(NSNotification *)notify{
    
    [self.conversationMessageCollectionView reloadData];
}


- (void)didTapCellPortrait:(NSString *)userId{
    
    UserInfoViewController *uinfo = [[UserInfoViewController alloc] init];
    
    uinfo._userId = userId;
    [self.navigationController pushViewController:uinfo animated:YES];
    
}
- (void)presentImagePreviewController:(RCMessageModel *)model{
    
    RCImageMessage *rc = (RCImageMessage*)model.content;
    
    //    self._imgeUrl = rc.imageUrl;
    //
    //    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil message:rc.imageUrl
    //                                                   delegate:self
    //                                          cancelButtonTitle:@"OK"
    //                                          otherButtonTitles:@"copy", nil];
    //    [alert show];
    
    PhotoMessageViewController *photoCtrl = [[PhotoMessageViewController alloc] init];
    photoCtrl._imageUrl = rc.imageUrl;
    [self presentViewController:photoCtrl animated:YES completion:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    //    if(buttonIndex != alertView.cancelButtonIndex)
    //    {
    //        UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    //        pasteboard.string = self._imgeUrl;
    //
    //
    //        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示"
    //                                                        message:@"已复制到剪切版。"
    //                                                       delegate:nil
    //                                              cancelButtonTitle:@"OK"
    //                                              otherButtonTitles:nil, nil];
    //        [alert show];
    //    }
}




#pragma mark - RCRealTimeLocationObserver
- (void)onRealTimeLocationStatusChange:(RCRealTimeLocationStatus)status {
    __weak typeof(&*self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        [weakSelf updateRealTimeLocationStatus];
    });
}

- (void)onReceiveLocation:(CLLocation *)location fromUserId:(NSString *)userId {
    __weak typeof(&*self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        [weakSelf updateRealTimeLocationStatus];
    });
}

- (void)onParticipantsJoin:(NSString *)userId {
    __weak typeof(&*self) weakSelf = self;
    if ([userId isEqualToString:[RCIMClient sharedRCIMClient]
         .currentUserInfo.userId]) {
        [self notifyParticipantChange:@"你加入了地理位置共享"];
    } else {
        [[RCIM sharedRCIM]
         .userInfoDataSource
         getUserInfoWithUserId:userId
         completion:^(RCUserInfo *userInfo) {
             if (userInfo.name.length) {
                 [weakSelf
                  notifyParticipantChange:
                  [NSString stringWithFormat:@"%@加入地理位置共享",
                   userInfo.name]];
             } else {
                 [weakSelf
                  notifyParticipantChange:
                  [NSString
                   stringWithFormat:@"user<%@>加入地理位置共享",
                   userId]];
             }
         }];
    }
}

- (void)onParticipantsQuit:(NSString *)userId {
    __weak typeof(&*self) weakSelf = self;
    if ([userId isEqualToString:[RCIMClient sharedRCIMClient]
         .currentUserInfo.userId]) {
        [self notifyParticipantChange:@"你退出地理位置共享"];
    } else {
        [[RCIM sharedRCIM]
         .userInfoDataSource
         getUserInfoWithUserId:userId
         completion:^(RCUserInfo *userInfo) {
             if (userInfo.name.length) {
                 [weakSelf
                  notifyParticipantChange:
                  [NSString stringWithFormat:@"%@退出地理位置共享",
                   userInfo.name]];
             } else {
                 [weakSelf
                  notifyParticipantChange:
                  [NSString
                   stringWithFormat:@"user<%@>退出地理位置共享",
                   userId]];
             }
         }];
    }
}

- (void)onRealTimeLocationStartFailed:(long)messageId {
    dispatch_async(dispatch_get_main_queue(), ^{
        for (int i = 0; i < self.conversationDataRepository.count; i++) {
            RCMessageModel *model = [self.conversationDataRepository objectAtIndex:i];
            if (model.messageId == messageId) {
                model.sentStatus = SentStatus_FAILED;
            }
        }
        NSArray *visibleItem =
        [self.conversationMessageCollectionView indexPathsForVisibleItems];
        for (int i = 0; i < visibleItem.count; i++) {
            NSIndexPath *indexPath = visibleItem[i];
            RCMessageModel *model =
            [self.conversationDataRepository objectAtIndex:indexPath.row];
            if (model.messageId == messageId) {
                [self.conversationMessageCollectionView
                 reloadItemsAtIndexPaths:@[ indexPath ]];
            }
        }
    });
}

- (void)notifyParticipantChange:(NSString *)text {
    __weak typeof(&*self) weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        [weakSelf._realTimeLocationStatusView updateText:text];
        [weakSelf performSelector:@selector(updateRealTimeLocationStatus)
                       withObject:nil
                       afterDelay:0.5];
    });
}

- (void)onFailUpdateLocation:(NSString *)description {
}


/*******************实时地理位置共享***************/
- (void)showRealTimeLocationViewController {
    
    RealTimeLocationViewController *lsvc =
    [[RealTimeLocationViewController alloc] init];
   
    
    if(self.conversationType == ConversationType_GROUP)
    {
        if([_groupMembers count])
        {
            lsvc._shareMembs = _groupMembers;
        }
        
        lsvc._targetObj = _targetGroup;
    }
    else
    {
        NSMutableArray *_membs = [NSMutableArray array];
        
        User *u = [UserDefaultsKV getUser];
        
        WSUser *my = [[WSUser alloc] init];
        my.userId = [u._userId intValue];
        my.fullname = u._userName;
        my.avatarurl = u._avatar;
        
        [_membs addObject:my];
        
        if(_targetUser)
        {
            [_membs addObject:_targetUser];
        }
        
        lsvc._shareMembs = _membs;
        lsvc._targetObj = _targetUser;
    }
    
    
    if([lsvc._shareMembs count] && lsvc._targetObj)
    {
        CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:lsvc];
        
        //    lsvc.realTimeLocationProxy = self._realTimeLocation;
        //    if ([self._realTimeLocation getStatus] ==
        //        RC_REAL_TIME_LOCATION_STATUS_INCOMING) {
        //        [self._realTimeLocation joinRealTimeLocation];
        //    } else if ([self._realTimeLocation getStatus] ==
        //               RC_REAL_TIME_LOCATION_STATUS_IDLE) {
        //        [self._realTimeLocation startRealTimeLocation];
        //    }
        
        
        [self.navigationController presentViewController:navi
                                                animated:YES
                                              completion:^{
                                                  
                                              }];

    }
    
}


- (void)updateRealTimeLocationStatus {
    if (self._realTimeLocation) {
        [self._realTimeLocationStatusView updateRealTimeLocationStatus];
        __weak typeof(&*self) weakSelf = self;
        NSArray *participants = nil;
        switch ([self._realTimeLocation getStatus]) {
            case RC_REAL_TIME_LOCATION_STATUS_OUTGOING:
                [self._realTimeLocationStatusView updateText:@"你正在共享位置"];
                break;
            case RC_REAL_TIME_LOCATION_STATUS_CONNECTED:
            case RC_REAL_TIME_LOCATION_STATUS_INCOMING:
                participants = [self._realTimeLocation getParticipants];
                if (participants.count == 1) {
                    NSString *userId = participants[0];
                    [weakSelf._realTimeLocationStatusView
                     updateText:[NSString
                                 stringWithFormat:@"user<%@>正在共享位置", userId]];
                    [[RCIM sharedRCIM]
                     .userInfoDataSource
                     getUserInfoWithUserId:userId
                     completion:^(RCUserInfo *userInfo) {
                         if (userInfo.name.length) {
                             dispatch_async(dispatch_get_main_queue(), ^{
                                 [weakSelf._realTimeLocationStatusView
                                  updateText:[NSString stringWithFormat:
                                              @"%@正在共享位置",
                                              userInfo.name]];
                             });
                         }
                     }];
                } else {
                    if (participants.count < 1)
                        [self._realTimeLocationStatusView removeFromSuperview];
                    else
                        [self._realTimeLocationStatusView
                         updateText:[NSString stringWithFormat:@"%d人正在共享地理位置",
                                     (int)participants.count]];
                }
                break;
            default:
                break;
        }
    }
}

#pragma mark - RealTimeLocationStatusViewDelegate
- (void)onJoin {
    [self showRealTimeLocationViewController];
}
- (RCRealTimeLocationStatus)getStatus {
    return [self._realTimeLocation getStatus];
}

- (void)onShowRealTimeLocationView {
    [self showRealTimeLocationViewController];
}
- (RCMessageContent *)willSendMessage:(RCMessageContent *)messageCotent {
    //可以在这里修改将要发送的消息
    if ([messageCotent isMemberOfClass:[RCTextMessage class]]) {
        // RCTextMessage *textMsg = (RCTextMessage *)messageCotent;
        // textMsg.extra = @"";
    }
    return messageCotent;
}

/*
- (void)inputTextView:(UITextView *)inputTextView
shouldChangeTextInRange:(NSRange)range
      replacementText:(NSString *)text{
    
    NSString *txt = inputTextView.text;
    if([txt length] >= 10)
    {
        inputTextView.text = txt;
        return;
    }
    
    NSLog(@"%@", text);
}
 */


/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

@end
