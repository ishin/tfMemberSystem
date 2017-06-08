//
//  SearchContactViewController.m
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "SearchContactViewController.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "WSUser.h"
#import "ShootQRCode.h"
#import "GoGoDB.h"
#import "UserDefaultsKV.h"
#import "UIButton+Color.h"
#import "UILabel+ContentSize.h"
#import "UIImage+Color.h"
#import "ShootQRCode.h"
#import "DataMatrix.h"
#import "QREncoder.h"


@interface SearchContactViewController ()<UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate>
{
    BOOL _isLoading;
    
    UISearchBar         *_searchBar;
    UITableView         *_tableView;
    
    UIView              *_headerView;
    
    UIView              *_membsChoosedPannel;
    UIScrollView        *_membsScroll;
    UILabel             *_membsNumL;
    
    UIView              *_myCodeView;
    
}
@property (nonatomic, strong) NSMutableArray *_headerDatas;
@property (nonatomic, strong) NSMutableArray *_datas;

@property (nonatomic, strong) NSMutableDictionary *_mapSelect;
@property (nonatomic, strong) NSMutableArray *_membs;

@property (nonatomic, assign) int _willDelIndex;

@end

@implementation SearchContactViewController
@synthesize _headerDatas;
@synthesize _datas;
@synthesize _membs;
@synthesize _mapSelect;
@synthesize _willDelIndex;


- (void) viewWillAppear:(BOOL)animated{
    
    self.navigationController.navigationBarHidden = NO;
    
}

- (void) backAction:(id)sender{
    
    [self hiddenMembersPannel];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:
                                
                                [UIColor whiteColor],
                                
                                NSForegroundColorAttributeName, nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:attributes];
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    
    UIButton *btnCancel = [UIButton buttonWithType:UIButtonTypeCustom];
    btnCancel.frame = CGRectMake(0, 0, 50, 40);
    [btnCancel setTitle:@"取消" forState:UIControlStateNormal];
    btnCancel.titleLabel.font = [UIFont boldSystemFontOfSize:15];
    [btnCancel addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnCancel setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnCancel];
    
    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"icon_fanhui_white.png"] forState:UIControlStateNormal];
    backBtn.frame = CGRectMake(0, 0, 25, 44);
    [backBtn addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    self.navigationItem.leftBarButtonItem = backBarButtonItem;

    self.title = @"添加到常用联系人";
    
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
    

    self._datas = [NSMutableArray array];
    
    self._mapSelect = [NSMutableDictionary dictionary];
    self._membs = [NSMutableArray array];
  
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 44, SCREEN_WIDTH, SCREEN_HEIGHT-64-44)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    UIView *tfooter = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 120)];
    _tableView.tableFooterView = tfooter;
    
    AppDelegate *app = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    _membsChoosedPannel = [app userMembsPannel];
    [app.window addSubview:_membsChoosedPannel];
    
    [app clearMembsPannel];
    
    _membsScroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 50, SCREEN_WIDTH, 60)];
    _membsScroll.backgroundColor = [UIColor clearColor];
    [_membsChoosedPannel addSubview:_membsScroll];
    
    UIButton *btnGroupOK = [UIButton buttonWithType:UIButtonTypeCustom];
    btnGroupOK.frame = CGRectMake(SCREEN_WIDTH-120, 0, 120, 50);
    [_membsChoosedPannel addSubview:btnGroupOK];
    btnGroupOK.backgroundColor = [UIColor clearColor];
    [btnGroupOK addTarget:self action:@selector(doneAddContacts:) forControlEvents:UIControlEventTouchUpInside];
    
    UIImageView *icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"group_ensure.png"]];
    [btnGroupOK addSubview:icon];
    icon.center = CGPointMake(25, 30);
    
    _membsNumL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(icon.frame)+5, 10, 80, 40)];
    [btnGroupOK addSubview:_membsNumL];
    _membsNumL.text = @"完成";
    _membsNumL.font = [UIFont systemFontOfSize:13];
    
   
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 150)];
    _tableView.tableFooterView = footerView;
    footerView.backgroundColor = [UIColor clearColor];
    
    
    UIButton *bg = [UIButton buttonWithColor:[UIColor whiteColor] selColor:LINE_COLOR];
    bg.frame = CGRectMake(0, 0, SCREEN_WIDTH, 60);
    [footerView addSubview:bg];
    [bg addTarget:self action:@selector(showMyCode:) forControlEvents:UIControlEventTouchUpInside];
    
    icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"QR-code.png"]];
    [bg addSubview:icon];
    icon.center = CGPointMake(30, 32);
    
    UILabel *tL = [[UILabel alloc] initWithFrame:CGRectMake(60,
                                                            0,
                                                            SCREEN_WIDTH-70, 60)];
    tL.backgroundColor = [UIColor clearColor];
    [bg addSubview:tL];
    tL.font = [UIFont systemFontOfSize:15];
    tL.textAlignment = NSTextAlignmentLeft;
    tL.textColor  = COLOR_TEXT_A;
    tL.text = @"我的二维码";
    

    UILabel *line = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(bg.frame), SCREEN_WIDTH, 1)];
    line.backgroundColor = LINE_COLOR;
    [footerView addSubview:line];
    
    UIButton *shareWechat = [UIButton buttonWithColor:[UIColor whiteColor] selColor:LINE_COLOR];
    shareWechat.frame = CGRectMake(0, CGRectGetMaxY(line.frame)+20, SCREEN_WIDTH, 60);
    [footerView addSubview:shareWechat];
    [shareWechat addTarget:self action:@selector(scanQR:) forControlEvents:UIControlEventTouchUpInside];
    
    icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"scan_sb.png"]];
    [shareWechat addSubview:icon];
    icon.center = CGPointMake(30, 32);
    
    tL = [[UILabel alloc] initWithFrame:CGRectMake(60,
                                                   0,
                                                   SCREEN_WIDTH-70, 60)];
    tL.backgroundColor = [UIColor clearColor];
    [shareWechat addSubview:tL];
    tL.font = [UIFont systemFontOfSize:15];
    tL.textAlignment = NSTextAlignmentLeft;
    tL.textColor  = COLOR_TEXT_A;
    tL.text = @"扫一扫";
  
}

- (void) showMyCode:(id)sender{
    
    
    if(_myCodeView)
    {
        [self.view addSubview:_myCodeView];
    }
    else
    {
        User *u = [UserDefaultsKV getUser];
        
        NSString *token = u._authtoken;
        if([token length] > 10)
            token = [token substringToIndex:10];
        NSString *qr = [NSString stringWithFormat:@"%@&%@", u._account, token];
        [self showMyInfo:qr];
        
    }
    
}

- (void) onTapSelected:(id)sender{
    
    [UIView animateWithDuration:0.25 animations:^{
        _myCodeView.alpha = 0.0;
    } completion:^(BOOL finished) {
        
        [_myCodeView removeFromSuperview];
        _myCodeView.alpha = 1.0;
    }];
}

- (void) showMyInfo:(NSString*)qr{
    
    _myCodeView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    _myCodeView.backgroundColor = RGBA(0x00, 0x27, 0x2C, 0.4);
    
    [self.view addSubview:_myCodeView];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(onTapSelected:)];
    [_myCodeView addGestureRecognizer:tap];
    
    int h = SCREEN_HEIGHT - 64;
    
    int ww = SCREEN_WIDTH - 30 - 30;
    int wh = SCREEN_WIDTH - 30 - 30 + 120;
    
    int whiteWidth =  SCREEN_WIDTH-30;
    
    UIView* whiteView = [[UIView alloc] initWithFrame:CGRectMake(15, 0, whiteWidth, wh)];
    whiteView.backgroundColor = [UIColor whiteColor];
    whiteView.layer.cornerRadius = 2;
    whiteView.clipsToBounds = YES;
    [_myCodeView addSubview:whiteView];
    whiteView.center = CGPointMake(SCREEN_WIDTH/2, h/2);
    
    UIImageView *qrcode = [[UIImageView alloc] initWithFrame:CGRectMake(20, 80, ww-10, ww-10)];
    [whiteView addSubview:qrcode];
    qrcode.layer.contentsGravity = kCAGravityCenter;
    
    User *u = [UserDefaultsKV getUser];
    
    DataMatrix *data_matrix = [QREncoder encodeWithECLevel:QR_ECLEVEL_AUTO version:QR_VERSION_AUTO string:qr];
    qrcode.image = [QREncoder renderDataMatrix:data_matrix imageDimension:200];
    
    UIImageView* actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(30, 30, 50, 50)];
    actorLogo.layer.cornerRadius = 25;
    actorLogo.clipsToBounds = YES;
    actorLogo.backgroundColor = [UIColor clearColor];
    actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
    [whiteView addSubview:actorLogo];
    [actorLogo setImageWithURL:[NSURL URLWithString:u._avatar]
              placeholderImage:[UIImage imageNamed:@"defalut_avatar.png"]];
    
    
    UILabel* nameL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(actorLogo.frame)+10,
                                                               30,
                                                               whiteWidth - 100, 25)];
    nameL.backgroundColor = [UIColor clearColor];
    [whiteView addSubview:nameL];
    nameL.font = [UIFont boldSystemFontOfSize:15];
    nameL.textAlignment = NSTextAlignmentLeft;
    nameL.textColor  = COLOR_TEXT_A;
    nameL.text = u._userName;
    
    UIImageView* u_gener = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"gender_male.png"]];
    [whiteView addSubview:u_gener];
    
    CGSize s = [nameL.text sizeWithAttributes:@{NSFontAttributeName:nameL.font}];
    
    u_gener.center = CGPointMake(CGRectGetMinX(nameL.frame)+s.width+20, nameL.center.y);
    
    if([u.gender intValue] == 1)
    {
        u_gener.image = [UIImage imageNamed:@"gender_male.png"];
    }
    else
    {
        u_gener.image = [UIImage imageNamed:@"gender_female.png"];
    }

    
    UILabel* cmpL = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(actorLogo.frame)+10,
                                                              CGRectGetMaxY(nameL.frame),
                                                              whiteWidth - (CGRectGetMaxX(actorLogo.frame)+30) , 20)];
    cmpL.backgroundColor = [UIColor clearColor];
    [whiteView addSubview:cmpL];
    cmpL.font = [UIFont systemFontOfSize:14];
    cmpL.textAlignment = NSTextAlignmentLeft;
    cmpL.textColor  = COLOR_TEXT_B;
    cmpL.text = [NSString stringWithFormat:@"%@ %@", u.companyname, u.ranktitle];
    
    
    UILabel* alertL = [[UILabel alloc] initWithFrame:CGRectMake(10,
                                                                CGRectGetMaxY(qrcode.frame)+10,
                                                                CGRectGetWidth(whiteView.frame)-20, 30)];
    alertL.backgroundColor = [UIColor clearColor];
    [whiteView addSubview:alertL];
    alertL.font = [UIFont systemFontOfSize:12];
    alertL.textAlignment = NSTextAlignmentCenter;
    alertL.textColor  = COLOR_TEXT_B;
    alertL.text = @"扫描上面二维码，加我为联系人";

    
    
}


- (void) scanQR:(id)sender{
    
    [[ShootQRCode sharedShootCodeInstance] shootCode:self];
}

- (void) showMembersPannel{
    
    [self showMembs];
    
    [UIView animateWithDuration:0.25
                     animations:^{
                         
                         _membsChoosedPannel.frame = CGRectMake(0, SCREEN_HEIGHT-110, SCREEN_WIDTH, 110);
                         
                     } completion:^(BOOL finished) {
                         
                     }];
}

- (void) hiddenMembersPannel{
    
    [UIView animateWithDuration:0.25
                     animations:^{
                         
                         _membsChoosedPannel.frame = CGRectMake(0, SCREEN_HEIGHT, SCREEN_WIDTH, 110);
                         
                     } completion:^(BOOL finished) {
                         
                     }];
}

- (void) showMembs{
    
    [[_membsScroll subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    int xx = 10;
    int w = 34;
    
    for(int i = 0; i < [_membs count]; i++)
    {
        WSUser *u = [_membs objectAtIndex:i];
        
        UIImageView *avatar = [[UIImageView alloc] initWithFrame:CGRectMake(xx, 5, w, w)];
        avatar.layer.cornerRadius = 17;
        avatar.clipsToBounds = YES;
        avatar.layer.contentsGravity = kCAGravityResizeAspectFill;
        [_membsScroll addSubview:avatar];
        [avatar setImageWithURL:[NSURL URLWithString:u.avatarurl]
                         placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
        
        
        UILabel* tL = [[UILabel alloc] initWithFrame:CGRectMake(xx-10,
                                                                CGRectGetMaxY(avatar.frame)+2,
                                                                w+20, 20)];
        tL.backgroundColor = [UIColor clearColor];
        [_membsScroll addSubview:tL];
        tL.font = [UIFont systemFontOfSize:11];
        tL.textAlignment = NSTextAlignmentCenter;
        tL.textColor  = COLOR_TEXT_A;
        
        tL.text = u.fullname;
        
        
        UIButton *btnUser = [UIButton buttonWithType:UIButtonTypeCustom];
        btnUser.frame = avatar.frame;
        [_membsScroll addSubview:btnUser];
        btnUser.tag = i;
        [btnUser addTarget:self action:@selector(clickedToRemove:) forControlEvents:UIControlEventTouchUpInside];

        
        xx+=w;
        xx+=12;
        
    }
        
}

- (void) clickedToRemove:(UIButton*)btn{
    
    self._willDelIndex = (int)btn.tag;
    
    
    WSUser *u = [_membs objectAtIndex:_willDelIndex];
    
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                    message:[NSString stringWithFormat:@"删除\"%@\"？", u.fullname]
                                                   delegate:self
                                          cancelButtonTitle:@"取消"
                                          otherButtonTitles:@"确定", nil];
    alert.tag = 12017;
    [alert show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    
    if(alertView.tag == 12017 && buttonIndex != alertView.cancelButtonIndex)
    {
         WSUser *u = [_membs objectAtIndex:_willDelIndex];
        id key = [NSNumber numberWithInt:u.userId];
        if([_mapSelect objectForKey:key])
        {
            [_mapSelect removeObjectForKey:key];
        }
        [_membs removeObjectAtIndex:_willDelIndex];
        
        [_tableView reloadData];
        
        if([_membs count])
        {
            [self showMembs];
        }
        else
        {
            [self hiddenMembersPannel];
        }
    }
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
    
    WSUser *person = [_datas objectAtIndex:indexPath.row];
    
    
    UIImageView* _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(32, 10, 50, 50)];
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


    
    UIImageView *select = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"add_c_normal.png"]];
    [cell.contentView addSubview:select];
    select.center = CGPointMake(18, 35);
  

    if([_mapSelect objectForKey:[NSNumber numberWithInt:person.userId]])
    {
        select.image = [UIImage imageNamed:@"add_c_selected.png"];
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
    id key = [NSNumber numberWithInt:person.userId];
    if([_mapSelect objectForKey:key])
    {
        [_mapSelect removeObjectForKey:key];
        [_membs removeObject:person];
    }
    else
    {
        [_mapSelect setObject:person forKey:key];
        [_membs insertObject:person atIndex:0];
    }

    
    _membsNumL.text = [NSString stringWithFormat:@"完成（%d）", (int)[_membs count]];
    
    if([_membs count])
    {
        [self showMembersPannel];
    }
    else
    {
        [self hiddenMembersPannel];
    }
    
    [_tableView reloadData];
}


- (void) doneAddContacts:(id)sender{
    
    User *my = [UserDefaultsKV getUser];
    
    //addMyFriend
    for(WSUser *u in _membs)
    {
        [u addMyFriend:my._account];
    }
    
      
    
    //[self hiddenMembersPannel];
    
    [self backAction:nil];
    
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
    
    
    [_datas removeAllObjects];
    
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
    
    [self doSearch:searchText];
}

- (void) doSearch:(NSString*)keywords{
    
   //member!searchUser
    if(_isLoading)
        return;
    _isLoading = YES;
    
    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._httpMethod = @"POST";
    

    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    
   
    _http._method = API_SEARCH_FANS;
    
    [params setObject:keywords forKey:@"account"];
     
    
    _http._requestParam = params;
    
    
    IMP_BLOCK_SELF(SearchContactViewController);
    
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        //NSLog(@"%@", response);
        
        _isLoading = NO;
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            if([v isKindOfClass:[NSArray class]])
            {
                [block_self reloadData:v];
                
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
        
        _isLoading = NO;
        
    }];
    
    
}


- (void) reloadData:(NSArray *)list{
    
    
    [_datas removeAllObjects];
    
   
    
    for(NSDictionary *dic in list)
    {
        if([dic objectForKey:@"id"])
        {
            WSUser *user = [[WSUser alloc] initWithDictionary:dic];
            [_datas addObject:user];
        }
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
