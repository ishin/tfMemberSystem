//
//  UserCell.m
//  ZHEvent
//
//  Created by jack on 8/29/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "UserCell.h"
#import "SSUser.h"
#import "UIButton+Color.h"


@interface UserCell ()
{
    
    UILabel *_nameL;
    UIImageView *_actorLogo;
 
    UILabel *_line;
    
    UIButton *_btnAdd;
}

@end

@implementation UserCell
@synthesize _btnAdd;

- (id) initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    
    if(self = [super initWithStyle:style reuseIdentifier:reuseIdentifier])
    {
        
        self.backgroundColor = [UIColor clearColor];
        
        
        _actorLogo = [[UIImageView alloc] initWithFrame:CGRectMake(14, 5, 40, 40)];
        _actorLogo.layer.cornerRadius = 3;
        _actorLogo.clipsToBounds = YES;
        _actorLogo.backgroundColor = [UIColor clearColor];
        _actorLogo.layer.contentsGravity = kCAGravityResizeAspectFill;
        [self.contentView addSubview:_actorLogo];

        
        _nameL = [[UILabel alloc] initWithFrame:CGRectMake(60,
                                                           0,
                                                           SCREEN_WIDTH-70, 50)];
        _nameL.backgroundColor = [UIColor clearColor];
        [self.contentView addSubview:_nameL];
        _nameL.font = [UIFont boldSystemFontOfSize:16];
        _nameL.textAlignment = NSTextAlignmentLeft;
        _nameL.textColor  = COLOR_TEXT_A;
        _nameL.text = @"";
        
        
        _line = [[UILabel alloc] initWithFrame:CGRectMake(10, 49, SCREEN_WIDTH, 1)];
        _line.backgroundColor = LINE_COLOR;
        [self.contentView addSubview:_line];
        
        
        _btnAdd = [UIButton buttonWithColor:[UIColor whiteColor] selColor:nil];
        _btnAdd.layer.borderColor = THEME_RED_COLOR.CGColor;
        _btnAdd.layer.borderWidth = 1;
        _btnAdd.frame = CGRectMake(SCREEN_WIDTH-75, 10, 65, 30);
        [self.contentView addSubview:_btnAdd];
        [_btnAdd setTitle:@"加好友" forState:UIControlStateNormal];
        [_btnAdd setTitleColor:THEME_RED_COLOR forState:UIControlStateNormal];
        _btnAdd.titleLabel.font = [UIFont boldSystemFontOfSize:14];
        _btnAdd.layer.cornerRadius = 3;
        _btnAdd.clipsToBounds = YES;
        _btnAdd.hidden = YES;
        
    }
    
    return self;
}

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void) fillData:(SSUser*) person{
    
    _nameL.text = person.fullname;
    
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

@end
