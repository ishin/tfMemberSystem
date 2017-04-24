    <%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        <html xmlns="http://www.w3.org/1999/html">
        <head lang="en">
        <meta charset="UTF-8">
        <title></title>
        <%--<link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/bootstrap.min.css"/>--%>
        <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/clipImg.css"/>
        <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/cropper.min.css"/>
        </head>
        <body>
        <%--修改头像部分--%>
        <div class="bMgMask"></div>
        <div class="container" id="crop-avatar">
            <div class="avatar-view" title="Change the avatar">
            <img src="" alt="Avatar">
            </div>
            <form class="avatar-form" action="<%=request.getContextPath() %>/upload!uploadUserLogo" enctype="multipart/form-data" method="post"  id="Uploader">
                <div class="bMg-cutPicture">
                    <h5 class="clearfix">
                        <span class="bMg-changPic">修改头像</span>
                        <i class="bMg-closeBtn" id="bMg-closeBtn"></i>
                    </h5>
                <p class="bMg-selectImg">使用下列所选照片</p>
                <div class="clearfix">
                    <div class="bMg-changImgSize">
                        <div class="bMg-ImgContainer">
                            <div class="avatar-wrapper"></div>
                            <%--<canvas id="canvas_bg" width="200" height="200"></canvas>--%>
                            <%--<canvas id="canvas" width="200" height="200"></canvas>--%>
                            <div class="bMg-rotateDrag clearfix avatar-btns">
                                <div class="btn-group bMg-leftRotate">
                                    <span class="" data-method="rotate" data-option="-90"  title="Rotate -90 degrees"></span>
                                </div>
                                <div class="btn-group">
                                    <i class="" data-method="rotate" data-option="90"  title="Rotate 90 degrees"></i>
                                </div>
                                <strong id="showGrid"></strong>
                            </div>
                            </div>
                        <p class="bMg-promptExp">* 选择上面中的一个做为你的照片，或点击"添加"来添加您电脑中的照片来作为您的照片...</p>
                    </div>
                    <div class="bMg-previewImg">
                        <%--<canvas id="big" width="80" height="80" radius="40"></canvas>--%>
                        <div class="avatar-preview preview-lg">
                            <img src="" class="bMg-selImg">
                        </div>
                        <%--<img src="css/img/1.jpg" class="bMg-selImg">--%>
                        <div class="bMg-gravityImg">
                        <span >
                            添加
                            <div class="avatar-upload">
                                <input class="avatar-src" name="avatar_src" type="hidden">
                                <input class="avatar-data" name="avatar_data" type="hidden">
                                <input class="avatar-input" id="avatarInput" name="avatar_file" type="file">
                            </div>
                         </span>
                        <span>删除</span>
                    </div>
                </div>
                </div>
                <div class="bMg-button clearfix">
                <b>取消</b>
                <button class="avatar-save btnSave" type="submit">保存</button>
                </div>
                </div>
            </form>
            <%--<div class="container" id="crop-avatar">--%>

        <%--<!-- Current avatar -->--%>
        <%--<div class="avatar-view" title="Change the avatar">--%>
            <%--<img src="css/img/1.jpg" alt="Avatar">--%>
        <%--</div>--%>

        <%--<!-- Cropping modal -->--%>
        <%--<div class="modal fade" id="avatar-modal" aria-hidden="true" aria-labelledby="avatar-modal-label" role="dialog" tabindex="-1">--%>
        <%--<div class="modal-dialog modal-lg">--%>
        <%--<div class="modal-content">--%>
        <%--<form class="avatar-form" action="crop.php" enctype="multipart/form-data" method="post">--%>
        <%--<div class="modal-header">--%>
        <%--<button class="close" data-dismiss="modal" type="button">&times;</button>--%>
        <%--<h4 class="modal-title" id="avatar-modal-label">Change Avatar</h4>--%>
        <%--</div>--%>
        <%--<div class="modal-body">--%>
        <%--<div class="avatar-body">--%>

        <%--<!-- Upload image and data -->--%>
        <%--<div class="avatar-upload">--%>
        <%--<input class="avatar-src" name="avatar_src" type="hidden">--%>
        <%--<input class="avatar-data" name="avatar_data" type="hidden">--%>
        <%--<label for="avatarInput">Local upload</label>--%>
        <%--<input class="avatar-input" id="avatarInput" name="avatar_file" type="file">--%>
        <%--</div>--%>

        <%--<!-- Crop and preview -->--%>
        <%--<div class="row">--%>
        <%--<div class="col-md-9">--%>
        <%--<div class="avatar-wrapper"></div>--%>
        <%--</div>--%>
        <%--<div class="col-md-3">--%>
        <%--<div class="avatar-preview preview-lg"></div>--%>
        <%--<div class="avatar-preview preview-md"></div>--%>
        <%--<div class="avatar-preview preview-sm"></div>--%>
        <%--</div>--%>
        <%--</div>--%>

        <%--<div class="row avatar-btns">--%>
        <%--<div class="col-md-9">--%>
        <%--<div class="btn-group">--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="-90" type="button" title="Rotate -90 degrees">Rotate Left</button>--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="-15" type="button">-15deg</button>--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="-30" type="button">-30deg</button>--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="-45" type="button">-45deg</button>--%>
        <%--</div>--%>
        <%--<div class="btn-group">--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="90" type="button" title="Rotate 90 degrees">Rotate Right</button>--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="15" type="button">15deg</button>--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="30" type="button">30deg</button>--%>
        <%--<button class="btn btn-primary" data-method="rotate" data-option="45" type="button">45deg</button>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--<div class="col-md-3">--%>
        <%--<button class="btn btn-primary btn-block avatar-save" type="submit">Done</button>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--<!-- <div class="modal-footer">--%>
        <%--<button class="btn btn-default" data-dismiss="modal" type="button">Close</button>--%>
        <%--</div> -->--%>
        <%--</form>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div><!-- /.modal -->--%>

        <!-- Loading state -->
        <div class="loading" aria-label="Loading" role="img" tabindex="-1"></div>
        </div>
        <script src="<%=request.getContextPath() %>/page/web/js/jquery-2.1.1.min.js"></script>
        <script src="<%=request.getContextPath() %>/page/web/js/bootstrap.min.js"></script>
        <script src="<%=request.getContextPath() %>/page/web/js/cropper.min.js"></script>
        <script src="<%=request.getContextPath() %>/page/web/js/clipImg.js"></script>
        </body>
        <html/>