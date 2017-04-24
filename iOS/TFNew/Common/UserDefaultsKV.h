//
//  UserDefaultsKV.h
//  zhucebao
//
//  Created by jack on 2/22/14.
//
//

#import <Foundation/Foundation.h>
#import "User.h"

@interface UserDefaultsKV : NSObject
+ (void) setRecordMeFlag;
+ (void) clearRecordMeFlag;
+ (BOOL) checkRecordMeFlag;

+ (void) saveRegPhone:(NSString*)phone;
+ (NSString*) getRegPhone;

+ (void) saveUser:(User*)u;
+ (User*)getUser;
+ (void) clearUser;

+ (void) saveUserPwd:(NSString*)pwd;
+ (NSString *) getUserPwd;

+ (int) networkCheckStatus;


#pragma mark- - mydata
+(void)saveMyCompanyName:(NSString *)myCompanyname;
+(NSString *)getCompanyName;
+(void)saveMyPosition:(NSString *)myPosition;
+(NSString *)getPosition;
+(void)savaMyArea:(NSString *)myArea;
+(NSString *)getArea;
+(void)saveMyIndustry:(NSString *)myIndustry;
+(NSString *)getIndustry;
+(void)saveMyTrueName:(NSString *)myTrueName;
+(NSString *)getTrueName;
+(void)saveMyAccount:(NSString *)account;
+(NSString *)getAccount;

+(void)cachedOrgCode:(NSString *)orgCode;
+(NSString *)getCachedOrgCode;

+ (NSString *) getUserCountryCode;

+(void) saveLoginSession;
+(void) updateSession;
+(void) removeLoginSession;
+ (NSHTTPCookie*)getCookie;

+ (CGSize) testLabelTextSize:(NSString*)txt frame:(CGRect)frame font:(UIFont*)font;

@end
