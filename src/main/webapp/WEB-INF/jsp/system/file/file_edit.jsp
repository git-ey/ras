<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/index/top.jsp"%>
	<link rel="stylesheet" href="static/html_UI/assets/css/bootstrap.css" />
	<link rel="stylesheet" href="static/html_UI/assets/css/font-awesome.css" />
	<!-- fonts -->
	<link rel="stylesheet" href="static/html_UI/assets/css/ace-fonts.css" />
	<link rel="stylesheet" href="static/html_UI/assets/css/ace.css" />
	<!--[if lte IE 9]>
	  <link rel="stylesheet" href="../assets/css/ace-ie.css" />
	<![endif]-->
</head>
<body class="no-skin">
<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
	 <div class="main-container">
	   <div id="zhongxin" style="padding-top: 13px;">
		<div class="center" style="width:400px; margin:12px;">
			<h3 class="header blue smaller lighter">
				上传文件至服务器
			</h3>
			<!-- our form -->
			<form id="Form" method="post" action="file/${msg}.do">
				<input  type="file" name="avatar[]" multiple />
				<div class="hr hr-12 dotted"></div>
				<button type="submit" class="btn btn-sm btn-primary">提交</button>
				<button type="reset" class="btn btn-sm">重置</button>
			</form>
		</div>
	  </div>
	 </div>

		<!-- basic scripts -->
		<!--[if !IE]> -->
		<script type="text/javascript">
		 window.jQuery || document.write("<script src='static/html_UI/assets/js/jquery.js'>"+"<"+"/script>");
		</script>
		<!-- <![endif]-->
		<!--[if IE]>
		<script type="text/javascript">
		 window.jQuery || document.write("<script src='static/html_UI/assets/js/jquery1x.js'>"+"<"+"/script>");
		</script>
		<![endif]-->
		
		<!-- ace scripts -->
		<script src="static/html_UI/assets/js/bootstrap.js"></script>
		<script src="static/html_UI/assets/js/ace-elements.js"></script>
		<script src="static/html_UI/assets/js/ace.js"></script>
				
		<script type="text/javascript">
			jQuery(function($) {
				var $form = $('#Form');
				//you can have multiple files, or a file input with "multiple" attribute
				var file_input = $form.find('input[type=file]');
				var upload_in_progress = false;

				file_input.ace_file_input({
					style : 'well',
					btn_choose : '选择 或 拖拽文件',
					btn_change: null,
					droppable: true,
					thumbnail: 'large',
					
					maxSize: 110000000,//bytes
					//allowExt: ["jpeg", "jpg", "png", "gif"],
					//allowMime: ["image/jpg", "image/jpeg", "image/png", "image/gif"],

					before_remove: function() {
						if(upload_in_progress)
							return false;//if we are in the middle of uploading a file, don't allow resetting file input
						return true;
					},

					preview_error: function(filename , code) {
						//code = 1 means file load error
						//code = 2 image load error (possibly file is not an image)
						//code = 3 preview failed
					}
				})
				file_input.on('file.error.ace', function(ev, info) {
					if(info.error_count['ext'] || info.error_count['mime']) alert('Invalid file type! Please select an image!');
					if(info.error_count['size']) alert('Invalid file size! Maximum 100KB');
					
					//you can reset previous selection on error
					//ev.preventDefault();
					//file_input.ace_file_input('reset_input');
				});
				
				var ie_timeout = null;//a time for old browsers uploading via iframe
				
				$form.on('submit', function(e) {
					e.preventDefault();
					var files = file_input.data('ace_input_files');
					if( !files || files.length == 0 ) return false;//no files selected
										
					var deferred ;
					if( "FormData" in window ) {
						//for modern browsers that support FormData and uploading files via ajax
						//we can do >>> var formData_object = new FormData($form[0]);
						//but IE10 has a problem with that and throws an exception
						//and also browser adds and uploads all selected files, not the filtered ones.
						//and drag&dropped files won't be uploaded as well
						
						//so we change it to the following to upload only our filtered files
						//and to bypass IE10's error
						//and to include drag&dropped files as well
						formData_object = new FormData();//create empty FormData object
						
						//serialize our form (which excludes file inputs)
						$.each($form.serializeArray(), function(i, item) {
							//add them one by one to our FormData 
							formData_object.append(item.name, item.value);							
						});
						//and then add files
						$form.find('input[type=file]').each(function(){
							var field_name = $(this).attr('name');
							//for fields with "multiple" file support, field name should be something like `myfile[]`
							var files = $(this).data('ace_input_files');
							if(files && files.length > 0) {
								for(var f = 0; f < files.length; f++) {
									formData_object.append(field_name, files[f]);
								}
							}
						});

						upload_in_progress = true;
						file_input.ace_file_input('loading', true);
						
						deferred = $.ajax({
							        url: $form.attr('action'),
							       type: $form.attr('method'),
							processData: false,//important
							contentType: false,//important
							   dataType: 'json',
							       data: formData_object
						})
					}
					else {
						deferred = new $.Deferred //create a custom deferred object
						var temporary_iframe_id = 'temporary-iframe-'+(new Date()).getTime()+'-'+(parseInt(Math.random()*1000));
						var temp_iframe = 
								$('<iframe id="'+temporary_iframe_id+'" name="'+temporary_iframe_id+'" \
								frameborder="0" width="0" height="0" src="about:blank"\
								style="position:absolute; z-index:-1; visibility: hidden;"></iframe>')
								.insertAfter($form)

						$form.append('<input type="hidden" name="temporary-iframe-id" value="'+temporary_iframe_id+'" />');
						temp_iframe.data('deferrer' , deferred);
						$form.attr({
									  method: 'POST',
									 enctype: 'multipart/form-data',
									  target: temporary_iframe_id //important
									});
						upload_in_progress = true;
						file_input.ace_file_input('loading', true);//display an overlay with loading icon
						$form.get(0).submit();
						//if we don't receive a response after 30 seconds, let's declare it as failed!
						ie_timeout = setTimeout(function(){
							ie_timeout = null;
							temp_iframe.attr('src', 'about:blank').remove();
							deferred.reject({'status':'fail', 'message':'Timeout!'});
						} , 30000);
					}
					//deferred callbacks, triggered by both ajax and iframe solution
					deferred
					.done(function(result) {//success
						//format of `result` is optional and sent by server
						//in this example, it's an array of multiple results for multiple uploaded files
						var message = '';
						for(var i = 0; i < result.length; i++) {
							if(result[i].status == 'OK') {
								message += "成功：" + result[i].url
							}
							else {
								message += "失败：" + result[i].message;
							}
							message += "\n";
						}
						alert(message);
					})
					.fail(function(result) {//failure
						alert("There was an error");
					})
					.always(function() {//called on both success and failure
						if(ie_timeout) clearTimeout(ie_timeout)
						ie_timeout = null;
						upload_in_progress = false;
						file_input.ace_file_input('loading', false);
					});

					deferred.promise();
				});
				//when "reset" button of form is hit, file field will be reset, but the custom UI won't
				//so you should reset the ui on your own
				$form.on('reset', function() {
					$(this).find('input[type=file]').ace_file_input('reset_input_ui');
				});
				if(location.protocol == 'file:') alert("For uploading to server, you should access this page using 'http' protocal, i.e. via a webserver.");

			});
		</script>
	<!-- /.main-content -->
</div>
<!-- /.main-container -->
	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<script type="text/javascript">
		$(top.hangge());
	</script>
</body>
</html>