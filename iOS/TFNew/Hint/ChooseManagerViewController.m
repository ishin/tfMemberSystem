//
//  ChooseManagerViewController.m
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "ChooseManagerViewController.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "WSUser.h"
#import "ShootQRCode.h"
#import "GoGoDB.h"
#import "UserDefaultsKV.h"
#import "UIButton+Color.h"
#import "UILabel+ContentSize.h"
#import "UIImage+Color.h"
#import "RCDUtilities.h"
#import "WaitDialog.h"


@interface ChooseManagerViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
    UIView              *_headerView;

}
@property (nonatomic, strong) NSMutableArray *_headerDatas;

@property (nonatomic, strong) WSUser *_selPerson;
@property (nonatomic, strong) NSMutableArray *_results;

@end

@implementation ChooseManagerViewController
@synthesize _headerDatas;
@synthesize _datas;
@synthesize _selPerson;
@synthesize _groupId;
@synthesize _results;
@synthesize _gCreatorId;


- (void) viewWillAppear:(BOOL)animated{
    
    self.navigationController.navigationBarHidden = NO;
    
}

- (void) backAction:(id)sender{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];
    
    
    self.title = @"选择新群主";
    
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
    

    self._results = [NSMutableArray arrayWithArray:_datas];
    
  
    
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
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1
                                      reuseIdentifier:CellIndentifier];
    }
    cell.accessoryType = UITableViewCellAccessoryNone;
   
    [[cell.contentView subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    if(indexPath.row < [_results count])
    {
        WSUser *person = [_results objectAtIndex:indexPath.row];
        
        
        UIImageView* _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(10, 10, 50, 50)];
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

    }
    


    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 69.5, SCREEN_WIDTH, 0.5)];
    line.backgroundColor = LINE_COLOR;
    [cell.contentView addSubview:line];
    
    return cell;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [_results count];
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
    
    
    [self buttonClicked:indexPath];
}

- (void) loginAction:(id)sender{
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    [app switchLogin];
}



- (void) buttonClicked:(NSIndexPath*)indexPath{
    
    if([_searchBar isFirstResponder])
    {
        [_searchBar resignFirstResponder];
    }
    
    WSUser *person = [_datas objectAtIndex:indexPath.row];
    
    self._selPerson = person;
    
    if(_selPerson.userId != _gCreatorId)
    {
        
        NSString *msg = [NSString stringWithFormat:@"确定选择 %@ 为新群主？您将自动放弃群主身份。", _selPerson.fullname];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                        message:msg
                                                       delegate:self
                                              cancelButtonTitle:@"取消"
                                              otherButtonTitles:@"确定", nil];
        alert.tag = 201701;
        [alert show];
        
    }
    
    //[_tableView reloadData];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(alertView.tag == 201701 && buttonIndex != alertView.cancelButtonIndex)
    {
        [self doneAddContacts:nil];
    }
}

- (void) doneAddContacts:(id)sender{
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_TRANS_GROUP;
    _http._httpMethod = @"GET";
    
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                             _groupId,@"groupid",
                             [NSString stringWithFormat:@"%d", _selPerson.userId], @"userid",
                             nil];
    
    
    IMP_BLOCK_SELF(ChooseManagerViewController);
    
    
    [[WaitDialog sharedDialog] setTitle:@"加载..."];
    [[WaitDialog sharedDialog] startLoading];
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        [[WaitDialog sharedDialog] endLoading];
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"code"] intValue];
                
                if(code == 1)
                {
                    // NSArray *membs = [v objectForKey:@"text"];
                    [block_self successDoneQuit];
                }
                
                return;
            }
            
            
        };
        
        SBJson4ErrorBlock eh = ^(NSError* err) {
            
            
            
            NSLog(@"OOPS: %@", err);
        };
        
        id parser = [SBJson4Parser multiRootParserWithBlock:block
                                               errorHandler:eh];
        
        id data = [response dataUsingEncoding:NSUTF8StringEncoding];
        [parser parse:data];
        
        
    } FailBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        NSLog(@"%@", response);
        
        [[WaitDialog sharedDialog] endLoading];
        
    }];
    
}

- (void) successDoneQuit{
    
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    [_searchBar setShowsCancelButton:YES animated:YES];

}
- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    
    if([searchBar.text length] == 0)
    {
        [_searchBar setShowsCancelButton:NO animated:YES];
    
    }
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    
    _searchBar.text=  @"";
    [_searchBar setShowsCancelButton:NO animated:YES];
    
     [_results removeAllObjects];
    [_results addObjectsFromArray:_datas];
    
    [_tableView reloadData];
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    
    NSString *searchText = searchBar.text;
    
    if([searchText length] > 0)
    {
        
        [self doSearch:searchText];
        
    }
    
    //[_searchBar setShowsCancelButton:NO animated:YES];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    
    
    if([searchText length] > 0)
    {
        
        [self doSearch:searchText];
        
    }
    
    
}

- (void) doSearch:(NSString*)keywords{
    
    NSMutableArray *tmp = [NSMutableArray array];
   for(WSUser *u in _datas)
   {
       if([RCDUtilities isContains:u.fullname withString:keywords])
       {
           [tmp addObject:u];
       }
       
   }
    
    if([tmp count])
    {
        [_results removeAllObjects];
        [_results addObjectsFromArray:tmp];
        
        [_tableView reloadData];
    }
    
}


- (void) reloadData:(NSArray *)list{
    
    
    [_datas removeAllObjects];
    
    for(NSDictionary *dic in list)
    {
        WSUser *user = [[WSUser alloc] initWithDictionary:dic];
        [_datas addObject:user];
    }
    
    [_tableView reloadData];
    
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
