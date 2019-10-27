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
					
					<form action="h11head/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="H11HEAD_ID" id="H11HEAD_ID" value="${pd.H11HEAD_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" readonly value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" readonly value="${pd.FIRM_CODE}" maxlength="255" title="公司代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票代码:</td>
								<td><input type="text" autocomplete="off" name="STOCK_CODE" id="STOCK_CODE" readonly value="${pd.STOCK_CODE}" maxlength="60" title="股票代码" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">股票简称:</td>
								<td><input type="text" autocomplete="off" name="STOCK_NAME" id="STOCK_NAME" readonly value="${pd.STOCK_NAME}" maxlength="255" title="股票简称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">AMAC所属行业:</td>
								<td><input type="text" autocomplete="off" name="INDUSTRY" id="INDUSTRY" readonly value="${pd.INDUSTRY}" maxlength="255" title="AMAC所属行业" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">最近交易日:</td>
								<td><input type="text" autocomplete="off" name="RECENT_TRX_DATE" id="RECENT_TRX_DATE" readonly value="${pd.RECENT_TRX_DATE}" maxlength="255" title="最近交易日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">AMAC估值价:</td>
								<td><input type="text" autocomplete="off" name="AMAC_PRICE" id="AMAC_PRICE" value="${pd.AMAC_PRICE}" maxlength="20" title="AMAC估值价" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">最近交易日收盘价:</td>
								<td><input type="text" autocomplete="off" name="RECENT_PRICE" id="RECENT_PRICE" value="${pd.RECENT_PRICE}" maxlength="20" title="最近交易日收盘价" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">使用AMAC估值:</td>
								<td>
								    <select class="chosen-select form-control" name="AMAC" id="AMAC" data-placeholder="请选择" style="width:49%;">
								    <option value=""></option>
								    <option value="Y" <c:if test="${pd.AMAC == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.AMAC == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:120px;text-align: right;padding-top: 13px;">估值价类型:</td>
								<td><input type="text" autocomplete="off" name="VAL_TYPE" id="VAL_TYPE" value="${pd.VAL_TYPE}" maxlength="10" title="估值价类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">估值价格:</td>
								<td><input type="text" autocomplete="off" name="VAL_PRICE" id="VAL_PRICE" value="${pd.VAL_PRICE}" maxlength="20" title="估值价格" style="width:98%;"/></td>
								<td style="width:120px;text-align: right;padding-top: 13px;">结论:</td>
								<td><input type="text" autocomplete="off" name="RESULT" id="RESULT" value="${pd.RESULT}" maxlength="10" title="结论" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:120px;text-align: right;padding-top: 13px;">备注:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="10" title="备注" style="width:98%;"/></td>
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
		            msg:'请输入股票简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STOCK_NAME").focus();
			return false;
			}
			if($("#RECENT_TRX_DATE").val()==""){
				$("#RECENT_TRX_DATE").tips({
					side:3,
		            msg:'请输入最近交易日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#RECENT_TRX_DATE").focus();
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