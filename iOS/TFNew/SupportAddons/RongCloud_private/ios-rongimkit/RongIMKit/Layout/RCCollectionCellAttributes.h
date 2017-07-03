//
//  RCCollectionCellAttributes.h
//  RongIMKit
//
//  Created by xugang on 3/18/15.
//  Copyright (c) 2015 RongCloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#define MAX_CELL_WIDTH 200
@interface RCCollectionCellAttributes : NSObject<NSCopying>

@property (nonatomic, assign) CGSize msgTimeLabelSize;
@property (nonatomic, assign) CGSize nickNameLabelSize;
@property (nonatomic, assign) CGSize portraitImageViewSize;
@property (nonatomic, assign) CGSize bubbleBackgroundViewSize;
@property (nonatomic, strong) UIFont *messageBubbleFont;
@end
