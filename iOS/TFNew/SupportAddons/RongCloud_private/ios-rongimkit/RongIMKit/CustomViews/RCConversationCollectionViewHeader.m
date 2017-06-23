//
//  RCConversationTableHeaderView.m
//  RCIM
//
//  Created by xugang on 6/21/14.
//  Copyright (c) 2014 RongCloud. All rights reserved.
//

#import "RCConversationCollectionViewHeader.h"

@implementation RCConversationCollectionViewHeader
@synthesize indicatorView = _indicatorView;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code

        self.indicatorView =
            [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        _indicatorView.frame = CGRectMake(0, 0, 20.0f, 20.0f);
        [self addSubview:_indicatorView];
        [_indicatorView setCenter:CGPointMake(frame.size.width / 2, frame.size.height / 2)];
    }
    return self;
}
- (void)startAnimating {
    if (self.indicatorView.isAnimating == NO) {
        [self.indicatorView startAnimating];
    }
}
- (void)stopAnimating {
    if (self.indicatorView.isAnimating == YES) {
        [self.indicatorView stopAnimating];
    }
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
