//
//  PhotoGetter.h
//  RTPG
//
//  Created by jack chen on 12-2-4.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HttpFileGetter.h"

@protocol PhotoGetterDelegate <NSObject>

@optional
- (void) didLoadingProgressUpdated:(float)p;
- (void) didSingleLoadingProgressUpdated:(float)p;

@end

@interface PhotoGetter : UIView <HttpFileGetterDelegate>
{
    NSMutableArray *pds;
    
    NSMutableArray *urls;
    int currentLoadedNumber;
    int nextIndex;
    
    UILabel  *progress;
    
    
    NSMutableArray *faileds;
    
    BOOL checkingExsit;
    
    BOOL isStop;
    
}
@property (nonatomic, weak) id delegate_;
@property (nonatomic, strong) NSMutableArray *urls;
@property (nonatomic) BOOL checkingExsit;



- (void) startLoading;
- (void) stopLoading;

- (BOOL) checkFaileds;


@end
