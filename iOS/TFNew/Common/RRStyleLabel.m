//
//  RRStyleLabel.m
//  mac.
//
//  Created by mac on 12-11-29.
//  Copyright (c) 2012年 mac.. All rights reserved.
//

#import "RRStyleLabel.h"



#pragma mark -
#pragma mark in use params & functions
@interface RRStyleLabel ()

@property (nonatomic, retain) CATextLayer *textLayer;

- (void)drawTexts;
- (CGRect)calculateTextLayerFrame;
- (NSMutableAttributedString*)combineAttributedStringWithParams:(NSArray*)txtParams;

@end



@implementation RRStyleLabel
@synthesize _text;
@synthesize textLayer = _textLayer;
@synthesize _textParams;
@dynamic textAlignment;
@synthesize _xMap;


- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = [UIColor clearColor];
        self.scrollEnabled = NO;
        _textAlignment = NSTextAlignmentLeft;
        
        _xMap = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void)dealloc 
{
    
}


- (NSString*)text
{
    return self._text;
}

- (void)setTextParams:(NSArray*)aparams
{
    self._textParams = aparams;
    
    [self drawTexts];
}

- (void)setTextAlignment:(NSTextAlignment)Alignment
{
    _textAlignment = Alignment;
    if(_textLayer)
    {
        [self drawTexts];
    }
}

- (NSTextAlignment)textAlignment
{
    return _textAlignment;
}





#pragma mark -
#pragma mark in use functions
- (void)drawTexts
{
    //先清空文字图层
    if(_textLayer && [_textLayer superlayer])
    {
        [_textLayer removeFromSuperlayer];
    }
    
    //创建文字串
    NSMutableAttributedString *showAttrString = [self combineAttributedStringWithParams:_textParams];
    if(!showAttrString) return;
    
    //创建文字图层
    CATextLayer *txtLayer = [CATextLayer layer];
    txtLayer.contentsScale = [UIScreen mainScreen].scale;
    txtLayer.string = showAttrString;  
    txtLayer.frame = [self calculateTextLayerFrame]; 
    self.textLayer = txtLayer;
    
    //添加图层
    [self.layer addSublayer:_textLayer]; 
}

- (CGRect)calculateTextLayerFrame
{
    //计算文字图层在本view上的显示区域
    if(!_textParams || [_textParams count] == 0) return CGRectZero;
    
    //计算所有文字段里边，最高需要的高度
    float maxH = 0.0;
    float maxW = 0.0;
    
    int tmpIndex = 0;
    for(NSDictionary *param in _textParams)
    {
        UIFont *font = (UIFont*)[param objectForKey:@"font"];
        NSString *txt = (NSString*)[param objectForKey:@"text"];
        
        //获取maxH
        float lineH = font.pointSize+2;
        if([[UIDevice currentDevice].systemVersion floatValue] >= 6.0)
        {
            lineH = font.lineHeight;
        }
        
        if(lineH > maxH)
        {
            maxH = lineH;
        }
        
        //获取maxW
        CGSize txtSize = [txt sizeWithFont:font constrainedToSize:CGSizeMake(2048, lineH)];
       
        [_xMap setObject:[NSNumber numberWithFloat:maxW] forKey:[NSNumber numberWithInt:tmpIndex]];
        tmpIndex++;
        
        maxW += txtSize.width;
        
        
    }
    
    //计算要显示的frame
    float sw = self.frame.size.width;
    float sh = self.frame.size.height;
    
    CGRect tlframe = CGRectZero;
    if(_textAlignment == NSTextAlignmentLeft)
    {
        tlframe = CGRectMake(0, (sh-maxH)/2, maxW, maxH);
    }
    else if(_textAlignment == NSTextAlignmentCenter)
    {
        tlframe = CGRectMake((sw-maxW)/2, (sh-maxH)/2, maxW, maxH);
    }
    else if(_textAlignment == NSTextAlignmentRight)
    {
        tlframe = CGRectMake(sw-maxW, (sh-maxH)/2, maxW, maxH);
    }

    int x = tlframe.origin.x;
    int y = tlframe.origin.y;
    int w = tlframe.size.width;
    int h = tlframe.size.height;
    
    return CGRectMake(x, y, w, h);
}

- (NSMutableAttributedString*)combineAttributedStringWithParams:(NSArray*)txtParams;
{
    if(!_textParams || [_textParams count] == 0) return nil;

    //组合整个要显示的文字串
    self._text = @"";
    for(NSDictionary *param in _textParams)
    {
        NSString *txt = (NSString*)[param objectForKey:@"text"];
        self._text = [self._text stringByAppendingString:txt];
    }
    
    
    //创建attriString
    NSMutableAttributedString *attriString = [[NSMutableAttributedString alloc] initWithString:self._text];
    
    //给attriString添加属性
    NSInteger location = 0;
    for(NSDictionary *param in _textParams)
    {
        NSString *txt = (NSString*)[param objectForKey:@"text"];
        NSInteger length = txt.length;
        
        //设定颜色
        UIColor *color = (UIColor*)[param objectForKey:@"color"];
        if(color)
        {
            [attriString addAttribute:(NSString *)kCTForegroundColorAttributeName  
                                value:(id)color.CGColor   
                                range:NSMakeRange(location, length)];
        }
        
        //设定字体
        UIFont *font   = (UIFont*)[param objectForKey:@"font"];
        if(font)
        {
            
            //CTFontRef v = CTFontCreateWithName((CFStringRef)font.fontName, font.pointSize, NULL);
            
            [attriString addAttribute:(NSString *)kCTFontAttributeName  
                                value:font
                                range:NSMakeRange(location, length)];
        }
                  
        
        
        location += length;
    }
    
    return attriString;
}


@end
