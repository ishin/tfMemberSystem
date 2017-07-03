//
//  RCPublicServiceImgTxtMsgCell.m
//  RongIMKit
//
//  Created by litao on 15/4/15.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCPublicServiceImgTxtMsgCell.h"
#import "RCPublicServiceViewConstants.h"
#import "RCloudImageView.h"
#import <RongIMLib/RongIMLib.h>
#import "RCKitUtility.h"
#import "RCKitCommonDefine.h"

@interface RCPublicServiceImgTxtMsgCell ()
@property(nonatomic, strong) UILabel *titleLabel;
@property(nonatomic, strong) UILabel *dateLabel;
@property(nonatomic, strong) RCloudImageView *imageView;
@property(nonatomic, strong) UILabel *contentLabel;
@property(nonatomic, strong) UILabel *readallLabel;
@property(nonatomic, strong) UIView *container;
@property(nonatomic, strong) UIView *line;
@property(nonatomic, strong) UIImageView *arrow;
@end

@implementation RCPublicServiceImgTxtMsgCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
  referenceExtraHeight:(CGFloat)extraHeight {
  
  CGFloat yOffset = 0;
  CGRect frame;
  RCPublicServiceRichContentMessage *content = (RCPublicServiceRichContentMessage *)model.content;
  
  yOffset += RCPublicServiceSingleCellPaddingTop;
  frame = [RCPublicServiceImgTxtMsgCell getTitleFrame:content withWidth:collectionViewWidth];
  yOffset += frame.size.height;
  
  yOffset += RCPublicServiceSingleCellPadding1;
  frame = [RCPublicServiceImgTxtMsgCell getDateFrame:model withWidth:collectionViewWidth];
  yOffset += frame.size.height;
  
  yOffset += RCPublicServiceSingleCellPadding2;
  frame = [RCPublicServiceImgTxtMsgCell getImageFrameWithWidth:collectionViewWidth];
  yOffset += frame.size.height;
  
  yOffset += RCPublicServiceSingleCellPadding3;
  frame = [RCPublicServiceImgTxtMsgCell getContentFrame:content withWidth:collectionViewWidth];
  yOffset += frame.size.height;
  
  yOffset += RCPublicServiceSingleCellPadding4;
  NSString *readAll = NSLocalizedStringFromTable(@"ReadAll",@"RongCloudKit",nil);
  frame = [RCPublicServiceImgTxtMsgCell getReadAllFrame:readAll withWidth:collectionViewWidth];
  yOffset += frame.size.height;
  
  yOffset += RCPublicServiceSingleCellPaddingButtom;
  
  yOffset += 20;
  
  yOffset += extraHeight;
  
  return CGSizeMake(collectionViewWidth, yOffset);
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];

    if (self) {
    }

    return self;
}
- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [UILabel new];
        [self.container addSubview:_titleLabel];
    }
    return _titleLabel;
}
- (UIView *)line {
    if (!_line) {
        _line = [UILabel new];
        [self.container addSubview:_line];
    }
    return _line;
}
- (UIImageView *)arrow {
    if (!_arrow) {
        _arrow = [UIImageView new];
        [_arrow setImage:IMAGE_BY_NAMED(@"arrow")];
        [self.container addSubview:_arrow];
    }
    return _arrow;
}
- (UILabel *)dateLabel {
    if (!_dateLabel) {
        _dateLabel = [UILabel new];
        [self.container addSubview:_dateLabel];
    }
    return _dateLabel;
}
- (RCloudImageView *)imageView {
    if (!_imageView) {
        _imageView = [RCloudImageView new];
        [self.container addSubview:_imageView];
    }
    return _imageView;
}
- (UILabel *)contentLabel {
    if (!_contentLabel) {
        _contentLabel = [UILabel new];
        [self.container addSubview:_contentLabel];
    }
    return _contentLabel;
}
- (UILabel *)readallLabel {
    if (!_readallLabel) {
        _readallLabel = [UILabel new];
        [self.container addSubview:_readallLabel];
    }
    return _readallLabel;
}
- (UIView *)container {
    if (!_container) {
        _container = [UIView new];
        [self.baseContentView addSubview:_container];
    }
    return _container;
}
+ (CGRect)getTitleFrame:(RCPublicServiceRichContentMessage *)content withWidth:(CGFloat)width {
    width = width - RCPublicServiceSingleCellPaddingLeft - 10 - RCPublicServiceSingleCellPaddingRight;
    CGSize size = CGSizeMake(
        width - RCPublicServiceSingleCellContentPaddingLeft - RCPublicServiceSingleCellContentPaddingRight, 2000);
    CGSize labelsize = [RCKitUtility getTextDrawingSize:content.richConent.title font:[UIFont systemFontOfSize:RCPublicServiceSingleCellBigFont] constrainedSize:size];
    return CGRectMake(RCPublicServiceSingleCellContentPaddingLeft, -5, labelsize.width, labelsize.height);
}

+ (CGRect)getDateFrame:(RCMessageModel *)model withWidth:(CGFloat)width {
    width = width - RCPublicServiceSingleCellPaddingLeft - RCPublicServiceSingleCellPaddingRight;
    CGSize size = CGSizeMake(
        width - RCPublicServiceSingleCellContentPaddingLeft - RCPublicServiceSingleCellContentPaddingRight, 2000);

    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    // formatter.timeZone = [NSTimeZone timeZoneWithName:@"shanghai"];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:NSLocalizedStringFromTable(@"DateFormat",@"RongCloudKit",nil)];
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:model.sentTime / 1000];
    NSString *dateString = [formatter stringFromDate:date];
    CGSize labelsize = [RCKitUtility getTextDrawingSize:dateString font:[UIFont systemFontOfSize:RCPublicServiceSingleCellSmallFont] constrainedSize:size];
    return CGRectMake(RCPublicServiceSingleCellContentPaddingLeft, 0, labelsize.width, labelsize.height);
}

+ (CGRect)getImageFrameWithWidth:(CGFloat)width {
    width = width - RCPublicServiceSingleCellPaddingLeft - RCPublicServiceSingleCellPaddingRight;
    return CGRectMake(RCPublicServiceSingleCellContentPaddingLeft, 0,
                      width - RCPublicServiceSingleCellContentPaddingLeft -
                          RCPublicServiceSingleCellContentPaddingRight,
                      RCPublicServiceSingleCellImageHeight);
}

+ (CGRect)getContentFrame:(RCPublicServiceRichContentMessage *)content withWidth:(CGFloat)width {
    width = width - RCPublicServiceSingleCellPaddingLeft - RCPublicServiceSingleCellPaddingRight;
    CGSize size = CGSizeMake(
        width - RCPublicServiceSingleCellContentPaddingLeft - RCPublicServiceSingleCellContentPaddingRight, 2000);
    CGSize labelsize = [RCKitUtility getTextDrawingSize:content.richConent.digest font:[UIFont systemFontOfSize:RCPublicServiceSingleCellSmallFont] constrainedSize:size];    
    return CGRectMake(RCPublicServiceSingleCellContentPaddingLeft, 0, labelsize.width, labelsize.height + 10);
}

+ (CGRect)getReadAllFrame:(NSString *)content withWidth:(CGFloat)width {
    width = width - RCPublicServiceSingleCellPaddingLeft - RCPublicServiceSingleCellPaddingRight;
    CGSize size =
        CGSizeMake(width - RCPublicServiceSingleCellContentPaddingLeft - RCPublicServiceSingleCellPaddingRight, 2000);
    CGSize labelsize = [RCKitUtility getTextDrawingSize:content font:[UIFont systemFontOfSize:RCPublicServiceSingleCellSmallFont] constrainedSize:size];
    return CGRectMake(RCPublicServiceSingleCellContentPaddingLeft, 0, labelsize.width, labelsize.height + 10);
}

- (void)setDataModel:(RCMessageModel *)model {
    [super setDataModel:model];
    CGFloat yOffset = 0;
    RCPublicServiceRichContentMessage *content = (RCPublicServiceRichContentMessage *)model.content;

    yOffset += RCPublicServiceSingleCellPaddingTop;
    CGRect titleframe = [RCPublicServiceImgTxtMsgCell getTitleFrame:content withWidth:self.frame.size.width];
    titleframe.origin.y += yOffset;
    self.titleLabel.frame = titleframe;
    // UILabel *titleLabel = [[UILabel alloc] initWithFrame:titleframe];
    self.titleLabel.numberOfLines = 0;
    self.titleLabel.font = [UIFont systemFontOfSize:RCPublicServiceSingleCellBigFont];
    self.titleLabel.textColor = [UIColor blackColor];
    self.titleLabel.textAlignment = NSTextAlignmentLeft;
    self.titleLabel.text = content.richConent.title;
    yOffset += titleframe.size.height;

    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    // formatter.timeZone = [NSTimeZone timeZoneWithName:@"shanghai"];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:NSLocalizedStringFromTable(@"DateFormat",@"RongCloudKit",nil)];
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:model.sentTime / 1000];
    NSString *dateString = [formatter stringFromDate:date];

    yOffset += RCPublicServiceSingleCellPadding1;
    CGRect dateframe = [RCPublicServiceImgTxtMsgCell getDateFrame:model withWidth:self.frame.size.width];
    dateframe.origin.y += yOffset;
    yOffset += dateframe.size.height;
    self.dateLabel.frame = dateframe;
    //    UILabel *dateLabel = [[UILabel alloc] initWithFrame:dateframe];
    self.dateLabel.numberOfLines = 0;
    self.dateLabel.font = [UIFont systemFontOfSize:RCPublicServiceSingleCellSmallFont];
    self.dateLabel.textColor = [UIColor grayColor];
    self.dateLabel.textAlignment = NSTextAlignmentLeft;
    self.dateLabel.text = dateString;

    yOffset += RCPublicServiceSingleCellPadding2;
    CGRect imageframe = [RCPublicServiceImgTxtMsgCell getImageFrameWithWidth:self.frame.size.width];
    imageframe.origin.y += yOffset;
    yOffset += imageframe.size.height;
    self.imageView.frame = imageframe;
    self.imageView.layer.masksToBounds = YES;
    self.imageView.contentMode = UIViewContentModeScaleAspectFill;
    //    RCloudImageView *imageView = [[RCloudImageView alloc]initWithFrame:imageframe];
    [self.imageView setImageURL:[NSURL URLWithString:content.richConent.imageURL]];

    yOffset += RCPublicServiceSingleCellPadding3;
    CGRect contentframe = [RCPublicServiceImgTxtMsgCell getContentFrame:content withWidth:self.frame.size.width];
    contentframe.origin.y += yOffset;
    yOffset += contentframe.size.height;
    
    self.contentLabel.frame = contentframe;
    //    UILabel *contentLabel = [[UILabel alloc] initWithFrame:contentframe];
    self.contentLabel.numberOfLines = 0;
    self.contentLabel.font = [UIFont systemFontOfSize:RCPublicServiceSingleCellSmallFont];
    self.contentLabel.textColor = [UIColor grayColor];
    self.contentLabel.textAlignment = NSTextAlignmentLeft;
    self.contentLabel.text = content.richConent.digest;

    yOffset += RCPublicServiceSingleCellPadding4;

    self.line.frame = CGRectMake(RCPublicServiceSingleCellPaddingLeft / 2, yOffset - 4,
                                 self.contentView.frame.size.width - RCPublicServiceSingleCellPaddingLeft * 3, 1);
    self.line.backgroundColor = [UIColor colorWithRed:221 / 255.0 green:221 / 255.0 blue:221 / 255.0 alpha:1];
    NSString *readAll = NSLocalizedStringFromTable(@"ReadAll",@"RongCloudKit",nil);
    CGRect readallframe = [RCPublicServiceImgTxtMsgCell getReadAllFrame:readAll withWidth:self.frame.size.width];
    readallframe.origin.y += yOffset;
    yOffset += readallframe.size.height;
    self.readallLabel.frame = readallframe;
    //    UILabel *readallLabel = [[UILabel alloc] initWithFrame:readallframe];
    self.readallLabel.numberOfLines = 0;
    self.readallLabel.font = [UIFont systemFontOfSize:RCPublicServiceSingleCellSmallFont];
    self.readallLabel.textColor = RGBCOLOR(21, 21, 21);
    self.readallLabel.textAlignment = NSTextAlignmentLeft;
    self.readallLabel.text = readAll;
    self.arrow.frame = CGRectMake(self.frame.size.width - RCPublicServiceSingleCellPaddingLeft -
                                      RCPublicServiceSingleCellPaddingRight - RCPublicServiceProfileCellPaddingRight - 10,
                                  readallframe.origin.y + (readallframe.size.height / 2) - 4, 8, 8);

    yOffset += RCPublicServiceSingleCellPaddingButtom;

    self.container.frame = CGRectMake(
        RCPublicServiceSingleCellPaddingLeft, RCPublicServiceSingleCellPaddingTop,
        self.frame.size.width - RCPublicServiceSingleCellPaddingLeft - RCPublicServiceSingleCellPaddingRight, yOffset);
    [self.container setBackgroundColor:[UIColor whiteColor]];

    self.container.layer.cornerRadius = 4;
    self.container.layer.masksToBounds = YES;

    if (content.richConent.url) {
        UITapGestureRecognizer *tapGesture =
            [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTaped:)];
        [self addGestureRecognizer:tapGesture];
    }
}
- (void)onTaped:(id)sender {
    RCPublicServiceRichContentMessage *content = (RCPublicServiceRichContentMessage *)self.model.content;
    [self.publicServiceDelegate didTapUrlInPublicServiceMessageCell:content.richConent.url model:self.model];
}
@end
