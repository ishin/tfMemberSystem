//
//  BaseViewController.m
//  hkeeping
//
//  Created by apple on 2/18/14.
//  Copyright (c) 2014 apple. All rights reserved.
//

#import "BaseViewController.h"

@interface BaseViewController ()

@end

@implementation BaseViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}



- (void) viewWillAppear:(BOOL)animated
{
    if ([[[UIDevice currentDevice] systemVersion] compare:@"7.0" options:NSNumericSearch] != NSOrderedAscending)
    {
        if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
            self.navigationController.interactivePopGestureRecognizer.delegate = nil;
        }
    }
}


- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    if(IOS7_OR_LATER){
        self.edgesForExtendedLayout = UIRectEdgeNone;
    }
    
//    [[UINavigationBar appearance] setTintColor:[UIColor whiteColor]];
//    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
//    
    self.view.backgroundColor = [UIColor whiteColor];//RGB(229, 233, 247);
    //RGB(0xf2, 0xf2, 0xf2)
    
    _http = [[WebClient alloc] initWithDelegate:self];
    
}

- (void) dealloc
{
    _http._successBlock = nil;
    _http._failBlock = nil;

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
