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
	<!-- 下拉框 -->
	<link rel="stylesheet" href="static/ace/css/chosen.css" />
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/index/top.jsp"%>
	<!-- 日期框 -->
	<link rel="stylesheet" href="static/ace/css/datepicker.css" />
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
					
					<form action="importData/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="IMPORT_ID" id="IMPORT_ID" value="${pd.IMPORT_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">文件类型:</td>
								<td>
							        <div class="sel"></div>
								    <select class="chosen-select form-control" name="IMPORT_FILE_TYPE" id="id" data-placeholder="请选择" style="width:49%;">
								    <option value="EXCEL" <c:if test="${pd.IMPORT_FILE_TYPE == 'EXCEL'}">selected</c:if>>EXCEL</option>
								    <option value="DBF" <c:if test="${pd.IMPORT_FILE_TYPE == 'DBF'}">selected</c:if>>DBF</option>
								    <div class="sel"></div>
								</td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">文件路径:</td>
								<td><input type="text" name="IMPORT_FILE_PATH" id="IMPORT_FILE_PATH" value="${pd.IMPORT_FILE_PATH}" maxlength="240" placeholder="这里输入文件路径" title="文件路径" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">确认导入</a>
									<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
								</td>
							</tr>
						</table>
						</div>
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
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


	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript">
		$(top.hangge());
		//保存
		function save(){
			if($("#IMPORT_FILE_TYPE").val()==""){
				$("#IMPORT_FILE_TYPE").tips({
					side:3,
		            msg:'请输入文件类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#IMPORT_FILE_TYPE").focus();
			return false;
			}
			if($("#IMPORT_FILE_PATH").val()==""){
				$("#IMPORT_FILE_PATH").tips({
					side:3,
		            msg:'请输入文件路径',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#IMPORT_FILE_PATH").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
		});
		</script>
</body>
</html>