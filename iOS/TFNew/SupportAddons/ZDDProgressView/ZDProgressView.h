//
//  ZDProgressView.h
//  TestProject
//
//  Created by  LWB on 15/4/20.
//  Copyright (c) 2015年  LWB. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ZDProgressView : UIView

@property (nonatomic,strong) NSString *text;
@property (nonatomic,strong) UIFont *textFont;
@property (nonatomic,assign) CGFloat progress;
@property (nonatomic,assign) NSInteger cornerRadius;
@property (nonatomic,assign) NSInteger borderWidth;

@property (nonatomic,strong) UIColor *noColor;
@property (nonatomic,strong) UIColor *prsColor;


@end
