//
//  ChatFilesView.m
//  Hint
//
//  Created by chen jack on 2017/5/7.
//  Copyright © 2017年 jack. All rights reserved.
//

#import "ChatFilesView.h"
#import "CMNavigationController.h"
#import "ForwardContactViewController.h"

#define  FILE_M  (1024*1024)

@interface ChatFilesView () <UITableViewDataSource, UITableViewDelegate>
{
    UITableView *_tableView;
    
    int _rowHeight;
    int _imgWidth;
    
    BOOL _isSelectMode;
    
    NSMutableArray *_selected;
}
@property (nonatomic, strong) NSMutableArray *_data;
@property (nonatomic, assign) long long _lastMsgId;

@property (nonatomic, strong)  NSMutableArray *_selected;
@property (nonatomic, strong)  NSArray *_fileExs;

@end


@implementation ChatFilesView
@synthesize converType;
@synthesize _targetId;

@synthesize _data;
@synthesize _lastMsgId;

@synthesize _selected;

@synthesize _viewCtrl;

@synthesize _isVideo;
@synthesize delegate;
@synthesize _fileExs;

- (id) initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame])
    {
        self._data = [NSMutableArray array];
        
        self.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
        

        _imgWidth = (SCREEN_WIDTH - 6)/3;
        _rowHeight = _imgWidth+3;
        
        
        self._selected = [NSMutableArray array];
        
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.backgroundColor = [UIColor clearColor];
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [self addSubview:_tableView];
    }
    
    return self;
}

- (void) initChat{
    
    NSArray *msgs = [[RCIMClient sharedRCIMClient] getLatestMessages:converType
                                                            targetId:_targetId
                                                               count:1];
    if([msgs count])
    {
        RCMessage *msg = [msgs objectAtIndex:0];
        self._lastMsgId = msg.messageId+1;
    }
    
    [NSTimer scheduledTimerWithTimeInterval:0.1
                                     target:self
                                   selector:@selector(loadImageMessages:)
                                   userInfo:nil
                                    repeats:NO];
}

- (void) selectAction:(id)sender{
    
    _isSelectMode = YES;
    
    [_tableView reloadData];
}

- (void) cancelSelect{
    
    [_selected removeAllObjects];
    
    _isSelectMode = NO;
    
    [_tableView reloadData];
}

- (NSArray *)selectedData{
    
    return _selected;
}

- (void) loadImageMessages:(id)sender{
    
    /*
     */
    self._fileExs = @[@"txt",@"doc",@"docx",@"xls",@"xlsx",@"ppt",@"pptx",@"pdf",
                      @"html",@"htm",@"js",@"xml",@"json",@"css",@"db",@"sql",
                      @"h",@"m",@"cpp",@"java",@"bat",@"dat",@"ini",@"xib",@"plist",
                      @"db-journal"];
    
    NSArray*tmp = [[RCIMClient sharedRCIMClient] getHistoryMessages:converType
                                                           targetId:_targetId
                                                         objectName:RCFileMessageTypeIdentifier
                                                    oldestMessageId:0
                                                              count:200];
    
    //NSLog(@"11");
    
    [_data removeAllObjects];
    
    for(int i = (int)[tmp count] - 1; i >= 0; i--)
    {
        RCMessage *msg = [tmp objectAtIndex:i];
        
        RCFileMessage *fileMsg = (RCFileMessage*)msg.content;
        NSString *ex = nil;
        if(fileMsg.type)
        {
            ex = [fileMsg.type lowercaseString];
        
        }
        
        BOOL video = NO;
        
        if([ex isEqualToString:@"mov"] || [ex isEqualToString:@"mp4"])
        {
            video = YES;
            
        }
        
        if(_isVideo)
        {
            if(video)
                [_data addObject:msg];
        }
        else
        {
            if(!video)
                [_data addObject:msg];
        }
        
    }

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
    cell.backgroundColor = [UIColor whiteColor];
    
    RCMessage* msg = [_data objectAtIndex:indexPath.row];
    
    id ct = msg.content;
    
    UIImageView *thumb = [[UIImageView alloc] initWithFrame:CGRectMake(40, 10, 60, 60)];
    thumb.image = [UIImage imageNamed:@"fileex_unknow.png"];
    [cell.contentView addSubview:thumb];
    
    UILabel *nameL = [[UILabel alloc] initWithFrame:CGRectMake(110, 10, SCREEN_WIDTH-120, 20)];
    nameL.font = [UIFont systemFontOfSize:15];
    [cell.contentView addSubview:nameL];
    
    UILabel *sizeL = [[UILabel alloc] initWithFrame:CGRectMake(110, 30, SCREEN_WIDTH-120, 20)];
    sizeL.font = [UIFont systemFontOfSize:14];
    [cell.contentView addSubview:sizeL];
    sizeL.textColor = COLOR_TEXT_2B;
    
    UILabel *dateL = [[UILabel alloc] initWithFrame:CGRectMake(110, 50, SCREEN_WIDTH-120, 20)];
    dateL.font = [UIFont systemFontOfSize:14];
    [cell.contentView addSubview:dateL];
    dateL.textColor = COLOR_TEXT_2B;
    
    if([ct isKindOfClass:[RCFileMessage class]])
    {
        RCFileMessage *fileMsg = (RCFileMessage*)msg.content;
        nameL.text = fileMsg.name;
        
        if(fileMsg.type)
        {
            NSString *ex = [fileMsg.type lowercaseString];
            
            NSRange range = [ex rangeOfString:@"application"];
            if(range.location != NSNotFound)
            {
                range = [fileMsg.name rangeOfString:@"." options:NSBackwardsSearch];
                if(range.location != NSNotFound)
                {
                    ex = [fileMsg.name substringFromIndex:range.location+1];
                }
            }
            
            if([ex isEqualToString:@"mp3"])
                thumb.image = [UIImage imageNamed:@"fileex_sound.png"];
            else if([_fileExs containsObject:ex])
            {
                thumb.image = [UIImage imageNamed:@"fileex_script.png"];
            }
        }
        
        double filesize = fileMsg.size;
        if(filesize < FILE_M)
        {
            sizeL.text = [NSString stringWithFormat:@"%0.1fKB", filesize/1024.0];
        }
        else
        {
            sizeL.text = [NSString stringWithFormat:@"%0.1fMB", filesize/FILE_M];
        }
        
        NSTimeInterval time = msg.receivedTime/1000;
        NSDate *date1 = [NSDate dateWithTimeIntervalSince1970:time];
        if(date1)
        {
            NSString *timeStr = @"";
            NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
            formatter.dateFormat = @"M-dd h:mm";
            timeStr = [NSString stringWithFormat:@"%@",[formatter stringFromDate:date1]];
            dateL.text = timeStr;
        }
        
    }
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 79, SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [cell.contentView addSubview:line];
    
    if(_isSelectMode)
    {
        UIButton *btnCheck = [UIButton buttonWithType:UIButtonTypeCustom];
        btnCheck.frame = CGRectMake(0, 0, 40, 80);
        [cell.contentView addSubview:btnCheck];
        [btnCheck setImage:[UIImage imageNamed:@"add_c_normal.png"] forState:UIControlStateNormal];
        btnCheck.tag = indexPath.row;
        [btnCheck addTarget:self action:@selector(clickedCheckBtn:) forControlEvents:UIControlEventTouchUpInside];
        
        if([_selected containsObject:msg])
        {
            [btnCheck setImage:[UIImage imageNamed:@"add_c_selected.png"] forState:UIControlStateNormal];
        }
    }
    
    return cell;
}





- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    
    return [_data count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return 80;
}



#pragma mark UITableView delegate



- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}

- (void) clickedThumbImg:(UIButton*)btn{
    
    
}

- (void) clickedCheckBtn:(UIButton*)btn{
    
    
    int idx = (int)btn.tag;
    if(idx < [_data count])
    {
        RCMessage *msg = [_data objectAtIndex:idx];
        
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
    
    if(delegate && [delegate respondsToSelector:@selector(didSelectFile)])
    {
        [delegate didSelectFile];
    }
    
    
}

- (void) deletAction{
    
    if([_selected count])
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                        message:[NSString stringWithFormat:@"删除选择的%d个文件?",
                                                                 (int)[_selected count]]
                                                       delegate:self
                                              cancelButtonTitle:@"取消"
                                              otherButtonTitles:@"删除", nil];
        alert.tag = 1314;
        [alert show];
        
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(alertView.tag == 1314 && buttonIndex != alertView.cancelButtonIndex)
    {
        [self doDelete];
    }
}

- (void) doDelete{
    
    NSMutableArray *msgids = [NSMutableArray array];
    for(RCMessage *msg in _selected)
    {
        [msgids addObject:[NSNumber numberWithLong:msg.messageId]];
    }
    
    [[RCIMClient sharedRCIMClient] deleteMessages:msgids];
    
    [_selected removeAllObjects];
    
    [self loadImageMessages:nil];
}

- (void) forwardAction{
    
    if([_selected count])
    {
        ForwardContactViewController *choose = [[ForwardContactViewController alloc] init];
        choose._selectedImages = self._selected;
        CMNavigationController *navi = [[CMNavigationController alloc] initWithRootViewController:choose];
        [self._viewCtrl presentViewController:navi
                                     animated:YES
                                   completion:^{
                                       
                                   }];
    }
    
    
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
