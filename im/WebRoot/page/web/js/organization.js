/**
 * Created by zhu_jq on 2017/1/14.
 */
$(function(){
    $('.BreadcrumbsOuter').undelegate('ul li','click')
    $('.BreadcrumbsOuter').delegate('ul li','click',function(){
        var targetID = $(this).attr('id');
        var className = $(this).attr('class');
        console.log('.organizationList li.'+className+'[id='+targetID+']')
        var targetNode = $('.organizationList li.'+className+'[id='+targetID+']');
        $('.organizationList').find('li').removeClass('active');
        targetNode.addClass('active');
        targetNode.click();
    })

    $('.orgNavClick2,.orgNavClick1').delegate('.sendMsg','click',function(){
        //跳转到消息下面的常用联系人
        //$('.chatHeaderMenu li')[0].click();
        //$('.chatContent ul li')[1].click();
        //会话区显示
    })

    //点击查看组织结构图
    $('.seeOrgnizeTree').click(function(){
        seeOrgnizeTree();
    })

    //点击组织通讯录
    $('.organizationList').undelegate('li','click')
    $('.organizationList').delegate('li','click',function(){
        $('.organizationList').find('li').removeClass('active')
        $(this).addClass('active');
        var state = $(this).hasClass('member');
        var id = $(this).attr('id');
        //从list中找到点击的这条信息
        $('.orgNavClick').addClass('chatHide');
        var SHTML = BreadcrumbGuid($(this));
        $('.Breadcrumbs').html(SHTML);
        if(state){//点击的是成员
            var data = searchFromList(1,id);
            var sHTML = changeClick2Content(data);
            $('.orgNavClick2').html(sHTML);
            $('.orgNavClick2').removeClass('chatHide');
            $('.BreadcrumbsOuter').removeClass('chatHide')
            //更换面包屑导航
        }else{//点击的是部门
            //branch!getBranchMember查询部门结构
            sendAjax('branch!getBranchMember',{branchId:id},function(data){
                var datas = JSON.parse(data);
                console.log(datas);
                var sHTML = changeClick1Content(datas);
                $('.orgNavClick1').html(sHTML);
                $('.orgNavClick1').removeClass('chatHide');
                $('.BreadcrumbsOuter').removeClass('chatHide');

            })
        }
    })

    //搜索
    $('.defaultText').unbind('click');
    $('.defaultText').click(function(){
        $('.searchInput').focus();
    })
    $('.searchInput').focus(function(){
        $('.defaultText').hide();
        $(this).css({backgroundPosition:'-380px -365px'});
        $(this).unbind('keypress');
        $(this).on('input',function(){
            var inputVal = $(this).val();
            if(inputVal){
                sendAjax('member!searchUser',{account:inputVal},function(data){
                    var datas = JSON.parse(data);
                    var parentDom = $('.orgnized');
                    if(datas.length==0){
                        //没有用户
                        if($('.searchResult').find('.searchNoResult').length==0){
                            $('.searchResult').remove();
                            var sHTML = '<div class="searchResult">'+
                                '<ul class="searchResultUL">'+
                                '<li class="searchNoResult">'+
                                '<span>没有搜索结果</span>'+
                                '</li>'+
                                '</ul>'+
                                '</div>';
                            parentDom.append($(sHTML));
                            $('.searchResult').show();
                        }
                    }else if(datas.length!=0){
                        //生成搜索结果
                        $('.searchResult').remove();
                        var liHTML = '';

                        for(var i = 0;i<datas.length;i++){
                            if(datas[i].logo){
                                var imgsrc = globalVar.imgSrc+datas[i].logo;
                            }else{
                                imgsrc = globalVar.defaultLogo;
                            }
                            var position = datas[i].position?'('+datas[i].positionname+')':'';
                            liHTML += '<li targetaccount="'+datas[i].account+'" targetid="'+datas[i].id+'"><img src="'+imgsrc+'"/>'+datas[i].name+position+'</li>'
                        }
                        var sHTML = ' <div class="searchResult">'+
                                    '<ul class="searchResultUL">'+liHTML+
                                    '</ul>'+
                                    '</div>'
                        parentDom.append($(sHTML));
                        $('.searchResult').show();
                    }else{
                        console.log(datas.text);
                    }

                })
            }
        })
    });
    //targetAccount account

    $('.orgnized').delegate('.searchResult','mouseleave',function(){
        var _this = $(this)

        setTimeout(function(){
            _this.remove();
        },1000)
    })
    $('.orgnized').delegate('.searchResultUL li','click',function(){
        var targetAccount = $(this).attr('targetaccount');
        var account = localStorage.getItem('account');
        if(account){
            var accpunts = JSON.parse(account);
            var account = accpunts.text.account
        }
        if($('.usualChatList').find('li[account='+targetAccount+']').length==0){


            addFriendAndRefreshList(account,targetAccount)
            //sendAjax('friend!addFriend',{account:account,friend:targetAccount},function(data){
            //    //var datas = JSON.parse(data);
            //    //console.log(data);
            //    //if(datas.code==1){
            //    //刷新常用联系人
            //    getMemberFriends(account,function(){
            //        $('.searchResult').remove();
            //        $('.chatHeaderMenu li')[0].click();
            //        $('.chatMenu .chatLeftIcon')[1].click();
            //        var targetDon = $('.usualChatList').find('li')
            //        targetDon.removeClass('active');
            //        var targetMember = $('.usualChatList').find('li[account='+targetAccount+']');
            //        targetMember.addClass('active').click();
            //        var targetID = targetMember.attr('targetid');
            //        var targeType = 'PRIVATE';
            //        conversationSelf(targetID,targeType);
            //    });
            //})
        }else{
            $('.searchResult').remove();
            jumpToFriendListOpen(targetAccount)
            //$('.chatHeaderMenu li')[0].click();
            //$('.chatMenu .chatLeftIcon')[1].click();
            //var targetDon = $('.usualChatList').find('li')
            //targetDon.removeClass('active');
            //var targetMember = $('.usualChatList').find('li[account='+targetAccount+']');
            //targetMember.addClass('active').click();
            //var targetID = targetMember.attr('targetid');
            //var targeType = 'PRIVATE';
            //conversationSelf(targetID,targeType);
        }
    })



    $('.searchInput').blur(function(){
        $(this).val('');
        $('.defaultText').show();
        $(this).css({backgroundPosition:'-281px -365px'});
        setTimeout(function(){
            $('.searchResult').remove();
        },1000)
    });
})