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
					
					<form action="futureinfo/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUTUREINFO_ID" id="FUTUREINFO_ID" value="${pd.FUTUREINFO_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">证券代码:</td>
								<td><input type="text" autocomplete="off" name="FUTURE_CODE" id="FUTURE_CODE" value="${pd.FUTURE_CODE}" maxlength="60" title="证券代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">证券简称:</td>
								<td><input type="text" autocomplete="off" name="FUTURE_NAME" id="FUTURE_NAME" value="${pd.FUTURE_NAME}" maxlength="255"  title="证券简称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">上市市场:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="30" title="上市市场" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易品种:</td>
								<td><input type="text" autocomplete="off" name="SUB_TYPE" id="SUB_TYPE" value="${pd.SUB_TYPE}" maxlength="60" title="交易品种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">合约标的:</td>
								<td><input type="text" autocomplete="off" name="OBJECT" id="OBJECT" value="${pd.OBJECT}" maxlength="255" title="合约标的" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">合约乘数:</td>
								<td><input type="number" autocomplete="off" name="MULTIPLIER" id="MULTIPLIER" value="${pd.MULTIPLIER}" maxlength="32" title="合约乘数" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">结算价:</td>
								<td><input type="number" autocomplete="off" name="UNIT_SETTL_PRICE" id="UNIT_SETTL_PRICE" value="${pd.UNIT_SETTL_PRICE}" maxlength="32" title="结算价" style="width:98%;"/></td>
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
			if($("#PERIOD").val()==""){
				$("#PERIOD").tips({
					side:3,
		            msg:'请输入期间',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PERIOD").focus();
			return false;
			}
			if($("#FUTURE_CODE").val()==""){
				$("#FUTURE_CODE").tips({
					side:3,
		            msg:'请输入证券代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUTURE_CODE").focus();
			return false;
			}
			if($("#FUTURE_NAME").val()==""){
				$("#FUTURE_NAME").tips({
					side:3,
		            msg:'请输入证券简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUTURE_NAME").focus();
			return false;
			}
			if($("#MARKET").val()==""){
				$("#MARKET").tips({
					side:3,
		            msg:'请输入上市市场',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MARKET").focus();
			return false;
			}
			if($("#SUB_TYPE").val()==""){
				$("#SUB_TYPE").tips({
					side:3,
		            msg:'请输入交易品种',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUB_TYPE").focus();
			return false;
			}
			if($("#OBJECT").val()==""){
				$("#OBJECT").tips({
					side:3,
		            msg:'请输入合约标的',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#OBJECT").focus();
			return false;
			}
			if($("#MULTIPLIER").val()==""){
				$("#MULTIPLIER").tips({
					side:3,
		            msg:'请输入合约乘数',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MULTIPLIER").focus();
			return false;
			}
			if($("#UNIT_SETTL_PRICE").val()==""){
				$("#UNIT_SETTL_PRICE").tips({
					side:3,
		            msg:'请输入结算价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT_SETTL_PRICE").focus();
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