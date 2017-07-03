//
//  TLSpringFlowLayout.h
//  UICollectionView-Spring-Demo
//
//  Created by Ash Furrow on 2013-07-31.
//  Copyright (c) 2013 Teehan+Lax. All rights reserved.
//
//RCMessageCollectionViewFlowLayout

#if !__has_feature(objc_modules)
    // Objective-C Modules are recommended over traditional import statements for numerous benefits
    #import <UIKit/UIKit.h>

#else
    @import UIKit;
    #import <RongIMLib/RongIMLib.h>
#endif

#ifndef __IPHONE_7_0
    #error TLSpringFlowLayout requires APIs only available in iOS SDK 7.0 and later
#endif


/// The default resistance factor that determines the bounce of the collection. Default is 900.0f.
#define kScrollResistanceFactorDefault 900.0f;
#define MAX_CELL_WIDTH 200

/// A UICollectionViewFlowLayout subclass that, when implemented, creates a dynamic / bouncing scroll effect for UICollectionViews.
@interface RCMessageCollectionViewFlowLayout : UICollectionViewFlowLayout


/// The scrolling resistance factor determines how much bounce / resistance the collection has. A higher number is less bouncy, a lower number is more bouncy. The default is 900.0f.
@property (nonatomic, assign) CGFloat scrollResistanceFactor;

/// The dynamic animator used to animate the collection's bounce
@property (nonatomic, strong, readonly) UIDynamicAnimator *dynamicAnimator;

/**
 *  RongCloud Module
 */
@property (nonatomic, strong) RCMessageContent* messageContent;

@property (nonatomic, strong) UIFont *messageBubbleFont;
@property (nonatomic, assign) CGSize msgTimeLabelSize;
@property (nonatomic, assign) CGSize nickNameLabelSize;
@property (nonatomic, assign) CGSize portraitImageViewSize;
@property (nonatomic, assign) CGSize bubbleBackgroundViewSize;
@property (nonatomic, assign) CGFloat yOfItem;

- (CGSize) sizeForItemAtIndexPath:(NSIndexPath *)indexPath;
- (CGSize) messageBubbleSizeForItemAtIndexPath:(NSIndexPath *)indexPath;
@end
