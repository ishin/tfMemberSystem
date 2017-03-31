//
//  GoGoDB.m
//  Gemini
//
//  Created by jack on 1/9/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import "wsDB.h"
#import "SBJson4.h"
#import "UserDefaultsKV.h"


@implementation wsDB
@synthesize databasePath_;
@synthesize _currentUserFolder;

static wsDB* wsDBInstance = nil;

+ (wsDB*)sharedDBInstance{
    
    User *mp = [UserDefaultsKV getUser];
    if(mp == nil)
        return nil;
    
    if(wsDBInstance == nil){
        wsDBInstance = [[wsDB alloc] init];
        [wsDBInstance open];
    }
    else
    {
        NSString *account = [UserDefaultsKV getAccount];
        if([account length] == 0)
        {
            account = mp._userId;
        }
        
        if(![account isEqualToString:wsDBInstance._currentUserFolder])
        {
            wsDBInstance = nil;
            wsDBInstance = [[wsDB alloc] init];
            [wsDBInstance open];
        }

    }
    return wsDBInstance;
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
    
    dbPath = [dbPath stringByAppendingPathComponent:@"wsDB.sqlite"];
    
    if(![fm fileExistsAtPath:dbPath])
    {
        NSString *sPath = [[NSBundle mainBundle] pathForResource:@"wsDB.sqlite" ofType:nil];
        
        [fm copyItemAtPath:sPath toPath:dbPath error:nil];
    }
    
    NSLog(@"%@",dbPath);
    
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
    dbPath = [dbPath stringByAppendingPathComponent:@"wsDB.sqlite"];
	
	self.databasePath_ = dbPath;
	if(sqlite3_open([dbPath UTF8String], &database_)== SQLITE_OK)
	{
        //初始化数据库
        [self checkAndCreateFavCache];
        
        [self checkAndCreateCompanyCache];
        
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


#pragma mark ---表

- (void) checkAndCreateRecordCache{
 
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblContentReadingCache'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblContentReadingCache");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblContentReadingCache(id INTEGER, status INTEGER, type INTEGER)";
        
        //
        
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblContentReadingCache!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
    
}

- (int) readingFlag:(int)oid withType:(int)type{
    
    if(oid == 0)
        return 0;
    
    if([self cahcedByIdAndType:oid type:type])
        return 0;
    
    const char *sqlStatement = "insert into tblContentReadingCache (id, status, type) VALUES (?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblContentReadingCache");
        return -1;
    }
    
    sqlite3_bind_int(statement, 1, oid);
    sqlite3_bind_int(statement, 2, 1);
    sqlite3_bind_int(statement, 3, type);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblContentReadingCache with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
}

- (BOOL)cahcedByIdAndType:(int)targetId type:(int)type{
    
    if(targetId == 0)
        return NO;
    
    NSString* s = [NSString stringWithFormat:@"select * from tblContentReadingCache where id = %d and type = %d", targetId, type];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblContentReadingCache");
        return NO;
    }
    
    
    int result = NO;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        result = YES;
        break;
    }
    
    sqlite3_finalize(statement);
    
    return result;
    
}

- (void)removeCacheByIdAndType:(int)oid type:(int)type{
    
    
    NSString *s = @"delete from tblFavCache where id = %d and type = %d";
    s = [NSString stringWithFormat:s, oid, type];
    
    const char * sql = [s UTF8String];
    sqlite3_stmt *delete_statement = nil;
    
    if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
        NSLog(@"Not delete from tblFavCache!");
    }
    
    sqlite3_step(delete_statement);
    sqlite3_finalize(delete_statement);
    
}


- (void) checkAndCreateFavCache{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblFavCache'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblFavCache");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblFavCache(id INTEGER, data BLOB, type INTEGER)";
        
        //

        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblFavCache!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
    
}


- (int) insertFav:(int)objid withType:(int)type{
    
    int oid = objid;
    
    if([self favByIdAndType:oid type:type])
        return 0;
    
    const char *sqlStatement = "insert into tblFavCache (id, type) VALUES (?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblFavCache");
        return -1;
    }
    
    sqlite3_bind_int(statement, 1, oid);
    sqlite3_bind_int(statement, 2, type);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblFavCache with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
}

- (int)favByIdAndType:(int)oid type:(int)type{
    
    NSString* s = [NSString stringWithFormat:@"select * from tblFavCache where id = %d and type = %d", oid, type];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblFavCache");
        return 0;
    }
    
    
    int result = 0;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        result = 1;
        break;
    }
    
    sqlite3_finalize(statement);
    
    return result;
    
}

- (void)removeFavByIdAndType:(int)oid type:(int)type{
    
    
    NSString *s = @"delete from tblFavCache where id = %d and type = %d";
    s = [NSString stringWithFormat:s, oid, type];
    
    const char * sql = [s UTF8String];
    sqlite3_stmt *delete_statement = nil;
    
    if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
        NSLog(@"Not delete from tblFavCache!");
    }
    
    sqlite3_step(delete_statement);
    sqlite3_finalize(delete_statement);

}

- (NSArray *)allFavItems{
    
    NSString* s = @"select data from tblFavCache";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblFavCache");
        return 0;
    }
    
    
    NSMutableArray * result = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        const void* data       = sqlite3_column_blob(statement, 0);
        int datasize           = sqlite3_column_bytes(statement, 0);

        if(datasize > 0)
        {
            NSData *jsonData = [[NSData alloc]initWithBytes:data length:datasize];
            NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:nil];
            [result addObject:dic];

        }
  
    }
    
    sqlite3_finalize(statement);
    
    return result;
    
}

- (void) checkAndCreateTopicCache{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblGTopicCacheV1'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGTopicCacheV1");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblGTopicCacheV1(id INTEGER, name TEXT, branchid INTEGER, role INTEGER, content TEXT)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblGroupTopicCache!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}


- (int) insertTopic:(NSDictionary*)dic{
    
    @synchronized(self)
    {
    
        int pid = [[dic objectForKey:@"id"] intValue];
        
        if([self topicById:pid])
            return 0;
        
        const char *sqlStatement = "insert into tblGTopicCacheV1 (id, name, branchid, role, content) VALUES (?, ?, ?, ?, ?)";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tblGTopicCacheV1");
            return -1;
        }
        
        
        NSString *name = [dic objectForKey:@"name"];
        if(name == nil)
            name = [dic objectForKey:@"title"];
        int branchid = [[dic objectForKey:@"branchid"] intValue];
        
        int role = 0;
        if([dic objectForKey:@"role"])
        {
            role = [[dic objectForKey:@"role"] intValue];
        }
        else
        {
            if([dic objectForKey:@"type"])
            {
                role = [[dic objectForKey:@"type"] intValue];
            }
        }
        
        if(role == 3)
            role = 4;
        
        sqlite3_bind_int(statement, 1, pid);
        sqlite3_bind_text(statement, 2, [name UTF8String], -1, SQLITE_TRANSIENT);
        sqlite3_bind_int(statement, 3, branchid);
        sqlite3_bind_int(statement, 4, role);
        
        NSString *content = [dic objectForKey:@"content"];
        if(content == nil)
            content = @"";
        sqlite3_bind_text(statement, 5, [content UTF8String], -1, SQLITE_TRANSIENT);
        
        success = sqlite3_step(statement);
        sqlite3_finalize(statement);
        
        if (success == SQLITE_ERROR) {
            NSLog(@"Error: failed to insert into tblGTopicCacheV1 with message.");
            return -1;
        }
        
        int lastRow = (int)sqlite3_last_insert_rowid(database_);
        
        return lastRow;
    }
    
}

- (NSString *)topicById:(int)tid{
    
    @synchronized(self)
    {

    
        NSString* s = [NSString stringWithFormat:@"select name from tblGTopicCacheV1 where id = %d", tid];
        
        const char *sqlStatement = [s UTF8String];
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tblGTopicCacheV1");
            return nil;
        }
        
        
        NSString *result = nil;
        
        while (sqlite3_step(statement) == SQLITE_ROW) {
            
            char* name     = (char*)sqlite3_column_text(statement, 0);
            
            
            result = [NSString stringWithUTF8String:name];
            break;
        }
        
        sqlite3_finalize(statement);
        
        return result;
    }
    
}

- (NSString *)topicContentById:(int)tid{
    @synchronized(self)
    {
        
        
        NSString* s = [NSString stringWithFormat:@"select content from tblGTopicCacheV1 where id = %d", tid];
        
        const char *sqlStatement = [s UTF8String];
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tblGTopicCacheV1");
            return nil;
        }
        
        
        NSString *result = nil;
        
        while (sqlite3_step(statement) == SQLITE_ROW) {
            
            char* name     = (char*)sqlite3_column_text(statement, 0);
            
            
            result = [NSString stringWithUTF8String:name];
            break;
        }
        
        sqlite3_finalize(statement);
        
        return result;
    }
    
}

- (NSArray *)topicListByBranchid:(int)branchid andRole:(int)role{
    
    NSString* s = [NSString stringWithFormat:@"select * from tblGTopicCacheV1 where branchid = %d and role = %d", branchid, role];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblGTopicCacheV1");
        return nil;
    }
    
    
    NSMutableArray *result = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int tid        = sqlite3_column_int(statement, 0);
        char* name     = (char*)sqlite3_column_text(statement, 1);
        int branchid   = sqlite3_column_int(statement, 2);
        int role   = sqlite3_column_int(statement, 3);
        char* content     = (char*)sqlite3_column_text(statement, 4);
    
        
        [result addObject:@{@"id":[NSNumber numberWithInt:tid],
                            @"title":[NSString stringWithUTF8String:name],
                            @"content":[NSString stringWithUTF8String:content],
                            @"branchid":[NSNumber numberWithInt:branchid],
                            @"role":[NSNumber numberWithInt:role]}];
        
    }
    
    sqlite3_finalize(statement);
    
    return result;
}

- (void) checkAndCreateBBSCache{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblBBSCache'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblBBSCache");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblBBSCache(id INTEGER, threads INTEGER)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblBBSCache!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}


- (int) insertBBSTopic:(NSDictionary*)dic{
    
    const char *sqlStatement = "insert into tblBBSCache (id, threads) VALUES (?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblBBSCache");
        return -1;
    }
    
    int pid = [[dic objectForKey:@"id"] intValue];
    int count = [[dic objectForKey:@"threads"] intValue];
    
    sqlite3_bind_int(statement, 1, pid);
    sqlite3_bind_int(statement, 2, count);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblBBSCache with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
}
- (int )threadsCount:(int)branchid{
    
    NSString* s = [NSString stringWithFormat:@"select threads from tblBBSCache where id = %d", branchid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblBBSCache");
        return 0;
    }
    
    
    int result = 0;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
       result     = sqlite3_column_int(statement, 0);
    
        break;
    }
    
    sqlite3_finalize(statement);
    
    return result;

}
#pragma mark ---足迹表
- (void) checkAndCreateProvince{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblProvince'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblProvince");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblProvince(id INTEGER, name TEXT)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblProvince!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}

#pragma mark ---足迹表
- (void) checkAndCreateCity{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblCity'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCity");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblCity(id INTEGER, name TEXT, pid INTEGER)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblCity!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}

#pragma mark ---足迹表
- (void) checkAndCreateArea{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblArea'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblArea");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblArea(id INTEGER, name TEXT, pid INTEGER)";
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblArea!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
}

- (int) insertProvince:(NSDictionary*)dic{
    
    const char *sqlStatement = "insert into tblProvince (id, name) VALUES (?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblProvince");
        return -1;
    }
    
    int pid = [[dic objectForKey:@"id"] intValue];
    NSString *name = [dic objectForKey:@"name"];
    
    sqlite3_bind_int(statement, 1, pid);
    sqlite3_bind_text(statement, 2, [name UTF8String], -1, SQLITE_TRANSIENT);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblProvince with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;

}

- (int) insertCity:(NSDictionary*)dic{
    
    const char *sqlStatement = "insert into tblCity (id, name, pid) VALUES (?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCity");
        return -1;
    }
    
    int pid = [[dic objectForKey:@"id"] intValue];
    NSString *name = [dic objectForKey:@"name"];
    int ppid = [[dic objectForKey:@"pid"] intValue];
    
    sqlite3_bind_int(statement, 1, pid);
    sqlite3_bind_text(statement, 2, [name UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(statement, 3, ppid);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblCity with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}

- (int) insertArea:(NSDictionary*)dic{
    
    const char *sqlStatement = "insert into tblArea (id, name, pid) VALUES (?, ?, ?)";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblArea");
        return -1;
    }
    
    int pid = [[dic objectForKey:@"id"] intValue];
    NSString *name = [dic objectForKey:@"name"];
    int ppid = [[dic objectForKey:@"pid"] intValue];
    
    sqlite3_bind_int(statement, 1, pid);
    sqlite3_bind_text(statement, 2, [name UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(statement, 3, ppid);
    
    success = sqlite3_step(statement);
    sqlite3_finalize(statement);
    
    if (success == SQLITE_ERROR) {
        NSLog(@"Error: failed to insert into tblArea with message.");
        return -1;
    }
    
    int lastRow = (int)sqlite3_last_insert_rowid(database_);
    
    return lastRow;
    
}


- (NSArray *)queryAllProvince{
 
    NSString* s = @"select * from tblProvince";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblProvince");
        return nil;
    }
    
    NSMutableArray *results = [[NSMutableArray alloc] init];

    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int pid        = sqlite3_column_int(statement, 0);
        char* name     = (char*)sqlite3_column_text(statement, 1);
        
        
        NSMutableDictionary *mdic = [[NSMutableDictionary alloc] init];
        if(name)
        {
            [mdic setObject:[NSString stringWithUTF8String:name] forKey:@"name"];
        }
        
        [mdic setObject:[NSString stringWithFormat:@"%d",pid] forKey:@"id"];
        
        
        [results addObject:mdic];
    }
    
    sqlite3_finalize(statement);
    
    return results;
    
}

- (NSDictionary *)searchProvince:(NSString*)provinceKey{
    
    NSString* s = [NSString stringWithFormat:@"select * from tblProvince where name like '%%%@%%'", provinceKey];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblProvince");
        return nil;
    }
    
     NSMutableDictionary *mdic = nil;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int pid        = sqlite3_column_int(statement, 0);
        char* name     = (char*)sqlite3_column_text(statement, 1);
        
        
        mdic = [[NSMutableDictionary alloc] init];
        if(name)
        {
            [mdic setObject:[NSString stringWithUTF8String:name] forKey:@"name"];
        }
        
        [mdic setObject:[NSString stringWithFormat:@"%d",pid] forKey:@"id"];
        
        
        break;
    }
    
    sqlite3_finalize(statement);
    
    return mdic;
    
}

- (NSDictionary *)searchCity:(NSString*)cityKey andProvince:(int)pid{
    
    NSString* s = [NSString stringWithFormat:@"select * from tblCity where name like '%%%@%%' and pid = %d", cityKey, pid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCity");
        return nil;
    }
    
    NSMutableDictionary *mdic = nil;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int cid        = sqlite3_column_int(statement, 0);
        char* name     = (char*)sqlite3_column_text(statement, 1);
        int pid        = sqlite3_column_int(statement, 2);
        
        mdic = [[NSMutableDictionary alloc] init];
        if(name)
        {
            [mdic setObject:[NSString stringWithUTF8String:name] forKey:@"name"];
        }
        
        [mdic setObject:[NSString stringWithFormat:@"%d",cid] forKey:@"id"];
        [mdic setObject:[NSNumber numberWithInt:pid] forKey:@"pid"];
        
        break;
    }
    
    sqlite3_finalize(statement);
    
    return mdic;
    
}


- (NSArray *)queryCityByProvince:(int)pid{
    
    NSString* s = [NSString stringWithFormat:@"select * from tblCity where pid = %d", pid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCity");
        return nil;
    }
    
    NSMutableArray *results = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int cid        = sqlite3_column_int(statement, 0);
        char* name     = (char*)sqlite3_column_text(statement, 1);
        int pid        = sqlite3_column_int(statement, 2);
        
        NSMutableDictionary *mdic = [[NSMutableDictionary alloc] init];
        if(name)
        {
            [mdic setObject:[NSString stringWithUTF8String:name] forKey:@"name"];
        }
        
        [mdic setObject:[NSString stringWithFormat:@"%d",cid] forKey:@"id"];
        [mdic setObject:[NSNumber numberWithInt:pid] forKey:@"pid"];
        
        
        [results addObject:mdic];
    }
    
    sqlite3_finalize(statement);
    
    return results;
    
}
- (NSArray *)queryAreaByCity:(int)pid{
    
    NSString* s = [NSString stringWithFormat:@"select * from tblArea where pid = %d", pid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblArea");
        return nil;
    }
    
    NSMutableArray *results = [[NSMutableArray alloc] init];
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        int cid        = sqlite3_column_int(statement, 0);
        char* name     = (char*)sqlite3_column_text(statement, 1);
        int pid        = sqlite3_column_int(statement, 2);
        
        NSMutableDictionary *mdic = [[NSMutableDictionary alloc] init];
        if(name)
        {
            [mdic setObject:[NSString stringWithUTF8String:name] forKey:@"name"];
        }
        
        [mdic setObject:[NSString stringWithFormat:@"%d",cid] forKey:@"id"];
        [mdic setObject:[NSNumber numberWithInt:pid] forKey:@"pid"];
        
        
        [results addObject:mdic];
    }
    
    sqlite3_finalize(statement);
    
    return results;
    
}


- (NSString *)provinceById:(int)oid{
    
    NSString* s = [NSString stringWithFormat:@"select name from tblProvince where id = %d", oid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblProvince");
        return nil;
    }
    
   
    NSString *result = @"";
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        char* name     = (char*)sqlite3_column_text(statement, 0);
        
        
        result = [NSString stringWithUTF8String:name];
        break;
    }
    
    sqlite3_finalize(statement);
    
    return result;
    
}
- (NSString *)cityById:(int)oid{
    
    NSString* s = [NSString stringWithFormat:@"select name from tblCity where id = %d", oid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCity");
        return nil;
    }
    
    
    NSString *result = @"";
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        char* name     = (char*)sqlite3_column_text(statement, 0);
        
        
        result = [NSString stringWithUTF8String:name];
        break;
    }
    
    sqlite3_finalize(statement);
    
    return result;
}
- (NSString *)areaById:(int)oid{
 
    NSString* s = [NSString stringWithFormat:@"select name from tblArea where id = %d", oid];
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblArea");
        return nil;
    }
    
    
    NSString *result = @"未知";
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        char* name     = (char*)sqlite3_column_text(statement, 0);
        
        
        result = [NSString stringWithUTF8String:name];
        break;
    }
    
    sqlite3_finalize(statement);
    
    return result;
}



- (BOOL) readingStatus:(int)targetId type:(int)type{
 
    
    return YES;
}



#pragma -- mark Company Records
- (void) checkAndCreateCompanyCache{
    
    NSString *s = @"SELECT * FROM sqlite_master WHERE type='table' AND name='tblCacheCompany'";
    
    const char *sqlStatement = [s UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCacheCompany");
        return;
    }
    
    BOOL have = NO;
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        have = YES;
    }
    sqlite3_finalize(statement);
    
    if(!have)
    {
        //create table
        
        s = @"CREATE TABLE tblCacheCompany(id INTEGER, zh_name TEXT, en_name TEXT, boothids TEXT, data BLOB)";
        
        //
    
        
        const char * sql = [s UTF8String];
        sqlite3_stmt *delete_statement = nil;
        
        if (sqlite3_prepare_v2(database_, sql, -1, &delete_statement, NULL) != SQLITE_OK) {
            NSLog(@"Not Prepared tblCacheCompany!");
        }
        
        sqlite3_step(delete_statement);
        sqlite3_finalize(delete_statement);
    }
    
}

- (int) insertOrg:(NSDictionary*)org{
    
    NSDictionary *company = [org objectForKey:@"company"];
    if(company)
    {
        NSDictionary *zh = [company objectForKey:@"zh-cn"];
        NSDictionary *en = [company objectForKey:@"en"];
        NSString *logourl = nil;
        NSString *fullname_zh = nil;
        NSString *fullname_en = nil;
        if(zh)
        {
            logourl = [zh objectForKey:@"logourl"];
            fullname_zh = [zh objectForKey:@"fullname"];
        }
        if(en)
        {
            logourl = [en objectForKey:@"logourl"];
            fullname_en = [en objectForKey:@"fullname"];
        }
        
        const char *sqlStatement = "insert into tblCacheCompany (id, zh_name, en_name, boothids, data) VALUES (?, ?, ?, ?, ?)";
        sqlite3_stmt *statement;
        
        int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
        if (success != SQLITE_OK) {
            NSLog(@"Error: failed to tblCacheCompany");
            return -1;
        }
        
        
        
        int uid = [[org objectForKey:@"companyid"] intValue];
        sqlite3_bind_int(statement, 1, uid);
        
        if(fullname_zh == nil)
            fullname_zh = @"";
        sqlite3_bind_text(statement, 2, [fullname_zh UTF8String], -1, SQLITE_TRANSIENT);
        
        if(fullname_en == nil)
            fullname_en = @"";
        sqlite3_bind_text(statement, 3, [fullname_en UTF8String], -1, SQLITE_TRANSIENT);
        
        NSString* value = [org objectForKey:@"boothid"];
        if(value == nil)
            value = @"";
        sqlite3_bind_text(statement, 4, [value UTF8String], -1, SQLITE_TRANSIENT);
        
        
        NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:org];
        sqlite3_bind_blob(statement, 5, [archiveData bytes], (int)[archiveData length], NULL);
        
        
        success = sqlite3_step(statement);
        sqlite3_finalize(statement);
        
        if (success == SQLITE_ERROR) {
            NSLog(@"Error: failed to insert into tblCacheCompany with message.");
            return -1;
        }
        
        int lastRow = (int)sqlite3_last_insert_rowid(database_);
        
        return lastRow;
    }
    
    return -1;
}

- (BOOL) isOrgLocalCahced:(id)orgid{
    
    const char *sqlStatement = "select * from tblCacheCompany where id = ?";
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCacheCompany");
        return NO;
    }
    
    sqlite3_bind_int(statement, 1, [orgid intValue]);
    
    
    BOOL result = NO;
    
    while (sqlite3_step(statement) == SQLITE_ROW) {
        
        result = YES;
        
        break;
    }
    sqlite3_finalize(statement);
    
    return result;
}

- (void) updateOrg:(NSDictionary*)org{
    
    NSDictionary *company = [org objectForKey:@"company"];
    if(company)
    {
        int orgid = [[org objectForKey:@"companyid"] intValue];
        if(orgid)
        {
            const char *sqlStatement = "UPDATE tblCacheCompany set zh_name = ?, en_name=?, boothids=?, data = ? where id = ?";
            sqlite3_stmt *statement;
            
            int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
            if (success != SQLITE_OK) {
                NSLog(@"Error: failed to tblCacheCompany");
                return;
            }
            
            NSDictionary *zh = [company objectForKey:@"zh-cn"];
            NSDictionary *en = [company objectForKey:@"en"];
            NSString *fullname_zh = nil;
            NSString *fullname_en = nil;
            if(zh)
            {
                fullname_zh = [zh objectForKey:@"fullname"];
            }
            if(en)
            {
                fullname_en = [en objectForKey:@"fullname"];
            }

        
            if(fullname_zh == nil)
                fullname_zh = @"";
            sqlite3_bind_text(statement, 1, [fullname_zh UTF8String], -1, SQLITE_TRANSIENT);
        
            if(fullname_en == nil)
                fullname_en = @"";
            sqlite3_bind_text(statement, 2, [fullname_en UTF8String], -1, SQLITE_TRANSIENT);
            
            NSString* value = [org objectForKey:@"boothid"];
            if(value == nil)
                value = @"";
            sqlite3_bind_text(statement, 3, [value UTF8String], -1, SQLITE_TRANSIENT);
            
            
            NSData *archiveData = [NSKeyedArchiver archivedDataWithRootObject:org];
            sqlite3_bind_blob(statement, 4, [archiveData bytes], (int)[archiveData length], NULL);
            

            sqlite3_bind_int(statement, 5, orgid);
            
            
            success = sqlite3_step(statement);
            sqlite3_finalize(statement);
            
            if (success == SQLITE_ERROR) {
                NSLog(@"Error: failed to insert into tblCacheCompany with message.");
                return;
            }
        }
    }
    
}

- (NSArray *)searchCompanyByKeyword:(NSString*)keyword{
    
    NSString *sql = [NSString stringWithFormat:@"select data from tblCacheCompany where zh_name like '%%%@%%' or en_name like '%%%@%%' or boothids like '%@%%'", keyword, keyword, keyword];
    
    const char *sqlStatement = [sql UTF8String];
    sqlite3_stmt *statement;
    
    int success = sqlite3_prepare_v2(database_, sqlStatement, -1, &statement, NULL);
    if (success != SQLITE_OK) {
        NSLog(@"Error: failed to tblCacheCompany");
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


@end

