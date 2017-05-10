/**
 * Created by zhu_jq on 2017/1/4.
 */
window.onload = function(){

    var saveSigninData = window.localStorage.saveSigninData;
    if(saveSigninData){
        saveSigninData =JSON.parse(saveSigninData);
        $('#organCode').val(saveSigninData.organCode);
        $('#username').val(saveSigninData.account);

    }
    //点击发送验证码
    $('#pwdIn').unbind('focus');
    $('#pwdIn').focus(function(){
        $(this).keypress(function(event) {
            if (event.which == 13) {
                signin();
            }
        })
    })
    window.clickFlag = true;
    $('.SendCheakCode').click(function(){
        //fSendCheakCode();
        if(clickFlag){
            var _this = $(this);
            var phoneNum = $('#phoneNum').val();
            var isPhone = phoneNum.match(/^1[3|4|5|8][0-9]\d{4,8}$/)
            if(phoneNum&&isPhone){
                sendAjax('system!requestText',{phone:phoneNum},function(){
                    console.log('验证码发送成功');
                    //_this.html();
                    //成功后开始倒计时
                    clickFlag = false;
                    countDown(_this,clickFlag);

                })
            }
        }
    })
    $('.SendCheakCode').click(function(){
        //fSendCheakCode();
        var phoneNum = $('#phoneNum').val();
        //var data = JSON.stringify({phoneNum:phoneNum});
        sendAjax('system!requestText',{phone:phoneNum},function(){
            console.log('验证码发送成功')
        })
    })
}


/*
* 通用AJAX
*/
function sendAjax(url,data,callback){
    $.ajax({
        type: "POST",
        url: url,
        data:data,
        success: function(data){
            callback && callback(data);
        }
    })
}

function countDown(curDom,clickFlag){
    var maxSecond = 60;

    var changeSec = function(sec){
        if(sec==0){
            clearInterval(timer);
            window.clickFlag = true;
            $(curDom).html('发送验证码');

        }else{
            $(curDom).html(sec+'s后再次发送');

        }
    }

    var timer = setInterval(function(){
        maxSecond--;
        changeSec(maxSecond);
    },1000)
}

/*
*
* 跳转到第二部
* */
function fToStep2(dom){
    var phoneNum = $('#phoneNum').val();
    var textcode = $('#checkCode').val();
    //var data = JSON.stringify({'phone':phoneNum,textcode:textcode});
    $('.sealtalk-forgetpassword').attr('account',phoneNum);
    sendAjax('system!testText',{phone:phoneNum,textcode:textcode},function(data){
        if(data){
            var datas = JSON.parse(data);
            if(datas.code=='1'){
                fToNext(dom)
            }else if(datas.code=='0'){
                new Window().alert({
                    title   : '',
                    content : '验证码错误！',
                    hasCloseBtn : false,
                    hasImg : true,
                    textForSureBtn : false,
                    textForcancleBtn : false,
                    autoHide:true
                });
            }
        }
    });
}
function fToStep3(dom){
    var newpwd = $('#newpassword').val();
    var comparepwd = $('#newpasswordCertain').val();
    var newPWD = hex_md5(newpwd);
    var comparePWD = hex_md5(comparepwd);
    if(newPWD!=comparePWD){
        alert('两次密码不一致')
    }else{
        var account = $('.sealtalk-forgetpassword').attr('account');
        sendAjax('system!newPassword',{newpwd:newPWD,comparepwd:comparePWD,account:account,type:'web'},function(data){
            if(data){
                var datas = JSON.parse(data);
                if(datas.code=='1'){
                    fToNext(dom)
                }
            }
        });
    }



}


/*
*
* 发送验证码
*
*/
//function fSendCheakCode(){
//    var phoneNum = $('#username').val();
//    var data = JSON.stringify({'phoneNum':phoneNum})
//    sendAjax('system!requestText',data);
//}

/*
*
* 登录
*
*/
function signin(){
    var accout = $('#username').val();
    var userpwd = hex_md5($('#pwdIn').val());
    var organCode = $('#organCode').val();
    var data = {account:accout,userpwd:userpwd,organCode:organCode};
    //验证
    sendAjax('system!afterLogin',data,function(datas){

        var datas = JSON.parse(datas);
        if(datas &&	datas.code == 1){
            data.token = datas.text.token;
            var saveSigninData = {account:accout,organCode:organCode}
            saveSigninData = JSON.stringify(saveSigninData);
            window.localStorage.saveSigninData = saveSigninData;
            window.localStorage.account=JSON.stringify(datas.text);
            window.location.href = 'page/cms/12.jsp';
        } else {
            new Window().alert({
                title   : '',
                content : '用户名或密码输入错误！',
                hasCloseBtn : false,
                hasImg : true,
                textForSureBtn : false,
                textForcancleBtn : false,
                autoHide:true
            });
        }
    },function(){
    });
}
/*
*
*跳转到登录页面
*
*/
function fBackToSignin(){
    var origin = window.location.origin
    window.location.href = origin+'/organ/system!login';
}

function addMD5(){
    var userpwd = hex_md5($('#pwdIn').val());
    $('#pwdIn').val(userpwd);
}


/*
*
*跳转到下一页处理
*
*/
function fToNext(dom){
    console.log(dom);
    var sStep = $(dom).closest('.form-inline').attr('step');
    var aStep = sStep.split('-');
    var i = aStep[1]?aStep[1]:undefined;
    if(i){
        i++;
        i>=4?fBackToSignin():fToNextStep(i);
    }
}

/*
 *
 *跳转到上一页处理
 *
 */
function fToPrev(dom){
    console.log(dom);
    var sStep = $(dom).closest('.form-inline').attr('step');
    var aStep = sStep.split('-');
    var i = aStep[1]?aStep[1]:undefined;
    if(i){
        i--;
        i<=0?fBackToSignin():fToNextStep(i);
    }
}

/*
 *
 *跳转
 *
 */
function fToNextStep(i){
    $('.resetStep').addClass('chatHide');
    $('[step=resetStep-'+i+']').closest('.resetStep').removeClass('chatHide');
}


/*
*
* 跳转到授权登陆页面
*
* */
//function jumpToAuth(){
//
//}