//
//  WaitDialog.h
//  
//
//  Created by steven on 4/8/09.
//  Copyright 2009 steven. All rights reserved.
//


@interface WaitDialog : UIView {
    
   

}

+ (WaitDialog *)sharedDialog;
+ (WaitDialog *)sharedAlertDialog;

- (void) setTitle:(NSString *)title;

- (void) startLoading;
- (void) endLoading;

- (void) animateShow;

@end

