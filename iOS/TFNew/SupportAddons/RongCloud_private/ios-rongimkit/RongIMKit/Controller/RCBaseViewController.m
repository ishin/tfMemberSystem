//
//  RCBaseViewController.m
//  RongIMKit
//
//  Created by xugang on 15/1/22.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCBaseViewController.h"

@interface RCBaseViewController ()

@property(nonatomic,strong)UIPercentDrivenInteractiveTransition *interactiveTransition;
@end

@implementation RCBaseViewController

- (void)loadView {
  CGRect bounds = [[UIScreen mainScreen] bounds];
  self.view = [[UIView alloc] initWithFrame:bounds];
}

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning];
  // Dispose of any resources that can be recreated.
}

@end
