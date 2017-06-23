//
//  RCConversationTableHeaderView.h
//  RCIM
//
//  Created by xugang on 6/21/14.
//  Copyright (c) 2014 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RCConversationCollectionViewHeader : UIView
@property(nonatomic, strong) UIActivityIndicatorView *indicatorView;

- (void)startAnimating;
- (void)stopAnimating;
@end
