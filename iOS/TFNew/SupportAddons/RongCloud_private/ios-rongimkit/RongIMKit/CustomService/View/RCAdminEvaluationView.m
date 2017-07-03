//
//  RCAdminEvaluationView.m
//  RongIMKit
//
//  Created by litao on 16/2/22.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCAdminEvaluationView.h"
#import "RCKitUtility.h"
#define STAR_COUNT 5
#define CLIENT_VIEW_HEIGHT 120
#define CLIENT_VIEW_WIDTH 290
#define TITLE_PADDING_TOP 10
#define TITLE_PADDING_LEFT_RIGHT 10
#define TITLE_HEIGHT 40
#define TITLE_WIDTH (CLIENT_VIEW_WIDTH - TITLE_PADDING_LEFT_RIGHT - TITLE_PADDING_LEFT_RIGHT)
#define STAR_PADDING_LEFT_RIGHT 20
#define STARS_PADDING 10
#define STAR_WIDTH ((CLIENT_VIEW_WIDTH - STAR_PADDING_LEFT_RIGHT - STAR_PADDING_LEFT_RIGHT + STARS_PADDING)/STAR_COUNT - STARS_PADDING)
#define STAR_PADDING_TOP_BUTTOM 15
#define STAR_HEIGHT (CLIENT_VIEW_HEIGHT - TITLE_PADDING_TOP - TITLE_HEIGHT - STAR_PADDING_TOP_BUTTOM - STAR_PADDING_TOP_BUTTOM)

@interface RCAdminEvaluationView () <RCCustomIOSAlertViewDelegate>
@property (nonatomic, weak)id<RCAdminEvaluationViewDelegate> adminEvaluationViewDelegate;
@property (nonatomic)int starValue; //0-4
@property (nonatomic, strong)NSArray *starButtonArray;
@property (nonatomic, strong)UILabel *titleLabel;
@end


@implementation RCAdminEvaluationView
- (instancetype)initWithDelegate:(id<RCAdminEvaluationViewDelegate>) delegate {
    self = [super init];
    if (self) {
        self.adminEvaluationViewDelegate = delegate;
        self.starValue = -1;
        self.delegate = self;
        [self setButtonTitles:[NSMutableArray arrayWithObjects:NSLocalizedStringFromTable(@"Submit", @"RongCloudKit", nil), NSLocalizedStringFromTable(@"Cancel", @"RongCloudKit", nil), nil]];
        [self setContainerView:[self createDemoView]];
    }
    return self;
}

//- (void)layoutSubviews {
//    [super layoutSubviews];
//    [self addSubview:self.yesButton];
//    [self addSubview:self.noButton];
//}
//- (void)show {
//    [super show];
//    [self setNeedsLayout];
//   self.frame = CGRectMake(350, 300, 320, 191);
//}
- (UIButton *)starAtIndex:(int)index {
    UIButton *starButton = [[UIButton alloc] initWithFrame:CGRectMake(STAR_PADDING_LEFT_RIGHT + index * (STARS_PADDING + STAR_WIDTH), STAR_PADDING_TOP_BUTTOM + TITLE_PADDING_TOP + TITLE_HEIGHT, STAR_WIDTH, STAR_HEIGHT)];
    [starButton setTag:index];
    [starButton addTarget:self action:@selector(onStarButton:) forControlEvents:UIControlEventTouchDown];
    [starButton setBackgroundImage: [RCKitUtility imageNamed:(@"custom_service_star_selected")ofBundle:@"RongCloud.bundle"] forState:UIControlStateNormal];

    return starButton;
}
- (NSArray *)starButtonArray {
    if (!_starButtonArray) {
        NSMutableArray *mutableArray = [[NSMutableArray alloc] init];
        for (int i = 0; i < STAR_COUNT; i++) {
            [mutableArray addObject:[self starAtIndex:i]];
        }
        _starButtonArray = mutableArray;
        
    }
    return _starButtonArray;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(TITLE_PADDING_LEFT_RIGHT, TITLE_PADDING_TOP, TITLE_WIDTH, TITLE_HEIGHT)];
        [_titleLabel setText:NSLocalizedStringFromTable(@"Admin_Comment_Title", @"RongCloudKit", nil)];
        [_titleLabel setTextAlignment:NSTextAlignmentCenter];
    }
    return _titleLabel;
}

- (void)onStarButton:(id)sender {
    UIButton *touchedButton = (UIButton *)sender;
    self.starValue = (int)touchedButton.tag;
}

- (void)setStarValue:(int)starValue {
    _starValue = starValue;
    for (int i = 0; i < STAR_COUNT; i++) {
        UIButton *btn = self.starButtonArray[i];
        if (i <= starValue) {
            [btn setBackgroundImage: [RCKitUtility imageNamed:(@"custom_service_evaluation_star_hover")ofBundle:@"RongCloud.bundle"] forState:UIControlStateNormal];
        } else {
            [btn setBackgroundImage: [RCKitUtility imageNamed:(@"custom_service_evaluation_star")ofBundle:@"RongCloud.bundle"] forState:UIControlStateNormal];
        }
    }
}
- (UIView *)createDemoView
{
    UIView *demoView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CLIENT_VIEW_WIDTH, CLIENT_VIEW_HEIGHT)];
    
    for (int i = 0; i < STAR_COUNT; i++) {
        UIButton *btn = self.starButtonArray[i];
        [demoView addSubview:btn];
    }
    [demoView addSubview:self.titleLabel];
    return demoView;
}

#pragma mark - RCCustomIOSAlertViewDelegate
// Called when a button is clicked. The view will be automatically dismissed after this call returns
- (void)customIOS7dialogButtonTouchUpInside: (RCCustomIOSAlertView *)alertView clickedButtonAtIndex: (NSInteger)buttonIndex
{
    DebugLog(@"Delegate: Button at position %d is clicked on alertView %d.", (int)buttonIndex, (int)[alertView tag]);
    if (buttonIndex == 1) {
        [self.adminEvaluationViewDelegate adminEvaluateViewCancel:self];
    } else if (buttonIndex == 0) {
        [self.adminEvaluationViewDelegate adminEvaluateView:self didEvaluateValue:self.starValue];
    }
    [alertView close];
}
@end

