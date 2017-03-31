
//
//  RCLocationPickerViewController.m
//  iOS-IMKit
//
//  Created by YangZigang on 14/10/31.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//

#import "RealTimeLocationViewController.h"
#import "HeadCollectionView.h"
#import "MBProgressHUD.h"
#import "RCAnnotation.h"
#import "RCDUtilities.h"
#import "RCLocationConvert.h"
#import "RCLocationView.h"
#import <MapKit/MapKit.h>
#import <RongIMKit/RongIMKit.h>
#import "UIColor+RCColor.h"
#import "UserDefaultsKV.h"

#import "WSUser.h"
#import "WSGroup.h"
#import "SBJson4.h"
#import <CoreLocation/CoreLocation.h>
#import "RRScrollView.h"

@interface RealTimeLocationViewController () < MKMapViewDelegate, HeadCollectionTouchDelegate,
    UIActionSheetDelegate, UIAlertViewDelegate, CLLocationManagerDelegate, RRScrollViewDelegate>
{
    
    WebClient *_uploadGPS;
    BOOL _isUploading;
    
    NSTimer *_timer;
    WebClient *_gpsUpdate;
    
    UIView *_maskView;
    UIView *_userListView;
    RRScrollView *_uScroll;
    int _pageNum;
    //int _cellHeight;
    
    UIImageView *_ringImage;
}

@property(nonatomic, strong) MKMapView *mapView;
@property(nonatomic, strong) UIView *headBackgroundView;
@property(nonatomic, strong) NSMutableDictionary *userAnnotationDic;
@property(nonatomic, assign) MKCoordinateSpan theSpan;
@property(nonatomic, assign) MKCoordinateRegion theRegion;
@property(nonatomic, assign) BOOL isFristTimeToLoad;
@property(nonatomic, strong) HeadCollectionView *headCollectionView;

@property (nonatomic, strong) CLLocationManager *_locationManager;
@property (nonatomic, strong) NSString* _myUid;
@property (nonatomic, strong) NSMutableDictionary *_mapUsers;
@property (nonatomic, assign) int _willAccesUid;

@end

@implementation RealTimeLocationViewController
@synthesize _locationManager;
@synthesize _shareMembs;
@synthesize _myUid;
@synthesize _targetObj;
@synthesize _mapUsers;
@synthesize _willAccesUid;

- (instancetype)init {
  if (self = [super init]) {
  }
  return self;
}

- (void) backAction:(id)sender{
    
    [self quitButtonPressed];
}

- (void) initLocation{
    
    self._locationManager = [[CLLocationManager alloc]init];
    _locationManager.delegate = self;
    
    //设置每隔100米更新位置
    _locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters;
    _locationManager.distanceFilter = 100;
    
    //以此来判断，是否是ios8
    if ([_locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)]) {
        [_locationManager requestWhenInUseAuthorization];
    }
    
    
    if([CLLocationManager headingAvailable])
    {
        self._locationManager.headingFilter = 10; //10°
        [self._locationManager startUpdatingHeading];
    }
    
    [_locationManager startUpdatingLocation];
}

- (void)viewDidLoad {
  [super viewDidLoad];
    
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];
    

    
    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
    backBtn.frame = CGRectMake(0, 0, 25, 44);
    [backBtn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    self.navigationItem.leftBarButtonItem = backBarButtonItem;
    
    User *u = [UserDefaultsKV getUser];
    self._myUid = u._userId;

  _isFristTimeToLoad = YES;
  self.userAnnotationDic = [[NSMutableDictionary alloc] init];
  self.mapView = [[MKMapView alloc]
      initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width,
                               [UIScreen mainScreen].bounds.size.height)];
  [self.mapView setMapType:MKMapTypeStandard];
  self.mapView.showsUserLocation = YES;
  self.mapView.delegate = self;
  self.mapView.showsUserLocation = NO;
  [self.view addSubview:self.mapView];
    
    
    self._mapUsers = [NSMutableDictionary dictionary];
    WSUser *_my = nil;
    for(WSUser *uu in _shareMembs)
    {
        if(uu.userId == [_myUid intValue])
        {
            _my  = uu;
           
        }
        [_mapUsers setObject:uu forKey:[NSNumber numberWithInt:uu.userId]];
        
    }
    
    if(_my)
    {
        self.headCollectionView = [[HeadCollectionView alloc]
                                   initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, 95)
                                   participants:[NSArray arrayWithObject:_my]
                                   touchDelegate:self];
        [self.view addSubview:self.headCollectionView];
        self.headCollectionView.touchDelegate = self;

    }
    else
    {
        [self backButtonPressed];
        return;
    }
    
    
  UIImageView *gpsImg = [[UIImageView alloc]
      initWithFrame:CGRectMake(18,
                               SCREEN_HEIGHT - 80 - 64,
                               50, 50)];
  gpsImg.image = [UIImage imageNamed:@"gps.png"];
  [self.view addSubview:gpsImg];
  gpsImg.userInteractionEnabled = YES;
  UITapGestureRecognizer *gpsImgTap = [[UITapGestureRecognizer alloc]
      initWithTarget:self
              action:@selector(tapGpsImgEvent:)];

  [gpsImg addGestureRecognizer:gpsImgTap];
    
    
    [self initLocation];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(notifyGPSReceived:)
                                                 name:@"GPS_Notify_update"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(notifyGPSExitReceived:)
                                                 name:@"GPS_Notify_Exit_ShareLocation"
                                               object:nil];
    
    [self startGpsUpdage];
    
    
    int width = 45*5+40+40;
    
    _maskView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(onTapSelected:)];
    [_maskView addGestureRecognizer:tap];
    
    _userListView = [[UIView alloc] initWithFrame:CGRectMake((SCREEN_WIDTH-width)/2, 0, width, 80)];
    _userListView.backgroundColor = RGBA(0x33, 0x33, 0x33,0.8);
    _userListView.layer.cornerRadius = 8;
    _userListView.clipsToBounds = YES;
    [_maskView addSubview:_userListView];
    
    
    _ringImage = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 40, 40)];
    _ringImage.image = [UIImage imageNamed:@"sharelocation_ring.png"];
    
    _uScroll = [[RRScrollView alloc] initWithFrame:CGRectMake(15, 10,
                                                              CGRectGetWidth(_userListView.frame)-30,
                                                              CGRectGetHeight(_userListView.frame) - 20)];
    [_userListView addSubview:_uScroll];
    _uScroll.delegate_ = self;
    _uScroll.backgroundColor = [UIColor clearColor];
    
    
    
}

- (void) onTapSelected:(id)sender{
    
    [_maskView removeFromSuperview];
}

- (void) showUserListView:(WSUser*)selUser{
    
    int pageNum = 1;
    int height = 80;
    
    NSArray *membs = [self.headCollectionView getParticipantsUserInfo];
    
    int count = (int)[membs count];
    
    if(count <= 6)
    {
        pageNum = 1;
        height = 80;
    }
    else if(count > 6 && count <= 12)
    {
        height = 160;
        pageNum = 1;
    }
    else if(count > 12)
    {
        height = 240;
        
        pageNum = count/18;
        if(count%18 > 0)
        {
            pageNum++;
        }
    }
    
    
    self._willAccesUid = selUser.userId;
    
    int targetIndex = 0;
    for(int i = 0 ; i < [membs count]; i++)
    {
        WSUser *tu = [membs objectAtIndex:i];
        if(tu.userId == _willAccesUid)
        {
            targetIndex = i;
            break;
        }
    }
    
    int targetPageNum = targetIndex/18;
    
    
    _pageNum = pageNum;
    
    CGRect rc = _userListView.frame;
    rc.size.height = height;
    _userListView.frame = rc;
    
    [self.view addSubview:_maskView];
    _userListView.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT/2-32);
    
    _uScroll.frame = CGRectMake(15, 10,
                                CGRectGetWidth(_userListView.frame)-30,
                                height - 20);
    _uScroll.backgroundColor = [UIColor clearColor];
    
    _uScroll.currentPageIndex = targetPageNum;
    [_uScroll loadData];
    
    
    
}


- (int) numberOfScrollPages{
    
    return _pageNum;
}

- (UIView*) scrollPageViewAtIndex:(int)pageIndex{
    
    CGRect rc = _uScroll.bounds;
    
    UIView *cells = [[UIView alloc] initWithFrame:rc];
    cells.backgroundColor = [UIColor clearColor];
    NSArray *membs = [self.headCollectionView getParticipantsUserInfo];
   
    int from = pageIndex*18;
    int to = from+18;
    if(to > [membs count])
        to = (int)[membs count];
    
    int x = 5;
    int y = 0;
    for(int i = from; i < to; i++)
    {
        WSUser *u = [membs objectAtIndex:i];
        
        if((i > from) && (i - from)%6 == 0)
        {
            y+=70;
            x = 5;
        }
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = CGRectMake(x, y, 40, 40);
        [cells addSubview:btn];
        btn.tag = u.userId;
        [btn addTarget:self action:@selector(viewUserOnMap:) forControlEvents:UIControlEventTouchUpInside];
        
        
        UIImageView *userHead = [[UIImageView alloc] init];
        [userHead
         setImageWithURL:[NSURL URLWithString:u.avatarurl]
         placeholderImage:[RCDUtilities imageNamed:@"default_portrait_msg"
                                          ofBundle:@"RongCloud.bundle"]];
        [userHead setFrame:btn.bounds];
        [btn addSubview:userHead];
        
        userHead.layer.cornerRadius = 20;
        userHead.layer.masksToBounds = YES;
        userHead.layer.borderWidth = 1.0f;
        userHead.layer.borderColor = [UIColor whiteColor].CGColor;

        
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(x - 3,
                                                                   y+40,
                                                                   46,
                                                                   20)];
        nameL.backgroundColor = [UIColor clearColor];
        [cells addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:12];
        nameL.textAlignment = NSTextAlignmentCenter;
        nameL.textColor  = [UIColor whiteColor];
        nameL.text = u.fullname;

        if(u.userId == _willAccesUid)
        {
            [btn addSubview:_ringImage];
            //_ringImage.frame =
        }
        
        x+=45;
        
        
    }
    
    
    return cells;
    
}

- (void) viewUserOnMap:(UIButton*)sender{
    
    [sender addSubview:_ringImage];
    
    int uid =  (int)sender.tag;
    [self onSelectUserLocationWithUserId:[NSString stringWithFormat:@"%d", uid]];
    
}

- (void) updateMembsGPSLocation{
    
    for(WSUser *uu in _shareMembs)
    {
        if(uu.userId != [_myUid intValue])
        {
            [uu startGpsUpdage];
        }
    }
}


- (void) endGPSTesting{
    
    [self stopGpsUpdate];
    [_locationManager stopUpdatingLocation];
    
    if([CLLocationManager headingAvailable])
    {
        [self._locationManager stopUpdatingHeading];
    }
    
    [self uploadingMyGPSLocation:nil];

    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    /*
    for(WSUser *uu in _shareMembs)
    {
        if(uu.userId != [_myUid intValue])
        {
            [uu stopGpsUpdate];
        }
    }
     */
}

- (void) notifyGPSReceived:(NSNotification *)notify
{
    
    WSUser *uu = notify.object;
    if(uu)
    {
        [self onParticipantsJoin:uu];
    }
    
}

- (void) notifyGPSExitReceived:(NSNotification*)notify{
    
    WSUser *uu = notify.object;
    if(uu)
    {
        [self onParticipantsQuit:uu];
    }

}

- (void)viewWillAppear:(BOOL)animated {
  [super viewWillAppear:animated];
 
    
  CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
  if (status == kCLAuthorizationStatusDenied) {
   // [hud hide:YES];
    UIAlertView *alertView =
        [
            [UIAlertView alloc] initWithTitle:@"无法访问"
                                      message:@"没"
                                              @"有权限访问位置信息，请从设置-"
                                              @"隐私-定位服务 "
                                              @"中打开位置访问权限"
                                     delegate:nil
                            cancelButtonTitle:@"确定"
                            otherButtonTitles:nil];
    [alertView show];
  }
}
- (void)viewWillDisappear:(BOOL)animated {
  [super viewWillDisappear:animated];
  
}

- (void)tapGpsImgEvent:(UIGestureRecognizer *)gestureRecognizer {
  [self
      onSelectUserLocationWithUserId:[RCIM sharedRCIM].currentUserInfo.userId];
}
- (void)onUserSelected:(WSUser *)user atIndex:(NSUInteger)index {
  [self onSelectUserLocationWithUserId:[NSString stringWithFormat:@"%d", user.userId]];
}

- (BOOL)quitButtonPressed {
    
  UIActionSheet *actionSheet =
      [[UIActionSheet alloc] initWithTitle:@"是否结束位置共享？"
                                  delegate:self
                         cancelButtonTitle:@"取消"
                    destructiveButtonTitle:@"结束"
                         otherButtonTitles:nil];
  [actionSheet showInView:self.view];
  return YES;
}

- (BOOL)backButtonPressed {
    
    [self endGPSTesting];
    
  [self dismissViewControllerAnimated:YES
                           completion:^{
                           }];
  return YES;
}


- (void) uploadingMyGPSLocation:(CLLocation*)loc{
    
    if(_isUploading)
    {
        return;
    }
    _isUploading = YES;
    
    if(_uploadGPS == nil)
    {
        _uploadGPS = [[WebClient alloc] initWithDelegate:self];
    }
    
    _uploadGPS._httpMethod = @"POST";
    _uploadGPS._method = @"/map!subLocation";
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    [params setObject:self._myUid forKey:@"userid"];
    
    if(loc == nil)
    {
        [params setObject:@"90" forKey:@"latitude"];
        [params setObject:@"180" forKey:@"longitude"];
    }
    else
    {
        [params setObject:[NSString stringWithFormat:@"%0.8f", loc.coordinate.latitude] forKey:@"latitude"];
        [params setObject:[NSString stringWithFormat:@"%0.8f", loc.coordinate.longitude] forKey:@"longitude"];
    }
    
    _uploadGPS._requestParam = params;
    
    //IMP_BLOCK_SELF(WSUser);
    
    [_uploadGPS requestWithSusessBlock:^(id lParam, id rParam) {
        
    
        _isUploading = NO;
        
    } FailBlock:^(id lParam, id rParam) {
        
        
        _isUploading = NO;
    }];

    
}



- (void)locationManager:(CLLocationManager *)manager
     didUpdateLocations:(NSArray *)locations{
    
    
    
    if([locations count])
    {
        CLLocation*newLocation = [locations objectAtIndex:0];
        
        
        
        if(self.isFristTimeToLoad)
        {
            [self uploadingMyGPSLocation:newLocation];
        }
        
//        NSString *lat = [NSString stringWithFormat:@"%0.5f", newLocation.coordinate.latitude];
//        NSString *lng = [NSString stringWithFormat:@"%0.5f", newLocation.coordinate.longitude];
//        self.title = [NSString stringWithFormat:@"%@ - %@  %d", lat, lng, -1 ];
//       
        
        if([locations count] >  1)
        {
            CLLocation *oldLocation = [locations objectAtIndex:1];
            
            int span = ([newLocation.timestamp timeIntervalSinceDate:oldLocation.timestamp]);
            
//            NSString *lat = [NSString stringWithFormat:@"%0.5f", newLocation.coordinate.latitude];
//            NSString *lng = [NSString stringWithFormat:@"%0.5f", newLocation.coordinate.longitude];
//            self.title = [NSString stringWithFormat:@"%@ - %@  %d", lat, lng, span];
//       
            
            if(span >= 30)
            {
                [self uploadingMyGPSLocation:newLocation];
            }
        }
        else
        {
            [self uploadingMyGPSLocation:newLocation];
        }
        [self onReceiveLocation:newLocation fromUserId:_myUid];

    }
    
    
    
}
//协议中的方法，作用是每当位置发生更新时会调用的委托方法
-(void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{

    if(self.isFristTimeToLoad)
    {
        [self uploadingMyGPSLocation:newLocation];
    }
    
    int span = ([newLocation.timestamp timeIntervalSinceDate:oldLocation.timestamp]);
    if(span >= 30)
    {
        [self uploadingMyGPSLocation:newLocation];
    }
    
    
    [self onReceiveLocation:newLocation fromUserId:_myUid];
}

- (void)locationManager:(CLLocationManager *)manager
       didUpdateHeading:(CLHeading *)newHeading{
    
    
    RCAnnotation *annotaton = [self.userAnnotationDic objectForKey:_myUid];
    if (annotaton)
    {
        
    //__weak typeof(&*self) __weakself = self;
        
    dispatch_async(dispatch_get_main_queue(), ^{
            [annotaton updateHeading:newHeading.magneticHeading];
        });
        
    }
}

//当位置获取或更新失败会调用的方法
-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    
    NSString *errorMsg = nil;
    if ([error code] == kCLErrorDenied) {
        errorMsg = @"访问被拒绝";
        
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil
                                                            message:errorMsg
                                                           delegate:nil
                                                  cancelButtonTitle:@"OK"
                                                  otherButtonTitles:nil, nil];
        [alertView show];
    }
    

    
    
    //[_locationManager stopUpdatingLocation];
    
    
    //
//    CLLocation *location = [[CLLocation alloc] initWithLatitude:39.994475 longitude:116.413181];
//    User *u = [UserDefaultsKV getUser];
//    [self onReceiveLocation:location fromUserId:u._userId];
//
//    [self uploadingMyGPSLocation:location];
    //
    
  
}



#pragma mark - RCRealTimeLocationObserver
- (void)onRealTimeLocationStatusChange:(RCRealTimeLocationStatus)status {
 
}
- (void)onReceiveLocation:(CLLocation *)location fromUserId:(NSString *)userId {
  __weak typeof(&*self) __weakself = self;
  if (self.isFristTimeToLoad) {
    if (-90.0f <= location.coordinate.latitude &&
        location.coordinate.latitude <= 90.0f &&
        -180.0f <= location.coordinate.longitude &&
        location.coordinate.longitude <= 180.0f) {
      CLLocationCoordinate2D center;
      center.latitude = location.coordinate.latitude;
      center.longitude = location.coordinate.longitude;
      MKCoordinateSpan span;
      span.latitudeDelta = 0.1;
      span.longitudeDelta = 0.1;
      MKCoordinateRegion region = {center, span};
      self.theSpan = span;
      self.theRegion = region;
      [self.mapView setCenterCoordinate:center animated:YES];
      [self.mapView setRegion:self.theRegion];
    }
  }
  self.isFristTimeToLoad = NO;

    
  RCAnnotation *annotaton = [self.userAnnotationDic objectForKey:userId];
  if (annotaton == nil) {
    RCLocationView *annotatonView = [[RCLocationView alloc] init];
    annotatonView.userId = userId;
    annotatonView.coordinate = [RCLocationConvert wgs84ToGcj02:location.coordinate];
    RCAnnotation *ann = [[RCAnnotation alloc] initWithThumbnail:annotatonView];
    [self.mapView addAnnotation:ann];
    [self.userAnnotationDic setObject:ann forKey:userId];

      
      AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
      [app getUserInfoWithUserId:userId completion:^(RCUserInfo *userInfo) {
          
          if(userInfo)
          {
              
              dispatch_async(dispatch_get_main_queue(), ^{
                  RCAnnotation *annotaton = [__weakself.userAnnotationDic
                                             objectForKey:userInfo.userId];
                  annotatonView.isMyLocation = NO;
                  if ([userId
                       isEqualToString:[RCIM sharedRCIM]
                       .currentUserInfo.userId]) {
                      annotatonView.isMyLocation = YES;
                  }
                  annotaton.thumbnail.imageurl = userInfo.portraitUri;
                  [annotaton updateThumbnail:annotaton.thumbnail
                                    animated:YES];
              });
              
          }
          
      }];


      
      annotatonView.tapBlock = ^()
      {
          [__weakself accessPOI:userId];
      };
      
  }
  else
  {
      dispatch_async(dispatch_get_main_queue(), ^{
      annotaton.coordinate = [RCLocationConvert wgs84ToGcj02:location.coordinate];
      annotaton.thumbnail.coordinate =
          [RCLocationConvert wgs84ToGcj02:location.coordinate];
      annotaton.thumbnail.isMyLocation = NO;
      if ([userId isEqualToString:[RCIM sharedRCIM].currentUserInfo.userId]) {
        annotaton.thumbnail.isMyLocation = YES;
      }
      // annotaton.thumbnail.imageurl=userInfo.portraitUri;
      //[__weakself.mapView removeAnnotation:annotaton];
      [__weakself.mapView addAnnotation:annotaton];
      [annotaton updateThumbnail:annotaton.thumbnail animated:YES];
    });
    
  }
}

- (void)onParticipantsJoin:(WSUser *)userInfo {
    
    NSString *userId = [NSString stringWithFormat:@"%d", userInfo.userId];
  RCAnnotation *annotaton = [self.userAnnotationDic objectForKey:userId];
  __weak typeof(&*self) __weakself = self;
  if (annotaton == nil) {
      
      RCLocationView *annotatonView = [[RCLocationView alloc] init];
      annotatonView.userId = userId;
      RCAnnotation *ann = [[RCAnnotation alloc] initWithThumbnail:annotatonView];
      annotatonView.coordinate = [RCLocationConvert wgs84ToGcj02:CLLocationCoordinate2DMake(userInfo._latitude, userInfo._longitude)];
      annotatonView.isMyLocation = NO;
      if ([userId isEqualToString:[RCIM sharedRCIM].currentUserInfo.userId]) {
          annotatonView.isMyLocation = YES;
      }
      [self.mapView addAnnotation:ann];
      [self.userAnnotationDic setObject:ann forKey:userId];
      
      dispatch_async(dispatch_get_main_queue(), ^{
          RCAnnotation *annotaton = [__weakself.userAnnotationDic
                                     objectForKey:userId];
          annotaton.thumbnail.imageurl = userInfo.avatarurl;
          [annotaton updateThumbnail:annotaton.thumbnail
                            animated:YES];
          
      });
      
      
      
      annotatonView.tapBlock = ^()
      {
          [__weakself accessPOI:userId];
      };
    
  }
  else{
      dispatch_async(dispatch_get_main_queue(), ^{
          annotaton.coordinate = [RCLocationConvert wgs84ToGcj02:CLLocationCoordinate2DMake(userInfo._latitude, userInfo._longitude)];
          annotaton.thumbnail.coordinate =
          [RCLocationConvert wgs84ToGcj02:CLLocationCoordinate2DMake(userInfo._latitude, userInfo._longitude)];
          annotaton.thumbnail.isMyLocation = NO;
          if ([userId isEqualToString:[RCIM sharedRCIM].currentUserInfo.userId]) {
              annotaton.thumbnail.isMyLocation = YES;
          }
          // annotaton.thumbnail.imageurl=userInfo.portraitUri;
          //[__weakself.mapView removeAnnotation:annotaton];
          [__weakself.mapView addAnnotation:annotaton];
          [annotaton updateThumbnail:annotaton.thumbnail animated:YES];
      });
  }

  if (self.headCollectionView) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [__weakself.headCollectionView participantJoin:userInfo];
        
        [__weakself updateTitle];
    });
  }
    
    
}

- (void)onParticipantsQuit:(WSUser *)userInfo {
    
    NSString *userId = [NSString stringWithFormat:@"%d", userInfo.userId];
    
    if ([userId isEqualToString:[RCIM sharedRCIM].currentUserInfo.userId]) {
       
        return;
    }
  __weak typeof(&*self) __weakself = self;
  if (self.headCollectionView) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [__weakself.headCollectionView participantQuit:userInfo];
        
        [__weakself updateTitle];
    });
  }

  RCAnnotation *annotaton = [self.userAnnotationDic objectForKey:userId];
  if (annotaton) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [__weakself.userAnnotationDic removeObjectForKey:userId];
      [__weakself.mapView removeAnnotation:annotaton];
    });
  }
    
}


- (void) accessPOI:(NSString*)userid{
    
    NSString* uid =  userid;
    id useridKey = [NSNumber numberWithInt:[uid intValue]];
    
    WSUser *uu = [_mapUsers objectForKey:useridKey];
    if(uu)
    {
        [self showUserListView:uu];
    }
}

- (void) updateTitle{
    
    NSArray *users = [self.headCollectionView getParticipantsUserInfo];
    
    int max = (int)[users count];
    if(max > 3)
        max = 3;
    
    NSString *title = @"";
    for(int i = 0; i < max; i++)
    {
        WSUser *uu = [users objectAtIndex:i];
        if([title length] == 0)
        {
            title = uu.fullname;
        }
        else
        {
            title = [NSString stringWithFormat:@"%@ %@", title, uu.fullname];
        }
    }
    
    if([users count] > 3)
    {
        title = [NSString stringWithFormat:@"%@等(%d人)",title, (int)[users count]];
    }
    else
    {
        title = [NSString stringWithFormat:@"%@(%d人)",title, (int)[users count]];
    }
    
    self.title = title;
    
}

- (void)onFailUpdateLocation:(NSString *)description {
  dispatch_async(dispatch_get_main_queue(), ^{
    //[hud hide:YES];
  });
}

//选择用户时以用户坐标为中心
- (void)onSelectUserLocationWithUserId:(NSString *)userId {
  __weak typeof(&*self) __weakself = self;
  RCAnnotation *annotaton = [self.userAnnotationDic objectForKey:userId];
  if (annotaton) {
    dispatch_async(dispatch_get_main_queue(), ^{
//      [__weakself.mapView removeAnnotation:annotaton];
//      [__weakself.mapView addAnnotation:annotaton];
      [__weakself.mapView setCenterCoordinate:annotaton.coordinate
                                     animated:YES];
      [__weakself.mapView selectAnnotation:annotaton animated:YES];

    });
  }
}

#pragma mark - MKMapViewDelegate

- (void)mapView:(MKMapView *)mapView
    didSelectAnnotationView:(MKAnnotationView *)view {
  if ([view conformsToProtocol:@protocol(RCAnnotationViewProtocol)]) {
      
    [((NSObject<RCAnnotationViewProtocol> *)view)
        didSelectAnnotationViewInMap:mapView];
      
      
  }
}

- (void)mapView:(MKMapView *)mapView
    didDeselectAnnotationView:(MKAnnotationView *)view {
  if ([view conformsToProtocol:@protocol(RCAnnotationViewProtocol)]) {
    [((NSObject<RCAnnotationViewProtocol> *)view)
        didDeselectAnnotationViewInMap:mapView];
  }
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView
            viewForAnnotation:(id<MKAnnotation>)annotation {
  if ([annotation conformsToProtocol:@protocol(RCAnnotationProtocol)]) {
    MKAnnotationView *view = [((NSObject<RCAnnotationProtocol> *)annotation)
        annotationViewInMap:mapView];
    return view;
  }
  return nil;
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated {
  self.theRegion = mapView.region;
}
- (void)actionSheet:(UIActionSheet *)actionSheet
    clickedButtonAtIndex:(NSInteger)buttonIndex {
  switch (buttonIndex) {
  case 0: {
    //__weak typeof(&*self) __weakself = self;
      
      [self endGPSTesting];
    [self dismissViewControllerAnimated:YES
                             completion:^{
//                               [__weakself.realTimeLocationProxy
//                                       quitRealTimeLocation];
                             }];

  } break;
  }
}

- (void)willPresentActionSheet:(UIActionSheet *)actionSheet {
  SEL selector = NSSelectorFromString(@"_alertController");

  if ([actionSheet respondsToSelector:selector]) {
    UIAlertController *alertController =
        [actionSheet valueForKey:@"_alertController"];
    if ([alertController isKindOfClass:[UIAlertController class]]) {
      alertController.view.tintColor = [UIColor colorWithWhite:0 alpha:0.6];
    }
  } else {
    for (UIView *subView in actionSheet.subviews) {
      if ([subView isKindOfClass:[UIButton class]]) {
        UIButton *btn = (UIButton *)subView;
          if ([btn.titleLabel.text isEqualToString:@"结束"]) {
              [btn setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
          } else {
              [btn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
          }
      }
    }
  }
}


- (void) startGpsUpdage{
    
    if(_gpsUpdate == nil)
    {
        _gpsUpdate = [[WebClient alloc] initWithDelegate:self];
    }
    
    _gpsUpdate._httpMethod = @"GET";
    _gpsUpdate._method = @"/map!getLocation";
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
    [params setObject:self._myUid forKey:@"userid"];
    
    if([_targetObj isKindOfClass:[WSUser class]])
    {
        [params setObject:[NSString stringWithFormat:@"%d", ((WSUser*)_targetObj).userId] forKey:@"targetid"];
        [params setObject:@"2" forKey:@"type"];
    }
    else if([_targetObj isKindOfClass:[WSGroup class]])
    {
        [params setObject:((WSGroup*)_targetObj).groupId forKey:@"targetid"];
        [params setObject:@"1" forKey:@"type"];
    }
    
    _gpsUpdate._requestParam = params;
    
    IMP_BLOCK_SELF(RealTimeLocationViewController);
    
    [_gpsUpdate requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    NSArray *membs = [v objectForKey:@"text"];
                    
                    if([membs isKindOfClass:[NSArray class]] && [membs count])
                    {
                       
                        [block_self notifyGPSUpdate:membs];
                    }
                    
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
        
        
        
    }];
    
}

- (void) notifyGPSUpdate:(NSArray*)membs{
    
    if(_timer && [_timer isValid])
    {
        [_timer invalidate];
        _timer = nil;
    }
    
    
    for(NSDictionary *uInfo in membs)
    {
        id userId = [NSNumber numberWithInt:[[uInfo objectForKey:@"userID"] intValue]];
        float latitude = [[uInfo objectForKey:@"latitude"] floatValue];
        float longtitude = [[uInfo objectForKey:@"longtitude"] floatValue];
        
        WSUser *uu = [_mapUsers objectForKey:userId];
        if(uu)
        {
            uu._latitude = latitude;
            uu._longitude = longtitude;
            
            if(latitude >= 90 && longtitude >= 180)
            {
                [self onParticipantsQuit:uu];
            }
            else
            {
                [self onParticipantsJoin:uu];
            }
        }
        
        
    }
    
    _timer = [NSTimer scheduledTimerWithTimeInterval:30
                                              target:self
                                            selector:@selector(startGpsUpdage)
                                            userInfo:nil
                                             repeats:NO];
    
    
}

- (void) stopGpsUpdate{
    
    if(_timer && [_timer isValid])
    {
        [_timer invalidate];
        _timer = nil;
    }
}


- (void)dealloc {
  //  [self.realTimeLocationProxy removeRealTimeLocationObserver:self];
  NSLog(@"dealloc");
}

@end
