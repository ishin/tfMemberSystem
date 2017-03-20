//
//  JRCDSearchView.m
//  RCloudMessage
//
//  Created by 张改红 on 16/9/18.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "JRCDSearchView.h"
#import "RCDSearchBar.h"
#import "RCDLabel.h"
#import "RCDCommonDefine.h"
#import "RCDSearchResultViewCell.h"
#import "RCDataBaseManager.h"
#import "RCDUtilities.h"
#import "RCDSearchResultModel.h"
#import "UIColor+RCColor.h"
#import "RCDSearchMoreViewCell.h"
#import "RCDSearchMoreController.h"
#import "RCDSearchDataManager.h"
#import "ChatViewController.h"

@interface JRCDSearchView ()<UITableViewDelegate,UITableViewDataSource,UIGestureRecognizerDelegate>
@property (nonatomic,strong)NSMutableDictionary *resultDictionary;
@property (nonatomic,strong)NSMutableArray *groupTypeArray;
//@property (nonatomic,strong)RCDSearchBar *searchBars;
@property (nonatomic,strong)UIButton *cancelButton;
@property (nonatomic,strong)UIView *searchView;
@property (nonatomic,strong)UITableView *resultTableView;
@property (nonatomic,strong)RCDLabel *emptyLabel;
@property (nonatomic, strong) UIView *searchBackgroundView;
@property (nonatomic,strong) NSDate *searchDate;

@property (nonatomic, strong) NSString *_searchTxt;

@end

@implementation JRCDSearchView

@synthesize _naviCtrl;
@synthesize _searchTxt;
@synthesize delegate;

- (UITableView *)resultTableView{
  if (!_resultTableView) {
    _resultTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height) style:UITableViewStylePlain];
    _resultTableView.backgroundColor = [UIColor clearColor];
    _resultTableView.delegate = self;
    _resultTableView.dataSource = self;
    _resultTableView.tableFooterView = [UIView new];
    [_resultTableView setSeparatorColor:HEXCOLOR(0xdfdfdf)];
    [self addSubview:_resultTableView];
  }
  return _resultTableView;
}

- (UILabel *)emptyLabel{
  if (!_emptyLabel) {
    _emptyLabel = [[RCDLabel alloc] initWithFrame:CGRectMake(10,45, self.frame.size.width-20, 16)];
    _emptyLabel.font = [UIFont systemFontOfSize:14.f];
    _emptyLabel.textAlignment = NSTextAlignmentCenter;
    _emptyLabel.numberOfLines = 0;
    [self.resultTableView addSubview:_emptyLabel];
  }
  return _emptyLabel;
}


- (id) initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame])
    {
        _groupTypeArray = [NSMutableArray array];
        _resultDictionary = [NSMutableDictionary dictionary];
        
        self.backgroundColor = RGBA(0x00, 0x27, 0x2C, 0.4);
        
        [self resultTableView];
        
        [self emptyLabel];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hideSerchBarWhenTapBackground:)];
        tap.delegate = self;
        [self addGestureRecognizer:tap];
    }
    
    return self;
    
}

- (void) searchFrinedWithKeywords:(NSString*) searchText{
    
    self._searchTxt = searchText;
    
    [self.resultDictionary removeAllObjects];
    [self.groupTypeArray removeAllObjects];
    
    [[RCDSearchDataManager shareInstance] searchDataWithSearchText:searchText
                                                      bySearchType:RCDSearchFriend
                                                          complete:^(NSDictionary *dic,NSArray *array) {
                                                              [self.resultDictionary  setDictionary:dic];
                                                              [self.groupTypeArray setArray:array];
                                                              dispatch_async(dispatch_get_main_queue(), ^{
                                                                  [self refreshSearchView:searchText];
                                                              });
                                                          }];
}

- (void) searchFriendAndGroupWithKeywords:(NSString*) searchText{
    
    self._searchTxt = searchText;
    
    [self.resultDictionary removeAllObjects];
    [self.groupTypeArray removeAllObjects];
    
    [[RCDSearchDataManager shareInstance] searchDataWithSearchText:searchText
                                                      bySearchType:RCDSearchFriend
                                                          complete:^(NSDictionary *dic,NSArray *array) {
                                                              [self.resultDictionary  setDictionary:dic];
                                                              [self.groupTypeArray setArray:array];
                                                              dispatch_async(dispatch_get_main_queue(), ^{
                                                                  [self refreshSearchView:searchText];
                                                              });
                                                          }];
    
    
    [[RCDSearchDataManager shareInstance] searchDataWithSearchText:searchText
                                                      bySearchType:RCDSearchGroup
                                                          complete:^(NSDictionary *dic,NSArray *array) {
                                                              [self.resultDictionary  addEntriesFromDictionary:dic];
                                                              [self.groupTypeArray addObjectsFromArray:array];
                                                              dispatch_async(dispatch_get_main_queue(), ^{
                                                                  [self refreshSearchView:searchText];
                                                              });
                                                          }];
    
}

- (void) searchGroupWithKeywords:(NSString*) searchText{
    
    self._searchTxt = searchText;
    
    [self.resultDictionary removeAllObjects];
    [self.groupTypeArray removeAllObjects];
    
    [[RCDSearchDataManager shareInstance] searchDataWithSearchText:searchText
                                                      bySearchType:RCDSearchGroup
                                                          complete:^(NSDictionary *dic,NSArray *array) {
                                                              [self.resultDictionary  setDictionary:dic];
                                                              [self.groupTypeArray setArray:array];
                                                              dispatch_async(dispatch_get_main_queue(), ^{
                                                                  [self refreshSearchView:searchText];
                                                              });
                                                          }];

    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
  return self.groupTypeArray.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
  NSArray *array = self.resultDictionary[self.groupTypeArray[section]];
  
  return array.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    RCDSearchResultViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];
    if (!cell) {
        cell = [[RCDSearchResultViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
    }
    NSArray *array = self.resultDictionary[self.groupTypeArray[indexPath.section]];
    cell.searchString = _searchTxt;
    [cell setDataModel:array[indexPath.row]];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{

  return 65;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
  return 40;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
  UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, 40)];
  view.backgroundColor = [UIColor whiteColor];
  UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10, 40-16-7, self.frame.size.width, 16)];
  label.font = [UIFont systemFontOfSize:14.];
  label.text = _groupTypeArray[section];
  label.textColor = HEXCOLOR(0x999999);
  [view addSubview:label];
    //添加与cell分割线等宽的session分割线
    CGRect viewFrame = view.frame;
    UIView *separatorLine = [[UIView alloc]initWithFrame:CGRectMake(10, viewFrame.size.height-1, viewFrame.size.width-10, 1)];
    separatorLine.backgroundColor = [UIColor colorWithRed:230/255.0 green:230/255.0 blue:230/255.0 alpha:1];
    [view addSubview:separatorLine];
  return view;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
  if(section == self.groupTypeArray.count-1){
     return nil;
  }
  UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, 40)];
  view.backgroundColor = HEXCOLOR(0xf0f0f6);
  return view;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
  if(section == self.groupTypeArray.count-1){
    return 0;
  }
  return 5;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    
    NSArray *array = self.resultDictionary[self.groupTypeArray[indexPath.section]];
    RCDSearchResultModel *model = array[indexPath.row];
    if (model.count > 1) {
        RCDSearchMoreController *viewController = [[RCDSearchMoreController alloc] init];
        viewController.searchString = _searchTxt;
        viewController.type = [NSString stringWithFormat:@"共%d条相关的聊天记录",model.count];
        NSArray *array = [[RCIMClient sharedRCIMClient] searchMessages:model.conversationType
                                                              targetId:model.targetId
                                                               keyword:_searchTxt count:model.count startTime:0];
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
            [resultArray addObject:messegeModel];
        }
        viewController.title = model.name;
        viewController.isShowSeachBar = NO;
        viewController.resultArray = resultArray;
        __weak typeof(self) weakSelf = self;
        [viewController setCancelBlock:^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf cancelButtonClicked];
            });
        }];
        
        viewController.hidesBottomBarWhenPushed = YES;
        [self._naviCtrl pushViewController:viewController animated:YES];
        return;
    }
    
    
    ChatViewController *_conversationVC = [[ChatViewController alloc] init];
    _conversationVC.conversationType = model.conversationType;
    _conversationVC.targetId = model.targetId;
    //_conversationVC.userName = model.name;
    int unreadCount = [[RCIMClient sharedRCIMClient] getUnreadCount:model.conversationType targetId:model.targetId];
    _conversationVC.unReadMessage = unreadCount;
    _conversationVC.enableNewComingMessageIcon = YES; //开启消息提醒
    _conversationVC.enableUnreadMessageIcon = YES;
    //如果是单聊，不显示发送方昵称
    if (model.conversationType == ConversationType_PRIVATE) {
        _conversationVC.displayUserNameInCell = NO;
    }
    _conversationVC.hidesBottomBarWhenPushed = YES;
    [self._naviCtrl pushViewController:_conversationVC
                              animated:YES];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
  [self.resultDictionary removeAllObjects];
  [self.groupTypeArray removeAllObjects];
//  dispatch_async(dispatch_get_global_queue(0, 0), ^{
    [[RCDSearchDataManager shareInstance] searchDataWithSearchText:searchText bySearchType:RCDSearchAll complete:^(NSDictionary *dic,NSArray *array) {
      [self.resultDictionary  setDictionary:dic];
      [self.groupTypeArray setArray:array];
      dispatch_async(dispatch_get_main_queue(), ^{
        [self refreshSearchView:searchText];
      });
    }];
//  });
}

- (void)refreshSearchView:(NSString *)searchText{
  [self.resultTableView reloadData];
  NSString *searchStr = [searchText stringByReplacingOccurrencesOfString:@" "  withString:@""];
  if (!self.groupTypeArray.count && searchText.length>0 && searchStr.length > 0 ) {
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
  }else{
    self.emptyLabel.hidden = YES;
  }
}

- (void) searchBarSearchButtonClicked:(UISearchBar *)searchBar{
 // [self.searchBars resignFirstResponder];
}

- (CGFloat)labelAdaptive:(NSString *)string{
  float maxWidth = self.frame.size.width-20;
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

- (NSString*)changeString:(NSString *)str appendStr:(NSString *)appendStr{
  if (str.length>0) {
    str = [NSString stringWithFormat:@"%@,%@",str,appendStr];
  }else{
    str = appendStr;
  }
  return str;
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self cancelButtonClicked];
}

- (void) cancelButtonClicked{
    
    if(delegate && [delegate respondsToSelector:@selector(onSearchCancelClick)])
    {
        [delegate onSearchCancelClick];
    }
}



- (void)hideSerchBarWhenTapBackground:(id)sender {
 // [self.searchBars resignFirstResponder];
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch{
  if ([NSStringFromClass([touch.view class]) isEqualToString:@"UITableViewCellContentView"]) {
    return NO;
  }
  return YES;
}

@end
