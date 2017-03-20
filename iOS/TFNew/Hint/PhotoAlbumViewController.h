//
//  PhotoAlbumViewController.h
//  hkeeping
//
//  Created by apple on 6/6/14.
//  Copyright (c) 2014 apple. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RRScrollView.h"

@interface JPhotoView : UIView <UIScrollViewDelegate>{
    
    UIScrollView *_content;
    UIImageView *_picView;
}
@property (nonatomic, strong) UIImageView *_picView;

- (void) orgZoom;

@end

@interface PhotoAlbumViewController : BaseViewController <RRScrollViewDelegate>
{
    RRScrollView *_picScroll;
    UIPageControl *_pageCtrl;
    
    
    NSMutableArray *_btns;
    
    UILabel *_tL;
    
}
@property (nonatomic, strong) NSArray *_pictures;
@property (nonatomic, assign) int _picIndex;;
@property (nonatomic, strong) NSString *_title;
@end
