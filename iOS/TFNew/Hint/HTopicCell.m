//
//  HTopicCell.m
//  ZHEvent
//
//  Created by jack on 9/26/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "HTopicCell.h"
#import "RongIMKit.h"
#import "UIImageView+WebCache.h"
#import "UILabel+ContentSize.h"
#import "GoGoDB.h"
#import "UIButton+Color.h"
#import "UIImage+Color.h"
#import <RongPTTLib/RongPTTLib.h>
#import "SBJson4.h"


@implementation TFCellEventView
@synthesize _actionObj;

- (id) initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame])
    {
        [self setup];
        
    }
    
    return self;
    
}


- (void) beginMenu{
    
    [self setSelect];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(menuDone:)
                                                 name:UIMenuControllerDidHideMenuNotification
                                               object:nil];
}

- (void) menuDone:(NSNotification*)notify{
    
    [self cancelSelect];
}

- (void) cancelSelect{
    
    self.backgroundColor = [UIColor clearColor];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) setSelect{
    
    self.backgroundColor = LINE_COLOR;
    
}

- (void)setup{
    //允许用户交互
    self.userInteractionEnabled = YES;
}
//允许自己成为第一响应者
- (BOOL)canBecomeFirstResponder{
    return YES;
}
//Label能够执行哪些操作（menu）
-(BOOL)canPerformAction:(SEL)action withSender:(id)sender{
    return (action == @selector(deleteAction:)
            || action == @selector(alertAction:));
    
}

- (void)deleteAction:(id)sender{
    
    if(_actionObj && [_actionObj respondsToSelector:@selector(didDeleteConversation:)])
    {
        [_actionObj didDeleteConversation:(int)self.tag];
    }
}

-(void)alertAction:(id)sender
{
    if(_actionObj && [_actionObj respondsToSelector:@selector(didSetMessageNotificationStatus:)])
    {
        [_actionObj didSetMessageNotificationStatus:(int)self.tag];
    }
    
    [self cancelSelect];
    
    if([self isFirstResponder])
    {
        [self resignFirstResponder];
    }

}


@end

@interface HTopicCell ()
{
    UILabel *_tName;
    UILabel *_lastMessage;
    UILabel *_unreadMsg;
    UILabel *_tTime;
    UIImageView *_avatar;
    
    UIImageView *_sliceIcon;
    
    UILabel *_flag;
    
    UILabel *mask;
    
    WebClient *_targetClient;
    WebClient *_senderClient;
    WebClient *_http;
    
    BOOL _isLoadingSender;
    BOOL _isloadingTargetUser;
    
    //TFCellEventView *_rowBtn;
}
@property (nonatomic, strong) NSString *stateName;
@property (nonatomic, strong) RCConversationModel *_dataModel;

@end

@implementation HTopicCell
@synthesize _rowBtn;
@synthesize stateName;
@synthesize _dataModel;


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (id) initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    
    if(self = [super initWithStyle:style reuseIdentifier:reuseIdentifier])
    {
        
        self._rowBtn = [[TFCellEventView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 70)];
        [self.contentView addSubview:_rowBtn];
        [_rowBtn setBackgroundImage:[UIImage imageWithColor:LINE_COLOR andSize:CGSizeMake(1, 1)] forState:UIControlStateHighlighted];
        //[_rowBtn setBackgroundImage:[UIImage imageWithColor:LINE_COLOR andSize:CGSizeMake(1, 1)] forState:UIControlStateSelected];
        
        _avatar = [[UIImageView alloc] initWithFrame:CGRectMake(10, 10, 50, 50)];
        _avatar.layer.cornerRadius = 25;
        _avatar.clipsToBounds = YES;
        [self.contentView addSubview:_avatar];
        _avatar.layer.contentsGravity = kCAGravityResizeAspectFill;
        _avatar.backgroundColor = COLOR_TEXT_C;
        [_avatar setImage:[UIImage imageNamed:@"default_avatar.png"]];
        
        
        mask = [[UILabel alloc] initWithFrame:_avatar.bounds];
        [_avatar addSubview:mask];
        //mask.backgroundColor = [Utls groupMaskColorWithId:[group.groupId intValue]];
        mask.textColor = [UIColor whiteColor];
       // mask.text = showName;
        mask.font = [UIFont boldSystemFontOfSize:24];
        mask.layer.cornerRadius = 25;
        mask.clipsToBounds = YES;
        mask.textAlignment = NSTextAlignmentCenter;
        mask.hidden = YES;
        
        
        _flag = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(_avatar.frame),
                                                          CGRectGetMinY(_avatar.frame),
                                                          8, 8)];
        _flag.backgroundColor = [UIColor redColor];
        [self.contentView addSubview:_flag];
        _flag.layer.cornerRadius = 4;
        _flag.clipsToBounds = YES;
        _flag.hidden = YES;
        
        
        int x = CGRectGetMaxX(_avatar.frame)+10;
        
        _tName = [[UILabel alloc] initWithFrame:CGRectMake(x, 10, SCREEN_WIDTH-x-80, 30)];
        _tName.backgroundColor = [UIColor clearColor];
        _tName.font = [UIFont systemFontOfSize:16];
        _tName.textColor = [UIColor blackColor];
        [self.contentView addSubview:_tName];
        
        _lastMessage = [[UILabel alloc] initWithFrame:CGRectMake(x, 40, SCREEN_WIDTH-x-10, 20)];
        _lastMessage.backgroundColor = [UIColor clearColor];
        _lastMessage.font = [UIFont systemFontOfSize:14];
        _lastMessage.textColor = COLOR_TEXT_A;
        [self.contentView addSubview:_lastMessage];
        
        
        _tTime = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(_tName.frame), 15, 70, 20)];
        _tTime.backgroundColor = [UIColor clearColor];
        _tTime.font = [UIFont systemFontOfSize:13];
        _tTime.textAlignment = NSTextAlignmentRight;
        _tTime.textColor = COLOR_TEXT_B;
        [self.contentView addSubview:_tTime];
        
        
        _unreadMsg = [[UILabel alloc] initWithFrame:CGRectMake(SCREEN_WIDTH - 30, 40, 20, 20)];
        _unreadMsg.backgroundColor = THEME_RED_COLOR;
        _unreadMsg.layer.cornerRadius = 10;
        _unreadMsg.clipsToBounds = YES;
        [self.contentView addSubview:_unreadMsg];
        _unreadMsg.textColor = [UIColor whiteColor];
        _unreadMsg.textAlignment = NSTextAlignmentCenter;
        _unreadMsg.font = [UIFont systemFontOfSize:10];
        _unreadMsg.hidden = YES;
        
        _sliceIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"block_notification.png"]];
        [self.contentView addSubview:_sliceIcon];
        _sliceIcon.center = CGPointMake(SCREEN_WIDTH-70, 25);
        _sliceIcon.hidden = YES;
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 69, SCREEN_WIDTH, 1)];
        line.backgroundColor = LINE_COLOR;
        [self.contentView addSubview:line];
        
        
        UILongPressGestureRecognizer * longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self
                                                                                                 action:@selector(longPress)];
        [_rowBtn addGestureRecognizer:longPress];
        


    }
    
    return self;
    
}


-(void)deleteAction:(id)sender
{
    //走代理方法
}

-(void)alertAction:(id)sender
{
    //走代理方法
}

- (void)longPress{
    
    if(_rowBtn._actionObj == nil)
        return;
    
    //让自己成为第一响应者
    [_rowBtn becomeFirstResponder];
    
    //初始化menu
    UIMenuController * menu = [UIMenuController sharedMenuController];
    
    UIMenuItem *delItem = [[UIMenuItem alloc] initWithTitle:@"删除" action:@selector(deleteAction:)];
    UIMenuItem *alertItem = [[UIMenuItem alloc] initWithTitle:self.stateName action:@selector(alertAction:)];
    UIMenuController *menuController = [UIMenuController sharedMenuController];
    menuController.menuItems = @[delItem, alertItem];
    
    if(menu.isMenuVisible)
    {
        return;
    }
    
    [_rowBtn beginMenu];
    

    //设置menu的显示位置
    [menu setTargetRect:_rowBtn.frame inView:self];
    //让menu显示并且伴有动画
    [menu setMenuVisible:YES animated:YES];
    
}


- (void) refreshUInfo:(RCUserInfo *)userInfo{
    
    [_avatar setImageWithURL:[NSURL URLWithString:userInfo.portraitUri]
            placeholderImage:[UIImage imageNamed:@"u_touxiang.png"]];
    
    _tName.text = userInfo.name;
    _dataModel.conversationTitle = userInfo.name;
}

- (void) refreshLastMessage:(RCUserInfo *)userInfo
{
    if([_dataModel.lastestMessage isKindOfClass:[RCTextMessage class]])
    {
        RCTextMessage *msg = (RCTextMessage*)_dataModel.lastestMessage;
        _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                             userInfo.name, msg.content];
        
    }
    else if([_dataModel.lastestMessage isKindOfClass:[RCImageMessage class]])
    {
        _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                             userInfo.name, @"[图片]"];
        
    }
    else if([_dataModel.lastestMessage isKindOfClass:[RCVoiceMessage class]])
    {
        _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                             userInfo.name, @"发了一段语音"];
        
    }
    else if([_dataModel.lastestMessage isKindOfClass:[RCPTTBeginMessage class]])
    {
        _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                             userInfo.name, @"发起语音对讲"];
        
    }
    else if([_dataModel.lastestMessage isKindOfClass:[RCPTTEndMessage class]])
    {
        _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                             userInfo.name, @"语音对讲结束"];
        
    }
    else if([_dataModel.lastestMessage isKindOfClass:[RCFileMessage class]])
    {
        _lastMessage.text = [NSString stringWithFormat:@"%@: [文件]%@",
                             userInfo.name, ((RCFileMessage*)_dataModel.lastestMessage).name];
        
    }
    
   
}

- (void) refreshGroupInfo:(NSDictionary *)groupInfo{
    
    _tName.text = [groupInfo objectForKey:@"name"];
    _dataModel.conversationTitle = [groupInfo objectForKey:@"name"];
    
    NSString *logo = [groupInfo objectForKey:@"logo"];
    NSString *logourl = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, logo];
    
    //NSLog(@"%@", logourl);
    [_avatar setImageWithURL:[NSURL URLWithString:logourl]
            placeholderImage:[UIImage imageNamed:@"u_touxiang.png"]];
    
    NSString *gName = _tName.text;
    NSString *showName = @"";
    
    if([gName length] > 1)
        showName = [gName substringWithRange:NSMakeRange(1, 1)];
    else if([gName length] == 1)
        showName = gName;
    
    mask.text = showName;
}

- (void) refreshGroupUserInfo:(RCUserInfo *)userInfo
{
    _tName.text = userInfo.name;
    _dataModel.conversationTitle = userInfo.name;
    
    [_avatar setImageWithURL:[NSURL URLWithString:userInfo.portraitUri]
            placeholderImage:[UIImage imageNamed:@"u_touxiang.png"]];
    
    NSString *gName = _tName.text;
    NSString *showName = @"";
    
    if([gName length] > 1)
        showName = [gName substringWithRange:NSMakeRange(1, 1)];
    else if([gName length] == 1)
        showName = gName;
    
    mask.text = showName;
}

- (void) fillData:(RCConversationModel*)model{
    
    _isloadingTargetUser = NO;
    _isLoadingSender = NO;
    
    self._dataModel = model;
    
    self.stateName = @"开启消息免打扰";
    
    _tName.text = model.conversationTitle;
    
    int unreadCount = (int)model.unreadMessageCount;
    
    if(unreadCount > 0)
    {
        _flag.hidden = NO;
        //_tName.textColor = COLOR_TEXT_A;
        _lastMessage.textColor = COLOR_TEXT_A;
    }
    else
    {
        _flag.hidden = YES;
        //_tName.textColor = COLOR_TEXT_C;
        _lastMessage.textColor = COLOR_TEXT_C;
    }
    
    _sliceIcon.hidden = YES;
    
    if(unreadCount > 0)
    {
        _unreadMsg.text = [NSString stringWithFormat:@"%d", unreadCount];
        _unreadMsg.hidden = NO;
    }
    else
    {
        _unreadMsg.hidden = YES;
    }
    
    
    mask.hidden = YES;
    
    IMP_BLOCK_SELF(HTopicCell);
    
    if(model.conversationType == ConversationType_PRIVATE)
    {
        RCUserInfo* cachedUser = [[GoGoDB sharedDBInstance] queryUser:model.targetId];
        if(cachedUser)
        {
            [self refreshUInfo:cachedUser];
        }
        else
        {
            [self getTargetUserInfo:model.targetId];
        }
        
        cachedUser = [[GoGoDB sharedDBInstance] queryUser:model.senderUserId];
        if(cachedUser)
        {
            [block_self refreshLastMessage:cachedUser];

        }
        else
        {
            [self getSenderUserInfo:model.senderUserId];
        }

        NSString *targetId = model.targetId;
        [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:ConversationType_PRIVATE
                                                                targetId:targetId
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                     if(nStatus == DO_NOT_DISTURB)
                                                                     {
                                                                         //block_self.stateName = @"关闭消息免打扰";
                                                                         //_sliceIcon.hidden = NO;
                                                                         
                                                                         [block_self refreshStateOn];
                                                                     }
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];

    }
    else if(model.conversationType == ConversationType_GROUP)
    {
        
        mask.hidden = NO;
        mask.backgroundColor = [Utls groupMaskColorWithId:[model.targetId intValue]];
        
        NSDictionary *groupInfo = [[GoGoDB sharedDBInstance] queryGroup:model.targetId];
    
        if(groupInfo)
        {
            [self refreshGroupInfo:groupInfo];
        }
        else
        {
            [self getGroupInfo:model.targetId];
        }
        
        
        RCUserInfo* cachedUser = [[GoGoDB sharedDBInstance] queryUser:model.senderUserId];
        if(cachedUser)
        {
            [block_self refreshLastMessage:cachedUser];
            
        }
        else
        {
            [self getSenderUserInfo:model.senderUserId];
        }
        
        
        NSString *targetId = model.targetId;
        [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:ConversationType_GROUP
                                                                targetId:targetId
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                     if(nStatus == DO_NOT_DISTURB)
                                                                     {
                                                                         //block_self.stateName = @"关闭消息免打扰";
                                                                         
                                                                         [block_self refreshStateOn];

                                                                     }
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];
        
    }
    
    
    if([model.lastestMessage isKindOfClass:[RCInformationNotificationMessage class]])
    {
        RCInformationNotificationMessage *msg = (RCInformationNotificationMessage*)model.lastestMessage;
        _lastMessage.text = [NSString stringWithFormat:@"%@", msg.message];
        
    }
    
    NSTimeInterval time = model.sentTime/1000;
    NSDate *date1 = [NSDate dateWithTimeIntervalSince1970:time];
    if(time > 0 && date1)
    {
        NSCalendar *calendar = [NSCalendar currentCalendar];
        NSUInteger unitFlags = NSCalendarUnitYear| NSCalendarUnitMonth | NSCalendarUnitDay |NSCalendarUnitHour |NSCalendarUnitMinute;
        NSDateComponents *cmp1 = [calendar components:unitFlags fromDate:date1];
        NSDateComponents *cmp2 = [calendar components:unitFlags fromDate:[NSDate date]];
        
        NSString *timeStr = @"";
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        if ([cmp1 day] == [cmp2 day]) { // 今天
            formatter.dateFormat = @"HH:mm";
            timeStr = [NSString stringWithFormat:@"%@",[formatter stringFromDate:date1]];
        } else if ([cmp1 year] == [cmp2 year]) { // 今年
            formatter.dateFormat = @"MM/dd";
            timeStr = [NSString stringWithFormat:@"%@",[formatter stringFromDate:date1]];
        } else {
            formatter.dateFormat = @"yyyy/MM/dd";
            timeStr = [NSString stringWithFormat:@"%@",[formatter stringFromDate:date1]];
        }
        
        _tTime.text = timeStr;
        
    
    }
    
    
    
}


- (void) refreshStateOn{
    
    self.stateName = @"关闭消息免打扰";
    _sliceIcon.hidden = NO;
}

- (void) refreshStateOff{
    
    
}


- (void) getSenderUserInfo:(NSString *)userId
{
    if(_isLoadingSender)
        return;
    _isLoadingSender = YES;

    if(_senderClient == nil)
    {
        _senderClient = [[WebClient alloc] initWithDelegate:self];
    }
    
    _senderClient._method = API_USER_PROFILE;
    _senderClient._httpMethod = @"GET";
    
    
    _senderClient._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           userId,@"userid",
                           nil];
    
    
    IMP_BLOCK_SELF(HTopicCell);
    
    [_senderClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        _isLoadingSender = NO;
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = userId;
                    user.name = [v objectForKey:@"name"];
                    user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@",
                                        WEB_API_URL,
                                        [v objectForKey:@"logo"]];
                    
                    [block_self saveSendUserToLocal:user];
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
        
        _isLoadingSender = NO;
        
    }];
}

- (void) saveSendUserToLocal:(RCUserInfo*)user{
    
    [[GoGoDB sharedDBInstance] saveUserInfo:user];
    
    [self refreshLastMessage:user];
    
   
}

- (void) getTargetUserInfo:(NSString *)userId
{
    if(_isloadingTargetUser)
        return;
    _isloadingTargetUser = YES;
    
    if(_targetClient == nil)
    {
        _targetClient = [[WebClient alloc] initWithDelegate:self];
    }
    
    _targetClient._method = API_USER_PROFILE;
    _targetClient._httpMethod = @"GET";
    
    
    _targetClient._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                                   userId,@"userid",
                                   nil];
    
    
    IMP_BLOCK_SELF(HTopicCell);
    
    [_targetClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        _isloadingTargetUser = NO;
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = userId;
                    user.name = [v objectForKey:@"name"];
                    user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@",
                                        WEB_API_URL,
                                        [v objectForKey:@"logo"]];
                    
                    [block_self saveTargetUserToLocal:user];
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
        
        _isloadingTargetUser = NO;
        
    }];
}

- (void) saveTargetUserToLocal:(RCUserInfo*)user{
    
    [[GoGoDB sharedDBInstance] saveUserInfo:user];
    
    [self refreshUInfo:user];
    
   
}


- (void) getGroupInfo:(NSString *)groupId{
    

    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_GROUP_INFO;
    _http._httpMethod = @"GET";
    
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           groupId,@"groupid",
                           nil];
    
    
    IMP_BLOCK_SELF(HTopicCell);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    
                    NSMutableDictionary *ginfo = [v objectForKey:@"text"];
                    
                    [block_self saveGroupToLocal:ginfo];
                    
                    RCUserInfo *user = [[RCUserInfo alloc] init];
                    user.userId = groupId;
                    user.name = [ginfo objectForKey:@"name"];
                    user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@",
                                        WEB_API_URL, [ginfo objectForKey:@"logo"]];
                
                    [block_self refreshGroupUserInfo:user];
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


- (void) saveGroupToLocal:(NSMutableDictionary*)ginfo
{
    [[GoGoDB sharedDBInstance] saveGroupInfo:ginfo];
    
}



@end
