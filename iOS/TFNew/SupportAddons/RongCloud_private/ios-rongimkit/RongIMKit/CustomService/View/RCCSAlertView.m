//
//  RCCSAlertView.m
//  RongIMKit
//
//  Created by litao on 16/2/23.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCCSAlertView.h"

#define STAR_COUNT 5
#define CLIENT_VIEW_HEIGHT 120
#define CLIENT_VIEW_WIDTH 220
#define TITLE_PADDING_TOP 10
#define TITLE_PADDING_LEFT_RIGHT 10
#define TITLE_HEIGHT 40
#define TITLE_WIDTH (CLIENT_VIEW_WIDTH - TITLE_PADDING_LEFT_RIGHT - TITLE_PADDING_LEFT_RIGHT)

#define CONTENT_PADDING_TOP_BUTTOM 10
#define CONTENT_PADDING_LEFT_RIGHT 10
#define CONTENT_HEIGHT (CLIENT_VIEW_HEIGHT - TITLE_PADDING_TOP - TITLE_HEIGHT - CONTENT_PADDING_TOP_BUTTOM - CONTENT_PADDING_TOP_BUTTOM)
#define CONTENT_WIDTH (CLIENT_VIEW_WIDTH - CONTENT_PADDING_LEFT_RIGHT - CONTENT_PADDING_LEFT_RIGHT)

@interface RCCSAlertView () <RCCustomIOSAlertViewDelegate>
@property (nonatomic, weak)id<RCCSAlertViewDelegate> csAlertViewDelegate;
@property (nonatomic, strong)UILabel *titleLabel;
@property (nonatomic, strong)UILabel *contentLabel;
@end


@implementation RCCSAlertView
- (instancetype)initWithTitle:(NSString *)title warning:(NSString *)warning delegate:(id<RCCSAlertViewDelegate>) delegate {
    self = [super init];
    if (self) {
        self.csAlertViewDelegate = delegate;
        self.delegate = self;
        [self setButtonTitles:[NSMutableArray arrayWithObjects:NSLocalizedStringFromTable(@"OK", @"RongCloudKit", nil), nil]];
        self.titleLabel.text = title;
        self.contentLabel.text = warning;
        [self setContainerView:[self createDemoView]];
    }
    return self;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(TITLE_PADDING_LEFT_RIGHT, TITLE_PADDING_TOP, TITLE_WIDTH, TITLE_HEIGHT)];
        [_titleLabel setText:NSLocalizedStringFromTable(@"EvaluateCustomerService", @"RongCloudKit", nil)];
        [_titleLabel setTextAlignment:NSTextAlignmentCenter];
    }
    return _titleLabel;
}

- (UILabel *)contentLabel {
    if (!_contentLabel) {
        _contentLabel = [[UILabel alloc] initWithFrame:CGRectMake(CONTENT_PADDING_LEFT_RIGHT, TITLE_PADDING_TOP + CONTENT_PADDING_TOP_BUTTOM + TITLE_HEIGHT, CONTENT_WIDTH, CONTENT_HEIGHT)];
        [_contentLabel setText:NSLocalizedStringFromTable(@"EvaluateCustomerService", @"RongCloudKit", nil)];
        [_contentLabel setTextAlignment:NSTextAlignmentCenter];
    }
    return _contentLabel;
}

- (UIView *)createDemoView
{
    UIView *demoView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CLIENT_VIEW_WIDTH, CLIENT_VIEW_HEIGHT)];
    
    if (self.titleLabel.text == nil) {
        CGRect frame = demoView.frame;
        frame.size.height -= (TITLE_HEIGHT + TITLE_PADDING_TOP);
        demoView.frame = frame;
        frame = self.contentLabel.frame;
        frame.origin.y -= (TITLE_HEIGHT + TITLE_PADDING_TOP);
        self.contentLabel.frame = frame;
    }
    [demoView addSubview:self.contentLabel];
    [demoView addSubview:self.titleLabel];
    return demoView;
}

#pragma mark - RCCustomIOSAlertViewDelegate
// Called when a button is clicked. The view will be automatically dismissed after this call returns
- (void)customIOS7dialogButtonTouchUpInside: (RCCustomIOSAlertView *)alertView clickedButtonAtIndex: (NSInteger)buttonIndex
{
    [self.csAlertViewDelegate willCSAlertViewDismiss:self];
    [alertView close];
}
@end
