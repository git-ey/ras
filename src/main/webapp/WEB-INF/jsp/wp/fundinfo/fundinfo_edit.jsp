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
					
					<form action="fundinfo/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUNDINFO_ID" id="FUNDINFO_ID" value="${pd.FUNDINFO_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">证券代码:</td>
								<td><input type="text" name="FUND_CODE" id="FUND_CODE" value="${pd.FUND_CODE}" maxlength="30" title="证券代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">证券简称:</td>
								<td><input type="text" name="FUND_NAME" id="FUND_NAME" value="${pd.FUND_NAME}" maxlength="120" title="证券简称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金全称:</td>
								<td><input type="text" name="FUND_FULLNAME" id="FUND_FULLNAME" value="${pd.FUND_FULLNAME}" maxlength="240" title="基金全称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">上市市场:</td>
								<td><input type="text" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="30" title="上市市场" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">交易状态:</td>
								<td><input type="text" name="TRX_STATUS" id="TRX_STATUS" value="${pd.TRX_STATUS}" maxlength="30" title="交易状态" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">申赎状态:</td>
								<td><input type="text" name="RE_STATUS" id="RE_STATUS" value="${pd.RE_STATUS}" maxlength="30" title="申赎状态" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">收盘价:</td>
								<td><input type="number" name="CLOSING_PRICE" id="CLOSING_PRICE" value="${pd.CLOSING_PRICE}" maxlength="32" title="收盘价" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">单位净值:</td>
								<td><input type="number" name="UNIT_NAV" id="UNIT_NAV" value="${pd.UNIT_NAV}" maxlength="32" title="单位净值" style="width:98%;"/></td>
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
			if($("#FUND_CODE").val()==""){
				$("#FUND_CODE").tips({
					side:3,
		            msg:'请输入证券代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_CODE").focus();
			return false;
			}
			if($("#FUND_NAME").val()==""){
				$("#FUND_NAME").tips({
					side:3,
		            msg:'请输入证券简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_NAME").focus();
			return false;
			}
			<!-- 
			if($("#FUND_FULLNAME").val()==""){
				$("#FUND_FULLNAME").tips({
					side:3,
		            msg:'请输入基金全称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_FULLNAME").focus();
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
			if($("#TRX_STATUS").val()==""){
				$("#TRX_STATUS").tips({
					side:3,
		            msg:'请输入交易状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_STATUS").focus();
			return false;
			}
			if($("#RE_STATUS").val()==""){
				$("#RE_STATUS").tips({
					side:3,
		            msg:'请输入申赎状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RE_STATUS").focus();
			return false;
			}
			if($("#CLOSING_PRICE").val()==""){
				$("#CLOSING_PRICE").tips({
					side:3,
		            msg:'请输入收盘价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CLOSING_PRICE").focus();
			return false;
			}
			if($("#UNIT_NAV").val()==""){
				$("#UNIT_NAV").tips({
					side:3,
		            msg:'请输入单位净值',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT_NAV").focus();
			return false;
			}
			-->
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