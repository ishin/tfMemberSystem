//
//  GlobalTouchButtonView.m
//  Hint
//
//  Created by jack on 1/13/17.
//  Copyright © 2017 jack. All rights reserved.
//

#import "GlobalTouchButtonView.h"
#import "UIButton+Color.h"
#import "UIButton+WebCache.h"

@interface GlobalTouchButtonView ()
{
    CGAffineTransform originalTransform;
    CGAffineTransform highSolutionTransform;

    UIImageView *_layer;
    BOOL isOpen;
    
    BOOL _isMoved;
    
    UIView *_globalCallView;
    UIImageView *_maskImg;
    
    UIButton *_btn0;
    UIButton *_btn1;
    UIButton *_btn2;
    UIButton *_btn3;
    UIButton *_btn4;
    
}
@property (nonatomic, strong) NSMutableArray *_arrBtns;
@property (nonatomic, strong) NSMutableArray *_arrMasks;

@end


@implementation GlobalTouchButtonView
@synthesize _arrBtns;
@synthesize _arrMasks;

- (id)initWithFrame:(CGRect)frame
{
    
    if (self = [super initWithFrame:frame])
    {
        originalTransform = CGAffineTransformIdentity;
        highSolutionTransform = CGAffineTransformIdentity;

        _layer = [[UIImageView alloc] initWithFrame:self.bounds];
        _layer.image = [UIImage imageNamed:@"global_button.png"];
        [self addSubview:_layer];
        
        
        self._arrBtns = [NSMutableArray array];
        self._arrMasks = [NSMutableArray array];
        
        _globalCallView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
        _globalCallView.backgroundColor = RGBA(0x00, 0x27, 0x2C, 0.4);
        
        _maskImg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"global_area.png"]];
        [_globalCallView addSubview:_maskImg];
        _maskImg.center = CGPointMake(SCREEN_WIDTH - CGRectGetWidth(_maskImg.frame)/2, SCREEN_HEIGHT/2);
        
        
        _btn0 = [UIButton buttonWithColor:[UIColor clearColor] selColor:nil];
        _btn0.frame = CGRectMake(0, 0, 40, 40);
        _btn0.layer.cornerRadius = 20;
        _btn0.clipsToBounds = YES;
        [_globalCallView addSubview:_btn0];
        
        
        
        UILabel *maskL = [[UILabel alloc] initWithFrame:_btn0.bounds];
        [_btn0 addSubview:maskL];
        maskL.textColor = [UIColor whiteColor];
        maskL.font = [UIFont boldSystemFontOfSize:20];
        maskL.layer.cornerRadius = 20;
        maskL.clipsToBounds = YES;
        maskL.textAlignment = NSTextAlignmentCenter;
        maskL.hidden = YES;
        
        [_arrMasks addObject:maskL];

        
        int xx = CGRectGetMinX(_maskImg.frame);
        int sw = CGRectGetWidth(_maskImg.frame)/3;
        
        int sh = CGRectGetHeight(_maskImg.frame)/2;
        
        _btn0.center = CGPointMake(SCREEN_WIDTH - sw, CGRectGetMinY(_maskImg.frame)+18);
        
        _btn1 = [UIButton buttonWithColor:[UIColor clearColor] selColor:nil];
        _btn1.frame = CGRectMake(0, 0, 40, 40);
        _btn1.layer.cornerRadius = 20;
        _btn1.clipsToBounds = YES;
        [_globalCallView addSubview:_btn1];
        
        _btn1.center = CGPointMake(xx + sw - 16, CGRectGetMinY(_maskImg.frame)+72);
        
        
        maskL = [[UILabel alloc] initWithFrame:_btn1.bounds];
        [_btn1 addSubview:maskL];
        maskL.textColor = [UIColor whiteColor];
        maskL.font = [UIFont boldSystemFontOfSize:20];
        maskL.layer.cornerRadius = 20;
        maskL.clipsToBounds = YES;
        maskL.textAlignment = NSTextAlignmentCenter;
        maskL.hidden = YES;
        
        [_arrMasks addObject:maskL];

        
        _btn2 = [UIButton buttonWithColor:[UIColor clearColor] selColor:nil];
        _btn2.frame = CGRectMake(0, 0, 40, 40);
        _btn2.layer.cornerRadius = 20;
        _btn2.clipsToBounds = YES;
        [_globalCallView addSubview:_btn2];
        
        _btn2.center = CGPointMake(xx+6, CGRectGetMinY(_maskImg.frame)+sh);
        
        maskL = [[UILabel alloc] initWithFrame:_btn2.bounds];
        [_btn2 addSubview:maskL];
        maskL.textColor = [UIColor whiteColor];
        maskL.font = [UIFont boldSystemFontOfSize:20];
        maskL.layer.cornerRadius = 20;
        maskL.clipsToBounds = YES;
        maskL.textAlignment = NSTextAlignmentCenter;
        maskL.hidden = YES;
        
        [_arrMasks addObject:maskL];
        
        _btn3 = [UIButton buttonWithColor:[UIColor clearColor] selColor:nil];
        _btn3.frame = CGRectMake(0, 0, 40, 40);
        _btn3.layer.cornerRadius = 20;
        _btn3.clipsToBounds = YES;
        [_globalCallView addSubview:_btn3];
        
        int offy = CGRectGetMidY(_maskImg.frame) - CGRectGetMidY(_btn1.frame);
        
        _btn3.center = CGPointMake(_btn1.center.x, CGRectGetMidY(_maskImg.frame)+offy-4);
  
        
        maskL = [[UILabel alloc] initWithFrame:_btn3.bounds];
        [_btn3 addSubview:maskL];
        maskL.textColor = [UIColor whiteColor];
        maskL.font = [UIFont boldSystemFontOfSize:20];
        maskL.layer.cornerRadius = 20;
        maskL.clipsToBounds = YES;
        maskL.textAlignment = NSTextAlignmentCenter;
        maskL.hidden = YES;
        
        [_arrMasks addObject:maskL];
        
        _btn4 = [UIButton buttonWithColor:[UIColor clearColor] selColor:nil];
        _btn4.frame = CGRectMake(0, 0, 40, 40);
        _btn4.layer.cornerRadius = 20;
        _btn4.clipsToBounds = YES;
        [_globalCallView addSubview:_btn4];
        
        offy = CGRectGetMidY(_maskImg.frame) - CGRectGetMidY(_btn0.frame);
        
        _btn4.center = CGPointMake(_btn0.center.x, CGRectGetMidY(_maskImg.frame)+offy-5);
  
        maskL = [[UILabel alloc] initWithFrame:_btn4.bounds];
        [_btn4 addSubview:maskL];
        maskL.textColor = [UIColor whiteColor];
        maskL.font = [UIFont boldSystemFontOfSize:20];
        maskL.layer.cornerRadius = 20;
        maskL.clipsToBounds = YES;
        maskL.textAlignment = NSTextAlignmentCenter;
        maskL.hidden = YES;
        
        [_arrMasks addObject:maskL];
        
        _maskImg.transform = CGAffineTransformMakeScale(0, 0);
        
        _btn0.hidden = YES;
        _btn1.hidden = YES;
        _btn2.hidden = YES;
        _btn3.hidden = YES;
        _btn4.hidden = YES;
        
        [_arrBtns addObject:_btn0];
        [_arrBtns addObject:_btn1];
        [_arrBtns addObject:_btn2];
        [_arrBtns addObject:_btn3];
        [_arrBtns addObject:_btn4];
        
        
        _btn0.tag = 0;
        _btn1.tag = 1;
        _btn2.tag = 2;
        _btn3.tag = 3;
        _btn4.tag = 4;
        
        [_btn0 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        [_btn1 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        [_btn2 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        [_btn3 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        [_btn4 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(onTapSelected:)];
        [_globalCallView addGestureRecognizer:tap];
    }
    
    return self;
    
}

- (void) onTapSelected:(id)sender{
    
    [UIView animateWithDuration:0.25 animations:^{
        _globalCallView.alpha = 0.0;
        _maskImg.transform = CGAffineTransformMakeScale(0, 0);
    } completion:^(BOOL finished) {
        
        [_globalCallView removeFromSuperview];
        _globalCallView.alpha = 1.0;
    }];
}

-(void) touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event {
    
    _isMoved = NO;
    
}

-(void) touchesMoved:(NSSet*)touches withEvent:(UIEvent*)event {
    
    _isMoved = YES;
    
    if([_globalCallView superview])
        return;
    
    
    NSSet *allTouches = [event allTouches];
    switch ([allTouches count])
    {
        case 1:
        {
            UITouch* touch = [touches anyObject];
            CGPoint previous = [touch previousLocationInView:self.superview];
            CGPoint current = [touch locationInView:self.superview];
            CGPoint offset = CGPointMake(current.x - previous.x, current.y - previous.y);
            
            CGAffineTransform translate = CGAffineTransformMakeTranslation(offset.x,offset.y);
            self.transform = CGAffineTransformConcat(originalTransform, translate);
            originalTransform = self.transform;
            
            
            
            
        }
            break;
        default:
            break;
    }
    
}

-(void) touchesEnded:(NSSet*)touches withEvent:(UIEvent*)event {
    
    if(_isMoved)
    {
        if([_globalCallView superview])
            return;
        
        CGRect rc = CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        if(!CGRectContainsRect(rc, self.frame))
        {
            CGRect c1 = self.frame;
            if(CGRectGetMaxX(c1) > SCREEN_WIDTH)
            {
                c1.origin.x = SCREEN_WIDTH - 60;
            }
            if(CGRectGetMinX(c1) < 0)
            {
                c1.origin.x = 10;
            }
            if(CGRectGetMaxY(c1) > SCREEN_HEIGHT)
            {
                c1.origin.y = SCREEN_HEIGHT - 60;
            }
            if(CGRectGetMinY(c1) < 0)
            {
                c1.origin.y = 10;
            }
            
            [UIView animateWithDuration:0.25
                             animations:^{
                                 self.frame = c1;
                             } completion:^(BOOL finished) {
                                 originalTransform = self.transform;
                                 
                             }];
        }
    }
    else
    {
        //touched
        
        originalTransform = CGAffineTransformIdentity;
        highSolutionTransform = CGAffineTransformIdentity;

        [[self superview] insertSubview:_globalCallView belowSubview:self];
        
        [UIView animateWithDuration:0.25
                         animations:^{
                             
                             self.center = CGPointMake(SCREEN_WIDTH-30, SCREEN_HEIGHT/2);
                             self.transform = CGAffineTransformIdentity;

                             [self show];
                             
                         } completion:^(BOOL finished) {
                            
                             
                         }];
        
        
    }
    
}


- (void) show{
    
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
    
        _btn0.hidden = YES;
        _btn1.hidden = YES;
        _btn2.hidden = YES;
        _btn3.hidden = YES;
        _btn4.hidden = YES;
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDelegate:self];
        [UIView setAnimationDidStopSelector:@selector(showContacts)];
        _maskImg.transform = CGAffineTransformMakeScale(1, 1);
        [UIView commitAnimations];
    
    
    
        
    });
}

- (void) showContacts{
    
    NSArray *last5_chat = [[NSUserDefaults standardUserDefaults] objectForKey:@"last5_chat_sync"];
    for(int i = 0; i < [last5_chat count]; i++)
    {
        NSDictionary *dic = [last5_chat objectAtIndex:i];
    
        if(i < 5)
        {
            UIButton *btn = [_arrBtns objectAtIndex:i];
            UILabel  *maskL = [_arrMasks objectAtIndex:i];
            
            btn.hidden = NO;
            int conversationType = [[dic objectForKey:@"type"] intValue];
            NSString *avatarurl = [dic objectForKey:@"avatarurl"];
            
            if(conversationType == 1)
            {
                maskL.hidden = NO;
                NSString *fullname = [dic objectForKey:@"name"];
                NSString *targetId = [dic objectForKey:@"id"];
                
                [btn setImageWithURL:[NSURL URLWithString:avatarurl]
                            forState:UIControlStateNormal
                    placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
                
                maskL.backgroundColor = [Utls groupMaskColorWithId:[targetId intValue]];
                
                NSString *gName = fullname;
                NSString *showName = @"";
                
                if([gName length] > 1)
                    showName = [gName substringWithRange:NSMakeRange(1, 1)];
                else if([gName length] == 1)
                    showName = gName;
                
                maskL.text = showName;
            }
            else
            {
                maskL.hidden = YES;
                [btn setImageWithURL:[NSURL URLWithString:avatarurl] forState:UIControlStateNormal];
            }
        }

    }
}


- (void) buttonAction:(UIButton*)btn{
    
    int idx = (int)btn.tag;
    
    NSArray *last5_chat = [[NSUserDefaults standardUserDefaults] objectForKey:@"last5_chat_sync"];
    if(idx < [last5_chat count])
    {
        NSDictionary *dic = [last5_chat objectAtIndex:idx];
        
        int conversationType = [[dic objectForKey:@"type"] intValue];
     
        NSString *targetId = [dic objectForKey:@"id"];
        
        AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        [app pushToChat:targetId type:conversationType];
        
    }
    
    
    [self onTapSelected:nil];
    
}


//计算扇形坐标
- (NSMutableArray *)getXWithTanAngle:(double)tanAngle {
    double x;
    double y;
    NSMutableArray *locationArray = [[NSMutableArray alloc]init];
    x = 60 / (sqrt(1 + tanAngle * tanAngle));
    y = tanAngle * x ;
    [locationArray addObject:[NSString stringWithFormat:@"%f",x]];
    [locationArray addObject:[NSString stringWithFormat:@"%f",y]];
    return locationArray;
}
- (void)menuButton:(UIButton *)sender {
    
    /*
    int n = 5;
    
    double tan0 = tan(0);
    double tan1 = tan(M_PI_2 / (n - 1));
    double tan2 = tan((M_PI_2 / (n - 1) * 2));
    double tan3 = tan((M_PI_2 / (n - 1) * 3));
    double tan4 = tan((M_PI_2 / (n - 1) * 4));
    
    double x0 = [[self getXWithTanAngle:tan0] [0] doubleValue];
    double x1 = [[self getXWithTanAngle:tan1] [0] doubleValue];
    double x2 = [[self getXWithTanAngle:tan2] [0] doubleValue];
    double x3 = [[self getXWithTanAngle:tan3] [0] doubleValue];
    
    double y1 = [[self getXWithTanAngle:tan1] [1] doubleValue];
    double y2 = [[self getXWithTanAngle:tan2] [1] doubleValue];
    double y3 = [[self getXWithTanAngle:tan3] [1] doubleValue];
    double y4 = [[self getXWithTanAngle:tan4] [1] doubleValue];
    
    
    if (isOpen) {
        isOpen = NO;
        [self.btn1.layer addAnimation:[self fromEndPoint:CGPointMake(centerX - x0, centerY) toStartPoint:self.menuButton.center duration:0.15 button:self.btn1] forKey:nil];
        [self.btn2.layer addAnimation:[self fromEndPoint:CGPointMake(centerX - x1, centerY - y1) toStartPoint:self.menuButton.center duration:0.15 button:self.btn2] forKey:nil];
        [self.btn3.layer addAnimation:[self fromEndPoint:CGPointMake(centerX - x2, centerY - y2) toStartPoint:self.menuButton.center duration:0.15 button:self.btn3] forKey:nil];
        [self.btn4.layer addAnimation:[self fromEndPoint:CGPointMake(centerX - x3, centerY - y3) toStartPoint:self.menuButton.center duration:0.15 button:self.btn4] forKey:nil];
        [self.btn5.layer addAnimation:[self fromEndPoint:CGPointMake(centerX,  centerY - y4) toStartPoint:self.menuButton.center duration:0.15 button:self.btn5] forKey:nil];
        
    }
    else {
        
        isOpen = YES;
        
        [self.btn1.layer addAnimation:[self fromPoint:self.menuButton.center toPoint:CGPointMake(centerX - x0, centerY) duration:0.3 button:self.btn1] forKey:nil];
        [self.btn2.layer addAnimation:[self fromPoint:self.menuButton.center toPoint:CGPointMake(centerX - x1, centerY - y1) duration:0.3 button:self.btn2] forKey:nil];
        [self.btn3.layer addAnimation:[self fromPoint:self.menuButton.center toPoint:CGPointMake(centerX - x2, centerY - y2) duration:0.3 button:self.btn3] forKey:nil];
        [self.btn4.layer addAnimation:[self fromPoint:self.menuButton.center toPoint:CGPointMake(centerX - x3, centerY - y3) duration:0.3 button:self.btn4] forKey:nil];
        [self.btn5.layer addAnimation:[self fromPoint:self.menuButton.center toPoint:CGPointMake(centerX,  centerY - y4) duration:0.3 button:self.btn5] forKey:nil];
    }
     
     */
    
}

//打开菜单
+ (CAAnimationGroup *)fromPoint:(CGPoint)from toPoint:(CGPoint)to duration:(CFTimeInterval)duration button:(UIButton *)button
{
    //路径曲线
    UIBezierPath *movePath = [UIBezierPath bezierPath];
    [movePath moveToPoint:from];
    //[movePath addLineToPoint:to];
    [movePath addQuadCurveToPoint:to
                     controlPoint:CGPointMake( to.x - 10, to.y - 10)];
    [movePath addQuadCurveToPoint:to
                     controlPoint:CGPointMake( to.x + 10, to.y + 10)];
    
    //关键帧
    CAKeyframeAnimation *moveAnim = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    moveAnim.path = movePath.CGPath;
    moveAnim.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseIn];
    moveAnim.removedOnCompletion = YES;
    
    CABasicAnimation *TransformAnim = [CABasicAnimation animationWithKeyPath:@"transform"];
    TransformAnim.fromValue = [NSValue valueWithCATransform3D:CATransform3DIdentity];
    //沿Z轴旋转
    TransformAnim.toValue = [NSValue valueWithCATransform3D: CATransform3DMakeRotation(M_PI,0,0,1)];
    TransformAnim.cumulative = YES;
    TransformAnim.duration = duration / 4;
    //旋转1遍，360度
    TransformAnim.repeatCount = 4;
    TransformAnim.removedOnCompletion = YES;
    
    CAAnimationGroup *animGroup = [CAAnimationGroup animation];
    animGroup.animations = [NSArray arrayWithObjects: TransformAnim, moveAnim,nil];
    animGroup.duration = duration;
    button.center = to;
    return animGroup;
}


//收回菜单
+ (CAAnimationGroup *)fromEndPoint:(CGPoint)from toStartPoint:(CGPoint)to duration:(CFTimeInterval)duration button:(UIButton *)button
{
    
    //路径曲线
    UIBezierPath *movePath = [UIBezierPath bezierPath];
    [movePath moveToPoint:from];
    [movePath addLineToPoint:to];
    
    //关键帧
    CAKeyframeAnimation *moveAnim = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    moveAnim.path = movePath.CGPath;
    moveAnim.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
    moveAnim.removedOnCompletion = YES;
    
    CABasicAnimation *TransformAnim = [CABasicAnimation animationWithKeyPath:@"transform"];
    TransformAnim.fromValue = [NSValue valueWithCATransform3D:CATransform3DIdentity];
    //沿Z轴旋转
    TransformAnim.toValue = [NSValue valueWithCATransform3D: CATransform3DMakeRotation(M_PI,0,0,1)];
    TransformAnim.cumulative = YES;
    TransformAnim.duration = duration / 3;
    //旋转1遍，360度
    TransformAnim.repeatCount = 3;
    TransformAnim.removedOnCompletion = YES;
    
    CAAnimationGroup *animGroup = [CAAnimationGroup animation];
    animGroup.animations = [NSArray arrayWithObjects:moveAnim, TransformAnim,nil];
    animGroup.duration = duration;
    button.center = to;
    return animGroup;
}

@end
