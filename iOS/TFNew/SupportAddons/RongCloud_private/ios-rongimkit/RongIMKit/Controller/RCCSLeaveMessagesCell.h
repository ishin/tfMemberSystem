//
//  RCCSLeaveMessagesCell.h
//  RongIMKit
//
//  Created by 张改红 on 2016/12/5.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <RongIMLib/RongIMLib.h>
@interface RCCSLeaveMessagesCell : UITableViewCell
@property (nonatomic,strong)UILabel *titleLabel;
@property (nonatomic,strong)UITextField *infoTextField;
@property (nonatomic,strong)UITextView *infoTextView;
- (void)setDataWithModel:(RCCSLeaveMessageItem *)model;
@end
