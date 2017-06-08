//
//  ChatPhotosView.h
//  Hint
//
//  Created by chen jack on 2017/5/7.
//  Copyright © 2017年 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChatPhotosViewDelegate <NSObject>

@optional
- (void) didSelectPhoto;

@end

@interface ChatPhotosView : UIView
{
    
}
@property (nonatomic, assign) RCConversationType converType;
@property (nonatomic, strong) NSString *_targetId;
@property (nonatomic, weak) UIViewController *_viewCtrl;
@property (nonatomic, weak) id <ChatPhotosViewDelegate> delegate;


- (void) forwardAction;
- (void) selectAction:(id)sender;
- (void) cancelSelect;
- (void) initChat;

- (void) deletAction;

- (NSArray *)selectedData;

@end
