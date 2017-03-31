//
//  KeyboardBar.h
//  iMokard
//
//  Created by steven on 1/1/10.
//  Copyright 2009 Steven Sun. All rights reserved.
//


@interface KeyboardBar : UIToolbar {
    
    
    
    SEL         didTapDone;
    SEL         didTapCancel;
}
@property (nonatomic, weak) id keyboardDelegate;
@property (nonatomic, weak) id rootObject_;


- (void) setDelegate:(id)de selector:(SEL)selector;
- (void) setDelegateWithCancel:(id)de selector:(SEL)selector;
- (void) setRootObject:(id)object;

- (id) getRootObject;


@end
