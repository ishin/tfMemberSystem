//
//  RRScrollView.m
//  PageControl
//
//  Created by jack on 12/28/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "RRScrollView.h"
//#import "NewsContentView.h"


@interface RRScrollView (PrivateMethods)
- (void)loadScrollViewWithPage:(int)page;
- (void)scrollViewDidScroll:(UIScrollView *)sender;
@end

@implementation RRScrollView
@synthesize delegate_;
@synthesize currentPageIndex;
@synthesize landScape;

- (id)initWithFrame:(CGRect)frame {
    
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code.
		
		pages = [[NSMutableArray alloc] init];
		
		
		scrollView_ = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
		[self addSubview:scrollView_];
		
		// a page is the width of the scroll view
		scrollView_.pagingEnabled = YES;
		scrollView_.showsHorizontalScrollIndicator = NO;
		scrollView_.showsVerticalScrollIndicator = NO;
		scrollView_.scrollsToTop = NO;
		scrollView_.delegate = self;
		scrollView_.scrollEnabled = YES;

		self.backgroundColor = [UIColor blackColor];
		
		currentPageIndex = 0;
		
		landScape = NO;
		isLoading = NO;
		
		contentOffset_old = scrollView_.contentOffset;
		
		UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleGesture:)];
		tapGesture.cancelsTouchesInView =  NO;
		tapGesture.numberOfTapsRequired = 2;
		[self addGestureRecognizer:tapGesture];
		//[tapGesture release];
		
		tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapGesture:)];
		tapGesture.cancelsTouchesInView =  NO;
		tapGesture.numberOfTapsRequired = 1;
		[self addGestureRecognizer:tapGesture];
		//[tapGesture release];
		
    }
    return self;
}

-(void)handleGesture:(UIGestureRecognizer*)gestureRecognizer{
	
	if(delegate_ && [delegate_ respondsToSelector:@selector(didDoubleTappedScrollView:tapPoint:)]){
		[delegate_ didDoubleTappedScrollView:scrollView_ tapPoint:[gestureRecognizer locationInView:self]];
	}
}
-(void)handleTapGesture:(UIGestureRecognizer*)gestureRecognizer{
	
	if(delegate_ && [delegate_ respondsToSelector:@selector(didTappedScrollView:tapPoint:)]){
		[delegate_ didTappedScrollView:scrollView_ tapPoint:[gestureRecognizer locationInView:self]];
	}
}

- (void) showLandscape{
	isLoading = YES;
	
	scrollView_.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
	//[self loadData];
	landScape = YES;
	
	
	scrollView_.contentSize = CGSizeMake(scrollView_.frame.size.width * _totalPages+1, scrollView_.frame.size.height);
	

	[self setPageIndex:currentPageIndex animationed:NO];
	
	isLoading = NO;
}
- (void) showPortrait{
	isLoading = YES;
	
	scrollView_.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
	//[self loadData];
	
	landScape = NO;

	scrollView_.contentSize = CGSizeMake(scrollView_.frame.size.width * _totalPages+1, scrollView_.frame.size.height);
	
	
	[self setPageIndex:currentPageIndex animationed:NO];
	
	isLoading = NO;
}

- (void) resizeWithNewPage:(int)page{
	
	if(page == _totalPages)return;
	
	if(page < _totalPages)
	{
		for(int i = _totalPages-1; i > page-1; i--){
			
			UIView *pageView = [pages objectAtIndex:i];
			if ((NSNull *)pageView != [NSNull null])
			{
				[pageView removeFromSuperview];
			}
			
			[pages removeObjectAtIndex:i];
		}
		
		_totalPages = page;
		currentPageIndex = 0;
		

		scrollView_.contentSize = CGSizeMake(scrollView_.frame.size.width * _totalPages+1, scrollView_.frame.size.height);
	
		
		[self setPageIndex:currentPageIndex animationed:NO];
	}
	else 
	{
		for(int i = _totalPages; i < page; i++){
			
			[pages addObject:[NSNull null]];
		}
		
		_totalPages = page;
		
		
		scrollView_.contentSize = CGSizeMake(scrollView_.frame.size.width * _totalPages+1, scrollView_.frame.size.height);
		
	}

}


- (void) loadData{
	
    scrollView_.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    
	for(UIView *v in [scrollView_ subviews]){
		[v removeFromSuperview];
	}
	
	if(delegate_ && [delegate_ respondsToSelector:@selector(numberOfScrollPages)]){
		_totalPages = [delegate_ numberOfScrollPages];
		
		[pages removeAllObjects];
		
        
		for (unsigned i = 0; i < _totalPages; i++)
		{
			[pages addObject:[NSNull null]];
		}
		
		
        
		scrollView_.contentSize = CGSizeMake(scrollView_.frame.size.width * _totalPages+1, scrollView_.frame.size.height);
		
		
        if(_totalPages == 0)
            return;

		// pages are created on demand
		// load the visible page
		// load the page on either side to avoid flashes when the user starts scrolling
		//
		//[self loadScrollViewWithPage:currentPageIndex-1];
//		[self loadScrollViewWithPage:currentPageIndex];
//		[self loadScrollViewWithPage:currentPageIndex+1];
		
		[self setPageIndex:currentPageIndex animationed:NO];
	}

	
}

- (UIView*)pageViewAtIndex:(int)pageIndex{
	
	if(pageIndex < 0)return nil;
	if(pageIndex >= _totalPages)return nil;
	UIView *pageView = [pages objectAtIndex:pageIndex];
    if ((NSNull *)pageView == [NSNull null])
    {
        return nil;
    }
	
	return pageView;
	
}
- (void)releaseScrollViewWithPage:(int)page{
	
		//NSLog(@"pages count %d of %d",[pages count],page);
	if (page < 0)
        return;
    if (page >= _totalPages)
        return;
	
	UIView *pageView = [pages objectAtIndex:page];
	
	

    if ((NSNull *)pageView == [NSNull null])
    {
        
    }
	else 
	{
		
		if (pageView.superview != nil){
			[pageView removeFromSuperview];
		}
		
		if(page < [pages count])
			[pages replaceObjectAtIndex:page withObject:[NSNull null]];
		
		//[pageView release];
		
		//NSLog(@"release %d ", page);
		
		
	}
}

- (void)loadScrollViewWithPage:(int)page
{
    if (page < 0)
        return;
    if (page >= _totalPages)
        return;
	
	//NSLog(@"load %d", page);
    
    // replace the placeholder if necessary
    UIView *pageView = [pages objectAtIndex:page];
    if ((NSNull *)pageView == [NSNull null])
    {
		pageView = [delegate_ scrollPageViewAtIndex:page];
		
		if(page < [pages count] && pageView)
			[pages replaceObjectAtIndex:page withObject:pageView];
    }
    
	CGRect frame = scrollView_.frame;
	frame.origin.x = frame.size.width * page;
	frame.origin.y = 0;
	pageView.frame = frame;
	
    // add the controller's view to the scroll view
    if (pageView.superview == nil)
    {
        [scrollView_ addSubview:pageView];
		
    }

}

- (void)scrollViewDidScroll:(UIScrollView *)sender
{
	
	if(delegate_ && [delegate_ respondsToSelector:@selector(RRscrollViewDidScroll:)]){
		[delegate_ RRscrollViewDidScroll:scrollView_];
	}
	if(sender != scrollView_)return;
	
    // Switch the indicator when more than 50% of the previous/next page is visible
    CGFloat pageWidth = scrollView_.frame.size.width;
	//NSLog(@"pageHeight = %f", pageHeight);
    int page = floor((scrollView_.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
	
	
	if(page == currentPageIndex){
		
		UIView *pageView = [pages objectAtIndex:page];
		if ((NSNull *)pageView != [NSNull null])
		{
			UIView *l = [pageView viewWithTag:1010];
		
			if(contentOffset_old.x < sender.contentOffset.x){
				l.frame = CGRectMake(l.frame.origin.x-10, l.frame.origin.y, l.frame.size.width, l.frame.size.height);
			}
			else {
				l.frame = CGRectMake(l.frame.origin.x+10, l.frame.origin.y, l.frame.size.width, l.frame.size.height);
				
			}
		}
	}
	
	//NSLog(@"page = %d", page);
    
	contentOffset_old = sender.contentOffset;
	
	if(page < 0)page = 0;
	if(page >= _totalPages)page = _totalPages-1;
	
	
	if(currentPageIndex == page){
		return;
    }
	
	
	currentPageIndex = page;
	
	
    // load the visible page and the page on either side of it (to avoid flashes when the user starts scrolling)
	
	[self releaseScrollViewWithPage:page-2];
//	
//	[self loadScrollViewWithPage:page - 3];
	//[self loadScrollViewWithPage:page - 2];
    [self loadScrollViewWithPage:page - 1];
    [self loadScrollViewWithPage:page];
    [self loadScrollViewWithPage:page + 1];
	//[self loadScrollViewWithPage:page + 2];
//	[self loadScrollViewWithPage:page + 3];
//	
	[self releaseScrollViewWithPage:page+2];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
	
	contentOffset_old = scrollView.contentOffset;
	
	UIView *pageView = [pages objectAtIndex:currentPageIndex];
	if ((NSNull *)pageView != [NSNull null])
	{
		UIView *l = [pageView viewWithTag:1010];
		
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationDuration:0.25];
		
		l.frame = CGRectMake(0, l.frame.origin.y, l.frame.size.width, l.frame.size.height);
		
		
		[UIView commitAnimations];
	}
	
	
		if(delegate_ && [delegate_ respondsToSelector:@selector(didScrollToPageAtIndex:)]){
			[delegate_ didScrollToPageAtIndex:currentPageIndex];
		}
    

}

- (void)setPageIndex:(int)pageIndex animationed:(BOOL)animationed
{
    int page = pageIndex;
	
	if(abs(currentPageIndex-page)>1)animationed=NO;
	
    // load the visible page and the page on either side of it (to avoid flashes when the user starts scrolling)
	
	[self releaseScrollViewWithPage:page-2];
//	
//	[self loadScrollViewWithPage:page - 3];
	//[self loadScrollViewWithPage:page - 2];
    [self loadScrollViewWithPage:page - 1];
    [self loadScrollViewWithPage:page];
    [self loadScrollViewWithPage:page + 1];
	//[self loadScrollViewWithPage:page + 2];
//	[self loadScrollViewWithPage:page + 3];
//	
	[self releaseScrollViewWithPage:page+2];
	
    //NSLog(@"page *** %d",page+2);
	// update the scroll view to the appropriate page
	
	//NSLog(@"setPageIndex = %d", page);
	currentPageIndex = page;
	
    CGRect frame = scrollView_.frame;
    frame.origin.x = frame.size.width * page;
    frame.origin.y = 0;
    [scrollView_ scrollRectToVisible:frame animated:animationed];
	
	if(delegate_ && [delegate_ respondsToSelector:@selector(didScrollToPageAtIndex:)]){
		[delegate_ didScrollToPageAtIndex:currentPageIndex];
	}
}



- (void) releaseCurrentPages{
	
	[self releaseScrollViewWithPage:currentPageIndex-1];
	[self releaseScrollViewWithPage:currentPageIndex];
	[self releaseScrollViewWithPage:currentPageIndex+1];
	
}




/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code.
}
*/

- (void) enableScroll:(BOOL)enable{
	scrollView_.scrollEnabled = enable;
}

- (void)dealloc {
	
	for(UIView *v in [scrollView_ subviews]){
		[v removeFromSuperview];
	}
	
}


@end
