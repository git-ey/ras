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
					
					<form action="v200risk/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="v200risk_ID" id="v200risk_ID" value="${pd.v200risk_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">公司代码:</td>
								<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="60" title="公司代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" autocomplete="off" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}" maxlength="30" title="基金代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
								<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">货币基金:</td>
								<td><input type="text" autocomplete="off" name="MMF" id="MMF" value="${pd.MMF}" maxlength="30" title="货币基金" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">摊余成本法:</td>
								<td><input type="text" autocomplete="off" name="MAC" id="MAC" value="${pd.MAC}" maxlength="30" title="摊余成本法" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">科目代码:</td>
								<td><input type="text" autocomplete="off" name="ACCOUNT_NUM" id="ACCOUNT_NUM" value="${pd.ACCOUNT_NUM}" maxlength="60" title="科目代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券代码:</td>
								<td><input type="text" autocomplete="off" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="30" title="债券代码" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">债券名称:</td>
								<td><input type="text" autocomplete="off" name="BOND_NAME" id="BOND_NAME" value="${pd.BOND_NAME}" maxlength="60" title="债券名称" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">市场:</td>
								<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="32" title="市场" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">类型:</td>
								<td><input type="text" autocomplete="off" name="TYPE" id="TYPE" value="${pd.TYPE}" maxlength="32" title="类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">子类型:</td>
								<td><input type="text" autocomplete="off" name="SUB_TYPE" id="SUB_TYPE" value="${pd.SUB_TYPE}" maxlength="32" title="子类型" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">V200分类:</td>
								<td><input type="text" autocomplete="off" name="V_TYPE" id="V_TYPE" value="${pd.V_TYPE}" maxlength="32" title="V200分类" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">数量:</td>
								<td><input type="number" autocomplete="off" name="QUANTITY" id="QUANTITY" value="${pd.QUANTITY}" maxlength="60" title="数量" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">市值perClient:</td>
								<td><input type="number" autocomplete="off" name="MKT_VALUE_CLIENT" id="MKT_VALUE_CLIENT" value="${pd.MKT_VALUE_CLIENT}" maxlength="60" title="市值perClient" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">最新债券评级机构:</td>
								<td><input type="text" autocomplete="off" name="BOND_RATING_ORG" id="BOND_RATING_ORG" value="${pd.BOND_RATING_ORG}" maxlength="32" title="最新债券评级机构" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">发债主体最新评级机构:</td>
								<td><input type="text" autocomplete="off" name="ENTITY_RATING_ORG" id="ENTITY_RATING_ORG" value="${pd.ENTITY_RATING_ORG}" maxlength="32" title="发债主体最新评级机构" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">发债主体最新评级:</td>
								<td><input type="text" autocomplete="off" name="ENTITY_RATING" id="ENTITY_RATING" value="${pd.ENTITY_RATING}" maxlength="32" title="发债主体最新评级" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期限值:</td>
								<td><input type="number" autocomplete="off" name="DURATING_NUM" id="DURATING_NUM" value="${pd.DURATING_NUM}" maxlength="60" title="期限值" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">期限分类:</td>
								<td><input type="text" autocomplete="off" name="DURATION" id="DURATION" value="${pd.DURATION}" maxlength="32" title="期限分类" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">评级取值:</td>
								<td><input type="text" autocomplete="off" name="V_RATING" id="V_RATING" value="${pd.V_RATING}" maxlength="32" title="评级取值" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:100px;text-align: right;padding-top: 13px;">评级分类:</td>
								<td><input type="text" autocomplete="off" name="RATING" id="RATING" value="${pd.RATING}" maxlength="32" title="评级分类" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">备注:</td>
								<td><input type="text" autocomplete="off" name="DESCRIPTION" id="DESCRIPTION" value="${pd.DESCRIPTION}" maxlength="32" title="备注" style="width:98%;"/></td>
								<td style="width:100px;text-align: right;padding-top: 13px;">状态:</td>
								<td><input type="text" autocomplete="off" name="STATUS" id="STATUS" value="${pd.STATUS}" maxlength="32" title="状态" style="width:98%;"/></td>	
							</tr>
							<tr>
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
			if($("#MARKET").val()==""){
				$("#MARKET").tips({
					side:3,
		            msg:'请输入市场',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MARKET").focus();
			return false;
			}
			if($("#TYPE").val()==""){
				$("#TYPE").tips({
					side:3,
		            msg:'请输入类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TYPE").focus();
			return false;
			}
			if($("#SUB_TYPE").val()==""){
				$("#SUB_TYPE").tips({
					side:3,
		            msg:'请输入子类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUB_TYPE").focus();
			return false;
			}
			if($("#QUANTITY").val()==""){
				$("#QUANTITY").tips({
					side:3,
		            msg:'请输入数量',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#QUANTITY").focus();
			return false;
			}
			if($("#MKT_VALUE_CLIENT").val()==""){
				$("#MKT_VALUE_CLIENT").tips({
					side:3,
		            msg:'请输入市值perClient',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MKT_VALUE_CLIENT").focus();
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