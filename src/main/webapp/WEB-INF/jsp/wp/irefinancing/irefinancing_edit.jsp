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
					
					<form action="irefinancing/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="IREFINANCING_ID" id="IREFINANCING_ID" value="${pd.IREFINANCING_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="60" title="公司代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" title="基金代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">合同编号:</td>
								<td><input type="text" autocomplete="off" name="CONTRACT_NUM" id="CONTRACT_NUM" value="${pd.CONTRACT_NUM}" maxlength="60" title="合同编号" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券代码:</td>
								<td><input type="text" autocomplete="off" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="30" title="债券代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券名称:</td>
								<td><input type="text" autocomplete="off" name="BOND_NAME" id="BOND_NAME" value="${pd.BOND_NAME}" maxlength="60" title="债券名称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">成交日期:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="TRX_DATE" id="TRX_DATE" value="${pd.TRX_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="成交日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">出借到期日:</td>
								<td><input class="span10 date-picker" autocomplete="off" name="DUE_DATE" id="DUE_DATE" value="${pd.DUE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="出借到期日" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期末估值单价:</td>
								<td><input type="number" autocomplete="off" name="UNIT_PRICE" id="UNIT_PRICE" value="${pd.UNIT_PRICE}" maxlength="30" title="期末估值单价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">数量(单位：股):</td>
								<td><input type="number" autocomplete="off" name="QUANTITY" id="QUANTITY" value="${pd.QUANTITY}" maxlength="60" title="数量(单位：股)" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期末估值总额:</td>
								<td><input type="number" autocomplete="off" name="VAL_VALUE" id="VAL_VALUE" value="${pd.VAL_VALUE}" maxlength="60" title="期末估值总额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">市场:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="32" title="市场" style="width:98%;"/></td>
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
			if($("#FIRM_CODE").val()==""){
				$("#FIRM_CODE").tips({
					side:3,
		            msg:'请输入公司代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIRM_CODE").focus();
			return false;
			}
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
			if($("#CONTRACT_NUM").val()==""){
				$("#CONTRACT_NUM").tips({
					side:3,
		            msg:'请输入合同编号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CONTRACT_NUM").focus();
			return false;
			}
			if($("#BOND_CODE").val()==""){
				$("#BOND_CODE").tips({
					side:3,
		            msg:'请输入债券代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_CODE").focus();
			return false;
			}
			if($("#BOND_NAME").val()==""){
				$("#BOND_NAME").tips({
					side:3,
		            msg:'请输入债券名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_NAME").focus();
			return false;
			}
			if($("#TRX_DATE").val()==""){
				$("#TRX_DATE").tips({
					side:3,
		            msg:'请输入成交日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_DATE").focus();
			return false;
			}
			if($("#DUE_DATE").val()==""){
				$("#DUE_DATE").tips({
					side:3,
		            msg:'请输入出借到期日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DUE_DATE").focus();
			return false;
			}
			if($("#UNIT_PRICE").val()==""){
				$("#UNIT_PRICE").tips({
					side:3,
		            msg:'请输入期末估值单价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT_PRICE").focus();
			return false;
			}
			if($("#QUANTITY").val()==""){
				$("#QUANTITY").tips({
					side:3,
		            msg:'请输入数量(单位：股)',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#QUANTITY").focus();
			return false;
			}
			if($("#VAL_VALUE").val()==""){
				$("#VAL_VALUE").tips({
					side:3,
		            msg:'请输入期末估值总额',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_VALUE").focus();
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