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
					
					<form action="fundstructured/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="FUNDSTRUCTURED_ID" id="FUNDSTRUCTURED_ID" value="${pd.FUNDSTRUCTURED_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金ID:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="100" title="基金ID" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">分级基金代码:</td>
								<td><input type="text" autocomplete="off" name="FUND_CODE" id="FUND_CODE" value="${pd.FUND_CODE}" maxlength="60" title="分级基金代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">分级基金简称:</td>
								<td><input type="text" autocomplete="off" name="SHORT_NAME" id="SHORT_NAME" value="${pd.SHORT_NAME}" maxlength="120" title="分级基金简称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">第几级:</td>
								<td><input type="text" autocomplete="off" name="LEVEL" id="LEVEL" value="${pd.LEVEL}" maxlength="10" title="第几级" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:110px;text-align: right;padding-top: 13px;">生效日期:</td>
								<td><input class="span10 date-picker" name="EFFECTIVE_DATE" id="EFFECTIVE_DATE" value="${pd.EFFECTIVE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="生效日期" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
								    <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
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
			if($("#FUND_ID").val()==""){
				$("#FUND_ID").tips({
					side:3,
		            msg:'请输入基金ID',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
			return false;
			}
			if($("#FUND_CODE").val()==""){
				$("#FUND_CODE").tips({
					side:3,
		            msg:'请输入分级基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_CODE").focus();
			return false;
			}
			if($("#SHORT_NAME").val()==""){
				$("#SHORT_NAME").tips({
					side:3,
		            msg:'请输入分级基金简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHORT_NAME").focus();
			return false;
			}
			if($("#LEVEL").val()==""){
				$("#LEVEL").tips({
					side:3,
		            msg:'请输入第几级',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LEVEL").focus();
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