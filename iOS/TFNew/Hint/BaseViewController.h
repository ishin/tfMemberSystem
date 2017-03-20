//
//  BaseViewController.h
//  hkeeping
//
//  Created by apple on 2/18/14.
//  Copyright (c) 2014 apple. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WebClient.h"

@interface BaseViewController : UIViewController
{
    WebClient *_http;
}
@end
