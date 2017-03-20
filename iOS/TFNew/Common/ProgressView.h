//
//  ProgressView.h
//  Hint
//
//  Created by jack on 11/25/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProgressView : UIView
{
    UIImageView *bk;
    UIImageView *front;
    
    UILabel *percent;
}

- (void) updateProgress:(float)value;

- (void) setProgressColor:(UIColor*)color;
- (void) setProgressHilightColor:(UIColor*)color;


@end
