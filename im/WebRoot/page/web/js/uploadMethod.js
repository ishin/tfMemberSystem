/**
* Created by zhu_jq on 2017/1/16.
*/
$(function(){
    //拖拽上传
    var messageContent  =document.getElementById('message-content')
    messageContent.addEventListener('drop', function(event){
        if (event.preventDefault) event.preventDefault();
    })
    var uploading = false;
    //点击button上传
    var token = globalVar.qiniuTOKEN;
    var config = {
        domain: globalVar.qiniuDOMAN,                              // default : '' ,必须设置文件服务器地址。
        file_data_name  : 'file',                                       // default : file , 文件对象的 key 。
        base64_size     : 4096,
        max_file_size: '256mb',                                          //最大文件体积限制
        chunk_size      : '4M',                                           // default : 10 单位 MB 。
        multi_parmas    : { },                                          // default : {} 扩展上传属性 。
        query           : { },                                          // default : {} 扩展 url 参数 e.g. http://rongcloud.cn?name=zhangsan 。
        support_options : true,                                         // default : true, 文件服务器不支持 OPTIONS 请求需设置为 false。
        getToken: function(callback){
            callback(token);
        }
    };
//文件拖拽上传
    var messageContent = document.getElementById('message-content');
    messageContent.ondragover = function(e){
        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;
    }
    messageContent.ondrop = function(e){
        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;

        var limit = $('body').attr('limit');
        if(limit.indexOf('ltszwjsc')==-1){
            new Window().alert({
                title   : '',
                content : '您无文件发送权限！',
                hasCloseBtn : false,
                hasImg : true,
                textForSureBtn : false,
                textForcancleBtn : false,
                autoHide:true
            });
        }else{
            var _this = this;
            var _file = e.dataTransfer.files[0];//取得文件对象
            var filedetail = {};
            filedetail.name = _file.name;
            filedetail.uniqueName = _file.uniqueName;
            filedetail.size = _file.size;
            filedetail.type = _file.type;
            //var content = JSON.stringify(filedetail);
            var extra = "uploadFile";
            //{content:"hello",extra:"附加信息"}
            var targetId = $(this).parents('.mesContainer').attr('targetid');
            var targetType = $(this).parents('.mesContainer').attr('targettype');

            UploadClient.initImage(config, function(uploadFile){
                var callback = {
                    onError: function (errorCode) {
                        //console.log(errorCode);
                        //uploading = false;
                    },
                    onProgress: function (loaded, total) {
                        //console.log('onProgress', loaded, total, this);
                        var className = this._self.uniqueTime;
                        var percent = Math.floor(loaded / total * 100);
                        var progressContent = $('#up_precent[uniquetime="'+className+'"]');
                        progressContent.width(percent + '%');
                        return percent;
                    },
                    onCompleted: function (data) {
                        var className = this._self.uniqueTime;
                        var downloadLink = returnDLLink(data.filename);
                        var filedetail = {};
                        filedetail.type = this._self.type;

                        filedetail.fileUrl = downloadLink;
                        var targetId = this._self.targetId;
                        var targetType = this._self.targetType;
                        var content = filedetail;
                        if(this._self.type=='image/png'||this._self.type=='image/jpeg'){
                            filedetail.base64Str = data.thumbnail;// 图片转为可以使用 HTML5 的 FileReader 或者 canvas 也可以上传到后台进行转换。
                            filedetail.imageUri = downloadLink;
                            $('img[uniquetime="'+className+'"]').attr('src',downloadLink);
                            $('img[uniquetime="'+className+'"]').on('load',function(){
                                var eDom=document.querySelector('#chatBox .mr-chatview');
                                eDom.scrollTop = eDom.scrollHeight;
                            })
                            if(data.thumbnail){
                                sendByRongImg(content,targetId,targetType,this._self.uniqueTime);
                            }
                        }else{
                            filedetail.name = this._self.name;
                            filedetail.uniqueTime = this._self.uniqueTime;
                            filedetail.size = this._self.size;
                            filedetail.type = this._self.type;
                            filedetail.filename = data.filename;
                            $('#up_process[uniquetime="'+className+'"]').parent().next().attr('href',downloadLink);
                            sendByRongFile(content,targetId,targetType,'',this._self.uniqueTime);
                        }
                    },
                    _self: _file
                }
                _file.callback = callback;
                sendFile(_file,_this,function(){
                    //显示到盒子里
                    uploadFile.upload(_file, callback);
                });
            });


        }

    }

    var $file = $(".comment-pic-upd");
    $file.on('change',function(){
        var limit = $('body').attr('limit');
        //var oLimit = JSON.parse(limit);
        if(limit.indexOf('ltszwjsc')==-1){
            return false;
        }else{
            var _this = this;
            var _file = this.files[0];
            $(this).val("");
            UploadClient.initImage(config, function(uploadFile){
                //uploading = true;
                var callback = {
                    onError: function (errorCode) {
                        //console.log(errorCode);
                        //uploading = false;
                    },
                    onProgress: function (loaded, total) {
                        //console.log('onProgress', loaded, total, this);
                        var className = this._self.uniqueTime;
                        var percent = Math.floor(loaded / total * 100);
                        var progressContent = $('#up_precent[uniquetime="'+className+'"]');
                        progressContent.width(percent + '%');
                        return percent;
                    },
                    onCompleted: function (data) {
                        var className = this._self.uniqueTime;
                        var downloadLink = returnDLLink(data.filename);
                        var filedetail = {};
                        filedetail.type = this._self.type;
                        var fileName = data.filename.split('.')[0];
                        filedetail.fileUrl = downloadLink;
                        var targetId = this._self.targetId;
                        var targetType = this._self.targetType;
                        var content = filedetail;
                        if(this._self.type=='image/png'||this._self.type=='image/jpeg'){
                            var image = new Image();
                            image.src = downloadLink;

                            //filedetail.base64Str = getBase64Image(downloadLink);// 图片转为可以使用 HTML5 的 FileReader 或者 canvas 也可以上传到后台进行转换。
                            filedetail.imageUri = downloadLink;
                            $('a[fileName='+className+']').attr('fileName',fileName);
                            $('img[uniquetime="'+className+'"]').attr('src',downloadLink);
                            $('img[uniquetime="'+className+'"]').on('load',function(){
                                var eDom=document.querySelector('#chatBox .mr-chatview');
                                eDom.scrollTop = eDom.scrollHeight;
                            })
                            if(data.thumbnail){
                                image.onload = function(){
                                    content.base64Str = data.thumbnail;
                                    sendByRongImg(content,targetId,targetType,className);
                                }
                            }
                        }else{
                            filedetail.name = this._self.name;
                            filedetail.uniqueTime = this._self.uniqueTime;
                            filedetail.size = this._self.size;
                            filedetail.type = this._self.type;
                            filedetail.filename = data.filename;
                            $('a[fileName='+filedetail.uniqueTime+']').attr('fileName',fileName);
                            $('a[fileName='+fileName+']').attr('href',downloadLink);
                            $('#up_process[uniquetime="'+filedetail.uniqueTime+'"]').remove();
                            sendByRongFile(content,targetId,targetType,'',this._self.uniqueTime);

                        }
                        //发送消息

                        //var extra = "uploadFile";
                        //console.log(data);
                        //uploading = false;
                    },
                    _self: _file
                }
                _file.callback = callback;
                sendFile(_file,_this,function(){
                    //显示到盒子里
                    uploadFile.upload(_file, callback);
                });
            });
        }

    })
})



function getBase64Image(img) {
    var canvas = document.createElement("canvas");
    canvas.width = img.width;
    canvas.height = img.height;
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0, img.width, img.height);
    var ext = img.src.substring(img.src.lastIndexOf(".")+1).toLowerCase();
    var dataURL = canvas.toDataURL("image/"+ext);
    return dataURL;
}

function sendFile(_file,_this,callback){
    var filedetail = {};
    filedetail.name = _file.name;
    _file.uniqueTime = new Date().getTime();
    filedetail.uniqueTime = _file.uniqueTime;
    filedetail.size = _file.size;
    filedetail.type = _file.type;
    var targetId = $(_this).parents('.mesContainer').attr('targetid');
    var targetType = $(_this).parents('.mesContainer').attr('targettype');
    _file.targetId = targetId;
    _file.targetType = targetType;
    var content = JSON.stringify(filedetail);
    var extra = "uploadFile";
    sendMsg(content,targetId,targetType,extra,callback);
}

function returnDLLink(filename){
    //debugger;
    return globalVar.qiniuDOWNLOAD+filename+'?attname='+filename;
}
//function returnOPLink(filename){
//    //debugger;
//    return globalVar.qiniuDOWNLOAD+filename;
//}











































