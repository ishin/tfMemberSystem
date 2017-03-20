//
//  RCPTTCallAcionView.h
//  Gemini
//
//  Created by jack on 1/29/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol RCPTTCallAcionViewDelegate <NSObject>

@optional
- (void) didTouchJCActionButtonIndex:(int)index;

@end
@interface RCPTTCallAcionView : UIView
{
    
}
@property (nonatomic, weak) id <RCPTTCallAcionViewDelegate> delegate_;
@property (nonatomic, strong) NSDictionary *_data;

- (void) animatedShow;
- (void) flyAnimatedShow;
- (void) stopAction:(id)sender;

- (void) fillUser:(NSString*)userid;

@end
