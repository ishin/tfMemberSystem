//
//  RCPTTCallAcionView.m
//  Gemini
//
//  Created by jack on 1/29/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "RCPTTCallAcionView.h"
#import "UIButton+Color.h"
#import "WSUser.h"
#import "SBJson4.h"
#import "GoGoDB.h"


@interface RCPTTCallAcionView ()
{
    UIImageView *_avatar;
    UILabel *_name;
    UILabel *_message;
    
    UIButton *_btnOn;
    UIButton *_btnOff;
    
    WebClient *_http;
}
@property (nonatomic, strong) NSString *_uid;
@end

@implementation RCPTTCallAcionView
@synthesize delegate_;
@synthesize _uid;
@synthesize _data;


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
        
        int h = 230;
        
        UIView *content = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 300, h)];
        content.backgroundColor = [UIColor whiteColor];
        [self addSubview:content];
        content.layer.cornerRadius = 5;
        content.clipsToBounds = YES;
        
        content.center = CGPointMake(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
        
        self.backgroundColor = RGBA(0x00, 0x27, 0x2C, 0.4);
        
        int top = 60;
        int xx  = 20;
        
        _avatar = [[UIImageView alloc] initWithFrame:CGRectMake(xx, top, 50, 50)];
        _avatar.layer.cornerRadius = 25;
        _avatar.clipsToBounds = YES;
        _avatar.layer.contentsGravity = kCAGravityResizeAspectFill;
        [content addSubview:_avatar];
        [_avatar setImage:[UIImage imageNamed:@"default_avatar.png"]];
        
        _name = [[UILabel alloc] initWithFrame:CGRectMake(xx+60, top,
                                                          300-xx-55, 20)];
        _name.backgroundColor = [UIColor clearColor];
        [content addSubview:_name];
        _name.font = [UIFont systemFontOfSize:15];
        _name.textAlignment = NSTextAlignmentLeft;
        _name.textColor  = COLOR_TEXT_A;
        
        _message = [[UILabel alloc] initWithFrame:CGRectMake(xx+60, top+20,
                                                             300-xx-55, 20)];
        _message.backgroundColor = [UIColor clearColor];
        [content addSubview:_message];
        _message.font = [UIFont systemFontOfSize:15];
        _message.textAlignment = NSTextAlignmentLeft;
        _message.textColor  = COLOR_TEXT_B;
        _message.text = @"请求与你对讲";
        
        top = CGRectGetMaxY(_avatar.frame)+40;
        
        _btnOn = [UIButton buttonWithColor:RGB(0x53, 0xcc, 0xdd) selColor:nil];
        _btnOn.frame = CGRectMake(xx, top, 120, 40);
        [content addSubview:_btnOn];
        _btnOn.layer.cornerRadius = 8;
        _btnOn.clipsToBounds = YES;
        _btnOn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_btnOn setTitle:@"接听" forState:UIControlStateNormal];
        [_btnOn addTarget:self action:@selector(connectAction:) forControlEvents:UIControlEventTouchUpInside];
        
        
        _btnOff = [UIButton buttonWithColor:RGB(0xf2, 0x9c, 0x9f) selColor:nil];
        _btnOff.frame = CGRectMake(300-120-xx, top, 120, 40);
        [content addSubview:_btnOff];
        _btnOff.layer.cornerRadius = 8;
        _btnOff.clipsToBounds = YES;
        _btnOff.titleLabel.font = [UIFont systemFontOfSize:16];
        [_btnOff setTitle:@"挂断" forState:UIControlStateNormal];
        [_btnOff addTarget:self action:@selector(stopAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return self;
}

- (void) fillUser:(NSString*)userid{
    
    self._uid = userid;
    

    RCUserInfo* cachedUser = [[GoGoDB sharedDBInstance] queryUser:userid];
    if(cachedUser)
    {
        [self fillUserInfo:cachedUser];
    }
    else
    {
        [self loadUserInfo:userid];
    }
}


- (void) loadUserInfo:(NSString *)userId{
    

    if(_http == nil)
    {
        _http = [[WebClient alloc] initWithDelegate:self];
    }
    
    _http._method = API_USER_PROFILE;
    _http._httpMethod = @"GET";
    
    
    _http._requestParam = [NSDictionary dictionaryWithObjectsAndKeys:
                           userId,@"userid",
                           nil];
    
    
    IMP_BLOCK_SELF(RCPTTCallAcionView);
    
    [_http requestWithSusessBlock:^(id lParam, id rParam) {
        
        NSString *response = lParam;
        // NSLog(@"%@", response);
        
        
        SBJson4ValueBlock block = ^(id v, BOOL *stop) {
            
            
            if([v isKindOfClass:[NSDictionary class]])
            {
                int code = [[v objectForKey:@"id"] intValue];
                
                if(code)
                {
            
                    [block_self parseUInfo:v];
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
        
        
    }];
}

- (void) parseUInfo:(NSDictionary*)res{
    
    RCUserInfo *user = [[RCUserInfo alloc] init];
    user.userId = _uid;
    user.name = [res objectForKey:@"name"];
    user.portraitUri = [NSString stringWithFormat:@"%@/upload/images/%@",
                        WEB_API_URL,
                        [res objectForKey:@"logo"]];
    
    [[GoGoDB sharedDBInstance] saveUserInfo:user];
    
    [self fillUserInfo:user];
}

- (void) fillUserInfo:(RCUserInfo*)uInfo{
    
    [_avatar setImageWithURL:[NSURL URLWithString:uInfo.portraitUri]
            placeholderImage:[UIImage imageNamed:@"default_avatar.png"]];
    
    _name.text = uInfo.name;
}


- (void) connectAction:(id)sender{
    
    if(delegate_ && [delegate_ respondsToSelector:@selector(didTouchJCActionButtonIndex:)])
    {
        [delegate_ didTouchJCActionButtonIndex:0];
    }
    
    [self stopAction:nil];
}



- (void) flashIcon{
    
   

}

- (void) animatedShow
{
    self.alpha = 0.0;

    [UIView animateWithDuration:0.25
                     animations:^{
                         
                         self.alpha = 1.0;
                         
                
                     } completion:^(BOOL finished) {
                         
                         
                     }];
}

- (void) flyAnimatedShow
{
    self.alpha = 0.0;
    
    
    //[self flashIcon];
    [UIView animateWithDuration:0.25
                     animations:^{
                         
                         self.alpha = 1.0;
                         
                         
                         
                     } completion:^(BOOL finished) {
                         
                         
                     }];
}

- (void) stopAction:(id)sender{
    

    [UIView animateWithDuration:0.35
                     animations:^{
                         
                         self.alpha = 0.0;
                         
                     } completion:^(BOOL finished) {
                         
                         [self removeFromSuperview];
                     }];
}


- (void) confirmSharePoint{
    
    
   
    
}


@end
