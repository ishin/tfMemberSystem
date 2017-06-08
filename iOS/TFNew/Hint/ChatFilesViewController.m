//
//  ChatFilesViewController.m
//  Hint
//
//  Created by chen jack on 2017/5/7.
//  Copyright © 2017年 jack. All rights reserved.
//

#import "ChatFilesViewController.h"
#import "ChatPhotosView.h"
#import "ChatFilesView.h"

@interface ChatFilesViewController () <UIScrollViewDelegate, ChatFilesViewDelegate, ChatPhotosViewDelegate>
{
    ChatPhotosView *_photosView;
    ChatFilesView  *_videosView;
    ChatFilesView  *_filesView;
    
    UIButton *_tab0;
    UIButton *_tab1;
    UIButton *_tab2;
    
    int _tabIndex;
    
    UILabel  *_selectLine;
    
    UIScrollView *_content;
    
    BOOL _isSelectM;
    
    UIView *_footerView;
}
@end

@implementation ChatFilesViewController
@synthesize converType;
@synthesize _targetId;


- (void) viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBarHidden = NO;
    
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"聊天文件";
    
    UIButton *btnSelect = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSelect.frame = CGRectMake(0, 0, 50, 40);
    [btnSelect setTitle:@"选择" forState:UIControlStateNormal];
    btnSelect.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnSelect addTarget:self action:@selector(selectAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSelect setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSelect];

    UIView *typeHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 40)];
    typeHeaderView.backgroundColor = [UIColor clearColor];
    
    
    int w = SCREEN_WIDTH;
    int w1 = w/3;
    
    ////
    _tab0 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tab0.frame = CGRectMake(0, 0, w1, 40);
    [typeHeaderView addSubview:_tab0];
    [_tab0 setTitle:@"图片" forState:UIControlStateNormal];
    
    
    _tab1 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tab1.frame = CGRectMake(w1, 0, w1, 40);
    [typeHeaderView addSubview:_tab1];
    [_tab1 setTitle:@"视频" forState:UIControlStateNormal];
    
    _tab2 = [UIButton buttonWithType:UIButtonTypeCustom];
    _tab2.frame = CGRectMake(w1*2, 0, w1, 40);
    [typeHeaderView addSubview:_tab2];
    [_tab2 setTitle:@"其他" forState:UIControlStateNormal];
    
    _tabIndex = 0;
    
    [self.view addSubview:typeHeaderView];
    
    [_tab0 setTitleColor:COLOR_TEXT_A forState:UIControlStateNormal];
    [_tab1 setTitleColor:COLOR_TEXT_A forState:UIControlStateNormal];
    [_tab2 setTitleColor:COLOR_TEXT_A forState:UIControlStateNormal];
    
    _tab0.titleLabel.font = [UIFont systemFontOfSize:16];
    _tab1.titleLabel.font = [UIFont systemFontOfSize:16];
    _tab2.titleLabel.font = [UIFont systemFontOfSize:16];
    
    _tab0.tag = 0;
    _tab1.tag = 1;
    _tab2.tag = 2;
    [_tab0 addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_tab1 addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_tab2 addTarget:self action:@selector(tabButtonClicked:) forControlEvents:UIControlEventTouchUpInside];

    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 39, SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [typeHeaderView addSubview:line];
    
    
    _selectLine = [[UILabel alloc] initWithFrame:CGRectMake(0, 36, 60, 4)];
    _selectLine.backgroundColor = THEME_RED_COLOR;
    [typeHeaderView addSubview:_selectLine];
    _selectLine.center = CGPointMake(_tab0.center.x, _selectLine.center.y);
    
    
    _content = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 40, SCREEN_WIDTH, SCREEN_HEIGHT-104)];
    [self.view insertSubview:_content atIndex:0];
    _content.bounces = NO;
    _content.contentSize = CGSizeMake(SCREEN_WIDTH*3, SCREEN_HEIGHT-104);
    _content.pagingEnabled = YES;
    _content.showsHorizontalScrollIndicator = NO;
    _content.delegate = self;
    _content.scrollEnabled = NO;
    
    _photosView = [[ChatPhotosView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64-40)];
    _photosView._targetId = _targetId;
    _photosView.converType = converType;
    [_photosView initChat];
    _photosView._viewCtrl = self;
    _photosView.delegate = self;
    [_content addSubview:_photosView];
    
    _videosView = [[ChatFilesView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64-40)];
    _videosView._targetId = _targetId;
    _videosView.converType = converType;
    [_videosView initChat];
    _videosView._viewCtrl = self;
    _videosView._isVideo = YES;
    _videosView.delegate = self;
    [_content addSubview:_videosView];
    
    _filesView = [[ChatFilesView alloc] initWithFrame:CGRectMake(SCREEN_WIDTH*2, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64-40)];
    _filesView._targetId = _targetId;
    _filesView.converType = converType;
    [_filesView initChat];
    _filesView._viewCtrl = self;
    _filesView.delegate = self;
    [_content addSubview:_filesView];

    _isSelectM = NO;
    
    
    _footerView = [[UIView alloc] initWithFrame:CGRectMake(0, SCREEN_HEIGHT - 64 - 40, SCREEN_WIDTH, 40)];
    _footerView.backgroundColor = [UIColor whiteColor];
    
    UIButton *btnForward = [UIButton buttonWithType:UIButtonTypeCustom];
    btnForward.frame = CGRectMake(10, 0, 50, 40);
    [btnForward setImage:[UIImage imageNamed:@"retransmission.png"] forState:UIControlStateNormal];
    [_footerView addSubview:btnForward];
    [btnForward addTarget:self action:@selector(forwarFiles:) forControlEvents:UIControlEventTouchUpInside];
    
    UIButton *btnDel = [UIButton buttonWithType:UIButtonTypeCustom];
    btnDel.frame = CGRectMake(SCREEN_WIDTH-60, 0, 50, 40);
    [btnDel setImage:[UIImage imageNamed:@"delete_files.png"] forState:UIControlStateNormal];
    [_footerView addSubview:btnDel];
    [btnDel addTarget:self action:@selector(deleteFile:) forControlEvents:UIControlEventTouchUpInside];
}

- (void) forwarFiles:(id)sender{
    
    if(_tabIndex == 0)
    {
        [_photosView forwardAction];
    }
    else if(_tabIndex == 1)
    {
        [_videosView forwardAction];
    }
    else if(_tabIndex == 2)
    {
        [_filesView forwardAction];
    }
}

- (void) deleteFile:(id)sender{
 
    if(_tabIndex == 0)
    {
        [_photosView deletAction];
    }
    else if(_tabIndex == 1)
    {
        [_videosView deletAction];
    }
    else if(_tabIndex == 2)
    {
        [_filesView deletAction];
    }
}



- (void) tabButtonClicked:(UIButton*)sender{
    
    int bTag = (int)sender.tag;
    
    if(_tabIndex != bTag)
    {
        _tabIndex = bTag;
        
        [_content setContentOffset:CGPointMake(_tabIndex*SCREEN_WIDTH, 0)];
        
       
    }
    
    _selectLine.center = CGPointMake(sender.center.x, _selectLine.center.y);
    
    [_footerView removeFromSuperview];
    if(_tabIndex == 0)
    {
        NSArray *data = [_photosView selectedData];
        if([data count])
        {
            [self.view addSubview:_footerView];
        }
    }
    else if(_tabIndex == 1)
    {
        NSArray *data = [_videosView selectedData];
        if([data count])
        {
            [self.view addSubview:_footerView];
        }
    }
    else if(_tabIndex == 2)
    {
        NSArray *data = [_filesView selectedData];
        if([data count])
        {
            [self.view addSubview:_footerView];
        }
    }
}

- (void) selectAction:(id)sender{
    
   
    [_photosView selectAction:nil];
    [_filesView selectAction:nil];
    [_videosView selectAction:nil];
    
    UIButton *btnSend = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSend.frame = CGRectMake(0, 0, 50, 40);
    [btnSend setTitle:@"取消" forState:UIControlStateNormal];
    btnSend.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnSend addTarget:self action:@selector(cancelAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSend setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSend];

}

- (void) cancelAction:(id)sender{
    
    [_photosView cancelSelect];
    [_filesView cancelSelect];
    [_videosView cancelSelect];
    
    [_footerView removeFromSuperview];
   
    
    UIButton *btnSelect = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSelect.frame = CGRectMake(0, 0, 50, 40);
    [btnSelect setTitle:@"选择" forState:UIControlStateNormal];
    btnSelect.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnSelect addTarget:self action:@selector(selectAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSelect setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSelect];

}

- (void) didSelectFile{
    
    if(_tabIndex == 1)
    {
        NSArray *data = [_videosView selectedData];
        if([data count])
        {
            [self.view addSubview:_footerView];
        }
    }
    else if(_tabIndex == 2)
    {
        NSArray *data = [_filesView selectedData];
        if([data count])
        {
            [self.view addSubview:_footerView];
        }
    }
        
}
- (void) didSelectPhoto{
    
    if(_tabIndex == 0)
    {
        NSArray *data = [_photosView selectedData];
        if([data count])
        {
            [self.view addSubview:_footerView];
        }
    }
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
