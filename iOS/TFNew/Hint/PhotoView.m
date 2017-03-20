//
//  PhotoView.m
//  Hint
//
//  Created by jack on 2/1/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "PhotoView.h"


@interface PhotoView () <UIScrollViewDelegate>
{
    UIScrollView *_content;
    UIImageView *_photo;
    
    UIView      *_bottomBar;
    
    UIButton *_btnLike;
    UIButton *_btnCopy;
    UIButton *_btnComment;
    UIButton *_btnShare;
    
    UILabel *_commentsNum;
    UILabel *_likeNum;
    
    float   _subWidth;
}

@end

@implementation PhotoView
@synthesize _photo;
@synthesize _meta;
@synthesize _container;
@synthesize _status;

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (id) initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame])
    {
        _content = [[UIScrollView alloc] initWithFrame:self.bounds];
        _content.delegate = self;
        [self addSubview:_content];
        
        _photo = [[UIImageView alloc] initWithFrame:CGRectZero];
        [_content addSubview:_photo];
        
        _content.zoomScale = 1.0;
        _content.maximumZoomScale = 2.0;
        _content.minimumZoomScale = 1.0;
        
        
        _bottomBar = [[UIView alloc] initWithFrame:CGRectMake(0, frame.size.height - 50, frame.size.width, 50)];
        _bottomBar.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.7];
        [self addSubview:_bottomBar];
        
        _subWidth = SCREEN_WIDTH/4.0;
        
        _btnLike = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 40, 40)];
        [_btnLike setImage:[UIImage imageNamed:@"iconfont_like.png"] forState:UIControlStateNormal];
        [_btnLike setImage:[UIImage imageNamed:@"iconfont_like_down.png"] forState:UIControlStateHighlighted];
        
        _btnCopy = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 40, 40)];
        [_btnCopy setImage:[UIImage imageNamed:@"iconfont_copy.png"] forState:UIControlStateNormal];
        [_btnCopy setImage:[UIImage imageNamed:@"iconfont_copy_down.png"] forState:UIControlStateHighlighted];
        
        _btnComment = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 40, 40)];
        [_btnComment setImage:[UIImage imageNamed:@"iconfon_comment.png"] forState:UIControlStateNormal];
        [_btnComment setImage:[UIImage imageNamed:@"iconfon_comment_down.png"] forState:UIControlStateHighlighted];
        
        _btnShare = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 40, 40)];
        [_btnShare setImage:[UIImage imageNamed:@"icon_share_up.png"] forState:UIControlStateNormal];
        [_btnShare setImage:[UIImage imageNamed:@"icon_share_down.png"] forState:UIControlStateHighlighted];

        
        _likeNum = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 100, 20)];
        _likeNum.backgroundColor = [UIColor clearColor];
        _likeNum.textAlignment = NSTextAlignmentLeft;
        _likeNum.font = [UIFont systemFontOfSize:12];
        _likeNum.textColor = COLOR_TEXT_A;
        
        _commentsNum = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 100, 20)];
        _commentsNum.backgroundColor = [UIColor clearColor];
        _commentsNum.textAlignment = NSTextAlignmentLeft;
        _commentsNum.font = [UIFont systemFontOfSize:12];
        _commentsNum.textColor = COLOR_TEXT_A;
        
        
        [_bottomBar addSubview:_btnComment];
        _btnComment.center = CGPointMake(_subWidth*0.5-10, 25);
        
        [_bottomBar addSubview:_btnLike];
        _btnLike.center = CGPointMake(_subWidth*1.5-10, 25);
        
        [_bottomBar addSubview:_btnShare];
        _btnShare.center = CGPointMake(_subWidth*2.5-10, 25);
        
        [_bottomBar addSubview:_btnCopy];
        _btnCopy.center = CGPointMake(_subWidth*3.5-10, 25);
        
        
        [_bottomBar addSubview:_likeNum];
        [_bottomBar addSubview:_commentsNum];
        
        _likeNum.frame = CGRectMake(CGRectGetMaxX(_btnLike.frame)-6, 15, 100, 20);
        _commentsNum.frame = CGRectMake(CGRectGetMaxX(_btnComment.frame)-6, 15, 100, 20);
        
        

        UITapGestureRecognizer *tapped = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tappedAction:)];
        tapped.cancelsTouchesInView = NO;
        tapped.numberOfTapsRequired = 1;
        [_content addGestureRecognizer:tapped];

    }
    
    return self;
}


- (void) tappedAction:(id)sender{
    
    self.backgroundColor = [UIColor clearColor];
    
    UIView *v = self._photo;
    
    _container.hidden = YES;
    
    NSString *target = [self._meta objectForKey:@"target"];
    NSArray *a = [target componentsSeparatedByString:@","];
    
    int _imageViewHeight = [[a lastObject] intValue];
    
    _content.zoomScale = 1.0;
    
    [UIView animateWithDuration:0.25 animations:^{
        
        v.frame = CGRectMake([[a objectAtIndex:0] floatValue], [[a objectAtIndex:1] floatValue], SCREEN_WIDTH, _imageViewHeight);
        
        
        
    } completion:^(BOOL finished) {
        
        v.layer.contentsGravity = kCAGravityResizeAspectFill;
        
        [self removeFromSuperview];
        
        _container.hidden = NO;
        
    }];
    
}

- (void) updateCenter{
    
    _photo.center = CGPointMake(_content.frame.size.width/2, _content.frame.size.height/2);
    
    BOOL haslike = [[_status objectForKey:@"haslike"] boolValue];
    if(haslike)
    {
        [_btnLike setImage:[UIImage imageNamed:@"iconfont_like_down.png"] forState:UIControlStateNormal];
    }
    else
    {
        [_btnLike setImage:[UIImage imageNamed:@"iconfont_like.png"] forState:UIControlStateNormal];
    }
    
    
    int count = [[_status objectForKey:@"numlike"] intValue];
    if(count)
        _likeNum.text = [NSString stringWithFormat:@"%d", count];
    else
        _likeNum.text = @"";
    
    count = [[_status objectForKey:@"numcomment"] intValue];
    if(count)
        _commentsNum.text = [NSString stringWithFormat:@"%d", count];
    else
        _commentsNum.text = @"";
}

#pragma mark -
#pragma mark UIScrollViewDelegate
- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView{
    
    return _photo;
}

- (void)scrollViewDidZoom:(UIScrollView *)scrollView{
    
   // _photo.center = CGPointMake(_content.frame.size.width/2, _content.frame.size.height/2);
    
    //float scale = scrollView.zoomScale;

    
    if(_photo.frame.size.width < _content.frame.size.width)
    {
        _photo.center = CGPointMake(_content.frame.size.width/2, _content.frame.size.height/2);
    }
    else
    {
        float max = _content.contentSize.height;
        float height = _content.frame.size.height;
        max = max > height?max:height;
        
        _photo.center = CGPointMake(_content.contentSize.width/2, max/2);
    }
    
    
    //NSLog(@"%f,%f",_photo.center.x, _photo.center.y);
}

- (void)scrollViewDidEndZooming:(UIScrollView *)scrollView withView:(UIView *)view atScale:(CGFloat)scale{
    
//    NSLog(@"%f", scale);
//    
//    if(scale<=1.0)
//    {
//        _photo.center = CGPointMake(_content.frame.size.width/2, _content.frame.size.height/2);
//    }
//    else
//    {
//        _photo.center = CGPointMake(_content.contentSize.width/2, _content.contentSize.height/2);
//        //
//    }
    
    //NSLog(@"%f,%f",_pdfView.frame.size.width,_pdfView.frame.size.height);
//    if(_photo.frame.size.width < _content.frame.size.width)
//    {
//        _photo.center = CGPointMake(_content.frame.size.width/2, _content.frame.size.height/2);
//    }
//    else
//    {
//        _photo.center = CGPointMake(_content.contentSize.width/2, _content.contentSize.height/2);
//        
//    }
}

@end
