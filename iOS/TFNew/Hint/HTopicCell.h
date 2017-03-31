//
//  HTopicCell.h
//  ZHEvent
//
//  Created by jack on 9/26/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <RongIMKit/RongIMKit.h>

@protocol TFCellEventViewDelegate <NSObject>

@optional
- (void) didDeleteConversation:(int)tag;
- (void) didSetMessageNotificationStatus:(int)tag;

@end

@interface TFCellEventView : UIButton
{
    
}
@property (nonatomic, weak) id <TFCellEventViewDelegate> _actionObj;
@end

@interface HTopicCell : RCConversationBaseCell
{
    
}
@property (nonatomic, strong) TFCellEventView *_rowBtn;

- (void) fillData:(RCConversationModel*)model;

@end
