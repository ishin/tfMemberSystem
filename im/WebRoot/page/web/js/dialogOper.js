/**
 * Created by zhu_jq on 2017/1/9.
 */
$(function(){


    //转让群操作
    $('.transferInfoBox tbody').delegate('.transferGroupTo','click',function(){
        $('.transferInfoBox .transferGroupTo').removeClass('active');
        $(this).addClass('active');
    })

    $('.contactBox').delegate('.contactSearchResult','mouseleave',function(){
        var _this = $(this);
        setTimeout(function(){
            _this.remove();
        },1000)
    })


    $('.WindowMask').delegate('.privateConvers .contactSearchResult li','click',function(){
        var targetId = $(this).attr('id');
        var targetLi = $('#contactBox .contactsList').find('li.member[id="'+targetId+'"]');
        $('.contactSearchResult').remove();
        $('.contactsSearch').val('');
        targetLi.find('.dialogCheckBox').click();
    })
    $('.contactBox').delegate('.contactSearchResult .dialogCheckBox','click',function(){
        if($(this).parents('li').attr('editable')=='true'){
            var targetId = $(this).closest('li').attr('id');
            var targetLi = $('#contactBox .contactsList').find('li.member[id="'+targetId+'"]');

            if($(this).hasClass('CheckBoxChecked')){//现状态是选中，取消选中（树中）并从已选联系人中移出
                //converseACount.push(targetId);
                targetLi.find('.dialogCheckBox').removeClass('CheckBoxChecked');
                deleteElement(converseACount,targetId)
                changeSelected(converseACount);
            }else{//现状态是未选中，选中（树中）并添加到已选联系人
                targetLi.find('.dialogCheckBox').addClass('CheckBoxChecked');
                converseACount.push(targetId);
                changeSelected(converseACount);
            }
        }
    })

    $('.contactsSearch').focus(function(){
        var sAccount = localStorage.getItem('account');
        var sdata = localStorage.getItem('datas');
        var account = JSON.parse(sAccount).account;
        var accountID = JSON.parse(sdata).id;
        var _this = $(this);
        _this.on('input',function(){
                $('.contactSearchResult').remove();
                var keyWord = _this.val();
                sendAjax('member!searchUser',{account:keyWord},function(data){
                    var datas = JSON.parse(data);
                    var parentDom = $('#contactBox');
                    if(datas.length==0){
                        //没有用户
                        var sHTML = '';
                        sHTML = '<div class="contactSearchResult">没有搜索结果</div>'
                        parentDom.append($(sHTML));
                    }else if(datas.length!=0){
                        //生成搜索结果
                        var sHTML = '';
                        $('.contactSearchResult').remove();
                        for(var i = 0;i<datas.length;i++){
                            if(hasItem(converseACount,datas[i].id)){
                                var className = 'CheckBoxChecked'
                            }else{
                                var className = ''
                            }
                            if(accountID==datas[i].id){
                                var editable = 'false';
                            }else{
                                var editable = 'true';
                            }
                            sHTML += '<li account="'+datas[i].account+'" id="'+datas[i].id+'" class="member" editable="'+editable+'">' +
                            '<div level="1" class="department">' +
                            '<span style="height: 20px;width: 0px;display:inline-block;float: left;"></span>' +
                            '<span class="chatLeftIcon dialogCheckBox '+className+'"></span>' +
                            '<span class="dialogGroupName">'+datas[i].name+'</span>' +
                            '</div>' +
                            '</li>';
                        }
                        var pHTML = '<ul class="contactSearchResult">'+sHTML+'</ul>';
                        parentDom.append($(pHTML));

                    }else{
                        console.log(datas.text);
                    }

                })
        })
    })


    window.converseACount = [];
    //删除群租种的成员
    $('.selectedList').delegate('.deleteMemberIcon','click',function(){
        if($(this).parents('li').attr('editable')=='true'){
            var name = $(this).prev().html();
            var memberID = $(this).parent().attr('memberID');
            deleteElement(converseACount,memberID);
            $('.contactsList li.member[id='+memberID+']').find('.dialogCheckBox').removeClass('CheckBoxChecked');
            changeSelected(converseACount);
        }
    })

    //弹窗中的树形结构的收起展开
    $('.conversWindow').delegate('.dialogCollspan','click',function(){
        $(this).closest('li').next('ul').slideToggle();
        $(this).toggleClass('dialogCollspanC','dialogCollspanO');
    });

    //弹窗中树形结构的选中
    $('.conversWindow').undelegate('.dialogCheckBox','click')
    $('.conversWindow').delegate('.dialogCheckBox','click',function(){
        if($(this).parents('li').attr('editable')=='true'){
            converseACount = [];
            if($('.selectedList').find('li').length!=0){
                var selected = $('.selectedList').find('li');
                for(var i = 0;i<selected.length;i++){
                    var account = $(selected[i]).attr('memberid')
                    if(!$(this).hasClass('CheckBoxChecked')){
                        converseACount.push(account);

                    }else{
                        deleteElement(converseACount,account)

                    }
                }
            }


            //bPrivate是私聊还是群聊
            var bPrivate = $(this).parents('.conversWindow').hasClass('privateConvers');
            var id =  $(this).closest('li').attr('id');
            var hasMember = $(this).closest('li').hasClass('member')||$(this).closest('li').hasClass('group');
            if(bPrivate&&hasMember){//创建个人的聊天页面 单选模式
                var account =  $(this).closest('li').attr('account');
                $('.dialogCheckBox').removeClass('CheckBoxChecked');
                $(this).addClass('CheckBoxChecked');
                converseACount.push(account);

            }else{//创建群组的聊天 多选模式
                //首先自己的选中状态

                if($(this).hasClass('CheckBoxChecked')){
                    $(this).removeClass('CheckBoxChecked');
                }else{
                    $(this).addClass('CheckBoxChecked');
                }

                //然后子级的选中状态
                var member = $(this).closest('li').next('ul').find('div.member');
                if(member){
                    for(var i = 0;i<member.length;i++){
                        $(member[i]).parent().attr('account');
                    }
                }
                if($(this).hasClass('CheckBoxChecked')){//选中的push
                    $(this).closest('li').next('ul').find('.dialogCheckBox').addClass('CheckBoxChecked');
                }else{//未选中，移出数组
                    var $dialogCheckBox = $(this).closest('li').next('ul').find('.dialogCheckBox');
                    for(var i = 0;i<$dialogCheckBox.length;i++){
                        if($($dialogCheckBox[i]).closest('li').attr('editable')=='true'){
                            $($dialogCheckBox[i]).removeClass('CheckBoxChecked');
                        }
                    }
                }


                //父级的选中状态
                var sonBox = $(this).closest('ul').find('.dialogCheckBox');
                var allBox = 0;
                for(var i = 0;i<sonBox.length;i++){
                    if($(sonBox[i]).hasClass('CheckBoxChecked')){
                        allBox++;
                    }
                }
                if(allBox==0){//全没选中
                    $(this).closest('ul').prev('li').find('.chatLeftIcon').removeClass('CheckBoxChecked');
                }else if(allBox==sonBox.length){//全选中
                    $(this).closest('ul').prev('li').find('.chatLeftIcon').removeClass('CheckBoxChecked');
                }
                var dialogCheckBox = $('.contactBox').find('.dialogCheckBox');

                for(var i = 0;i<dialogCheckBox.length;i++){
                    var diacjeck = $(dialogCheckBox[i])
                    if(diacjeck.hasClass('CheckBoxChecked')&&diacjeck.closest('div').hasClass('member')){
                        var account = diacjeck.closest('li').attr('id');
                        var name = diacjeck.next().html();
                        var bhas = arrHasElement(converseACount,account);
                        if(!bhas){
                            converseACount.push(account);
                        }
                    }
                }
                changeSelected(converseACount);
            }
        }
    })
})


//数组中是否包含某个元素
function arrHasElement(converseACount,account){
    var bHas = false;
    for(var i = 0;i<converseACount.length;i++){
        if(converseACount[i]==account){
            bHas = true;
            break;
        }
    }
    return bHas;
}

function hasItem(parentLevelarr,parentLevel){
    var bhas = 0;
    for(i = 0;i<parentLevelarr.length;i++){
        if(parentLevel==parentLevelarr[i]){
            bhas = 1;
        }
    }
    return bhas;
}

function creatDialogTree(data,className,title,callback,selected,addGroupIn){
    $('.WindowMask').find('.conversWindow').attr('class','conversWindow '+className);
    $('.WindowMask').find('.dialogHeader').html(title);
    $('.WindowMask').show();
    var sHTML = '';
    var level = 0;
    var dataAll = localStorage.getItem('datas');
    var datasAll = JSON.parse(dataAll);
    var userID = datasAll.id;

    var HTML = DialogTreeLoop(data,sHTML,level,userID);
    $('.contactsList').html('');
    if(addGroupIn&&addGroupIn=='group'){
        var sGroupHtml = DialogGroupLoop();
    }

    $('.contactsList').append($(sGroupHtml));
    $('.contactsList').append($(HTML));
    var dom = $('.selectedList ul');

    selected = unique3(selected);
    if(selected){
        console.log('selected',selected);
        //找到自己的id 不用放到左侧管理

        var sHTML = '';

        converseACount = [];
        for(var i = 0;i<selected.length;i++){
            converseACount.push(selected[i]);
            var targetList = searchFromList(1,selected[i]);
            if(targetList){
                if(selected[i]==userID){
                    var editable = false;
                    $('.contactsList').find('li[account='+targetList.account+'][id='+selected[i]+']').find('.dialogCheckBox').addClass('CheckBoxChecked');
                    $('.contactsList').find('li[account='+targetList.account+'][id='+selected[i]+']').attr('editable','false');

                }else {
                    var editable = true;
                    $('.contactsList').find('li[account='+targetList.account+'][id='+selected[i]+']').find('.dialogCheckBox').addClass('CheckBoxChecked');
                }
                sHTML += '<li memberID="'+selected[i]+'" editable="'+editable+'"><span class="memberName">'+targetList.name+'</span><span class="chatLeftIcon deleteMemberIcon"></span></li>'
            }
        }
        dom.html(sHTML);
        selectedNum(selected)
    }else{
        dom.html('');
    }
    //console.log(HTML);

    $('.manageSure').unbind('click');
    $('.manageSure').click(function(){
        callback&&callback();
    });
}

function DialogGroupLoop(){
    var sGroupInfo = localStorage.getItem('groupInfo');
    if(sGroupInfo){
        var oGroupInfo = JSON.parse(sGroupInfo);
        var aGroupList = oGroupInfo.text;
        var sHTML = '';
        sHTML += '<ul>';
        sHTML += '<li  id="1" class="department" editable="true">' +
                    '<div level="1" class="department">' +
                    '<span style="height: 20px;width: 0px;display:inline-block;float: left;">' +
                    '</span><span class="dialogCollspan chatLeftIcon dialogCollspanO">' +
                    '</span><span class="chatLeftIcon dialogCheckBox"></span>' +
                    '<span class="dialogGroupName">我的群租</span></div></li><ul>';
        console.log(aGroupList);
        for(var i = 0;i<aGroupList.length;i++){
            aGroupList[i];
            sHTML += '<li account="'+aGroupList[i].account+'" id="'+aGroupList[i].GID+'" class="group" editable="true">' +
                        '<div level="1" class="group">' +
                            '<span style="height: 20px;width: 22px;display:inline-block;float: left;">' +
                            '</span>' +
                            '<span class="dialogCollspan chatLeftIcon"></span><span class="chatLeftIcon dialogCheckBox"></span>' +
                            '<span class="dialogGroupName">'+aGroupList[i].name+'</span>' +
                        '</div>' +
                    '</li>'
        }
        sHTML += '</ul></ul>';
    }
    return sHTML;
}

//删除数组中的某个对象
function deleteElement(arr,name,value){
    for(var i = 0;i<arr.length;i++){
        var curObj = arr[i];
        for(var key in curObj){
            if(curObj[key]==value&&key==name){
                deleteElement(arr,curObj);
            }
        }
    }
    return curObj;
}

//修改select里面的成员 以及已选联系人数量
function changeSelected(converseACount){
    //var sAccount = localStorage.getItem('account');
    var sdata = localStorage.getItem('datas');
    //var account = JSON.parse(sAccount).account;
    var accountID = JSON.parse(sdata).id;
    var dom = $('.selectedList ul');
    var sHTML = '';
    for(var i = 0;i<converseACount.length;i++){
        if(accountID==converseACount[i]){
            var editable = 'false';
        }else{
            var editable = 'true';
        }
        var name = searchFromList(1,converseACount[i]).name;
        sHTML+='<li memberID="'+converseACount[i]+'" editable="'+editable+'"><span class="memberName">'+name+'</span><span class="chatLeftIcon deleteMemberIcon"></span></li>'
    }
    dom.html($(sHTML));
    selectedNum(converseACount);
}

function selectedNum(converseACount){
    var selectNum = converseACount.length;
    var parentDom = $('.selectedContactOuter .outerTitle em');
    var memberCount = findMemberCount()
    var sHTML = '('+selectNum+'/'+memberCount+')';
    parentDom.html(sHTML);
}


function deleteElement(converseACount,account){
    console.log(converseACount);
    for(var i = 0;i<converseACount.length;i++){
        if(converseACount[i]==account){
            converseACount.splice(i,1);
            break;
        }
    }
}