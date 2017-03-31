//
//  MyRecContactsViewController.h
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "MyRecContactsViewController.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "MenuView.h"
#import "WSUser.h"
#import "ShootQRCode.h"
#import "GoGoDB.h"
#import "UserDefaultsKV.h"
#import "UIButton+Color.h"
#import "UILabel+ContentSize.h"
#import "UIImage+Color.h"
#import "FanCell.h"
#import "UserInfoViewController.h"
#import "JRCDSearchView.h"

@interface MyRecContactsViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, JRCDSearchViewDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
    JRCDSearchView      *_searchView;
}
@property (nonatomic, strong) NSMutableArray *_datas;
@property (nonatomic, strong) WSUser *_selectUser;


@end

@implementation MyRecContactsViewController
@synthesize _datas;
@synthesize _isChooseModel;
@synthesize _mapSelect;
@synthesize delegate_;

@synthesize _forwardMsgs;
@synthesize _selectUser;


- (void) viewWillAppear:(BOOL)animated{
    

    self.navigationController.navigationBarHidden = NO;
    
    [self loadFromDBCache];
}


- (void) loadFromDBCache{
    
    
    [self._datas removeAllObjects];
    
    NSArray *arr = [[GoGoDB sharedDBInstance] queryAllFriends];
    for(NSDictionary *u in arr)
    {
        [self._datas addObject:[[WSUser alloc] initWithDictionary:u]];
    }
    
    [_tableView reloadData];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.navigationItem.title = @"常用联系人";
    
    
    UIView *footer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 44)];
    footer.backgroundColor = [UIColor whiteColor];
    
    
    UIView *colorLine = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 44)];
    colorLine.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    [footer addSubview:colorLine];
    
    _searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(5, 2, SCREEN_WIDTH-10, 40)];
    _searchBar.delegate = self;
    _searchBar.barTintColor = RGB(0xf2, 0xf2, 0xf2);
    //_searchBar.placeholder = @"Search";
    _searchBar.placeholder = @"搜索";
    _searchBar.backgroundColor = [UIColor clearColor];
    _searchBar.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _searchBar.searchBarStyle = UISearchBarStyleProminent;
    _searchBar.backgroundImage = [UIImage imageWithColor:RGB(0xf2, 0xf2, 0xf2) andSize:CGSizeMake(1, 1)];
    
    [footer addSubview:_searchBar];
    
    [self.view addSubview:footer];
    
   
    _searchView = [[JRCDSearchView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)];
    _searchView.delegate = self;
    _searchView._naviCtrl = self.navigationController;
   
    
   self._datas = [NSMutableArray array];
   
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    
    
  
}



#pragma mark UITableView dataSource
- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIndentifier = @"UserCell";
    UITableViewCell *cell = (UITableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:CellIndentifier];
    }
    cell.accessoryType = UITableViewCellAccessoryNone;
   
    [[cell.contentView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    WSUser *person = [_datas objectAtIndex:indexPath.row];
    
    
    int xx = 10;
    if(_isChooseModel)
    {
        xx = 32;
    }
    
    UIImageView* _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(xx, 10, 50, 50)];
    _actorLogo.layer.cornerRadius = 25;
    _actorLogo.clipsToBounds = YES;
    _actorLogo.backgroundColor = [UIColor clearColor];
    _actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    [cell.contentView addSubview:_actorLogo];
    
    
    UILabel *_nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(_actorLogo.frame)+10,
                                                                15,
                                                                SCREEN_WIDTH-100, 20)];
    _nameL.backgroundColor = [UIColor clearColor];
    [cell.contentView addSubview:_nameL];
    _nameL.font = [UIFont boldSystemFontOfSize:15];
    _nameL.textAlignment = NSTextAlignmentLeft;
    _nameL.textColor  = [UIColor blackColor];
    _nameL.text = @"";
    
    UILabel* _metaL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(_nameL.frame),
                                                                35,
                                                                SCREEN_WIDTH-150, 20)];
    _metaL.backgroundColor = [UIColor clearColor];
    [cell.contentView addSubview:_metaL];
    _metaL.font = [UIFont systemFontOfSize:13];
    _metaL.textAlignment = NSTextAlignmentLeft;
    _metaL.textColor  = COLOR_TEXT_A;
    _metaL.text = @"";
    
    
    _nameL.text = person.fullname;
    
    if([person.companyname length])
    {
        _metaL.text = person.companyname;
        
        if(person.ranktitle)
        {
            _metaL.text = [NSString stringWithFormat:@"%@ %@",person.companyname, person.ranktitle];
        }
    }
    else
    {
        if(person.ranktitle)
        {
            _metaL.text = person.ranktitle;
        }
    }
    
    NSString *avatarUrl = person.avatarurl;
    if(avatarUrl)
    {
        [_actorLogo setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
        
    }
    else
    {
        [_actorLogo setImage:[UIImage imageNamed:@"default_avatar.png"]];
    }
    
    
    
   if(_isChooseModel)
   {
       UIImageView *select = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"add_c_normal.png"]];
       [cell.contentView addSubview:select];
       select.center = CGPointMake(18, 35);
       
       
       if([_mapSelect objectForKey:[NSNumber numberWithInt:person.userId]])
       {
           select.image = [UIImage imageNamed:@"add_c_selected.png"];
       }
   }
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 69.5, SCREEN_WIDTH, 0.5)];
    line.backgroundColor = LINE_COLOR;
    [cell.contentView addSubview:line];
   
    return cell;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    return [_datas count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 70;
}


#pragma mark UITableView delegate


- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    WSUser *user = [_datas objectAtIndex:indexPath.row];

    if(_isChooseModel)
    {
        id key = [NSNumber numberWithInt:user.userId];
        if([_mapSelect objectForKey:key])
        {
            if(delegate_ && [delegate_ respondsToSelector:@selector(didCancelChoosedPerson:)])
            {
                [delegate_ didCancelChoosedPerson:user];
            }
            
            //[_mapSelect removeObjectForKey:key];
        }
        else
        {
            
           
            if(delegate_ && [delegate_ respondsToSelector:@selector(didChoosedPerson:)])
            {
                [delegate_ didChoosedPerson:user];
                
               // [_mapSelect setObject:user forKey:key];
            }
        }
        
        
        
        [_tableView reloadData];
    }
   else
   {
       if([_forwardMsgs count])
       {
           self._selectUser = user;
           
           UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"发送到"
                                                           message:[NSString stringWithFormat:@"%@", user.realname]
                                                          delegate:self
                                                 cancelButtonTitle:@"取消"
                                                 otherButtonTitles:@"发送", nil];
           alert.tag = 201701;
           [alert show];
       }
       else
       {
           UserInfoViewController *info = [[UserInfoViewController alloc] init];
           info._user = user;
           info._isFriend = YES;
           [self.navigationController pushViewController:info animated:YES];

       }
       
   }
}


- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if(alertView.tag == 201701 && buttonIndex != alertView.cancelButtonIndex)
    {
        for(RCMessage *msg in _forwardMsgs)
        {
            
            RCImageMessage *imgMsg = (RCImageMessage*)msg.content;
            
            NSString *_imageUrl = imgMsg.imageUrl;
            
            NSRange range = [_imageUrl rangeOfString:@"http"];
            if(range.location == NSNotFound)
            {
                imgMsg = [RCImageMessage messageWithImage:[UIImage imageWithContentsOfFile:_imageUrl]];
            }
            
            [[RCIMClient sharedRCIMClient] sendMediaMessage:ConversationType_PRIVATE
                                                   targetId:[NSString stringWithFormat:@"%d", _selectUser.userId]
                                                    content:imgMsg
                                                pushContent:nil
                                                   pushData:nil
                                                   progress:^(int progress, long messageId) {
                                                       
                                                   } success:^(long messageId) {
                                                       
                                                       NSLog(@"200 OK");
                                                       
                                                   } error:^(RCErrorCode errorCode, long messageId) {
                                                       
                                                       NSLog(@"111");
                                                       
                                                   } cancel:^(long messageId) {
                                                       
                                                       NSLog(@"000");
                                                   }];
            
        }
    }
    
}


- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    [_searchBar setShowsCancelButton:YES animated:YES];
    
    [self.view addSubview:_searchView];
    
}
- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    
    if([searchBar.text length] == 0)
    {
        [_searchBar setShowsCancelButton:NO animated:YES];
        
        if([_searchView superview])
            [_searchView removeFromSuperview];
    }
    
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    
    _searchBar.text=  @"";
    [_searchBar setShowsCancelButton:NO animated:YES];
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];
    
    if([_searchView superview])
        [_searchView removeFromSuperview];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    
    NSString *searchText = searchBar.text;
    
    if([searchText length] > 0)
    {
        
        [self doSearch:searchText];
        
    }
    
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    
    
    if([searchText length] > 0)
    {
        
        [self doSearch:searchText];
        
    }
    
    
}

- (void) doSearch:(NSString*)searchTxt{
    
    [_searchView searchFrinedWithKeywords:searchTxt];
}

- (void) onSearchCancelClick{
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];
    
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
