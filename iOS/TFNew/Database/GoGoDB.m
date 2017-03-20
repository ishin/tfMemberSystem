//
//  GoGoDB.m
//  Gemini
//
//  Created by jack on 1/9/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "GoGoDB.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"
#import "WSUser.h"


@implementation GoGoDB
@synthesize databasePath_;
@synthesize _currentUserFolder;

static GoGoDB* gogoDBInstance = nil;

+ (GoGoDB*)sharedDBInstance{
    
    User *mp = [UserDefaultsKV getUser];
    if(mp == nil)
        return nil;
    
    if(gogoDBInstance == nil){
        gogoDBInstance = [[GoGoDB alloc] init];
        [gogoDBInstance open];
    }
    else
    {
        NSString *account = [UserDefaultsKV getAccount];
        if([account length] == 0)
        {
            account = mp._userId;
        }
        
        if(![account isEqualToString:gogoDBInstance._currentUserFolder])
        {
            gogoDBInstance = nil;
            gogoDBInstance = [[GoGoDB alloc] init];
            [gogoDBInstance open];
        }
        
    }
    return gogoDBInstance;
}

+ (void)logoutDB{
    gogoDBInstance = nil;
}

- (id) init{
    
    self = [super init];
    
    User *mp = [UserDefaultsKV getUser];
    if(mp == nil)
    {
        return nil;
    }
    
    NSString *account = [UserDefaultsKV getAccount];
    if([account length] == 0)
    {
        account = mp._userId;
    }
    
    self._currentUserFolder = account;
    
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:_currentUserFolder];
    
    NSFileManager *fm = [NSFileManager defaultManager];
    if(![fm fileExistsAtPath:dbPath])
    {
        [fm createDirectoryAtPath:dbPath withIntermediateDirectories:NO attributes:nil error:nil];
    }
    
    dbPath = [dbPath stringByAppendingPathComponent:@"GoGoDB.sqlite"];
    
    if(![fm fileExistsAtPath:dbPath])
    {
        NSString *sPath = [[NSBundle mainBundle] pathForResource:@"GoGoDB.sqlite" ofType:nil];
        
        [fm copyItemAtPath:sPath toPath:dbPath error:nil];
    }
    
    return self;
}


-(int) open
{
    if (database_) {
        return 1;
    }
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:_currentUserFolder];
    dbPath = [dbPath stringByAppendingPathComponent:@"GoGoDB.sqlite"];
    
    self.databasePath_ = dbPath;
    if(sqlite3_open([dbPath UTF8String], &database_)== SQLITE_OK)
    {
        //初始化数据库
        
        [self checkAndCreateUserTable];
        
        [self checkAndCreateChatTable];
        
        
        [self checkAndCreateFriendsTable];
        
        [self checkAndCreateGroupTable];
        
        [self checkAndCreateOrgTable];
        
        return 1;
    }
    else
    {
        sqlite3_close(database_);
        NSLog(@"Open DataBase Failed");
        return -1;
    }
    
    
}

-(void) close
{
    if (database_) {
        sqlite3_close(database_);
    }
    
}

- (void)dealloc{
    
}

#pragma mark ---创建好友表
- (void) checkAndCreateFriendsTable{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tf_tblMyFriends'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tf_tblMyFriends");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        s = @"CREATE TABLE tf_tblMyFriends(uid INTEGER, name TEXT, cellphone TEXT, pinyin TEXT, data BLOB)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared DataBase! -- tf_tblMyFriends");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}

- (void) deleteFriends{
    
    @synchronized(self)
    {
        
        NSString *s = @"delete from tf_tblMyFriends";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not delete from tf_tblMyFriends!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}

- (NSArray *)searchFriendsWithKeyword:(NSString *)keyword{
    
    NSString *sql = [NSString stringWithFormat:@"select data from tf_tblMyFriends where name like '%%%@%%' or cellphone like '%@%%' or pinyin like'%%%@%%'", keyword, keyword, [keyword lowercaseString]];
    
    const char *sqlStatement = [sql UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tf_tblMyFriends");
        return nil;
    }
    
    
    NSMutableArray *results = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* achievement_id       = sqlite3_column_blob(statement, 0);
        int achievement_idSize           = sqlite3_column_bytes(statement, 0);
        
        if(achievement_id)
        {
            NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
            NSDictionary * dic = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            if([dic count])
            {
                WSUser *uu = [[WSUser alloc] initWithDictionary:dic];
                uu.familiy = 1;
                [results addObject:uu];
            }
            
            
        }
    }
    sqlite3_finalize(statement);
    
    return results;
}

//获取拼音首字母(传入汉字字符串, 返回大写拼音首字母)
- (NSString *)convPinyinFromCharactor:(NSString *)aString
{
    //转成了可变字符串
    NSMutableString *str = [NSMutableString stringWithString:aString];
    //先转换为带声调的拼音
    CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformMandarinLatin,NO);
    //再转换为不带声调的拼音
    CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformStripDiacritics,NO);
    //转化为大写拼音
    NSString *pinYin = [str lowercaseString];
    //获取并返回首字母
    return pinYin;
}


- (int) insertAFriend:(NSDictionary*)user{
    
    @synchronized(self)
    {
        
        int uid = [[user objectForKey:@"id"] intValue];
        
        if([self isFriendLocalCahced:[NSString stringWithFormat:@"%d", uid]])
        {
            [self updateAFriend:user];
            return 0;
        }
        
        const char *sqlStatement = "insert into tf_tblMyFriends (uid, name, cellphone, pinyin, data) VALUES (?, ?, ?, ?, ?)";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tf_tblMyFriends");
            return -1;
        }
        
        //int uid = [[user objectForKey:@"id"] intValue];
        sqlite3_bind_int(statement, 1, uid);
        
        NSString* name = [user objectForKey:@"fullname"];
        if(name == nil)
            name = @"";
        sqlite3_bind_text(statement, 2, [name UTF8String], -1, SQLITE_TRANSIENT);
        
        NSString* value = [user objectForKey:@"cellphone"];
        if(value == nil)
            value = @"";
        sqlite3_bind_text(statement, 3, [value UTF8String], -1, SQLITE_TRANSIENT);
        
        
        
        value = [self convPinyinFromCharactor:name];
        if(value == nil)
            value = @"";
        sqlite3_bind_text(statement, 4, [value UTF8String], -1, SQLITE_TRANSIENT);
        
        
        NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:user];
        sqlite3_bind_blob(statement, 5, [archiveData bytes], (int)[archiveData length], NULL);
        
        
        success = sqlite3_step(statement);
        sqlite3_finalize(statement);
        
        if (success == SQLITE_ERROR) {
            NSLog(@"Error: failed to insert into tf_tblMyFriends with message.");
            return -1;
        }
        
        int lastRow = (int)sqlite3_last_insert_rowid(database_);
        
        return lastRow;
        
    }
    
}

- (BOOL) isFriendLocalCahced:(NSString*)uid{
    
    @synchronized(self)
    {
        
        const char *sqlStatement = "select * from tf_tblMyFriends where uid = ?";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tf_tblMyFriends");
            return NO;
        }
        
        sqlite3_bind_int(statement, 1, [uid intValue]);
        
        
        BOOL result = NO;
        
        while (sqlite3_step(statement) == SQLITE_ROW) {
            
            result = YES;
            
            break;
        }
        sqlite3_finalize(statement);
        
        return result;
    }
}

- (void) deleteFriendByUid:(NSString*)uid{
    
    @synchronized(self)
    {
        const char *sqlStatement = "delete from tf_tblMyFriends where uid = ?";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tf_tblMyFriends");
            return;
        }
        
        sqlite3_bind_int(statement, 1, [uid intValue]);
        
        sqlite3_step(statement);
        sqlite3_finalize(statement);
    }
    
}

- (void) updateAFriend:(NSDictionary*)user{
    
    @synchronized(self)
    {
        const char *sqlStatement = "UPDATE tf_tblMyFriends set name = ?, cellphone = ?, pinyin = ?, data = ? where uid = ?";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tf_tblMyFriends");
            return;
        }
        
        
        NSString *name = [user objectForKey:@"name"];
        if(name == nil)
            name = @"";
        sqlite3_bind_text(statement, 1, [name UTF8String], -1, SQLITE_TRANSIENT);
        
        NSString* value = [user objectForKey:@"cellphone"];
        if(value == nil)
            value = @"";
        sqlite3_bind_text(statement, 2, [value UTF8String], -1, SQLITE_TRANSIENT);
        
        value = [self convPinyinFromCharactor:name];
        if(value == nil)
            value = @"";
        sqlite3_bind_text(statement, 3, [value UTF8String], -1, SQLITE_TRANSIENT);
        
        
        NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:user];
        sqlite3_bind_blob(statement, 4, [archiveData bytes], (int)[archiveData length], NULL);
        
        int uid = [[user objectForKey:@"id"] intValue];
        sqlite3_bind_int(statement, 5, uid);
        
        success = sqlite3_step(statement);
        sqlite3_finalize(statement);
        
        if (success == SQLITE_ERROR) {
            NSLog(@"Error: failed to insert into tf_tblMyFriends with message.");
            return;
        }
    }
}

- (NSArray *) queryAllFriends{
    
    @synchronized(self)
    {
        const char *sqlStatement = "select data from tf_tblMyFriends";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tf_tblMyFriends");
            return nil;
        }
        
        
        NSMutableArray *results = [[NSMutableArray alloc] init];
        
        while (sqlite3_step(statement) == SQLITE_ROW) {
            
            const void* achievement_id       = sqlite3_column_blob(statement, 0);
            int achievement_idSize           = sqlite3_column_bytes(statement, 0);
            
            if(achievement_id)
            {
                NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
                NSDictionary * dic = [NSKeyedUnarchiver unarchiveObjectWithData:data];
                
                [results addObject:dic];
            }
        }
        sqlite3_finalize(statement);
        
        return results;
    }
}



#pragma mark ---创建组织结构表
- (void) checkAndCreateOrgTable{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblTFOrgs'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblTFOrgs");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        s = @"CREATE TABLE tblTFOrgs(id INTEGER, pid INTEGER, flag INTEGER, name TEXT, cellphone TEXT, data BLOB)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared DataBase! -- tblTFOrgs");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}

- (void) deleteAllOrgs{
    
    NSString *s = @"delete from tblTFOrgs";
    
    const char * sql = [s UTF8String];
    sqlite3_stmt *delete_statement = nil;
    
    if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
        NSLog(@"Not delete from tblTFOrgs!");
    }
    
    sqlite3_step(delete_statement);
    sqlite3_finalize(delete_statement);
}

- (int) insertOrgUnit:(NSDictionary*)unit{
    
    const char *sqlStatement = "insert into tblTFOrgs (id, pid, flag, name, cellphone, data) VALUES (?, ?, ?, ?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblTFOrgs");
        return -1;
    }
    
    
    int uid = [[unit objectForKey:@"id"] intValue];
    sqlite3_bind_int(statement, 1, uid);
    
    int pid = [[unit objectForKey:@"pid"] intValue];
    sqlite3_bind_int(statement, 2, pid);
    
    int flag = [[unit objectForKey:@"flag"] intValue];
    sqlite3_bind_int(statement, 3, flag);
    
    NSString *account = @"";
    NSString *name = @"";
    if(flag == 1)
    {
        account = [unit objectForKey:@"account"];
        name = [unit objectForKey:@"name"];
        
        if(account == nil)
            account = @"";
        if(name == nil)
            name = @"";
    }
    
    sqlite3_bind_text(statement, 4, [name UTF8String], -1, SQLITE_TRANSIENT);
    
    sqlite3_bind_text(statement, 5, [account UTF8String], -1, SQLITE_TRANSIENT);
    
    NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:unit];
    sqlite3_bind_blob(statement, 6, [archiveData bytes], (int)[archiveData length], NULL);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblTFOrgs with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}

- (NSArray *) queryOrgUnitsByPid:(int)pid{
    
    const char *sqlStatement = "select data from tblTFOrgs where pid = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblTFOrgs");
        return nil;
    }
    
    sqlite3_bind_int(statement, 1, pid);
    
    
    NSMutableArray *results = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* achievement_id       = sqlite3_column_blob(statement, 0);
        int achievement_idSize           = sqlite3_column_bytes(statement, 0);
        
        if(achievement_id)
        {
            NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
            NSDictionary * dic = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            [results addObject:dic];
        }
    }
    sqlite3_finalize(statement);
    
    return results;
}


- (NSArray *)searchOrgPersonsWithKeyword:(NSString *)keyword{
    
    NSString *sql = [NSString stringWithFormat:@"select data from tblTFOrgs where flag = 1 and (name like '%%%@%%' or cellphone like '%@%%')",
                     keyword,
                     keyword];
    
    const char *sqlStatement = [sql UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblTFOrgs");
        return nil;
    }
    
    
    NSMutableArray *results = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* achievement_id       = sqlite3_column_blob(statement, 0);
        int achievement_idSize           = sqlite3_column_bytes(statement, 0);
        
        if(achievement_id)
        {
            NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
            NSDictionary * dic = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            if([dic count])
            {
                WSUser *uu = [[WSUser alloc] initWithDictionary:dic];
                uu.familiy = 0;
                [results addObject:uu];
            }
            
            
        }
    }
    sqlite3_finalize(statement);
    
    return results;
}

#pragma mark ---创建表
- (void) checkAndCreateRequestMessagesTable{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblRequestMsgs'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //uid, fullname, avatarurl, gender, mtime, unread, status(状态0-未处理，1-接受), label
        s = @"CREATE TABLE tblRequestMsgs(uid INTEGER, fullname TEXT,  avatarurl TEXT, gender INTEGER, mtime INTEGER, unread INTEGER, status INTEGER, label TEXT)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared DataBase!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}


- (int) saveRequestMessages:(NSDictionary*)request{
    
    int uid = [[request objectForKey:@"id"] intValue];
    
    if([self checkRequestExsit:uid])
    {
        [self updateRequestStatus:request];
        
        return 1;
    }
    
    const char *sqlStatement = "insert into tblRequestMsgs (uid, fullname, avatarurl, gender, mtime, unread, status, label) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return -1;
    }
    
    
    sqlite3_bind_int(statement, 1, uid);
    
    NSString *value = [request objectForKey:@"fullname"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 2, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    value = [request objectForKey:@"avatarurl"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 3, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    int gender = [[request objectForKey:@"gender"] intValue];
    sqlite3_bind_int(statement, 4, gender);
    
    int mtime = [[request objectForKey:@"mtime"] intValue];
    sqlite3_bind_int(statement, 5, mtime);
    
    sqlite3_bind_int(statement, 6, 1);
    
    sqlite3_bind_int(statement, 7, 0);
    
    value = [request objectForKey:@"label"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 8, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblRequestMsgs with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}

- (int) checkRequestExsit:(int)uid{
    
    const char *sqlStatement = "select * from tblRequestMsgs where uid = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return 0;
    }
    
    sqlite3_bind_int(statement, 1, uid);
    
    int iRes = 0;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        iRes = 1;
        break;
    }
    sqlite3_finalize(statement);
    
    return iRes;
}


- (void) updateRequestMessages:(NSDictionary*)request{
    
    const char *sqlStatement = "UPDATE tblRequestMsgs set fullname=?, avatarurl=?, gender=?, mtime=?, unread = ?, label = ? where uid = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return;
    }
    
    
    NSString *value = [request objectForKey:@"fullname"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 1, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    value = [request objectForKey:@"avatarurl"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 2, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    int gender = [[request objectForKey:@"gender"] intValue];
    sqlite3_bind_int(statement, 3, gender);
    
    int mtime = [[request objectForKey:@"mtime"] intValue];
    sqlite3_bind_int(statement, 4, mtime);
    
    sqlite3_bind_int(statement, 5, 1);
    
    value = [request objectForKey:@"label"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 6, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    
    int uid = [[request objectForKey:@"id"] intValue];
    sqlite3_bind_int(statement, 7, uid);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblRequestMsgs with message.");
        return;
    }
    
}


- (void) updateRequestStatus:(NSDictionary*)request{
    
    const char *sqlStatement = "UPDATE tblRequestMsgs set unread = ?, status = ? where uid = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return;
    }
    
    sqlite3_bind_int(statement, 1, 1);
    sqlite3_bind_int(statement, 2, 0);
    
    int uid = [[request objectForKey:@"id"] intValue];
    sqlite3_bind_int(statement, 3, uid);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblRequestMsgs with message.");
        return;
    }
    
}


- (NSArray*) queryAllRequestMessages{
    
    const char *sqlStatement = "select * from tblRequestMsgs";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return nil;
    }
    
    
    NSMutableArray *results = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int uid                  = sqlite3_column_int(statement, 0);
        char* fullname           = (char*)sqlite3_column_text(statement, 1);
        char* avatar             = (char*)sqlite3_column_text(statement, 2);
        int gender               = sqlite3_column_int(statement, 3);
        int mtime                = sqlite3_column_int(statement, 4);
        int unread               = sqlite3_column_int(statement, 5);
        int status               = sqlite3_column_int(statement, 6);
        char* label              = (char*)sqlite3_column_text(statement, 7);
        
        
        NSMutableDictionary *req = [[NSMutableDictionary alloc] init];
        
        [req setObject:[NSNumber numberWithInt:uid] forKey:@"uid"];
        [req setObject:[NSNumber numberWithInt:gender] forKey:@"gender"];
        [req setObject:[NSNumber numberWithInt:mtime] forKey:@"mtime"];
        [req setObject:[NSNumber numberWithInt:unread] forKey:@"unread"];
        [req setObject:[NSNumber numberWithInt:status] forKey:@"status"];
        
        if(fullname)
        {
            [req setObject:[NSString stringWithUTF8String:fullname] forKey:@"fullname"];
        }
        if(avatar)
        {
            [req setObject:[NSString stringWithUTF8String:avatar] forKey:@"avatarurl"];
        }
        if(label)
        {
            [req setObject:[NSString stringWithUTF8String:label] forKey:@"label"];
        }
        [results addObject:req];
    }
    sqlite3_finalize(statement);
    
    return results;
}

- (int) unreadMessagesCount{
    
    const char *sqlStatement = "select count(*) from tblRequestMsgs where unread = 1";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return 0;
    }
    
    
    int count = 0;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        count                  = sqlite3_column_int(statement, 0);
        
        break;
    }
    sqlite3_finalize(statement);
    
    return count;
}

- (void) readAllMessages{
    
    const char *sqlStatement = "UPDATE tblRequestMsgs set unread = 0";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return;
    }
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblRequestMsgs with message.");
        return;
    }
    
}

- (void) processRequestMessages:(int)uid accept:(BOOL)accept{
    
    const char *sqlStatement = "UPDATE tblRequestMsgs set status = ? where uid = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblRequestMsgs");
        return;
    }
    
    
    if(accept)
    {
        sqlite3_bind_int(statement, 1, 1);
    }
    else
    {
        sqlite3_bind_int(statement, 1, 0);
    }
    
    sqlite3_bind_int(statement, 2, uid);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblRequestMsgs with message.");
        return;
    }
    
}


#pragma mark ---创建会话表
- (void) checkAndCreateChatTable{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblConversation'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblConversation");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        s = @"CREATE TABLE tblConversation(target_id TEXT,  mute_state INTEGER)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared DataBase!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}



- (int) updateConversationMute:(NSString*)targetId mute:(int)mute{
    
    if([self checkConversationExsit:targetId])
    {
        [self updateConversation:targetId mute:mute];
        
        return 1;
    }
    
    const char *sqlStatement = "insert into tblConversation (target_id, mute_state) VALUES (?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblConversation");
        return -1;
    }
    
    
    sqlite3_bind_text(statement, 1, [targetId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(statement, 2, mute);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblConversation with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}


- (int) checkConversationExsit:(NSString*)targetId{
    
    const char *sqlStatement = "select * from tblConversation where target_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblConversation");
        return 0;
    }
    
    sqlite3_bind_text(statement, 1, [targetId UTF8String], -1, SQLITE_TRANSIENT);
    
    int iRes = 0;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        iRes = 1;
        break;
    }
    sqlite3_finalize(statement);
    
    return iRes;
}


- (void) updateConversation:(NSString*)targetId mute:(int)mute{
    
    const char *sqlStatement = "UPDATE tblConversation set mute_state = ? where target_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblConversation");
        return;
    }
    
    sqlite3_bind_int(statement, 1, mute);
    sqlite3_bind_text(statement, 2, [targetId UTF8String], -1, SQLITE_TRANSIENT);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblConversation with message.");
        return;
    }
    
}

- (int) muteStateWithTarget:(NSString*)targetId{
    
    const char *sqlStatement = "select * from tblConversation where target_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblConversation");
        return -1;
    }
    
    sqlite3_bind_text(statement, 1, [targetId UTF8String], -1, SQLITE_TRANSIENT);
    
    int result_mute = 0;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        result_mute   = sqlite3_column_int(statement, 1);
        
        break;
    }
    sqlite3_finalize(statement);
    
    return result_mute;
    
}


#pragma mark ---用户表
- (void) checkAndCreateUserTable{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblUser'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblUser");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        s = @"CREATE TABLE tblUser(user_id TEXT,  user_name TEXT, user_avatar TEXT)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared DataBase!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}


- (int) saveUserInfo:(RCUserInfo*)uInfo{
    
    if([self checkUserExsit:uInfo.userId])
    {
        [self updateUserInfo:uInfo];
        
        return 1;
    }
    
    const char *sqlStatement = "insert into tblUser (user_id, user_name, user_avatar) VALUES (?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblUser");
        return -1;
    }
    
    NSString *value = @"";
    if(uInfo.userId)
        value = uInfo.userId;
    sqlite3_bind_text(statement, 1, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    if(uInfo.name)
        value = uInfo.name;
    sqlite3_bind_text(statement, 2, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    if(uInfo.portraitUri)
        value = uInfo.portraitUri;
    sqlite3_bind_text(statement, 3, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblUser with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}


- (int) checkUserExsit:(NSString*)userId{
    
    const char *sqlStatement = "select user_id from tblUser where user_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblUser");
        return 0;
    }
    
    sqlite3_bind_text(statement, 1, [userId UTF8String], -1, SQLITE_TRANSIENT);
    
    int iRes = 0;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        iRes = 1;
        break;
    }
    sqlite3_finalize(statement);
    
    return iRes;
}


- (void) updateUserInfo:(RCUserInfo*)uInfo{
    
    const char *sqlStatement = "UPDATE tblUser set user_name = ?, user_avatar = ? where user_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblUser");
        return;
    }
    
    sqlite3_bind_text(statement, 1, [uInfo.name UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(statement, 2, [uInfo.portraitUri UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(statement, 3, [uInfo.userId UTF8String], -1, SQLITE_TRANSIENT);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblUser with message.");
        return;
    }
    
}

- (RCUserInfo *)queryUser:(NSString*)userId{
    
    const char *sqlStatement = "select * from tblUser where user_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblUser");
        return nil;
    }
    
    sqlite3_bind_text(statement, 1, [userId UTF8String], -1, SQLITE_TRANSIENT);
    
    RCUserInfo *result = nil;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        char* name           = (char*)sqlite3_column_text(statement, 1);
        char* avatar         = (char*)sqlite3_column_text(statement, 2);
        
        
        result = [[RCUserInfo alloc] init];
        result.userId = userId;
        if(name)
        {
            result.name = [NSString stringWithUTF8String:name];
        }
        if(avatar)
        {
            result.portraitUri = [NSString stringWithUTF8String:avatar];
        }
        
        break;
    }
    sqlite3_finalize(statement);
    
    return result;
}

#pragma mark ---Group表
- (void) checkAndCreateGroupTable{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblGroupCache'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        s = @"CREATE TABLE tblGroupCache (group_id TEXT,  group_name TEXT, group_avatar TEXT, g_members BLOB, g_data BLOB)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared DataBase!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}


- (int) saveGroupInfo:(NSDictionary*)gInfo{
    
    int gid = [[gInfo objectForKey:@"GID"] intValue];
    NSString *sGid = [NSString stringWithFormat:@"%d", gid];
    
    if([self checkGroupExsit:sGid])
    {
        [self updateGroupInfo:gInfo];
        
        return 1;
    }
    
    const char *sqlStatement = "insert into tblGroupCache (group_id, group_name, group_avatar, g_data) VALUES (?, ?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return -1;
    }
    
    NSString *value = @"";
    if(gid)
        value = sGid;
    sqlite3_bind_text(statement, 1, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    
    value = [gInfo objectForKey:@"name"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 2, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    value = @"";
    if([gInfo objectForKey:@"logo"])
    {
        value = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [gInfo objectForKey:@"logo"]];
    }
    sqlite3_bind_text(statement, 3, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:gInfo];
    sqlite3_bind_blob(statement, 4, [archiveData bytes], (int)[archiveData length], NULL);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblGroupCache with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}


- (int) checkGroupExsit:(NSString*)userId{
    
    const char *sqlStatement = "select group_id from tblGroupCache where group_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return 0;
    }
    
    sqlite3_bind_text(statement, 1, [userId UTF8String], -1, SQLITE_TRANSIENT);
    
    int iRes = 0;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        iRes = 1;
        break;
    }
    sqlite3_finalize(statement);
    
    return iRes;
}


- (void) updateGroupInfo:(NSDictionary*)gInfo{
    
    const char *sqlStatement = "UPDATE tblGroupCache set group_name = ?, group_avatar = ?, g_data = ? where group_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return;
    }
    
    int gid = [[gInfo objectForKey:@"GID"] intValue];
    NSString *sGid = [NSString stringWithFormat:@"%d", gid];
    
    NSString* value = [gInfo objectForKey:@"name"];
    if(value == nil)
        value = @"";
    sqlite3_bind_text(statement, 1, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    value = @"";
    if([gInfo objectForKey:@"logo"])
    {
        value = [NSString stringWithFormat:@"%@/upload/images/%@", WEB_API_URL, [gInfo objectForKey:@"logo"]];
    }
    sqlite3_bind_text(statement, 2, [value UTF8String], -1, SQLITE_TRANSIENT);
    
    NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:gInfo];
    sqlite3_bind_blob(statement, 3, [archiveData bytes], (int)[archiveData length], NULL);
    
    
    sqlite3_bind_text(statement, 4, [sGid UTF8String], -1, SQLITE_TRANSIENT);
    
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblGroupCache with message.");
        return;
    }
    
}

- (NSDictionary *)queryGroup:(NSString*)groupId{
    
    const char *sqlStatement = "select g_data from tblGroupCache where group_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return nil;
    }
    
    sqlite3_bind_text(statement, 1, [groupId UTF8String], -1, SQLITE_TRANSIENT);
    
    
    NSDictionary *result = nil;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* achievement_id       = sqlite3_column_blob(statement, 0);
        int achievement_idSize           = sqlite3_column_bytes(statement, 0);
        
        if(achievement_id)
        {
            NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
            result = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            break;
        }
        
        break;
    }
    sqlite3_finalize(statement);
    
    return result;
}

- (NSArray *) queryAllGroups{
    
    const char *sqlStatement = "select g_data from tblGroupCache";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return nil;
    }
    
    NSMutableArray *results = [NSMutableArray array];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* achievement_id       = sqlite3_column_blob(statement, 0);
        int achievement_idSize           = sqlite3_column_bytes(statement, 0);
        
        if(achievement_id)
        {
            NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
            NSDictionary* result = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            [results addObject:result];
        }
        
    }
    sqlite3_finalize(statement);
    
    return results;
}

- (void) saveGroupMembers:(NSArray*)members groupId:(NSString*)groupId{
    
    //说明是新数据
    const char *sqlStatement = "update tblGroupCache set g_members = ? where group_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return;
    }
    
    NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:members];
    sqlite3_bind_blob(statement, 1, [archiveData bytes], (int)[archiveData length], NULL);
    
    sqlite3_bind_text(statement, 2, [groupId UTF8String], -1, SQLITE_TRANSIENT);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblGroupCache with message.");
        return;
    }
    
}
- (NSArray *) queryGroupMembers:(NSString*)groupId{
    
    const char *sqlStatement = "select g_members from tblGroupCache where group_id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGroupCache");
        return nil;
    }
    
    sqlite3_bind_text(statement, 1, [groupId UTF8String], -1, SQLITE_TRANSIENT);
    
    NSMutableArray *result = nil;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* achievement_id       = sqlite3_column_blob(statement, 0);
        int achievement_idSize           = sqlite3_column_bytes(statement, 0);
        
        if(achievement_id)
        {
            NSData *data = [[NSData alloc]initWithBytes:achievement_id length:achievement_idSize];
            result = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            break;
        }
        
    }
    sqlite3_finalize(statement);
    
    return result;
}



@end

