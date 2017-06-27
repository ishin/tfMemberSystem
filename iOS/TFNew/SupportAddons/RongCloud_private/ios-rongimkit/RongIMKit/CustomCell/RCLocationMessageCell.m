//
//  RCLocationMessageCell.m
//  RongIMKit
//
//  Created by xugang on 15/2/2.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCLocationMessageCell.h"
#import "RCIM.h"
#import "RCKitUtility.h"
#import "RCKitCommonDefine.h"

@interface RCLocationMessageCell ()
@property(nonatomic, strong) UIImageView *maskView;
@property(nonatomic, strong) UIImageView *shadowMaskView;
- (void)initialize;
@end

@implementation RCLocationMessageCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
  referenceExtraHeight:(CGFloat)extraHeight {
  CGFloat __messagecontentview_height =  240 / 2.0f;

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

- (void)initialize {
    self.pictureView = [[UIImageView alloc] initWithFrame:CGRectZero];
    self.pictureView.clipsToBounds = YES;
    self.pictureView.contentMode = UIViewContentModeScaleToFill;
    self.locationNameLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.locationNameLabel.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.6];
    self.locationNameLabel.textAlignment = NSTextAlignmentCenter;
    self.locationNameLabel.textColor = [UIColor whiteColor];
    self.locationNameLabel.font = [UIFont systemFontOfSize:14.0f];
    self.locationNameLabel.clipsToBounds = YES;
    [self.pictureView addSubview:self.locationNameLabel];
    
    [self.messageContentView addSubview:self.pictureView];
    UILongPressGestureRecognizer *longPress =
    [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressed:)];
    [self.pictureView addGestureRecognizer:longPress];
    UITapGestureRecognizer *pictureTap =
        [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapPicture:)];
    pictureTap.numberOfTapsRequired = 1;
    pictureTap.numberOfTouchesRequired = 1;
    [self.pictureView addGestureRecognizer:pictureTap];
    self.pictureView.userInteractionEnabled = YES;
}

- (void)setMaskImage:(UIImage *)maskImage{
    if (_maskView == nil) {
        _maskView = [[UIImageView alloc] initWithImage:maskImage];
        
        _maskView.frame = self.pictureView.bounds;
        self.pictureView.layer.mask = _maskView.layer;
        self.pictureView.layer.masksToBounds = YES;
    } else {
        _maskView.image = maskImage;
        _maskView.frame = self.pictureView.bounds;
    }
    if(_shadowMaskView){
        [_shadowMaskView removeFromSuperview];
    }
    _shadowMaskView = [[UIImageView alloc] initWithImage:maskImage];
    
    _shadowMaskView.frame = CGRectMake(-0.2,- 0.2, self.pictureView.frame.size.width + 1.2, self.pictureView.frame.size.height +1.2);
    [self.messageContentView addSubview:_shadowMaskView];
    [self.messageContentView bringSubviewToFront:self.pictureView];
    
}


- (void)tapPicture:(UIGestureRecognizer *)gestureRecognizer {
    if ([self.delegate respondsToSelector:@selector(didTapMessageCell:)]) {
        [self.delegate didTapMessageCell:self.model];
    }
}

#pragma mark override,
- (void)setDataModel:(RCMessageModel *)model {
    [super setDataModel:model];
    RCLocationMessage *_locationMessage = (RCLocationMessage *)model.content;
    if (_locationMessage) {
        self.locationNameLabel.text = _locationMessage.locationName;
        //写死尺寸 408*240
      
//这个尺寸在生成缩略图的地方有定义，还有发送位置消息时对尺寸有裁剪。如果修改尺寸，需要把对应的地方同时修改
#define TARGET_LOCATION_THUMB_WIDTH  408
#define TARGET_LOCATION_THUMB_HEIGHT 240
      
        CGSize imageSize = CGSizeMake(TARGET_LOCATION_THUMB_WIDTH / 2.0f, TARGET_LOCATION_THUMB_HEIGHT / 2.0f);

        CGRect messageContentViewRect = self.messageContentView.frame;
        self.pictureView.image = _locationMessage.thumbnailImage;
        UIImage *maskImage = nil;
        self.shadowMaskView.image = nil;
        if (model.messageDirection == MessageDirection_RECEIVE) {
            messageContentViewRect.size.width = imageSize.width;
            messageContentViewRect.size.height = imageSize.height;
            self.messageContentView.frame = messageContentViewRect;
            maskImage = [RCKitUtility imageNamed:@"chat_from_bg_normal" ofBundle:@"RongCloud.bundle"];
            self.pictureView.frame = CGRectMake(0.5, 0.5,imageSize.width-1, imageSize.height-1);
            maskImage = [maskImage
                             resizableImageWithCapInsets:UIEdgeInsetsMake(maskImage.size.height * 0.8, maskImage.size.width * 0.8,
                                                                          maskImage.size.height * 0.2, maskImage.size.width * 0.2)];
            
            self.locationNameLabel.frame = CGRectMake(7,imageSize.height - 25, imageSize.width-7, 25);

        } else {
            messageContentViewRect.size.width = imageSize.width;
            messageContentViewRect.size.height = imageSize.height;
            messageContentViewRect.origin.x =
                self.baseContentView.bounds.size.width -
                (messageContentViewRect.size.width + HeadAndContentSpacing + [RCIM sharedRCIM].globalMessagePortraitSize.width + 10);
            self.messageContentView.frame = messageContentViewRect;
            self.pictureView.frame = CGRectMake(0.5,0.5, imageSize.width-1, imageSize.height-1);
            maskImage = [RCKitUtility imageNamed:@"chat_to_bg_normal" ofBundle:@"RongCloud.bundle"];
            maskImage = [maskImage
                         resizableImageWithCapInsets:UIEdgeInsetsMake(maskImage.size.height * 0.8, maskImage.size.width * 0.2,
                                                                      maskImage.size.height * 0.2, maskImage.size.width * 0.8)];
            self.locationNameLabel.frame = CGRectMake(0,imageSize.height - 25, imageSize.width-8, 25);

        }
        [self setMaskImage:maskImage];
    } else {
        DebugLog(@"[RongIMKit]: RCMessageModel.content is NOT RCLocationMessage object");
    }
    UIBezierPath *maskPath = [UIBezierPath bezierPathWithRoundedRect:self.locationNameLabel.bounds byRoundingCorners:UIRectCornerBottomLeft | UIRectCornerBottomRight cornerRadii:CGSizeMake(5, 5)];
    CAShapeLayer *maskLayer = [[CAShapeLayer alloc] init];
    maskLayer.frame = self.locationNameLabel.bounds;
    maskLayer.path = maskPath.CGPath;
    self.locationNameLabel.layer.mask = maskLayer;
    [self setAutoLayout];
}

//传入原始图片对象
-(UIImage *)getImageFromImage:(UIImage*) image withMessageDirection:(RCMessageDirection)messageDirection
{
    CGFloat width = image.size.width;
    CGFloat height = image.size.height;
    CGFloat radius = 10;
    CGFloat rightSpace = 15;
    CGFloat leftSpace = 0;
    if (messageDirection == MessageDirection_RECEIVE) {
        rightSpace = 0;
        leftSpace = 15;
    }
    //开始绘制图片
    UIGraphicsBeginImageContext(image.size);
    CGContextRef gc = UIGraphicsGetCurrentContext();
    
    CGContextMoveToPoint(gc, radius+leftSpace, 0);
    CGContextAddLineToPoint(gc, width-rightSpace-radius,0);
    CGContextAddArcToPoint(gc, width-rightSpace, 0,width-rightSpace, height-radius, radius);
    CGContextAddLineToPoint(gc, width-rightSpace,20);
    CGContextAddLineToPoint(gc, width, 35);
    CGContextAddLineToPoint(gc, width-rightSpace,50);
    CGContextAddLineToPoint(gc, width-rightSpace,height-radius);
    CGContextAddArcToPoint(gc, width-rightSpace,height,width-rightSpace-radius, height,radius);
    CGContextAddLineToPoint(gc,radius+leftSpace, height);
    CGContextAddArcToPoint(gc, 0+leftSpace,height,0+leftSpace,height-radius, radius);
    CGContextAddLineToPoint(gc, 0+leftSpace,20);
    CGContextAddLineToPoint(gc, 0, 35);
    CGContextAddLineToPoint(gc, leftSpace,50);
    CGContextAddLineToPoint(gc, 0+leftSpace, height-radius);
    CGContextAddArcToPoint(gc,0+leftSpace, 0,radius+leftSpace,0, radius);
    CGContextClosePath(gc);
    CGContextClip(gc);

    //坐标系转换
    //因为CGContextDrawImage会使用Quartz内的以左下角为(0,0)的坐标系
    CGContextTranslateCTM(gc, 0, height);
    CGContextScaleCTM(gc, 1, -1);
    CGContextDrawImage(gc, CGRectMake(0, 0, width, height), [image CGImage]);
    //结束绘画
    UIImage *destImg = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    //返回裁剪的部分图像
    return destImg;
}


- (void)setAutoLayout {
}

//#pragma mark override, prepare to send message
//-(void)messageCellUpdateSendingStatusEvent:(NSNotification *)notification
//{
//    DebugLog(@"%s", __FUNCTION__);
//}
// override
- (void)msgStatusViewTapEventHandler:(id)sender {
    //[super msgStatusViewTapEventHandler:sender];

    // to do something.
}

- (void)longPressed:(id)sender {
    UILongPressGestureRecognizer *press = (UILongPressGestureRecognizer *)sender;
    if (press.state == UIGestureRecognizerStateEnded) {
        DebugLog(@"long press end");
        return;
    } else if (press.state == UIGestureRecognizerStateBegan) {
        [self.delegate didLongTouchMessageCell:self.model inView:self.pictureView];
    }
}

@end
