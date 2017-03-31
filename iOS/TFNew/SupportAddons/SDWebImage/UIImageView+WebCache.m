/*
 * This file is part of the SDWebImage package.
 * (c) Olivier Poitrey <rs@dailymotion.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

#import "UIImageView+WebCache.h"
#import "objc/runtime.h"
#import "UIImage+ImageEffects.h"

static char operationKey;

@implementation UIImageView (WebCache)

- (void)setImageWithURL:(NSURL *)url
{
    [self setImageWithURL:url placeholderImage:nil options:0 progress:nil completed:nil];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder
{
    [self setImageWithURL:url placeholderImage:placeholder options:0 progress:nil completed:nil];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder options:(SDWebImageOptions)options
{
    [self setImageWithURL:url placeholderImage:placeholder options:options progress:nil completed:nil];
}

- (void)setImageWithURL:(NSURL *)url completed:(SDWebImageCompletedBlock)completedBlock
{
    [self setImageWithURL:url placeholderImage:nil options:0 progress:nil completed:completedBlock];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder completed:(SDWebImageCompletedBlock)completedBlock
{
   
    [self setImageWithURL:url placeholderImage:placeholder options:0 progress:nil completed:completedBlock];
}

- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder options:(SDWebImageOptions)options completed:(SDWebImageCompletedBlock)completedBlock
{
    [self setImageWithURL:url placeholderImage:placeholder options:options progress:nil completed:completedBlock];
}


- (void) cacheUrlImage:(NSString *)url image:(UIImage*)image{
	NSString * name = [NSString stringWithFormat:@"%@.jpg", md5Encode(url)];
	
	NSData* imageData = UIImageJPEGRepresentation(image, 1.0);
	NSArray* paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
	NSString* cachesDirectory = [paths objectAtIndex:0];
	NSString* fullPathToFile = [cachesDirectory stringByAppendingPathComponent:name];
	[imageData writeToFile:fullPathToFile atomically:NO];
}

- (UIImage *) cachedUrlImage:(NSString*)url{
	if(url == nil)return nil;
	NSString * name = [NSString stringWithFormat:@"%@.jpg", md5Encode(url)];
	
	NSFileManager *fileManager = [NSFileManager defaultManager];
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
	NSString *cachesDirectory = [paths objectAtIndex:0];
	NSString* fullPathToFile = [cachesDirectory stringByAppendingPathComponent:name];
	
	if ([fileManager fileExistsAtPath:fullPathToFile] == YES){
		
		NSData * data = [NSData dataWithContentsOfFile:fullPathToFile];
		return [UIImage imageWithData:data];
	}
	return nil;
}


- (void)setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder options:(SDWebImageOptions)options progress:(SDWebImageDownloaderProgressBlock)progressBlock completed:(SDWebImageCompletedBlock)completedBlock;
{
    [self cancelCurrentImageLoad];

    self.image = placeholder;
    
    if (url)
    {
        if(![self viewWithTag:2012]){
            UIActivityIndicatorView *indicator  = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake((self.frame.size.width-25)/2, (self.frame.size.height-25)/2+25, 25, 25)];
            indicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
            indicator.tag = 2012;
            [self addSubview:indicator];
            //[indicator release];
            
            indicator.center = CGPointMake(self.frame.size.width/2, self.frame.size.height/2);
            [indicator startAnimating];
        }
        else
        {
            UIActivityIndicatorView *indicator = (UIActivityIndicatorView*)[self viewWithTag:2012];
            if([indicator respondsToSelector:@selector(startAnimating)]){
                [indicator startAnimating];
                
                indicator.center = CGPointMake(self.frame.size.width/2, self.frame.size.height/2);
            }
        }

        
        __weak UIImageView *wself = self;
        id<SDWebImageOperation> operation = [SDWebImageManager.sharedManager downloadWithURL:url options:options progress:progressBlock completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished)
        {
            
            UIActivityIndicatorView *indicator = (UIActivityIndicatorView*)[self viewWithTag:2012];
            if([indicator respondsToSelector:@selector(stopAnimating)]){
                [indicator stopAnimating];
            }
            
            __strong UIImageView *sself = wself;
            if (!sself) return;
            if (image)
            {
                if(self.tag == 101010)
                {
                   //image = [UIImage imageNamed:@"ttt.jpg"];
                    NSString * url1 = [url absoluteString];
                    UIImage *img = [self cachedUrlImage:url1];
                    if(img)
                    {
                        sself.image = img;
                    }
                    else
                    {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            // 更新界面
                           UIImage* imgt = [image applyBlurWithRadius:6
                                                           tintColor:nil
                                               saturationDeltaFactor:1.8
                                                            maskImage:nil];
                            [sself cacheUrlImage:url1 image:imgt];
                            sself.image = imgt;
                        });
                    }
                    
                    
                }
                else
                {
                    sself.image = image;
                }
                
                [sself setNeedsLayout];
            }
            if (completedBlock && finished)
            {

                completedBlock(image, error, cacheType);
            }
        }];
        objc_setAssociatedObject(self, &operationKey, operation, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
}

- (void)cancelCurrentImageLoad
{
    UIActivityIndicatorView *indicator = (UIActivityIndicatorView*)[self viewWithTag:2012];
    if([indicator respondsToSelector:@selector(stopAnimating)]){
        [indicator stopAnimating];
    }
    
    // Cancel in progress downloader from queue
    id<SDWebImageOperation> operation = objc_getAssociatedObject(self, &operationKey);
    if (operation)
    {
        [operation cancel];
        objc_setAssociatedObject(self, &operationKey, nil, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    }
}


@end
