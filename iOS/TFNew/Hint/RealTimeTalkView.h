//
//  RealTimeTalkView.h
//  Hint
//
//  Created by jack on 1/12/17.
//  Copyright © 2017 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WSUser;
@interface RealTimeTalkView : UIView
{
    
}
@property (nonatomic, strong) WSUser *_targetUser;


- (void) prepareUI;

- (void) startRCPTT;
- (void) endRCPTT;
@end
