//
//  RCDSearchMoreController.h
//  RCloudMessage
//
//  Created by 张改红 on 16/9/26.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RCDSearchMoreController : UIViewController
@property (nonatomic,copy)NSString *searchString;
@property (nonatomic,copy)NSString *type;
@property (nonatomic,strong)NSArray *resultArray;
@property (nonatomic,copy) void (^cancelBlock)();
@property (nonatomic,assign) BOOL isShowSeachBar;
@property (nonatomic,assign)int messageCount;
@end
