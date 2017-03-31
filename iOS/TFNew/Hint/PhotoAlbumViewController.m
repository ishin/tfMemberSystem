//
//  PhotoAlbumViewController.m
//  hkeeping
//
//  Created by apple on 6/6/14.
//  Copyright (c) 2014 apple. All rights reserved.
//

#import "PhotoAlbumViewController.h"
#import "SBJson4.h"
#import "UIImageView+WebCache.h"

@implementation JPhotoView
@synthesize _picView;

- (id) initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    
    _content = [[UIScrollView alloc] initWithFrame:self.bounds];
    _content.delegate = self;
    [self addSubview:_content];
    _content.minimumZoomScale = 1.0;
    _content.maximumZoomScale = 5.0;
    
    _picView = [[UIImageView alloc] initWithFrame:self.bounds];
    _picView.contentMode = UIViewContentModeScaleAspectFit;
    _picView.clipsToBounds = YES;
    [_content addSubview:_picView];
    
    return self;
}

- (void) orgZoom{
    
    _content.zoomScale = 1.0;
}

//#pragma mark -
//#pragma mark UIScrollViewDelegate
- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView{
	return _picView;
}


@end

@interface PhotoAlbumViewController ()

@end

@implementation PhotoAlbumViewController
@synthesize _pictures;
@synthesize _picIndex;
@synthesize _title;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void) viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBarHidden = YES;
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    
    _picScroll = [[RRScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    [self.view addSubview:_picScroll];
    _picScroll.delegate_ = self;
    _picScroll.backgroundColor = [UIColor blackColor];
    
    
    
    _picScroll.currentPageIndex = _picIndex;
    [_picScroll loadData];
 
    
}

- (void)didTappedScrollView:(UIScrollView*)scrollView tapPoint:(CGPoint)tapPoint{
    
    [self tappedAction:nil];
}

- (void) tappedAction:(id)sender{
    
    [self.navigationController popViewControllerAnimated:NO];
}


- (int) numberOfScrollPages{
    
    int count = (int)[_pictures count];
    
    return count;
}
- (UIView*) scrollPageViewAtIndex:(int)pageIndex{
    
    JPhotoView *pageView = [[JPhotoView alloc] initWithFrame:_picScroll.bounds];
    
    RCMessage* dic = [_pictures objectAtIndex:pageIndex];
    
    if([dic isKindOfClass:[RCMessage class]])
    {
        RCImageMessage *imgMsg = (RCImageMessage*)dic.content;
        
        NSString *_imageUrl = imgMsg.imageUrl;
        
        NSRange range = [_imageUrl rangeOfString:@"http"];
        id url = nil;
        if(range.location != NSNotFound)
        {
            url = [NSURL URLWithString:_imageUrl];
        }
        else
        {
            url = [NSURL fileURLWithPath:_imageUrl];
        }

        
        [pageView._picView setImageWithURL:url];
    }
    
    pageView.contentMode = UIViewContentModeScaleAspectFit;
    pageView.clipsToBounds = YES;
    
    return pageView;
}
- (void) didScrollToPageAtIndex:(int)pageIndex{
    

    if(pageIndex < [_pictures count]-1)
    {
        JPhotoView *p = (JPhotoView*)[_picScroll pageViewAtIndex:pageIndex+1];
        [p orgZoom];
    }
    

    if(pageIndex>0)
    {
        JPhotoView *p = (JPhotoView*)[_picScroll pageViewAtIndex:pageIndex-1];
        [p orgZoom];
    }
    
    
}


- (void)didReceiveMemoryWarning
{
    
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
