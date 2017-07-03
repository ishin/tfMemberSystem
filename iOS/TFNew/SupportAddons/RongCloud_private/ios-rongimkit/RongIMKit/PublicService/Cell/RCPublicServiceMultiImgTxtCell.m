//
//  RCPublicServiceMultiImgTxtCell.m
//  RongIMKit
//
//  Created by litao on 15/4/14.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCPublicServiceMultiImgTxtCell.h"
#import "RCPublicServiceMultiImgTxtCellHeaderCell.h"
#import "RCPublicServiceMultiImgTxtCellContentCell.h"
#import "RCPublicServiceViewConstants.h"

@interface RCPublicServiceMultiImgTxtCell () <UITableViewDataSource, UITableViewDelegate>
@property(nonatomic, strong) UITableView *tableView;
@property(nonatomic, strong) UIView *container;
@end

@implementation RCPublicServiceMultiImgTxtCell
+ (CGSize)sizeForMessageModel:(RCMessageModel *)model withCollectionViewWidth:(CGFloat)collectionViewWidth
  referenceExtraHeight:(CGFloat)extraHeight {
  
  CGFloat height = [self getCellHeight:(RCPublicServiceMultiRichContentMessage *)model.content withWidth:collectionViewWidth];
  
  height += extraHeight;
  
  return CGSizeMake(collectionViewWidth, height);
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];

    if (self) {
    }

    return self;
}

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [UITableView new];
        [self.container addSubview:_tableView];
    }
    return _tableView;
}
- (UIView *)container {
    if (!_container) {
        _container = [UIView new];
        [self.contentView addSubview:_container];
    }
    return _container;
}

- (void)setDataModel:(RCMessageModel *)model {
    [super setDataModel:model];
    if (self.tableView) {
        [self.tableView removeFromSuperview];
    }
    self.tableView = [[UITableView alloc]
        initWithFrame:CGRectMake(RCPublicServiceCellPaddingLeft, 5,
                                 self.frame.size.width - RCPublicServiceCellPaddingLeft -
                                     RCPublicServiceCellPaddingRight,
                                 [RCPublicServiceMultiImgTxtCell
                                     getCellHeight:(RCPublicServiceMultiRichContentMessage *)model.content
                                         withWidth:self.frame.size.width] -
                                     5)
                style:UITableViewStylePlain];
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.layer.cornerRadius = 4;
    self.tableView.layer.masksToBounds = YES;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;

    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {

        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
    [self.baseContentView addSubview:self.tableView];
    [self.tableView reloadData];
    self.tableView.tableFooterView = [UIView new];
    [self.tableView setScrollEnabled:NO];
}

+ (CGFloat)getCellHeight:(RCPublicServiceMultiRichContentMessage *)mpMsg withWidth:(CGFloat)width {
    width = width - RCPublicServiceCellPaddingLeft - RCPublicServiceCellPaddingRight;
    CGFloat out = 0;
    for (int i = 0; i < mpMsg.richConents.count; i++) {
        if (i == 0) {
            out += [RCPublicServiceMultiImgTxtCellHeaderCell getHeaderCellHeight];
        } else {
            RCRichContentMessage *richContentMsg = mpMsg.richConents[i];
            out += [RCPublicServiceMultiImgTxtCellContentCell getContentCellSize:richContentMsg withWidth:width].height;
            out += RCPublicServiceCellHeaderPaddingTop;
            out += RCPublicServiceCellHeaderPaddingButtom / 2;
        }
    }
    return out;
}
#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return ((RCPublicServiceMultiRichContentMessage *)self.model.content).richConents.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RCPublicServiceMultiRichContentMessage *content = (RCPublicServiceMultiRichContentMessage *)self.model.content;

    if (indexPath.row == 0) {
        RCPublicServiceMultiImgTxtCellHeaderCell *cell =
            [tableView dequeueReusableCellWithIdentifier:@"mpnewscellheadercell"];
        if (!cell) {
            cell = [[RCPublicServiceMultiImgTxtCellHeaderCell alloc] initWithFrame:tableView.frame
                                                                   reuseIdentifier:@"mpnewscellheadercell"];
        }
        cell.publicServiceDelegate = self.publicServiceDelegate;
        cell.richContent = content.richConents[indexPath.row];
        return cell;
    } else {
        RCPublicServiceMultiImgTxtCellContentCell *cell =
            [tableView dequeueReusableCellWithIdentifier:@"mpnewscellcontentcell"];
        if (!cell) {
            cell = [[RCPublicServiceMultiImgTxtCellContentCell alloc] initWithFrame:tableView.frame
                                                                    reuseIdentifier:@"mpnewscellcontentcell"];
        }
        cell.publicServiceDelegate = self.publicServiceDelegate;
        cell.isShowline = YES;
        if (indexPath.row == content.richConents.count - 1) {
            cell.isShowline = NO;
        }
        cell.richContent = content.richConents[indexPath.row];
        return cell;
    }
}
#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return [RCPublicServiceMultiImgTxtCellHeaderCell getHeaderCellHeight];
    } else {
        RCPublicServiceMultiRichContentMessage *content = (RCPublicServiceMultiRichContentMessage *)self.model.content;
        return [RCPublicServiceMultiImgTxtCellContentCell getContentCellSize:content.richConents[indexPath.row]
                                                                   withWidth:tableView.frame.size.height]
                   .height +
               RCPublicServiceCellHeaderPaddingButtom;
    }
}
@end
