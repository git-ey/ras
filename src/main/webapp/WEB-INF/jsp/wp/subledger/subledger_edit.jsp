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
					
					<form action="subledger/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="SUBLEDGER_ID" id="SUBLEDGER_ID" value="${pd.SUBLEDGER_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="255" placeholder="这里输入基金" title="基金" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="255" placeholder="这里输入期间" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" autocomplete="off" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="255" placeholder="这里输入科目代码" title="科目代码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">凭证序号:</td>
								<td><input type="text" autocomplete="off" name="LINE" id="LINE" value="${pd.LINE}" maxlength="255" placeholder="这里输入凭证序号" title="凭证序号" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">记账日期:</td>
								<td><input class="span10 date-picker" name="EFFECTIVE_DATE" id="EFFECTIVE_DATE" value="${pd.EFFECTIVE_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="记账日期" title="记账日期" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">凭证编号:</td>
								<td><input type="text" autocomplete="off" name="SQUENCE_NUM" id="SQUENCE_NUM" value="${pd.SQUENCE_NUM}" maxlength="255" placeholder="这里输入凭证编号" title="凭证编号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">摘要:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="255" placeholder="这里输入摘要" title="摘要" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">币种:</td>
								<td><input type="text" autocomplete="off" name="CURRENCY" id="CURRENCY" value="${pd.CURRENCY}" maxlength="30" placeholder="这里输入币种" title="币种" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">借方份额:</td>
								<td><input type="number" autocomplete="off" name="DR_UNITS" id="DR_UNITS" value="${pd.DR_UNITS}" maxlength="32" placeholder="这里输入借方份额" title="借方份额" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">贷方份额:</td>
								<td><input type="number" autocomplete="off" name="CR_UNITS" id="CR_UNITS" value="${pd.CR_UNITS}" maxlength="32" placeholder="这里输入贷方份额" title="贷方份额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">借方金额:</td>
								<td><input type="number" autocomplete="off" name="DR_AMOUNT" id="DR_AMOUNT" value="${pd.DR_AMOUNT}" maxlength="32" placeholder="这里输入借方金额" title="借方金额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">贷方金额:</td>
								<td><input type="number" autocomplete="off" name="CR_AMOUNT" id="CR_AMOUNT" value="${pd.CR_AMOUNT}" maxlength="32" placeholder="这里输入贷方金额" title="贷方金额" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">年末借贷方:</td>
								<td><input type="number" autocomplete="off" name="END_DRCR" id="END_DRCR" value="${pd.END_DRCR}" maxlength="32" placeholder="这里输入年末借贷方" title="年末借贷方" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">年末份额:</td>
								<td><input type="number" autocomplete="off" name="END_UNITS" id="END_UNITS" value="${pd.END_UNITS}" maxlength="32" placeholder="这里输入年末份额" title="年末份额" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">年末金额:</td>
								<td><input type="number" autocomplete="off" name="END_BALANCE" id="END_BALANCE" value="${pd.END_BALANCE}" maxlength="32" placeholder="这里输入年末金额" title="年末金额" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">启用:</td>
								<td>
							        <select class="chosen-select form-control" name="ACTIVE" id="ACTIVE" data-placeholder="请选择" style="width:49%;">
								    <option value="Y" <c:if test="${pd.ACTIVE == 'Y'}">selected</c:if>>是</option>
								    <option value="N" <c:if test="${pd.ACTIVE == 'N'}">selected</c:if>>否</option>
								    </select>
								</td>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
								<td>
									<select class="chosen-select form-control" name="STATUS" id="STATUS" data-placeholder="请选择" style="width:49%;">
								    <option value="INITIAL" <c:if test="${pd.STATUS == 'INITIAL'}">selected</c:if>>INITIAL</option>
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
		            msg:'请输入基金',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_ID").focus();
			return false;
			}
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
			if($("#ACCOUNT_NUM").val()==""){
				$("#ACCOUNT_NUM").tips({
					side:3,
		            msg:'请输入科目代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACCOUNT_NUM").focus();
			return false;
			}
			if($("#ACTIVE").val()==""){
				$("#ACTIVE").tips({
					side:3,
		            msg:'请输入启用',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ACTIVE").focus();
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