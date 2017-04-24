/**
 * Created by zhu_jq on 2017/3/13.
 */
function loginOA(){
    var appId = globalVar.appID;
    sendAjax('auth!getTempTokenSceneTwo',{appId:appId},function(data){
        if(data){
            var datas = JSON.parse(data);
            if(datas&&datas.code=='200'){
                //console.log('==============');
                var unAuthToken = datas.text;
                var appid = globalVar.appID;
                sendAjax('auth!reqAuthorizeTwo',{unAuthToken:unAuthToken,appId:appid},function(data){
                    if(data){
                        var datas = JSON.parse(data);
                        if(datas&&datas.code=='200'){
                            //console.log('==============');
                            var authToken = datas.text;
                            var secret = globalVar.secret;
                            sendAjax('auth!getRealToken',{authToken:authToken,secret:secret},function(data){
                                if(data){
                                    var datas = JSON.parse(data);
                                    if(datas&&datas.code=='200'){
                                        //console.log('==============');
                                        var visitToken = datas.text;
                                        sendAjax('auth!getAuthResource',{visitToken:visitToken},function(data){
                                            if(data){
                                                var datas = JSON.parse(data);
                                                if(datas&&datas.code=='200'){
                                                    console.log('==============');
                                                    //var visitToken = datas.text;
                                                }
                                            }
                                        })
                                    }
                                }
                            })
                        }
                    }
                })
            }
        }
    })
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