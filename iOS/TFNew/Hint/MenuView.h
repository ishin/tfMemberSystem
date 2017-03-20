//
//  MenuView.h
//  bmwTrainingApp
//
//  Created by jack on 2/27/14.
//  Copyright (c) 2014 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void (^MenuViewButtonClickedBlock)(NSInteger index);


@interface MenuView : UIView
{
    UIButton *btn1;
    UIButton *btn2;
    UIButton *btn3;
    UIButton *btn4;
    UIButton *btn5;

    
    MenuViewButtonClickedBlock _clickedBlock;
    
    int _maxWidth;
}
@property (nonatomic, strong) NSMutableArray *_btns;
@property (nonatomic, assign) int _maxWidth;


- (id) initWithFrame_Menu:(CGRect)frame;
- (void) setMenuClickedBlock:(MenuViewButtonClickedBlock)block;

@end
