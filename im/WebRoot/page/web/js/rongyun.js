/**
 * Created by zhu_jq on 2017/1/9.
 */
$(function(){


    var userid = $('body').attr('userid');
    var token = $('body').attr('token');
    sendAjax('member!getOneOfMember',{userid:userid},function(data){
        window.localStorage.datas=data;
        var datas = JSON.parse(data);
        var changeFormatData = {};
        changeFormatData.text = datas
        if(datas){
            window.localStorage.account=JSON.stringify(changeFormatData);
			if(RongIMLib.VCDataProvider&&window.Electron){
                //RongIMClient.init(globalVar.rongKey,new RongIMLib.VCDataProvider(window.Electron.addon),{navi:globalVar.navi});//私有云适用120
                RongIMClient.init(globalVar.rongKey,new RongIMLib.VCDataProvider(window.Electron.addon));			//公有云   适用本地或35
            }else{
                //RongIMClient.init(globalVar.rongKey,null,{navi:globalVar.navi});		//私有云适用120
                RongIMClient.init(globalVar.rongKey);			//公有云   适用本地或35
            }
            var account = datas.account;
            var accountID = datas.id;
            //获取常用联系人
            getMemberFriends(account);
            //获取左侧组织树状图
            getBranchTreeAndMember();
            //获取会话列表(只能在与服务器连接成功之后调用)
            //getConverList();
            //获取群组列表
            getGroupList(accountID);
            //获取系统提示音
            getSysTipVoice(accountID);
            //鼠标在联系人上悬停

            // 设置连接监听状态 （ status 标识当前连接状态）
            // 连接状态监听器
            RongIMClient.setConnectionStatusListener({
                onChanged: function (status) {
                    switch (status) {
                        //链接成功
                        case RongIMLib.ConnectionStatus.CONNECTED:
                            console.log('链接成功');
                            clearTimeout(globalVar.disconnectTimer);
                            if($('.window_mask')){
                                $('.window_mask').remove()
                            }
                            //显示会话列表
                            getConverList()
                            break;
                        //正在链接
                        case RongIMLib.ConnectionStatus.CONNECTING:

                            console.log('正在链接');
                            break;
                        //重新链接
                        case RongIMLib.ConnectionStatus.DISCONNECTED:
                            globalVar.disconnectTimer = setTimeout(function(){
                                if($('.window_mask').length==0){
                                    new Window().alert({
                                        title   : '',
                                        content : '断开连接！',
                                        hasCloseBtn : false,
                                        hasImg : true,
                                        textForSureBtn : false,
                                        textForcancleBtn : false
                                    });
                                }
                            },1000);


                           // RongIMClient.clearListeners();
                           // RongIMClient._memoryStore.listenerList={};
                            console.log('断开连接');
                            break;
                        //其他设备登录
                        case RongIMLib.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT:
                            if($('.window_mask').length==0){
                                new Window().alert({
                                    title   : '',
                                    content : '其他设备登录！',
                                    hasCloseBtn : false,
                                    hasImg : true,
                                    textForSureBtn : false,
                                    textForcancleBtn : false
                                });
                            }
                            console.log('其他设备登录');
                            break;
                        //网络不可用
                        case RongIMLib.ConnectionStatus.NETWORK_UNAVAILABLE:
                            new Window().alert({
                                title   : '',
                                content : '网络不可用！',
                                hasCloseBtn : false,
                                hasImg : true,
                                textForSureBtn : false,
                                textForcancleBtn : false
                            });
                            console.log('网络不可用');
                            break;
                    }
                }});

            // 消息监听器
            RongIMClient.setOnReceiveMessageListener({
                // 接收到的消息
                onReceived: function (message) {
                    // 判断消息类型
                    switch(message.messageType){
                        case RongIMClient.MessageType.TextMessage:
                            //1.获取系统提示音接口
                            //2.获取单独的群消息设置
                            playSound(message,userid);
                            //reciveInBox(message);
                            break;
                        case 'FileMessage':
                            playSound(message,userid)
                            //reciveInBox(message);
                            break;
                        case RongIMClient.MessageType.VoiceMessage:
                            // 对声音进行预加载
                            // message.content.content 格式为 AMR 格式的 base64 码
                            //RongIMLib.RongIMVoice.preLoaded(base64Str);
                            RongIMLib.RongIMVoice.preLoaded(message.content.content);
                            playSound(message,userid);
                            break;
                        case RongIMClient.MessageType.ImageMessage:
                            // do something...
                            playSound(message,userid);
                            break;
                        case RongIMClient.MessageType.DiscussionNotificationMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.LocationMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.RichContentMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.DiscussionNotificationMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.InformationNotificationMessage:

                            reciveInBox(message);
                            break;
                        case RongIMClient.MessageType.ContactNotificationMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.ProfileNotificationMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.CommandNotificationMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.CommandMessage:
                            // do something...
                            break;
                        case RongIMClient.MessageType.UnknownMessage:
                            // do something...
                            break;
                        default:
                        // 自定义消息
                        // do something...
                            break;
                    }
                }
            });

            // 连接融云服务器。
            RongIMClient.connect(token, {
                onSuccess: function(userId) {
                    console.log('连接成功');
                },
                onTokenIncorrect: function() {
                    new Window().alert({
                        title   : '',
                        content : 'token无效！',
                        hasCloseBtn : false,
                        hasImg : true,
                        textForSureBtn : false,
                        textForcancleBtn : false
                        //,
                        //autoHide:true
                    });
                    console.log('token无效');
                },
                onError:function(errorCode){
                    var info = '';
                    switch (errorCode) {
                        case RongIMLib.ErrorCode.TIMEOUT:
                            info = '超时';
                            break;
                        case RongIMLib.ErrorCode.UNKNOWN_ERROR:
                            info = '未知错误';

                            break;
                        case RongIMLib.ErrorCode.UNACCEPTABLE_PaROTOCOL_VERSION:
                            info = '不可接受的协议版本';

                            break;
                        case RongIMLib.ErrorCode.IDENTIFIER_REJECTED:
                            info = 'appkey不正确';

                            break;
                        case RongIMLib.ErrorCode.SERVER_UNAVAILABLE:
                            info = '服务器不可用';

                            break;
                    }
                    new Window().alert({
                        title   : '',
                        content : info+'！',
                        hasCloseBtn : false,
                        hasImg : true,
                        textForSureBtn : false,
                        textForcancleBtn : false
                        //,
                        //autoHide:true
                    });
                    console.log(errorCode);
                }
            },'');
            //}
            //初始化emoji表情
            initEmoji();
            //初始化声音库
            RongIMLib.RongIMVoice.init();
        }
    })



})


function playSound(message,userid){
    if(globalVar.SYSTEMSOUND){
        if(message.conversationType==3){
            var targetId = message.targetId;
            sendAjax('fun!getNotRecieveMsg',{groupid:targetId,userid:userid},function(data){
                if(data){
                    var datas = JSON.parse(data);
                    if(datas&&datas.code==1&&datas.text==false){
                        console.log(4444);
                    }else{
                        voicePlay();
                    }
                }
            })
        }else{
            voicePlay();
        }
        //1。获取targetID 查询群禁言设置  if(禁言)、、声音不播放

    }
    reciveInBox(message);
}
function voicePlay(){
    var systemSound_recive = document.getElementById('systemSound_recive');
    systemSound_recive.play();
}

function setConverToTop(Type,targetId,$topEle) {
    var conversationtype = RongIMLib.ConversationType[Type]; // 私聊
    var sData=window.localStorage.getItem("datas");
    var oData= JSON.parse(sData);
    var sId=oData.id;
    var nTopType;
    var sTopHas=$topEle.attr('data-top');
    switch(Type){
        case 'GROUP':
            nTopType=1;
            break;
        case 'PRIVATE':
            nTopType=2;
            break;
    }
    if(sTopHas==1){
        sendAjax('fun!cancelMsgTop',{userid:sId,topid:targetId,toptype:nTopType},function(data){
            var oCancelData=JSON.parse(data);
            if(oCancelData.code==1){
                //var nIndex;
                //var aNoTop=[];
                //$('.usualChatListUl li').each(function(index){
                //    if(!$(this).hasClass('top')){
                //        //nIndex=index;
                //        aNoTop.push($(this));
                //    }
                //});
                getConverList();
                //$('.usualChatListUl li').each(function(index){
                //    var targetEle=$(this);
                //    var sTopId=$(this).attr('targetid');
                //    if(sTopId==targetId){
                //        $('.usualChatListUl li').eq(index).remove();
                //        if(aNoTop.length>0){
                //            aNoTop[0].before(targetEle);
                //        }else{
                //            $('.usualChatListUl').append(targetEle);
                //        }
                //        targetEle.removeClass('top');
                //        targetEle.removeClass('active');
                //    }
                //});
            }
        });
    }else{
        sendAjax('fun!setMsgTop',{userid:sId,topid:targetId,toptype:nTopType},function(data){
            var oData=JSON.parse(data);
            if(oData.code==1){
                $('.usualChatListUl li').each(function(index){
                    var targetEle=$(this);
                    var sTopId=$(this).attr('targetid');
                    if(sTopId==targetId){
                        $('.usualChatListUl li').eq(index).remove();
                        $('.usualChatListUl').prepend(targetEle);
                        targetEle.addClass('top');
                    }
                });
            }
        });
    }
}
