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
					
					<form action="urowset/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="UROWSET_ID" id="UROWSET_ID" value="${pd.UROWSET_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">行集代码:</td>
								<td><input type="text" autocomplete="off" name="ROW_SET_CODE" id="ROW_SET_CODE" value="${pd.ROW_SET_CODE}" maxlength="30" placeholder="这里输入行集代码" title="行集代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">行集:</td>
								<td><input type="text" autocomplete="off" name="ROW_SET" id="ROW_SET" value="${pd.ROW_SET}" maxlength="255" placeholder="这里输入行集" title="行集" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">行集类型:</td>
								<td>
								    <select class="chosen-select form-control" name="ROW_SET_TPYE" id="ROW_SET_TPYE" data-placeholder="请选择" style="width:49%;">
								        <option value=""></option>
								        <option value="STOCKS" <c:if test="${pd.ROW_SET_TPYE == 'STOCKS'}">selected</c:if>>STOCKS</option>
								        <option value="FUND" <c:if test="${pd.ROW_SET_TPYE == 'FUND'}">selected</c:if>>FUND</option>
								        <option value="BOND" <c:if test="${pd.ROW_SET_TPYE == 'BOND'}">selected</c:if>>BOND</option>
								        <option value="ABS" <c:if test="${pd.ROW_SET_TPYE == 'ABS'}">selected</c:if>>ABS</option>
								        <option value="GOLD" <c:if test="${pd.ROW_SET_TPYE == 'GOLD'}">selected</c:if>>GOLD</option>
								        <option value="DI_WARRAMT" <c:if test="${pd.ROW_SET_TPYE == 'DI_WARRAMT'}">selected</c:if>>DI_WARRAMT</option>
								        <option value="DI_OTHER" <c:if test="${pd.ROW_SET_TPYE == 'DI_OTHER'}">selected</c:if>>DI_OTHER</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">列:</td>
								<td><input type="number" autocomplete="off" name="COLUMN_NUM" id="COLUMN_NUM" value="${pd.COLUMN_NUM}" maxlength="32" placeholder="这里输入列" title="列" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">行:</td>
								<td><input type="number" autocomplete="off" name="ROW_NUM" id="ROW_NUM" value="${pd.ROW_NUM}" maxlength="32" placeholder="这里输入行" title="行" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">行名称:</td>
								<td><input type="text" autocomplete="off" name="ROW_NAME" id="ROW_NAME" value="${pd.ROW_NAME}" maxlength="255" placeholder="这里输入行名称" title="行名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
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
			if($("#ROW_SET_CODE").val()==""){
				$("#ROW_SET_CODE").tips({
					side:3,
		            msg:'请输入行集代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ROW_SET_CODE").focus();
			return false;
			}
			if($("#ROW_SET").val()==""){
				$("#ROW_SET").tips({
					side:3,
		            msg:'请输入行集',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ROW_SET").focus();
			return false;
			}
			if($("#ROW_SET_TPYE").val()==""){
				$("#ROW_SET_TPYE").tips({
					side:3,
		            msg:'请输入行集类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ROW_SET_TPYE").focus();
			return false;
			}
			if($("#COLUMN_NUM").val()==""){
				$("#COLUMN_NUM").tips({
					side:3,
		            msg:'请输入列',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#COLUMN_NUM").focus();
			return false;
			}
			if($("#ROW_NUM").val()==""){
				$("#ROW_NUM").tips({
					side:3,
		            msg:'请输入行',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ROW_NUM").focus();
			return false;
			}
			if($("#ROW_NAME").val()==""){
				$("#ROW_NAME").tips({
					side:3,
		            msg:'请输入行名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ROW_NAME").focus();
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