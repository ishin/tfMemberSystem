//
//  RCPublicServiceProfileOwnerCell.m
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import "RCPublicServiceProfileOwnerCell.h"
#import "RCPublicServiceViewConstants.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"

@interface RCPublicServiceProfileOwnerCell ()
@property(nonatomic, strong) UILabel *title;
@property(nonatomic, strong) UILabel *content;
@property(nonatomic, strong) NSString *urlString;
@property(nonatomic, weak) id<RCPublicServiceProfileViewUrlDelegate> delegate;
@end

@implementation RCPublicServiceProfileOwnerCell

- (instancetype)init {
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"hello"];
    ;

    if (self) {
        [self setup];
    }

    return self;
}

- (void)setup {
    CGRect bounds = [[UIScreen mainScreen] bounds];
    bounds.size.height = 0;

    self.frame = bounds;

    self.title = [[UILabel alloc] initWithFrame:CGRectZero];
    self.content = [[UILabel alloc] initWithFrame:CGRectZero];

    self.title.numberOfLines = 0;
    self.title.font = [UIFont systemFontOfSize:RCPublicServiceProfileBigFont];
    self.title.textColor = [UIColor blackColor];
    self.title.textAlignment = NSTextAlignmentLeft;
    self.content.numberOfLines = 0;
    self.content.lineBreakMode = NSLineBreakByCharWrapping;
    self.content.textColor = [UIColor grayColor];
    self.content.font = [UIFont systemFontOfSize:RCPublicServiceProfileSmallFont];
    [self.contentView addSubview:self.title];
    [self.contentView addSubview:self.content];
    self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
}

- (void)setTitle:(NSString *)title
         Content:(NSString *)content
             url:(NSString *)urlString
        delegate:(id<RCPublicServiceProfileViewUrlDelegate>)delegate {
    self.title.text = title;
    self.content.text = content;
    self.urlString = urlString;
    [self updateFrame];

    if (urlString && urlString.length > 0) {
        UITapGestureRecognizer *tapGesture =
            [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTaped:)];
        [self addGestureRecognizer:tapGesture];
    }

    self.delegate = delegate;
}

- (void)onTaped:(id)sender {
    [self.delegate gotoUrl:self.urlString];
}

- (void)updateFrame {
    CGRect contentViewFrame = self.frame;
    UIFont *font1 = [UIFont systemFontOfSize:RCPublicServiceProfileBigFont];
    //设置一个行高上限
    CGSize size = CGSizeMake(RCPublicServiceProfileCellTitleWidth, 2000);
    CGSize labelsize1 = [RCKitUtility getTextDrawingSize:self.title.text font:font1 constrainedSize:size];
    self.title.frame = CGRectMake(2 * RCPublicServiceProfileCellPaddingLeft, RCPublicServiceProfileCellPaddingTop,
                                  labelsize1.width, labelsize1.height);

#define DiscloseIndicatorWidth 20
    size = CGSizeMake(self.frame.size.width - RCPublicServiceProfileCellPaddingLeft - RCPublicServiceProfileCellTitleWidth -
                          RCPublicServiceProfileCellPaddingRight - DiscloseIndicatorWidth,
                      2000);
    UIFont *font2 = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
    CGSize labelsize2 = [RCKitUtility getTextDrawingSize:self.content.text font:font2 constrainedSize:size];

    float offset = 0;
    if (labelsize2.height < labelsize1.height) {
        offset = (labelsize1.height - labelsize2.height) / 2;
        offset += (font1.xHeight - font2.xHeight) / 2;
    }
    self.content.frame = CGRectMake(RCPublicServiceProfileCellPaddingLeft + RCPublicServiceProfileCellTitleWidth,
                                    RCPublicServiceProfileCellPaddingTop + offset, labelsize2.width, labelsize2.height);

    contentViewFrame.size.height = MAX(self.title.frame.size.height, self.content.frame.size.height) +
                                   RCPublicServiceProfileCellPaddingTop + RCPublicServiceProfileCellPaddingBottom;
    [self.content sizeToFit];
    self.frame = contentViewFrame;
}
- (void)drawRect:(CGRect)rect {
    CGContextRef context = UIGraphicsGetCurrentContext();

    CGContextSetFillColorWithColor(context, [UIColor clearColor].CGColor);
    CGContextFillRect(context, rect);

    //上分割线，
    CGContextSetStrokeColorWithColor(context, HEXCOLOR(0xFFFFFF).CGColor);
    CGContextStrokeRect(context, CGRectMake(5, -1, rect.size.width - 10, 1));

    //下分割线
    CGContextSetStrokeColorWithColor(context, HEXCOLOR(0xe2e2e2).CGColor);
    CGContextStrokeRect(context, CGRectMake(5, rect.size.height, rect.size.width - 10, 1));
}
@end
