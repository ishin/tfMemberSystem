//
//  UILabel+ContentSize.m
//  hkeeping
//
//  Created by jack on 2/24/14.
//  Copyright (c) 2015 G-Wearable Inc. All rights reserved..
//

#import "UILabel+ContentSize.h"

@implementation UILabel (ContentSize)


- (CGSize)contentSize {
    
    CGSize contentSize = CGSizeZero;
    if(0)
    {
        self.numberOfLines = 0;
        
        NSMutableParagraphStyle * paragraphStyle = [[NSMutableParagraphStyle alloc] init];
        paragraphStyle.lineBreakMode = self.lineBreakMode;
        paragraphStyle.alignment = self.textAlignment;
        NSDictionary * attributes = @{NSFontAttributeName : self.font,
                                  NSParagraphStyleAttributeName : paragraphStyle};
        contentSize = [self.text boundingRectWithSize:self.frame.size
                                                 options:(NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading)
                                              attributes:attributes
                                                 context:nil].size;
    
      
        CGRect tc = self.frame;
        tc.size.height = contentSize.height;
   
        self.frame = tc;

    }
    else
    {
        self.numberOfLines = 0;
        CGRect rect = CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width, 10000);
        CGRect sizeToFit= [self textRectForBounds:rect limitedToNumberOfLines:self.numberOfLines];
        if(sizeToFit.size.height<0)
            sizeToFit.size.height=0;
        if (sizeToFit.size.width<0) {
            sizeToFit.size.width=0;
        }
        
        sizeToFit.size.height += 5;
        
        CGRect tc = self.frame;
        tc.size.height = sizeToFit.size.height;
        
        self.frame = tc;
        
        contentSize = tc.size;
    }
    
        
    return contentSize;
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
