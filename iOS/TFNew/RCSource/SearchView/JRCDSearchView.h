//
//  JRCDSearchView.h
//  RCloudMessage
//
//  Created by 张改红 on 16/9/18.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>


@protocol JRCDSearchViewDelegate <NSObject>
- (void)onSearchCancelClick;
@end

@interface JRCDSearchView : UIView

@property (nonatomic,weak) UINavigationController* _naviCtrl;
@property (nonatomic,weak) id<JRCDSearchViewDelegate> delegate;

- (void) searchGroupWithKeywords:(NSString*) searchText;
- (void) searchFriendAndGroupWithKeywords:(NSString*) searchText;
- (void) searchFrinedWithKeywords:(NSString*) searchText;

@end
