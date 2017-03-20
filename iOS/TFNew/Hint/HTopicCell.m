//
//  HTopicCell.m
//  ZHEvent
//
//  Created by jack on 9/26/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "HTopicCell.h"
#import <RongIMKit/RongIMKit.h>
#import "UIImageView+WebCache.h"
#import "UILabel+ContentSize.h"
#import "GoGoDB.h"
#import "UIButton+Color.h"
#import "UIImage+Color.h"
#import <RongPTTLib/RongPTTLib.h>


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
    
    //TFCellEventView *_rowBtn;
}
@property (nonatomic, strong) NSString *stateName;

@end

@implementation HTopicCell
@synthesize _rowBtn;
@synthesize stateName;


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
        
        _sliceIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"slice_down.png"]];
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


- (void) fillData:(RCConversationModel*)model{
    
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
    
    if([[GoGoDB sharedDBInstance] muteStateWithTarget:model.targetId] == 1)
    {
        _sliceIcon.hidden = NO;
    }
    else
    {
        _sliceIcon.hidden = YES;
    }
    
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
        AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        [app getUserInfoWithUserId:model.targetId completion:^(RCUserInfo *userInfo) {
            
            if(userInfo)
            {
                
                [_avatar setImageWithURL:[NSURL URLWithString:userInfo.portraitUri]
                        placeholderImage:[UIImage imageNamed:@"empty-avatar.png"]];
                
                _tName.text = userInfo.name;
                model.conversationTitle = userInfo.name;
                //NSLog(@"%@",userInfo.name);
                
            }
            
        }];
        
        [app getUserInfoWithUserId:model.senderUserId completion:^(RCUserInfo *userInfo) {
            
            if(userInfo)
            {
                if([model.lastestMessage isKindOfClass:[RCTextMessage class]])
                {
                    RCTextMessage *msg = (RCTextMessage*)model.lastestMessage;
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, msg.content];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCImageMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"[图片]"];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCVoiceMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"发了一段语音"];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCPTTBeginMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"发起语音对讲"];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCPTTEndMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"语音对讲结束"];
                    
                }
            }
            
        }];
        
        
        
        NSString *targetId = model.targetId;
        [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:ConversationType_PRIVATE
                                                                targetId:targetId
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                     if(nStatus == DO_NOT_DISTURB)
                                                                     {
                                                                         block_self.stateName = @"关闭消息免打扰";
                                                                     }
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];

    }
    else if(model.conversationType == ConversationType_GROUP)
    {
        
        mask.hidden = NO;
        mask.backgroundColor = [Utls groupMaskColorWithId:[model.targetId intValue]];
        
        AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        NSDictionary *groupInfo = [[GoGoDB sharedDBInstance] queryGroup:model.targetId];
    
        if(groupInfo)
        {
            _tName.text = [groupInfo objectForKey:@"name"];
            model.conversationTitle = [groupInfo objectForKey:@"name"];
            
            NSString *logo = [groupInfo objectForKey:@"logo"];
            NSString *logourl = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, logo];
            
            [_avatar setImageWithURL:[NSURL URLWithString:logourl]
                    placeholderImage:[UIImage imageNamed:@"empty-avatar.png"]];
            
            NSString *gName = _tName.text;
            NSString *showName = @"";
            
            if([gName length] > 1)
                showName = [gName substringWithRange:NSMakeRange(1, 1)];
            else if([gName length] == 1)
                showName = gName;
            
            mask.text = showName;
        }
        else
        {
            [app getGroupInfoWithUserId:model.targetId completion:^(RCUserInfo *userInfo) {
                
                if(userInfo)
                {
                    _tName.text = userInfo.name;
                    model.conversationTitle = userInfo.name;
                    
                    [_avatar setImageWithURL:[NSURL URLWithString:userInfo.portraitUri]
                            placeholderImage:[UIImage imageNamed:@"empty-avatar.png"]];
                    
                    NSString *gName = _tName.text;
                    NSString *showName = @"";
                    
                    if([gName length] > 1)
                        showName = [gName substringWithRange:NSMakeRange(1, 1)];
                    else if([gName length] == 1)
                        showName = gName;
                    
                    mask.text = showName;
                }
                
            }];
        }
        
        
        [app getUserInfoWithUserId:model.senderUserId completion:^(RCUserInfo *userInfo) {
            
            if(userInfo)
            {
                if([model.lastestMessage isKindOfClass:[RCTextMessage class]])
                {
                    RCTextMessage *msg = (RCTextMessage*)model.lastestMessage;
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, msg.content];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCImageMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"[图片]"];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCVoiceMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"发了一段语音"];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCPTTBeginMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"发起语音对讲"];
                    
                }
                else if([model.lastestMessage isKindOfClass:[RCPTTEndMessage class]])
                {
                    _lastMessage.text = [NSString stringWithFormat:@"%@: %@",
                                         userInfo.name, @"语音对讲结束"];
                    
                }
            }
            
        }];
        
        
        NSString *targetId = model.targetId;
        [[RCIMClient sharedRCIMClient] getConversationNotificationStatus:ConversationType_GROUP
                                                                targetId:targetId
                                                                 success:^(RCConversationNotificationStatus nStatus) {
                                                                     
                                                                     if(nStatus == DO_NOT_DISTURB)
                                                                     {
                                                                         block_self.stateName = @"关闭消息免打扰";
                                                                     }
                                                                     
                                                                 } error:^(RCErrorCode status) {
                                                                     
                                                                 }];
        
    }
    
    
    if([model.lastestMessage isKindOfClass:[RCInformationNotificationMessage class]])
    {
        RCInformationNotificationMessage *msg = (RCInformationNotificationMessage*)model.lastestMessage;
        _lastMessage.text = [NSString stringWithFormat:@"%@", msg.message];
        
    }
    
    //else if([model.lastestMessage isKindOfClass:[RCPTTBeginMessage class]])
    {
        NSLog(@"0000");
    }
    
    NSTimeInterval time = model.receivedTime/1000;
    NSDate *date1 = [NSDate dateWithTimeIntervalSince1970:time];
    if(date1)
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

@end
