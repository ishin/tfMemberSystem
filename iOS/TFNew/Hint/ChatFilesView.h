//
//  ChatFilesView.h
//  Hint
//
//  Created by chen jack on 2017/5/7.
//  Copyright © 2017年 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ChatFilesViewDelegate <NSObject>

@optional
- (void) didSelectFile;

@end

@interface ChatFilesView : UIView
{
    
}
@property (nonatomic, assign) RCConversationType converType;
@property (nonatomic, strong) NSString *_targetId;
@property (nonatomic, weak) UIViewController *_viewCtrl;
@property (nonatomic, assign) int _isVideo;
@property (nonatomic, weak) id <ChatFilesViewDelegate> delegate;


- (void) forwardAction;
- (void) selectAction:(id)sender;
- (void) cancelSelect;
- (void) initChat;

- (void) deletAction;

- (NSArray *)selectedData;

@end
