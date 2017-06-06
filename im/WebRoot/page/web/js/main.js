/**
 * Created by zhu_jq on 2017/1/4.
 */
var ifWinClose = true;
var win;

window.onbeforeunload = function(){
    brforeClose();
}
$(function(){


    $('.chatHeaderOper li')[0].onclick = function(){
        sendAjax('system!logOut','',function(){
            if (window.Electron) {
                RongIMLib.RongIMClient.getInstance().logout();

                var curWindow = window.Electron.remote.getCurrentWindow().reload();
            }else{
                window.location.href = 'system!login';

            }
        });
    }
    $('.chatHeaderOper li')[1].onclick = function(){
        $('.news').addClass('chatHide');
        $('.orgnized').addClass('chatHide');
        $('.personalCenter').find('#backstageMgId li').removeClass('active');
        $('.personalCenter').removeClass('chatHide');
        $('.chatHeaderMenu li').removeClass('active');
        $('#chatBox').children().each(function(){
            if(!$(this).hasClass('chatHide')){
                $(this).addClass('chatHide');
            }
        });
    }
    $('.contactsList').perfectScrollbar();
    $('.dialogClose,.manageCancle').click(function(){
        $('.WindowMask,.WindowMask2').hide();
        $('.contactsSearch').val('');
        $('.contactSearchResult').remove();
    });

    $(document).bind('contextmenu',function(){
        event.preventDefault();
        return false;
    });
    $('.organizationList').delegate('ul li .groupCollspan','click',function(e){
        //按钮样式
        var $groupCollspanO = $(this)
        var bOpen = $groupCollspanO.hasClass('groupCollspanO')
        if(bOpen){
            $groupCollspanO.removeClass('groupCollspanO');
            $groupCollspanO.addClass('groupCollspanC');
        }else{
            $groupCollspanO.removeClass('groupCollspanC');
            $groupCollspanO.addClass('groupCollspanO');
        }
        //内容显示隐藏
        $(this).closest('li').next('ul').slideToggle();
    })

    /*顶部&&左侧导航切换*/
    $('.chatHeaderMenu,.chatMenu').click(function (e) {
        $(e.target).addClass('active')
        $(e.target).siblings('li').removeClass('active');
        var nShowClass = $(e.target).attr('bindPanel');
        switch(nShowClass)
        {
            case 'news':
                //$('.chatBox').html('aaaaa')
                break;
            case 'orgnized':
                //获取左侧组织树状图
                getBranchTreeAndMember();
                console.log('newTree');
                break;
            case 'back':
                //$('.chatBox').html('cccc')
                break;
            case 'newsChatList':
                getConverList();
				console.log('aaaa')
        }
        nShowClass&&showPanel(nShowClass);
    })

    /*展开关闭子级列表*/
    $('.listCtrl').click(function(){
        var $chatLeftIcon = $(this).find('.chatLeftIcon')
        var bOpen = $chatLeftIcon.hasClass('triOpen')
        if(bOpen){
            $chatLeftIcon.removeClass('triOpen');
            $chatLeftIcon.addClass('triClose');
        }else{
            $chatLeftIcon.removeClass('triClose');
            $chatLeftIcon.addClass('triOpen');
        }
        $(this).find('.chatLeftIcon').hasClass('.triOpen')
        $(this).next('ul').slideToggle();
    })
})


function initEmoji(){
    RongIMLib.RongIMEmoji.init();
    var emojis = RongIMLib.RongIMEmoji.emojis;
    //console.log(emojis);
    $('.rongyun-emoji').append($(emojis));
    $('.rongyun-emoji').perfectScrollbar();
}
function brforeClose(){

    if(win){
        win.close();
    }
}
function jumpToBack(fresh){
    const path = 'page/admin/13.jsp';

    if(ifWinClose){
        var origin = window.location.origin;
        if(window.Electron){
            const BrowserWindow = window.Electron.remote.BrowserWindow;
            win = new BrowserWindow({ width: 1000, height: 700 })
            win.on('close', function () { win = null ;ifWinClose = true;});
            win.loadURL(origin+'/im/page/admin/13.jsp');
            win.show();
            ifWinClose = false;
        }else{
            //ifWinClose = false;
            window.open(path,'_black');
        }
    }

}
//memShip表示与此操作相关的人员account
function fshowContexMenu(arr,style,id,memShip,targettype,bTopHas,eTarget){
    var curGroup = groupInfoFromList(memShip);
    var listHTML = '';
    for(var i = 0;i<arr.length;i++){
        var limit = $('body').attr('limit');
        if(arr[i].limit=='znsqz'){
            if(parseInt(curGroup.mid)==parseInt(accountID)){
                listHTML+='<li>'+arr[i].value+'</li>'
            }else{
                listHTML+='<li displaylimit="false">'+arr[i].value+'</li>'
            }
            continue;
        }
        if(arr[i].limit!=''&&limit.indexOf(arr[i].limit)==-1){
            listHTML+='<li displaylimit="false">'+arr[i].value+'</li>'
        }else{
            if(i==0 || i==5){
                if(bTopHas){
                    //if(curGroup.mid!=accountID){
                    //    listHTML+='<li displaylimit="false">'+arr[i].value+'</li>'
                    //}
                    listHTML+='<li data-top="1">'+arr[i].value+'</li>'
                }else{
                    listHTML+='<li data-top="0">'+arr[i].value+'</li>'
                }
            }else{
               
                listHTML+='<li>'+arr[i].value+'</li>'
            }

        }
    }
    var targetType = '';
    if(targettype){
        targetType = targettype;
    }
    var eDom='';
    if(eTarget){
        eDom=eTarget;
    }
    var sHTML = '<div memShip="'+memShip+'" class="myContextMenu" id="'+id+'" style="'+style+'" targetType="'+targetType+'" data-e="'+eDom+'">'+
        '<div class="contextTri chatLeftIcon"></div>'+
        '<ul>'+listHTML+'</ul>'+
        '</div>';
    $('body').append($(sHTML));
    return false;
}


function showPanel(panelClass){
    var eShowNode = $("."+panelClass);
    if(eShowNode){
        eShowNode.removeClass('chatHide');
        eShowNode.find('#backstageMgId li').removeClass('active');
        eShowNode.siblings(".chatContent").addClass('chatHide');
        $('#chatBox').children().each(function(){
            if(!$(this).hasClass('chatHide')){
                $(this).addClass('chatHide');
            }
        });
    }
}


function sendAjax(url,data,callback,callbackB){
    $.ajax({
        type: "POST",
        url: url,
        data:data,
        success: function(data){
            callback && callback(data);
        },
        error:function(){
            callbackB&&callbackB();
        }
    })
}