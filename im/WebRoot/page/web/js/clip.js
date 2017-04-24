'use strict';

var clipTools = Class.extend({
	ctor : function(querySelector){
		this._div = document.createElement('div');
		this._createCanvas();
		this._cInt(querySelector);
	},

	_createCanvas : function(){
		var dom = '<div class="pop-bg"></div>\
		<div class="bMg-cutPicture">\
		<h5 class="clearfix">\
			<span class="bMg-changPic">修改头像</span>\
		<i class="bMg-closeBtn"></i>\
		</h5>\
		<p class="bMg-selectImg">使用下列所选照片</p>\
		<div class="clearfix">\
		<div class="bMg-changImgSize">\
		<div class="bMg-ImgContainer">\
		<canvas id="canvas_bg" width="200" height="200"></canvas>\
		<canvas id="canvas" width="200" height="200"></canvas>\
		</div>\
		</div>\
		<div class="bMg-previewImg">\
		<canvas id="big" width="80" height="80" radius="40"></canvas>\
		<div class="bMg-gravityImg">\
		<span >添加<input type="file" class="flie"></span>\
		<span>删除</span>\
		</div>\
		</div>\
		</div>\
		<div class="bMg-button clearfix">\
		<b>取消</b>\
		<b>保存</b>\
		</div></div>';
		this._div.innerHTML = dom;
		this._div.setAttribute('id', 'pop_clip');
		if(document.body){
			document.body.appendChild(this._div);
			this._div.owner = this;
		};
	},
	_cInt : function(querySelector){
		this.bth  = document.querySelector('.'+querySelector);//触发按钮
		this.win  = document.querySelector('#pop_clip');//弹窗盒子
		this.file = this.win.querySelector('.bMg-gravityImg [type="file"]');//上传文件
		//this.reset= this.win.querySelector('.reset [type="file"]');//重新上传文件
		//this.conf = this.win.querySelector('.confirm');//确认
		//this.cance= this.win.querySelector('.cancel');//取消
		this.cDom = this.win.querySelector('#canvas');//裁剪画布
		//this.rotL = this.win.querySelector('dd a.l');//旋转-左
		//this.rotR = this.win.querySelector('dd a.r');//旋转-右
		this.views= this.win.querySelectorAll('.bMg-previewImg canvas');//预览头像画布
		this.nDom = this.win.querySelector('#canvas_bg');//裁剪画布
		this.ctx  = this.cDom.getContext('2d');
		this.nCtx = this.nDom.getContext('2d');
		this.img  = new Image();
		this.cW  = 200;//画布宽
		this.cH  = 200;//画布高
		this.cR  = 50;//选取区域, 半径 （最大值 = 画布宽(画布高)/2）
		this.cX  = 100;//选取区域, x轴心点（默认值）
		this.cY  = 100;//选取区域, y轴心点（默认值）
		this.rot = 0;//旋转角度
		this.isPoint = null;// null-默认值
		this.state=false;

		if(!this.src){
			var eParent = this.bth.parentNode;
			var img=eParent.querySelector('img');
			this.src = img.getAttribute('src');
		};
		this.img.src = this.src;
		this._cEvent();
	},

	_cEvent : function(){
		var _self = this;

		this.uploading = function(){
			var file = this.files[0];
			_self.state=true;
			_self.conf.className="confirm active";
			var reader = new FileReader();

			reader.onload = function(){
				var url = reader.result;

				_self._setdataSrc(url);
			};

			reader.readAsDataURL(file);
		};

		this.leftRot = function(){
			_self.rot -= 90;

			if(_self.rot === -90){
				_self.rot = 270;
			};

			//_self.drawing();
			_self.drawRect();
		};

		this.rightRot = function(){
			_self.rot += 90;
			//_self.drawing();
			_self.drawRect();
			if(_self.rot === 270){
				_self.rot = -90;
			};
		};

		this.file.addEventListener('change', this.uploading);//本地上传图片
		//this.reset.addEventListener('change', this.uploading);//重新上传

		//图片加载完成
		this.img.onload = function(){
			//_self.drawing();
			_self.drawRect();
		};

		this.cDom.addEventListener('mousedown', function(ev){//绑定鼠标按下事件
			_self._down(ev);
		});

		this.bth.addEventListener('click', function(){
			_self.win.style.display = 'block';
		});

		//this.rotL.addEventListener('click', function(){//左——旋转
		//	_self.leftRot();
		//});
        //
		//this.rotR.addEventListener('click', function(){//右——旋转
		//	_self.rightRot();
		//});
        //
		//this.cance.addEventListener('click', function(){//取消按钮
        //
		//	_self.win.style.display = 'none';
		//});

		//this.conf.addEventListener('click',  function(){//点击确定按钮 上传到服务器
		//	if(_self.state){
		//	var base = _self._saveImg();
        //
		//	_self.win.style.display = 'none';
		//	upDateImg(base);
		//	}
		//});
	},

	_setdataSrc : function(src){
		var _self = this;
		this.img = new Image();
		this.src = src;
		this.img.src = this.src;

		var _default = this.win.querySelector('.default');
		var _dl  = this.win.querySelector('.left dl');

		_default.style.display = 'none';
		_dl.style.display = 'block';

		this.img.onload = function(){
			_self._reset();
			//_self.drawing();
			_self.drawRect();
		};
	},

	//存为base图片格式
	_saveImg : function(){
		var canBig= document.querySelector('#big');
		var base  = canBig.toDataURL('image/png');

		return base;
	},

	_down : function(ev){
		var _self  = this;
		var oEvent = ev || event;
		var cPos   = this.cDom.getBoundingClientRect();

		this.oldX  = parseInt(oEvent.clientX - cPos.left);
		this.oldY  = parseInt(oEvent.clientY - cPos.top);
		this._testScope(this.oldX, this.oldY);

		if(this.isPoint != null){
			this.move = function(ev){
				_self._move(ev);
			};

			this.up = function(){
				_self._up();
			};

			document.addEventListener('mousemove', this.move);
			document.addEventListener('mouseup', this.up);
		};
	},

	_move : function(ev){
		var oEvent = ev || event;
		var cPos   = this.cDom.getBoundingClientRect();
		var cliX   = parseInt(oEvent.clientX - cPos.left);
		var cliY   = parseInt(oEvent.clientY - cPos.top);

		if(this.oldX != cliX || this.oldY != cliY){
			if(this.isPoint){
				this.cX = cliX;
				this.cY = cliY;
			}else{
				this.cR = (cliX - this.cX)/Math.cos(45 * Math.PI/180);
				this.cX = cliX - (this.cR * Math.cos(45 * Math.PI/180));
				this.cY = cliY - (this.cR * Math.sin(45 * Math.PI/180));
			};
			//this.drawing();
			this.drawRect();
		};
	},

	_up : function(){
		this.isPoint = null;
		document.removeEventListener('mousemove', this.move);
		document.removeEventListener('mouseup', this.up);
	},

	_reset : function(){
		this.cX  = 200;
		this.cY  = 200;
		this.rot = 0;

		//this.drawing();
		this.drawRect();
	},

	//检测范围
	_testScope : function(x, y){
		var _x = this.cX + this.cR * Math.cos(45 * Math.PI/180);
		var _y = this.cY + this.cR * Math.sin(45 * Math.PI/180);

		this.ctx.arc(this.cX, this.cY, this.cR, 0, Math.PI*2, true);
		if(this.ctx.isPointInPath(x, y)){
			this.isPoint = true;
		};
		this.ctx.beginPath();
		this.ctx.arc(_x, _y, 8, 0, Math.PI*2);
		if(this.ctx.isPointInPath(x, y)){
			this.isPoint = false;
		};
	},

	//绘画
	drawing : function(){
		this._rotate();
		this._layer();
		this._circle();
		this._point();
	},

	//画矩形
	drawRect : function(){
		this._rotate();
		this._layer();
		this._rect();
		this._rPoint();
	},

	//遮罩层
	_layer : function(){
		this.ctx.clearRect(0, 0, this.cW, this.cH);
		this.ctx.save();
		this.ctx.fillStyle = '#000000';
		this.ctx.globalAlpha = 0.7;
		this.ctx.fillRect(0, 0, this.cW, this.cH);
	},

	_drawImg : function(){
		this.nCtx.clearRect(0, 0, this.cW, this.cH);
		this.nCtx.drawImage(this.img, 0, 0);
	},

	//旋转
	_rotate : function(){
		var _rot = Math.PI * this.rot / 180;
		var _cos = Math.round(Math.cos(_rot) * 1000) / 1000;
		var _sin = Math.round(Math.sin(_rot) * 1000) / 1000;

		this.nCtx.save();

		if(_rot <= Math.PI / 2){
			this.nCtx.translate(_sin * this.cH, 0);
		}else if(_rot <= Math.PI){
			this.nCtx.translate(this.cW, -_cos * this.cH);
		}else if(_rot <= Math.PI * 1.5){
			this.nCtx.translate(-_cos * this.cW, this.cH);
		}else{
			this.nCtx.translate(0, -_sin * this.cW);
		};

		this.nCtx.rotate(_rot);
		this._drawImg();
		this.nCtx.restore();
		this._view();
	},

	//预览区域
	_view : function(){
		var lens = this.views.length;

		for(var i=0; i<lens; i++){
			var canvas= this.views[i];
			var _ctx  = canvas.getContext('2d');
			var width = canvas.getAttribute('width');
			var height= canvas.getAttribute('height');
			var radius= canvas.getAttribute('radius');

			_ctx.clearRect(0, 0, width, height);
			_ctx.drawImage(this.nDom, this.cX-this.cR, this.cY-this.cR, this.cW/2*(this.cR/50), this.cW/2*(this.cR/50), 0, 0, width, height);
		};
	},

	//裁剪区域-画圆
	_circle : function(){
		this.ctx.save();
		this.ctx.globalCompositeOperation = 'xor';
		this.ctx.fillStyle = 'white';
		this.ctx.beginPath();
		this.ctx.arc(this.cX, this.cY, this.cR, 0, Math.PI*2, true);
		this.ctx.fill();
		this.ctx.restore();
	},

	//裁剪区域-矩形
	_rect : function(){
		this.ctx.save();
		this.ctx.globalCompositeOperation = 'xor';
		this.ctx.fillStyle = 'white';
		this.ctx.beginPath();
		this.ctx.fillRect(this.cX/2, this.cY/2, this.cR*2, this.cR*2);
		this.ctx.fill();
		this.ctx.restore();
	},

	//圆点
	_point : function(){
		var _x = this.cX + this.cR * Math.cos(45 * Math.PI/180);
		var _y = this.cY + this.cR * Math.sin(45 * Math.PI/180);
		this.ctx.restore();
		this.ctx.beginPath();
		this.ctx.arc(_x, _y, 8, 0, Math.PI*2);
		this.ctx.fillStyle = '#30c0da';
		this.ctx.fill();
		this.ctx.restore();
	},

	_rPoint : function(){
		var _x = this.cX + this.cR;
		var _y = this.cY + this.cR;

		this.ctx.restore();
		this.ctx.beginPath();
		this.ctx.fillRect(_x-2, _y-2, 4, 4);
		this.ctx.fillStyle = '#30c0da';
		this.ctx.fill();
		this.ctx.restore();
	}
});

//上传到服务器
function upDateImg(base){
	var tmp = '["'+base+'"]';//服务器需要的格式

	$.ajax({
		url :'../pcapi/webapi/uploadHead',
		type:'post',
		data:{
			name:'fl_ResourceCutImageBase64',
			base64 : tmp
		},

		success : function(data){
			var _data = JSON.parse(data);

			if(_data.state == 'ok'){
				var url = _data['resourcecutimageurl'];//游戏截图路径
				var b64 = _data['resourcecutimagebase64'];//游戏截图路径Base64
				var isrc= _data['resourceimageurl']//游戏缩略图图片路径

				$('.headdiv img').attr('datasrc', b64);
				ajaxplupload();
			}else if(_data.state == 'false' || _data.state == '200'){
				var msgBox = new vtMsgBox('', '上传失败','warning');
				msgBox.show();
			};
		},

		error : function(err){
			console.info(err);
		}
	});
};


$(function(){
	window.clip = new clipTools('perSetBox-modifyHead');
});