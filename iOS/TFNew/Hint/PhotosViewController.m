//
//  PhotosViewController.m
//  Hint
//
//  Created by jack on 2/15/17.
//  Copyright © 2017 jack. All rights reserved.
//

#import "PhotosViewController.h"
#import <RongIMLib/RongIMLib.h>
#import "PhotoAlbumViewController.h"
#import "ForwardContactViewController.h"
#import "CMNavigationController.h"

@interface PhotosViewController () <UITableViewDataSource, UITableViewDelegate>
{
    UITableView *_tableView;
    
    int _rowHeight;
    int _imgWidth;
    
    BOOL _isSelectMode;
    
    NSMutableArray *_selected;
}
@property (nonatomic, strong) NSMutableDictionary *_data;
@property (nonatomic, strong) NSArray *_keys;
@property (nonatomic, assign) long long _lastMsgId;

@property (nonatomic, strong)  NSMutableArray *_selected;

@end


@implementation PhotosViewController
@synthesize _targetId;
@synthesize converType;

@synthesize _data;
@synthesize _lastMsgId;
@synthesize _keys;

@synthesize _selected;

- (void) viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBarHidden = NO;

}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"聊天文件";
    
    self._data = [NSMutableDictionary dictionary];
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    NSArray *msgs = [[RCIMClient sharedRCIMClient] getLatestMessages:converType
                                            targetId:_targetId
                                               count:1];
    if([msgs count])
    {
        RCMessage *msg = [msgs objectAtIndex:0];
        self._lastMsgId = msg.messageId+1;
    }
    
    
    //int m = (int)SCREEN_WIDTH % 3;
    _imgWidth = (SCREEN_WIDTH - 6)/3;
    _rowHeight = _imgWidth+3;
    
    
    self._selected = [NSMutableArray array];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    [NSTimer scheduledTimerWithTimeInterval:0.1
                                     target:self
                                   selector:@selector(loadImageMessages:)
                                   userInfo:nil
                                    repeats:NO];
    
    
    UIButton *btnSelect = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSelect.frame = CGRectMake(0, 0, 50, 40);
    [btnSelect setTitle:@"选择" forState:UIControlStateNormal];
    btnSelect.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnSelect addTarget:self action:@selector(selectAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSelect setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSelect];

    
}

- (void) selectAction:(id)sender{
    
    _isSelectMode = YES;
    
    [_tableView reloadData];
}


- (void) loadImageMessages:(id)sender{
    
    
    
    NSArray*tmp = [[RCIMClient sharedRCIMClient] getHistoryMessages:converType
                                                           targetId:_targetId
                                                         objectName:RCImageMessageTypeIdentifier
                                                    oldestMessageId:0
                                                              count:200];
    
    //NSLog(@"11");
    
    for(int i = (int)[tmp count] - 1; i >= 0; i--)
    {
        RCMessage *imgMsg = [tmp objectAtIndex:i];
        
        NSTimeInterval time = imgMsg.receivedTime/1000;
        NSDate *date1 = [NSDate dateWithTimeIntervalSince1970:time];
        if(date1)
        {
            NSString *timeStr = @"";
            NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
            formatter.dateFormat = @"yyyy年MM月";
            timeStr = [NSString stringWithFormat:@"%@",[formatter stringFromDate:date1]];
            
            
            NSMutableArray *imgs = [_data objectForKey:timeStr];
            if(imgs == nil)
            {
                imgs = [NSMutableArray array];
                [_data setObject:imgs forKey:timeStr];
            }
            
            [imgs addObject:imgMsg];
            
        }
    }
    
    self._keys = [_data allKeys];
    
    [_tableView reloadData];
    
    
}

#pragma mark UITableView dataSource
- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIndentifier = @"PhotoRowCell";
    UITableViewCell *cell = (UITableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:CellIndentifier];
    }
    
    cell.accessoryType = UITableViewCellAccessoryNone;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    [[cell.contentView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    cell.backgroundColor = [UIColor clearColor];
    
    id key = [_keys objectAtIndex:indexPath.section];
    NSArray *datas = [_data objectForKey:key];

    //cell 1
    int idx = (int)indexPath.row * 3;
    if(idx < [datas count])
    {
        UIImageView *cellImg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, _imgWidth, _imgWidth)];
        [cell.contentView addSubview:cellImg];
        cellImg.layer.contentsGravity = kCAGravityResizeAspectFill;
        cellImg.clipsToBounds = YES;
        
        RCMessage *msg = [datas objectAtIndex:idx];
        RCImageMessage *imgMsg = (RCImageMessage*)msg.content;
        [cellImg setImage:imgMsg.thumbnailImage];
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = cellImg.frame;
        [cell.contentView addSubview:btn];
        btn.tag = indexPath.section * 10000 + idx;
        [btn addTarget:self action:@selector(clickedThumbImg:) forControlEvents:UIControlEventTouchUpInside];
        
        if(_isSelectMode)
        {
            UIButton *btnCheck = [UIButton buttonWithType:UIButtonTypeCustom];
            btnCheck.frame = CGRectMake(CGRectGetMaxX(cellImg.frame)-50, 0, 50, 50);
            [cell.contentView addSubview:btnCheck];
            [btnCheck setImage:[UIImage imageNamed:@"file_selected_n.png"] forState:UIControlStateNormal];
            [btnCheck setImageEdgeInsets:UIEdgeInsetsMake(-20, 0, 0, -20)];
            btnCheck.tag = indexPath.section * 10000 + idx;
            [btnCheck addTarget:self action:@selector(clickedCheckBtn:) forControlEvents:UIControlEventTouchUpInside];
            
            if([_selected containsObject:msg])
            {
                [btnCheck setImage:[UIImage imageNamed:@"file_selected.png"] forState:UIControlStateNormal];
            }
        }
 
    }
    idx++;
    if(idx < [datas count])
    {
        UIImageView *cellImg = [[UIImageView alloc] initWithFrame:CGRectMake(0+_imgWidth+3, 0, _imgWidth, _imgWidth)];
        [cell.contentView addSubview:cellImg];
        cellImg.layer.contentsGravity = kCAGravityResizeAspectFill;
        cellImg.clipsToBounds = YES;
        
        RCMessage *msg = [datas objectAtIndex:idx];
        RCImageMessage *imgMsg = (RCImageMessage*)msg.content;
        [cellImg setImage:imgMsg.thumbnailImage];
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = cellImg.frame;
        [cell.contentView addSubview:btn];
        btn.tag = indexPath.section * 10000 + idx;
        [btn addTarget:self action:@selector(clickedThumbImg:) forControlEvents:UIControlEventTouchUpInside];
        
        if(_isSelectMode)
        {
            UIButton *btnCheck = [UIButton buttonWithType:UIButtonTypeCustom];
            btnCheck.frame = CGRectMake(CGRectGetMaxX(cellImg.frame)-50, 0, 50, 50);
            [cell.contentView addSubview:btnCheck];
            [btnCheck setImage:[UIImage imageNamed:@"file_selected_n.png"] forState:UIControlStateNormal];
            [btnCheck setImageEdgeInsets:UIEdgeInsetsMake(-20, 0, 0, -20)];
            btnCheck.tag = indexPath.section * 10000 + idx;
            [btnCheck addTarget:self action:@selector(clickedCheckBtn:) forControlEvents:UIControlEventTouchUpInside];
            
            if([_selected containsObject:msg])
            {
                [btnCheck setImage:[UIImage imageNamed:@"file_selected.png"] forState:UIControlStateNormal];
            }
        }
    }
    idx++;
    if(idx < [datas count])
    {
        UIImageView *cellImg = [[UIImageView alloc] initWithFrame:CGRectMake(0+_imgWidth*2+6, 0, _imgWidth, _imgWidth)];
        [cell.contentView addSubview:cellImg];
        cellImg.layer.contentsGravity = kCAGravityResizeAspectFill;
        cellImg.clipsToBounds = YES;
        
        RCMessage *msg = [datas objectAtIndex:idx];
        RCImageMessage *imgMsg = (RCImageMessage*)msg.content;
        [cellImg setImage:imgMsg.thumbnailImage];
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = cellImg.frame;
        [cell.contentView addSubview:btn];
        btn.tag = indexPath.section * 10000 + idx;
        [btn addTarget:self action:@selector(clickedThumbImg:) forControlEvents:UIControlEventTouchUpInside];
        
        if(_isSelectMode)
        {
            UIButton *btnCheck = [UIButton buttonWithType:UIButtonTypeCustom];
            btnCheck.frame = CGRectMake(CGRectGetMaxX(cellImg.frame)-50, 0, 50, 50);
            [cell.contentView addSubview:btnCheck];
            [btnCheck setImage:[UIImage imageNamed:@"file_selected_n.png"] forState:UIControlStateNormal];
            [btnCheck setImageEdgeInsets:UIEdgeInsetsMake(-20, 0, 0, -20)];
            btnCheck.tag = indexPath.section * 10000 + idx;
            [btnCheck addTarget:self action:@selector(clickedCheckBtn:) forControlEvents:UIControlEventTouchUpInside];
            
            if([_selected containsObject:msg])
            {
                [btnCheck setImage:[UIImage imageNamed:@"file_selected.png"] forState:UIControlStateNormal];
            }
        }
    }
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    id key = [_keys objectAtIndex:section];
    NSArray *datas = [_data objectForKey:key];
    
    int count = (int)[datas count];
    int rows = count/3;
    if(count%3 != 0)
    {
        rows++;
    }
    
    return rows;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return [_keys count];
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return _rowHeight;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
 
    
    
    
    return 30;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 30)];
    header.backgroundColor = RGBA(0xf8, 0xf8, 0xf8, 0.8);
    
    
    UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, SCREEN_WIDTH, 30)];
    tL.font = [UIFont systemFontOfSize:15];
    tL.text = [_keys objectAtIndex:section];
    [header addSubview:tL];
    
    return header;
}


#pragma mark UITableView delegate



- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];

}

- (void) clickedThumbImg:(UIButton*)btn{
    
    
    int tag = (int)btn.tag;
    int sec = tag/10000;
    int idx = tag%10000;
    
    id key = [_keys objectAtIndex:sec];
    NSArray *datas = [_data objectForKey:key];
    if(idx < [datas count])
    {
        PhotoAlbumViewController *album = [[PhotoAlbumViewController alloc] init];
        album._picIndex = idx;
        album._pictures = datas;
        [self.navigationController pushViewController:album animated:NO];
    }
    
}

- (void) clickedCheckBtn:(UIButton*)btn{
    
    int tag = (int)btn.tag;
    int sec = tag/10000;
    int idx = tag%10000;
    
    id key = [_keys objectAtIndex:sec];
    NSArray *datas = [_data objectForKey:key];
    if(idx < [datas count])
    {
         RCMessage *msg = [datas objectAtIndex:idx];
        
        if([_selected containsObject:msg])
        {
            [_selected removeObject:msg];
        }
        else
        {
            [_selected addObject:msg];
        }
    }
    
    [_tableView reloadData];
    
    if([_selected count] > 0)
    {
        UIButton *btnSend = [UIButton buttonWithType:UIButtonTypeCustom];
        btnSend.frame = CGRectMake(0, 0, 50, 40);
        [btnSend setTitle:@"发送" forState:UIControlStateNormal];
        btnSend.titleLabel.font = [UIFont boldSystemFontOfSize:15];
        [btnSend addTarget:self action:@selector(sendAction:) forControlEvents:UIControlEventTouchUpInside];
        [btnSend setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSend];

    }
    else
    {
        UIButton *btnSelect = [UIButton buttonWithType:UIButtonTypeCustom];
        btnSelect.frame = CGRectMake(0, 0, 50, 40);
        [btnSelect setTitle:@"选择" forState:UIControlStateNormal];
        btnSelect.titleLabel.font = [UIFont boldSystemFontOfSize:15];
        [btnSelect addTarget:self action:@selector(selectAction:) forControlEvents:UIControlEventTouchUpInside];
        [btnSelect setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSelect];

    }
    
    
}

- (void) sendAction:(id)sender{
    
    ForwardContactViewController *choose = [[ForwardContactViewController alloc] init];
    choose._selectedImages = self._selected;
    CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:choose];
    [self presentViewController:navi
                       animated:YES
                     completion:^{
                         
                     }];
}


@end
