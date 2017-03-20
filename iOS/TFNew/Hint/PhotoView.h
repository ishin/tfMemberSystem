//
//  PhotoView.h
//  Hint
//
//  Created by jack on 2/1/16.
//  Copyright © 2016 jack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PhotoView : UIView
{
    
}
@property (nonatomic, readonly) UIImageView *_photo;
@property (nonatomic, strong) NSDictionary *_meta;

@property (nonatomic, weak) UIView *_container;
@property (nonatomic, strong) NSDictionary *_status;

- (void) updateCenter;

@end
