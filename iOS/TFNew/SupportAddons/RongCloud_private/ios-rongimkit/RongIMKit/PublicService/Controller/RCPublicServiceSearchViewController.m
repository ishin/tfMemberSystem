//
//  RCPublicServiceSearchViewController.m
//  RongIMKit
//
//  Created by litao on 15/4/21.
//  Copyright (c) 2015年 RongCloud. All rights reserved.
//

#import "RCPublicServiceSearchViewController.h"
#import "RCSearchItemView.h"
#import "RCPublicServiceSearchHintCell.h"
#import "RCExtensionUtility.h"
#import <RongIMLib/RongIMLib.h>
#import "RCPublicServiceListViewCell.h"
#import "RCPublicServiceProfileViewController.h"

@interface RCPublicServiceSearchViewController () <UISearchBarDelegate, UISearchDisplayDelegate, UITableViewDelegate,
                                                   UITableViewDataSource, RCSearchItemDelegate>
@property(nonatomic, strong) UISearchBar *mySearchBar;
@property(nonatomic, strong) UISearchDisplayController *mySearchDisplayController;

@property(nonatomic, strong) RCSearchItemView *searchItem;
@property(nonatomic, strong) NSArray *searchResults; // of RCPublicServiceProfile
@property(nonatomic, strong) NSString *searchKey;
@property(nonatomic, strong) NSMutableDictionary *offscreenCells; // of RCPublicServiceListViewCell
@end

@implementation RCPublicServiceSearchViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    self.mySearchBar = [[UISearchBar alloc] init];
    self.mySearchBar.delegate = self;
    [self.mySearchBar setAutocapitalizationType:UITextAutocapitalizationTypeNone];
    [self.mySearchBar sizeToFit];
    UIColor *color = self.navigationController.navigationBar.barTintColor;
    [self.navigationController.view setBackgroundColor:color];
    self.tableView.tableHeaderView = self.mySearchBar;
    self.mySearchDisplayController =
        [[UISearchDisplayController alloc] initWithSearchBar:self.mySearchBar contentsController:self];
    [self setMySearchDisplayController:self.mySearchDisplayController];
    [self.mySearchDisplayController setDelegate:self];
    [self.mySearchDisplayController setSearchResultsDataSource:self];
    [self.mySearchDisplayController setSearchResultsDelegate:self];
    self.tableView.tableFooterView = [UIView new];
    self.mySearchDisplayController.searchResultsTableView.tableFooterView = [UIView new];
    self.mySearchDisplayController.searchResultsTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self setTitle:NSLocalizedStringFromTable(@"SearchPublicService", @"RongCloudKit", nil)];
}
- (void)searchDisplayControllerDidEndSearch:(UISearchDisplayController *)controller
{
    if (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1) {
        [self.tableView insertSubview:self.searchDisplayController.searchBar aboveSubview:self.tableView];
    }
    return;
}
- (NSMutableDictionary *)offscreenCells {
    if (!_offscreenCells) {
        _offscreenCells = [[NSMutableDictionary alloc] init];
    }
    return _offscreenCells;
}
- (void)setSearchResults:(NSArray *)searchResults {
    _searchResults = searchResults;
    [self.mySearchDisplayController.searchResultsTableView reloadData];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    if (self.tableView == tableView) {
        return 0;
    }
    if (self.searchKey) {
        return 1;
    }
    return 0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.tableView == tableView) {
        return 0;
    }
    if (self.searchKey) {
        if ([self.searchResults count]) {
            return [self.searchResults count];
        } else {
            return 1;
        }
    }
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.tableView == tableView) {
        return nil;
    }
    if (self.searchKey) {
        if ([self.searchResults count]) {
            RCPublicServiceListViewCell *cell =
                [tableView dequeueReusableCellWithIdentifier:@"public account list view cell"];

            if (!cell) {
                cell = [[RCPublicServiceListViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                                          reuseIdentifier:@"public account list view cell"];
            }
            RCPublicServiceProfile *info = self.searchResults[indexPath.row];
            //[cell.headerImageView setImage:[RCPublicServiceUtility imagesNamedFromPABundle:@"searchItem"]];
            cell.searchKey = self.searchKey;
            [cell setName:info.name];
            [cell setDescription:info.introduction];
            [cell.headerImageView setImageURL:[NSURL URLWithString:info.portraitUrl]];
            [cell setNeedsUpdateConstraints];
            [cell updateConstraintsIfNeeded];

            return cell;
        }
        CGRect frame = tableView.bounds;
        frame.size.height = 50;
        RCPublicServiceSearchHintCell *cell = [[RCPublicServiceSearchHintCell alloc] initWithFrame:frame];
        return cell;
    }
    return nil;
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    self.searchKey = searchText;
    [self.searchItem setKeyContent:searchText];
    [self.searchDisplayController.searchResultsTableView reloadData];
}
- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar {
    self.searchResults = nil;
    [self.searchItem setHidden:NO];
}
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self startSearch];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (self.tableView == tableView) {
        return 0;
    }
    if ([self.searchResults count]) {
        return 0;
    }
    return 50;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        if (!self.searchItem) {
            self.searchItem = [[RCSearchItemView alloc] initWithFrame:CGRectMake(0, 0, 320, 50)];
            self.searchItem.delegate = self;
        }
        [self.searchItem setKeyContent:self.searchKey];
        return self.searchItem;
    }
    return nil;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.tableView == tableView) {
        return 0;
    }
    if (![self.searchResults count]) {
        return 0;
    }
    NSString *reuseIdentifier = @"public account list view cell";
    RCPublicServiceListViewCell *cell = [self.offscreenCells objectForKey:reuseIdentifier];
    if (!cell) {
        cell = [[RCPublicServiceListViewCell alloc] init];
        [self.offscreenCells setObject:cell forKey:reuseIdentifier];
    }

    RCPublicServiceProfile *info = self.searchResults[indexPath.row];

    [cell.headerImageView setImage:[UIImage imageNamed:@"searchItem"]];
    cell.searchKey = self.searchKey;
    [cell setName:info.name];
    [cell setDescription:info.introduction];
    [cell setNeedsUpdateConstraints];
    [cell updateConstraintsIfNeeded];

    cell.bounds = CGRectMake(0.0f, 0.0f, CGRectGetWidth(tableView.bounds), CGRectGetHeight(cell.bounds));

    [cell setNeedsLayout];
    [cell layoutIfNeeded];

    CGFloat height = [cell.contentView systemLayoutSizeFittingSize:UILayoutFittingCompressedSize].height;

    height += 19.0f;

    return height;
}
- (void)onSearchItemTapped {
    DebugLog(@"taped");
    [self startSearch];
}
- (void)startSearch {
  [[[UIApplication sharedApplication] keyWindow] endEditing:YES];

  [RCExtensionUtility
      showProgressViewFor:self.tableView
                     text:NSLocalizedStringFromTable(@"Searching", @"RongCloudKit", nil)
                 animated:YES];

  [self.searchItem setHidden:YES];

  __weak RCPublicServiceSearchViewController *weakSelf = self;
  [[RCIMClient sharedRCIMClient] searchPublicService:RC_SEARCH_TYPE_FUZZY
      searchKey:self.searchKey
      success:^(NSArray *accounts) {
        dispatch_async(dispatch_get_main_queue(), ^{
          weakSelf.searchResults = accounts;
          [RCExtensionUtility hideProgressViewFor:weakSelf.tableView animated:YES];
        });
      }
      error:^(RCErrorCode status) {
        [RCExtensionUtility hideProgressViewFor:weakSelf.tableView animated:YES];
      }];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView != self.tableView && [self.searchResults count]) {
        RCPublicServiceProfile *serviceProfile = self.searchResults[indexPath.row];
        RCPublicServiceProfileViewController *infoVC = [[RCPublicServiceProfileViewController alloc] init];
        infoVC.serviceProfile = serviceProfile;
        [self.navigationController pushViewController:infoVC animated:YES];
    }
}
/*
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:; forIndexPath:indexPath];

    // Configure the cell...

    return cell;
}
*/

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath
*)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
