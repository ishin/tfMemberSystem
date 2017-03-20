//
//  MenuView.m
//  bmwTrainingApp
//
//  Created by jack on 2/27/14.
//  Copyright (c) 2014 jack. All rights reserved.
//

#import "MenuView.h"
#import "UIButton+Color.h"

@implementation MenuView
@synthesize _btns;
@synthesize _maxWidth;


- (void) dealloc
{
    
    [_btns removeAllObjects];
    
   
}

- (id) initWithFrame_Menu:(CGRect)frame{
    
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        //144*170

//        UIImageView *iconUp = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"menu_arrow_up.png"]];
//        [self addSubview:iconUp];
//       
        
        UIImageView *mbg = [[UIImageView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH-149-2, 56, 149, 97)];
        //mbg.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.8];
        [self addSubview:mbg];
        mbg.userInteractionEnabled = YES;
        mbg.image = [UIImage imageNamed:@"box.png"];
      
        
        self.backgroundColor = [UIColor clearColor];
        
        float cw = 128;
        float ch = 45;
        
        //iconUp.center = CGPointMake(SCREEN_WIDTH - 30, 58+6);
        

        btn1 = [UIButton buttonWithColor:nil selColor:LINE_COLOR];
        btn1.frame = CGRectMake(10, 10, cw, ch-4);
        btn1.backgroundColor = [UIColor clearColor];
        [mbg addSubview:btn1];
        btn1.layer.cornerRadius = 3;
        btn1.clipsToBounds = YES;
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(btn1.frame)-3,
                                                                  cw-20, 1)];
        line.backgroundColor = [UIColor clearColor];
        [mbg addSubview:line];
     
        
        
        btn2 = [UIButton buttonWithColor:nil selColor:LINE_COLOR];
        btn2.frame = CGRectMake(10, CGRectGetMaxY(line.frame), cw, ch-10);
        btn2.backgroundColor = [UIColor clearColor];
        [mbg addSubview:btn2];
        btn2.layer.cornerRadius = 3;
        btn2.clipsToBounds = YES;

//        line = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(btn2.frame)+1,
//                                                         cw-20, 1)];
//        line.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.3];
//        [mbg addSubview:line];
//        
//        btn3 = [UIButton buttonWithColor:nil selColor:LINE_COLOR];
//        btn3.frame = CGRectMake(0, CGRectGetMaxY(line.frame)+1, cw, ch-2);
//        btn3.backgroundColor = [UIColor clearColor];
//        [mbg addSubview:btn3];
    
        UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_m_friends.png"]];
        [btn1 addSubview:icon];
        icon.center = CGPointMake(25, 22);
        icon.tag = 100;
       
        UILabel *tL1 = [[UILabel alloc] initWithFrame:CGRectMake(45, 12, cw-50, 20)];
        tL1.backgroundColor = [UIColor clearColor];
        tL1.textColor = COLOR_TEXT_A;
        tL1.font = [UIFont systemFontOfSize:13];
        [btn1 addSubview:tL1];
        tL1.text = @"发起群聊";
       
        icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon_m_group.png"]];
        [btn2 addSubview:icon];
        icon.center = CGPointMake(25, 20);
        icon.tag = 100;
        
        UILabel *tL2 = [[UILabel alloc] initWithFrame:CGRectMake(45, 6, cw-50, 20)];
        tL2.backgroundColor = [UIColor clearColor];
        tL2.textColor = COLOR_TEXT_A;
        tL2.font = [UIFont systemFontOfSize:13];
        [btn2 addSubview:tL2];
        tL2.text = @"添加常用";
        
        
        
//        icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"iconfont_groupchat.png"]];
//        [btn3 addSubview:icon];
//        icon.center = CGPointMake(15, 20);
//        icon.tag = 100;
//        
//        UILabel *tL3 = [[UILabel alloc] initWithFrame:CGRectMake(35, 10, cw-50, 20)];
//        tL3.backgroundColor = [UIColor clearColor];
//        tL3.textColor = [UIColor whiteColor];
//        tL3.font = [UIFont systemFontOfSize:13];
//        [btn3 addSubview:tL3];
//        tL3.text = @"创建群聊";
    
    
        
        self._btns = [NSMutableArray array];
        [_btns addObject:btn1];
        [_btns addObject:btn2];
        //[_btns addObject:btn3];
        
        
        btn1.tag = 0;
        btn2.tag = 1;
       // btn3.tag = 2;
    
        
        [btn1 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        [btn2 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        [btn3 addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapped:)];
        tap.cancelsTouchesInView = NO;
        [self addGestureRecognizer:tap];
    }
    return self;
}


- (void) tapped:(id)sender{
    
    [UIView animateWithDuration:0.25
                     animations:^{
                         self.alpha = 0.0;
                     } completion:^(BOOL finished) {
                         [self removeFromSuperview];
                     }];
}

- (void) setMenuClickedBlock:(MenuViewButtonClickedBlock)block{
    
    _clickedBlock = [block copy];
}


- (void) buttonAction:(UIButton*)btn{
    
    
    [self removeFromSuperview];
    
    if(_clickedBlock)
    {
        _clickedBlock(btn.tag);
    }
    
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
