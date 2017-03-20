//
//  UserDefaultsKV.m
//  zhucebao
//
//  Created by jack on 2/22/14.
//
//

#import "UserDefaultsKV.h"
#import "NetworkChecker.h"
#import "UILabel+ContentSize.h"

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

+ (int) networkCheckStatus{
    
    NetworkStatus status = [[NetworkChecker sharedNetworkChecker] networkStatus];
    if(status == NotReachable)
        return 0;
    
    return 2;
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
+ (CGSize) testLabelTextSize:(NSString*)txt frame:(CGRect)frame font:(UIFont*)font{
    
    UILabel *tL = [[UILabel alloc] initWithFrame:frame];
    tL.backgroundColor = [UIColor clearColor];
    tL.font = font;
    tL.text = txt;
    tL.numberOfLines = 0;
    tL.lineBreakMode = NSLineBreakByWordWrapping;
    
    CGSize size = [tL contentSize];
    
    return size;
    
}

@end
