//
//  MyRecContactsViewController.h
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "BaseViewController.h"

@protocol MyRecContactsViewControllerDelegate <NSObject>

@optional
- (void) didChoosedPerson:(id)person;
- (void) didCancelChoosedPerson:(id)person;

@end

@interface MyRecContactsViewController : BaseViewController
{
    
}
@property (nonatomic, assign) BOOL _isChooseModel;
@property (nonatomic, strong) NSMutableDictionary *_mapSelect;
@property (nonatomic, weak) id <MyRecContactsViewControllerDelegate> delegate_;

@property (nonatomic, strong) NSArray *_forwardMsgs;

@end
