//
//  RCFilePreviewViewController.m
//  RongIMKit
//
//  Created by Jue on 16/7/29.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCFilePreviewViewController.h"
#import "RCIM.h"
#import "RCKitCommonDefine.h"
#import "RCKitUtility.h"
#import <RongIMLib/RongIMLib.h>

extern NSString *const RCKitDispatchDownloadMediaNotification;

@interface RCFilePreviewViewController ()

@property(nonatomic, strong) RCFileMessage *fileMessage;
@property(nonatomic, strong) UIWebView *webView;
@property(nonatomic, strong) UILabel *nameLabel;
@property(nonatomic, strong) UILabel *sizeLabel;
@property(nonatomic, strong) UILabel *progressLabel;
@property(nonatomic, strong) UIImageView *typeIconView;
@property(nonatomic, strong) UIProgressView *progressView;
@property(nonatomic, strong) UIButton *downloadButton;
@property(nonatomic, strong) UIButton *openInOtherAppButton;
@property(nonatomic, strong) UIButton *cancelButton;

@end

@implementation RCFilePreviewViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  self.view.backgroundColor = [UIColor whiteColor];
  self.title = NSLocalizedStringFromTable(@"PreviewFile", @"RongCloudKit", nil);

  //设置右键
  UIButton *rightBtn =
      [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 17.5, 17.5)];
  UIImage *rightImage =
      [RCKitUtility imageNamed:@"forwardIcon" ofBundle:@"RongCloud.bundle"];
  [rightBtn setImage:rightImage forState:UIControlStateNormal];
  [rightBtn addTarget:self
                action:@selector(moreAction)
      forControlEvents:UIControlEventTouchUpInside];
  self.navigationItem.rightBarButtonItem =
      [[UIBarButtonItem alloc] initWithCustomView:rightBtn];

  //设置左键
  UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
  backBtn.frame = CGRectMake(0, 6, 87, 23);
  UIImageView *backImage =
      [[UIImageView alloc] initWithImage:IMAGE_BY_NAMED(@"navigator_btn_back")];
  backImage.frame = CGRectMake(-6, 4, 10, 17);
  [backBtn addSubview:backImage];
  UILabel *backText = [[UILabel alloc] initWithFrame:CGRectMake(9, 4, 85, 17)];
  backText.text = NSLocalizedStringFromTable(@"Back", @"RongCloudKit", nil);
  [backText setBackgroundColor:[UIColor clearColor]];
  [backText setTextColor:[RCIM sharedRCIM].globalNavigationBarTintColor];
  [backBtn addSubview:backText];
  [backBtn addTarget:self
                action:@selector(clickBackBtn:)
      forControlEvents:UIControlEventTouchUpInside];
  self.navigationItem.leftBarButtonItem =
      [[UIBarButtonItem alloc] initWithCustomView:backBtn];

  [[NSNotificationCenter defaultCenter]
      addObserver:self
         selector:@selector(updateDownloadMediaStatus:)
             name:RCKitDispatchDownloadMediaNotification
           object:nil];

  if ([self isFileDownloaded] && [self isFileSupported]) {
    [self layoutAndPreviewFile];
  } else {
    [self layoutForShowFileInfo];
  }
  [self.view bringSubviewToFront:_cancelButton];
}

- (void)layoutForShowFileInfo {
  self.webView.hidden = YES;
  self.navigationItem.rightBarButtonItem.enabled = NO;

  self.typeIconView.hidden = NO;
  self.nameLabel.hidden = NO;
  self.sizeLabel.hidden = NO;
  self.cancelButton.hidden = YES;
  self.progressView.hidden = YES;
  self.progressLabel.hidden = YES;
  if ([self isFileDownloaded]) {
    self.downloadButton.hidden = YES;
    self.openInOtherAppButton.hidden = NO;
  } else {
    self.downloadButton.hidden = NO;
    self.openInOtherAppButton.hidden = YES;
  }
}

- (void)layoutForDownloading {
  self.webView.hidden = YES;
  self.navigationItem.rightBarButtonItem.enabled = NO;

  self.typeIconView.hidden = NO;
  self.nameLabel.hidden = NO;
  self.sizeLabel.hidden = YES;
  self.downloadButton.hidden = YES;
  self.openInOtherAppButton.hidden = YES;
  self.cancelButton.hidden = NO;
  self.progressView.hidden = NO;
  self.progressLabel.hidden = NO;
}

- (void)layoutAndPreviewFile {
  self.webView.hidden = NO;
  self.navigationItem.rightBarButtonItem.enabled = YES;

  self.typeIconView.hidden = YES;
  self.nameLabel.hidden = YES;
  self.sizeLabel.hidden = YES;
  self.downloadButton.hidden = YES;
  self.openInOtherAppButton.hidden = YES;
  self.cancelButton.hidden = YES;
  self.progressView.hidden = YES;
  self.progressLabel.hidden = YES;

  if ([self.fileMessage.localPath hasSuffix:@".txt"]) {
    NSArray *encodeList = @[@(NSUTF8StringEncoding), @(CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingGB_18030_2000)), @(NSUTF16StringEncoding), @(NSUTF32StringEncoding)];
    NSError *error = nil;
    NSString *textContent = nil;
    for (NSNumber *encode in encodeList) {
      NSError *error = nil;
      textContent = [NSString stringWithContentsOfFile:self.fileMessage.localPath encoding:[encode integerValue] error:&error];
      if (!error) {
        break;
      }
    }
    if (!textContent) {
      textContent = [NSString stringWithContentsOfFile:self.fileMessage.localPath encoding:NSUTF8StringEncoding error:&error];
    }
    [self.webView loadHTMLString:textContent baseURL:nil];
  } else {
    NSURL *fileURL = [NSURL fileURLWithPath:self.fileMessage.localPath];
    [self.webView loadRequest:[NSURLRequest requestWithURL:fileURL]];
  }
}

- (UIWebView *)webView {
  if (!_webView) {
    _webView = [[UIWebView alloc]
        initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width,
                                 [UIScreen mainScreen].bounds.size.height -
                                     64)];
    _webView.scalesPageToFit = YES;

    [self.view addSubview:_webView];
  }

  return _webView;
}

- (UIImageView *)typeIconView {
  if (!_typeIconView) {
    _typeIconView = [[UIImageView alloc]
        initWithFrame:CGRectMake((self.view.bounds.size.width - 75) / 2, 30, 75,
                                 75)];
    NSString *fileTypeIcon =
        [RCKitUtility getFileTypeIcon:self.fileMessage.type];
    _typeIconView.image =
        [RCKitUtility imageNamed:fileTypeIcon ofBundle:@"RongCloud.bundle"];

    [self.view addSubview:_typeIconView];
  }

  return _typeIconView;
}

- (UILabel *)nameLabel {
  if (!_nameLabel) {
    _nameLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(10, 122, self.view.bounds.size.width - 10 * 2,
                                 21)];
    _nameLabel.font = [UIFont systemFontOfSize:16.0f];
    _nameLabel.text = self.fileMessage.name;
    _nameLabel.textAlignment = UITextAlignmentCenter;
    _nameLabel.textColor = HEXCOLOR(0x343434);
    [self.view addSubview:_nameLabel];
  }
  return _nameLabel;
}

- (UILabel *)sizeLabel {
  if (!_sizeLabel) {
    _sizeLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(0, 151, self.view.bounds.size.width, 12)];
    _sizeLabel.font = [UIFont systemFontOfSize:13.0f];
    _sizeLabel.text =
        [RCKitUtility getReadableStringForFileSize:self.fileMessage.size];
    _sizeLabel.textAlignment = UITextAlignmentCenter;
    _sizeLabel.textColor = HEXCOLOR(0xa8a8a8);
    [self.view addSubview:_sizeLabel];
  }
  return _sizeLabel;
}

- (UILabel *)progressLabel {
  if (!_progressLabel) {
    _progressLabel = [[UILabel alloc]
        initWithFrame:CGRectMake(10, 151, self.view.bounds.size.width - 10 * 2,
                                 21)];
    _progressLabel.textColor = HEXCOLOR(0xa8a8a8);
    _progressLabel.textAlignment = UITextAlignmentCenter;
    _progressLabel.font = [UIFont systemFontOfSize:13.0f];
    [self.view addSubview:_progressLabel];
  }
  return _progressLabel;
}

- (UIProgressView *)progressView {
  if (!_progressView) {
    _progressView = [[UIProgressView alloc]
        initWithFrame:CGRectMake(10, 184, self.view.bounds.size.width - 10 * 3,
                                 8)];
    _progressView.transform = CGAffineTransformMakeScale(1.0f, 4.0f);
    _progressView.progressViewStyle = UIProgressViewStyleDefault;
    _progressView.progressTintColor = HEXCOLOR(0x0099ff);
    [self.view addSubview:_progressView];
  }
  return _progressView;
}

- (UIButton *)cancelButton {
  if (!_cancelButton) {
    _cancelButton = [[UIButton alloc]
        initWithFrame:CGRectMake(self.view.bounds.size.width - 10 - 24, 173, 24,
                                 24)];
    [_cancelButton setImage:[RCKitUtility imageNamed:@"cancelButton"
                                            ofBundle:@"RongCloud.bundle"]
                   forState:UIControlStateNormal];
    [_cancelButton addTarget:self
                      action:@selector(cancelFileDownload)
            forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_cancelButton];
  }
  return _cancelButton;
}

- (UIButton *)downloadButton {
  if (!_downloadButton) {
    _downloadButton = [[UIButton alloc]
        initWithFrame:CGRectMake(10, 197, self.view.bounds.size.width - 10 * 2,
                                 40)];
    _downloadButton.backgroundColor = HEXCOLOR(0x0099ff);
    _downloadButton.layer.cornerRadius = 5.0f;
    _downloadButton.layer.borderWidth = 0.5f;
    _downloadButton.layer.borderColor = [HEXCOLOR(0x0181dd) CGColor];
    [_downloadButton setTitle:NSLocalizedStringFromTable(
                                  @"StartDownloadingFile", @"RongCloudKit", nil)
                     forState:UIControlStateNormal];
    [_downloadButton addTarget:self
                        action:@selector(startFileDownLoad)
              forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_downloadButton];
  }
  return _downloadButton;
}

- (UIButton *)openInOtherAppButton {
  if (!_openInOtherAppButton) {
    _openInOtherAppButton = [[UIButton alloc]
        initWithFrame:CGRectMake(10, 197, self.view.bounds.size.width - 10 * 2,
                                 40)];
    _openInOtherAppButton.backgroundColor = HEXCOLOR(0x0099ff);
    _openInOtherAppButton.layer.cornerRadius = 5.0f;
    _openInOtherAppButton.layer.borderWidth = 0.5f;
    _openInOtherAppButton.layer.borderColor = [HEXCOLOR(0x0181dd) CGColor];
    [_openInOtherAppButton
        setTitle:NSLocalizedStringFromTable(@"OpenFileInOtherApp",
                                            @"RongCloudKit", nil)
        forState:UIControlStateNormal];
    [_openInOtherAppButton addTarget:self
                               action:@selector(openCurrentFileInOtherApp)
                     forControlEvents:UIControlEventTouchUpInside];

    [self.view addSubview:_openInOtherAppButton];
  }
  return _openInOtherAppButton;
}

- (void)moreAction {
  UIActionSheet *actionSheet = [[UIActionSheet alloc]
               initWithTitle:nil
                    delegate:self
           cancelButtonTitle:NSLocalizedStringFromTable(@"Cancel",
                                                        @"RongCloudKit", nil)
      destructiveButtonTitle:nil
           otherButtonTitles:NSLocalizedStringFromTable(@"OpenFileInOtherApp",
                                                        @"RongCloudKit", nil),
                             nil];
  [actionSheet showInView:self.view];
}

- (void)actionSheet:(UIActionSheet *)actionSheet
    clickedButtonAtIndex:(NSInteger)buttonIndex {
  if (buttonIndex == 0) {
    [self openInOtherApp:self.fileMessage.localPath];
  }
}

- (void)clickBackBtn:(id)sender {
  [self.navigationController popViewControllerAnimated:YES];
}

- (void)openInOtherApp:(NSString *)localPath {
  UIActivityViewController *activityVC = [[UIActivityViewController alloc]
      initWithActivityItems:@[ [NSURL fileURLWithPath:localPath] ]
      applicationActivities:nil];
  [self presentViewController:activityVC animated:YES completion:nil];
}

- (void)openCurrentFileInOtherApp {
  [self openInOtherApp:self.fileMessage.localPath];
}

- (void)startFileDownLoad {
  [self downloading:0];
  [[RCIM sharedRCIM] downloadMediaMessage:self.messageModel.messageId
      progress:^(int progress) {

      }
      success:^(NSString *mediaPath) {

      }
      error:^(RCErrorCode errorCode) {

      }
      cancel:^{

      }];
}

- (void)cancelFileDownload {
  [[RCIM sharedRCIM] cancelDownloadMediaMessage:self.messageModel.messageId];
  _progressView.progress = 0;
}

- (void)updateDownloadMediaStatus:(NSNotification *)notify {
  NSDictionary *statusDic = notify.userInfo;
  if (self.messageModel.messageId == [statusDic[@"messageId"] longValue]) {
    if ([statusDic[@"type"] isEqualToString:@"progress"]) {
      float progress = (float)[statusDic[@"progress"] intValue] / 100.0f;
      [self downloading:progress];
    } else if ([statusDic[@"type"] isEqualToString:@"success"]) {
      dispatch_async(dispatch_get_main_queue(), ^{
        self.fileMessage.localPath = statusDic[@"mediaPath"];
        if ([self isFileSupported]) {
          [self layoutAndPreviewFile];
        } else {
          [self layoutForShowFileInfo];
        }
      });
    } else if ([statusDic[@"type"] isEqualToString:@"error"]) {
      dispatch_async(dispatch_get_main_queue(), ^{
        [self layoutForShowFileInfo];

        UIAlertView *alert = [[UIAlertView alloc]
                initWithTitle:nil
                      message:NSLocalizedStringFromTable(@"FileDownloadFailed",
                                                         @"RongCloudKit", nil)
                     delegate:nil
            cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit",
                                                         nil)
            otherButtonTitles:nil];
        [alert show];
      });
    } else if ([statusDic[@"type"] isEqualToString:@"cancel"]) {
      dispatch_async(dispatch_get_main_queue(), ^{
        [self layoutForShowFileInfo];

        UIAlertView *alert = [[UIAlertView alloc]
                initWithTitle:nil
                      message:NSLocalizedStringFromTable(
                                  @"FileDownloadCanceled", @"RongCloudKit", nil)
                     delegate:nil
            cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit",
                                                         nil)
            otherButtonTitles:nil];
        [alert show];
      });
    }
  }
}

- (RCFileMessage *)fileMessage {
  if (!_fileMessage) {
    return (RCFileMessage *)self.messageModel.content;
  }
  return _fileMessage;
}

- (void)downloading:(float)progress {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self layoutForDownloading];
    [self.progressView setProgress:progress animated:YES];
    self.progressLabel.text = [NSString
                               stringWithFormat:@"%@(%@/%@)",
                               NSLocalizedStringFromTable(@"FileIsDownloading",
                                                          @"RongCloudKit", nil),
                               [RCKitUtility
                                getReadableStringForFileSize:progress *
                                self.fileMessage
                                .size],
                               [RCKitUtility
                                getReadableStringForFileSize:self.fileMessage
                                .size]];
  });
}

- (BOOL)isFileDownloading {
  // todo
  return NO;
}

- (BOOL)isFileDownloaded {
  if (self.fileMessage.localPath.length > 0 &&
      [RCFileUtility isFileExist:self.fileMessage.localPath]) {
    return YES;
  } else {
    return NO;
  }
}

- (BOOL)isFileSupported {
  if (![[RCKitUtility getFileTypeIcon:self.fileMessage.type] isEqualToString:@"OtherFile"]) {
    return YES;
  } else {
    return NO;
  }
}

@end
