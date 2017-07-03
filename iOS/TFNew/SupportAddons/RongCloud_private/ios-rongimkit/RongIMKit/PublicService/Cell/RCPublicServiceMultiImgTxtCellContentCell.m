//
//  RCPublicServiceMultiImgTxtCellContentCell.m
//  RongIMKit
//
//  Created by litao on 15/4/15.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCPublicServiceMultiImgTxtCellContentCell.h"
#import "RCloudImageView.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import "RCPublicServiceViewConstants.h"

@interface RCPublicServiceMultiImgTxtCellContentCell ()
@property(nonatomic, strong) RCloudImageView *headerImageView;
@property(nonatomic, strong) UILabel *headerLabel;
@property(nonatomic, strong) UIView *line;

@end

@implementation RCPublicServiceMultiImgTxtCellContentCell
- (UIView *)line {
    if (!_line) {
        _line = [UILabel new];
        _line.backgroundColor = [UIColor colorWithRed:221 / 255.0 green:221 / 255.0 blue:221 / 255.0 alpha:1];
        [self addSubview:_line];
    }
    return _line;
}

- (instancetype)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];

    if (self) {
        self.frame = frame;
        self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width, 150);
        self.headerImageView = [[RCloudImageView alloc]
            initWithFrame:CGRectMake(self.frame.size.width - RCPublicServiceCellHeaderPaddingLeft -
                                         RCPublicServiceCellContentCellImageWidth,
                                     RCPublicServiceCellHeaderPaddingTop / 2, RCPublicServiceCellContentCellImageWidth,
                                     RCPublicServiceCellContentCellImageHeight)];
        self.headerImageView.layer.cornerRadius = 4;
        self.headerImageView.layer.masksToBounds = YES;
        self.headerImageView.contentMode = UIViewContentModeScaleAspectFill;
        // self.headerImageView.contentMode            =   UIViewContentModeCenter;
        //        self.headerImageView.image                  =   IMAGE_BY_NAMED(@"default_portrait");
//        self.headerImageView.placeholderImage = IMAGE_BY_NAMED(@"default_portrait");

        self.headerLabel = [UILabel new];
        self.headerLabel.textAlignment = NSTextAlignmentLeft;
        self.headerLabel.lineBreakMode = NSLineBreakByCharWrapping;
        self.headerLabel.numberOfLines = 2;

        self.headerLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];

        [self addSubview:self.headerImageView];
        [self addSubview:self.headerLabel];
    }
    return self;
}
+ (CGSize)getContentCellSize:(RCRichContentMessage *)richContent withWidth:(CGFloat)width {
    //设置一个行高上限
    CGSize size = CGSizeMake(width - RCPublicServiceCellHeaderPaddingLeft - RCPublicServiceCellHeaderPaddingRight -
                                 RCPublicServiceCellContentCellImageWidth,
                             2000);
    CGSize labelsize = [RCKitUtility getTextDrawingSize:richContent.title font:[UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline] constrainedSize:size];
    labelsize.height = RCPublicServiceCellContentCellImageHeight;
    return labelsize;
}
- (void)setRichContent:(RCRichContentMessage *)richContent {
    _richContent = richContent;
    [self.headerImageView setImageURL:[NSURL URLWithString:_richContent.imageURL]];

    CGSize labelsize =
        [RCPublicServiceMultiImgTxtCellContentCell getContentCellSize:_richContent withWidth:self.frame.size.width];

    self.headerLabel.frame = CGRectMake(RCPublicServiceCellHeaderPaddingLeft, RCPublicServiceCellHeaderPaddingTop / 3,
                                        labelsize.width, labelsize.height);
    self.headerLabel.text = _richContent.title;
    CGRect cellFrame = self.frame;
    cellFrame.size.height =
        RCPublicServiceCellHeaderPaddingTop + RCPublicServiceCellHeaderPaddingButtom + labelsize.height;
    self.frame = cellFrame;
    if (_isShowline) {
        self.line.frame =
            CGRectMake(0, self.headerImageView.frame.size.height + 10, self.contentView.frame.size.width + 20, 1);
    }

    if (_richContent.url) {
        UITapGestureRecognizer *tapGesture =
            [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTaped:)];
        [self addGestureRecognizer:tapGesture];
    }
}
- (void)onTaped:(id)sender {
    DebugLog(@"ontaped:");
    [self.publicServiceDelegate didTapUrlInPublicServiceMessageCell:self.richContent.url model:nil];
}
@end
