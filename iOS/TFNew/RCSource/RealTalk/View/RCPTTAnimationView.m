//
//  RCPTTAnimationView.m
//  RongPTTKit
//
//  Created by Sin on 17/1/9.
//  Copyright © 2017年 RongCloud. All rights reserved.
//

#import "RCPTTAnimationView.h"

@interface RCPTTAnimationView ()
@property (nonatomic,strong) UIBezierPath *bezierPath;
@property (nonatomic,strong) CAShapeLayer *shapeLayer;
@property (nonatomic,strong) CAGradientLayer *gradientLayer;
@property (nonatomic,strong) CALayer *animationLayer;
@property (nonatomic,assign) BOOL isPlaying;
@end

@implementation RCPTTAnimationView
+ (instancetype)animationViewWithFrame:(CGRect)frame {
  return [[self alloc] initWithFrame:frame];
}

- (instancetype)initWithFrame:(CGRect)frame {
  CGSize size = frame.size;
  CGFloat maxValue = size.width > size.height ?size.width:size.height;
  size = CGSizeMake(maxValue, maxValue);
  frame.size = size;
  self = [super initWithFrame:frame];
  if (self) {
    self.tintColor = [UIColor redColor];
    self.borderWidth = 2.0f;
  }
  return self;
}

- (void)startAnimation {
  self.isPlaying = YES;
  
  [self.layer addSublayer:self.animationLayer];
  //创建一个圆环
  UIBezierPath *bezierPath = self.bezierPath;
  
  //圆环遮罩
  CAShapeLayer *shapeLayer = self.shapeLayer;
  shapeLayer.path = bezierPath.CGPath;
  
  //颜色渐变
  NSMutableArray *colors = [NSMutableArray arrayWithObjects:(id)self.tintColor.CGColor, nil];
  CAGradientLayer *gradientLayer = self.gradientLayer;
  [gradientLayer setColors:[NSArray arrayWithArray:colors]];
  [self.animationLayer addSublayer:gradientLayer]; //设置颜色渐变
  [self.animationLayer setMask:shapeLayer]; //设置圆环遮罩
  
  CABasicAnimation *rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
  rotationAnimation.fromValue = [NSNumber numberWithFloat:0];
  rotationAnimation.toValue = [NSNumber numberWithFloat:6.0*M_PI];
  rotationAnimation.repeatCount = MAXFLOAT;
  rotationAnimation.duration = 2;
  
  
  //组合动画
  CAAnimationGroup *groupAnnimation = [CAAnimationGroup animation];
  groupAnnimation.duration = 4;
  groupAnnimation.animations = @[ rotationAnimation];
  groupAnnimation.repeatCount = MAXFLOAT;
  [self.animationLayer addAnimation:groupAnnimation forKey:@"groupAnnimation"];
}
- (void)stopAnimation {
  self.isPlaying = NO;
  [self.animationLayer removeAllAnimations];
  [self.animationLayer removeFromSuperlayer];
}

//lazy load

- (CALayer *)animationLayer {
  if(!_animationLayer){
    _animationLayer = [CALayer layer];
    _animationLayer.backgroundColor = self.tintColor.CGColor; //圆环底色
    _animationLayer.frame = self.bounds;
  }
  return _animationLayer;
}

- (UIBezierPath *)bezierPath {
  if(!_bezierPath){
    CGFloat width = self.bounds.size.width;
    _bezierPath = [UIBezierPath bezierPathWithArcCenter:CGPointMake(width/2, width/2) radius:width/2-self.borderWidth startAngle:0 endAngle:M_PI clockwise:YES];
  }
  return _bezierPath;
}

- (CAShapeLayer *)shapeLayer {
  if(!_shapeLayer){
    _shapeLayer = [CAShapeLayer layer];
    _shapeLayer.fillColor = [UIColor clearColor].CGColor;
    _shapeLayer.strokeColor = self.tintColor.CGColor;
    _shapeLayer.lineWidth = 5;
    _shapeLayer.strokeStart = 0;
    _shapeLayer.strokeEnd = 0.8;
    _shapeLayer.lineCap = @"round";
    _shapeLayer.lineDashPhase = 0.8;
  }
  return _shapeLayer;
}

- (CAGradientLayer *)gradientLayer {
  if(!_gradientLayer){
    _gradientLayer = [CAGradientLayer layer];
    _gradientLayer.shadowPath = self.bezierPath.CGPath;
    _gradientLayer.frame = CGRectMake(50, 50, 60, 60);
    _gradientLayer.startPoint = CGPointMake(0, 1);
    _gradientLayer.endPoint = CGPointMake(1, 0);
  }
  return _gradientLayer;
}

@end
