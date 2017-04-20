package io.rong.imkit.userInfoCache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import io.rong.common.RLog;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

class RongDatabaseDao {
    private static final String TAG = "RongDatabaseDao";
    private RongUserCacheDatabaseHelper rongUserCacheDatabaseHelper;
    private SQLiteDatabase db;
    private final String usersTable = "users";
    private final String groupUsersTable = "group_users";
    private final String groupsTable = "groups";
    private final String discussionsTable = "discussions";
//    private final String publicServiceProfilesTable = "public_service_profiles";

    RongDatabaseDao() {
    }

    void open(Context context, String appKey, String currentUserId) {
        RongUserCacheDatabaseHelper.setDbPath(context, appKey, currentUserId);
        try {
            rongUserCacheDatabaseHelper = new RongUserCacheDatabaseHelper(context);
            db = rongUserCacheDatabaseHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            RLog.e(TAG, "SQLiteException occur");
            e.printStackTrace();
        }
    }

    void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (db != null) {
            db.close();
        }
        super.finalize();
    }

    UserInfo getUserInfo(final String userId) {
        if (userId == null) {
            RLog.w(TAG, "getUserInfo userId is invalid");
            return null;
        }
        if (db == null) {
            RLog.w(TAG, "getUserInfo db is invalid");
            return null;
        }

        Cursor c = db.query(usersTable, null, "id = ?", new String[] {userId}, null, null, null);
        UserInfo info = null;
        if (c != null) {
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndex("id"));
                String name = c.getString(c.getColumnIndex("name"));
                String portrait = c.getString(c.getColumnIndex("portrait"));
                info = new UserInfo(id, name, Uri.parse(portrait));
            }
            c.close();
        }
        return info;
    }

    synchronized void insertUserInfo (UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            RLog.w(TAG, "insertUserInfo userId is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "insertUserInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", userInfo.getUserId());
        cv.put("name", userInfo.getName());
        cv.put("portrait", userInfo.getPortraitUri() + "");
        db.insert(usersTable, null, cv);
    }

    synchronized void updateUserInfo (UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            RLog.w(TAG, "updateUserInfo userId is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "updateUserInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", userInfo.getUserId());
        cv.put("name", userInfo.getName());
        cv.put("portrait", userInfo.getPortraitUri() + "");
        db.update(usersTable, cv, "id = ?", new String[] {userInfo.getUserId()});
    }

    synchronized void putUserInfo (UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            RLog.w(TAG, "putUserInfo userId is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "putUserInfo db is invalid");
            return;
        }

        db.execSQL("replace into users (id, name, portrait) values (?, ?, ?)", new String[] {userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri() + ""});
    }

    GroupUserInfo getGroupUserInfo(final String groupId, final String userId) {
        if (userId == null || groupId == null) {
            RLog.w(TAG, "getGroupUserInfo parameter is invalid");
            return null;
        }
        if (db == null) {
            RLog.w(TAG, "getGroupUserInfo db is invalid");
            return null;
        }

        Cursor c = db.query(groupUsersTable, null, "group_id = ? and user_id = ?", new String[] {groupId, userId}, null, null, null);
        GroupUserInfo info = null;
        if (c != null) {
            if (c.moveToFirst()) {
                String gId = c.getString(c.getColumnIndex("group_id"));
                String uId = c.getString(c.getColumnIndex("user_id"));
                String nickname = c.getString(c.getColumnIndex("nickname"));
                info = new GroupUserInfo(gId, uId, nickname);
            }
            c.close();
        }
        return info;
    }

    synchronized void insertGroupUserInfo (GroupUserInfo userInfo) {
        if (userInfo == null || userInfo.getGroupId() == null || userInfo.getUserId() == null) {
            RLog.w(TAG, "insertGroupUserInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "insertGroupUserInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("group_id", userInfo.getGroupId());
        cv.put("user_id", userInfo.getUserId());
        cv.put("nickname", userInfo.getNickname());
        db.insert(groupUsersTable, null, cv);
    }

    synchronized void updateGroupUserInfo (GroupUserInfo userInfo) {
        if (userInfo == null || userInfo.getGroupId() == null || userInfo.getUserId() == null) {
            RLog.w(TAG, "updateGroupUserInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "updateGroupUserInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("group_id", userInfo.getGroupId());
        cv.put("user_id", userInfo.getUserId());
        cv.put("nickname", userInfo.getNickname());
        db.update(groupUsersTable, cv, "group_id=? and user_id=?", new String[] {userInfo.getGroupId(), userInfo.getUserId()});
    }

    synchronized void putGroupUserInfo (GroupUserInfo userInfo) {
        if (userInfo == null || userInfo.getGroupId() == null || userInfo.getUserId() == null) {
            RLog.w(TAG, "putGroupUserInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "putGroupUserInfo db is invalid");
            return;
        }

        db.execSQL("delete from group_users where group_id=? and user_id=?", new String[] {userInfo.getGroupId(), userInfo.getUserId()});
        db.execSQL("insert into group_users (group_id, user_id, nickname) values (?, ?, ?)", new String[] {userInfo.getGroupId(), userInfo.getUserId(), userInfo.getNickname()});
    }

    Group getGroupInfo(final String groupId) {
        if (groupId == null) {
            RLog.w(TAG, "getGroupInfo parameter is invalid");
            return null;
        }
        if (db == null) {
            RLog.w(TAG, "getGroupInfo db is invalid");
            return null;
        }

        Cursor c = db.query(groupsTable, null, "id = ?", new String[] {groupId}, null, null, null);
        Group group = null;
        if (c != null) {
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndex("id"));
                String name = c.getString(c.getColumnIndex("name"));
                String portrait = c.getString(c.getColumnIndex("portrait"));
                group = new Group(id, name, Uri.parse(portrait));
            }
            c.close();
        }
        return group;
    }

    synchronized void insertGroupInfo (Group group) {
        if (group == null || group.getId() == null) {
            RLog.w(TAG, "insertGroupInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "insertGroupInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", group.getId());
        cv.put("name", group.getName());
        cv.put("portrait", group.getPortraitUri() + "");
        db.insert(groupsTable, null, cv);
    }

    synchronized void updateGroupInfo (Group group) {
        if (group == null || group.getId() == null) {
            RLog.w(TAG, "updateGroupInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "updateGroupInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", group.getId());
        cv.put("name", group.getName());
        cv.put("portrait", group.getPortraitUri() + "");
        db.update(groupsTable, cv, "id = ?", new String[] {group.getId()});
    }

    synchronized void putGroupInfo (Group group) {
        if (group == null || group.getId() == null) {
            RLog.w(TAG, "putGroupInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "putGroupInfo db is invalid");
            return;
        }

        db.execSQL("replace into groups (id, name, portrait) values (?, ?, ?)", new String[] {group.getId(), group.getName(), group.getPortraitUri() + ""});
    }

    Discussion getDiscussionInfo(final String discussionId) {
        if (discussionId == null) {
            RLog.w(TAG, "getDiscussionInfo parameter is invalid");
            return null;
        }
        if (db == null) {
            RLog.w(TAG, "getDiscussionInfo db is invalid");
            return null;
        }

        Cursor c = db.query(discussionsTable, null, "id = ?", new String[] {discussionId}, null, null, null);
        Discussion discussion = null;
        if (c != null) {
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndex("id"));
                String name = c.getString(c.getColumnIndex("name"));
                //String portrait = c.getString(c.getColumnIndex("portrait"));
                discussion = new Discussion(id, name);
            }
            c.close();
        }

        return discussion;
    }

    synchronized void insertDiscussionInfo (Discussion discussion) {
        if (discussion == null || discussion.getId() == null) {
            RLog.w(TAG, "insertDiscussionInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "insertDiscussionInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", discussion.getId());
        cv.put("name", discussion.getName());
        cv.put("portrait", "");
        db.insert(discussionsTable, null, cv);
    }

    synchronized void updateDiscussionInfo (Discussion discussion) {
        if (discussion == null || discussion.getId() == null) {
            RLog.w(TAG, "updateDiscussionInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "updateDiscussionInfo db is invalid");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", discussion.getId());
        cv.put("name", discussion.getName());
        cv.put("portrait", "");
        db.update(discussionsTable, cv, "id = ?", new String[] {discussion.getId()});
    }

    synchronized void putDiscussionInfo (Discussion discussion) {
        if (discussion == null || discussion.getId() == null) {
            RLog.w(TAG, "putDiscussionInfo parameter is invalid");
            return;
        }
        if (db == null) {
            RLog.w(TAG, "putDiscussionInfo db is invalid");
            return;
        }

        db.execSQL("replace into discussions (id, name, portrait) values (?, ?, ?)", new String[] {discussion.getId(), discussion.getName(), ""});
    }

//    PublicServiceProfile getPublicServiceProfile(final String type, final String targetId) {
//        if (targetId == null)
//            return null;
//
//        Cursor c = db.query(publicServiceProfilesTable, null, "type = ? and id = ?", new String[]{type, targetId}, null, null, null);
//        PublicServiceProfile profile = null;
//        if (c.moveToFirst()) {
//            String tp = c.getString(c.getColumnIndex("type"));
//            Conversation.ConversationType conversationType = null;
//            if (Integer.parseInt(tp) == Conversation.ConversationType.APP_PUBLIC_SERVICE.getValue())
//                conversationType = Conversation.ConversationType.APP_PUBLIC_SERVICE;
//            else if (Integer.parseInt(tp) == Conversation.ConversationType.PUBLIC_SERVICE.getValue())
//                conversationType = Conversation.ConversationType.PUBLIC_SERVICE;
//            String id = c.getString(c.getColumnIndex("id"));
//            String name = c.getString(c.getColumnIndex("name"));
//            String portrait = c.getString(c.getColumnIndex("portrait"));
//
//            profile = new PublicServiceProfile();
//            profile.setPublicServiceType(conversationType);
//            profile.setTargetId(id);
//            profile.setName(name);
//            profile.setPortraitUri(Uri.parse(portrait));
//        }
//        c.close();
//
//        return profile;
//    }
//
//    void insertPublicServiceProfile (PublicServiceProfile profile) {
//        if (profile == null || profile.getConversationType() == null || profile.getTargetId() == null)
//            return;
//
//        ContentValues cv = new ContentValues();
//        cv.put("type", profile.getConversationType().getValue());
//        cv.put("id", profile.getTargetId());
//        cv.put("name", profile.getName());
//        cv.put("portrait", profile.getPortraitUri() + "");
//        db.insert(publicServiceProfilesTable, null, cv);
//    }
//
//    void updatePublicServiceProfile (PublicServiceProfile profile) {
//        if (profile == null || profile.getConversationType() == null || profile.getTargetId() == null)
//            return;
//
//        ContentValues cv = new ContentValues();
//        cv.put("type", profile.getConversationType().getValue());
//        cv.put("id", profile.getTargetId());
//        cv.put("name", profile.getName());
//        cv.put("portrait", profile.getPortraitUri() + "");
//        db.update(publicServiceProfilesTable, cv, "type = ? and id = ?", new String[]{profile.getConversationType().getValue()+"", profile.getTargetId()});
//    }
//
//    void putPublicServiceProfile (PublicServiceProfile profile) {
//        if (profile == null || profile.getConversationType() == null || profile.getTargetId() == null)
//            return;
//
//        db.execSQL("delete from public_service_profiles where type=? and id=?", new String[]{profile.getConversationType().getValue()+"", profile.getTargetId()});
//        db.execSQL("insert into public_service_profiles (type, id, name, portrait) values (?, ?, ?, ?)", new String[]{profile.getConversationType().getValue()+"", profile.getTargetId(), profile.getName(), profile.getPortraitUri()+""});
//    }
}
