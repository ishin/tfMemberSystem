//
//  RCLocationViewController.m
//  iOS-IMKit
//
//  Created by YangZigang on 14/11/4.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//

#import "RCLocationViewController.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import <MapKit/MapKit.h>
#import "RCIM.h"

@interface RCLocationViewControllerAnnotation : NSObject <MKAnnotation>
@property(nonatomic, assign) CLLocationCoordinate2D coordinate;
@property(nonatomic, copy) NSString *title;
@property(nonatomic, strong) NSString *subTitle;

@end

@implementation RCLocationViewControllerAnnotation

- (instancetype)initWithLocation:(CLLocationCoordinate2D)location locationName:(NSString *)locationName {
    if (self = [super init]) {
        self.coordinate = location;
        self.title = locationName;
    }
    return self;
}

@end

@interface RCLocationViewController ()

@property(nonatomic, strong) MKMapView *mapView;
@property(nonatomic, strong) RCLocationViewControllerAnnotation *annotation;
/** 设置NavigationBar */
- (void)configureNavigationBar;
@end

@implementation RCLocationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.mapView = [[MKMapView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:self.mapView];
    self.annotation =
        [[RCLocationViewControllerAnnotation alloc] initWithLocation:self.location locationName:self.locationName];
    [self.mapView addAnnotation:self.annotation];
    self.mapView.delegate = self;
    if (!self.title) {
        self.navigationItem.title = self.title =
            NSLocalizedStringFromTable(@"LocationInformation", @"RongCloudKit", nil); //@"位置信息";
    }
    [self configureNavigationBar];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    MKCoordinateRegion coordinateRegion;
    coordinateRegion.center = self.location;
    coordinateRegion.span.latitudeDelta = 0.01;
    coordinateRegion.span.longitudeDelta = 0.01;
    if (-90.0f <= self.location.latitude && self.location.latitude <= 90.0f &&
        -180.0f <= self.location.longitude && self.location.longitude <= 180.0f)
    {
        [self.mapView setRegion:coordinateRegion animated:NO];
        [self.mapView selectAnnotation:self.annotation animated:YES];
    }else
    {
        NSLog(@"经纬度无效！！！");
    }
}

- (void)configureNavigationBar {

    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    backBtn.frame = CGRectMake(0, 6, 87, 23);
    UIImageView *backImg = [[UIImageView alloc]
                            initWithImage:IMAGE_BY_NAMED(@"navigator_btn_back")];
    backImg.frame = CGRectMake(-6, 4, 10, 17);
    [backBtn addSubview:backImg];
    UILabel *backText =
    [[UILabel alloc] initWithFrame:CGRectMake(9,4, 85, 17)];
    backText.text = NSLocalizedStringFromTable(@"Back", @"RongCloudKit", nil); // NSLocalizedStringFromTable(@"Back",
    // @"RongCloudKit", nil);
    //   backText.font = [UIFont systemFontOfSize:17];
    [backText setBackgroundColor:[UIColor clearColor]];
    [backText setTextColor:[RCIM sharedRCIM].globalNavigationBarTintColor];
    [backBtn addSubview:backText];
    [backBtn addTarget:self
                action:@selector(leftBarButtonItemPressed:)
      forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *leftButton = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    [self.navigationItem setLeftBarButtonItem:leftButton];

    //    UILabel* titleLab = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 100, 44)];
    //    titleLab.font = [UIFont systemFontOfSize:18];
    //    [titleLab setBackgroundColor:[UIColor clearColor]];
    //    titleLab.textColor = [UIColor whiteColor];
    //    titleLab.textAlignment = NSTextAlignmentCenter;
    //    titleLab.tag = 1000;
    //    self.navigationItem.titleView=titleLab;
    //    titleLab.text = self.title;
}

- (void)leftBarButtonItemPressed:(id)sender {
    //[self.navigationController popViewControllerAnimated:YES];
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark -
#pragma mark MKMapViewDelegate
- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation {
    static NSString *pinAnnotationIdentifier = @"PinAnnotationIdentifier";
    MKPinAnnotationView *pinAnnotationView =
        (MKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:pinAnnotationIdentifier];
    if (!pinAnnotationView) {
        pinAnnotationView =
            [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:pinAnnotationIdentifier];
        pinAnnotationView.pinColor = MKPinAnnotationColorGreen;
        pinAnnotationView.canShowCallout = YES;
    }
    return pinAnnotationView;
}
@end
