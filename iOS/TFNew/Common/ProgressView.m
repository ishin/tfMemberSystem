//
//  ProgressView.m
//  Hint
//
//  Created by jack on 11/25/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "ProgressView.h"


@implementation ProgressView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        bk = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        bk.backgroundColor = [UIColor whiteColor];
        //bk.image = [UIImage imageNamed:@"progress_bk.png"];
        [self addSubview:bk];
        front = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 0, frame.size.height)];
        //front.image = [UIImage imageNamed:@"progress_front.png"];
        front.backgroundColor = THEME_RED_COLOR;
        [self addSubview:front];
        
        percent = [[UILabel alloc] initWithFrame:CGRectMake(0, frame.size.height, 100, 20)];
        percent.backgroundColor = [UIColor clearColor];
        percent.font = [UIFont systemFontOfSize:15];
        percent.textColor = [UIColor blackColor];
//
        
        
        percent.textAlignment = NSTextAlignmentCenter;
        
        self.clipsToBounds = YES;
    }
    return self;
}

- (void) setProgressColor:(UIColor*)color{
    
    bk.backgroundColor = color;
}

- (void) setProgressHilightColor:(UIColor*)color{
    
    front.backgroundColor = color;
}

- (void) updateProgress:(float)value{
    
    float fw = value * (self.frame.size.width);
    
    
    if(1)    //下载进度条动画
    {
        [UIView beginAnimations:nil context:nil];
        front.frame = CGRectMake(0, 0, fw, self.frame.size.height);
        //percent.center = CGPointMake(fw, self.frame.size.height+10);
        [UIView commitAnimations];
    }
    else{
        [UIView beginAnimations:nil context:nil];
        front.frame = CGRectMake(0, 0, fw, self.frame.size.height);
        // percent.center = CGPointMake(fw, self.frame.size.height+10);
        [UIView commitAnimations];
    }
    
}
- (void) dealloc{
    
    
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
