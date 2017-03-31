//
//  PhotoMessageViewController.m
//  Hint
//
//  Created by jack on 2/29/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "PhotoMessageViewController.h"


@interface PhotoMessageViewController () <UIScrollViewDelegate>
{
    UIScrollView *_content;
    UIImageView *_photo;

    
    float   _subWidth;
    
    UIActivityIndicatorView *_active;
    
}
@end

@implementation PhotoMessageViewController
@synthesize _imageUrl;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.view.backgroundColor = [UIColor blackColor];
    
    _content = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    _content.delegate = self;
    [self.view addSubview:_content];
    
    _active = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
    [self.view addSubview:_active];
    _active.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
    _active.hidesWhenStopped = YES;
    
    _photo = [[UIImageView alloc] initWithFrame:CGRectZero];
    [_content addSubview:_photo];
    
    _content.zoomScale = 1.0;
    _content.maximumZoomScale = 2.0;
    _content.minimumZoomScale = 1.0;
    
    
    
    IMP_BLOCK_SELF(PhotoMessageViewController);
    
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
    
    
    [_active startAnimating];
    
    [_photo setImageWithURL:url
                  completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType) {
                      
                      [block_self stopLoading];
                      
                      if(image.size.height > 0)
                      {
                          float f = image.size.width/image.size.height;
                          [block_self adjustPhotoFrame:f];
                      }
                  }];
    
    
    UIButton *btnCLose = [UIButton buttonWithType:UIButtonTypeCustom];
    btnCLose.backgroundColor = [UIColor whiteColor];
    [btnCLose setImage:[UIImage imageNamed:@"icon_delete_photo.png"] forState:UIControlStateNormal];
    btnCLose.frame = CGRectMake(0, 0, 50, 50);
    btnCLose.layer.cornerRadius = 25;
    btnCLose.clipsToBounds = YES;
    [self.view addSubview:btnCLose];
    btnCLose.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT-40);
    [btnCLose addTarget:self action:@selector(tappedAction:) forControlEvents:UIControlEventTouchUpInside];
    
//    UITapGestureRecognizer *tapped = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tappedAction:)];
//    tapped.cancelsTouchesInView = NO;
//    tapped.numberOfTapsRequired = 1;
//    [_content addGestureRecognizer:tapped];
//
//    UITapGestureRecognizer* tappedDouble = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tappedDoubleAction:)];
//    tappedDouble.cancelsTouchesInView = NO;
//    tappedDouble.numberOfTapsRequired = 2;
//    [_content addGestureRecognizer:tappedDouble];
//    
//    [tapped requireGestureRecognizerToFail:tappedDouble];

}




- (void) stopLoading{
    
   
    [_active stopAnimating];
}


- (void) tappedDoubleAction:(UITapGestureRecognizer*)sender{
    
    if(_content.zoomScale == 1)
    {
        [_content setZoomScale:2 animated:YES];
    }
    else
    {
        [_content setZoomScale:1 animated:YES];
    }
    
}




- (void) tappedAction:(id)sender{
    
    [_content setZoomScale:1 animated:NO];
    
    [self dismissViewControllerAnimated:YES completion:nil];

}

- (void) adjustPhotoFrame:(float)dlt{
    
    if(dlt > 0)
    {
        int h = SCREEN_WIDTH/dlt;
        float yy = (SCREEN_HEIGHT-h)/2;
        
        if(yy < 0)
            yy = 0;
        
        CGRect rcNew = CGRectMake(0, yy, SCREEN_WIDTH, h);
        
        _photo.frame = rcNew;
        
        if(yy > 0)
        {
            _photo.center = CGPointMake(_content.frame.size.width/2, _content.frame.size.height/2);
        }
    }
    
    [_content setZoomScale:1 animated:NO];
    
    
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
    
   
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
