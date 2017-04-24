/**
 * Created by zhu_jq on 2017/3/13.
 */
$(function(){
    //重新获取访问令牌
    var secret = globalVar.secret;
    var sURL = window.location.href;
    var authToken = UrlParamHash(sURL).authToken
    sendAjax('auth!getRealToken',{authToken:authToken,secret:secret},function(data){
        if(data){
            var datas = JSON.parse(data);
            if(datas&&datas.code=='200'){
                var visitToken = datas.text;
                sendAjax('auth!getAuthResource',{visitToken:visitToken},function(data){
                    if(data){
                        var datas = JSON.parse(data);
                        if(datas&&datas.code=='200'){
                            //console.log('0000000000000');
                            document.write(datas.text)
                        }
                    }
                })
            }
        }
    })
})
//function(){
//
//}
//
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
//function jumoToAuth(){
//    var appid = globalVar.appID;
//    sendAjax('auth!getTempTokenSceneOne',{appId:appid},function(data){
//        if(data){
//            var datas = JSON.parse(data);
//            if(datas&&datas.code=='200'){
//                window.location.href = 'auth!redirectLogin?unAuthToken='+datas.text;
//            }
//        }
//    })
//}