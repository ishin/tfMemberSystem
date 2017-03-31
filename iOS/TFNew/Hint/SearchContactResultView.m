//
//  SearchContactResultView.m
//  Hint
//
//  Created by jack on 6/14/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "SearchContactResultView.h"
#import "UserCell.h"
#import "WSUser.h"
#import "UIButton+Color.h"
#import "UserInfoViewController.h"
#import "UserDefaultsKV.h"
#import "SBJson4.h"
#import "WaitDialog.h"



@interface SearchContactResultView () <UITableViewDataSource, UITableViewDelegate>
{
    UITableView     *_tableView;

}

@end

@implementation SearchContactResultView
@synthesize _mapSelect;
@synthesize _results;
@synthesize _ctrl;
@synthesize _isChooseModel;


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (id) initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame])
    {
        _tableView = [[UITableView alloc] initWithFrame:self.bounds];
        _tableView.backgroundColor = [UIColor clearColor];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [self addSubview:_tableView];
        
        _isChooseModel = YES;
        
        self.backgroundColor = RGBA(0x00, 0x27, 0x2C, 0.4);
        
        
        
        
    }
    
    return self;
}



- (void) refreshData{
    
    
    
    [_tableView reloadData];
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
    
    WSUser *person = [_results objectAtIndex:indexPath.row];
    
    
    
    int xx = 10;
    if(_isChooseModel)
    {
        xx = 32;
        
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
    
    return [_results count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    
    return 1;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return 70;
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    WSUser* obj = [_results objectAtIndex:indexPath.row];
    
    if(_isChooseModel)
    {
        obj._isSelect = !obj._isSelect;
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"NotifyRefreshContactSelection" object:obj];
        
        [_tableView reloadData];
    }
    else
    {
        UserInfoViewController *info = [[UserInfoViewController alloc] init];
        info._user = obj;
        info.hidesBottomBarWhenPushed = YES;
        //info._isFriend = YES;
        [self._ctrl.navigationController pushViewController:info animated:YES];

    }
    
}




@end
