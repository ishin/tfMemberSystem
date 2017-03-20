//
//  PickerView.h
//  iMokard
//
//  Created by steven on 1/5/10.
//  Copyright 2009 Steven Sun. All rights reserved.
//


typedef void(^CustomDatePickerSelectionBlock)(NSDate* date);


@interface CustomDatePickerView : UIView  {
    
    CustomDatePickerSelectionBlock _selectionBlock;
}
@property (nonatomic, copy) CustomDatePickerSelectionBlock _selectionBlock;

- (void) showInView:(UIView*)view;

@end
