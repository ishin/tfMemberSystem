package com.tianfangIMS.im;

/**
 * Created by Lmy on 2016/12/29.
 * 常量类。
 */

public class ConstantValue {
    //服务器地址
    public static String Urls = "http://120.26.42.225:8080/im/";
    public static final String AFTERLOGINUSERINFO =Urls + "member!getOneOfMember";//获取登录后用户单独的信息接口
    public static final int SUBSTRINGDATA = 7;
    public static String ImageFile = Urls + "upload/images/";//网络图片路径

    public static final String AFTERLOGIN = Urls + "system!afterLogin";//登录接口

    public static final String DEPARTMENT = Urls + "branch!getBranchTree";//部门接口

    public static final String REQUESTTEXT = Urls + "system!requestText";//发送短信验证码

    public static final String NEWPASSWORD = Urls + "system!newPassword";//修改密码

    public static final String DEPARTMENTPERSON = Urls + "branch!getBranchTreeAndMember";//部门+人员

    public static final String CONTACTSPERSON = Urls + "branch!getBranchMember";//人员

    public static final String SEARCHFRIEND = Urls + "member!searchUser";//查找联系人

    public static final String ADDTOPCONTACTS = Urls + "friend!addFriend";//添加常用联系人

    public static final String GETCONTACTSLIST = Urls + "friend!getMemberFriends";//获取常用联系人列表

    public static final String MINEGROUP = Urls + "group!groupListWithAction";//我的群组接口

    public static final String CREATEGROUP = Urls + "group!createGroup";//创建群聊

    public static final String SYNCUSERGROUP = Urls + "group!syncUserGroup";//同步用户群列表

    public static final String GETALLGROUP = Urls + "group!groupList";//获取所有群组信息

    public static final String GETONEPERSONINFO = Urls +  "member!getOneOfMember";//获取单用户信息

    public static final String GETONEGROUPINFO = Urls +  "group!groupInfo";//获取单群组信息

    public static final String SINGOUTGROUP = Urls +  "group!leftGroup";//退出群组

    public static final String CHANGEGROUPNAME = Urls +  "group!changeGroupName";//修改群组名称

    public static final String SINGOUTUSER = Urls +  "system!logOut";//登出

    public static final String GETALLPERSONINFO = Urls +  "member!getAllMemberInfo";//获取全部人员信息

    public static final String DISSGROUP = Urls +  "group!disslovedGroup";//解散群组

    public static final String ADDGROUPUSRT = Urls +"group!joinGroup";//添加群组成员

    public static final String UPDATEUSERPHOTONOTCUT = Urls +  "upload!uploadUserLogoNotCut";//上传头像非裁剪

    public static final String SUBLOCATION = Urls +"map!subLocation";//上传位置信息

    public static final String GROUPALLUSERINFO = Urls +"group!listGroupMemebersData";//获取群组信息

    public static final String ISFRIEND = Urls +"friend!getFriendsRelation";//判断是否为好友

    public static final String DELTETFRIEND = Urls +"friend!delFriend";//删除好友

    public static final String SREACHGROUPUSER = Urls +"group!listGroupMemebers";//删除好友

    public static final String GETLOCATION = Urls +"map!getLocation";//获取好友或者群组的坐标

    public static final String TRANSFERGROUP = Urls+"group!transferGroup";//转移群主

    public static final String ISSESSION = Urls+"system!attemptSession";//判断session是否失效

    public static final String LOGOUT = Urls+"system!logOut";//登出

    public static final String SCANADDCONTACTS = Urls+"  friend!scanAddFriend";//扫码添加联系人

    public static final String CALLPHONE = Urls+"adm!getTTTPriv";//紧急呼叫权限


}
