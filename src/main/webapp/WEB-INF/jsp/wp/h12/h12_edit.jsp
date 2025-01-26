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
					
					<form action="h12/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="H12_ID" id="H12_ID" value="${pd.H12_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" readonly value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" readonly value="${pd.FIRM_CODE}" maxlength="255" title="公司代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">期末交易状态:</td>
								<td><input type="text" autocomplete="off" name="TRX_STATUS" id="TRX_STATUS" readonly value="${pd.TRX_STATUS}" maxlength="255" title="期末交易状态" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票代码:</td>
								<td><input type="text" autocomplete="off" name="STOCK_CODE" id="STOCK_CODE" readonly value="${pd.STOCK_CODE}" maxlength="60" title="股票代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票名称:</td>
								<td><input type="text" autocomplete="off" name="STOCK_NAME" id="STOCK_NAME" readonly value="${pd.STOCK_NAME}" maxlength="255" title="股票名称" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">折扣率来源:</td>
								<td><input type="text" autocomplete="off" name="LOMD_SOURCE" id="LOMD_SOURCE" value="${pd.LOMD_SOURCE}" maxlength="60" title="折扣率来源" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">折扣率:</td>
								<td><input type="text" autocomplete="off" name="LOMD" id="LOMD" value="${pd.LOMD}" maxlength="20" title="折扣率" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">参考估值类型代码:</td>
								<td><input type="text" autocomplete="off" name="VAL_TYPE_REF" id="VAL_TYPE_REF" value="${pd.VAL_TYPE_REF}" maxlength="60" title="参考估值类型代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票的估值类型:</td>
								<td><input type="text" autocomplete="off" name="VAL_TYPE_NAME" id="VAL_TYPE_NAME" value="${pd.VAL_TYPE_NAME}" maxlength="120" title="股票的估值类型" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票的估值单价:</td>
								<td><input type="text" autocomplete="off" name="VAL_PRICE_REF" id="VAL_PRICE_REF" value="${pd.VAL_PRICE_REF}" maxlength="20" title="股票的估值单价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">非公开股票的估值单价:</td>
								<td><input type="text" autocomplete="off" name="VAL_PRICE" id="VAL_PRICE" value="${pd.VAL_PRICE}" maxlength="20" title="非公开股票的估值单价" style="width:98%;"/></td>
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
			if($("#TRX_STATUS").val()==""){
				$("#TRX_STATUS").tips({
					side:3,
		            msg:'请输入期末交易状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TRX_STATUS").focus();
			return false;
			}
			if($("#STOCK_CODE").val()==""){
				$("#STOCK_CODE").tips({
					side:3,
		            msg:'请输入股票代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_CODE").focus();
			return false;
			}
			if($("#STOCK_NAME").val()==""){
				$("#STOCK_NAME").tips({
					side:3,
		            msg:'请输入股票名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_NAME").focus();
			return false;
			}
			if($("#LOMD_SOURCE").val()==""){
				$("#LOMD_SOURCE").tips({
					side:3,
		            msg:'请输入折扣率来源',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LOMD_SOURCE").focus();
			return false;
			}
			if($("#LOMD").val()==""){
				$("#LOMD").tips({
					side:3,
		            msg:'请输入折扣率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LOMD").focus();
			return false;
			}
			if($("#VAL_TYPE_REF").val()==""){
				$("#VAL_TYPE_REF").tips({
					side:3,
		            msg:'请输入参考估值类型代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_TYPE_REF").focus();
			return false;
			}
			if($("#VAL_TYPE_NAME").val()==""){
				$("#VAL_TYPE_NAME").tips({
					side:3,
		            msg:'请输入股票的估值类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_TYPE_NAME").focus();
			return false;
			}
			if($("#VAL_PRICE_REF").val()==""){
				$("#VAL_PRICE_REF").tips({
					side:3,
		            msg:'请输入股票的估值单价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_PRICE_REF").focus();
			return false;
			}
			if($("#VAL_PRICE").val()==""){
				$("#VAL_PRICE").tips({
					side:3,
		            msg:'请输入非公开股票的估值单价',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#VAL_PRICE").focus();
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