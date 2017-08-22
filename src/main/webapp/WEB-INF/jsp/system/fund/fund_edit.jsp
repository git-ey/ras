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
					
					<form action="fund/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="FUND_ID" id="FUND_ID" value="${pd.FUND_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">基金代码:</td>
								<td><input type="text" name="FUND_CODE" id="FUND_CODE" value="${pd.FUND_CODE}" maxlength="120" placeholder="这里输入基金代码" title="基金代码" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">管理公司代码:</td>
								<td><input type="text" name="FIRM_CODE" id="FIRM_CODE" value="${pd.FIRM_CODE}" maxlength="120" placeholder="这里输入管理公司代码" title="管理公司代码" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">基金简称:</td>
								<td><input type="text" name="SHORT_NAME" id="SHORT_NAME" value="${pd.SHORT_NAME}" maxlength="60" placeholder="这里输入基金简称" title="基金简称" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">基金全称:</td>
								<td><input type="text" name="FULL_NAME" id="FULL_NAME" value="${pd.FULL_NAME}" maxlength="240" placeholder="这里输入基金全称" title="基金全称" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">基金原名:</td>
								<td><input type="text" name="FULL_NAME_ORIGINAL" id="FULL_NAME_ORIGINAL" value="${pd.FULL_NAME_ORIGINAL}" maxlength="240" placeholder="这里输入基金原名" title="基金原名" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">帐套号:</td>
								<td><input type="text" name="LEDGER_NUM" id="LEDGER_NUM" value="${pd.LEDGER_NUM}" maxlength="60" placeholder="这里输入帐套号" title="帐套号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">TA基金名称:</td>
								<td><input type="text" name="TA_NAME" id="TA_NAME" value="${pd.TA_NAME}" maxlength="240" placeholder="这里输入TA基金名称" title="TA基金名称" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">财务系统:</td>
								<td><input type="text" name="FIN_SYSTEM" id="FIN_SYSTEM" value="${pd.FIN_SYSTEM}" maxlength="60" placeholder="这里输入财务系统" title="财务系统" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">分级:</td>
								<td><input type="text" name="STRUCTURED" id="STRUCTURED" value="${pd.STRUCTURED}" maxlength="30" placeholder="这里输入分级" title="分级" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">保本:</td>
								<td><input type="text" name="GUARANTEED" id="GUARANTEED" value="${pd.GUARANTEED}" maxlength="10" placeholder="这里输入保本" title="保本" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">封闭:</td>
								<td><input type="text" name="CLOSED" id="CLOSED" value="${pd.CLOSED}" maxlength="30" placeholder="这里输入封闭" title="封闭" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">沪港:</td>
								<td><input type="text" name="SHHK" id="SHHK" value="${pd.SHHK}" maxlength="10" placeholder="这里输入沪港" title="沪港" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">QD:</td>
								<td><input type="text" name="QD" id="QD" value="${pd.QD}" maxlength="10" placeholder="这里输入QD" title="QD" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">货基:</td>
								<td><input type="text" name="MF" id="MF" value="${pd.MF}" maxlength="10" placeholder="这里输入货基" title="货基" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">指数:</td>
								<td><input type="text" name="IDX" id="IDX" value="${pd.IDX}" maxlength="10" placeholder="这里输入指数" title="指数" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">LOF:</td>
								<td><input type="text" name="LOF" id="LOF" value="${pd.LOF}" maxlength="10" placeholder="这里输入LOF" title="LOF" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">ETF:</td>
								<td><input type="text" name="ETF" id="ETF" value="${pd.ETF}" maxlength="10" placeholder="这里输入ETF" title="ETF" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">FOF:</td>
								<td><input type="text" name="FOF" id="FOF" value="${pd.FOF}" maxlength="10" placeholder="这里输入FOF" title="FOF" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">货币量纲:</td>
								<td><input type="number" name="UNIT" id="UNIT" value="${pd.UNIT}" maxlength="32" placeholder="这里输入货币量纲" title="货币量纲" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">合同生效日:</td>
								<td><input class="span10 date-picker" name="DATE_FROM" id="DATE_FROM" value="${pd.DATE_FROM}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="合同生效日" title="合同生效日" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">基金终止日:</td>
								<td><input class="span10 date-picker" name="DATE_TO" id="DATE_TO" value="${pd.DATE_TO}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="基金终止日" title="基金终止日" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">基金转型日:</td>
								<td><input class="span10 date-picker" name="DATE_TRANSFORM" id="DATE_TRANSFORM" value="${pd.DATE_TRANSFORM}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="基金转型日" title="基金转型日" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">利率风险敞口账龄:</td>
								<td><input type="text" name="INTEREST_RATE_PERIOD" id="INTEREST_RATE_PERIOD" value="${pd.INTEREST_RATE_PERIOD}" maxlength="120" placeholder="这里输入利率风险敞口账龄" title="利率风险敞口账龄" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">其他负债披露类型:</td>
								<td><input type="text" name="OTHER_LIABILITIES" id="OTHER_LIABILITIES" value="${pd.OTHER_LIABILITIES}" maxlength="120" placeholder="这里输入其他负债披露类型" title="其他负债披露类型" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">启用:</td>
								<td><input type="text" name="ACTIVE" id="ACTIVE" value="${pd.ACTIVE}" maxlength="10" placeholder="这里输入启用" title="启用" style="width:98%;"/></td>
								<td style="width:75px;text-align: right;padding-top: 13px;">状态:</td>
								<td><input type="text" name="STATUS" id="STATUS" value="${pd.STATUS}" maxlength="30" placeholder="这里输入状态" title="状态" style="width:98%;"/></td>
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
			if($("#FUND_CODE").val()==""){
				$("#FUND_CODE").tips({
					side:3,
		            msg:'请输入基金代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FUND_CODE").focus();
			return false;
			}
			if($("#FIRM_CODE").val()==""){
				$("#FIRM_CODE").tips({
					side:3,
		            msg:'请输入管理公司代码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIRM_CODE").focus();
			return false;
			}
			if($("#SHORT_NAME").val()==""){
				$("#SHORT_NAME").tips({
					side:3,
		            msg:'请输入基金简称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHORT_NAME").focus();
			return false;
			}
			if($("#FULL_NAME").val()==""){
				$("#FULL_NAME").tips({
					side:3,
		            msg:'请输入基金全称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FULL_NAME").focus();
			return false;
			}
			if($("#FULL_NAME_ORIGINAL").val()==""){
				$("#FULL_NAME_ORIGINAL").tips({
					side:3,
		            msg:'请输入基金原名',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FULL_NAME_ORIGINAL").focus();
			return false;
			}
			if($("#LEDGER_NUM").val()==""){
				$("#LEDGER_NUM").tips({
					side:3,
		            msg:'请输入帐套号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LEDGER_NUM").focus();
			return false;
			}
			if($("#TA_NAME").val()==""){
				$("#TA_NAME").tips({
					side:3,
		            msg:'请输入TA基金名称',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#TA_NAME").focus();
			return false;
			}
			if($("#FIN_SYSTEM").val()==""){
				$("#FIN_SYSTEM").tips({
					side:3,
		            msg:'请输入财务系统',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FIN_SYSTEM").focus();
			return false;
			}
			if($("#STRUCTURED").val()==""){
				$("#STRUCTURED").tips({
					side:3,
		            msg:'请输入分级',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STRUCTURED").focus();
			return false;
			}
			if($("#GUARANTEED").val()==""){
				$("#GUARANTEED").tips({
					side:3,
		            msg:'请输入保本',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#GUARANTEED").focus();
			return false;
			}
			if($("#CLOSED").val()==""){
				$("#CLOSED").tips({
					side:3,
		            msg:'请输入封闭',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CLOSED").focus();
			return false;
			}
			if($("#SHHK").val()==""){
				$("#SHHK").tips({
					side:3,
		            msg:'请输入沪港',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#SHHK").focus();
			return false;
			}
			if($("#QD").val()==""){
				$("#QD").tips({
					side:3,
		            msg:'请输入QD',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#QD").focus();
			return false;
			}
			if($("#MF").val()==""){
				$("#MF").tips({
					side:3,
		            msg:'请输入货基',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#MF").focus();
			return false;
			}
			if($("#IDX").val()==""){
				$("#IDX").tips({
					side:3,
		            msg:'请输入指数',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#IDX").focus();
			return false;
			}
			if($("#LOF").val()==""){
				$("#LOF").tips({
					side:3,
		            msg:'请输入LOF',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#LOF").focus();
			return false;
			}
			if($("#ETF").val()==""){
				$("#ETF").tips({
					side:3,
		            msg:'请输入ETF',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#ETF").focus();
			return false;
			}
			if($("#FOF").val()==""){
				$("#FOF").tips({
					side:3,
		            msg:'请输入FOF',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#FOF").focus();
			return false;
			}
			if($("#UNIT").val()==""){
				$("#UNIT").tips({
					side:3,
		            msg:'请输入货币量纲',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT").focus();
			return false;
			}
			if($("#DATE_FROM").val()==""){
				$("#DATE_FROM").tips({
					side:3,
		            msg:'请输入合同生效日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_FROM").focus();
			return false;
			}
			if($("#DATE_TO").val()==""){
				$("#DATE_TO").tips({
					side:3,
		            msg:'请输入基金终止日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_TO").focus();
			return false;
			}
			if($("#DATE_TRANSFORM").val()==""){
				$("#DATE_TRANSFORM").tips({
					side:3,
		            msg:'请输入基金转型日',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#DATE_TRANSFORM").focus();
			return false;
			}
			if($("#INTEREST_RATE_PERIOD").val()==""){
				$("#INTEREST_RATE_PERIOD").tips({
					side:3,
		            msg:'请输入利率风险敞口账龄',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#INTEREST_RATE_PERIOD").focus();
			return false;
			}
			if($("#OTHER_LIABILITIES").val()==""){
				$("#OTHER_LIABILITIES").tips({
					side:3,
		            msg:'请输入其他负债披露类型',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#OTHER_LIABILITIES").focus();
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
			if($("#STATUS").val()==""){
				$("#STATUS").tips({
					side:3,
		            msg:'请输入状态',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STATUS").focus();
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