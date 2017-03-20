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
    UIBarButtonItem *_backBarButtonItem;
    
    NSMutableArray  *_captureArray; //io5->ios6做返回动画使用
    
    UIView          *_backgroundView;
    UIImageView     *_topImageView;
    UIImageView     *_bottomImageView;
}

@end
