//
//  ShootQRCode.h
//  Hint
//
//  Created by jack on 12/14/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ShootQRCode : NSObject
{
    
}
@property (nonatomic, weak) UIViewController *_viewController;


+ (ShootQRCode*)sharedShootCodeInstance;

- (void) shootCode:(UIViewController*)sender;

- (void) showMyCode:(UIViewController*)sender;

@end
