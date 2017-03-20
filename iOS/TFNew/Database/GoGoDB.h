//
//  GoGoDB.h
//  Gemini
//
//  Created by jack on 1/9/15.
//  Copyright (c) 2015 jack. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>
#import "RCJUser.h"



@interface GoGoDB : NSObject {
    
    sqlite3  *database_;
    
}
@property (strong, nonatomic) NSString *databasePath_;
@property (strong, nonatomic) NSString *_currentUserFolder;


+ (GoGoDB*)sharedDBInstance;
+ (void)logoutDB;


-(int)open;
-(void)close;

- (int) saveUserInfo:(RCUserInfo*)uInfo;
- (RCUserInfo *)queryUser:(NSString*)userId;

- (int) saveGroupInfo:(NSDictionary*)gInfo;
- (NSDictionary *)queryGroup:(NSString*)userId;
- (void) saveGroupMembers:(NSArray*)members groupId:(NSString*)groupId;
- (NSArray *) queryGroupMembers:(NSString*)groupId;
- (NSArray *) queryAllGroups;

- (int) updateConversationMute:(NSString*)targetId mute:(int)mute;
- (int) muteStateWithTarget:(NSString*)targetId;

- (int) saveRequestMessages:(NSDictionary*)request;
- (NSArray*) queryAllRequestMessages;
- (void) readAllMessages;
- (void) processRequestMessages:(int)uid accept:(BOOL)accept;
- (int) unreadMessagesCount;

- (void) deleteFriends;
- (NSArray *) queryAllFriends;
- (int) insertAFriend:(NSDictionary*)user;
- (BOOL) isFriendLocalCahced:(NSString*)uid;
- (void) deleteFriendByUid:(NSString*)uid;

- (NSArray *)searchFriendsWithKeyword:(NSString *)keyword;


- (NSArray *) queryOrgUnitsByPid:(int)pid;
- (int) insertOrgUnit:(NSDictionary*)unit;
- (void) deleteAllOrgs;
- (NSArray *)searchOrgPersonsWithKeyword:(NSString *)keyword;

//- (int) saveCmdMessages:(NSDictionary*)msg;
//- (NSArray*) queryAllCmdMsg:(int)limit;
//- (int) unreadCmdMsgCount;
//- (void) readAllCmdMessages;

@end
