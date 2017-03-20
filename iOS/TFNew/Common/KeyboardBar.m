//
//  KeyboardBar.m
//  iMokard
//
//  Created by steven on 1/1/10.
//  Copyright 2009 Steven Sun. All rights reserved.
//

#import "KeyboardBar.h"
#import "UIButton+Color.h"

@implementation KeyboardBar
@synthesize keyboardDelegate;
@synthesize rootObject_;


- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
		
		self.alpha = 0.9;
		
		UIBarButtonItem *space = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
        
        UIButton *btnDone = [UIButton buttonWithColor:THEME_COLOR selColor:nil];
        btnDone.frame = CGRectMake(0, 0, 60, 30);
        [btnDone setTitle:LAN(@"Done") forState:UIControlStateNormal];
        [btnDone addTarget:self action:@selector(doneAction:) forControlEvents:UIControlEventTouchUpInside];
        btnDone.clipsToBounds = YES;
        btnDone.layer.cornerRadius = 5;
        btnDone.titleLabel.font = [UIFont systemFontOfSize:13];
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithCustomView:btnDone];
        
        UIButton *btnCancel = [UIButton buttonWithColor:THEME_COLOR selColor:nil];
        btnCancel.frame = CGRectMake(0, 0, 60, 30);
        [btnCancel setTitle:LAN(@"Cancel") forState:UIControlStateNormal];
        [btnCancel addTarget:self action:@selector(cancelAction:) forControlEvents:UIControlEventTouchUpInside];
        btnCancel.clipsToBounds = YES;
        btnCancel.layer.cornerRadius = 5;
        btnCancel.titleLabel.font = [UIFont systemFontOfSize:13];
        UIBarButtonItem *canelButton = [[UIBarButtonItem alloc] initWithCustomView:btnCancel];
		self.items = [NSArray arrayWithObjects:canelButton, space, doneButton, nil];
		
        
    }
    return self;
}

- (void) setRootObject:(id)object {
    self.rootObject_ = object;
}

- (id) getRootObject{
	return self.rootObject_;
}
- (void) setDelegate:(id)de selector:(SEL)selector {
    self.keyboardDelegate = de;
    didTapDone = selector;
}

- (void) setDelegateWithCancel:(id)de selector:(SEL)selector{
    self.keyboardDelegate = de;
    didTapCancel = selector;
}

- (void)doneAction:(id) send {
    if (rootObject_ != nil) {
		[rootObject_ resignFirstResponder];
        
        
		if (keyboardDelegate && didTapDone) {
			//[keyboardDelegate performSelector:didTapDone withObject:nil];
            
            IMP imp = [keyboardDelegate methodForSelector:didTapDone];
            void (*func)(id, SEL) = (void *)imp;
            func(keyboardDelegate, didTapDone);
            
		}
    } else {
		if (keyboardDelegate && didTapDone) {
			
            IMP imp = [keyboardDelegate methodForSelector:didTapDone];
            void (*func)(id, SEL) = (void *)imp;
            func(keyboardDelegate, didTapDone);
            
		}
	}
	
	if(rootObject_ == nil && keyboardDelegate == nil){
		[[NSNotificationCenter defaultCenter] postNotificationName:UIKeyboardWillHideNotification object:nil];
	}
    
}

- (void) cancelAction:(id)sender{
    if (rootObject_ != nil) {
		[rootObject_ resignFirstResponder];
        
        
		if (keyboardDelegate && didTapCancel) {
			IMP imp = [keyboardDelegate methodForSelector:didTapDone];
            void (*func)(id, SEL) = (void *)imp;
            func(keyboardDelegate, didTapCancel);
		}
    } else {
		if (keyboardDelegate && didTapCancel) {
			IMP imp = [keyboardDelegate methodForSelector:didTapDone];
            void (*func)(id, SEL) = (void *)imp;
            func(keyboardDelegate, didTapCancel);
		}
	}
	
	if(rootObject_ == nil && keyboardDelegate == nil){
		[[NSNotificationCenter defaultCenter] postNotificationName:UIKeyboardWillHideNotification object:nil];
	}
    
}



- (void)dealloc {
    
}


@end
