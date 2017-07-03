//
//  PreviewViewController.m
//  RCIM
//
//  Created by Heq.Shinoda on 14-5-27.
//  Copyright (c) 2014年 Heq.Shinoda. All rights reserved.
//

#import "RCImagePreviewController.h"
#import "RCKitCommonDefine.h"
#import "RCMessageModel.h"
#import "RCImageMessageProgressView.h"
#import "RCIM.h"
#import "RCKitUtility.h"

@interface RCImagePreviewController () <UIScrollViewDelegate>

@property(nonatomic) NSUInteger progressStep;
@property(nonatomic, strong) UIScrollView *scrollView;
@property(nonatomic, strong) UIImageView *imageView;

- (void)startDownload:(RCMessageModel *)messageModel;
- (void)leftBarButtonItemPressed:(id)sender;
- (void)rightBarButtonItemPressed:(id)sender;

@end

@implementation RCImagePreviewController
@synthesize originalImageView;

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.progressStep = 0;
    }
    return self;
}
- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        self.progressStep = 0;
    }
    return self;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];

    [self.view addSubview:self.rcImageProressView];
    [self.rcImageProressView setCenter:CGPointMake(self.view.bounds.size.width / 2, self.view.bounds.size.height / 2)];
    [self.rcImageProressView startAnimating];

    [self startDownload:self.messageModel];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    UIBarButtonItem *left =
        [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Cancel", @"RongCloudKit", nil)
                                         style:UIBarButtonItemStylePlain
                                        target:self
                                        action:@selector(leftBarButtonItemPressed:)];

    [left setTintColor:[RCIM sharedRCIM].globalNavigationBarTintColor];
    self.navigationItem.leftBarButtonItem = left;

    UIBarButtonItem *right =
        [[UIBarButtonItem alloc] initWithTitle:NSLocalizedStringFromTable(@"Save", @"RongCloudKit", nil)
                                         style:UIBarButtonItemStylePlain
                                        target:self
                                        action:@selector(rightBarButtonItemPressed:)];
    [right setTintColor:[RCIM sharedRCIM].globalNavigationBarTintColor];
    self.navigationItem.rightBarButtonItem = right;
    // add imageview
    [self.view setBackgroundColor:[UIColor blackColor]];

    self.originalImageView = [[UIImageView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:originalImageView];
    self.rcImageProressView = [[RCImageMessageProgressView alloc] initWithFrame:CGRectMake(0, 0, 135, 135)];
}

- (void)startDownload:(RCMessageModel *)messageModel {
//    int type = 0;
//    RCMessageContent *messageContent = messageModel.content;
//    type = [messageContent isMemberOfClass:RCTextMessage.class]
//               ? 0
//               : ([messageContent isMemberOfClass:RCImageMessage.class]
//                      ? 1
//                      : ([messageContent isMemberOfClass:RCVoiceMessage.class] ? 2 : -1));
    RCImageMessage *imageContent = (RCImageMessage *)messageModel.content;

    if (messageModel.messageDirection == MessageDirection_SEND && ![imageContent.imageUrl hasPrefix:@"http"]) {
        
        
        UIImage *image = [UIImage imageWithContentsOfFile:imageContent.imageUrl];
        DebugLog(@"imageurl is %@", imageContent.imageUrl);
        if (image) {
            self.originalImageView.image = image;
            [self createScaleView:image];
        } else {
            image = [RCKitUtility imageNamed:@"exclamation" ofBundle:@"RongCloud.bundle"];
            UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
            [imageView setCenter:CGPointMake(self.view.bounds.size.width / 2, self.view.bounds.size.height / 2 - 50)];
            [self.view addSubview:imageView];
            UILabel *failLabel = [[UILabel alloc] initWithFrame:CGRectMake(self.view.bounds.size.width / 2 - 75, self.view.bounds.size.height / 2, 150, 30)];
          failLabel.text = NSLocalizedStringFromTable(@"ImageHasBeenDeleted",
                                                      @"RongCloudKit", nil);
            failLabel.textAlignment = NSTextAlignmentCenter;
            [self.view addSubview:failLabel];
            self.scrollView.backgroundColor = [UIColor whiteColor];
            self.imageView.backgroundColor = [UIColor whiteColor];
            self.view.backgroundColor = [UIColor whiteColor];
        }
        [self.rcImageProressView stopAnimating];
        [self.rcImageProressView removeFromSuperview];
        [self imageDownloadDone];
    } else {
        __weak typeof(&*self) blockSelf = self;
        [[RCIMClient sharedRCIMClient] downloadMediaFile:messageModel.conversationType
            targetId:messageModel.targetId
            mediaType:MediaType_IMAGE
            mediaUrl:imageContent.imageUrl
            progress:^(int progress) {
              DebugLog(@"[RongIMKit]: downloadMediaFile.progress > %d, isMainThread > %d", progress,
                       [NSThread isMainThread]);
              dispatch_async(dispatch_get_main_queue(), ^{
                [blockSelf.rcImageProressView updateProgress:progress];
              });
            }
            success:^(NSString *mediaPath) {
              DebugLog(@"[RongIMKit]:downloadMediaFile.mediaPath > %@, isMainThread > %d", mediaPath,
                       [NSThread isMainThread]);
              dispatch_async(dispatch_get_main_queue(), ^{
                UIImage *image = [UIImage imageWithContentsOfFile:mediaPath];
                  if (image) {
                      blockSelf.originalImageView.image = image;
                      [blockSelf createScaleView:image];
                  } else {
                      image = [RCKitUtility imageNamed:@"exclamation" ofBundle:@"RongCloud.bundle"];
                      UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
                      [imageView setCenter:CGPointMake(blockSelf.view.bounds.size.width / 2, blockSelf.view.bounds.size.height / 2 - 50)];
                      [blockSelf.view addSubview:imageView];
                      UILabel *failLabel = [[UILabel alloc] initWithFrame:CGRectMake(blockSelf.view.bounds.size.width / 2 - 75, blockSelf.view.bounds.size.height / 2, 150, 30)];
                      failLabel.text = NSLocalizedStringFromTable(@"ImageLoadFailed",
                                                                  @"RongCloudKit", nil);
                      failLabel.textAlignment = NSTextAlignmentCenter;
                      [blockSelf.view addSubview:failLabel];
                      blockSelf.scrollView.backgroundColor = [UIColor whiteColor];
                      blockSelf.imageView.backgroundColor = [UIColor whiteColor];
                      blockSelf.view.backgroundColor = [UIColor whiteColor];
                  }
                  [blockSelf.rcImageProressView stopAnimating];
                  [blockSelf.rcImageProressView removeFromSuperview];
                  [blockSelf imageDownloadDone];
              });

            }
            error:^(RCErrorCode errorCode) {
              DebugLog(@"[RongIMKit]: downloadMediaFile.errorCode > %d", (int)errorCode);

                dispatch_async(dispatch_get_main_queue(), ^{
                    UILabel *failLabel = [[UILabel alloc] initWithFrame:CGRectMake(blockSelf.view.bounds.size.width / 2 - 75, blockSelf.view.bounds.size.height / 2, 150, 30)];
                    failLabel.textAlignment = NSTextAlignmentCenter;
                    [blockSelf.view addSubview:failLabel];
                    UIImage *image = nil;
                    if (errorCode == 404) {
                        image = [RCKitUtility imageNamed:@"exclamation" ofBundle:@"RongCloud.bundle"];
                      failLabel.text = NSLocalizedStringFromTable(@"ImageHasBeenDeleted",
                                                                  @"RongCloudKit", nil);
                    } else {
                        image = [RCKitUtility imageNamed:@"broken" ofBundle:@"RongCloud.bundle"];
                      failLabel.text = NSLocalizedStringFromTable(@"ImageLoadFailed",
                                                                  @"RongCloudKit", nil);
                    }
                    UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
                    [imageView setCenter:CGPointMake(blockSelf.view.bounds.size.width / 2, blockSelf.view.bounds.size.height / 2 - 50)];
                    [blockSelf.view addSubview:imageView];
                    [blockSelf.rcImageProressView stopAnimating];
                    [blockSelf.rcImageProressView removeFromSuperview];
                    blockSelf.scrollView.backgroundColor = [UIColor whiteColor];
                    blockSelf.imageView.backgroundColor = [UIColor whiteColor];
                    blockSelf.view.backgroundColor = [UIColor whiteColor];
                    [blockSelf imageDownloadDone];
                });

            }];
    }
}

- (UIImageView *)createUIImageView:(UIImage *)image withParentView:(UIView *)view {
    UIImageView *_resizedImageView = [[UIImageView alloc] initWithImage:image];

    _resizedImageView.contentMode = UIViewContentModeScaleAspectFill;

    CGFloat imageWidth = image.size.width;
    CGFloat imageHeight = image.size.height;
    CGFloat imageScaleX = imageWidth / view.bounds.size.width;
    CGFloat imageScaleY = imageHeight / view.bounds.size.height;
    CGFloat maxScale = imageScaleX > imageScaleY ? imageScaleX : imageScaleY;
    if (maxScale > 1) {
        [_resizedImageView setFrame:CGRectMake(0, 0, imageWidth / maxScale, imageHeight / maxScale)];
    } else {
        [_resizedImageView setFrame:CGRectMake(0, 0, imageWidth, imageHeight)];
    }
    return _resizedImageView;
}

- (void)createScaleView:(UIImage *)image {
    self.scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];

    [self.scrollView setBackgroundColor:[UIColor blackColor]];
    [self.scrollView setDelegate:self];
    [self.scrollView setShowsHorizontalScrollIndicator:NO];
    [self.scrollView setShowsVerticalScrollIndicator:NO];
    [self.scrollView setMaximumZoomScale:10.0];

    self.imageView = [self createUIImageView:image withParentView:self.scrollView];

    [self.scrollView setContentSize:self.view.bounds.size];
    [self.scrollView setMinimumZoomScale:1.0f];
    [self.scrollView setZoomScale:[self.scrollView minimumZoomScale]];
    [self.scrollView addSubview:self.imageView];

    [self.imageView
        setCenter:CGPointMake(self.scrollView.bounds.size.width / 2, self.scrollView.bounds.size.height / 2)];
    [[self view] addSubview:self.scrollView];
}

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
    return self.imageView;
}

- (void)scrollViewDidZoom:(UIScrollView *)scrollView {

    CGSize boundsSize = scrollView.bounds.size;
    CGRect imgFrame = self.imageView.frame;
    CGSize contentSize = scrollView.contentSize;

    CGPoint centerPoint = CGPointMake(contentSize.width / 2, contentSize.height / 2);

    // center horizontally
    if (imgFrame.size.width <= boundsSize.width) {
        centerPoint.x = boundsSize.width / 2;
    }

    // center vertically
    if (imgFrame.size.height <= boundsSize.height) {
        centerPoint.y = boundsSize.height / 2;
    }

    self.imageView.center = centerPoint;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)leftBarButtonItemPressed:(id)sender {
    [self.navigationController dismissViewControllerAnimated:YES completion:NULL];
}
- (void)rightBarButtonItemPressed:(id)sender {
    //保存图片
    UIImage *image = self.originalImageView.image;
    UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    if (error != NULL) {
        //失败
        DebugLog(@" save image fail");
        UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle:nil
                                       message:NSLocalizedStringFromTable(@"SavePhotoFailed", @"RongCloudKit", nil)
                                      delegate:nil
                             cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit", nil)
                             otherButtonTitles:nil];
        [alert show];
        return;
    } else {
        //成功
        DebugLog(@"save image suceed");
        UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle:nil
                                       message:NSLocalizedStringFromTable(@"SavePhotoSuccess", @"RongCloudKit", nil)
                                      delegate:nil
                             cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit", nil)
                             otherButtonTitles:nil];
        [alert show];
        return;
    }
}
#pragma mark -override
- (void)imageDownloadDone {
}
@end