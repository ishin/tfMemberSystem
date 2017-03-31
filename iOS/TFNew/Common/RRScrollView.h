//
//  RRScrollView.h
//  PageControl
//
//  Created by Error on 12/28/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol RRScrollViewDelegate

@required
- (int) numberOfScrollPages;
- (UIView*) scrollPageViewAtIndex:(int)pageIndex;

@optional
- (void)RRscrollViewDidScroll:(UIScrollView *)scrollView;
- (void)RRscrollViewDidEndDecelerating:(UIScrollView *)scrollView;
- (void)didDoubleTappedScrollView:(UIScrollView*)scrollView tapPoint:(CGPoint)tapPoint;
- (void)didTappedScrollView:(UIScrollView*)scrollView tapPoint:(CGPoint)tapPoint;


- (void) didScrollToPageAtIndex:(int)pageIndex;

@end


@interface RRScrollView : UIView <UIScrollViewDelegate> {
	

	UIScrollView	*scrollView_;
	
	NSMutableArray	*pages;
	
	int				_totalPages;
	int				currentPageIndex;
	
	BOOL			landScape;
	BOOL			isLoading;
	
	CGPoint         contentOffset_old;
}
@property (nonatomic, weak) id delegate_;
@property (nonatomic, assign) int currentPageIndex;
@property (nonatomic, assign) BOOL landScape;

- (void) loadData;
- (void) showLandscape;
- (void) showPortrait;

- (void)setPageIndex:(int)pageIndex animationed:(BOOL)animationed;

- (UIView*)pageViewAtIndex:(int)pageIndex;

- (void) resizeWithNewPage:(int)page;

- (void) enableScroll:(BOOL)enable;

- (void) releaseCurrentPages;

@end
