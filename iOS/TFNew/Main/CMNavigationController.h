//
//  CMNavigationController.h
//  CMTabBarController
//
//  Created by mac on 13-8-13.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UINavigationItem (CustomTitle)

- (void) setTitle:(NSString *)title;

@end


@interface CMNavigationController : UINavigationController <UIGestureRecognizerDelegate> {
    
    
    UIView          *_backgroundView;
    UIImageView     *_topImageView;
    UIImageView     *_bottomImageView;
}
@property (nonatomic, strong) UIBarButtonItem *_backBarButtonItem;
@property (nonatomic, strong) NSMutableArray  *_captureArray;
@end
