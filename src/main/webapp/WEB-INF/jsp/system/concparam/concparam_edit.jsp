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
					
					<form action="concparam/${msg}.do" name="Form" id="Form" method="post">
						<input type="hidden" name="CONC_PARAM_ID" id="CONC_PARAM_ID" value="${pd.CONC_PARAM_ID}"/>
						<input type="hidden" name="CONC_ID" id="CONC_ID" value="${pd.CONC_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">序号:</td>
								<td><input type="number" name="SEQ" id="SEQ" value="${pd.SEQ}" maxlength="60" title="序号" style="width:49%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">参数代码:</td>
								<td><input type="text" name="PARAM_CODE" id="PARAM_CODE" value="${pd.PARAM_CODE}" maxlength="60" title="参数代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">参数名称:</td>
								<td><input type="text" name="PARAM_NAME" id="PARAM_NAME" value="${pd.PARAM_NAME}" maxlength="120"  title="参数名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">参数类型:</td>
								<td>
								    <select class="chosen-select form-control" name="PARAM_TYPE" id="PARAM_TYPE" data-placeholder="请选择" style="width:60%;">
								    <option value="String" <c:if test="${pd.PARAM_TYPE == 'String'}">selected</c:if>>String</option>
								    <option value="Date" <c:if test="${pd.PARAM_TYPE == 'Date'}">selected</c:if>>Date</option>
								    </select>
								</td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">参数值:</td>
								<td><input type="text" name="CONC_VALUE" id="CONC_VALUE" value="${pd.CONC_VALUE}" maxlength="255"  title="参数值" style="width:98%;"/></td>
							</tr>
							<tr>
							    <td style="width:75px;text-align: right;padding-top: 13px;">允许为空:</td>
								<td>
								    <select class="chosen-select form-control" name="NULL_FLAG" id="NULL_FLAG" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.NULL_FLAG == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.NULL_FLAG == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
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
			if($("#SEQ").val()==""){
				$("#SEQ").tips({
					side:3,
		            msg:'请输入参数序号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SEQ").focus();
			return false;
			}
			if($("#PARAM_CODE").val()==""){
				$("#PARAM_CODE").tips({
					side:3,
		            msg:'请输入参数代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARAM_CODE").focus();
			return false;
			}
			if($("#PARAM_NAME").val()==""){
				$("#PARAM_NAME").tips({
					side:3,
		            msg:'请输入参数名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARAM_NAME").focus();
			return false;
			}
			if($("#PARAM_TYPE").val()==""){
				$("#PARAM_TYPE").tips({
					side:3,
		            msg:'请输入参数类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PARAM_TYPE").focus();
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