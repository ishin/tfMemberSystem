//
//  TLSpringFlowLayout.m
//  UICollectionView-Spring-Demo
//
//  Created by Ash Furrow on 2013-07-31.
//  Copyright (c) 2013 Teehan+Lax. All rights reserved.
//

#import "RCMessageCollectionViewFlowLayout.h"
#import "RCMessageCollectionViewLayoutAttributes.h"

@interface RCMessageCollectionViewFlowLayout ()

//RongCloud Module
@property (nonatomic, strong) NSMutableArray *layoutAttributesArray;

- (void)configureLayoutAttributes:(RCMessageCollectionViewLayoutAttributes *)layoutAttributes;
@end

@implementation RCMessageCollectionViewFlowLayout

- (instancetype)init {
    self = [super init];
    if (self){
        [self setup];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self){
        [self setup];
    }
    return self;
}

- (void)setup {
    self.minimumLineSpacing      = 0.0f;
    self.sectionInset            = UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f);
    self.scrollDirection         = UICollectionViewScrollDirectionVertical;
    
    self.messageBubbleFont        = [UIFont systemFontOfSize:16];
    self.nickNameLabelSize        = CGSizeMake(100, 20);
    self.portraitImageViewSize    = CGSizeMake(60, 60);
    self.msgTimeLabelSize         = CGSizeMake(120, 15);
    self.bubbleBackgroundViewSize = CGSizeMake(200, 0);
    
    self.layoutAttributesArray = [NSMutableArray new];
    
    self.yOfItem = 0.0f;
}
+ (Class)layoutAttributesClass
{
    return [RCMessageCollectionViewLayoutAttributes class];
}
- (CGSize)sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    //the width of cell must less than the width of collection view minus the left and right value of section inset.
    CGFloat _width = CGRectGetWidth(self.collectionView.frame) - self.sectionInset.left - self.sectionInset.right;
    
    //height
    CGSize _msgBubbleSize = [self messageBubbleSizeForItemAtIndexPath:indexPath];
    
    RCMessageCollectionViewLayoutAttributes *attributes = (RCMessageCollectionViewLayoutAttributes *)[self layoutAttributesForItemAtIndexPath:indexPath];
    
    CGFloat _height = _msgBubbleSize.height;
    _height += attributes.nickNameLabelSize.height;
    _height += attributes.msgTimeLabelSize.height;
    _height += 30;
    
    attributes.frame = CGRectMake(0, self.yOfItem, _width, _height);
    [self.layoutAttributesArray addObject:attributes];
    
    self.yOfItem += _height;
    
    
    return CGSizeMake(_width, _height);
}
- (CGSize)messageBubbleSizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    CGSize _bubbleContentSize = CGSizeZero;

    if ([self.messageContent isKindOfClass:[RCTextMessage class]]) {
        
        RCTextMessage *_textMessage = (RCTextMessage *)self.messageContent;
#if __IPHONE_OS_VERSION_MAX_ALLOWED < __IPHONE_7_0
        
        _bubbleContentSize = [_textMessageContent sizeWithFont:self.messageBubbleFont constrainedToSize:CGSizeMake(MAX_CELL_WIDTH, MAXFLOAT) lineBreakMode:NSLineBreakByWordWrapping];
#else
        CGSize _textMessageSize = [_textMessage.content boundingRectWithSize:CGSizeMake(MAX_CELL_WIDTH, MAXFLOAT)
                                                                options:(NSStringDrawingTruncatesLastVisibleLine|NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading)
                                                             attributes:@{ NSFontAttributeName : self.messageBubbleFont }
                                                                context:nil].size;
        
        //NSLog(@"_textMessage.content & frame > %@, %@ ", _textMessage.content, NSStringFromCGSize(_textMessageSize));
        
        
        
        //背景图的最小宽度
        CGFloat minBubbleWidth  = _textMessageSize.width + 20 < 100 ? 100 : _textMessageSize.width + 20;
        CGFloat minBubbleHeight = _textMessageSize.height + 10 < 35 ? 35 : _textMessageSize.height + 10;
        
        //reset the bubbleContentSize by messageContentSize.
//        CGFloat w_margin = 50;
//        CGFloat h_margin = 0;
        _bubbleContentSize = CGSizeMake(minBubbleWidth, minBubbleHeight);
        
#endif
        
    }else if ([self.messageContent isKindOfClass:[RCImageMessage class]])
    {
        RCImageMessage *_imageMessage = (RCImageMessage *)self.messageContent;
        
        
        // resize the thumbnail image for shown
        CGSize originalThumbnailImageSize = _imageMessage.thumbnailImage.size;
        
        CGFloat width_thumbnailImage  = 120;
        CGFloat height_thumbnailImage = 120;
        
        if (originalThumbnailImageSize.width > width_thumbnailImage || originalThumbnailImageSize.height > height_thumbnailImage) {
            width_thumbnailImage  = originalThumbnailImageSize.width/2.0f;
            height_thumbnailImage = originalThumbnailImageSize.height/2.0f;
        }else
        {
            width_thumbnailImage  = originalThumbnailImageSize.width;
            height_thumbnailImage = originalThumbnailImageSize.height;
        }
        
        _bubbleContentSize = CGSizeMake(width_thumbnailImage, height_thumbnailImage);
        
    }else if ([self.messageContent isKindOfClass:[RCVoiceMessage class]])
    {
        _bubbleContentSize = CGSizeMake(100, 50);
        
    }else if ([self.messageContent isKindOfClass:[RCLocationMessage class]])
    {
        RCLocationMessage *_locationMessage = (RCLocationMessage *)self.messageContent;
        
        CGSize _locationNameSize = CGSizeZero;
#if __IPHONE_OS_VERSION_MAX_ALLOWED < __IPHONE_7_0
        
        _locationNameSize = [_locationMessage.locationName sizeWithFont:self.messageBubbleFont constrainedToSize:CGSizeMake(MAX_CELL_WIDTH, MAXFLOAT) lineBreakMode:NSLineBreakByWordWrapping];
#else
        _locationNameSize = [_locationMessage.locationName boundingRectWithSize:CGSizeMake(MAX_CELL_WIDTH, MAXFLOAT)
                                                                        options:(NSStringDrawingUsesLineFragmentOrigin| NSStringDrawingUsesFontLeading)
                                                                     attributes:@{ NSFontAttributeName : self.messageBubbleFont }
                                                                        context:nil].size;

#endif
        CGSize _locationImageSize = CGSizeMake(360/2.0f,207/2.0f);
        
        _bubbleContentSize = CGSizeMake(_locationImageSize.width, _locationImageSize.height);
        
    }else if ([self.messageContent isKindOfClass:[RCRichContentMessage class]])
    {
        DebugLog(@"[RongIMKit]: skip RCRichContentMessage ");
    }
    //DebugLog(@"_size_textMessageContent > %@", NSStringFromCGSize(_bubbleContentSize));
    
    self.bubbleBackgroundViewSize = CGSizeMake(ceilf(_bubbleContentSize.width), ceilf(_bubbleContentSize.height));
    return self.bubbleBackgroundViewSize;
}
- (void)prepareLayout {
    //NSLog(@"%s", __FUNCTION__);
    [super prepareLayout];

}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect {
    //NSLog(@"%s", __FUNCTION__);
    
    NSMutableArray *allAttributes = [NSMutableArray arrayWithCapacity:self.layoutAttributesArray.count];
    [self.layoutAttributesArray enumerateObjectsUsingBlock:^(RCMessageCollectionViewLayoutAttributes *attributesItem, NSUInteger idx, BOOL *stop) {
        if(CGRectIntersectsRect(rect, attributesItem.frame)){
            [allAttributes addObject:attributesItem];
        }
    }];

    return allAttributes;
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)indexPath {
    //NSLog(@"%s", __FUNCTION__);
    
    RCMessageCollectionViewLayoutAttributes *customAttributes = (RCMessageCollectionViewLayoutAttributes *)[super layoutAttributesForItemAtIndexPath:indexPath];
    
    if (customAttributes.representedElementCategory == UICollectionElementCategoryCell) {
        [self configureLayoutAttributes:customAttributes];
    }
    
    return customAttributes;
}

- (void)configureLayoutAttributes:(RCMessageCollectionViewLayoutAttributes *)layoutAttributes
{
    layoutAttributes.bubbleBackgroundViewSize   = self.bubbleBackgroundViewSize;
    layoutAttributes.nickNameLabelSize          = self.nickNameLabelSize;
    layoutAttributes.portraitImageViewSize      = self.portraitImageViewSize;
    layoutAttributes.msgTimeLabelSize           = self.msgTimeLabelSize;
    layoutAttributes.messageBubbleFont          = self.messageBubbleFont;
}

- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds {
    
    CGRect oldBounds = self.collectionView.bounds;
    if (CGRectGetWidth(newBounds) != CGRectGetWidth(oldBounds)) {
        NSLog(@"%s, return > YES", __FUNCTION__);
        return YES;
    }
    //NSLog(@"%s, return > NO", __FUNCTION__);
    return NO;
}

- (void)prepareForCollectionViewUpdates:(NSArray *)updateItems {
    NSLog(@"%s", __FUNCTION__);
    [super prepareForCollectionViewUpdates:updateItems];
    
    [updateItems enumerateObjectsUsingBlock:^(UICollectionViewUpdateItem *updateItem, NSUInteger idx, BOOL *stop) {
        if (updateItem.updateAction == UICollectionUpdateActionInsert) {
            
            CGFloat collectionViewHeight = CGRectGetHeight(self.collectionView.bounds);
            
            RCMessageCollectionViewLayoutAttributes *attributes = [RCMessageCollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:updateItem.indexPathAfterUpdate];
            
            if (attributes.representedElementCategory == UICollectionElementCategoryCell) {
                [self configureLayoutAttributes:attributes];
            }
            
            attributes.frame = CGRectMake(0.0f,
                                          collectionViewHeight + CGRectGetHeight(attributes.frame),
                                          CGRectGetWidth(attributes.frame),
                                          CGRectGetHeight(attributes.frame));
        }
    }];
}

@end
