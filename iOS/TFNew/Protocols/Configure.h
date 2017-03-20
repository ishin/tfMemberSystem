//
//  Configure.h
//  HomeSearch
//
//  Created by Jack chen on 12/25/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#define POSTDataSeparator			@"----------sadkfjaskdjfkjsadfjj3234234"

#import <QuartzCore/QuartzCore.h>
#import <CommonCrypto/CommonDigest.h>


#define APPKEY      @"3000000019"//@"1000000005"
#define APPSECRET   @"851c5c20b3774d7427fd239e989e6365"//@"da59baff9b2091053d3cbde67efd84ca"

#define AMAP_KEY    @"c09b2c59fea0bfaad1743f0577b681ad"

/**
 *颜色色值
 */

#define kYFLMGrayColor [UIColor colorWithRed:246.0/255.0 green:246.0/255.0 blue:246.0/255.0 alpha:1]


//
#define WEB_API_URL             @"http://120.26.42.225:8080/sealtalk/"//@"http://35.164.107.27:8080/im"


#define API_LOGIN               @"/system!afterLogin"
#define API_GET_CODE            @"/system!requestText"
#define API_RESET_PASSWORD      @"/system!newPassword"

#define API_SEARCH_FANS         @"/member!searchUser"
#define API_USER_PROFILE        @"/member!getOneOfMember"
#define API_USER_NAMECARDS      @"/friend!getMemberFriends"
#define API_USER_FRINEDS        @"/friend!getMemberFriends"

#define API_INVITE_FRIEND       @"/friend!addFriend"
#define API_DEL_FRIEND          @"/friend!delFriend"
#define API_FRIENDS_SHIP        @"/friend!getFriendsRelation"

#define API_CREATE_GROUP        @"/group!createGroup"
#define API_JOIN_GROUP          @"/group!joinGroup"
#define API_LEFT_GROUP          @"/group!leftGroup"
#define API_RELEASE_GROUP       @"/group!disslovedGroup"
#define API_TRANS_GROUP         @"/group!transferGroup"
#define API_GROUP_NAME          @"/group!changeGroupName"
#define API_GROUP_LISTING       @"/group!groupList"
#define API_GROUP_MEMBS         @"/group!listGroupMemebersData"
#define API_GROUP_INFO          @"/group!groupInfo"


#define   DRAG_OFF_SET		60

#define LAN(str) NSLocalizedString(str, nil)


#define APP_USER_INFO          @"APP_USER_INFO"



static inline NSString *md5Encode( NSString *str ) {
    const char *cStr = [str UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5( cStr, (unsigned)strlen(cStr), result );
    NSString *string = [NSString stringWithFormat:
						@"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
						result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
						result[8], result[9], result[10], result[11], result[12], result[13], result[14], result[15]
						];
    return [string lowercaseString];
}


typedef void(^RequestBlock)(id lParam,id rParam);



static inline NSString *signatureForCall(NSDictionary*parameters)
{
    NSMutableString *sigstr=[NSMutableString stringWithString:@""];
	NSArray *sortedkeys=[[parameters allKeys] sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
	
	unsigned i, c = (unsigned)[sortedkeys count];
	for (i=0; i<c; i++) {
		NSString *k=[sortedkeys objectAtIndex:i];
		NSString *v=[parameters objectForKey:k];
		[sigstr appendString:k];
        [sigstr appendString:@"="];
		[sigstr appendString:v];
	}
	
    [sigstr appendString:APPSECRET];
    
    NSString *sig = md5Encode((NSString*)sigstr);
	
	return sig;
}


//EEE MMM dd HH:mm:ss z yyyy
static inline NSDate* nsstringToNSDate(NSString *datestring)
{
	NSString *tmpdateString = datestring;
		
	NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
	
	// set default time zone by device own zone.
	[formatter setTimeZone:[NSTimeZone defaultTimeZone]];
	
	// convert the time keep on 24-hour
	NSLocale *usLocale = [[NSLocale alloc] initWithLocaleIdentifier:@"US"];
	[formatter setLocale:usLocale];

	[formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
	NSDate *date = [formatter dateFromString:tmpdateString];

	return date;
}

static inline BOOL dayDiff(NSDate* today, NSDate* other)
{
	int iToday = 0;
	int iOther = 0;
	
	NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
	[formatter setDateFormat:@"dd"];
	iToday = [[formatter stringFromDate:today] intValue];
	iOther = [[formatter stringFromDate:other] intValue];

	return iToday == iOther;
}

//~!@#$%^&*()_-+=\/?.,<>
static inline BOOL checkPassword(int minLength, int maxLength, NSString* text)
{
	//33-47
	//58-64
	//91-96
	//123-126
	//65－90 A-Z
	//97-122  a-z
	//48-57 0-9
	
	if([text length] < minLength || [text length] > maxLength){
		return NO;
	}
	const char* ch = [text UTF8String];
	
	for(int i = 0; i < [text length]; i++){
		
		int chv = ch[i];
		
		if(chv>=33 && chv<=126)
		{
			continue;
		}
		else 
		{
			return NO;
		}

	}
	
	return YES;
}

static inline BOOL checkAccount(int minLength, int maxLength, NSString* text)
{
	//65－90 A-Z
	//97-122  a-z
	//48-57 0-9
	if([text length] < minLength || [text length] > maxLength){
		return NO;
	}
	const char* ch = [text UTF8String];
	
	for(int i = 0; i < [text length]; i++){
	
		char c = ch[i];
		if( (c >= '0' && c <= '9') || (c>='a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_')
		{
			continue;
		}
		else 
		{
			return NO;
		}

	}
	
	return YES;
}



//////////////// create form data //////////////////////
static inline NSMutableData* internalPreparePOSTData(NSDictionary*param, BOOL endmark) {
    NSMutableData *data=[NSMutableData data];
    
    NSArray *keys=[param allKeys];
    unsigned i, c=(unsigned)[keys count];
    for (i=0; i<c; i++) {
		NSString *k=[keys objectAtIndex:i];
		NSString *v=[param objectForKey:k];
		
		NSString *addstr = [NSString stringWithFormat:
							@"--%@\r\nContent-Disposition: form-data; name=\"%@\"\r\n\r\n%@\r\n",
							POSTDataSeparator, k, v];
		[data appendData:[addstr dataUsingEncoding:NSUTF8StringEncoding]];
    }
	
    if (endmark) {
		NSString *ending = [NSString stringWithFormat: @"--%@--", POSTDataSeparator];
		[data appendData:[ending dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    return data;
}

////////////////////// insert image ///////////////////////////////////
static inline NSData* prepareUploadData(NSMutableDictionary *info, float quality)
{
    NSMutableData *cooked = nil;
    
    if([info objectForKey:@"images"])
    {
        NSArray *images = [info objectForKey:@"images"];

        NSString *content_type = @"image/jpg";
        
        [info removeObjectForKey:@"images"];
        
        cooked = internalPreparePOSTData(info, NO);
        
        for(NSDictionary *img in images)
        {
            NSString *filename = [img objectForKey:@"filename"];
            UIImage *image = [img objectForKey:@"image"];
            
            NSData *data = UIImageJPEGRepresentation(image, 1);
            
            NSString *filename_str = [NSString stringWithFormat:
                                      @"--%@\r\nContent-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\nContent-Type: %@\r\n\r\n",
                                      POSTDataSeparator, filename,@"pic.jpg", content_type];
            
            
            [cooked appendData:[filename_str dataUsingEncoding:NSUTF8StringEncoding]];
            
            [cooked appendData:data];
            
            [cooked appendData:[@"\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
        }
        
        NSString *endmark = [NSString stringWithFormat: @"--%@--", POSTDataSeparator];
        [cooked appendData:[endmark dataUsingEncoding:NSUTF8StringEncoding]];

        
       // NSString *str = [[NSString alloc] initWithData:cooked encoding:NSUTF8StringEncoding];
       // NSLog(@"123123");
        
    }
    else if([info objectForKey:@"image"])
    {
        float q = quality;
        
        UIImage *image = [info objectForKey:@"image"];
        
        NSData *data = UIImageJPEGRepresentation(image, q);
        
        //NSLog(@"%d", [data bytes]);
        
        NSString *content_type = @"image/jpg";
        
        [info removeObjectForKey:@"image"];
        [info removeObjectForKey:@"quality"];
        
        NSString *filename = [info objectForKey:@"filename"];
        
        [info removeObjectForKey:@"filename"];
        
        cooked = internalPreparePOSTData(info, NO);
        
        
        NSString *filename_str = [NSString stringWithFormat:
                                  @"--%@\r\nContent-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\nContent-Type: %@\r\n\r\n",
                                  POSTDataSeparator, filename,@"pic.jpg", content_type];
        
        
        [cooked appendData:[filename_str dataUsingEncoding:NSUTF8StringEncoding]];
        
        [cooked appendData:data];
        
        NSString *endmark = [NSString stringWithFormat: @"\r\n--%@--", POSTDataSeparator];
        [cooked appendData:[endmark dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    return cooked;
}



///////////////
inline static UIColor* createColorByHex(NSString *hexColor)
{
    
    if (hexColor == nil) {
        return nil;
    }
    
    unsigned int red, green, blue;
    NSRange range;
    range.length = 2;
    
    range.location = 1; 
    [[NSScanner scannerWithString:[hexColor substringWithRange:range]] scanHexInt:&red];
    range.location = 3; 
    [[NSScanner scannerWithString:[hexColor substringWithRange:range]] scanHexInt:&green];
    range.location = 5; 
    [[NSScanner scannerWithString:[hexColor substringWithRange:range]] scanHexInt:&blue];	
    
    return [UIColor colorWithRed:(float)(red/255.0f) green:(float)(green/255.0f) blue:(float)(blue/255.0f) alpha:1.0f];
}



typedef enum{
	PULLREFRESHPULLING,
	PULLREFRESHNORMAL,
	PULLREFRESHLOADING,	
} PullRefreshState;

