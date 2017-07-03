//
//  RCNetworkIndicatorView.m
//  RongIMKit
//
//  Created by MiaoGuangfa on 3/16/15.
//  Copyright (c) 2015 RongCloud. All rights reserved.
//

#import "RCNetworkIndicatorView.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"

@interface RCNetworkIndicatorView ()

@property(nonatomic, strong) UIImageView *networkUnreachableImageView;
@property(nonatomic, strong) UILabel *networkUnreachableDescriptionLabel;
@property(nonatomic, strong) UIView *lineView;

@end

@implementation RCNetworkIndicatorView
- (instancetype)initWithText:(NSString *)text {
    self = [super init];
    if (self) {
        self.networkUnreachableImageView = [[UIImageView alloc] init];
        self.networkUnreachableImageView.image = IMAGE_BY_NAMED(@"network_fail");

        self.networkUnreachableDescriptionLabel = [[UILabel alloc] init];
        self.networkUnreachableDescriptionLabel.textColor = HEXCOLOR(0x776868);
        self.networkUnreachableDescriptionLabel.font = [UIFont systemFontOfSize:14.0f];
        self.networkUnreachableDescriptionLabel.text = text;
        self.networkUnreachableDescriptionLabel.backgroundColor = [UIColor clearColor];
      
        self.lineView = [[UIView alloc] init];
        self.lineView.backgroundColor = HEXCOLOR(0xeccecd);
      
        [self addSubview:self.lineView];
        [self addSubview:self.networkUnreachableImageView];
        [self addSubview:self.networkUnreachableDescriptionLabel];

        //self.translatesAutoresizingMaskIntoConstraints = NO;
        self.networkUnreachableImageView.translatesAutoresizingMaskIntoConstraints = NO;
        self.networkUnreachableDescriptionLabel.translatesAutoresizingMaskIntoConstraints = NO;
        self.lineView.translatesAutoresizingMaskIntoConstraints = NO;

        // set autoLayout
        NSDictionary *bindingViews =
            NSDictionaryOfVariableBindings(_networkUnreachableImageView, _networkUnreachableDescriptionLabel,_lineView);

        [self addConstraints:[NSLayoutConstraint
                                 constraintsWithVisualFormat:@"H:|-19-[_networkUnreachableImageView(26)]-20-[_"
                                                             @"networkUnreachableDescriptionLabel]"
                                                     options:0
                                                     metrics:nil
                                                       views:bindingViews]];
        [self addConstraints:[NSLayoutConstraint
                              constraintsWithVisualFormat:@"V:[_networkUnreachableImageView(26)]"
                              options:0
                              metrics:nil
                              views:bindingViews]];
      
      [self addConstraints:[NSLayoutConstraint
                            constraintsWithVisualFormat:@"H:|[_lineView]|"
                            options:0
                            metrics:nil
                            views:bindingViews]];
      [self addConstraints:[NSLayoutConstraint
                            constraintsWithVisualFormat:@"V:[_lineView(0.5)]|"
                            options:0
                            metrics:nil
                            views:bindingViews]];

        [self addConstraint:[NSLayoutConstraint constraintWithItem:_networkUnreachableDescriptionLabel
                                                         attribute:NSLayoutAttributeCenterY
                                                         relatedBy:NSLayoutRelationEqual
                                                            toItem:self
                                                         attribute:NSLayoutAttributeCenterY
                                                        multiplier:1.0f
                                                          constant:0]];
        
        [self addConstraint:[NSLayoutConstraint constraintWithItem:_networkUnreachableImageView
                                                         attribute:NSLayoutAttributeCenterY
                                                         relatedBy:NSLayoutRelationEqual
                                                            toItem:self
                                                         attribute:NSLayoutAttributeCenterY
                                                        multiplier:1.0f
                                                          constant:0]];
      
    }
    return self;
}

@end
