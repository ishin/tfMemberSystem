//
//  CMTabBarController.h
//  CMTabBarController
//
//  Created by mac on 13-8-13.
//  Copyright (c) 2013年 mac. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface CMTabBarController : UITabBarController <UITabBarControllerDelegate>{
 
    UILabel *tab1Title;
    //UILabel *tab2Title;
    UILabel *tab3Title;
    UILabel *tab4Title;
    UILabel *tab5Title;
    
    UIImageView *barItem01;
    //UIImageView *barItem02;
    UIImageView *barItem03;
    UIImageView *barItem04;
    UIImageView *barItem05;
    
    int _curIndex;
    
    UILabel *numberMsgs;
    UILabel *numberReqs;
    UILabel *_alertPoint;
}

- (void) setCurrentTabIndex:(int)index;

@end
