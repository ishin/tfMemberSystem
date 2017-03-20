//
//  WaitDialog.m
//
//
//  Created by steven on 4/8/09.
//  Copyright 2009 steven. All rights reserved.
//

#import "WaitDialog.h"
#import "AppDelegate.h"
#import "UILabel+ContentSize.h"
//#import "FlipImagesAppDelegate.h"

#define LABEL_TAG 130
#define INDICATOR_TAG 271


static WaitDialog *_sharedWaitDialog;
static WaitDialog *_sharedAlertWaitDialog;

@interface WaitDialog ()
{
    UILabel *_label;
    UIActivityIndicatorView *_indicator;
    
    UIImageView *_grayBk;
}

@end

@implementation WaitDialog


+ (WaitDialog *)sharedDialog
{
    if (!_sharedWaitDialog) {
        _sharedWaitDialog = [[WaitDialog alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    }
    return _sharedWaitDialog;
}

+ (WaitDialog *)sharedAlertDialog
{
    if (!_sharedAlertWaitDialog) {
        _sharedAlertWaitDialog = [[WaitDialog alloc] initWithAlertFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    }
    return _sharedAlertWaitDialog;
}

- (id) initWithFrame:(CGRect)frame{
    if(self = [super initWithFrame:frame]){
        
        
        self.backgroundColor = RGBA(0, 0, 0, 0.35);
        
        UIImageView *bk = [[UIImageView alloc] init];
        [self addSubview:bk];
        bk.layer.cornerRadius = 5;
        bk.clipsToBounds = YES;
        bk.backgroundColor = [UIColor whiteColor];
        bk.frame = CGRectMake(0, 0, SCREEN_WIDTH*0.7, 100);
        bk.center = self.center;
        
        
        // create indicator
        _indicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0, 10, 20, 20)];
        _indicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        _indicator.hidesWhenStopped = YES;
        [bk addSubview:_indicator];
        _indicator.center = CGPointMake(CGRectGetWidth(bk.frame)/2, 30);
        
        
        _label = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_indicator.frame)+15, CGRectGetWidth(bk.frame)-20, 20)];
        _label.backgroundColor = [UIColor clearColor];
        _label.textColor = THEME_RED_COLOR;
        _label.font = [UIFont boldSystemFontOfSize:15];
        _label.textAlignment = NSTextAlignmentCenter;
        //label.baselineAdjustment = UIBaselineAdjustmentAlignCenters;
        _label.tag = LABEL_TAG;
        _label.text = @"Loading";
        [bk addSubview:_label];
        
        
        return self;
    }
    return nil;
}


- (id) initWithAlertFrame:(CGRect)frame{
    if(self = [super initWithFrame:frame]){
        
        
        self.backgroundColor = [UIColor clearColor];
        
        _grayBk = [[UIImageView alloc] init];
        [self addSubview:_grayBk];
        _grayBk.layer.cornerRadius = 5;
        _grayBk.clipsToBounds = YES;
        _grayBk.backgroundColor = RGBA(0, 0, 0, 0.3);
        _grayBk.frame = CGRectMake(0, 0, SCREEN_WIDTH*0.7, 30);
        _grayBk.center = self.center;
        
        
        _label = [[UILabel alloc] initWithFrame:CGRectMake(10, 8, CGRectGetWidth(_grayBk.frame)-20, 20)];
        _label.backgroundColor = [UIColor clearColor];
        _label.textColor = [UIColor redColor];
        _label.font = [UIFont systemFontOfSize:13];
        _label.textAlignment = NSTextAlignmentCenter;
        _label.tag = LABEL_TAG;
        _label.text = @"Loading";
        [_grayBk addSubview:_label];
        
        
        return self;
    }
    return nil;
}


- (void) updateSpin{
    
    UIView *v = [self viewWithTag:INDICATOR_TAG];
    v.center = self.center;
}

- (void) dealloc {
    
}



- (void) setTitle:(NSString *)title {
    
    _label.text = title;
}

#pragma mark Actions for wait dialog
- (void) startLoading {
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app.window addSubview:self];
    
    self.alpha = 0.0;
    
    [UIView animateWithDuration:0.35
                     animations:^{
                         self.alpha = 1.0;
                     } completion:^(BOOL finished) {
                         
                     }];
    
    [_indicator startAnimating];
    
    
}

- (void) endLoading {
    
    [_indicator stopAnimating];
    
    [UIView animateWithDuration:0.35
                     animations:^{
                         self.alpha = 0.0;
                     } completion:^(BOOL finished) {
                         [self removeFromSuperview];
                     }];
    
}

- (void) animateShow{
    
    _label.frame = CGRectMake(10, 8, CGRectGetWidth(_grayBk.frame)-20, 20);
    [_label contentSize];
    
    int h = CGRectGetHeight(_label.frame)+16;
    _grayBk.frame = CGRectMake(0, 0, SCREEN_WIDTH*0.7, h);
    _grayBk.center = self.center;
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app.window addSubview:self];
    
    self.alpha = 0.0;
    
    
    [UIView animateWithDuration:0.5
                     animations:^{
                         self.alpha = 1.0;
                     } completion:^(BOOL finished) {
                         
                         [self endShowing];
                     }];
}

- (void) endShowing {
    
    [UIView animateKeyframesWithDuration:0.35
                                   delay:1
                                 options:UIViewKeyframeAnimationOptionCalculationModeLinear
                              animations:^{
                                  self.alpha = 0.0;
                              } completion:^(BOOL finished) {
                                  
                                  [self removeFromSuperview];
                                  
                              }];
}


@end