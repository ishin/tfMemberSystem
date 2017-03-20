//
//  CMTabBarController.m
//  CMTabBarController
//
//  Created by mac on 13-8-13.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import "CMTabBarController.h"
#import "CMNavigationController.h"
#import "UIImage+Color.h"
#import "MessagesViewController.h"
#import "MeViewController.h"
#import "ContactsViewController.h"
#import "GoGoDB.h"
#import "ExploreViewController.h"


@implementation CMTabBarController


#pragma mark - view lifecycle

- (void ) dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor clearColor];

    // do it just test for the push and merge
    self.delegate = self;
    
    [self setViewControllers:[NSArray arrayWithObjects:
                              [[CMNavigationController alloc] initWithRootViewController:[[MessagesViewController alloc] init]],
                              [[CMNavigationController alloc] initWithRootViewController:[[ExploreViewController alloc] init]],
                              [[CMNavigationController alloc] initWithRootViewController:[[ContactsViewController alloc] init]],
                              [[CMNavigationController alloc] initWithRootViewController:[[MeViewController alloc] init]],
                              nil]];
    
//    [self cofigTabBar];
    [self configTabBarAboveIOS5];
}

#pragma mark tabBar 相关


- (void)configTabBarAboveIOS5{

    
    self.tabBar.backgroundImage = [UIImage imageWithColor:RGB(0xf8, 0xf8, 0xf8) andSize:CGSizeMake(1,1)];
    
    if ([self.tabBar respondsToSelector:@selector(setShadowImage:)]) {
        self.tabBar.shadowImage = [UIImage imageWithColor:[UIColor clearColor] andSize:self.tabBar.bounds.size];
    }
    
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                              0,
                                                              SCREEN_WIDTH, 0.5)];
    line.backgroundColor = RGB(0xb7, 0xb7, 0xb7);
    [self.tabBar addSubview:line];
    
    
    
    self.tabBar.backgroundColor = RGB(0xf8, 0xf8, 0xf8);
   self.tabBar.selectionIndicatorImage = [UIImage imageWithColor:[UIColor clearColor] andSize:self.tabBar.frame.size];

    
    
    
    
    CGSize itemSize = CGSizeMake(SCREEN_WIDTH/4, self.tabBar.frame.size.height);
    int tab_width = (int)itemSize.width;

    int item_height = 30;
    
    barItem01 = [[UIImageView alloc] initWithFrame:CGRectMake(0, 3, itemSize.width, item_height)];
    barItem01.tag = 10;
    barItem01.image = [UIImage imageNamed:@"message_down.png"];
    [self.tabBar addSubview:barItem01];
    barItem01.contentMode = UIViewContentModeCenter;
    
    barItem03 = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(barItem01.frame),3,
                                                              itemSize.width, 30)];
    barItem03.tag = 11;
    barItem03.image = [UIImage imageNamed:@"explore_up.png"];
    [self.tabBar addSubview:barItem03];
    barItem03.contentMode = UIViewContentModeCenter;
    
    barItem04 = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(barItem03.frame), 3,
                                                              itemSize.width, item_height)];
    barItem04.tag = 12;
    barItem04.image = [UIImage imageNamed:@"contacts_up.png"];
    [self.tabBar addSubview:barItem04];
    barItem04.contentMode = UIViewContentModeCenter;
    
    
    barItem05 = [[UIImageView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(barItem04.frame), 3,
                                                              itemSize.width, item_height)];
    barItem05.tag = 13;
    barItem05.image = [UIImage imageNamed:@"me_up.png"];
    [self.tabBar addSubview:barItem05];
    barItem05.contentMode = UIViewContentModeCenter;
    
    float top = CGRectGetMaxY(barItem01.frame)-6;
   
    tab1Title = [[UILabel alloc] initWithFrame:CGRectMake(0, top, tab_width, 20)];
    tab1Title.backgroundColor = [UIColor clearColor];
    tab1Title.font = [UIFont systemFontOfSize:11];
    tab1Title.textColor = RGB(0xa3, 0xa3, 0xa3);
    tab1Title.textAlignment = NSTextAlignmentCenter;
    tab1Title.text = @"消息";
    [self.tabBar addSubview:tab1Title];

    
    tab3Title = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tab1Title.frame), top, tab_width, 20)];
    tab3Title.backgroundColor = [UIColor clearColor];
    tab3Title.font = [UIFont systemFontOfSize:11];
    tab3Title.textColor = [UIColor whiteColor];
    tab3Title.textAlignment = NSTextAlignmentCenter;
    tab3Title.text = @"工作";
    [self.tabBar addSubview:tab3Title];
    
    tab4Title = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tab3Title.frame), top, tab_width, 20)];
    tab4Title.backgroundColor = [UIColor clearColor];
    tab4Title.font = [UIFont systemFontOfSize:11];
    tab4Title.textColor = RGB(0xa3, 0xa3, 0xa3);
    tab4Title.textAlignment = NSTextAlignmentCenter;
    tab4Title.text = @"联系人";
    [self.tabBar addSubview:tab4Title];
    
    
    tab5Title = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(tab4Title.frame), top, tab_width, 20)];
    tab5Title.backgroundColor = [UIColor clearColor];
    tab5Title.font = [UIFont systemFontOfSize:11];
    tab5Title.textColor = RGB(0xa3, 0xa3, 0xa3);
    tab5Title.textAlignment = NSTextAlignmentCenter;
    tab5Title.text = @"我的";
    [self.tabBar addSubview:tab5Title];
    
    tab1Title.center = CGPointMake(barItem01.center.x, tab1Title.center.y);
   // tab2Title.center = CGPointMake(barItem02.center.x, tab2Title.center.y);
    tab3Title.center = CGPointMake(barItem03.center.x, tab3Title.center.y);
    tab4Title.center = CGPointMake(barItem04.center.x, tab4Title.center.y);
    tab5Title.center = CGPointMake(barItem05.center.x, tab5Title.center.y);
    
    
    _curIndex = -1;
    
    self.selectedIndex = 0;
    
    [self resetCurrentState];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(updateUnReadMessages:)
                                                 name:@"Refresh_Unread_message_count"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(notifyRefreshStatus:)
                                                 name:@"NotifyRefreshRequestStatus"
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(receivedNewMessageNotify:)
                                                 name:@"ReceivedNewRequestMessagesNotify"
                                               object:nil];

    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(refreshRedPoint:)
                                                 name:@"Notify_refresh_red_point"
                                               object:nil];
    
    numberMsgs = [[UILabel alloc] initWithFrame:CGRectMake(tab1Title.center.x+10, -2, 20, 20)];
    [self.tabBar addSubview:numberMsgs];
    numberMsgs.backgroundColor = [UIColor redColor];
    numberMsgs.layer.cornerRadius = 10;
    numberMsgs.textColor = [UIColor whiteColor];
    numberMsgs.clipsToBounds = YES;
    numberMsgs.textAlignment = NSTextAlignmentCenter;
    numberMsgs.font = [UIFont systemFontOfSize:12];
    
    numberMsgs.hidden = YES;
    
    
    numberReqs = [[UILabel alloc] initWithFrame:CGRectMake(tab3Title.center.x+5, 2, 10, 10)];
    [self.tabBar addSubview:numberReqs];
    numberReqs.backgroundColor = [UIColor redColor];
    numberReqs.layer.cornerRadius = 5;
    numberReqs.clipsToBounds = YES;
    
    numberReqs.hidden = YES;
    
    
    
    
    _alertPoint = [[UILabel alloc] initWithFrame:CGRectMake(tab5Title.center.x+5, 2, 10, 10)];
    _alertPoint.layer.cornerRadius = 5;
    _alertPoint.clipsToBounds = YES;
    _alertPoint.backgroundColor = THEME_RED_COLOR;
    [self.tabBar addSubview:_alertPoint];
    
    _alertPoint.hidden = YES;
    
   // [self refreshRedPoint:nil];
}


- (void) refreshRedPoint:(id)sender{
    
   
}

- (void) notifyRefreshStatus:(NSNotification*)notify{
    
    
    int unreadCount = [[GoGoDB sharedDBInstance] unreadMessagesCount];
    
    if(unreadCount > 0)
    {
        numberReqs.hidden = NO;
    }
    else
    {
        numberReqs.hidden = YES;
    }
}

- (void) receivedNewMessageNotify:(NSNotification*)notify{
    
    
    int unreadCount = [[GoGoDB sharedDBInstance] unreadMessagesCount];
    
    if(unreadCount > 0)
    {
        numberReqs.hidden = NO;
    }
    else
    {
        numberReqs.hidden = YES;
    }
}

- (void) updateUnReadMessages:(NSNotification*)notify{
    
    int countMsgs = [[RCIMClient sharedRCIMClient] getUnreadCount:@[@(ConversationType_PRIVATE),@(ConversationType_DISCUSSION), @(ConversationType_APPSERVICE), @(ConversationType_PUBLICSERVICE),@(ConversationType_GROUP),@(ConversationType_SYSTEM)]];;
    if(countMsgs > 0)
    {
        numberMsgs.hidden = NO;
        if(countMsgs >= 100)
        {
            numberMsgs.text = @"...";
        }
        else
        {
            numberMsgs.text = [NSString stringWithFormat:@"%d", countMsgs];
        }
        
        [UIApplication sharedApplication].applicationIconBadgeNumber = countMsgs;
    }
    else
    {
        numberMsgs.hidden = YES;
        
        [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
    }
    
}

- (void) showTabBar:(NSNotification*) notify{
    
    self.tabBar.hidden = NO;
    
    [UIView animateWithDuration:0.5
                     animations:^{
                         
                         CGRect rc =  self.tabBar.frame;
                         rc.origin.y = rc.origin.y-rc.size.height;
                         self.tabBar.frame = rc;
                         
                     } completion:^(BOOL finished) {
                         
                         
                     }];
    
}


- (void) hiddenTabBar:(NSNotification*) notify{
    
    [UIView animateWithDuration:0.5
                     animations:^{
                         
                         CGRect rc =  self.tabBar.frame;
                         rc.origin.y = rc.origin.y+rc.size.height;
                         self.tabBar.frame = rc;
                         
                     } completion:^(BOOL finished) {
                        
                         self.tabBar.hidden = YES;
                     }];
    
}

- (void) setCurrentTabIndex:(int)index{
    
    self.selectedIndex = index;
    [self resetCurrentState];
}

- (void) resetCurrentState{
    
    if(_curIndex == self.selectedIndex && _curIndex == 2)
    {
        // NSLog(@"OK1");
        [[NSNotificationCenter defaultCenter] postNotificationName:@"QuickStart_Clicked" object:nil];
    }
    
    _curIndex = (int)self.selectedIndex;
    
    if(self.selectedIndex == 0)
    {
        tab1Title.textColor = THEME_RED_COLOR;
        //tab2Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab3Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab4Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab5Title.textColor = RGB(0xa3, 0xa3, 0xa3);
   
        barItem01.image = [UIImage imageNamed:@"message_xindown.png"];
        barItem03.image = [UIImage imageNamed:@"explore_up.png"];
        barItem04.image = [UIImage imageNamed:@"contacts_up.png"];
        barItem05.image = [UIImage imageNamed:@"me_up.png"];
        
        
    }
    else if(self.selectedIndex == 1)
    {
        tab1Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        //tab2Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab3Title.textColor = THEME_RED_COLOR;
        tab4Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab5Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        
        barItem01.image = [UIImage imageNamed:@"message_xinup.png"];
        //barItem02.image = [UIImage imageNamed:@"explore_up.png"];
        barItem03.image = [UIImage imageNamed:@"explore_down.png"];
        barItem04.image = [UIImage imageNamed:@"contacts_up.png"];
        barItem05.image = [UIImage imageNamed:@"me_up.png"];
  
        

    }
    else if(self.selectedIndex == 2)
    {
        
        tab1Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        //tab2Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab3Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab4Title.textColor = THEME_RED_COLOR;
        tab5Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        
        barItem01.image = [UIImage imageNamed:@"message_xinup.png"];
        //barItem02.image = [UIImage imageNamed:@"explore_up.png"];
        barItem03.image = [UIImage imageNamed:@"explore_up.png"];
        barItem04.image = [UIImage imageNamed:@"contacts_down.png"];
        barItem05.image = [UIImage imageNamed:@"me_up.png"];

    }
    else if(self.selectedIndex == 3)
    {
        
        tab1Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        //tab2Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab3Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab4Title.textColor = RGB(0xa3, 0xa3, 0xa3);
        tab5Title.textColor = THEME_RED_COLOR;
        
        barItem01.image = [UIImage imageNamed:@"message_xinup.png"];
        //barItem02.image = [UIImage imageNamed:@"explore_up.png"];
        barItem03.image = [UIImage imageNamed:@"explore_up.png"];
        barItem04.image = [UIImage imageNamed:@"contacts_up.png"];
        barItem05.image = [UIImage imageNamed:@"me_down.png"];
        

    }
    else if(self.selectedIndex == 4)
    {
        
        
        
    }

}

- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController{
    
    [self resetCurrentState];
}

- (BOOL)tabBarController:(UITabBarController *)tabBarController shouldSelectViewController:(UIViewController *)viewController{
    
    return YES;
}



@end
