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
					
					<form action="bondinfo/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="hidden" name="BONDINFO_ID" id="BONDINFO_ID" value="${pd.BONDINFO_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
							<table id="table_report" class="table table-striped table-bordered table-hover">
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">公司简称:</td>
									<td><input type="text" autocomplete="off" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="120" title="公司简称" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">期间:</td>
									<td><input type="text" autocomplete="off" name="PERIOD" id="PERIOD" value="${pd.PERIOD}" maxlength="30" title="期间" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">债券代码:</td>
									<td><input type="text" autocomplete="off" name="BOND_CODE" id="BOND_CODE" value="${pd.BOND_CODE}" maxlength="60" title="债券代码" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">债券简称:</td>
									<td><input type="text" autocomplete="off" name="BOND_NAME" id="BOND_NAME" value="${pd.BOND_NAME}" maxlength="120" title="债券简称" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">债券全称:</td>
									<td><input type="text" autocomplete="off" name="FULL_NAME" id="FULL_NAME" value="${pd.FULL_NAME}" maxlength="255" title="债券全称" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">上市市场:</td>
									<td><input type="text" autocomplete="off" name="MARKET" id="MARKET" value="${pd.MARKET}" maxlength="10" title="上市市场" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">债券类型:</td>
									<td><input type="text" autocomplete="off" name="BOND_TYPE" id="BOND_TYPE" value="${pd.BOND_TYPE}" maxlength="60" title="债券类型" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">停牌状态:</td>
									<td><input type="text" autocomplete="off" name="SUSPENSION" id="SUSPENSION" value="${pd.SUSPENSION}" maxlength="10" title="停牌状态" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">停牌原因:</td>
									<td><input type="text" autocomplete="off" name="SUSPENSION_INFO" id="SUSPENSION_INFO" value="${pd.SUSPENSION_INFO}" maxlength="255" title="停牌原因" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">发行面值:</td>
									<td><input type="number" autocomplete="off" name="PAR_VALUE_ISSUE" id="PAR_VALUE_ISSUE" value="${pd.PAR_VALUE_ISSUE}" maxlength="32" title="发行面值" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">最新面值:</td>
									<td><input type="number" autocomplete="off" name="PAR_VALUE_LAST" id="PAR_VALUE_LAST" value="${pd.PAR_VALUE_LAST}" maxlength="32" title="最新面值" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">票面利率%:</td>
									<td><input type="number" autocomplete="off" name="COUPON_RATE" id="COUPON_RATE" value="${pd.COUPON_RATE}" maxlength="32" title="票面利率%" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">发行价格 元:</td>
									<td><input type="number" autocomplete="off" name="ISSUE_PRICE" id="ISSUE_PRICE" value="${pd.ISSUE_PRICE}" maxlength="32" title="发行价格 元" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">起息日期:</td>
									<td><input class="span10 date-picker" name="DATE_FROM" id="DATE_FROM" value="${pd.DATE_FROM}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="起息日期" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">止息日期:</td>
									<td><input class="span10 date-picker" name="DATE_TO" id="DATE_TO" value="${pd.DATE_TO}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="止息日期" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">兑付日:</td>
									<td><input class="span10 date-picker" name="DATE_PAY" id="DATE_PAY" value="${pd.DATE_PAY}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="兑付日" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">计息方式:</td>
									<td><input type="text" autocomplete="off" name="INTEREST_MODE" id="INTEREST_MODE" value="${pd.INTEREST_MODE}" maxlength="255" title="计息方式" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">付息方式:</td>
									<td><input type="text" autocomplete="off" name="PAYMENT_METHOD" id="PAYMENT_METHOD" value="${pd.PAYMENT_METHOD}" maxlength="255" title="付息方式" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">年付息次数:</td>
									<td><input type="number" autocomplete="off" name="PAYMENT_TIMES_YEAR" id="PAYMENT_TIMES_YEAR" value="${pd.PAYMENT_TIMES_YEAR}" maxlength="32" title="年付息次数" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">一年多次付息应计利息处理规则:</td>
									<td><input type="text" autocomplete="off" name="INTEREST_PAY_METHOD" id="INTEREST_PAY_METHOD" value="${pd.INTEREST_PAY_METHOD}" maxlength="255" title="一年多次付息应计利息处理规则" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">每年付息日:</td>
									<td><input class="span10 date-picker" name="PAY_DATE_YEAR" id="PAY_DATE_YEAR" value="${pd.PAY_DATE_YEAR}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="每年付息日" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">上一付息日:</td>
									<td><input class="span10 date-picker" name="PAY_DATE_LAST" id="PAY_DATE_LAST" value="${pd.PAY_DATE_LAST}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="上一付息日" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">下一付息日:</td>
									<td><input class="span10 date-picker" name="PAY_DATE_NEXT" id="PAY_DATE_NEXT" value="${pd.PAY_DATE_NEXT}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="下一付息日" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">最新债券评级:</td>
									<td><input type="text" autocomplete="off" name="BOND_RATING" id="BOND_RATING" value="${pd.BOND_RATING}" maxlength="255" title="最新债券评级" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">最新债券评级机构:</td>
									<td><input type="text" autocomplete="off" name="BOND_RATING_ORG" id="BOND_RATING_ORG" value="${pd.BOND_RATING_ORG}" maxlength="255" title="最新债券评级机构" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">最新债券评级日期:</td>
									<td><input class="span10 date-picker" name="BOND_RATING_DATE" id="BOND_RATING_DATE" value="${pd.BOND_RATING_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="最新债券评级日期" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">发债主体最新评级:</td>
									<td><input type="text" autocomplete="off" name="ENTITY_RATING" id="ENTITY_RATING" value="${pd.ENTITY_RATING}" maxlength="255" title="发债主体最新评级" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">发债主体最新评级机构:</td>
									<td><input type="text" autocomplete="off" name="ENTITY_RATING_ORG" id="ENTITY_RATING_ORG" value="${pd.ENTITY_RATING_ORG}" maxlength="255" title="发债主体最新评级机构" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">发债主体最新评级日期:</td>
									<td><input class="span10 date-picker" name="ENTITY_RATING_DATE" id="ENTITY_RATING_DATE" value="${pd.ENTITY_RATING_DATE}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" title="发债主体最新评级日期" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">是否免税:</td>
									<td><input type="text" autocomplete="off" name="TAX_FREE" id="TAX_FREE" value="${pd.TAX_FREE}" maxlength="10" title="是否免税" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">税率:</td>
									<td><input type="number" autocomplete="off" name="TAX_RATE" id="TAX_RATE" value="${pd.TAX_RATE}" maxlength="32" title="税率" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">特殊条款:</td>
									<td><input type="text" autocomplete="off" name="SPECIAL_CLAUSE" id="SPECIAL_CLAUSE" value="${pd.SPECIAL_CLAUSE}" maxlength="255" title="特殊条款" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">是否存在提前行权:</td>
									<td><input type="text" autocomplete="off" name="EARLY_EXERCISE" id="EARLY_EXERCISE" value="${pd.EARLY_EXERCISE}" maxlength="10" title="是否存在提前行权" style="width:98%;"/></td>	
									<td style="width:100px;text-align: right;padding-top: 13px;">第N年末行权:</td>
									<td><input type="number" autocomplete="off" name="YEAR_N" id="YEAR_N" value="${pd.YEAR_N}" maxlength="32" title="第N年末行权" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">发行人利率选择权:</td>
									<td><input type="text" autocomplete="off" name="INTEREST_RATE_OPTION" id="INTEREST_RATE_OPTION" value="${pd.INTEREST_RATE_OPTION}" maxlength="255" title="发行人利率选择权" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">回售权:</td>
									<td><input type="text" autocomplete="off" name="SELL_BACK" id="SELL_BACK" value="${pd.SELL_BACK}" maxlength="60" title="回售权" style="width:98%;"/></td>
								</tr>
								<tr>
									<td style="width:100px;text-align: right;padding-top: 13px;">赎回权:</td>
									<td><input type="text" autocomplete="off" name="REDEMPTION" id="REDEMPTION" value="${pd.REDEMPTION}" maxlength="120" title="赎回权" style="width:98%;"/></td>
									<td style="width:100px;text-align: right;padding-top: 13px;">计息规则类型:</td>
									<td><input type="text" name="INTEREST_RULE_TYPE" id="INTEREST_RULE_TYPE" value="${pd.INTEREST_RULE_TYPE}" maxlength="120" title="计息规则类型" style="width:98%;"/></td>
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
		            msg:'请输入债券简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_NAME").focus();
			return false;
			}
			<!--
			if($("#FULL_NAME").val()==""){
				$("#FULL_NAME").tips({
					side:3,
		            msg:'请输入债券全称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FULL_NAME").focus();
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
			if($("#BOND_TYPE").val()==""){
				$("#BOND_TYPE").tips({
					side:3,
		            msg:'请输入债券类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_TYPE").focus();
			return false;
			}
			if($("#SUSPENSION").val()==""){
				$("#SUSPENSION").tips({
					side:3,
		            msg:'请输入停牌状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUSPENSION").focus();
			return false;
			}
			if($("#SUSPENSION_INFO").val()==""){
				$("#SUSPENSION_INFO").tips({
					side:3,
		            msg:'请输入停牌原因',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SUSPENSION_INFO").focus();
			return false;
			}
			if($("#PAR_VALUE_ISSUE").val()==""){
				$("#PAR_VALUE_ISSUE").tips({
					side:3,
		            msg:'请输入发行面值',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAR_VALUE_ISSUE").focus();
			return false;
			}
			if($("#PAR_VALUE_LAST").val()==""){
				$("#PAR_VALUE_LAST").tips({
					side:3,
		            msg:'请输入最新面值',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAR_VALUE_LAST").focus();
			return false;
			}
			if($("#COUPON_RATE").val()==""){
				$("#COUPON_RATE").tips({
					side:3,
		            msg:'请输入票面利率%',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#COUPON_RATE").focus();
			return false;
			}
			if($("#ISSUE_PRICE").val()==""){
				$("#ISSUE_PRICE").tips({
					side:3,
		            msg:'请输入发行价格 元',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ISSUE_PRICE").focus();
			return false;
			}
			if($("#DATE_FROM").val()==""){
				$("#DATE_FROM").tips({
					side:3,
		            msg:'请输入起息日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_FROM").focus();
			return false;
			}
			if($("#DATE_TO").val()==""){
				$("#DATE_TO").tips({
					side:3,
		            msg:'请输入止息日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_TO").focus();
			return false;
			}
			if($("#DATE_PAY").val()==""){
				$("#DATE_PAY").tips({
					side:3,
		            msg:'请输入兑付日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_PAY").focus();
			return false;
			}
			if($("#INTEREST_MODE").val()==""){
				$("#INTEREST_MODE").tips({
					side:3,
		            msg:'请输入计息方式',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_MODE").focus();
			return false;
			}
			if($("#PAYMENT_METHOD").val()==""){
				$("#PAYMENT_METHOD").tips({
					side:3,
		            msg:'请输入付息方式',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAYMENT_METHOD").focus();
			return false;
			}
			if($("#PAYMENT_TIMES_YEAR").val()==""){
				$("#PAYMENT_TIMES_YEAR").tips({
					side:3,
		            msg:'请输入年付息次数',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAYMENT_TIMES_YEAR").focus();
			return false;
			}
			if($("#INTEREST_PAY_METHOD").val()==""){
				$("#INTEREST_PAY_METHOD").tips({
					side:3,
		            msg:'请输入一年多次付息应计利息处理规则',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_PAY_METHOD").focus();
			return false;
			}
			if($("#PAY_DATE_YEAR").val()==""){
				$("#PAY_DATE_YEAR").tips({
					side:3,
		            msg:'请输入每年付息日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAY_DATE_YEAR").focus();
			return false;
			}
			if($("#PAY_DATE_LAST").val()==""){
				$("#PAY_DATE_LAST").tips({
					side:3,
		            msg:'请输入上一付息日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAY_DATE_LAST").focus();
			return false;
			}
			if($("#PAY_DATE_NEXT").val()==""){
				$("#PAY_DATE_NEXT").tips({
					side:3,
		            msg:'请输入下一付息日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PAY_DATE_NEXT").focus();
			return false;
			}
			if($("#BOND_RATING").val()==""){
				$("#BOND_RATING").tips({
					side:3,
		            msg:'请输入最新债券评级',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_RATING").focus();
			return false;
			}
			if($("#BOND_RATING_ORG").val()==""){
				$("#BOND_RATING_ORG").tips({
					side:3,
		            msg:'请输入最新债券评级机构',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_RATING_ORG").focus();
			return false;
			}
			if($("#BOND_RATING_DATE").val()==""){
				$("#BOND_RATING_DATE").tips({
					side:3,
		            msg:'请输入最新债券评级日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#BOND_RATING_DATE").focus();
			return false;
			}
			if($("#ENTITY_RATING").val()==""){
				$("#ENTITY_RATING").tips({
					side:3,
		            msg:'请输入发债主体最新评级',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENTITY_RATING").focus();
			return false;
			}
			if($("#ENTITY_RATING_ORG").val()==""){
				$("#ENTITY_RATING_ORG").tips({
					side:3,
		            msg:'请输入发债主体最新评级机构',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENTITY_RATING_ORG").focus();
			return false;
			}
			if($("#ENTITY_RATING_DATE").val()==""){
				$("#ENTITY_RATING_DATE").tips({
					side:3,
		            msg:'请输入发债主体最新评级日期',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ENTITY_RATING_DATE").focus();
			return false;
			}
			if($("#TAX_FREE").val()==""){
				$("#TAX_FREE").tips({
					side:3,
		            msg:'请输入是否免税',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TAX_FREE").focus();
			return false;
			}
			if($("#TAX_RATE").val()==""){
				$("#TAX_RATE").tips({
					side:3,
		            msg:'请输入税率',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TAX_RATE").focus();
			return false;
			}
			if($("#SPECIAL_CLAUSE").val()==""){
				$("#SPECIAL_CLAUSE").tips({
					side:3,
		            msg:'请输入特殊条款',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SPECIAL_CLAUSE").focus();
			return false;
			}
			if($("#EARLY_EXERCISE").val()==""){
				$("#EARLY_EXERCISE").tips({
					side:3,
		            msg:'请输入是否存在提前行权',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#EARLY_EXERCISE").focus();
			return false;
			}
			if($("#YEAR_N").val()==""){
				$("#YEAR_N").tips({
					side:3,
		            msg:'请输入第N年末行权',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#YEAR_N").focus();
			return false;
			}
			if($("#INTEREST_RATE_OPTION").val()==""){
				$("#INTEREST_RATE_OPTION").tips({
					side:3,
		            msg:'请输入发行人利率选择权',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_RATE_OPTION").focus();
			return false;
			}
			if($("#SELL_BACK").val()==""){
				$("#SELL_BACK").tips({
					side:3,
		            msg:'请输入回售权',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SELL_BACK").focus();
			return false;
			}
			if($("#REDEMPTION").val()==""){
				$("#REDEMPTION").tips({
					side:3,
		            msg:'请输入赎回权',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#REDEMPTION").focus();
			return false;
			}
			if($("#INTEREST_RULE_TYPE").val()==""){
				$("#INTEREST_RULE_TYPE").tips({
					side:3,
		            msg:'计息规则类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_RULE_TYPE").focus();
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