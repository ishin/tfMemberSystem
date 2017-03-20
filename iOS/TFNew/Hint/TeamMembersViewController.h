//
//  TeamMembersViewController.h
//  Hint
//
//  Created by jack on 2/21/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import "BaseViewController.h"

@class TeamOrg;
@interface TeamMembersViewController : BaseViewController
{
    
}
@property (nonatomic, strong) NSMutableArray *_treeLevel;
@property (nonatomic, strong) TeamOrg *_teamOrg;
@property (nonatomic, assign) BOOL _isChooseModel;

@end
