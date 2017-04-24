/**
 * Created by zhu_jq on 2017/1/12.
 */
$(function(){


    $('.mesContainer').delegate('.clearfix','mouseenter',function(){
        $(this).find('.sendSuccess').show();
    })
    $('.mesContainer').delegate('.clearfix','mouseleave',function(){
        $(this).find('.sendSuccess').hide();
    })


    function placeCaretAtEnd(el) {
        el.focus();
        if (typeof window.getSelection != "undefined" && typeof document.createRange != "undefined") {
            var range = document.createRange();
            range.selectNodeContents(el);
            range.collapse(false);
            var sel = window.getSelection();
            sel.removeAllRanges();
            sel.addRange(range);
        } else if (typeof document.body.createTextRange != "undefined") {
            var textRange = document.body.createTextRange();
            textRange.moveToElementText(el);
            textRange.collapse(false);
            textRange.select();
        }
    }

    //回车发送消息
    $('.textarea').off('focus');
    $('.textarea').focus(function(){
        var down=0;
        var _this = $(this);
        var jsThis = this;

        _this.off('keydown');
        _this.on('keydown',function(){
            if(event.keyCode==17)
            {
                down=1;      //ctrl按下
            }
            if (event.which == 13) {

                if(down==1)//ctrl+enter
                {
                    var oldContent = _this.html();
                    var newContent = oldContent+'<div><br><div >';
                    _this.html(newContent);
                    placeCaretAtEnd(jsThis)
                    down=0;
                }else//enter
                {
                    event.cancelBubble=true;
                    event.preventDefault();
                    event.stopPropagation();
                    sendMsgBoxMsg(_this);
                }
            }
        })
    })

    //点击会话标题上的地图跳到定位
    $('.mr-Location').click(function(){
        var $parentNode = $(this).parents('.mesContainer');
        var targetType = $parentNode.attr('targettype');
        var targetId = $parentNode.attr('targetid');
        if(targetType=='PRIVATE'){
            $('.chatMenu li')[1].click();
            $('.usualChatList').find('li[targetid='+targetId+']').dblclick();
        }else if(targetType=='GROUP'){
            $('.chatMenu li')[1].click();
            $('.groupChatList').find('li[targetid='+targetId+']').dblclick();
        }
    })

})

//包括单聊，群聊，聊天室
function sendMsg(content,targetId,way,extra,callback,uniqueTime){
    //发出去的消息 先显示到盒子里,
    //权限有没有
    console.log(content);
    var limit = $('body').attr('limit');
    if(limit.indexOf('ltszqzlt')==-1&&way== 'GROUP'){//没有权限
        var sGroupConverLisit = '<p class="converLimit">!</p>';
        new Window().alert({
            title   : '',
            content : '您无群组聊天权限！',
            hasCloseBtn : false,
            hasImg : true,
            textForSureBtn : false,
            textForcancleBtn : false,
            autoHide:true
        });
    }else{
        var sGroupConverLisit = ''
    }
    var nSendTime=uniqueTime;
    if(extra=='uploadFile'){//如果是上传文件
        //var content = JSON.parse(content);
        var sendMsg = JSON.parse(content);
        nSendTime=uniqueTime || sendMsg.uniqueTime;
        var uniqueTime = sendMsg.uniqueTime;
        var sFilePaste=sendMsg.filepaste;
        var Msize = KBtoM(sendMsg.size);
        if(sendMsg.type!='image/png'&&sendMsg.type!='image/jpeg'&& sendMsg.type!='ImageMessage'){
            var imgSrc = imgType(sendMsg.type);
            var file = sendMsg.name.split('.')[0];
            //var str = RongIMLib.RongIMEmoji.symbolToHTML('成功发送文件');
            if(sFilePaste==1){
                var fileOperate='';
                var downLoadFile=''
                var sHTML = '<li class="mr-chatContentRFile clearfix" uniqueTime="'+nSendTime+'">'+
                    '<div class="mr-ownChat">'+
                    '<div class="file_type fl"><img src="'+imgSrc+'"></div>'+
                    '<div class="file_content fl">' +
                    '<p class="p1 file_name" data-type="'+sendMsg.type+'">'+sendMsg.name+'</p>' +
                    '<p class="p2 file_size" data-s="'+sendMsg.size+'">'+Msize+'</p>' +
                    '<em class="infoLoading"  infoTime="'+nSendTime+'"></em></div>';
                if(window.Electron) {
                    var localPath = sendMsg.fileUrl?window.Electron.chkFileExists(sendMsg.fileUrl):'';
                    if (localPath) {
                        fileOperate = '<div id="fileOperate">' +
                        '<span class="openFile"></span>' +
                        '<span class="openFloder"></span>' +
                        '</div>'
                        downLoadFile = '<a fileName="' + uniqueTime + '"  class="downLoadFile" href="' + sendMsg.fileUrl + '" style="visibility:hidden;"></a>' ;
                    } else {
                        downLoadFile = '<a fileName="' + uniqueTime + '"  class="downLoadFile" href="' + sendMsg.fileUrl + '"></a>' ;
                    }
                }
                sHTML+=fileOperate+downLoadFile+ '<em class="infoLoading"  infoTime="'+nSendTime+'"></em></div>' +
                '</li>';
                //'<a fileName="'+uniqueTime+'" class="downLoadFile" href="'+sendMsg.fileUrl+'"></a>' +
            }else{
                var sHTML = '<li class="mr-chatContentRFile clearfix" uniqueTime="'+nSendTime+'">'+
                    '<div class="mr-ownChat">'+
                    '<div class="file_type fl"><img src="'+imgSrc+'"/></div>'+
                    '<div class="file_content fl">' +
                    '<p class="p1 file_name" data-type="'+sendMsg.type+'">'+sendMsg.name+'</p>' +
                    '<p class="p2 file_size" data-s="'+sendMsg.size+'">'+Msize+'</p>' +
                    '<div id="up_process" uniqueTime="'+uniqueTime+'">' +
                    '<div id="up_precent" uniqueTime="'+uniqueTime+'">' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '<a fileName="'+uniqueTime+'" class="downLoadFile" href="'+sendMsg.fileUrl+'"></a>' +
                    '<em class="infoLoading"  infoTime="'+nSendTime+'"></em></div>'+
                    '</li>';
            }
        }else{//上传的是图片类型的文件
            var sImgSrc=sendMsg.fileUrl || globalVar.cssImgSrc+'imgLoading.gif';
            var sHTML = '<li class="mr-chatContentR clearfix" uniqueTime="'+nSendTime+'">'+
                '<div class=" mr-ownImg"><img uniqueTime="'+uniqueTime+'" src="'+sImgSrc+'" class="uploadImg uploadImgFile" data-type="'+sendMsg.type+'">'+
                '<em class="infoLoading"  infoTime="'+nSendTime+'"></em></div></li>';
        }
    }else{//如果是普通消息
        var str = RongIMLib.RongIMEmoji.symbolToHTML(content);
        var sHTML = '<li class="mr-chatContentR clearfix" uniqueTime="'+uniqueTime+'">'+
            '<div class="mr-ownChat">'+
            '<span name="'+content+'">' + str + '</span>'+
            '<i></i><em class="infoLoading"  infoTime="'+nSendTime+'"></em>'+
            '</div>'+
            sGroupConverLisit+
            '</li>';
    }
    if(way=='PRIVATE'){
        var parent = $('.mesContainerSelf');
        var parentNode = $('.mesContainerSelf .mr-chatview .mr-chatContent');
        var eDom=document.querySelector('#perContainer .mr-chatview');

    }else{
        var parent = $('.mesContainerGroup');
        var parentNode = $('.mesContainerGroup .mr-chatview .mr-chatContent');
        var eDom=document.querySelector('#groupContainer .mr-chatview');
    }
    //将消息放入盒子
    parentNode.append($(sHTML));
    //滚动条滚动到最低
    if(eDom){
        $('.uploadImgFile').on('load',function(){
            eDom.scrollTop = eDom.scrollHeight;
        })
        eDom.scrollTop = eDom.scrollHeight;
    }

    //写消息区域清空
    callback&&callback();
    //调用融云的发送文件
    if(extra!='uploadFile'&&(limit.indexOf('ltszwjsc')!=-1||way== 'PRIVATE')){
        //sendByRong(content,targetId,way);
        parent.find('.textarea').empty();
        sendByRong(content,targetId,way,'',uniqueTime);
    }
}


function setRongTimer(uniqueTime){
    if($('.infoLoading[infoTime='+uniqueTime+']').length!=0){
        $('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer=null;
        $('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer=setTimeout(function(){
            $('.infoLoading[infoTime='+uniqueTime+']').addClass('show');
        },1000);
        return true;
    }else{
        return false

    }
}
function clearRongTimer(uniqueTime){
    clearTimeout($('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer);
}
//上传文件
function sendByRongFile(content,targetId,way,extra,uniqueTime){
    //console.log(content);
    var msg = new RongIMLib.FileMessage(content);
    var conversationtype = RongIMLib.ConversationType[way]; // 私聊,其他会话选择相应的消息类型即可。
    var transFlag = null;
    transFlag = setRongTimer(uniqueTime);
    RongIMClient.getInstance().sendMessage(conversationtype, targetId, msg, {
            onSuccess: function (message) {
                if(transFlag){
                    clearRongTimer(uniqueTime)
                }
                //clearTimeout($('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer);
                $('.infoLoading[infoTime='+uniqueTime+']').removeClass('show');
                //message 为发送的消息对象并且包含服务器返回的消息唯一Id和发送消息时间戳
                getConverList();
                var sHTML = '<span class="sendSuccess"></span>';
                $('li[uniqueTime='+uniqueTime+'] .mr-ownChat').parent().append($(sHTML));
                console.log("Send successfully");
            },
            onError: function (errorCode,message) {
                var info = '';
                switch (errorCode) {
                    case RongIMLib.ErrorCode.TIMEOUT:
                        info = '超时';
                        break;
                    case RongIMLib.ErrorCode.UNKNOWN_ERROR:
                        info = '未知错误';
                        break;
                    case RongIMLib.ErrorCode.REJECTED_BY_BLACKLIST:
                        info = '在黑名单中，无法向对方发送消息';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_DISCUSSION:
                        info = '不在讨论组中';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_GROUP:
                        info = '不在群组中';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_CHATROOM:
                        info = '不在聊天室中';
                        break;
                    default :
                        info = x;
                        break;
                }
                clearRongTimer(uniqueTime);
                //clearTimeout($('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer);
                $('.infoLoading[infoTime='+uniqueTime+']').removeClass('show');
                var eNode = $('<span class="sendStatus" data-type="uploadFile" data-t="'+uniqueTime+'" data-fUrl="'+content.fileUrl+'" data-fName="'+content.filename+'" data-name="'+content.filename+'" data-fSize="'+content.size+'">!</span>');
                $('li[uniqueTime='+uniqueTime+'] .mr-ownChat').append(eNode);
                console.log('发送失败:' + info);
            }
        }
    );
}

//上传文件为图片类型
function sendByRongImg(content,targetId,way,uniqueTime){
    var conversationtype = RongIMLib.ConversationType[way]; // 私聊,其他会话选择相应的消息类型即可。
    var sImgUrl=content.imageUri;
    var sType=content.type;
    var msg = new RongIMLib.ImageMessage(content);
    var transFlag = null;
    transFlag = setRongTimer(uniqueTime);

    RongIMClient.getInstance().sendMessage(conversationtype, targetId, msg, {
        onSuccess: function (message) {
            if(transFlag){
                clearRongTimer(uniqueTime)
            }

            $('.infoLoading[infoTime='+uniqueTime+']').removeClass('show');
            //message 为发送的消息对象并且包含服务器返回的消息唯一Id和发送消息时间戳
            console.log("Send successfully");
            getConverList();
            var sHTML = '<span class="sendSuccess"></span>';
            $('li[uniquetime='+uniqueTime+'] .mr-ownImg').parent().append($(sHTML));
        },
        onError: function (errorCode,message) {
            var info = '';
            switch (errorCode) {
                case RongIMLib.ErrorCode.TIMEOUT:
                    info = '超时';
                    break;
                case RongIMLib.ErrorCode.UNKNOWN_ERROR:
                    info = '未知错误';
                    break;
                case RongIMLib.ErrorCode.REJECTED_BY_BLACKLIST:
                    info = '在黑名单中，无法向对方发送消息';
                    break;
                case RongIMLib.ErrorCode.NOT_IN_DISCUSSION:
                    info = '不在讨论组中';
                    break;
                case RongIMLib.ErrorCode.NOT_IN_GROUP:
                    info = '不在群组中';
                    break;
                case RongIMLib.ErrorCode.NOT_IN_CHATROOM:
                    info = '不在聊天室中';
                    break;
                default :
                    //info = x;
                    break;
            }
            clearRongTimer(uniqueTime)
            //clearTimeout($('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer);
            $('.infoLoading[infoTime='+uniqueTime+']').removeClass('show');
            var eNode = $('<span class="sendStatus" data-type="imgMessage" data-t="'+uniqueTime+'" data-ImgT="'+sType+'" data-ImgU="'+sImgUrl+'">!</span>');
            $('li[uniqueTime='+uniqueTime+'] .mr-ownImg').append(eNode);
            console.log('发送失败:' + info);
        }
    });
}

function sendByRong(content,targetId,way,extra,uniqueTime){
    // 定义消息类型,文字消息使用 RongIMLib.TextMessage
    var transFlag = null;

    transFlag = setRongTimer(uniqueTime);
    var msg = new RongIMLib.TextMessage({content:content,extra:extra});
    //或者使用RongIMLib.TextMessage.obtain 方法.具体使用请参见文档
    var conversationtype = RongIMLib.ConversationType[way]; // 私聊
    var targetId = targetId; // 目标 Id
    RongIMClient.getInstance().sendMessage(conversationtype, targetId, msg, {
        // 发送消息成功
        onSuccess: function (message) {
            if(transFlag){
                clearRongTimer(uniqueTime)
                //clearTimeout($('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer);
                $('.infoLoading[infoTime='+uniqueTime+']').removeClass('show');
            }
            //message 为发送的消息对象并且包含服务器返回的消息唯一Id和发送消息时间戳
            getConverList();
            var sHTML = '<span class="sendSuccess"></span>';
            $('li[uniqueTime='+uniqueTime+'] .mr-ownChat').parent().append($(sHTML));
            //console.log("Send successfully");
        },
        onError: function (errorCode,message) {
            var info = '';
            switch (errorCode) {
                case RongIMLib.ErrorCode.TIMEOUT:
                    info = '超时';
                    break;
                case RongIMLib.ErrorCode.UNKNOWN_ERROR:
                    info = '未知错误';
                    break;
                case RongIMLib.ErrorCode.REJECTED_BY_BLACKLIST:
                    info = '在黑名单中，无法向对方发送消息';
                    break;
                case RongIMLib.ErrorCode.NOT_IN_DISCUSSION:
                    info = '不在讨论组中';
                    break;
                case RongIMLib.ErrorCode.NOT_IN_GROUP:
                    info = '不在群组中';
                    break;
                case RongIMLib.ErrorCode.NOT_IN_CHATROOM:
                    info = '不在聊天室中';
                    break;
                default :
                    info = '已禁言';
                    break;
            }
            if(transFlag){
                clearRongTimer(uniqueTime)
                //clearTimeout($('.infoLoading[infoTime='+uniqueTime+']')[0].sendByRongTimer);
                $('.infoLoading[infoTime='+uniqueTime+']').removeClass('show');
                var eNode = $('<span class="sendStatus" data-type="textMessage" data-t="'+uniqueTime+'">!</span>');
                $('li[uniqueTime='+uniqueTime+'] .mr-ownChat').append(eNode);
                $('li[uniqueTime='+uniqueTime+'] .mr-ownChat .sendStatus')[0].content=content;
                console.log('发送失败:' + info);
            }else{
                console.log('转发发送失败:' + info);
            }
        }
    });
}


function fillGroupPage(targetID,targetType,groupName){
    RongIMClient.getInstance().getHistoryMessages(RongIMLib.ConversationType[targetType], targetID, 0, 20, {
        onSuccess: function(list, hasMsg) {
            if(list.length==0 && !hasMsg){
                $('#groupContainer .mr-chatview').attr('data-on',0);
            }else{
                $('#groupContainer .mr-chatview').attr('data-on',1);
            }
            $('.mesContainerGroup .mr-chatview').remove();
            var sDoM = '<div class="mr-chatview"><ul class="mr-chatContent">';
            sDoM=createConversationList(sDoM,list,targetType);
            sDoM+='</ul></div>';
            $('.orgNavClick').addClass('chatHide');
            $('.mesContainerGroup').removeClass('chatHide');
            $('.mr-record').addClass('active');
            $('.mesContainerGroup').removeClass('mesContainer-translateL');
            //$('#groupContainer .mr-chatview').empty();
            $('#groupContainer .mr-chateditBox').before(sDoM);
            var eDom=document.querySelector('#groupContainer .mr-chatview');
            if(eDom.scrollHeight>$('#groupContainer .mr-chatview').height()){
                if($('#groupContainer .uploadImgFile').length!=0){
                    $('.uploadImgFile').on('load',function(){
                        eDom.scrollTop = eDom.scrollHeight;
                    })
                }
                eDom.scrollTop = eDom.scrollHeight;
            }
            var $container = $('#groupContainer .mr-chatview');
            var eDom=document.querySelector('#groupContainer .mr-chatview');
            if(eDom.scrollHeight>$('#groupContainer .mr-chatview').height()){
                //$container.perfectScrollbar();
                $container.scroll(function(e) {
                    if($container.scrollTop() === 0) {
                        if($container.attr('data-on')==0){
                            return;
                        }
                        var stampTime=parseInt($('#groupContainer .mr-chatview').find('ul li').first().attr('data-t'));
                        RongIMClient.getInstance().getHistoryMessages(RongIMLib.ConversationType[targetType], targetID, stampTime, 20, {
                            onSuccess: function(list, hasMsg) {
                                if(list.length==0 && !hasMsg){
                                    $('#groupContainer .mr-chatview').attr('data-on',0)
                                }
                                var sDoM = '';
                                sDoM=createConversationList(sDoM,list);
                                $('#groupContainer .mr-chatview ul').prepend(sDoM);
                            },
                            onError: function(error) {
                                // APP未开启消息漫游或处理异常
                                // throw new ERROR ......
                            }
                        });
                    }
                });
            }
        },
        onError: function(error) {
            $('.mesContainerGroup .mr-chatview').remove();
            var sDoM = '<div class="mr-chatview"><ul class="mr-chatContent"></ul></div>';
            $('.orgNavClick').addClass('chatHide');
            $('.mesContainerGroup').removeClass('chatHide');
            $('.mr-record').addClass('active');
            $('.mesContainerGroup').removeClass('mesContainer-translateL');
            $('#groupContainer .mr-chateditBox').before(sDoM);
            // APP未开启消息漫游或处理异常
            // throw new ERROR ......
        }
    });
}

function conversationGroup(targetID,targetType,groupName,callback){
    //噗页面
    fillGroupPage(targetID,targetType,groupName)
    //清空消息盒子
    //checkShutUp();
    $('.mesContainerGroup').find('.textarea').html('');
    //换title
    $('.perSetBox-title span').html(groupName);
    //将重要信息放到title的属性上
    $('.mesContainerGroup').attr('targetID',targetID)
    $('.mesContainerGroup').attr('targetType',targetType)
    //页面滚动条
    //点击emoji表情
    $('.rongyun-emoji>span').unbind('click');
    $('.rongyun-emoji>span').on('click',function(){
        var name = $(this).find('span').attr('name');
        $(".mesContainer:not(.chatHide)").find('.textarea').append(name)
    })
    $('.showEmoji').click(function(){

        $('.rongyun-emoji').show();
        $('.rongyun-emoji').blur(function(){
            console.log(1);
        })

    });
    var rimerEmoji = null;
    $('.rongyun-emoji').on('mouseenter',function(){
        clearTimeout(rimerEmoji);
    })
    $('.rongyun-emoji').on('mouseleave',function(){
        rimerEmoji = setTimeout(function(){
            $('.rongyun-emoji').hide();
        },1000)
    })
    //发送消息
    $('.sendMsgBTN').unbind('click')
    $('.sendMsgBTN').click(function(){
        var contentBox = $(this).prev()
        sendMsgBoxMsg(contentBox);
    });


    $('.mr-record').addClass('active');
    $('.mesContainerGroup').removeClass('mesContainer-translateL');
    clearNoReadMsg(targetType,targetID);
    getConverList();
    callback&&callback();
}
function po_Last_Div(obj) {
    if (window.getSelection) {//ie11 10 9 ff safari
        obj.focus(); //解决ff不获取焦点无法定位问题
        var range = window.getSelection();//创建range
        range.selectAllChildren(obj);//range 选择obj下所有子内容
        range.collapseToEnd();//光标移至最后
    }
    else if (document.selection) {//ie10 9 8 7 6 5
        var range = document.selection.createRange();//创建选择对象
        //var range = document.body.createTextRange();
        range.moveToElementText(obj);//range定位到obj
        range.collapse(false);//光标移至最后
        range.select();
    }
}

//发送消息()
function sendMsgBoxMsg(contentBox){
    var content = contentBox.html();
    if(content){
        var targetNode = $(".mesContainer:not(.chatHide)")
        var targetType = targetNode.attr('targetType');
        var targetId = targetNode.attr('targetID');
        if(targetType=='GROUP'){
            var flag = contentBox.attr('contenteditable');
            if(flag=='true'){
                sendMsg(content,targetId,targetType,'','',new Date().getTime())
            }
        }else{
            sendMsg(content,targetId,targetType,'','',new Date().getTime());
        }
    }
}

function createConversationList(sDoM,list,targetType){
    var timestamp = new Date().getTime();//获取当前时间戳
    var sStartTime=0;
    var sCurrentTime = changeTimeFormat(timestamp, 'yh');
    var sCurrentDateTime = changeTimeFormat(timestamp, 'y');
    for (var i = 0; i < list.length; i++) {
        var sSentTime = list[i].sentTime;
        var extra = list[i].messageType || '';
        var sContent = extra=='TextMessage'?list[i].content.content:list[i].content ;
        switch(targetType){
            case 'GROUP':
                var sTargetId = list[i].senderUserId;
                var sData=window.localStorage.getItem("datas");
                var oData= JSON.parse(sData);
                var sId=oData.id;
                if(sId==sTargetId){
                    sTargetId=sId;
                }
                break;
            case 'PRIVATE':
                var sTargetId = list[i].senderUserId;
                break;
        }
        var sDateTime = changeTimeFormat(sSentTime, 'y');
        var sDateHoursTime = changeTimeFormat(sSentTime, 'yh');
        var sHoursTime=changeTimeFormat(sSentTime, 'h');
        if (sDateTime != sCurrentDateTime) {
            sCurrentDateTime = sDateTime;
            sCurrentTime = sDateHoursTime;
            sStartTime=sSentTime;
            var sNowTime = new Date().getTime();//获取当前时间戳
            var sNowCurrentTime = changeTimeFormat(sNowTime, 'y');
            if(sNowCurrentTime == sDateTime){
                sDoM += ' <li data-t="'+sSentTime+'">\
                    <p class="mr-Date">' + sHoursTime + '</p>\
                    </li>';
            }else{
                sDoM += ' <li data-t="'+sSentTime+'">\
                    <p class="mr-Date">' + sCurrentTime + '</p>\
                    </li>';
            }
        } else {
            var sNowTime1 = new Date().getTime();//获取当前时间戳
            var sNowCurrentTime1 = changeTimeFormat(sNowTime1, 'y');
            if (sSentTime - sStartTime >300000) {
                if(sNowCurrentTime1==sDateTime){
                    var sfiveBeforeTime = changeTimeFormat(sSentTime, 'h');
                }else{
                    var sfiveBeforeTime = changeTimeFormat(sSentTime, 'yh');
                }
                sDoM += '<li data-t="'+sSentTime+'">\
                        <p class="mr-Date">' + sfiveBeforeTime + '</p>\
                        </li>';
            }
            sStartTime=sSentTime;
        }
        sDoM=sessionContent(sDoM,sTargetId,sContent,extra,sSentTime,targetType);
    }
    return sDoM;
}
//function ondayTime(sCurrentTime,sContrastTime){
//   // var sDateTime=changeTimeFormat(sContrastTime,'y');
//    //var sDateHoursTime=changeTimeFormat(sContrastTime,'yh');
//}
function sessionContent(sDoM,sTargetId,sContent,extra,sSentTime,targetType){
    var sdata = localStorage.getItem('datas');
    var accountObj = JSON.parse(sdata);
    var accountID = accountObj.id;
    if (sTargetId!=''&&sTargetId !=accountID) {//别人的发的
        var oData=searchFromList(1,sTargetId);
        if(oData){
            var sImg=oData.logo?globalVar.imgSrc+oData.logo:globalVar.defaultLogo;
        }else {
            var sImg=globalVar.defaultLogo;
        }
        switch(extra){
            case "FileMessage":
                var Msize = KBtoM(sContent.size);
                var fileURL = sContent.fileUrl;
                var imgSrc = imgType(sContent.type)
                var file = fileURL?getFileUniqueName(fileURL):'';
                if(fileURL.indexOf('token')!=-1){//有%
                    file = getFileUniqueNameFromApp(fileURL) //文件唯一标识
                }
                var sFileUrl = fileURL;
                var fileOperate = '';
                var downLoadFile = '';
                if(window.Electron) {
                    if(fileURL.indexOf('%')!=-1){//有%
                       var checkURL = fileFromApp(fileURL); //文件的checkbox连接
                        //checkURL = checkURL.split('&')[0];
                    }
                    var localPath = checkURL?window.Electron.chkFileExists(checkURL):'';
                    if (localPath) {
                        fileOperate = '<div id="fileOperate">' +
                        '<span class="openFile"></span>' +
                        '<span class="openFloder"></span>' +
                        '</div>'
                        downLoadFile = '<a fileName="' + file + '"  class="downLoadFile" href="' + sFileUrl + '" style="visibility:hidden;"></a>' ;
                    } else {
                        downLoadFile = '<a fileName="' + file + '"  class="downLoadFile" href="' + sFileUrl + '"></a>' ;
                    }
                }

                sDoM += '<li class="mr-chatContentLFile clearfix" data-t="' + sSentTime + '">' +
                '<img class="headImg" src="' + sImg + '">' +
                '<div class="mr-ownChat">' +
                '<div class="file_type fl"><img  class="fileImg" src="' + imgSrc + '"></div>' +
                '<div class="file_content fl">' +
                '<p class="p1 file_name">' + sContent.name + '</p>' +
                '<p class="p2 file_size" data-s="'+sContent.size+'">' + Msize + '</p>' +
                '</div>' +
                downLoadFile+fileOperate+'</div>'+
                '<span class="sendSuccess"></span>'+
                '</li>';
                break;
            case "ImageMessage":
                var sImgUrl=sContent.imageUri;
                var sImageType='';
                if(sImgUrl){
                    var sImgName=sImgUrl.split('attname=')[1];
                    if(sImgName){
                        var sImgType=sImgName.split('.')[1];
                    }
                    switch (sImgType){
                        case 'jpg':
                            sImageType='image/jpeg';
                            break;
                        case 'png':
                            sImageType='image/png';
                            break;
                    }
                }else{
                    sImageType='ImageMessage'
                }
                sDoM += ' <li class="mr-chatContentL clearfix" data-t="'+sSentTime+'">'+
                '<img class="headImg" src="'+sImg+'">'+
                '<div class="mr-otherImg"><img src="'+sContent.imageUri+'" class="uploadImgLeft uploadImgFile" data-type="'+sImageType+'"></div>'+
                '<span class="sendSuccess"></span>'+
                '</li>';
                break;
            case "InformationNotificationMessage":
                sDoM += '<li data-t="1486971032807"><p class="mr-Date">'+sContent.message+'</p></li>'
                break;
            case "TextMessage":
                var str = RongIMLib.RongIMEmoji.symbolToHTML(sContent);
                sDoM += ' <li class="mr-chatContentL clearfix" data-t="' + sSentTime + '">\
                            <img class="headImg" src="' + sImg + '">\
                            <div class="mr-chatBox">\
                                <span name="'+sContent+'">' + str + '</span>\
                                <i></i>\
                            </div>\
                            <span class="sendSuccess"></span>\
                        </li>';
                break;
            case "VoiceMessage":
                var base64Str = sContent.content;
                var duration = base64Str.length/1024;
                //w:20px~170px  durating:1s~50s
                var curWidth = duration*3+20;
                if(curWidth>170){
                    curWidth = 170;
                }
                RongIMLib.RongIMVoice.preLoaded(base64Str);
                RongIMLib.RongIMVoice.play(base64Str,duration);
                RongIMLib.RongIMVoice.stop(base64Str);


                sDoM += ' <li class="mr-chatContentL clearfix" data-t="">' +
                '<img class="headImg" src="'+sImg+'">'+
                '<div class="mr-chatBox">'+
                '<p class="voiceMsgContent" style="width:'+curWidth+'px" base64Str="'+base64Str+'"></p>'+
                '</div>'+
                '<p class="voiceSecond"><span>'+sContent.duration+'S</span></p>'+
                '</li>';
                break;
        }
    }else {//自己的
        switch(extra){
            case "FileMessage":
                var sendMsg = sContent;
                var Msize = KBtoM(sendMsg.size);
                var imgSrc = imgType(sContent.type)
                var file = getFileUniqueName(sendMsg.fileUrl);
                var fileOperate = '';
                var downLoadFile = '';
                //var downstyle = '';
                var sFileUrl = sendMsg.fileUrl;
                var sURL = sendMsg.fileUrl
                if(window.Electron) {
                    //var sFileUrl = sendMsg.fileUrl
                    if(sendMsg.fileUrl){
                        if(sURL.indexOf('token')!=-1){//有%
                            sURL = fileFromApp(sendMsg.fileUrl);
                        }
                        var localPath = sURL?window.Electron.chkFileExists(sURL):'';
                        if (localPath) {
                            fileOperate = '<div id="fileOperate">' +
                            '<span class="openFile"></span>' +
                            '<span class="openFloder"></span>' +
                            '</div>';
                            downLoadFile = '<a fileName="' + file + '"  class="downLoadFile" href="' + sendMsg.fileUrl + '" style="visibility:hidden;"></a>' ;
                            //downLoadFile = '<a fileName="1111111111111"  class="downLoadFile" href="' + fileURL + '" style="visibility:hidden;"></a>' ;

                        } else {
                            downLoadFile = '<a fileName="'+file+'" class="downLoadFile" href="'+sendMsg.fileUrl+'"></a>' ;
                        }
                    }else{
                        //downLoadFile = '<a fileName="'+file+'" class="downLoadFile" href="'+sendMsg.fileUrl+'"></a>' ;
                    }
                }
                sDoM += '<li class="mr-chatContentRFile clearfix" data-t="'+sSentTime+'">'+
                '<div class="mr-ownChat">'+
                '<div class="file_type fl"><img src="'+imgSrc+'"></div>'+
                '<div class="file_content fl">' +
                '<p class="p1 file_name">'+sendMsg.name+'</p>' +
                '<p class="p2 file_size" data-s="'+sendMsg.size+'">'+Msize+'</p>' +
                '</div>' +
                    //'<a fileName="'+file+'" class="downLoadFile" href="'+sendMsg.fileUrl+'"></a>'+
                fileOperate+downLoadFile+
                '</div>'+
                '<span class="sendSuccess"></span>'+
                '</li>';
                break;
            case "ImageMessage":
                var sImgUrl=sContent.imageUri;
                if(sImgUrl){
                    var sImgName=sImgUrl.split('attname=')[1];
                    var  sImgType=sImgName.split('.')[1];
                    var sImageType='';
                    switch (sImgType){
                        case 'jpg':
                            sImageType='image/jpeg';
                            break;
                        case 'png':
                            sImageType='image/png';
                            break;
                    }
                    sDoM += ' <li class="mr-chatContentR clearfix" data-t="'+sSentTime+'">'+
                    '<div class=" mr-ownImg"><img src="'+sContent.imageUri+'" class="uploadImg uploadImgFile" data-type="'+sImageType+'"></div>'+
                    '<span class="sendSuccess"></span>'+
                    '</li>';
                }
                break;
            case "InformationNotificationMessage":
                sDoM += '<li data-t="1486971032807"><p class="mr-Date">'+sContent.message+'</p></li>'
                break;
            case "TextMessage":
                var str = RongIMLib.RongIMEmoji.symbolToHTML(sContent);
                sDoM += '<li class="mr-chatContentR clearfix" data-t="'+sSentTime+'">\
                            <div class="mr-ownChat">\
                            <span name="'+sContent+'">' + str + '</span>\
                            <i></i>\
                            </div>\
                            <span class="sendSuccess"></span>\
                            </li>';
                break;
        }
    }
    return sDoM;
}

function fillSelfPage(targetID,targetType){
    RongIMClient.getInstance().getHistoryMessages(RongIMLib.ConversationType[targetType], targetID, 0, 20, {
        onSuccess: function(list, hasMsg) {
            if(list.length==0 && !hasMsg){
                $('#perContainer .mr-chatview').attr('data-on',0);
                //$('#description').attr('data-on',0);
            }else{
                $('#perContainer .mr-chatview').attr('data-on',1);
            }
            $('#perContainer').find('.mr-chatview').remove();
            var sDoM = ' <div class="mr-chatview"><ul class="mr-chatContent">';
            sDoM=createConversationList(sDoM,list,targetType);
            sDoM+='</ul></div>';
            //$('#perContainer .mr-chatview').empty();
            $('.orgNavClick').addClass('chatHide');
            $('.mesContainerSelf').removeClass('chatHide');
            $('.mr-record').addClass('active');
            $('#perContainer .mr-chateditBox').before(sDoM);
            var eDom=document.querySelector('#perContainer .mr-chatview');
            eDom.scrollTop = eDom.scrollHeight;
            if($('#perContainer .uploadImgFile').length!=0){
                $('.uploadImgFile').on('load',function(){
                    eDom.scrollTop = eDom.scrollHeight;
                })
            }else{
                eDom.scrollTop = eDom.scrollHeight;
            }
            var $container = $('#perContainer .mr-chatview');
            var eDom=document.querySelector('#perContainer .mr-chatview');
            if(eDom.scrollHeight>$('#perContainer .mr-chatview').height()){
                //$container.perfectScrollbar();
                $container.scroll(function(e) {
                    if($container.scrollTop() === 0) {
                        if($container.attr('data-on')==0){
                            return;
                        }
                        var stampTime=parseInt($('#perContainer .mr-chatview').find('ul li').first().attr('data-t'));
                        RongIMClient.getInstance().getHistoryMessages(RongIMLib.ConversationType[targetType], targetID, stampTime, 20, {
                            onSuccess: function(list, hasMsg) {
                                if(list.length==0 && !hasMsg){
                                    $('#perContainer .mr-chatview').attr('data-on',0)
                                }
                                var sDoM = '';
                                sDoM=createConversationList(sDoM,list,targetType);
                                $('#perContainer .mr-chatview ul').prepend(sDoM);
                            },
                            onError: function(error) {
                                // APP未开启消息漫游或处理异常
                                // throw new ERROR ......
                            }
                        });
                        //$status.text('it reaches the top!');
                    }
                });
            }
        },
        onError: function(error) {
            $('#perContainer').find('.mr-chatview').remove();
            var sDoM = ' <div class="mr-chatview"><ul class="mr-chatContent"></ul></div>';
            $('.orgNavClick').addClass('chatHide');
            $('.mesContainerSelf').removeClass('chatHide');
            $('.mr-record').addClass('active');
            $('#perContainer .mr-chateditBox').before(sDoM);
            // APP未开启消息漫游或处理异常
            // throw new ERROR ......
        }
    });
}
function conversationSelf(targetID,targetType,callback){
    //var target = targetID;
    //噗页面 把targetID放进去
    fillSelfPage(targetID,targetType);
    $('.mesContainerSelf').find('.textarea').html('');
    $('.mesContainerSelf').removeClass('mesContainer-translateL');
    var curTargetList = searchFromList(1,targetID);
    var name =curTargetList?curTargetList.name : '';
    $('.perSetBox-title span').html(name);
    $('.mesContainerSelf').attr('targetID',targetID);
    $('.mesContainerSelf').attr('targetType',targetType);
    $('.message-content').html();
    $('.rongyun-emoji>span').off('click')
    $('.rongyun-emoji>span').on('click',function(){
        $('.textarea b').attr('contenteditable','false');
        var name = $(this).find('span').attr('name');
        //var newEmo = $(this).clone();
        $(".mesContainer:not(.chatHide)").find('.textarea').append(name)
        //var textarea = document.getElementById('message-content')
        //po_Last_Div(textarea);
    })
    $('.showEmoji').click(function(){

        $('.rongyun-emoji').show();
        $('.rongyun-emoji').blur(function(){
            console.log(1);
        })

    });
    var rimerEmoji = null;
    $('.rongyun-emoji').on('mouseenter',function(){
        clearTimeout(rimerEmoji);
    })
    $('.rongyun-emoji').on('mouseleave',function(){
        rimerEmoji = setTimeout(function(){
            $('.rongyun-emoji').hide();
        },1000)
    })
    $('.sendMsgBTN').unbind('click')
    $('.sendMsgBTN').click(function(){
        var contentBox = $(this).prev()
        sendMsgBoxMsg(contentBox);
    })
    //获取右侧的联系人资料聊天记录
    clearNoReadMsg(targetType,targetID);
    getConverList();
    console.log('conversation 857')
    callback&&callback()
}
function getInfoDetails(targetID,targetType,oInfoDetails){
    getPerInfo(oInfoDetails);
}
/*获取群组资料*/
function getGroupDetails(groupId){
    var datas = localStorage.getItem('groupInfo');
    var data = JSON.parse(datas);
    var aText=data.text;
    var sDom='';
    var sdata = localStorage.getItem('datas');
    var accountID = JSON.parse(sdata).id;
    var voiceState = '';
    //消息免打扰
    sendAjax('fun!getNotRecieveMsg',{groupid:groupId,userid:accountID},function(data){
        if(data){
            var datas = JSON.parse(data);
            if(datas&&datas.code==1&&datas.text==false){
                voiceState = datas.code==true?'active':'';
                $('.voiceSet').addClass('active');
            }
        }
    })
    ////<input type="text" value='+sName+' class="groupSetBox-name">\
    //<b>'+sName+'</b>\
    for(var i = 0;i<aText.length;i++){
        if(aText[i].GID==groupId){
            var sName=aText[i].name || '';//群名称
            var sCreatorId=aText[i].mid;//群创建者id
            var sCreatedate=subTimer(aText[i].createdate);//创建时间
            var oCreator=searchFromList(1,sCreatorId);
            var sImg=oCreator.logo?globalVar.imgSrc+oCreator.logo:globalVar.defaultLogo;
            var groupName = sCreatorId==accountID?'<input type="text" value='+sName+' class="groupSetBox-name">':'<b>'+sName+'</b>';
            sDom+='<ul class="groupInfo">\
                <li class="groupInfo-name">\
                <span>群组名称：</span>\
                '+groupName+'\
            </li>\
            <li class="groupInfo-setTime">\
            <span>创建时间：</span>\
            <b>'+sCreatedate+'</b>\
            </li>\
            <li class="groupInfo-Controller">\
            <span>群主/管理员：</span>\
            <img src="'+sImg+'">\
            </li>\
            <li class="groupInfo-disturb">\
            <span>消息免打扰：</span>\
            <p class="voiceSet '+voiceState+'">\
            </p>\
            </li>\
            </ul><div class="groupInfo-memberList"></div>';
            $('#groupData .group-data').empty();
            $('#groupData .group-data').append(sDom);
        }
    }
    getGroupMembersList(groupId);
}
function getGroupMembersList(groupid){
    sendAjax('group!listGroupMemebers',{groupid:groupid},function(data) {
        var oGroupidList = JSON.parse(data);
        var aMember=oGroupidList.text;
        if(aMember){
            var aNewMember=unique3(aMember);
            var smemship = JSON.stringify(aNewMember);
            var sDom='<div class="groupInfo-number clearfix">\
            <span>成员('+aNewMember.length+')</span>\
            <p class="clearfix">\
            <i class="groupInfo-noChat" data-groupid="'+groupid+'" title="禁言"></i>\
            <i class="groupInfo-groupManage" memship="'+smemship+'" title="群成员管理"></i>\
            </p>\
            </div>\
            <ul class="groupInfo-memberAll">';
            if(aNewMember.length>0){
                for(var i=0;i<aNewMember.length;i++){
                    var oCreator=searchFromList(1,aNewMember[i]);
                    if(oCreator){
                        var sMemberName=oCreator.name;
                        var sJob=oCreator.account;
                        var sImg=oCreator.logo?globalVar.imgSrc+oCreator.logo:globalVar.defaultLogo;
                        sDom+=' <li>\
                            <img src="'+sImg+'">\
                            <p>'+sMemberName+'('+sJob+')</p>\
                            </li>';
                    }
                }
            }
            sDom+='</ul>';
            $('#groupData .group-data .groupInfo-memberList').empty();
            $('#groupData .group-data .groupInfo-memberList').append(sDom);
            console.log(oGroupidList);
            //查询群禁言状态
            checkShutUp();
            //sendAjax('group!getShutUpGroupStatus',{groupid:groupid},function(data){
            //    var sdata = localStorage.getItem('datas');
            //    var accountID = JSON.parse(sdata).id;
            //    var groupInfo = groupInfoFromList(groupid);
            //    if(data){
            //        var datas = JSON.parse(data);
            //        if(datas&&datas.code==1&&datas.text==true){
            //            //if(datas&&datas.code==1&&datas.text==true&&accountID!=groupInfo.mid){
            //            $('.groupInfo-noChat').attr('data-chat','1');
            //            if(accountID!=groupInfo.mid) {
            //                $('#groupContainer #message-content').attr('contenteditable', 'false');
            //                $('#groupContainer #message-content').html('群主已开启禁言!');
            //            }
            //
            //        }else if(datas&&datas.code==1&&datas.text==false){
            //            $('.groupInfo-noChat').attr('data-chat','0');
            //
            //            $('#groupContainer #message-content').attr('contenteditable','true');
            //            $('#groupContainer #message-content').attr('placeholder','请输入文字...');
            //        }
            //    }
            //})
        }
    });
}
/**
 *
 * @param oInfoDetails 个人资料
 */
function getPerInfo(oInfoDetails){
    console.log(oInfoDetails);
    var sTargetId = oInfoDetails.id
    //var memberInfoFromList = searchFromList(1,sTargetId);
    var sName=oInfoDetails.name || '';//姓名
    var sLogo=oInfoDetails.logo?  globalVar.imgSrc+oInfoDetails.logo : globalVar.defaultLogo;//头像
    var sMobile=oInfoDetails.telephone || '';//手机
    var sEmail=oInfoDetails.email || '';//邮箱
    var sBranch=oInfoDetails.branchname || '';//部门
    var sJob=oInfoDetails.postitionname || '';//职位
    var sOrg=oInfoDetails.organname || '';//组织conversationType
    var sAddress=oInfoDetails.address || '';//地址
    var sTargetType=oInfoDetails.flag==1?'PRIVATE':'GROUP';//成员类型
    var sDom='\
        <div class="infoDet-personal clearfix">\
    <img src="'+sLogo+'">\
    <div class="infoDet-text">\
    <p>'+sName+'</p>\
    <ul class="clearfix showPersonalInfo showPerCainter" targetid="'+sTargetId+'" targettype="'+sTargetType+'">\
    <li class="sendMsg" title="发起聊天"></li>\
    <li class="checkPosition" title="查看位置"></li>\
    <li class="addConver" title="加入会话"></li>\
    </ul>\
    </div>\
    </div>\
    <ul class="infoDetList clearfix">\
    <li>\
    <span>手机：</span>\
    <b>'+sMobile+'</b>\
    </li>\
    <li>\
    <span>邮箱：</span>\
    <b>'+sEmail+'</b>\
    </li>\
    <li>\
    <span>部门：</span>\
    <b>'+sBranch+'</b>\
    </li>\
    <li>\
    <span>职位：</span>\
    <b>'+sJob+'</b>\
    </li>\
    <li>\
    <span>组织：</span>\
    <b>'+sOrg+'</b>\
    </li>\
    <li>\
    <span>地址：</span>\
    <b>'+sAddress+'</b>\
    </li>\
    </ul>\
    ';
    $('.infoDetails-data').empty();
    $('.infoDetails-data').append(sDom);
}

//获取会话的历史记录
function getChatRecord(aList,sClass){
    var sDom='<ul class="infoDet-contentDet">';
    var sLi='';
    var aInfo=aList;
    $(sClass).empty();
    var aDate=[];
    var defaultDate=0;
    if(aInfo.length>0){
        for(var i=0;i<aInfo.length;i++){
            var sTargetId=aInfo[i].senderUserId;//f发送者id
            var sSentTime=aInfo[i].sentTime;//发送时间
            var sExtra=aInfo[i].messageType;//信息信息类型
            var sContent=aInfo[i].content;
            switch (sExtra){
                case 'FileMessage':
                    var imgSrc = imgType(sContent.type);
                    var Msize = KBtoM(sContent.size);
                    var uniqueTime = sContent.uniqueTime;
                    var fileURL=sContent.fileUrl;
                    var file = fileURL?getFileUniqueName(fileURL):'';
                    if(fileURL.indexOf('token')!=-1){//有%
                        file = getFileUniqueNameFromApp(fileURL) //文件唯一标识
                    }


                    var fileOperate='';
                    var downLoadFile='';
                    var sURL = fileURL;
                    if(window.Electron) {
                        if(sURL.indexOf('token')!=-1){//有%
                            sURL = fileFromApp(sURL);
                        }
                        var localPath = sURL?window.Electron.chkFileExists(sURL):'';
                        if (localPath) {
                            fileOperate = '<div id="fileOperate">' +
                            '<span class="openFile">打开文件</span>' +
                            '<span class="openFloder">打开文件夹</span>' +
                            '</div>'
                            downLoadFile = '<a fileName="' + file + '"  class="downLoadFile" href="' + fileURL + '" style="visibility:hidden;"></a>' ;
                        } else {
                            downLoadFile = '<a fileName="' + file + '"  class="downLoadFile" href="' + fileURL + '"></a>' ;
                        }
                    }
                    sContent= '<div class="downLoadFileInfo clearfix">'+
                    '<div class="file_typeHos fl"><img src="'+imgSrc+'"></div>'+
                    '<div class="file_contentHos fl">' +
                    '<p class="p1 file_nameHos">'+sContent.name+'</p>' +
                    '<p class="p2 file_sizeHos">'+Msize+'</p>' +
                    '<div id="up_process" uniqueTime="'+uniqueTime+'"><div id="up_precent" uniqueTime="'+uniqueTime+'"></div>' +
                    '</div>' +
                    '</div>' +
                    fileOperate+downLoadFile;
                    break;
                case "ImageMessage":
                    var imgURL=sContent.imageUri;
                    sContent='<img src="'+imgURL+'" class="uploadImgFile">';
                    break;
                case "TextMessage":
                    var sTextContent=sContent.content;
                    var  str= RongIMLib.RongIMEmoji.symbolToHTML(sTextContent);
                    sContent='<span><span ></span>'+str+'</span><i></i>';
                    break;
                case "InformationNotificationMessage":
                    continue;
                    break;
                case "VoiceMessage":
                    var base64Str = sContent.content;
                    var duration = base64Str.length/1024;
                    var curWidth = duration*3+20;
                    if(curWidth>170){
                        curWidth = 170;
                    }
                    RongIMLib.RongIMVoice.preLoaded(base64Str);
                    RongIMLib.RongIMVoice.play(base64Str,duration);
                    RongIMLib.RongIMVoice.stop(base64Str);
                    sContent= '<div class="voiceDownLoad">'+
                    '<p class="voiceMsgContent" style="width:'+curWidth+'px" base64Str="'+base64Str+'"></p>'+
                    '</div>'+
                    '<p class="voiceSecond2"><span>'+sContent.duration+'S</span></p>';
                    break;

            }

            var sMessageId=aInfo[i].messageId;//信息id
            var sSentTimeReg=changeTimeFormat(sSentTime,'h');
            var sSentDate=changeTimeFormat(sSentTime,'y');
            //aDate.push(sSentDate);
            if(sSentDate != defaultDate){
                sLi+='<li >\
                    <p class="infoDet-timeRecord ">'+sSentDate+'</p>\
                </li>';
                defaultDate=sSentDate;
            }
            var sdata = localStorage.getItem('datas');
            var oLocData=JSON.parse(sdata);
            var accountID = oLocData.id;
            if(sTargetId !=accountID){
                var oThers=searchFromList(1,sTargetId);
                var sName=oThers?oThers.name: '';
                sLi+='<li class="infoDet-OthersSay" data-time="'+sSentTime+'">\
                   <b>'+sName+'&nbsp&nbsp&nbsp'+sSentTimeReg+'</b>\
                <div class="pageHostoryBox clearfix">'+sContent+'</div>\
                </li>';
            }else{
                var sSelfName=oLocData.name;
                sLi+='<li class="infoDet-selfSay" data-time="'+sSentTime+'">\
                   <b>'+sSelfName+'&nbsp&nbsp&nbsp'+sSentTimeReg+'</b>\
                <div class="pageHostoryBox clearfix">'+sContent+'</div>\
                </li>';
            }
        }
        sDom+=sLi+'</ul>';
    }
    $(sClass).append(sDom);
    var eDom=document.querySelector(sClass);
    eDom.scrollTop = eDom.scrollHeight;
}
//获取聊天历史记录
function getFileRecord(aList,sClass){
    var sDom='<ul class="chatFile">';
    var sLi='';
    var aInfo=aList;
    $(sClass).empty();
    var aDate=[];
    var defaultDate=0;
    if(aInfo.length>0) {
        for (var i = 0; i < aInfo.length; i++) {
            var sTargetId = aInfo[i].senderUserId;//f发送者id
            var sSentTime = aInfo[i].sentTime;//发送时间
            var sContent = aInfo[i].content;
            var fileSrc = sContent.fileUrl;
            var sFilrUrl = sContent.fileUrl;
            var file = getFileUniqueName(fileSrc);
            if(fileSrc.indexOf('token')!=-1){//有%
                file = getFileUniqueNameFromApp(fileSrc) //文件唯一标识
            }


            var sSentTimeReg = changeTimeFormat(sSentTime, 'ym');
            var Msize = KBtoM(sContent.size);
            var sFileName = sContent.name;
            var sFileType = sContent.type;//文件类型
            var uniqueTime = sContent.uniqueTime;
            var sdata = localStorage.getItem('datas');
            var oLocData = JSON.parse(sdata);
            var accountID = oLocData.id;
            if (sTargetId != accountID) {
                var oThers = searchFromList(1,sTargetId);
                var sSendfName = oThers ? oThers.name : '';
            } else {
                var sSendfName = oLocData.name;
            }
            if(window.Electron){
                if(fileSrc.indexOf('token')!=-1){//有%
                    fileSrc = fileFromApp(fileSrc);
                }
                var localPath = fileSrc?window.Electron.chkFileExists(fileSrc):'';
                if(localPath){
                    sLi += ' <li class="chatFile-folder">\
                            <i></i>\
                            <p>\
                            <b class="clearfix"><em class="hosFileName">'+sFileName+'</em><em>(' + Msize + ')</em></b>\
                            <span>' + sSentTimeReg + sSendfName + '</span>\
                            </p>\
                            <strong  data-url="'+sFilrUrl+'" class="hosOpenFile">打开</strong>\
                            <strong data-url="'+sFilrUrl+'" class="hosOpenFloder">打开文件夹</strong>\
                            </li>';
                }else{
                    sLi += ' <li class="chatFile-folder">\
                            <i></i>\
                            <p>\
                            <b class="clearfix"><em class="hosFileName">'+sFileName+'</em><em>(' + Msize + ')</em></b>\
                            <span>' + sSentTimeReg + sSendfName + '</span>\
                            </p>\
                            <strong  data-url="'+sFilrUrl+'" class="hosOpenFile"><a fileName="' + file + '"  class="downLoadFile" href="' + sFilrUrl + '"></a></strong>\
                            </li>';
                }
            }
        }

        sDom += sLi + '</ul>';
    }
    $(sClass).append(sDom);
    var eDom=document.querySelector(sClass);
    eDom.scrollTop = eDom.scrollHeight;
}

//滚动条滚动到底
function scrollTop(eDom){
    eDom.scrollTop = eDom.scrollHeight;
}

//不同文件类型返回不同图片
function imgType(type){
    switch (type){
        case 'image/png':
            var imgSrc = 'page/web/css/img/formatImg.jpg';
            break;
        case 'image/jpeg':
            var imgSrc = 'page/web/css/img/formatImg.jpg';
            break;
        default :
            var imgSrc = 'page/web/css/img/formatUnknew.jpg';
    }
    return imgSrc;
}

//显示会话列表
function getConverList(){
    RongIMClient.getInstance().getConversationList({
        onSuccess: function(list) {
            usualChatList(list);
        },
        onError: function(error) {
            console.log('同步会话列表ERROR');
        }
    },null);
}

/*
 *
 * 改变时间格式
 * mSec 毫秒数，format需要返回的时间格式
 *
 * */
function changeTimeFormat(mSec,format){
    //var oldTime = (new Date("2012/12/25 20:11:11")).getTime(); //得到毫秒数
    var time;
    var newTime = new Date(mSec); //就得到普通的时间了
    var y=ifPlusZero(newTime.getFullYear());
    var month=ifPlusZero(newTime.getMonth()+1);
    var d=ifPlusZero(newTime.getDate());
    var w=ifPlusZero(newTime.getDay());
    var h = ifPlusZero(newTime.getHours()); //获取系统时，
    var m = ifPlusZero(newTime.getMinutes()); //分
    var s = ifPlusZero(newTime.getSeconds()); //秒
    switch(format){
        case 'y':
            time = y+'-'+month+'-'+d;
            break;
        case 'h':
            time = h+':'+m+':'+s;
            break;
        case 'yh':
            time=y+'-'+month+'-'+d+' '+h+':'+m+':'+s;
            break;
        case 'ym':
            time=y+'-'+month+'-'+d+' '+h+':'+m;
            break;
    }
    return time;
}
function ifPlusZero(num){
    if(num<10){
        num = '0'+num;
    }
    return num;
}

//获取数据中的成员数量
function findMemberCount(targetId){
    var memberCount = 0;
    var normalInfo = localStorage.getItem('normalInfo');
    if(normalInfo){
        var aNormalInfo = JSON.parse(normalInfo);
        for(var i = 0;i<aNormalInfo.length;i++){
            var curInfo = aNormalInfo[i];
            if(curInfo.flag==1){
                memberCount++;
            }
        }
    }
    return memberCount;
}

//显示会话列表
function usualChatList(list){
    var sHTML = '';
    var sData=window.localStorage.getItem("datas");
    var oData= JSON.parse(sData);
    var sId=oData.id;


    var sdata = localStorage.getItem('datas');
    var oData=JSON.parse(sdata);
    var accountID = oData?oData.id :'';
    var createTopList = function(){
        sendAjax('fun!getMsgTop',{userid:sId},function(data){
            var oData=JSON.parse(data);
            var aText=oData.text;
            $('.usualChatListUl').empty();
            if(oData.code==1){
                var aTopList=[];
                for(var i=0;i<aText.length;i++){
                    var sTopType=aText[i].type;
                    var nTopId=aText[i].topId;
                    for(var j=0;j<list.length;j++){
                        if(nTopId==list[j].targetId){
                            var sTopList=list[j];
                            list.splice(j,1);
                            j--;
                            aTopList.unshift(sTopList);
                        }
                    }
                }
                sHTML+=creatTopList(sHTML,aTopList,true);
                sHTML=creatTopList(sHTML,list,false);
                $('.usualChatListUl').html(sHTML);
            }else{
                sHTML=creatTopList(sHTML,list,false);
                $('.usualChatListUl').html(sHTML);
            }
        });
    }
    //createTopList();
    refreshGroup(accountID,createTopList);



}
//消息置顶
function creatTopList(sHTML,list,bFlg){
    for(var i = 0;i<list.length;i++){
        var curList = list[i];
        var conversationType = curList.conversationType
        var content = curList.latestMessage.content.content;
        var extra = curList.latestMessage.messageType;
        var sendTime = curList.sentTime;
        var nowTime = new Date().getTime();

        if(extra=="FileMessage"){
            content="[发送文件]";
        }else if(extra=="ImageMessage"){
            content="[发送图片]";
        }else if(extra=="VoiceMessage"){
            content="[语音]";
        }else if(extra=="InformationNotificationMessage"){
            content="系统消息";
        }else if(extra=="TextMessage"){
            content = content;
        }else{
            content="系统消息";
        }
        var targetId = curList.targetId;
        if(nowTime - sendTime>=globalVar.newsChatListTime){//消息列表的显示时间为最近一月内的消息，超过一月的消息将从消息列表中删除
            conversationType==1?removeConvers('PRIVATE',targetId):removeConvers('GROUP',targetId);
            continue;
        }
        var timeNow = new Date().getTime();
        var deltTime = timeNow-curList.sentTime;
        if(deltTime>=86400000){
            var lastTime = changeTimeFormat(curList.sentTime,'y');
        }else{
            var lastTime = changeTimeFormat(curList.sentTime,'h');
        }
        var unreadMessageCount = curList.unreadMessageCount;
        var sNum = unreadMessageCount==0?'':'<i class="notReadMsg">'+unreadMessageCount+'</i>'
        //changeTimeFormat(mSec,format)
        if(conversationType==1){ //个人聊天
            var member = searchFromList(1,targetId);
            if(member){
                var logo = member.logo?globalVar.imgSrc+member.logo:globalVar.defaultLogo;
                var name = member.name || '';
                if(bFlg){
                    sHTML += ' <li targetid="'+targetId+'" targetType="PRIVATE" class="top">'+
                    '<div><img class="groupImg" src="'+logo+'" alt=""/>'+
                    sNum+
                    '<span class="groupName">'+name+'</span>'+
                    '<span class="usualLastMsg">'+content+'</span>'+
                    '<span class="lastTime">'+lastTime+'</span>'+
                    '</div>'+
                    '</li>'
                }else{
                    sHTML += ' <li targetid="'+targetId+'" targetType="PRIVATE">'+
                    '<div><img class="groupImg" src="'+logo+'" alt=""/>'+
                    sNum+
                    '<span class="groupName">'+name+'</span>'+
                    '<span class="usualLastMsg">'+content+'</span>'+
                    '<span class="lastTime">'+lastTime+'</span>'+
                    '</div>'+
                    '</li>'
                }
            }else{
                removeConvers('PRIVATE',targetId);
            }
        }else if(conversationType==3){
            var curGroup = groupInfo(targetId);
                var curGroup = groupInfo(targetId);
                if(curGroup){
                    if(bFlg){
                        sHTML += ' <li targetid="'+targetId+'" targetType="GROUP" class="top">'+
                        '<div><img class="groupImg" src="'+globalVar.defaultGroupLogo+'" alt=""/>'+
                        sNum+
                        '<span class="groupName">'+curGroup.name+'</span>'+
                        '<span class="usualLastMsg">'+content+'</span>'+
                        '<span class="lastTime">'+lastTime+'</span>'+
                        '</div>'+
                        '</li>'
                    }else{
                        sHTML += ' <li targetid="'+targetId+'" targetType="GROUP">'+
                        '<div><img class="groupImg" src="'+globalVar.defaultGroupLogo+'" alt=""/>'+
                        sNum+
                        '<span class="groupName">'+curGroup.name+'</span>'+
                        '<span class="usualLastMsg">'+content+'</span>'+
                        '<span class="lastTime">'+lastTime+'</span>'+
                        '</div>'+
                        '</li>'
                    }
                }
        }
    }
    return sHTML;
}


function refreshGroup(accountID,callback){
    sendAjax('group!groupList',{userid:accountID},function(data){
        if(data){
            window.localStorage.groupInfo = data;
            var datas = JSON.parse(data);
            callback&&callback();
        }
    })
}
//查询单个群信息
function groupInfo(id){
    var groupInfo = localStorage.getItem('groupInfo');
    var curInfo = '';
    if(groupInfo){
        groupInfo = JSON.parse(groupInfo);
        for(var i = 0;i<groupInfo.text.length;i++){
            if(groupInfo.text[i].GID==id){
                curInfo = groupInfo.text[i]
            }
        }
    }
    return curInfo;
}


//KB转换成M
function KBtoM(kb){
    if(!kb){
        return 0;
    }else{
        return Math.floor(kb/1024 * 100) / 100;
    }
}
//接收到的消息显示在盒子里或者在消息列表中显示
function reciveInBox(msg){
    //打包后的程序收到消息的弹层提示

    var targetID = msg.targetId;
    var messageType = msg.messageType;
    var content = messageType=="TextMessage"?msg.content.content:msg.content;

    var alertMsg = messageType=="TextMessage"?msg.content.content:'发送文件'
    var targetType = msg.conversationType;
    var senderUserId =msg.senderUserId;
    var targetID = msg.targetId;
    var senderUser = searchFromList(1,senderUserId);


    if(senderUser){
        var senderImg = senderUser.logo?globalVar.imgSrc+senderUser.logo:globalVar.defaultLogo;
        var sender = senderUser.name;
    }else{
        var senderImg = globalVar.defaultLogo;
        var sender = '';
    }

    if(window.Electron&&msg.messageType!='InformationNotificationMessage'){
        if(targetType==3){
            var targetGroup = matchGroupList(targetID)
            var sender = targetGroup.name||'';
            window.Electron.displayBalloon(sender,{body:alertMsg})
        }else if(targetType==1){
            var sender = senderUser.name||'';
            window.Electron.displayBalloon(sender,{body:alertMsg})
        }
    }

    if(targetType==3){//群聊 找到各自的消息容器
        var $MesContainer = $('.mesContainerGroup');
        var eDom = document.querySelector('#groupContainer .mr-chatview');
    }else if(targetType==1){//个人聊天
        var $MesContainer = $('.mesContainerSelf');
        var eDom = document.querySelector('#perContainer .mr-chatview');
    }

    if (!$MesContainer.hasClass('chatHide') && $MesContainer.attr('targetID') == targetID) {//如果当前页面正是你要聊的对象
        switch (messageType){
            case "FileMessage":
                var Msize = KBtoM(content.size);
                var fileURL = content.fileUrl;
                var imgSrc = imgType(content.type);
                var file = getFileUniqueName(fileURL);
                if(fileURL.indexOf('token')!=-1){//有%
                    file = getFileUniqueNameFromApp(fileURL) //文件唯一标识
                }
                //var str = RongIMLib.RongIMEmoji.symbolToHTML('成功发送文件');
                var sHTML = '<li class="mr-chatContentLFile clearfix" sentTime="' + msg.sentTime + '">'+
                    '<img class="headImg" src="'+senderImg+'">'+
                    '<div class="mr-chatBox">'+
                    '<div class="file_type fl"><img class="fileImg" src="'+imgSrc+'"></div>'+
                    '<div class="file_content fl">' +
                    '<p class="p1 file_name">'+content.name+'</p>' +
                    '<p class="p2 file_size" data-s="'+content.size+'">'+Msize+'</p>' +
                    '</div>' +
                    '<a fileName="'+file+'" class="downLoadFile" href="'+fileURL+'"></a></div>'+
                    '<span class="sendSuccess"></span>'+
                    '</li>';
                var parentNode = $MesContainer.find('.mr-chatview .mr-chatContent');
                parentNode.append($(sHTML));
                break;
            case "ImageMessage":
                var content = msg.content;
                var fileURL = content.imageUri;
                if(fileURL){
                    var sImgName=fileURL.split('attname=')[1];
                    var sImgType=sImgName.split('.')[1];
                    var sImageType='';
                    switch (sImgType){
                        case 'jpg':
                            sImageType='image/jpeg';
                            break;
                        case 'png':
                            sImageType='image/png';
                            break;
                    }
                }else{
                    sImageType='ImageMessage';
                }
                var file = getFileUniqueName(fileURL);
                var sHTML = ' <li class="mr-chatContentL clearfix" data-t="" sentTime="' + msg.sentTime + '">'+
                    '<img class="headImg" src="'+senderImg+'">'+
                    '<div class="mr-otherImg"><img src="'+content.imageUri+'" class="uploadImgLeft uploadImgFile" data-type="'+sImageType+'"></div>'+
                    '<span class="sendSuccess"></span>'+
                    '</li>';
                var parentNode = $MesContainer.find('.mr-chatview .mr-chatContent');
                parentNode.append($(sHTML));
                //$('.uploadImgFile').on('load',function(){
                //    var eDom=document.querySelector('#perContainer .mr-chatview');
                //    eDom.scrollTop = eDom.scrollHeight;
                //})
                break;
            case "VoiceMessage":
                var base64Str = content.content;
                var duration = base64Str.length/1024;
                //w:20px~170px  durating:1s~50s
                var curWidth = duration*3+20;
                if(curWidth>170){
                    curWidth = 170;
                }
                RongIMLib.RongIMVoice.preLoaded(base64Str);
                RongIMLib.RongIMVoice.play(base64Str,duration);
                RongIMLib.RongIMVoice.stop(base64Str);
                var sHTML = '<li messageUId="' + msg.messageUId + '" sentTime="' + msg.sentTime + '" class="mr-chatContentL clearfix">' +
                    '<img class="headImg" src="'+senderImg+'">'+
                    '<div class="mr-chatBox">'+
                    '<p class="voiceMsgContent" style="width:'+curWidth+'px" base64Str="'+base64Str+'"></p>'+
                    '</div>'+
                    '<p class="voiceSecond"><span class="msgUnread"></span><span>'+content.duration+'S</span></p>'+
                    '</li>';
                var parentNode = $MesContainer.find('.mr-chatview .mr-chatContent');
                parentNode.append($(sHTML));
                break;
            case "TextMessage":
                var str = RongIMLib.RongIMEmoji.symbolToHTML(content);
                var sHTML = '<li messageUId="' + msg.messageUId + '" sentTime="' + msg.sentTime + '" class="mr-chatContentL clearfix">' +
                    '<img class="headImg" src="'+senderImg+'">' +
                    '<div class="mr-chatBox">' +
                    '<span>' + str + '</span>' +
                    '<i></i>' +
                    '</div>' +
                    '<span class="sendSuccess"></span>'+
                    '</li>';
                var parentNode = $MesContainer.find('.mr-chatview .mr-chatContent');
                parentNode.append($(sHTML));
                break;
        }
        if($('#groupContainer .uploadImgFile').length!=0){
            $('.uploadImgFile').on('load',function(){
                eDom.scrollTop = eDom.scrollHeight;
            })
        }
        eDom.scrollTop = eDom.scrollHeight;
        var targetType = targetType == 1?'PRIVATE':'GROUP';
        clearNoReadMsg(targetType,targetID,function(){
            getConverList();
            console.log('conversation 1562')
        });
    }else{
        getConverList();
        console.log('conversation 1566')

    }
}

function matchGroupList(sId){
    var datas = localStorage.getItem('groupInfo');
    var data = JSON.parse(datas);
    var aText=data.text;
    var targetGroup;
    for(var i = 0;i<aText.length;i++){
        if(aText[i].GID==sId){
            targetGroup = aText[i]
            //showGroupMemberInfo(aText[i],pos);
        }
    }
    return targetGroup;
}

//从URL连接中文件唯一标识 时间戳？
function getFileUniqueNameFromApp(fileURL){
    if(fileURL){
        var aURM = fileURL.split('?attname=')[0];
        var fileName = aURM.split('_');
        var UniqueName = fileName[fileName.length-1];

        return UniqueName;
    }else{
        return "";
    }
}

//从URL连接中取得文件名
function getFileUniqueName(fileURL){
    if(fileURL){
        var aURM = fileURL.split('attname=')[1];
        var fileName = aURM.split('.')[0];
        if(fileName.indexOf('&')!=-1){
            fileName = fileName.split('&')[0];
        }
        return fileName;
    }else{
        return "";
    }
}
//清除未读消息数
function clearNoReadMsg(Type,targetId,callback){
    var conversationType = RongIMLib.ConversationType[Type];
    //var targetId = "xxx";
    RongIMClient.getInstance().clearUnreadCount(conversationType,targetId,{
        onSuccess:function(){
            callback&&callback();
            //alert('11111111111');
        },
        onError:function(error){
            // error => 清除未读消息数错误码。
        }
    });
}


function drop(event){
    event.preventDefault();
    console.log('ondrop',event);
}