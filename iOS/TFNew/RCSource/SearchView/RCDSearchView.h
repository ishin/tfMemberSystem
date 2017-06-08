//
//  RCDSearchView.h
//  RCloudMessage
//
//  Created by 张改红 on 16/9/18.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RCDSearchResultModel;
@protocol RCDSearchViewDelegate <NSObject>
- (void)onSearchCancelClick;
- (void) didDragScroll;
@end

@interface RCDSearchView : UIView<UINavigationControllerDelegate>

@property (nonatomic,weak) id<RCDSearchViewDelegate> delegate;
@property (nonatomic, weak) UIViewController *_ctrl;

- (void) doSearchWithWord:(NSString*)searchText;

@end
