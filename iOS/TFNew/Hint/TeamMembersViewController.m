//
//  TeamMembersViewController.h
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "TeamMembersViewController.h"
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
#import "TeamOrg.h"
#import "UserInfoViewController.h"
#import "SearchContactResultView.h"
#import "RCDUtilities.h"


@interface TeamMembersViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
     SearchContactResultView *_resultView;
    
    NSMutableArray      *_tmpSearchMembs;
}
@property (nonatomic, strong) NSArray *_datas;
@property (nonatomic, strong) NSMutableArray *_membs;

@end

@implementation TeamMembersViewController
@synthesize _datas;
@synthesize _treeLevel;
@synthesize _teamOrg;

@synthesize _isChooseModel;
@synthesize _membs;


- (void) viewWillAppear:(BOOL)animated{

    self.navigationController.navigationBarHidden = NO;

}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    

    _tmpSearchMembs = [[NSMutableArray alloc] init];
    
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
    
    self._datas = _teamOrg._membs;


    _resultView = [[SearchContactResultView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)];
    _resultView._ctrl = self;
    _resultView._isChooseModel = _isChooseModel;
    
    
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
   
    id obj = [_datas objectAtIndex:indexPath.row];
    
    if([obj isKindOfClass:[TeamOrg class]])
    {
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
   
        
        TeamOrg *tt = obj;
        
        int xx = 20;
        if(_isChooseModel)
        {
            xx = 32;
            
            UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
            btn.frame = CGRectMake(0, 0, 100, 50);
            [cell.contentView addSubview:btn];
            [btn addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
            btn.tag = indexPath.row;
            
            UIImageView *select = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"add_c_normal.png"]];
            [cell.contentView addSubview:select];
            select.center = CGPointMake(18, 25);
            
            
            if(tt._isSelect)
            {
                select.image = [UIImage imageNamed:@"add_c_selected.png"];
            }
            
        }
        
        UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(xx,
                                                                   0,
                                                                   SCREEN_WIDTH-60, 50)];
        nameL.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:nameL];
        nameL.font = [UIFont systemFontOfSize:15];
        nameL.textAlignment = NSTextAlignmentLeft;
        nameL.textColor  = COLOR_TEXT_A;
        nameL.text = tt._teamName;
        
        cell.detailTextLabel.text = [NSString stringWithFormat:@"%d", (int)[tt._membs count]];
        cell.detailTextLabel.font = [UIFont systemFontOfSize:15];
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 49.5, SCREEN_WIDTH, 0.5)];
        line.backgroundColor = LINE_COLOR;
        [cell.contentView addSubview:line];
    }
    else
    {
        WSUser *person = obj;
        
        
        int xx = 10;
        if(_isChooseModel)
        {
            xx = 32;
            
            UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
            btn.frame = CGRectMake(0, 0, 100, 70);
            [cell.contentView addSubview:btn];
            [btn addTarget:self action:@selector(buttonClicked:) forControlEvents:UIControlEventTouchUpInside];
            btn.tag = indexPath.row;
            
            UIImageView *select = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"add_c_normal.png"]];
            [cell.contentView addSubview:select];
            select.center = CGPointMake(18, 35);
            
            
            if(person._isSelect)
            {
                select.image = [UIImage imageNamed:@"add_c_selected.png"];
            }

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
                                                                    SCREEN_WIDTH-10-CGRectGetMinX(_nameL.frame), 20)];
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
        
        UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 69.5, SCREEN_WIDTH, 0.5)];
        line.backgroundColor = LINE_COLOR;
        [cell.contentView addSubview:line];
        
    }
    return cell;
}


- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    return [_datas count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    id obj = [_datas objectAtIndex:indexPath.row];
    
    if([obj isKindOfClass:[TeamOrg class]])
    {
    return 50;
    }
    
    return 70;
}



- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return 50;
}


- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 50)];
    header.backgroundColor = [UIColor whiteColor];
    
    UIScrollView *content = [[UIScrollView alloc] initWithFrame:header.bounds];
    [header addSubview:content];
    
    int xx = 20;
    for(int i = 0; i < [_treeLevel count]; i++)
    {
        NSDictionary *dic = [_treeLevel objectAtIndex:i];
        
        UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(xx, 0, SCREEN_WIDTH-20, 50)];
        tL.font = [UIFont systemFontOfSize:14];
        [content addSubview:tL];
        tL.textColor = COLOR_TEXT_A;
        
        tL.text = [dic objectForKey:@"title"];
        
        
        
        CGSize s = [tL.text sizeWithAttributes:@{NSFontAttributeName:tL.font}];
        float width = s.width;
        CGRect rc = tL.frame;
        rc.size.width = width+5;
        tL.frame = rc;
        
        if(i <= [_treeLevel count] - 1)
        {
            tL.textColor = YELLOW_THEME_COLOR;
            
            UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
            btn.frame = tL.frame;
            btn.tag = i;
            [btn addTarget:self action:@selector(treeLevelButton:) forControlEvents:UIControlEventTouchUpInside];
            [content addSubview:btn];
        
        }

        
        UIImageView *expand = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"normal_expand.png"]];
        [content addSubview:expand];
        expand.center = CGPointMake(CGRectGetMaxX(rc)+10, 25);
        
        xx = CGRectGetMaxX(expand.frame);
        
    }
    
    
    
    UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(xx, 0, SCREEN_WIDTH-20, 50)];
    tL.font = [UIFont systemFontOfSize:14];
    [content addSubview:tL];
    tL.textColor = COLOR_TEXT_A;
    
    tL.text = _teamOrg._teamName;
   
    
    CGSize s = [tL.text sizeWithAttributes:@{NSFontAttributeName:tL.font}];
    float width = s.width;
    CGRect rc = tL.frame;
    rc.size.width = width+5;
    tL.frame = rc;
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = tL.frame;
    [content addSubview:btn];
    
    content.contentSize = CGSizeMake(CGRectGetMaxX(btn.frame)+10, 50);
    
    int offsetx = CGRectGetMaxX(btn.frame)+10 - SCREEN_WIDTH;
    if(offsetx < 0)
        offsetx = 0;
    [content setContentOffset:CGPointMake(offsetx, 0)];
    
    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, 49.5, SCREEN_WIDTH, 0.5)];
    line.backgroundColor = LINE_COLOR;
    [header addSubview:line];
    
    return header;
}


#pragma mark UITableView delegate


- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    
    id obj = [_datas objectAtIndex:indexPath.row];
    
    if([obj isKindOfClass:[TeamOrg class]])
    {
        TeamMembersViewController *team = [[TeamMembersViewController alloc] init];
        team.hidesBottomBarWhenPushed = YES;
        
        NSMutableArray *arr = [NSMutableArray arrayWithArray:_treeLevel];
        [arr addObject:@{@"title":_teamOrg._teamName, @"controller":self}];
        team._treeLevel = arr;//[NSMutableArray arrayWithObject:_teamOrg._teamName];
        team._teamOrg = obj;
        team._isChooseModel = _isChooseModel;
        [self.navigationController pushViewController:team animated:YES];
 
    }
    else
    {
        UserInfoViewController *info = [[UserInfoViewController alloc] init];
        info._user = obj;
        //info._isFriend = YES;
        [self.navigationController pushViewController:info animated:YES];
        

    }
   
    
}

- (void) treeLevelButton:(UIButton*)sender{
    

    NSDictionary *dic = [_treeLevel objectAtIndex:sender.tag];
    
    UIViewController *ctrl = [dic objectForKey:@"controller"];
    if(ctrl)
    {
        [self.navigationController popToViewController:ctrl animated:YES];
    }
    
}


- (void) doSetOrgSelect:(BOOL)sel team:(TeamOrg*)team{
    
    for(TeamOrg *t in team._membs)
    {
        if([t isKindOfClass:[TeamOrg class]])
        {
            t._isSelect = sel;
            
            [self doSetOrgSelect:sel team:t];
        }
        else
        {
            WSUser *uu = (WSUser*)t;
            uu._isSelect = sel;
        }
    }
    
}


- (void) buttonClicked:(UIButton*)sender{
    
    id obj = [_datas objectAtIndex:sender.tag];
    
    if([obj isKindOfClass:[TeamOrg class]])
    {
        TeamOrg *org = (TeamOrg*)obj;
        org._isSelect = !org._isSelect;
        
        [self doSetOrgSelect:org._isSelect team:org];
    }
    else
    {
        WSUser *uu = (WSUser*)obj;
        uu._isSelect = !uu._isSelect;
    }
    
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"NotifyRefreshContactSelection" object:nil];

    [_tableView reloadData];
}



- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
    
    [_searchBar setShowsCancelButton:YES animated:YES];
    
    [self.view addSubview:_resultView];
    
}
- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar{
    
    if([searchBar.text length] == 0)
    {
        [_searchBar setShowsCancelButton:NO animated:YES];
        
        if([_resultView superview])
            [_resultView removeFromSuperview];
    }
    
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    
    _searchBar.text=  @"";
    [_searchBar setShowsCancelButton:NO animated:YES];
    
    if([_searchBar isFirstResponder])
        [_searchBar resignFirstResponder];
    
    if([_resultView superview])
        [_resultView removeFromSuperview];
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


- (void) queryOrgUserMembs:(TeamOrg*)team{
    
    for (id obj in team._membs) {
        
        if([obj isKindOfClass:[TeamOrg class]])
        {
            [self queryOrgUserMembs:obj];
        }
        else
        {
            WSUser *uu = (WSUser*)obj;
            
            [_tmpSearchMembs addObject:uu];
        }
    }

}


- (void) doSearch:(NSString*)keywords{
    
    
    
    NSMutableArray *results = [NSMutableArray array];
    NSMutableDictionary *map = [NSMutableDictionary dictionary];
    
    
    [_tmpSearchMembs removeAllObjects];
    [self queryOrgUserMembs:_teamOrg];
    
    for(WSUser *uu in _tmpSearchMembs)
    {
        
        if([RCDUtilities isContains:uu.fullname withString:keywords])
        {
            id key = [NSNumber numberWithInt:uu.userId];
            if(![map objectForKey:key])
            {
                [map setObject:@"1" forKey:key];
                [results addObject:uu];
            }
        }
 
    }
    
    if([results count])
    {
        [self.view addSubview:_resultView];
    }
    
    _resultView._mapSelect = nil;
    _resultView._results = results;
    [_resultView refreshData];
    
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
