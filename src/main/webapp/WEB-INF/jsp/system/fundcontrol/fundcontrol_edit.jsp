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
					
					<form action="fundcontrol/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUNDCONTROL_ID" id="FUNDCONTROL_ID" value="${pd.FUNDCONTROL_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" title="基金代码" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票投资收益构成:</td>
								<td><input type="text" name="STOCK_ALL" id="STOCK_ALL" value="${pd.STOCK_ALL}" maxlength="30" title="股票投资收益构成" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票买卖差价收入:</td>
								<td><input type="text" name="STOCK_BS" id="STOCK_BS" value="${pd.STOCK_BS}" maxlength="30" title="股票买卖差价收入" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票赎回差价收入:</td>
								<td><input type="text" name="STOCK_R" id="STOCK_R" value="${pd.STOCK_R}" maxlength="30" title="股票赎回差价收入" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">股票申购差价收入:</td>
								<td><input type="text" name="STOCK_P" id="STOCK_P" value="${pd.STOCK_P}" maxlength="30" title="股票申购差价收入" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券投资收益构成:</td>
								<td><input type="text" name="BOND_ALL" id="BOND_ALL" value="${pd.BOND_ALL}" maxlength="30" title="债券投资收益构成" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券买卖差价收入:</td>
								<td><input type="text" name="BOND_BS" id="BOND_BS" value="${pd.BOND_BS}" maxlength="30" title="债券买卖差价收入" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券赎回差价收入:</td>
								<td><input type="text" name="BOND_R" id="BOND_R" value="${pd.BOND_R}" maxlength="30" title="债券赎回差价收入" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">债券申购差价收入:</td>
								<td><input type="text" name="BOND_P" id="BOND_P" value="${pd.BOND_P}" maxlength="30" title="债券申购差价收入" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属投资收益构成:</td>
								<td><input type="text" name="GOLD_ALL" id="GOLD_ALL" value="${pd.GOLD_ALL}" maxlength="30" title="贵金属投资收益构成" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属买卖差价收入:</td>
								<td><input type="text" name="GOLD_BS" id="GOLD_BS" value="${pd.GOLD_BS}" maxlength="30" title="贵金属买卖差价收入" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属赎回差价收入:</td>
								<td><input type="text" name="GOLD_R" id="GOLD_R" value="${pd.GOLD_R}" maxlength="30" title="贵金属赎回差价收入" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">贵金属申购差价收入:</td>
								<td><input type="text" name="GOLD_P" id="GOLD_P" value="${pd.GOLD_P}" maxlength="30" title="贵金属申购差价收入" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">利率风险敏感性:</td>
								<td><input type="text" name="RISK_S_INT" id="RISK_S_INT" value="${pd.RISK_S_INT}" maxlength="30" title="利率风险敏感性" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">价格风险敏感性:</td>
								<td><input type="text" name="RISK_S_PRICE" id="RISK_S_PRICE" value="${pd.RISK_S_PRICE}" maxlength="30" title="价格风险敏感性" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:140px;text-align: right;padding-top: 13px;">价格风险敞口:</td>
								<td><input type="text" name="RISK_E_PRICE" id="RISK_E_PRICE" value="${pd.RISK_E_PRICE}" maxlength="30" title="价格风险敞口" style="width:98%;"/></td>
								<td style="width:140px;text-align: right;padding-top: 13px;">启用:</td>
								<td><input type="text" name="ACTIVE" id="ACTIVE" value="${pd.ACTIVE}" maxlength="10" title="启用" style="width:98%;"/></td>
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
		            msg:'请输入基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
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