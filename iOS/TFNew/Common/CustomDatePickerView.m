//
//  CustomDatePickerView.m
//  iMokard
//
//  Created by steven on 1/5/10.
//  Copyright 2009 Steven Sun. All rights reserved.
//

#import "CustomDatePickerView.h"
#import "UIButton+Color.h"


@interface CustomDatePickerView ()
{
    UIDatePicker    *_myPickerView;
    UIView          *_pickerContainer;
    
    
    BOOL            _isShowing;
    
}

@end

@implementation CustomDatePickerView
@synthesize _selectionBlock;


- (id)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0];
        
        _pickerContainer = [[UIView alloc] initWithFrame:CGRectMake(0, frame.size.height,
                                                                    frame.size.width, 260)];
        _pickerContainer.backgroundColor = [UIColor whiteColor];
        [self addSubview:_pickerContainer];
        
        _myPickerView = [[UIDatePicker alloc] initWithFrame:CGRectMake(0.0, 0, frame.size.width, 210)];
        _myPickerView.datePickerMode = UIDatePickerModeDate;
        [_pickerContainer addSubview:_myPickerView];
        
        
        UIButton *btnOK = [UIButton buttonWithColor:THEME_COLOR selColor:nil];
        btnOK.frame = CGRectMake(15, 210+5, SCREEN_WIDTH-30, 40);
        [_pickerContainer addSubview:btnOK];
        btnOK.layer.cornerRadius = 3;
        btnOK.clipsToBounds = YES;
        [btnOK setTitle:@"确定" forState:UIControlStateNormal];
        btnOK.titleLabel.font = [UIFont boldSystemFontOfSize:16];
        [btnOK addTarget:self action:@selector(doneAction:) forControlEvents:UIControlEventTouchUpInside];
        
        
    }
    return self;
}
- (void) doneAction:(id)sender{
    
    _isShowing = NO;
    
    if(_selectionBlock)
    {
        _selectionBlock(_myPickerView.date);
    }
    
    [UIView animateWithDuration:0.35
                     animations:^{
                         
                         _pickerContainer.frame = CGRectMake(0.0, self.frame.size.height,
                                                             self.frame.size.width, 260);
                         
                         self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0];
                         
                     } completion:^(BOOL finished) {
                         
                         [self removeFromSuperview];
                     }];
}

- (void)cancelButtonAction:(id)sender
{
    [UIView animateWithDuration:0.35
                     animations:^{
                         
                         _pickerContainer.frame = CGRectMake(0.0, self.frame.size.height,
                                                             self.frame.size.width, 260);
                         
                         self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0];
                         
                     } completion:^(BOOL finished) {
                         
                         [self removeFromSuperview];
                     }];

}
- (void) showInView:(UIView*)view{
    
    _isShowing = !_isShowing;
    
    if(_isShowing)
    {
        [view addSubview:self];
        
        [UIView animateWithDuration:0.35
                         animations:^{
                             
                             _pickerContainer.frame = CGRectMake(0.0, self.frame.size.height-260,
                                                                 self.frame.size.width, 260);
                             
                             self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
                             
                             
                         } completion:^(BOOL finished) {
                             
                             
                         }];
    }
    else
    {
        [UIView animateWithDuration:0.35
                         animations:^{
                             
                             _pickerContainer.frame = CGRectMake(0.0, self.frame.size.height,
                                                                 self.frame.size.width, 260);
                             
                             self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0];
                             
                         } completion:^(BOOL finished) {
                             
                             [self removeFromSuperview];
                         }];
    }
    
    
}



- (void)dealloc {
  
    
   
}


@end
