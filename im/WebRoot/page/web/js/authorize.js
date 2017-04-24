/**
 * Created by zhu_jq on 2017/3/10.
 */
$(function(){
    window.onload=window.onresize=function(){
        document.documentElement.style.fontSize=20*document.documentElement.clientWidth/375+'px';
    };

    var sURL = window.location.href;
    var erroeText = UrlParamHash(sURL)
    if(sURL.indexOf('err')!=-1){
        $('.login-tips').css('visibility','visible');
    }

    $('.authorize-signin').click(function(){
        var userName = $('.authorize-user');
        var userPsd = $('.authorize-psd');
        var info = '';;
        var flag1 = false;
        var flag2 = false;
        var checkMem1 = $('.checkMem1');
        var checkMem2 = $('.checkMem2')

        if(checkMem1.find('.dialogCheckBox').hasClass('CheckBoxChecked')){
            flag1 =true
        }
        if(checkMem1.find('.dialogCheckBox').hasClass('CheckBoxChecked')){
            flag2 = true;
        }
        if(flag1&&flag2){
            info = 3;
        }else if(flag1&&!flag2){
            info = 1;
        }else if(!flag1&&flag2){
            info = 2;
        }else{
            info = 0;
        }
        var userpwd = hex_md5($('.authorize-psd').val());
        $('.authorize-psd').val(userpwd);
        var sURL = window.location.href;
        var unAuthToken = UrlParamHash(sURL).unAuthToken;
        var appid = globalVar.appID;
        var sHTML = '<input class="authorize-user" name="info" value="'+info+'" type="text" style="display:none">'
        sHTML += '<input class="authorize-user" name="unAuthToken" value="'+unAuthToken+'" type="text" style="display:none">'
        sHTML += '<input class="authorize-user" name="appId" value="'+appid+'" type="text" style="display:none">'
        $('.authorize-submit').append($(sHTML));
        $('.authorize-submit')[0].submit();
        //document.submit();
    })
})

function UrlParamHash(url) {
    var params = [], h;
    var hash = url.slice(url.indexOf("?") + 1).split('&');
    for (var i = 0; i < hash.length; i++) {
        h = hash[i].split("=");
        params.push(h[0]);
        params[h[0]] = h[1];
    }
    return params;
}