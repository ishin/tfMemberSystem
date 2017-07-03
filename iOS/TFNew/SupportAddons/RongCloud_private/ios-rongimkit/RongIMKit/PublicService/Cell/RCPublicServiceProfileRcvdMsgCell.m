//
//  RCPublicServiceProfileRcvdMsgCell.m
//  HelloIos
//
//  Created by litao on 15/4/10.
//  Copyright (c) 2015年 litao. All rights reserved.
//

#import "RCPublicServiceProfileRcvdMsgCell.h"
#import "RCPublicServiceViewConstants.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import <RongIMLib/RongIMLib.h>

@interface RCPublicServiceProfileRcvdMsgCell ()
@property(nonatomic, strong) UILabel *title;
@property(nonatomic, strong) UISwitch *switcher;
@end

@implementation RCPublicServiceProfileRcvdMsgCell

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

    self.title.numberOfLines = 0;
    self.title.font = [UIFont preferredFontForTextStyle:UIFontTextStyleHeadline];
    self.title.textAlignment = NSTextAlignmentLeft;
    self.title.font = [UIFont systemFontOfSize:RCPublicServiceProfileBigFont];
    self.title.textColor = [UIColor blackColor];
    self.switcher = [[UISwitch alloc] init];
    [self.switcher addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];

    [self.contentView addSubview:self.title];
    [self.contentView addSubview:self.switcher];

    CGRect frame = self.contentView.frame;
    DebugLog(@"frame size is %f, %f", frame.size.width, frame.size.height);
}

- (void)setTitleText:(NSString *)title {
    self.title.text = title;
    [self updateFrame];
}

- (void)switchAction:(id)sender {
    BOOL enableNotification = self.switcher.on;

    [[RCIMClient sharedRCIMClient] setConversationNotificationStatus:(RCConversationType)self.serviceProfile.publicServiceType
        targetId:self.serviceProfile.publicServiceId
        isBlocked:!enableNotification
        success:^(RCConversationNotificationStatus nStatus) {
          dispatch_async(dispatch_get_main_queue(), ^{
            [self setOn:enableNotification];
          });
        }
        error:^(RCErrorCode status) {
          DebugLog(@"set error");
          dispatch_async(dispatch_get_main_queue(), ^{
            [self setOn:enableNotification];
          });
        }];
}
- (void)setOn:(BOOL)enableNotification {
    [self.switcher setOn:enableNotification];
}

- (void)updateFrame {
    CGRect contentViewFrame = self.frame;
    CGSize size = CGSizeMake(RCPublicServiceProfileCellTitleWidth, MAXFLOAT);
    CGSize labelsize = [RCKitUtility getTextDrawingSize:self.title.text font:[UIFont systemFontOfSize:RCPublicServiceProfileBigFont] constrainedSize:size];
    CGFloat maxHeigh = MAX(labelsize.height, self.switcher.frame.size.height);
    self.title.frame = CGRectMake(2 * RCPublicServiceProfileCellPaddingLeft,
                                  RCPublicServiceProfileCellPaddingTop + (maxHeigh - labelsize.height) / 2,
                                  labelsize.width, labelsize.height);

    CGRect frame = self.switcher.frame;

    frame.origin.y = RCPublicServiceProfileCellPaddingTop + (maxHeigh - frame.size.height) / 2;
    frame.origin.x = self.frame.size.width - RCPublicServiceProfileCellPaddingRight - frame.size.width - 10;

    self.switcher.frame = frame;

    contentViewFrame.size.height = MAX(self.title.frame.size.height, self.switcher.frame.size.height) +
                                   RCPublicServiceProfileCellPaddingTop + RCPublicServiceProfileCellPaddingBottom;
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
