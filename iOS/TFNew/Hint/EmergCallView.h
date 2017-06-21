//
//  EmergCallView.h
//  Hint
//
//  Created by jack on 1/29/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol EmergCallViewDelegate <NSObject>

@optional
- (void) didEmergCallButtonDown:(NSString*)mobile;

@end
@interface EmergCallView : UIView
{
    
}
@property (nonatomic, weak) id <EmergCallViewDelegate> delegate_;
@property (nonatomic, strong) NSDictionary *_data;

- (void) animatedShow;
- (void) flyAnimatedShow;
- (void) stopAction:(id)sender;

- (void) fillUser:(NSString*)userid;

@end
