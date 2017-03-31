//
//  DashLine.m
//  cosmetology
//
//  Created by jack on 12/2/13.
//  Copyright (c) 2013 jack. All rights reserved.
//

#import "DashLine.h"

@implementation DashLine
@synthesize _bkColor;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        self._bkColor = [UIColor grayColor];
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
    
//    CGContextRef context =UIGraphicsGetCurrentContext();
//    CGContextBeginPath(context);
//    CGContextSetLineWidth(context, 1.0);
//    CGContextSetStrokeColorWithColor(context, _bkColor.CGColor);
//    double lengths[] = {2,1};
//    CGContextSetLineDash(context, 0, lengths,2);
//    CGContextMoveToPoint(context, 0.0, 0.0);
//    CGContextAddLineToPoint(context, self.frame.size.width,0.0);
//    CGContextStrokePath(context);
//    CGContextClosePath(context);
    
}

- (void) dealloc
{
    
}


@end
