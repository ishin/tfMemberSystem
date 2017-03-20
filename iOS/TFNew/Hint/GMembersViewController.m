//
//  GMembersViewController.m
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "GMembersViewController.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "WSUser.h"
#import "GoGoDB.h"
#import "UserDefaultsKV.h"
#import "UIButton+Color.h"
#import "UILabel+ContentSize.h"
#import "UIImage+Color.h"
#import "WSGroup.h"
#import "WaitDialog.h"


@interface GMembersViewController ()<UITableViewDataSource, UITableViewDelegate>
{
    BOOL _isLoading;
    
    UITableView         *_tableView;
    
    
    UIButton *btnSignin;
    
    WebClient *_client;
}
@property (nonatomic, strong) NSMutableDictionary *_mapSelect;
@property (nonatomic, strong) NSMutableArray *_datas;
@end

@implementation GMembersViewController
@synthesize _membs;
@synthesize _group;
@synthesize _mapSelect;
@synthesize _datas;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self._mapSelect = [NSMutableDictionary dictionary];
    
    self._datas = [NSMutableArray arrayWithArray:_membs];
    
    User *my = [UserDefaultsKV getUser];
    for(WSUser *u in _datas)
    {
        if(u.userId == [my._userId intValue])
        {
            [_datas removeObject:u];
            break;
        }
    }
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)
                                              style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor whiteColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    

    btnSignin = [UIButton buttonWithType:UIButtonTypeCustom];
    btnSignin.frame = CGRectMake(0, 0, 50, 40);
    [btnSignin setTitle:@"删除" forState:UIControlStateNormal];
    btnSignin.titleLabel.font = [UIFont systemFontOfSize:15];
    [btnSignin addTarget:self action:@selector(delAction:) forControlEvents:UIControlEventTouchUpInside];
    [btnSignin setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:btnSignin];

}

- (void) delAction:(id)sender{
    
    if([_mapSelect count] == 0){
        [self successDoneQuit];
        return;
    }
    
    NSString *gids = @"";
    for(WSUser *u in [_mapSelect allValues])
    {
        if([gids length] == 0)
            gids = [NSString stringWithFormat:@"%d", u.userId];
        else
            gids = [NSString stringWithFormat:@"%@,%d", gids, u.userId];
    }
    
    if(_client == nil)
    {
        _client = [[WebClient alloc] initWithDelegate:self];
    }
    
    _client._method = API_LEFT_GROUP;
    _client._httpMethod = @"GET";

    
    _client._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                             self._group.groupId,@"groupid",
                             [NSString stringWithFormat:@"[%@]", gids], @"groupids",
                             nil];
    
    
    IMP_BLOCK_SELF(GMembersViewController);
    
    
    [[WaitDialog sharedDialog] setTitle:@"正在删除..."];
    [[WaitDialog sharedDialog] startLoading];
    
    [_client requestWithSusessBlock:^(id lParam, id rParam) {
        
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
    
   
    WSUser *person = obj;
        
        
    int xx = 10;
    xx = 32;
    
    UIImageView *select = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"add_c_normal.png"]];
    [cell.contentView addSubview:select];
    select.center = CGPointMake(18, 35);
    
    id key = [NSNumber numberWithInt:person.userId];
    if([_mapSelect objectForKey:key])
    {
        select.image = [UIImage imageNamed:@"add_c_selected.png"];
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
    
   
    WSUser* person = [_datas objectAtIndex:indexPath.row];
    
    id key = [NSNumber numberWithInt:person.userId];
    if([_mapSelect objectForKey:key])
    {
        [_mapSelect removeObjectForKey:key];
    }
    else
    {
        [_mapSelect setObject:person forKey:key];
    }
    
    if([_mapSelect count])
    {
        int c = (int)[_mapSelect count];
        [btnSignin setTitle:[NSString stringWithFormat:@"删除(%d)", c] forState:UIControlStateNormal];
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
