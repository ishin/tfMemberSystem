/**
 * Created by zhu_jq on 2017/1/10.
 */
$(function(){


    setInterval(function(){
        searchTree()
    },30000)
    //查询群组禁言
    var groupShutupTimer = null
    groupShutupTimer = setInterval(function(){
        checkShutUp()
    },5000)

    //获取常用联系人左侧
    //var sAccount = localStorage.getItem('account');
    //var sdata = localStorage.getItem('datas');
    //var oData=JSON.parse(sdata);
    //var account = oData?oData.account : '';
    //var accountID = oData?oData.id :'';
    var timer=null,timer1 = null;


    //刷新群组的在线人数
    var groupOnlineNum = null;
    groupOnlineNum = setInterval(function(){

        changeGroupOnlineN(accountID);
    },globalVar.refreshGroupOnline)
    //获取个人在线状态
    var personOnlineNum = null;
    personOnlineNum = setInterval(function(){
        changePersonOnlineN('');
    },globalVar.refreshPersonalOnline)


    function showPersonDetailDia(e,CurList){
        var pos = {};
        pos.top = e.clientY;
        pos.left = 300;
        var data = '';
        var account = CurList.attr('account');
        var datas = localStorage.getItem('MemberFriends');
        var data = JSON.parse(datas);
        for(var i = 0;i<data.text.length;i++){
            if(data.text[i].account == account){
                showMemberInfo(data.text[i],pos);
                console.log(i);
                break;
            }
        }
    }

    $('.usualChatList').delegate('li .groupImg','mouseenter',function(e){
        var _this = $(this).closest('li');
        timer=setTimeout(function(){
            showPersonDetailDia(e,_this);
        },500);
    })
    $('.usualChatList').delegate('li .groupImg','mouseleave',function(e){
        clearTimeout(timer);
        timer1 = setTimeout(function(){
            $('.memberHover').remove();
        },100)
    })
    $('body').delegate('.memberHover','mouseenter',function(){
        clearTimeout(timer1);
    })
    $('body').delegate('.memberHover','mouseleave',function(){
        $('.memberHover').remove();
    })

    //点击的事件  弹窗上的
    $(window).click(function(e){
            var targetID = $(e.target).closest('.showPersonalInfo').attr('targetid');
            if(!targetID){
                var targetID = $(e.target).parents('.selfImgInfo').next().attr('targetid');
            }
            if(!targetID){
                var targetID = $(e.target).parents('li').attr('id');
            }
            if($(e.target).parent().hasClass('selfImgOpera')||$(e.target).parent().hasClass('personalOperaIcon')){
                var targeType = 'member';
            }else{
                var targeType = $(e.target).parents('.showPersonalInfo').attr('targettype');
            }
            var data = localStorage.getItem('getBranchTree');
            var datas = JSON.parse(data);
            switch (e.target.className){
                case 'sendMsg'://发起聊天
                    var limit = $('body').attr('limit');
                    //var oLimit = JSON.parse(limit);
                    if(limit.indexOf('ltszfqgrlt')==-1){//没有权限
                        new Window().alert({
                            title   : '',
                            content : '您无权限发起聊天！',
                            hasCloseBtn : false,
                            hasImg : true,
                            textForSureBtn : false,
                            textForcancleBtn : false,
                            autoHide:true
                        });
                    }else{//targetID
                        var targeType = 'PRIVATE';


                        newContactList(targeType,targetID);
                    }
                    $('.memberHover').remove();
                    break;
                case 'checkPosition'://查看位置
                    var limit = $('body').attr('limit');
                    //var oLimit = JSON.parse(limit);
                    if(limit.indexOf('dpjhzsjbmkfckdlwz')==-1){//没有权限
                        new Window().alert({
                            title   : '',
                            content : '您无权限查看位置！',
                            hasCloseBtn : false,
                            hasImg : true,
                            textForSureBtn : false,
                            textForcancleBtn : false,
                            autoHide:true
                        });
                        //$('.memberHover').remove();
                        break;
                    }else{
                        $('.orgNavClick').addClass('chatHide');
                        $('.groupMap').removeClass('chatHide');
                        if(targeType=='member'){
                            targeType = 'PRIVATE';

                        }
                        creatMemberMap(targetID,targeType);
                        //console.log(targeType,targetID,datas);
                    }
                    $('.memberHover').remove();
                    break;
                case 'addConver'://添加群聊
                    var limit = $('body').attr('limit');
                    //var oLimit = JSON.parse(limit);
                    if(limit.indexOf('qzcjq')==-1){//没有权限
                        new Window().alert({
                            title   : '',
                            content : '您无权限添加群聊！',
                            hasCloseBtn : false,
                            hasImg : true,
                            textForSureBtn : false,
                            textForcancleBtn : false,
                            autoHide:true
                        });
                    }else{
                        var memShipArr = [targetID,accountID];
                        memShipArr = unique3(memShipArr);
                        converseACount.push(accountID);
                        creatDialogTree(datas,'groupConvers','添加会话',function(){
                            var sConverseACount = JSON.stringify(converseACount);
                            sendAjax('group!createGroup',{userid:accountID,groupids:sConverseACount},function(data){
                                if(data){
                                    $('.manageCancle').click();
                                    var datas = JSON.parse(data);
                                    if(datas.code==200){
                                        new Window().alert({
                                            title   : '',
                                            content : '创建群组成功！',
                                            hasCloseBtn : false,
                                            hasImg : true,
                                            textForSureBtn : false,
                                            textForcancleBtn : false,
                                            autoHide:true
                                        });
                                        getGroupList(accountID);
                                        $('.chatHeaderMenu li')[0].click();
                                        $('.chatMenu li')[0].click();
                                    }else{
                                        alert('失败',datas.text);
                                    }
                                }
                            })
                        },memShipArr);
                    }
                    $('.memberHover').remove();
                    break;
                default :
            }
        $('.myContextMenu').remove();
    })

    $('body').undelegate('#sysMsgMenu li','click');
    $('body').delegate('#sysMsgMenu li','click',function(e){
        $('.myContextMenu').remove();
        var index = $(this).closest('ul').find('li').index($(this));
        switch (index)
        {
            case 0:
                //系统消息从消息列表删除
                var $THIS=$(this);
                new Window().alert({
                    title   : '清空系统消息',
                    content : '确定要清空系统消息么？',
                    hasCloseBtn : true,
                    hasImg : true,
                    textForSureBtn : '确定',              //确定按钮
                    textForcancleBtn : '取消',            //取消按钮
                    handlerForCancle : null,
                    handlerForSure : function(){
                        removeSysConvers();

                    }
                });
                break;
        }
    })

    $('body').undelegate('#newsLeftClick li','click');
    $('body').delegate('#newsLeftClick li','click',function(e){
        //var targetID =
        var targetID = $(this).parents('.myContextMenu').attr('memship');
        var targetType = $(this).parents('.myContextMenu').attr('targettype');
        $('.myContextMenu').remove();
        var index = $(this).closest('ul').find('li').index($(this));
        switch (index)
        {
            case 0:
                //置顶会话
                setConverToTop(targetType,targetID,$(this));
                break;
            case 1:
                //发送文件
                var limit = $('body').attr('limit');
                //var oLimit = JSON.parse(limit);
                if(limit.indexOf('qzcjq')==-1) {//没有权限
                    return false;
                }else{
                    var eDom = $('.usualChatListUl').find('[targetid='+targetID+'][targettype='+targetType+']');
                    var groupName = eDom.find('.groupName').html();
                    newContactList(targetType,targetID,groupName,function(){
                        if(targetType=='PRIVATE'){
                            $('#perContainer').find('#upload_file').click()
                        }else{
                            $('#groupContainer').find('#upload_file').click()
                        }
                    })
                }

                break;
            case 2:
                //查看资料
                if(targetType=='PRIVATE'){
                    var memberid = $(this).parents('.myContextMenu').attr('memship');
                    var CurList = $('[targetid='+memberid+'][targettype=PRIVATE]');
                    var pos = {};
                    pos.top = parseInt(e.clientY)-100;
                    pos.left = e.clientX;
                    var data = '';
                    var targerID = memberid;
                    var datas = localStorage.getItem('MemberFriends');
                    var data = JSON.parse(datas);
                    for(var i = 0;i<data.text.length;i++){
                        if(data.text[i].id == targerID){
                            showMemberInfo(data.text[i],pos);
                            console.log(i);
                            break;
                        }
                    }
                }else{
                    var memberid = $(this).parents('.myContextMenu').attr('memship');  //39
                    var CurList = $('[targetid='+memberid+'][targettype=GROUP]');
                    var pos = {};
                    pos.top = parseInt(e.clientY)-100;
                    pos.left = e.clientX;
                    var account = CurList.attr('account');
                    var datas = localStorage.getItem('groupInfo');
                    var data = JSON.parse(datas);
                    var aText=data.text;
                    for(var i = 0;i<aText.length;i++){
                        if(aText[i].GID==memberid){
                            showGroupMemberInfo(aText[i],pos);
                        }
                    }
                }

                break;
            case 3:
                //添加新成员
                var limit = $('body').attr('limit');
                //var oLimit = JSON.parse(limit);
                if(limit.indexOf('qzcjq')==-1) {//没有权限
                    return false;
                }else{
                    var data = localStorage.getItem('getBranchTree');
                    var datas = JSON.parse(data);
                    if(targetType=="PRIVATE"){
                        var memShipArr = [targetID,accountID];
                        converseACount.push(accountID);
                        creatDialogTree(datas,'groupConvers','添加会话',function(){
                            var sConverseACount = JSON.stringify(converseACount);
                            sendAjax('group!createGroup',{userid:accountID,groupids:sConverseACount},function(data){
                                if(data){
                                    $('.manageCancle').click();
                                    var datas = JSON.parse(data);
                                    if(datas.code==200){
                                        new Window().alert({
                                            title   : '',
                                            content : '创建群组成功！',
                                            hasCloseBtn : false,
                                            hasImg : true,
                                            textForSureBtn : false,
                                            textForcancleBtn : false,
                                            autoHide:true
                                        });
                                        getGroupList(accountID);
                                        $('.chatHeaderMenu li')[0].click();
                                        $('.chatMenu li')[0].click();
                                    }else{
                                        alert('失败',datas.text);
                                    }

                                }
                            })
                        },memShipArr);
                    }else{
                        globalVar.temporary = targetID;
                        sendAjax('group!listGroupMemebers',{groupid:targetID},function(data){
                            if(data) {
                                var groupDatas = JSON.parse(data);
                                if (groupDatas && groupDatas.code == 1) {
                                    var memShipArr = groupDatas.text;
                                    console.log('11111111111111',globalVar.temporary)
                                    creatDialogTree(datas,'groupConvers','添加会话',function(){
                                        var sConverseACount = JSON.stringify(converseACount);
                                        console.log('2222222222222222222222',globalVar.temporary)
                                        sendAjax('group!manageGroupMem',{groupid:globalVar.temporary,groupids:sConverseACount},function(data){
                                            if(data){
                                                console.log('33333333333333333333333',globalVar.temporary)
                                                $('.manageCancle').click();
                                                var datas = JSON.parse(data);
                                                if(datas.code==1){
                                                    new Window().alert({
                                                        title   : '',
                                                        content : '修改群组成功！',
                                                        hasCloseBtn : false,
                                                        hasImg : true,
                                                        textForSureBtn : false,
                                                        textForcancleBtn : false,
                                                        autoHide:true
                                                    });
                                                    getGroupList(accountID);
                                                    getGroupMembersList(globalVar.temporary);
                                                }else{
                                                    alert('失败',datas.text);
                                                }
                                            }
                                        })
                                    },memShipArr);
                                }
                            }
                        })
                    }
                }

                break;
            case 4:
                //定位到所在组织
                if(targetType=="PRIVATE"){
                    orginizPos(targetID,'member');
                }
                break;
            case 5:
                //从消息列表删除
                var $THIS=$(this);
                new Window().alert({
                    title   : '删除会话',
                    content : '确定要从会话列表中删除么？',
                    hasCloseBtn : true,
                    hasImg : true,
                    textForSureBtn : '确定',              //确定按钮
                    textForcancleBtn : '取消',            //取消按钮
                    handlerForCancle : null,
                    handlerForSure : function(){
                        if(targetType=='GROUP'){
                            removeConvers('GROUP',targetID,$THIS);
                        }else{
                            removeConvers('PRIVATE',targetID,$THIS);
                        }

                    }
                });

                break;
        }
    })
    //点击消息列表
    $('.newsChatList').undelegate('li','mousedown');
    $('.newsChatList').delegate('li','mousedown',function(e){
        $('.myContextMenu').remove();
        var targetID = $(this).attr('targetid');
        var targeType = $(this).attr('targettype');
        var groupName = $(this).find('.groupName').html();
        var $topEle=$(this);
        if(e.buttons==2){
            var left = e.clientX;
            var top = e.clientY;
            var sData=window.localStorage.getItem("datas");
            var oData= JSON.parse(sData);
            var sId=oData.id;
            sendAjax('fun!getMsgTop',{userid:sId},function(data){
                var oData=JSON.parse(data);
                var aText=oData.text;
                if(oData.code==1){
                    var bTopHas;
                    var sTopChat='';
                    for(var i=0;i<aText.length;i++){
                        // var oTopText=aText[i].text;
                        var sTopType=aText[i].type;
                        var nTopId=aText[i].topId;
                        if(nTopId==targetID){
                            bTopHas=true;
                        }
                    }
                    if(bTopHas){
                        sTopChat='取消置顶';
                    }else{
                        sTopChat='置顶会话';
                    }
                    if(targeType == "PRIVATE"){
                        var arr = [{limit:'',value:sTopChat},{limit:'ltszwjsc',value:'发送文件'},{limit:'',value:'查看资料'},{limit:'ltszqzlt',value:'添加新成员'},{limit:'',value:'定位到所在组织'},{limit:'',value:'从消息列表删除'}];

                    }else if(targeType == "system"){
                        var arr = [{limit:'',value:'清空系统消息'}];
                        var sysId = 'sysMsgMenu';
                        var style = 'left:'+left+'px;top:'+top+'px';
                        fshowContexMenu(arr,style,sysId,'','','');
                        return;
                    }else {
                        var arr = [{limit:'',value:sTopChat},{limit:'ltszwjsc',value:'发送文件'},{limit:'',value:'查看资料'},{limit:'ltszqzlt',value:'添加新成员'},{limit:'aaaa',value:'定位到所在组织'},{limit:'',value:'从消息列表删除'}];

                    }
                    var style = 'left:'+left+'px;top:'+top+'px';
                    var id = 'newsLeftClick';
                    var memberShip = $topEle.attr('targetid');
                    fshowContexMenu(arr,style,id,memberShip,targeType,bTopHas);
                }else{
                    var arr = [{limit:'',value:'置顶会话'},{limit:'ltszqzlt',value:'发送文件'},{limit:'',value:'查看资料'},{limit:'qzcjq',value:'添加新成员'},{limit:'',value:'定位到所在组织'},{limit:'',value:'从消息列表删除'}];
                    var style = 'left:'+left+'px;top:'+top+'px';
                    var id = 'newsLeftClick';
                    var memberShip = $topEle.attr('targetid');
                    //var memberShip =
                    fshowContexMenu(arr,style,id,memberShip,targeType,false);
                }
            });
        }else{//单击常用联系人
            newContactList(targeType,targetID,groupName);
        }
        $('.newsChatList li').removeClass('active');
        $(this).addClass('active');
        return false;
    })
    //消息列表右键菜单
    $('body').undelegate('#usualLeftClick li','click')
    $('body').delegate('#usualLeftClick li','click',function(ev){
        console.info('____', ev);
        var memShip = $('.myContextMenu').attr('memship');
        var index = $(this).closest('ul').find('li').index($(this));
        $('.myContextMenu').remove();
        switch (index)
        {
            case 0:
                new Window().alert({
                    title   : '解除好友',
                    content : '确定要解除好友吗？',
                    hasCloseBtn : true,
                    hasImg : true,
                    textForSureBtn : '确定',              //确定按钮
                    textForcancleBtn : '取消',            //取消按钮
                    handlerForCancle : null,
                    handlerForSure : function(){
                        cancleRelation(account,memShip);
                    }
                });
                break;
        }
    })
    var oChatList=null;
    //点击常用联系人（左右键）3$('.usualChatList').delegate('li','mousedown'
    $('.usualChatList').undelegate('li','mousedown')
    $('.usualChatList').delegate('li','mousedown',function(e){
        $('.myContextMenu').remove();
        if(e.buttons==2){
            var left = e.clientX;
            var top = e.clientY;
            //var arr = ['解除好友'];
            var arr = [{limit:'',value:'解除好友'}];

            var style = 'left:'+left+'px;top:'+top+'px';
            var id = 'usualLeftClick';
            var friend = $(this).attr('account');
            var targettype = $(this).attr('targettype')
            //var memShip = JSON.stringify()
            fshowContexMenu(arr,style,id,friend,targettype);
        }else{//单击常用联系人
            clearTimeout(oChatList);
            var sThis=$(this);
            oChatList=setTimeout(function(){
                var targetID = sThis.attr('targetid');
                var targeType = 'PRIVATE';
                conversationSelf(targetID,targeType);
                //$('.orgNavClick').addClass('chatHide');
                //$('.mesContainerSelf').removeClass('chatHide');
            },200);
        }
        $('.usualChatList li').removeClass('active');
        $(this).addClass('active');
        return false;
    });
    //双击常用联系人
    $('.usualChatList').undelegate('li','dblclick')
    $('.usualChatList').delegate('li','dblclick',function(){
        clearTimeout(oChatList);
        var targetID = $(this).attr('targetid');
        var targeType = 'PRIVATE';
        $('.orgNavClick').addClass('chatHide');
        $('.groupMap').removeClass('chatHide');
        creatMemberMap(targetID,targeType);
    });





    //将系统消息从消息列表中删除
    function removeSysConvers(){
        localStorage.removeItem('systemMsg');
        getConverList();
        $('#sysContainer .mr-sysHistory').html('');
    }

    //定位到所在组织
    function orginizPos(targetID,type){
        $('.chatHeaderMenu li')[1].click();
        $('.organizationList').find('li').removeClass('active');
        var targetNode = $('.organizationList').find('li.member[id='+targetID+']')
        targetNode.addClass('active');
        targetNode.click();
    }
    function creatMemberMap(targetID,targeType){
        var curTargetList = searchFromList(1,targetID);
        var name = curTargetList.name;
        $('.perSetBox-title span').html(name);
        $('.groupMap').attr('targetID',targetID);
        $('.groupMap').attr('targetType',targeType);
        $('.groupMapMember').addClass('chatHide');
        getGroupMap(targetID,2);
    }
    function creatGroupMap(targetID,targeType,groupName){
        $('.groupMapMember ul').empty();
        $('.perSetBox-title span').html(groupName);
        $('.groupMap').attr('targetID',targetID);
        $('.groupMap').attr('targetType',targeType);
        $('.groupMapMember').removeClass('chatHide');
        getGroupMap(targetID,1);
    }
    function getGroupMap(targetID,count){
        var sData=window.localStorage.getItem("datas");
        var oData= JSON.parse(sData);
        var sId=oData.id;
        var map = new AMap.Map('container', {
            resizeEnable: true,
            zoom: 10
        });
        var _onClick = function(position){
            map.setZoomAndCenter(18, position);
        };
        var sdata = localStorage.getItem('datas');
        var oData=JSON.parse(sdata);
        //var account = oData?oData.account : '';
        var accountID = oData?oData.id :'';
        if(targetID==accountID){
            targetID = 0;
        }
        sendAjax('map!getLocation',{userid:sId,targetid:targetID,type:count,isInit:1},function(data){
            var aDatas=JSON.parse(data);
            var aText=aDatas.text;
            if(aDatas.code==1) {
                if(aText.length>0){
                    for(var i=0;i<aText.length;i++){
                        var sLatitude=aText[i].latitude;//经度
                        var sLongtitude=aText[i].longtitude;//纬度
                        var sLogo=aText[i].logo?globalVar.imgSrc+aText[i].logo : globalVar.defaultLogo;//用户头像
                        var sUserID=aText[i].userID;//用户id
                        var marker;
                        var lnglats=[];
                        if(sLatitude=='90'&&sLongtitude=='180'){
                            continue;
                        }
                        //sLatitude = sLatitude>=90?39.90923:sLatitude;
                        //sLongtitude = sLongtitude>=180?116.397428:sLongtitude;
                        lnglats.push(sLongtitude);
                        lnglats.push(sLatitude);
                        if(!$('.groupMapMember').hasClass('chatHide')){
                            var sDom='<li>\
                                        <img src="'+sLogo+'">\
                                     </li>';
                            $('.groupMapMember ul').append(sDom);
                        }
                        var ssUserID = sUserID+''
                        if(sId==ssUserID){
                            var content= '<div class="selfPrPos">' +
                                '<img src="'+sLogo+'"></div>';
                        }else{
                            var content= '<div class="perPos">' +
                                '<img src="'+sLogo+'"></div>';
                        }
                        marker = new AMap.Marker({
                            content: content,
                            position: lnglats,
                            offset: new AMap.Pixel(-54,-66),
                            map: map
                        });
                        //var t=[116.480983+i, 39.989628];
                        marker.index=i;
                        marker.t=lnglats;
                        marker.setMap(map);
                        AMap.event.addListener(marker,'dblclick',function(e){
                            _onClick(e.target.t);
                            $('.perPos').removeClass('active');
                            $('.perPos').eq($(this).index()).addClass('active');
                        });
                    }
                }
            }

            map.setFitView();
        });
    }

    //点击群组
    var groupTimer=null;
    $('.groupChatList').delegate('li','mousedown',function(e){
        $('.myContextMenu').remove();
        if(e.buttons==2){
            var left = e.clientX;
            var memship = $(this).attr('targetid');
            var top = e.clientY;
            //var arr = ['群成员管理','解散群','转让群'];
            //var arr = [{limit:'qzxgqcjz',value:'群成员管理'},{limit:'qzjsq',value:'解散群'},{limit:'qzxgqcjz',value:'转让群'}];
            var arr = [{limit:'qzxgqcjz',value:'群成员管理'},{limit:'znsqz',value:'解散群'},{limit:'znsqz',value:'转让群'}];

            var style = 'left:'+left+'px;top:'+top+'px';
            var id = 'groupLeftClick'
            fshowContexMenu(arr,style,id,memship);
        }else{//点击群组
            clearTimeout(groupTimer);
            var targetID = $(this).attr('targetid');
            var targeType = 'GROUP';
            var groupName = $(this).find('.groupName').html();
            groupTimer=setTimeout(function (){
                $('.orgNavClick').addClass('chatHide');
                $('.mesContainerGroup').removeClass('chatHide');
                checkShutUp();
                conversationGroup(targetID,targeType,groupName);
            },200);
        }
        $('.groupChatListUl li').removeClass('active');
        $(this).addClass('active');
        return false;
    })
    $('.groupChatList').delegate('li','dblclick',function(e){
        clearTimeout(groupTimer);
        var targetID = $(this).attr('targetid');
        var targeType = 'GROUP';
        var groupName = $(this).find('.groupName').html();
        $('.orgNavClick').addClass('chatHide');
        $('.groupMap').removeClass('chatHide');
        creatGroupMap(targetID,targeType,groupName);
    });

    //群设置中的群成员管理
    $('.personalData').undelegate('.groupInfo-groupManage','click');
    $('.personalData').delegate('.groupInfo-groupManage','click',function(){
        var memship = $(this).attr('memship');
        var memShipArr = memship?JSON.parse(memship):[];
        var groupid = $('.mesContainerGroup').attr('targetid');
        var data = localStorage.getItem('getBranchTree');
        var datas = JSON.parse(data)
        creatDialogTree(datas,'groupConvers','群组管理',function(){
            var sConverseACount = JSON.stringify(converseACount);
            sendAjax('group!manageGroupMem',{groupid:groupid,groupids:sConverseACount},function(data){
                if(data){
                    $('.manageCancle').click();
                    var datas = JSON.parse(data);
                    if(datas.code==1){
                        new Window().alert({
                            title   : '',
                            content : '修改群组成功！',
                            hasCloseBtn : false,
                            hasImg : true,
                            textForSureBtn : false,
                            textForcancleBtn : false,
                            autoHide:true
                        });
                        getGroupList(accountID);
                        getGroupMembersList(groupid)
                    }else{
                        alert('失败',datas.text);
                    }
                }
            })
        },memShipArr,'',groupid);
    })

    //群组右键菜单
    $('body').delegate('#groupLeftClick li','click',function(){
        $('.myContextMenu').remove();
        var _this = $(this);
        //查询群成员
        var memShipArr = [];
        var groupid = _this.parents('.myContextMenu').attr('memship');
        sendAjax('group!listGroupMemebers',{groupid:groupid},function(data){
            if(data) {
                var datas = JSON.parse(data);
                if(datas&&datas.code==1){
                    memShipArr = datas.text;
                    var index = _this.closest('ul').find('li').index(_this);
                    var data = localStorage.getItem('getBranchTree');
                    var datas = JSON.parse(data)
                    switch (index)
                    {
                        case 0:
                            //群成员管理
                            //creatDialogTree四个参数 1结构数据 2类名(groupConvers/privateConvers) 3title 4已选联系人
                            creatDialogTree(datas,'groupConvers','群组管理',function(){
                                var sConverseACount = JSON.stringify(converseACount);
                                sendAjax('group!manageGroupMem',{groupid:groupid,groupids:sConverseACount},function(data){
                                    if(data){
                                        $('.manageCancle').click();
                                        var datas = JSON.parse(data);
                                        if(datas.code==1){
                                            new Window().alert({
                                                title   : '',
                                                content : '修改群组成功！',
                                                hasCloseBtn : false,
                                                hasImg : true,
                                                textForSureBtn : false,
                                                textForcancleBtn : false,
                                                autoHide:true
                                            });
                                            getGroupList(accountID);
                                            getGroupMembersList(groupid);
                                        }else{
                                            alert('失败',datas.text);
                                        }
                                    }
                                })
                            },memShipArr,'',groupid);
                            break;
                        case 1:
                            //解散群
                            if(_this.attr('displaylimit')=='false'){
                                return false;

                            }else{
                                new Window().alert({
                                    title   : '解散群',
                                    content : '确定要解散群吗？',
                                    hasCloseBtn : true,
                                    hasImg : true,
                                    textForSureBtn : '确定',              //确定按钮
                                    textForcancleBtn : '取消',            //取消按钮
                                    handlerForCancle : null,
                                    handlerForSure : function(){
                                        //解散群组接口
                                        var datas = localStorage.getItem('datas');
                                        //if(sAccount){
                                        var data = JSON.parse(datas);
                                        var userid = data.id;
                                        sendAjax('group!disslovedGroup',{userid:userid,groupid:groupid},function(){
                                            $('.orgNavClick').addClass('chatHide');
                                            getGroupList(userid);
                                            removeConvers("GROUP",groupid);
                                        },function(){
                                            console.log('失败');
                                        })
                                    }
                                });
                            }

                            break;
                        case 2:
                            //转让群
                            if(_this.attr('displaylimit')=='false'){
                                return false;
                            }else{
                                sendAjax('group!listGroupMemebersData',{groupid:groupid},function(data){
                                    var datas = JSON.parse(data).text;
                                    transferGroup(datas,groupid,function(){
                                        var transferTarget = $('.transferGroupTo.active');
                                        if(transferTarget){
                                            var target = transferTarget.closest('tr');
                                            var tatgetID = target.attr('targetid');
                                            var targetLimit = target.attr('transferlimit');
                                            if(tatgetID&&targetLimit=='true'){//有转让权限
                                                sendAjax('group!transferGroup',{userid:tatgetID, groupid:groupid},function(data){
                                                    //console.log('11111',data);
                                                    if(data){
                                                        var datas = JSON.parse(data);
                                                        if(datas&&datas.code==1){
                                                            new Window().alert({
                                                                title   : '',
                                                                content : '群组转让成功！',
                                                                hasCloseBtn : false,
                                                                hasImg : true,
                                                                textForSureBtn : false,
                                                                textForcancleBtn : false,
                                                                autoHide:true
                                                            });
                                                            getGroupList(accountID);
                                                            $('.WindowMask2').hide();
                                                        }else if(datas&&datas.code==0){
                                                            new Window().alert({
                                                                title   : '',
                                                                content : '群组转让失败！',
                                                                hasCloseBtn : false,
                                                                hasImg : true,
                                                                textForSureBtn : false,
                                                                textForcancleBtn : false,
                                                                autoHide:true
                                                            });
                                                            $('.WindowMask2').hide();
                                                        }
                                                    }
                                                })
                                            }else if(tatgetID&&targetLimit=='false'){//无转让权限
                                                new Window().alert({
                                                    title   : '',
                                                    content : '该成员无群组管理权限！',
                                                    hasCloseBtn : false,
                                                    hasImg : true,
                                                    textForSureBtn : false,
                                                    textForcancleBtn : false,
                                                    autoHide:true
                                                });
                                            }
                                        }
                                    });
                                })
                            }
                            break;
                    }
                }
            }
        })
    })

    /*
     * 点击加号 “+”
     * */
    $('.operMenuList').unbind('click');
    $('.operMenuList').click(function(e){
        clearTimeout(plusTimer);
        var afterNewTree = function(){
            var getBranchTree = localStorage.getItem('getBranchTree');
            if(getBranchTree){
                var data = JSON.parse(getBranchTree);
            }
            var index = $(e.target).closest('ul').find('li').index($(e.target));
            switch (index)
            {
                case 0:
                    //添加好友
                    //creatDialogTree四个参数 1结构数据 2类名(groupConvers/privateConvers) 3title 4已选联系人
                    creatDialogTree(data,'privateConvers','添加好友',function(){
                        sendAjax('friend!addFriend',{account:account,friend:converseACount[0]},function(data){
                            var datas = JSON.parse(data);
                            console.log(data);
                            if(datas.code==1){
                                //刷新常用联系人
                                getMemberFriends(account);
                                $('.manageCancle').click();
                                new Window().alert({
                                    title   : '添加好友',
                                    content : '好友添加成功！',
                                    hasCloseBtn : false,
                                    hasImg : true,
                                    textForSureBtn : false,
                                    textForcancleBtn : false,
                                    autoHide:true
                                });
                            }else{
                                new Window().alert({
                                    title   : '添加好友',
                                    content : '好友添加失败！',
                                    hasCloseBtn : false,
                                    hasImg : true,
                                    textForSureBtn : false,
                                    textForcancleBtn : false,
                                    autoHide:true
                                });
                            }
                        })
                    });
                    break;
                case 1:
                    //发起聊天
                    if($(e.target).attr('displaylimit')=='false'){
                        return false;
                    }
                    creatDialogTree(data,'privateConvers','发起聊天',function(){
                        var targetAccount = converseACount[0];
                        $('.manageCancle').click();
                        if(targetAccount!=account){//是自己
                            var targetID = searchIDFromList(1,targetAccount);
                            newContactList('PRIVATE',targetID);
                        }
                    })
                    break;
                case 2:

                    var limit = $('body').attr('limit');
                    //var oLimit = JSON.parse(limit);
                    if(limit.indexOf('qzcjq')==-1){//没有权限
                        return false;
                        break;
                    }else{
                        //创建群组
                        converseACount = [];
                        converseACount.push(accountID);
                        creatDialogTree(data,'groupConvers','创建群组',function(){

                            var sConverseACount = JSON.stringify(converseACount);
                            var groupname = $('.addGroupGroupName').val();
                            sendAjax('group!createGroup',{userid:accountID,groupids:sConverseACount,groupname:groupname},function(data){
                                if(data){
                                    $('.manageCancle').click();
                                    var datas = JSON.parse(data);
                                    if(datas.code==200){
                                        new Window().alert({
                                            title   : '',
                                            content : '创建群组成功！',
                                            hasCloseBtn : false,
                                            hasImg : true,
                                            textForSureBtn : false,
                                            textForcancleBtn : false,
                                            autoHide:true
                                        });
                                        getGroupList(accountID);
                                    }else{
                                        alert('失败',datas.text);
                                    }

                                }
                            });
                        },converseACount)
                    }
                    break;
            }
        }
        getBranchTreeAndMember(afterNewTree);
    })
    /*点击"+"*/
    var plusTimer = null;
    $('.footerPlus').click(function(){
        $(this).find('.operMenuList').slideToggle();
        //plusTimer = setTimeout(function(){
        //    $('.footerPlus').click();
        //},2000)
    })
    $('.footerPlus').mouseenter(function(){
        clearTimeout(plusTimer);
    })
    $('.footerPlus').mouseleave(function(){
        plusTimer = setTimeout(function(){
            $('.footerPlus').find('.operMenuList').slideUp();
        },100)
    })

})
//系统提示音
function getSysTipVoice(userid){
    sendAjax('fun!getSysTipVoice',{userid:userid},function(data){
        var oData=JSON.parse(data);
        if(oData.code==1){
            globalVar.SYSTEMSOUND=oData.text;
            if(oData.text==1){//开启系统提示因
                //globalVar.SYSTEMSOUND=!globalVar.SYSTEMSOUND;
                $('.systemVoiceBtn').removeClass('active');
            }else{
                //globalVar.SYSTEMSOUND=data.text;
                $('.systemVoiceBtn').addClass('active');
            }

        }
    })
}

//跳转到常用联系人并打开会话
function jumpToFriendListOpen(targetAccount){
    $('.chatHeaderMenu li')[0].click();
    $('.chatMenu .chatLeftIcon')[1].click();
    var targetDon = $('.usualChatList').find('li')
    targetDon.removeClass('active');
    var targetMember = $('.usualChatList').find('li[account='+targetAccount+']');
    targetMember.addClass('active').click();
    var targetID = targetMember.attr('targetid');
    var targeType = 'PRIVATE';
    conversationSelf(targetID,targeType);
}
//添加好友并跳转到常用联系人打开会话
function addFriendAndRefreshList(account,targetAccount){
    sendAjax('friend!addFriend',{account:account,friend:targetAccount},function(data){
        //var datas = JSON.parse(data);
        //console.log(data);
        //if(datas.code==1){
        //刷新常用联系人
        getMemberFriends(account,function(){
            $('.searchResult').remove();
            $('.chatHeaderMenu li')[0].click();
            $('.chatMenu .chatLeftIcon')[1].click();
            var targetDon = $('.usualChatList').find('li')
            targetDon.removeClass('active');
            var targetMember = $('.usualChatList').find('li[account='+targetAccount+']');
            targetMember.addClass('active').click();
            var targetID = targetMember.attr('targetid');
            var targeType = 'PRIVATE';
            conversationSelf(targetID,targeType);
        });
    })
}


function changePersonOnlineN(accountID){
    var $organizationList = $('.organizationList');
    sendAjax('member!getAllMemberOnLineStatus',{userid:accountID},function(data){
        if(data){
            if(data.indexOf('DOCTYPE')!=-1){
                //返回登录页面
                window.location = window.location.origin+'/im/';
            }else{
                var datas = JSON.parse(data);
                var memberList = datas.text;
                for(var key in memberList){
                    var targetGroup = $organizationList.find('li#'+key+'.member');
                    var sMemberStatus = '';
                    var onlineClassName = '';
                    //memberList[key] = '3';
                    var onLineStatues = memberList[key]+''
                    switch (onLineStatues){

                        case '0'://离线
                            onlineClassName = 'imgToGrey';
                            sMemberStatus = '';

                            //sMemberStatus = '离线'
                            break;
                        case '1'://在线
                            onlineClassName = '';
                            sMemberStatus = '';

                            //sMemberStatus = '在线'
                            break;
                        case '2'://手机在线
                            onlineClassName = '';

                            sMemberStatus = 'phoneOnline';

                            break;
                        case '3'://繁忙
                            onlineClassName = '';
                            sMemberStatus = 'memberbusy';
                            break;
                    }
                    if(targetGroup&&targetGroup.hasClass('member')){
                        targetGroup.find('.onlineStatus ').attr('class','onlineStatus '+sMemberStatus)
                        targetGroup.find('.groupImg').attr('class','groupImg '+onlineClassName);
                    }
                }
            }

        }
    })
}
function newContactList(targeType,targetID,groupName,callback){
    $('.newsChatList').find('li').removeClass('active');
    $(this).addClass('active');
    if(targeType=='PRIVATE'){
        $('.orgNavClick').addClass('chatHide');
        $('.mesContainerSelf').removeClass('chatHide');
        $('#groupContainer #message-content').attr('contenteditable','true');
        $('#groupContainer #message-content').attr('placeholder','请输入文字...');
        $('#groupContainer #message-content').html('');
        conversationSelf(targetID,targeType,callback);
    }else if(targeType=='system'){
        $('.orgNavClick').addClass('chatHide');
        $('.mesContainerSys').removeClass('chatHide');
        conversationSys(targetID,targeType,callback);

    }else{
        $('.orgNavClick').addClass('chatHide');
        $('.mesContainerGroup').removeClass('chatHide');
        checkShutUp();
        conversationGroup(targetID,targeType,groupName,callback);
    }
}

function changeGroupOnlineN(accountID){

    groupOnlineMember(accountID)

    //sendAjax('group!getGroupOnLineMember',{userid:accountID},function(data){
    //    if(data){
    //        var datas = JSON.parse(data);
    //        var groupList = datas.text;
    //        for(var key in groupList){
    //            var targetGroup = $groupChatList.find('li[targetid='+key+']');
    //            if(targetGroup){
    //                targetGroup.find('.onlineCount ').html(groupList[key]);
    //            }
    //        }
    //    }
    //})
}

//数组去重
function unique3(arr){
    if(arr){
        var res = [];
        var json = {};
        for(var i = 0; i < arr.length; i++){
            if(!json[arr[i]]){
                res.push(arr[i]);
                json[arr[i]] = 1;
            }
        }
        return res;
    }
    else{
        return [];
    }
}

//创建群组列表
function getGroupList(accountID,callback){
    var dom = $('.groupChatList .groupChatListUl');
    var sHTML = '';
    sendAjax('group!groupList',{userid:accountID},function(data){
        if(data){
            window.localStorage.groupInfo = data;
            var datas = JSON.parse(data);
            callback&&callback();
            var groupArr = datas.text;
            if(groupArr){
                for(var i = 0;i<groupArr.length;i++){
                    var curGroup = groupArr[i];
                    sHTML+='<li targetid="'+curGroup.GID+'">'+
                    '<div>'+
                    '<img class="groupImg" src="'+globalVar.defaultGroupLogo+'" alt="">'+
                    '<span class="groupName">'+curGroup.name+
                    '</span>'+
                    '<em class="groupInlineNum">(<span class="onlineCount">'+curGroup.volumeuse+'</span>/<span class="memberCount">'+curGroup.volumeuse+'</span>)</em>'+
                    '</div>'+
                    '</li>'
                }
            }else{
                sHTML = '';
            }
            dom.html(sHTML);
            changeGroupOnlineN(accountID);

        }
    })

}

//更新面包屑导航
function BreadcrumbGuid(target){
    console.log(target);
    var sHTML = '';
    sHTML += findParentCatalog(target,sHTML);
    return sHTML;
}

function findParentCatalog(target,sHTML){
    var category = target.find('.groupName').html();
    var sClass = target.attr('class');
    var sID = target.attr('id');

    sHTML = '<li class="'+sClass+'" id="'+sID+'"><a> '+category+' </a> &gt;</li>';
    if(target.parent().prev().length!=0&&target.parent().prev()[0].tagName=='LI'){
        sHTML = findParentCatalog(target.parent().prev(),sHTML)+sHTML
    }
    return sHTML;
}

function removeConvers(type,id,$topEle){

    RongIMClient.getInstance().removeConversation(RongIMLib.ConversationType[type],id,{
        onSuccess:function(bool){
            // 删除会话成功。
            var nTopType;
            switch(type){
                case 'GROUP':
                    nTopType=1;
                    $('.orgNavClick').addClass('chatHide');
                    break;
                case 'PRIVATE':
                    $('.orgNavClick').addClass('chatHide');
                    nTopType=2;
                    break;
            }
            var sData=window.localStorage.getItem("datas");
            var oData= JSON.parse(sData);
            var sId=oData.id;
            if($topEle){
                var sTopHas=$topEle.attr('data-top');
                if(sTopHas==1){
                    sendAjax('fun!cancelMsgTop',{userid:sId,topid:id,toptype:nTopType},function(data){
                        getConverList();
                    });
                }else{
                    getConverList();
                }
            }
            console.log('删除会话列表成功');

        },
        onError:function(error){
            // error => 删除会话的错误码
        }
    });
}

function groupOnlineMember(accountID){
    var $groupChatList = $('.groupChatList');
    sendAjax('group!getGroupOnLineMember',{userid:accountID},function(data){
        if(data){
            var datas = JSON.parse(data);
            var groupList = datas.text;
            for(var key in groupList){
                var targetGroup = $groupChatList.find('li[targetid='+key+']');
                if(targetGroup){
                    targetGroup.find('.onlineCount ').html(groupList[key][0]);
                    targetGroup.find('.memberCount ').html(groupList[key][1]);
                }
            }
        }
    })
}

//点击的是部门
function changeClick1Content(data){
    var sHTML = ''
    if(data&&data.length!=0){
        sHTML = '<div class="orgNavTitle">部门领导</div><ul>';
        for(var i = 0;i<data.length;i++){
            if(data[i].isleader==1){
                var sHeadImg=data[i].logo || 'PersonImg.png';//头像
                var sName=data[i].name||'';//姓名
                if(data[i].logo){
                    var imgHTML = '<img src="'+globalVar.imgSrc+sHeadImg+'" alt="">';
                }else{
                    var imgHTML = '<img src="'+globalVar.defaultLogo+'" alt="">';

                }
                sHTML += '<li id="'+data[i].id+'" account="'+data[i].account+'">'+
                '<div class="showImgInfo">'+
                imgHTML+
                '</div>'+
                '<div class="showPersonalInfo">'+
                '<span>'+sName+'</span>'+
                '<ul class="personalOperaIcon">'+
                '<li class="sendMsg" title="发起聊天"></li>'+
                '<li class="checkPosition" title="查看位置"></li>'+
                '<li class="addConver" title="加入会话"></li>'+
                '</ul>'+
                '</div>'+
                '</li>';
                remove(data,i);
                break;
            }

        }
        sHTML+='</ul>'
        sHTML += '<div class="orgNavTitle">成员</div><ul>';
        for(var i = 0;i<data.length;i++){
            var sHeadImg=data[i].logo || 'PersonImg.png';//头像
            var sName=data[i].name||'';//姓名
            if(data[i].logo){
                var imgHTML = '<img src="'+globalVar.imgSrc+sHeadImg+'" alt="">';
            }else{
                var imgHTML = '<img src="'+globalVar.defaultLogo+'" alt="">';

            }
            sHTML += '<li id="'+data[i].id+'" account="'+data[i].account+'">'+
            '<div class="showImgInfo">'+
            imgHTML+
            '</div>'+
            '<div class="showPersonalInfo">'+
            '<span>'+sName+'</span>'+
            '<ul class="personalOperaIcon">'+
            '<li class="sendMsg" title="发起聊天"></li>'+
            '<li class="checkPosition" title="查看位置"></li>'+
            '<li class="addConver" title="加入会话"></li>'+
            '</ul>'+
            '</div>'+
            '</li>';
        }
        sHTML+='</ul>'
    }

    return sHTML;
}


//点击的是成员
function changeClick2Content(data){
    var sName=data.name ||'';
    var sHeadImg=data.logo?globalVar.imgSrc+data.logo:globalVar.defaultLogo;
    var sTel=data.mobile ||'';
    var sBranch = data.branchname;
    var sEmail=data.email ||'';
    var sJob=data.postitionname ||'';
    var sGroupuse=data.organname ||'';
    var sAddress=data.address||'';
    var sHTML = '<div class="personalDetailContent">'+
                '<div class="selfImgInfo">'+
                    '<img src="'+sHeadImg+'" alt=""/><div>'+
                '<p>'+sName+'</p>'+
                '<ul class="selfImgOpera">'+
                    '<li class="sendMsg" title="发起聊天"></li><li class="checkPosition" title="查看位置"></li><li class="addConver" title="加入会话"></li>'+
                '</ul></div></div><div class="showPersonalInfo" targetID="'+data.id+'" targetTpe="PRIVATE">'+
                '<ul>'+
                    '<li><div>手机:</div><div>'+sTel+'</div></li>'+
                    '<li><div>邮箱:</div><div>'+sEmail+'</div></li>'+
                    '<li><div>部门:</div><div>'+sBranch+'</div></li>'+
                    '<li><div>职位:</div><div>'+sJob+'</div></li>'+
                    '<li><div>组织:</div><div>'+sGroupuse+'</div></li>'+
                    '<li><div>地址:</div><div>'+ sAddress+'</div></li>'+
                '</ul></div></div></div></div>';
    return sHTML;
}

function searchTree(){
    sendAjax('branch!getBranchTreeAndMember','',function(data){
        window.localStorage.normalInfo = data;
    })
    sendAjax('branch!getMembersByOrgan','',function(data){
        var oData = JSON.parse(data);
        data = oData.text;
        var datas = JSON.stringify(data)
        window.localStorage.getOrganTree = datas;
    })
}
function searchIDFromList(flag,account){
    var curList;
    var normalInfo = localStorage.getItem('normalInfo');
    if(normalInfo){
        var data = JSON.parse(normalInfo);
    }
    //account = parseInt(account);
    for(var i = 0;i<data.length;i++){
        if(data[i].account==account&&data[i].flag==flag){
            curList = data[i];
        }
    }
    if(!curList){
        var normalInfo = localStorage.getItem('getOrganTree');
        if(normalInfo){
            var data = JSON.parse(normalInfo);
        }
        //account = parseInt(account);
        for(var i = 0;i<data.length;i++){
            if(data[i].account==account&&data[i].flag==flag){
                curList = data[i];
            }
        }
    }
    return curList.id;
}
//从组织结构中找到相应的成员、部门数据
function searchFromList(flag,id){
    var curList;
    var normalInfo = localStorage.getItem('normalInfo');
    if(normalInfo){
        var data = JSON.parse(normalInfo);
    }
    id = parseInt(id);
    for(var i = 0;i<data.length;i++){
        if(data[i].id==id&&data[i].flag==flag){
            curList = data[i];
        }
    }
    if(!curList){
        var normalInfo = localStorage.getItem('getOrganTree');
        if(normalInfo){
            var data = JSON.parse(normalInfo);
        }
        id = parseInt(id);
        for(var i = 0;i<data.length;i++){
            if(data[i].id==id&&data[i].flag==flag){
                curList = data[i];
            }
        }
    }
    return curList;
}
//获取组织结构图
function getBranchTreeAndMember(callback){
    sendAjax('branch!getBranchTreeAndMember','',function(data){
        window.localStorage.normalInfo = data;
        var datas = JSON.parse(data);
        if(datas && data.length!=0){
            var myData = changeFormat(data);
            if(myData){
                window.localStorage.getBranchTree = JSON.stringify(myData);
            }
            var $ParendtDom = $('.organizationList');
            var i = 0;
            var sHTML = '';
            var HTML = createOrganizList(myData,sHTML,i);
            $ParendtDom.html(HTML);
            $('.firstUL').css('width',globalVar.FirstULWidth+'px');
            changePersonOnlineN('');
            console.log('newTree');
            callback&&callback();
        }
    })
}
//获取常用联系人
function getMemberFriends(account,callback){
    sendAjax('friend!getMemberFriends',{account:account},function(data){
        var myData = JSON.parse(data);
        window.localStorage.MemberFriends = data;
        var $ParendtDom = $('.usualChatList').find('ul.groupChatListUl');
        var sHTML = '';
        if(myData.text){
            for(var i = 0;i<myData.text.length;i++){
                var curData = myData.text[i];
                var account = curData.account;
                var fullname = curData.fullname;
                var workno = curData.position?' ('+curData.position+')':''
                var logo = curData.logo?globalVar.imgSrc+curData.logo:globalVar.defaultLogo;
                sHTML += ' <li account="'+account+'" targetid="'+curData.id+'">'+
                '<div>'+
                '<img class="groupImg" src="'+logo+'" alt=""/>'+
                '<span class="groupName">'+fullname+workno+'</span>'+
                '</div>'+
                '</li>';
            }
        }

        $ParendtDom.html(sHTML);
        callback&&callback();
    },function(){
        callback&&callback();
    })
}
//解除好友
function cancleRelation(account,friend){
    sendAjax('friend!delFriend',{friend:friend,account:account},function(data){
        var datas = JSON.parse(data);
        console.log(data);
        if(datas.code==1){
            //刷新常用联系人
            new Window().alert({
                title   : '解除成功',
                content : '好友解除成功！',
                hasCloseBtn : false,
                hasImg : true,
                textForSureBtn : false,
                textForcancleBtn : false,
                autoHide:true
            });
            setTimeout(function(){
                $('.orgNavClick').addClass('chatHide');
                getMemberFriends(account);
            },1000)
        }
    })
}

function loop(data,small,temp){
    var tempdata = [];
    for(var p = 0;p<data.length;p++){
        tempdata[p] = data[p];
    }
    for(var i = 0;i<data.length;i++){
        //if(data[i].pid==0){
        //    small[i].hasChild.push(data[i]);
        //}
        for(var j = 0;j<small.length;j++){
            if(data[i].pid==0||(data[i].pid==small[j].id&&small[j].flag!=1)){
                small[j].hasChild.push(data[i]);
                removeObj(tempdata,data[i]);
            }
        }
    }
    //console.log(small);
    for(var k = 0;k<small.length;k++){
        if(tempdata.length !=0&&small[k].flag!=1){
            loop(tempdata,small[k].hasChild);
        }
    }
    return small;
}

//删除数组元素
function remove(arr,dx)
{
    if(isNaN(dx)||dx>arr.length){return false;}
    for(var i=0,n=0;i<arr.length;i++)
    {
        if(arr[i]!=arr[dx])
        {
            arr[n++]=arr[i]
        }
    }
    arr.length-=1
}

function showMemberInfo(data,pos){
    //console.log('=================',data);
    var sName=data.fullname ||'';
    var sHeadImg=data.logo?globalVar.imgSrc+data.logo :globalVar.defaultLogo;
    var sTel=data.mobile ||'';
    var sEmail=data.email ||'';
    var sBranch=data.branch ||'';


    var sJob=data.position ||'';
    var sHTML = '<div class="memberHover" style="left:'+pos.left+'px;top:'+pos.top+'px">'+
                    '<div class="contextTri chatLeftIcon"></div>'+
                    '<ul class="memberInfoHover">'+
                        '<li>'+
                            '<div class="showImgInfo">'+
                                '<img src="'+sHeadImg+'" alt="">'+
                            '</div>'+
                            '<div class="showPersonalInfo" targetID="'+data.id+'"targetType="PRIVATE">'+
                                '<span>'+sName+'</span>'+
                                '<ul class="personalOperaIcon">'+
                                    '<li class="sendMsg" title="发起聊天"></li>'+
                                    '<li class="checkPosition" title="查看位置"></li>'+
                                    '<li class="addConver" title="加入会话"></li>'+
                                '</ul>'+
                            '</div>'+
                        '</li>'+
                        '<li><span>手机：</span><span>'+sTel+'</span></li>'+
                        '<li><span>邮箱：</span><span>'+sEmail+'</span></li>'+
                        '<li><span>部门：</span><span>'+sBranch+'</span></li>'+
                        '<li><span>职位：</span><span>'+sJob+'</span></li>'+
                    '</ul>'+
                '</div>';
    $('body').append($(sHTML));
}

function compare(property){
    return function(a,b){
        var value1 = a[property];
        var value2 = b[property];
        return value1 - value2;
    }
}
function changeFormat(data){

    var data = JSON.parse(data);
    var rootL = [];
    var small = [];
    for(var i = 0;i<data.length;i++){
        data[i].hasChild = [];
        //if(data[i].pid==-1){
        //    small.push(data[i]);
        //}
    }
    data.sort(compare('pid'));
    console.log('paixu',data);
    for(var i = 0;i<data.length;i++){
        if(data[i].flag==-1){
            small.push(data[i]);
            remove(data,i);
        }
    }

    var delArr = [];

    for(var i = 0;i<data.length;i++){
        delArr[i] = data[i];
    }

    //for(var i = 0;i<data.length;i++){
    //    if(data[i].pid==0){
    //        small.push(data[i]);
    //        //delArr.push(i);
    //        removeObj(delArr,data[i]);
    //    }
    //}


    //for(var i = 0;i<data.length;i++){
    //    if(small[0].pid==data[i].pid){
    //        small.push(data[i]);
    //        //delArr.push(i);
    //        removeObj(delArr,data[i]);
    //    }
    //}
    //for(var i = 0;i<delArr.length;i++){
    //    remove(data,i);
    //}


    return loop(delArr,small);
}
function removeObj(delArr,ele){
    for(var i = 0;i<delArr.length;i++){
        if(delArr[i]==ele){
            delArr.splice(i,1);
        }
    }
}

function createOrganizList(data,sHTML,level){
    if(level&&globalVar.maxLevel<level){
        globalVar.maxLevel = level;
        globalVar.FirstULWidth = level*32+30+32+70+70
        //var FirstULWidth = level*32+30+32+70+70
        console.log(globalVar.FirstULWidth);
    }
    if(!level){
        sHTML += '<ul class="firstUL">';

    }else{
        sHTML += '<ul>';
    }
    var k = data.length;
    for(var i = 0;i<data.length;i++){
        var num = level
        var oData = data[i];
        var hasChild = oData.hasChild.length==0?false:true;
        var state = oData.flag==1?'member':'department';
        if(oData.flag==1||oData.hasChild.length==0){
            var collspan = ''
        }else{
            var collspan = '<span class="groupCollspanO chatLeftIcon groupCollspan"></span>'
        }
        if(oData.flag==1){
            if(oData.logo){
                var imgSrc = globalVar.imgSrc+oData.logo;
            }else{
                var imgSrc = globalVar.defaultLogo;
            }
            var poaitionName = oData.postitionname?'('+oData.postitionname+')':'';
        }else if(oData.flag==-1){
            var imgSrc = globalVar.defaultComLogo;
            var poaitionName = ''
        }else{
            var imgSrc = globalVar.defaultDepLogo;
            var poaitionName = ''
        }
        //console.log(level);
        sHTML += '<li class="'+state+'" id="'+oData.id+'">'+
                    '<div level="">'+
                    '<span style="height: 20px;width: '+level*32+'px;display:inline-block;float: left;"></span>'+
                    '<img class="groupImg" src="'+imgSrc+'" alt="">'+
                    '<span class="groupName">'+oData.name+'</span>'+collspan+''+
                    '<span class="positionName" >'+poaitionName+'</span>'+
                    '<span class="memberPos" style="position: absolute;left: '+level*32+'px"></span>'+
                    '</div>'+
                '</li>'
        if(hasChild){
            num ++;
            sHTML = createOrganizList(oData.hasChild,sHTML,num);
        }
    }
    sHTML += '</ul>';
    return sHTML;
}

function DialogTreeLoop(data,sHTML,level,userID){
    sHTML += '<ul>';
    var k = data.length;
    for(var i = 0;i<data.length;i++){
        var num = level
        var oData = data[i];
        //if(oData.id==userID&&oData.flag!=0){
            //var editable = false
        //}else{
            var editable = true;
        //}
        var hasChild = oData.hasChild.length==0?false:true;
        if(oData.flag==1){//成员
            var collspan =  '<span class="dialogCollspan chatLeftIcon"></span>'+
                            '<span class="chatLeftIcon dialogCheckBox"></span>';
            var department = 'member';
            //var positionName = '('+oData.postitionname+')';
        }else{//部门
            var collspan =  '<span class="dialogCollspan chatLeftIcon dialogCollspanO"></span>'+
                            '<span class="chatLeftIcon dialogCheckBox"></span>';
            var department = 'department';
            //var positionName = '';
        }
        if(oData.hasChild.length==0){
            var collspan =  '<span class="dialogCollspan chatLeftIcon"></span>'+
                            '<span class="chatLeftIcon dialogCheckBox"></span>';
        }
        //console.log('*******************************');
        sHTML +=  '<li account = '+data[i].account+' id="'+data[i].id+'" class="'+department+'" editable="'+editable+'">'+
                        '<div level="1" class="'+department+'">'+
                            '<span style="height: 20px;width: '+level*22+'px;display:inline-block;float: left;"></span>'+
                            ''+collspan+'<span class="dialogGroupName">'+oData.name+'</span>'+
                        '</div>'+
                    '</li>';
        if(hasChild){
            num ++;
            sHTML = DialogTreeLoop(oData.hasChild,sHTML,num,userID);
        }
    }
    sHTML += '</ul>';
    return sHTML;
}





function transferGroup(data,groupid,callback){
    $('.WindowMask2').show();
    //var adata = unique3(data)
    var sHTML = createTransforContent(data,groupid);
    var dom = $('.transferInfoBox tbody');
    dom.html(sHTML);
    $('.manageSure').unbind('click');
    $('.manageSure').click(function(){
        callback&&callback();
    });
}


function createTransforContent(data,groupid){
    var sHTML = '';

    for(var i = 0;i<data.length;i++){
        var curList = data[i];
        var curGroup = searchFromList('1',curList.id);
        //var limit = $('body').attr('limit');
        //var limit = limit.indexOf('ltszfqgrlt')==-1?'false':'true';//没有权限
        var limit = curList.qzqx=='true'?'true':'false';
        var limitText = curList.qzqx=='true'?'是':'否';
        var transferText = curList.qzqx=='true'?'<span class="transferGroupTo">转让群</span>':''
        var img = curList.logo?globalVar.imgSrc+curList.logo:globalVar.defaultLogo;
        sHTML+='<tr targetid="'+curList.id+'" transferlimit="'+limit+'">'+
                    '<td><img class="transferImg" src="'+img+'" alt="">'+curList.fullname+'</td>'+
                    '<td>'+curGroup.postitionname+'</td>'+
                    '<td>'+limitText+'</td>'+
                    '<td class="operate">'+transferText+'</td>'+
                '</tr>'
    }
    return sHTML;
}