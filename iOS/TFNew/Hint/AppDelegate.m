//
//  AppDelegate.m
//  Hint
//
//  Created by jack on 1/16/17.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "AppDelegate.h"
#import "SigninViewController.h"
#import "CMNavigationController.h"
#import "CMTabBarController.h"
#import "UserDefaultsKV.h"

#import <RongIMKit/RongIMKit.h>
#import <RongIMKit/RCIM.h>
//#import <RongIMLib/RongIMLib.h>

#import <RongPTTLib/RongPTTLib.h>

#import "RCJUser.h"
#import "SBJson4.h"
#import "GoGoDB.h"

#import "MobClick.h"
#import "DataSync.h"

#import "GlobalTouchButtonView.h"

#import "ChatViewController.h"

#define RONG_CLOUD_KEY  @"m7ua80guyso7u"//@"k51hidw10345e"//@"e5t4ouvpe564a"//m7ua80guyso7u


@interface AppDelegate () <RCIMConnectionStatusDelegate, RCIMReceiveMessageDelegate, RCIMUserInfoDataSource>
{
    WebClient *_RChttp;
    
    BOOL _isConnected;
    
    CMTabBarController *tabbar;
    
    WebClient *_msgChecker;
    
    BOOL _isChecking;
    
    WebClient *_uClient;
    
    WebClient *_autoLoginC;
    
    int _retryCount;
    
 
    GlobalTouchButtonView *_globalButton;
}
@property (strong, nonatomic) NSMutableDictionary *_rcUserInfoMap;
@property (strong, nonatomic) NSMutableDictionary *_rcGroupInfoMap;

@end

@implementation AppDelegate
@synthesize _rcUserInfoMap;
@synthesize _rcGroupInfoMap;



- (void)umengTrack {
    //    [MobClick setCrashReportEnabled:NO]; // 如果不需要捕捉异常，注释掉此行
   // [MobClick setLogEnabled:YES];  // 打开友盟sdk调试，注意Release发布时需要注释掉此行,减少io消耗
    [MobClick setAppVersion:XcodeAppVersion]; //参数为NSString * 类型,自定义app版本信息，如果不设置，默认从CFBundleVersion里取
    //
    [MobClick startWithAppkey:UMENG_APP_KEY reportPolicy:(ReportPolicy) REALTIME channelId:nil];
    //   reportPolicy为枚举类型,可以为 REALTIME, BATCH,SENDDAILY,SENDWIFIONLY几种
    //   channelId 为NSString * 类型，channelId 为nil或@""时,默认会被被当作@"App Store"渠道
}



- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    //[[UINavigationBar appearance] setBackgroundColor:[UIColor whiteColor]];
    //[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0) {
        UIUserNotificationType myTypes = UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound;
        UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:myTypes categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:settings];
    }else
    {
        UIRemoteNotificationType myTypes = UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeAlert|UIRemoteNotificationTypeSound;
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:myTypes];
    }

    
    UILocalNotification *notification = [launchOptions objectForKey:UIApplicationLaunchOptionsLocalNotificationKey];
    if (notification) {
        [self application:application didReceiveLocalNotification:notification];
    }
    
   // [self umengTrack];
    
    
    _globalButton = [[GlobalTouchButtonView alloc] initWithFrame:CGRectMake(0, 0, 56, 56)];
    
   
    
    self._rcUserInfoMap = [[NSMutableDictionary alloc] init];
    self._rcGroupInfoMap = [[NSMutableDictionary alloc] init];
    //初始化融云IM
    [self initRongCloudIM];
  
    [GoGoDB sharedDBInstance];
    
   
    
    
    User *u = [UserDefaultsKV getUser];
    if(u._authtoken)
    {
        [self didLogin];
    }
    else
    {
        SigninViewController *_login = [[SigninViewController alloc] init];
        CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:_login];
        navi.navigationBarHidden = YES;
        self.window.rootViewController = navi;
     
    }
    
     [self checkXFQ];
   
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(notifyRelogin:)
                                                 name:@"Notify_Re_Login"
                                               object:nil];
    
    return YES;
}

- (void) checkXFQ{
    
    int xfq_on_off = [[[NSUserDefaults standardUserDefaults] objectForKey:@"xfq_on_off"] intValue];
    if(xfq_on_off > 0)
    {
        _globalButton.hidden = NO;
    }
    else
    {
        _globalButton.hidden = YES;
    }
}

- (void) switchLogin{
    
    SigninViewController *_login = [[SigninViewController alloc] init];
    CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:_login];
    navi.navigationBarHidden = YES;
    self.window.rootViewController = navi;
}

- (void) skipLogin{
 
    
    tabbar = [[CMTabBarController alloc] init];
    self.window.rootViewController = tabbar;
    
}

- (void) notifyRelogin:(NSNotification*)notify{
    
    [self autoLogin];
    
}


- (void) autoLogin{
    
    if(_autoLoginC == nil)
        _autoLoginC = [[WebClient alloc] initWithDelegate:self];
    
    _autoLoginC._httpMethod = @"GET";
    _autoLoginC._method = API_LOGIN;
    
    NSString *uname = [UserDefaultsKV getAccount];
    NSString *pwd = [UserDefaultsKV getUserPwd];
    NSString *uCode = [UserDefaultsKV getUserCountryCode];
    
    if(uname && pwd)
    {
        NSMutableDictionary *params = [NSMutableDictionary dictionary];
        [params setObject:uname forKey:@"account"];
        [params setObject:md5Encode(pwd) forKey:@"userpwd"];
        
        if(uCode)
            [params setObject:uCode forKey:@"countrycode"];
        
        _autoLoginC._requestParam = params;
        
        
        IMP_BLOCK_SELF(AppDelegate);
        
        [_autoLoginC requestWithSusessBlock:^(id lParam, id rParam) {
            
            NSString *response = lParam;
            //NSLog(@"%@", response);
            
            
            SBJson4ValueBlock block = ^(id v, BOOL *stop) {
                
                if([v isKindOfClass:[NSDictionary class]])
                {
                    int code = [[v objectForKey:@"code"] intValue];
                    if(code == 1)
                    {
                        NSDictionary *value = [v objectForKey:@"text"];
                        
                        User *u = [[User alloc] initWithDicionary:value];
                       // u._authtoken = token;
                        [UserDefaultsKV saveUser:u];
                        
                        [block_self didLogin];
                    }
                    else
                    {
                        [block_self didLogout];
                        
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
    
}

- (void) initShareSDK{
    
    
    
    
}


- (void) updatePersonInfo{
    
    if(_uClient == nil)
    {
        _uClient = [[WebClient alloc] initWithDelegate:self];
    }
    
    _uClient._method = API_USER_PROFILE;
    _uClient._httpMethod = @"GET";
    
    User *u = [UserDefaultsKV getUser];
    
    _uClient._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                              u._userId,@"userid",
                              nil];
    
    
    // IMP_BLOCK_SELF(AppDelegate);
    
    [_uClient requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
                    
                    //u._userId = [NSString stringWithFormat:@"%d", [[v objectForKey:@"id"] intValue]];
                    [u updateUserInfo:v];
                    
                    [UserDefaultsKV saveUser:u];
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


- (void) didLogin{
    

    User *u = [UserDefaultsKV getUser];
    id key = [NSString stringWithFormat:@"user_%@_login_once", u._authtoken];
    [[NSUserDefaults standardUserDefaults] setObject:@"1" forKey:key];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    
    tabbar = [[CMTabBarController alloc] init];
    self.window.rootViewController = tabbar;
    
    _retryCount = 0;
    
    [self loginRongCloud];
    

   // [self updatePersonInfo];
    
    [[DataSync sharedDataSync] syncMyContacts];
    [[DataSync sharedDataSync] syncMyGroups];
    
    [tabbar.view addSubview:_globalButton];
    _globalButton.center = CGPointMake(SCREEN_WIDTH-30, SCREEN_HEIGHT/2);
    
    
    [[DataSync sharedDataSync] syncTFOrgs];
    
}


- (void) pushToChat:(NSString*)targetId type:(int)type{
    
    ChatViewController *conversationVC = [[ChatViewController alloc]init];
    if(type == 0)
        conversationVC.conversationType = ConversationType_PRIVATE;
    else
        conversationVC.conversationType = ConversationType_GROUP;
    conversationVC.targetId = targetId;
    conversationVC._userName = @"";
    conversationVC.hidesBottomBarWhenPushed = YES;
    
    if(type == 1)
    {
        conversationVC._groupType = 1;
    }
    
    UINavigationController *msgCtrl = [tabbar.viewControllers objectAtIndex:0];
    [tabbar setCurrentTabIndex:0];
    [msgCtrl pushViewController:conversationVC animated:YES];
    

}
- (void) switchAtTabIndex:(int)index{
    
    [tabbar setCurrentTabIndex:index];
    
}

- (void) didLogout{
    
    [UserDefaultsKV clearUser];
    [GoGoDB logoutDB];
    
    //[[DataSync sharedDataSync] clearAll];
    
    [[RCIM sharedRCIM] disconnect];
    
    SigninViewController *_login = [[SigninViewController alloc] init];
    CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:_login];
    navi.navigationBarHidden = YES;
    
    self.window.rootViewController = navi;

}



- (void) initRongCloudIM{
    
    //初始化融云SDK，
    [[RCIM sharedRCIM] initWithAppKey:RONG_CLOUD_KEY];
    [RCIM sharedRCIM].userInfoDataSource = self;
    [RCIM sharedRCIM].receiveMessageDelegate = self;
    
    [[RCIM sharedRCIM] setConnectionStatusDelegate:self];
    
    
    [RCIM sharedRCIM].globalMessageAvatarStyle = RC_USER_AVATAR_CYCLE;
    
   //[[RCPTTClient sharedPTTClient] setServerURL:@"http://35.164.107.27:8080/rce/restapi/ptt"];
    [[RCPTTClient sharedPTTClient] setServerURL:@"http://120.26.42.225:8080/rce/restapi/ptt"];

    [[RCIMClient sharedRCIMClient] setServerInfo:@"103.36.132.10:80" fileServer:nil];
    
}



- (void) loginRongCloud{
    
    if(_RChttp == nil)
    {
        _RChttp = [[WebClient alloc] initWithDelegate:self];
    }
    
    User *u = [UserDefaultsKV getUser];
    
    if(u == nil)
        return;
    
    RCUserInfo *user = [[RCUserInfo alloc] initWithUserId:u._userId
                                                     name:u._userName portrait:nil];
    [RCIM sharedRCIM].currentUserInfo = user;
    user.portraitUri = u._avatar;
    [RCIMClient sharedRCIMClient].currentUserInfo = user;

    
    NSString *RongCloud_IM_User_Token = [[NSUserDefaults standardUserDefaults] objectForKey:@"RongCloud_IM_User_Token"];
    if(RongCloud_IM_User_Token == nil)
    {
        RongCloud_IM_User_Token = u._authtoken;
        [[NSUserDefaults standardUserDefaults] setObject:RongCloud_IM_User_Token forKey:@"RongCloud_IM_User_Token"];
        [[NSUserDefaults standardUserDefaults] synchronize];

    }
    
    [self connectRCloudServer:RongCloud_IM_User_Token];
    
}

- (void) connectRCloudServer:(NSString*)token{
    
    // 快速集成第二步，连接融云服务器
    
    IMP_BLOCK_SELF(AppDelegate);
    
    
    [[RCIM sharedRCIM] connectWithToken:token
                                success:^(NSString *userId) {
                                    
                                    NSLog(@"%@",userId);
                                    
                                    
                                    _retryCount = 0;
                                    
                                    _isConnected = YES;
                                    [block_self updateBadgeValueForTabBarItem];
                                    
                                    [block_self updateDeviceToken];
                                    
                                    
                                } error:^(RCConnectErrorCode status) {
                                    
                                    
                                    NSLog(@"connectWithToken failed: %d", (int)status);
                                    
                                    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"RongCloud_IM_User_Token"];
                                    [[NSUserDefaults standardUserDefaults] synchronize];
                                    
                                    if(_retryCount <= 0)
                                    {
                                        _retryCount++;
                                        [block_self loginRongCloud];
                                    }
                                    
                                } tokenIncorrect:^{
                                    NSLog(@"token 过期");
                                    
                                    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"RongCloud_IM_User_Token"];
                                    [[NSUserDefaults standardUserDefaults] synchronize];
                                    
                                    if(_retryCount <= 0)
                                    {
                                        _retryCount++;
                                        [block_self loginRongCloud];
                                    }
                                    
                                    
                                }];

}

- (void) updateDeviceToken{
    
    NSString *token = [[NSUserDefaults standardUserDefaults] objectForKey:kDeviceToken];
    
    if(token){
        
        [[RCIMClient sharedRCIMClient] setDeviceToken:token];
    }
    
    
    
    
}

#pragma mark - RCIMConnectionStatusDelegate

/**
 *  网络状态变化。
 *
 *  @param status 网络状态。
 */

- (void)onRCIMConnectionStatusChanged:(RCConnectionStatus)status
{
    if (status == ConnectionStatus_KICKED_OFFLINE_BY_OTHER_CLIENT) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示"
                                                        message:@"您的帐号在别的设备上登录，您被迫下线！"
                                                       delegate:self
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil, nil];
        alert.tag = 1314;
        [alert show];
        
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(alertView.tag == 1314)
    {
        [self didLogout];
    }
}


- (void)getUserInfoWithUserId:(NSString *)userId completion:(void (^)(RCUserInfo *userInfo))completion{
    
    //NSLog(@"need user %@", userId);
    
    RCUserInfo* cachedUser = [[GoGoDB sharedDBInstance] queryUser:userId];
    if(cachedUser)
    {
        return completion(cachedUser);
    }
    else
    {
        RCJUser *u = [self._rcUserInfoMap objectForKey:userId];
        if(u == nil)
        {
            u = [[RCJUser alloc] init];
            [self._rcUserInfoMap setObject:u forKey:userId];
            [u loadUserInfo:userId completion:completion];
        }
        else
        {
            RCUserInfo *user = [[RCUserInfo alloc] init];
            user.userId = userId;
            user.name = [u._userInfo objectForKey:@"fullname"];
            user.portraitUri = [u._userInfo objectForKey:@"avatarurl"];
            
            return completion(user);
            
        }
    }
    
    /*
     
     RCJUser *u = [self._rcUserInfoMap objectForKey:userId];
     if(u == nil)
     {
     
     u = [[RCJUser alloc] init];
     [self._rcUserInfoMap setObject:u forKey:userId];
     
     
     //访问数据库缓存
     RCUserInfo* cachedUser = [[GoGoDB sharedDBInstance] queryUser:userId];
     if(!cachedUser)
     {
     //            cachedUser = [[RCUserInfo alloc] init];
     //            cachedUser.userId = userId;
     //            cachedUser.name = @"";
     //            cachedUser.portraitUri = nil;
     
     completion(cachedUser);
     }
     
     //completion(cachedUser);
     
     
     [u loadUserInfo:userId completion:completion];
     
     }
     else
     {
     RCUserInfo *user = [[RCUserInfo alloc] init];
     user.userId = userId;
     user.name = [u._userInfo objectForKey:@"fullname"];
     user.portraitUri = [u._userInfo objectForKey:@"avatarurl"];
     
     return completion(user);
     
     }
     
     
     */
    
    
}

- (void)getGroupInfoWithUserId:(NSString *)targetId completion:(void (^)(RCUserInfo *userInfo))completion{
    

    RCUserInfo *group = [[RCUserInfo alloc] init];
    group.userId = targetId;
    
    
    NSDictionary* cachedGInfo = [[GoGoDB sharedDBInstance] queryGroup:targetId];
    if(cachedGInfo)
    {
        group.name = [cachedGInfo objectForKey:@"name"];
        group.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [cachedGInfo objectForKey:@"logo"]];
        
        return completion(group);
    }
    
    RCJUser *u = [self._rcGroupInfoMap objectForKey:targetId];
    if(u == nil)
    {
        
        u = [[RCJUser alloc] init];
        [self._rcGroupInfoMap setObject:u forKey:targetId];
        [u loadGroupInfo:targetId completion:completion];
        

    }
    else
    {
        RCUserInfo *user = [[RCUserInfo alloc] init];
        user.userId = targetId;
        user.name = [u._userInfo objectForKey:@"name"];
        user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [u._userInfo objectForKey:@"logo"]];
        
        return completion(user);
        
    }
    
    
    return completion(group);
    
}


- (void)onRCIMReceiveMessage:(RCMessage *)message left:(int)left{
    
    if([message.content isKindOfClass:[RCPTTBeginMessage class]])
    {
        NSDictionary *dic = @{@"userid":message.senderUserId,
                              @"type":[NSNumber numberWithInt:message.conversationType],
                              @"targetId":message.targetId};
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PTT_call_notify" object:dic];
    }
    else if([message.content isKindOfClass:[RCPTTEndMessage class]])
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PTT_call_notify" object:nil];
    }
    
    NSLog(@"%@", [message.content class]);
    
    
    [self updateBadgeValueForTabBarItem];
}

- (void)updateBadgeValueForTabBarItem
{
    
    if(!_isConnected)
        return;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        int count = [[RCIMClient sharedRCIMClient] getUnreadCount:@[@(ConversationType_PRIVATE),@(ConversationType_DISCUSSION), @(ConversationType_APPSERVICE), @(ConversationType_PUBLICSERVICE),@(ConversationType_GROUP)]];//@(ConversationType_SYSTEM)
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Refresh_Chat_List"
                                                            object:@{@"count":[NSNumber numberWithInt:count]}];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"Refresh_Unread_message_count" object:nil];
        
    });
}



- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
    [application registerForRemoteNotifications];
}


- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    NSString *token = [[deviceToken description] stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"<>"]];
    token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    if([token length])
    {
        
        [[NSUserDefaults standardUserDefaults] setObject:token forKey:kDeviceToken];
        [[NSUserDefaults standardUserDefaults] synchronize];
        
        [[RCIMClient sharedRCIMClient] setDeviceToken:token];
        
    }
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
//    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示"
//                                                    message:[error description]
//                                                   delegate:nil
//                                          cancelButtonTitle:@"OK"
//                                          otherButtonTitles:nil, nil];
//    [alert show];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    
    
    [self updateBadgeValueForTabBarItem];
    
   // [self checkNewRequestMessages];
    
    
}



- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


- (void) proccessMessags:(NSArray*)list{
    
    for(NSDictionary *msg in list)
    {
        [[GoGoDB sharedDBInstance] saveRequestMessages:msg];
    }
    
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"ReceivedNewRequestMessagesNotify" object:nil];
}

@end
