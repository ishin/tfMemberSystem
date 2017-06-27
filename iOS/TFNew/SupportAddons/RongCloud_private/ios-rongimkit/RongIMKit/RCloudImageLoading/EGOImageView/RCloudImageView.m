//
//  EGOImageView.m
//  EGOImageLoading
//
//  Created by Shaun Harrison on 9/15/09.
//  Copyright (c) 2009-2010 enormego
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//

#import "RCloudImageView.h"
#import "RCloudImageLoader.h"
#import <RongIMLib/RongIMLib.h>

@implementation RCloudImageView
@synthesize imageURL, placeholderImage, delegate;

- (instancetype)initWithPlaceholderImage:(UIImage*)anImage {
	return [self initWithPlaceholderImage:anImage delegate:nil];	
}

- (instancetype)initWithPlaceholderImage:(UIImage*)anImage delegate:(id<RCloudImageViewDelegate>)aDelegate {
	if((self = [super initWithImage:anImage])) {
		self.placeholderImage = anImage;
		self.delegate = aDelegate;
	}
	
	return self;
}
-(void)setPlaceholderImage:(UIImage *)__placeholderImage
{
    if (placeholderImage) {
        [placeholderImage release];
        placeholderImage = nil;
    }
    placeholderImage = [__placeholderImage retain];
    
    self.image = placeholderImage;
    
}
- (void)setImageURL:(NSURL *)aURL {
    self.contentMode = UIViewContentModeScaleAspectFill;
	if(imageURL) {
		[[RCloudImageLoader sharedImageLoader] removeObserver:self forURL:imageURL];
		[imageURL release];
		imageURL = nil;
	}
	
	if(!aURL) {
		self.image = self.placeholderImage;
		imageURL = nil;
		return;
	} else {
		imageURL = [aURL retain];
	}

  if (!aURL.scheme || [aURL.scheme.lowercaseString isEqualToString:@"file"]) {
    NSString *path = aURL.absoluteString;
    if([path length] > 0) {
      path = [RCUtilities getCorrectedFilePath:path];
      UIImage *anImage = [[[UIImage alloc]initWithContentsOfFile:path] autorelease];
      if(anImage) {
        self.image = anImage;
        return;
      }
    } else {
      self.image = self.placeholderImage;
      imageURL = nil;
      return;
    }
  }
  
	[[RCloudImageLoader sharedImageLoader] removeObserver:self];
	UIImage* anImage = [[RCloudImageLoader sharedImageLoader] imageForURL:aURL shouldLoadWithObserver:self];
	
	if(anImage) {
		self.image = anImage;

		// trigger the delegate callback if the image was found in the cache
		if([self.delegate respondsToSelector:@selector(imageViewLoadedImage:)]) {
			[self.delegate imageViewLoadedImage:self];
		}
	} else {
		self.image = self.placeholderImage;
	}
}

#pragma mark -
#pragma mark Image loading

- (void)cancelImageLoad {
	[[RCloudImageLoader sharedImageLoader] cancelLoadForURL:self.imageURL];
	[[RCloudImageLoader sharedImageLoader] removeObserver:self forURL:self.imageURL];
}

- (void)imageLoaderDidLoad:(NSNotification*)notification {
	if(![[notification userInfo][@"imageURL"] isEqual:self.imageURL]) return;

	UIImage* anImage = [notification userInfo][@"image"];
	self.image = anImage;
	[self setNeedsDisplay];
	
	if([self.delegate respondsToSelector:@selector(imageViewLoadedImage:)]) {
		[self.delegate imageViewLoadedImage:self];
	}	
}

- (void)imageLoaderDidFailToLoad:(NSNotification*)notification {
	if(![[notification userInfo][@"imageURL"] isEqual:self.imageURL]) return;
	
	if([self.delegate respondsToSelector:@selector(imageViewFailedToLoadImage:error:)]) {
		[self.delegate imageViewFailedToLoadImage:self error:[notification userInfo][@"error"]];
	}
}

#pragma mark -
- (void)dealloc {
	[[RCloudImageLoader sharedImageLoader] removeObserver:self];
	self.delegate = nil;
	self.imageURL = nil;
	self.placeholderImage = nil;
    [super dealloc];
}

@end
