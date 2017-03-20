//
//  MyCodeViewController.h
//  Hint
//
//  Created by jack on 11/11/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "BaseViewController.h"

@interface MyCodeViewController : BaseViewController
{
    BOOL _isShootMyCodePresent;
}
@property (nonatomic, strong) User *_u;

@property (nonatomic, assign) BOOL _isShootMyCodePresent;
@property (nonatomic, strong) NSString * _noteMark;

@end
