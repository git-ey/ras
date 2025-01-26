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
					
					<form action="termline/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="TERMLINE_ID" id="TERMLINE_ID" value="${pd.TERMLINE_ID}"/>
						<input type="hidden" name="TERMHEAD_ID" id="TERMHEAD_ID" value="${pd.TERMHEAD_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">顺序:</td>
								<td><input type="number" autocomplete="off" name="SORT" id="SORT" value="${pd.SORT}" maxlength="32" placeholder="这里输入顺序" title="顺序" style="width:49%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">账龄期限名称:</td>
								<td><input type="text" autocomplete="off" name="PERIOD_NAME" id="PERIOD_NAME" value="${pd.PERIOD_NAME}" maxlength="60" placeholder="这里输入账龄期限名称" title="账龄期限名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">披露名称:</td>
								<td><input type="text" autocomplete="off" name="REVEAL_NAME" id="REVEAL_NAME" value="${pd.REVEAL_NAME}" maxlength="120" placeholder="这里输入披露名称" title="披露名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">月数从:</td>
								<td><input type="number" autocomplete="off" name="MONTH_FROM" id="MONTH_FROM" value="${pd.MONTH_FROM}" maxlength="32" placeholder="均为>" title="月数从" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">月数至:</td>
								<td><input type="number" autocomplete="off" name="MONTH_TO" id="MONTH_TO" value="${pd.MONTH_TO}" maxlength="32" placeholder="均为<=" title="月数至" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">说明:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="140" placeholder="这里输入说明" title="说明" style="width:98%;"/></td>
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
			if($("#SORT").val()==""){
				$("#SORT").tips({
					side:3,
		            msg:'请输入顺序',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SORT").focus();
			return false;
			}
			if($("#PERIOD_NAME").val()==""){
				$("#PERIOD_NAME").tips({
					side:3,
		            msg:'请输入账龄期限名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PERIOD_NAME").focus();
			return false;
			}
			if($("#REVEAL_NAME").val()==""){
				$("#REVEAL_NAME").tips({
					side:3,
		            msg:'请输入披露名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#REVEAL_NAME").focus();
			return false;
			}
			if($("#MONTH_FROM").val()==""){
				$("#MONTH_FROM").tips({
					side:3,
		            msg:'请输入月数从',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MONTH_FROM").focus();
			return false;
			}
			if($("#MONTH_TO").val()==""){
				$("#MONTH_TO").tips({
					side:3,
		            msg:'请输入月数至',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MONTH_TO").focus();
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