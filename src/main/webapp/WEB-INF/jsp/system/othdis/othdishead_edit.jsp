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
					
					<form action="othdishead/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="OTHDISHEAD_ID" id="OTHDISHEAD_ID" value="${pd.OTHDISHEAD_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">披露口径代码:</td>
								<td><input type="text" name="OTHDISHEAD_ID" id="OTHDISHEAD_ID" value="${pd.OTHDISHEAD_ID}" maxlength="60" placeholder="这里输入披露口径代码" title="披露口径代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">披露口径名称:</td>
								<td><input type="text" name="DISCLOSURE_NAME" id="DISCLOSURE_NAME" value="${pd.DISCLOSURE_NAME}" maxlength="240" placeholder="这里输入披露口径名称" title="披露口径名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">说明:</td>
								<td><input type="text" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="480" placeholder="这里输入说明" title="说明" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">是否启用:</td>
							    <td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								        <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								        <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
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

<c:if test="${'edit' == msg }">
	<div>
		<iframe name="treeFrame" id="treeFrame" frameborder="0" src="<%=basePath%>/othdisline/list.do?OTHDISHEAD_ID=${pd.OTHDISHEAD_ID}" style="margin:0 auto;width:805px;height:368px;;"></iframe>
	</div>
</c:if>

<footer>
<div style="width: 100%;padding-bottom: 2px;" class="center">
	<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
	<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
</div>
</footer>

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
			if($("#OTHDISHEAD_ID").val()==""){
				$("#OTHDISHEAD_ID").tips({
					side:3,
		            msg:'请输入披露口径代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#OTHDISHEAD_ID").focus();
			return false;
			}
			if($("#DISCLOSURE_NAME").val()==""){
				$("#DISCLOSURE_NAME").tips({
					side:3,
		            msg:'请输入披露口径名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DISCLOSURE_NAME").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入是否启用',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACTIVE").focus();
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