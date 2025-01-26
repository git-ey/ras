<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">

<!-- jsp文件头和头部 -->
<%@ include file="../index/top.jsp"%>

</head>
<body class="no-skin">

	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<form action="user/readExcel.do" name="Form" id="Form" method="post" enctype="multipart/form-data">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
								<div id="zhongxin">
								<table style="width:95%;" >
									<tr>
										<td style="padding-top: 20px;"><input type="file" id="excel" name="excel" style="width:50px;" onchange="fileType(this)" /></td>
									</tr>
									<tr>
										<td style="text-align: center;padding-top: 10px;">
											<a class="btn btn-mini btn-primary" onclick="save();">导入</a>
											<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
											<a class="btn btn-mini btn-success" onclick="window.location.href='<%=basePath%>/user/downExcel.do'">下载模版</a>
										</td>
									</tr>
								</table>
								</div>
								<div id="zhongxin2" class="center" style="display:none"><br/><img src="static/images/jzx.gif" /><br/><h4 class="lighter block green"></h4></div>
							</form>
						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->
	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../index/foot.jsp"%>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!-- 上传控件 -->
	<script src="static/ace/js/ace/elements.fileinput.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<script type="text/javascript">
		$(top.hangge());
		$(function() {
			//上传
			$('#excel').ace_file_input({
				no_file:'请选择EXCEL ...',
				btn_choose:'选择',
				btn_change:'更改',
				droppable:false,
				onchange:null,
				thumbnail:false, //| true | large
				whitelist:'xls|xls',
				blacklist:'gif|png|jpg|jpeg'
				//onchange:''
			});
		});
		
		//保存
		function save(){
			if($("#excel").val()=="" || document.getElementById("excel").files[0] =='请选择xls格式的文件'){
				
				$("#excel").tips({
					side:3,
		            msg:'请选择文件',
		            bg:'#AE81FF',
		            time:3
		        });
				return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		function fileType(obj) {  
			var maxSize = 5242880000; // 设置最大文件大小为50GB（5242880000字节）  
			var fileSize = obj.files[0].size; // 获取文件大小（以字节为单位）  
			var fileType = obj.value.substr(obj.value.lastIndexOf(".")).toLowerCase(); // 获得文件后缀名  
			// 检查文件大小是否超过限制  
			if (fileSize > maxSize) {  
				$("#excel").tips({  
					side: 3,  
					msg: '文件大小不能超过50GB',  
					bg: '#AE82FF',  
					time: 3  
				});  
				$("#excel").val('');   
			}  
			// 读取文件的二进制数据  
			var file = obj.files[0];  
			var reader = new FileReader();  
			reader.onload = function(e) {
				var data = new Uint8Array(e.target.result);  
				var signature = '';  
				for (var i = 0; i < data.length && signature.length < 16; i++) { // 修改循环条件，确保读取到足够的数据  
					signature += data[i].toString(16);  
				}
				// 检查文件头签名是否符合xls或xlsx格式  
				if (signature !== 'd0cf11e0a1b11ae1' && signature !== '504b3414060800021') { // 添加对较旧的BIFF5格式的xls文件的支持  
					$("#excel").tips({  
						side: 3,  
						msg: '请上传Excel格式的文件',  
						bg: '#AE82FF',  
						time: 3  
					});
					$('#excel').ace_file_input('reset_input');
				}
			}; 
			reader.readAsArrayBuffer(file);  
		}	
	</script>


</body>
</html>