//
//  RCImageSlideController.m
//  RongIMKit
//
//  Created by liulin on 16/5/18.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCIM.h"
#import "RCImageMessageProgressView.h"
#import "RCImageSlideController.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import "RCMessageModel.h"
#import "RCloudImageLoader.h"
#import "RCloudImageView.h"

#define SCREENWIDTH [UIScreen mainScreen].bounds.size.width
#define SCREENHEIGTH [UIScreen mainScreen].bounds.size.height

@interface RCImageSlideController () <
    UIScrollViewDelegate, RCloudImageViewDelegate, UIActionSheetDelegate>
// scrollView
@property(nonatomic, strong) UIScrollView *scrollView;
//当前图片消息的数据模型
@property(nonatomic, strong) NSMutableArray<RCMessageModel *> *imageArray;
//当前图片消息的index
@property(nonatomic, assign) NSInteger currentIndex;
//当前图片的View
@property(nonatomic, strong) RCloudImageView *currentImageView;
//滑动时的offset
@property(nonatomic, assign) CGFloat newContentOffsetX;
@property(nonatomic, assign) CGFloat offsettest;
@property(nonatomic, assign) CGFloat ContentOffset;
@property(nonatomic, assign) NSInteger preSelectIndex;
//图片列表
@property(nonatomic, strong) NSMutableArray<RCloudImageView *> *imageViewList;
@property(nonatomic, strong) NSMutableArray<RCImageMessage *> *imagemessageList;
@property(nonatomic, strong) NSMutableDictionary *imageProgressList;

@end

@implementation RCImageSlideController

- (void)viewDidLoad {
  [super viewDidLoad];
    
  self.imageProgressList = [NSMutableDictionary new];
  //取当前界面中一定数量的图片
  [self getMessageFromModel:self.messageModel];
  self.scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
  [self.scrollView setBackgroundColor:[UIColor blackColor]];
  [self.scrollView setDelegate:self];
  [self.scrollView setPagingEnabled:YES];
  [self.scrollView setShowsHorizontalScrollIndicator:NO];
  [self.scrollView setShowsVerticalScrollIndicator:NO];
  [self.scrollView setContentSize:CGSizeMake([self.imageArray count] *
                                                 self.view.bounds.size.width,
                                             0)];

  //长按可选择是否保存图片
  UILongPressGestureRecognizer *longPress =
      [[UILongPressGestureRecognizer alloc]
          initWithTarget:self
                  action:@selector(longPressed:)];

  self.imageViewList = [NSMutableArray new];
  self.imagemessageList = [NSMutableArray new];
  self.currentImageView = [RCloudImageView new];

  //添加图片到scroll子视图
  [self refreshimage:self.imageArray];
  [self refreshScrollView:self.imageViewList];

  self.ContentOffset = 0;
  self.offsettest = 0.0;
  self.scrollView.contentOffset =
      CGPointMake(self.currentIndex * self.view.frame.size.width, 0);
  self.ContentOffset = self.currentIndex * self.view.frame.size.width;
  self.automaticallyAdjustsScrollViewInsets = NO;
  self.currentImageView = self.imageViewList[self.currentIndex];
  [self.view addSubview:self.scrollView];
  //    [self.view addSubview:self.rcImageProressView];
  [self.view addGestureRecognizer:longPress];
}

////取当前界面中一定数量的图片
- (void)getMessageFromModel:(RCMessageModel *)model {
  NSArray *imageArrayForward = [[RCIMClient sharedRCIMClient]
      getHistoryMessages:model.conversationType
                targetId:model.targetId
              objectName:[RCImageMessage getObjectName]
           baseMessageId:model.messageId
               isForward:true
                   count:2];
  NSArray *imageArrayBackward = [[RCIMClient sharedRCIMClient]
      getHistoryMessages:model.conversationType
                targetId:model.targetId
              objectName:[RCImageMessage getObjectName]
           baseMessageId:model.messageId
               isForward:false
                   count:2];

  NSMutableArray *ImageArr = [[NSMutableArray alloc] init];
  for (NSInteger j = [imageArrayForward count] - 1; j >= 0; j--) {
    RCMessage *rcMsg = [imageArrayForward objectAtIndex:j];
    RCMessageModel *modelindex = [RCMessageModel modelWithMessage:rcMsg];

    [ImageArr addObject:modelindex];
  }
  [ImageArr addObject:model];
  for (int i = 0; i < [imageArrayBackward count]; i++) {
    RCMessage *rcMsg = [imageArrayBackward objectAtIndex:i];
    RCMessageModel *modelindex = [RCMessageModel modelWithMessage:rcMsg];

    [ImageArr addObject:modelindex];
  }

  self.imageArray = ImageArr;
  for (int i = 0; i < ImageArr.count; i++) {
    RCMessageModel *modelindex1 = [ImageArr objectAtIndex:i];
    if (model.messageId == modelindex1.messageId) {
      self.currentIndex = i;
      self.preSelectIndex = self.currentIndex;
    }
  }
}

- (void)singleTap:(UITapGestureRecognizer *)sender {
  [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

- (void)doubleTap:(UITapGestureRecognizer *)sender {
  UIScrollView *subview = self.scrollView.subviews[self.currentIndex];
  if (subview.contentSize.width > self.view.frame.size.width) {
    [subview setZoomScale:1.0 animated:YES];
  } else {
    CGPoint touchPoint = [sender locationInView:subview];
    CGFloat newZoomScale = subview.maximumZoomScale;
    CGFloat xsize = self.view.frame.size.width / newZoomScale;
    CGFloat ysize = self.view.frame.size.height / newZoomScale;
    [subview zoomToRect:CGRectMake(touchPoint.x - xsize / 2,
                                   touchPoint.y - ysize / 2, xsize, ysize)
               animated:YES];
  }
}

- (void)longPressed:(id)sender {
  UILongPressGestureRecognizer *press = (UILongPressGestureRecognizer *)sender;
  if (press.state == UIGestureRecognizerStateEnded) {
    return;
  } else if (press.state == UIGestureRecognizerStateBegan) {
    UIActionSheet *actionSheet = [[UIActionSheet alloc]
                 initWithTitle:nil
                      delegate:self
             cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel",
                                                          @"RongCloudKit", nil)
        destructiveButtonTitle:nil
             otherButtonTitles:NSLocalizedStringFromTable(@"Save",
                                                          @"RongCloudKit", nil),
                               nil];
    [actionSheet showInView:self.view];
  }
}

#pragma mark - UIActionSheet Delegate
- (void)actionSheet:(UIActionSheet *)actionSheet
clickedButtonAtIndex:(NSInteger)buttonIndex {
    switch (buttonIndex) {
        case 0: {
            RCImageMessage *cImageMessage = self.imagemessageList[self.currentIndex];
            RCloudImageView *cImageView = self.imageViewList[self.currentIndex];
            [cImageView setImageURL:[NSURL URLWithString:cImageMessage.imageUrl]];
            UIImage *image = cImageView.image;

            UIImageWriteToSavedPhotosAlbum(
                                           image, self, @selector(image:didFinishSavingWithError:contextInfo:),
                                           nil);
        } break;
    }
}

- (void)image:(UIImage *)image
    didFinishSavingWithError:(NSError *)error
                 contextInfo:(void *)contextInfo {
  if (error != NULL) {
    //失败
    DebugLog(@" save image fail");
    UIAlertView *alert = [[UIAlertView alloc]
            initWithTitle:nil
                  message:NSLocalizedStringFromTable(@"SavePhotoFailed",
                                                     @"RongCloudKit", nil)
                 delegate:nil
        cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit",
                                                     nil)
        otherButtonTitles:nil];
    [alert show];
  } else {
    //成功
    DebugLog(@"save image suceed");
    UIAlertView *alert = [[UIAlertView alloc]
            initWithTitle:nil
                  message:NSLocalizedStringFromTable(@"SavePhotoSuccess",
                                                     @"RongCloudKit", nil)
                 delegate:nil
        cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit",
                                                     nil)
        otherButtonTitles:nil];
    [alert show];
  }
}

- (void)viewWillAppear:(BOOL)animated {
  [super viewWillAppear:animated];
  [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewDidAppear:(BOOL)animated {
  [super viewDidAppear:animated];
  [[UIApplication sharedApplication] setStatusBarHidden:YES];
}

//调整图片大小
- (void)resizeSubviews:(RCloudImageView *)ImageView {
  ImageView.contentMode = UIViewContentModeScaleAspectFit;
  CGFloat imageWidth = self.view.frame.size.width;
  CGFloat imageHeight = self.view.frame.size.height;
  [ImageView setFrame:CGRectMake(0, 0, imageWidth, imageHeight)];
}

- (void)refreshimage:(NSMutableArray *)imagearray {
  [self.imageViewList removeAllObjects];
  [self.imagemessageList removeAllObjects];

  for (int i = 0; i < imagearray.count; i++) {
    RCMessageModel *model = imagearray[i];
    RCImageMessage *imageMessage = (RCImageMessage *)model.content;
    [self.imagemessageList addObject:imageMessage];
    RCloudImageView *imageView = [[RCloudImageView alloc] init];
    imageView.delegate = self;
    //判断图片路径
    if ([imageMessage.imageUrl hasPrefix:@"http"]) {
      RCImageMessageProgressView *imageProressView =
          [[RCImageMessageProgressView alloc]
              initWithFrame:CGRectMake(0, 0, 135, 135)];
      imageProressView.label.hidden = YES;
      imageProressView.indicatorView.color = [UIColor blackColor];
      imageProressView.backgroundColor = [UIColor clearColor];
      [imageProressView
          setCenter:CGPointMake(self.view.bounds.size.width / 2,
                                self.view.bounds.size.height / 2)];
      [imageView addSubview:imageProressView];

      [self.imageProgressList setObject:imageProressView
                                 forKey:imageMessage.imageUrl];
      //判断是否已加载
      if ([[RCloudImageLoader sharedImageLoader]
              hasLoadedImageURL:[NSURL URLWithString:imageMessage.imageUrl]]) {
        [imageView setImageURL:[NSURL URLWithString:imageMessage.imageUrl]];
      } else {
        imageView.delegate = self;
        imageView.PlaceholderImage = imageMessage.thumbnailImage;
        [imageProressView startAnimating];
      }
    } else {
      imageView.PlaceholderImage = imageMessage.thumbnailImage;
      [imageView setImageURL:[NSURL URLWithString:imageMessage.imageUrl]];
      [self resizeSubviews:imageView];
    }

    imageView.tag = i + 1;
    [self resizeSubviews:imageView];

    [self.imageViewList addObject:imageView];
  }
}

- (void)refreshScrollView:(NSMutableArray *)imageViewList {

  while (self.scrollView.subviews.count > 0) {
    [self.scrollView.subviews[0] removeFromSuperview];
  }

  for (int i = 0; i < imageViewList.count; i++) {
    // scrollView
    UIScrollView *imagesrcoll = [[UIScrollView alloc]
        initWithFrame:CGRectMake(i * self.view.frame.size.width, 0,
                                 self.view.frame.size.width,
                                 self.view.frame.size.height)];
    [imagesrcoll setContentSize:CGSizeMake(self.view.bounds.size.width, 0)];
    imagesrcoll.delegate = self;

    UIImageView *imageView = imageViewList[i];
    RCloudImageView *newImageView = [[RCloudImageView alloc]
        initWithFrame:CGRectMake(0, 0, self.view.frame.size.width,
                                 self.view.frame.size.height)];
    newImageView.contentMode = UIViewContentModeScaleAspectFit;
    newImageView.PlaceholderImage = self.imagemessageList[i].thumbnailImage;
    newImageView.delegate = self;
    if ([self.imagemessageList[i].imageUrl hasPrefix:@"http"]) {
      RCImageMessageProgressView *imageProressView =
          [[RCImageMessageProgressView alloc]
              initWithFrame:CGRectMake(0, 0, 135, 135)];
      imageProressView.label.hidden = YES;
      imageProressView.indicatorView.color = [UIColor blackColor];
      imageProressView.backgroundColor = [UIColor clearColor];
      [imageProressView
          setCenter:CGPointMake(self.view.bounds.size.width / 2,
                                self.view.bounds.size.height / 2)];
      [self.imageProgressList setObject:imageProressView
                                 forKey:self.imagemessageList[i].imageUrl];
      [newImageView addSubview:imageProressView];
      //判断是否已加载
      if ([[RCloudImageLoader sharedImageLoader]
              hasLoadedImageURL:[NSURL URLWithString:self.imagemessageList[i]
                                                         .imageUrl]]) {
        [newImageView
            setImageURL:[NSURL
                            URLWithString:self.imagemessageList[i].imageUrl]];

      } else {
        newImageView.PlaceholderImage = self.imagemessageList[i].thumbnailImage;
        [imageProressView startAnimating];
        [newImageView
            setImageURL:[NSURL
                            URLWithString:self.imagemessageList[i].imageUrl]];
      }
    } else {

      newImageView.image = imageView.image;
    }
    [self resizeSubviews:newImageView];
    [imagesrcoll addSubview:newImageView];
    UITapGestureRecognizer *singleTap =
        [[UITapGestureRecognizer alloc] initWithTarget:self
                                                action:@selector(singleTap:)];
    [imagesrcoll addGestureRecognizer:singleTap];

    //双击放大或缩小
    UITapGestureRecognizer *doubleTap =
        [[UITapGestureRecognizer alloc] initWithTarget:self
                                                action:@selector(doubleTap:)];
    [singleTap requireGestureRecognizerToFail:doubleTap];
    doubleTap.numberOfTapsRequired = 2;
    imagesrcoll.userInteractionEnabled = YES;
    [imagesrcoll addGestureRecognizer:doubleTap];

    [self.scrollView addSubview:imagesrcoll];

    imagesrcoll.minimumZoomScale = 1.0;
    imagesrcoll.maximumZoomScale = 2.0;
    [imagesrcoll setZoomScale:1.0];
  }
}

#pragma mark - UIScrollViewDelegate
- (void)imageViewLoadedImage:(RCloudImageView *)imageView {
  //图片加载成功后，去掉加载中的标识
  RCImageMessageProgressView *imageProressView =
      self.imageProgressList[imageView.imageURL.absoluteString];
  if (imageProressView) {
    [imageProressView stopAnimating];
    [imageProressView setHidden:YES];
  }
}

- (void)imageViewFailedToLoadImage:(RCloudImageView *)imageView
                             error:(NSError *)error {
  [NSTimer scheduledTimerWithTimeInterval:60.0
                                   target:self
                                 selector:@selector(action:)
                                 userInfo:imageView
                                  repeats:NO];
}

- (void)action:(NSTimer *)scheduledTimer {
  RCloudImageView *imageView = (RCloudImageView *)(scheduledTimer.userInfo);
  NSString *imageUrl = [imageView.imageURL absoluteString];
    RCImageMessageProgressView *imageProressView =
    self.imageProgressList[imageUrl];
    if (imageProressView) {
        [imageProressView stopAnimating];
        [imageProressView setHidden:YES];
    }

  if ([imageUrl hasPrefix:@"http"]) {
    UIImage *image =
        [RCKitUtility imageNamed:@"broken" ofBundle:@"RongCloud.bundle"];
    imageView.image = nil;
    UIImageView *imageViewTip = [[UIImageView alloc] initWithImage:image];
    [imageViewTip setFrame:CGRectMake(0, 0, 81, 60)];
    [imageViewTip setCenter:CGPointMake(self.view.frame.size.width / 2,
                                        self.view.frame.size.height / 2)];
    [imageView addSubview:imageViewTip];
    UILabel *failLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(self.view.frame.size.width / 2 - 75,
                                 self.view.frame.size.height / 2 + 44, 150,
                                 30)];
    failLabel.text =
        NSLocalizedStringFromTable(@"ImageLoadFailed", @"RongCloudKit", nil);
    failLabel.textAlignment = NSTextAlignmentCenter;
    failLabel.textColor = HEXCOLOR(0x999999);
    [imageView addSubview:failLabel];
  } else {
    UIImage *image =
        [RCKitUtility imageNamed:@"exclamation" ofBundle:@"RongCloud.bundle"];
    imageView.image = nil;
    UIImageView *imageViewTip = [[UIImageView alloc] initWithImage:image];
    [imageViewTip setFrame:CGRectMake(0, 0, 71, 71)];
    [imageViewTip setCenter:CGPointMake(self.view.frame.size.width / 2,
                                        self.view.frame.size.height / 2)];
    [imageView addSubview:imageViewTip];
    UILabel *failLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(self.view.frame.size.width / 2 - 75,
                                 self.view.frame.size.height / 2 + 49.5, 150,
                                 30)];
    failLabel.text = NSLocalizedStringFromTable(@"ImageHasBeenDeleted",
                                                @"RongCloudKit", nil);
    failLabel.textAlignment = NSTextAlignmentCenter;
    failLabel.textColor = HEXCOLOR(0x999999);
    [imageView addSubview:failLabel];
  }
}

// scrollView 开始拖动

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
  self.newContentOffsetX = self.scrollView.contentOffset.x;
  if (self.scrollView != scrollView) {
    return;
  }

  if (self.newContentOffsetX < self.ContentOffset)
    self.currentImageView = self.imageViewList[self.currentIndex];
  self.currentIndex = scrollView.contentOffset.x / self.view.frame.size.width;

  RCImageMessage *cImageMessage = self.imagemessageList[self.currentIndex];
  RCloudImageView *cImageView = self.imageViewList[self.currentIndex];
  cImageView.delegate = self;

  if ([cImageMessage.imageUrl hasPrefix:@"http"]) {
    //判断当前图片是否已记载大图，如果已加载，跳过；如果没有，加载
    if ([[RCloudImageLoader sharedImageLoader]
            hasLoadedImageURL:[NSURL URLWithString:cImageMessage.imageUrl]]) {
      [cImageView setImageURL:[NSURL URLWithString:cImageMessage.imageUrl]];
      [self resizeSubviews:cImageView];
    } else {
      cImageView.delegate = self;
      cImageView.PlaceholderImage = cImageMessage.thumbnailImage;
      [cImageView setImageURL:[NSURL URLWithString:cImageMessage.imageUrl]];
      [self resizeSubviews:cImageView];
    }
  } else {
    cImageView.PlaceholderImage = cImageMessage.thumbnailImage;
    [cImageView setImageURL:[NSURL URLWithString:cImageMessage.imageUrl]];
    [self resizeSubviews:cImageView];
  }
  if (self.preSelectIndex != self.currentIndex) {
    [self refreshScrollView:self.imageViewList];
    self.preSelectIndex = self.currentIndex;
  }
  //当左滑到第二张图片，或快速滑动到第一张图片时，获取更多的历史数据中的图片消息
  if (self.newContentOffsetX < self.ContentOffset &&
      (self.currentIndex == 0 || self.currentIndex == 1)) {
    NSArray *imageMessageForward = [[RCIMClient sharedRCIMClient]
        getHistoryMessages:self.imageArray[0].conversationType
                  targetId:self.imageArray[0].targetId
                objectName:[RCImageMessage getObjectName]
             baseMessageId:self.imageArray[0].messageId
                 isForward:true
                     count:5];

    //判断是否已经没有图片可取了
    if ([imageMessageForward count] > 0) {
      NSMutableArray *imageArr = [[NSMutableArray alloc] init];
      for (NSInteger j = [imageMessageForward count] - 1; j >= 0; j--) {
        RCMessage *rcMsg = [imageMessageForward objectAtIndex:j];
        RCMessageModel *modelindex = [RCMessageModel modelWithMessage:rcMsg];

        [imageArr addObject:modelindex];
      }
      NSMutableArray *imageList;
      NSMutableArray *msgList;
      if (self.currentIndex == 0) {
        imageList = [[NSMutableArray alloc]
            initWithObjects:self.imageViewList[0], self.imageViewList[1], nil];
        msgList = [[NSMutableArray alloc]
            initWithObjects:self.imagemessageList[0], self.imagemessageList[1],
                            nil];
      } else {
        imageList = [[NSMutableArray alloc]
            initWithObjects:self.imageViewList[0], self.imageViewList[1],
                            self.imageViewList[2], nil];
        msgList = [[NSMutableArray alloc]
            initWithObjects:self.imagemessageList[0], self.imagemessageList[1],
                            self.imagemessageList[2], nil];
      }
      //把拉取到的图片放进数组里
      [self refreshimage:imageArr];
      //把当前图片列表中的前三张加进新的列表中，确保从当前第二张往前往后滑动时都可以正常进行
      for (int i = 0; i < [imageList count]; i++) {
        [self.imageViewList addObject:[imageList objectAtIndex:i]];
        [self.imagemessageList addObject:[msgList objectAtIndex:i]];
      }
      //添加到scrollView
      [self refreshScrollView:self.imageViewList];
      //刷新self.imageArray
      [imageArr addObject:self.imageArray[0]];
      [imageArr addObject:self.imageArray[1]];
      if (self.currentIndex == 1) {
        [imageArr addObject:self.imageArray[2]];
      }
      [self.imageArray removeAllObjects];
      for (int i = 0; i < imageArr.count; i++) {
        [self.imageArray addObject:imageArr[i]];
      }
      //设置ContentSize
      [self.scrollView
          setContentSize:CGSizeMake([imageArr count] *
                                        self.view.bounds.size.width,
                                    0)];
      //更新当前图片的索引和坐标
      self.currentIndex = [imageArr count] - 2;
      self.preSelectIndex = self.currentIndex;
      [scrollView
          setContentOffset:CGPointMake(self.newContentOffsetX +
                                           imageMessageForward.count *
                                               self.view.frame.size.width,
                                       0)];
      self.currentImageView = self.imageViewList[self.currentIndex];
    }
  } else if (self.newContentOffsetX > self.ContentOffset &&
             (self.currentIndex == self.imageArray.count - 1 ||
              self.currentIndex == self.imageArray.count - 2)) {
    //当右滑到倒数第二张图片或快速滑动到最后一张图片时，获取更多的历史数据中的图片消息
    NSArray *imageMessagebackward = [[RCIMClient sharedRCIMClient]
        getHistoryMessages:self.imageArray[self.imageArray.count - 1]
                               .conversationType
                  targetId:self.imageArray[self.imageArray.count - 1].targetId
                objectName:[RCImageMessage getObjectName]
             baseMessageId:self.imageArray[self.imageArray.count - 1].messageId
                 isForward:false
                     count:5];
    //判断是否已经没有图片可取了
    if ([imageMessagebackward count] > 0) {
      NSMutableArray *imageArr = [[NSMutableArray alloc] init];

      for (int i = 0; i < [imageMessagebackward count]; i++) {
        RCMessage *rcMsg = [imageMessagebackward objectAtIndex:i];
        RCMessageModel *modelindex = [RCMessageModel modelWithMessage:rcMsg];
        [imageArr addObject:modelindex];
      }
      //如果当前图片多于两张，把当前图片列表中的最后三张加进新的列表中，确保从当前倒数第二张往前往后滑动时都可以正常进行；如果当前图片只有两张，全部加进新的列表中。
      NSMutableArray *imageList;
      NSMutableArray *msgList;
      if (self.currentIndex == self.imageArray.count - 2 &&
          self.imageArray.count > 2) {
        imageList = [[NSMutableArray alloc]
            initWithObjects:self.imageViewList[self.imageViewList.count - 3],
                            self.imageViewList[self.imageViewList.count - 2],
                            self.imageViewList[self.imageViewList.count - 1],
                            nil];
        msgList = [[NSMutableArray alloc]
            initWithObjects:
                self.imagemessageList[self.imagemessageList.count - 3],
                self.imagemessageList[self.imagemessageList.count - 2],
                self.imagemessageList[self.imagemessageList.count - 1], nil];
      } else {
        imageList = [[NSMutableArray alloc]
            initWithObjects:self.imageViewList[self.imageViewList.count - 2],
                            self.imageViewList[self.imageViewList.count - 1],
                            nil];
        msgList = [[NSMutableArray alloc]
            initWithObjects:
                self.imagemessageList[self.imagemessageList.count - 2],
                self.imagemessageList[self.imagemessageList.count - 1], nil];
      }
      //把拉取到的图片放进数组里
      [self refreshimage:imageArr];
      for (NSInteger i = imageList.count - 1; i >= 0; i--) {
        [self.imageViewList insertObject:[imageList objectAtIndex:i] atIndex:0];
        [self.imagemessageList insertObject:[msgList objectAtIndex:i]
                                    atIndex:0];
      }
      //添加到scrollView
      [self refreshScrollView:self.imageViewList];
      //刷新self.imageArray
      [imageArr insertObject:self.imageArray[self.imageArray.count - 1]
                     atIndex:0];
      [imageArr insertObject:self.imageArray[self.imageArray.count - 2]
                     atIndex:0];
      if (self.currentIndex == self.imageArray.count - 2 &&
          self.imageArray.count > 2) {
        [imageArr insertObject:self.imageArray[self.imageArray.count - 3]
                       atIndex:0];
      }
      [self.imageArray removeAllObjects];
      for (int i = 0; i < imageArr.count; i++) {
        [self.imageArray addObject:imageArr[i]];
      }
      //设置ContentSize
      [self.scrollView
          setContentSize:CGSizeMake([imageArr count] *
                                        self.view.bounds.size.width,
                                    0)];

      //更新当前图片的索引和坐标
      //                  if (imageArr.count - imageMessagebackward.count > 2) {
      //                      self.currentIndex = 2;
      //                      self.preSelectIndex = self.currentIndex;
      //                      [self.scrollView
      //                       setContentOffset:CGPointMake(self.currentIndex *
      //                                                    self.view.frame.size.width,
      //                                                    0)];
      //                      self.currentImageView =
      //                      self.imageViewList[self.currentIndex];
      //                  } else {
      self.currentIndex = 1;
      self.preSelectIndex = self.currentIndex;
      [self.scrollView
          setContentOffset:CGPointMake(self.view.frame.size.width, 0)];
      self.currentImageView = self.imageViewList[self.currentIndex];
      //                  }
    }
  }

  self.ContentOffset = self.newContentOffsetX;
}

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
  if ([scrollView.subviews count] > 0) {
    return scrollView.subviews[0];
  } else {
    return nil;
  }
}

- (void)scrollViewDidZoom:(UIScrollView *)scrollView {
  //  [self setImageCenter:scrollView];
}

- (void)setImageCenter:(UIScrollView *)scrollView {
  UIScrollView *subview = self.scrollView.subviews[self.currentIndex];
  CGFloat offsetX =
      (subview.frame.size.width > subview.contentSize.width)
          ? (subview.frame.size.width - subview.contentSize.width) * 0.5
          : 0.0;
  CGFloat offsetY =
      (subview.frame.size.height > subview.contentSize.height)
          ? (subview.frame.size.height - subview.contentSize.height) * 0.5
          : 0.0;
  self.imageViewList[self.currentIndex].center =
      CGPointMake(subview.contentSize.width * 0.5 + offsetX,
                  subview.contentSize.height * 0.5 + offsetY);
}

- (void)backButtonAction {
  [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning];
  // Dispose of any resources that can be recreated.
}

- (void)viewWillDisappear:(BOOL)animated {
  [super viewWillDisappear:animated];
  [[UIApplication sharedApplication] setStatusBarHidden:NO];
}

@end
