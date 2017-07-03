//
//  RCMessageBubbleTipView.m
//  RCIM
//
//  Created by Heq.Shinoda on 14-6-20.
//  Copyright (c) 2014年 Heq.Shinoda. All rights reserved.
//

#import "RCMessageBubbleTipView.h"
#import <QuartzCore/QuartzCore.h>
#import "RCKitCommonDefine.h"

#define kDefaultbubbleTipTextColor [UIColor whiteColor]
#define kDefaultbubbleTipBackgroundColor [UIColor redColor]
#define kDefaultOverlayColor [UIColor colorWithWhite:1.0f alpha:0.3]

#define kDefaultbubbleTipTextFont [UIFont systemFontOfSize:[UIFont smallSystemFontSize]]

#define kDefaultbubbleTipShadowColor [UIColor clearColor]

#define kbubbleTipStrokeColor [UIColor whiteColor]
#define kbubbleTipStrokeWidth 0.0f

#define kMarginToDrawInside (kbubbleTipStrokeWidth * 2)

#define kShadowOffset CGSizeMake(0.0f, 3.0f)
#define kShadowOpacity 0.2f
#define kShadowColor [UIColor colorWithWhite:0.0f alpha:kShadowOpacity]
#define kShadowRadius 1.0f

#define kbubbleTipHeight 18.0f
#define kbubbleTipTextSideMargin 6.0f

#define kbubbleTipCornerRadius 10.0f

#define kDefaultbubbleTipAlignment RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_RIGHT

@interface RCMessageBubbleTipView ()

- (void)_init;
@property(NS_NONATOMIC_IOSONLY, readonly) CGSize sizeOfTextForCurrentSettings;

@end

@implementation RCMessageBubbleTipView

@synthesize bubbleTipAlignment = _bubbleTipAlignment;

@synthesize bubbleTipPositionAdjustment = _bubbleTipPositionAdjustment;
@synthesize frameToPositionInRelationWith = _frameToPositionInRelationWith;

@synthesize bubbleTipText = _bubbleTipText;
@synthesize bubbleTipTextColor = _bubbleTipTextColor;
@synthesize bubbleTipTextShadowColor = _bubbleTipTextShadowColor;
@synthesize bubbleTipTextShadowOffset = _bubbleTipTextShadowOffset;
@synthesize bubbleTipTextFont = _bubbleTipTextFont;
@synthesize bubbleTipBackgroundColor = _bubbleTipBackgroundColor;
@synthesize bubbleTipOverlayColor = _bubbleTipOverlayColor;

- (void)awakeFromNib {
    [super awakeFromNib];

    [self _init];
}

- (instancetype)initWithFrame:(CGRect)frame {
    if ((self = [super initWithFrame:frame])) {
        [self _init];
    }

    return self;
}

- (instancetype)initWithParentView:(UIView *)parentView alignment:(RCMessageBubbleTipViewAlignment)alignment {
    if ((self = [self initWithFrame:CGRectZero])) {
        self.bubbleTipAlignment = alignment;
        [parentView addSubview:self];
        //[self setCenter:CGPointMake(parentView.frame.origin.x + parentView.frame.size.width,
        //parentView.frame.origin.y)];
    }

    return self;
}

- (void)_init {
    self.backgroundColor = [UIColor clearColor];

    self.bubbleTipAlignment = kDefaultbubbleTipAlignment;

    self.bubbleTipBackgroundColor = kDefaultbubbleTipBackgroundColor;
    _bubbleTipOverlayColor = kDefaultOverlayColor;
    self.bubbleTipTextColor = kDefaultbubbleTipTextColor;
    self.bubbleTipTextShadowColor = kDefaultbubbleTipShadowColor;
    self.bubbleTipTextFont = kDefaultbubbleTipTextFont;
}

#pragma mark - Layout

- (void)layoutSubviews {
    [super layoutSubviews];
    // return;
    CGRect newFrame = self.frame;
    CGRect superviewFrame =
        CGRectIsEmpty(_frameToPositionInRelationWith) ? self.superview.frame : _frameToPositionInRelationWith;

    CGFloat textWidth = [self sizeOfTextForCurrentSettings].width;

    CGFloat viewWidth = textWidth + kbubbleTipTextSideMargin + (kMarginToDrawInside * 2);
    CGFloat viewHeight = kbubbleTipHeight + (kMarginToDrawInside * 2);
    //viewWidth = viewWidth;

    
    CGFloat superviewWidth = superviewFrame.size.width;
    CGFloat superviewHeight = superviewFrame.size.height;

    
     if (self.isShowNotificationNumber) {
         newFrame.size.width = viewWidth;
         newFrame.size.height = viewHeight;
     }else{
         newFrame.size.width = 10;
         newFrame.size.height = 10;
         viewHeight = 14;
         viewWidth = 10;
     }
    switch (self.bubbleTipAlignment) {
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_LEFT:
        newFrame.origin.x = -viewWidth / 2.0f;
        newFrame.origin.y = -viewHeight / 2.0f;
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_RIGHT:
        newFrame.origin.y = -viewHeight / 2.0f +3;
          newFrame.origin.x = superviewWidth - viewWidth +6;
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_CENTER:
        newFrame.origin.x = (superviewWidth - viewWidth) / 2.0f;
        newFrame.origin.y = -viewHeight / 2.0f;
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_CENTER_LEFT:
        newFrame.origin.x = -viewWidth / 2.0f;
        newFrame.origin.y = (superviewHeight - viewHeight) / 2.0f;
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_CENTER_RIGHT:
        newFrame.origin.x = superviewWidth - (viewWidth / 2.0f);
        newFrame.origin.y = (superviewHeight - viewHeight) / 2.0f;
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_BOTTOM_LEFT:
        newFrame.origin.x = -textWidth / 2.0f;
        newFrame.origin.y = superviewHeight - (viewHeight / 2.0f);
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_BOTTOM_RIGHT:
        newFrame.origin.x = superviewWidth - (viewWidth / 2.0f);
        newFrame.origin.y = superviewHeight - (viewHeight / 2.0f);
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_BOTTOM_CENTER:
        newFrame.origin.x = (superviewWidth - viewWidth) / 2.0f;
        newFrame.origin.y = superviewHeight - (viewHeight / 2.0f);
        break;
    case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_CENTER:
        newFrame.origin.x = (superviewWidth - viewWidth) / 2.0f;
        newFrame.origin.y = (superviewHeight - viewHeight) / 2.0f;
        break;
    default:
        NSAssert(NO, @"Unimplemented JSbubbleTipAligment type %d", (int)self.bubbleTipAlignment);
    }

    newFrame.origin.x += _bubbleTipPositionAdjustment.x;
    newFrame.origin.y += _bubbleTipPositionAdjustment.y;

    self.frame = CGRectIntegral(newFrame);

    [self setNeedsDisplay];
}

#pragma mark - Private

- (CGSize)sizeOfTextForCurrentSettings {
    CGSize __size;
    if (RC_IOS_SYSTEM_VERSION_LESS_THAN(@"7.0"))
    {
        __size = [self.bubbleTipText sizeWithFont:kDefaultbubbleTipTextFont];
    }else
    {
        __size = [self.bubbleTipText sizeWithAttributes:@{NSFontAttributeName : self.bubbleTipTextFont}];
    }
    if (self.bubbleTipText.length == 1) {
        __size.width = 12;
        //__size.height = 13;
    }
    if (self.bubbleTipText.length == 2) {
        __size.width = 18;
        //__size.height = 13;
    }
    if (self.bubbleTipText.length == 3) {
        __size.width = 18;
        // __size.height = 13;
    }

    return CGSizeMake(ceilf(__size.width), ceilf(__size.height));
}

#pragma mark - Setters

- (void)setBubbleTipAlignment:(RCMessageBubbleTipViewAlignment)bubbleTipAlignment {
    if (bubbleTipAlignment != _bubbleTipAlignment) {
        _bubbleTipAlignment = bubbleTipAlignment;

        switch (bubbleTipAlignment) {
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_LEFT:
            self.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleRightMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_RIGHT:
            self.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleLeftMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_TOP_CENTER:
            self.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleLeftMargin |
                                    UIViewAutoresizingFlexibleRightMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_CENTER_LEFT:
            self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin |
                                    UIViewAutoresizingFlexibleRightMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_CENTER_RIGHT:
            self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin |
                                    UIViewAutoresizingFlexibleLeftMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_BOTTOM_LEFT:
            self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleRightMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_BOTTOM_RIGHT:
            self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleLeftMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_BOTTOM_CENTER:
            self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleLeftMargin |
                                    UIViewAutoresizingFlexibleRightMargin;
            break;
        case RC_MESSAGE_BUBBLE_TIP_VIEW_ALIGNMENT_CENTER:
            self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleLeftMargin |
                                    UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleBottomMargin;
            break;
        default:
            NSAssert(NO, @"Unimplemented JSbubbleTipAligment type %d", (int)self.bubbleTipAlignment);
        }

        [self setNeedsLayout];
    }
}

- (void)setBubbleTipPositionAdjustment:(CGPoint)bubbleTipPositionAdjustment {
    _bubbleTipPositionAdjustment = bubbleTipPositionAdjustment;

    [self setNeedsLayout];
}

- (void)setBubbleTipText:(NSString *)bubbleTipText {
    [self setHidden:NO];
    _bubbleTipText = [bubbleTipText copy];
    [self layoutSubviews];
    [self setNeedsDisplay];

    //    if (bubbleTipText != _bubbleTipText)
    //    {
    //
    //    }
}

- (void)setBubbleTipNumber:(int)msgCount {
    if (msgCount < 100 && msgCount > 0) {
        if(self.isShowNotificationNumber)
            [self setBubbleTipText:[NSString stringWithFormat:@"%d", msgCount]];
        else
            [self setBubbleTipText:@" "];
    } else if (msgCount >= 100) {
        if(self.isShowNotificationNumber)
            [self setBubbleTipText:@"···"];
        else
            [self setBubbleTipText:@" "];
    } else {
        [self setHidden:YES];
    }
}

- (void)setBubbleTipTextColor:(UIColor *)bubbleTipTextColor {
    if (bubbleTipTextColor != _bubbleTipTextColor) {
        _bubbleTipTextColor = bubbleTipTextColor;

        [self setNeedsDisplay];
    }
}

- (void)setBubbleTipTextShadowColor:(UIColor *)bubbleTipTextShadowColor {
    if (bubbleTipTextShadowColor != _bubbleTipTextShadowColor) {
        _bubbleTipTextShadowColor = bubbleTipTextShadowColor;

        [self setNeedsDisplay];
    }
}

- (void)setBubbleTipTextShadowOffset:(CGSize)bubbleTipTextShadowOffset {
    _bubbleTipTextShadowOffset = bubbleTipTextShadowOffset;

    [self setNeedsDisplay];
}

- (void)setBubbleTipTextFont:(UIFont *)bubbleTipTextFont {
    if (bubbleTipTextFont != _bubbleTipTextFont) {
        _bubbleTipTextFont = bubbleTipTextFont;

        [self setNeedsDisplay];
    }
}

- (void)setBubbleTipBackgroundColor:(UIColor *)bubbleTipBackgroundColor {
    if (bubbleTipBackgroundColor != _bubbleTipBackgroundColor) {
        _bubbleTipBackgroundColor = bubbleTipBackgroundColor;

        [self setNeedsDisplay];
    }
}

#pragma mark - Drawing

- (void)drawRect:(CGRect)rect {
    BOOL anyTextToDraw = (self.bubbleTipText.length > 0);
    
    if(!self.isShowNotificationNumber)
        [self setBubbleTipText:@" "];

    if (anyTextToDraw) {
        CGContextRef ctx = UIGraphicsGetCurrentContext();

        CGRect rectToDraw = CGRectInset(rect, kMarginToDrawInside, kMarginToDrawInside);

        UIBezierPath *borderPath =
            [UIBezierPath bezierPathWithRoundedRect:rectToDraw
                                  byRoundingCorners:(UIRectCorner)UIRectCornerAllCorners
                                        cornerRadii:CGSizeMake(kbubbleTipCornerRadius, kbubbleTipCornerRadius)];

        /* Background and shadow */
        CGContextSaveGState(ctx);
        {
            CGContextAddPath(ctx, borderPath.CGPath);

            CGContextSetFillColorWithColor(ctx, self.bubbleTipBackgroundColor.CGColor);
            // CGContextSetShadowWithColor(ctx, kShadowOffset, kShadowRadius, kShadowColor.CGColor);

            CGContextDrawPath(ctx, kCGPathFill);
        }
        CGContextRestoreGState(ctx);

        /* Stroke */
        CGContextSaveGState(ctx);
        {
            CGContextAddPath(ctx, borderPath.CGPath);

            CGContextSetLineWidth(ctx, kbubbleTipStrokeWidth);
            CGContextSetStrokeColorWithColor(ctx, kbubbleTipStrokeColor.CGColor);

            CGContextDrawPath(ctx, kCGPathStroke);
        }
        CGContextRestoreGState(ctx);

        /* Text */
        CGContextSaveGState(ctx);
        {
            CGContextSetFillColorWithColor(ctx, self.bubbleTipTextColor.CGColor);
            CGContextSetShadowWithColor(ctx, self.bubbleTipTextShadowOffset, 1.0,
                                        self.bubbleTipTextShadowColor.CGColor);

            CGRect textFrame = rectToDraw;
            CGSize textSize = [self sizeOfTextForCurrentSettings];

            textFrame.size.height = textSize.height;
            textFrame.origin.y = rectToDraw.origin.y + ceilf((rectToDraw.size.height - textFrame.size.height) / 2.0f);
            if(RC_IOS_SYSTEM_VERSION_LESS_THAN(@"7.0"))
            {
                [self.bubbleTipText drawInRect:textFrame
                                      withFont:self.bubbleTipTextFont
                                 lineBreakMode:NSLineBreakByCharWrapping
                                     alignment:NSTextAlignmentCenter];
            }
            else
            {
                NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
                paragraphStyle.lineBreakMode = NSLineBreakByCharWrapping;
                paragraphStyle.alignment = NSTextAlignmentCenter;
                
                [self.bubbleTipText drawInRect:textFrame
                                withAttributes:@{
                                                 NSFontAttributeName : self.bubbleTipTextFont,
                                                 NSForegroundColorAttributeName : kDefaultbubbleTipTextColor,
                                                 NSParagraphStyleAttributeName : paragraphStyle
                                                 }];
            }

        }
        CGContextRestoreGState(ctx);
    }
}
@end
