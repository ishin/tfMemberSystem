//
//  CircleProgressView.h
//  test
//
//  Created by jack on 06/11/13.
//  Copyright (c) 2013 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CircleProgressView : UIView
{
    float _progress;
    UILabel *pL;
}


- (void) setProgress:(float)progress;
- (void) updateOffest:(float)offset;

- (void) smallRefreshMode;
@end
