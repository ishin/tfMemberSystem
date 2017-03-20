//
//  PhotoGetter.m
//  RTPG
//
//  Created by jack chen on 12-2-4.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "PhotoGetter.h"

@implementation PhotoGetter
@synthesize urls;
@synthesize delegate_;
@synthesize checkingExsit;

- (void) dealloc{
    
  }

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        self.backgroundColor = [UIColor grayColor];
        
        pds = [[NSMutableArray alloc] init];
        faileds = [[NSMutableArray alloc] init];
        
        for(int i = 0; i < 5; i++){
            HttpFileGetter *http = [[HttpFileGetter alloc] init];
            http.delegate_ = self;
            [pds addObject:http];
        }
        
        progress = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
        [self addSubview:progress];
        progress.backgroundColor = [UIColor clearColor];
        
        isStop = NO;
        
    }
    return self;
}

- (void) startLoading{
    
    isStop = NO;
    
    currentLoadedNumber = 0;
    nextIndex = 0;
    [faileds removeAllObjects];
    
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString* cachesDirectory = [paths objectAtIndex:0];
    NSFileManager *fm = [NSFileManager defaultManager];
    
    @synchronized(urls){
        
        int loadingCount = 0;
        for(int i = 0; i < [urls count]; i++){
            
            if(loadingCount > 4)break;
            
            NSDictionary *dic = [urls objectAtIndex:i];
            HttpFileGetter *http = [pds objectAtIndex:loadingCount];
            NSString *url = [dic objectForKey:@"url"];
            
            NSString *fileName = [dic objectForKey:@"filename"];
            
            if(checkingExsit){
                NSString* fullPathToFile = [cachesDirectory stringByAppendingPathComponent:fileName];
                if([fm fileExistsAtPath:fullPathToFile]){
                    
                    NSLog(@"File exist, continue next. At index: %d", nextIndex);
                    
                    nextIndex++;
                    
                    currentLoadedNumber++;
                    if(delegate_ && [delegate_ respondsToSelector:@selector(didLoadingProgressUpdated:)]){
                        [delegate_ didLoadingProgressUpdated:(float)currentLoadedNumber/[urls count]];
                    }
                    
                    continue;
                }
                
            }
           
            http.fileName_ = fileName;
            [http startLoading:url];

            loadingCount++;
            nextIndex++;
        }
    }
    
    
}

- (void) stopLoading{
    
    if(isStop)return;
    
    isStop = YES;
    
    @synchronized(pds){
        
        for(HttpFileGetter *http in pds){
            
            if([http isLoading])
            {
                [http cancel];
            }
            
        }
        
    }
    
    
}

- (void) didEndLoadingFile:(id)object success:(BOOL)success{
    currentLoadedNumber++;
    //progress.text = [NSString stringWithFormat:@"%d / %d", currentLoadedNumber, [urls count]];
    
    if(delegate_ && [delegate_ respondsToSelector:@selector(didLoadingProgressUpdated:)]){
        [delegate_ didLoadingProgressUpdated:(float)currentLoadedNumber/[urls count]];
    }
    
    if(!success)
    {
        int fi = nextIndex-1;
        if(fi < [urls count]){
            [faileds addObject:[urls objectAtIndex:fi]];
        }
    }
    
    if(isStop)
    {
        NSLog(@"Checked Stopped 0");
       return; 
    }
    
    if(nextIndex < [urls count]){
        
        @synchronized(pds){
            
            NSArray* paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
            NSString* cachesDirectory = [paths objectAtIndex:0];
            NSFileManager *fm = [NSFileManager defaultManager];
            
            for(HttpFileGetter *http in pds){
                
                BOOL end = NO;
                
                do{
                    
                    if(isStop)
                    {
                        NSLog(@"Checked Stopped 1");
                        return; 
                    }
                    
                    if(nextIndex >= [urls count])
                    {
                        end = YES;
                        break;
                    }
                    
                    if(![http isLoading]){
                        
                        NSDictionary *dic = [urls objectAtIndex:nextIndex];
                        NSString *url = [dic objectForKey:@"url"];
                        
                        NSString *fileName = [dic objectForKey:@"filename"];
                        if(checkingExsit){
                            NSString* fullPathToFile = [cachesDirectory stringByAppendingPathComponent:fileName];
                            if([fm fileExistsAtPath:fullPathToFile]){
                                
                                NSLog(@"File exist, continue next. At index: %d", nextIndex);
                                
                                nextIndex++;
                                
                                currentLoadedNumber++;
                                if(delegate_ && [delegate_ respondsToSelector:@selector(didLoadingProgressUpdated:)]){
                                    [delegate_ didLoadingProgressUpdated:(float)currentLoadedNumber/[urls count]];
                                }
                                
                                continue;
                                
                            }
                            
                        }
                        
                        NSLog(@"Loading at index: %d", nextIndex);
                        http.fileName_ = fileName;
                        [http startLoading:url];
                        nextIndex++;
                        
                        
                        break;
                    }
                    else{
                        break;
                    }
                
                }while (nextIndex < [urls count]);
                
                if(end)break;
                
                
            }
        }
    }
    
}
- (BOOL) checkFaileds{
    BOOL bRes = YES;
    [urls removeAllObjects];
    for(int i = 0; i < [faileds count]; i++){
        [urls addObject:[faileds objectAtIndex:i]];
        bRes = NO;
    }
    
    return bRes;
}

- (void) didLoadingPerProgress:(id)object progress:(float)p{
    if(delegate_ && [delegate_ respondsToSelector:@selector(didSingleLoadingProgressUpdated:)]){
        [delegate_ didSingleLoadingProgressUpdated:p];
    }
    
}

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect
 {
 // Drawing code
 }
 */

@end
