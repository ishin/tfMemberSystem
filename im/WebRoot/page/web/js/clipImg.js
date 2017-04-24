/**
 * Created by gao_yn on 2017/1/10.
 */
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof exports === 'object') {
        // Node / CommonJS
        factory(require('jquery'));
    } else {
        factory(jQuery);
    }
})(function ($) {

    'use strict';
   //点击是否显示网格
    var console = window.console || { log: function () {} };
    function CropAvatar($element) {
        this.$container = $element;

        this.$avatarView = this.$container.find('.avatar-view');
        this.$avatar = this.$avatarView.find('img');
        //this.$avatarModal = this.$container.find('#avatar-modal');
        this.$loading = this.$container.find('.loading');

        this.$avatarForm = this.$container.find('.avatar-form');
        this.$avatarUpload = this.$avatarForm.find('.avatar-upload');
        this.$avatarSrc = this.$avatarForm.find('.avatar-src');
        this.$avatarData = this.$avatarForm.find('.avatar-data');
        this.$avatarInput = this.$avatarForm.find('.avatar-input');
        this.$avatarSave = this.$avatarForm.find('.avatar-save');
        this.$avatarBtns = this.$avatarForm.find('.avatar-btns');

        this.$avatarWrapper = this.$container.find('.avatar-wrapper');
        this.$avatarPreview =this.$container.find('.avatar-preview');

        this.init();
        console.log(this);
    }

    CropAvatar.prototype = {
        constructor: CropAvatar,

        support: {
            fileList: !!$('<input type="file">').prop('files'),
            blobURLs: !!window.URL && URL.createObjectURL,
            formData: !!window.FormData
        },

        init: function () {
            this.support.datauri = this.support.fileList && this.support.blobURLs;

            if (!this.support.formData) {
                this.initIframe();
            }

            this.initTooltip();
            this.initModal();
            this.addListener();
        },

        addListener: function () {
            //this.$avatarModal.modal('show');
            this.initPreview();
           // this.$avatarView.on('click', $.proxy(this.click, this));
            this.$avatarInput.on('change', $.proxy(this.change, this));
            this.$avatarForm.on('submit', $.proxy(this.submit, this));
            this.$avatarBtns.on('click', $.proxy(this.rotate, this));
        },

        initTooltip: function () {
            this.$avatarView.tooltip({
                placement: 'bottom'
            });
        },

        initModal: function () {
            //this.$avatarModal.modal({
            //    show: false
            //});
        },

        initPreview: function () {
            var url = this.$avatar.attr('src');

            this.$avatarPreview.empty().html('<img src="' + url + '">');
        },

        initIframe: function () {
            var target = 'upload-iframe-' + (new Date()).getTime(),
                $iframe = $('<iframe>').attr({
                    name: target,
                    src: ''
                }),
                _this = this;

            // Ready ifrmae
            $iframe.one('load', function () {

                // respond response
                $iframe.on('load', function () {
                    var data;

                    try {
                        data = $(this).contents().find('body').text();
                    } catch (e) {
                        console.log(e.message);
                    }

                    if (data) {
                        try {
                            data = $.parseJSON(data);
                        } catch (e) {
                            console.log(e.message);
                        }

                        _this.submitDone(data);
                    } else {
                        _this.submitFail('Image upload failed!');
                    }

                    _this.submitEnd();

                });
            });

            this.$iframe = $iframe;
            this.$avatarForm.attr('target', target).after($iframe.hide());
        },

        click: function () {
            //this.$avatarModal.modal('show');
            //this.initPreview();
        },

        change: function () {
            var files,
                file;

            if (this.support.datauri) {
                files = this.$avatarInput.prop('files');

                if (files.length > 0) {
                    file = files[0];

                    if (this.isImageFile(file)) {
                        if (this.url) {
                            URL.revokeObjectURL(this.url); // Revoke the old one
                        }

                        this.url = URL.createObjectURL(file);
                        this.startCropper();
                    }else{
                        new Window().alert({
                            title   : '',
                            content : '您选择的不是图片，请选择选择！',
                            hasCloseBtn : false,
                            hasImg : true,
                            textForSureBtn : false,
                            textForcancleBtn : false,
                            autoHide:true
                        });
                        $('.bMg-cropImgSet').removeClass('chatHide');
                        $('.bMg-cropImgBox').addClass('chatHide');
                        $('.bMg-gravityImg').removeClass('active');
                        $('.bMg-confirm').addClass('chatHide');
                        $('.bMg-preserve').removeClass('chatHide');
                        $('.bMg-imgList li').removeClass('active');
                    }
                }
            } else {
                file = this.$avatarInput.val();

                if (this.isImageFile(file)) {
                    this.syncUpload();
                }else{
                    new Window().alert({
                        title   : '',
                        content : '您选择的不是图片，请选择选择！',
                        hasCloseBtn : false,
                        hasImg : true,
                        textForSureBtn : false,
                        textForcancleBtn : false,
                        autoHide:true
                    });
                    $('.bMg-cropImgSet').removeClass('chatHide');
                    $('.bMg-cropImgBox').addClass('chatHide');
                    $('.bMg-gravityImg').removeClass('active');
                    $('.bMg-confirm').addClass('chatHide');
                    $('.bMg-preserve').removeClass('chatHide');
                    $('.bMg-imgList li').removeClass('active');
                }
            }
        },

        submit: function () {
            var _self=this;
            if (!this.$avatarSrc.val() && !this.$avatarInput.val()) {
                return false;
            }

            if (this.support.formData) {
                $('.imgLoading').addClass('active');
                setTimeout(function(){
                    _self.ajaxUpload();
                },1000);
                return false;
            }
        },

        rotate: function (e) {
            var data;

            if (this.active) {
                data = $(e.target).data();

                if (data.method) {
                    this.$img.cropper(data.method, data.option);
                }
            }
        },

        isImageFile: function (file) {
            if (file.type) {
                return /^image\/\w+$/.test(file.type);
            } else {
                return /\.(jpg|jpeg|png|gif)$/.test(file);
            }
        },

        startCropper: function () {
            var _this = this;

            if (this.active) {
                this.$img.cropper('replace', this.url);
            } else {
                this.$img = $('<img src="' + this.url + '">');
                this.$avatarWrapper.empty().html(this.$img);
                this.$img.cropper({
                    aspectRatio: 1,
                    preview: this.$avatarPreview.selector,
                    strict: false,
                    modal:false,
                    background:false,
                    crop: function (data) {
                        var json = [
                            '{"x":' + data.x,
                            '"y":' + data.y,
                            '"height":' + data.height,
                            '"width":' + data.width,
                            '"rotate":' + data.rotate + '}'
                        ].join();

                        _this.$avatarData.val(json);
                    }
                });

                this.active = true;
            }
        },

        stopCropper: function () {
            if (this.active) {
                this.$img.cropper('destroy');
                this.$img.remove();
                this.active = false;
            }
        },

        ajaxUpload: function () {
            var url = this.$avatarForm.attr('action'),
                data = null,
                _this = this;
            var fileInput = document.getElementById("Uploader");
            var file = fileInput[2].files[0];
                data=JSON.parse(_this.$avatarData.val());
            console.log(data);
            console.log(data);
            var formData = new FormData();
            formData.append("file", file);
            formData.append("width", data.width);
            formData.append("height", data.height);
            formData.append("x", data.x);
            formData.append("y", data.y);
            formData.append("degree", data.rotate);
            var sData=window.localStorage.getItem("datas");
            var oData= JSON.parse(sData);
            var sId=oData.id;
            formData.append("userid",sId);
            $.ajax(url, {
                type: 'post',
                data: formData,
                dataType: 'json',
                processData: false,
                contentType: false,

                beforeSend: function () {
                    _this.submitStart();
                },

                success: function (data) {
                    _this.submitDone(data);
                },

                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    _this.submitFail(textStatus || errorThrown);
                },

                complete: function () {
                    //_this.submitEnd();
                }
            });
        },

        syncUpload: function () {
            this.$avatarSave.click();
        },

        submitStart: function () {
            this.$loading.fadeIn();
        },

        submitDone: function (data) {
            var _this=this;
            console.log(data);
            if(data.code === 1){
                if (data.text) {
                    $('.imgLoading').removeClass('active');
                    this.url = 'upload/images/'+data.text;
                    if (this.support.datauri || this.uploaded) {
                        this.uploaded = false;
                        this.cropDone();
                    } else {
                        this.uploaded = true;
                        this.$avatarSrc.val(this.url);
                        this.startCropper();
                    }
                    this.$avatarInput.val('');
                    $('.bMg-cropImgSet').removeClass('chatHide');
                    $('.bMg-cropImgBox').addClass('chatHide');
                    $('.bMg-gravityImg').removeClass('active');
                    $('.bMg-confirm').addClass('chatHide');
                    $('.bMg-preserve').removeClass('chatHide');
                    $('.bMg-imgList li').removeClass('active');
                    _this.initPreview();
                    var sDom=' <li class="active" data-name="'+data.text+'">\
                        <img src="'+this.url+'"/>\
                    </li>';
                    $('.bMg-imgList').append(sDom);
                }
            }else{
                this.alert('Failed to response');
            }
           /* if ($.isPlainObject(data) && data.code === 1) {
                if (data.result) {
                    this.url = data.result;

                    if (this.support.datauri || this.uploaded) {
                        this.uploaded = false;
                        this.cropDone();
                    } else {
                        this.uploaded = true;
                        this.$avatarSrc.val(this.url);
                        this.startCropper();
                    }

                    this.$avatarInput.val('');
                } else if (data.message) {
                    this.alert(data.message);
                }
            } else {
                this.alert('Failed to response');
            }*/
        },

        submitFail: function (msg) {
            this.alert(msg);
        },

        submitEnd: function () {
            this.$loading.fadeOut();
        },

        cropDone: function () {
            this.$avatarForm.get(0).reset();
            this.$avatar.attr('src', this.url);
            //this.stopCropper();
            //this.$avatarModal.modal('hide');
        },

        alert: function (msg) {
            var $alert = [
                '<div class="alert alert-danger avater-alert">',
                '<button type="button" class="close" data-dismiss="alert">&times;</button>',
                msg,
                '</div>'
            ].join('');

            this.$avatarUpload.after($alert);
        }
    };

    $(function () {
        return new CropAvatar($('#crop-avatar'));
    });

});
function getmatrix(a,b,c,d,e,f){
    var aa=Math.round(180*Math.asin(a)/ Math.PI);
    var bb=Math.round(180*Math.acos(b)/ Math.PI);
    var cc=Math.round(180*Math.asin(c)/ Math.PI);
    var dd=Math.round(180*Math.acos(d)/ Math.PI);
    var deg=0;
    if(aa==bb||-aa==bb){
        deg=dd;
    }else if(-aa+bb==180){
        deg=180+cc;
    }else if(aa+bb==180){
        deg=360-cc||360-dd;
    }
    return deg>=360?0:deg;
    //return (aa+','+bb+','+cc+','+dd);
}