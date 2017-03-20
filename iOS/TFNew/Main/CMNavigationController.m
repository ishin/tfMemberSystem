//
//  CMNavigationController.m
//  CMTabBarController
//
//  Created by mac on 13-8-13.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import "CMNavigationController.h"
#import "UIImage+Color.h"


@implementation CMNavigationController

- (id)initWithRootViewController:(UIViewController *)rootViewController{
    if (self = [super initWithRootViewController:rootViewController]) {
        self.view.backgroundColor = [UIColor whiteColor];
        if ([UIDevice currentDevice].systemVersion.floatValue >= 7.0) {
            _backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style: UIBarButtonItemStyleBordered target: nil action: nil];
            _backBarButtonItem.title = @"";
            rootViewController.navigationItem.backBarButtonItem = _backBarButtonItem;
        }
        _captureArray = [[NSMutableArray alloc] initWithCapacity:0];
    }
    return self;
}


#pragma mark - view lifecycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //self.navigationBar.tintColor = [UIColor whiteColor];
    
    [self.navigationBar setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor blackColor],NSForegroundColorAttributeName,nil]];
    
    UIImage *themeImg = [UIImage imageNamed:@"navi_image.png"];
    if ([self.navigationBar respondsToSelector:@selector(setBackgroundImage:forBarMetrics:)]) {
        [self.navigationBar setBackgroundImage:themeImg forBarMetrics:UIBarMetricsDefault];
        
        if ([self.navigationBar respondsToSelector:@selector(setShadowImage:)]) {
            [self.navigationBar setShadowImage:[UIImage imageWithColor:[UIColor clearColor] andSize:CGSizeMake(1, 1)]];
        }
        
    }
    self.navigationBar.backgroundColor = [UIColor clearColor];
    
    

}

#pragma mark - overload method

- (void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated{
    //统一修改返回按钮
    
    //-代替上面-关闭统一返回效果
    if(self.viewControllers.count >= 1)
    {
        UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
        backBtn.frame = CGRectMake(0, 0, 25, 44);

        [backBtn addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
        UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
        
        if(IOS8_OR_LATER)
            viewController.navigationItem.leftBarButtonItem = backBarButtonItem;
        else
            viewController.navigationItem.leftBarButtonItem = backBarButtonItem;

        
    }
    //---
    
    
    
    [super pushViewController:viewController animated:animated];
}

// override the pop method
- (UIViewController *)popViewControllerAnimated:(BOOL)animated
{
    if ([UIDevice currentDevice].systemVersion.floatValue < 7.0) {
        [_captureArray removeLastObject];
    }
    
    return [super popViewControllerAnimated:animated];
}

- (NSArray *)popToRootViewControllerAnimated:(BOOL)animated{
    if ([UIDevice currentDevice].systemVersion.floatValue < 7.0) {
        if (_captureArray.count > 1) {
            [_captureArray removeObjectsInRange:NSMakeRange(1, _captureArray.count - 1)];
        }
    }
    return [super popToRootViewControllerAnimated:animated];
}

- (void)back{
    [self popViewControllerAnimated:YES];
}

#pragma mark - support method

- (void)HandlePaningGesture:(UIPanGestureRecognizer *)recognizer{
    
    if (self.viewControllers.count == 1) {
        return;
    }
    
    UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
    CGPoint touchPoint = [recognizer locationInView:keyWindow];
    CGPoint velocity = [recognizer velocityInView:keyWindow];
    
    static CGFloat orginalTouchPositionX = 0;
    
    if (!_backgroundView) {
        _backgroundView = [[UIView alloc] initWithFrame:keyWindow.bounds];
        
        _bottomImageView = [[UIImageView alloc] initWithFrame:_backgroundView.bounds];
        [_backgroundView addSubview:_bottomImageView];
        
        _topImageView = [[UIImageView alloc] initWithFrame:_backgroundView.bounds];
        [_backgroundView addSubview:_topImageView];
        
        UIImageView *shadowView = [[UIImageView alloc] initWithFrame:CGRectMake(- 9, 0, 9, XY_ORG_HEIGHT)];
        shadowView.image = ImageFromResource(@"nav_shadow_bg.png");
        [_topImageView addSubview:shadowView];
       
    }
    
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        orginalTouchPositionX = touchPoint.x;
        
        _topImageView.image = [self capture];
        _bottomImageView.image = [_captureArray lastObject];
        
        [keyWindow addSubview:_backgroundView];
        
        _topImageView.frame = _backgroundView.bounds;
        _bottomImageView.frame = CGRectMake(- 100.0, 0, _bottomImageView.frame.size.width, _bottomImageView.frame.size.height);
    }
    else if (recognizer.state == UIGestureRecognizerStateChanged){
        CGFloat currentTouchPositionX = touchPoint.x;
        CGFloat panAmount = currentTouchPositionX - orginalTouchPositionX;
        orginalTouchPositionX = currentTouchPositionX;
        
        CGFloat topImageViewOriginX = _topImageView.frame.origin.x + panAmount;
        CGFloat bottomImageViewOriginX = _bottomImageView.frame.origin.x + panAmount * 100.0/320.0;
        
        if (topImageViewOriginX <= 0) {
            topImageViewOriginX = 0;
            bottomImageViewOriginX = -100.0;
        }
        else if (topImageViewOriginX >= 320){
            topImageViewOriginX = 320;
            bottomImageViewOriginX = 0;
        }

        _topImageView.frame = CGRectMake(topImageViewOriginX,_topImageView.frame.origin.y, CGRectGetWidth(_topImageView.bounds), CGRectGetHeight(_topImageView.bounds));
        _bottomImageView.frame = CGRectMake(bottomImageViewOriginX, _bottomImageView.frame.origin.y, CGRectGetWidth(_bottomImageView.bounds), CGRectGetHeight(_bottomImageView.bounds));
    }
    else if (recognizer.state == UIGestureRecognizerStateEnded || recognizer.state == UIGestureRecognizerStateCancelled){
        if (_topImageView.frame.origin.x > 160  || velocity.x > 500) {
            [UIView animateWithDuration:0.25 animations:^{
                _topImageView.frame = CGRectMake(320,_topImageView.frame.origin.y, CGRectGetWidth(_topImageView.bounds), CGRectGetHeight(_topImageView.bounds));
                _bottomImageView.frame = CGRectMake(0, _bottomImageView.frame.origin.y, CGRectGetWidth(_bottomImageView.bounds), CGRectGetHeight(_bottomImageView.bounds));
            } completion:^(BOOL finished) {
                [_backgroundView removeFromSuperview];
                [self popViewControllerAnimated:NO];
            }];
        }
        else {
            [UIView animateWithDuration:0.25 animations:^{
                _topImageView.frame = CGRectMake(0,_topImageView.frame.origin.y, CGRectGetWidth(_topImageView.bounds), CGRectGetHeight(_topImageView.bounds));
                _bottomImageView.frame = CGRectMake(-100, _bottomImageView.frame.origin.y, CGRectGetWidth(_bottomImageView.bounds), CGRectGetHeight(_bottomImageView.bounds));
            } completion:^(BOOL finished) {
                [_backgroundView removeFromSuperview];
            }];
        }
        
        
        return;
    }
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    if ([gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]) {
        CGPoint translation = [(UIPanGestureRecognizer *)gestureRecognizer translationInView:self.view];
        UIWindow *keyWindow = [UIApplication sharedApplication].keyWindow;
        CGPoint touchPoint = [gestureRecognizer locationInView:keyWindow];
        if (fabs(translation.x) > fabs(translation.y) && touchPoint.x < 50) {
            return YES;
        }
        else {
            return NO;
        }
    }
    
    return YES;
}

- (UIImage *)capture
{
    UIWindow *topWindow = [UIApplication sharedApplication].keyWindow;
    CGRect rect = [UIScreen mainScreen].bounds;
    UIGraphicsBeginImageContextWithOptions(rect.size, NO, [UIScreen mainScreen].scale);
    
    if([[UIDevice currentDevice].systemVersion floatValue] >= 7.0)
    {
        [topWindow drawViewHierarchyInRect:rect afterScreenUpdates:YES];
    }
    else
    {
        CGContextRef ctx = UIGraphicsGetCurrentContext();
        [topWindow.layer renderInContext:ctx];
    }
    
    UIImage * img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return img;
}

@end
