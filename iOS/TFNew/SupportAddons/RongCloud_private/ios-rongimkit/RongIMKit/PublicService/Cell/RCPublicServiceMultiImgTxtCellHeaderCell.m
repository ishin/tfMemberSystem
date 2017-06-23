//
//  RCPublicServiceMultiImgTxtCellHeaderCell.m
//  RongIMKit
//
//  Created by litao on 15/4/15.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCPublicServiceMultiImgTxtCellHeaderCell.h"
#import "RCloudImageView.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import "RCPublicServiceViewConstants.h"

@interface RCPublicServiceMultiImgTxtCellHeaderCell ()
@property(nonatomic, strong) RCloudImageView *headerImageView;
@property(nonatomic, strong) UILabel *headerLabel;
@property(nonatomic, strong) UIView *line;
@end

@implementation RCPublicServiceMultiImgTxtCellHeaderCell
- (UIView *)line {
    if (!_line) {
        _line = [UILabel new];
        _line.backgroundColor = [UIColor colorWithRed:221 / 255.0 green:221 / 255.0 blue:221 / 255.0 alpha:1];
        [self.contentView addSubview:_line];
    }
    return _line;
}
- (instancetype)initWithFrame:(CGRect)frame reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];

    if (self) {
        self.frame = frame;
        self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width,
                                RCPublicServiceCellHeaderHight + RCPublicServiceCellHeaderPaddingTop +
                                    RCPublicServiceCellHeaderPaddingButtom);
        self.headerImageView = [[RCloudImageView alloc]init];
        self.layer.cornerRadius = 4;
        self.layer.masksToBounds = YES;
        self.headerImageView.layer.cornerRadius = 2.0f;
        self.headerImageView.layer.masksToBounds = YES;
//        self.headerImageView.placeholderImage = IMAGE_BY_NAMED(@"default_portrait");
        self.headerImageView.contentMode=UIViewContentModeScaleAspectFill;
        self.headerImageView.frame =CGRectMake(RCPublicServiceCellHeaderPaddingLeft, RCPublicServiceCellHeaderPaddingTop,
                                                                       self.frame.size.width - RCPublicServiceCellHeaderPaddingLeft -
                                                                       RCPublicServiceCellHeaderPaddingRight,
                                                                       self.frame.size.height - RCPublicServiceCellHeaderPaddingTop -
                                                                       RCPublicServiceCellHeaderPaddingButtom);
        self.headerLabel = [UILabel new];
        self.headerLabel.textAlignment = NSTextAlignmentLeft;
        self.headerLabel.lineBreakMode = NSLineBreakByCharWrapping;
        self.headerLabel.numberOfLines = 0;
        self.headerLabel.textColor = [UIColor whiteColor];
        self.headerLabel.font = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline];
        self.headerLabel.backgroundColor = [UIColor blackColor];
        self.headerLabel.alpha = 0.5;

        [self addSubview:self.headerImageView];
        [self addSubview:self.headerLabel];
    }

    return self;
}

+ (CGFloat)getHeaderCellHeight {
    return RCPublicServiceCellHeaderHight + RCPublicServiceCellHeaderPaddingTop +
           RCPublicServiceCellHeaderPaddingButtom + 5;
}

- (void)setRichContent:(RCRichContentMessage *)richContent {
    _richContent = richContent;

    [self.headerImageView setImageURL:[NSURL URLWithString:_richContent.imageURL]];

    //设置一个行高上限
    CGSize size = CGSizeMake(
        self.frame.size.width - RCPublicServiceCellHeaderPaddingLeft - RCPublicServiceCellHeaderPaddingRight, 2000);
    CGSize labelsize = [RCKitUtility getTextDrawingSize:_richContent.title font:[UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline] constrainedSize:size];    
    if (labelsize.height > RCPublicServiceCellHeaderHight) {
        labelsize.height = RCPublicServiceCellHeaderHight;
    }
    if (labelsize.height < RCPublicServiceCellHeaderMinHight) {
        labelsize.height = RCPublicServiceCellHeaderMinHight;
    }

    self.headerLabel.frame =
        CGRectMake(RCPublicServiceCellHeaderPaddingLeft,
                   self.frame.size.height - labelsize.height - RCPublicServiceCellHeaderPaddingButtom,
                   self.headerImageView.frame.size.width, labelsize.height);
    self.line.frame = CGRectMake(0, self.frame.size.height + 2, self.contentView.frame.size.width + 20, 1);
    self.headerLabel.text = _richContent.title;
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
