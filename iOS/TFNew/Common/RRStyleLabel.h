//
//  RRStyleLabel.h
//  mac.
//
//  Created by mac on 12-11-29.
//  Copyright (c) 2012年 mac.. All rights reserved.
//





#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <CoreText/CoreText.h>
#import <QuartzCore/QuartzCore.h>

@interface RRStyleLabel : UIScrollView {
    
    CATextLayer *_textLayer;
    
    NSTextAlignment _textAlignment;
}

@property (nonatomic, strong) NSString *_text;



@property (nonatomic) NSTextAlignment textAlignment;  

@property (nonatomic, strong) NSArray *_textParams;

@property (nonatomic, readonly) NSMutableDictionary *_xMap;

- (void)drawTexts;

@end
