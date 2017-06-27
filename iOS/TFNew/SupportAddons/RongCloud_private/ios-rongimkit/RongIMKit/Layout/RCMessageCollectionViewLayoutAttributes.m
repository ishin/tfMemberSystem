//
//  TLCollectionViewLayoutAttributes.m
//  UICollectionView-Spring-Demo
//
//  Created by MiaoGuangfa on 3/18/15.
//  Copyright (c) 2015 Teehan+Lax. All rights reserved.
//

#import "RCMessageCollectionViewLayoutAttributes.h"

@implementation RCMessageCollectionViewLayoutAttributes

- (void)setMessageBubbleFont:(UIFont *)messageBubbleFont
{
    _messageBubbleFont = messageBubbleFont;
}
- (void)setMsgTimeLabelSize:(CGSize)msgTimeLabelSize
{
    _msgTimeLabelSize = msgTimeLabelSize;
}
- (void)setNickNameLabelSize:(CGSize)nickNameLabelSize
{
    _nickNameLabelSize = nickNameLabelSize;
}
- (void)setPortraitImageViewSize:(CGSize)portraitImageViewSize
{
    _portraitImageViewSize = portraitImageViewSize;
}
- (void)setBubbleBackgroundViewSize:(CGSize)bubbleBackgroundViewSize
{
    _bubbleBackgroundViewSize = bubbleBackgroundViewSize;
}

-(id)init
{
    self = [super init];
    if (self) {
        self.messageBubbleFont        = [UIFont systemFontOfSize:16];
        self.nickNameLabelSize        = CGSizeMake(100, 20);
        self.portraitImageViewSize    = CGSizeMake(60, 60);
        self.msgTimeLabelSize         = CGSizeMake(120, 15);
        self.bubbleBackgroundViewSize = CGSizeMake(200, 0);
    }
    return self;
}

#pragma mark - NSCopying

- (instancetype)copyWithZone:(NSZone *)zone
{
    RCMessageCollectionViewLayoutAttributes *copy = [super copyWithZone:zone];
    if (copy.representedElementCategory != UICollectionElementCategoryCell) {
        return copy;
    }
    
    copy.messageBubbleFont        = self.messageBubbleFont;
    copy.msgTimeLabelSize         = self.msgTimeLabelSize;
    copy.portraitImageViewSize    = self.portraitImageViewSize;
    copy.nickNameLabelSize        = self.nickNameLabelSize;
    copy.bubbleBackgroundViewSize = self.bubbleBackgroundViewSize;
    
    return copy;
}
#pragma mark - NSObject

- (BOOL)isEqual:(id)object
{
    if (self == object) {
        return YES;
    }
    
    if (![object isKindOfClass:[self class]]) {
        return NO;
    }
    
    if (self.representedElementCategory == UICollectionElementCategoryCell) {
        RCMessageCollectionViewLayoutAttributes *layoutAttributes = (RCMessageCollectionViewLayoutAttributes *)object;
        
        if (![layoutAttributes.messageBubbleFont isEqual:self.messageBubbleFont]
            || !CGSizeEqualToSize(layoutAttributes.msgTimeLabelSize, self.msgTimeLabelSize)
            || !CGSizeEqualToSize(layoutAttributes.portraitImageViewSize, self.portraitImageViewSize)
            || !CGSizeEqualToSize(layoutAttributes.nickNameLabelSize, self.nickNameLabelSize)
            || !CGSizeEqualToSize(layoutAttributes.bubbleBackgroundViewSize, self.bubbleBackgroundViewSize)) {
            return NO;
        }
    }
    
    return [super isEqual:object];
}

- (NSUInteger)hash
{
    return [self.indexPath hash];
}
@end
