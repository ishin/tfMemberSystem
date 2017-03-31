//
//  RCAnnotationView.m
//  LocationSharer
//
//  Created by 杜立召 on 15/7/27.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCAnnotationView.h"
#import "RCDUtilities.h"
#import "UIImageView+WebCache.h"

@implementation RCAnnotationView
float fromValue = 0.0f;
- (id)initWithAnnotation:(id<MKAnnotation>)annotation {
  self =
      [super initWithAnnotation:annotation reuseIdentifier:@"RCAnnotationView"];

  if (self) {
    self.canShowCallout = NO;
    self.frame = CGRectMake(0, 0, 40, 40+35);
    self.backgroundColor = [UIColor clearColor];
    self.centerOffset = CGPointMake(0, 0);
    _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 40, 40)];
    [_imageView
        setImageWithURL:[NSURL URLWithString:self.imageUrl]
          placeholderImage:[RCDUtilities imageNamed:@"default_portrait_msg"
                                           ofBundle:@"RongCloud.bundle"]];
    _imageView.layer.cornerRadius = 20.0;
    _imageView.layer.masksToBounds = YES;
    _imageView.layer.borderColor = [[UIColor whiteColor] CGColor];
    _imageView.layer.borderWidth = 2;
    UIImageView *arrow =
        [[UIImageView alloc] initWithFrame:CGRectMake(12, -1+35, 16, 12)];
    arrow.image = [UIImage imageNamed:@"big_arrow.png"];
    _locationImageView =
        [[UIImageView alloc] initWithFrame:CGRectMake(5, 10+35, 30, 30)];
    [self addObserver:self
           forKeyPath:@"imageUrl"
              options:NSKeyValueObservingOptionNew
              context:nil];
    [self addSubview:_locationImageView];
    [self addSubview:arrow];
    [self addSubview:_imageView];
      
      UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
      btn.frame = self.bounds;
      [self addSubview:btn];
      [btn addTarget:self action:@selector(didTapDisclosureButton:) forControlEvents:UIControlEventTouchUpInside];
//

    //        CABasicAnimation* rotationAnimation = [CABasicAnimation
    //        animationWithKeyPath:@"transform.rotation.z"];
    //        rotationAnimation.delegate = self;
    //        rotationAnimation.fromValue = [NSNumber
    //        numberWithFloat:fromValue]; // 起始角度
    //        rotationAnimation.toValue = [NSNumber numberWithFloat:2 *
    //        M_PI];//一圈
    //        rotationAnimation.duration = 1.0f;
    //        rotationAnimation.autoreverses = NO;
    //        rotationAnimation.timingFunction = [CAMediaTimingFunction
    //        functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
    //        rotationAnimation.removedOnCompletion = NO;
    //        rotationAnimation.fillMode = kCAFillModeBoth;
    //        [_locationImageView.layer addAnimation:rotationAnimation
    //        forKey:@"revItUpAnimation"];
  }

  return self;
}
- (void)refreshHead:(float )heading
{
    //NSLog(@"%f", heading);
    
    // 将设备的方向角度换算成弧度
    CGFloat headings = 1.0f * M_PI * heading / 180.0f;
    // 创建不断改变CALayer的transform属性的属性动画
    CABasicAnimation* anim = [CABasicAnimation
                              animationWithKeyPath:@"transform"];
    CATransform3D fromValue = _locationImageView.layer.transform;
    // 设置动画开始的属性值
    anim.fromValue = [NSValue valueWithCATransform3D: fromValue];
    // 绕Z轴旋转heading弧度的变换矩阵
    CATransform3D toValue = CATransform3DMakeRotation(headings , 0 , 0 , 1);
    // 设置动画结束的属性
    anim.toValue = [NSValue valueWithCATransform3D: toValue];
    anim.duration = 0.5;
    anim.removedOnCompletion = YES;
    // 设置动画结束后znzLayer的变换矩阵
    _locationImageView.layer.transform = toValue;
    // 为znzLayer添加动画
    [_locationImageView.layer addAnimation:anim forKey:nil];
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context {
  if ([keyPath isEqualToString:@"imageUrl"]) {
    dispatch_async(dispatch_get_main_queue(), ^{
      if (![[change objectForKey:@"new"] isKindOfClass:[NSNull class]]) {
        [_imageView
            setImageWithURL:[NSURL
                                   URLWithString:[change objectForKey:@"new"]]
              placeholderImage:[RCDUtilities imageNamed:@"default_portrait_msg"
                                               ofBundle:@"RongCloud.bundle"]];
      }
    });
  }
}
#pragma mark 重写销毁方法
- (void)dealloc {
  [self removeObserver:self forKeyPath:@"imageUrl"];
}
- (void)didTapDisclosureButton:(id)sender {
  if (_tapBlock)
    _tapBlock();
}

- (void)didSelectAnnotationViewInMap:(MKMapView *)mapView {
  [mapView setCenterCoordinate:_coordinate animated:YES];
}

- (void)didDeselectAnnotationViewInMap:(MKMapView *)mapView {
}

@end
