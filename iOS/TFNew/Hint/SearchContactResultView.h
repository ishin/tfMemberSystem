//
//  SearchContactResultView.h
//  Hint
//
//  Created by jack on 6/14/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SearchContactDelegate <NSObject>

@optional
- (void) didContactSelected:(id)user;
- (void) didScroll;
- (void) didCancelSearch;

@end

@interface SearchContactResultView : UIView
{
    
}
@property (nonatomic, strong) NSArray *_results;
@property (nonatomic, strong) NSDictionary *_mapSelect;
@property (nonatomic, weak) UIViewController *_ctrl;
@property (nonatomic, assign) BOOL _isChooseModel;
@property (nonatomic, assign) BOOL _isForwardSearch;
@property (nonatomic, weak) id <SearchContactDelegate> delegate;

- (void) refreshData;


@end
