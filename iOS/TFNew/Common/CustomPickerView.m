//
//  CustomPickerView.m
//  iMokard
//
//  Created by steven on 1/5/10.
//  Copyright 2009 Steven Sun. All rights reserved.
//

#import "CustomPickerView.h"


@interface CustomPickerView ()
{
    UIPickerView    *_myPickerView;
    UIView          *_pickerContainer;
    
    NSMutableDictionary *_values;
    
    BOOL            _isShowing;
    
    NSInteger       _comSelected;
    NSInteger       _rowSelected;
}

@end

@implementation CustomPickerView
@synthesize _pickerDataArray;
@synthesize delegate_;
@synthesize _selectionBlock;

@synthesize _unitString;

- (id)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        // Initialization code
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0];
        
        _pickerContainer = [[UIView alloc] initWithFrame:CGRectMake(0, frame.size.height,
                                                                    frame.size.width, 260)];
        _pickerContainer.backgroundColor = [UIColor whiteColor];
        [self addSubview:_pickerContainer];
        
        _myPickerView = [[UIPickerView alloc] initWithFrame:CGRectMake(0.0, 50, frame.size.width, 210)];
        _myPickerView.delegate = self;
        _myPickerView.dataSource = self;
         _myPickerView.showsSelectionIndicator = YES;
        [_pickerContainer addSubview:_myPickerView];
        
        UIToolbar *toolBar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, 44)];
        UIBarButtonItem *space = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
        
//        UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone
//                                                                                    target:self
//                                                                                    action:@selector(doneAction:)];
        UIBarButtonItem *doneButton = [[UIBarButtonItem alloc]initWithTitle:@"完成" style:UIBarButtonItemStyleBordered target:self action:@selector(doneAction:)];
        
        UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc]initWithTitle:@"取消" style:UIBarButtonItemStyleBordered target:self action:@selector(cancelButtonAction:)];
        toolBar.items = [NSArray arrayWithObjects: cancelButton,space, doneButton, nil];
        [_pickerContainer addSubview:toolBar];
        
        
        _values = [[NSMutableDictionary alloc] init];
        
    }
    return self;
}
- (void) doneAction:(id)sender{
    
    if(_selectionBlock)
    {
        _selectionBlock(_values);
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




- (void)selectRow:(NSInteger)row inComponent:(NSInteger)component {
	
    _comSelected = component;
    _rowSelected = row;
    
    
    NSDictionary *section = [_pickerDataArray objectAtIndex:component];
    NSArray *values = [section objectForKey:@"values"];
    
    [_values setObject:[values objectAtIndex:row]
                forKey:[NSNumber numberWithInteger:component]];
    
    [_myPickerView selectRow:row inComponent:component animated:YES];
}


#pragma mark -
#pragma mark PickerView delegate methods
#pragma mark -
#pragma mark PickerView delegate methods

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    _rowSelected = row;
    
    NSDictionary *section = [_pickerDataArray objectAtIndex:component];
    NSArray *values = [section objectForKey:@"values"];
    
    [_values setObject:[values objectAtIndex:row]
                forKey:[NSNumber numberWithInteger:component]];
}

- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component
{
    NSDictionary *section = [_pickerDataArray objectAtIndex:component];
    
    return [[section objectForKey:@"width"] floatValue];
}

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
{
    return 50.0;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    
    NSDictionary *section = [_pickerDataArray objectAtIndex:component];
    NSArray *values = [section objectForKey:@"values"];
    
    return [values count];
}

- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view{
    
    NSDictionary *section = [_pickerDataArray objectAtIndex:component];
    
    CGFloat componentWidth = [[section objectForKey:@"width"] floatValue];
    
    NSArray *values = [section objectForKey:@"values"];

    UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, componentWidth, 50)];
    tL.backgroundColor = [UIColor clearColor];
    
    id valueRow = [values objectAtIndex:row];
    if([valueRow isKindOfClass:[NSString class]])
    {
        tL.text = valueRow;
    }
    else if([valueRow isKindOfClass:[NSDictionary class]])
    {
        tL.text = [valueRow objectForKey:@"title"];
    }
    
    tL.textAlignment = NSTextAlignmentCenter;
    tL.textColor = [UIColor blackColor];
    tL.font = [UIFont systemFontOfSize:18];
    
    return tL;
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return [_pickerDataArray count];
}

- (void)dealloc {
  
    
   
}


@end
