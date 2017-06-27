//
//  RCConversationSettingTableViewHeader.m
//  RongIMKit
//
//  Created by Liv on 15/3/25.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCConversationSettingTableViewHeader.h"
#import "RCConversationSettingTableViewHeaderItem.h"
#import <RongIMLib/RongIMLib.h>
#import "RCKitUtility.h"
#import "RCloudImageView.h"

@interface RCConversationSettingTableViewHeader () <
    RCConversationSettingTableViewHeaderItemDelegate>

@end
@implementation RCConversationSettingTableViewHeader

- (NSArray *)users {
  if (!_users) {
    _users = [@[] mutableCopy];
  }
  return _users;
}

- (instancetype)init {
  CGRect tempRect =
      CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, 120);
  UICollectionViewFlowLayout *flowLayout =
      [[UICollectionViewFlowLayout alloc] init];
  flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
  self = [super initWithFrame:tempRect collectionViewLayout:flowLayout];
  if (self) {
    self.delegate = self;
    self.dataSource = self;
    self.scrollEnabled = NO;
    [self registerClass:[RCConversationSettingTableViewHeaderItem class]
        forCellWithReuseIdentifier:@"RCConversationSettingTableViewHeaderItem"];
    self.isAllowedInviteMember = YES;
  }
  return self;
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView
     numberOfItemsInSection:(NSInteger)section {
  if (self.isAllowedDeleteMember) {
    return self.users.count + 2;
  } else {
    if (self.isAllowedInviteMember) {
      return self.users.count + 1;
    } else {
      return self.users.count;
    }
  }
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView
                  cellForItemAtIndexPath:(NSIndexPath *)indexPath {
  RCConversationSettingTableViewHeaderItem *cell =
      [collectionView dequeueReusableCellWithReuseIdentifier:
                          @"RCConversationSettingTableViewHeaderItem"
                                                forIndexPath:indexPath];
  if (self.users.count && (self.users.count - 1 >= indexPath.row)) {
    RCUserInfo *user = self.users[indexPath.row];
    NSURL *userPortURL = [NSURL URLWithString:user.portraitUri];
    [cell.ivAva setImageURL:userPortURL];
    cell.titleLabel.text = user.name;
      cell.userId=user.userId;
    [cell.btnImg setHidden:!self.showDeleteTip];

    cell.delegate = self;

    //长按显示减号
    UILongPressGestureRecognizer *longPressGestureRecognizer =
        [[UILongPressGestureRecognizer alloc]
            initWithTarget:self
                    action:@selector(showDeleteTip:)];
    longPressGestureRecognizer.minimumPressDuration = 0.28;
    [cell addGestureRecognizer:longPressGestureRecognizer];
      //cell.tag=[NSString stringWithFormat:@"%@",user.userId];
    //点击隐藏减号
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
        initWithTarget:self
                action:@selector(hidesDeleteTip:)];
    [cell addGestureRecognizer:tap];

  } else if (self.users.count >= indexPath.row) {

    cell.btnImg.hidden = YES;
    cell.gestureRecognizers = nil;
    cell.titleLabel.text = @"";
    [cell.ivAva setImage:[RCKitUtility imageNamed:@"add_members"
                                         ofBundle:@"RongCloud.bundle"]];

  } else {
    cell.btnImg.hidden = YES;
    cell.gestureRecognizers = nil;
    cell.titleLabel.text = @"";
    [cell.ivAva setImage:[RCKitUtility imageNamed:@"delete_members"
                                         ofBundle:@"RongCloud.bundle"]];
    //长按显示减号
    UILongPressGestureRecognizer *longPressGestureRecognizer =
        [[UILongPressGestureRecognizer alloc]
            initWithTarget:self
                    action:@selector(showDeleteTip:)];
    longPressGestureRecognizer.minimumPressDuration = 0.28;
    [cell addGestureRecognizer:longPressGestureRecognizer];
  }

  return cell;
}

#pragma mark - RCConversationSettingTableViewHeaderItemDelegate
- (void)deleteTipButtonClicked:
    (RCConversationSettingTableViewHeaderItem *)item {

  NSIndexPath *indexPath = [self indexPathForCell:item];
  RCUserInfo *user = self.users[indexPath.row];
  if ([user.userId isEqualToString:[RCIMClient sharedRCIMClient]
                                       .currentUserInfo.userId]) {
    UIAlertView *alertView = [[UIAlertView alloc]
            initWithTitle:nil
                  message:NSLocalizedStringFromTable(@"CanNotRemoveSelf",
                                                     @"RongCloudKit", nil)
                 delegate:nil
        cancelButtonTitle:NSLocalizedStringFromTable(@"OK", @"RongCloudKit",
                                                     nil)
        otherButtonTitles:nil, nil];
    ;
    [alertView show];
    return;
  }
  [self.users removeObjectAtIndex:indexPath.row];
  [self deleteItemsAtIndexPaths:@[ indexPath ]];

  if (self.settingTableViewHeaderDelegate &&
      [self.settingTableViewHeaderDelegate
          respondsToSelector:@selector(deleteTipButtonClicked:)]) {
    [self.settingTableViewHeaderDelegate deleteTipButtonClicked:indexPath];
    [self reloadData];
  }
}

//长按显示减号
- (void)showDeleteTip:(RCConversationSettingTableViewHeaderItem *)cell {
  if (self.isAllowedDeleteMember) {
    self.showDeleteTip = YES;
    [self reloadData];
  }
}

//点击隐藏减号
- (void)hidesDeleteTip:(UITapGestureRecognizer *)recognizer {
  if (self.showDeleteTip) {
    self.showDeleteTip = NO;
    [self reloadData];
  } else {
    if (self.settingTableViewHeaderDelegate &&
        [self.settingTableViewHeaderDelegate
            respondsToSelector:@selector(didTipHeaderClicked:)]) {
            RCConversationSettingTableViewHeaderItem *cell=(RCConversationSettingTableViewHeaderItem *)recognizer.view;
            [self.settingTableViewHeaderDelegate didTipHeaderClicked:cell.userId];
    }
  }
}

#pragma mark - UICollectionViewDelegateFlowLayout
- (CGSize)collectionView:(UICollectionView *)collectionView
                  layout:(UICollectionViewLayout *)collectionViewLayout
  sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
  float width = 56;
  float height = width + 15 + 5;

  return CGSizeMake(width, height);
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView
                        layout:(UICollectionViewLayout *)collectionViewLayout
        insetForSectionAtIndex:(NSInteger)section {
  UICollectionViewFlowLayout *flowLayout =
      (UICollectionViewFlowLayout *)collectionViewLayout;
  flowLayout.minimumInteritemSpacing = 5;
  flowLayout.minimumLineSpacing = 5;
  return UIEdgeInsetsMake(10, 10, 10, 10);
}

#pragma mark - UICollectionViewDelegate
- (void)collectionView:(UICollectionView *)collectionView
    didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
  if (indexPath && self.settingTableViewHeaderDelegate &&
      [self.settingTableViewHeaderDelegate
          respondsToSelector:@selector(settingTableViewHeader:
                                      indexPathOfSelectedItem:
                                           allTheSeletedUsers:)]) {
    [self.settingTableViewHeaderDelegate settingTableViewHeader:self
                                        indexPathOfSelectedItem:indexPath
                                             allTheSeletedUsers:self.users];
  }
}

@end
