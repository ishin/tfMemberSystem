//
//  RongIMListener.h
//  RongIMLib
//
//  Created by xugang on 14/12/23.
//  Copyright (c) 2014年 RongCloud. All rights reserved.
//

#ifndef __RongIMListener
#define __RongIMListener
#import <Foundation/Foundation.h>
#import "BizListener.h"
#import "RCloudBiz.h"
#import "RCStatusDefine.h"
#import "RCCommonDefine.h"
#import "RongIMClient.h"
#import "RCMessage.h"

RCConnectionStatus mapConnectionStatusWithException(int exceptionStatus);

/**
 *  连接回调
 */
class RCConnectAckListener : public ConnectAckListener
{
public:
    
    void (^connectSuccessBlock)(NSString* userId);
    void (^connectErrorBlock)(RCConnectErrorCode status);
    RCConnectAckListener();
    virtual ~RCConnectAckListener();
    /**
     *  操作结果回调函数
     *
     *  @param status 错误码   0-成功 非0-失败
     *  @param userId 描述信息 成功，返回用户id，失败，返回错误描述
     */
    virtual void operationComplete(int status,const char* userId);
};
/**
 *  消息监听
 */
class RCReceiveMessageListener : public MessageListener {
public:
    id<RongIMClientReceiveMessageDelegate> rcReceiveMsgDelegate;
    id originalObject;
    RCReceiveMessageListener();
    virtual ~RCReceiveMessageListener(){};
    /**
     *  信息接收回调函数
     *
     *  @param mi    消息记录
     *  @param nLeft 剩余消息数
     */
    virtual void OnReceive(CMessageInfo* mi,int nLeft);
};





class RCSendMessageListener : public PublishAckListener
{
public:
    RCMessage *currentMsg;
    void (^successBlock)(long messageId);
    void (^errorBlock)(RCErrorCode nErrorCode, long messageId);
    RCSendMessageListener();
    
    virtual ~RCSendMessageListener();
    /**
     *  操作结果回调
     *
     *  @param status 操作结果 0-成功 非0-失败
     */
    virtual void operationComplete(int status);
};

class RCUserInfoOutputListener : public UserInfoListener
{
public:
    
    void (^completion)(RCUserInfo* user) = NULL;
    void (^error)(RCErrorCode status) = NULL;
    
    RCUserInfoOutputListener();
    
    virtual ~RCUserInfoOutputListener();
    /**
     *  用户信息回调函数
     *
     *  @param userId       用户id
     *  @param userName     用户名称
     *  @param userPortrait 用户头像
     */
    virtual void OnResponse(const char* userId,const char* userName,const char* userPortrait);
    /**
     *  错误信息回调函数
     *
     *  @param status 错误码
     */
    virtual void OnError(int status);
};


/**
 * RCDiscussionInfoListener
 */
class RCDiscussionInfoListener : public DiscussionInfoListener
{
public:
    
    void (^successBlock)(RCDiscussion *discussion);
    void (^errorBlock)(RCErrorCode status);
    
    RCDiscussionInfoListener();
    virtual ~RCDiscussionInfoListener();
    
    /**
     *  信息接收回调函数
     *
     *  @param di    讨论组信息
     *  @param count 数量
     */
    virtual void OnReceive(CDiscussionInfo *di,int count);
    
    /**
     *  操作结果回调
     *
     *  @param status 错误码
     */
    virtual void OnError(int status);

};

/**
 * 讨论组重命名
 */
class RCOperationAckListener :public PublishAckListener{
public:
    void (^successBlock)();
    void (^errorBlock)(RCErrorCode status);
    
    RCOperationAckListener();
    virtual ~RCOperationAckListener();
    
    virtual void operationComplete(int status);
};

class RCCreateDiscussionListener : public CreateDiscussionListener
{
public:
    void (^createDiscussionCompletionBlock)(RCDiscussion* discussInfo) = NULL;
    void (^createDiscussionErrorBlock)(RCErrorCode status) = NULL;
    
    virtual ~RCCreateDiscussionListener();
    
    /**
     *  创建成功回调函数
     *
     *  @param discussionId 讨论组id
     */
    virtual void OnSuccess(const char* discussionId);
    /**
     *  创建失败回调函数
     *
     *  @param status 错误码
     */
    virtual void OnError(int status);
    
};


//----讨论组邀请、退出、T人回调
class RCOperateDiscussionListener : public PublishAckListener
{
public:
    NSString* discussionId;
    
    int methodTag; //多个方法公用，添加一个方法标记判断，100-邀请；101-踢人；102-退出。
    
    void (^discussionSuccessBlock)(RCDiscussion* discussion) = NULL;
    void (^discussionErrorBlock)(RCErrorCode status) = NULL;
    
    RCOperateDiscussionListener();
    virtual ~RCOperateDiscussionListener();
    virtual void operationComplete(int status);
    
};



class RCUploadFileListener :  public ImageListener
{
public:
    RCMessage* currentMessage;
    void(^progressBlock)(int nProgress,long messageId)=NULL;
    void(^successBlock)(RCMessageContent *content,long messageId)=NULL;
    void(^errorBlock)(RCErrorCode errorCode)=NULL;
    
    
    virtual ~RCUploadFileListener();
    /**
     *  进度
     *
     *  @param nProgress 进度值
     */
    virtual void OnProgress(int nProgress);
    /**
     *  操作结果回调
     *
     *  @param nErrorCode     错误码 0 - 成功 非0 - 失败
     *  @param pszDescription 信息描述 成功，返回图片uri 失败 返回错误描述
     */
    virtual void OnError(int nErrorCode, const char* pszDescription);
    
};

//文件下载回调
class RCDownFileListener : public ImageListener
{
    
public:
    void(^progressBlock)(int nProgress)=NULL;
    void(^successBlock)(NSString* mediaPath)=NULL;
    void(^errorBlock)(RCErrorCode errorCode)=NULL;
    
    
    virtual ~RCDownFileListener();
    /**
     *  进度
     *
     *  @param nProgress 进度值
     */
    virtual void OnProgress(int nProgress);
    /**
     *  操作结果回调
     *
     *  @param nErrorCode     错误码 0 - 成功 非0 - 失败
     *  @param pszDescription 信息描述 成功，返回图片uri 失败 返回错误描述
     */
    virtual void OnError(int nErrorCode, const char* pszDescription);
    
};


/**
 * RCStatusAckListener
 */
class RCStatusAckListener :public BizAckListener{
public:
    void (^successBlock)(RCConversationNotificationStatus nStatus);
    void (^errorBlock)(RCErrorCode status);
    
    RCStatusAckListener();
    virtual ~RCStatusAckListener();
    virtual void operationComplete(int opStatus,int bizStatus);
    
};

/**
 * RCSetInviteStatusListener
 */
class RCSetInviteStatusListener :public PublishAckListener{
public:
    void (^successBlock)();
    void (^errorBlock)(RCErrorCode status);
    NSString *discussionId;
    
    RCSetInviteStatusListener();
    virtual ~RCSetInviteStatusListener();
    virtual void operationComplete(int status);
};

/**
 * RCGroupOperationListener
 */
class RCGroupOperationListener : public PublishAckListener
{
public:
    void (^successBlock)();
    void (^errorBlock)(RCErrorCode status);
    
    RCGroupOperationListener();
    virtual ~RCGroupOperationListener();
    
    virtual void operationComplete(int status);
};

/**
 * RCChatRoomOperationListener
 */
class RCChatRoomOperationListener : public PublishAckListener
{
    
public:
    void (^successBlock)();
    void (^errorBlock)(RCErrorCode errorCode);
    RCChatRoomOperationListener();
    virtual ~RCChatRoomOperationListener();
    
    virtual void operationComplete(int status);
};


class RCEnvironmentChangeNotifyListener : public EnvironmentChangeNotifyListener
{
public:
    RCEnvironmentChangeNotifyListener();
    void (^EnvChangedBlock)(int nState, NSString* description) = NULL;
    virtual ~RCEnvironmentChangeNotifyListener();
    /**
     *  环境改变，底层处理后的回调
     *
     *  @param nType 类型
     *  @param pData 附带数据
     */
    virtual void Complete(int nType, char* pData);

};


class RCConnectionStatusChangeListener:public ExceptionListener
{
public:
    id<RCConnectionStatusChangeDelegate> rcConnectionChangeDelegate;
    virtual ~RCConnectionStatusChangeListener(){}
    RCConnectionStatusChangeListener();
    virtual void OnError(int status,const char* exceptionDescription);

};




#endif