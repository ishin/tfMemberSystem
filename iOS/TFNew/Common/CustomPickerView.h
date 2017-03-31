//
//  PickerView.h
//  iMokard
//
//  Created by steven on 1/5/10.
//  Copyright 2009 Steven Sun. All rights reserved.
//

@protocol CustomPickerViewDelegate <NSObject>



@end

typedef void(^CustomPickerSelectionBlock)(NSDictionary* values);


@interface CustomPickerView : UIView <UIPickerViewDelegate, UIPickerViewDataSource> {
    
    CustomPickerSelectionBlock _selectionBlock;
}
@property (nonatomic, copy) CustomPickerSelectionBlock _selectionBlock;

@property (nonatomic, strong) NSArray *_pickerDataArray;
@property (nonatomic, weak) id  <CustomPickerViewDelegate> delegate_;

@property (nonatomic, strong) NSString *_unitString;

- (void)selectRow:(NSInteger)row inComponent:(NSInteger)component;

- (void) showInView:(UIView*)view;

@end
