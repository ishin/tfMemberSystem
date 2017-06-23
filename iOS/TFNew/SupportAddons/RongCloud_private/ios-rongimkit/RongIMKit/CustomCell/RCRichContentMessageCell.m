//
//  RCRichContentMessageCell.m
//  RongIMKit
//
//  Created by xugang on 15/2/2.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCRichContentMessageCell.h"
#import "RCAttributedLabel.h"
#import "RCloudImageView.h"
#import "RCKitUtility.h"
#import "RCKitCommonDefine.h"
#import "RCIM.h"
#import "RCKitUtility.h"

@interface RCRichContentMessageCell ()

- (void)initialize;
- (void)tapBubbleBackgroundViewEvent:(UIGestureRecognizer *)gestureRecognizer;

@end

@implementation RCRichContentMessageCell

+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
  referenceExtraHeight:(CGFloat)extraHeight {
  RCRichContentMessage *richContentMsg = (RCRichContentMessage *)model.content;
  CGSize _titleLabelSize = [RCKitUtility getTextDrawingSize:richContentMsg.title font:[UIFont systemFontOfSize:RichContent_Title_Font_Size] constrainedSize:CGSizeMake((int)(collectionViewWidth * 0.637)-24, MAXFLOAT)];
  CGFloat __messagecontentview_height = 17 + _titleLabelSize.height + 10 + 45;
  
  __messagecontentview_height += extraHeight;
  
  return CGSizeMake(collectionViewWidth, __messagecontentview_height);
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self initialize];
    }

    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self initialize];
    }

    return self;
}

#define RICH_CONTENT_TITLE_PADDING_TOP 10.5
#define RICH_CONTENT_TITLE_CONTENT_PADDING 7.5
#define RICH_CONTENT_PADDING_LEFT 12
#define RICH_CONTENT_PADDING_RIGHT 19.5
#define RICH_CONTENT_PADDING_BOTTOM 12
#define RICH_CONTENT_THUMBNAIL_CONTENT_PADDING 6

- (void)initialize {
    self.bubbleBackgroundView = [[UIImageView alloc] initWithFrame:CGRectZero];

    UITapGestureRecognizer *bubbleBackgroundViewTap =
        [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapBubbleBackgroundViewEvent:)];
    UILongPressGestureRecognizer *contentViewLongPress =
    [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressed:)];
    
    bubbleBackgroundViewTap.numberOfTapsRequired = 1;
    bubbleBackgroundViewTap.numberOfTouchesRequired = 1;

    [self.messageContentView addGestureRecognizer:bubbleBackgroundViewTap];
    [self.messageContentView addGestureRecognizer:contentViewLongPress];
    self.contentView.userInteractionEnabled = YES;

    UIImage *bundleImage = [RCKitUtility imageNamed:@"rc_richcontentmsg_placeholder" ofBundle:@"RongCloud.bundle"];

    self.richContentImageView = [[RCloudImageView alloc] initWithPlaceholderImage:bundleImage];
    self.richContentImageView.layer.cornerRadius = 5.0f;
    self.richContentImageView.layer.masksToBounds = YES;
    self.richContentImageView.contentMode=UIViewContentModeScaleAspectFill;
    self.richContentImageView.frame = CGRectMake(0, 0, RICH_CONTENT_THUMBNAIL_WIDTH, RICH_CONTENT_THUMBNAIL_HIGHT);

    self.titleLabel = [[RCAttributedLabel alloc] init];
    [self.titleLabel setFont:[UIFont systemFontOfSize:RichContent_Title_Font_Size]];
    [self.titleLabel setNumberOfLines:2];
  

    self.digestLabel = [[RCAttributedLabel alloc] init];
    [self.digestLabel setFont:[UIFont systemFontOfSize:RichContent_Message_Font_Size]];
    [self.digestLabel setNumberOfLines:3];
    self.digestLabel.textColor = HEXCOLOR(0x888888);

    self.bubbleBackgroundView.layer.cornerRadius = 4;
    self.bubbleBackgroundView.layer.masksToBounds = YES;
    [self.bubbleBackgroundView addSubview:self.titleLabel];
    [self.bubbleBackgroundView addSubview:self.richContentImageView];
    [self.bubbleBackgroundView addSubview:self.digestLabel];
    [self.messageContentView addSubview:self.bubbleBackgroundView];
}
- (void)tapMessage:(UIGestureRecognizer *)gestureRecognizer {
    if ([self.delegate respondsToSelector:@selector(didTapMessageCell:)]) {
        [self.delegate didTapMessageCell:self.model];
    }
}
#pragma mark - override, configure RichContentMessage Cell
- (void)setDataModel:(RCMessageModel *)model {
    [super setDataModel:model];
    RCRichContentMessage *richContentMsg = (RCRichContentMessage *)model.content;

    self.titleLabel.text = richContentMsg.title;
    self.digestLabel.text = richContentMsg.digest;

    CGSize richContentThumbImageFrame = CGSizeMake(RICH_CONTENT_THUMBNAIL_WIDTH, RICH_CONTENT_THUMBNAIL_HIGHT);
    CGRect messageContentViewRect = self.messageContentView.frame;
    messageContentViewRect.size.width = (int)(self.baseContentView.bounds.size.width * 0.637) + 7;
    CGSize _titleLabelSize = [RCKitUtility getTextDrawingSize:richContentMsg.title font:[UIFont systemFontOfSize:RichContent_Title_Font_Size] constrainedSize:CGSizeMake(messageContentViewRect.size.width - RICH_CONTENT_PADDING_LEFT - RICH_CONTENT_PADDING_RIGHT, MAXFLOAT)];

  CGSize _digestLabelSize = [RCKitUtility getTextDrawingSize:richContentMsg.digest font:[UIFont systemFontOfSize:RichContent_Message_Font_Size] constrainedSize:CGSizeMake(messageContentViewRect.size.width - richContentThumbImageFrame.width - RICH_CONTENT_PADDING_LEFT-6- RICH_CONTENT_THUMBNAIL_CONTENT_PADDING - RICH_CONTENT_PADDING_RIGHT, MAXFLOAT)];
  if (_titleLabelSize.height > 36) {
    _titleLabelSize.height = 36;
  }
  if (_digestLabelSize.height > RICH_CONTENT_THUMBNAIL_HIGHT) {
    _digestLabelSize.height = RICH_CONTENT_THUMBNAIL_HIGHT;
  }
  
  messageContentViewRect.size.height = RICH_CONTENT_TITLE_PADDING_TOP + _titleLabelSize.height +
                                         RICH_CONTENT_TITLE_CONTENT_PADDING + RICH_CONTENT_THUMBNAIL_HIGHT +
                                         RICH_CONTENT_PADDING_BOTTOM-3;
  if (MessageDirection_RECEIVE == self.messageDirection) {
    self.messageContentView.frame = messageContentViewRect;
    [self.richContentImageView setImageURL:[NSURL URLWithString:richContentMsg.imageURL]];
    self.bubbleBackgroundView.frame =
    CGRectMake(0, 0, self.messageContentView.frame.size.width,self.messageContentView.frame.size.height);
    self.bubbleBackgroundView.image = [RCKitUtility imageNamed:@"chat_from_bg_normal" ofBundle:@"RongCloud.bundle"];
    self.titleLabel.frame =
    CGRectMake(RICH_CONTENT_PADDING_LEFT+7, RICH_CONTENT_TITLE_PADDING_TOP,
               self.messageContentView.frame.size.width - RICH_CONTENT_PADDING_LEFT - RICH_CONTENT_PADDING_RIGHT+1,
               _titleLabelSize.height);
    self.digestLabel.frame = CGRectMake(RICH_CONTENT_PADDING_LEFT+7, CGRectGetMaxY(self.titleLabel.frame)+RICH_CONTENT_TITLE_CONTENT_PADDING, messageContentViewRect.size.width-6-RICH_CONTENT_PADDING_LEFT-RICH_CONTENT_PADDING_RIGHT-RICH_CONTENT_THUMBNAIL_WIDTH-RICH_CONTENT_THUMBNAIL_CONTENT_PADDING, _digestLabelSize.height);
    self.richContentImageView.frame =
    CGRectMake(CGRectGetMaxX(self.bubbleBackgroundView.frame)-12.5-45,
               CGRectGetMinY(self.digestLabel.frame)-2,
               richContentThumbImageFrame.width, richContentThumbImageFrame.height);
    UIImage *image = self.bubbleBackgroundView.image;
    self.bubbleBackgroundView.image = [self.bubbleBackgroundView.image
                                       resizableImageWithCapInsets:UIEdgeInsetsMake(image.size.height * 0.8, image.size.width * 0.8,image.size.height * 0.2, image.size.width * 0.2)];
  }else{
    messageContentViewRect.origin.x = self.baseContentView.bounds.size.width -
    (messageContentViewRect.size.width + 10 + [RCIM sharedRCIM].globalMessagePortraitSize.width + HeadAndContentSpacing);
    self.messageContentView.frame = messageContentViewRect;
    
    [self.richContentImageView setImageURL:[NSURL URLWithString:richContentMsg.imageURL]];
    self.bubbleBackgroundView.frame =
    CGRectMake(0, 0, self.messageContentView.frame.size.width,self.messageContentView.frame.size.height);
    self.bubbleBackgroundView.image = [RCKitUtility imageNamed:@"chat_to_bg_normal" ofBundle:@"RongCloud.bundle"];
    self.titleLabel.frame =
    CGRectMake(RICH_CONTENT_PADDING_LEFT, RICH_CONTENT_TITLE_PADDING_TOP,
               self.messageContentView.frame.size.width - RICH_CONTENT_PADDING_LEFT - RICH_CONTENT_PADDING_RIGHT,
               _titleLabelSize.height);
    self.digestLabel.frame = CGRectMake(RICH_CONTENT_PADDING_LEFT, CGRectGetMaxY(self.titleLabel.frame)+RICH_CONTENT_TITLE_CONTENT_PADDING, messageContentViewRect.size.width-RICH_CONTENT_PADDING_LEFT-6-RICH_CONTENT_PADDING_RIGHT-RICH_CONTENT_THUMBNAIL_WIDTH-RICH_CONTENT_THUMBNAIL_CONTENT_PADDING, _digestLabelSize.height);
    self.richContentImageView.frame =
    CGRectMake(CGRectGetMaxX(self.bubbleBackgroundView.frame)-7-12.5-45,
                CGRectGetMinY(self.digestLabel.frame)-2,
               richContentThumbImageFrame.width, richContentThumbImageFrame.height);
    UIImage *image = self.bubbleBackgroundView.image;
    self.bubbleBackgroundView.image = [self.bubbleBackgroundView.image
                                       resizableImageWithCapInsets:UIEdgeInsetsMake(image.size.height * 0.8, image.size.width * 0.2,image.size.height * 0.2, image.size.width * 0.8)];
    CGRect statusFrame = self.statusContentView.frame;
    statusFrame.origin.x = statusFrame.origin.x +5;
    [self.statusContentView setFrame:statusFrame];
  }
    //NSLog(@"bound width is %f", self.bubbleBackgroundView.frame.size.width);
}

#pragma mark - cell tap event, open the related URL
- (void)tapBubbleBackgroundViewEvent:(UIGestureRecognizer *)gestureRecognizer {
    if (gestureRecognizer.state == UIGestureRecognizerStateEnded) {
        // to do something.
        RCRichContentMessage *richContentMsg = (RCRichContentMessage *)self.model.content;
        DebugLog(@"%s, URL > %@", __FUNCTION__, richContentMsg.imageURL);
        if (nil != richContentMsg.url) {
            if ([self.delegate respondsToSelector:@selector(didTapUrlInMessageCell:model:)]) {
                [self.delegate didTapUrlInMessageCell:richContentMsg.url model:self.model];
                return;
            }
        } else if (nil != richContentMsg.imageURL) {
            if ([self.delegate respondsToSelector:@selector(didTapUrlInMessageCell:model:)]) {
                [self.delegate didTapUrlInMessageCell:richContentMsg.imageURL model:self.model];
                return;
            }
        }
        
        if ([self.delegate respondsToSelector:@selector(didTapMessageCell:)]) {
            [self.delegate didTapMessageCell:self.model];
        }
    }
}

- (void)longPressed:(id)sender {
    UILongPressGestureRecognizer *press = (UILongPressGestureRecognizer *)sender;
    if (press.state == UIGestureRecognizerStateEnded) {
        DebugLog(@"long press end");
        return;
    } else if (press.state == UIGestureRecognizerStateBegan) {
        [self.delegate didLongTouchMessageCell:self.model inView:self.bubbleBackgroundView];
    }
}
@end
