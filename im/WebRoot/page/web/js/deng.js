/**
 * Created by zhu_jq on 2017/3/13.
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
function jumoToAuth(){
    var appid = globalVar.appID;
    sendAjax('auth!getTempTokenSceneOne',{appId:appid},function(data){
        if(data){
            var datas = JSON.parse(data);
            if(datas&&datas.code=='200'){
                window.location.href = 'auth!redirectLogin?unAuthToken='+datas.text;
            }
        }
    })
}