//
//  ActivityDetailViewController.h
//  ws
//
//  Created by jack on 10/28/14.
//  Copyright (c) 2014 jack. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LMapView.h"

@class WSEvent;

@interface ActivityDetailViewController : BaseViewController <MapDelegate>
{
    UIScrollView *_content;
    
    LMapView *_mapView;
}
@property (nonatomic, strong) WSEvent *_activity;
@end
