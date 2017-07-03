//
//  RCCollectionCellAttributes.m
//  RongIMKit
//
//  Created by xugang on 3/18/15.
//  Copyright (c) 2015 RongCloud. All rights reserved.
//

#import "RCCollectionCellAttributes.h"

@implementation RCCollectionCellAttributes

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.messageBubbleFont          = [UIFont systemFontOfSize:16];
        self.nickNameLabelSize          = CGSizeMake(100, 20);
        self.portraitImageViewSize      = CGSizeMake(60, 60);
        self.msgTimeLabelSize           = CGSizeMake(120, 20);
        self.bubbleBackgroundViewSize   = CGSizeMake(200, 0);
    }
    return self;
}
//这个方法必须实现
- (id)copyWithZone:(NSZone *)zone
{
    RCCollectionCellAttributes *attributes = [[self class] allocWithZone:zone];
    attributes.msgTimeLabelSize = self.msgTimeLabelSize;
    attributes.nickNameLabelSize = self.nickNameLabelSize;
    attributes.portraitImageViewSize = self.portraitImageViewSize;
    attributes.bubbleBackgroundViewSize = self.bubbleBackgroundViewSize;
    attributes.messageBubbleFont = self.messageBubbleFont;
    
    return attributes;
}
@end
