//
//  SearchContactResultView.h
//  Hint
//
//  Created by jack on 6/14/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SearchContactResultView : UIView
{
    
}
@property (nonatomic, strong) NSArray *_results;
@property (nonatomic, strong) NSDictionary *_mapSelect;
@property (nonatomic, weak) UIViewController *_ctrl;
@property (nonatomic, assign) BOOL _isChooseModel;

- (void) refreshData;


@end
