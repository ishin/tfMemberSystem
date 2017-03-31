//
//  wsDB.h
//  Gemini
//
//  Created by jack on 1/9/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>




@interface wsDB : NSObject {

	sqlite3  *database_;
	
}
@property (strong, nonatomic) NSString *databasePath_;
@property (strong, nonatomic) NSString *_currentUserFolder;


+ (wsDB*)sharedDBInstance;


-(int)open;
-(void)close;


- (NSArray *)queryAllProvince;
- (NSArray *)queryCityByProvince:(int)pid;
- (NSArray *)queryAreaByCity:(int)pid;

- (NSDictionary *)searchProvince:(NSString*)provinceKey;
- (NSDictionary *)searchCity:(NSString*)cityKey andProvince:(int)pid;

- (NSString *)provinceById:(int)oid;
- (NSString *)cityById:(int)oid;
- (NSString *)areaById:(int)oid;


- (int) insertFav:(int)objid withType:(int)type;
- (int)favByIdAndType:(int)oid type:(int)type;
- (void)removeFavByIdAndType:(int)oid type:(int)type;


- (int) insertOrg:(NSDictionary*)org;
- (BOOL) isOrgLocalCahced:(id)orgid;
- (void) updateOrg:(NSDictionary*)org;
- (NSArray *)searchCompanyByKeyword:(NSString*)keyword;

@end
