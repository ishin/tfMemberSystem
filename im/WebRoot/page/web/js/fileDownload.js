/**
 * Created by zhu_jq on 2017/2/7.
 */
$(function(){

    $('.mesContainer').delegate('.sendStatus','click',function(){
        var _this = $(this);
        var targetId = _this.closest('.mesContainer').attr('targetid');
        var targetType = _this.closest('.mesContainer').attr('targettype');
        var sOldTimer= _this.attr('data-t');
        new Window().alert({
            title   : '重新发送',
            content : '确定要重新发送么？',
            hasCloseBtn : true,
            hasImg : true,
            textForSureBtn : '确定',              //确定按钮
            textForcancleBtn : '取消',            //取消按钮
            handlerForCancle : null,
            handlerForSure : function(){
                var sType=_this.attr('data-type');
                switch (sType){
                    case 'textMessage':
                        var content= $('li[uniqueTime='+sOldTimer+'] .mr-ownChat .sendStatus')[0].content;
                        sendMsg(content,targetId,targetType,'','',new Date().getTime());
                        break;
                    case 'imgMessage':
                        var sImgSrc=_this.attr('data-ImgU');
                        var content='<img src="'+sImgSrc+'"; class="uploadImgFile"/>';
                        sendMsg(content,targetId,targetType,'','',new Date().getTime());
                        break;
                    case 'uploadFile':
                        var nSendTime=new Date().getTime();
                        var extra='uploadFile';
                        var oFileInfo={};
                        oFileInfo.name=_this.attr('data-name');
                        oFileInfo.size=_this.attr('data-fSize');
                        oFileInfo.type='';
                        oFileInfo.filepaste=1;
                        oFileInfo.fileUrl=_this.attr('data-fUrl');
                        var fileInfo=JSON.stringify(oFileInfo);
                        sendMsg(fileInfo,targetId,targetType,extra,'',nSendTime);
                        sendByRongFile(oFileInfo,targetId,targetType,'',nSendTime);

                }
                //var content = _this.parent().find('.mr-ownChat span').html();
            }
        })
    })
    var _fnDown = function(ev){
        if(window.Electron){
            var url = $(this).attr('href');
            var localPath = window.Electron.chkFileExists(url);
            console.log(localPath);
            if(localPath){//本地有这个文件
                var parentNode = $(this).parent();
                parentNode.find('#fileOperate').remove();
                var sHTML = '<div id="fileOperate" uniquetime="1486626340273">' +
                '<span class="openFile"></span>' +
                '<span class="openFloder"></span>' +
                '</div>'
                parentNode.append($(sHTML));
                return false;
            }
        }
    };
    var _fnDownHos = function(ev){
        if(window.Electron){
            var url = $(this).attr('href');
            var localPath = window.Electron.chkFileExists(url);
            if(localPath){//本地有这个文件
                var $selfEle=$(this);
                var parentNode = $(this).parent().parent();
                parentNode.find('strong').remove();
                var sHTML = '<strong  data-url="'+url+'" class="hosOpenFile">打开</strong>\
                             <strong data-url="'+url+'" class="hosOpenFloder">打开文件夹</strong>';
                parentNode.append($(sHTML));
                return false;
            }
        }
    };

    $('.orgNavClick').delegate('.downLoadFile', 'click', _fnDown);
    $('.infoDet-chatRecord .chatRecordSel').delegate('.downLoadFile', 'click', _fnDown);
    $('.infoDet-flieRecord .chatRecordSel').delegate('.downLoadFile', 'click', _fnDownHos);

    var DownImgFlag = false;
    $('.orgNavClick').undelegate('.uploadImgFile','click')
    $('.orgNavClick').delegate('.uploadImgFile','click',function(){
        if(!DownImgFlag){
            DownImgFlag = true;
            var url = $(this).attr('src');
            if(window.Electron){
                var localPath = window.Electron.chkFileExists(url);
                if(localPath){//本地有这个文件
                    if(url.indexOf('token')!=-1){//有%
                        url = fileFromApp(url);
                    }
                    window.Electron.openFile(url);
                }else{
                    //console.log('本地没有这个文件');
                    window.location.href = url;
                    //window.Electron.openFile(url);
                }
                DownImgFlag = false;
            }
        }
    })

    $('.orgNavClick').undelegate('.openFloder','click');
    $('.orgNavClick').delegate('.openFloder','click',function(){
        if(window.Electron){
            var URL = $(this).parents('li').find('a').attr('href');

            window.Electron.openFileDir(URL);
        }
    })

    $('.orgNavClick').undelegate('.openFile','click');
    $('.orgNavClick').delegate('.openFile','click',function(){
        if(window.Electron){
            var URL = $(this).parents('li').find('a').attr('href');

            if(URL.indexOf('token')!=-1){//有%
                URL = fileFromApp(URL);
            }
            window.Electron.openFile(URL);
        }
    })

    $('.infoDet-chatRecord .chatRecordSel').undelegate('.openFile','click');
    $('.infoDet-chatRecord .chatRecordSel').delegate('.openFile','click',function(){
        if(window.Electron){
            var URL = $(this).parent().prev().attr('href');
            if(URL.indexOf('token')!=-1){//有%
                URL = fileFromApp(URL);
            }
            window.Electron.openFile(URL);
        }
    })
    $('.infoDet-chatRecord .chatRecordSel').undelegate('.openFloder','click');
    $('.infoDet-chatRecord .chatRecordSel').delegate('.openFloder','click',function(){
        if(window.Electron){
            var URL = $(this).parent().prev().attr('href');
            window.Electron.openFileDir(URL);
        }

    })
    $('.infoDet-flieRecord .chatRecordSel').undelegate('.hosOpenFile','click');
    $('.infoDet-flieRecord .chatRecordSel').delegate('.hosOpenFile','click',function(){
        if(window.Electron){
            var URL = $(this).attr('data-url');
            if(URL.indexOf('token%')!=-1){//有%
                URL = fileFromApp(URL);
            }
            window.Electron.openFile(URL);
        }

    })
    $('.infoDet-flieRecord .chatRecordSel').undelegate('.hosOpenFloder','click');
    $('.infoDet-flieRecord .chatRecordSel').delegate('.hosOpenFloder','click',function(){
        if(window.Electron){
            var URL = $(this).attr('data-url');
            window.Electron.openFileDir(URL);
        }

    })
    $('.orgNavClick').undelegate('.voiceMsgContent','click');
    $('.orgNavClick').delegate('.voiceMsgContent','click',function(){
        var _this = $(this);
        var voiceTimer = null;
        //把正在播放的声音停止
        var voicePlaying = $('.voiceMsgContent.playing');
        if(voicePlaying.length!=0){
            var base64Str = voicePlaying.attr('base64str');
            clearTimeout(voiceTimer);
            RongIMLib.RongIMVoice.stop(base64Str);
            voicePlaying.removeClass('playing');
        }
        var base64Str = _this.attr('base64str');
        var duration = base64Str.length/1024;
        _this.parents('li').find('.msgUnread').remove();
        if(!_this.hasClass('playing')){
            RongIMLib.RongIMVoice.play(base64Str,duration);
            _this.addClass('playing');
            voiceTimer = setTimeout(function(){
                _this.removeClass('playing');
            },duration*1000);
        }else{
            clearTimeout(voiceTimer);
            RongIMLib.RongIMVoice.stop(base64Str);
            _this.parent().removeClass('playing');
        }
    })
})

