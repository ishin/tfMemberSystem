//
//  ActivityDetailViewController.m
//  ws
//
//  Created by jack on 10/28/14.
//  Copyright (c) 2014 jack. All rights reserved.
//

#import "ActivityDetailViewController.h"
#import "UIImageView+WebCache.h"
#import "UIButton+Color.h"
#import "WSEvent.h"
#import "UILabel+ContentSize.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"


@interface ActivityDetailViewController ()
{
    UILabel* join;
    UIButton *btnJoin;
    
    UIImageView *_speakersView;
}
@end

@implementation ActivityDetailViewController
@synthesize _activity;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void) backAction:(id)sender{
    
    [self.navigationController popViewControllerAnimated:YES];
    
}

- (void) viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBarHidden = YES;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"活动详细";
    

    _content = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    [self.view addSubview:_content];
   
    
    UIButton *back_Btn = [UIButton buttonWithType:UIButtonTypeCustom];
	[back_Btn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
	//[back_Btn addTarget:target action:@selector(backAction) forControlEvents:UIControlEventTouchDown];
	back_Btn.frame = CGRectMake(0, 20, 50, 40);
    [back_Btn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:back_Btn];

    
    
    
    UIImageView *cellImage = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 200)];
    [_content addSubview:cellImage];
    cellImage.tag = 101010;
    cellImage.layer.contentsGravity = kCAGravityResizeAspectFill;
    cellImage.clipsToBounds = YES;
    [cellImage setImageWithURL:[NSURL URLWithString:_activity.coverurl]];
    
    UIView *mask = [[UIView alloc] initWithFrame:cellImage.bounds];
    [cellImage addSubview:mask];
    mask.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.4];
    
    UIImageView *cellIcon = [[UIImageView alloc] initWithFrame:CGRectMake((SCREEN_WIDTH-70)/2, 30, 70, 70)];
    [_content addSubview:cellIcon];
    cellIcon.layer.contentsGravity = kCAGravityResizeAspectFill;
    cellIcon.clipsToBounds = YES;
    cellIcon.layer.cornerRadius = 8;
    cellIcon.layer.borderColor = [UIColor whiteColor].CGColor;
    cellIcon.layer.borderWidth = 1;
    
    
    [cellIcon setImageWithURL:[NSURL URLWithString:_activity.wsUser.avatarurl]];//
    
    UILabel* titleL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                CGRectGetMaxY(cellIcon.frame)+10,
                                                                SCREEN_WIDTH-20, 40)];
    titleL.backgroundColor = [UIColor clearColor];
    [cellImage addSubview:titleL];
    titleL.font = [UIFont systemFontOfSize:15];
    titleL.textColor  = [UIColor whiteColor];
    titleL.text = _activity.title;
    titleL.textAlignment = NSTextAlignmentCenter;
    titleL.numberOfLines = 2;
    
    
    NSString *dayday = @"";
    if([_activity.openMonthDay isEqualToString:_activity.closeMonthDay])
    {
        dayday = _activity.openMonthDay;
        dayday = [NSString stringWithFormat:@"%@ %@\n%@ - %@",dayday,
                  _activity.openWeek,
                  _activity.openHour,
                  _activity.closeHour];
    }
    else
    {
        dayday = [NSString stringWithFormat:@"%@ %@ - %@ %@",_activity.openMonthDay, _activity.openWeek,
                  _activity.closeMonthDay, _activity.closeWeek];
    }
    
    
    UILabel* startTime = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                   CGRectGetMaxY(titleL.frame),
                                                                   SCREEN_WIDTH-20, 40)];
    startTime.backgroundColor = [UIColor clearColor];
    [cellImage addSubview:startTime];
    startTime.textAlignment = NSTextAlignmentCenter;
    startTime.font = [UIFont systemFontOfSize:12];
    startTime.textColor  = [UIColor whiteColor];
    startTime.text = dayday;
    startTime.numberOfLines = 2;
    
    
    UIImageView *bk = [[UIImageView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(cellImage.frame), SCREEN_WIDTH, 50)];
    bk.backgroundColor = [UIColor whiteColor];
    [_content addSubview:bk];
    bk.userInteractionEnabled = YES;
    

//    UILabel* line = [[UILabel alloc] initWithFrame:CGRectMake(5,
//                                                              5,
//                                                              CGRectGetWidth(bk.frame)-10, 1)];
//    line.backgroundColor = LINE_COLOR;
//    [bk addSubview:line];
    
    btnJoin = [UIButton buttonWithColor:nil selColor:LINE_COLOR];
    [bk addSubview:btnJoin];
    
    UILabel* price = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                              10,
                                                              SCREEN_WIDTH-20, 40)];
    price.backgroundColor = [UIColor clearColor];
    [bk addSubview:price];
    price.font = [UIFont systemFontOfSize:12];
    price.textColor  = [UIColor redColor];
    price.text = @"¥200";

    
    join = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                                10,
                                                                SCREEN_WIDTH, 40)];
    join.backgroundColor = [UIColor clearColor];
    [bk addSubview:join];
    join.font = [UIFont systemFontOfSize:15];
    join.textColor  = THEME_COLOR;
    
    join.textAlignment = NSTextAlignmentCenter;
    
    btnJoin.frame = join.frame;
    
    
    if(!_activity.hasparticipate)
    {
        [btnJoin addTarget:self action:@selector(applyActivity:) forControlEvents:UIControlEventTouchUpInside];
        join.text = @"我要参加";
    }
    else
    {
        join.text = @"已报名";
    }

    
    UILabel* line = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                     CGRectGetHeight(bk.frame)-1,
                                                     CGRectGetWidth(bk.frame), 1)];
    line.backgroundColor = LINE_COLOR;
    [bk addSubview:line];
    
    
    UIImageView *eventContent = [[UIImageView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(bk.frame)+10, SCREEN_WIDTH, 200)];
    eventContent.backgroundColor = [UIColor clearColor];
    [_content addSubview:eventContent];
    eventContent.userInteractionEnabled = YES;
    
    
    float fy = 10;
    UILabel *description = [[UILabel alloc] initWithFrame:CGRectMake(10, fy, SCREEN_WIDTH-20, 20)];
    description.backgroundColor = [UIColor clearColor];
    [eventContent addSubview:description];
    description.text = _activity.summary;
    description.textColor = COLOR_TEXT_A;
    description.font = [UIFont systemFontOfSize:13];
    description.numberOfLines = 0;
    [description contentSize];
    

    UIButton *btnSeeMore = [UIButton buttonWithColor:nil selColor:LINE_COLOR];
    [eventContent addSubview:btnSeeMore];
    
    UILabel* seeMore = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                              CGRectGetMaxY(description.frame),
                                                              SCREEN_WIDTH, 35)];
    seeMore.backgroundColor = [UIColor clearColor];
    [eventContent addSubview:seeMore];
    seeMore.font = [UIFont systemFontOfSize:15];
    seeMore.textColor  = THEME_COLOR;
    seeMore.text = @"More";
    seeMore.textAlignment = NSTextAlignmentCenter;
    
    btnSeeMore.frame = seeMore.frame;
    
    
    CGRect rc = eventContent.frame;
    rc.size.height = CGRectGetMaxY(btnSeeMore.frame);
    eventContent.frame = rc;
    
    
    UIImageView *addressBg = [[UIImageView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(eventContent.frame)+10, SCREEN_WIDTH, 60)];
    addressBg.backgroundColor = [UIColor whiteColor];
    [_content addSubview:addressBg];
    addressBg.userInteractionEnabled = YES;
    line = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                     0,
                                                     SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [addressBg addSubview:line];
    line = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                     CGRectGetHeight(addressBg.frame)-1,
                                                     SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [addressBg addSubview:line];
    
//    _mapView = [[LMapView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 150)];
//    [_mapView setDelegate:self];
//    _mapView.needShowAccessView = YES;
//    [bk2 addSubview:_mapView];
//    
//    if(_activity.gpsLat && _activity.gpsLng)
//    {
//        float lat = _activity.gpsLat;
//        float lng = _activity.gpsLng;
//        [_mapView MoveMapToLocation:lat withLng:lng];
//        
//        Marker *marker = [[Marker alloc] init];
//        marker.annImage = [UIImage imageNamed:@"map_poi_icon.png"];
//        marker.dataIndex = [NSString stringWithFormat:@"%d", _activity.wsId];
//        marker.latitude = lat;
//        marker.longitude = lng;
//        marker.title = _activity.address;
//        marker.pinAnColor = MKPinAnnotationColorRed;
//        
//        [_mapView AddMarkers:@[marker]];
//    }
//    
    UILabel* address = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                 10,
                                                                 SCREEN_WIDTH-20-40, 40)];
    address.backgroundColor = [UIColor clearColor];
    [addressBg addSubview:address];
    address.font = [UIFont systemFontOfSize:13];
    address.textColor  = COLOR_TEXT_A;
    address.text = _activity.address;
    address.numberOfLines = 2;
    
    UIImageView *mapIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"blue_map_icon.png"]];
    [addressBg addSubview:mapIcon];
    mapIcon.center = CGPointMake(SCREEN_WIDTH - 30, 30);
    
    _speakersView = [[UIImageView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(addressBg.frame)+30, SCREEN_WIDTH, 250)];
    _speakersView.backgroundColor = [UIColor whiteColor];
    [_content addSubview:_speakersView];
    _speakersView.userInteractionEnabled = YES;
    line = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                     0,
                                                     SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [_speakersView addSubview:line];
    
    
    
    [self layoutSpeakers];
    
    
    line = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                     CGRectGetHeight(_speakersView.frame)-1,
                                                     SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [_speakersView addSubview:line];

    
    line = [[UILabel alloc] initWithFrame:CGRectMake(5,
                                                     CGRectGetHeight(_speakersView.frame)-50,
                                                     SCREEN_WIDTH-10, 1)];
    line.backgroundColor = LINE_COLOR;
    [_speakersView addSubview:line];
    
    
    UIImageView *orgLogo = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"org_logo.png"]];
    orgLogo.backgroundColor = [UIColor whiteColor];
    [_speakersView addSubview:orgLogo];
    orgLogo.center = CGPointMake(SCREEN_WIDTH/2, 50);
    
    UIButton *btnContact = [UIButton buttonWithColor:nil selColor:LINE_COLOR];
    [_speakersView addSubview:btnContact];
    
    
    
    UILabel* contact = [[UILabel alloc] initWithFrame:CGRectMake(0,
                                                                 CGRectGetMaxY(line.frame),
                                                                 SCREEN_WIDTH, 50)];
    contact.backgroundColor = [UIColor clearColor];
    [_speakersView addSubview:contact];
    contact.font = [UIFont systemFontOfSize:15];
    contact.textColor  = THEME_COLOR;
    contact.text = @"联系主办方";
    contact.textAlignment = NSTextAlignmentCenter;
    
    btnContact.frame = contact.frame;
    
    _content.contentSize = CGSizeMake(SCREEN_WIDTH, CGRectGetMaxY(_speakersView.frame)+20);

}

- (void) layoutSpeakers{
    
    UILabel* tL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                            10,
                                                            SCREEN_WIDTH-20, 20)];
    tL.backgroundColor = [UIColor clearColor];
    [_speakersView addSubview:tL];
    tL.font = [UIFont systemFontOfSize:17];
    tL.textColor  = COLOR_TEXT_A;
    tL.text = @"Speaker";
    
    NSArray *speakers = @[@{},@{},@{}];
    
    int py = 40;
    for(NSDictionary *speaker in speakers)
    {
        UIImageView *avatar = [[UIImageView alloc] initWithFrame:CGRectMake(10, py, 40, 40)];
        [_speakersView addSubview:avatar];
        avatar.layer.contentsGravity = kCAGravityResizeAspectFill;
        avatar.clipsToBounds = YES;
        avatar.layer.cornerRadius = 2;
        [avatar setImageWithURL:[NSURL URLWithString:@"http://img1.gtimg.com/tech/pics/hv1/216/69/2022/131498361.jpg"]];
        
        UILabel* tName = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(avatar.frame)+10,
                                                                   CGRectGetMinY(avatar.frame),
                                                                   SCREEN_WIDTH-(CGRectGetMaxX(avatar.frame)+10), 20)];
        tName.backgroundColor = [UIColor clearColor];
        [_speakersView addSubview:tName];
        tName.font = [UIFont systemFontOfSize:15];
        tName.textColor  = COLOR_TEXT_A;
        tName.text = @"Mr. R";
        
        
        UILabel* tRank = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(avatar.frame)+10,
                                                                   CGRectGetMaxY(tName.frame),
                                                                   SCREEN_WIDTH-(CGRectGetMaxX(avatar.frame)+10), 20)];
        tRank.backgroundColor = [UIColor clearColor];
        [_speakersView addSubview:tRank];
        tRank.font = [UIFont systemFontOfSize:15];
        tRank.textColor  = COLOR_TEXT_A;
        tRank.text = @"CEO at HuaWei";
        
        py+=50;
    }
    
    
    
}

- (void) applyActivity:(id)sender{
    
    ///v1/event/participant
    
    User *u = [UserDefaultsKV getUser];
    
    NSString *token = nil;
    if(u)
    {
        token = u._authtoken;
    }
    
    
    if(_http == nil)
        _http = [[WebClient alloc] initWithDelegate:self];
    
    NSMutableDictionary *param = [NSMutableDictionary dictionary];
    
    if(token)
    {
        [param setObject:token forKey:@"token"];
    }
    
    [param setObject:[NSString stringWithFormat:@"%d", _activity.wsId] forKey:@"eventid"];
    
    if(_activity.wsUser.realname)
        [param setObject:_activity.wsUser.realname forKey:@"realname"];
    else
        [param setObject:@"" forKey:@"realname"];
    
    if(_activity.wsUser.cellphone)
        [param setObject:_activity.wsUser.cellphone forKey:@"cellphone"];
    else
        [param setObject:@"" forKey:@"cellphone"];
    
    //_http._method = API_APPLY_EVENT;
    _http._httpMethod = @"POST";
    _http._requestParam = param;
    
    
    IMP_BLOCK_SELF(ActivityDetailViewController);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        //[self stopRefreshing];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                
                NSString *result = [v objectForKey:@"code"];
                
                if([result intValue] == 0)
                {
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                                    message:@"已经成功报名！"
                                                                   delegate:nil
                                                          cancelButtonTitle:@"OK"
                                                          otherButtonTitles:nil, nil];
                    [alert show];
                    
                    [block_self changeJoinStatus];
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
        
       // [self stopRefreshing];
    }];
}

- (void) changeJoinStatus{
    
    join.text = @"已报名";
    btnJoin.enabled = NO;
}

#pragma mark --
#pragma mark MapView delegate
-(void)MapLoadFinishFromMapView
{
	//[self moveMapToCenter];
	
}
-(void)MapLoadFailedFromMapView
{
	
}

- (void)updateMyLocation:(float)longitude latitude:(float)latitude{
	
	
}
-(void)RetuenAnnotationCalloutAccessoryTapFromMapView:(LMapView*)lmapview withMarker:(Marker*)marker
{
	
	
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
