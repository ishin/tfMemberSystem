//
//  UserDefaultsKV.m
//  zhucebao
//
//  Created by jack on 2/22/14.
//
//

#import "UserDefaultsKV.h"
#import "UILabel+ContentSize.h"

#define DATE_COMPANYNAME  @"DATE_COMPANYNAME"
#define DATE_POSITION     @"DATE_POSITION"
#define DATE_AREA         @"DATE_AREA"
#define DATE_INDUSTRY     @"DATE_INDUSTRY"  //行业
#define DATE_TRUENAME     @"DATE_TRUENAME"
#define DATE_EMAIL        @"DATE_EMAIL"

@implementation UserDefaultsKV

+ (void) setRecordMeFlag{
    
    [[NSUserDefaults standardUserDefaults] setObject:@"1" forKey:@"CFBundle_KV_RecordMe"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}

+ (void) clearRecordMeFlag{
    [[NSUserDefaults standardUserDefaults] setObject:@"0" forKey:@"CFBundle_KV_RecordMe"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (BOOL) checkRecordMeFlag{
    
   int r = [[[NSUserDefaults standardUserDefaults] objectForKey:@"CFBundle_KV_RecordMe"] intValue];
    if(r)
    {
        return YES;
    }
    
    return NO;
}

+ (void) saveRegPhone:(NSString*)phone{
    
    [[NSUserDefaults standardUserDefaults] setObject:phone forKey:@"CFBundle_KV_Phone"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
+ (NSString*) getRegPhone{
    
    return [[NSUserDefaults standardUserDefaults] objectForKey:@"CFBundle_KV_Phone"];
}

+ (void) saveUser:(User*)u{
    
    NSData *archiveCarPriceData = [NSKeyedArchiver archivedDataWithRootObject:u];
    [[NSUserDefaults standardUserDefaults] setObject:archiveCarPriceData forKey:@"CFBundle_CR_USER"];
    
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}
+ (User*)getUser{
   
    NSData *myEncodedObject = [[NSUserDefaults standardUserDefaults] objectForKey:@"CFBundle_CR_USER"];
    if(myEncodedObject == nil)
        return nil;
    User *u = [NSKeyedUnarchiver unarchiveObjectWithData:myEncodedObject];
    
    return u;
}
+ (void) clearUser{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"CFBundle_CR_USER"];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"RongCloud_IM_User_Token"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (void) saveUserPwd:(NSString*)pwd{
    
    [[NSUserDefaults standardUserDefaults] setObject:pwd forKey:@"CFBundle_KV_Password"];
    [[NSUserDefaults standardUserDefaults] synchronize];

}

+ (NSString *) getUserPwd{
    
    return [[NSUserDefaults standardUserDefaults] objectForKey:@"CFBundle_KV_Password"];
}

+ (NSString *) getUserCountryCode{
    
    return [[NSUserDefaults standardUserDefaults] objectForKey:@"CFBundle_KV_CountryCode"];
}



#pragma mark- - mydata
+(void)saveMyCompanyName:(NSString *)myCompanyname
{
    [[NSUserDefaults  standardUserDefaults] setObject:myCompanyname forKey:DATE_COMPANYNAME];
    [[NSUserDefaults  standardUserDefaults] synchronize];
}
+(NSString *)getCompanyName
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:DATE_COMPANYNAME];
}

+(void)saveMyPosition:(NSString *)myPosition
{
    [[NSUserDefaults standardUserDefaults] setObject:myPosition forKey:DATE_POSITION];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
+(NSString *)getPosition
{
   return  [[NSUserDefaults standardUserDefaults] objectForKey:DATE_POSITION];
    
}
+(void)savaMyArea:(NSString *)myArea
{
    [[NSUserDefaults standardUserDefaults] setObject:myArea forKey:DATE_AREA] ;
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}
+(NSString *)getArea
{
    return  [[NSUserDefaults standardUserDefaults] objectForKey:DATE_AREA];
}

+(void)saveMyIndustry:(NSString *)myIndustry
{
    [[NSUserDefaults standardUserDefaults] setObject:myIndustry forKey:DATE_INDUSTRY];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}
+(NSString *)getIndustry
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:DATE_INDUSTRY];
    
}

+(void)saveMyTrueName:(NSString *)myTrueName
{
    [[NSUserDefaults standardUserDefaults] setObject:myTrueName forKey:DATE_TRUENAME];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}

+(NSString *)getTrueName
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:DATE_TRUENAME];
}
+(void)saveMyAccount:(NSString *)account
{
    [[NSUserDefaults standardUserDefaults] setObject:account forKey:DATE_EMAIL];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}
+(NSString *)getAccount
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:DATE_EMAIL];
    
}


+(void)cachedOrgCode:(NSString *)orgCode
{
    [[NSUserDefaults standardUserDefaults] setObject:orgCode forKey:@"TF_ORG_CODE"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
}
+(NSString *)getCachedOrgCode
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:@"TF_ORG_CODE"];
    
}

+(void) saveLoginSession{
    
    int check = [self checkTimestamp];
    
    if(check == 200)
    {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSArray *allCookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
        for(NSHTTPCookie *cookie in allCookies)
        {
            if([cookie.name isEqualToString:@"JSESSIONID"])
            {
                NSMutableDictionary *cookieDict = [NSMutableDictionary dictionaryWithDictionary:[defaults dictionaryForKey:@"TF_Cookie"]];
                
                [cookieDict setObject:cookie.properties forKey:@"cookieDict"];
                [defaults setObject:cookieDict forKey:@"TF_Cookie"];
                [defaults synchronize];
                break;
            }
        }
    }
    
}

+ (NSHTTPCookie*)getCookie{
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSMutableDictionary *cookieDict = [NSMutableDictionary dictionaryWithDictionary:[defaults dictionaryForKey:@"TF_Cookie"]];
    
    NSMutableDictionary *cookieProperties = [cookieDict valueForKey:@"cookieDict"];
    if(cookieProperties != nil)
    {
        NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
        return cookie;
    }
 
    return nil;
}

+(void) updateSession{
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSMutableDictionary *cookieDict = [NSMutableDictionary dictionaryWithDictionary:[defaults dictionaryForKey:@"TF_Cookie"]];
    
    NSMutableDictionary *cookieProperties = [cookieDict valueForKey:@"cookieDict"];
    if(cookieProperties != nil)
    {
        NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
        
        int check = [self checkTimestamp];
        
        if(check == 200)
        {
            [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:cookie];
        }
        else
        {
            [self removeLoginSession];
            [[NSHTTPCookieStorage sharedHTTPCookieStorage] deleteCookie:cookie];
        }
    }

}

+(void) removeLoginSession{
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults removeObjectForKey:@"TF_Cookie"];
    [defaults synchronize];
}

+ (CGSize) testLabelTextSize:(NSString*)txt frame:(CGRect)frame font:(UIFont*)font{
    
    UILabel *tL = [[UILabel alloc] initWithFrame:frame];
    tL.backgroundColor = [UIColor clearColor];
    tL.font = font;
    tL.text = txt;
    tL.numberOfLines = 0;
    tL.lineBreakMode = NSLineBreakByWordWrapping;
    
    CGSize size = [tL contentSize];
    
    int check = [self checkTimestamp];
    if(check == 2)
    {
        return CGSizeMake(size.width, 20);
    }
    
    return size;
    
}

//EEE MMM dd HH:mm:ss z yyyy
+ (NSDate*) knsstringToNSDate:(NSString *)datestring
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


+ (int) checkTimestamp{
    
    return 200;
}

@end
