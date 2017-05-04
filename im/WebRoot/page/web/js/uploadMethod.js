/**
* Created by zhu_jq on 2017/1/16.
*/
$(function(){
    //拖拽上传

    $(document).on({
        dragleave:function(e){
            if (e.preventDefault) e.preventDefault();
            else e.returnValue = false;
        },
        drop:function(e){
            if (e.preventDefault) e.preventDefault();
            else e.returnValue = false;
        },
        dragenter:function(e){
            if (e.preventDefault) e.preventDefault();
            else e.returnValue = false;
        },
        dragover:function(e){
            if (e.preventDefault) e.preventDefault();
            else e.returnValue = false;
        }
    })
    //var messageContent  =document.getElementById('groupContainer')
    //messageContent.addEventListener('drop', function(event){
    //    if (event.preventDefault) event.preventDefault();
    //})
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
        },
        init: {
            'FilesAdded': function(up, files) {
                plupload.each(files, function(file) {
                    // 文件添加进队列后，处理相关的事情
                });
            },
            'BeforeUpload': function(up, file) {
                // 每个文件上传前，处理相关的事情
            },
            'UploadProgress': function(up, file) {
                // 每个文件上传时，处理相关的事情
            },
            'FileUploaded': function(up, file, info) {
                // 每个文件上传成功后，处理相关的事情
                // 其中info是文件上传成功后，服务端返回的json，形式如：
                // {
                //    "hash": "Fh8xVqod2MQ1mocfI4S4KpRL6D98",
                //    "key": "gogopher.jpg"
                //  }
                // 查看简单反馈
                // var domain = up.getOption('domain');
                // var res = parseJSON(info);
                // var sourceLink = domain +"/"+ res.key; 获取上传成功后的文件的Url
            },
            'Error': function(up, err, errTip) {
                //上传出错时，处理相关的事情
            },
            'UploadComplete': function() {
                //队列文件处理完毕后，处理相关的事情
            },
            'Key': function(up, file) {
                // 若想在前端对每个文件的key进行个性化处理，可以配置该函数
                // 该配置必须要在unique_names: false，save_key: false时才生效
                console.log(file,up);
                var key = file.name;
                // do something with key here
                return key
            }
        }
    };
//文件拖拽上传
//    var messageContent = $('.mesContainer')[0];
    var perContainer = document.getElementById('perContainer');
    perContainer.ondragover = function(e){
        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;
    }
    perContainer.ondrop = function(e){

        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;

        if(e.target.id!='message-content'){
            return;
        }

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
            //var _this = this;
            //var _file = this.files[0];
            //$(this).val("");


            UploadClient.initImage(config, function(uploadFile){
                //uploading = true;
                var callback = {
                    onError: function (errorCode) {
                        console.log(errorCode);
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
                        //把换村里的该文件删除掉
                        uploadComplete(data,this)
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
    var groupContainer = document.getElementById('groupContainer');
    groupContainer.ondragover = function(e){
        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;
    }
    groupContainer.ondrop = function(e){

        if (e.preventDefault) e.preventDefault();
        else e.returnValue = false;

        if(e.target.id!='message-content'){
            return;
        }

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
            //var _this = this;
            //var _file = this.files[0];
            //$(this).val("");


            UploadClient.initImage(config, function(uploadFile){
                //uploading = true;
                var callback = {
                    onError: function (errorCode) {
                        console.log(errorCode);
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
                        //把换村里的该文件删除掉
                        uploadComplete(data,this)
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
                        console.log(errorCode);
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
                        uploadComplete(data,this)
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

function fropFile(e){
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

        UploadClient.initImage(config, function(uploadFile){
            //uploading = true;
            var callback = {
                onError: function (errorCode) {
                    console.log(errorCode);
                },
                onProgress: function (loaded, total) {
                    var className = this._self.uniqueTime;
                    var percent = Math.floor(loaded / total * 100);
                    var progressContent = $('#up_precent[uniquetime="'+className+'"]');
                    progressContent.width(percent + '%');
                    return percent;
                },
                onCompleted: function (data) {
                    //把换村里的该文件删除掉
                    uploadComplete(data)
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

function uploadComplete(data,_this){
    var className = _this._self.uniqueTime;
    var downloadLink = returnDLLink(data.filename,className);
    var filedetail = {};
    filedetail.type = _this._self.type;
    var fileName = data.filename.split('.')[0];
    filedetail.fileUrl = downloadLink;
    var targetId = _this._self.targetId;
    var targetType = _this._self.targetType;
    var content = filedetail;
    var getLocalFileKey = targetId+''+targetType;
    var aFileDetail = globalVar.fileStroage[getLocalFileKey];
    if(aFileDetail){//如果有该联系人的文件没有上传完成
        //var aFileDetail = JSON.parse(sFileDetail);
        for(var i = 0;i<aFileDetail.length;i++){
            var curFile = aFileDetail[i];
            for(var key in curFile){
                if(key==_this._self.uniqueTime){
                    var newFileDetail = aFileDetail.splice(i,1);
                }
            }
        }
        if(newFileDetail&&newFileDetail.length==0){
            localStorage.removeItem(getLocalFileKey);
            delete globalVar.fileStroage[getLocalFileKey];
        }else{
            //var sNewFileDetail = JSON.stringify(aFileDetail);
            globalVar.fileStroage[getLocalFileKey] = aFileDetail;
            console.log('3333333333333',globalVar.fileStroage)

        }
    }
    if(!data.key){
        return
    }

    if(_this._self.type=='image/png'||_this._self.type=='image/jpeg'){
        var image = new Image();
        image.src = downloadLink;
        //filedetail.base64Str = getBase64Image(downloadLink);// 图片转为可以使用 HTML5 的 FileReader 或者 canvas 也可以上传到后台进行转换。
        filedetail.imageUri = downloadLink+'';
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
        filedetail.name = _this._self.name;
        filedetail.uniqueTime = _this._self.uniqueTime;
        filedetail.size = _this._self.size;
        filedetail.type = _this._self.type;
        filedetail.filename = _this._self.uniqueTime;
        var unique = data.filename.split('/')[0];
        $('a[fileName='+filedetail.uniqueTime+']').attr('fileName',unique);
        $('a[fileName='+unique+']').attr('href',downloadLink);
        $('#up_process[uniquetime="'+filedetail.uniqueTime+'"]').remove();
        sendByRongFile(content,targetId,targetType,'',_this._self.uniqueTime);
    }
}

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
    var targetId = $(_this).closest('.mesContainer').attr('targetid');
    var targetType = $(_this).closest('.mesContainer').attr('targettype');
    _file.targetId = targetId;
    _file.targetType = targetType;
    var content = JSON.stringify(filedetail);
    var extra = "uploadFile";
    sendMsg(content,targetId,targetType,extra,callback);
}

function returnDLLink(filename,className){
    //debugger;
    //var today = new Date();
    //var y = today.getFullYear();
    //var m = today.getMonth()+1;
    //var d = today.getDay();
    //var floderName = 'text_plain__RC-'+y+'-'+m+'-'+d+'_'+className;
    var unique = filename.split('/')[0];
    return globalVar.qiniuDOWNLOAD+filename+'?attname='+filename+'&uniquetime='+unique;
}
//function returnOPLink(filename){
//    //debugger;
//    return globalVar.qiniuDOWNLOAD+filename;
//}






























