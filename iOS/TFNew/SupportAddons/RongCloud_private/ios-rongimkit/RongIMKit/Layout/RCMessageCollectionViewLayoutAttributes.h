//
//  TLCollectionViewLayoutAttributes.h
//  UICollectionView-Spring-Demo
//
//  Created by MiaoGuangfa on 3/18/15.
//  Copyright (c) 2015 Teehan+Lax. All rights reserved.
//

#import <UIKit/UIKit.h>
//RCMessageCollectionViewLayoutAttributes
@interface RCMessageCollectionViewLayoutAttributes : UICollectionViewLayoutAttributes <NSCopying>

@property (nonatomic, strong) UIFont *messageBubbleFont;
@property (nonatomic, assign) CGSize msgTimeLabelSize;
@property (nonatomic, assign) CGSize nickNameLabelSize;
@property (nonatomic, assign) CGSize portraitImageViewSize;
@property (nonatomic, assign) CGSize bubbleBackgroundViewSize;


@end
