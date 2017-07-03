//
//  RCSearchItemView.m
//  HelloIos
//
//  Created by litao on 15/4/9.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import "RCSearchItemView.h"
#import "RCPublicServiceViewConstants.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"

@interface RCSearchItemView ()
@property(nonatomic, weak) UILabel *keyLabel;
@end

@implementation RCSearchItemView
- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setup];
    }
    return self;
}
- (void)setup {
    UIImageView *imageView = [[UIImageView alloc]
        initWithFrame:CGRectMake(RCPublicServiceProfileCellPaddingLeft, RCPublicServiceProfileCellPaddingTop,
                                 RCPublicServiceProfileHeaderImageWidth - 20, RCPublicServiceProfileHeaderImageHeigh - 20)];
    [imageView setImage:[self imageNamed:@"default_portrait"]];

    UIFont *font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
    CGSize size = CGSizeMake(RCPublicServiceProfileCellTitleWidth, 2000);
#if __IPHONE_OS_VERSION_MAX_ALLOWED < __IPHONE_7_0
    CGSize labelsize = [NSLocalizedStringFromTable(@"Search",@"RongCloudKit",nil) sizeWithFont:font constrainedToSize:size lineBreakMode:NSLineBreakByCharWrapping];
#else
    CGSize labelsize = [RCKitUtility getTextDrawingSize:NSLocalizedStringFromTable(@"Search",@"RongCloudKit",nil) font:font constrainedSize:size];
#endif
    UILabel *searchLabel =
        [[UILabel alloc] initWithFrame:CGRectMake(RCPublicServiceProfileHeaderImageWidth,
                                                  RCPublicServiceProfileHeaderImageHeigh / 2 - labelsize.height / 2,
                                                  labelsize.width, labelsize.height)];
    CGRect frame = searchLabel.frame;
    frame.origin.x += frame.size.width + 5;
    frame.size.width = self.frame.size.width - frame.origin.x - RCPublicServiceProfileHeaderPaddingRight;
    UILabel *keyLabel = [[UILabel alloc] initWithFrame:frame];

    searchLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
    keyLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];

    searchLabel.textAlignment = NSTextAlignmentLeft;
    searchLabel.numberOfLines = 1;
    [searchLabel setText:@"搜索: "];

    keyLabel.textAlignment = NSTextAlignmentLeft;
    keyLabel.numberOfLines = 1;

    [self addSubview:imageView];
    [self addSubview:searchLabel];
    [self addSubview:keyLabel];

    ////    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeCenterY
    ///relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:0]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeLeft
    //    relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeTop
    //    relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0
    //    constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeBottom
    //    relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0
    //    constant:8]];
    //
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:searchLabel attribute:NSLayoutAttributeCenterY
    //    relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:0]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:searchLabel attribute:NSLayoutAttributeLeft
    //    relatedBy:NSLayoutRelationEqual toItem:imageView attribute:NSLayoutAttributeRight multiplier:1.0 constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:searchLabel attribute:NSLayoutAttributeTop
    //    relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0
    //    constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:searchLabel attribute:NSLayoutAttributeBottom
    //    relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0
    //    constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:keyLabel attribute:NSLayoutAttributeCenterY
    //    relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:0]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:keyLabel attribute:NSLayoutAttributeLeft
    //    relatedBy:NSLayoutRelationEqual toItem:searchLabel attribute:NSLayoutAttributeRight multiplier:1.0
    //    constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:keyLabel attribute:NSLayoutAttributeTop
    //    relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0
    //    constant:8]];
    //
    //    [self addConstraint:[NSLayoutConstraint constraintWithItem:keyLabel attribute:NSLayoutAttributeBottom
    //    relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0
    //    constant:8]];
    //
    ////    [self addConstraint:[NSLayoutConstraint constraintWithItem:keyLabel attribute:NSLayoutAttributeRight
    ///relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0
    ///constant:8]];

    self.keyLabel = keyLabel;

    UITapGestureRecognizer *tapGesture =
        [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTaped:)];
    [self addGestureRecognizer:tapGesture];
}

- (void)onTaped:(id)sender {
    [self.delegate onSearchItemTapped];
}

- (void)setKeyContent:(NSString *)keyContent {
    NSDictionary *dict = @{NSForegroundColorAttributeName : RGBCOLOR(83, 213, 105)};
    NSAttributedString *key = [[NSAttributedString alloc] initWithString:keyContent attributes:dict];
    self.keyLabel.attributedText = key;
}

- (UIImage *)imageNamed:(NSString *)name {
    UIImage *image = nil;
    NSString *bundleName = @"RongCloud.bundle";
    NSString *image_name = [NSString stringWithFormat:@"%@.png", name];
    NSString *resourcePath = [[NSBundle mainBundle] resourcePath];
    NSString *bundlePath = [resourcePath stringByAppendingPathComponent:bundleName];
    NSString *image_path = [bundlePath stringByAppendingPathComponent:image_name];
    image = [UIImage imageWithContentsOfFile:image_path];

    return image;
}
@end
