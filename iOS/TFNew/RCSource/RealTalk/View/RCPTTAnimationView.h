//
//  RCPTTAnimationView.h
//  RongPTTKit
//
//  Created by Sin on 17/1/9.
//  Copyright © 2017年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RCPTTAnimationView : UIView
+ (instancetype)animationViewWithFrame:(CGRect)frame;

@property (nonatomic,assign) CGFloat borderWidth;
@property (nonatomic,strong) UIColor *tintColor;
@property (nonatomic,assign,readonly) BOOL isPlaying;

- (void)startAnimation;
- (void)stopAnimation;
@end
