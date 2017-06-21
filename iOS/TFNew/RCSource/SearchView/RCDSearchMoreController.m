//
//  RCDSearchMoreController.m
//  RCloudMessage
//
//  Created by 张改红 on 16/9/26.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCDSearchMoreController.h"
#import "RCDCommonDefine.h"
#import "UIColor+RCColor.h"
#import "RCDSearchBar.h"
#import "RCDSearchResultViewCell.h"
#import "RCDSearchResultModel.h"
#import "ChatViewController.h"
#import "RCDSearchDataManager.h"
#import "RCDLabel.h"


@interface RCDSearchMoreController ()<UISearchBarDelegate, UITableViewDelegate, UITableViewDataSource>
{
    UITableView *_resultTableView;
}
@property (nonatomic,strong)RCDSearchBar *searchBars;
@property (nonatomic,strong)UIButton *cancelButton;
@property (nonatomic,strong)UIView *searchView;
@property (nonatomic,strong)UIView *headerView;
@property (nonatomic,strong)RCDLabel *emptyLabel;
@end

@implementation RCDSearchMoreController


- (UILabel *)emptyLabel{
    if (!_emptyLabel) {
        self.emptyLabel = [[RCDLabel alloc] initWithFrame:CGRectMake(10,45, self.view.frame.size.width-20, 16)];
        self.emptyLabel.font = [UIFont systemFontOfSize:14.f];
        self.emptyLabel.textAlignment = NSTextAlignmentCenter;
        self.emptyLabel.numberOfLines = 0;
        [_resultTableView addSubview:self.emptyLabel];
        
    }
    return _emptyLabel;
}

- (UIView *)headerView{
    if (!_headerView) {
        _headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 40)];
        _headerView.backgroundColor = [UIColor whiteColor];
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10, 40-16-7, self.view.frame.size.width, 16)];
        label.font = [UIFont systemFontOfSize:14.];
        label.text = self.type;
        label.textColor = HEXCOLOR(0x999999);
        [_headerView addSubview:label];
        UIView *view = [[UIView alloc] initWithFrame:CGRectMake(10, 39.5, self.view.frame.size.width-10, 0.5)];
        view.backgroundColor = HEXCOLOR(0xdfdfdf);
        [self.headerView addSubview:view];
    }
    return _headerView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];
    
    self.view.backgroundColor = [UIColor whiteColor];
    
    [self.view addSubview:self.searchView];
    self.searchBars.text = self.searchString;
    self.title = self.searchString;
    
    _resultTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0,
                                                                     SCREEN_WIDTH,
                                                                     SCREEN_HEIGHT-64)
                                                    style:UITableViewStylePlain];
    _resultTableView.backgroundColor = [UIColor clearColor];
    _resultTableView.delegate = self;
    _resultTableView.dataSource = self;
    _resultTableView.tableFooterView = [UIView new];
    [_resultTableView setSeparatorColor:HEXCOLOR(0xdfdfdf)];
    [self.view addSubview:_resultTableView];
    
    
}

- (void)cancelButtonClicked{
    
    self.cancelBlock();
    
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    _resultTableView.tableHeaderView = self.headerView;
}

- (void)viewWillDisappear:(BOOL)animated{
    
    [super viewWillDisappear:animated];
}

- (void)viewWillLayoutSubviews{
    
    [super viewWillLayoutSubviews];
}

#pragma mark - Table view data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.resultArray.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    RCDSearchResultViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (!cell) {
        cell = [[RCDSearchResultViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
    }
    if (self.searchBars.text) {
        cell.searchString = self.searchBars.text;
    }else{
        cell.searchString = self.searchString;
    }
    [cell setDataModel:self.resultArray[indexPath.row]];
    return cell;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 65;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    [self.searchBars resignFirstResponder];
    RCDSearchResultModel *model = self.resultArray[indexPath.row];
    if (model.count > 1) {
        RCDSearchMoreController *viewController = [[RCDSearchMoreController alloc] init];
        viewController.searchString = self.searchBars.text;
        viewController.type = [NSString stringWithFormat:@"共%d条相关的聊天记录",model.count];
        NSArray *array = [[RCIMClient sharedRCIMClient] searchMessages:model.conversationType targetId:model.targetId keyword:self.searchBars.text count:model.count startTime:0];
        NSMutableArray *resultArray = [NSMutableArray array];
        for (RCMessage *message in array) {
            RCDSearchResultModel *messegeModel = [[RCDSearchResultModel alloc] init];
            messegeModel.conversationType = model.conversationType;
            messegeModel.name = model.name;
            messegeModel.targetId = model.targetId;
            messegeModel.searchType = model.searchType;
            messegeModel.portraitUri = model.portraitUri;
            NSString *string = nil;
            messegeModel.objectName = message.objectName;
            if([message.content isKindOfClass:[RCRichContentMessage class]]){
                RCRichContentMessage *rich = (RCRichContentMessage *)message.content;
                string = rich.title;
            }else if([message.content isKindOfClass:[RCFileMessage class]]){
                RCFileMessage *file = (RCFileMessage *)message.content;
                string = file.name;
            }else{
                string = [RCKitUtility formatMessage:message.content];
            }
            messegeModel.time = message.sentTime;
            messegeModel.otherInformation = string;
            [resultArray addObject:messegeModel];    }
        viewController.title = model.name;
        viewController.isShowSeachBar = NO;
        viewController.resultArray = resultArray;
        __weak typeof(self) weakSelf = self;
        [viewController setCancelBlock:^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf cancelButtonClicked];
            });
        }];
        [self.navigationController pushViewController:viewController animated:YES];
        return;
    }
    /*
     RCDChatViewController *_conversationVC =
     [[RCDChatViewController alloc] init];
     _conversationVC.conversationType = model.conversationType;
     _conversationVC.targetId = model.targetId;
     _conversationVC.userName = model.name;
     int unreadCount = [[RCIMClient sharedRCIMClient] getUnreadCount:model.conversationType targetId:model.targetId];
     _conversationVC.unReadMessage = unreadCount;
     _conversationVC.enableNewComingMessageIcon = YES; //开启消息提醒
     _conversationVC.enableUnreadMessageIcon = YES;
     //如果是单聊，不显示发送方昵称
     if (model.conversationType == ConversationType_PRIVATE) {
     _conversationVC.displayUserNameInCell = NO;
     }
     [self.navigationController pushViewController:_conversationVC
     animated:YES];
     */
    
    ChatViewController *_conversationVC = [[ChatViewController alloc] init];
    _conversationVC.conversationType = model.conversationType;
    _conversationVC.targetId = model.targetId;
    //_conversationVC.userName = model.name;
    int unreadCount = [[RCIMClient sharedRCIMClient] getUnreadCount:model.conversationType targetId:model.targetId];
    _conversationVC.unReadMessage = unreadCount;
    _conversationVC.enableNewComingMessageIcon = YES; //开启消息提醒
    _conversationVC.enableUnreadMessageIcon = YES;
    _conversationVC.hidesBottomBarWhenPushed = YES;
    //如果是单聊，不显示发送方昵称
    if (model.conversationType == ConversationType_PRIVATE) {
        _conversationVC.displayUserNameInCell = NO;
    }
    _conversationVC.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:_conversationVC
                                               animated:YES];
}

- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    searchBar.text = nil;
    searchBar.placeholder = self.type;
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    self.resultArray = nil;
    NSInteger type;
    if ([self.type isEqualToString:@"联系人"]) {
        type = RCDSearchFriend;
    }else if([self.type isEqualToString:@"群组"]){
        type = RCDSearchGroup;
    }else if([self.type isEqualToString:@"聊天记录"]){
        type = RCDSearchChatHistory;
    }
    __weak typeof(self) weakSelf = self;
    [[RCDSearchDataManager shareInstance] searchDataWithSearchText:searchText bySearchType:type complete:^(NSDictionary *dic, NSArray *array) {
        weakSelf.resultArray = [dic objectForKey:self.type];
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf refreshSearchView:searchText];
        });
    }];
}

- (void) searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    [self.searchBars resignFirstResponder];
}

- (void)refreshSearchView:(NSString *)searchText{
    [_resultTableView reloadData];
    NSString *searchStr = [searchText stringByReplacingOccurrencesOfString:@" "  withString:@""];
    if (!self.resultArray.count && searchText.length>0 && searchStr.length > 0) {
        NSString *str =[NSString stringWithFormat:@"没有搜索到“%@”相关的内容",searchText];
        self.emptyLabel.textColor = HEXCOLOR(0x999999);
        NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:str];
        [attributedString addAttribute:NSForegroundColorAttributeName value:HEXCOLOR(0x0099ff) range:NSMakeRange(6, searchText.length)];
        self.emptyLabel.attributedText = attributedString;
        CGFloat height = [self labelAdaptive:str];
        CGRect rect = self.emptyLabel.frame;
        rect.size.height = height;
        self.emptyLabel.frame = rect;
        self.emptyLabel.hidden = NO;
        _resultTableView.tableHeaderView = nil;
    }else{
        self.emptyLabel.hidden = YES;
        if (self.resultArray.count > 0) {
            _resultTableView.tableHeaderView = self.headerView;
        }else{
            _resultTableView.tableHeaderView = nil;
        }
    }
}

- (CGFloat)labelAdaptive:(NSString *)string{
    float maxWidth = self.view.frame.size.width-20;
    CGRect textRect = [string
                       boundingRectWithSize:CGSizeMake(maxWidth, 8000)
                       options:(NSStringDrawingTruncatesLastVisibleLine |
                                NSStringDrawingUsesLineFragmentOrigin |
                                NSStringDrawingUsesFontLeading)
                       attributes:@{
                                    NSFontAttributeName :
                                        [UIFont systemFontOfSize:14.0]
                                    }
                       context:nil];
    textRect.size.height = ceilf(textRect.size.height);
    return textRect.size.height + 5;
}
@end
