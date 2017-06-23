//
//  RCAdminEvaluationView.h
//  RongIMKit
//
//  Created by litao on 16/2/22.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RCCustomIOSAlertView.h"

@class RCAdminEvaluationView;

@protocol RCAdminEvaluationViewDelegate <NSObject>

- (void)adminEvaluateViewCancel:(RCAdminEvaluationView *)view;
- (void)adminEvaluateView:(RCAdminEvaluationView *)view didEvaluateValue:(int)starValues;

@end

@interface RCAdminEvaluationView : RCCustomIOSAlertView
- (instancetype)initWithDelegate:(id<RCAdminEvaluationViewDelegate>) delegate;
@property (nonatomic)BOOL quitAfterEvaluation;
@property (nonatomic, strong)NSString *dialogId;
@end

