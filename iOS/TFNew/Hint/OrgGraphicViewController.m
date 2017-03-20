//
//  OrgGraphicViewController.m
//  Hint
//
//  Created by jack on 2/15/17.
//  Copyright © 2017 jack. All rights reserved.
//

#import "OrgGraphicViewController.h"
#import "UIButton+Color.h"
#import "TeamOrg.h"
#import "TeamMembersViewController.h"

@interface OrgGView : UIView
{
    NSMutableArray *_lines;
    
    int _rightX;
}
@property (nonatomic, strong) NSMutableDictionary *_indexData;
@property (nonatomic, strong) NSMutableDictionary *_indexLines;
@property (nonatomic, strong) NSMutableDictionary *_indexLevelBtns;
@property (nonatomic, strong) NSMutableDictionary *_indexLMainBtns;

@property (nonatomic, weak) UINavigationController *_navictrl;
@property (nonatomic, weak) UIViewController *_ctrl;
@end

@implementation OrgGView
@synthesize _indexData;
@synthesize _indexLines;
@synthesize _indexLevelBtns;
@synthesize _indexLMainBtns;

@synthesize _navictrl;
@synthesize _ctrl;

- (id) initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame])
    {
        self.backgroundColor = [UIColor clearColor];
        
        _lines = [[NSMutableArray alloc] init];
        
        
    }
    return self;
}


- (void) drawOrgTils:(TeamOrg*)team{
  
    self._indexData = [NSMutableDictionary dictionary];
    self._indexLines = [NSMutableDictionary dictionary];
    self._indexLevelBtns = [NSMutableDictionary dictionary];
    self._indexLMainBtns = [NSMutableDictionary dictionary];
    
    NSArray *membs = team._membs;
    
    int c = (int)[membs count];
    
    int th = self.frame.size.height;
    
    NSString *name = team._teamName;
    
    CGSize s = [name sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:15]}];
    float width = s.width;
    
    UIButton *btn = [UIButton buttonWithColor:RGB(0xd8, 0xc8, 0xa8) selColor:nil];
    btn.frame = CGRectMake(10, th/2-20, width+20, 40);
    [btn setTitle:name forState:UIControlStateNormal];
    btn.titleLabel.font = [UIFont systemFontOfSize:15];
    [btn setTitleColor:COLOR_TEXT_A forState:UIControlStateNormal];
    [self addSubview:btn];
    btn.layer.cornerRadius = 3;
    btn.clipsToBounds = YES;
    
    int xx = CGRectGetMaxX(btn.frame)+50;
    
    int cy = CGRectGetMidY(btn.frame);
    
    int x0 = CGRectGetMaxX(btn.frame);
    int y0 = CGRectGetMidY(btn.frame);
    
    int rightSpace = 0;
    if(c)
    {
        int ty =  cy - (c*40 + (c-1)*20)/2;
        
        int maxW = 0;
        for(TeamOrg *t in membs)
        {
            NSString *name = t._teamName;
            
            CGSize s = [name sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:15]}];
            float width = s.width+30;
            
            if(maxW <= width)
                maxW = width;
        }
        
        for(TeamOrg *t in membs)
        {
            NSString *name = t._teamName;
            [_indexData setObject:t forKey:[NSNumber numberWithInt:t._teamId]];
            
    
            UIButton *btn = [UIButton buttonWithColor:[UIColor whiteColor] selColor:nil];
            btn.frame = CGRectMake(xx, ty, maxW, 40);
            [btn setTitle:name forState:UIControlStateNormal];
            btn.titleLabel.font = [UIFont systemFontOfSize:15];
            [btn setTitleColor:COLOR_TEXT_A forState:UIControlStateNormal];
            [self addSubview:btn];
            btn.layer.cornerRadius = 3;
            btn.clipsToBounds = YES;
            btn.tag = t._teamId;
            [btn addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
            
            int x1 = CGRectGetMinX(btn.frame);
            int y1 = CGRectGetMidY(btn.frame);
            
            ty+=60;
            
            NSArray *t1 = @[@{@"x":[NSNumber numberWithInt:x0], @"y":[NSNumber numberWithInt:y0]},
                            @{@"x":[NSNumber numberWithInt:x1], @"y":[NSNumber numberWithInt:y1]}];
            
            [_lines addObject:t1];
            
        }
        
        _rightX = xx;
        
        rightSpace = _rightX + maxW + 50;
        
        //[self drawSubTiles:membs];
    }
    
    
    if(rightSpace >= SCREEN_WIDTH)
    {
        CGRect rc = self.frame;
        rc.size.width = rightSpace;
        self.frame = rc;
        
        UIScrollView *content = (UIScrollView*)[self superview];
        [content setContentSize:CGSizeMake(CGRectGetWidth(self.frame), content.frame.size.height)];
        
        
    }
    
    
    
    
    [self setNeedsDisplay];
}



- (void) buttonAction:(UIButton*)pBtn{
    
    
    
    TeamOrg * team = [_indexData objectForKey:[NSNumber numberWithInt:(int)pBtn.tag]];
    if(team)
    {
        
        
        UIButton *oldBtn = [_indexLMainBtns objectForKey:[NSNumber numberWithInt:team._levelIndex]];
        [oldBtn setImageColor:[UIColor whiteColor]];
        
        [pBtn setImageColor:RGB(0xd8, 0xc8, 0xa8)];
        [_indexLMainBtns setObject:pBtn forKey:[NSNumber numberWithInt:team._levelIndex]];
        
        
        NSArray *membs = team._membs;
        
        int c = (int)[membs count];
        
        int xx = CGRectGetMaxX(pBtn.frame)+50;
        
        _rightX = CGRectGetMinX(pBtn.frame);
        
        int cy = CGRectGetMidY(pBtn.frame);
        
        int x0 = CGRectGetMaxX(pBtn.frame);
        int y0 = CGRectGetMidY(pBtn.frame);
        
        int maxW = 50;
        int ty = 0;
        
        BOOL haveSubNodes = NO;
        
        if(c)
        {
            ty =  cy - (c*40 + (c-1)*20)/2;
            if(ty < 50)
                ty = 50;
            
            
            for(TeamOrg *t in membs)
            {
                if([t isKindOfClass:[TeamOrg class]])
                {
                    NSString *name = t._teamName;
                    
                    CGSize s = [name sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:15]}];
                    float width = s.width+30;
                    
                    if(maxW <= width)
                        maxW = width;
                    
                    haveSubNodes = YES;
                }
                
            }
        }
        
        if(!haveSubNodes)
        {
            [self jumpUserMembs:team];
            return;
        }
        
        //移除其他分支线条
        
        NSMutableArray *leaveLines = [_indexLines objectForKey:[NSNumber numberWithInt:team._levelIndex]];
        if(leaveLines == nil)
        {
            leaveLines = [NSMutableArray array];
            [_indexLines setObject:leaveLines forKey:[NSNumber numberWithInt:team._levelIndex]];
            
        }
        else
        {
            [leaveLines removeAllObjects];
            
            int idx = team._levelIndex + 1;
            
            do {
                id key = [NSNumber numberWithInt:idx];
                
                if([_indexLines objectForKey:key])
                {
                    [_indexLines removeObjectForKey:key];
                    
                    idx++;
                }
                else
                {
                    break;
                }
                
            } while (1);

        }
       
        //移除其他分支按钮
        
        NSMutableArray *orgBtns = [_indexLevelBtns objectForKey:[NSNumber numberWithInt:team._levelIndex]];
        if(orgBtns == nil)
        {
            orgBtns = [NSMutableArray array];
            [_indexLevelBtns setObject:orgBtns forKey:[NSNumber numberWithInt:team._levelIndex]];
            
        }
        else
        {
            [orgBtns makeObjectsPerformSelector:@selector(removeFromSuperview)];
            [orgBtns removeAllObjects];
            
            int idx = team._levelIndex + 1;
            
            do {
                id key = [NSNumber numberWithInt:idx];
                
                NSArray *tt = [_indexLevelBtns objectForKey:key];
                if(tt)
                {
                    [tt makeObjectsPerformSelector:@selector(removeFromSuperview)];
                    
                    [_indexLevelBtns removeObjectForKey:key];
                    
                    idx++;
                }
                else
                {
                    break;
                }
                
            } while (1);
            
        }
        
        
        if(c)
        {
            for(TeamOrg *t in membs)
            {
                if([t isKindOfClass:[TeamOrg class]])
                {
                    NSString *name = t._teamName;
                    [_indexData setObject:t forKey:[NSNumber numberWithInt:t._teamId]];
                    
                    
                    UIButton *btn = [UIButton buttonWithColor:[UIColor whiteColor] selColor:nil];
                    btn.frame = CGRectMake(xx, ty, maxW, 40);
                    [btn setTitle:name forState:UIControlStateNormal];
                    btn.titleLabel.font = [UIFont systemFontOfSize:15];
                    [btn setTitleColor:COLOR_TEXT_A forState:UIControlStateNormal];
                    [self addSubview:btn];
                    btn.layer.cornerRadius = 3;
                    btn.clipsToBounds = YES;
                    btn.tag = t._teamId;
                    [btn addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
                    
                    [orgBtns addObject:btn];
                    
                    int x1 = CGRectGetMinX(btn.frame);
                    int y1 = CGRectGetMidY(btn.frame);
                    
                    ty+=60;
                    
                    NSArray *t1 = @[@{@"x":[NSNumber numberWithInt:x0], @"y":[NSNumber numberWithInt:y0]},
                                    @{@"x":[NSNumber numberWithInt:x1], @"y":[NSNumber numberWithInt:y1]}];
                    
                    [leaveLines addObject:t1];
                }
                
            }
        }
        
        
        CGRect rc = self.frame;
        rc.size.width = xx + maxW + 50;
        self.frame = rc;
        
        
        UIScrollView *content = (UIScrollView*)[self superview];
        [content setContentSize:CGSizeMake(CGRectGetWidth(self.frame), content.frame.size.height)];

        [content setContentOffset:CGPointMake(_rightX-50, 0) animated:YES];
        
    }
    
    [self setNeedsDisplay];
}


- (void) jumpUserMembs:(TeamOrg*)team{
    
    TeamMembersViewController *teamCtrl = [[TeamMembersViewController alloc] init];
    teamCtrl._treeLevel = [NSMutableArray arrayWithObject:@{@"title":@"组织机构", @"controller":self._ctrl}];
    teamCtrl._teamOrg = team;
    
    [self._navictrl pushViewController:teamCtrl animated:YES];
}

- (void)drawRect:(CGRect)rect
{
    //1.获取上下文
    CGContextRef context = UIGraphicsGetCurrentContext();
    //2.设置当前上下问路径
    
    
    for(NSArray *line in _lines)
    {
        
        NSDictionary *s = [line objectAtIndex:0];
        NSDictionary *e = [line objectAtIndex:1];
        
        //设置起始点
        CGContextMoveToPoint(context, [[s objectForKey:@"x"] intValue], [[s objectForKey:@"y"] intValue]);
        //增加点
        CGContextAddLineToPoint(context, [[e objectForKey:@"x"] intValue], [[e objectForKey:@"y"] intValue]);
        //CGContextAddLineToPoint(context, 50, 200);
        
    }
    
    for(id key in [_indexLines allKeys])
    {
        NSArray *lines = [_indexLines objectForKey:key];
        
        for(NSArray *line in lines)
        {
            
            NSDictionary *s = [line objectAtIndex:0];
            NSDictionary *e = [line objectAtIndex:1];
            
            //设置起始点
            CGContextMoveToPoint(context, [[s objectForKey:@"x"] intValue], [[s objectForKey:@"y"] intValue]);
            //增加点
            CGContextAddLineToPoint(context, [[e objectForKey:@"x"] intValue], [[e objectForKey:@"y"] intValue]);
            //CGContextAddLineToPoint(context, 50, 200);
            
        }

    }
    
    
    //关闭路径
    CGContextClosePath(context);
    //3.设置属性
    /*
     UIKit会默认导入 core Graphics框架，UIKit对常用的很多的唱歌方法做了封装
     UIColor setStroke设置边线颜色
     uicolor setFill 设置填充颜色
     
     */
    [COLOR_TEXT_B setStroke];
    //[[UIColor blueColor]setFill];
    //    [[UIColor yellowColor]set];
    //4.绘制路径
    CGContextDrawPath(context, kCGPathFillStroke);
}


@end

@interface OrgGraphicViewController ()
{
    OrgGView *_orgView;
}

@end


@implementation OrgGraphicViewController
@synthesize _team;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"组织结构";
    
    self.view.backgroundColor = RGB(0xf2, 0xf2, 0xf2);
    
    UIScrollView *content = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    [self.view addSubview:content];
    
    
    _orgView = [[OrgGView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT-64)];
    [_orgView drawOrgTils:_team];
    [content addSubview:_orgView];
    _orgView._navictrl = self.navigationController;
    _orgView._ctrl = self;
    
    [content setContentSize:CGSizeMake(CGRectGetWidth(_orgView.frame), content.frame.size.height)];

}

@end
