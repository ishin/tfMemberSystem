//
//  HttpFileGetter.h
//  
//
//  Created by jack on 8/1/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol HttpFileGetterDelegate

@optional
- (void) didEndLoadingFile:(id)object success:(BOOL)success;
- (void) didLoadingProgress:(id)object progress:(float)progress;
- (void) didLoadingPerProgress:(id)object progress:(float)progress;

@end

@interface HttpFileGetter : NSObject {
	NSURLConnection    *connection;
	NSMutableData      *characterBuffer;
	
	BOOL               done;
	
	NSString		  *url_;
	NSThread		  *_subThreed;
	
	BOOL			bLoading_;
	UIImage			*photo;
	
	long long		total_;
	long long		received_;
	NSString		*fileName_;
    
    BOOL isCancel;
	
}
@property (nonatomic, strong) NSURLConnection		*connection;
@property (nonatomic, strong) NSMutableData			*characterBuffer;
@property (nonatomic, strong) NSString				*url_;
@property (nonatomic, strong) UIImage				*photo;
@property (nonatomic, weak) id                       delegate_;
@property (nonatomic, strong) NSString				*fileName_;
@property (nonatomic, weak) id targetUpdate;


/*****************************************
 ** Start to loading thumb nail image
 *****************************************/
- (void) startLoading:(NSString*) url;

/*****************************************
 ** Cancel loading
 *****************************************/
- (void) cancel;

- (BOOL) isLoading;

@end
