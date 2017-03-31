//
//  CircleProgressView.m
//  test
//
//  Created by jack on 06/11/13.
//  Copyright (c) 2013 jack. All rights reserved.
//

#import "CircleProgressView.h"
#import <QuartzCore/QuartzCore.h>


@interface CircleProgressView ()

@property (strong, nonatomic) UIColor *backColor;
@property (strong, nonatomic) UIColor *progressColor;
@property (assign, nonatomic) CGFloat lineWidth;
@property (assign, nonatomic) CGFloat currentProgress;


@end

@implementation CircleProgressView


#define SPEED_30 0.05

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
       
        self.backgroundColor =[UIColor clearColor];
       
        _progress = 0;
        self.currentProgress = 0;
         
        self.backColor = [UIColor whiteColor];
        self.progressColor = THEME_RED_COLOR;
        self.lineWidth = 5;
        
        pL = [[UILabel alloc] initWithFrame:self.bounds];
        pL.backgroundColor = [UIColor clearColor];
        pL.font = [UIFont boldSystemFontOfSize:15];
        pL.textColor = THEME_RED_COLOR;
        pL.textAlignment = NSTextAlignmentCenter;
        [self addSubview:pL];
    }
    return self;
}



- (void)drawRect:(CGRect)rect
{
    //draw background circle
    UIBezierPath *backCircle = [UIBezierPath bezierPathWithArcCenter:CGPointMake(self.bounds.size.width / 2,self.bounds.size.height / 2)
                                                              radius:self.bounds.size.width / 2 - self.lineWidth / 2
                                                          startAngle:(CGFloat) - M_PI_2
                                                            endAngle:(CGFloat)(1.5 * M_PI)
                                                           clockwise:YES];
    [self.backColor setStroke];
    backCircle.lineWidth = self.lineWidth;
    [backCircle stroke];
    
    if (self.currentProgress != 0) {
        //draw progress circle
        UIBezierPath *progressCircle = [UIBezierPath bezierPathWithArcCenter:
                                        CGPointMake(self.bounds.size.width / 2,self.bounds.size.height / 2)
                                                                      radius:self.bounds.size.width / 2 - self.lineWidth / 2
                                                                  startAngle:(CGFloat) - M_PI_2
                                                                    endAngle:(CGFloat)(- M_PI_2 + self.currentProgress * 2 * M_PI)
                                                                   clockwise:YES];
        [self.progressColor setStroke];
        progressCircle.lineWidth = self.lineWidth;
        [progressCircle stroke];
    }
}



- (void) setProgress:(float)progress{
    
    _progress = progress;
    
    pL.text = [NSString stringWithFormat:@"%d%%",(int)(_progress*100)];
    
    self.currentProgress = _progress;
    [self setNeedsDisplay];
    
}

- (void) updateOffest:(float)offset{
    
    _progress = offset;
    
    self.currentProgress = _progress;
    [self setNeedsDisplay];
}

- (void) smallRefreshMode{
    self.lineWidth = 1;
    self.backColor = [UIColor clearColor];
    
}

- (void) dealloc
{

    
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
