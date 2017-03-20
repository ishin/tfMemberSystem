//
//  iHttpAdmin.h
//  
//
//  Created by Jack on 3/12/09.
//  Copyright 2009 Jack. All rights reserved.
//




@protocol iHttpAdminDelegate;

@interface iHttpAdmin : NSObject {
    
	//id						delegate_;
    //data

	NSURLConnection			*connection;
	NSMutableData			*characterBuffer;
	//NSAutoreleasePool		*uploadPool;
	
	NSInteger				errorCode_;
	
	BOOL					done;
	
	NSString				*error_;
		
	//ArchiveParser			*archiveParser_;
		
	int						curPage;
	int						curCommentPage_;
	int						curPublicPage;
	int						curMyCommentPage;
    
    
    long long				total_;
	long long				received_;
    
	
}
@property (nonatomic, weak) id  delegate_;
@property (nonatomic, strong) NSURLConnection *connection;
@property (nonatomic, strong) NSMutableData *characterBuffer;
//@property (nonatomic, assign) NSAutoreleasePool *uploadPool;
@property NSInteger errorCode_;
@property (nonatomic, strong) NSString *error_;

+ (NSString*) escapeURIComponent:(NSString*)src;


- (void)sendUrlRequest:(NSString*)url param:(NSDictionary*)param;
- (void)sendUrlRequestWithMethod:(NSString*)url param:(NSDictionary*)param method:(NSString*)method;

- (void)postUrlRequest:(NSString*)url param:(NSDictionary*)param;

- (void)postPhotoWithUrlRequest:(NSString *)url param:(NSDictionary *)param;

- (void) postJSONData:(NSString*)url body:(NSData*)body;

- (void) BCR_postPhotoWithUrlRequest:(NSString *)url param:(NSDictionary *)param image:(UIImage*)image;
@end


@protocol iHttpAdminDelegate<NSObject>
@optional
- (void)didSendMessageSuccess;
- (void)sendMessageFinish;
- (void)sendMessage:(id)sender didFailWithError:(NSString*)error;
- (void)sendMessage:(id)sender didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite;

- (void) didReceiveStringData:(NSString*)resultString;

- (void) didLoadingPerProgress:(id)object progress:(float)progress;


@end

